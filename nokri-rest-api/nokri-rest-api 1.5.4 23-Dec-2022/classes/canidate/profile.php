<?php

add_action('rest_api_init', 'nokriAPI_canidate_profile_hook', 0);

function nokriAPI_canidate_profile_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/profile/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_canidate_profile_get',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_profile_get')) {

    function nokriAPI_canidate_profile_get($user_id = '', $return_arr = false) {

        global $nokriAPI;
        global $nokri;
        if (is_numeric($user_id)) {
            $user = get_userdata($user_id);
            $user_id = $user->ID;
        } else {
            $user_id = get_current_user_id();
            $user = get_userdata($user_id);
        }
        $registered = $user->user_registered;
        /* Getting Cand type */
        $cand_type = get_user_meta($user->ID, '_cand_type', true);
        $get_term_type = get_term_by('id', $cand_type, 'job_type');
        if ($cand_type) {
            $cand_type_name = $get_term_type->name;
        } else {
            $cand_type_name = '';
        }


        $resumes_link = nokri_get_resume_publically_api($user_id, '');
        $resume_name = nokri_get_resume_name_publically_api($user_id, '');
        $cand_resume_down = (isset($nokri['cand_resume_down'])) ? $nokri['cand_resume_down'] : true;
        echo nokri_updating_candidate_profile_percent();
        $profile_percent = get_user_meta($user_id, '_cand_profile_percent', true);

        $map_switch = isset($nokri['cand_map_switch']) ? $nokri['cand_map_switch'] : "0";
        /* Getting Cand level */
        $cand_level = get_user_meta($user->ID, '_cand_level', true);

        $get_term_level = get_term_by('id', $cand_level, 'job_level');

        if ($cand_level) {
            $cand_level_name = $get_term_level->name;
        } else {
            $cand_level_name = '';
        }

        /* Getting Cand experience */
        $cand_experience = get_user_meta($user->ID, '_cand_experience', true);

        $get_term_experience = get_term_by('id', $cand_experience, 'job_experience');

        if ($cand_experience) {
            $cand_experience_name = $get_term_experience->name;
        } else {
            $cand_experience_name = '';
        }

        $salary_type = get_user_meta($user->ID, '_cand_salary_type', true);
        $salary_range = get_user_meta($user->ID, '_cand_salary_range', true);
        $salary_curren = get_user_meta($user->ID, '_cand_salary_curren', true);

        $salry_full = nokri_job_post_single_taxonomies('job_currency', $salary_curren) . " " . nokri_job_post_single_taxonomies('job_salary', $salary_range) . " " . '/' . " " . nokri_job_post_single_taxonomies('job_salary_type', $salary_type);


        /* Map default values */
        $cand_map_long = get_user_meta($user->ID, '_cand_map_long', true);
        if ($cand_map_long == '') {
            $cand_map_long = $nokriAPI['sb_default_long'];
        }
        $cand_map_lat = get_user_meta($user->ID, '_cand_map_lat', true);
        if ($cand_map_lat == '') {
            $cand_map_lat = $nokriAPI['sb_default_lat'];
        }
        /* Profile setting option */
        $is_show_profile_option = (isset($nokriAPI['user_profile_setting_option_API']) && $nokriAPI['user_profile_setting_option_API']) ? true : false;



        $info[] = nokriAPI_canidate_fields(__("Your DashBoard", "nokri-rest-api"), '', 'textfield', true, '', 'your_dashbord');
        $name_hldr = __("Your full name", "nokri-rest-api");
        $info[] = nokriAPI_canidate_fields(__("Name", "nokri-rest-api"), $user->display_name, $name_hldr, true, '', 'cand_name');

        if (isset($nokriAPI['api_user_contact']) && ($nokriAPI['api_user_contact']) == "1") {
            $info[] = nokriAPI_canidate_fields(__("Email", "nokri-rest-api"), $user->user_email, 'textfield', true, '', 'cand_email');
            $phone_hldr = __("Contact number", "nokri-rest-api");
            $info[] = nokriAPI_canidate_fields(__("Phone", "nokri-rest-api"), get_user_meta($user->ID, '_sb_contact', true), 'textfield', true, '', 'cand_phone');
        }

        $info[] = nokriAPI_canidate_fields(nokri_feilds_label('cand_mem', esc_html__('Member Since', 'nokri')), date("M Y", strtotime($registered)), 'textfield', true, '', 'cand_rgstr');
        $bday_hldr = __("Select date of birth", "nokri-rest-api");
        $info[] = nokriAPI_canidate_fields(nokri_feilds_label('cand_dob_label', esc_html__('Date of birth:', 'nokri')), get_user_meta($user->ID, '_cand_dob', true), $bday_hldr, true, '', 'cand_dob');

        $gender_hldr = __("Gender", "nokri-rest-api");
        $gender_val = get_user_meta($user->ID, '_cand_gender', true);
        if ($gender_val == "male") {

            $gender_val = esc_html__('Male', 'nokri-rest-api');
        } else if ($gender_val == "female") {
            $gender_val = esc_html__('Female', 'nokri-rest-api');
        } else {
            $gender_val = esc_html__('Other', 'nokri-rest-api');
        }
        $info[] = nokriAPI_canidate_fields(nokri_feilds_label('cand_gend', esc_html__('Gender', 'nokri')), $gender_val, $gender_hldr, true, '', 'cand_gender');

        $type_hldr = __("Type", "nokri-rest-api");
        $info[] = nokriAPI_canidate_fields(__("Type ", "nokri-rest-api"), $cand_type_name, $type_hldr, true, '', 'cand_type');

        $level_hldr = __("Level", "nokri-rest-api");
        $info[] = nokriAPI_canidate_fields(__("Level ", "nokri-rest-api"), $cand_level_name, $level_hldr, true, '', 'cand_level');

        $experience_hldr = __("Experience", "nokri-rest-api");
        $info[] = nokriAPI_canidate_fields(__("Experience ", "nokri-rest-api"), $cand_experience_name, $experience_hldr, true, '', 'cand_experience');

        $salary_hldr = __("Salary", "nokri-rest-api");
        $info[] = nokriAPI_canidate_fields(__("Salary", "nokri-rest-api"), $salry_full, $salary_hldr, true, '', 'cand_salary');



        $prof_hldr = __("e.g Web developer", "nokri-rest-api");
        $info[] = nokriAPI_canidate_fields(__("Profession", "nokri-rest-api"), get_user_meta($user->ID, '_user_headline', true), $prof_hldr, true, '', 'cand_hand');



        $profile_hldr = __("Set your profile", "nokri-rest-api");
        $info[] = nokriAPI_canidate_fields(__("Set your profile", "nokri-rest-api"), get_user_meta($user->ID, '_user_profile_status', true), $profile_hldr, $is_show_profile_option, '', 'set_profile');


        $info[] = nokriAPI_canidate_fields(__("Address", "nokri-rest-api"), get_user_meta($user->ID, '_cand_address', true), 'textfield', true, '', 'cand_adress');
        $info[] = nokriAPI_canidate_fields(__("About Me", "nokri-rest-api"), get_user_meta($user->ID, '_cand_intro', true), 'textfield', true, '', 'about_me');
        $info[] = nokriAPI_canidate_fields(__("Location & Map", "nokri-rest-api"), '', 'textfield', true, '', 'loc');
        $info[] = nokriAPI_canidate_fields(__("Longitude", "nokri-rest-api"), $cand_map_long, 'textfield', true, '', 'cand_long');
        $info[] = nokriAPI_canidate_fields(__("Latitude", "nokri-rest-api"), $cand_map_lat, 'textfield', true, '', 'cand_lat');
        $info[] = nokriAPI_canidate_fields(__("Save Resume", "nokri-rest-api"), '', 'btn', true, '', 'saving_resumes');
        $info[] = nokriAPI_canidate_fields($resumes_link, '', 'btn', true, $cand_resume_down, 'download_resumes');
        $info[] = nokriAPI_canidate_fields($resume_name, '', 'btn', true, '', 'resume_name');


        $data['info'] = $info;
        $data['profile_img'] = nokriAPI_candidate_dp($user->ID);
        $data['skills'] = nokriAPI_canidate_skills_tags($user->ID);
        $data['social'] = nokriAPI_canidate_social_icons($user->ID);
        $data['extra'][] = nokriAPI_canidate_fields(__("About Me", "nokri-rest-api"), __("You have not written about yourself", "nokri-rest-api"), 'textfield', true, '', 'cand_about');
        $data['extra'][] = nokriAPI_canidate_fields(__("Skills", "nokri-rest-api"), __("You have not selected any skills", "nokri-rest-api"), 'textfield', true, '', 'cand_skills');
        $data['cand_scoring'] = nokriAPI_canidate_fields(__("Profile Percent", "nokri-rest-api"), $profile_percent, 'textfield', true, 2, 'cand_scrore');
        $del_acount = false;
        if ((isset($nokriAPI['deactivate_app_acount'])) && $nokriAPI['deactivate_app_acount'] == '1') {
            $del_acount = true;
        }
        $data['extra'][] = nokriAPI_canidate_fields(__("Delete account", "nokri-rest-api"), __("Delete account", "nokri-rest-api"), 'textfield', $del_acount, '', 'del_acount');
        $data['extra'][] = nokriAPI_canidate_fields('', $map_switch, 'textfield', true, '', 'map_switch');
        $percentage_switch = isset($nokri['cand_per_switch']) ? $nokri['cand_per_switch'] : "0";

        if ($percentage_switch == "0") {
            $percentage_switch = false;
        } else {
            $percentage_switch = true;
        }


        $is_video_uppload = isset($nokri['cand_video_resume_switch']) ? $nokri['cand_video_resume_switch'] : "false";

        $cand_into_video = get_user_meta($user_id, '_cand_intro_vid', true);

        if ($is_video_uppload) {
            $video_id = get_user_meta($user_id, 'cand_video_resumes', true);
            $cand_into_video = $video_id != "" ? wp_get_attachment_url($video_id) : "";
        }



        $data['extra'][] = nokriAPI_canidate_fields('', $percentage_switch, 'textfield', true, '', 'percentage_switch');


        $data['extra'][] = nokriAPI_canidate_fields(esc_html__('Resume Video', 'nokri'), $is_video_uppload, 'textfield', true, '', 'is_video_upload');

        $data['extra'][] = nokriAPI_canidate_fields('', $cand_into_video, 'textfield', true, '', 'cand_intro_video');



        $message = __("Edit", "nokri-rest-api");
        $scheduled_hours = nokriAPI_canidate_schedule_hours_fun(array(), true, $user_id);
        $data['scheduled_hours'] = $scheduled_hours;
        $response = array('success' => true, 'data' => $data, 'message' => $message);

        return ($return_arr) ? $data : $response;
    }

}
/* * ********************************************** */
/*   Get Candidate Certifications And Add More   */
/* * ********************************************** */

add_action('rest_api_init', 'nokriAPI_canidate_certifications_hook', 0);

function nokriAPI_canidate_certifications_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/certifications/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_canidate_certifications_get',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );

    register_rest_route(
            'nokri/v1', '/canidate/certifications/add_more/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_canidate_certifications_get_more',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_certifications_get_more')) {

    function nokriAPI_canidate_certifications_get_more() {
        $default = array();
        $default[] = array
            (
            'certification_name' => __("Certifications Name Here", "nokri-rest-api"),
            'certification_duration' => '',
            'certification_start' => '',
            'certification_end' => '',
            'certification_institute' => '',
            'certification_desc' => '',
        );


        $data = $certification = array();
        $title = '';
        $val = '';
        $type = 'textfield';
        $req = true;
        foreach ($default as $key => $cand_education) {
            $title = __("Certification Title", "nokri-rest-api");
            $type = 'textfield';
            $req = true;
            $certification[$key][] = nokriAPI_canidate_fields($title, '', $type, $req, 2, 'certification_name');


            $title = __("Certification Duration", "nokri-rest-api");
            $type = 'datetime';
            $req = true;
            $certification[$key][] = nokriAPI_canidate_fields($title, '', $type, $req, 2, 'certification_duration');



            $title = __("Certification Start", "nokri-rest-api");
            $type = 'datetime';
            $req = true;
            $certification[$key][] = nokriAPI_canidate_fields($title, '', $type, $req, 2, 'certification_start');


            $title = __("Certification End", "nokri-rest-api");
            $type = 'textfield';
            $req = true;
            $certification[$key][] = nokriAPI_canidate_fields($title, '', $type, $req, 1, 'certification_end');

            $title = __("Certification Institute", "nokri-rest-api");
            $type = 'textfield';
            $req = true;
            $certification[$key][] = nokriAPI_canidate_fields($title, '', $type, $req, 1, 'certification_institute');

            $title = __("Certification Description", "nokri-rest-api");
            $type = 'textfield';
            $req = true;
            $certification[$key][] = nokriAPI_canidate_fields($title, '', $type, $req, 1, 'certification_desc');
        }

        $data['certification'] = $certification;

        $response = array('success' => true, 'data' => $data, 'message' => '');


        return $response;
    }

}


if (!function_exists('nokriAPI_canidate_certifications_get')) {

    function nokriAPI_canidate_certifications_get($user_id = '', $return_arr = false) {

        if (is_numeric($user_id)) {
            $user = get_userdata($user_id);
            $user_id = $user->ID;
        } else {
            $user_id = get_current_user_id();
            $user = get_userdata($user_id);
        }


        $cand_certifications = get_user_meta($user_id, '_cand_certifications', true);

        $default = array();
        $default[] = array
            (
            'certification_name' => '',
            'certification_duration' => '',
            'certification_start' => '',
            'certification_end' => '',
            'certification_institute' => '',
            'certification_desc' => '',
        );

        $cand_certifications = (!empty($cand_certifications)) ? $cand_certifications : $default;

        $data = $cand_certification = array();

        if (isset($cand_certifications)) {
            $title = '';
            $val = '';
            $type = 'textfield';
            $req = true;
            foreach ($cand_certifications as $key => $certification) {

                $title = nokri_feilds_label('cand_certi_label', esc_html__('Certification Title', 'nokri'));
                $val = isset($certification['certification_name']) ? $certification['certification_name'] : '';
                $type = 'textfield';
                $req = true;
                $cand_certification[$key][] = nokriAPI_canidate_fields($title, $val, $type, $req, 2, 'certification_name');

                $title = nokri_feilds_label('cand_certi_dur_label', esc_html__('Certification Duration', 'nokri'));
                $val = isset($certification['certification_duration']) ? $certification['certification_duration'] : '';
                $type = 'textfield';
                $req = true;
                $cand_certification[$key][] = nokriAPI_canidate_fields($title, $val, $type, $req, 2, 'certification_duration');



                $title = nokri_feilds_label('cand_certi_start_label', esc_html__('Certification Start Date', 'nokri'));
                $val = isset($certification['certification_start']) ? $certification['certification_start'] : '';
                $type = 'datetime';
                $req = true;
                $cand_certification[$key][] = nokriAPI_canidate_fields($title, $val, $type, $req, 2, 'certification_start');

                $title = nokri_feilds_label('cand_certi_end_label', esc_html__('Certification End Date', 'nokri'));
                $val = isset($certification['certification_end']) ? $certification['certification_end'] : '';
                $type = 'datetime';
                $req = true;
                $cand_certification[$key][] = nokriAPI_canidate_fields($title, $val, $type, $req, 2, 'certification_end');

                $title = nokri_feilds_label('cand_certi_inst_label', esc_html__('Certification Institute', 'nokri'));
                $val = isset($certification['certification_institute']) ? $certification['certification_institute'] : '';
                $type = 'textfield';
                $req = true;
                $cand_certification[$key][] = nokriAPI_canidate_fields($title, $val, $type, $req, 1, 'certification_institute');

                $title = nokri_feilds_label('cand_quali_certi_label', esc_html__('Description', 'nokri'));
                $val = isset($certification['certification_desc']) ? $certification['certification_desc'] : '';
                $type = 'textarea';
                $req = true;
                $cand_certification[$key][] = nokriAPI_canidate_fields($title, $val, $type, $req, 1, 'certification_desc');
            }
        }


        $data['certification'] = $cand_certification;

        $data['extras'][] = nokriAPI_canidate_fields('Section name', nokri_feilds_label('certification_section_label', esc_html__('Certification Details', 'nokri')), '', '', 1, 'section_name');
        $data['extras'][] = nokriAPI_canidate_fields('Button name', nokri_feilds_label('cand_certi_btn', esc_html__('Save Certifications', 'nokri')), '', '', 1, 'btn_name');
        $data['extras'][] = nokriAPI_canidate_fields('Not Added', __("You Have Not Added Any Certifications Details Yet", "nokri-rest-api"), '', '', 1, 'not_added');


        $response = array('success' => true, 'data' => $data, 'message' => '');


        return ($return_arr) ? $data : $response;
    }

}



/* * ********************************** */
/* Get Candidate And Update Educations */
/* * ********************************** */



add_action('rest_api_init', 'nokriAPI_canidate_education_hook', 0);

function nokriAPI_canidate_education_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/education/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_canidate_education_get',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
    register_rest_route(
            'nokri/v1', '/canidate/education/add_more/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_canidate_education_get_more',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_education_get_more')) {

    function nokriAPI_canidate_education_get_more() {
        $default = array();
        $default[] = array
            (
            'degree_name' => __("Degreee Name Here", "nokri-rest-api"),
            'degree_institute' => '',
            'degree_start' => '',
            'degree_end' => '',
            'degree_percent' => '',
            'degree_grade' => '',
            'degree_detail' => '',
        );

        $data = $education = array();
        $title = '';
        $val = '';
        $type = 'textfield';
        $req = true;
        foreach ($default as $key => $cand_education) {
            $title = __("Qualification Title", "nokri-rest-api");
            $phldr = __("Enter title", "nokri-rest-api");
            $type = 'textfield';
            $req = true;
            $education[$key][] = nokriAPI_canidate_fields($title, $phldr, $type, $req, 2, 'degree_name');


            $title = __("Start Date", "nokri-rest-api");
            $phldr = __("Select start date", "nokri-rest-api");
            $type = 'datetime';
            $req = true;
            $education[$key][] = nokriAPI_canidate_fields($title, $phldr, $type, $req, 2, 'degree_start');



            $title = __("End Date", "nokri-rest-api");
            $phldr = __("Select end date", "nokri-rest-api");
            $type = 'datetime';
            $req = true;
            $education[$key][] = nokriAPI_canidate_fields($title, $phldr, $type, $req, 2, 'degree_end');


            $title = __("Institute Name", "nokri-rest-api");
            $phldr = __("Enter institute", "nokri-rest-api");
            $type = 'textfield';
            $req = true;
            $education[$key][] = nokriAPI_canidate_fields($title, $phldr, $type, $req, 1, 'degree_institute');

            $title = __("Percent", "nokri-rest-api");
            $phldr = __("e.g 80.0 with out %", "nokri-rest-api");
            $type = 'textfield';
            $req = true;
            $education[$key][] = nokriAPI_canidate_fields($title, $phldr, $type, $req, 1, 'degree_percent');

            $title = __("Grades", "nokri-rest-api");
            $phldr = __("only letters e.g A,B+ etc", "nokri-rest-api");
            $type = 'textfield';
            $req = true;
            $education[$key][] = nokriAPI_canidate_fields($title, $phldr, $type, $req, 1, 'degree_grade');


            $title = __("Details", "nokri-rest-api");
            $phldr = __("Enter details", "nokri-rest-api");
            $type = 'textarea';
            $req = true;
            $education[$key][] = nokriAPI_canidate_fields($title, '', $type, $req, 1, 'degree_detail');
        }

        $data['education'] = $education;

        $response = array('success' => true, 'data' => $data, 'message' => '');


        return $response;
    }

}

if (!function_exists('nokriAPI_canidate_education_get')) {

    function nokriAPI_canidate_education_get($user_id = '', $return_arr = false) {

        if (is_numeric($user_id)) {

            $user = get_userdata($user_id);
            $user_id = $user->ID;
        } else {
            $user_id = get_current_user_id();
            $user = get_userdata($user_id);
        }

        $cand_educations = get_user_meta($user_id, '_cand_education', true);

        $default = array();
        $default[] = array
            (
            'degree_name' => '',
            'degree_institute' => '',
            'degree_start' => '',
            'degree_end' => '',
            'degree_percent' => '',
            'degree_grade' => '',
            'degree_detail' => '',
        );

        $can_educations = (!empty($cand_educations)) ? $cand_educations : $default;

        $data = $education = array();
        if (isset($can_educations)) {

            $title = '';
            $val = '';
            $type = 'textfield';
            $req = true;
            foreach ($can_educations as $key => $cand_education) {
                $title = nokri_feilds_label('cand_quali_label', esc_html__('Qualification Title', 'nokri'));
                $phldr = nokri_feilds_label('cand_quali_plc', esc_html__('Degree Title', 'nokri'));
                $val = isset($cand_education['degree_name']) ? $cand_education['degree_name'] : '';
                $type = 'textfield';
                $req = true;
                $education[$key][] = nokriAPI_canidate_fields($title, $val, $type, $req, $phldr, 'degree_name');


                $title = nokri_feilds_label('cand_quali_start_label', esc_html__('Start Date', 'nokri'));
                $phldr = __("Select start date", "nokri-rest-api");
                $val = isset($cand_education['degree_start']) ? $cand_education['degree_start'] : '';
                $type = 'datetime';
                $req = true;
                $education[$key][] = nokriAPI_canidate_fields($title, $val, $type, $req, $phldr, 'degree_start');



                $title = nokri_feilds_label('cand_quali_end_label', esc_html__('End Date', 'nokri'));
                $phldr = __("Select end date", "nokri-rest-api");
                $val = isset($cand_education['degree_end']) ? $cand_education['degree_end'] : '';
                $type = 'datetime';
                $req = true;
                $education[$key][] = nokriAPI_canidate_fields($title, $val, $type, $req, $phldr, 'degree_end');


                $title = nokri_feilds_label('cand_inst_label', esc_html__('Institute Name', 'nokri'));
                $phldr = nokri_feilds_label('cand_inst_plc', esc_html__('Institute Name', 'nokri'));
                $val = isset($cand_education['degree_institute']) ? $cand_education['degree_institute'] : '';
                $type = 'textfield';
                $req = true;
                $education[$key][] = nokriAPI_canidate_fields($title, $val, $type, $req, $phldr, 'degree_institute');

                $title = nokri_feilds_label('cand_quali_percent_label', esc_html__('Percentage', 'nokri'));
                $phldr = __("e.g 80.0 with out %", "nokri-rest-api");
                $val = isset($cand_education['degree_percent']) ? $cand_education['degree_percent'] : '';
                $type = 'textfield';
                $req = true;
                $education[$key][] = nokriAPI_canidate_fields($title, $val, $type, $req, $phldr, 'degree_percent');

                $title = nokri_feilds_label('cand_quali_grades_label', esc_html__('Grades', 'nokri'));
                $phldr = nokri_feilds_label('cand_quali_grades_plc', esc_html__('Only Grade Letter e.g A+,B,C', 'nokri'));
                $val = isset($cand_education['degree_grade']) ? $cand_education['degree_grade'] : '';
                $type = 'textfield';
                $req = true;
                $education[$key][] = nokriAPI_canidate_fields($title, $val, $type, $req, $phldr, 'degree_grade');


                $title = nokri_feilds_label('cand_quali_desc_label', esc_html__('Description', 'nokri'));
                $phldr = __("Enter details", "nokri-rest-api");
                $val = isset($cand_education['degree_detail']) ? $cand_education['degree_detail'] : '';
                $type = 'textarea';
                $req = true;
                $education[$key][] = nokriAPI_canidate_fields($title, $val, $type, $req, $phldr, 'degree_detail');
            }
        }

        $data['education'] = $education;

        $data['extras'][] = nokriAPI_canidate_fields('Section name', nokri_feilds_label('education_section_label', esc_html__('Educational Details', 'nokri')), '', '', 1, 'section_name');
        $data['extras'][] = nokriAPI_canidate_fields('Button name', nokri_feilds_label('cand_quali_btn', esc_html__('Save Education', 'nokri')), '', '', 1, 'btn_name');
        $data['extras'][] = nokriAPI_canidate_fields('Not Added', __("You have not added any education yet", "nokri-rest-api"), '', '', 1, 'not_added');


        $response = array('success' => true, 'data' => $data, 'page_title' => '');


        return ($return_arr) ? $data : $response;
    }

}


