<?php
/*-----   Woo Products Starts Here	 -----*/
add_action( 'rest_api_init', 'nokriAPI_packages_get_hook', 0 );
function nokriAPI_packages_get_hook() {

    register_rest_route( 'nokri/v1', '/packages/',
        array(
				'methods'  => WP_REST_Server::READABLE,
				'callback' => 'nokriAPI_packages_get',
				'permission_callback' => function () {  return nokriAPI_basic_auth();  },
        	)
    );
}
if (!function_exists('nokriAPI_packages_get'))
{
	function nokriAPI_packages_get( $request )
	{ 
  		
		$user = wp_get_current_user();	
		@$user_id = $user->data->ID;	
		global  $woocommerce;
		$pdata = array();
		$products = array();
		global $nokriAPI;
        global $nokri;
		$message = '';
		$success = true;
		if ( class_exists( 'WooCommerce' ) ){
		$productsData = $nokriAPI['api_woo_products_multi'];
				if( is_array($productsData) && count( $productsData ) > 0 )
				{
					$c_terms = get_terms('job_class', array('hide_empty' => false , 'orderby'=> 'id', 'order' => 'ASC' ));


					foreach( $productsData as $product )
					{
	
						$productData	=	new WC_Product( $product );
						
						$pdata['color'] = 'light';
						if( get_post_meta( $product, 'package_bg_color', true ) == 'dark' )
							$pdata['color'] = 'dark';
							
						
						$pdata['days_text'] = __('Validity','nokri-rest-api');
						$pdata['days_value'] = 0;
						
						if( get_post_meta( $product, 'package_expiry_days', true ) == "-1" )
						{
							$pdata['days_value'] = __('Lifetime','nokri-rest-api');
						}
						else if( get_post_meta( $product, 'package_expiry_days', true ) != "" )
						{
							$pdata['days_value'] = get_post_meta( $product, 'package_expiry_days', true ) .' '. __('Days','nokri-rest-api');						
						}
                                                
                                                
                                               
                                                
         					
						$pdata['free_ads_text'] = __('Free Ads','nokri-rest-api');
						$pdata['free_ads_value'] = 0;					
						if( get_post_meta( $product, 'package_free_ads', true ) != "" )
						{
							$pdata['free_ads_value'] = get_post_meta( $product, 'package_free_ads', true );
							
						}
                                                                                              
                                             
                                                
                                                $other_details           =    array();
                                                $other_details_counter   =     0;
                                                                                               
                                                
                                                if ((isset($nokri['allow_bump_jobs'])) && $nokri['allow_bump_jobs']) {
                                                $bump_up_jobs   =   get_post_meta( $product, 'pack_bump_ads_limit', true );
                                                $bump_up_name    = esc_html__('bump up jobs','nokri-rest-api');
                                                
                                                
                                                
                                                if( $bump_up_jobs == "-1" )
						{
                                                         $other_details[$other_details_counter]['no_of_jobs']    = __('Lifetime','nokri-rest-api');
					                 $other_details[$other_details_counter]['name']          =   $bump_up_name;
                                                         $other_details_counter   = 1 ;
						}
						else if( $bump_up_jobs != "" )
						{
							$other_details[$other_details_counter]['no_of_jobs']    = $bump_up_jobs;
					                $other_details[$other_details_counter]['name']          =   $bump_up_name;
                                                        $other_details_counter   =  1;
						}
                                                
                                                else {
                                                    
                                                    $other_details[$other_details_counter]['no_of_jobs']    = "0";
					            $other_details[$other_details_counter]['name']          =   $bump_up_name;
                                                    $other_details_counter   =  1;
                                                }                                               
                                                }                                              
                                                $featured_days  =   "0";
                                               
                                                $featured_meta   =   get_post_meta( $product, 'pack_emp_featured_profile', true );
                                                
                                                if( $featured_meta == "-1" )
						{
							$featured_days = __('Lifetime','nokri-rest-api');
						}
						else if( $featured_meta != "" )
						{
							$featured_days = $featured_meta;						
						}                                           
                                              
                                                $other_details[$other_details_counter]['no_of_jobs']    = $featured_days;
					        $other_details[$other_details_counter]['name']          = esc_html__('Featured profile','nokri-rest-api');                                           
                                             
						/* Cand search package info */
						$pdata['is_cand_search'] = false;
						 if (get_post_meta( $product, 'is_candidates_search', true ))
						{
							$pdata['is_cand_search'] = true;
							if(get_post_meta($product, 'candidate_search_values', true ) == '-1')
							{
								$pdata['cand_search'] = __('Candidates Search','nokri');
								$pdata['cand_search_value'] = __('Unlimited','nokri');
							}
							else
							{
								if (get_post_meta( $product, 'candidate_search_values', true ) )
								{
									$pdata['cand_search'] = __('Candidates Search','nokri');
									$pdata['cand_search_value'] = get_post_meta( $product, 'candidate_search_values', true );
								}
							}
						}
						
						
					   $p_job_data = array();
					   if( count( $c_terms ) > 0 )
						 { 
						 	$count = 0;
						  	foreach( $c_terms as $c_term)
						  	{
						   		$meta_name  =  'package_job_class_'.$c_term->term_id;
						   		$meta_value =  get_post_meta($product, $meta_name, true);
                                                                
                                  if($meta_value  ==  "-1"){

                                     $meta_value    = esc_html__('Unlimited','nokri-rest-api');

                                  }                            
                                                                
						       if( $meta_value != "" )
						   		{
									$p_job_data[$count]['no_of_jobs']    = $meta_value;
									$p_job_data[$count]['name']          = ucfirst( $c_term->name);
									$count++;
						   		}
						  	}
							
						 }	                                                
						$pdata['premium_jobs'] 	= array_merge($p_job_data , $other_details );	
						$pdata['product_id'] 	= $product ;
						$pdata['product_title'] = get_the_title( $product );
						$pdata['is_free']       = get_post_meta($product, 'op_pkg_typ',true );
						$pdata['product_android_inapp'] = get_post_meta( $product, 'package_product_code_android', true );
						$pdata['product_ios_inapp'] = get_post_meta( $product, 'package_product_code_ios', true ); 
						$pdata['product_price'] = html_entity_decode(strip_tags(wc_price($productData->get_price())));
						$pdata['product_price_sign'] = html_entity_decode(strip_tags(get_woocommerce_currency_symbol()));
						$pdata['product_price_only'] = $productData->get_price();
						if(get_post_meta($product, 'op_pkg_typ',true ) == 1)
						{
							$pdata['product_price_only']      =  'Free';
							$pdata['product_price_sign']      =  '';
						}
						$pdata['product_link']  = get_the_permalink( $product );
						$pdata['product_qty']   = 1;
						$pdata['product_btn']   = __('Select Plan','nokri-rest-api');
						$pdata['product_appCode']['android']   =  get_post_meta( $product, 'package_product_code_android', true );
						$pdata['product_appCode']['ios']       = get_post_meta( $product, 'package_product_code_ios', true );
						$pdata['product_appCode']['message']   = __('InApp purchase not available for this product','nokri-rest-api');
						
						$products[] = $pdata;	
						
		$methods = array();
		$methods[] =  array("key" => "", "value" =>__( 'Select Option', 'nokri-rest-api' ));
		if( isset( $nokriAPI['api-payment-packages'] ) && count($nokriAPI['api-payment-packages']) > 0   ) 
		{
			foreach( $nokriAPI['api-payment-packages'] as $type )
			{
				$name = nokriAPI_payment_types($type);
				if( $name != "" )
				{
					$methods[] =  array("key" => $type, "value" => $name  );
				}
				//$methods[$type] = nokriAPI_payment_types($type);
			}
		}
		
		
		$data["payment_types"]  =  $methods;
		$extra["page_title"]    =  __('Packages','nokri-rest-api');
		$extra["billing_error"] =  __('something went wrong while billing your account','nokri-rest-api');
	
	
		/* Paypal Account Currency Settings Starts */
		$paypalKey = ( isset( $nokriAPI['appKey_paypalKey'] ) && $nokriAPI['appKey_paypalKey'] != "" ) ? $nokriAPI['appKey_paypalKey'] : '';
		$merchant_name = ( isset( $nokriAPI['paypalKey_merchant_name'] ) && $nokriAPI['paypalKey_merchant_name'] != "" ) ? $nokriAPI['paypalKey_merchant_name'] : '';
		
		$paypal_currency = ( isset( $nokriAPI['paypalKey_currency'] ) && $nokriAPI['paypalKey_currency'] != "" ) ? $nokriAPI['paypalKey_currency'] : '';
		$privecy_url = ( isset( $nokriAPI['paypalKey_privecy_url'] ) && $nokriAPI['paypalKey_privecy_url'] != "" ) ? $nokriAPI['paypalKey_privecy_url'] : '';
		$agreement_url = ( isset( $nokriAPI['paypalKey_agreement'] ) && $nokriAPI['paypalKey_agreement'] != "" ) ? $nokriAPI['paypalKey_agreement'] : '';
		
		$appKey_paypalMode = ( isset( $nokriAPI['appKey_paypalMode'] ) && $nokriAPI['appKey_paypalMode'] != "" ) ? $nokriAPI['appKey_paypalMode'] : 'live';
		$has_key = ( $paypalKey == "" ) ? false : true;
		$data["is_paypal_key"] = $has_key;
		if( $has_key == true )
		{
			$data["paypal"]["mode"] 				= 	$appKey_paypalMode;
			$data["paypal"]["api_key"] 				= 	$paypalKey;
			$data["paypal"]["merchant_name"] 		= 	$merchant_name;
			$data["paypal"]["currency"] 			= 	$paypal_currency;
			$data["paypal"]["privecy_url"] 			= 	$privecy_url;
			$data["paypal"]["agreement_url"] 		= 	$agreement_url;
		}
		/* Paypal Account Currency Settings Ends */
					}				
				}
				else
				{
					$success = false;
					$message = __("No Product Found", "nokri-rest-api");
				}			
		}
		else  
		{
			$success = false;
			$message = __("No Product Found", "nokri-rest-api");
		}
		$data["products"]     =  $products;
		$extra["page_title"]  =  __('Packages','nokri-rest-api');
				/*Android All InApp Settings */
		$inappAndroid = (isset( $nokriAPI['inApp_androidSecret'] ) &&  $nokriAPI['inApp_androidSecret'] != "" ) ? $nokriAPI['inApp_androidSecret'] : '';
		
		$inappAndroid_on = (isset( $nokriAPI['api-inapp-android-app'] ) &&  $nokriAPI['api-inapp-android-app'] ) ? true : false;
		
		$extra['android']['title_text'] 		  = __('InApp Purchases','nokri-rest-api');
		$extra['android']['in_app_on'] 		  	  = $inappAndroid_on;
		$extra['android']['secret_code'] 		  = $inappAndroid; /*Secret code*/
		$extra['android']['message']['no_market'] = __('Play Market app is not installed.','nokri-rest-api');
		$extra['android']['message']['one_time']  = __('One Time Purchase not Supported on your Device.','nokri-rest-api');		
		/*IOS All InApp Settings */
		$inappIos = (isset( $nokriAPI['inApp_iosSecret'] ) &&  $nokriAPI['inApp_iosSecret'] != "" ) ? $nokriAPI['inApp_iosSecret'] : '';
		$iosInApp_on = (isset( $nokriAPI['api-inapp-ios-app'] ) &&  $nokriAPI['api-inapp-ios-app'] ) ? true : false;
		
		$extra['ios']['title_text'] 		 = __('InApp Purchases','nokri-rest-api');
		$extra['ios']['payment_type'] 		 = 'InApp Purchases';
		$extra['ios']['in_app_on'] 		  	 = $iosInApp_on;
		$extra['ios']['secret_code'] 		 = $inappIos; /*Secret code*/	
                $extra['no_packge'] 		         = esc_html__('No products were added','nokri-rest-api')  ;
			
		$response = array( 'success' => $success, 'data' => $data, 'message' => $message, 'extra' => $extra );
		return $response;
		
	}
}

