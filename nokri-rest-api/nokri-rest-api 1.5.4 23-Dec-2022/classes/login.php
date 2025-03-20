<?php

/* * *********************** */
/* Login activity         */
/* * *********************** */
add_action('rest_api_init', 'nokriAPI_login_activity_hook_get', 0);

function nokriAPI_login_activity_hook_get() {

    register_rest_route('nokri/v1', '/login_activity/',
            array(
                'methods' => WP_REST_Server::READABLE,
                'callback' => 'nokriAPI_login_activity_get',
                'permission_callback' => function () { return true ;  },
            )
    );
}

if (!function_exists('nokriAPI_login_activity_get')) {

    function nokriAPI_login_activity_get() {
        $data['logo'] = nokriAPI_appLogo();
        $data['signin'] = __("Signin", "nokri-rest-api");
        $data['signup'] = __("Signup", "nokri-rest-api");

        return $response = array('success' => true, 'data' => $data, 'message' => '');
    }

}
/* * *********************** */
/* Login Get service     */
/* * *********************** */


add_action('rest_api_init', 'nokriAPI_login_api_hooks_get', 0);

function nokriAPI_login_api_hooks_get() {

    register_rest_route('nokri/v1', '/login/',
            array(
                'methods' => WP_REST_Server::READABLE,
                'callback' => 'nokriAPI_loginMe_get',
                'permission_callback' => function () { return true ;  },
            )
    );
}

if (!function_exists('nokriAPI_loginMe_get')) {

    function nokriAPI_loginMe_get() {
        global $nokriAPI;
        $fb_btn = (isset($nokriAPI['app_settings_fb_btn']) ) ? $nokriAPI['app_settings_fb_btn'] : '';
        $google_btn = (isset($nokriAPI['app_settings_google_btn']) ) ? $nokriAPI['app_settings_google_btn'] : '';
        $linkedin_btn = (isset($nokriAPI['app_settings_linkedin_btn']) ) ? $nokriAPI['app_settings_linkedin_btn'] : '';
        $apple_btn = (isset($nokriAPI['app_settings_apple_btn']) ) ? $nokriAPI['app_settings_apple_btn'] : '';

        $data['bg_color'] = '#000';
        $data['logo'] = nokriAPI_appLogo();
        $data['heading'] = __("sign in using social accounts", "nokri-rest-api");
        $data['email_placeholder'] = __("Your Email Address", "nokri-rest-api");
        $data['password_placeholder'] = __("Your Password", "nokri-rest-api");
        $data['forgot_text'] = __("Forgot Password", "nokri-rest-api");
        $data['form_btn'] = __("Submit", "nokri-rest-api");
        $data['separator'] = __("OR", "nokri-rest-api");
        $data['facebook_switch'] = $fb_btn;
        $data['facebook_btn'] = __("Facebook", "nokri-rest-api");
        $data['Apple_switch'] = $apple_btn;
        $data['Apple_btn'] = __("Continue with Apple", "nokri-rest-api");
        $data['google_switch'] = $google_btn;
        $data['google_btn'] = __("Google+", "nokri-rest-api");
        $data['linkedin_switch'] = $linkedin_btn;
        $data['linkedin_btn'] = __("Linkedin", "nokri-rest-api");
        $data['register_text'] = __("New here ?", "nokri-rest-api");
        $data['register_text2'] = __("Signup", "nokri-rest-api");


        return $response = array('success' => true, 'data' => $data, 'message' => '');
    }

}



add_action('rest_api_init', 'nokriAPI_login_api_hooks_post', 0);

function nokriAPI_login_api_hooks_post() {

    register_rest_route('nokri/v1', '/login/',
            array(
                'methods' => WP_REST_Server::EDITABLE,
                'callback' => 'nokriAPI_loginMe_post',
                'permission_callback' => function () {
                    return nokriAPI_basic_auth();
                },
            )
    );
}