/* * ****************************************** */
/* Candidate Updating   Education           */
/* * ***************************************** */




add_action('rest_api_init', 'nokriAPI_candidate_update_education_hooks', 0);

function nokriAPI_candidate_update_education_hooks() {
    register_rest_route('nokri/v1', '/candidate/update_education/',
            array(
                'methods' => WP_REST_Server::EDITABLE,
                'callback' => 'nokriAPI_candidate_update_education',
                'permission_callback' => function () {
                    return nokriAPI_basic_auth();
                },
            )
    );
}

if (!function_exists('nokriAPI_candidate_update_education')) {

    function nokriAPI_candidate_update_education($request) {

        $user_id = get_current_user_id();
        $user = get_userdata($user_id);

        $json_data = $request->get_json_params();

        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }

        $arrp = $education = array();
        $proData = nokriAPI_convert_to_array($json_data);


        $countNum = ( $proData['count'] == 0 ) ? 0 : $proData['count'] - 1;


        for ($i = 0; $i <= $countNum; $i++) {
            $arr = $proData['arr'];

            $arrp['degree_name'] = $arr[$i]['degree_name'];
            $arrp['degree_institute'] = $arr[$i]['degree_institute'];
            $arrp['degree_start'] = $arr[$i]['degree_start'];
            $arrp['degree_end'] = $arr[$i]['degree_end'];
            $arrp['degree_percent'] = $arr[$i]['degree_percent'];
            $arrp['degree_grade'] = $arr[$i]['degree_grade'];
            $arrp['degree_detail'] = $arr[$i]['degree_detail'];
            $education[] = $arrp;
        }

        $cand_education = ($arr);


        $chek_education = get_user_meta($user_id, '_cand_education', true);



        if ($education) {
            update_user_meta($user_id, '_cand_education', $cand_education);
        }



        $response = array('success' => true, 'data' => '', 'message' => __("Education updated", "nokri-rest-api"), 'page_title' => '');
        return $response;
    }

}

/* * ********************************** */
/*   Get Candidate Professions       */
/* * ********************************** */



add_action('rest_api_init', 'nokriAPI_canidate_professions_hook', 0);

function nokriAPI_canidate_professions_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/profession/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_canidate_professions_get',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
    register_rest_route(
            'nokri/v1', '/canidate/profession/add_more/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_canidate_profession_get_more',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_profession_get_more')) {

    function nokriAPI_canidate_profession_get_more() {
        $default = array();
        $default[] = array
            (
            'project_organization' => __("Organization Name Here", "nokri-rest-api"),
            'project_start' => '',
            'project_end' => '',
            'project_role' => '',
            'project_name' => '',
            'project_desc' => '',
        );

        $data = $profession = array();
        $title = '';
        $val = '';
        $type = 'textfield';
        $req = true;

        foreach ($default as $key => $cand_profession) {

            $title = nokri_feilds_label('cand_org_label', esc_html__('Organization Name', 'nokri'));
            $phldr = nokri_feilds_label('cand_org_plc', esc_html__('Organization Name', 'nokri'));
            $type = 'textfield';
            $req = true;
            $profession[$key][] = nokriAPI_canidate_fields($title, $phldr, $type, $req, 1, 'project_organization');


            $title = nokri_feilds_label('cand_exper_role_label', esc_html__('Your Role', 'nokri'));
            $plch = nokri_feilds_label('cand_exper_role_plc', esc_html__('Software Engineer Etc', 'nokri'));
            $type = 'textfield';
            $req = true;
            $profession[$key][] = nokriAPI_canidate_fields($title, $plch, $type, $req, 1, 'project_role');



            $title = nokri_feilds_label('cand_exper_start_label', esc_html__('Job start Date', 'nokri'));
            $plch = __("Select job start date", "nokri-rest-api");
            $type = 'datetime';
            $req = true;
            $profession[$key][] = nokriAPI_canidate_fields($title, $plch, $type, $req, 2, 'project_start');


            $title = nokri_feilds_label('cand_exper_current_label', esc_html__('Are You Currently Working There?', 'nokri'));
            $type = 'textfield';
            $req = true;
            $profession[$key][] = nokriAPI_canidate_fields($title, '', $type, $req, 2, 'project_name');


            $title = nokri_feilds_label('cand_exper_end_label', esc_html__('Job End Date', 'nokri'));
            $plch = __("Select job start date", "nokri-rest-api");
            $type = 'datetime';
            $req = true;
            $profession[$key][] = nokriAPI_canidate_fields($title, $plch, $type, $req, 2, 'project_end');




            $title = nokri_feilds_label('cand_exper_desc_label', esc_html__('Description', 'nokri'));
            $plch = __("Select job end date", "nokri-rest-api");
            $type = 'textfield';
            $req = true;
            $profession[$key][] = nokriAPI_canidate_fields($title, $plch, $type, $req, 1, 'project_desc');
        }

        $data['profession'] = $profession;



        $response = array('success' => true, 'data' => $data, 'message' => '');


        return $response;
    }

}




if (!function_exists('nokriAPI_canidate_professions_get')) {

    function nokriAPI_canidate_professions_get($user_id = '', $return_arr = false) {
        if (is_numeric($user_id)) {
            $user = get_userdata($user_id);
            $user_id = $user->ID;
        } else {
            $user_id = get_current_user_id();
            $user = get_userdata($user_id);
        }

        $cand_professions = get_user_meta($user_id, '_cand_profession', true);


        $default = array();
        $default[] = array
            (
            'project_organization' => '',
            'project_start' => '',
            'project_end' => '',
            'project_role' => '',
            'project_name' => '',
            'project_desc' => '',
        );

        $cand_professions = (!empty($cand_professions) ) ? $cand_professions : $default;

        $data = $profession = array();

        $data = array();
        if (isset($cand_professions)) {
            $title = '';
            $val = '';
            $type = 'textfield';
            $req = true;
            foreach ($cand_professions as $key => $cand_profession) {
                $title = nokri_feilds_label('cand_org_label', esc_html__('Organization Name', 'nokri'));
                $plch = nokri_feilds_label('cand_org_plc', esc_html__('Organization Name', 'nokri'));
                $val = isset($cand_profession['project_organization']) ? $cand_profession['project_organization'] : '';
                $req = true;
                $profession[$key][] = nokriAPI_canidate_fields($title, $val, $plch, $req, 1, 'project_organization');

                $title = nokri_feilds_label('cand_exper_role_label', esc_html__('Your Role', 'nokri'));
                $plch = nokri_feilds_label('cand_exper_role_plc', esc_html__('Software Engineer Etc', 'nokri'));
                $val = isset($cand_profession['project_role']) ? $cand_profession['project_role'] : '';
                $type = 'datetime';
                $req = true;
                $profession[$key][] = nokriAPI_canidate_fields($title, $val, $plch, $req, 2, 'project_role');


                $title = nokri_feilds_label('cand_exper_current_label', esc_html__('Are You Currently Working There?', 'nokri'));
                $val = isset($cand_profession['project_name']) ? $cand_profession['project_name'] : '';
                $type = 'textfield';
                $req = true;
                $profession[$key][] = nokriAPI_canidate_fields($title, $val, $type, $req, 2, 'project_name');

                $title = nokri_feilds_label('cand_exper_start_label', esc_html__('Job start Date', 'nokri'));
                $plch = __("Select job start date", "nokri-rest-api");
                $val = isset($cand_profession['project_start']) ? $cand_profession['project_start'] : '';
                $type = 'textfield';
                $req = true;
                $profession[$key][] = nokriAPI_canidate_fields($title, $val, $plch, $req, 2, 'project_start');

                $title = nokri_feilds_label('cand_exper_end_label', esc_html__('Job End Date', 'nokri'));
                $plch = __("Select job end date", "nokri-rest-api");
                $val = isset($cand_profession['project_end']) ? $cand_profession['project_end'] : '';
                $type = 'datetime';
                $req = true;

                if (isset($cand_profession['project_name']) && $cand_profession['project_name'] == '1') {
                    $val = __("Currently working", "nokri-rest-api");
                }

                $profession[$key][] = nokriAPI_canidate_fields($title, $val, $plch, $req, 2, 'project_end');


                $title = nokri_feilds_label('cand_exper_desc_label', esc_html__('Description', 'nokri'));
                $val = isset($cand_profession['project_desc']) ? $cand_profession['project_desc'] : '';
                $type = 'textarea';
                $req = true;
                $profession[$key][] = nokriAPI_canidate_fields($title, $val, $type, $req, 1, 'project_desc');
            }
        }
        $data['profession'] = $profession;

        $data['extras'][] = nokriAPI_canidate_fields('Section name', __("Work Experience", "nokri-rest-api"), '', '', 1, 'section_name');
        $data['extras'][] = nokriAPI_canidate_fields('Button name', nokri_feilds_label('cand_exper_btn', esc_html__('Save experience', 'nokri')), '', '', 1, 'btn_name');
        $data['extras'][] = nokriAPI_canidate_fields('Not Added', __("You have not added any experience details yet", "nokri-rest-api"), '', '', 1, 'not_added');

        $response = array('success' => true, 'data' => $data, 'page_title' => '');

        return ($return_arr) ? $data : $response;
    }

}


/* * ********************************** */
/* Candidate Portfolio Upload          */
/* * ********************************** */

add_action('rest_api_init', 'nokriAPI_candidate_api_portfolio_upload_hook', 0);

function nokriAPI_candidate_api_portfolio_upload_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/portfolio_upload/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_candidate_api_portfolio_upload',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_candidate_api_portfolio_upload')) {

    function nokriAPI_candidate_api_portfolio_upload($request) {
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
                $user_portfolio = get_user_meta($user_id, '_cand_portfolio', true);
                if ($user_portfolio != "") {
                    $media = explode(',', $user_portfolio);
                    if (count($media) >= $nokriAPI['api_upload_portfolio_limit']) {
                        $message = esc_html__("You can not upload more than ", 'nokri') . " " . $nokriAPI['api_upload_portfolio_limit'] . " " . esc_html__("images ", 'nokri-rest-api');

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
                        $user_portfolio = get_user_meta($user_id, '_cand_portfolio', true);
                        if ($user_portfolio != "") {
                            $updated_portfolio = $user_portfolio . ',' . $attach_id;
                        } else {
                            $updated_portfolio = $attach_id;
                        }
                        update_user_meta($user_id, '_cand_portfolio', $updated_portfolio);
                    }
                }
            }
        }

        if (is_wp_error($attach_id)) {
            $message = __("Some thing went wrong", "nokri-rest-api");

            return $response = array('success' => false, 'data' => '', 'message' => $message);
        } else {

            $message = __("Portfolio uploaded successfully", "nokri-rest-api");

            return $response = array('success' => true, 'data' => '', 'message' => $message);
        }
    }

}

/* * ********************************** */
/*   Get Candidate Portfolio          */
/* * ********************************** */
add_action('rest_api_init', 'nokriAPI_canidate_portfolio_hook', 0);

function nokriAPI_canidate_portfolio_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/portfolio/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_canidate_portfolio_get',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_portfolio_get')) {

    function nokriAPI_canidate_portfolio_get($user_id = '', $return_arr = false) {
        if (is_numeric($user_id)) {
            $user = get_userdata($user_id);
            $user_id = $user->ID;
        } else {
            $user_id = get_current_user_id();
            $user = get_userdata($user_id);
        }

        /* Getting Candidate Portfolio */
        if (get_user_meta($user_id, '_cand_portfolio', true) != "") {
            $port = get_user_meta($user_id, '_cand_portfolio', true);
            $portfolios = explode(',', $port);
            foreach ($portfolios as $portfolio) {
                $portfolio_image = wp_get_attachment_image_src($portfolio, '');
                $data['img'][] = nokriAPI_canidate_fields('Img_url', $portfolio_image[0], $portfolio, '', 1, 'Img_url');
            }
        } else {
            $data['img'] = array();
        }
        $video = false;
        $cand_video_url = get_user_meta($user_id, '_cand_video', true);
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
        $data['extra'][] = nokriAPI_canidate_fields('Click text', __("Click To upload Your Portfolio ", "nokri-rest-api"), '', '', 1, 'click_text');
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
/*   Candidate Add video url          */
/* * ********************************** */

add_action('rest_api_init', 'nokriAPI_canidate_video_url_hook', 0);

function nokriAPI_canidate_video_url_hook() {
    register_rest_route(
            'nokri/v1', '/candidate/portfolio_video_url/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_canidate_portfolio_video_url',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );

    register_rest_route(
            'nokri/v1', '/candidate/portfolio_video_url/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_canidate_portfolio_add_video_url',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

/* Getting vdieo url */
if (!function_exists('nokriAPI_canidate_portfolio_video_url')) {

    function nokriAPI_canidate_portfolio_video_url($user_id = '', $return_arr = false) {
        if (is_numeric($user_id)) {
            $user = get_userdata($user_id);
            $user_id = $user->ID;
        } else {
            $user_id = get_current_user_id();
            $user = get_userdata($user_id);
        }

        /* Getting Candidate Portfolio */
        if (get_user_meta($user_id, '_cand_video', true) != "") {
            $data['video_url'][] = nokriAPI_canidate_fields('video_url', get_user_meta($user_id, '_cand_video', true), '', '', 1, 'video_url');
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
if (!function_exists('nokriAPI_canidate_portfolio_add_video_url')) {

    function nokriAPI_canidate_portfolio_add_video_url($request) {
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


            if ($valid || $video_url == "") {
                $message = __("Successfully added", "nokri-rest-api");

                update_user_meta($user_id, '_cand_video', $video_url);
                $success = true;
            } else {
                $message = __("Video url not valid", "nokri-rest-api");
                $success = false;
            }

            return $response = array('success' => $success, 'data' => '', 'message' => $message);
        }
    }

}

/* * ********************************** */
/*   Delete Candidate Portfolio      */
/* * ********************************** */



add_action('rest_api_init', 'nokriAPI_canidate_del_portfolio_hook', 0);

function nokriAPI_canidate_del_portfolio_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/del_portfolio/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_canidate_del_portfolio',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_del_portfolio')) {

    function nokriAPI_canidate_del_portfolio($request) {
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
        if (get_user_meta($user_id, '_cand_portfolio', true) != "") {
            $ids = get_user_meta($user_id, '_cand_portfolio', true);
            $res = str_replace($attachmentid, "", $ids);
            $res = str_replace(',,', ",", $res);
            $img_ids = trim($res, ',');
            update_user_meta($user_id, '_cand_portfolio', $img_ids);
        }


        wp_delete_attachment($attachmentid, true);

        $message = __("Delete Successfully", "nokri-rest-api");

        return $response = array('success' => true, 'data' => '', 'message' => $message);
    }

}





/* * ********************************** */
/*    Candidate Feilds Funtion    */
/* * ********************************** */

if (!function_exists('nokriAPI_canidate_fields')) {

    function nokriAPI_canidate_fields($key = '', $value = '', $fieldname = 'textfield', $is_required = false, $column = 1, $fieldTypename = '') {

        return array("key" => $key, "value" => $value, "fieldname" => $fieldname, "is_required" => $is_required, "is_show" => $column, "field_type_name" => $fieldTypename);
    }

}

/* * ********************************** */
/*  Social Icons Function            */
/* * ********************************** */


if (!function_exists('nokriAPI_canidate_social_icons')) {

    function nokriAPI_canidate_social_icons($user_id = '') {
        global $nokriAPI;
        $current_user_id = get_current_user_id();
        $is_show = (isset($nokriAPI['user_contact_social_API']) && $nokriAPI['user_contact_social_API']) ? true : true;
        if (!$is_show && $user_id != $current_user_id) {
            $is_show = false;
        }
        $data['is_show'] = $is_show;
        $data['facebook'] = get_user_meta($user_id, '_cand_fb', true);
        $data['twitter'] = get_user_meta($user_id, '_cand_twiter', true);
        $data['linkedin'] = get_user_meta($user_id, '_cand_linked', true);
        $data['google_plus'] = get_user_meta($user_id, '_cand_google', true);

        return $data;
    }

}


/* * ********************************** */
/* Getting Candidate Cover Photo Function */
/* * ********************************** */

if (!function_exists('nokriAPI_candidate_cover')) {

    function nokriAPI_candidate_cover($user_id = '') {

        /* Getting Candidate Cover Photo */
        $image_cover_link = get_template_directory_uri() . '/images/home-secreen.jpg';
        if (get_user_meta($user_id, '_cand_cover', true) != "") {
            $attach_cover_id = get_user_meta($user_id, '_cand_cover', true);
            $image_cover_link = wp_get_attachment_image_src($attach_cover_id, '');
            $image_cover_link = $image_cover_link[0];
        }

        $data['img'] = $image_cover_link;

        return $data;
    }

}

/* * ********************************** */
/* Change Candidate Cover Photo  */
/* * ********************************** */

add_action('rest_api_init', 'nokriAPI_profile_api_update_cover', 0);

function nokriAPI_profile_api_update_cover() {
    register_rest_route(
            'nokri/v1', 'candidate/cover/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_profile_update_cover',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_profile_update_cover')) {

    function nokriAPI_profile_update_cover($request) {

        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        require_once ABSPATH . 'wp-admin/includes/image.php';
        require_once ABSPATH . 'wp-admin/includes/file.php';
        require_once ABSPATH . 'wp-admin/includes/media.php';
        $attach_id = media_handle_upload('cover_img', 0);
        /*         * ***** Assign image to user *********** */
        update_user_meta($user_id, '_cand_cover', $attach_id);
        $image_link = wp_get_attachment_image_src($attach_id, 'nokri-user-profile');
        $idata['cover_img'] = $image_link[0];
        $response = array('success' => true, 'data' => $image_link, 'message' => __("Cover image updated successfully", "nokri-rest-api"));
        return $response;
    }

}






/* * ********************************** */
/* Getting Candidate Profile Picture Function */
/* * ********************************** */

if (!function_exists('nokriAPI_candidate_dp')) {

    function nokriAPI_candidate_dp($user_id = '') {

        /* Getting Candidate Dp */
        $image_dp_link = get_template_directory_uri() . '/images/candidate-dp.jpg';
        if (get_user_meta($user_id, '_cand_dp', true) != "") {
            $attach_dp_id = get_user_meta($user_id, '_cand_dp', true);
            $image_dp_link = wp_get_attachment_image_src($attach_dp_id, '');
            $image_dp_link = $image_dp_link[0];
        }

        $data['img'] = $image_dp_link;

        return $image_dp_link;
    }

}




/* * ********************************** */
/* Change Candidate Profile Picture  */
/* * ********************************** */

add_action('rest_api_init', 'nokriAPI_profile_api_update_img', 0);

function nokriAPI_profile_api_update_img() {
    register_rest_route(
            'nokri/v1', '/candidate/dp/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_profile_update_img',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_profile_update_img')) {

    function nokriAPI_profile_update_img($request) {

        if (!isset($_FILES['logo_img']))
            return array('success' => false, 'data' => '', 'message' => __("Something went wrong.", "nokri-rest-api"));


        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }

        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        require_once ABSPATH . 'wp-admin/includes/image.php';
        require_once ABSPATH . 'wp-admin/includes/file.php';
        require_once ABSPATH . 'wp-admin/includes/media.php';
        $attach_id = media_handle_upload('logo_img', 0);
        if ($attach_id) {
            /*             * ***** Assign image to user *********** */
            update_user_meta($user_id, '_cand_dp', $attach_id);


            $image_link = wp_get_attachment_image_src($attach_id, 'nokri-rest-api');


            $idata['logo_img'] = $image_link[0];

            $response = array('success' => true, 'data' => $idata, 'message' => __("Profile image updated successfully", "nokri-rest-api"));

            return $response;
        } else {
            $response = array('success' => false, 'data' => '', 'message' => __("Something went wrong.", "nokri-rest-api"));
            return $response;
        }
    }

}


/* * ********************************** */
/* Candidate Resume Upload          */
/* * ********************************** */

add_action('rest_api_init', 'nokriAPI_candidate_api_resume_upload_hook', 0);

function nokriAPI_candidate_api_resume_upload_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/resume_upload/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_candidate_api_resume_upload',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_candidate_api_resume_upload')) {

    function nokriAPI_candidate_api_resume_upload($request) {
        global $nokriAPI;
        $json_data = $request->get_json_params();
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        ;
        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }
        if (!isset($_FILES['my_cv_upload']))
            return array('success' => false, 'data' => '', 'message' => __("Something went wrong.", "nokri-rest-api"));

        //authenticate_check();
        require_once ABSPATH . 'wp-admin/includes/image.php';
        require_once ABSPATH . 'wp-admin/includes/file.php';
        require_once ABSPATH . 'wp-admin/includes/media.php';

        $size_arr = explode('-', $nokriAPI['api_upload_resume_size']);
        $display_size = $size_arr[1];
        $actual_size = $size_arr[0];

        /* Getting Extension  */
        $path = $_FILES['my_cv_upload']['name'];
        $imageFileType = pathinfo($path, PATHINFO_EXTENSION);



        /* Check File Format */
        if ($imageFileType != "doc" && $imageFileType != "docx" && $imageFileType != "pdf") {
            $message = esc_html__("Sorry, only Doc, Docx and Pdf are allowed.", 'nokri-rest-api');

            $response = array('success' => false, 'data' => '', 'message' => $message);

            return $response;
        }

        // Check file size */
        if ($_FILES['my_cv_upload']['size'] > $nokriAPI['api_upload_resume_size']) {
            $message = esc_html__("Max allowed resume size is", 'nokri-rest-api') . " " . $display_size;
            $response = array('success' => false, 'data' => '', 'message' => $message);
            return $response;
        }
        // Check max resume limit
        $user_resume = get_user_meta($user_id, '_cand_resume', true);
        if ($user_resume != "") {
            $media = explode(',', $user_resume);
            if (count($media) >= $nokriAPI['api_upload_resume_limit']) {
                $message = esc_html__("You can not upload more than ", 'nokri') . " " . $nokriAPI['api_upload_resume_limit'] . " " . esc_html__("resumes ", 'nokri-rest-api');

                $response = array('success' => false, 'data' => '', 'message' => $message);

                return $response;
            }
        }
        $attachment_id = media_handle_upload('my_cv_upload', 0);
        if (!is_wp_error($attachment_id)) {
            $user_resume = get_user_meta($user_id, '_cand_resume', true);
            if ($user_resume != "") {
                $updated_resume = $user_resume . ',' . $attachment_id;
            } else {
                $updated_resume = $attachment_id;
            }
            update_user_meta($user_id, '_cand_resume', $updated_resume);
            $message = esc_html__("Uploaded successfully", 'nokri-rest-api');
        } else {
            $error_string = $attachment_id->get_error_message();
            $message = $error_string;
            $response = array('success' => false, 'data' => '', 'message' => $message);
            return $response;
        }
        $response = array('success' => true, 'data' => '', 'message' => $message);

        return $response;
    }

}

/* * ********************************** */
/* Candidate Get Resumes Service */
/* * ********************************** */



add_action('rest_api_init', 'nokriAPI_canidate_resumes_hook', 0);

function nokriAPI_canidate_resumes_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/resumes_list/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_candidate_resumes',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_candidate_resumes')) {

    function nokriAPI_candidate_resumes($request) {

        global $nokri;
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        /* Getting All Resumes */
        $cand_resume = get_user_meta($user_id, '_cand_resume', true);
        $resumes = array();
        if ($cand_resume) {
            $cand_resumes = explode(',', $cand_resume);
            $count = 0;
            $data = array();
            foreach ($cand_resumes as $resume) {
                $resumes[$count][] = nokriAPI_canidate_fields(__("Resume ID", "nokri-rest-api"), $resume, 'textfield', true, '', 'resume_id');
                $resumes[$count][] = nokriAPI_canidate_fields(__("Resume Name", "nokri-rest-api"), basename(get_attached_file($resume)), 'textfield', true, '', 'resume_name');
                $resumes[$count][] = nokriAPI_canidate_fields(__("Resume URL", "nokri-rest-api"), get_permalink($resume), 'textfield', true, '', 'resume_url');
                $count++;
            }
        }
        $data['resumes'] = $resumes;
        $video = false;
        $cand_video_url = get_user_meta($user_id, '_cand_video', true);
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
        $data['extras'][] = nokriAPI_canidate_fields('Section name', __("Add Resumes", "nokri-rest-api"), '', '', 1, 'section_name');
        $data['extras'][] = nokriAPI_canidate_fields('Section text', __("Only allowed txt, doc , docx , and pdf", "nokri-rest-api"), '', '', 1, 'section_text');
        $data['extras'][] = nokriAPI_canidate_fields('Click text', __("Click To upload Your Resume", "nokri-rest-api"), '', '', 1, 'click_text');
        $data['extras'][] = nokriAPI_canidate_fields('Button name', __("Save Resume", "nokri-rest-api"), '', '', 1, 'btn_name');
        $data['extras'][] = nokriAPI_canidate_fields('Section label', __("Your Resume", "nokri-rest-api"), '', '', 1, 'section_label');
        $data['extras'][] = nokriAPI_canidate_fields('Ad More', __("Add More Resume", "nokri-rest-api"), '', '', 1, 'ad_more_btn');
        $data['extras'][] = nokriAPI_canidate_fields('Sr Txt', __("Sr #", "nokri-rest-api"), '', '', 1, 'sr_txt');
        $data['extras'][] = nokriAPI_canidate_fields('Resume Name', __("Name", "nokri-rest-api"), '', '', 1, 'resume_name');
        $data['extras'][] = nokriAPI_canidate_fields('Download', __("Download", "nokri-rest-api"), '', '', 1, 'dwnld_resume');
        $data['extras'][] = nokriAPI_canidate_fields('Download', __("Download ", "nokri-rest-api"), '', '', 1, 'dwnld');
        $data['extras'][] = nokriAPI_canidate_fields('Delete Resume', __("Delete", "nokri-rest-api"), '', '', 1, 'del_resume');
        $data['extras'][] = nokriAPI_canidate_fields('Not  Uploaded', __("You have not uploaded any resume", "nokri-rest-api"), '', '', 1, 'not_added');

        $data['extra'][] = nokriAPI_canidate_fields(__("Video url (only youtube)", "nokri-rest-api"), $cand_video_url, '', $video, 1, 'video_url');
        $data['extra'][] = nokriAPI_canidate_fields('No Video url', __("No video url", "nokri-rest-api"), '', '', 1, 'no_video_url');

        $is_video_uppload = isset($nokri['cand_video_resume_switch']) ? $nokri['cand_video_resume_switch'] : "false";
        $video_limit = isset($nokri['cand_video_resume_limit']) ? $nokri['cand_video_resume_limit'] : 2;

        $data['extra'][] = nokriAPI_canidate_fields(esc_html__('Upload Video', 'nokri-rest-api'), $is_video_uppload, '', '', 1, 'is_video_upload');
        $data['extra'][] = nokriAPI_canidate_fields(esc_html__('File size should be less than 30 Mb', 'nokri-rest-api'), $video_limit, '', '', 1, 'video_limit');

        $data['extra'][] = nokriAPI_canidate_fields('save button', __("Save video", "nokri-rest-api"), '', '', 1, 'video_save_btn');
        return $response = array('success' => true, 'data' => $data, 'message' => '');
    }

}




