<?php

add_action('rest_api_init', 'nokriAPI_profile_api_hooks_dashboard', 0);

function nokriAPI_profile_api_hooks_dashboard() {
    register_rest_route(
            'nokri/v1', '/canidate/dashboard/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_mydashboard_get',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_mydashboard_get')) {

    function nokriAPI_mydashboard_get() {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);

        if ($user) {
            $user_id = @$user->data->ID;
        }


        $data['profile'] = nokriAPI_canidate_profile_get($user_id);
        $data['social_icons'] = nokriAPI_canidate_social_icons($user_id);
        $data['cand_cover'] = nokriAPI_candidate_cover($user_id);
        $data['cand_dp'] = nokriAPI_candidate_dp($user_id);

        $data['tabs']['dashboard1'] = __("Dashboard1", "nokri-rest-api");
        $data['tabs']['edit'] = __("Edit Profile", "nokri-rest-api");
        $data['tabs']['full_profile'] = __("Full Profile", "nokri-rest-api");
        $data['tabs']['dashboard4'] = __("Dashboard4", "nokri-rest-api");
        $data['tabs']['dashboard5'] = __("Dashboard5", "nokri-rest-api");
        $data['tabs']['dashboard6'] = __("Dashboard6", "nokri-rest-api");
        $data['tabs']['dashboard7'] = __("Dashboard7", "nokri-rest-api");

        $data['extra']['change_password'] = __("Change Password", "nokri-rest-api");

        $message = __("Edit", "nokri-rest-api");

        $response = array('success' => true, 'data' => $data, "message" => $message);
        return $response;
    }

}



/* Candidate Dashboard */
add_action('rest_api_init', 'nokriAPI_candidate_dashboard_api_hook', 0);