/*-----   Woo Products Starts Here candidate packages	  -----*/
add_action( 'rest_api_init', 'nokriAPI_cand_packages_get_hook', 0 );
function nokriAPI_cand_packages_get_hook() {

    register_rest_route( 'nokri/v1', '/cand_packages/',
        array(
				'methods'  => WP_REST_Server::READABLE,
				'callback' => 'nokriAPI_cand_packages_get',
				'permission_callback' => function () {  return nokriAPI_basic_auth();  },
        	)
    );
}
if (!function_exists('nokriAPI_cand_packages_get'))
{
	function nokriAPI_cand_packages_get( $request )
	{ 
  		
		$user = wp_get_current_user();	
		@$user_id = $user->data->ID;	
		global  $woocommerce;
		$pdata = array();
		$products = array();
		global $nokriAPI;
		$message = '';
		$success = true;
                
               
		if ( class_exists( 'WooCommerce' ) ){
                     
		$productsData = $nokriAPI['api_woo_cand_products_multi'];
              
				if( count( $productsData ) > 0 )
				{
					$c_terms = get_terms('job_class', array('hide_empty' => false , 'orderby'=> 'id', 'order' => 'ASC' ));
					
					foreach( $productsData as $product )
					{
	
						$productData	=	new WC_Product( $product );
						
						$pdata['color'] = 'light';
						if( get_post_meta( $product, 'package_bg_color', true ) == 'dark' )
							$pdata['color'] = 'dark';
						$pdata['days_text'] = __('Validity','nokri-rest-api');
						$pdata['days_value'] = 0;
						if( get_post_meta( $product, 'package_expiry_days', true ) == "-1" )
						{
							$pdata['days_value'] = __('Lifetime','nokri-rest-api');
						}
						else if( get_post_meta( $product, 'package_expiry_days', true ) != "" )
						{
							$pdata['days_value'] = get_post_meta( $product, 'package_expiry_days', true ) .' '. __('Days','nokri-rest-api');						
						}                                               
                                                /*jobs left */
                                                $pdata['jobs_left_text'] = __('Jobs Left','nokri-rest-api');
                                                 $jobs_left_value        = get_post_meta( $product, 'candidate_jobs', true );
                                                 $jobs_left_value_days  =   '';
                                                 if ($jobs_left_value)
							{
								if($jobs_left_value == '-1')
								{
									$jobs_left_value_days    = __('Unlimited Jobs','nokri-rest-api');
								}
								else
								{
									
								         $jobs_left_value_days     =  $jobs_left_value .' '.  __('Jobs','nokri-rest-api');
									
								}
							}
                                                $pdata['jobs_left_value'] = $jobs_left_value_days;
                                                
                                                 $pdata['feature_days_text'] = __('Featured profile for','nokri-rest-api');
                                                 $feature_profile_days      = get_post_meta( $product, 'candidate_feature_list', true );
                                                 $feature_profile_days_value  =   '';
                                                 if ($feature_profile_days)
							{
								if($feature_profile_days == '-1')
								{
									$feature_profile_days_value    = __('Unlimited Days','nokri-rest-api');
								}
								else
								{
									
								         $feature_profile_days_value     =  $feature_profile_days .' '.  __('Days','nokri-rest-api');
									
								}
							}
                                                $pdata['feature_days_value'] = $feature_profile_days_value;
                                                
                                                
				
						$pdata['free_ads_text'] = __('Free Ads','nokri-rest-api');
						$pdata['free_ads_value'] = 0;					
						if( get_post_meta( $product, 'package_free_ads', true ) != "" )
						{
							$pdata['free_ads_value'] = get_post_meta( $product, 'package_free_ads', true );
							
						}
                                                
						/* Cand search package info */																	  						 						 						 						 										
						$pdata['product_id'] 	= $product ;
						$pdata['product_title'] = get_the_title( $product );
						$pdata['is_free']       = get_post_meta($product, 'op_pkg_typ',true );
						$pdata['product_android_inapp'] = get_post_meta( $product, 'package_product_code_android', true );
						$pdata['product_ios_inapp'] = get_post_meta( $product, 'package_product_code_ios', true ); 
						$pdata['product_price'] = html_entity_decode(strip_tags(wc_price($productData->get_price())));
						$pdata['product_price_sign'] = html_entity_decode(strip_tags(get_woocommerce_currency_symbol()));
						$pdata['product_price_only'] = $productData->get_price();
						if(get_post_meta($product, 'op_pkg_typ',true ) == 1)
						{
							$pdata['product_price_only']      =  'Free';
							$pdata['product_price_sign']      =  '';
						}
						$pdata['product_link']  = get_the_permalink( $product );
						$pdata['product_qty']   = 1;
						$pdata['product_btn']   = __('Select Plan','nokri-rest-api');
						$pdata['product_appCode']['android']   =  get_post_meta( $product, 'package_product_code_android', true );
						$pdata['product_appCode']['ios']       = get_post_meta( $product, 'package_product_code_ios', true );
						$pdata['product_appCode']['message']   = __('InApp purchase not available for this product','nokri-rest-api');
						
						$products[] = $pdata;	
						
		$methods = array();
		$methods[] =  array("key" => "", "value" =>__( 'Select Option', 'nokri-rest-api' ));
		if( isset( $nokriAPI['api-payment-packages'] ) && count($nokriAPI['api-payment-packages']) > 0   ) 
		{
			foreach( $nokriAPI['api-payment-packages'] as $type )
			{
				$name = nokriAPI_payment_types($type);
				if( $name != "" )
				{
					$methods[] =  array("key" => $type, "value" => $name  );
				}
				//$methods[$type] = nokriAPI_payment_types($type);
			}
		}
		
		
		$data["payment_types"]  =  $methods;
		$extra["page_title"]    =  __('Packages','nokri-rest-api');
		$extra["billing_error"] =  __('something went wrong while billing your account','nokri-rest-api');
	
	
		/* Paypal Account Currency Settings Starts */
		$paypalKey = ( isset( $nokriAPI['appKey_paypalKey'] ) && $nokriAPI['appKey_paypalKey'] != "" ) ? $nokriAPI['appKey_paypalKey'] : '';
		$merchant_name = ( isset( $nokriAPI['paypalKey_merchant_name'] ) && $nokriAPI['paypalKey_merchant_name'] != "" ) ? $nokriAPI['paypalKey_merchant_name'] : '';
		
		$paypal_currency = ( isset( $nokriAPI['paypalKey_currency'] ) && $nokriAPI['paypalKey_currency'] != "" ) ? $nokriAPI['paypalKey_currency'] : '';
		$privecy_url = ( isset( $nokriAPI['paypalKey_privecy_url'] ) && $nokriAPI['paypalKey_privecy_url'] != "" ) ? $nokriAPI['paypalKey_privecy_url'] : '';
		$agreement_url = ( isset( $nokriAPI['paypalKey_agreement'] ) && $nokriAPI['paypalKey_agreement'] != "" ) ? $nokriAPI['paypalKey_agreement'] : '';
		
		$appKey_paypalMode = ( isset( $nokriAPI['appKey_paypalMode'] ) && $nokriAPI['appKey_paypalMode'] != "" ) ? $nokriAPI['appKey_paypalMode'] : 'live';
		$has_key = ( $paypalKey == "" ) ? false : true;
		$data["is_paypal_key"] = $has_key;
		if( $has_key == true )
		{
			$data["paypal"]["mode"] 				= 	$appKey_paypalMode;
			$data["paypal"]["api_key"] 				= 	$paypalKey;
			$data["paypal"]["merchant_name"] 		= 	$merchant_name;
			$data["paypal"]["currency"] 			= 	$paypal_currency;
			$data["paypal"]["privecy_url"] 			= 	$privecy_url;
			$data["paypal"]["agreement_url"] 		= 	$agreement_url;
		}
		/* Paypal Account Currency Settings Ends */
					}				
				}
				else
				{
					$success = false;
					$message = __("No Product Found", "nokri-rest-api");
				}			
		}
		else  
		{
			$success = false;
			$message = __("No Product Found", "nokri-rest-api");
		}
		$data["products"]     =  $products;
		$extra["page_title"]  =  __('Packages','nokri-rest-api');
				/*Android All InApp Settings */
		$inappAndroid = (isset( $nokriAPI['inApp_androidSecret'] ) &&  $nokriAPI['inApp_androidSecret'] != "" ) ? $nokriAPI['inApp_androidSecret'] : '';
		
		$inappAndroid_on = (isset( $nokriAPI['api-inapp-android-app'] ) &&  $nokriAPI['api-inapp-android-app'] ) ? true : false;
		
		$extra['android']['title_text'] 		  = __('InApp Purchases','nokri-rest-api');
		$extra['android']['in_app_on'] 		  	  = $inappAndroid_on;
		$extra['android']['secret_code'] 		  = $inappAndroid; /*Secret code*/
		$extra['android']['message']['no_market'] = __('Play Market app is not installed.','nokri-rest-api');
		$extra['android']['message']['one_time']  = __('One Time Purchase not Supported on your Device.','nokri-rest-api');
		
		
		/*IOS All InApp Settings */
		$inappIos = (isset( $nokriAPI['inApp_iosSecret'] ) &&  $nokriAPI['inApp_iosSecret'] != "" ) ? $nokriAPI['inApp_iosSecret'] : '';
		$iosInApp_on = (isset( $nokriAPI['api-inapp-ios-app'] ) &&  $nokriAPI['api-inapp-ios-app'] ) ? true : false;
		
		$extra['ios']['title_text'] 		 = __('InApp Purchases','nokri-rest-api');
		$extra['ios']['payment_type'] 		 = 'InApp Purchases';
		$extra['ios']['in_app_on'] 		  	 = $iosInApp_on;
		$extra['ios']['secret_code'] 		 = $inappIos; /*Secret code*/	
			
		$response = array( 'success' => $success, 'data' => $data, 'message' => $message, 'extra' => $extra );
		return $response;
		
	}
}
if ( ! function_exists( 'nokriAPI_payment_types' ) )
{
	function nokriAPI_payment_types($key = '')
	{
		global $nokriAPI;
		$paypalKey = ( isset( $nokriAPI['appKey_paypalKey'] ) && $nokriAPI['appKey_paypalKey'] != "" ) ? $nokriAPI['appKey_paypalKey'] : '';
		$stripeSKey = ( isset( $nokriAPI['appKey_stripeSKey'] ) && $nokriAPI['appKey_stripeSKey'] != "" ) ? $nokriAPI['appKey_stripeSKey'] : '';
		$arr = array();
		
			$arr['stripe'] 				= __( 'Stripe', 'nokri-rest-api' );
			$arr['paypal'] 				= __( 'PayPal', 'nokri-rest-api' );
		    $arr['bank_transfer'] 		= __( 'Bank Transfer', 'nokri-rest-api' );
		    $arr['cash_on_delivery'] 	= __( 'Cash On Delivery', 'nokri-rest-api' );
		    $arr['cheque'] 	            = __( 'Payment By Check', 'nokri-rest-api' );
			$arr['app_inapp'] 	        = __( 'InApp Purchase', 'nokri-rest-api' );
		
		return ($key != "" ) ? $arr[$key] : $arr;
		
	}
}