/* * ********************************** */
/* Candidate Dell Resumes Service */
/* * ********************************** */



add_action('rest_api_init', 'nokriAPI_canidate_resumes_del_hook', 0);

function nokriAPI_canidate_resumes_del_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/dell_resumes/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_candidate_resumes_del',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_candidate_resumes_del')) {

    function nokriAPI_candidate_resumes_del($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();

        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }

        $resume_id = (isset($json_data['resume_id'])) ? trim($json_data['resume_id']) : '';


        /* Getting All Resumes */
        if (get_user_meta($user_id, '_cand_resume', true) != "") {
            $ids = get_user_meta($user_id, '_cand_resume', true);
            $res = str_replace($resume_id, "", $ids);
            $res = str_replace(',,', ",", $res);
            $img_ids = trim($res, ',');
            update_user_meta($user_id, '_cand_resume', $img_ids);
        }

        wp_delete_attachment($resume_id, true);

        $message = __("Delete Successfully", "nokri-rest-api");

        return $response = array('success' => true, 'data' => '', 'message' => $message);
    }

}





/* * ********************************** */
/* Candidate Saved Jobs Service */
/* * ********************************** */

function nokriAPI_time_ago() {
    return human_time_diff(get_the_time('U'), current_time('timestamp')) . ' ' . esc_html__('ago', 'nokri-rest-api');
}

/* * ********************************** */
/* Candidate Get Saved Jobs Service */
/* * ********************************** */



add_action('rest_api_init', 'nokriAPI_canidate_saved_jobs_hook', 0);

function nokriAPI_canidate_saved_jobs_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/saved_jobs/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_candidate_saved_jobs',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );


    register_rest_route(
            'nokri/v1', '/canidate/saved_jobs/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_candidate_saved_jobs',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_candidate_saved_jobs')) {

    function nokriAPI_candidate_saved_jobs($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);


        $json_data = $request->get_json_params();
        $keyword = (isset($json_data['keyword'])) ? trim($json_data['keyword']) : '';


        if (get_query_var('paged')) {
            $paged = get_query_var('paged');
        } else if (isset($json_data['page_number'])) {
            // This will occur if on front page.
            $paged = $json_data['page_number'];
        } else {
            $paged = 1;
        }
        add_action('pre_get_posts', 'my_pre_get_posts');

        $jobs = array();
        /* Getting Saved Jobs */
        $args = array(
            'post_type' => 'job_post',
            'paged' => $paged,
            's' => $keyword,
            'orderby' => 'date',
            'order' => 'DESC',
            'meta_query' => array(array('key' => '_job_saved_value_' . $user_id,),),);
        $query = new WP_Query($args);

        $jobs = array();
        if ($query->have_posts()) {
            $count = 0;
            while ($query->have_posts()) {
                $query->the_post();
                $query->post_author;
                $job_id = get_the_id();
                /* Getting Job Author Informations */
                $post_author_id = get_post_field('post_author', $job_id);
                $company_name = get_the_author_meta('display_name', $post_author_id);
                $job_type = wp_get_post_terms($job_id, 'job_type', array("fields" => "ids"));
                $job_type = isset($job_type[0]) ? $job_type[0] : '';
                $job_salary = wp_get_post_terms($job_id, 'job_salary', array("fields" => "ids"));
                $job_salary = isset($job_salary[0]) ? $job_salary[0] : '';
                $job_salary_type = wp_get_post_terms($job_id, 'job_salary_type', array("fields" => "ids"));
                $job_salary_type = isset($job_salary_type[0]) ? $job_salary_type[0] : '';
                $job_currency = wp_get_post_terms($job_id, 'job_currency', array("fields" => "ids"));
                $job_currency = isset($job_currency[0]) ? $job_currency[0] : '';
                $job_location = get_post_meta($job_id, '_job_address', true);

                /* Getting Company  Profile Photo */
                if (get_user_meta($post_author_id, '_sb_user_pic', true) != "") {
                    $attach_id = get_user_meta($post_author_id, '_sb_user_pic', true);
                    $image_link = wp_get_attachment_image_src($attach_id, 'nokri_job_post_single');
                }

                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Id", "nokri-rest-api"), get_the_id(), 'textfield', true, 1, 'job_id');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Title", "nokri-rest-api"), nokriAPI_convert_uniText(get_the_title()), 'textfield', true, 1, 'job_title');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Company Id", "nokri-rest-api"), $post_author_id, 'textfield', true, 1, 'company_id');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Company Name", "nokri-rest-api"), $company_name, 'textfield', true, 1, 'company_name');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Salary", "nokri-rest-api"), nokri_job_post_single_taxonomies('job_currency', $job_currency) . " " . nokri_job_post_single_taxonomies('job_salary', $job_salary) . "/" . nokri_job_post_single_taxonomies('job_salary_type', $job_salary_type), 'textfield', true, 1, 'job_salary');

                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Currency", "nokri-rest-api"), nokri_job_post_single_taxonomies('job_currency', $job_currency), 'textfield', true, 1, 'job_currency');


                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Type", "nokri-rest-api"), nokri_job_post_single_taxonomies('job_type', $job_type), 'textfield', true, 1, 'job_type');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Company Logo", "nokri-rest-api"), $image_link[0], 'textfield', true, 1, 'company_logo');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Posted", "nokri-rest-api"), nokriAPI_time_ago(), 'textfield', true, 1, 'job_posted');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Location", "nokri-rest-api"), $job_location, 'textfield', true, 1, 'job_location');

                $count++;
            }
            wp_reset_postdata();
        }



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








        $data['page_title'] = __("Saved Jobs", "nokri-rest-api");
        $data['button_title'] = __("Delete Job", "nokri-rest-api");
        $data['jobs'] = $jobs;
        $message = __("You have not saved any job yet", "nokri-rest-api");
        return $response = array('success' => true, 'data' => $data, 'message' => $message, "pagination" => $pagination);
    }

}








/* * ********************************** */
/* Candidate Dell Saved Jobs Service */
/* * ********************************** */



add_action('rest_api_init', 'nokriAPI_canidate_del_saved_job_hook', 0);

function nokriAPI_canidate_del_saved_job_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/dell_saved_job/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_canidate_del_saved_job',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_del_saved_job')) {

    function nokriAPI_canidate_del_saved_job($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();

        $job_id = (isset($json_data['job_id'])) ? trim($json_data['job_id']) : '';

        $applied_job_key_val = $user_id . '|' . $job_id;
        if ($job_id != "") {
            delete_post_meta($job_id, '_job_saved_value_' . $user_id, $applied_job_key_val);
        }

        $message = __("Delete Successfully", "nokri-rest-api");

        return $response = array('success' => true, 'data' => '', 'message' => $message);
    }

}


/* * ********************************** */
/* Candidate Saving Jobs Service */
/* * ********************************** */



add_action('rest_api_init', 'nokriAPI_canidate_saving_jobs_hook', 0);

function nokriAPI_canidate_saving_jobs_hook() {
    register_rest_route(
            'nokri/v1', '/candidate/saving_jobs/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_candidate_saving_jobs',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_candidate_saving_jobs')) {

    function nokriAPI_candidate_saving_jobs($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();

        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }

        $job_id = (isset($json_data['job_id'])) ? trim($json_data['job_id']) : '';

        $job_bookmark = get_post_meta($job_id, '_job_saved_value_' . $user_id, true);


        $applied_job_key_val = $user_id . '|' . $job_id;

        if ($job_bookmark == "") {
            update_post_meta($job_id, '_job_saved_value_' . $user_id, $applied_job_key_val);
            $message = __("Saved Successfully", "nokri-rest-api");
        } else {
            $message = __("Already  Saved", "nokri-rest-api");
        }


        return $response = array('success' => true, 'data' => '', 'message' => $message);
    }

}




/* * ********************************** */
/* Candidate Apllying Jobs Pop Up Service  */
/* * ********************************** */
add_action('rest_api_init', 'nokriAPI_canidate_applying_jobs_hook', 0);

function nokriAPI_canidate_applying_jobs_hook() {
    register_rest_route(
            'nokri/v1', '/candidate/applying_jobs/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_applying_jobs',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_applying_jobs')) {

    function nokriAPI_applying_jobs($request) {
        global $nokri;
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();
        $job_id = (isset($json_data['job_id'])) ? trim($json_data['job_id']) : '';


        $job_emp_qualifications = get_post_meta($job_id, '_job_qualifications', true);
        $job_cand_qualifications = get_user_meta($user_id, '_cand_last_edu', true);
        $job_questions = get_post_meta($job_id, '_job_questions', true);
        $allow = (isset($nokri['allow_questinares']) && $nokri['allow_questinares'] != "") ? $nokri['allow_questinares'] : false;
        $question_array = array();
        //question array
        if (isset($job_questions) && !empty($job_questions) && $allow) {
            $question_array = $job_questions;
        }
        /* Getting Candidate Resume */
        $resume_options = array();
        if (get_user_meta($user_id, '_cand_resume', true) != "") {
            $resume = get_user_meta($user_id, '_cand_resume', true);
            $resumes = explode(',', $resume);

            $resume_options[] = nokriAPI_canidate_fields('', esc_html__('Select Resume', 'nokri'), '', true);
            foreach ($resumes as $resum) {
                $resume_options[] = nokriAPI_canidate_fields($resum, basename(get_attached_file($resum)), '', true);
            }
        }

        update_user_meta($user_id, '_job_answers' . $user_id, ($answers_sanatize));
        $info[] = nokriAPI_canidate_fields(__("Want to apply for job", "nokri-rest-api"), '', 'textfield', true, '', 'job_apply');
        $info[] = nokriAPI_canidate_fields(__("Select your resume ", "nokri-rest-api"), '', 'textfield', true, '', 'job_resume');
        $info[] = nokriAPI_canidate_fields(__("Write cover letter", "nokri-rest-api"), '', 'textfield', true, '', 'job_cvr');
        $info[] = nokriAPI_canidate_fields(__("Apply now", "nokri-rest-api"), '', 'textfield', true, '', 'job_btn');
        $data['info'] = $info;
        $data['job_questions'] = $question_array;
        $data['resumes'] = $resume_options;

        return $response = array('success' => true, 'data' => $data, 'message' => '');
    }

}

/* * ********************************** */
/* Candidate Apllying Jobs  Service  */
/* * ********************************** */
add_action('rest_api_init', 'nokriAPI_canidate_sending_resume_hook', 0);

function nokriAPI_canidate_sending_resume_hook() {
    register_rest_route(
            'nokri/v1', '/candidate/sending_resume/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_canidate_sending_resume',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_sending_resume')) {

    function nokriAPI_canidate_sending_resume($request) {
        global $nokri;
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $user_type = get_user_meta($user_id, '_sb_reg_type', true);
        $json_data = $request->get_json_params();
        $job_id = (isset($json_data['job_id'])) ? trim($json_data['job_id']) : '';
        $cand_apply_resume = (isset($json_data['cand_apply_resume'])) ? trim($json_data['cand_apply_resume']) : '';
        $cand_cover_letter = (isset($json_data['cand_cover_letter'])) ? trim($json_data['cand_cover_letter']) : '';
        $cand_date = (isset($json_data['cand_date'])) ? trim($json_data['cand_date']) : '';
        $email = isset($json_data['cand_email']) ? $json_data['cand_email'] : '';
        $user_name = isset($json_data['cand_name']) ? $json_data['cand_name'] : '';
        $job_answers = isset($json_data['questions_ans']) ? $json_data['questions_ans'] : array();
        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }
        $exists = email_exists($email);
        if ($exists) {
            $message = __("Sorry email already exists", "nokri-rest-api");
            return $response = array('success' => true, 'data' => '', 'message' => $message);
        }
        $is_apply_pkg_base = ( isset($nokri['job_apply_package_base']) && $nokri['job_apply_package_base'] != "" ) ? $nokri['job_apply_package_base'] : false;
        if ($is_apply_pkg_base == '1' && $user_type != '1') {
            $is_package = nokri_candidate_package_expire_notify();
            if ($is_package == 'ae' || $is_package == 'pe' || $is_package == 'np') {
                $message = esc_html__("Please Purchase Package", "nokri-rest-api");
                return $response = array('success' => false, 'data' => '', 'message' => $message);
            }
        }


        if ($user_id == '') {
            $password = nokri_randomString(10);
            $user_id = nokri_do_register_without_login($email, $user_name, $password);
            update_user_meta($user_id, '_sb_reg_type', '0');
            echo nokri_apply_without_login_password($user_id, $password, $job_id);
        }
        $applied_job_key_val = $user_id . '|' . $cand_apply_resume;
        if ($cand_apply_resume != "") {
            update_post_meta($job_id, '_job_applied_resume_' . $user_id, $applied_job_key_val);
        }
        if ($cand_cover_letter != "") {
            update_post_meta($job_id, '_job_applied_cover_' . $user_id, $cand_cover_letter);
        }
        update_post_meta($job_id, '_job_applied_status_' . $user_id, 0);
        update_post_meta($job_id, '_job_applied_date_' . $user_id, $cand_date);

        $answers_sanatize = array();
        if (isset($job_answers) && !empty($job_answers)) {
            foreach ($job_answers as $key) {
                $answers_sanatize[] = sanitize_text_field($key);
            }
        }
        if ($is_apply_pkg_base == '1') {
            $job_applied_rem = get_user_meta($user_id, '_candidate_applied_jobs', true);
            if ($job_applied_rem != '0' && $job_applied_rem != '-1') {
                update_user_meta($user_id, '_candidate_applied_jobs', $job_applied_rem - 1);
            }
        }
        $message = __("Applied successfully", "nokri-rest-api");
        $emp_id = get_post_field('post_author', $job_id);
        $request_from = nokriAPI_getSpecific_headerVal('Nokri-Request-From');
        $emp = '';
        $test = adforestAPI_messages_sent_func($request_from, $emp_id, $user_id, $job_id, $cand_date);
        return $response = array('success' => true, 'data' => '', 'message' => $message, 'notify' => $test);
    }

}
/* * ********************************** */
/* Candidate Apllying Jobs  Service  */
/* * ********************************** */
add_action('rest_api_init', 'nokriAPI_canidate_sending_email_resume_hook', 0);

function nokriAPI_canidate_sending_email_resume_hook() {
    register_rest_route(
            'nokri/v1', '/candidate/sending_email_resume/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_canidate_sending_email_resume',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_sending_email_resume')) {

    function nokriAPI_canidate_sending_email_resume($request) {
        global $nokri;
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();
        $job_id = (isset($json_data['job_id'])) ? trim($json_data['job_id']) : '';
        $cand_apply_resume = (isset($json_data['cand_apply_resume'])) ? trim($json_data['cand_apply_resume']) : '';
        $cand_cover_letter = (isset($json_data['cand_cover_letter'])) ? trim($json_data['cand_cover_letter']) : '';
        $cand_date = (isset($json_data['cand_date'])) ? trim($json_data['cand_date']) : '';
        $email = isset($json_data['cand_email']) ? $json_data['cand_email'] : '';
        $user_name = isset($json_data['cand_name']) ? $json_data['cand_name'] : '';

        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }

        $exists = email_exists($email);
        if ($exists) {
            $message = __("Sorry email already exists", "nokri-rest-api");
            return $response = array('success' => true, 'data' => '', 'message' => $message);
        }
        $is_apply_pkg_base = ( isset($nokri['job_apply_package_base']) && $nokri['job_apply_package_base'] != "" ) ? $nokri['job_apply_package_base'] : false;
        if ($user_id == '') {
            $password = nokri_randomString(10);
            $user_id = nokri_do_register_without_login($email, $user_name, $password);
            update_user_meta($user_id, '_sb_reg_type', '0');
            echo nokri_apply_without_login_password($user_id, $password, $job_id);
        }

        nokri_new_candidate_email_apply($job_id, $user_id, $cand_apply_resume, $cand_cover_letter);
        $message = __("Applied successfully", "nokri-rest-api");

        $emp_id = get_post_field('post_author', $job_id);
        $request_from = nokriAPI_getSpecific_headerVal('Nokri-Request-From');
        $emp = '';
        $test = adforestAPI_messages_sent_func($request_from, $emp_id, $user_id, $job_id, $cand_date);
        return $response = array('success' => true, 'data' => '', 'message' => $message, 'notify' => $test);
    }

}
/* * ********************************** */
/* Candidate Apllying Jobs external link */
/* * ********************************** */
add_action('rest_api_init', 'nokriAPI_canidate_apply_job_external_hook', 0);

function nokriAPI_canidate_apply_job_external_hook() {
    register_rest_route(
            'nokri/v1', '/candidate/external_apply/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_canidate_external_apply',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_external_apply')) {

    function nokriAPI_canidate_external_apply($request) {

        global $nokri;
        $user_id = get_current_user_id();
        $user_type = get_user_meta($user_id, '_sb_reg_type', true);
        $json_data = $request->get_json_params();
        $job_id = (isset($json_data['job_id'])) ? trim($json_data['job_id']) : '';
        /* demo check */
        $is_demo = nokri_demo_mode();
        if ($is_demo) {

            return $response = array('success' => false, 'data' => '', 'message' => 'Demo mode');
        }
        /* Is applying job package base */
        $is_apply_pkg_base = ( isset($nokri['job_apply_package_base']) && $nokri['job_apply_package_base'] != "" ) ? $nokri['job_apply_package_base'] : false;
        /* signin page */
        $sign_in = ( isset($nokri['sb_sign_in_page']) && $nokri['sb_sign_in_page'] != "" ) ? $nokri['sb_sign_in_page'] : '';
        if ($is_apply_pkg_base == '1') {
            /* signin page */
            $sign_in = ( isset($nokri['sb_sign_in_page']) && $nokri['sb_sign_in_page'] != "" ) ? $nokri['sb_sign_in_page'] : '';
            if ($is_apply_pkg_base == '1' && $user_id == '') {
                return $response = array('success' => false, 'data' => '', 'message' => 'Please Login To Apply');
            }
            /* Cand package page */
            $cand_package_page = ( isset($nokri['cand_package_page']) && $nokri['cand_package_page'] != "" ) ? $nokri['cand_package_page'] : '';
            /* Validating candidate job package */
            if ($is_apply_pkg_base == '1' && $user_type != '1') {
                $is_package = nokri_candidate_package_expire_notify();
                if ($is_package == 'ae' || $is_package == 'pe' || $is_package == 'np') {
                    return $response = array('success' => false, 'data' => '', 'message' => 'Please Purchase Package');
                    delete_user_meta($user_id, '_job_applied_external_' . $apply_job_id);
                    die();
                }
            }
            /* Cand external jobs */
            $cand_external_jobs = get_user_meta($user_id, '_job_applied_external_' . $apply_job_id, true);
            $external_jobs_array = (explode(",", $cand_external_jobs));
            if (!in_array($apply_job_id, $external_jobs_array)) {
                if ($cand_external_jobs != '') {
                    $apply_job_id = $cand_external_jobs . ',' . $apply_job_id;
                }
                update_user_meta($user_id, '_job_applied_external_' . $apply_job_id, ($apply_job_id));
                if ($is_apply_pkg_base == '1') {
                    $job_applied_rem = get_user_meta($user_id, '_candidate_applied_jobs', true);

                    if ($job_applied_rem != '0' && $job_applied_rem != '-1') {
                        update_user_meta($user_id, '_candidate_applied_jobs', $job_applied_rem - 1);
                    }
                }
                return $response = array('success' => true, 'data' => '', 'message' => '');
                die();
            } else {
                return $response = array('success' => true, 'data' => '', 'message' => '');
                die();
            }
        } else {
            /* with login or not */
            $with_log = ( isset($nokri['job_apply_package_base_wl']) && $nokri['job_apply_package_base_wl'] != "" ) ? $nokri['job_apply_package_base_wl'] : '2';
            if ($with_log == '1' && $user_id == '') {
                return $response = array('success' => false, 'data' => '', 'message' => 'Please Login To Apply');
                die();
            } else {
                return $response = array('success' => true, 'data' => '', 'message' => '');
                die();
            }
        }
    }

}

/* * *************************************************** */
/* Candidate Apllying Jobs through linkedin  Service  */
/* * *************************************************** */



add_action('rest_api_init', 'nokriAPI_canidate_applied_linkedin_hook', 0);

function nokriAPI_canidate_applied_linkedin_hook() {
    register_rest_route(
            'nokri/v1', '/candidate/aplly_linkedin/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_canidate_aplly_linkedin',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_aplly_linkedin')) {

    function nokriAPI_canidate_aplly_linkedin($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();

        $job_id = (isset($json_data['job_id'])) ? trim($json_data['job_id']) : '';
        $url = (isset($json_data['url'])) ? trim($json_data['url']) : '';



        $resume_exist = get_post_meta($job_id, '_job_applied_resume_' . $user_id, true);
        $profile_exist = get_post_meta($job_id, '_job_applied_linked_profile' . $user_id, true);


        if ($resume_exist != '' || $profile_exist != '') {
            $message = __("Already applied", "nokri-rest-api");

            return $response = array('success' => false, 'data' => '', 'message' => $message);
        } else {

            $applied_job_key_val = $user_id . '|' . $url;

            // Updating User Data In Job Meta
            if ($job_id != "") {

                update_post_meta($job_id, '_job_applied_resume_' . $user_id, $applied_job_key_val);
            }

            $cand_date = date("F j, Y");


            update_user_meta($user_id, '_cand_linked', $url);
            update_post_meta($job_id, '_job_applied_status_' . $user_id, 0);
            update_post_meta($job_id, '_job_applied_date_' . $user_id, $cand_date);

            $message = __("Apllied Successfully", "nokri-rest-api");


            return $response = array('success' => true, 'data' => '', 'message' => $message);
        }
    }

}

/* * ********************************** */
/* Candidate Apllied Jobs Service */
/* * ********************************** */
add_action('rest_api_init', 'nokriAPI_canidate_applied_jobs_hook', 0);

function nokriAPI_canidate_applied_jobs_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/applied_jobs/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_candidate_applied_jobs',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );



    register_rest_route(
            'nokri/v1', '/canidate/applied_jobs/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_candidate_applied_jobs',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_candidate_applied_jobs')) {

    function nokriAPI_candidate_applied_jobs($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);


        $json_data = $request->get_json_params();
        $keyword = (isset($json_data['keyword'])) ? trim($json_data['keyword']) : '';


        if (get_query_var('paged')) {
            $paged = get_query_var('paged');
        } else if (isset($json_data['page_number'])) {
            // This will occur if on front page.
            $paged = $json_data['page_number'];
        } else {
            $paged = 1;
        }

        add_action('pre_get_posts', 'my_pre_get_posts');
        $jobs = array();
        $args = array(
            'post_type' => 'job_post',
            'orderby' => 'date',
            'order' => 'DESC',
            's' => $keyword,
            'paged' => $paged,
            'meta_query' => array(
                array(
                    'key' => '_job_applied_resume_' . $user_id,
                ),
            ),
        );
        $query = new WP_Query($args);
        if ($query->have_posts()) {
            $count = 0;
            while ($query->have_posts()) {
                $query->the_post();
                $query->post_author;
                $job_id = get_the_id();
                $post_author_id = get_post_field('post_author', $job_id);
                $company_name = get_the_author_meta('display_name', $post_author_id);
                $job_salary = wp_get_post_terms($job_id, 'job_salary', array("fields" => "ids"));
                $job_salary = isset($job_salary[0]) ? $job_salary[0] : '';

                $job_currency = wp_get_post_terms($job_id, 'job_currency', array("fields" => "ids"));
                $job_currency = isset($job_currency[0]) ? $job_currency[0] : '';


                $job_type = wp_get_post_terms($job_id, 'job_type', array("fields" => "ids"));
                $job_type = isset($job_type[0]) ? $job_type[0] : '';


                $job_adress = get_post_meta($job_id, '_job_address', true);

                if ($job_adress == "") {
                    $job_adress = nokri_job_categories_with_chlid_no_href($job_id, 'ad_location');
                }

                $job_salary_type = get_post_meta($job_id, '_job_salary_type', true);
                /* Getting Company  Profile Photo */
                if (get_user_meta($post_author_id, '_sb_user_pic', true) != "") {
                    $attach_id = get_user_meta($post_author_id, '_sb_user_pic', true);
                    $image_link = wp_get_attachment_image_src($attach_id, 'nokri_job_post_single');
                }

                $seprator = "";
                $job_salary_name = nokri_job_post_single_taxonomies('job_salary', $job_salary);
                $job_salary_type_name = nokri_job_post_single_taxonomies('job_salary_type', $job_salary_type);
                $job_currency_name = nokri_job_post_single_taxonomies('job_currency', $job_currency);


                //   return $job_salary;

                if ($job_salary_name != "" && $job_salary_type_name != "") {

                    $seprator = "/";
                }


                $cand_status = get_post_meta($job_id, '_job_applied_status_' . $user_id, true);
                $cand_final = nokri_canidate_apply_status($cand_status);

                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Id", "nokri-rest-api"), get_the_id(), 'textfield', true, 1, 'job_id');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Name", "nokri-rest-api"), nokriAPI_convert_uniText(get_the_title()), 'textfield', true, 1, 'job_name');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Company Id", "nokri-rest-api"), $post_author_id, 'textfield', true, 1, 'company_id');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Company Name", "nokri-rest-api"), get_the_author_meta('display_name', $post_author_id), 'textfield', true, 1, 'company_name');



                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Salary", "nokri-rest-api"), $job_currency_name . $job_salary_name . $seprator . $job_salary_type_name, 'textfield', true, 1, 'job_salary');


                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Type", "nokri-rest-api"), nokri_job_post_single_taxonomies('job_type', $job_type), 'textfield', true, 1, 'job_type');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Posted", "nokri-rest-api"), nokriAPI_time_ago(), 'textfield', true, 1, 'job_posted');




                $jobs[$count][] = nokriAPI_canidate_fields(__("Company Logo", "nokri-rest-api"), $image_link[0], 'textfield', true, 1, 'company_logo');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Location", "nokri-rest-api"), $job_adress, 'textfield', true, 1, 'job_location');

                $jobs[$count][] = nokriAPI_canidate_fields(__("Status", "nokri-rest-api"), $cand_final, 'textfield', true, 1, 'job_apply_status');



                $count++;
            }

            wp_reset_postdata();
        }



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


        $data['page_title'] = __("Applied Jobs", "nokri-rest-api");
        $data['jobs'] = $jobs;
        $message = __("You have not applied for any job yet", "nokri-rest-api");
        return $response = array('success' => true, 'data' => $data, 'message' => $message, "pagination" => $pagination);
    }

}