if (!function_exists('nokriAPI_loginMe_post')) {

    function nokriAPI_loginMe_post($request) {

        global $nokri;
        $json_data = $request->get_json_params();
        $email = (isset($json_data['email'])) ? $json_data['email'] : '';
        $password = (isset($json_data['pass'])) ? $json_data['pass'] : '';
        $remember = (isset($json_data['remember'])) ? $json_data['remember'] : '';
        $type = (isset($json_data['type'])) ? $json_data['type'] : 'normal';
          
        $creds = array();
        $creds['remember'] = $remember;
        $creds['user_login'] = $email;
        $creds['user_password'] = $password;
       
        if ($type == 'social') {

            if (email_exists($email) == true) {
                $user = get_user_by('email', $email);
                if ($user) {
                    //NOKRI_API_ALLOW_EDITING
                    $mode = nokriAPI_allow_editing($request->get_method());
                    if (isset($mode) && count($mode) > 0) {
                        return $mode;
                    }
                    $user_id = $user->ID;
                    $profile_arr = array();
                    $profile_arr['id'] = $user->ID;
                    $profile_arr['acount_type'] = get_user_meta($user->ID, '_sb_reg_type', true);
                    $profile_arr['user_email'] = $user->user_email;
                    $profile_arr['display_name'] = $user->display_name;
                    $profile_arr['phone'] = get_user_meta($user->ID, '_sb_contact', true);
                    $profile_arr['profile_img'] = nokriAPI_user_dp($user->ID);
                    $response = array('success' => true, 'data' => $profile_arr, 'message' => __("Login Successful", "nokri-rest-api"));
                } else {
                    $response = array('success' => true, 'data' => '', 'message' => __("Something went wrong", "nokri-rest-api"));
                }
            } else {
                $password = mt_rand(1000, 999999);
                $uid = nokriAPI_do_register($email, $password);
               
                    // Email for new user
                    if (function_exists('nokri_email_on_new_user')) {
                     
                        nokri_email_on_new_user($uid, $password);
                    }
             
                $user = get_user_by('id', $uid);
                if ($user) {
                     $user_id = $user->ID;
                    $profile_arr = array();
                    $profile_arr['id'] = $user->ID;
                    $profile_arr['acount_type'] = get_user_meta($user->ID, '_sb_reg_type', true);
                    $profile_arr['user_email'] = $user->user_email;
                    $profile_arr['display_name'] = $user->display_name;
                    $profile_arr['phone'] = get_user_meta($user->ID, '_sb_contact', true);
                    $profile_arr['profile_img'] = nokriAPI_user_dp($user->ID);
                    $response = array('success' => true, 'data' => $profile_arr, 'message' => __("Register and login Successful", "nokri-rest-api"));
                }
                else{
                 $response = array('success' => true, 'data' =>'', 'message' => __("Something went wrong", "nokri-rest-api"));
            }}
        } else {
            $user = wp_signon($creds, false);
            if (is_wp_error($user)) {
                $response = array('success' => false, 'data' => '', 'message' => __("Invalid Login Details", "nokri-rest-api"));
            } else {
                
                
                $profile_arr = array();
                $profile_arr['id'] = $user->ID;
                $profile_arr['user_type'] = get_user_meta($user->ID, '_sb_reg_type', true);
                $profile_arr['user_email'] = $user->user_email;
                $profile_arr['display_name'] = $user->display_name;
                $profile_arr['phone'] = get_user_meta($user->ID, '_sb_contact', true);
                $profile_arr['profile_img'] = nokriAPI_user_dp($user->ID);
                $cand_linked = get_user_meta($user->ID, '_cand_linked', true);
                $cand_linked = $cand_linked != "" ? $cand_linked : "";
                $profile_arr['linked_url'] = $cand_linked;


                $role = (array) $user->roles;
                $res = nokri_auto_login($user->user_email, $password, $remember);
                if ($role[0] == 'subscriber') {
                    $response = array('success' => true, 'data' => $profile_arr, 'message' => __("Login Successful", "nokri-rest-api"));
                } else {
                    $response = array('success' => false, 'data' => '', 'message' => __("Account not verified", "nokri-rest-api"));
                }
            }
        }
        return $response;
    }

}





add_action('rest_api_init', 'nokriAPI_set_acount_type_hook', 0);

function nokriAPI_set_acount_type_hook() {


    register_rest_route('nokri/v1', '/set_acount/',
            array(
                'methods' => WP_REST_Server::READABLE,
                'callback' => 'nokriAPI_set_acount_type_get',
                'permission_callback' => function () {
                    return nokriAPI_basic_auth();
                },
            )
    );


    register_rest_route('nokri/v1', '/set_acount/',
            array(
                'methods' => WP_REST_Server::EDITABLE,
                'callback' => 'nokriAPI_set_acount_type',
                'permission_callback' => function () {
                    return nokriAPI_basic_auth();
                },
            )
    );
}

