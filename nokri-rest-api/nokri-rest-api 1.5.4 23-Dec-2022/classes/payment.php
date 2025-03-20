<?php

/* ----- Payment Procedure Starts Here	 ----- */
add_action('rest_api_init', 'nokriAPI_payment_get_hook', 0);

function nokriAPI_payment_get_hook() {
    register_rest_route(
            'nokri/v1', '/payment/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_payment_process',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
    register_rest_route(
            'nokri/v1', '/payment/verify_paytm/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_payment_verify_paytm',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
    register_rest_route(
            'nokri/v1', '/cand_payment/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_payment_process',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_payment_process')) {

    function nokriAPI_payment_process($request) {

        $user = wp_get_current_user();
        @$user_id = $user->data->ID;
        $json_data = $request->get_json_params();
        $package_id = (isset($json_data['package_id'])) ? trim($json_data['package_id']) : '';
        $source_token = (isset($json_data['source_token'])) ? trim($json_data['source_token']) : '';
        $payment_from = (isset($json_data['payment_from']) && $json_data['payment_from'] != "" ) ? trim($json_data['payment_from']) : 'stripe';
        $orderid = (isset($json_data['orderid']) && $json_data['orderid'] != "" ) ? trim($json_data['orderid']) : '';
        $email = (isset($json_data['email']) && $json_data['email'] != "" ) ? trim($json_data['email']) : '';
        $mobile = (isset($json_data['edtmobile']) && $json_data['edtmobile'] != "" ) ? trim($json_data['edtmobile']) : '';
        $amount = (isset($json_data['edtamount']) && $json_data['edtamount'] != "" ) ? trim($json_data['edtamount']) : '';

        if ($package_id == "") {
            $response = array('success' => false, 'data' => '', 'message' => __("Package Id not found", "nokri-rest-api"));
            return $response;
        }
        if ($payment_from == "free") {
            $is_avail = get_user_meta($user_id, 'avail_free_package', true);
            if ($is_avail == 1) {
                $success = false;
                $message = esc_html__("You have already availed free package", "nokri-rest-api");
            } else {
                update_user_meta($user_id, 'avail_free_package', (int) '1');
                nokri_free_package($package_id);
                $success = true;
                $message = esc_html__("Purchased successfully", "nokri-rest-api");
            }
            $response = array('success' => $success, 'data' => '', 'message' => $message);
            return $response;
        }
        global $nokriAPI;
        global $woocommerce;
        $package = wc_get_product($package_id);
        if ($package) {

            if ($payment_from == "cheque") {
                nokriAPI_create_ad_order($package_id, "cheque");
                return array('success' => true, 'data' => '', 'message' => __("Order Placed Successfully", "nokri-rest-api"));
            } else if ($payment_from == "bank_transfer") {
                nokriAPI_create_ad_order($package_id, "bacs");
                return array('success' => true, 'data' => '', 'message' => __("Order Placed Successfully", "nokri-rest-api"));
            } else if ($payment_from == "cash_on_delivery") {
                nokriAPI_create_ad_order($package_id, "cod");
                return array('success' => true, 'data' => '', 'message' => __("Order Placed Successfully", "nokri-rest-api"));
            } else if ($payment_from == "braintree") {
                nokriAPI_create_ad_order($package_id, "braintree");
                return array('success' => true, 'data' => '', 'message' => __("Order Placed Successfully", "nokri-rest-api"));
            } else if ($payment_from == "paypal") {
                if ($source_token == "") {
                    $response = array('success' => false, 'data' => '', 'message' => __("Invalid Payment Token", "nokri-rest-api"));
                    return $response;
                }
                $payment_id = $source_token;
                $payment_client = (isset($json_data['payment_client'])) ? trim($json_data['payment_client']) : '';
                /* Paypal ClientID */
                $paypal_client_id = (isset($json_data['appKey_paypalKey'])) ? trim($json_data['appKey_paypalKey']) : '';
                /* Paypal Secret */
                $paypal_secret = (isset($json_data['appKey_paypal_secret'])) ? trim($json_data['appKey_paypal_secret']) : '';

                $paymentClient_json = $payment_client;
                $path = NOKRI_API_PLUGIN_FRAMEWORK_PATH . 'inc/PayPal-PHP-SDK/autoload.php';
                $charge = '';
                require_once( $path );
                $pay_pal = new PayPal\Api\Payment;
                /** 				
                 * verifying the mobile payment on the server side
                 * method - POST
                 * @param paymentId paypal payment id
                 * @param paymentClientJson paypal json after the payment
                 */
                $success_status = false;
                $response_message = __("Payment Verified Successfully.", "nokri-rest-api");
                try {
                    $paymentId = $payment_id;
                    $payment_client = json_decode($paymentClient_json, true);

                    $apiContext = new \PayPal\Rest\ApiContext(
                            new \PayPal\Auth\OAuthTokenCredential(
                                    $paypal_client_id, // ClientID
                                    $paypal_secret      // ClientSecret
                            )
                    );
                    // Gettin payment details by making call to paypal rest api
                    $payment = $pay_pal::get($paymentId, $apiContext);
                    // Verifying the state approved
                    if ($payment->getState() != 'approved') {
                        $success_status = false;
                        $response_message = __("Payment has not been verified. Status is ", "nokri-rest-api") . $payment->getState();
                        return array('success' => $success_status, 'data' => '', 'message' => $response_message);
                    }
                    // Amount on client side
                    $amount_client = $payment_client["amount"];
                    // Currency on client side
                    $currency_client = $payment_client["currency_code"];

                    // Amount on server side
                    $amount_server = html_entity_decode(strip_tags($package->get_price($package_id)));
                    // Currency on server side
                    $currency_server = "USD";
                    $sale_state = 'completed';

                    // Verifying the amount
                    if ($amount_server != $amount_client) {
                        $success_status = false;
                        $response_message = __("Payment amount doesn't matched.", "nokri-rest-api");
                        return array('success' => $success_status, 'data' => '', 'message' => $response_message);
                    }
                    // Verifying the currency
                    if ($currency_server != $currency_client) {
                        $success_status = false;
                        $response_message = __("Payment currency doesn't matched.", "nokri-rest-api");
                        return array('success' => $success_status, 'data' => '', 'message' => $response_message);
                    }

                    // Verifying the sale state
                    if ($sale_state != 'completed') {
                        $success_status = false;
                        $response_message = __("Sale not completed", "nokri-rest-api");
                        return array('success' => $success_status, 'data' => '', 'message' => $response_message);
                    }

                    // storing the saled items
                    /* insertItemSales($payment_id_in_db, $transaction, $sale_state); */
                    nokriAPI_create_ad_order($package_id, "paypal");
                    $response_message = __("Payment Made successfully.", "nokri-rest-api");
                    return array('success' => true, 'data' => '', 'message' => $response_message);

                    echoResponse(200, $response);
                } catch (\PayPal\Exception\PayPalConnectionException $exc) {
                    if ($exc->getCode() == 404) {
                        $response_message = __("Payment not found!", "nokri-rest-api");
                        $success_status = false;
                    } else {
                        $response_message = __("Unknown error occurred!", "nokri-rest-api") . ' ' . $exc->getMessage();
                        $success_status = false;
                    }
                } catch (Exception $exc) {
                    $response_message = __("Unknown error occurred!", "nokri-rest-api") . ' ' . $exc->getMessage();
                    $success_status = false;
                }
            } else if ($payment_from == "stripe") {
                $appKey_stripeSKey = (isset($nokriAPI['appKey_stripeSKey']) ) ? $nokriAPI['appKey_stripeSKey'] : '';
                if ($appKey_stripeSKey == '') {

                    $response = array('success' => false, 'data' => '', 'message' => __("Stripe secret key not setup", "nokri-rest-api"));
                    return $response;
                }
                if ($source_token == "") {
                    $response = array('success' => false, 'data' => '', 'message' => __("Invalid Payment Token", "nokri-rest-api"));
                    return $response;
                }
                /* Stripe Payment Starts */
                $currency = get_woocommerce_currency();
                $amount = (float) $package->get_price() * 100;
                $path = ADFOREST_API_PLUGIN_FRAMEWORK_PATH . 'inc/stripe-php/init.php';
                $charge = '';
                require_once( $path );
                $curl = new \Stripe\HttpClient\CurlClient(array(CURLOPT_SSLVERSION => CURL_SSLVERSION_TLSv1_2));
                \Stripe\ApiRequestor::setHttpClient($curl);
                \Stripe\Stripe::setApiKey($appKey_stripeSKey);
                $args = array();
                $success = false;
                try {
                    /* Stripe_Charge::all(); */
                    $args = array('source' => $source_token, 'amount' => $amount, 'currency' => $currency);
                    $charge = \Stripe\Charge::create($args);
                    $success = true;
                    nokriAPI_create_ad_order($package_id, "Stripe");
                    $message = __("You order has been placed.", "nokri-rest-api");
                } catch (Exception $e) {
                    $message = $e->getMessage();
                }
                $response = array('success' => $success, 'data' => $args, 'message' => $message);
                return $response;
                /* Stripe Payment Ends */
            } else if ($payment_from == "paystack") {
                nokriAPI_create_ad_order($package_id, "paystack");
                return array('success' => true, 'data' => '', 'message' => __("Order placed successfully.", "nokri-rest-api"));
            }else if ($payment_from == "braintree") {
                nokriAPI_create_ad_order($package_id, "braintree");
                return array('success' => true, 'data' => '', 'message' => __("Order placed successfully.", "nokri-rest-api"));
            }
             else if ($payment_from == "app_inapp") {
                nokriAPI_create_ad_order($package_id, "app_inapp");
                return array('success' => true, 'data' => '', 'message' => __("Order placed successfully.", "nokri-rest-api"));
            } else if ($payment_from == "app_inapp") {
                $path = NOKRI_API_PLUGIN_FRAMEWORK_PATH . 'inc/PayPal-PHP-SDK/autoload.php';
                return array('success' => true, 'data' => '', 'message' => __("Order placed successfully.", "nokri-rest-api"));
            } else {
                return array('success' => false, 'data' => '', 'message' => __("Something Went Wrong.", "nokri-rest-api"));
            }
        }
        /* Get Product Info Ends */
    }

}

if (!function_exists("nokriAPI_payment_verify_paytm")) {

    function nokriAPI_payment_verify_paytm($request) {


        //$user           =  wp_get_current_user();	
        //@$user_id       =  $user->data->ID;
        $json_data = $request->get_json_params();

        $m_id = (isset($json_data['m_id'])) ? trim($json_data['m_id']) : '';
        $checksumash = (isset($json_data['checksumash'])) ? trim($json_data['checksumash']) : '';
        $orderid = (isset($json_data['orderid']) && $json_data['orderid'] != "" ) ? trim($json_data['orderid']) : '';

        header("Pragma: no-cache");
        header("Cache-Control: no-cache");
        header("Expires: 0");
        // following files need to be included
        $path1 = NOKRI_API_PLUGIN_FRAMEWORK_PATH . 'inc/paytm-kit/lib/config_paytm.php';
        $path2 = NOKRI_API_PLUGIN_FRAMEWORK_PATH . 'inc/paytm-kit/lib/encdec_paytm.php';

        require_once($path1);
        require_once($path2);
        $paytmChecksum = "";
        $paramList = array();
        $isValidChecksum = FALSE;
        $paramList = $_POST;
        $return_array = $_POST;
        $paytmChecksum = $checksumash; //Sent by Paytm pg
        //Verify all parameters received from Paytm pg to your application. Like MID received from paytm pg is same as your application’s MID, TXN_AMOUNT and ORDER_ID are same as what was sent by you to Paytm PG for initiating transaction etc.

        $isValidChecksum = verifychecksum_e($paramList, PAYTM_MERCHANT_KEY, $paytmChecksum); //will return TRUE or FALSE string.print_r($checksumash);
        // if ($isValidChecksum===TRUE)
        //   $return_array["IS_CHECKSUM_VALID"] = "Y";
        // else
        //   $return_array["IS_CHECKSUM_VALID"] = "N";

        $return_array["IS_CHECKSUM_VALID"] = $isValidChecksum ? "Y" : "N";

        //$return_array["TXNTYPE"] = "";
        //$return_array["REFUNDAMT"] = "";

        unset($return_array["CHECKSUMHASH"]);
        $mid = $m_id;
        $orderid = $orderid;
        $curl = curl_init();
        // Set some options - we are passing in a useragent too here
        curl_setopt_array($curl, array(
            CURLOPT_RETURNTRANSFER => 1,
            CURLOPT_URL => 'https://pguat.paytm.com/oltp/HANDLER_INTERNAL/TXNSTATUS?JsonData={"MID":"' . $mid . '","ORDERID":"' . $orderid . '","CHECKSUMHASH":"' . $paytmChecksum . '"}',
            CURLOPT_USERAGENT => 'Codular Sample cURL Request'
        ));
        // Send the request & save response to $resp
        $resp = curl_exec($curl);
        print_r($resp);
        exit;
        $status = json_decode($resp)->STATUS;
        echo $resp;
        //exit();
        // Close request to clear up some resources
        curl_close($curl);
        $encoded_json = htmlentities(json_encode($return_array));
    }

}

add_action('rest_api_init', 'nokriAPI_payment_card_hook', 0);

function nokriAPI_payment_card_hook() {
    register_rest_route(
            'nokri/v1', '/payment/card/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_payment_card_get',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists("nokriAPI_payment_card_get")) {

    function nokriAPI_payment_card_get() {
        $data['page_title'] = __('Checkout Process', 'nokri-rest-api');

        $current_year = date('Y');
        $year_arr = range($current_year, $current_year + 12);
        $data['form']['card_input_text'] = __('Card Number', 'nokri-rest-api');
        $data['form']['select_title'] = __('Expiry Date', 'nokri-rest-api');
        $data['form']['select_month'] = __('Month', 'nokri-rest-api');
        $data['form']['select_year'] = __('Year', 'nokri-rest-api');
        $data['form']['select_option_year'] = $year_arr;
        $data['form']['cvc_input_text'] = __('CVC Number', 'nokri-rest-api');
        $data['form']['btn_text'] = __('Checkout', 'nokri-rest-api');

        $data['error']['card_number'] = __('The card number that you entered is invalid', 'nokri-rest-api');
        $data['error']['expiration_date'] = __('The expiration date that you entered is invalid', 'nokri-rest-api');
        $data['error']['invalid_cvc'] = __('The CVC code that you entered is invalid', 'nokri-rest-api');
        $data['error']['card_details'] = __('The card details that you entered are invalid', 'nokri-rest-api');

        $response = array('success' => true, 'data' => $data, 'message' => '');
        return $response;
    }

}

if (!function_exists("nokriAPI_create_ad_order")) {

    function nokriAPI_create_ad_order($product_id = '', $payment_method = '') {

        global $woocommerce;
        $user = wp_get_current_user();
        $user_id = $user->data->ID;

        $sb_address = get_user_meta($user_id, '_sb_address', true);
        $st_address = $city = $state = $country = '';
        if ($sb_address != "") {
            $exp = explode(",", $sb_address);
            if (count($exp) == 4) {

                $country = array_slice($exp, -1, 1);
                $country = ( isset($country[0]) && $country[0] != "" ) ? trim($country[0]) : '';

                $state = array_slice($exp, -2, 1);
                $state = ( isset($state[0]) && $state[0] != "" ) ? trim($state[0]) : '';

                $city = array_slice($exp, -3, 1);
                $city = ( isset($city[0]) && $city[0] != "" ) ? trim($city[0]) : '';

                $st_address = array_slice($exp, -4, 1);
                $st_address = ( isset($st_address[0]) && $st_address[0] != "" ) ? trim($st_address[0]) : '';
            } else if (count($exp) == 3) {
                $country = array_slice($exp, -1, 1);
                $country = ( isset($country[0]) && $country[0] != "" ) ? trim($country[0]) : '';

                $state = array_slice($exp, -2, 1);
                $state = ( isset($state[0]) && $state[0] != "" ) ? trim($state[0]) : '';

                $city = array_slice($exp, -3, 1);
                $city = ( isset($city[0]) && $city[0] != "" ) ? trim($city[0]) : '';
            } else if (count($exp) == 2) {
                $country = array_slice($exp, -1, 1);
                $country = ( isset($country[0]) && $country[0] != "" ) ? trim($country[0]) : '';

                $state = array_slice($exp, -2, 1);
                $state = ( isset($state[0]) && $state[0] != "" ) ? trim($state[0]) : '';
            } else if (count($exp) == 1) {
                $country = array_slice($exp, -1, 1);
                $country = ( isset($country[0]) && $country[0] != "" ) ? trim($country[0]) : '';
            }
        }

        $address = array(
            'first_name' => $user->data->display_name,
            'last_name' => '',
            'company' => '',
            'email' => $user->data->user_email,
            'phone' => get_user_meta($user_id, '_sb_contact', true),
            'address_1' => $st_address,
            'address_2' => '',
            'city' => $city,
            'state' => $state,
            'postcode' => '',
            'country' => $country
        );
        /* Now we create the order */
        $order = wc_create_order();

        $order->add_product(get_product($product_id), 1); // This is an existing SIMPLE product
        $order->set_address($address, 'billing');

        $order->calculate_totals();

        /* Gateway settings starts */
        /* --
          PayPal
          cod		Cash On Delivery
          cheque	Check payments
          bacs    Direct bank transfer
          -- */
        $payment_gateways = WC()->payment_gateways->payment_gateways();
        $saveGateway = ($payment_method != "" ) ? @$payment_gateways[$payment_method] : '';
        $saveGateway = (isset($saveGateway) && $saveGateway != "" ) ? $saveGateway : '';
        /* Gateway settings Ends */
        $order->set_payment_method($saveGateway);
        $order->set_payment_method_title($payment_method);
        $order->update_status("processing", 'Imported order', TRUE);

        $count = wc_update_new_customer_past_orders($user_id);
        update_user_meta($user_id, '_wc_linked_order_count', $count);
        return $order;
    }

}


/* ----- 	Payment Procedure Done Starts Here	 ----- */
add_action('rest_api_init', 'nokriAPI_payment_complete_get_hook', 0);

function nokriAPI_payment_complete_get_hook() {
    register_rest_route(
            'nokri/v1', '/payment/complete/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_payment_complete_process',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
    register_rest_route(
            'nokri/v1', '/payment/complete/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_payment_complete_process',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_payment_complete_process')) {

    function nokriAPI_payment_complete_process($request) {

        global $woocommerce;
        global $nokriAPI;
        $user_id = get_current_user_id();
        $order = wc_get_customer_last_order($user_id);
        $methods = WC()->payment_gateways->payment_gateways();
        $html = '';
        $html .= '<div class="woocommerce-order">';
        if ($order) :
            if ($order->has_status('failed')) :
                $html .= '<p class="woocommerce-notice woocommerce-notice--error woocommerce-thankyou-order-failed">' . __('Unfortunately your order cannot be processed as the originating bank/merchant has declined your transaction. Please attempt your purchase again.', 'nokri-rest-api') . '</p>';
                $html .= '<p class="woocommerce-notice woocommerce-notice--error woocommerce-thankyou-order-failed-actions">
				<a href="' . esc_url($order->get_checkout_payment_url()) . '" class="button pay">' . __('Pay', 'nokri-rest-api') . '</a>';
                if (is_user_logged_in()) :
                /* $html .= '<a href="'.esc_url( wc_get_page_permalink( 'myaccount' ) ).'" class="button pay">'. __( 'My account', 'woocommerce' ).'</a>'; */
                endif;
                $html .= '</p>';
            else :
                $html .= '<p class="woocommerce-notice woocommerce-notice--success woocommerce-thankyou-order-received">' . apply_filters('woocommerce_thankyou_order_received_text', __('Thank you. Your order has been received.', 'nokri-rest-api'), $order) . '</p>';
                $html .= '<ul class="woocommerce-order-overview woocommerce-thankyou-order-details order_details">';
                $html .= '<li class="woocommerce-order-overview__order order">';
                $html .= __('Order number:', 'nokri-rest-api');
                $html .= '<strong>' . $order->get_order_number() . '</strong>';
                $html .= '</li>';
                $html .= '<li class="woocommerce-order-overview__date date">';
                $html .= __('Date:', 'nokri-rest-api');
                $html .= '<strong>' . wc_format_datetime($order->get_date_created()) . '</strong>';
                $html .= '</li>';

                if (is_user_logged_in() && $order->get_user_id() === get_current_user_id() && $order->get_billing_email()) :
                    $html .= '<li class="woocommerce-order-overview__email email">';
                    $html .= __('Email:', 'nokri-rest-api');
                    $html .= '<strong>' . $order->get_billing_email() . '</strong>';
                    $html .= '</li>';
                endif;
                $html .= '<li class="woocommerce-order-overview__total total">';
                $html .= __('Total:', 'nokri-rest-api');
                $html .= '<strong>' . $order->get_formatted_order_total() . ' </strong>';
                $html .= '</li>';
                if ($order->get_payment_method_title()) :
                    $html .= '<li class="woocommerce-order-overview__payment-method method">';
                    $html .= __('Payment method:', 'nokri-rest-api');
                    $html .= '<strong>' . wp_kses_post($order->get_payment_method_title()) . '</strong>';
                    $html .= '</li>';
                endif;

                $html .= '</ul>';

            endif;
            $thankyou = $get_payment_method = '';
            ob_start();
            do_action('woocommerce_thankyou_' . $order->get_payment_method(), $order->get_id());
            
            $get_payment_method = ob_get_contents();
            ob_end_clean();
            ob_start();
            do_action('woocommerce_thankyou', $order->get_id());
            $thankyou = ob_get_contents();
            ob_end_clean();

            $html .= $get_payment_method;
            $html .= $thankyou;
        else :
            $html .= '<p class="woocommerce-notice woocommerce-notice--success woocommerce-thankyou-order-received">' . apply_filters('woocommerce_thankyou_order_received_text', __('Thank you. Your order has been received.', 'nokri-rest-api'), null) . '</p>';

        endif;
        $html .= '</div>';
        $thankYou = (isset($nokriAPI['payment_thankyou'])) ? $nokriAPI['payment_thankyou'] : __('Thank You For Your Order', 'nokri-rest-api');
        $thankBtn = (isset($nokriAPI['btn_thankyou'])) ? $nokriAPI['btn_thankyou'] : __('Continue', 'nokri-rest-api');
        $html = nokriAPI_strip_single_tag($html, 'a');
        $html = str_replace("\r", "", $html);
        $html = str_replace("\n", "", $html);
        $html_title = __('Thank you. Your order has been received.', 'nokri-rest-api');
        $html_data = '<!doctype html><html><head><meta charset="utf-8"><title>' . $thankYou . '</title></head><body>' . $html . '</body></html>';
        $data['data'] = ($html_data);
        $data['order_thankyou_title'] = (@$thankYou);
        $data['order_thankyou_btn'] = (@$thankBtn);
        $response = array('success' => true, 'data' => $data, 'message' => '');
        return $response;
    }

}

function nokriAPI_strip_single_tag($str, $tag) {

    $str1 = preg_replace('/<\/' . $tag . '>/i', '', $str);
    if ($str1 != $str) {
        $str = preg_replace('/<' . $tag . '[^>]*>/i', '', $str1);
    }
    return $str;
}