/* * ********************************** */
/* Candidate Jobs Matches skills Jobs Id's */
/* * ********************************** */
if (!function_exists('nokriAPI_canidate_jobs_matched_ids')) {

    function nokriAPI_canidate_jobs_matched_ids() {
        $user_crnt_id = get_current_user_id();


        $query = array('post_type' => 'job_post', 'post_status' => 'publish', 'posts_per_page' => 3, 'orderby' => 'date', 'order' => 'DESC');
        $loop = new WP_Query($query);
        $jobs_ids = array();

        while ($loop->have_posts()) {
            $loop->the_post();
            $job_id = get_the_ID();
            $post_author_id = get_post_field('post_author', $job_id);
            $company_name = get_the_author_meta('display_name', $post_author_id);
            $job_skills = wp_get_post_terms($job_id, 'job_skills', array("fields" => "ids"));
            $cand_skills = get_user_meta($user_crnt_id, '_cand_skills', true);



            if (is_array($job_skills) && is_array($cand_skills)) {

                $final_array = array_intersect($job_skills, $cand_skills);
                if (count($final_array) > 0) {

                    $jobs_ids[] = get_the_id();
                }
            }
            $count++;
        }
        wp_reset_postdata();




        return $jobs_ids;
    }

}
//candidate skills notification 
add_action('rest_api_init', 'nokriAPI_canidate_match_skills_job_notification_hook', 0);

function nokriAPI_canidate_match_skills_job_notification_hook() {
    register_rest_route(
            'nokri/v1', '/candidate/skill_match_notification/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_canidate_match_skills_job_notification',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

function nokriAPI_canidate_match_skills_job_notification($request) {
    global $nokri;
    global $nokriAPI;
    $data = array();
    $notification = array();
    $cand_job_notif_en = ( isset($nokri['cand_job_notif']) && $nokri['cand_job_notif'] != "" ) ? $nokri['cand_job_notif'] : '1';
    $cand_job_notif = ( isset($nokri['cand_job_notif']) && $nokri['cand_job_notif'] != "" ) ? $nokri['cand_job_notif'] : false;
    $user_crnt_id = get_current_user_id();
    $get_companies = nokri_following_company_ids($user_crnt_id);
    $noti_message = '';
    if (!empty($get_companies) && $cand_job_notif == '2') {
        $authors = $get_companies;
        $noti_message = esc_html__('Follow companies for job notifications', 'nokri-rest-api');
    } else if ($cand_job_notif == '1') {
        $authors = 0;
        $noti_message = esc_html__('Set your skills for job notifications', 'nokri-rest-api');
    } else {
        $authors = 98780;
        $noti_message = esc_html__('Follow companies for job notifications', 'nokri-rest-api');
    }

    $json_data = $request->get_json_params();
    $page_number = $paged = (isset($json_data['page_number'])) ? $json_data['page_number'] : 1;
    $current_page = $paged;
    $user_per_page = 3;
    $paged = (int) $paged;
    $get_offset = ($paged - 1);
    $offset = $get_offset * $user_per_page;

    $query = array(
        'post_type' => 'job_post',
        'post_status' => 'publish',
        'posts_per_page' => $user_per_page,
        'orderby' => 'date',
        'order' => 'DESC',
        'offset' => $offset,
        'author__in' => $authors,
        'meta_query' => array(
            array(
                'key' => '_job_status',
                'value' => 'active',
                'compare' => '=',
            ),
        ),
    );
    $args = nokri_wpml_show_all_posts_callback($query);
    $loop = new WP_Query($query);

    $count = 0;
    while ($loop->have_posts()) {

        $loop->the_post();
        $job_id = get_the_ID();
        $post_author_id = get_post_field('post_author', $job_id);
        $company_name = get_the_author_meta('display_name', $post_author_id);
        $job_skills = wp_get_post_terms($job_id, 'job_skills', array("fields" => "ids"));
        $cand_skills = get_user_meta($user_crnt_id, '_cand_skills', true);
        $image_link[0] = get_template_directory_uri() . '/images/candidate-dp.jpg';
        if (isset($nokri['nokri_user_dp']['url']) && $nokri['nokri_user_dp']['url'] != "") {
            $image_link = array($nokri['nokri_user_dp']['url']);
        }
        if (get_user_meta($post_author_id, '_sb_user_pic', true) != "") {
            $attach_id = get_user_meta($post_author_id, '_sb_user_pic', true);
            $image_link = wp_get_attachment_image_src($attach_id, '');
        }
        if (empty($image_link[0])) {
            $image_link[0] = get_template_directory_uri() . '/images/candidate-dp.jpg';
        }


        if (is_array($job_skills) && is_array($cand_skills)) {
            $final_array = array_intersect($job_skills, $cand_skills);
            if (count($final_array) > 0) {
                $noti_message = esc_html__('These jobs match your skills', 'nokri-rest-api');
                $notification[$count][] = nokriAPI_canidate_fields(__("Company id", "nokri-rest-api"), $post_author_id, 'textfield', true, 1, 'company_id');
                $notification[$count][] = nokriAPI_canidate_fields(__("Company name", "nokri-rest-api"), $company_name, 'textfield', true, 1, 'company_name');
                $notification[$count][] = nokriAPI_canidate_fields(__("Company Image", "nokri-rest-api"), $image_link[0], 'textfield', true, 1, 'company_img');
                $notification[$count][] = nokriAPI_canidate_fields(__("Company post", "nokri-rest-api"), esc_html__('posted a new job', 'nokri-rest-api'), 'textfield', true, 1, 'job_post');
                $notification[$count][] = nokriAPI_canidate_fields(__("Posting time", "nokri-rest-api"), nokri_time_ago(), 'textfield', true, 1, 'posting_time');
                $notification[$count][] = nokriAPI_canidate_fields(__("Job title", "nokri-rest-api"), get_the_title(), 'textfield', true, 1, 'job_title');
                $notification[$count][] = nokriAPI_canidate_fields(__("Job title", "nokri-rest-api"), get_the_ID(), 'textfield', true, 1, 'job_id');
                $count++;
            }
        }
    }
    $max_num_pages = ceil($total_users / $user_per_page);

    $nextPaged = (int) ($paged) + 1;
    $has_next_page = ( $nextPaged <= (int) $max_num_pages ) ? true : false;

    $pagination = array("max_num_pages" => (int) $max_num_pages, "current_page" => (int) $paged, "next_page" => (int) $nextPaged, "increment" => (int) $user_per_page, "total_notification" => count($notification), "has_next_page" => $has_next_page);
    $data['page_title'] = __("Job notification", "nokri-rest-api");
    if ($notification) {
        $data['notification'] = $notification;
        return $response = array('success' => true, 'data' => $data, 'message' => $noti_message, "pagination" => $pagination);
    } else {
        $data['notification'] = $notification;
        return $response = array('success' => false, 'data' => $data, 'message' => $noti_message, "pagination" => $pagination);
    }
}

add_action('rest_api_init', 'nokriAPI_canidate_jobs_match_skills_hook', 0);

function nokriAPI_canidate_jobs_match_skills_hook() {
    register_rest_route(
            'nokri/v1', '/candidate/jobs_matched/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_canidate_jobs_match_skills',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
    register_rest_route(
            'nokri/v1', '/candidate/jobs_matched/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_canidate_jobs_match_skills',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_jobs_match_skills')) {

    function nokriAPI_canidate_jobs_match_skills($request) {
        $user_crnt_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();
        $page_number = (isset($json_data['page_number'])) ? trim($json_data['page_number']) : '';


        if (get_query_var('paged')) {
            $paged = get_query_var('paged');
        } else if (isset($json_data['page_number'])) {
            // This will occur if on front page.
            $paged = $json_data['page_number'];
        } else {
            $paged = 1;
        }

        add_action('pre_get_posts', 'my_pre_get_posts');
        $jobs_in = nokriAPI_canidate_jobs_matched_ids();
        $jobs = array();
        if (count($jobs_in) > 0 && is_array($jobs_in)) {
            $query2 = new WP_Query(array('post_type' => 'job_post', 'post__in' => $jobs_in, 'paged' => $paged));
            if ($query2->have_posts()) {
                $message = esc_html__('Job matches your skills', 'nokri-rest-api');
                $count = 0;
                while ($query2->have_posts()) {
                    $query2->the_post();
                    $job_id = get_the_ID();
                    $post_author_id = get_post_field('post_author', $job_id);
                    $company_name = get_the_author_meta('display_name', $post_author_id);
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
                    $job_adress = get_post_meta($job_id, '_job_address', true);
                    /* Getting Company  Profile Photo */
                    if (get_user_meta($post_author_id, '_sb_user_pic', true) != "") {
                        $attach_id = get_user_meta($post_author_id, '_sb_user_pic', true);
                        $image_link = wp_get_attachment_image_src($attach_id, 'nokri_job_post_single');
                    }

                    $jobs[$count][] = nokriAPI_canidate_fields(__("Job Id", "nokri-rest-api"), get_the_id(), 'textfield', true, 1, 'job_id');
                    $jobs[$count][] = nokriAPI_canidate_fields(__("Job Name", "nokri-rest-api"), nokriAPI_convert_uniText(get_the_title()), 'textfield', true, 1, 'job_name');
                    $jobs[$count][] = nokriAPI_canidate_fields(__("Company Id", "nokri-rest-api"), $post_author_id, 'textfield', true, 1, 'company_id');
                    $jobs[$count][] = nokriAPI_canidate_fields(__("Company Name", "nokri-rest-api"), get_the_author_meta('display_name', $post_author_id), 'textfield', true, 1, 'company_name');



                    $jobs[$count][] = nokriAPI_canidate_fields(__("Job Salary", "nokri-rest-api"), nokri_job_post_single_taxonomies('job_currency', $job_currency) . nokri_job_post_single_taxonomies('job_salary', $job_salary) . "/" . nokri_job_post_single_taxonomies('job_salary_type', $job_salary_type), 'textfield', true, 1, 'job_salary');


                    $jobs[$count][] = nokriAPI_canidate_fields(__("Job Type", "nokri-rest-api"), nokri_job_post_single_taxonomies('job_type', $job_type), 'textfield', true, 1, 'job_type');
                    $jobs[$count][] = nokriAPI_canidate_fields(__("Job Posted", "nokri-rest-api"), nokriAPI_time_ago(), 'textfield', true, 1, 'job_posted');




                    $jobs[$count][] = nokriAPI_canidate_fields(__("Company Logo", "nokri-rest-api"), $image_link[0], 'textfield', true, 1, 'company_logo');
                    $jobs[$count][] = nokriAPI_canidate_fields(__("Job Location", "nokri-rest-api"), $job_adress, 'textfield', true, 1, 'job_location');

                    $count++;
                }
                wp_reset_postdata();
            }
        } else {
            $message = esc_html__('Set your skills for job notifications', 'nokri-rest-api');
        }
        if (get_query_var('paged')) {
            $paged2 = get_query_var('paged');
        } else if (isset($json_data['page_number'])) {
            // This will occur if on front page.
            $paged2 = $json_data['page_number'];
        } else {
            $paged2 = 1;
        }
        $nextPaged2 = $paged2 + 1;
        $has_next_page2 = ( $nextPaged2 <= (int) $query2->max_num_pages ) ? true : false;
        $pagination2 = array(
            "max_num_pages" => (int) $query2->max_num_pages,
            "current_page" => (int) $paged2,
            "next_page" => (int) $nextPaged2,
            "increment" => (int) get_option('posts_per_page'),
            "current_no_of_ads" => (int) count($query2->posts),
            "has_next_page" => $has_next_page2
        );
        $data['page_title'] = __("Jobs for you", "nokri-rest-api");
        $data['jobs'] = $jobs;
        return $response = array('success' => true, 'data' => $data, 'message' => $message, "pagination" => $pagination2);
    }

}






/* * ********************************* */
/* Return Followed Companies ID's */
/* * ********************************* */
if (!function_exists('nokriAPI_following_company_ids')) {

    function nokriAPI_following_company_ids($user_id) {
        /* Query For Getting All Followed Companies */
        global $wpdb;
        $query = "SELECT meta_value FROM $wpdb->usermeta WHERE user_id = '$user_id' AND meta_key like '_cand_follow_company_%'";
        $cand_followings = $wpdb->get_results($query);
        if (count((array) $cand_followings) > 0) {
            $ids = array();
            foreach ($cand_followings as $companies) {
                $ids[] = $companies->meta_value;
            }
            return $ids;
        }
    }

}





/* * ********************************** */
/*  Candidate Followed Companies Service     */
/* * ********************************** */



add_action('rest_api_init', 'nokriAPI_canidate_followed_companies_hook', 0);

function nokriAPI_canidate_followed_companies_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/followed_companies/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_candidate_followed_companies',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );

    register_rest_route(
            'nokri/v1', '/canidate/followed_companies/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_candidate_followed_companies',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_candidate_followed_companies')) {

    function nokriAPI_candidate_followed_companies($request) {
        global $nokriAPI;
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        /* Get Followed Companies Id's */
        $get_result = nokriAPI_following_company_ids($user_id);
        $jobs = array();


        if (count((array) $get_result) > 0) {
            /* Query For Getting All Followed Companies */
            $json_data = $request->get_json_params();
            $page_number = $paged = (isset($json_data['page_number'])) ? $json_data['page_number'] : 1;
            $current_page = $paged;
            $user_per_page = (isset($nokriAPI['api_user_pagination'])) ? $nokriAPI['api_user_pagination'] : "10";
            $paged = (int) $paged;
            $get_offset = ($paged - 1);
            $offset = $get_offset * $user_per_page;



            $args = array(
                //'search'    => "*".esc_attr( $c_name )."*",
                'number' => $user_per_page,
                'offset' => $offset,
                'order' => 'DESC',
                'include' => $get_result
            );
            $users = new WP_User_Query($args);
            $cand_followings = $users->get_results();
            $total_users = $users->get_total();



            $max_num_pages = ceil($total_users / $user_per_page);

            $jobs = array();
            $message = '';


            $check = array();

            if ($cand_followings) {
                $count = 0;
                foreach ($cand_followings as $key => $object) {
                    $company_id = $object->ID;

                    $check[] = $company_id;

                    $company_adress = get_user_meta($company_id, '_emp_map_location', true);
                    /* Getting Company  Profile Photo  */
                    $image_link[0] = get_template_directory_uri() . '/images/candidate-dp.jpg';
                    if (get_user_meta($company_id, '_sb_user_pic', true) != "") {
                        $attach_id = get_user_meta($company_id, '_sb_user_pic', true);
                        $image_link = wp_get_attachment_image_src($attach_id, 'nokri_job_post_single');
                    }
                    $company_tot_jobs = ( count_user_posts($company_id, 'job_post') );
                    $jobs[$count][] = nokriAPI_canidate_fields(__("Company Id", "nokri-rest-api"), $company_id, 'textfield', true, 1, 'company_id');
                    $jobs[$count][] = nokriAPI_canidate_fields(__("Open Position", "nokri-rest-api"), $company_tot_jobs, 'textfield', true, 1, 'open_position');
                    $jobs[$count][] = nokriAPI_canidate_fields(__("Company Logo", "nokri-rest-api"), $image_link[0], 'textfield', true, 1, 'company_logo');
                    $jobs[$count][] = nokriAPI_canidate_fields(__("Company Name", "nokri-rest-api"), get_the_author_meta('display_name', $company_id), 'textfield', true, 1, 'company_name');
                    $jobs[$count][] = nokriAPI_canidate_fields(__("Company Adress", "nokri-rest-api"), $company_adress, 'textfield', true, 1, 'company_adress');

                    $count++;
                }
            }
        } else {
            $message = __("You have not followed any company yet", "nokri-rest-api");
            $paged = $max_num_pages = $user_per_page = $total_users = '';
        }
        $data['page_title'] = __("Followed Companies", "nokri-rest-api");
        $data['btn_text'] = __("Unfollow", "nokri-rest-api");
        $data['comapnies'] = $jobs;


        /* Pagination */


        $nextPaged = (int) ($paged) + 1;
        $has_next_page = ( $nextPaged <= (int) $max_num_pages ) ? true : false;

        $pagination = array("max_num_pages" => (int) $max_num_pages, "current_page" => (int) $paged, "next_page" => (int) $nextPaged, "increment" => (int) $user_per_page, "total_followed" => (int) $total_users, "has_next_page" => $has_next_page);



        return $response = array('success' => true, 'data' => $data, 'pagination' => $pagination, 'message' => $message);
    }

}




/* * ****************************************** */
/* Candidate Dell Followed Companies Service */
/* * ***************************************** */



add_action('rest_api_init', 'nokriAPI_canidate_dell_followed_companies_hook', 0);

function nokriAPI_canidate_dell_followed_companies_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/dell_followed_companies/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_canidate_del_followed_companies',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_del_followed_companies')) {

    function nokriAPI_canidate_del_followed_companies($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();

        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }

        $company_id = (isset($json_data['company_id'])) ? trim($json_data['company_id']) : '';

        if ($company_id != "") {
            if (delete_user_meta($user_id, '_cand_follow_company_' . $company_id)) {
                $message = __("Delete Successfully", "nokri-rest-api");
            } else {
                $message = __("Something Went Wrong", "nokri-rest-api");
            }
        }

        return $response = array('success' => true, 'data' => '', 'message' => $message);
    }

}


/* * ****************************************** */
/* Candidate Following Companies Service */
/* * ***************************************** */



add_action('rest_api_init', 'nokriAPI_canidate_following_companies_hook', 0);

function nokriAPI_canidate_following_companies_hook() {
    register_rest_route(
            'nokri/v1', '/candidate/following_companies/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_canidate_following_companies',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_following_companies')) {

    function nokriAPI_canidate_following_companies($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();

        $company_id = (isset($json_data['company_id'])) ? trim($json_data['company_id']) : '';
        $follow_date = (isset($json_data['follow_date'])) ? trim($json_data['follow_date']) : '';

        if (get_user_meta($user_id, '_cand_follow_company_' . $company_id, $company_id)) {
            $message = __("Already Followed", "nokri-rest-api");
        } else if ($company_id != "") {
            update_user_meta($user_id, '_cand_follow_company_' . $company_id, $company_id);
            update_user_meta($user_id, '_cand_follow_date', $follow_date);
            $message = __("Followed Successfully", "nokri-rest-api");
        } else {
            $message = __("Something Went Wrong", "nokri-rest-api");
        }

        return $response = array('success' => true, 'data' => '', 'message' => $message);
    }

}




/* * ********************************** */
/* Candidate Full Profile           */
/* * ********************************** */



add_action('rest_api_init', 'nokriAPI_canidate_full_profile_hook', 0);

function nokriAPI_canidate_full_profile_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/full_profile/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_canidate_full_profile',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_full_profile')) {

    function nokriAPI_canidate_full_profile($user_id = '') {

        if ($user_id != "") {
            $user_id = get_current_user_id();
            $user = get_userdata($user_id);
        } else {
            $user_id = $user_id;
        }


        /* Getting Cand Last Education */
        $cand_get_last_edu = get_user_meta($user_id, '_cand_last_edu', true);
        if ($cand_get_last_edu != '') {
            $cand_ed = nokriAPI_job_post_taxonomies('job_qualifications', $cand_get_last_edu);
        }




        $data['title'] = __("Dashboard", "nokri-rest-api");
        $c_name = __("Name:", "nokri-rest-api");
        $data['name'] = nokriAPI_canidate_fields($c_name, $user->display_name, 'textfield', true, 2, 'canidate_name');

        $c_email = __("Email:", "nokri-rest-api");
        $data['name'] = nokriAPI_canidate_fields($c_email, $user->user_email, '', true, 2, 'canidate_name');

        $c_headline = __("Profession:", "nokri-rest-api");
        $data['name'] = nokriAPI_canidate_fields($c_headline, get_user_meta($user_id, '_cand_headline', true), '', true, 2, 'canidate_name');

        $c_last_edu = __("Last Education:", "nokri-rest-api");
        $data['name'] = nokriAPI_canidate_fields($c_last_edu, get_user_meta($user_id, '_cand_headline', true), 'textfield', true, 2, 'canidate_name');

        $c_last_edu = __("Last Education:", "nokri-rest-api");
        $data['name'] = nokriAPI_canidate_fields($c_last_edu, $cand_ed, 'calander', true, 2, 'canidate_name');

        $c_intro = __("Last Education:", "nokri-rest-api");
        $data['name'] = nokriAPI_canidate_fields($c_intro, get_user_meta($user_id, '_cand_intro', true), 'calander', true, 2, 'canidate_name');


        $data['email'] = array("key" => __("Email:", "nokri-rest-api"), "value" => $user->user_email);
        $data['phone_no'] = array("key" => __("Phone:", "nokri-rest-api"), "value" => get_user_meta($user->ID, '_sb_contact', true));
        $data['dob'] = array("key" => __("Date Of Birth:", "nokri-rest-api"), "value" => get_user_meta($user->ID, '_cand_dob', true));
        $data['last_edu'] = array("key" => __("Last Education:", "nokri-rest-api"), "value" => $cand_ed);
        $data['headline'] = array("key" => __("Profession:", "nokri-rest-api"), "value" => get_user_meta($user->ID, '_cand_headline', true));
        $data['description'] = array("key" => __("Description:", "nokri-rest-api"), "value" => get_user_meta($user->ID, '_cand_intro', true));
        $data['skills'] = $skill_tags;

        return $data;
    }

}