if (!function_exists('nokriAPI_set_acount_type_get')) {

    function nokriAPI_set_acount_type_get() {

        $data['desc'] = __("Select your account type", "nokri-rest-api");
        $data['btn_emp'] = __("Employer", "nokri-rest-api");
        $data['btn_cand'] = __("Candidate", "nokri-rest-api");
        $data['continue'] = __("Continue", "nokri-rest-api");


        return $response = array('success' => true, 'data' => $data, 'message' => '');
    }

}

if (!function_exists('nokriAPI_set_acount_type')) {

    function nokriAPI_set_acount_type($request) {

        global $nokri;
        $json_data = $request->get_json_params();

        $type = (isset($json_data['user_type'])) ? $json_data['user_type'] : '';
        $user_id = (isset($json_data['user_id'])) ? $json_data['user_id'] : '';

        if ($type != '') {
            update_user_meta($user_id, '_sb_reg_type', $type);
            $product_id = nokri_assign_free_package();
            if (isset($product_id) && $product_id != '') {
                if (isset($nokri['user_assign_pkg']) && $nokri['user_assign_pkg'] == '1' && $type == '1') {
                    $is_pkg_free = get_post_meta($product_id, 'op_pkg_typ', true);
                    if ($is_pkg_free == 1) {
                        nokri_free_package($product_id, $user_id);
                    }
                }
            }
            /* Assign package to candidate */
            $product_cand_id = nokri_candidate_assign_free_package();
            if (isset($product_cand_id) && $product_cand_id != '') {
                if (isset($nokri['cand_assign_pkg']) && $nokri['cand_assign_pkg'] == '1' && $type == '0') {
                    $is_pkg_free = get_post_meta($product_cand_id, 'op_pkg_typ', true);
                    if ($is_pkg_free == 1) {
                        nokri_free_package_for_candidate($product_cand_id, $user_id);
                    }
                }
            }
        }


        return $response = array('success' => true, 'data' => '', 'message' => __("Successfully created", "nokri-rest-api"));
    }

}

/* add_action('wp_login', 'nokriAPI_setLastLogin'); */
if (!function_exists('nokriAPI_setLastLogin')) {

    function nokriAPI_setLastLogin($login, $user) {
        $cur_user = get_user_by('login', $login);
        update_user_meta($cur_user->ID, '_sb_last_login', time());
    }

}

if (!function_exists('nokriAPI_setLastLogin2')) {

    function nokriAPI_setLastLogin2($userID = '') {
        update_user_meta($userID, '_sb_last_login', time());
    }

}

/* * ********************* */
/* Forgot Password Post */
/* * ********************* */

add_action('rest_api_init', 'nokriAPI_profile_forgotpass_hooks_post', 0);

