<?php

/* * ********************************** */
/* Getting Employer Profile  */
/* * ********************************** */

add_action('rest_api_init', 'nokriAPI_employer_update_profile_hooks', 0);

function nokriAPI_employer_update_profile_hooks() {

    register_rest_route('nokri/v1', '/employer/get_profile/',
            array(
                'methods' => WP_REST_Server::READABLE,
                'callback' => 'nokriAPI_employer_get_profile',
                'permission_callback' => function () {
                    return nokriAPI_basic_auth();
                },
            )
    );

    register_rest_route('nokri/v1', '/employer/update_profile/',
            array(
                'methods' => WP_REST_Server::EDITABLE,
                'callback' => 'nokriAPI_employer_update_profile',
                'permission_callback' => function () {
                    return nokriAPI_basic_auth();
                },
            )
    );
}

if (!function_exists('nokriAPI_employer_get_profile')) {

    function nokriAPI_employer_get_profile($user_id = '', $return_arr = false) {
        global $nokri;
        global $nokriAPI;



        if (is_numeric($user_id)) {
            $user = get_userdata($user_id);
            $user_id = (int) $user->ID;
        } else {
            $user_id = (int) get_current_user_id();
            $user = get_userdata($user_id);
        }
        $registered = $user->data->user_registered;



        /* Map default values */
        $map_switch = isset($nokri['emp_map_switch']) ? $nokri['emp_map_switch'] : "0";
        $emp_map_long = get_user_meta($user->data->ID, '_emp_map_long', true);
        if ($emp_map_long == '') {
            $emp_map_long = $nokriAPI['sb_default_long'];
        }
        $emp_map_lat = get_user_meta($user->data->ID, '_emp_map_lat', true);
        if ($emp_map_lat == '') {
            $emp_map_lat = $nokriAPI['sb_default_lat'];
        }




        $user_name = $user->data->display_name != '' ? $user->data->display_name : '';
        $user_email = $user->user_email != '' ? $user->user_email : '';

        $contact = get_user_meta($user->ID, '_sb_contact', true) != '' ? get_user_meta($user->ID, '_sb_contact', true) : '';
        $user_heading = get_user_meta($user->ID, '_user_headline', true) != '' ? get_user_meta($user->ID, '_user_headline', true) : '';
        $emp_no = get_user_meta($user->ID, '_emp_nos', true) != '' ? get_user_meta($user->ID, '_emp_nos', true) : '';
        $emp_map_location = get_user_meta($user->ID, '_emp_map_location', true) != '' ? get_user_meta($user->ID, '_emp_map_location', true) : '';
        $emp_intro = get_user_meta($user->ID, '_emp_intro', true) != '' ? get_user_meta($user->ID, '_emp_intro', true) : '';
        $emp_port_video = get_user_meta($user->ID, '_emp_video', true) != '' ? get_user_meta($user->ID, '_emp_video', true) : '';
        $emp_web = get_user_meta($user->ID, '_emp_web', true);
        $info[] = nokriAPI_employer_fields(__("Your DashBoard", "nokri-rest-api"), '', 'textfield', true, '', 'your_dashbord');


        $video = false;

        $emp_video = '';
        if ($emp_port_video != '') {
            $rx = '~
							  ^(?:https?://)?                           # Optional protocol
							   (?:www[.])?                              # Optional sub-domain
							   (?:youtube[.]com/watch[?]v=|youtu[.]be/) # Mandatory domain name (w/ query string in .com)
							   ([^&]{11})                               # Video id of 11 characters as capture group 1
								~x';
            $valid = preg_match($rx, $emp_port_video, $matches);
            $emp_video = $matches[1];
            $video = true;
        }




        $info[] = nokriAPI_employer_fields(__("Company name", "nokri-rest-api"), $user_name, 'textfield', true, '', 'emp_name');
        $info[] = nokriAPI_employer_fields(__("Email", "nokri-rest-api"), $user_email, 'textfield', true, '', 'emp_email');
        $info[] = nokriAPI_employer_fields(__("Phone", "nokri-rest-api"), $contact, 'textfield', true, '', 'emp_phone');
        $info[] = nokriAPI_employer_fields(__("Member Since", "nokri-rest-api"), date("M Y", strtotime($registered)), 'textfield', true, '', 'emp_rgstr');
        $info[] = nokriAPI_employer_fields(__("Headline", "nokri-rest-api"), $user_heading, 'textfield', true, '', 'emp_head');
        $info[] = nokriAPI_employer_fields(__("No. of Employees ", "nokri-rest-api"), $emp_no, 'textfield', true, '', 'emp_nos');
        $info[] = nokriAPI_employer_fields(__("Address", "nokri-rest-api"), $emp_map_location, 'textfield', true, '', 'emp_adress');
        $info[] = nokriAPI_employer_fields(__("About Company", "nokri-rest-api"), $emp_intro, 'textarea', true, '', 'about_me');
        $info[] = nokriAPI_employer_fields(__("Location & Map", "nokri-rest-api"), '', 'textfield', true, '', 'loc');
        $info[] = nokriAPI_employer_fields(__("Longitude", "nokri-rest-api"), $emp_map_long, 'textfield', true, '', 'emp_long');
        $info[] = nokriAPI_employer_fields(__("Latitude", "nokri-rest-api"), $emp_map_lat, 'textfield', true, '', 'emp_lat');
        $info[] = nokriAPI_employer_fields(__("Website", "nokri-rest-api"), $emp_web, 'textfield', true, '', 'emp_web');




        $custom_feild_id = (isset($nokri['custom_registration_feilds'])) ? $nokri['custom_registration_feilds'] : '';
        $custom_filed_data = '';
        if ($custom_feild_id) {
            $custom_filed_data = nokri_get_emp_register_custom_feildsApi($user_id, 'Registration', $custom_feild_id, '', '');
        }
        $info = !empty($custom_filed_data) ? array_merge($info, $custom_filed_data) : $info;
        $data['info'] = $info;
        if (get_user_meta($user_id, '_comp_gallery', true) != "") {
            $port = get_user_meta($user_id, '_comp_gallery', true);
            $portfolios = explode(',', $port);
            foreach ($portfolios as $portfolio) {
                $portfolio_image = wp_get_attachment_image_src($portfolio, '');
                $data['gal_images'][] = nokriAPI_canidate_fields('Img_url', $portfolio_image[0], $portfolio, '', 1, 'Img_url');
            }
        }
        $data['profile_img'] = nokriAPI_employer_dp($user->ID);
        $data['cvr_img'] = nokriAPI_employer_cover($user->ID);
        $data['skills'] = nokriAPI_emp_skills_tags($user->ID);
        $data['social'] = nokriAPI_emp_social_icons($user->ID);
        $data['extra'][] = nokriAPI_employer_fields(__("Change Password", "nokri-rest-api"), __("Change Password", "nokri-rest-api"), 'textfield', true, '', 'change_password');
        $data['extra'][] = nokriAPI_employer_fields(__("Skills", "nokri-rest-api"), __("Skills", "nokri-rest-api"), 'textfield', true, '', 'emp_skills');
        $data['extra'][] = nokriAPI_employer_fields(__("Not Added", "nokri-rest-api"), __("You have not written about yourself", "nokri-rest-api"), 'textfield', true, '', 'emp_about');
        $data['extra'][] = nokriAPI_employer_fields(__("Not added", "nokri-rest-api"), __("You have not selected any category", "nokri-rest-api"), 'textfield', true, '', 'emp_not_skills');
        $data['extra'][] = nokriAPI_employer_fields(__("Employer Video", "nokri-rest-api"), $emp_video, 'textfield', true, esc_html__('You have added no video', 'nokri-rest-api'), 'video_url');
        $data['extra'][] = nokriAPI_employer_fields(__("My Portfolio", "nokri-rest-api"), '', 'textfield', true, esc_html__('You have not added any portfolio images', 'nokri-rest-api'), 'port_text');

        $data['extra'][] = nokriAPI_employer_fields('', $map_switch, 'textfield', true, '', 'map_switch');
        $del_acount = false;
        if ((isset($nokriAPI['deactivate_app_acount'])) && $nokriAPI['deactivate_app_acount'] == '1') {
            $del_acount = true;
        }
        $data['extra'][] = nokriAPI_canidate_fields(__("Delete account", "nokri-rest-api"), __("Delete acount", "nokri-rest-api"), 'textfield', $del_acount, '', 'del_acount');


        $message = __("Edit", "nokri-rest-api");

        $response = array('success' => true, 'data' => $data, 'message' => $message);

        return ($return_arr) ? $data : $response;
    }

}
/* * ************************************************** */
/* Employer Updating Personal Information           */
/* * ************************************************ */



add_action('rest_api_init', 'nokriAPI_employer_edit_personal_info_hook', 0);

function nokriAPI_employer_edit_personal_info_hook() {
    register_rest_route(
            'nokri/v1', '/employer/update_personal_info/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_employer_update_personal_info',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );

    register_rest_route(
            'nokri/v1', '/employer/update_personal_info/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_employer_update_personal_info',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_employer_update_personal_info')) {

    function nokriAPI_employer_update_personal_info($request) {
        global $nokriAPI;
        global $nokri;
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();

        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }

        /* Profile setting option */
        $is_show_profile_option = (isset($nokriAPI['user_profile_setting_option_API']) && $nokriAPI['user_profile_setting_option_API']) ? true : false;

        /* Profile setting */
        $profile_dropdown_name = array("pub" => __("Public", "nokri-rest-api"), "priv" => __("Private", "nokri-rest-api"),
        );
        foreach ($profile_dropdown_name as $key => $value) {
            $selected = ( $key == get_user_meta($user_id, '_user_profile_status', true) ) ? true : false;
            $option[] = array(
                "key" => $key,
                "value" => esc_html($value),
                "selected" => $selected
            );
        }



        $emp_name = (isset($json_data['emp_name'])) ? trim($json_data['emp_name']) : '';
        $emp_phone = (isset($json_data['emp_phone'])) ? trim($json_data['emp_phone']) : '';
        $emp_headline = (isset($json_data['emp_headline'])) ? trim($json_data['emp_headline']) : '';
        $emp_web = (isset($json_data['emp_web'])) ? trim($json_data['emp_web']) : '';
        $emp_skills = (isset($json_data['emp_skills'])) ? trim($json_data['emp_skills']) : '';
        $emp_intro = (isset($json_data['emp_intro'])) ? trim($json_data['emp_intro']) : '';
        $emp_prof_stat = (isset($json_data['emp_prof_stat'])) ? trim($json_data['emp_prof_stat']) : '';


        $m_name = __("Name:", "nokri-rest-api");
        $data[] = nokriAPI_employer_fields($m_name, @$user->display_name, 'textfield', true, 2, 'emp_name');

        $m_phone = __("Phone:", "nokri-rest-api");
        $data[] = nokriAPI_employer_fields($m_phone, get_user_meta($user_id, '_sb_contact', true), 'textfield', true, 2, 'emp_phone');

        $m_email = __("Email:", "nokri-rest-api");
        $data[] = nokriAPI_employer_fields($m_email, @$user->user_email, 'textfield', true, 2, 'emp_email');

        $m_headline = __("Headline:", "nokri-rest-api");
        $data[] = nokriAPI_employer_fields($m_headline, get_user_meta($user_id, '_user_headline', true), '', true, 2, 'emp_headline');


        $m_web = __("Web:", "nokri-rest-api");
        $data[] = nokriAPI_employer_fields($m_web, get_user_meta($user_id, '_emp_web', true), '', true, 2, 'emp_web');

        $m_profile = __("Set your profile:", "nokri-rest-api");
        $data[] = nokriAPI_employer_fields($m_profile, $option, '', $is_show_profile_option, 2, 'emp_prof_stat');

        $m_profile_img = __("Profile Image:", "nokri-rest-api");
        $data[] = nokriAPI_employer_fields($m_profile_img, nokriAPI_employer_dp($user_id), 'dropdown', true, 2, 'emp_dp');


        $m_intro = __("About company:", "nokri-rest-api");
        $data[] = nokriAPI_employer_fields($m_intro, get_user_meta($user_id, '_emp_intro', true), 'textarea', true, 2, 'emp_intro');

        $custom_filed = (isset($json_data['custom_fields'])) ? (array) ($json_data['custom_fields'] ) : array();


        $request_from = nokriAPI_getSpecific_headerVal('Nokri-Request-From');



        if ($request_from == 'ios') {
            $custom_filed = json_decode(@$json_data['custom_fields'], true);
        }

        /* Updating Values In User Meta Of Current User */

        if ($emp_name != "") {
            wp_update_user(array('ID' => $user_id, 'display_name' => $emp_name));
        }

        if ($emp_phone != '') {
            update_user_meta($user_id, '_sb_contact', $emp_phone);
        }
        if ($emp_headline != '') {
            update_user_meta($user_id, '_user_headline', $emp_headline);
        }
        
            update_user_meta($user_id, '_emp_web', $emp_web);
       
        if (!empty($emp_skills)) {
            update_user_meta($user_id, '_emp_skills', $emp_skills);
        }
        if ($emp_intro != '') {
            update_user_meta($user_id, '_emp_intro', $emp_intro);
        }

        if ($is_show_profile_option) {
            if ($emp_prof_stat != '') {
                update_user_meta($user_id, '_user_profile_status', $emp_prof_stat);
            }
        } else {
            update_user_meta($user_id, '_user_profile_status', 'pub');
        }

        if (isset($custom_filed) && count($custom_filed) > 0) {

            foreach ($custom_filed as $key => $val) {
                if (is_array($val)) {
                    $dataArr = array();
                    foreach ($val as $k)
                        $dataArr[] = $k;
                    $val = stripslashes(json_encode($dataArr, JSON_UNESCAPED_UNICODE));
                }
                $dataVal = ltrim($val, ",");

                update_user_meta($user_id, $key, sanitize_text_field($val));
            }
        }

        $extras[] = nokriAPI_canidate_fields('Section name', __("Personal Information", "nokri-rest-api"), '', '', 1, 'section_name');
        $extras[] = nokriAPI_canidate_fields('Button name', __("Save Information", "nokri-rest-api"), '', '', 1, 'btn_name');

        $extras[] = nokriAPI_canidate_fields('Change password', __("Update password", "nokri-rest-api"), '', '', 1, 'change_pasword');

        $extras[] = nokriAPI_canidate_fields('Dell Account', __("Delete account?", "nokri-rest-api"), '', '', 1, 'del_acount');

        $custom_feild_id = (isset($nokri['custom_registration_feilds'])) ? $nokri['custom_registration_feilds'] : '';
        $custom_filed_data = array();
        if ($custom_feild_id != '') {
            $custom_filed_data = nokri_get_register_custom_feildsApi('', 'Registration', $custom_feild_id, '', '');
        }

        $custom_emp_feild_id = (isset($nokri['custom_employer_feilds'])) ? $nokri['custom_employer_feilds'] : '';
        $custom_emp_filed_data = '';
        if ($custom_emp_feild_id != '') {
            $custom_emp_filed_data = nokri_get_register_custom_feildsApi('', 'Candidate', $custom_cand_filed_data, '', '');
        }
        $custom_fields = '';


        if (!empty($custom_filed_data)) {
            $custom_fields = $custom_filed_data;
        } else if (!empty($custom_emp_filed_data)) {
            $custom_fields = $custom_emp_filed_data;
        }
        if (!empty($custom_filed_data) && !empty($custom_cand_filed_data)) {

            $custom_fields = array_merge($custom_filed_data, $custom_emp_filed_data);
        }


        $response = array('success' => true, 'data' => $data, 'message' => __("Personal Information Updated.", "nokri-rest-api"), 'extras' => $extras, 'custom_fields' => $custom_fields);

        return $response;
    }

}



/* * *************************************** */
/* Getting Selected Skills For Employer */
/* * *************************************** */
if (!function_exists('nokriAPI_emp_skills_tags')) {

    function nokriAPI_emp_skills_tags($user_id = '') {
        /* Getting User Skills Tags */
        $cand_skills = get_user_meta($user_id, '_emp_skills', true);

        $data = array();
        if (isset($cand_skills) && is_array($cand_skills)) {
            $taxonomies = get_terms('emp_specialization', array('hide_empty' => false, 'orderby' => 'id', 'order' => 'ASC', 'parent' => 0));
            if (count($taxonomies) > 0) {
                foreach ($taxonomies as $taxonomy) {
                    if (in_array($taxonomy->term_id, $cand_skills))
                        $data[] = array("key" => $taxonomy->term_id, "value" => esc_html($taxonomy->name));
                }
            }
        }
        return $data;
    }

}

if (!function_exists('nokriAPI_employer_fields')) {

    function nokriAPI_employer_fields($key = '', $value = '', $fieldname = 'textfield', $is_required = false, $column = 1, $fieldTypename = '') {

        return array("key" => $key, "value" => $value, "fieldname" => $fieldname, "is_required" => $is_required, "column" => $column, "field_type_name" => $fieldTypename);
    }

}

add_action('rest_api_init', 'nokriAPI_employer_profile_hook', 0);

function nokriAPI_employer_profile_hook() {
    register_rest_route(
            'nokri/v1', '/employer/profile/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_employer_profile_get',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_employer_profile_get')) {

    function nokriAPI_employer_profile_get($user_id = '') {

        if ($user_id != "") {
            $user_id = get_current_user_id();
            $user = get_userdata($user_id);
        } else {
            $user_id = $user_id;
        }





        $data[] = nokriAPI_canidate_fields(__("Name", "nokri-rest-api"), $user->display_name, 'textfield', true);
        $data[] = nokriAPI_canidate_fields(__("Email", "nokri-rest-api"), $user->user_email, 'textfield', true);
        $data[] = nokriAPI_canidate_fields(__("Phone", "nokri-rest-api"), get_user_meta($user->ID, '_sb_contact', true), 'textfield', true);
        $data[] = nokriAPI_canidate_fields(__("Establish", "nokri-rest-api"), get_user_meta($user->ID, '_emp_est', true), 'calendor', true);
        $data[] = nokriAPI_canidate_fields(__("Employees", "nokri-rest-api"), get_user_meta($user->ID, '_emp_nos', true), 'textfield', true);
        $data[] = nokriAPI_canidate_fields(__("headline", "nokri-rest-api"), get_user_meta($user->ID, '_emp_headline', true), 'textfield', true);
        $data[] = nokriAPI_canidate_fields(__("adress", "nokri-rest-api"), get_user_meta($user->ID, '_emp_map_location', true), 'textfield', true);
        $data[] = nokriAPI_canidate_fields(__("dp", "nokri-rest-api"), nokriAPI_employer_dp($user->ID), 'fileinput', true);
        $data[] = nokriAPI_canidate_fields(__("cover", "nokri-rest-api"), nokriAPI_employer_cover($user->ID), 'fileinput', true);
        $data[] = nokriAPI_canidate_fields(__("description", "nokri-rest-api"), get_user_meta($user->ID, '_cand_intro', true), 'textfield', true);


        return $data;
    }

}




/* * ********************************** */
/*  Social Icons Function            */
/* * ********************************** */


if (!function_exists('nokriAPI_emp_social_icons')) {

    function nokriAPI_emp_social_icons($user_id = '') {

        global $nokriAPI;
        $current_user_id = get_current_user_id();
        $is_show = (isset($nokriAPI['user_contact_social_API']) && $nokriAPI['user_contact_social_API']) ? true : true;
        if (!$is_show && $user_id != $current_user_id) {
            $is_show = false;
        }
        $data['is_show'] = $is_show;
        $data['facebook'] = get_user_meta($user_id, '_emp_fb', true);
        $data['twitter'] = get_user_meta($user_id, '_emp_twitter', true);
        $data['linkedin'] = get_user_meta($user_id, '_emp_linked', true);
        $data['google_plus'] = get_user_meta($user_id, '_emp_google', true);
        return $data;
    }

}



/* * ******************************** */
/* Candidate Updating Social Link  */
/* * ******************************* */



add_action('rest_api_init', 'nokriAPI_emp_edit_social_link_hook', 0);

function nokriAPI_emp_edit_social_link_hook() {
    register_rest_route(
            'nokri/v1', '/employer/update_social_link/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_emp_update_social_link',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );

    register_rest_route(
            'nokri/v1', '/employer/update_social_link/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_emp_update_social_link',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_emp_update_social_link')) {

    function nokriAPI_emp_update_social_link($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();

        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }

        $emp_fb = (isset($json_data['emp_fb'])) ? trim($json_data['emp_fb']) : '';
        $emp_twiter = (isset($json_data['emp_twiter'])) ? trim($json_data['emp_twiter']) : '';
        $emp_linked = (isset($json_data['emp_linked'])) ? trim($json_data['emp_linked']) : '';
        $emp_google = (isset($json_data['emp_google'])) ? trim($json_data['emp_google']) : '';




        $fb = __("Facebook:", "nokri-rest-api");
        $data[] = nokriAPI_employer_fields($fb, get_user_meta($user_id, '_emp_fb', true), 'textfield', true, 2, 'emp_fb');

        $twitter = __("Twitter:", "nokri-rest-api");
        $data[] = nokriAPI_employer_fields($twitter, get_user_meta($user_id, '_emp_twitter', true), 'textfield', true, 2, 'emp_twiter');


        $linked = __("LinkedIn:", "nokri-rest-api");
        $data[] = nokriAPI_employer_fields($linked, get_user_meta($user_id, '_emp_linked', true), 'textfield', true, 2, 'emp_linked');

        $google = __("Instagram:", "nokri-rest-api");
        $data[] = nokriAPI_employer_fields($google, get_user_meta($user_id, '_emp_google', true), 'textfield', true, 2, 'emp_google');





        /* Updating Values In User Meta Of Current User */

        if ($emp_fb != "") {
            update_user_meta($user_id, '_emp_fb', $emp_fb);
        }
        if ($emp_twiter != '') {
            update_user_meta($user_id, '_emp_twitter', $emp_twiter);
        }
        if ($emp_linked != '') {
            update_user_meta($user_id, '_emp_linked', $emp_linked);
        }
        if ($emp_google != '') {
            update_user_meta($user_id, '_emp_google', $emp_google);
        }


        $fb = __("https://www.facebook.com", "nokri-rest-api");
        $tw = __("https://www.twitter.com", "nokri-rest-api");
        $lk = __("https://www.linkedin.com", "nokri-rest-api");
        $g = __("https://www.instagram.com/", "nokri-rest-api");


        $extras[] = nokriAPI_employer_fields('', $fb, 'textfield', true, 2, 'fb_txt');

        $extras[] = nokriAPI_employer_fields('', $tw, 'textfield', true, 2, 'tw_txt');

        $extras[] = nokriAPI_employer_fields('', $lk, 'textfield', true, 2, 'lk_txt');

        $extras[] = nokriAPI_employer_fields('', $g, 'textfield', true, 2, 'g+_txt');



        $extras[] = nokriAPI_employer_fields('', __("Social Links", "nokri-rest-api"), 'textfield', true, 2, 'page_txt');
        $extras[] = nokriAPI_employer_fields('', __("Save Links", "nokri-rest-api"), 'textfield', true, 2, 'btn_txt');



        $response = array('success' => true, 'data' => $data, 'message' => __("Social links updated.", "nokri-rest-api"), 'extras' => $extras);
        return $response;
    }

}







/* * ********************************** */
/* Getting Employer Cover/DP Text  */
/* * ********************************** */

add_action('rest_api_init', 'nokriAPI_profile_api_cvr_dp_txt_hook', 0);

function nokriAPI_profile_api_cvr_dp_txt_hook() {
    register_rest_route(
            'nokri/v1', '/employer/cover_dp', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_emp_cvr_dp_txt',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_emp_cvr_dp_txt')) {

    function nokriAPI_emp_cvr_dp_txt() {


        $data['extra'][] = nokriAPI_employer_fields('dp text', __("Profile Image", "nokri-rest-api"), 'textfield', true, 1, 'dp_text');
        $data['extra'][] = nokriAPI_employer_fields('dp upload', __("Upload Profile Image", "nokri-rest-api"), 'textfield', true, 1, 'dp_upload');
        $data['extra'][] = nokriAPI_employer_fields('cvr text', __("Cover Image", "nokri-rest-api"), 'textfield', true, 1, 'cvr_text');
        $data['extra'][] = nokriAPI_employer_fields('cvr upload', __("Upload Cover Image", "nokri-rest-api"), 'textfield', true, 1, 'cvr_upload');
        $data['extra'][] = nokriAPI_employer_fields('btn text', __("Save Profile", "nokri-rest-api"), 'textfield', true, 1, 'btn_txt');


        $response = array('success' => true, 'data' => $data, 'message' => '');

        return $response;
    }

}







/* * ********************************** */
/* Getting Employer Profile Picture Function */
/* * ********************************** */

if (!function_exists('nokriAPI_employer_dp')) {

    function nokriAPI_employer_dp($user_id = '') {
        global $nokriAPI;
        /* Getting Candidate Dp */
        $image_dp_link = get_template_directory_uri() . '/images/company-logo.jpg';
        if (isset($nokriAPI['sb_user_dp']['url']) && $nokriAPI['sb_user_dp']['url'] != "") {
            $image_dp_link = array($nokriAPI['sb_user_dp']['url']);
            $image_dp_link = $image_dp_link[0];
        }
        if (get_user_meta($user_id, '_sb_user_pic', true) != "") {
            $attach_dp_id = get_user_meta($user_id, '_sb_user_pic', true);
            $image_dp_link = wp_get_attachment_image_src($attach_dp_id, '');
            $image_dp_link = $image_dp_link[0];
        }
        $data['img'] = $image_dp_link;
        $data['extra'] = __("Profile picture updated successfully", "nokri-rest-api");
        return $data;
    }

}



/* * ********************************** */
/* Getting Employer Cover Photo Function */
/* * ********************************** */

if (!function_exists('nokriAPI_employer_cover')) {

    function nokriAPI_employer_cover($user_id = '') {

        /* Getting Candidate Cover Photo */
        $image_cover_link = get_template_directory_uri() . '/images/sample-cover.jpg';
        if (get_user_meta($user_id, '_sb_user_cover', true) != "") {
            $attach_cover_id = get_user_meta($user_id, '_sb_user_cover', true);
            $image_cover_link = wp_get_attachment_image_src($attach_cover_id, '');
            $image_cover_link = $image_cover_link[0];
        }

        $data['img'] = $image_cover_link;

        return $data;
    }

}



/* * ********************************** */
/* Change Employer Cover Photo  */
/* * ********************************** */

add_action('rest_api_init', 'nokriAPI_profile_api_update_emp_cover', 0);

function nokriAPI_profile_api_update_emp_cover() {
    register_rest_route(
            'nokri/v1', '/employer/cover', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_emp_update_cover',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_emp_update_cover')) {

    function nokriAPI_emp_update_cover($request) {

        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        require_once ABSPATH . 'wp-admin/includes/image.php';
        require_once ABSPATH . 'wp-admin/includes/file.php';
        require_once ABSPATH . 'wp-admin/includes/media.php';
        $attach_id = media_handle_upload('cover_img', 0);
        /*         * ***** Assign image to user *********** */
        update_user_meta($user_id, '_sb_user_cover', $attach_id);
        $image_link = wp_get_attachment_image_src($attach_id, 'nokri-rest-api');
        $idata['cover_img'] = $image_link[0];


        $response = array('success' => true, 'data' => $image_link, 'message' => __("Cover image updated successfully", "nokri-rest-api"));

        return $response;
    }

}



/* * ********************************** */
/* Change Candidate Dp */
/* * ********************************** */

add_action('rest_api_init', 'nokriAPI_profile_api_update_emp_dp', 0);

function nokriAPI_profile_api_update_emp_dp() {
    register_rest_route(
            'nokri/v1', '/employer/logo', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_emp_update_dp',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_emp_update_dp')) {

    function nokriAPI_emp_update_dp($request) {

        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        require_once ABSPATH . 'wp-admin/includes/image.php';
        require_once ABSPATH . 'wp-admin/includes/file.php';
        require_once ABSPATH . 'wp-admin/includes/media.php';
        $attach_id = media_handle_upload('logo_img', 0);

        if (is_numeric($attach_id)) {
            /*             * ***** Assign image to user *********** */
            update_user_meta($user_id, '_sb_user_pic', $attach_id);
        } else {
            update_user_meta($user_id, '_sb_user_pic', '');
        }



        $image_link = wp_get_attachment_image_src($attach_id, 'nokri-rest-api');
        $idata['logo_img'] = $image_link[0];


        $response = array('success' => true, 'data' => $idata, 'message' => __("Company image updated successfully", "nokri-rest-api"));

        return $response;
    }

}



/* * ********************************** */
/* Get Employer Email Templates  Service */
/* * ********************************** */

add_action('rest_api_init', 'nokriAPI_emp_templates_hook', 0);

function nokriAPI_emp_templates_hook() {
    register_rest_route(
            'nokri/v1', '/employer/templates_list/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_emp_templates',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_emp_templates')) {

    function nokriAPI_emp_templates($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);

        /* Getting Email Templates */
        $resumes = nokri_get_resumes_list($user_id);
        $templates = array();
        $templates[] = array(
            "number" => "SR #",
            "name" => "Name",
            "view_update" => "View/Update",
            "delete" => "Delete",
            "key" => "",
        );
        $count = 1;
        $has_email_templates = false;
        if (isset($resumes) && count($resumes) > 0) {
            $has_email_templates = true;
            foreach ($resumes as $key => $val) {
                $templates[] = array(
                    "number" => $count,
                    "name" => $val['name'],
                    "view_update" => __("View/Update", "nokri-rest-api"),
                    "delete" => __("Delete", "nokri-rest-api"),
                    "key" => $key,
                );
                $count++;
            }
        }


        $data['page_title'] = __("Email Templates", "nokri-rest-api");
        $data['has_email_templates'] = $has_email_templates;

        $data['email_templates'] = $templates;

        $data['add_new_btn'] = __("Ad New", "nokri-rest-api");
        $message = '';
        if ($has_email_templates == false) {
            $message = __("no email template found", "nokri-rest-api");
        }

        return $response = array('success' => true, 'data' => $data, 'message' => $message);
    }

}





/* * ********************************** */
/* Get Employer Package            */
/* * ********************************** */



add_action('rest_api_init', 'nokriAPI_emp_package_details_hook', 0);

function nokriAPI_emp_package_details_hook() {
    register_rest_route(
            'nokri/v1', '/employer/package_details/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_emp_package_details',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_emp_package_details')) {

    function nokriAPI_emp_package_details($request) {
        global $nokri;
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $remaining_searches = '';
        if (get_user_meta($user_id, '_sb_cand_search_value', true) != '') {
            $remaining_searches = get_user_meta($user_id, '_sb_cand_search_value', true);
        }
        $infos = array();
        $infos = array(
            "number" => __("SR #", "nokri-rest-api"),
            "name" => __("Title", "nokri-rest-api"),
            "details" => __("Details", "nokri-rest-api"),
            "expiry" => __("Package expiry", "nokri-rest-api"),
        );
        /* Is Package base */
        $is_pkg_base = false;
        if (isset($nokri['cand_search_mode']) && $nokri['cand_search_mode'] == '2') {
            $is_pkg_base = true;
        }
        $resumes = array(
            "is_req" => $is_pkg_base,
            "title" => __("Resume Views Remaining", "nokri-rest-api"),
            "nos" => $remaining_searches,
        );
        $jobs = __("Jobs", 'nokri-rest-api');
        $days = __("Days", 'nokri-rest-api');
        $message = '';
        $packages = array();
        $package_date = get_user_meta($user_id, '_sb_expire_ads', true);

        if ($package_date == "-1") {

            $package_date = esc_html__('Unlimited', 'nokri-rest-api');
        } else {

            $package_date = date_i18n(get_option('date_format'), strtotime($package_date));
        }





        $other_details = array();
        $other_details_counter = 0;


        if ((isset($nokri['allow_bump_jobs'])) && $nokri['allow_bump_jobs']) {


            $bump_up_jobs = get_user_meta($user_id, 'bump_ads_limit', true);
            $bump_up_name = esc_html__('bump up jobs', 'nokri-rest-api');



            if ($bump_up_jobs == "-1") {
                $other_details[$other_details_counter]['no_of_jobs'] = __('Lifetime', 'nokri-rest-api');
                $other_details[$other_details_counter]['name'] = $bump_up_name;
                $other_details_counter = 1;
            } else if ($bump_up_jobs != "") {
                $other_details[$other_details_counter]['no_of_jobs'] = $bump_up_jobs;
                $other_details[$other_details_counter]['name'] = $bump_up_name;
                $other_details_counter = 1;
            }
        }

        $featured_days = "0";

        $featured_meta = get_user_meta($user_id, '_emp_feature_profile', true);

        if ($featured_meta == "-1") {
            $featured_days = __('Lifetime', 'nokri-rest-api');
        } else if ($featured_meta != "") {

            $featured_days = date_i18n(get_option('date_format'), strtotime($featured_meta));
        }


        $other_details[$other_details_counter]['no_of_jobs'] = $featured_days;
        $other_details[$other_details_counter]['name'] = esc_html__('Featured profile', 'nokri-rest-api');



        $count = 1;
        $has_email_templates = false;
        /* Employer Purchase Any Package */
        if (get_user_meta($user_id, '_sb_expire_ads', true) != '') {
            /* Getting Employer Packages Details */
            $p_job_data = array();
            $class_terms = get_terms('job_class', array('hide_empty' => false, 'orderby' => 'id', 'order' => 'ASC'));
            if (count($class_terms) > 0) {
                $count = 0;
                foreach ($class_terms as $c_term) {
                    $meta_name = 'package_job_class_' . $c_term->term_id;
                    $class = get_user_meta($user_id, $meta_name, true);
                    if ($class == '') {
                        $class = __("N/A", "nokri-rest-api");
                    }
                    if ($class != "") {
                        $packages[$count]['no_of_jobs'] = esc_attr($class);
                        $packages[$count]['name'] = esc_html(ucfirst($c_term->name)) . " " . $jobs;
                        $count++;
                    }
                }
            }
        } else {
            $message = __("No package found", "nokri-rest-api");
        }
        $data['bump_jobs'] = $other_details;
        $data['page_title'] = __("Packages", "nokri-rest-api");
        $data['expiry'] = $package_date;
        $data['no_of_res'] = esc_attr($class);
        $data['title'] = esc_html(ucfirst($c_term->name)) . " " . $jobs;
        $data['packages'] = $packages;
        $data['info'] = $infos;
        $data['resumes'] = $resumes;


        return $response = array('success' => true, 'data' => $data, 'message' => $message);
    }

}
/* * ********************************** */
/* Function Email Templates     */
/* * ********************************** */

if (!function_exists('nokriApi_get_resumes_list')) {

    function nokriApi_get_resumes_list($user_id = '') {
        global $wpdb;
        /* Query For Getting All Resumes Against Job */
        $query = "SELECT meta_key, meta_value FROM $wpdb->usermeta WHERE user_id = '$user_id' AND meta_key like '_email_temp_name_$user_id%' ";
        $resumes = $wpdb->get_results($query);

        $data = array();
        foreach ($resumes as $resume) {
            $temps = base64_decode($resume->meta_value);
            $value = json_decode($temps, true);
            $data["$resume->meta_key"] = $value;
        }
        return $data;
    }

}
/* * ********************************** */
/* View Employer Email Templates     */
/* * ********************************** */
add_action('rest_api_init', 'nokriAPI_view_emp_templates_hook', 0);

function nokriAPI_view_emp_templates_hook() {
    register_rest_route(
            'nokri/v1', '/employer/view_template/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_view_emp_templates',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_view_emp_templates')) {

    function nokriAPI_view_emp_templates($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);


        /* Getting Email Templates */
        $resumes = nokriAPI_get_resumes_list($user_id);
        $templates = array();
        if (isset($resumes) && count($resumes) > 0) {
            $resume_table = '';
            $sr_no = '1';
            $count = 0;
            foreach ($resumes as $key => $val) {
                $data = array();
                $templates[$count][] = nokriAPI_employer_fields(__("ID", "nokri-rest-api"), $key, 'textfield', true, '', 'temp_id');
                $templates[$count][] = nokriAPI_employer_fields(__("Name", "nokri-rest-api"), esc_html($val['name']), 'textfield', true, '', 'temp_name');
                $count++;
            }
        }

        $data['templates'] = $templates;

        $data['extras'][] = nokriAPI_employer_fields('Section name', __("Your email templates", "nokri-rest-api"), '', '', 1, 'section_name');
        $data['extras'][] = nokriAPI_employer_fields('Add New', __("Add New", "nokri-rest-api"), '', '', 1, 'btn_txt');
        $data['extras'][] = nokriAPI_employer_fields('Sr', __("Sr #", "nokri-rest-api"), '', '', 1, 'sr_text');
        $data['extras'][] = nokriAPI_employer_fields('Name', __("Name", "nokri-rest-api"), '', '', 1, 'name');
        $data['extras'][] = nokriAPI_employer_fields('Update', __("Update", "nokri-rest-api"), '', '', 1, 'update');
        $data['extras'][] = nokriAPI_employer_fields('Delete', __("Delete", "nokri-rest-api"), '', '', 1, 'del');
        $data['extras'][] = nokriAPI_employer_fields('Not Added', __("You have not created any template yet", "nokri-rest-api"), '', '', 1, 'not_added');
        $data['extras'][] = nokriAPI_employer_fields('Update', __("Update", "nokri-rest-api"), '', '', 1, 'btn_update');
        $data['extras'][] = nokriAPI_employer_fields('Delete', __("Delete", "nokri-rest-api"), '', '', 1, 'btn_del');


        return $response = array('success' => true, 'data' => $data, 'message' => '');
    }

}

/* * ********************************** */
/*   Companies Followers Service     */
/* * ********************************** */



add_action('rest_api_init', 'nokriAPI_company_followers_hook', 0);

function nokriAPI_company_followers_hook() {
    register_rest_route(
            'nokri/v1', '/employer/company_followers/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_company_followers',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );


    register_rest_route(
            'nokri/v1', '/employer/company_followers/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_company_followers',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_company_followers')) {

    function nokriAPI_company_followers($request) {
        global $nokriAPI;
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);


        /* Query For Getting All Followed Companies */

        $json_data = $request->get_json_params();
        $page_number = $paged = (isset($json_data['page_number'])) ? $json_data['page_number'] : 1;
        $current_page = $paged;
        $user_per_page = (isset($nokriAPI['api_user_pagination'])) ? $nokriAPI['api_user_pagination'] : "10";

        $paged = (int) $paged;
        $get_offset = ($paged - 1);
        $offset = $get_offset * $user_per_page;



        $args = array(
            'meta_key' => '_cand_follow_company_' . $user_id,
            'meta_value' => $user_id,
            'meta_compare' => 'LIKE',
            //'search'    => "*".esc_attr( $c_name )."*",
            'number' => $user_per_page,
            'offset' => $offset,
            'order' => 'DESC',
        );
        $user_query = new WP_User_Query($args);

        $cand_followers = $user_query->get_results();
        $total_users = $user_query->get_total();
        $max_num_pages = ceil($total_users / $user_per_page);




        $company = array();
        $message = '';
        if ($cand_followers) {
            $count = 0;
            foreach ($cand_followers as $follower) {
                $followers = get_user_by('id', $follower->ID);



                /* Getting Company  Profile Photo  */
                $image_dp_link[0] = get_template_directory_uri() . '/images/candidate-dp.jpg';
                if (get_user_meta($followers->ID, '_cand_dp', true) != "") {
                    $attach_dp_id = get_user_meta($followers->ID, '_cand_dp', true);
                    $image_dp_link = wp_get_attachment_image_src($attach_dp_id, '');
                }

                $company[$count][] = nokriAPI_employer_fields(__("Follower Dp", "nokri-rest-api"), $image_dp_link[0], 'fileinput', true, 1, 'follower_dp');
                $company[$count][] = nokriAPI_employer_fields(__("Follower ID", "nokri-rest-api"), $followers->ID, 'fileinput', true, 1, 'follower_id');
                $company[$count][] = nokriAPI_employer_fields(__("Follower Name", "nokri-rest-api"), $followers->display_name, 'fileinput', true, 1, 'follower_name');
                $company[$count][] = nokriAPI_employer_fields(__("Follower Link", "nokri-rest-api"), get_author_posts_url($followers->ID), 'fileinput', true, 1, 'follower_link');
                $company[$count][] = nokriAPI_employer_fields(__("Follower Profession", "nokri-rest-api"), get_user_meta($followers->ID, '_cand_headline', true), 'fileinput', true, 1, 'follower_pro');
                $count++;
            }
        } else {
            $message = __("Company has no follower yet", "nokri-rest-api");
        }


        $data['page_title'] = __("Company Followers", "nokri-rest-api");
        $data['btn_text'] = __("Remove", "nokri-rest-api");
        $data['comapnies'] = $company;



        $nextPaged = (int) ($paged) + 1;
        $has_next_page = ( $nextPaged <= (int) $max_num_pages ) ? true : false;

        $pagination = array("max_num_pages" => (int) $max_num_pages, "current_page" => (int) $paged, "next_page" => (int) $nextPaged, "increment" => (int) $user_per_page, "current_no_of_users" => (int) $total_users, "has_next_page" => $has_next_page);
        return $response = array('success' => true, 'data' => $data, 'pagination' => $pagination, 'message' => $message);
    }

}



/* * ********************************** */
/*   Companies Deleting Followers  */
/* * ********************************** */



add_action('rest_api_init', 'nokriAPI_company_followers_del_hook', 0);

function nokriAPI_company_followers_del_hook() {
    register_rest_route(
            'nokri/v1', '/employer/company_del_followers/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_company_followers_del',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_company_followers_del')) {

    function nokriAPI_company_followers_del($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();

        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }
        $follower_id = (isset($json_data['follower_id'])) ? trim($json_data['follower_id']) : '';


        if ($follower_id != "") {
            if (delete_user_meta($follower_id, '_cand_follow_company_' . $user_id)) {
                $message = __("Deleted Successfully", "nokri-rest-api");
            } else {
                $message = __("Something Went Wrong", "nokri-rest-api");
            }
        }

        return $response = array('success' => true, 'data' => '', 'message' => $message);
    }

}



/* * ******************************** */
/* Employer Updating Skills  */
/* * ******************************* */



add_action('rest_api_init', 'nokriAPI_emp_edit_skills_hook', 0);

function nokriAPI_emp_edit_skills_hook() {
    register_rest_route(
            'nokri/v1', '/employer/update_skills/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_emp_update_skills',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );

    register_rest_route(
            'nokri/v1', '/employer/update_skills/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_emp_update_skills',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_emp_update_skills')) {

    function nokriAPI_emp_update_skills($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();

        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }


        $emp_skills = (isset($json_data['emp_skills'])) ? $json_data['emp_skills'] : '';
        $emp_nos = (isset($json_data['emp_nos'])) ? $json_data['emp_nos'] : '';
        $emp_estab = (isset($json_data['emp_establish'])) ? $json_data['emp_establish'] : '';


        $skill_arr = nokriAPI_emp_skills_tags($user_id);


        $data['skills_selected'] = $skill_arr;

        $skills = __("Company specialization:", "nokri-rest-api");

        $emp_no = __("Number  of employees:", "nokri-rest-api");

        $establish = __("Company established in:", "nokri-rest-api");


        $data['skills_field'] = nokriAPI_employer_fields($skills, nokriAPI_job_post_taxonomies('emp_specialization'), 'textfield', true, 2, 'emp_skills');
        $data['employes_field'] = nokriAPI_employer_fields($emp_no, get_user_meta($user_id, '_emp_nos', true), 'textfield', true, 2, 'emp_nos');


        $data['establish'] = nokriAPI_employer_fields($establish, get_user_meta($user_id, '_emp_est', true), 'textfield', true, '', 'emp_establish');



        /* Updating Values In User Meta Of Current User */

        if (isset($json_data) && (!empty($emp_skills))) {
            update_user_meta($user_id, '_emp_skills', $emp_skills);
        }
        if ($emp_nos != '') {
            update_user_meta($user_id, '_emp_nos', $emp_nos);
        }
        if ($emp_estab != '') {
            update_user_meta($user_id, '_emp_est', $emp_estab);
        }


        $extras[] = nokriAPI_employer_fields(__("Type and select skills", "nokri-rest-api"), __("Company Specialization", "nokri-rest-api"), 'textfield', true, 2, 'skill_txt');
        $extras[] = nokriAPI_employer_fields(__("Select established date", "nokri-rest-api"), __("Company Established In", "nokri-rest-api"), 'textfield', true, 2, 'est_txt');
        $extras[] = nokriAPI_employer_fields(__("Enter number of employees", "nokri-rest-api"), __("No Of Employees:", "nokri-rest-api"), 'textfield', true, 2, 'section_name');
        $extras[] = nokriAPI_employer_fields('btn_name', __("Save Section", "nokri-rest-api"), 'textfield', true, 2, 'btn_name');


        $response = array('success' => true, 'data' => $data, 'message' => __("Skills Updated", "nokri-rest-api"), 'extras' => $extras);
        return $response;
    }

}



/* * ******************************** */
/* Employer Updating Locations  */
/* * ******************************* */



add_action('rest_api_init', 'nokriAPI_emp_edit_location_hook', 0);

function nokriAPI_emp_edit_location_hook() {
    register_rest_route(
            'nokri/v1', '/employer/update_location/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_emp_update_location',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );

    register_rest_route(
            'nokri/v1', '/employer/update_location/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_emp_update_location',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_emp_update_location')) {

    function nokriAPI_emp_update_location($request) {

        global $nokriAPI;

        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();

        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }

        $emp_lat = (isset($json_data['emp_lat'])) ? trim($json_data['emp_lat']) : '';
        $emp_long = (isset($json_data['emp_long'])) ? trim($json_data['emp_long']) : '';
        $emp_loc = (isset($json_data['emp_loc'])) ? trim($json_data['emp_loc']) : '';

        $emp_country = (isset($json_data['emp_country'])) ? trim($json_data['emp_country']) : '';
        $emp_country_states = (isset($json_data['emp_country_states'])) ? trim($json_data['emp_country_states']) : '';
        $emp_country_cities = (isset($json_data['emp_country_cities'])) ? trim($json_data['emp_country_cities']) : '';
        $emp_country_towns = (isset($json_data['emp_country_towns'])) ? trim($json_data['emp_country_towns']) : '';


        $emp_location = array();



        if ($emp_country != "") {
            $emp_location[] = $emp_country;
        }
        if ($emp_country_states != "") {
            $emp_location[] = $emp_country_states;
        }
        if ($emp_country_cities != "") {
            $emp_location[] = $emp_country_cities;
        }
        if ($emp_country_towns != "") {
            $emp_location[] = $emp_country_towns;
        }


        if (!empty($emp_location)) {
            update_user_meta($user_id, '_emp_custom_location', ($emp_location));
        }


        $loc_section_heading = ( isset($nokriAPI['API_job_country_level_heading']) && $nokriAPI['API_job_country_level_heading'] != "" ) ? $nokriAPI['API_job_country_level_heading'] : '';


        //Countries
        $ad_country = nokri_get_cats('ad_location', 0);
        $country_html = array();
        foreach ($ad_country as $ad_count) {
            $children = get_terms($ad_count->taxonomy, array('parent' => $ad_count->term_id, 'hide_empty' => false));
            $has_child = ($children) ? true : false;
            $country_html[] = array("key" => $ad_count->term_id, "value" => esc_html($ad_count->name), "has_child" => $has_child);
        }



        $lat = __("Latitude:", "nokri-rest-api");
        $data[] = nokriAPI_employer_fields($lat, get_user_meta($user_id, '_emp_map_lat', true), 'textfield', true, 2, 'emp_lat');

        $long = __("Longitude:", "nokri-rest-api");
        $data[] = nokriAPI_employer_fields($long, get_user_meta($user_id, '_emp_map_long', true), 'textfield', true, 2, 'emp_long');


        $loc = __("Set Your Location:", "nokri-rest-api");
        $data[] = nokriAPI_employer_fields($loc, get_user_meta($user_id, '_emp_map_location', true), 'textfield', true, 2, 'emp_loc');


        $custom_loc = $loc_section_heading;
        $data[] = nokriAPI_employer_fields($custom_loc, $country_html, 'textfield', true, 2, 'emp_custom_loc');

        /* Updating Values In User Meta Of Current User */

        if ($emp_lat != "") {
            update_user_meta($user_id, '_emp_map_lat', $emp_lat);
        }
        if ($emp_long != '') {
            update_user_meta($user_id, '_emp_map_long', $emp_long);
        }
        if ($emp_loc != '') {
            update_user_meta($user_id, '_emp_map_location', $emp_loc);
        }

        /* Location headings */
        $job_country_level_1 = ( isset($nokriAPI['API_job_country_level_1']) && $nokriAPI['API_job_country_level_1'] != "" ) ? $nokriAPI['API_job_country_level_1'] : '';

        $job_country_level_2 = ( isset($nokriAPI['API_job_country_level_2']) && $nokriAPI['API_job_country_level_2'] != "" ) ? $nokriAPI['API_job_country_level_2'] : '';

        $job_country_level_3 = ( isset($nokriAPI['API_job_country_level_3']) && $nokriAPI['API_job_country_level_3'] != "" ) ? $nokriAPI['API_job_country_level_3'] : '';

        $job_country_level_4 = ( isset($nokriAPI['API_job_country_level_4']) && $nokriAPI['API_job_country_level_4'] != "" ) ? $nokriAPI['API_job_country_level_4'] : '';

        $map_heading = ( isset($nokriAPI['API_job_map_heading_txt']) && $nokriAPI['API_job_map_heading_txt'] != "" ) ? $nokriAPI['API_job_map_heading_txt'] : '';

        $extras[] = nokriAPI_employer_fields('', __("Location and address", "nokri-rest-api"), 'textfield', true, 2, 'loc');
        $extras[] = nokriAPI_employer_fields('', __("Save Location", "nokri-rest-api"), 'textfield', true, 2, 'save');
        $extras[] = nokriAPI_employer_fields('', $job_country_level_1, 'textfield', true, 2, 'country_txt');
        $extras[] = nokriAPI_employer_fields('', $job_country_level_2, 'textfield', true, 2, 'state_txt');
        $extras[] = nokriAPI_employer_fields('', $job_country_level_3, 'textfield', true, 2, 'city_txt');
        $extras[] = nokriAPI_employer_fields('', $job_country_level_4, 'textfield', true, 2, 'town_txt');




        $cand_custom_loc = get_user_meta($user_id, '_emp_custom_location', true);
        $levelz = count((array) $cand_custom_loc);
        $country_html = !(empty($levelz)) ? get_user_custom_location($cand_custom_loc, $levelz) : array();




        $response = array('success' => true, 'data' => $data, 'message' => esc_html__('Location updated', 'nokri-rest-api'), 'extras' => $extras, 'custom_location' => $country_html);
        return $response;
    }

}
/* * ******************************** */
/* Employer Creating Email Templates */
/* * ******************************* */
add_action('rest_api_init', 'nokriAPI_emp_email_template_hook', 0);

function nokriAPI_emp_email_template_hook() {
    register_rest_route(
            'nokri/v1', '/employer/email_template/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_emp_email_templates',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );

    register_rest_route(
            'nokri/v1', '/employer/email_template/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_emp_email_templates',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_emp_email_templates')) {

    function nokriAPI_emp_email_templates($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();
        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }

        $email_temp_name = (isset($json_data['email_temp_name'])) ? trim($json_data['email_temp_name']) : '';
        $email_temp_subject = (isset($json_data['email_temp_subject'])) ? trim($json_data['email_temp_subject']) : '';
        $email_temp_details = (isset($json_data['email_temp_details'])) ? trim($json_data['email_temp_details']) : '';
        $template_id = (isset($json_data['template_id'])) ? trim($json_data['template_id']) : '';
        $email_temp_for = (isset($json_data['email_temp_for'])) ? trim($json_data['email_temp_for']) : '';
        $email_temp_date = (isset($json_data['email_temp_date'])) ? trim($json_data['email_temp_date']) : '';



        /* Updating Values In User Meta Of Current User */


        if ($template_id != "") {
            $template_meta_key = $template_id;
        } else {
            $template_meta_key = '_email_temp_name_' . $user_id . '_' . time();
        }


        $template['name'] = $email_temp_name;
        $template['subject'] = $email_temp_subject;
        $template['body'] = $email_temp_details;
        $template['for'] = $email_temp_for;
        $template['date'] = $email_temp_date;
        $templateData = json_encode($template);
        $templateData = base64_encode($templateData);
        if ($email_temp_name != "") {
            update_user_meta($user_id, $template_meta_key, $templateData);
        }
        $template_for = '';
        if (isset($template_id) && $template_id != "") {
            $template_id = $template_id;
            $meta_data = get_user_meta($user_id, $template_id, true);
            $meta_data = base64_decode($meta_data);
            $val = json_decode($meta_data, true);
            $template_name = $val['name'];
            $template_subject = $val['subject'];
            $template_body = $val['body'];
            $template_for = $val['for'];
        } else {
            /* Employer Details For Email */
            $emp_detials = get_user_by('id', $user_id);
            $emp_name = $emp_detials->display_name;
            $emp_headline = get_user_meta($user_id, '_emp_headline', true);
            $emp_web = get_user_meta($user_id, '_emp_web', true);
            $template_body = '<table class="body" style="border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f6f6f6; width: 100%;" border="0" cellspacing="0" cellpadding="0">
		<tbody>
		<tr>
		<td style="font-family: sans-serif; font-size: 14px; vertical-align: top;"></td>
		<td class="container" style="font-family: sans-serif; font-size: 14px; vertical-align: top; display: block; max-width: 480px; padding: 10px; width: 480px; margin: 0 auto !important;">
		<div class="content" style="box-sizing: border-box; display: block; margin: 0 auto; max-width: 480px; padding: 10px;">
		<table class="main" style="border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; background: #fff; border-radius: 3px; width: 100%;">
		<tbody>
		<tr>
		<td class="wrapper" style="font-family: sans-serif; font-size: 14px; vertical-align: top; box-sizing: border-box; padding: 20px;">
		<table style="border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; width: 100%;" border="0" cellspacing="0" cellpadding="0">
		<tbody>
		<tr style="font-family: \'Helvetica Neue\',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;">
		<td class="alert" style="font-family: \'Helvetica Neue\',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 16px; vertical-align: top; color: #000; font-weight: 500; text-align: center; border-radius: 3px 3px 0 0; background-color: #fff; margin: 0; padding: 20px;" align="center" valign="top" bgcolor="#fff">' . $emp_headline . '</td>
		</tr>
		<tr>
		<td style="font-family: sans-serif; font-size: 14px; vertical-align: top;">
		<p style="font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; margin-bottom: 15px;"><span style="font-family: sans-serif; font-weight: normal;">' . $emp_name . '</span><span style="font-family: \'Helvetica Neue\', Helvetica, Arial, sans-serif;"><b>,</b></span></p>
		Your Are Selected For  %site_name%;
		
		Name: %display_name%
		
		Email: %email%
		
		&nbsp;
		<p style="font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; margin-bottom: 15px;"><strong>Thanks!</strong></p>
		<p style="font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; margin-bottom: 15px;">' . $emp_name . '</p>
		</td>
		</tr>
		</tbody>
		</table>
		</td>
		</tr>
		</tbody>
		</table>
		<div class="footer" style="clear: both; padding-top: 10px; text-align: center; width: 100%;">
		<table style="border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; width: 100%;" border="0" cellspacing="0" cellpadding="0">
		<tbody>
		<tr>
		<td class="content-block powered-by" style="font-family: sans-serif; font-size: 12px; vertical-align: top; color: #999999; text-align: center;"><a style="color: #999999; text-decoration: underline; font-size: 12px; text-align: center;" href="' . $emp_web . '">' . $emp_name . '</a>.</td>
		</tr>
		</tbody>
		</table>
		</div>
		&nbsp;
		
		</div></td>
		<td style="font-family: sans-serif; font-size: 14px; vertical-align: top;"></td>
		</tr>
		</tbody>
		</table>';
            $template_name = '';
            $template_subject = '';
            $template_for = '';
        }


        $t_name = __("Name:", "nokri-rest-api");
        $data[] = nokriAPI_canidate_fields($t_name, $template_name, 'textfield', true, 2, 'email_temp_name');

        $t_sub = __("Subject:", "nokri-rest-api");
        $data[] = nokriAPI_canidate_fields($t_sub, $template_subject, 'textfield', true, 2, 'email_temp_subject');


        $t_details = __("Details:", "nokri-rest-api");
        $data[] = nokriAPI_canidate_fields($t_details, $template_body, 'textfield', true, 2, 'email_temp_details');

        $t_for = __("For:", "nokri-rest-api");
        $data[] = nokriAPI_canidate_fields($t_for, nokriAPI_canidate_apply_status_post($template_for), 'textfield', true, 2, 'email_temp_for');


        $extras[] = nokriAPI_employer_fields('', __("Add your email templates", "nokri-rest-api"), 'textfield', true, 2, 'add');
        $extras[] = nokriAPI_employer_fields('', __("Save Template", "nokri-rest-api"), 'textfield', true, 2, 'save');
        $extras[] = nokriAPI_employer_fields('', __("View Templates", "nokri-rest-api"), 'textfield', true, 2, 'view');

        $extras[] = nokriAPI_employer_fields('', __("Email Templates", "nokri-rest-api"), 'textfield', true, 2, 'page_title');



        $response = array('success' => true, 'data' => $data, 'message' => __("Saved Successfully.", "nokri-rest-api"), 'extras' => $extras);
        return $response;
    }

}



/* * ********************************** */
/*   Employer Deleting Email Templates */
/* * ********************************** */



add_action('rest_api_init', 'nokriAPI_email_del_hook', 0);

function nokriAPI_email_del_hook() {
    register_rest_route(
            'nokri/v1', '/employer/del_email_temp/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_del_email_temp',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_del_email_temp')) {

    function nokriAPI_del_email_temp($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();

        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }

        $temp_id = (isset($json_data['temp_id'])) ? trim($json_data['temp_id']) : '';

        if ($temp_id != "") {
            if (delete_user_meta($user_id, $temp_id)) {
                $message = __("Deleted Successfully", "nokri-rest-api");
            } else {
                $message = __("Something Went Wrong", "nokri-rest-api");
            }
        }

        return $response = array('success' => true, 'data' => '', 'message' => $message);
    }

}

/* * ********************************** */
/* Getting Job Class Name */
/* * ********************************** */

if (!function_exists('nokriAPI_job_class_values')) {

    function nokriAPI_job_class_values() {
        $taxonomies = get_terms('job_class', array('hide_empty' => false, 'orderby' => 'id', 'order' => 'ASC', 'parent' => 0));
        $data = array();
        if (count($taxonomies) > 0) {
            foreach ($taxonomies as $taxonomy) {
                $data[] = array("key" => $taxonomy->term_id, "value" => esc_html($taxonomy->name));
            }
        }
        return $data;
    }

}

/* * ********************************** */
/*  Employer Active  Jobs              */
/* * ********************************** */
add_action('rest_api_init', 'nokriAPI_emp_active_jobs_hook', 0);

function nokriAPI_emp_active_jobs_hook() {
    register_rest_route(
            'nokri/v1', '/employer/active_jobs/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_emp_active_jobs',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );


    register_rest_route(
            'nokri/v1', '/employer/active_jobs/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_emp_active_jobs',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_emp_active_jobs')) {

    function nokriAPI_emp_active_jobs($request) {

        $user_id = get_current_user_id();
        $json_data = $request->get_json_params();
        $job_class = (isset($json_data['job_class'])) ? trim($json_data['job_class']) : '';
        $page_number = (isset($json_data['page_number'])) ? trim($json_data['page_number']) : 1;




        return nokriAPI_emp_active_jobs2($user_id, $job_class, $page_number);
    }

}

if (!function_exists('nokriAPI_emp_active_jobs2')) {

    function nokriAPI_emp_active_jobs2($user_id = '', $job_class = '', $page_number = 1, $return_arr = '') {
        $jobs = array();
        $job_filter = array();
        global $nokri;
        $meta_key = ( $job_class != "" ) ? 'package_job_class_' . $job_class : '';
        /* Filtering  Jobs By Class */
        if ($job_class != '') {
            $job_filter = array(
                'key' => $meta_key,
                'value' => $job_class,
                'compare' => '='
            );
        }

        if (get_query_var('paged')) {
            $paged = get_query_var('paged');
        } else if (isset($page_number)) {
            // This will occur if on front page.
            $paged = $page_number;
        } else {
            $paged = 1;
        }
        /* Getting Filtered  Jobs */
        $args = array(
            'post_type' => 'job_post',
            'orderby' => 'date',
            'order' => 'DESC',
            'author' => $user_id,
            'paged' => $paged,
            'post_status' => array('publish', 'draft'),
            'meta_query' => array(
                'relation' => 'AND',
                $job_filter,
                array(
                    'key' => '_job_status',
                    'value' => 'active',
                    'compare' => '='
                )
            )
        );
        $args = nokri_wpml_show_all_posts_callback($args);
        $query = new WP_Query($args);
        $jobs = array();
        if ($query->have_posts()) {
            $count = 0;
            while ($query->have_posts()) {
                $query->the_post();
                $query->post_author;
                $job_id = get_the_id();
                $post_author_id = get_post_field('post_author', $job_id);
                $autho_name = get_the_author_meta('display_name', $post_author_id);
                $job_type = wp_get_post_terms($job_id, 'job_type', array("fields" => "ids"));
                $job_type = isset($job_type[0]) ? $job_type[0] : '';
                $job_salary = wp_get_post_terms($job_id, 'job_salary', array("fields" => "ids"));
                $job_salary = isset($job_salary[0]) ? $job_salary[0] : '';
                $job_salary_type = wp_get_post_terms($job_id, 'job_salary_type', array("fields" => "ids"));
                $job_salary_type = isset($job_salary_type[0]) ? $job_salary_type[0] : '';
                $job_currency = wp_get_post_terms($job_id, 'job_currency', array("fields" => "ids"));
                $job_currency = isset($job_currency[0]) ? $job_currency[0] : '';
                $job_deadline_n = get_post_meta($job_id, '_job_date', true);
                $job_deadline = date_i18n(get_option('date_format'), strtotime($job_deadline_n));
                $job_expiry = get_post_meta($job_id, '_job_date', true);
                $job_status = get_post_meta($job_id, '_job_status', true);
                $job_location = get_post_meta($job_id, '_job_address', true);

                /* Getting Last Child Value */
                $job_categories = wp_get_object_terms($job_id, array('job_category'), array('orderby' => 'term_group'));
                $project = array();
                $last_cat = '';
                foreach ($job_categories as $c) {
                    $project = $c->name;
                }
                $jobs[$count][] = nokriAPI_employer_fields(__("Job Id", "nokri-rest-api"), get_the_id(), 'textfield', true, 1, 'job_id');
                $jobs[$count][] = nokriAPI_employer_fields(__("Company Name", "nokri-rest-api"), $project, 'textfield', true, 1, 'company_name');
                $jobs[$count][] = nokriAPI_employer_fields(__("Job Title", "nokri-rest-api"), nokriAPI_convert_uniText(get_the_title()), 'textfield', true, 1, 'job_name');
                $jobs[$count][] = nokriAPI_employer_fields(__("Job Category", "nokri-rest-api"), $project, 'textfield', true, 1, 'job_category');
                $jobs[$count][] = nokriAPI_employer_fields(__("Job Expiry", "nokri-rest-api"), date_i18n(get_option('date_format'), strtotime($job_expiry)), 'textfield', true, 1, 'job_expiry');
                $jobs[$count][] = nokriAPI_employer_fields(__("Job Type", "nokri-rest-api"), nokri_job_post_single_taxonomies('job_type', $job_type), 'textfield', true, 1, 'job_type');
                $jobs[$count][] = nokriAPI_employer_fields(__("Job Salary", "nokri-rest-api"), nokri_job_post_single_taxonomies('job_currency', $job_currency) . nokri_job_post_single_taxonomies('job_salary', $job_salary) . "/" . nokri_job_post_single_taxonomies('job_salary_type', $job_salary_type), 'textfield', true, 1, 'job_salary');

                $jobs[$count][] = nokriAPI_employer_fields(__("Job Currency", "nokri-rest-api"), nokri_job_post_single_taxonomies('job_currency', $job_currency), 'textfield', true, 1, 'job_currency');
                $jobs[$count][] = nokriAPI_employer_fields(__("View Resumes", "nokri-rest-api"), $job_id, 'textfield', true, 1, 'view_resumes');
                $jobs[$count][] = nokriAPI_employer_fields(__("Job Location", "nokri-rest-api"), $job_location, 'textfield', true, 1, 'job_location');
                $jobs[$count][] = nokriAPI_employer_fields(__("Inactive", "nokri-rest-api"), '', 'textfield', true, 1, 'inactive_job');
                $jobs[$count][] = nokriAPI_employer_fields(__("Posted Date", "nokri-rest-api"), date_i18n(get_option('date_format'), strtotime(get_the_date())), 'textfield', true, 1, 'job_post');
                $jobs[$count][] = nokriAPI_employer_fields($job_status, '', 'textfield', true, 1, 'job_status');
                $count++;
            }
        }
        $data['page_title'] = __("Active Jobs", "nokri-rest-api");
        $data['job_filter'] = nokriAPI_employer_fields(__("Select job class", "nokri-rest-api"), nokriAPI_job_class_values(), 'textfield', true, 1, 'job_class');
        $data['jobs'] = $jobs;

        $allow_bump_jobs = isset($nokri['allow_bump_jobs']) ? isset($nokri['allow_bump_jobs']) : false;
        $data['allow_bump_jobs'] = $allow_bump_jobs;
        $data['bump_up_txt'] = esc_html__('Bump up', 'nokri-rest-api');


        $message = __("You have no active jobs yet", "nokri-rest-api");

        $nextPaged = $paged + 1;
        $has_next_page = ( $nextPaged <= (int) $query->max_num_pages ) ? true : false;
        $pagination = array(
            "max_num_pages" => (int) $query->max_num_pages,
            "current_page" => (int) $paged,
            "next_page" => (int) $nextPaged,
            "increment" => (int) get_option('posts_per_page'),
            "current_no_of_ads" => (int) count($query->posts),
            "has_next_page" => $has_next_page
        );


        $response = array('success' => true, 'data' => $data, 'message' => $message, "pagination" => $pagination);


        if ($return_arr == 'jobs') {
            return array('jobs' => $jobs, 'message' => $message, "pagination" => $pagination);
        } else {
            return $response;
        }
    }

}
/* * ********************************** */
/* Getting Cand Updated Status      */
/* * ********************************** */

if (!function_exists('nokriAPI_canidate_updated_status')) {

    function nokriAPI_canidate_updated_status($getName = '') {
        $arr = array(
            "0" => __("Received", "nokri-rest-api"),
            "1" => __("In Review", "nokri-rest-api"),
            "2" => __("Rejection", "nokri-rest-api"),
            "3" => __("Short Listed", "nokri-rest-api"),
            "4" => __("Interview", "nokri-rest-api"),
            "5" => __("Selection", "nokri-rest-api"),
        );

        return ( $getName == "" ) ? $arr : $arr["$getName"];
    }

}

/* * ********************************** */
/* Getting Cand Status              */
/* * ********************************** */
if (!function_exists('nokriAPI_canidate_apply_status')) {

    function nokriAPI_canidate_apply_status() {

        $statuses = array(
            "0" => __("Received", "nokri-rest-api"),
            "1" => __("In Review", "nokri-rest-api"),
            "2" => __("Rejected", "nokri-rest-api"),
            "3" => __("Short Listed", "nokri-rest-api"),
            "4" => __("Interview", "nokri-rest-api"),
            "5" => __("Selected", "nokri-rest-api"),
        );

        $data = array();
        if (count($statuses) > 0) {
            foreach ($statuses as $status => $val) {

                $data[] = array("key" => $val, "value" => esc_html($status));
            }
        }
        return $data;
    }

}



/* * ********************************** */
/* Getting Cand Status              */
/* * ********************************** */
if (!function_exists('nokriAPI_canidate_gender_dropdown')) {

    function nokriAPI_canidate_gender_dropdown() {
        $data = array();
        $data[] = array("key" => __("Male", "nokri-rest-api"), "value" => "male");
        $data[] = array("key" => __("Female", "nokri-rest-api"), "value" => "female");
        $data[] = array("key" => __("Other", "nokri-rest-api"), "value" => "other");

        return $data;
    }

}



/* * ********************************** */
/* Getting Cand Statuses with selection              */
/* * ********************************** */
if (!function_exists('nokriAPI_canidate_apply_status_post')) {

    function nokriAPI_canidate_apply_status_post($template_for) {

        $statuses = array(
            "0" => __("Received", "nokri-rest-api"),
            "1" => __("In Review", "nokri-rest-api"),
            "2" => __("Rejected", "nokri-rest-api"),
            "3" => __("Short Listed", "nokri-rest-api"),
            "4" => __("Interview", "nokri-rest-api"),
            "5" => __("Selected", "nokri-rest-api"),
        );

        $data = array();
        if (count($statuses) > 0) {
            foreach ($statuses as $status => $val) {

                $selected = false;
                if ($template_for == $status) {

                    $selected = true;
                }

                $data[] = array("key" => $val, "value" => esc_html($status), "selected" => $selected);
            }
        }
        return $data;
    }

}
/* * ********************************** */
/*  Employer Resumes Recieved         */
/* * ********************************** */
add_action('rest_api_init', 'nokriAPI_emp_resumes_recieved_hook', 0);

function nokriAPI_emp_resumes_recieved_hook() {
    register_rest_route(
            'nokri/v1', '/employer/resumes_recieved/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_emp_resumes_recieved',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );

    register_rest_route(
            'nokri/v1', '/employer/resumes_recieved/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_emp_resumes_recieved',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_emp_resumes_recieved')) {

    function nokriAPI_emp_resumes_recieved($request) {
        global $nokriAPI;
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();
        $jobs = array();
        $job_id = (isset($json_data['job_id'])) ? trim($json_data['job_id']) : '';
        $c_status = (isset($json_data['c_status'])) ? trim($json_data['c_status']) : '';
        $c_name = (isset($json_data['c_name'])) ? trim($json_data['c_name']) : '';

        $cand_loc = isset($json_data['cand_loc']) ? $json_data['cand_loc'] : "";
        $cand_gender = isset($json_data['cand_gender']) ? $json_data['cand_gender'] : "";

        $cand_loc_meta = array();


        if ($cand_loc != "") {
            $cand_loc_meta[] = array(
                'key' => '_cand_custom_location',
                'value' => $cand_loc,
                'compare' => 'like'
            );
        }


        $cand_gender_meta = array();
        if ($cand_gender != "") {
            $cand_gender_meta[] = array(
                'key' => '_cand_gender',
                'value' => $cand_gender,
                'compare' => "=",
            );
        }





        /* Query For Getting All Resumes Against Job */
        $status_wise = false;
        $extra = " AND meta_key like '_job_applied_resume_%'";
        if (isset($c_status)) {
            if (isset($c_status) && $c_status != "") {
                $c_status = $c_status;
                $extra = " AND meta_key LIKE '_job_applied_status_%' AND meta_value = '$c_status'";
                $status_wise = true;
            }
        }
        if (isset($c_name)) {
            if (isset($c_name) && $c_name != "") {
                $c_name = $c_name;
            }
        }
        $applier = array();
        global $wpdb;
        $query = "SELECT * FROM $wpdb->postmeta WHERE post_id = '$job_id' $extra";
        $applier_resumes = $wpdb->get_results($query);
        if (count($applier_resumes) == 0) {
            $message = __("You have no resume against this job.", "nokri-rest-api");
            $data['page_title'] = __("Resume Recieved", "nokri-rest-api");
            $data['cand_filter'] = nokriAPI_canidate_apply_status(__("Select Status", "nokri-rest-api"), nokriAPI_canidate_apply_status(), 'textfield', true, 1, 'cand_filter');
            $data['gender_filters'] = nokriAPI_canidate_gender_dropdown();
            $data['jobs'] = $jobs;

            $pagination = array("max_num_pages" => 0, "current_page" => 0, "next_page" => 0, "increment" => 0, "total_appliers" => 0, "has_next_page" => false);

            return $response = array('success' => true, 'data' => $data, 'pagination' => $pagination, 'message' => $message);
        }


        if (count($applier_resumes) > 0) {
            foreach ($applier_resumes as $resumes) {

                if ($status_wise) {
                    $array_data = explode('_', $resumes->meta_key);
                    $applier[] = $array_data[4];
                } else {
                    $array_data = explode('|', $resumes->meta_value);
                    $applier[] = $array_data[0];
                }
            }
        }
        if ($status_wise && count($applier) == 0) {
            $applier[] = '@#/!';
        }
        /* For Pagination */
        $page_number = $paged = (isset($json_data['page_number'])) ? $json_data['page_number'] : 1;
        $current_page = $paged;
        $user_per_page = (isset($nokriAPI['api_user_pagination'])) ? $nokriAPI['api_user_pagination'] : "10";
        $paged = (int) $paged;
        $get_offset = ($paged - 1);
        $offset = $get_offset * $user_per_page;


        $args = array(
            'search' => "*" . esc_attr($c_name) . "*",
            'number' => $user_per_page,
            'offset' => $offset,
            'include' => $applier,
            'order ' => 'ASC',
            'search_columns' => array('display_name',),
            'meta_query' => array(
                $cand_loc_meta,
                $cand_gender_meta,
            ),
        );
        $user_query = new WP_User_Query($args);
        $authors = $user_query->get_results();
        $total_users = $user_query->get_total();
        $max_num_pages = ceil($total_users / $user_per_page);

        $jobs = array();
        if ($authors) {
            $count = 0;
            foreach ($authors as $author) {
                // get all the user's id's
                $candidate_id = ($author->ID);
                $cand_resume = get_post_meta($job_id, '_job_applied_resume_' . $candidate_id, true);
                $cand_status = get_post_meta($job_id, '_job_applied_status_' . $candidate_id, true);
                $cand_gender = get_user_meta($candidate_id, '_cand_gender', true);
                if ($cand_status == "") {

                    $cand_status = "0";
                }
                $cand_final = nokri_canidate_apply_status($cand_status);


                $cand_headline = get_user_meta($candidate_id, '_cand_headline', true);
                $cand_adress = get_user_meta($candidate_id, '_cand_address', true);
                $job_date = get_post_meta($job_id, '_job_applied_date_' . $candidate_id, true);
                $cand_cover = get_post_meta($job_id, '_job_applied_cover_' . $candidate_id, true);
                $cand_vid_resume = get_user_meta($candidate_id, '_cand_intro_vid', true);
                $is_cover = false;
                $cover_letter = '';
                if ($cand_cover != '') {
                    $is_cover = true;
                    $cover_letter = get_post_meta($job_id, '_job_applied_cover_' . $candidate_id, true);
                }
                $array_data = explode('|', $cand_resume);
                $attachment_id = $array_data[1];
                $image_dp_link[0] = get_template_directory_uri() . '/images/company-logo.jpg';
                /* Getting Candidate Dp */
                if (get_user_meta($candidate_id, '_cand_dp', true) != "") {
                    $attach_dp_id = get_user_meta($candidate_id, '_cand_dp', true);
                    $image_dp_link = wp_get_attachment_image_src($attach_dp_id, '');
                }

                $jobs[$count][] = nokriAPI_employer_fields(__("Candidate Id", "nokri-rest-api"), $candidate_id, 'textfield', true, 1, 'cand_id');
                $jobs[$count][] = nokriAPI_employer_fields(__("Candidate Dp", "nokri-rest-api"), $image_dp_link[0], 'textfield', true, 1, 'cand_dp');
                $jobs[$count][] = nokriAPI_employer_fields(__("Candidate Name", "nokri-rest-api"), $author->display_name, 'textfield', true, 1, 'cand_name');
                $jobs[$count][] = nokriAPI_employer_fields(__("Candidate Status", "nokri-rest-api"), $cand_final, 'textfield', true, 1, 'cand_stat');
                $jobs[$count][] = nokriAPI_employer_fields(__("Candidate Gender", "nokri-rest-api"), $cand_gender, 'textfield', true, 1, 'cand_gender');



                if (is_numeric($attachment_id)) {
                    $jobs[$count][] = nokriAPI_employer_fields(__("Resume Download", "nokri-rest-api"), get_permalink($attachment_id), 'textfield', true, 1, 'cand_dwnlod');
                } else {
                    $jobs[$count][] = nokriAPI_employer_fields(__("Linkedin profile", "nokri-rest-api"), $attachment_id, 'textfield', true, 1, 'cand_linked');
                }


                $jobs[$count][] = nokriAPI_employer_fields(__("Resume Name", "nokri-rest-api"), basename(get_attached_file($attachment_id)), 'textfield', true, 1, 'resume_name');
                $jobs[$count][] = nokriAPI_employer_fields(__("Candidate Adress", "nokri-rest-api"), $cand_adress, 'textfield', true, 1, 'cand_adress');
                $jobs[$count][] = nokriAPI_employer_fields(__("Applied Date", "nokri-rest-api"), $job_date, 'textfield', true, 1, 'job_date');
                $jobs[$count][] = nokriAPI_employer_fields(__("Cover Letter", "nokri-rest-api"), $cover_letter, 'textfield', $is_cover, 1, 'cand_cover');

                $jobs[$count][] = nokriAPI_employer_fields(__("Video Resume", "nokri-rest-api"), $cand_vid_resume, 'textfield', $is_cover, 1, 'video_resume');

                $jobs[$count][] = nokriAPI_employer_fields(__("Take Action", "nokri-rest-api"), '', 'textfield', true, 1, 'cand_action');
                $jobs[$count][] = nokriAPI_employer_fields(__("Detail", "nokri-rest-api"), nokri_get_questions_answers_api($job_id, $candidate_id), 'textfield', true, 1, 'cand_question');


                $count++;
            }
        }
        $data['page_title'] = __("Resume Received", "nokri-rest-api");
        $data['cand_filter'] = nokriAPI_canidate_apply_status(__("Select Status", "nokri-rest-api"), nokriAPI_canidate_apply_status(), 'textfield', true, 1, 'cand_filter');
        $data['gender_filters'] = nokriAPI_canidate_gender_dropdown();
        $data['jobs'] = $jobs;

        $message = (count($jobs) > 0 ) ? '' : __("You have no resume against this job.", "nokri-rest-api");


        /* Pagination */
        $nextPaged = (int) ($paged) + 1;
        $has_next_page = ( $nextPaged <= (int) $max_num_pages ) ? true : false;

        $pagination = array("max_num_pages" => (int) $max_num_pages, "current_page" => (int) $paged, "next_page" => (int) $nextPaged, "increment" => (int) $user_per_page, "total_appliers" => (int) $total_users, "has_next_page" => $has_next_page);
        return $response = array('success' => true, 'data' => $data, 'pagination' => $pagination, 'message' => $message, 'job_id' => $job_id);
    }

}
/* * ********************************** */
/*  Employer InActive His  Jobs      */
/* * ********************************** */



add_action('rest_api_init', 'nokriAPI_emp_inactive_this_jobs_hook', 0);

function nokriAPI_emp_inactive_this_jobs_hook() {

    register_rest_route(
            'nokri/v1', '/employer/inactive_this_job/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_emp_inactive_this_job',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_emp_inactive_this_job')) {

    function nokriAPI_emp_inactive_this_job($request) {

        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();
        $message = '';
        $job_id = (isset($json_data['job_id'])) ? ($json_data['job_id']) : '';
        //NOKRI_API_ALLOW_EDITING
        //  $mode = nokriAPI_allow_editing($request->get_method());
        //  if (isset($mode) && count($mode) > 0) {
        //      return $mode;
        //  }     
        if ($job_id != '') {
            update_post_meta($job_id, '_job_status', 'inactive');
            $message = __("inactive successfully", "nokri-rest-api");
        } else {
            $message = __("Something Went Wrong", "nokri-rest-api");
        }
        return $response = array('success' => true, 'data' => '', 'message' => $message);
    }

}
/* * ********************************** */
/*  Employer Active His  Jobs      */
/* * ********************************** */

add_action('rest_api_init', 'nokriAPI_emp_active_this_jobs_hook', 0);

function nokriAPI_emp_active_this_jobs_hook() {
    register_rest_route(
            'nokri/v1', '/employer/active_this_job/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_emp_active_this_job',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_emp_active_this_job')) {

    function nokriAPI_emp_active_this_job($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();
        $message = '';
        $job_id = (isset($json_data['job_id'])) ? trim($json_data['job_id']) : '';

        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }
        $package_date = get_user_meta($user_id, '_sb_expire_ads', true);
        $today = date("Y-m-d");
        $take_action = true;
        $expiry_date_string = strtotime($package_date);
        $today_string = strtotime($today);
        if ($today_string > $expiry_date_string) {
            $take_action = false;
        }
        if ($job_id != "" && $take_action) {
            if ($job_id != '') {
                update_post_meta($job_id, '_job_status', 'active');
                $message = __("active successfully", "nokri-rest-api");
            } else {
                $message = __("Something Went Wrong", "nokri-rest-api");
            }
            return $response = array('success' => true, 'data' => '', 'message' => $message);
        } else {
            $message = __("please purchase package", "nokri-rest-api");
            return $response = array('success' => false, 'data' => '', 'message' => $message);
        }
    }

}




/* * ********************************** */
/*  Employer Bump up  this  Jobs      */
/* * ********************************** */

add_action('rest_api_init', 'nokriAPI_bump_up_this_jobs_hook', 0);

function nokriAPI_bump_up_this_jobs_hook() {
    register_rest_route(
            'nokri/v1', '/employer/bump_this_job/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_bump_up_this_job',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_bump_up_this_job')) {

    function nokriAPI_bump_up_this_job($request) {

        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();
        $message = '';
        $job_id = (isset($json_data['job_id'])) ? trim($json_data['job_id']) : '';
        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }
        $is_show_bump_up = false;
        global $nokri;

        $package_date = get_user_meta($user_id, '_sb_expire_ads', true);
        $today = date("Y-m-d");
        $expiry_date_string = strtotime($package_date);
        $today_string = strtotime($today);


        $bump_ads_limit = get_user_meta($user_id, 'bump_ads_limit', true);
        if ((isset($nokri['allow_bump_jobs'])) && $nokri['allow_bump_jobs'] && $bump_ads_limit != "" && $today_string < $expiry_date_string) {
            $is_show_bump_up = true;
        }
        $message = "";

        if ($job_id != "" && $is_show_bump_up) {
            $time = current_time('mysql');
            $bump_ads_limit = get_user_meta($user_id, 'bump_ads_limit', true);
            if ($bump_ads_limit != "" && $bump_ads_limit != -1) {
                $remaing_bump = absint($bump_ads_limit) - 1;
                update_user_meta($user_id, 'bump_ads_limit', $remaing_bump);
            }
            $post_to_change = array(
                'ID' => $job_id,
                'post_date' => $time,
                'post_date_gmt' => get_gmt_from_date($time));
            wp_update_post($post_to_change);

            if ($bump_ads_limit != "" && $bump_ads_limit != -1) {
                $remaing_bump = absint($bump_ads_limit) - 1;
                update_user_meta($user_id, 'bump_ads_limit', $remaing_bump);
            }

            $message = __("Bump Up successfully", "nokri-rest-api");
            return $response = array('success' => true, 'data' => '', 'message' => $message);
        } else {

            $message = __("Please purchase package", "nokri-rest-api");
            return $response = array('success' => false, 'data' => '', 'message' => $message);
        }
    }

}

/* * ********************************** */
/*  Employer In Active  Jobs            */
/* * ********************************** */

add_action('rest_api_init', 'nokriAPI_emp_inactive_jobs_hook', 0);

function nokriAPI_emp_inactive_jobs_hook() {
    register_rest_route(
            'nokri/v1', '/employer/inactive_jobs/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_emp_inactive_jobs',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );

    register_rest_route(
            'nokri/v1', '/employer/inactive_jobs/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_emp_inactive_jobs',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_emp_inactive_jobs')) {

    function nokriAPI_emp_inactive_jobs($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();
        $jobs = array();



        /* Getting Filtered  Jobs */
        $paged = ( get_query_var('paged') ) ? get_query_var('paged') : 1;
        $args = array(
            'author' => $user_id, 'post_type' => 'job_post', 'post_status' => array('publish', 'draft'),
            'meta_key' => '_job_status',
            'meta_value' => 'inactive'
        );
        $query = new WP_Query($args);
        $jobs = array();
        if ($query->have_posts()) {
            $count = 0;
            while ($query->have_posts()) {
                $query->the_post();
                $query->post_author;
                $job_id = get_the_id();
                $job_salary = get_post_meta($job_id, '_job_salary', true);
                $job_type = get_post_meta($job_id, '_job_type', true);
                $company_name = get_user_meta($user_id, '_emp_name', true);
                $job_expiry = get_post_meta($job_id, '_job_date', true);
                $job_status = get_post_meta($job_id, '_job_status', true);
                $job_location = get_post_meta($job_id, '_job_address', true);

                $jobs[$count][] = nokriAPI_employer_fields(__("Job Id", "nokri-rest-api"), get_the_id(), 'textfield', true, 1, 'job_id');
                $jobs[$count][] = nokriAPI_employer_fields(__("Job Title", "nokri-rest-api"), nokriAPI_convert_uniText(get_the_title()), 'textfield', true, 1, 'job_title');
                $jobs[$count][] = nokriAPI_employer_fields(__("Job Expiry", "nokri-rest-api"), $job_expiry, 'textfield', true, 1, 'job_expiry');
                $jobs[$count][] = nokriAPI_employer_fields(__("Job Type", "nokri-rest-api"), nokri_job_post_single_taxonomies('job_type', $job_type), 'textfield', true, 1, 'job_type');
                $jobs[$count][] = nokriAPI_employer_fields(__("Job Location", "nokri-rest-api"), $job_location, 'textfield', true, 1, 'job_location');
                $jobs[$count][] = nokriAPI_employer_fields(__("Inactive", "nokri-rest-api"), '', 'textfield', true, 1, 'inactive_job');

                $count++;
            }
        }
        $data['page_title'] = __("InActive Jobs", "nokri-rest-api");
        $data['jobs'] = $jobs;
        $message = __("You Have No InActive Jobs Yet", "nokri-rest-api");
        return $response = array('success' => true, 'data' => $data, 'message' => $message);
    }

}



/* * ********************************** */
/*  Employer Deleting His Job        */
/* * ********************************** */



add_action('rest_api_init', 'nokriAPI_emp_del_this_jobs_hook', 0);

function nokriAPI_emp_del_this_jobs_hook() {

    register_rest_route(
            'nokri/v1', '/employer/del_this_job/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_emp_del_this_job',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_emp_del_this_job')) {

    function nokriAPI_emp_del_this_job($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();
        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }

        $message = '';
        $job_id = (isset($json_data['job_id'])) ? trim($json_data['job_id']) : '';

        if ($job_id != "") {
            if ($job_id != '') {
                wp_trash_post($job_id);
            } else {
                $message = __("Something Went Wrong", "nokri-rest-api");
            }
        }

        return $response = array('success' => true, 'data' => '', 'message' => $message);
    }

}


/* * ********************************** */
/*  Employer Getting Templates       */
/* * ********************************** */


if (!function_exists('nokriAPI_get_templates_list')) {

    function nokriAPI_get_templates_list($user_id = '') {
        global $wpdb;
        /* Query For Getting All Resumes Against Job */
        $query = "SELECT meta_key, meta_value FROM $wpdb->usermeta WHERE user_id = '$user_id' AND meta_key like '_email_temp_name_$user_id%' ";
        $resumes = $wpdb->get_results($query);

        $data = array();
        foreach ($resumes as $resume) {
            $temps = base64_decode($resume->meta_value);
            $value = json_decode($temps, true);
            $data["$resume->meta_key"] = $value;
        }
        return $data;
    }

}




/* * ********************************** */
/*  Employer Action On Resumes       */
/* * ********************************** */



add_action('rest_api_init', 'nokriAPI_emp_action_on_resumes_hook', 0);

function nokriAPI_emp_action_on_resumes_hook() {

    register_rest_route(
            'nokri/v1', '/employer/get_templates/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_emp_action_resumes',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_emp_action_resumes')) {

    function nokriAPI_emp_action_resumes($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();
        $message = '';
        $res = nokriAPI_get_templates_list($user_id);
        $email_data = array();
        $count = 1;
        if (isset($res) && count($res) > 0) {
            foreach ($res as $key => $val) {
                $email_data[] = array("key" => esc_attr($key), "value" => esc_html($val['name']));
                $count++;
            }
        }
        $data['email_data'] = $email_data;


        /* Getting No email status */
        $cand_status = array();
        $cand_status = nokri_canidate_apply_status();
        $status_data = array();
        foreach ($cand_status as $key => $val) {
            $status_data[] = array("key" => esc_attr($key), "value" => esc_html($val));
        }
        $data['status_data'] = $status_data;

        $data['extra'][] = nokriAPI_employer_fields(__("Do you want to send email as well", "nokri-rest-api"), '', 'textfield', true, 1, 'email_send');
        $data['extra'][] = nokriAPI_employer_fields(__("Select status", "nokri-rest-api"), '', 'textfield', true, 1, 'select_status');
        $data['extra'][] = nokriAPI_employer_fields(__("Yes", "nokri-rest-api"), '', 'textfield', true, 1, 'email_yes');
        $data['extra'][] = nokriAPI_employer_fields(__("No", "nokri-rest-api"), '', 'textfield', true, 1, 'email_no');
        $data['extra'][] = nokriAPI_employer_fields(__("Select Email Template", "nokri-rest-api"), '', 'textfield', true, 1, 'email_temp');
        $data['extra'][] = nokriAPI_employer_fields(__("Email Subject", "nokri-rest-api"), '', 'textfield', true, 1, 'email_sub');
        $data['extra'][] = nokriAPI_employer_fields(__("Email Body", "nokri-rest-api"), '', 'textfield', true, 1, 'email_body');
        $data['extra'][] = nokriAPI_employer_fields(__("Send", "nokri-rest-api"), '', 'textfield', true, 1, 'email_btn');

        return $response = array('success' => true, 'data' => $data, 'message' => '');
    }

}


/* * ********************************** */
/*  Employer Templates Load         */
/* * ********************************** */

add_action('rest_api_init', 'nokriAPI_emp_template_loads_hook', 0);

function nokriAPI_emp_template_loads_hook() {

    register_rest_route(
            'nokri/v1', '/employer/template_load/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_emp_load_template',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_emp_load_template')) {

    function nokriAPI_emp_load_template($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();
        $template_data = array();
        $temp_val = (isset($json_data['temp_val'])) ? trim($json_data['temp_val']) : '';
        $meta_key = $temp_val;
        if ($meta_key != "") {
            $meta_data = get_user_meta($user_id, $meta_key, true);
            $meta_data = base64_decode($meta_data);
            $val = json_decode($meta_data, true);
            $template_name = $val['name'];
            $template_subject = $val['subject'];
            $template_body = $val['body'];
            $template_for = $val['for'];
        }
        $template_data[] = array("key" => 'name', "value" => esc_html($template_name));
        $template_data[] = array("key" => 'body', "value" => esc_html($template_body));
        $template_data[] = array("key" => 'status', "value" => esc_html($template_for));


        return $response = array('success' => true, 'data' => $template_data, 'message' => '');
    }

}


/* * ********************************** */
/*  Employer Sending Email          */
/* * ********************************** */

add_action('rest_api_init', 'nokriAPI_emp_sending_email_hook', 0);

function nokriAPI_emp_sending_email_hook() {

    register_rest_route(
            'nokri/v1', '/employer/sending_email/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_emp_sending_email',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_emp_sending_email')) {

    function nokriAPI_emp_sending_email($request) {
        global $nokri;
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();
        $message = '';

        $candidate_id = (isset($json_data['candidate_id'])) ? trim($json_data['candidate_id']) : '';
        $job_id = (isset($json_data['job_id'])) ? trim($json_data['job_id']) : '';
        $cand_status = (isset($json_data['cand_status'])) ? trim($json_data['cand_status']) : '';
        $is_send_mail = (isset($json_data['is_send_mail'])) ? trim($json_data['is_send_mail']) : '';
        $email_sub = (isset($json_data['email_sub'])) ? trim($json_data['email_sub']) : '';
        $email_body = (isset($json_data['email_body'])) ? trim($json_data['email_body']) : '';


        $candidate = get_userdata($candidate_id);

        if ($candidate_id != "") {
            update_post_meta($job_id, '_job_applied_status_' . $candidate_id, $cand_status);
        }
        if ($is_send_mail && $candidate) {
            $candidate_details = get_user_by('id', $candidate_id);
            $to = $candidate_details->user_email;
            $subject = $email_sub;
            $body = $email_body;
            nokri_employer_status_email($job_id, $candidate_id, $subject, $body);

            $message = __("Email Sent Successfully", "nokri-rest-api");
        } else {
            $message = __("Status update successfully", "nokri-rest-api");
        }
        return $response = array('success' => true, 'data' => '', 'message' => $message);
    }

}









/* * ******************************** */
/*   Company Public Profile       */
/* * ******************************* */



add_action('rest_api_init', 'nokriAPI_company_public_profile_hook', 0);

function nokriAPI_company_public_profile_hook() {
    register_rest_route(
            'nokri/v1', '/employer/public_profile/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_employer_public_profile',
        'permission_callback' => function () {
            return true;
        },
            )
    );
}

if (!function_exists('nokriAPI_employer_public_profile')) {

    function nokriAPI_employer_public_profile($request) {

        global $nokriAPI;
        global $nokri;
        $current_user_id = get_current_user_id();

        $json_data = $request->get_json_params();
        $company_id = (isset($json_data['company_id'])) ? $json_data['company_id'] : '';
        $page_number = (isset($json_data['page_number'])) ? $json_data['page_number'] : 1;
        $profile_msg = isset($nokriAPI['api_user_profile_msg']) ? $nokriAPI['api_user_profile_msg'] : '';
        $user_info = get_userdata($company_id);
        
        
        
        

        $map_switch = isset($nokri['emp_map_switch']) ? $nokri['emp_map_switch'] : "0";

        if (get_user_meta($company_id, '_user_profile_status', true) == 'pub' || $current_user_id == $company_id) {

            if ($page_number == 1) {
                $data['basic_ifo'] = nokriAPI_employer_get_profile($company_id, true);
            }
            $job_data = nokriAPI_emp_active_jobs2($company_id, '', $page_number, 'jobs');
            $message = '';
            $data['jobs'] = $job_data['jobs'];           
            $data['user_reviews'] = nokri_get_employer_rating($company_id);
            
            
            $data['pagination'] = $job_data['pagination'];

            $data['user_contact']['receiver_id'] = $user_info->ID;
            $data['user_contact']['receiver_name'] = __("Contact", "nokri-rest-api") . " " . $user_info->display_name;
            $data['user_contact']['receiver_email'] = $user_info->user_email;
            $data['user_contact']['sender_name'] = __("Please enter name", "nokri-rest-api");
            $data['user_contact']['sender_email'] = __("Please enter email", "nokri-rest-api");
            $data['user_contact']['sender_subject'] = __("Please enter subject", "nokri-rest-api");
            $data['user_contact']['sender_message'] = __("Please enter message", "nokri-rest-api");
            $data['user_contact']['btn_txt'] = __("Message", "nokri-rest-api");

            $data['extra'][] = nokriAPI_employer_fields(__("Company Long", "nokri-rest-api"), get_user_meta($company_id, 'emp_long', true), 'textfield', true, 1, 'comp_long');

            $data['extra'][] = nokriAPI_employer_fields(__("Company Lat", "nokri-rest-api"), get_user_meta($company_id, 'emp_lat', true), 'textfield', true, 1, 'comp_lat');

            $data['extra'][] = nokriAPI_employer_fields(__("Company Profile", "nokri-rest-api"), '', 'textfield', true, 1, 'comp_profile');
            $data['extra'][] = nokriAPI_employer_fields(__("Follow Us", "nokri-rest-api"), '', 'textfield', true, 1, 'comp_follow');
            $data['extra'][] = nokriAPI_employer_fields(__("Message", "nokri-rest-api"), '', 'textfield', true, 1, 'comp_message');
            $data['extra'][] = nokriAPI_employer_fields(__("Profile Detail", "nokri-rest-api"), '', 'textfield', true, 1, 'comp_detail');
            $data['extra'][] = nokriAPI_employer_fields(__("Company Details", "nokri-rest-api"), '', 'textfield', true, 1, 'page_title');

            $data['extra'][] = nokriAPI_employer_fields(__("Please login as candidate", "nokri-rest-api"), '', 'textfield', true, 1, 'login_as');
            $data['extra'][] = nokriAPI_employer_fields('', $map_switch, 'textfield', true, '', 'map_switch');
            
            $is_follow_show    =   isset($nokri['emp_det_follow_btn'])   ?  $nokri['emp_det_follow_btn']  : "";
            
            $data['is_follow_show']  =  $is_follow_show;
            
            
            
            $message = $job_data['message'];
            $success = true;
        } else {
            $data = array();
            $message = $profile_msg;
            $success = false;
        }

        $response = array('success' => $success, 'data' => $data, 'message' => $message);
        return $response;
    }

}

function nokri_get_emp_register_custom_feildsApi($author_id = '', $feilds_for = '', $id = '', $edit_profile = '', $show_profile = '') {

    $user_id = $author_id;
    $user = get_userdata($author_id);
    $registered = $user->user_registered;
    $edit_profile = $edit_profile;
    $args = array(
        'p' => $id,
        'post_type' => 'custom_feilds',
        'post_status' => 'publish',
        'posts_per_page' => -1,
        'meta_query' => array(array('key' => '_custom_feild_for', 'value' => $feilds_for)),
    );
    $args = nokri_wpml_show_all_posts_callback($args);
    $posts = new WP_Query($args);
    $custom_feilds = '';
    if ($posts->have_posts()) {
        while ($posts->have_posts()) {
            $posts->the_post();
            the_content();
            $id = get_the_id();
            $custom_feilds_for = get_post_meta($id, '_custom_feild_for', true);
            $custom_feilds = json_decode(get_post_meta($id, '_custom_feilds', true));
        }
    }
    wp_reset_query();
    $custom_feilds_html = $read_only = $requires = '';

    $myhtml = array();
    $name_hldr = __("Your full name", "nokri-rest-api");

    if (is_array($custom_feilds)) {
        foreach ($custom_feilds as $value) {
            $field_type = $value->feild_type;
            $field_label = $value->feild_label;
            $field_value = $value->feild_value;
            $field_required = $value->feild_req;
            $field_public = $value->feild_pub;

            $field_req = false;
            if ($field_required = "yes") {
                $field_req = true;
            } else {
                $field_req = false;
            }

            $field_pub = true;
            if ($field_public = "yes") {
                $field_pub = true;
            } else {
                $field_pub = false;
            }


            $field_values = (explode("|", $field_value));
            if (!$show_profile) {
                /* Check boxes */
                if ($field_type == 'RadioButton') {

                    foreach ($field_values as $value) {
                        $field_slug = preg_replace('/\s+/', '', $field_label);
                        $meta_value = get_user_meta($user_id, $field_slug, true) != '' ? get_user_meta($user_id, $field_slug, true) : '';
                        $checked = ($meta_value == $value) ? 'true' : 'false';
                    }

                    $myhtml[] = array("key" => esc_html($field_label), "field_type" => 'checkbox', "field_type_name" => "$field_slug", "value" => $meta_value, "is_required" => $field_req, "is_show" => $field_pub);
                }
                /* Input */
                if ($field_type == 'Input') {
                    $field_slug = preg_replace('/\s+/', '', $field_label);
                    $meta_value = get_user_meta($user_id, $field_slug, true) != '' ? get_user_meta($user_id, $field_slug, true) : '';
                    $myhtml[] = array("key" => $field_label, "field_type" => 'textfield', "field_type_name" => "$field_slug", "field_val" => $field_value, "value" => $meta_value, "is_required" => $field_req, "is_show" => $field_pub);
                }
                /* Number */
                if ($field_type == 'Number') {
                    $field_slug = preg_replace('/\s+/', '', $field_label);
                    $meta_value = get_user_meta($user_id, $field_slug, true) != '' ? get_user_meta($user_id, $field_slug, true) : '';
                    $requires = '';


                    $myhtml[] = array("key" => $field_label, "field_type" => 'Number', "field_type_name" => "$field_slug", "field_val" => $field_value, "value" => $meta_value, "is_required" => $field_req, "is_show" => $field_pub);
                }
                /* Text Area */
                if ($field_type == 'Text Area') {
                    $field_slug = preg_replace('/\s+/', '', $field_label);
                    $meta_value = get_user_meta($user_id, $field_slug, true) != '' ? get_user_meta($user_id, $field_slug, true) : '';
                    $myhtml[] = array("key" => $field_label, "field_type" => 'Textarea', "field_type_name" => "$field_slug", "field_val" => $field_value, "value" => '', "is_required" => $field_req, "is_show" => $field_pub);
                }
                /* Select Box */
                if ($field_type == 'Select Box') {
                    $options = $selected = '';
                    $field_slug = preg_replace('/\s+/', '', $field_label);
                    $meta_value = get_user_meta($user_id, $field_slug, true) != '' ? get_user_meta($user_id, $field_slug, true) : '';
                    $myhtml[] = array("key" => $field_slug, "field_type" => 'select', "field_type_name" => "$field_slug", "value" => $meta_value, "is_required" => $field_req, "is_show" => $field_pub);
                }
                /* Date  */
                if ($field_type == 'Date') {

                    $field_slug = preg_replace('/\s+/', '', $field_label);
                    $meta_value = get_user_meta($user_id, $field_slug, true) != '' ? get_user_meta($user_id, $field_slug, true) : '';

                    $myhtml[] = array("key" => $field_slug, "field_type" => 'date', "field_type_name" => "$field_slug", "field_val" => $field_value, "value" => $meta_value, "is_required" => $field_req, "is_show" => $field_pub);
                }
            } else {
                if ($field_pub == 'Yes') {
                    $field_slug = preg_replace('/\s+/', '', $field_label);
                    $meta_value = get_user_meta($user_id, $field_slug, true) != '' ? get_user_meta($user_id, $field_slug, true) : '';
                    if ($meta_value != '')
                        $custom_feilds_html .= '<li><small>' . $field_label . '</small><strong>' . $meta_value . '</li></strong>';
                    else
                        $custom_feilds_html .= '';
                }
            }
        }
    }
    return $myhtml;
}

function get_user_custom_location($countries, $levelz) {

    global $nokriAPI;
    $job_country_level_1 = ( isset($nokriAPI['API_job_country_level_1']) && $nokriAPI['API_job_country_level_1'] != "" ) ? $nokriAPI['API_job_country_level_1'] : '';
    $job_country_level_2 = ( isset($nokriAPI['API_job_country_level_2']) && $nokriAPI['API_job_country_level_2'] != "" ) ? $nokriAPI['API_job_country_level_2'] : '';
    $job_country_level_3 = ( isset($nokriAPI['API_job_country_level_3']) && $nokriAPI['API_job_country_level_3'] != "" ) ? $nokriAPI['API_job_country_level_3'] : '';
    $job_country_level_4 = ( isset($nokriAPI['API_job_country_level_4']) && $nokriAPI['API_job_country_level_4'] != "" ) ? $nokriAPI['API_job_country_level_4'] : '';
    $ad_countries = nokriAPI_get_cats('ad_location', 0);
    $country_html = $country_states = $country_cities = $country_towns = array();
    $country_html = array();
    $location_html = array();
    foreach ($ad_countries as $ad_country) {
        $children = get_terms($ad_country->taxonomy, array('parent' => $ad_country->term_id, 'hide_empty' => false));
        $has_child = ($children) ? true : false;
        if ($levelz > 0 && $ad_country->term_id == $countries[0]) {
            $selected = true;
        } else {
            $selected = false;
        }
        $country_html[] = array("key" => $ad_country->term_id, "value" => esc_html($ad_country->name), "selected" => $selected, "has_child" => $has_child);
    }
    if ($levelz >= 2) {
        $ad_states = nokriAPI_get_cats('ad_location', $countries[0]);


        foreach ($ad_states as $ad_state) {
            $children = get_terms($ad_state->taxonomy, array('parent' => $ad_state->term_id, 'hide_empty' => false));


            $has_child = ($children) ? true : false;
            if ($levelz > 0 && $ad_state->term_id == $countries[1]) {
                $selected = true;
            } else {
                $selected = false;
            }
            $country_states[] = array("key" => $ad_state->term_id, "value" => esc_html($ad_state->name), "selected" => $selected, "has_child" => $has_child);
        }
    }
    if ($levelz >= 3) {
        $ad_country_cities = nokriAPI_get_cats('ad_location', $countries[1]);
        foreach ($ad_country_cities as $ad_city) {
            $children = get_terms($ad_city->taxonomy, array('parent' => $ad_city->term_id, 'hide_empty' => false));
            $has_child = ($children) ? true : false;
            if ($levelz > 0 && $ad_city->term_id == $countries[2]) {
                $selected = true;
            } else {
                $selected = false;
            }
            $country_cities[] = array("key" => $ad_city->term_id, "value" => esc_html($ad_city->name), "selected" => $selected, "has_child" => $has_child);
        }
    }
    if ($levelz >= 4) {
        $ad_country_town = nokriAPI_get_cats('ad_location', $countries[2]);
        foreach ($ad_country_town as $ad_town) {
            $children = get_terms($ad_town->taxonomy, array('parent' => $ad_town->term_id, 'hide_empty' => false));
            $has_child = ($children) ? true : false;
            if ($levelz > 0 && $ad_town->term_id == $countries[3]) {
                $selected = true;
            } else {
                $selected = false;
            }
            $country_towns[] = array("key" => $ad_town->term_id, "value" => esc_html($ad_town->name), "selected" => $selected, "has_child" => $has_child);
        }
    }
    $job_country = $job_country_level_1;
    $location_html['job_country'] = nokriAPI_canidate_fields($job_country, $country_html, 'textfield', true, 2, 'job_country');
    $location_html['job_state'] = nokriAPI_canidate_fields($job_country_level_2, $country_states, 'textfield', true, 2, 'job_state');
    $location_html['job_city'] = nokriAPI_canidate_fields($job_country_level_3, $country_cities, 'textfield', true, 2, 'job_city');
    $job_town = $job_country_level_4;
    $location_html['job_town'] = nokriAPI_canidate_fields($job_town, $country_towns, 'textfield', true, 2, 'job_town');
    return $location_html;
}

/* * ********************************** */
/*   Employer Saved Resume            */
/* * ********************************** */
add_action('rest_api_init', 'nokriAPI_employer_saved_resumes_hook', 0);

function nokriAPI_employer_saved_resumes_hook() {
    register_rest_route(
            'nokri/v1', '/employer/saved_resumes', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_get_emp_saved_resume',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

function nokriAPI_get_emp_saved_resume($request) {

    global $nokri;
    global $nokriAPI;
    $json_data = $request->get_json_params();
    $page_number = $paged = (isset($json_data['page_number'])) ? $json_data['page_number'] : 1;
    $current_page = $paged;
    $user_per_page = (isset($nokriAPI['api_user_pagination'])) ? $nokriAPI['api_user_pagination'] : "10";
    $paged = (int) $paged;
    $get_offset = ($paged - 1);
    $offset = $get_offset * $user_per_page;
    $resumes_array = nokri_emp_saved_resumes_ids();
    $user_query = new WP_User_Query(
            array(
        'include' => $resumes_array,
        'number' => $limit,
        'offset' => $offset,
        'order' => $c_order,
    ));
    $saved_resumes = $user_query->get_results();
    $total_users = $user_query->get_total();
    $max_num_pages = ceil($total_users / $user_per_page);

    $jobs = array();
    $message = '';
    $check = array();

    if ($saved_resumes) {
        $count = 0;
        foreach ($saved_resumes as $key => $object) {
            $candidate_id = $object->ID;
            /* Getting Company  Profile Photo  */
            $image_link = nokri_get_user_profile_pic($candidate_id, '_cand_dp');
            $company_tot_jobs = ( count_user_posts($candidate_id, 'job_post') );
            $jobs[$count][] = nokriAPI_canidate_fields(__("Candidate Id", "nokri-rest-api"), $candidate_id, 'textfield', true, 1, 'candidate_id');
            $jobs[$count][] = nokriAPI_canidate_fields(__("Candidate Logo", "nokri-rest-api"), $image_link, 'textfield', true, 1, 'candidate_logo');
            $jobs[$count][] = nokriAPI_canidate_fields(__("Candidate Name", "nokri-rest-api"), get_the_author_meta('display_name', $candidate_id), 'textfield', true, 1, 'candidate_name');
            $jobs[$count][] = nokriAPI_canidate_fields(__("View Profile", "nokri-rest-api"), esc_url(get_author_posts_url($candidate_id)), 'textfield', true, 1, 'candidate_profile_link');
            $count++;
        }
    } else {
        $message = __("You have not saved any resume yet", "nokri-rest-api");
        $paged = $max_num_pages = $user_per_page = $total_users = '';
    }
    $data['page_title'] = __("Saved Companies", "nokri-rest-api");
    $data['btn_text'] = __("Remove", "nokri-rest-api");
    $data['Resumes'] = $jobs;
    /* Pagination */
    $nextPaged = (int) ($paged) + 1;
    $has_next_page = ( $nextPaged <= (int) $max_num_pages ) ? true : false;
    $pagination = array("max_num_pages" => (int) $max_num_pages, "current_page" => (int) $paged, "next_page" => (int) $nextPaged, "increment" => (int) $user_per_page, "total_followed" => (int) $total_users, "has_next_page" => $has_next_page);
    return $response = array('success' => true, 'data' => $data, 'pagination' => $pagination, 'message' => $message);
}

/* * ********************************** */
/*   Employer matched resumes           */
/* * ********************************** */



add_action('rest_api_init', 'nokriAPI_employer_jobs_matched_resume_hook', 0);

function nokriAPI_employer_jobs_matched_resume_hook() {
    register_rest_route(
            'nokri/v1', '/employer/jobs_matched_resumes', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_get_emp_jobs_matched_resume',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
    register_rest_route(
            'nokri/v1', '/employer/jobs_matched_resumes_filter', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_get_emp_jobs_matched_resume',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

function nokriAPI_get_emp_jobs_matched_resume($request) {

    $user_id = get_current_user_id();
    $json_data = $request->get_json_params();
    $job_class = (isset($json_data['job_class'])) ? trim($json_data['job_class']) : '';
    $page_number = (isset($json_data['page_number'])) ? trim($json_data['page_number']) : 1;
    return nokriAPI_emp_active_jobs3($user_id, $job_class, $page_number);
}

if (!function_exists('nokriAPI_emp_active_jobs3')) {

    function nokriAPI_emp_active_jobs3($user_id, $job_class = '', $page_number = 1, $return_arr = '') {
        $jobs = array();
        $job_filter = '';
        $meta_key = ( $job_class != "" ) ? 'package_job_class_' . $job_class : '';
        /* Filtering  Jobs By Class */
        if ($job_class != '') {
            $job_filter = array(
                'key' => $meta_key,
                'value' => $job_class,
                'compare' => '='
            );
        }

        if (get_query_var('paged')) {
            $paged = get_query_var('paged');
        } else if (isset($page_number)) {
            // This will occur if on front page.
            $paged = $page_number;
        } else {
            $paged = 1;
        }

        /* Getting Filtered  Jobs */
        $args = array(
            'post_type' => 'job_post',
            'orderby' => 'date',
            'order' => 'DESC',
            'author' => $user_id,
            'paged' => $paged,
            'post_status' => array('publish'),
            'meta_query' => array(
                'relation' => 'AND',
                $job_filter,
                array(
                    'key' => '_job_status',
                    'value' => 'active',
                    'compare' => '='
                )
            )
        );
        $query = new WP_Query($args);
        $jobs = array();
        if ($query->have_posts()) {
            $count = 0;
            while ($query->have_posts()) {
                $query->the_post();
                $query->post_author;
                $job_id = get_the_id();
                $post_author_id = get_post_field('post_author', $job_id);
                $autho_name = get_the_author_meta('display_name', $post_author_id);
                $job_deadline_n = get_post_meta($job_id, '_job_date', true);
                $job_deadline = date_i18n(get_option('date_format'), strtotime($job_deadline_n));
                $job_expiry = get_post_meta($job_id, '_job_date', true);
                $job_status = get_post_meta($job_id, '_job_status', true);
                $job_location = get_post_meta($job_id, '_job_address', true);


                $jobs[$count][] = nokriAPI_employer_fields(__("Job Id", "nokri-rest-api"), get_the_id(), 'textfield', true, 1, 'job_id');
                $jobs[$count][] = nokriAPI_employer_fields(__("Job Title", "nokri-rest-api"), nokriAPI_convert_uniText(get_the_title()), 'textfield', true, 1, 'job_name');
                $jobs[$count][] = nokriAPI_employer_fields(__("Posted Date", "nokri-rest-api"), date_i18n(get_option('date_format'), strtotime(get_the_date())), 'textfield', true, 1, 'job_post');
                $jobs[$count][] = nokriAPI_employer_fields(__("Job Expiry", "nokri-rest-api"), date_i18n(get_option('date_format'), strtotime($job_expiry)), 'textfield', true, 1, 'job_expiry');
                $jobs[$count][] = nokriAPI_employer_fields($job_status, '', 'textfield', true, 1, 'job_status');
                $jobs[$count][] = nokriAPI_employer_fields(__("View Resumes", "nokri-rest-api"), $job_id, 'textfield', true, 1, 'view_resumes');
                $count++;
            }
        }
        $data['page_title'] = __("Active Jobs", "nokri-rest-api");
        $data['job_filter'] = nokriAPI_employer_fields(__("Select job class", "nokri-rest-api"), nokriAPI_job_class_values(), 'textfield', true, 1, 'job_class');
        $data['jobs'] = $jobs;
        $message = __("you have no matched resumes", "nokri-rest-api");
        $nextPaged = $paged + 1;
        $has_next_page = ( $nextPaged <= (int) $query->max_num_pages ) ? true : false;
        $pagination = array(
            "max_num_pages" => (int) $query->max_num_pages,
            "current_page" => (int) $paged,
            "next_page" => (int) $nextPaged,
            "increment" => (int) get_option('posts_per_page'),
            "current_no_of_ads" => (int) count($query->posts),
            "has_next_page" => $has_next_page
        );
        $response = array('success' => true, 'data' => $data, 'message' => $message, "pagination" => $pagination);
        if ($return_arr == 'jobs') {
            return array('jobs' => $jobs, 'message' => $message, "pagination" => $pagination);
        } else {
            return $response;
        }
    }

}
/* * ********************************** */
/*     single job matched resumes       */
/* * ********************************** */
add_action('rest_api_init', 'nokriAPI_employer_single_job_matched_resume_hook', 0);

function nokriAPI_employer_single_job_matched_resume_hook() {

    register_rest_route('nokri/v1', '/employer/single_job_matched_resume/',
            array(
                'methods' => WP_REST_Server::EDITABLE,
                'callback' => 'nokriAPI_get_single_job_matched_resume',
                'permission_callback' => function () {
                    return nokriAPI_basic_auth();
                },
            )
    );
}

function nokriAPI_get_single_job_matched_resume($request) {

    global $nokri;
    global $nokriAPI;
    $json_data = $request->get_json_params();
    $job_id = (isset($json_data['job_id'])) ? trim($json_data['job_id']) : '';
    $page_number = $paged = (isset($json_data['page_number'])) ? $json_data['page_number'] : 1;
    $resumes_array = nokri_get_candidates_ids_for_matched_resumes($job_id);



    $current_page = $paged;
    $user_per_page = (isset($nokriAPI['api_user_pagination'])) ? $nokriAPI['api_user_pagination'] : "10";
    $paged = (int) $paged;
    $get_offset = ($paged - 1);
    $offset = $get_offset * $user_per_page;
    $limit = isset($nokri['user_pagination']) ? $nokri['user_pagination'] : 5;

    $jobs = array();
    if (!empty($resumes_array)) {
        $user_query = new WP_User_Query(
                array(
            'include' => $resumes_array,
            'number' => $limit,
            'orderby' => 'meta_value_num',
            'meta_key' => '_cand_skills_sum',
            'offset' => $offset,
        ));

        $authors = $user_query->get_results();
        $total_users = $user_query->get_total();
        $max_num_pages = ceil($total_users / $user_per_page);
        if ($authors) {
            $count = 0;
            foreach ($authors as $author) {
                // get all the user's id's
                $candidate_id = ($author->ID);
                $cand_headline = get_user_meta($candidate_id, '_user_headline', true);
                $cand_head = '';
                if ($cand_headline != '') {

                    $cand_head = $cand_headline;
                }
                $cand_adress = get_user_meta($candidate_id, '_cand_address', true);
                $job_date = get_post_meta($job_id, '_job_applied_date_' . $candidate_id, true);
                $resume_id = nokri_get_resume_publically($candidate_id, 'id');
                $array_data = explode('|', $cand_resume);
                $attachment_id = $array_data[1];
                $image_dp_link[0] = get_template_directory_uri() . '/images/company-logo.jpg';

                /* Getting Candidate Dp */
                if (get_user_meta($candidate_id, '_cand_dp', true) != "") {
                    $attach_dp_id = get_user_meta($candidate_id, '_cand_dp', true);
                    $image_dp_link = wp_get_attachment_image_src($attach_dp_id, '');
                }
                $jobs[$count][] = nokriAPI_employer_fields(__("Candidate Id", "nokri-rest-api"), $candidate_id, 'textfield', true, 1, 'cand_id');
                $jobs[$count][] = nokriAPI_employer_fields(__("Candidate Dp", "nokri-rest-api"), $image_dp_link[0], 'textfield', true, 1, 'cand_dp');
                $jobs[$count][] = nokriAPI_employer_fields(__("Candidate Name", "nokri-rest-api"), $author->display_name, 'textfield', true, 1, 'cand_name');
                $jobs[$count][] = nokriAPI_employer_fields(__("Candidate Headiing", "nokri-rest-api"), $cand_head, 'textfield', true, 1, 'cand_head');
                if ($resume_id != '') {
                    $jobs[$count][] = nokriAPI_employer_fields(__("Download", "nokri-rest-api"), wp_get_attachment_url($resume_id), 'textfield', true, 1, 'attachment_id');
                } else {

                    $jobs[$count][] = nokriAPI_employer_fields(__("Not Uploaded", "nokri-rest-api"), $resume_id, 'textfield', true, 1, 'attachment_id');
                }
                $count++;
            }
        }
    }
    $data['page_title'] = __("Matched Candidate", "nokri-rest-api");
    $data['jobs'] = $jobs;
    $message = (count($jobs) > 0 ) ? '' : __("No matched resume found.", "nokri-rest-api");
    /* Pagination */
    $nextPaged = (int) ($paged) + 1;
    $has_next_page = ( $nextPaged <= (int) $max_num_pages ) ? true : false;
    $pagination = array("max_num_pages" => (int) $max_num_pages, "current_page" => (int) $paged, "next_page" => (int) $nextPaged, "increment" => (int) $user_per_page, "total_resumes" => (int) $total_users, "has_next_page" => $has_next_page);
    return $response = array('success' => true, 'data' => $data, 'pagination' => $pagination, 'message' => $message);
}

/* * ********************************** */
/*   Get employer  Portfolio          */
/* * ********************************** */

add_action('rest_api_init', 'nokriAPI_employer_portfolio_hook', 0);

function nokriAPI_employer_portfolio_hook() {
    register_rest_route(
            'nokri/v1', '/employer/portfolio/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_employer_portfolio_get',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_employer_portfolio_get')) {

    function nokriAPI_employer_portfolio_get($user_id = '', $return_arr = false) {
        if (is_numeric($user_id)) {
            $user = get_userdata($user_id);
            $user_id = $user->ID;
        } else {
            $user_id = get_current_user_id();
            $user = get_userdata($user_id);
        }
        /* Getting Candidate Portfolio */
        if (get_user_meta($user_id, '_comp_gallery', true) != "") {
            $port = get_user_meta($user_id, '_comp_gallery', true);
            $portfolios = explode(',', $port);
            foreach ($portfolios as $portfolio) {
                $portfolio_image = wp_get_attachment_image_src($portfolio, '');
                $data['img'][] = nokriAPI_canidate_fields('Img_url', $portfolio_image[0], $portfolio, '', 1, 'Img_url');
            }
        } else {
            $data['img'] = array();
        }
        $video = false;
        $cand_video_url = get_user_meta($user_id, '_emp_video', true);
        $cand_video = '';
        if ($cand_video_url != '') {
            $rx = '~
							  ^(?:https?://)?                           # Optional protocol
							   (?:www[.])?                              # Optional sub-domain
							   (?:youtube[.]com/watch[?]v=|youtu[.]be/) # Mandatory domain name (w/ query string in .com)
							   ([^&]{11})                               # Video id of 11 characters as capture group 1
								~x';
            $valid = preg_match($rx, $cand_video_url, $matches);
            $cand_video = $matches[1];
            $video = true;
        }
        $data['extra'][] = nokriAPI_canidate_fields('Section label', __("My Portfolio", "nokri-rest-api"), '', '', 1, 'section_label');
        $data['extra'][] = nokriAPI_canidate_fields('Section name', __("Add Portfolio", "nokri-rest-api"), '', '', 1, 'section_name');
        $data['extra'][] = nokriAPI_canidate_fields('Section text', __("Photos for your projects only allowed jpg, png and jpeg and max file will not more than 800kb", "nokri-rest-api"), '', '', 1, 'section_txt');
        $data['extra'][] = nokriAPI_canidate_fields('Click text', __("Click To upload Your portfolio", "nokri-rest-api"), '', '', 1, 'click_text');
        $data['extra'][] = nokriAPI_canidate_fields('Button name', __("Save Portfolio", "nokri-rest-api"), '', '', 1, 'btn_name');
        $data['extra'][] = nokriAPI_canidate_fields('Delete Text', __("Delete Picture", "nokri-rest-api"), '', '', 1, 'del_txt');
        $data['extra'][] = nokriAPI_canidate_fields('Not Added', __("You have not uploaded any portfolio", "nokri-rest-api"), '', '', 1, 'not_added');

        $data['extra'][] = nokriAPI_canidate_fields(__("Video url (only youtube)", "nokri-rest-api"), $cand_video_url, '', $video, 1, 'video_url');

        $data['extra'][] = nokriAPI_canidate_fields('No Video url', __("No video url", "nokri-rest-api"), '', '', 1, 'no_video_url');

        $data['extra'][] = nokriAPI_canidate_fields('save button', __("Save video", "nokri-rest-api"), '', '', 1, 'video_save_btn');


        $response = array('success' => true, 'data' => $data, 'extras' => '');


        return ($return_arr) ? $data : $response;
    }

}
/* * ********************************** */
/*   Employer Add video url          */
/* * ********************************** */
add_action('rest_api_init', 'nokriAPI_employer_video_url_hook', 0);

function nokriAPI_employer_video_url_hook() {
    register_rest_route(
            'nokri/v1', '/employer/portfolio_video_url/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_employer_portfolio_video_url',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );

    register_rest_route(
            'nokri/v1', '/employer/portfolio_video_url/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_employer_portfolio_add_video_url',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

/* Getting vdieo url */
if (!function_exists('nokriAPI_employer_portfolio_video_url')) {

    function nokriAPI_employer_portfolio_video_url($user_id = '', $return_arr = false) {
        if (is_numeric($user_id)) {
            $user = get_userdata($user_id);
            $user_id = $user->ID;
        } else {
            $user_id = get_current_user_id();
            $user = get_userdata($user_id);
        }

        /* Getting Candidate Portfolio */
        if (get_user_meta($user_id, '_emp_video', true) != "") {
            $data['video_url'][] = nokriAPI_canidate_fields('video_url', get_user_meta($user_id, '_emp_video', true), '', '', 1, 'video_url');
        } else {
            $data['video_url'] = array();
        }

        $data['extra'][] = nokriAPI_canidate_fields('Section label', __("Video url (only youtube) ", "nokri-rest-api"), '', '', 1, 'section_label');

        $data['extra'][] = nokriAPI_canidate_fields('Button label', __("Save Video", "nokri-rest-api"), '', '', 1, 'button_label');



        $response = array('success' => true, 'data' => $data, 'extras' => '');


        return ($return_arr) ? $data : $response;
    }

}
/* Updating vdieo url */
if (!function_exists('nokriAPI_employer_portfolio_add_video_url')) {

    function nokriAPI_employer_portfolio_add_video_url($request) {
        $user_id = get_current_user_id();
        $json_data = $request->get_json_params();
        $video_url = (isset($json_data['video_url'])) ? $json_data['video_url'] : '';

        if (is_numeric($user_id)) {
            $user = get_userdata($user_id);
            $user_id = $user->ID;
        } else {
            $user_id = get_current_user_id();
            $user = get_userdata($user_id);
        }

        if (isset($json_data)) {
            $rx = '~
		  ^(?:https?://)?                           # Optional protocol
		   (?:www[.])?                              # Optional sub-domain
		   (?:youtube[.]com/watch[?]v=|youtu[.]be/) # Mandatory domain name (w/ query string in .com)
		   ([^&]{11})                               # Video id of 11 characters as capture group 1
			~x';
            $valid = preg_match($rx, $video_url, $matches);


            if ($valid || $valid == "") {
                $message = __("Successfully added", "nokri-rest-api");
                update_user_meta($user_id, '_emp_video', $video_url);
                $success = true;
            } else {
                $message = __("Video url not valid", "nokri-rest-api");
                $success = false;
            }
        } else {
            $message = __("No video added", "nokri-rest-api");
            $success = false;
        }
        return $response = array('success' => $success, 'data' => '', 'message' => $message);
    }

}
/* * ********************************** */
/*   Delete employer Portfolio      */
/* * ********************************** */
add_action('rest_api_init', 'nokriAPI_employer_del_portfolio_hook', 0);

function nokriAPI_employer_del_portfolio_hook() {
    register_rest_route(
            'nokri/v1', '/employer/del_portfolio/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_employer_del_portfolio',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_employer_del_portfolio')) {

    function nokriAPI_employer_del_portfolio($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);

        $json_data = $request->get_json_params();

        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }

        $portfolio_id = (isset($json_data['portfolio_id'])) ? trim($json_data['portfolio_id']) : '';


        /* Getting Portfolio */
        $attachmentid = $portfolio_id;


        if (get_user_meta($user_id, '_comp_gallery', true) != "") {
            $ids = get_user_meta($user_id, '_comp_gallery', true);
            $res = str_replace($attachmentid, "", $ids);
            $res = str_replace(',,', ",", $res);
            $img_ids = trim($res, ',');
            update_user_meta($user_id, '_comp_gallery', $img_ids);
        }


        wp_delete_attachment($attachmentid, true);

        $message = __("Deleted Successfully", "nokri-rest-api");

        return $response = array('success' => true, 'data' => '', 'message' => $message);
    }

}
/* * ********************************** */
/* Employer Portfolio Upload          */
/* * ********************************** */

add_action('rest_api_init', 'nokriAPI_employer_api_portfolio_upload_hook', 0);

function nokriAPI_employer_api_portfolio_upload_hook() {
    register_rest_route(
            'nokri/v1', '/employer/portfolio_upload/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_employer_api_portfolio_upload',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_employer_api_portfolio_upload')) {

    function nokriAPI_employer_api_portfolio_upload($request) {
        global $nokriAPI;

        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }

        /* Check image size from theme options */
        $size_arr = explode('-', $nokriAPI['api_upload_portfolio_size']);
        $display_size = $size_arr[1];
        $actual_size = $size_arr[0];


        /* Current user informations */
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $attach_id = '';
        $message = '';
        // These files need to be included as dependencies when on the front end.
        require_once( ABSPATH . 'wp-admin/includes/image.php' );
        require_once( ABSPATH . 'wp-admin/includes/file.php' );
        require_once( ABSPATH . 'wp-admin/includes/media.php' );

        $files = $_FILES["portfolio_upload"];
        foreach ($files['name'] as $key => $value) {
            if ($files['name'][$key]) {
                $file = array(
                    'name' => $files['name'][$key],
                    'type' => $files['type'][$key],
                    'tmp_name' => $files['tmp_name'][$key],
                    'error' => $files['error'][$key],
                    'size' => $files['size'][$key]
                );
                $_FILES = array("portfolio_upload" => $file);



                /* Check max image limit */
                $user_portfolio = get_user_meta($user_id, '_comp_gallery', true);
                if ($user_portfolio != "") {
                    $media = explode(',', $user_portfolio);
                    if (count($media) >= $nokriAPI['api_upload_portfolio_limit']) {
                        $message = esc_html__("You can not upload more than ", "nokri-rest-api") . " " . $nokriAPI['api_upload_portfolio_limit'] . " " . esc_html__("images ", 'nokri-rest-api');

                        return $response = array('success' => false, 'data' => '', 'message' => $message);
                    }
                }

                foreach ($_FILES as $file => $array) {
                    // Check uploaded size


                    if ($array['size'] > $actual_size) {
                        $message = __("Max allowed image size is", "nokri-rest-api") . " " . $display_size;
                        return $response = array('success' => false, 'data' => '', 'message' => $message);
                    }


                    $attach_id = media_handle_upload($file, -1);
                    if (!is_wp_error($attach_id)) {
                        $user_portfolio = get_user_meta($user_id, '_comp_gallery', true);
                        if ($user_portfolio != "") {
                            $updated_portfolio = $user_portfolio . ',' . $attach_id;
                        } else {
                            $updated_portfolio = $attach_id;
                        }
                        update_user_meta($user_id, '_comp_gallery', $updated_portfolio);
                        $message = __("Portfolio uploaded successfully", "nokri-rest-api");
                    } else {

                        $message = __($attach_id->errors['upload_error'][0]);
                        return $response = array('success' => false, 'data' => '', 'message' => $message);
                    }
                }
            }
        }


        $data = array();
        if (get_user_meta($user_id, '_comp_gallery', true) != "") {
            $port = get_user_meta($user_id, '_comp_gallery', true);
            $portfolios = explode(',', $port);
            foreach ($portfolios as $portfolio) {
                $portfolio_image = wp_get_attachment_image_src($portfolio, '');
                $data['logo_img'][] = nokriAPI_canidate_fields('Img_url', $portfolio_image[0], $portfolio, '', 1, 'Img_url');
            }
        } else {
            $data = array();
        }

        return $response = array('success' => true, 'data' => $data, 'message' => $message);
    }

}



/* * ********************************** */
/* Delete candidate against  job     */
/* * ********************************** */

add_action('rest_api_init', 'nokriAPI_employer_del_job_candidate_hook', 0);

function nokriAPI_employer_del_job_candidate_hook() {
    register_rest_route(
            'nokri/v1', '/employer/del_this_candidate', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_emp_del_this_candidate',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_emp_del_this_candidate')) {

    function nokriAPI_emp_del_this_candidate($request) {

        global $nokri;
        $json_data = $request->get_json_params();
        $cand_id = (isset($json_data['cand_id'])) ? $json_data['cand_id'] : '';
        $job_id = (isset($json_data['job_id'])) ? $json_data['job_id'] : '';
        /* demo check */
        $is_demo = nokri_demo_mode();
        if ($is_demo) {
            return $response = array('success' => false, 'data' => '', 'message' => __("Not Allowed in demo mode", "nokri-rest-api"));
        }
        delete_post_meta($job_id, '_job_applied_resume_' . $cand_id);
        delete_post_meta($job_id, '_job_applied_cover_' . $cand_id);
        delete_post_meta($job_id, '_job_applied_status_' . $cand_id);
        delete_post_meta($job_id, '_job_applied_date_' . $cand_id);
        $response = array('success' => true, 'data' => '', 'message' => __("Deleted successfully", "nokri-rest-api"));
        return $response;
    }

}


add_action('rest_api_init', 'nokriAPI_employer_comp_oprofile_hook', 0);

function nokriAPI_employer_comp_oprofile_hook() {
    register_rest_route(
            'nokri/v1', '/employer/comp_profile/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_employer_comp_profile_get',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_employer_comp_profile_get')) {

    function nokriAPI_employer_comp_profile_get($request) {
        $data = array();
        $update_personal_info = nokriAPI_employer_update_personal_info($request);
        $data['update_personal_info'] = $update_personal_info;

        $skills_get = nokriAPI_emp_update_skills($request);
        $data['skills_get '] = $skills_get;

        $social_link_get = nokriAPI_emp_update_social_link($request);
        $data['social_link_get'] = $social_link_get;


        $update_location_get = nokriAPI_emp_update_location($request);
        $data['update_location_get'] = $update_location_get;


        return $response = array('success' => true, 'data' => $data, 'message' => '', 'page_title' => '');
    }
}

// employer rating options

if (!function_exists('nokri_get_employer_rating')) {
    function nokri_get_employer_rating($user_id, $all = false, $paged = 1) {
      
        global $nokri;
        $reviews_title = isset($nokri['sb_write_reviews_title']) ? $nokri['sb_write_reviews_title'] : "";
        $first_rating = isset($nokri['sb_first_rating_stars_title']) ? $nokri['cand_first_rating_stars_title'] : "";
        $second_rating = isset($nokri['sb_second_rating_stars_title']) ? $nokri['cand_second_rating_stars_title'] : "";
        $third_rating = isset($nokri['sb_third_rating_stars_title']) ? $nokri['cand_third_rating_stars_title'] : "";
        $page_title    =   isset($nokri['sb_reviews_title'])   ?   $nokri['sb_reviews_title']   :  "";       
        $data = array();
        $extras = array();
        $extras['reviews_title']  = $reviews_title;
        $extras['first_rating']   = $first_rating;
        $extras['second_rating']  = $second_rating;
        $extras['third_rating']   = $third_rating;
        $extras['title_review']   = esc_html__('Review Title', 'nokri-rest-api');
        $extras['your_review']    = esc_html__('Your Review', 'nokri-rest-api');
        $extras['cand_id']        = $user_id;
        $extras['login_first']    = esc_html__('Please Login First', 'nokri-rest-api');
        $extras['enter_title']    = esc_html__('Please Enter Title', 'nokri-rest-api');
        $extras['enter_message']  = esc_html__('Please Enter Messsage', 'nokri-rest-api');
        $extras['submit']         = esc_html__('submit', 'nokri-rest-api');
        $extras['page_title']     = esc_html__($page_title, 'nokri-rest-api');
        $extras['reply_btn_text'] = esc_html__('Reply', 'nokri-rest-api');
        $extras['cancel_btn']     = esc_html__('Cancel', 'nokri-rest-api');
        $extras['add_review']     = esc_html__('Add Review', 'nokri-rest-api');
        $extras ['view_all']       = esc_html__('View All reviews','nokri-rest-api');
   
        $limit = isset($nokri['sb_reviews_count_limit']) ? $nokri['sb_reviews_count_limit'] : 5;

        if ($all) {
            $limit = 100;
        }
        $args = array(
            'user_id' => $user_id,
            'type' => 'dealer_review',
            'order' => 'DESC',
            'number' => $limit,
           // 'paged' => $paged,
        );
        
        
        $get_rating = get_comments($args);
        $image_link[0] = get_template_directory_uri() . '/images/candidate-dp.jpg';
        if (isset($nokri['nokri_user_dp']['url']) && $nokri['nokri_user_dp']['url'] != "") {
                 $image_link = array($nokri['nokri_user_dp']['url']);
              }            
        $review = array();
        $data = array();
        if (count((array) $get_rating) > 0) {
            $counter = 0;
            foreach ($get_rating as $get_ratings) {
                $comment_ids = $get_ratings->comment_ID;
                $service_stars = get_comment_meta($comment_ids, '_rating_service', true);
                $process_stars = get_comment_meta($comment_ids, '_rating_proces', true);
                $selection_stars = get_comment_meta($comment_ids, '_rating_selection', true);
                $comment_title = get_comment_meta($comment_ids, '_rating_title', true);
                $single_avg = 0;
                $total_stars = $service_stars + $process_stars + $selection_stars;
                $single_avg = round($total_stars / "3", 1);
                $comment_poster = get_userdata($get_ratings->comment_post_ID);
                $comment_poster_name = esc_html($comment_poster->display_name);

                $is_reply = get_comment_meta($comment_ids, '_rating_reply', true);
                if ($is_reply == "") {
                    $is_reply = true;
                } else {
                    $is_reply = false;
                }
                /* employer dp */
                if (get_user_meta($get_ratings->comment_post_ID, '_cand_dp', true) != "") {
                    $attach_id = get_user_meta($user_id, '_cand_dp', true);
                    if (is_numeric($attach_id)) {
                        $image_link = wp_get_attachment_image_src($attach_id, 'nokri_job_post_single');
                        
                    } 
                }
                          
                $reply_text     =    get_comment_meta($comment_ids, '_rating_reply', true);     
                
                $has_reply    =    false;
                if($reply_text  != ""){
                    
                    $has_reply   = true;
                    
                }                        
                $reply_text      =    get_comment_meta($comment_ids, '_rating_reply', true);              
                $review[$counter]['cid']     =   $comment_ids;
                $review[$counter]['_rating_service']     = $service_stars;
                $review[$counter]['_rating_proces']      = $process_stars;
                $review[$counter]['_rating_selection']   = $selection_stars;
                $review[$counter]['_rating_avg']         = $single_avg;
                $review[$counter]['_rating_title']       = esc_html($comment_title);
                $review[$counter]['_rating_description'] = strip_tags_content($get_ratings->comment_content);
                $review[$counter]['_rating_poster']      = $comment_poster_name;
                $review[$counter]['_rating_date']        = date_i18n(get_option('date_format'), strtotime($get_ratings->comment_date));
                $review[$counter]['can_reply']           = $is_reply;
                $review[$counter]['reply_text']          = $reply_text;
                $review[$counter]['has_reply']            = $has_reply;
                $review[$counter]['reply_heading']        = esc_html__('Candidate reply' ,'nokri-rest-api');               
                $review[$counter]['cand_image']           =  $image_link[0];
                $counter++;
            }
        }
        $data['reviews_data']   = $review;
        $data['extra']          = $extras;
        return $data;
    }
}

add_action('rest_api_init', 'nokriAPI_employer_get_rating', 0);
function nokriAPI_employer_get_rating() {
    register_rest_route(
            'nokri/v1', '/employer/get_ratings/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_employer_get_rating_fun',
        'permission_callback' => function () {
           return  nokriAPI_basic_auth();
        },
            )
    );
}
if (!function_exists('nokriAPI_employer_get_rating_fun')) {
    function nokriAPI_employer_get_rating_fun($request) {
        $json_data    = $request->get_json_params();
        
        $user_id      = isset($json_data['user_id']) ? $json_data['user_id'] : "";   
        
        $paged = 1;
        if (get_query_var('paged')) {
            $paged   = get_query_var('paged');
        } else if (isset($json_data['page_number'])) {
            $paged   = $json_data['page_number'];
        } else {
            $paged   = 1;
        }            
        $reviews_data          = nokri_get_employer_rating($user_id, true, $paged);
        $data                  = array();
        $data['user_reviews']  = $reviews_data;
        return array('success' => true, 'data' => $data, 'message' => '');    
    }
}

add_action('rest_api_init', 'nokriAPI_employer_post_rating', 0);
function nokriAPI_employer_post_rating() {
    register_rest_route(
            'nokri/v1', '/employer/post_ratings/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_employer_post_rating_fun',
        'permission_callback' => function () {
          return   nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_employer_post_rating_fun')) {

    function nokriAPI_employer_post_rating_fun($request) {

        $json_data              = $request->get_json_params();
        $rating_service_stars   = $json_data['rating_service'];
        $rating_process_stars   = $json_data['rating_process'];
        $rating_selection_stars = $json_data['rating_selection'];
        $review_title           = $json_data['review_title'];
        $review_message         = $json_data['review_message'];
        $employer_id            = $json_data['emp_id'];
            
        $current_user           = wp_get_current_user();
        $cand                   = get_current_user_id();
        $data                   = array();
        $user_type              = get_user_meta($cand, '_sb_reg_type', true);

        
   
        if ($employer_id  ==   $cand) {
            return array('success' => false, 'data' => $data, 'message' => esc_html__("You can not rate yourself", 'nokri-rest-api'));
            die();
        }
        
        
        if ($user_type   ==   1) {
            return array('success' => false, 'data' => $data, 'message' => esc_html__("Only candidate can do this", 'nokri-rest-api'));
            die();
        }
        
        if (get_user_meta($employer_id, '_is_rated_' . $cand, true) == $cand) {
            return array('success' => false, 'data' => $data, 'message' => esc_html__("You have already rated this user", 'nokri-rest-api'));
            die();
        } else {
                $time = current_time('mysql');
                $data = array(
                'comment_post_ID'      => $cand,
                'comment_author'       => $current_user->display_name,
                'comment_author_email' => $current_user->user_email,
                'comment_author_url'   => '',
                'comment_content'      => sanitize_text_field($review_message),
                'comment_type'         => 'dealer_review',
                'comment_parent'       => 0,
                'user_id'              => $employer_id,
                'comment_author_IP'    => $_SERVER['REMOTE_ADDR'],
                'comment_date'         => $time,
                'comment_approved'     => 0,
            );
            $comment_id   =    wp_insert_comment($data);
            update_comment_meta($comment_id, '_rating_service', sanitize_text_field($rating_service_stars));
            update_comment_meta($comment_id, '_rating_proces', sanitize_text_field($rating_process_stars));
            update_comment_meta($comment_id, '_rating_selection', sanitize_text_field($rating_selection_stars));
            update_comment_meta($comment_id, '_rating_title', sanitize_text_field($review_title));
            update_user_meta($employer_id, '_is_rated_' . $cand, $cand);

            $total_stars     = $rating_service_stars + $rating_process_stars + $rating_selection_stars;
            $ratting         = round($total_stars / "3", 1);
            // Send email if enabled
            if (isset($nokri['email_to_user_on_rating']) && $nokri['email_to_user_on_rating']) {
                //nokri_send_email_new_rating( $cand, $employer_id, $ratting, $review_message );
            }
            return array('success' => true, 'data' => $data, 'message' => esc_html__("You have succesfully rated this user", 'nokri-rest-api'));
            die();
        }
    }
}