if ( ! function_exists( 'nokriAPI_payment_types_ios' ) )
{
	function nokriAPI_payment_types_ios($key = '')
	{
		global $nokriAPI;
		$paypalKey = ( isset( $nokriAPI['appKey_paypalKey'] ) && $nokriAPI['appKey_paypalKey'] != "" ) ? $nokriAPI['appKey_paypalKey'] : '';
		$stripeSKey = ( isset( $nokriAPI['appKey_stripeSKey'] ) && $nokriAPI['appKey_stripeSKey'] != "" ) ? $nokriAPI['appKey_stripeSKey'] : '';
		$arr = array();
			
			$arr['app_inapp'] 	        = __( 'InApp Purchase', 'nokri-rest-api' );
		
		return ($key != "" ) ? $arr[$key] : $arr;
		
	}
}

/*Added Meta For The Android InApp Purchase*/
add_action( 'add_meta_boxes', 'nokriAPI_andrid_product_key_hook' );
function nokriAPI_andrid_product_key_hook()
{
    add_meta_box( 'nokriAPI_metaboxes_product_android_ios', __('InApp Purchase Settings For Android and IOS Apps','nokri-rest-api' ), 'nokriAPI_andrid_product_key_func', 'product', 'normal', 'high' );
}
if (!function_exists('nokriAPI_andrid_product_key_func'))
{
	function nokriAPI_andrid_product_key_func( $post )
	{
		wp_nonce_field( 'my_meta_box_nonce_product', 'meta_box_nonce_product' );
		?>
			<div>
			<p><?php echo __('Android Product Code','nokri-rest-api' ); ?></p>
				<input type="text" name="package_product_code_android" class="project_meta" placeholder="<?php echo esc_attr__('Enter you android product code here.', 'nokri-rest-api' ); ?>" size="30" value="<?php echo esc_attr( get_post_meta($post->ID, "package_product_code_android", true) ); ?>" id="package_product_code_android" spellcheck="true" autocomplete="off">
		<div><?php echo __( "Please enter product code for the andrid product. Leave empty if you dont't have any. Only enter in case you have bought android app.", 'nokri-rest-api' ); ?></div>
			</div>
			<div>
			<p><?php echo __('IOS Product Code','nokri-rest-api' ); ?></p>
				<input type="text" name="package_product_code_ios" class="project_meta" placeholder="<?php echo esc_attr__('Enter you ios product code here.', 'nokri-rest-api' ); ?>" size="30" value="<?php echo esc_attr( get_post_meta($post->ID, "package_product_code_ios", true) ); ?>" id="package_product_code_ios" spellcheck="true" autocomplete="off">
				<div><?php echo __( "Please enter product code for the andrid product. Leave empty if you dont't have any. Only enter in case you have bought ios app.", 'nokri-rest-api' ); ?></div>
			</div>  
            
            <p><strong>*<?php echo __( "Please make sure you have created the **** product while create packages in AppStore/PlayStore accounts.", 'nokri-rest-api' ); ?></strong></p>      
		<?php		
	}
}
add_action( 'save_post', 'nokriAPI_save_appProduct_ids' );
if (!function_exists('nokriAPI_save_appProduct_ids'))
{
	function nokriAPI_save_appProduct_ids( $post_id )
	{

	  	/*Bail if we're doing an auto save*/
		if( defined( 'DOING_AUTOSAVE' ) && DOING_AUTOSAVE ) return;
		 
		/*if our nonce isn't there, or we can't verify it, bail*/
		if( !isset( $_POST['meta_box_nonce_product'] ) || !wp_verify_nonce( $_POST['meta_box_nonce_product'], 'my_meta_box_nonce_product' ) ) return;
		 
		/*if our current user can't edit this post, bail*/
		if( !current_user_can( 'edit_post' ) ) return;
		
		/*Make sure your data is set before trying to save it*/
		if( isset( $_POST['package_product_code_android'] ) ){
			update_post_meta( $post_id, 'package_product_code_android', $_POST['package_product_code_android'] );		
		}
		else
		{
			update_post_meta( $post_id, 'package_product_code_android', '' );
		}
		/*For IOS */
		if( isset( $_POST['package_product_code_ios'] ) ){
			update_post_meta( $post_id, 'package_product_code_ios', $_POST['package_product_code_ios'] );		
		}
		else
		{
			update_post_meta( $post_id, 'package_product_code_ios', '' );
		}		
	}
}