function nokriAPI_profile_forgotpass_hooks_post() {

    register_rest_route(
            'nokri/v1', '/forgot/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_profile_forgotpass_post',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_profile_forgotpass_post')) {

    function nokriAPI_profile_forgotpass_post($request) {
        $user = wp_get_current_user();
        $user_id = $user->data->ID;

        $json_data = $request->get_json_params();
        $email = (isset($json_data['email'])) ? trim($json_data['email']) : '';

        if (NOKRI_API_ALLOW_EDITING == false) {
            $response = array('success' => false, 'data' => '', 'message' => __("Editing Not Allowed In Demo", "nokri-rest-api"));
            return $response;
        }
        if (email_exists($email) == true) {
            $response = nokriAPI_forgot_pass_email_link($email);
        } else {
            $success = false;
            $message = __('Email is not resgistered with us.', 'nokri-rest-api');
            $response = array('success' => $success, 'data' => '', 'message' => $message);
        }
        return $response;
    }

}


/* * ********************* */
/* Reset Password */
/* * ********************* */

add_action('rest_api_init', 'nokriAPI_reset_password_api_hooks_get', 0);

function nokriAPI_reset_password_api_hooks_get() {

    register_rest_route('nokri/v1', '/reset_password/',
            array(
                'methods' => WP_REST_Server::READABLE,
                'callback' => 'nokriAPI_reset_password_api_get',
                'permission_callback' => function () {
                    return nokriAPI_basic_auth();
                },
            )
    );

    register_rest_route('nokri/v1', '/reset_password/',
            array(
                'methods' => WP_REST_Server::EDITABLE,
                'callback' => 'nokriAPI_reset_password_api_post',
                'permission_callback' => function () {
                    return nokriAPI_basic_auth();
                },
            )
    );
}

/* GET */
if (!function_exists('nokriAPI_reset_password_api_get')) {

    function nokriAPI_reset_password_api_get() {
        $data['logo'] = nokriAPI_appLogo();
        $data['old_password'] = __("Enter current password", "nokri-rest-api");
        $data['new_password'] = __("Enter new password", "nokri-rest-api");
        $data['confirm_password'] = __("Enter confirm password", "nokri-rest-api");
        $data['ok'] = __("Submit", "nokri-rest-api");
        $data['cancel'] = __("Cancel", "nokri-rest-api");

        return $response = array('success' => true, 'data' => $data, 'message' => '');
    }

}


/* Post */
if (!function_exists('nokriAPI_reset_password_api_post')) {

    function nokriAPI_reset_password_api_post($request) {
        $user_id = get_current_user_id();

        $json_data = $request->get_json_params();
        $old_password = (isset($json_data['old_password'])) ? trim($json_data['old_password']) : '';
        $new_password = (isset($json_data['new_password'])) ? trim($json_data['new_password']) : '';
        $confirm_password = (isset($json_data['confirm_password'])) ? trim($json_data['confirm_password']) : '';

        $user = get_user_by('ID', $user_id);
        if ($user && wp_check_password($old_password, $user->data->user_pass, $user->ID)) {
            if ($new_password != $confirm_password) {
                $message = __('Password mismatched', 'nokri-rest-api');

                return $response = array('success' => false, 'data' => '', 'message' => $message);
            }

            if ($confirm_password != '') {
                wp_set_password($confirm_password, $user_id);
                $message = __('Password updated successfully', 'nokri-rest-api');

                return $response = array('success' => true, 'data' => '', 'message' => $message);
            }
        } else {
            $message = __('Invalid old password', 'nokri-rest-api');

            return $response = array('success' => false, 'data' => '', 'message' => $message);
        }
    }

}
/* * ************************* */
/* Deactivate acount  */
/* * ************************ */

add_action('rest_api_init', 'nokriAPI_profile_gdpr_delete_user_hook', 0);

function nokriAPI_profile_gdpr_delete_user_hook() {

    register_rest_route('nokri/v1', '/deactivate_my_acount/',
            array(
                'methods' => WP_REST_Server::EDITABLE,
                'callback' => 'nokriAPI_profile_gdpr_delete_user',
                'permission_callback' => function () {
                    return nokriAPI_basic_auth();
                },
            )
    );
}

if (!function_exists('nokriAPI_profile_gdpr_delete_user')) {

    function nokriAPI_profile_gdpr_delete_user($request) {
        global $nokriAPI;
        /* For Redux */
        $json_data = $request->get_json_params();
        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }

        $user_id = (isset($json_data['user_id']) && $json_data['user_id'] != "" ) ? $json_data['user_id'] : '';
        $current_user = get_current_user_id();
        $success = false;
        $message = __("Something went wrong.", "nokri-rest-api");
        $if_user_exists = nokriAPI_user_id_exists($user_id);


        if ($if_user_exists && $user_id == $current_user) {
            if (current_user_can('administrator')) {
                $success = false;
                $message = __("Admin can not delete his account from here.", "nokri-rest-api");
            } else {
                if (is_multisite()) {
                    require_once('./wp-admin/includes/ms.php');
                    $user_delete = wpmu_delete_user($user_id);
                    wpmu_delete_user($user_id);
                }
                wp_delete_user($user_id);
                $success = true;
                $message = __("You account has been deleted successfully.", "nokri-rest-api");
            }
        }
        return array('success' => $success, 'data' => '', 'message' => $message);
    }

}
if (!function_exists('nokriAPI_user_id_exists')) {

    function nokriAPI_user_id_exists($user) {

        global $wpdb;

        $count = $wpdb->get_var($wpdb->prepare("SELECT COUNT(*) FROM $wpdb->users WHERE ID = %d", $user));

        if ($count == 1) {
            return true;
        } else {
            return false;
        }
    }

}