/* * ********************************** */
/*   Convert To array Function         */
/* * ********************************** */

function nokriAPI_convert_to_array($data = array()) {
    $count = 0;
    $arr = array();
    foreach ($data as $key => $val) {
        $key = str_replace("'", "", $key);
        $arr[$key] = $val;
    }
    $count = count($data);
    return array("count" => $count, "arr" => $arr);
}

function nokriAPI_stripslashesFull($input) {
    if (is_array($input)) {
        $input = array_map('stripslashesFull', $input);
    } elseif (is_object($input)) {
        $vars = get_object_vars($input);
        foreach ($vars as $k => $v) {
            $input->{$k} = stripslashesFull($v);
        }
    } else {
        $input = stripslashes($input);
    }
    return $input;
}

/* * ****************************************** */
/* Candidate Updating  Professions          */
/* * ***************************************** */




add_action('rest_api_init', 'nokriAPI_candidate_update_profession_hooks', 0);

function nokriAPI_candidate_update_profession_hooks() {

    register_rest_route('nokri/v1', '/candidate/update_profession/',
            array(
                'methods' => WP_REST_Server::EDITABLE,
                'callback' => 'nokriAPI_candidate_update_profession',
                'permission_callback' => function () {
                    return nokriAPI_basic_auth();
                },
            )
    );
}

if (!function_exists('nokriAPI_candidate_update_profession')) {

    function nokriAPI_candidate_update_profession($request) {

        $user_id = get_current_user_id();
        $user = get_userdata($user_id);

        $json_data = $request->get_json_params();

        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }


        $arrp = $profession = array();
        $proData = nokriAPI_convert_to_array($json_data);

        $countNum = ( $proData['count'] == 0 ) ? 0 : $proData['count'] - 1;

        for ($i = 0; $i <= $countNum; $i++) {

            $arr = $proData['arr'];
            $arrp['project_organization'] = $arr[$i]['project_organization'];
            $arrp['project_role'] = $arr[$i]['project_role'];
            $arrp['project_start'] = $arr[$i]['project_start'];
            $arrp['project_name'] = isset($arr[$i]['project_name']) ? $arr[$i]['project_name'] : 1;

            if ($arrp['project_name'] == 1) {
                $arrp['project_end'] = "";
            } else {
                $arrp['project_end'] = isset($arr[$i]['project_end']) ? $arr[$i]['project_end'] : '';
            }
            $arrp['project_desc'] = $arr[$i]['project_desc'];
            $profession[] = $arrp;
        }



        $cand_projects = ($profession);

        if ($profession != '') {
            update_user_meta($user_id, '_cand_profession', $profession);
        }


        $response = array('success' => true, 'data' => '', 'message' => __("Profession Updated", "nokri-rest-api"), 'page_title' => '');
        return $response;
    }

}







/* * ****************************************** */
/* Candidate Updating  Certifications       */
/* * ***************************************** */




add_action('rest_api_init', 'nokriAPI_candidate_update_certifications_hooks', 0);

function nokriAPI_candidate_update_certifications_hooks() {

    register_rest_route('nokri/v1', '/candidate/update_certifications/',
            array(
                'methods' => WP_REST_Server::EDITABLE,
                'callback' => 'nokriAPI_candidate_update_certifications',
                'permission_callback' => function () {
                    return nokriAPI_basic_auth();
                },
            )
    );
}

if (!function_exists('nokriAPI_candidate_update_certifications')) {

    function nokriAPI_candidate_update_certifications($request) {

        $user_id = get_current_user_id();
        $user = get_userdata($user_id);

        $json_data = $request->get_json_params();

        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }

        $arrp = $certification = array();
        $proData = nokriAPI_convert_to_array($json_data);


        $countNum = ( $proData['count'] == 0 ) ? 0 : $proData['count'] - 1;


        for ($i = 0; $i <= $countNum; $i++) {
            $arr = $proData['arr'];

            $arrp['certification_name'] = $arr[$i]['certification_name'];
            $arrp['certification_start'] = $arr[$i]['certification_start'];
            $arrp['certification_end'] = $arr[$i]['certification_end'];
            $arrp['certification_duration'] = $arr[$i]['certification_duration'];
            $arrp['certification_institute'] = $arr[$i]['certification_institute'];
            $arrp['certification_desc'] = $arr[$i]['certification_desc'];
            $certification[] = $arrp;
        }

        $cand_certification = ($arr);



        if ($certification != '') {
            update_user_meta($user_id, '_cand_certifications', $certification);
        }



        $response = array('success' => true, 'data' => '', 'message' => __("Certifications Updated", "nokri-rest-api"), 'page_title' => '');
        return $response;
    }

}







/* * ************************************************** */
/* Candidate Updating Personal Information  Profile */
/* * ************************************************ */



add_action('rest_api_init', 'nokriAPI_canidate_edit_personal_info_hook', 0);

function nokriAPI_canidate_edit_personal_info_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/update_personal_info/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_canidate_update_personal_info',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );

    register_rest_route(
            'nokri/v1', '/canidate/update_personal_info/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_canidate_update_personal_info',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_update_personal_info')) {

    function nokriAPI_canidate_update_personal_info($request) {
        global $nokriAPI;
        global $nokri;
        //NOKRI_API_ALLOW_EDITING


        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }


        $user_id = get_current_user_id();
        $user = get_userdata($user_id);

        $json_data = $request->get_json_params();
        /* Getting Cand Last Education */
        $cand_get_last_edu = get_user_meta($user_id, '_cand_last_edu', true);
        if ($cand_get_last_edu != '') {
            $cand_ed = nokriAPI_job_post_taxonomies('job_qualifications', $cand_get_last_edu);
        } else {
            $cand_ed = nokriAPI_job_post_taxonomies('job_qualifications', '');
        }
        /* Getting Cand type */
        $cand_get_type = get_user_meta($user_id, '_cand_type', true);
        if ($cand_get_type != '') {
            $cand_get_type = nokriAPI_job_post_taxonomies('job_type', $cand_get_type);
        } else {
            $cand_get_type = nokriAPI_job_post_taxonomies('job_type', '');
        }

        /* Getting Cand level */
        $cand_get_level = get_user_meta($user_id, '_cand_level', true);
        if ($cand_get_level != '') {
            $cand_get_level = nokriAPI_job_post_taxonomies('job_level', $cand_get_level);
        } else {
            $cand_get_level = nokriAPI_job_post_taxonomies('job_level', '');
        }

        /* Getting Cand experience */
        $cand_get_experience = get_user_meta($user_id, '_cand_experience', true);
        if ($cand_get_experience != '') {
            $cand_get_experience = nokriAPI_job_post_taxonomies('job_experience', $cand_get_experience);
        } else {
            $cand_get_experience = nokriAPI_job_post_taxonomies('job_experience', '');
        }


        /* getting sallery options */
        $cand_salary_type = get_user_meta($user_id, '_cand_salary_type', true);
        $cand_salary_range = get_user_meta($user_id, '_cand_salary_range', true);
        $cand_salary_curren = get_user_meta($user_id, '_cand_salary_curren', true);


        $cand_get_salary_type = get_user_meta($user_id, '_cand_salary_type', true);
        if ($cand_get_salary_type != '') {
            $cand_get_salary_type = nokriAPI_job_post_taxonomies('job_salary_type', $cand_salary_type);
        } else {
            $cand_get_salary_type = nokriAPI_job_post_taxonomies('job_salary_type', '');
        }


        $cand_get_salary_range = get_user_meta($user_id, '_cand_salary_range', true);
        if ($cand_get_salary_range != '') {
            $cand_get_salary_range = nokriAPI_job_post_taxonomies('job_salary', $cand_salary_range);
        } else {
            $cand_get_salary_range = nokriAPI_job_post_taxonomies('job_salary', '');
        }


        $cand_get_salary_curren = get_user_meta($user_id, '_cand_salary_curren', true);
        if ($cand_get_salary_curren != '') {
            $cand_get_salary_curren = nokriAPI_job_post_taxonomies('job_currency', $cand_salary_curren);
        } else {
            $cand_get_salary_curren = nokriAPI_job_post_taxonomies('job_currency', '');
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
        /* Profile selection */
        $profile_dropdown_value = array("key" => "Public", "val" => "pub", "val" => "priv");
        $cand_name = (isset($json_data['cand_name'])) ? trim($json_data['cand_name']) : '';
        $cand_phone = (isset($json_data['cand_phone'])) ? trim($json_data['cand_phone']) : '';
        $cand_headline = (isset($json_data['cand_headline'])) ? trim($json_data['cand_headline']) : '';
        $cand_dob = (isset($json_data['cand_dob'])) ? trim($json_data['cand_dob']) : '';
        $cand_last_edu = (isset($json_data['cand_last'])) ? trim($json_data['cand_last']) : '';
        $cand_level = (isset($json_data['cand_level'])) ? trim($json_data['cand_level']) : '';
        $cand_type = (isset($json_data['cand_type'])) ? trim($json_data['cand_type']) : '';
        $cand_type = (isset($json_data['cand_type'])) ? trim($json_data['cand_type']) : '';
        $cand_experience = (isset($json_data['cand_experience'])) ? trim($json_data['cand_experience']) : '';
        $cand_intro = (isset($json_data['cand_intro'])) ? trim($json_data['cand_intro']) : '';
        $cand_prof_stat = (isset($json_data['cand_prof_stat'])) ? trim($json_data['cand_prof_stat']) : '';

        $cand_salary_type = (isset($json_data['cand_salary_type'])) ? trim($json_data['cand_salary_type']) : '';
        $cand_salary_range = (isset($json_data['cand_salary_range'])) ? trim($json_data['cand_salary_range']) : '';
        $cand_salary_curren = (isset($json_data['cand_salary_curren'])) ? trim($json_data['cand_salary_curren']) : '';
        $cand_gender = (isset($json_data['cand_gender'])) ? trim($json_data['cand_gender']) : '';

        $custom_filed = (isset($json_data['custom_fields'])) ? (array) ($json_data['custom_fields'] ) : array();

        $request_from = nokriAPI_getSpecific_headerVal('Nokri-Request-From');

        if ($request_from == 'ios') {
            $custom_filed = json_decode(@$json_data['custom_fields'], true);
        }

        /* Updating Values In User Meta Of Current User */

        if ($cand_name != "") {
            wp_update_user(array('ID' => $user_id, 'display_name' => $cand_name));
        }
        if ($cand_phone != '') {
            update_user_meta($user_id, '_sb_contact', $cand_phone);
        }
        if ($cand_headline != '') {
            update_user_meta($user_id, '_user_headline', $cand_headline);
        }
        if ($cand_dob != '') {
            update_user_meta($user_id, '_cand_dob', $cand_dob);
        }

        if ($cand_gender != '') {
            if ($cand_gender == 1) {
                $cand_gender = "male";
            } else if ($cand_gender == 2) {
                $cand_gender = "female";
            } else if ($cand_gender == 3) {
                $cand_gender = "other";
            }
            update_user_meta($user_id, '_cand_gender', $cand_gender);
        }

        if ($cand_last_edu != '') {
            update_user_meta($user_id, '_cand_last_edu', $cand_last_edu);
        }

        if ($cand_type != '') {
            update_user_meta($user_id, '_cand_type', $cand_type);
        }
        if ($cand_level != '') {
            update_user_meta($user_id, '_cand_level', $cand_level);
        }
        if ($cand_experience != '') {
            update_user_meta($user_id, '_cand_experience', $cand_experience);
        }

        if ($cand_salary_type != '') {
            update_user_meta($user_id, '_cand_salary_type', $cand_salary_type);
        }

        if ($cand_salary_range != '') {
            update_user_meta($user_id, '_cand_salary_range', $cand_salary_range);
        }

        if ($cand_salary_curren != '') {
            update_user_meta($user_id, '_cand_salary_curren', $cand_salary_curren);
        }
        if ($cand_intro != '') {
            update_user_meta($user_id, '_cand_intro', $cand_intro);
        }

        if ($is_show_profile_option) {
            if ($cand_prof_stat != '') {
                update_user_meta($user_id, '_user_profile_status', $cand_prof_stat);
            }
        } else {
            update_user_meta($user_id, '_user_profile_status', 'pub');
        }

        /* Updating Custom feilds */
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

        $c_name = nokri_feilds_label('cand_name_label', esc_html__('Name', 'nokri'));
        $data[] = nokriAPI_canidate_fields($c_name, $user->display_name, 'textfield', true, 2, 'cand_name');

        $c_phone = nokri_feilds_label('cand_phone_label', esc_html__('Phone', 'nokri'));
        $data[] = nokriAPI_canidate_fields($c_phone, get_user_meta($user_id, '_sb_contact', true), 'textfield', true, 2, 'cand_phone');

        $c_email = __("Email:", "nokri-rest-api");
        $data[] = nokriAPI_canidate_fields($c_email, $user->user_email, 'textfield', true, 2, '');

        $c_headline = nokri_feilds_label('cand_profession_label', esc_html__('Profession', 'nokri'));
        $data[] = nokriAPI_canidate_fields($c_headline, get_user_meta($user_id, '_user_headline', true), '', true, 2, 'cand_headline');

        $c_dob = nokri_feilds_label('cand_dob_label', esc_html__('Date Of Birth', 'nokri'));
        $data[] = nokriAPI_canidate_fields($c_dob, get_user_meta($user_id, '_cand_dob', true), '', true, 2, 'cand_dob');


        $c_gender = nokri_feilds_label('cand_gend_label', esc_html__('Select Gender', 'nokri'));
        $gender_value = nokriAPI_cand_gender(get_user_meta($user_id, '_cand_gender', true));
        $data[] = nokriAPI_canidate_fields($c_gender, $gender_value, '', true, 2, 'cand_gender');





        $c_last_edu = nokri_feilds_label('cand_quali_end_label', esc_html__('Last Education', 'nokri'));
        $data[] = nokriAPI_canidate_fields($c_last_edu, $cand_ed, 'dropdown', true, 2, 'cand_last');

        $c_level = nokri_feilds_label('cand_level_label', esc_html__('Level', 'nokri'));
        $data[] = nokriAPI_canidate_fields($c_level, $cand_get_level, 'dropdown', true, 2, 'cand_level');

        $c_type = nokri_feilds_label('cand_type_label', esc_html__('Type', 'nokri'));
        $data[] = nokriAPI_canidate_fields($c_type, $cand_get_type, 'dropdown', true, 2, 'cand_type');

        $c_experience = nokri_feilds_label('cand_exper_label', esc_html__('Experience', 'nokri'));
        $data[] = nokriAPI_canidate_fields($c_experience, $cand_get_experience, 'dropdown', true, 2, 'cand_experience');

        $c_salary_type = nokri_feilds_label('cand_salary_type_label', esc_html__('Salary type', 'nokri'));
        $data[] = nokriAPI_canidate_fields($c_salary_type, $cand_get_salary_type, 'dropdown', true, 2, 'cand_salary_type');

        $c_salary_range = nokri_feilds_label('cand_salary_range_label', esc_html__('Salary', 'nokri'));
        $data[] = nokriAPI_canidate_fields($c_salary_range, $cand_get_salary_range, 'dropdown', true, 2, 'cand_salary_range');

        $c_salary_curren = nokri_feilds_label('cand_salary_curren_label', esc_html__('Currency', 'nokri'));
        $data[] = nokriAPI_canidate_fields($c_salary_curren, $cand_get_salary_curren, 'dropdown', true, 2, 'cand_salary_curren');

        $c_profile = nokri_feilds_label('cand_profile_label', esc_html__('Set your profile', 'nokri'));
        $data[] = nokriAPI_canidate_fields($c_profile, $option, 'dropdown', $is_show_profile_option, 2, 'cand_prof_stat');

        $c_profile_img = nokri_feilds_label('cand_dp_label', esc_html__('Profile Image', 'nokri'));
        $data[] = nokriAPI_canidate_fields($c_profile_img, '', 'dropdown', true, 2, 'cand_dp');

        $c_profile_cvr = __("Cover Image:", "nokri-rest-api");
        $data[] = nokriAPI_canidate_fields($c_profile_cvr, '', 'dropdown', true, 2, 'cand_cvr');

        $c_intro = nokri_feilds_label('cand_about_label', esc_html__('About yourSelf', 'nokri'));
        $data[] = nokriAPI_canidate_fields($c_intro, get_user_meta($user_id, '_cand_intro', true), 'textarea', true, 2, 'cand_intro');


        $extras[] = nokriAPI_canidate_fields('Section name', nokri_feilds_label('cand_prof_sec_label', esc_html__('Personal Information', 'nokri')), '', '', 1, 'section_name');
        $extras[] = nokriAPI_canidate_fields('Button name', nokri_feilds_label('cand_about_btn', esc_html__('Save Information', 'nokri')), '', '', 1, 'btn_name');

        $extras[] = nokriAPI_canidate_fields('Change password', __("Update password", "nokri-rest-api"), '', '', 1, 'change_pasword');






        $del_acount = "0";
        if ((isset($nokriAPI['deactivate_app_acount'])) && $nokriAPI['deactivate_app_acount'] == '1') {
            $del_acount = "";
        }

        $extras[] = nokriAPI_canidate_fields('Dell Account', __("Delete account?", "nokri-rest-api"), '', '', $del_acount, 'del_acount');

        $custom_feild_id = (isset($nokri['custom_registration_feilds'])) ? $nokri['custom_registration_feilds'] : '';
        $custom_filed_data = array();
        if ($custom_feild_id != '') {
            $custom_filed_data = nokri_get_register_custom_feildsApi('', 'Registration', $custom_feild_id, '', '');
        }

        $custom_cand_feild_id = (isset($nokri['custom_candidate_feilds'])) ? $nokri['custom_candidate_feilds'] : array();
        $custom_cand_filed_data = array();
        if ($custom_cand_feild_id != '') {
            $custom_cand_filed_data = nokri_get_register_custom_feildsApi('', 'Candidate', $custom_cand_filed_data, '', '', false);
        }
        $custom_fields = array();


        if (!empty($custom_filed_data)) {
            $custom_fields = $custom_filed_data;
        } else if (!empty($custom_cand_filed_data)) {
            $custom_fields = $custom_cand_filed_data;
        }
        if (!empty($custom_filed_data) && !empty($custom_cand_filed_data)) {
            $custom_fields = array_merge($custom_filed_data, $custom_cand_filed_data);
        }
        $response = array('success' => true, 'data' => $data, 'message' => __("Personal information updated.", "nokri-rest-api"), 'extras' => $extras, 'custom_fields' => $custom_fields);
        return $response;
    }

}
/* * ******************************** */
/* Candidate Updating Social Link  */
/* * ******************************* */



add_action('rest_api_init', 'nokriAPI_canidate_edit_social_link_hook', 0);

function nokriAPI_canidate_edit_social_link_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/update_social_link/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_canidate_update_social_link',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );

    register_rest_route(
            'nokri/v1', '/canidate/update_social_link/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_canidate_update_social_link',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_update_social_link')) {

    function nokriAPI_canidate_update_social_link($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();

        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }

        $cand_fb = (isset($json_data['cand_fb'])) ? trim($json_data['cand_fb']) : '';
        $cand_twiter = (isset($json_data['cand_twiter'])) ? trim($json_data['cand_twiter']) : '';
        $cand_linked = (isset($json_data['cand_linked'])) ? trim($json_data['cand_linked']) : '';
        $cand_google = (isset($json_data['cand_google'])) ? trim($json_data['cand_google']) : '';




        $fb = nokri_feilds_label('cand_fb_label', esc_html__('Facebook', 'nokri'));
        $data[] = nokriAPI_canidate_fields($fb, get_user_meta($user_id, '_cand_fb', true), 'textfield', true, 2, 'cand_fb');

        $twitter = nokri_feilds_label('cand_twtr_label', esc_html__('Twitter', 'nokri'));
        $data[] = nokriAPI_canidate_fields($twitter, get_user_meta($user_id, '_cand_twiter', true), 'textfield', true, 2, 'cand_twiter');


        $linked = nokri_feilds_label('cand_linked_label', esc_html__('LinkedIn', 'nokri'));
        $data[] = nokriAPI_canidate_fields($linked, get_user_meta($user_id, '_cand_linked', true), 'textfield', true, 2, 'cand_linked');

        $google = nokri_feilds_label('cand_insta_label', esc_html__('Instagram', 'nokri'));
        $data[] = nokriAPI_canidate_fields($google, get_user_meta($user_id, '_cand_google', true), 'textfield', true, 2, 'cand_google');






        /* Updating Values In User Meta Of Current User */
        update_user_meta($user_id, '_cand_fb', $cand_fb);
        update_user_meta($user_id, '_cand_twiter', $cand_twiter);
        update_user_meta($user_id, '_cand_linked', $cand_linked);
        update_user_meta($user_id, '_cand_google', $cand_google);


        $extras['fb_txt'] = __("https://www.facebook.com", "nokri-rest-api");
        $extras['tw_txt'] = __("https://www.twitter.com", "nokri-rest-api");
        $extras['lk_txt'] = __("https://www.linkedin.com", "nokri-rest-api");
        $extras['g+_txt'] = __("https://www.instagram.com/", "nokri-rest-api");
        $extras['page_title'] = nokri_feilds_label('social_section_label', esc_html__('Social Links', 'nokri'));
        $extras['btn_txt'] = nokri_feilds_label('cand_social_btn', esc_html__('Save Profiles Links', 'nokri'));


        $response = array('success' => true, 'data' => $data, 'message' => __("Social links updated.", "nokri-rest-api"), 'extras' => $extras);
        return $response;
    }

}