function nokriAPI_candidate_dashboard_api_hook() {
    register_rest_route(
            'nokri/v1', '/candidate/dashboard_tabs/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_candidate_dashboard',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}
if (!function_exists('nokriAPI_candidate_dashboard')) {

    function nokriAPI_candidate_dashboard() {
        global $nokriAPI;
        global $nokri;
        $appKey_stripeSKey = (isset($nokriAPI['appKey_stripeSKey']) ) ? $nokriAPI['appKey_stripeSKey'] : '';
        $appKey_stripePKey = (isset($nokriAPI['stripe_pulish_key']) ) ? $nokriAPI['stripe_pulish_key'] : '';
        $premium_jobs_heading = (isset($nokriAPI['premium_jobs_heading']) ) ? $nokriAPI['premium_jobs_heading'] : '';


        /* App about */
        $data['about']['about_section'] = (isset($nokriAPI['app_settings_about_switch']) && $nokriAPI['app_settings_about_switch']) ? true : false;
        $data['about']['about_title'] = (isset($nokriAPI['app_settings_about_title']) && $nokriAPI['app_settings_about_title']) ? $nokriAPI['app_settings_about_title'] : '';
        $data['about']['about_details'] = (isset($nokriAPI['app_settings_about_txt']) && $nokriAPI['app_settings_about_txt']) ? $nokriAPI['app_settings_about_txt'] : '';

        /* App version */
        $data['version']['version_section'] = (isset($nokriAPI['app_settings_version_switch']) && $nokriAPI['app_settings_version_switch']) ? true : false;
        $data['version']['version_txt'] = (isset($nokriAPI['app_settings_version_txt']) && $nokriAPI['app_settings_version_txt']) ? $nokriAPI['app_settings_version_txt'] : '';

        /* App rating */
        $data['rating']['rating_section'] = (isset($nokriAPI['app_settings_rating_switch']) && $nokriAPI['app_settings_rating_switch']) ? true : false;
        $data['rating']['rating_txt'] = (isset($nokriAPI['app_settings_rating_txt']) && $nokriAPI['app_settings_rating_txt']) ? $nokriAPI['app_settings_rating_txt'] : '';

        $data['rating']['rating_url_andriod'] = (isset($nokriAPI['app_settings_rating_url']) && $nokriAPI['app_settings_rating_url']) ? $nokriAPI['app_settings_rating_url'] : '';
        $data['rating']['app_id_ios'] = (isset($nokriAPI['app_settings_rating_id']) && $nokriAPI['app_settings_rating_id']) ? $nokriAPI['app_settings_rating_id'] : '';

        /* App share */
        $data['share']['share_section'] = (isset($nokriAPI['app_settings_share_switch']) && $nokriAPI['app_settings_share_switch']) ? true : false;
        $data['share']['popup_title'] = (isset($nokriAPI['app_settings_share_txt']) && $nokriAPI['app_settings_share_txt']) ? $nokriAPI['app_settings_share_txt'] : '';
        $data['share']['subject'] = (isset($nokriAPI['app_settings_share_subject']) && $nokriAPI['app_settings_share_subject']) ? $nokriAPI['app_settings_share_subject'] : '';
        $data['share']['url'] = (isset($nokriAPI['app_settings_share_url']) && $nokriAPI['app_settings_share_url']) ? $nokriAPI['app_settings_share_url'] : '';

        /* Privacy policy */
        $data['privacy']['privacy_section'] = (isset($nokriAPI['app_settings_privacy_switch']) && $nokriAPI['app_settings_privacy_switch']) ? true : false;
        $data['privacy']['privacy_title'] = (isset($nokriAPI['app_settings_privacy_title']) && $nokriAPI['app_settings_privacy_title']) ? $nokriAPI['app_settings_privacy_title'] : '';
        $data['privacy']['url'] = (isset($nokriAPI['app_settings_privacy_url']) && $nokriAPI['app_settings_privacy_url']) ? $nokriAPI['app_settings_privacy_url'] : '';

        /* Term & conidtions */
        $data['terms_n_conditions']['terms_section'] = (isset($nokriAPI['app_settings_terms_switch']) && $nokriAPI['app_settings_terms_switch']) ? true : false;
        $data['terms_n_conditions']['terms_title'] = (isset($nokriAPI['app_settings_terms_title']) && $nokriAPI['app_settings_terms_title']) ? $nokriAPI['app_settings_terms_title'] : '';
        $data['terms_n_conditions']['url'] = (isset($nokriAPI['app_settings_terms_url']) && $nokriAPI['app_settings_terms_url']) ? $nokriAPI['app_settings_terms_url'] : '';

        /* Feedback */
        $app_tandc_show = (isset($nokriAPI['app_feedback_show']) && $nokriAPI['app_feedback_show'] ) ? true : false;
        $data['feedback']['is_show'] = $app_tandc_show;
        $has_template = false;
        if (isset($nokri['job_post_form']) && $nokri['job_post_form'] == '1') {
            $has_template = true;
        }
        $data['job_form'] = $has_template;
        if ($app_tandc_show) {
            $data['feedback']['title'] = (isset($nokriAPI['app_feedback_title']) && $nokriAPI['app_feedback_title'] != "" ) ? $nokriAPI['app_feedback_title'] : __("Feedback", "nokri-rest-api");

            $data['feedback']['subline'] = (isset($nokriAPI['app_feedback_subline']) && $nokriAPI['app_feedback_subline'] != "" ) ? $nokriAPI['app_feedback_subline'] : __("Got any queries? We are here to help you!", "nokri-rest-api");
            $data['feedback']['form']['header'] = __("Feedback", "nokri-rest-api");
            $data['feedback']['form']['title'] = __("Enter Your Subject", "nokri-rest-api");
            $data['feedback']['form']['email'] = __("Enter Your Email", "nokri-rest-api");
            $data['feedback']['form']['message'] = __("Enter Your Feedback", "nokri-rest-api");
            $data['feedback']['form']['btn_submit'] = __("Submit", "nokri-rest-api");
            $data['feedback']['form']['btn_cancel'] = __("Cancel", "nokri-rest-api");
        }
        /* faqs */
        $data['faqs_section']['faq'] = (isset($nokriAPI['app_settings_faqs_switch']) && $nokriAPI['app_settings_faqs_switch']) ? true : false;

   
        $data['tabs']['loading'] = __("Please Wait", "nokri-rest-api");
        $data['tabs']['dashboard'] = __("Dashboard", "nokri-rest-api");
        $data['tabs']['edit'] = __("Update Profile", "nokri-rest-api");
        $data['tabs']['profile'] = __("My Profile", "nokri-rest-api");
        $data['tabs']['resume'] = __("My Resumes", "nokri-rest-api");
        $data['tabs']['apllied'] = __("Jobs Applied", "nokri-rest-api");
        $data['tabs']['saved'] = __("Saved Jobs", "nokri-rest-api");
        $data['tabs']['jobs'] = __("All Jobs", "nokri-rest-api");
        $data['tabs']['company'] = __("Followed Companies", "nokri-rest-api");
        $data['tabs']['search'] = __("Advance Job Search", "nokri-rest-api");
        $data['tabs']['cand_search'] = __("Candidate Search", "nokri-rest-api");
        $data['tabs']['blog'] = __("Blog", "nokri-rest-api");
        $data['tabs']['home'] = __("Home", "nokri-rest-api");
        $data['tabs']['faq'] = __("Faq's", "nokri-rest-api");
        $data['tabs']['logout'] = __("Logout", "nokri-rest-api");
        $data['tabs']['exit'] = __("Exit", "nokri-rest-api");


        $data['cand_tabs']['personal'] = __("Personal Information", "nokri-rest-api");
        $data['cand_tabs']['skills'] = __("Add Skills", "nokri-rest-api");
        $data['cand_tabs']['resumes'] = __("Add Resumes", "nokri-rest-api");
        $data['cand_tabs']['education'] = __("Educational Details", "nokri-rest-api");
        $data['cand_tabs']['experience'] = __("Job Experience", "nokri-rest-api");
        $data['cand_tabs']['certification'] = __("Certifications Detail", "nokri-rest-api");
        $data['cand_tabs']['portfolio'] = __("Add Portfolio", "nokri-rest-api");
        $data['cand_tabs']['socail'] = __("Social Links", "nokri-rest-api");
        $data['cand_tabs']['cand_search'] = __("Candidate search", "nokri-rest-api");
        $data['cand_tabs']['loca'] = __("Location And Address", "nokri-rest-api");
        $data['cand_tabs']['job_for'] = __("Jobs For You", "nokri-rest-api");
         $data['cand_tabs']['cand_notify'] = __("Candidate notifications", "nokri-rest-api");
        
        
       


        $data['emp_tabs']['dashboard'] = __("Dashboard", "nokri-rest-api");
        $data['emp_tabs']['profile'] = __("Update Profile", "nokri-rest-api");
        $data['emp_tabs']['templates'] = __("Email Templates", "nokri-rest-api");
        $data['emp_tabs']['jobs'] = __("My Jobs", "nokri-rest-api");
        $data['emp_tabs']['followers'] = __("Followers", "nokri-rest-api");
        $data['emp_tabs']['post_job'] = __("Post Job", "nokri-rest-api");
        $data['emp_tabs']['pkg_detail'] = __("Package Details", "nokri-rest-api");
        $data['emp_tabs']['buy_package'] = __("Buy Package", "nokri-rest-api");
        $data['emp_tabs']['cand_search'] = __("Candidate Search", "nokri-rest-api");
        $data['emp_tabs']['blog'] = __("Blog", "nokri-rest-api");
        $data['emp_tabs']['home'] = __("Home", "nokri-rest-api");
        $data['emp_tabs']['faq'] = __("Faq's", "nokri-rest-api");
        $data['emp_tabs']['logout'] = __("Logout", "nokri-rest-api");
        $data['emp_tabs']['exit'] = __("Exit", "nokri-rest-api");


        $data['emp_jobs']['active'] = __("Active", "nokri-rest-api");
        $data['emp_jobs']['inactive'] = __("Inactive", "nokri-rest-api");


        $data['public_jobs']['premium'] = $premium_jobs_heading;
        $data['public_jobs']['latest'] = __("Latest jobs", "nokri-rest-api");

        $data['compny_public_jobs']['open'] = __("Open Positions", "nokri-rest-api");
        $data['compny_public_jobs']['details'] = __("Company Details", "nokri-rest-api");


        $data['progress_txt']['title'] = __("Uploading", "nokri-rest-api");
        $data['progress_txt']['title_success'] = __("Uploaded", "nokri-rest-api");
        $data['progress_txt']['title_fail'] = __("Failed", "nokri-rest-api");
        $data['progress_txt']['msg_success'] = __("File uploaded", "nokri-rest-api");
        $data['progress_txt']['msg_fail'] = __("File upload failed", "nokri-rest-api");
        $data['progress_txt']['btn_ok'] = __("Ok", "nokri-rest-api");



        $data['guest_tabs']['guset'] = __("Welcome Guest", "nokri-rest-api");
        $data['guest_tabs']['cand_dp'] = nokriAPI_candidate_dp();
        $data['guest_tabs']['guset'] = __("Welcome Guest", "nokri-rest-api");
        $data['guest_tabs']['home'] = __("Home", "nokri-rest-api");
        $data['guest_tabs']['faq'] = __("Faq's", "nokri-rest-api");
        $data['guest_tabs']['explore'] = __("Explore", "nokri-rest-api");
        $data['guest_tabs']['cand_search'] = __("Candidate search", "nokri-rest-api");
        $data['guest_tabs']['templates'] = __("Blog", "nokri-rest-api");
        $data['guest_tabs']['signin'] = __("Sign In", "nokri-rest-api");
        $data['guest_tabs']['signup'] = __("Sign Up", "nokri-rest-api");
        $data['guest_tabs']['exit'] = __("Exit", "nokri-rest-api");


        $data['emp_edit_tabs']['info'] = __("Basic Information", "nokri-rest-api");
        $data['emp_edit_tabs']['special'] = __("Company Specialization", "nokri-rest-api");
        $data['emp_edit_tabs']['social'] = __("Company Social Links", "nokri-rest-api");
        $data['emp_edit_tabs']['loc'] = __("Set Your Location", "nokri-rest-api");


        $data['generic_txts']['confirm'] = __("Are you sure?", "nokri-rest-api");
        $data['generic_txts']['btn_cancel'] = __("Cancel", "nokri-rest-api");
        $data['generic_txts']['btn_confirm'] = __("Confirm", "nokri-rest-api");
        $data['generic_txts']['success'] = __("Success", "nokri-rest-api");


        $data['menu_active']['resume'] = __("Resume received", "nokri-rest-api");
        $data['menu_active']['edit'] = __("Edit", "nokri-rest-api");
        $data['menu_active']['del'] = __("Delete", "nokri-rest-api");
        $data['menu_active']['view'] = __("View", "nokri-rest-api");


        $data['menu_job']['view'] = __("View job", "nokri-rest-api");
        $data['menu_job']['company'] = __("View company", "nokri-rest-api");


        $data['menu_resume']['action'] = __("Take action", "nokri-rest-api");
        $data['menu_resume']['download'] = __("Download", "nokri-rest-api");
        $data['menu_resume']['linkedin'] = __("Linkedin profile", "nokri-rest-api");
        $data['menu_resume']['profile'] = __("View profile", "nokri-rest-api");

        $data['menu_saved']['view'] = __("View job", "nokri-rest-api");
        $data['menu_saved']['delete'] = __("Delete job", "nokri-rest-api");


        $cand_map_switch = isset($nokri['cand_map_switch']) ? $nokri['cand_map_switch'] : "0";
        
       if($cand_map_switch  == "1"){
           
           $cand_map_switch   =  true;
       }
       
       else{
           $cand_map_switch   =  false;
       }
       
        $emp_map_switch = isset($nokri['emp_map_switch']) ? $nokri['emp_map_switch'] : "0";
        if($emp_map_switch  == "1"){          
           $emp_map_switch   =  true;
       }     
       else{
           $emp_map_switch   =  false;
       }
  
       
         $data['extra']['cand_map_switch'] = $cand_map_switch;
         $data['extra']['emp_map_switch'] = $emp_map_switch;
        $data['extra']['stripe_Skey'] = $appKey_stripeSKey;
        $data['extra']['stripe_Pkey'] = $appKey_stripePKey;
        $data['extra']['nxt_step'] = __("Next step", "nokri-rest-api");
        $data['extra']['is_login'] = __("Please login first", "nokri-rest-api");
        $data['extra']['is_login_emp'] = __("Please login as employer", "nokri-rest-api");
        $data['extra']['all_fields'] = __("All fields are mandatory", "nokri-rest-api");
        $data['extra']['no_url'] = __("No url found", "nokri-rest-api");
        $data['extra']['coment_first'] = __("Please write a comment first", "nokri-rest-api");
        $data['extra']['rply_first'] = __("Please write a reply first", "nokri-rest-api");
        $data['extra']['no_app'] = __("No app found to open this file", "nokri-rest-api");
        $data['extra']['exit'] = __("Exit?", "nokri-rest-api");
        $data['extra']['valid_email'] = __("Email format not valid", "nokri-rest-api");
        $data['extra']['agree_term'] = __("You must agree with our term and services", "nokri-rest-api");
        $data['extra']['click_back'] = __("Please click back again to exit", "nokri-rest-api");
        $data['extra']['valid_skill'] = __("Please select a valid skill from the droprown", "nokri-rest-api");
        $data['extra']['select_one'] = __("Plase select at least one skill", "nokri-rest-api");
        $data['extra']['not_instal'] = __("This app is not installed", "nokri-rest-api");
        $data['extra']['invalid_url'] = __("invalid url", "nokri-rest-api");
        $data['extra']['select_txt'] = __("Select", "nokri-rest-api");
        $data['extra']['camera_txt'] = __("Camera", "nokri-rest-api");
        $data['extra']['gallery_txt'] = __("Gallery", "nokri-rest-api");
        $data['extra']['not_camera'] = __("Camera not available", "nokri-rest-api");
        $data['extra']['not_gallery'] = __("Gallery not available", "nokri-rest-api");
        $data['extra']['settings_txt'] = __("Settings", "nokri-rest-api");
        $data['extra']['place_hldr'] = __("Search job here", "nokri-rest-api");
        $data['extra']['saved_resumes'] = __("Saved resumes", "nokri-rest-api");
        $data['extra']['view_profile'] = __("View", "nokri-rest-api");
        $data['extra']['remove_resume'] = __("Remove", "nokri-rest-api");
        $data['extra']['matched_resume'] = __("Matched Resume", "nokri-rest-api");
        $data['extra']['log_txt'] = __("Go to login", "nokri-rest-api");
        $data['extra']['Jobs_for'] = __("Jobs for you", "nokri-rest-api");
        $data['extra']['notify_page'] = __("Job notification", "nokri-rest-api");
        
         $data['extra']['ext_cancel_btn'] = __("Cancel", "nokri-rest-api");
         $data['extra']['ext_confirm_btn'] = __("Confirm", "nokri-rest-api");
         $data['extra']['ext_confrim_title'] = __("Confirmation", "nokri-rest-api");
         $data['extra']['ext_confirm_content'] = __("You are redirecting to external apply", "nokri-rest-api");
         $data['extra']['skill_txt'] = __("Skills", "nokri-rest-api");
         $data['extra']['select_opt'] = __("Select option", "nokri-rest-api");
         $data['extra']['emp_search_txt'] = __("Employer Search", "nokri-rest-api");
       
         $data['extra']['e_email_txt'] = __("Email", "nokri-rest-api");
         $data['extra']['e_alert_txt'] = __("Alert Name", "nokri-rest-api");
         $data['extra']['e_next_txt'] = __("Next", "nokri-rest-api");
         $data['extra']['e_back_txt'] = __("Back", "nokri-rest-api");
         $data['extra']['e_jalert_txt'] = __("Job Alert", "nokri-rest-api");
         $data['extra']['enter_linked_url'] = __("Enter your LinkedIn public profile url here", "nokri-rest-api");
     
          $data['extra']['valid_link_text'] = __("Please enter a valid linked in url", "nokri-rest-api");  
          $data['extra']['have_applied'] = __("Applied", "nokri-rest-api");  
           $data['extra']['password_mismatch'] = __("Password does not match", "nokri-rest-api"); 
            $data['extra']['pass_confirm'] = __("Confirm password", "nokri-rest-api"); 
          
          
          $style_check  =  isset($nokriAPI['app_theme_style'])  ?  $nokriAPI['app_theme_style'] : "style1";
            $data['extra']['app_style_check'] = $style_check; 
          
        $job_alerts = ( isset($nokri['job_alerts_switch']) && $nokri['job_alerts_switch'] != "" ) ? $nokri['job_alerts_switch'] : false;
        if ($job_alerts) {

            $job_alerts_title = ( isset($nokri['job_alerts_title']) && $nokri['job_alerts_title'] != "" ) ? $nokri['job_alerts_title'] : '';
            /* Job alert tagline */
            $job_alerts_tagline = ( isset($nokri['job_alerts_tagline']) && $nokri['job_alerts_tagline'] != "" ) ? $nokri['job_alerts_tagline'] : '';
            /* Job alert btn */
            $job_alerts_btn = ( isset($nokri['job_alerts_btn']) && $nokri['job_alerts_btn'] != "" ) ? $nokri['job_alerts_btn'] : '';


            $data['extra']['job_alerts_title '] = __($job_alerts_title, "nokri-rest-api");
            $data['extra']['job_alerts_tagline'] = __($job_alerts_tagline, "nokri-rest-api");
            $data['extra']['job_alerts_btn'] = __($job_alerts_btn, "nokri-rest-api");
            $data['extra']['job_alerts_btn'] = __($job_alerts_btn, "nokri-rest-api");

            $data['extra']['alert_name'] = __("Alert", "nokri-rest-api");
            $data['extra']['alert_email'] = __("Your Email", "nokri-rest-api");
            $data['extra']['email_freq_plc'] = __("Select email frequency", "nokri-rest-api");
            $data['extra']['external_email_link'] = __("Enter Valid Email", "nokri-rest-api");
            $data['extra']['external_link'] = __("Put Link Here", "nokri-rest-api");
            $data['extra']['job_desc'] = __("Please add description", "nokri-rest-api");
            
            
            
            
            if (nokri_add_taxonomies_on_job_alert('job_type', true)) {
                $data['extra']['job_type_plc'] = __("Job Type", "nokri-rest-api");
            }
            if (nokri_add_taxonomies_on_job_alert('job_experience', true)) {
                $data['extra']['job_experience_plc'] = __("Job Experience", "nokri-rest-api");
            }
            if (nokri_add_taxonomies_on_job_alert('ad_location', true)) {
                $data['extra']['job_location_plc'] = __("Job Location", "nokri-rest-api");
            }
            if (nokri_add_taxonomies_on_job_alert('job_category', true)) {
                $data['extra']['job_category_plc'] = __("Job Category", "nokri-rest-api");
            }
        }
        $adds = false;
        if (isset($nokriAPI['api_ad_show']) && $nokriAPI['api_ad_show'] == true) {
            $adds = true;
        }
        $data['ads']['show'] = $adds;
        $data['ads']['type'] = 'banner';
        $is_show_banner = (isset($nokriAPI['api_ad_type_banner']) && $nokriAPI['api_ad_type_banner']) ? true : false;
        $data['ads']['is_show_banner'] = $is_show_banner;
        if ($is_show_banner) {
            $ad_position = (isset($nokriAPI['api_ad_position']) && $nokriAPI['api_ad_position'] != "") ? $nokriAPI['api_ad_position'] : 'top';
            $data['ads']['position'] = $ad_position;

            $ad_key_banner = ( NOKRI_API_REQUEST_FROM == 'ios' ) ? $nokriAPI['api_ad_key_banner_ios'] : $nokriAPI['api_ad_key_banner'];

            $data['ads']['banner_id'] = $ad_key_banner;
        }

        $is_show_initial = (isset($nokriAPI['api_ad_type_initial']) && $nokriAPI['api_ad_type_initial']) ? true : false;
        $data['ads']['is_show_initial'] = $is_show_initial;
        if ($is_show_initial) {
            $data['ads']['time_initial'] = ($nokriAPI['api_ad_time_initial'] != "" ) ? $nokriAPI['api_ad_time_initial'] : 30;
            $data['ads']['time'] = ($nokriAPI['api_ad_time'] != "" ) ? $nokriAPI['api_ad_time'] : 30;

            $ad_id = ( NOKRI_API_REQUEST_FROM == 'ios' ) ? $nokriAPI['api_ad_key_ios'] : $nokriAPI['api_ad_key'];
            $data['ads']['ad_id'] = $ad_id;
        }



        /* Is RTL */
        $is_rtl = (isset($nokriAPI['app_settings_rtl'])) ? $nokriAPI['app_settings_rtl'] : "";
        $data['is_rtl'] = $is_rtl;
        $data['app_color'] = (isset($nokriAPI['button-set-colour'])) ? $nokriAPI['button-set-colour'] : "#fb236a";
        $data['home'] = (isset($nokriAPI['api_home_screen_style'])) ? $nokriAPI['api_home_screen_style'] : "1";
        $data['isBlog'] = "0";
        if (isset($nokriAPI['home_secreen_blog']) && $nokriAPI['home_secreen_blog'] == '1') {
            $data['isBlog'] = '1';
        }


        $message = '';

        $response = array('success' => true, 'data' => $data, "message" => $message);
        return $response;
    }

}


/* * ************************ */
/* Email feedback */
/* * ************************ */

add_action('rest_api_init', 'nokriAPI_feedback_get_hook', 0);

function nokriAPI_feedback_get_hook() {
    register_rest_route(
            'nokri/v1', '/feedback/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_app_extra_api_feedback_func',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_app_extra_api_feedback_func')) {

    function nokriAPI_app_extra_api_feedback_func($request) {
        global $nokriAPI;

        $json_data = $request->get_json_params();
        $subject = (isset($json_data['subject'])) ? trim($json_data['subject']) : '';
        $email = (isset($json_data['email'])) ? trim($json_data['email']) : '';
        $message = (isset($json_data['message'])) ? trim($json_data['message']) : '';


        $admin_email = (isset($nokriAPI['app_feedback_admin_email']) && $nokriAPI['app_feedback_admin_email'] != "" ) ? $nokriAPI['app_feedback_admin_email'] : "";
        if ($admin_email == "") {
            return $response = array('success' => false, 'data' => '', 'message' => __("Admin email not setup.", "nokri-rest-api"));
        }
        if ($subject == "") {
            return $response = array('success' => false, 'data' => '', 'message' => __("Please enter your subject.", "nokri-rest-api"));
        }
        if ($email == "") {
            return $response = array('success' => false, 'data' => '', 'message' => __("Please enter your email.", "nokri-rest-api"));
        }
        if ($message == "") {
            return $response = array('success' => false, 'data' => '', 'message' => __("Please enter your message.", "nokri-rest-api"));
        }

        /* Send feedback email */
        if (NOKRI_API_REQUEST_FROM == 'ios') {
            $feednack_on = __("IOS", "nokri-rest-api");
        } else {
            $feednack_on = __("Android", "nokri-rest-api");
        }

        $from = get_bloginfo('name');
        if (isset($nokriAPI['sb_app_feedback_from']) && $nokriAPI['sb_app_feedback_from'] != "") {
            $from = $nokriAPI['sb_app_feedback_from'];
        }

        $headers = array('Content-Type: text/html; charset=UTF-8', "From: $from");

        $subject_keywords = array('%site_name%', '%feedback_from%');
        $subject_replaces = array(get_bloginfo('name'), $feednack_on);
        $subject_title = str_replace($subject_keywords, $subject_replaces, $nokriAPI['sb_app_feedback_subject']);

        $msg_keywords = array('%feedback_subject%', '%feedback_email%', '%feedback_message%', '%feedback_from%');
        $msg_replaces = array($subject, $email, $message, $feednack_on);
        $body = str_replace($msg_keywords, $msg_replaces, $nokriAPI['sb_app_feedback_message']);

        $to = $admin_email;

        $mail_sent = wp_mail($to, $subject_title, $body, $headers);
        if ($mail_sent) {
            return $response = array('success' => true, 'data' => '', 'message' => __("Feedback submitted successfully.", "nokri-rest-api"));
        } else {
            return $response = array('success' => false, 'data' => '', 'message' => __("Something went wrong.", "nokri-rest-api"));
        }
    }

}