/* * ********************************** */
/* Getting Selected Taxonomies Name */
/* * ********************************** */

if (!function_exists('nokriAPI_job_post_taxonomies')) {

    function nokriAPI_job_post_taxonomies($taxonomy_name = '', $value = '') {
        $taxonomies = get_terms($taxonomy_name, array('hide_empty' => false, 'orderby' => 'id', 'order' => 'ASC', 'parent' => 0));
        $option = array();
        if (count($taxonomies) > 0) {
            foreach ($taxonomies as $taxonomy) {

                $children = get_terms($taxonomy->taxonomy, array('parent' => $taxonomy->term_id, 'hide_empty' => false));
                $has_child = ($children) ? true : false;
                $selected = ( $value == $taxonomy->term_id ) ? true : false;

                $option[] = array(
                    "key" => $taxonomy->term_id,
                    "value" => esc_html($taxonomy->name),
                    "has_child" => $has_child,
                    "selected" => $selected,
                    "slug" => sanitize_text_field(($taxonomy->slug)),
                );
            }
        }
        return $option;
    }

}


/* * ********************************** */
/*  Getting candidate gender            */
/* * ********************************** */

if (!function_exists('nokriAPI_cand_gender')) {

    function nokriAPI_cand_gender($value = '') {
        $option = array();
        $male = $female = $other = false;
        if ($value == "male") {
            $male = true;
        } else if ($value == "female") {
            $female = true;
        } else if ($value == "other") {

            $other = true;
        }
        $option[] = array(
            "key" => 1,
            "value" => esc_html__('Male', 'nokri-rest-api'),
            "has_child" => false,
            "selected" => $male,
            "slug" => 'male',
        );
        $option[] = array(
            "key" => 2,
            "value" => esc_html__('Female', 'nokri-rest-api'),
            "has_child" => false,
            "selected" => $female,
            "slug" => 'female',
        );
        $option[] = array(
            "key" => 3,
            "value" => esc_html__('Other', 'nokri-rest-api'),
            "has_child" => false,
            "selected" => $other,
            "slug" => 'other',
        );
        return $option;
    }

}

/* * ********************************** */
/*  Getting Selected external sources    */
/* * ********************************** */

if (!function_exists('nokriAPI_job_external_links')) {

    function nokriAPI_job_external_links($pid, $switch) {

        global $nokri;
        $job_apply_with = get_post_meta($pid, '_job_apply_with', true);
        $job_ext_url = get_post_meta($pid, '_job_apply_url', true);
        $job_ext_mail = get_post_meta($pid, '_job_apply_mail', true);
        $job_ext_whatsapp = get_post_meta($pid, '_job_apply_whatsapp', true);
        $option = array();
        if (!empty($nokri['job_external_source'])) {
            $exter = $inter = $mail = $whatsapp = false;
            foreach ($nokri['job_external_source'] as $key => $value) {
                if ($value == 'exter')
                    $exter = true;
                if ($value == 'inter')
                    $inter = true;
                if ($value == 'mail')
                    $mail = true;
                if ($value == 'whatsapp')
                    $whatsapp = true;
            }
        }

        $select1 = $select2 = $select3 = $select4 = false;
        $val1 = $val2 = $val3 = $val4 = '';
        if ($job_apply_with == 'exter') {
            $select2 = true;
            $val2 = $job_ext_url;
        } else if ($job_apply_with == 'mail') {

            $select3 = true;
            $val3 = $job_ext_mail;
        } else if ($job_apply_with == 'whatsapp') {

            $select4 = true;
            $val4 = $job_ext_whatsapp;
        } else if ($job_apply_with == 'inter') {
            $select1 = true;
        }
        $option[] = array(
            "key" => 0,
            "value" => esc_html__('Select Option', 'nokri-rest-api'),
            "has_child" => false,
            "selected" => false,
            "slug" => esc_html__('Select Option', 'nokri-rest-api'),
            "val" => '',
        );
        if ($inter) {
            $option[] = array(
                "key" => 1,
                "value" => esc_html__('Internal link', 'nokri-rest-api'),
                "has_child" => false,
                "selected" => $select1,
                "slug" => esc_html__('Internal link', 'nokri-rest-api'),
                "val" => '',
            );
        }
        if ($exter) {
            $option[] = array(
                "key" => 2,
                "value" => esc_html__('External link', 'nokri-rest-api'),
                "has_child" => false,
                "selected" => $select2,
                "slug" => esc_html__('External link', 'nokri-rest-api'),
                "val" => $val2,
            );
        }
        if ($mail) {
            $option[] = array(
                "key" => 3,
                "value" => esc_html__('Email', 'nokri-rest-api'),
                "has_child" => "false",
                "selected" => $select3,
                "slug" => esc_html__('Email', 'nokri-rest-api'),
                "val" => $val3,
            );
        }

        if ($whatsapp) {
            $option[] = array(
                "key" => 4,
                "value" => esc_html__('Whatsapp', 'nokri-rest-api'),
                "has_child" => "false",
                "selected" => $select4,
                "slug" => esc_html__('Whatsapp', 'nokri-rest-api'),
                "val" => $val4,
            );
        }
        return $option;
    }

}

/* * ********************************** */
/* Getting Candidate Skills Tags */
/* * ********************************** */

if (!function_exists('nokriAPI_canidate_skills_tags')) {

    function nokriAPI_canidate_skills_tags($user_id = '') {
        /* Getting User Skills Tags */
        $cand_skills = get_user_meta($user_id, '_cand_skills', true);
        $data = array();

        $cand_skills_values = get_user_meta($user_id, '_cand_skills_values', true);
        $cand_skills = get_user_meta($user_id, '_cand_skills', true);

        if (isset($cand_skills) && !empty($cand_skills) && count($cand_skills) > 0) {
            foreach ($cand_skills as $key => $csv) {
                $term = get_term_by('id', $csv, 'job_skills');
                if ($term) {
                    $skill_lavel = 100;

                    if (isset($cand_skills_values) && is_array($cand_skills_values)) {
                        if (array_key_exists($key, $cand_skills_values)) {
                            $skill_lavel = $cand_skills_values[$key];
                        }
                    }

                    $data[] = array("name" => $term->name, "Percent value" => (int) $skill_lavel, "id" => $term->term_id);
                }
            }
        }
        return $data;
    }

}



/* Getting Selected Taxonomies Name */
if (!function_exists('nokriAPI_cand_skills_values')) {

    function nokriAPI_cand_skills_values($skill_value = '') {


        for ($i = 5; $i <= 100;) {
            $array_values[] = $i;
            $i = $i + 5;
        }


        $option = '';
        if (empty($skill_value)) {
            $skill_value = array();
        }

        if (count((array) $array_values) > 0) {
            $option = array();
            foreach ($array_values as $array_value) {
                if (in_array($array_value, $skill_value)) {
                    $selected = true;
                } else {
                    $selected = false;
                }



                $option[] = array("value" => esc_html($array_value), "selected" => $selected);
            }
        }

        return $option;
    }

}
/* * ******************************** */
/* Candidate Updating Skills  */
/* * ******************************* */
add_action('rest_api_init', 'nokriAPI_canidate_edit_skills_hook', 0);

function nokriAPI_canidate_edit_skills_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/update_skills/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_canidate_update_skills',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );

    register_rest_route(
            'nokri/v1', '/canidate/update_skills/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_canidate_update_skills',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_update_skills')) {

    function nokriAPI_canidate_update_skills($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();

        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }



        $cand_skills = (isset($json_data['cand_skills'])) ? $json_data['cand_skills'] : '';

        $cand_skills_values = (isset($json_data['cand_skills_values'])) ? $json_data['cand_skills_values'] : '';


        $skill_arr = nokriAPI_canidate_skills_tags($user_id);


        $data['skills_selected'] = $skill_arr;

        $skills = nokri_feilds_label('cand_skills_label', esc_html__('Select Your Skill', 'nokri'));
        $data['skills_field'] = nokriAPI_canidate_fields($skills, nokriAPI_job_post_taxonomies('job_skills'), 'textfield', true, 2, 'cand_skills');

        $skills_values = nokri_feilds_label('cand_skills_value_plc', esc_html__('Select Your Skill Value', 'nokri'));
        $data['skills_field_values'] = nokriAPI_canidate_fields($skills_values, nokriAPI_cand_skills_values(get_user_meta($user_id, '_cand_skills_values', true)), 'textfield', true, 2, 'cand_skills_values');


        /* Updating Values In User Meta Of Current User */

        if (isset($json_data) && $cand_skills != "") {
            update_user_meta($user_id, '_cand_skills', $cand_skills);
        }
        if (isset($json_data) && $cand_skills_values != "") {
            update_user_meta($user_id, '_cand_skills_values', $cand_skills_values);
        }



        $extras['page_title'] = nokriAPI_canidate_fields(nokri_feilds_label('skill_section_label', esc_html__('Skills', 'nokri')), '', 'textfield', true, 2, 'section_name');
        $extras['btn_name'] = nokriAPI_canidate_fields(__("Save Skills", "nokri-rest-api"), '', 'textfield', true, 2, 'btn_name');


        $response = array('success' => true, 'data' => $data, 'message' => __("Skills updated", "nokri-rest-api"), 'extras' => $extras);
        return $response;
    }

}


/* * ******************************** */
/* Candidate Updating Locations  */
/* * ******************************* */



add_action('rest_api_init', 'nokriAPI_canidate_edit_location_hook', 0);

function nokriAPI_canidate_edit_location_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/update_location/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_canidate_update_location',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );

    register_rest_route(
            'nokri/v1', '/canidate/update_location/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_canidate_update_location',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_update_location')) {

    function nokriAPI_canidate_update_location($request) {
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();

        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }

        global $nokriAPI;

        $cand_lat = (isset($json_data['cand_lat'])) ? trim($json_data['cand_lat']) : '';
        $cand_long = (isset($json_data['cand_long'])) ? trim($json_data['cand_long']) : '';
        $cand_loc = (isset($json_data['cand_loc'])) ? trim($json_data['cand_loc']) : '';

        $cand_country = (isset($json_data['cand_country'])) ? trim($json_data['cand_country']) : '';
        $cand_country_states = (isset($json_data['cand_country_states'])) ? trim($json_data['cand_country_states']) : '';
        $cand_country_cities = (isset($json_data['cand_country_cities'])) ? trim($json_data['cand_country_cities']) : '';
        $cand_country_towns = (isset($json_data['cand_country_towns'])) ? trim($json_data['cand_country_towns']) : '';


        $cand_location = array();

        if ($cand_country != "") {
            $cand_location[] = $cand_country;
        }
        if ($cand_country_states != "") {
            $cand_location[] = $cand_country_states;
        }
        if ($cand_country_cities != "") {
            $cand_location[] = $cand_country_cities;
        }
        if ($cand_country_towns != "") {
            $cand_location[] = $cand_country_towns;
        }


        if (!empty($cand_location)) {
            update_user_meta($user_id, '_cand_custom_location', ($cand_location));
        }



        //Countries
        $ad_country = nokri_get_cats('ad_location', 0);
        $country_html = array();
        foreach ($ad_country as $ad_count) {
            $children = get_terms($ad_count->taxonomy, array('parent' => $ad_count->term_id, 'hide_empty' => false));
            $has_child = ($children) ? true : false;
            $country_html[] = array("key" => $ad_count->term_id, "value" => esc_html($ad_count->name), "has_child" => $has_child);
        }


        //Countries
        //$custom_locations   = get_user_meta($user_id, '_cand_custom_location', true);
        //$levelz	          =	(array) count($custom_locations);
        $loc_section_heading = ( isset($nokriAPI['API_job_country_level_heading']) && $nokriAPI['API_job_country_level_heading'] != "" ) ? $nokriAPI['API_job_country_level_heading'] : '';


        $lat = nokri_feilds_label('cand_lat_label', esc_html__('Latitude', 'nokri'));
        $data[] = nokriAPI_canidate_fields($lat, get_user_meta($user_id, '_cand_map_lat', true), 'textfield', true, 2, 'cand_lat');

        $long = nokri_feilds_label('cand_long_label', esc_html__('Longitude', 'nokri'));
        $data[] = nokriAPI_canidate_fields($long, get_user_meta($user_id, '_cand_map_long', true), 'textfield', true, 2, 'cand_long');


        $loc = __("Set Your Location:", "nokri-rest-api");
        $data[] = nokriAPI_canidate_fields($loc, get_user_meta($user_id, '_cand_address', true), 'textfield', true, 2, 'cand_loc');

        $custom_loc = $loc_section_heading;
        $data[] = nokriAPI_canidate_fields($custom_loc, $country_html, 'textfield', true, 2, 'cand_custom_loc');




        /* Updating Values In User Meta Of Current User */

        if ($cand_lat != "") {
            update_user_meta($user_id, '_cand_map_lat', $cand_lat);
        }
        if ($cand_long != '') {
            update_user_meta($user_id, '_cand_map_long', $cand_long);
        }
        if ($cand_loc != '') {
            update_user_meta($user_id, '_cand_address', $cand_loc);
        }
        if ($cand_custom_loc != '') {
            update_user_meta($user_id, '_cand_custom_location', $cand_custom_loc);
        }


        /* Location headings */
        $job_country_level_1 = ( isset($nokriAPI['API_job_country_level_1']) && $nokriAPI['API_job_country_level_1'] != "" ) ? $nokriAPI['API_job_country_level_1'] : '';

        $job_country_level_2 = ( isset($nokriAPI['API_job_country_level_2']) && $nokriAPI['API_job_country_level_2'] != "" ) ? $nokriAPI['API_job_country_level_2'] : '';

        $job_country_level_3 = ( isset($nokriAPI['API_job_country_level_3']) && $nokriAPI['API_job_country_level_3'] != "" ) ? $nokriAPI['API_job_country_level_3'] : '';

        $job_country_level_4 = ( isset($nokriAPI['API_job_country_level_4']) && $nokriAPI['API_job_country_level_4'] != "" ) ? $nokriAPI['API_job_country_level_4'] : '';

        $map_heading = ( isset($nokriAPI['API_job_map_heading_txt']) && $nokriAPI['API_job_map_heading_txt'] != "" ) ? $nokriAPI['API_job_map_heading_txt'] : '';


        $extras['page_title'] = nokri_feilds_label('loc_section_label', esc_html__('Location And Address', 'nokri'));
        $extras['btn_txt'] = nokri_feilds_label('cand_social_btn', esc_html__('Save Location', 'nokri'));
        $extras['country_txt'] = $job_country_level_1;
        $extras['state_txt'] = $job_country_level_2;
        $extras['city_txt'] = $job_country_level_3;
        $extras['town_txt'] = $job_country_level_4;

        $cand_custom_loc = get_user_meta($user_id, '_cand_custom_location', true);
        $levelz = count((array) $cand_custom_loc);
        $country_html = !(empty($levelz)) ? get_user_custom_location($cand_custom_loc, $levelz) : array();

        $response = array('success' => true, 'data' => $data, 'message' => __("Location Updated.", "nokri-rest-api"), 'extras' => $extras, 'custom_location' => $country_html);
        return $response;
    }

}
/* * ******************************** */
/* Candidate Public Profile       */
/* * ******************************* */
add_action('rest_api_init', 'nokriAPI_candidate_public_profile_hook', 0);

function nokriAPI_candidate_public_profile_hook() {
    register_rest_route(
            'nokri/v1', '/candidate/public_profile/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_candidate_public_profile',
        'permission_callback' => function () {
            return true;
        },
            )
    );

    register_rest_route(
            'nokri/v1', '/candidate/public_profile/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_candidate_public_profile',
        'permission_callback' => function () {
            return true;
        },
            )
    );
}

if (!function_exists('nokriAPI_candidate_public_profile')) {

    function nokriAPI_candidate_public_profile($request) {
        global $nokriAPI;
        global $nokri;


        $current_user_id = get_current_user_id();


        $profile_msg = isset($nokriAPI['api_user_profile_msg']) ? $nokriAPI['api_user_profile_msg'] : '';
        $json_data = $request->get_json_params();
        $user_id = (isset($json_data['user_id'])) ? $json_data['user_id'] : $current_user_id;
        $user_info = get_userdata($user_id);
        $can_search = nokriAPI_is_cand_search_allowed($user_id);

        echo nokri_updating_candidate_profile_percent();
        $profile_percent = get_user_meta($current_user_id, '_cand_profile_percent', true);
        $map_switch = isset($nokri['cand_map_switch']) ? $nokri['cand_map_switch'] : "0";
        /* Is pkg expired */
        $is_pkg_expired = nokriAPI_is_resume_pkg_expired();
        if ($is_pkg_expired != '1') {
            $profile_msg = $is_pkg_expired;
        }
        if ($can_search) {
            $data['basic_ifo'] = nokriAPI_canidate_profile_get($user_id, true);
            $data['certifications'] = nokriAPI_canidate_certifications_get($user_id, true);
            $data['Education'] = nokriAPI_canidate_education_get($user_id, true);
            $data['Profession'] = nokriAPI_canidate_professions_get($user_id, true);
            $data['portfolio'] = nokriAPI_canidate_portfolio_get($user_id, true);
            $data['user_contact']['receiver_id'] = $user_info->ID;
            $data['user_contact']['receiver_name'] = __("Contact", "nokri-rest-api") . " " . $user_info->display_name;
            $data['user_contact']['receiver_email'] = $user_info->user_email;
            $data['user_contact']['sender_name'] = __("Please enter name", "nokri-rest-api");
            $data['user_contact']['sender_email'] = __("Please enter email", "nokri-rest-api");
            $data['user_contact']['sender_subject'] = __("Please enter subject", "nokri-rest-api");
            $data['user_contact']['sender_message'] = __("Please enter message", "nokri-rest-api");
            $data['user_contact']['btn_txt'] = __("Message", "nokri-rest-api");
            $data['cand_scoring'] = nokriAPI_canidate_fields(__("Profile Percent", "nokri-rest-api"), $profile_percent, 'textfield', true, 2, 'cand_scrore');



            $data['page_title'] = __("Candidate Details", "nokri-rest-api");

            $is_skill_tag = isset($nokri['skills_as_tag']) ? $nokri['skills_as_tag'] : false;
            $data['is_skill_tag'] = $is_skill_tag;


            $is_video_uppload = isset($nokri['cand_video_resume_switch']) ? $nokri['cand_video_resume_switch'] : "false";
            $data['is_video_upload'] = $is_video_uppload;

            $cand_into_video = get_user_meta($user_id, '_cand_intro_vid', true);

            if ($is_video_uppload) {
                $video_id = get_user_meta($user_id, 'cand_video_resumes', true);
                $cand_into_video = $video_id != "" ? wp_get_attachment_url($video_id) : "";
            }
            $data['cand_intro_video'] = $cand_into_video;

            $is_skill_tag = isset($nokri['skills_as_tag']) ? $nokri['skills_as_tag'] : false;
            $data['extra']['is_skill_tag'] = nokriAPI_canidate_fields('', $is_skill_tag, 'textfield', true, '', 'is_skill_tag');


            $custom_feild_id = (isset($nokri['custom_registration_feilds'])) ? $nokri['custom_registration_feilds'] : '';

            $custom_filed_data = array();
            if ($custom_feild_id) {
                $custom_filed_data = nokri_get_cand_register_custom_feildsApi($user_id, 'Registration', $custom_feild_id, '', '');
            }


            $custom_cand_feild_id = (isset($nokri['custom_candidate_feilds'])) ? $nokri['custom_candidate_feilds'] : '';
            $custom_cand_filed_data = array();
            if ($custom_cand_feild_id != '') {
                $custom_cand_filed_data = nokri_get_cand_register_custom_feildsApi($user_id, 'Candidate', $custom_cand_filed_data, '', '');
            }

            $custom_fields = array();


            if (!empty($custom_filed_data)) {
                $custom_fields = $custom_filed_data;
            } else if (!empty($custom_cand_filed_data)) {
                $custom_fields = $custom_cand_filed_data;
            }
            if (!empty($custom_filed_data) && !empty($custom_cand_filed_data)) {
                $custom_fields = array_merge($custom_filed_data, $custom_cand_filed_data);
            }

            $data['custom_fields'] = $custom_fields;
            $data['user_reviews'] = nokri_get_candidate_rating($user_id);
            $message = '';
            $success = true;
        } else {
            $data = array();
            $message = $profile_msg;
            $success = false;
        }

        $response = array('success' => $success, 'data' => $data, "message" => $message);
        return $response;
    }

}

/* * ******************************** */
/* user contact service            */
/* * ******************************* */
add_action('rest_api_init', 'nokriAPI_user_contact_hook', 0);

function nokriAPI_user_contact_hook() {
    register_rest_route(
            'nokri/v1', '/user_contact/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_user_contact',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );

    register_rest_route(
            'nokri/v1', '/user_contact/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_candidate_public_profile',
        'permission_callback' => function () {
            return true;
        },
            )
    );
}

if (!function_exists('nokriAPI_user_contact')) {

    function nokriAPI_user_contact($request) {
        global $nokriAPI;
        $current_user_id = get_current_user_id();
        $json_data = $request->get_json_params();

        $receiver_id = (isset($json_data['receiver_id'])) ? $json_data['receiver_id'] : '';
        $sender_name = (isset($json_data['sender_name'])) ? $json_data['sender_name'] : '';
        $sender_email = (isset($json_data['sender_email'])) ? $json_data['sender_email'] : '';
        $sender_subject = (isset($json_data['sender_subject'])) ? $json_data['contact_subject'] : '';
        $sender_message = (isset($json_data['sender_message'])) ? $json_data['sender_message'] : '';

        nokri_contact_me_email($receiver_id, $sender_email, $sender_name, $sender_subject, $sender_message);


        $response = array('success' => true, 'data' => '', "message" => __("Sent successfully", "nokri-rest-api"));


        return $response;
    }

}

function nokri_get_cand_register_custom_feildsApi($author_id = '', $feilds_for = '', $id = '', $edit_profile = '', $show_profile = '') {

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
    //  $myhtml[] = nokriAPI_canidate_fields(__("Name", "nokri-rest-api"), $user->display_name, $name_hldr, true, '', 'cand_name');
    // $myhtml[] = nokriAPI_canidate_fields(__("Member Since", "nokri-rest-api"), date("M Y", strtotime($registered)), 'textfield', true, '', 'cand_rgstr');
    //  $myhtml[] = nokriAPI_canidate_fields(__("Email", "nokri-rest-api"), $user->user_email, 'textfield', true, '', 'cand_email');
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
                        $meta_value = get_user_meta($user_id, $field_slug, true);
                        $checked = ($meta_value == $value) ? 'true' : 'false';
                    }

                    $myhtml[] = array("key" => esc_html($field_label), "field_type" => 'checkbox', "field_type_name" => "$field_slug", "value" => $meta_value, "is_required" => $field_req, "is_show" => $field_pub);
                }
                /* Input */
                if ($field_type == 'Input') {
                    $field_slug = preg_replace('/\s+/', '', $field_label);
                    $meta_value = get_user_meta($user_id, $field_slug, true);
                    $myhtml[] = array("key" => $field_label, "field_type" => 'textfield', "field_type_name" => "$field_slug", "field_val" => $field_value, "value" => $meta_value, "is_required" => $field_req, "is_show" => $field_pub);
                }
                /* Number */
                if ($field_type == 'Number') {
                    $field_slug = preg_replace('/\s+/', '', $field_label);
                    $meta_value = get_user_meta($user_id, $field_slug, true);
                    $requires = '';


                    $myhtml[] = array("key" => $field_label, "field_type" => 'Number', "field_type_name" => "$field_slug", "field_val" => $field_value, "value" => $meta_value, "is_required" => $field_req, "is_show" => $field_pub);
                }
                /* Text Area */
                if ($field_type == 'Text Area') {
                    $field_slug = preg_replace('/\s+/', '', $field_label);
                    $meta_value = get_user_meta($user_id, $field_slug, true);
                    $myhtml[] = array("key" => $field_label, "field_type" => 'Textarea', "field_type_name" => "$field_slug", "field_val" => $field_value, "value" => $meta_value, "is_required" => $field_req, "is_show" => $field_pub);
                }
                /* Select Box */
                if ($field_type == 'Select Box') {
                    $options = $selected = '';
                    $field_slug = preg_replace('/\s+/', '', $field_label);
                    $meta_value = get_user_meta($user_id, $field_slug, true);
                    $myhtml[] = array("key" => $field_label, "field_type" => 'select', "field_type_name" => "$field_slug", "value" => $meta_value, "is_required" => $field_req, "is_show" => $field_pub);
                }
                /* Date  */
                if ($field_type == 'Date') {

                    $field_slug = preg_replace('/\s+/', '', $field_label);
                    $meta_value = get_user_meta($user_id, $field_slug, true);

                    $myhtml[] = array("key" => $field_slug, "field_type" => 'date', "field_type_name" => "$field_slug", "field_val" => $field_value, "value" => $meta_value, "is_required" => $field_req, "is_show" => $field_pub);
                }
            } else {
                if ($field_pub == 'Yes') {
                    $field_slug = preg_replace('/\s+/', '', $field_label);
                    $meta_value = get_user_meta($user_id, $field_slug, true);
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

add_action('rest_api_init', 'nokriAPI_post_cand_video_hook', 0);

function nokriAPI_post_cand_video_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/cand_save_video/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_cand_post_video_url',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

function nokriAPI_cand_post_video_url($request) {
    $user_id = get_current_user_id();
    $json_data = $request->get_json_params();
    $cand_video_url = isset($json_data['cand_video']) ? $json_data['cand_video'] : '';
    if ($cand_video_url != '') {
        update_user_meta($user_id, '_cand_video', $cand_video_url);
    }



    $response = array('success' => true, 'data' => '', "message" => __("Saved Succesfully", "nokri-rest-api"));


    return $response;
}

/* * ******************************** */
/* emp saving candidate resume           */
/* * ******************************* */
add_action('rest_api_init', 'nokriAPI_employer_saving_resumes_hook', 0);

function nokriAPI_employer_saving_resumes_hook() {
    register_rest_route(
            'nokri/v1', '/employer/saving_cand_resumes/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_emp_saving_cand_resume',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

function nokriAPI_emp_saving_cand_resume($request) {

    $emp_id = get_current_user_id();
    $json_data = $request->get_json_params();
    $cand_id = isset($json_data['cand_id']) ? $json_data['cand_id'] : '';

    if (get_user_meta($emp_id, '_sb_reg_type', true) == '0') {
        return $response = array('success' => true, 'data' => '', "message" => __("Only employer can do this", "nokri-rest-api"));
    }

    $resumes = get_user_meta($emp_id, '_emp_saved_resume_' . $emp_id, true);
    $resumesArray = explode(',', $resumes);

    if (in_array($cand_id, $resumesArray)) {
        return $response = array('success' => true, 'data' => '', "message" => __("Profile Already Saved", "nokri-rest-api"));
    }

    if ($resumes != "") {
        $updated_resumes = $resumes . ',' . sanitize_text_field($cand_id);
    } else {
        $updated_resumes = sanitize_text_field($cand_id);
    }
    update_user_meta($emp_id, '_emp_saved_resume_' . $emp_id, $updated_resumes);
    return $response = array('success' => true, 'data' => '', "message" => __("Profile Saved Succesfully", "nokri-rest-api"));
}

/* * ******************************** */
/* emp saving candidate resume           */
/* * ******************************* */
add_action('rest_api_init', 'nokriAPI_employer_removing_resumes_hook', 0);

function nokriAPI_employer_removing_resumes_hook() {
    register_rest_route(
            'nokri/v1', '/employer/removing_cand_resumes/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_emp_removing_cand_resume',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

function nokriAPI_emp_removing_cand_resume($request) {
    $emp_id = get_current_user_id();
    $json_data = $request->get_json_params();
    $resume_id = isset($json_data['resume_id']) ? $json_data['resume_id'] : '';
    if ($resume_id != '') {
        if (get_user_meta($emp_id, '_emp_saved_resume_' . $emp_id, true) != "") {
            $ids = get_user_meta($emp_id, '_emp_saved_resume_' . $emp_id, true);
            $res = str_replace($resume_id, "", $ids);
            $res = str_replace(',,', ",", $res);
            $img_ids = trim($res, ',');
            update_user_meta($emp_id, '_emp_saved_resume_' . $emp_id, $img_ids);
        }
        delete_user_meta($emp_id, '_emp_saved_resume_' . $emp_id, $resume_id);
        return $response = array('success' => true, 'data' => '', "message" => __("Removed Succesfully", "nokri-rest-api"));
    }
}

/* * ********************************** */
/*   Candidate job alerts               */
/* * ********************************** */

add_action('rest_api_init', 'nokriAPI_candidate_job_alrt_hook', 0);

function nokriAPI_candidate_job_alrt_hook() {
    register_rest_route(
            'nokri/v1', '/candidate/job_alerts', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_get_cand_job_alerts',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

function nokriAPI_get_cand_job_alerts($request) {

    global $nokri;
    global $nokriAPI;
    $current_id = get_current_user_id();
    $job_alert = nokri_get_candidates_job_alerts($current_id);
    $alerts = array();
    $data = array();
    $message = '';
    if (isset($job_alert) && !empty($job_alert)) {
        $count = 0;
        foreach ($job_alert as $key => $val) {
            $terms = get_term_by('id', $val['alert_category'], 'job_category');
            $term_name = $terms->name;
            $alert_name = $val['alert_name'];
            $alert_frequency = nokri_get_candidates_job_alerts_freq($val['alert_frequency']);
            $alerts[$count][] = nokriAPI_canidate_fields(__("Alert Name", "nokri-rest-api"), $alert_name, 'textfield', true, 1, 'alert_name');
            $alerts[$count][] = nokriAPI_canidate_fields(__("Alert Frequency", "nokri-rest-api"), $alert_frequency, 'textfield', true, 1, 'alert_frequency');
            $alerts[$count][] = nokriAPI_canidate_fields(__("Alert Category", "nokri-rest-api"), $term_name, 'textfield', true, 1, 'alert_category');
            $alerts[$count][] = nokriAPI_canidate_fields(__("Delete", "nokri-rest-api"), $key, 'textfield', true, 1, 'alert_key');
            $count++;
        }
    } else {

        $message = __("You have not subscribe yet", "nokri-rest-api");
    }
    $data['alerts'] = $alerts;
    return $response = array('success' => true, 'data' => $data, "message" => $message);
}

/* * ********************************** */
/*   Candidate deleting jobs alerts     */
/* * ********************************** */
add_action('rest_api_init', 'nokriAPI_candidate_delete_alert_hook', 0);

function nokriAPI_candidate_delete_alert_hook() {
    register_rest_route(
            'nokri/v1', '/candidate/delete_job_alert', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_cand_removing_alert',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

function nokriAPI_cand_removing_alert($request) {

    $user_id = get_current_user_id();
    $json_data = $request->get_json_params();
    $alert_id = isset($json_data['alert_id']) ? $json_data['alert_id'] : '';


    if ($alert_id != "") {
        if (delete_user_meta($user_id, $alert_id)) {
            return $response = array('success' => true, "message" => esc_html__('Deleted Successfully', 'nokri-rest-api'));
        } else {
            return $response = array('success' => false, "message" => esc_html__('Something Went Wrong', 'nokri-rest-api'));
        }
    }
}

add_action('rest_api_init', 'nokriAPI_cand_package_details_hook', 0);

function nokriAPI_cand_package_details_hook() {
    register_rest_route(
            'nokri/v1', '/candidate/package_details/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_cand_package_details',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_cand_package_details')) {

    function nokriAPI_cand_package_details($request) {
        global $nokri;
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);

        $infos = array();
        $data = array();

        /* Is Package base */

        $package_date = '';
        $no_of_jobs = '';
        $featured_prof = '';
        $pkg_message = '';
        $is_pkg_exired = nokri_candidate_package_expire_notify();
        if ($is_pkg_exired == 're') {
            $pkg_message = esc_html__('Your number of jobs has been expired', 'nokri-rest-api');
        } else if ($is_pkg_exired == 'pe') {
            $pkg_message = esc_html__('Your package has been expired. Please renew package', 'nokri-rest-api');
        } else if ($is_pkg_exired == 'np') {
            $pkg_message = esc_html__('No packages purchased yet', 'nokri-rest-api');
        }

        $is_pkg_exired = nokri_candidate_package_expire_notify();
        if ($is_pkg_exired != 're' && $is_pkg_exired != 'pe' && $is_pkg_exired != 'np') {

            $infos = array(
                "name" => __("Title", "nokri-rest-api"),
                "details" => __("Description", "nokri-rest-api"),
                "expiry" => __("Package expiry", "nokri-rest-api"),
                "feature_profile" => __("Featured Profile Expiry", "nokri-rest-api"),
                "jobs_left" => __("Jobs To Apply", "nokri-rest-api"),
            );


            $package_date = get_user_meta($user_id, '_sb_expire_ads', true);
            if ($package_date == '-1') {
                $package_date = __(" Unlimited", 'nokri-rest-api');
            } else {
                $package_date = date_i18n(get_option('date_format'), strtotime($package_date));
            }

            $no_of_jobs = get_user_meta($user_id, '_candidate_applied_jobs', true);
            $featured_prof = get_user_meta($user_id, '_candidate_feature_profile', true);
            if ($no_of_jobs == '-1') {
                $no_of_jobs = __(" Unlimited", 'nokri-rest-api');
            }
            if ($featured_prof == '-1') {
                $featured_prof = __(" Unlimited", 'nokri-rest-api');
            } else {
                $featured_prof = date_i18n(get_option('date_format'), strtotime($featured_prof));
            }


            $data['expiry'] = $package_date;
            $data['feature_profile'] = $featured_prof;
            $data['jobs_left'] = $no_of_jobs;
            $data['info'] = $infos;
        }

        $data['page_title'] = __("Packages", "nokri-rest-api");
        return $response = array('success' => true, 'data' => $data, 'message' => $pkg_message);
    }

}


/* * ********************************** */
/* Candidate  Upload  Resume without login  */
/* * ********************************** */

add_action('rest_api_init', 'nokriAPI_candidate_api_uploading_resume_hook', 0);

function nokriAPI_candidate_api_uploading_resume_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/uploding_resume/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_candidate_api_uploading_resume',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_candidate_api_uploading_resume')) {

    function nokriAPI_candidate_api_uploading_resume($request) {
        global $nokriAPI;
        $json_data = $request->get_json_params();

        $data = array();


        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }
        if (!isset($_FILES['my_cv_upload']))
            return array('success' => false, 'data' => '', 'message' => __("Something went wrong.", "nokri-rest-api"), 'attach_id' => '');

        //authenticate_check();
        require_once ABSPATH . 'wp-admin/includes/image.php';
        require_once ABSPATH . 'wp-admin/includes/file.php';
        require_once ABSPATH . 'wp-admin/includes/media.php';

        $size_arr = explode('-', $nokriAPI['api_upload_resume_size']);
        $display_size = $size_arr[1];
        $actual_size = $size_arr[0];

        /* Getting Extension  */
        $path = $_FILES['my_cv_upload']['name'];
        $imageFileType = pathinfo($path, PATHINFO_EXTENSION);



        /* Check File Format */
        if ($imageFileType != "doc" && $imageFileType != "docx" && $imageFileType != "pdf") {
            $message = esc_html__("Sorry, only Doc, Docx and Pdf are allowed.", 'nokri-rest-api');

            $response = array('success' => false, 'data' => '', 'message' => $message, 'attach_id' => '');

            return $response;
        }

        // Check file size */
        if ($_FILES['my_cv_upload']['size'] > $nokriAPI['api_upload_resume_size']) {
            $message = esc_html__("Max allowed resume size is", 'nokri-rest-api') . " " . $display_size;
            $response = array('success' => false, 'data' => '', 'message' => $message);
            return $response;
        }

        $attachment_id = media_handle_upload('my_cv_upload', 0);
        if (!is_wp_error($attachment_id)) {


            $updated_resume = $attachment_id;
            $message = esc_html__("Uploaded successfully", 'nokri-rest-api');
        } else {
            $error_string = $attachment_id->get_error_message();
            $message = $error_string;
            $response = array('success' => false, 'data' => '', 'message' => $message);
            return $response;
        }

        $id = "" . $attachment_id;
        $response = array('success' => true, 'data' => $attachment_id, 'message' => $message, 'attach_id' => $id);

        return $response;
    }

}



add_action('rest_api_init', 'nokriAPI_canidate_comp_oprofile_hook', 0);

function nokriAPI_canidate_comp_oprofile_hook() {
    register_rest_route(
            'nokri/v1', '/canidate/comp_profile/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_canidate_comp_profile_get',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_comp_profile_get')) {

    function nokriAPI_canidate_comp_profile_get($request) {
        $data = array();




        $user_id = get_current_user_id();

        $update_personal_info = nokriAPI_canidate_update_personal_info($request);
        $data['update_personal_info'] = $update_personal_info;


        $certifications_get = nokriAPI_canidate_certifications_get();
        $data['certifications_get'] = $certifications_get;

        $certifications_get_more = nokriAPI_canidate_certifications_get_more();
        $data['certifications_get_more'] = $certifications_get_more;


        $education_get = nokriAPI_canidate_education_get();
        $data['education_get'] = $education_get;



        $education_get_more = nokriAPI_canidate_education_get_more();
        $data['education_get_more'] = $education_get;



        $profession_get = nokriAPI_canidate_professions_get();
        $data['profession_get'] = $profession_get;




        $profession_get_more = nokriAPI_canidate_profession_get_more();
        $data['profession_get_more'] = $profession_get_more;

        $skills_get = nokriAPI_canidate_update_skills($request);
        $data['skills_get '] = $skills_get;

        $portfolio_get = nokriAPI_canidate_portfolio_get();
        $data['portfolio_get'] = $portfolio_get;



        $social_link_get = nokriAPI_canidate_update_social_link($request);
        $data['social_link_get'] = $social_link_get;


        $update_location_get = nokriAPI_canidate_update_location($request);
        $data['update_location_get'] = $update_location_get;



        $resumes_list_get = nokriAPI_candidate_resumes($request);
        $data['resumes_list_get'] = $resumes_list_get;



        $scheduled_hours = nokriAPI_canidate_schedule_hours_fun($request);
        $data['scheduled_hours'] = $scheduled_hours;


        return $response = array('success' => true, 'data' => $data, 'message' => '', 'page_title' => '');
    }

}

add_action('rest_api_init', 'nokriAPI_canidate_post_paid_alert_hook', 0);

function nokriAPI_canidate_post_paid_alert_hook() {
    register_rest_route(
            'nokri/v1', '/candidate/post_paid_alert/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_canidate_paid_alert_fun',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

function nokriAPI_canidate_paid_alert_fun($request) {


    global $nokri;
    $user_id = get_current_user_id();
    $json_data = $request->get_json_params();
    $alert_name = (isset($json_data['alert_name'])) ? $json_data['alert_name'] : "";
    $alert_email = (isset($json_data['alert_email'])) ? $json_data['alert_email'] : "";
    $alert_category = (isset($json_data['alert_category'])) ? $json_data['alert_category'] : "";
    $alert_location = (isset($json_data['alert_location'])) ? $json_data['alert_location'] : "";
    $random_string = nokri_randomString(5);
    $alert_start = date("Y/m/d");
    $type = get_user_meta($user_id, '_sb_reg_type', true);
    /* demo check */
    /* Not cand */
    if ($type != '0') {
        return $response = array('success' => false, 'data' => '', 'message' => esc_html('Only candidate can do this', "nokri-rest-api"));
    }
    /* countries */
    $cand_alert = array();
    if ($alert_name != "") {
        $cand_alert['alert_name'] = $alert_name;
    }
    if ($alert_email != "") {
        $cand_alert['alert_email'] = $alert_email;
    }

    if ($alert_category != "") {
        $cand_alert['alert_category'] = $alert_category;
    }
    if ($alert_location != "") {
        $cand_alert['alert_location'] = $alert_location;
    }
    if ($alert_start != "") {
        $cand_alert['alert_start'] = $alert_start;
    }
    $my_alert = json_encode($cand_alert);
    update_user_meta($user_id, 'temp_test_alert', $my_alert);

    $product_id = isset($nokri['job_alert_package']) ? $nokri['job_alert_package'] : "";

    if ($product_id == "") {
        return $response = array('success' => false, 'data' => '', 'message' => esc_html('No package Selected', "nokri-rest-api"));
    }
    $product = wc_get_product($product_id);
    $name = $product->get_name();
    $price = $product->regular_price;
    $data = array();
    $data['pkgId'] = $product_id;
    $data['product'] = esc_html__('Product', 'nokri-rest-api');
    $data['productVal'] = $name;
    $data['price'] = esc_html__('Price', 'nokri-rest-api');
    $data['priceVal'] = $price;
    $data['buy'] = esc_html__('Buy', 'nokri-rest-api');
    $data['page_title'] = esc_html__('Alert Subscription', 'nokri-rest-api');

    return $response = array('success' => true, 'data' => $data, 'message' => esc_html('subscribed succesfully', "nokri-rest-api"));
}

/* * ******************************** */
/* emp saving candidate resume           */
/* * ******************************* */
add_action('rest_api_init', 'nokriAPI_candidate_resumes_video_hook', 0);

function nokriAPI_candidate_resumes_video_hook() {
    register_rest_route(
            'nokri/v1', '/candidate/cand_resumes_video', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_cand_resume_video',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

function nokriAPI_cand_resume_video($request) {

    $user_id = get_current_user_id();
    if ($user_id == "") {
        return $response = array('success' => false, 'data' => '', 'message' => esc_html__('Please login First', 'nokri-rest-api'));
    }
    $user_type = get_user_meta($user_id, '_sb_reg_type', true);

    if ($user_type != "0") {

        return $response = array('success' => false, 'data' => '', 'message' => esc_html__('Only Candidate Can do this', 'nokri-rest-api'));
    }
    global $nokri;
    require_once ABSPATH . 'wp-admin/includes/image.php';
    require_once ABSPATH . 'wp-admin/includes/file.php';
    require_once ABSPATH . 'wp-admin/includes/media.php';
    /* get files information */
    $vid_file_name = $_FILES['resume_video']['name'];
    $vid_file_size = $_FILES['resume_video']['size'];

    $vid_convert_to_mb = ($vid_file_size / 1000000);
    $vid_file_format = explode('.', $vid_file_name);
    $format_name = strtolower(end($vid_file_format));

    $allowed_format = ['mp4', 'm4v', 'avi', '3gp', 'mov'];

    if (!in_array($format_name, $allowed_format)) {

        $format_message = __("Format not supported , allowed format are", 'nokri-rest-api') . $format_name . "   " . "mp4,m4v,avi,3gp,mov";
        return $response = array('success' => false, 'data' => '', 'message' => esc_html($format_message));
    }

    /* max upload size in MB */
    $vid_actual_size = $nokri['cand_video_resume_limit'];


    /* Check file size */
    if ($vid_convert_to_mb > $vid_actual_size || $vid_convert_to_mb == 0) {
        $max_size = __("Max allowd video size in MB is", 'nokri-rest-api') . " " . $vid_actual_size;
        return $response = array('success' => false, 'data' => '', 'message' => esc_html($max_size));
    }
    /* get already attachment ids */
    $store_vid_ids = '';
    $store_vid_ids_arr = array();
    $store_vid_ids = get_user_meta($user_id, 'cand_video_resumes', true);

    $attachment_id = media_handle_upload('resume_video', 0);

    $previous_video = get_user_meta($user_id, 'cand_video_resumes', true);
    if (is_numeric($previous_video)) {
        wp_delete_attachment($previous_video);
    }
    if (!is_wp_error($attachment_id)) {

        $previous_video = get_user_meta($user_id, 'cand_video_resumes', true);
        if (is_numeric($previous_video)) {
            wp_delete_attachment($previous_video);
        }


        update_user_meta($user_id, 'cand_video_resumes', $attachment_id);
        return $response = array('success' => true, 'data' => $attachment_id, 'message' => esc_html('Uploaded succesfully', 'nokri-rest-api'));
    } else {
        $message = isset($attachment_id->errors['upload_error'][0]) ? $attachment_id->errors['upload_error'][0] : __("Something went wrong please try later", 'nokri');
        return $response = array('success' => false, 'data' => '', 'message' => $message);
        die();
    }
}

add_action('rest_api_init', 'nokriAPI_canidate_schedule_hours', 0);

function nokriAPI_canidate_schedule_hours() {
    register_rest_route(
            'nokri/v1', '/canidate/schedule_hours/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_canidate_schedule_hours_fun',
        'permission_callback' => function () {
            return
                    nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_schedule_hours_fun')) {

    function nokriAPI_canidate_schedule_hours_fun($request, $return_arr = false, $user_id = '') {

        global $nokriAPI;
        global $nokri;

        if ($user_id != "") {
            $user_crnt_id = $user_id;
        } else {
            $user_crnt_id = get_current_user_id();
        }

        $data = array();
        $days = array();

        $is_scheduled_hours = isset($nokri['cand_hours_swicth']) ? $nokri['cand_hours_swicth'] : false;

        $listing_timezone = get_user_meta($user_crnt_id, 'nokri_user_timezone', true);
        if ($is_scheduled_hours) {

            $selected_val = get_user_meta($user_crnt_id, 'nokri_business_hours', true);
            $allow_hours = get_user_meta($user_crnt_id, 'nokri_is_hours_allow', true);
            $selected_val = get_user_meta($user_crnt_id, 'nokri_business_hours', true);

            if (function_exists('nokri_fetch_business_hours')) {
                if ($return_arr) {
                    if (!empty(nokri_fetch_business_hours($user_crnt_id, true))) {
                        $days = nokri_fetch_business_hours($user_crnt_id, true);
                    } else if ($selected_val != "") {
                        $dayss = nokri_week_days(true);
                        foreach ($dayss as $key => $val) {
                            $days[] = array("day_name" => $val, "start_time" => '', "end_time" => '', "closed" => false);
                        }
                    }
                } else {

                    if (!empty(nokri_fetch_business_hours($user_crnt_id, true))) {
                        $days = nokri_fetch_business_hours($user_crnt_id, true);
                    } else {
                        $dayss = nokri_week_days(true);
                        foreach ($dayss as $key => $val) {
                            $days[] = array("day_name" => $val, "start_time" => '', "end_time" => '', "closed" => false);
                        }
                    }
                }
            }
            $zones = ["Africa/Abidjan", "Africa/Accra", "Africa/Addis_Ababa", "Africa/Algiers", "Africa/Asmara", "Africa/Bamako", "Africa/Bangui", "Africa/Banjul", "Africa/Bissau", "Africa/Blantyre", "Africa/Brazzaville", "Africa/Bujumbura", "Africa/Cairo", "Africa/Casablanca", "Africa/Ceuta", "Africa/Conakry", "Africa/Dakar", "Africa/Dar_es_Salaam", "Africa/Djibouti", "Africa/Douala", "Africa/El_Aaiun", "Africa/Freetown", "Africa/Gaborone", "Africa/Harare", "Africa/Johannesburg", "Africa/Juba", "Africa/Kampala", "Africa/Khartoum", "Africa/Kigali", "Africa/Kinshasa", "Africa/Lagos", "Africa/Libreville", "Africa/Lome", "Africa/Luanda", "Africa/Lubumbashi", "Africa/Lusaka", "Africa/Malabo", "Africa/Maputo", "Africa/Maseru", "Africa/Mbabane", "Africa/Mogadishu", "Africa/Monrovia", "Africa/Nairobi", "Africa/Ndjamena", "Africa/Niamey", "Africa/Nouakchott", "Africa/Ouagadougou", "Africa/Porto-Novo", "Africa/Sao_Tome", "Africa/Tripoli", "Africa/Tunis", "Africa/Windhoek", "America/Adak", "America/Anchorage", "America/Anguilla", "America/Antigua", "America/Araguaina", "America/Argentina/Buenos_Aires", "America/Argentina/Catamarca", "America/Argentina/Cordoba", "America/Argentina/Jujuy", "America/Argentina/La_Rioja", "America/Argentina/Mendoza", "America/Argentina/Rio_Gallegos", "America/Argentina/Salta", "America/Argentina/San_Juan", "America/Argentina/San_Luis", "America/Argentina/Tucuman", "America/Argentina/Ushuaia", "America/Aruba", "America/Asuncion", "America/Atikokan", "America/Bahia", "America/Bahia_Banderas", "America/Barbados", "America/Belem", "America/Belize", "America/Blanc-Sablon", "America/Boa_Vista", "America/Bogota", "America/Boise", "America/Cambridge_Bay", "America/Campo_Grande", "America/Cancun", "America/Caracas", "America/Cayenne", "America/Cayman", "America/Chicago", "America/Chihuahua", "America/Costa_Rica", "America/Creston", "America/Cuiaba", "America/Curacao", "America/Danmarkshavn", "America/Dawson", "America/Dawson_Creek", "America/Denver", "America/Detroit", "America/Dominica", "America/Edmonton", "America/Eirunepe", "America/El_Salvador", "America/Fort_Nelson", "America/Fortaleza", "America/Glace_Bay", "America/Godthab", "America/Goose_Bay", "America/Grand_Turk", "America/Grenada", "America/Guadeloupe", "America/Guatemala", "America/Guayaquil", "America/Guyana", "America/Halifax", "America/Havana", "America/Hermosillo", "America/Indiana/Indianapolis", "America/Indiana/Knox", "America/Indiana/Marengo", "America/Indiana/Petersburg", "America/Indiana/Tell_City", "America/Indiana/Vevay", "America/Indiana/Vincennes", "America/Indiana/Winamac", "America/Inuvik", "America/Iqaluit", "America/Jamaica", "America/Juneau", "America/Kentucky/Louisville", "America/Kentucky/Monticello", "America/Kralendijk", "America/La_Paz", "America/Lima", "America/Los_Angeles", "America/Lower_Princes", "America/Maceio", "America/Managua", "America/Manaus", "America/Marigot", "America/Martinique", "America/Matamoros", "America/Mazatlan", "America/Menominee", "America/Merida", "America/Metlakatla", "America/Mexico_City", "America/Miquelon", "America/Moncton", "America/Monterrey", "America/Montevideo", "America/Montserrat", "America/Nassau", "America/New_York", "America/Nipigon", "America/Nome", "America/Noronha", "America/North_Dakota/Beulah", "America/North_Dakota/Center", "America/North_Dakota/New_Salem", "America/Ojinaga", "America/Panama", "America/Pangnirtung", "America/Paramaribo", "America/Phoenix", "America/Port-au-Prince", "America/Port_of_Spain", "America/Porto_Velho", "America/Puerto_Rico", "America/Punta_Arenas", "America/Rainy_River", "America/Rankin_Inlet", "America/Recife", "America/Regina", "America/Resolute", "America/Rio_Branco", "America/Santarem", "America/Santiago", "America/Santo_Domingo", "America/Sao_Paulo", "America/Scoresbysund", "America/Sitka", "America/St_Barthelemy", "America/St_Johns", "America/St_Kitts", "America/St_Lucia", "America/St_Thomas", "America/St_Vincent", "America/Swift_Current", "America/Tegucigalpa", "America/Thule", "America/Thunder_Bay", "America/Tijuana", "America/Toronto", "America/Tortola", "America/Vancouver", "America/Whitehorse", "America/Winnipeg", "America/Yakutat", "America/Yellowknife", "Antarctica/Casey", "Antarctica/Davis", "Antarctica/DumontDUrville", "Antarctica/Macquarie", "Antarctica/Mawson", "Antarctica/McMurdo", "Antarctica/Palmer", "Antarctica/Rothera", "Antarctica/Syowa", "Antarctica/Troll", "Antarctica/Vostok", "Arctic/Longyearbyen", "Asia/Aden", "Asia/Almaty", "Asia/Amman", "Asia/Anadyr", "Asia/Aqtau", "Asia/Aqtobe", "Asia/Ashgabat", "Asia/Atyrau", "Asia/Baghdad", "Asia/Bahrain", "Asia/Baku", "Asia/Bangkok", "Asia/Barnaul", "Asia/Beirut", "Asia/Bishkek", "Asia/Brunei", "Asia/Chita", "Asia/Choibalsan", "Asia/Colombo", "Asia/Damascus", "Asia/Dhaka", "Asia/Dili", "Asia/Dubai", "Asia/Dushanbe", "Asia/Famagusta", "Asia/Gaza", "Asia/Hebron", "Asia/Ho_Chi_Minh", "Asia/Hong_Kong", "Asia/Hovd", "Asia/Irkutsk", "Asia/Jakarta", "Asia/Jayapura", "Asia/Jerusalem", "Asia/Kabul", "Asia/Kamchatka", "Asia/Karachi", "Asia/Kathmandu", "Asia/Khandyga", "Asia/Kolkata", "Asia/Krasnoyarsk", "Asia/Kuala_Lumpur", "Asia/Kuching", "Asia/Kuwait", "Asia/Macau", "Asia/Magadan", "Asia/Makassar", "Asia/Manila", "Asia/Muscat", "Asia/Nicosia", "Asia/Novokuznetsk", "Asia/Novosibirsk", "Asia/Omsk", "Asia/Oral", "Asia/Phnom_Penh", "Asia/Pontianak", "Asia/Pyongyang", "Asia/Qatar", "Asia/Qyzylorda", "Asia/Riyadh", "Asia/Sakhalin", "Asia/Samarkand", "Asia/Seoul", "Asia/Shanghai", "Asia/Singapore", "Asia/Srednekolymsk", "Asia/Taipei", "Asia/Tashkent", "Asia/Tbilisi", "Asia/Tehran", "Asia/Thimphu", "Asia/Tokyo", "Asia/Tomsk", "Asia/Ulaanbaatar", "Asia/Urumqi", "Asia/Ust-Nera", "Asia/Vientiane", "Asia/Vladivostok", "Asia/Yakutsk", "Asia/Yangon", "Asia/Yekaterinburg", "Asia/Yerevan", "Atlantic/Azores", "Atlantic/Bermuda", "Atlantic/Canary", "Atlantic/Cape_Verde", "Atlantic/Faroe", "Atlantic/Madeira", "Atlantic/Reykjavik", "Atlantic/South_Georgia", "Atlantic/St_Helena", "Atlantic/Stanley", "Australia/Adelaide", "Australia/Brisbane", "Australia/Broken_Hill", "Australia/Currie", "Australia/Darwin", "Australia/Eucla", "Australia/Hobart", "Australia/Lindeman", "Australia/Lord_Howe", "Australia/Melbourne", "Australia/Perth", "Australia/Sydney", "Europe/Amsterdam", "Europe/Andorra", "Europe/Astrakhan", "Europe/Athens", "Europe/Belgrade", "Europe/Berlin", "Europe/Bratislava", "Europe/Brussels", "Europe/Bucharest", "Europe/Budapest", "Europe/Busingen", "Europe/Chisinau", "Europe/Copenhagen", "Europe/Dublin", "Europe/Gibraltar", "Europe/Guernsey", "Europe/Helsinki", "Europe/Isle_of_Man", "Europe/Istanbul", "Europe/Jersey", "Europe/Kaliningrad", "Europe/Kiev", "Europe/Kirov", "Europe/Lisbon", "Europe/Ljubljana", "Europe/London", "Europe/Luxembourg", "Europe/Madrid", "Europe/Malta", "Europe/Mariehamn", "Europe/Minsk", "Europe/Monaco", "Europe/Moscow", "Europe/Oslo", "Europe/Paris", "Europe/Podgorica", "Europe/Prague", "Europe/Riga", "Europe/Rome", "Europe/Samara", "Europe/San_Marino", "Europe/Sarajevo", "Europe/Saratov", "Europe/Simferopol", "Europe/Skopje", "Europe/Sofia", "Europe/Stockholm", "Europe/Tallinn", "Europe/Tirane", "Europe/Ulyanovsk", "Europe/Uzhgorod", "Europe/Vaduz", "Europe/Vatican", "Europe/Vienna", "Europe/Vilnius", "Europe/Volgograd", "Europe/Warsaw", "Europe/Zagreb", "Europe/Zaporozhye", "Europe/Zurich", "Indian/Antananarivo", "Indian/Chagos", "Indian/Christmas", "Indian/Cocos", "Indian/Comoro", "Indian/Kerguelen", "Indian/Mahe", "Indian/Maldives", "Indian/Mauritius", "Indian/Mayotte", "Indian/Reunion", "Pacific/Apia", "Pacific/Auckland", "Pacific/Bougainville", "Pacific/Chatham", "Pacific/Chuuk", "Pacific/Easter", "Pacific/Efate", "Pacific/Enderbury", "Pacific/Fakaofo", "Pacific/Fiji", "Pacific/Funafuti", "Pacific/Galapagos", "Pacific/Gambier", "Pacific/Guadalcanal", "Pacific/Guam", "Pacific/Honolulu", "Pacific/Kiritimati", "Pacific/Kosrae", "Pacific/Kwajalein", "Pacific/Majuro", "Pacific/Marquesas", "Pacific/Midway", "Pacific/Nauru", "Pacific/Niue", "Pacific/Norfolk", "Pacific/Noumea", "Pacific/Pago_Pago", "Pacific/Palau", "Pacific/Pitcairn", "Pacific/Pohnpei", "Pacific/Port_Moresby", "Pacific/Rarotonga", "Pacific/Saipan", "Pacific/Tahiti", "Pacific/Tarawa", "Pacific/Tongatapu", "Pacific/Wake", "Pacific/Wallis", "UTC"];
            $zones_data = array();
            $selected = "";
            foreach ($zones as $zone) {
                $selected = false;
                if ($listing_timezone == $zone) {
                    $selected = true;
                }

                $zones_data[] = array("key" => $zone, "value" => $zone, "selected" => $selected, "has_child" => "");
            }

            $extra = array();


            $status_type = nokri_business_hours_status($user_crnt_id);


            if ($status_type == 0) {
                $business_hours_status = esc_html__('Closed', 'nokri-rest-');
            } else {
                $business_hours_status = esc_html__('Open Now', 'nokri-rest-api');
            }


            $data['hours_type'] = $selected_val;
            $data['status'] = $business_hours_status;
            $data['zones'] = $zones_data;
            $data['days'] = $days;
            $extra = array();
            $extra['cand_availability'] = esc_html__('Candidate Availability', 'nokri-rest-api');
            $extra['selected_hours'] = esc_html__('Selected Hours', 'nokri-rest-api');
            $extra['time_zone'] = esc_html__('Time Zone', 'nokri-rest-api');
            $extra['submit'] = esc_html__('Submit', 'nokri-rest-api');
            $extra['to'] = esc_html__('To', 'nokri-rest-api');
            $extra['open'] = esc_html__('Open 24/7', 'nokri-rest-api');
            $extra['selective_hours'] = esc_html__('Selective Hours', 'nokri-rest-api');
            $extra['not_available'] = esc_html__('Not Available', 'nokri-rest-api');
            $extra['open_now'] = esc_html__('Open Now', 'nokri-rest-api');
            $extra['cloes_now'] = esc_html__('Closed', 'nokri-rest-api');
            $extra['schedule_hours'] = esc_html__('Scheduled Hours', 'nokri-rest-api');
            $data['extra'] = $extra;


            if ($return_arr) {
                $data['zones'] = $listing_timezone;
                return $data;
            } else {
                return $response = array('success' => true, 'data' => $data, 'message' => "");
            }
        } else {

            if ($return_arr) {
                return $data;
            }

            return $response = array('success' => false, 'data' => $data, 'message' => "");
        }
    }

}

add_action('rest_api_init', 'nokriAPI_canidate_schedule_hours_post', 0);

function nokriAPI_canidate_schedule_hours_post() {
    register_rest_route(
            'nokri/v1', '/candidate/schedule_hours_post/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_canidate_schedule_hours_post_fun',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_schedule_hours_post_fun')) {

    function nokriAPI_canidate_schedule_hours_post_fun($request) {

        $json_data = $request->get_json_params();
        $user_id = get_current_user_id();

        if ($user_id == 0 || $user_id == "") {
            return;
        }

        $listing_is_open = isset($json_data['hour_type']) ? ($json_data['hour_type']) : "";
        $listing_timezone = isset($json_data['zones']) ? sanitize_text_field($json_data['zones']) : "";

        $days_data = isset($json_data['days']) ? $json_data['days'] : array();
        $end_from = $start_from = $is_closed = array();


        if (!empty($days_data)) {
            foreach ($days_data as $day) {
                $is_closed [] = $day['closed'];
                $start_from [] = $day['end_time'];
                $end_from [] = $day['start_time'];
            }
        }
        if ($listing_is_open == 1) {
            update_user_meta($user_id, 'nokri_is_hours_allow', '1');
            update_user_meta($user_id, 'nokri_business_hours', $listing_is_open);
        }
        // listing business hours
        else if ($listing_is_open == 2) {
            //business hours
            $custom_days = array('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday');
            for ($a = 0; $a <= 6; $a++) {
                $to = '';
                $from = '';
                $days = '';
                //get days
                $days = lcfirst($custom_days[$a]);
                $from = date("H:i:s", strtotime(str_replace(" : ", ":", $end_from[$a])));
                $to = date("H:i:s", strtotime(str_replace(" : ", ":", $start_from[$a])));
                //day status open or not 
                $close_day = "0";
                if ($is_closed[$a]) {

                    $close_day = "1";
                }
                update_user_meta($user_id, '_timingz_' . $days . '_open', $close_day);

                //day hours from
                update_user_meta($user_id, '_timingz_' . $days . '_from', $from);
                update_user_meta($user_id, '_timingz_' . $days . '_to', $to);
            }
            update_user_meta($user_id, 'nokri_business_hours', 2);
            update_user_meta($user_id, 'nokri_user_timezone', $listing_timezone);
            update_user_meta($user_id, 'nokri_is_hours_allow', '1');
        } else {
            update_user_meta($user_id, 'nokri_is_hours_allow', '0');
            update_user_meta($user_id, 'nokri_business_hours', '0');
        }

        return array('success' => true, 'message' => esc_html__('Hours saveed succesfully', 'nokri'));
    }

}


if (!function_exists('nokri_get_candidate_rating')) {

    function nokri_get_candidate_rating($user_id, $all = false, $paged = 1) {
        global $nokri;
        $reviews_title = isset($nokri['cand_write_reviews_title']) ? $nokri['cand_write_reviews_title'] : "";
        $first_rating = isset($nokri['cand_first_rating_stars_title']) ? $nokri['cand_first_rating_stars_title'] : "";
        $second_rating = isset($nokri['cand_second_rating_stars_title']) ? $nokri['cand_second_rating_stars_title'] : "";
        $third_rating = isset($nokri['cand_third_rating_stars_title']) ? $nokri['cand_third_rating_stars_title'] : "";
        $page_title = isset($nokri['sb_cand_reviews_title']) ? $nokri['sb_cand_reviews_title'] : "";
        $data = array();
        $extras = array();
        $extras['reviews_title'] = $reviews_title;
        $extras['first_rating'] = $first_rating;
        $extras['second_rating'] = $second_rating;
        $extras['third_rating'] = $third_rating;
        $extras['title_review'] = esc_html__('Review Title', 'nokri-rest-api');
        $extras['your_review'] = esc_html__('Your Review', 'nokri-rest-api');
        $extras['cand_id'] = $user_id;
        $extras['login_first'] = esc_html__('Please Login First', 'nokri-rest-api');
        $extras['enter_title'] = esc_html__('Please Enter Title', 'nokri-rest-api');
        $extras['enter_message'] = esc_html__('Please Enter Messsage', 'nokri-rest-api');
        $extras['submit'] = esc_html__('submit', 'nokri-rest-api');
        $extras['page_title'] = esc_html__($page_title, 'nokri-rest-api');
        $extras['reply_btn_text'] = esc_html__('Reply', 'nokri-rest-api');
        $extras['cancel_btn'] = esc_html__('Cancel', 'nokri-rest-api');
        $extras['add_review'] = esc_html__('Add Review', 'nokri-rest-api');
        $extras ['view_all'] = esc_html__('View All reviews', 'nokri-rest-api');

        $limit = isset($nokri['cand_reviews_count_limit']) ? $nokri['cand_reviews_count_limit'] : 5;

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
                if (get_user_meta($get_ratings->comment_post_ID, '_sb_user_pic', true) != "") {
                    $attach_id = get_user_meta($user_id, '_sb_user_pic', true);
                    if (is_numeric($attach_id)) {
                        $image_link = wp_get_attachment_image_src($attach_id, 'nokri_job_post_single');
                    } else {
                        $image_link[0] = $attach_id;
                    }
                }

                $reply_text = get_comment_meta($comment_ids, '_rating_reply', true);

                $has_reply = false;
                if ($reply_text != "") {

                    $has_reply = true;
                }

                $reply_text = get_comment_meta($comment_ids, '_rating_reply', true);

                $review[$counter]['cid'] = $comment_ids;
                $review[$counter]['_rating_service'] = $service_stars;
                $review[$counter]['_rating_proces'] = $process_stars;
                $review[$counter]['_rating_selection'] = $selection_stars;
                $review[$counter]['_rating_avg'] = $single_avg;
                $review[$counter]['_rating_title'] = esc_html($comment_title);
                $review[$counter]['_rating_description'] = strip_tags_content($get_ratings->comment_content);
                $review[$counter]['_rating_poster'] = $comment_poster_name;
                $review[$counter]['_rating_date'] = date_i18n(get_option('date_format'), strtotime($get_ratings->comment_date));
                $review[$counter]['can_reply'] = $is_reply;
                $review[$counter]['reply_text'] = $reply_text;
                $review[$counter]['has_reply'] = $has_reply;
                $review[$counter]['reply_heading'] = esc_html__('Candidate reply', 'nokri-rest-api');
                $review[$counter]['emp_image'] = $image_link[0];
                $counter++;
            }
        }
        $data['reviews_data'] = $review;
        $data['extra'] = $extras;
        return $data;
    }

}

add_action('rest_api_init', 'nokriAPI_canidate_get_rating', 0);

function nokriAPI_canidate_get_rating() {
    register_rest_route(
            'nokri/v1', '/candidate/get_ratings/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_canidate_get_rating_fun',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_get_rating_fun')) {

    function nokriAPI_canidate_get_rating_fun($request) {
        $json_data = $request->get_json_params();

        $user_id = isset($json_data['user_id']) ? $json_data['user_id'] : "";

        $paged = 1;
        if (get_query_var('paged')) {
            $paged = get_query_var('paged');
        } else if (isset($json_data['page_number'])) {
            $paged = $json_data['page_number'];
        } else {
            $paged = 1;
        }
        $reviews_data = nokri_get_candidate_rating($user_id, true, $paged);
        $data = array();
        $data['user_reviews'] = $reviews_data;
        return array('success' => true, 'data' => $data, 'message' => '');
    }

}

add_action('rest_api_init', 'nokriAPI_canidate_post_rating', 0);

function nokriAPI_canidate_post_rating() {
    register_rest_route(
            'nokri/v1', '/candidate/post_ratings/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_canidate_post_rating_fun',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_post_rating_fun')) {

    function nokriAPI_canidate_post_rating_fun($request) {

        $json_data = $request->get_json_params();
        $rating_service_stars = $json_data['rating_service'];
        $rating_process_stars = $json_data['rating_process'];
        $rating_selection_stars = $json_data['rating_selection'];
        $review_title = $json_data['review_title'];
        $review_message = $json_data['review_message'];
        $employer_id = $json_data['cand_id'];

        $current_user = wp_get_current_user();
        $cand = get_current_user_id();
        $data = array();
        $user_type = get_user_meta($cand, '_sb_reg_type', true);

        if ($employer_id == $cand) {
            return array('success' => false, 'data' => $data, 'message' => esc_html__("You can not rate yourself", 'nokri-rest-api'));
            die();
        }


        if ($user_type == 0) {
            return array('success' => false, 'data' => $data, 'message' => esc_html__("Only employer can do this", 'nokri-rest-api'));
            die();
        }

        if (get_user_meta($employer_id, '_is_rated_' . $cand, true) == $cand) {
            return array('success' => false, 'data' => $data, 'message' => esc_html__("You have already rated this user", 'nokri-rest-api'));
            die();
        } else {
            $time = current_time('mysql');
            $data = array(
                'comment_post_ID' => $cand,
                'comment_author' => $current_user->display_name,
                'comment_author_email' => $current_user->user_email,
                'comment_author_url' => '',
                'comment_content' => sanitize_text_field($review_message),
                'comment_type' => 'dealer_review',
                'comment_parent' => 0,
                'user_id' => $employer_id,
                'comment_author_IP' => $_SERVER['REMOTE_ADDR'],
                'comment_date' => $time,
                'comment_approved' => 0,
            );
            $comment_id = wp_insert_comment($data);
            update_comment_meta($comment_id, '_rating_service', sanitize_text_field($rating_service_stars));
            update_comment_meta($comment_id, '_rating_proces', sanitize_text_field($rating_process_stars));
            update_comment_meta($comment_id, '_rating_selection', sanitize_text_field($rating_selection_stars));
            update_comment_meta($comment_id, '_rating_title', sanitize_text_field($review_title));
            update_user_meta($employer_id, '_is_rated_' . $cand, $cand);

            $total_stars = $rating_service_stars + $rating_process_stars + $rating_selection_stars;
            $ratting = round($total_stars / "3", 1);
            // Send email if enabled
            if (isset($nokri['email_to_user_on_rating']) && $nokri['email_to_user_on_rating']) {
                //nokri_send_email_new_rating( $cand, $employer_id, $ratting, $review_message );
            }
            return array('success' => true, 'data' => $data, 'message' => esc_html__("You have succesfully rated this user", 'nokri-rest-api'));
            die();
        }
    }

}


add_action('rest_api_init', 'nokriAPI_canidate_post_reply', 0);

function nokriAPI_canidate_post_reply() {
    register_rest_route(
            'nokri/v1', '/user_post_reply/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_canidate_post_reply_fun',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_canidate_post_reply_fun')) {

    function nokriAPI_canidate_post_reply_fun($request) {
        $json_data = $request->get_json_params();
        $data = array();
        if (get_current_user_id() == "") {
            $message = esc_html__("You are not logged in.", 'nokri-rest-api');

            return array('success' => false, 'data' => $data, 'message' => $message);
            die();
        }
        $comment_id = isset($json_data['cid']) ? $json_data['cid'] : "";
        $reply_text = isset($json_data['reply_text']) ? $json_data['reply_text'] : "";
        if ($comment_id != "" && $reply_text != "") {
            if (!get_comment_meta($comment_id, '_rating_reply', true)) {
                update_comment_meta($comment_id, '_rating_reply', sanitize_text_field($reply_text));

                return array('success' => true, 'data' => $data, 'message' => esc_html__("Your reply posted.", 'nokri-rest-api'));
                die();
            } else {
                return array('success' => false, 'data' => $data, 'message' => esc_html__("Already Replied", 'nokri-rest-api'));
                die();
            }
        } else {
            return array('success' => false, 'data' => $data, 'message' => esc_html__("comment id or reply is missing", 'nokri-rest-api'));
            die();
        }
    }

}