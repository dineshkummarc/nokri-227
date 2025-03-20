<?php

add_action('rest_api_init', 'nokriAPI_get_job_post_hook', 0);

function nokriAPI_get_job_post_hook() {
    /* Get Job Post */
    register_rest_route(
            'nokri/v1', '/employer/job_post/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_get_job_post',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
    /* Update Job */
    register_rest_route(
            'nokri/v1', '/employer/job_update/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_get_job_post',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
    /* Job Post */
    register_rest_route(
            'nokri/v1', '/employer/job_post/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_post_job_now',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_get_job_post')) {

    function nokriAPI_get_job_post($request) {
        global $nokriAPI;
        global $nokri;
        /* Is only admin can post */
        $is_admin_post = isset($nokri['job_post_for_admin']) ? $nokri['job_post_for_admin'] : true;
        $is_admin_post_msg = isset($nokri['job_post_for_admin_message']) ? $nokri['job_post_for_admin_message'] : '';
        if ($is_admin_post == '1') {
            return $response = array('success' => false, 'data' => '', 'message' => $is_admin_post_msg);
        }
        if (!is_super_admin(get_current_user_id()))
            $json_data = $request->get_json_params();
        $is_update = (isset($json_data['is_update']) && $json_data['is_update'] != "" ) ? $json_data['is_update'] : '';
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);

        $tagline = (isset($nokriAPI['hom_sec_tagline'])) ? $nokriAPI['hom_sec_tagline'] : "";
        $headline = (isset($nokriAPI['hom_sec_headline'])) ? $nokriAPI['hom_sec_headline'] : "";
        $key_wrd_hdng = (isset($nokriAPI['hom_sec_key_word_heading'])) ? $nokriAPI['hom_sec_key_word_heading'] : "";
        if ($is_update == '') {
            /* Check jobs and package validity */
            $message = nokriAPI_check_jobs_validity();
            if ($message == 0) {
                return $response = array('success' => false, 'data' => '', 'message' => __("Purchase package", "nokri-rest-api"));
            } elseif ($message == 1) {
                return $response = array('success' => false, 'data' => '', 'message' => __("Purchase package", "nokri-rest-api"));
            } else {
                
            }
        }
        $pid = get_user_meta($user_id, 'ad_in_progress', true);
       
        if ($is_update != "") {
            $pid = $is_update;
        } else if (get_post_status($pid) && $pid != "") {
            $pid = $pid;
        } else {
            // Gather post data.
            $my_post = array('post_status' => 'pending', 'post_author' => $user_id, 'post_type' => 'job_post');
            $id = wp_insert_post($my_post);
            $pid = $id;
            if ($id) {
                update_user_meta($user_id, 'ad_in_progress', $id);
                update_post_meta($id, '_job_status', 'active');
            }
        }
        
        $restrict_update = false;
        if ($is_update != "") {
            
            /* Displaying Jobs Selected Values */
            $tags_array = wp_get_object_terms($pid, 'job_tags', array('fields' => 'names'));
            $tags = implode(',', $tags_array);
            $post = get_post($pid);
            $description = $post->post_content;
            $title = $post->post_title;
            $description = get_the_excerpt($post->ID);
            $deadline = get_post_meta($pid, '_job_date', true);
            $type = get_post_meta($pid, '_job_type', true);
            $jlevel = get_post_meta($pid, '_job_level', true);
            $shift = get_post_meta($pid, '_job_shift', true);
            $experience = get_post_meta($pid, '_job_experience', true);
            $skills = get_post_meta($pid, '_job_skills', true);
            $salary = get_post_meta($pid, '_job_salary', true);
            $salary_type = get_post_meta($pid, '_job_salary_type', true);
            $qualifications = get_post_meta($pid, '_job_qualifications', true);
            $currency = get_post_meta($pid, '_job_currency', true);
            $mapLocation = get_post_meta($pid, '_job_address', true);
            $map_lat = get_post_meta($pid, '_job_lat', true);
            $map_long = get_post_meta($pid, '_job_long', true);
            $job_posts = get_post_meta($pid, '_job_posts', true);


            /* Getting Jobs Categories Level */
            $cats = nokri_get_jobs_cats($pid);
            $level = count($cats);
            /* Make cats selected on update Job */
            $ad_cats = nokriAPI_get_cats('job_category', 0);
            $cats_html = $sub_cats_html = $sub_sub_cats_html = $sub_sub_sub_cats_html = array();
            foreach ($ad_cats as $ad_cat) {
                $children = get_terms($ad_cat->taxonomy, array('parent' => $ad_cat->term_id, 'hide_empty' => false));
                $has_child = ($children) ? true : false;
                if ($level > 0 && $ad_cat->term_id == $cats[0]['id']) {
                    $selected = true;
                } else {
                    $selected = false;
                }
                $cats_html[] = array("key" => $ad_cat->term_id, "value" => esc_html($ad_cat->name), "selected" => $selected, "has_child" => $has_child);
            }
            if ($level >= 2) {
                $ad_sub_cats = nokri_get_cats('job_category', $cats[0]['id']);
                $sub_cats_html = array();
                foreach ($ad_sub_cats as $ad_cat) {
                    $children = get_terms($ad_cat->taxonomy, array('parent' => $ad_cat->term_id, 'hide_empty' => false));
                    $has_child = ($children) ? true : false;

                    if ($level > 0 && $ad_cat->term_id == $cats[1]['id']) {
                        $selected = true;
                    } else {
                        $selected = false;
                    }
                    $sub_cats_html[] = array("key" => $ad_cat->term_id, "value" => esc_html($ad_cat->name), "selected" => $selected, "has_child" => $has_child);
                }
            }
            if ($level >= 3) {
                $ad_sub_sub_cats = nokri_get_cats('job_category', $cats[1]['id']);
                $sub_sub_cats_html = array();
                foreach ($ad_sub_sub_cats as $ad_cat) {
                    $children = get_terms($ad_cat->taxonomy, array('parent' => $ad_cat->term_id, 'hide_empty' => false));
                    $has_child = ($children) ? true : false;
                    if ($level > 0 && $ad_cat->term_id == $cats[2]['id']) {
                        $selected = true;
                    } else {
                        $selected = false;
                    }
                    $sub_sub_cats_html[] = array("key" => $ad_cat->term_id, "value" => esc_html($ad_cat->name), "selected" => $selected, "has_child" => $has_child);
                }
            }
            if ($level >= 4) {
                $ad_sub_sub_sub_cats = nokri_get_cats('job_category', $cats[2]['id']);
                $sub_sub_sub_cats_html = array();
                foreach ($ad_sub_sub_sub_cats as $ad_cat) {
                    $children = get_terms($ad_cat->taxonomy, array('parent' => $ad_cat->term_id, 'hide_empty' => false));
                    $has_child = ($children) ? true : false;
                    if ($level > 0 && $ad_cat->term_id == $cats[3]['id']) {
                        $selected = true;
                    } else {
                        $selected = false;
                    }
                    $sub_sub_sub_cats_html[] = array("key" => $ad_cat->term_id, "value" => esc_html($ad_cat->name), "selected" => $selected, "has_child" => $has_child);
                }
            }
            //Countries
            $countries = nokri_get_jobs_cats($pid, '', true);
            $levelz = count($countries);
            /* Make cats selected on update ad */
            $ad_countries = nokriAPI_get_cats('ad_location', 0);
            $country_html = $country_states = $country_cities = $country_towns = array();
            $country_html = array();
            foreach ($ad_countries as $ad_country) {
                $children = get_terms($ad_country->taxonomy, array('parent' => $ad_country->term_id, 'hide_empty' => false));
                $has_child = ($children) ? true : false;
                if ($levelz > 0 && $ad_country->term_id == $countries[0]['id']) {
                    $selected = true;
                } else {
                    $selected = false;
                }
                $country_html[] = array("key" => $ad_country->term_id, "value" => esc_html($ad_country->name), "selected" => $selected, "has_child" => $has_child);
            }
            if ($levelz >= 2) {
                $ad_states = nokriAPI_get_cats('ad_location', $countries[0]['id']);
                foreach ($ad_states as $ad_state) {
                    $children = get_terms($ad_state->taxonomy, array('parent' => $ad_state->term_id, 'hide_empty' => false));
                    $has_child = ($children) ? true : false;
                    if ($levelz > 0 && $ad_state->term_id == $countries[1]['id']) {
                        $selected = true;
                    } else {
                        $selected = false;
                    }
                    $country_states[] = array("key" => $ad_state->term_id, "value" => esc_html($ad_state->name), "selected" => $selected, "has_child" => $has_child);
                }
            }
            if ($levelz >= 3) {
                $ad_country_cities = nokriAPI_get_cats('ad_location', $countries[1]['id']);
                foreach ($ad_country_cities as $ad_city) {
                    $children = get_terms($ad_city->taxonomy, array('parent' => $ad_city->term_id, 'hide_empty' => false));
                    $has_child = ($children) ? true : false;
                    if ($levelz > 0 && $ad_city->term_id == $countries[2]['id']) {
                        $selected = true;
                    } else {
                        $selected = false;
                    }
                    $country_cities[] = array("key" => $ad_city->term_id, "value" => esc_html($ad_city->name), "selected" => $selected, "has_child" => $has_child);
                }
            }
            if ($levelz >= 4) {
                $ad_country_town = nokriAPI_get_cats('ad_location', $countries[2]['id']);
                foreach ($ad_country_town as $ad_town) {
                    $children = get_terms($ad_town->taxonomy, array('parent' => $ad_town->term_id, 'hide_empty' => false));
                    $has_child = ($children) ? true : false;
                    if ($levelz > 0 && $ad_town->term_id == $countries[3]['id']) {
                        $selected = true;
                    } else {
                        $selected = false;
                    }
                    $country_towns[] = array("key" => $ad_town->term_id, "value" => esc_html($ad_town->name), "selected" => $selected, "has_child" => $has_child);
                }
            }
           
           
            $is_restricted = isset($nokri['restrict_job_update']) ? $nokri['restrict_job_update'] : false; 
                           
  
            if ($is_restricted) {
                           
       
                $update_days = isset($nokri['days_of_jobs_update']) ? $nokri['days_of_jobs_update'] : 5;
                $publish_job_date = get_the_time('Y-m-d', $is_update);
                $update_limit_date = date('Y-m-d', strtotime($publish_job_date . " + $update_days days"));
                $update_limit_date = strtotime($update_limit_date);
                   
                $today = date("Y-m-d");
                $today_string = strtotime($today);
                
                if ($today_string > $update_limit_date) {
                    $restrict_update = true;
                }
            }
                   
        } else {
            $cats_html = $sub_cats_html = $sub_sub_cats_html = $sub_sub_sub_cats_html = array();
            $country_html = $country_states = $country_cities = $country_cities = $country_towns = array();
            $tags = $post = $description = $title = $deadline = $type = $jlevel = $shift = $skills = $salary = $salary_type = $qualifications = $currency = $mapLocation = $map_lat = $map_long = $job_posts = $experience = '';
            $ad_cats = nokriAPI_get_cats('job_category', 0);
            $cats_html = array();
            foreach ($ad_cats as $ad_cat) {
                $children = get_terms($ad_cat->taxonomy, array('parent' => $ad_cat->term_id, 'hide_empty' => false));
                $has_child = ($children) ? true : false;
                $cats_html[] = array("key" => $ad_cat->term_id, "value" => esc_html($ad_cat->name), "has_child" => $has_child);
            }
            //Countries
            $ad_country = nokri_get_cats('ad_location', 0);
            $country_html = array();
            foreach ($ad_country as $ad_count) {
                $children = get_terms($ad_count->taxonomy, array('parent' => $ad_count->term_id, 'hide_empty' => false));
                $has_child = ($children) ? true : false;
                $country_html[] = array("key" => $ad_count->term_id, "value" => esc_html($ad_count->name), "has_child" => $has_child);
            }
        }
        /* Category headings */
        $job_cat_1 = ( isset($nokriAPI['API_job_cat_level_1']) && $nokriAPI['API_job_cat_level_1'] != "" ) ? $nokriAPI['API_job_cat_level_1'] : '';
        $job_cat_2 = ( isset($nokriAPI['API_job_cat_level_2']) && $nokriAPI['API_job_cat_level_2'] != "" ) ? $nokriAPI['API_job_cat_level_2'] : '';
        $job_cat_3 = ( isset($nokriAPI['API_job_cat_level_3']) && $nokriAPI['API_job_cat_level_3'] != "" ) ? $nokriAPI['API_job_cat_level_3'] : '';
        $job_cat_4 = ( isset($nokriAPI['API_job_cat_level_4']) && $nokriAPI['API_job_cat_level_4'] != "" ) ? $nokriAPI['API_job_cat_level_4'] : '';
        /* Location headings */
        $loc_section_heading = ( isset($nokriAPI['API_job_country_level_heading']) && $nokriAPI['API_job_country_level_heading'] != "" ) ? $nokriAPI['API_job_country_level_heading'] : '';
        $job_country_level_1 = ( isset($nokriAPI['API_job_country_level_1']) && $nokriAPI['API_job_country_level_1'] != "" ) ? $nokriAPI['API_job_country_level_1'] : '';
        $job_country_level_2 = ( isset($nokriAPI['API_job_country_level_2']) && $nokriAPI['API_job_country_level_2'] != "" ) ? $nokriAPI['API_job_country_level_2'] : '';
        $job_country_level_3 = ( isset($nokriAPI['API_job_country_level_3']) && $nokriAPI['API_job_country_level_3'] != "" ) ? $nokriAPI['API_job_country_level_3'] : '';
        $job_country_level_4 = ( isset($nokriAPI['API_job_country_level_4']) && $nokriAPI['API_job_country_level_4'] != "" ) ? $nokriAPI['API_job_country_level_4'] : '';
        $map_heading = ( isset($nokriAPI['API_job_map_heading_txt']) && $nokriAPI['API_job_map_heading_txt'] != "" ) ? $nokriAPI['API_job_map_heading_txt'] : '';
        $job_apply_with_option = isset($nokri['job_apply_with']) ? $nokri['job_apply_with'] : "0";
        $data['job_id'] = $pid;

        $has_template = false;
        if (isset($nokri['job_post_form']) && $nokri['job_post_form'] == '1') {
            $has_template = true;
        }
        $data['job_form'] = $has_template;

        $basic_info = __("Basic information", "nokri-rest-api");
        $data['basic_info'] = nokriAPI_canidate_fields($basic_info, '', 'textfield', true, 2, 'basic_info');

        $job_title = __("Job Title", "nokri-rest-api");
        $data['job_title'] = nokriAPI_canidate_fields($job_title, $title, 'textfield', true, 2, 'job_title');


        $job_desc = __("Job Details", "nokri-rest-api");
        $data['job_desc'] = nokriAPI_canidate_fields($job_desc, $description, 'textfield', true, 2, 'job_desc');

        $job_category = $job_cat_1;
        $data['job_category'] = nokriAPI_canidate_fields($job_category, $cats_html, 'textfield', true, 2, 'job_category');

        $job_sub_category = $job_cat_2;
        $data['job_sub_category'] = nokriAPI_canidate_fields($job_sub_category, $sub_cats_html, 'textfield', true, 2, 'job_sub_category');

        $job_sub_sub_category = $job_cat_3;
        $data['job_sub_sub_category'] = nokriAPI_canidate_fields($job_sub_sub_category, $sub_sub_cats_html, 'textfield', true, 2, 'job_sub_sub_category');

        $job_sub_sub_sub_category = $job_cat_4;
        $data['job_sub_sub_sub_category'] = nokriAPI_canidate_fields($job_sub_sub_sub_category, $sub_sub_sub_cats_html, 'textfield', true, 2, 'job_sub_sub_sub_category');

        $job_description = __("Job Description", "nokri-rest-api");
        $data['job_description'] = nokriAPI_canidate_fields($job_description, '', 'textfield', true, 2, 'job_description');

        $job_deadline = __("Application Deadline", "nokri-rest-api");
        $data['job_deadline'] = nokriAPI_canidate_fields($job_deadline, $deadline, 'textfield', true, 2, 'job_deadline');


        $shown = 1;
        if ($job_apply_with_option == "1") {

            $shown = 2;
        }
        $data['job_apply_with'] = nokriAPI_canidate_fields('Apply With Link', nokriAPI_job_external_links($pid, $job_apply_with_option), 'textfield', true, $shown, 'job_apply_with');


        $job_qualifications = __("Job Qualifications", "nokri-rest-api");
        $data['job_qualifications'] = nokriAPI_canidate_fields($job_qualifications, nokriAPI_job_post_taxonomies('job_qualifications', $qualifications), 'textfield', true, 2, 'job_qualifications');

        $job_type = __("Job Type", "nokri-rest-api");
        $data['job_type'] = nokriAPI_canidate_fields($job_type, nokriAPI_job_post_taxonomies('job_type', $type), 'textfield', true, 2, 'job_type');


        $salary_type1 = __("Salary Type", "nokri-rest-api");
        $data['salary_type'] = nokriAPI_canidate_fields($salary_type1, nokriAPI_job_post_taxonomies('job_salary_type', $salary_type), 'textfield', true, 2, 'salary_type');

        $salary_currency = __("Salary Currency", "nokri-rest-api");
        $data['salary_currency'] = nokriAPI_canidate_fields($salary_currency, nokriAPI_job_post_taxonomies('job_currency', $currency), 'textfield', true, 2, 'salary_currency');

        $salary_offer = __("Salary Offer", "nokri-rest-api");
        $data['salary_offer'] = nokriAPI_canidate_fields($salary_offer, nokriAPI_job_post_taxonomies('job_salary', $salary), 'textfield', true, 2, 'salary_offer');

        $job_experience = __("Job Experience", "nokri-rest-api");
        $data['job_experience'] = nokriAPI_canidate_fields($job_experience, nokriAPI_job_post_taxonomies('job_experience', $experience), 'textfield', true, 2, 'job_experience');

        $job_shift = __("Job Shift", "nokri-rest-api");
        $data['job_shift'] = nokriAPI_canidate_fields($job_shift, nokriAPI_job_post_taxonomies('job_shift', $shift), 'textfield', true, 2, 'job_shift');

        $job_level = __("Job Level", "nokri-rest-api");
        $data['job_level'] = nokriAPI_canidate_fields($job_level, nokriAPI_job_post_taxonomies('job_level', $jlevel), 'textfield', true, 2, 'job_level');


        $job_no_pos = __("Number Of Posts", "nokri-rest-api");
        $data['job_no_pos'] = nokriAPI_canidate_fields($job_no_pos, $job_posts, 'textfield', true, 2, 'job_no_pos');

        $job_skills = __("Job Skills", "nokri-rest-api");
        $data['job_skills'] = nokriAPI_canidate_fields($job_skills, nokriAPI_job_selected_skills('job_skills', '_job_skills', $skills), 'textfield', true, 2, 'job_skills');

        $job_tags = __("Job Tags", "nokri-rest-api");
        $data['job_tags'] = nokriAPI_canidate_fields($job_tags, $tags, 'textfield', true, 2, 'job_skills');


        $job_location = $loc_section_heading;
        $data['job_location'] = nokriAPI_canidate_fields($job_location, '', 'textfield', true, 2, '');


        $job_country = $job_country_level_1;
        $data['job_country'] = nokriAPI_canidate_fields($job_country, $country_html, 'textfield', true, 2, 'job_country');

        $data['job_state'] = nokriAPI_canidate_fields($job_country_level_2, $country_states, 'textfield', true, 2, 'job_state');

        $data['job_city'] = nokriAPI_canidate_fields($job_country_level_3, $country_cities, 'textfield', true, 2, 'job_city');


        $job_town = $job_country_level_4;
        $data['job_town'] = nokriAPI_canidate_fields($job_town, $country_towns, 'textfield', true, 2, 'job_town');

        $exp_limit = 0;
        $exp_check = isset($nokri['job_exp_limit_switch']) ? $nokri['job_exp_limit_switch'] : false;
        if ($exp_check) {

            $exp_limit = isset($nokri['job_exp_limit']) ? $nokri['job_exp_limit'] : 0;
        }


        $data['expiry_limit'] = nokriAPI_canidate_fields('', (int) $exp_limit, 'textfield', true, 2, 'expiry_limit');

        $job_location_head = $map_heading;
        $data['job_location_head'] = nokriAPI_canidate_fields($job_location_head, '', 'textfield', true, 2, 'job_location_head');



        $job_loc = __("Set Your Location", "nokri-rest-api");
        $data['job_loc'] = nokriAPI_canidate_fields($job_loc, $mapLocation, 'textfield', true, 2, 'job_loc');

        $job_lat = __("Latitude", "nokri-rest-api");
        $data['job_lat'] = nokriAPI_canidate_fields($job_lat, $map_lat, 'textfield', true, 2, 'job_lat');

        $job_long = __("Longitude", "nokri-rest-api");
        $data['job_long'] = nokriAPI_canidate_fields($job_long, $map_long, 'textfield', true, 2, 'job_long');

        $job_page_title = __("Job Post", "nokri-rest-api");
        $data['job_page_title'] = nokriAPI_canidate_fields($job_page_title, '', 'textfield', true, 2, 'job_page_title');

        /* Premium Jobs */
        if (get_user_meta(get_current_user_id(), '_sb_expire_ads', true) != '') {
            $job_boost = __("Boost your job with adons", "nokri-rest-api");
            $data['job_boost'] = nokriAPI_canidate_fields($job_boost, '', 'textfield', true, true, 'job_boost');
            $job_classes = get_terms(array('taxonomy' => 'job_class', 'hide_empty' => false,));
            foreach ($job_classes as $job_class) {
                $term_id = $job_class->term_id;
                $job_class_user_meta = get_user_meta(get_current_user_id(), 'package_job_class_' . $term_id, true);
                $emp_class_check = get_term_meta($job_class->term_id, 'emp_class_check', true);
                $job_class_checked = wp_get_post_terms($pid, 'job_class', array("fields" => "names"));
                if (in_array($job_class->name, $job_class_checked)) {
                    $is_chekced = true;
                } else {
                    $is_chekced = false;
                }
                if ($job_class_user_meta > 0 || $job_class_user_meta == '-1' && $emp_class_check != 1) {
                    $job = __("Jobs ", "nokri-rest-api");
                    $rem = __("Remaining", "nokri-rest-api");
                    if ($job_class_user_meta == '-1') {
                        $job_class_user_meta = __("Unlimited", "nokri-rest-api");
                    }
                    $data['premium_jobs'][] = nokriAPI_canidate_fields(esc_html($job_class->name) . " " . $job, $job_class_user_meta, $rem, $is_chekced, true, $term_id);
                }
            }
        }
        $firbase_api_key = isset($nokriAPI['api_firebase_id']) && $nokriAPI['api_firebase_id'] != '' ? $nokriAPI['api_firebase_id'] : '';
        $data['home_screen']['img'] = nokritAPI_home_secreen_bg();
        $data['home_screen']['logo'] = nokriAPI_appLogo();
        $data['home_screen']['tagline'] = $tagline;
        $data['home_screen']['heading'] = $headline;
        $data['home_screen']['key_wrd_headng'] = $key_wrd_hdng;
        $data['home_screen']['api_firebase_key'] = $firbase_api_key;

        $job_post_btn = __("Post Job", "nokri-rest-api");
        $question_switch = $nokri['allow_questinares'];
        $question_lable = nokri_feilds_label('question_label', esc_html__('Job Question', 'nokri-rest-api'));
        $question_placeholder = nokri_feilds_label('question_plc', esc_html__('Job Question', 'nokri-rest-api'));
        $rem_question_txt = nokri_feilds_label('question_rem_btn', esc_html__('Remove', 'nokri-rest-api'));
        $add_question_txt = nokri_feilds_label('question_ad_btn', esc_html__('Add More', 'nokri-rest-api'));
        $data['job_post_btn'] = nokriAPI_canidate_fields($job_post_btn, '', 'textfield', true, 2, 'job_post_btn');
        $job_questions = get_post_meta($pid, '_job_questions', true);
        $job_questions = !empty($job_questions) ? $job_questions : array();
        $data['job_questions'] = $job_questions;
        $data['questionnair_sec']['questionnaire_switch'] = $question_switch;

        if ($question_switch) {
            $data['questionnair_sec']['sec_heading'] = esc_html__('Add Questionnaire', 'nokri-rest-api');
            $data['questionnair_sec']['btn_yes'] = esc_html__('Yes', 'nokri-rest-api');
            $data['questionnair_sec']['btn_no'] = esc_html__('No', 'nokri-rest-api');
            $data['questionnair_sec']['question_lable'] = $question_lable;
            $data['questionnair_sec']['question_plc'] = $question_placeholder;
            $data['questionnair_sec']['question_add'] = $add_question_txt;
            $data['questionnair_sec']['question_remove'] = $rem_question_txt;
            $data['questionnair_sec']['question_removed'] = esc_html__('Removed', 'nokri-rest-api');
            $data['questionnair_sec']['question_added'] = esc_html__('Question added', 'nokri-rest-api');
            $data['questionnair_sec']['questions_added'] = esc_html__('Questions added', 'nokri-rest-api');
        }
        $data['video_url_heading'] = esc_html__('Add Youtube video Url', 'nokri-rest-api');
        $data['job_post_txt'] = esc_html__('Post Job', 'nokri-rest-api');
        
        
        $data['is_restrict'] = $restrict_update ;
        return $response = array('success' => true, 'data' => $data, 'message' => '');
    }

}
/* ================ */
/*   Job Post Now  */
/* =============== */
if (!function_exists('nokriAPI_post_job_now')) {

    function nokriAPI_post_job_now($request) {
        global $nokriAPI;
        $json_data = $request->get_json_params();
        //NOKRI_API_ALLOW_EDITING


        $job_mail_link = (isset($json_data['external_email'])) ? ($json_data['external_email']) : '';
        $job_exter_link = (isset($json_data['external_link'])) ? ($json_data['external_link']) : '';
        $job_exter_whatsapp = (isset($json_data['whatsapp'])) ? ($json_data['whatsapp']) : '';


        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }
        $is_update = (isset($json_data['is_update']) && $json_data['is_update'] != "" ) ? $json_data['is_update'] : '';
        $job_id = (isset($json_data['job_id']) && $json_data['job_id'] != "" ) ? $json_data['job_id'] : '';
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        //$jobs_available =  nokriAPI_check_jobs_validity();
        $ad_status = 'publish';
        if ($is_update != "" && $job_id != '') {
            if ($nokriAPI['sb_update_approval'] == 'manual') {
                $ad_status = 'pending';
            }
            $pid = $is_update;
            delete_user_meta($user_id, 'ad_in_progress');
        } else {
            if ($nokriAPI['sb_ad_approval'] == 'manual') {
                $ad_status = 'pending';
            }
            $pid = get_user_meta($user_id, 'ad_in_progress', true);
            /* Now user can post new Job */
            delete_user_meta($user_id, 'ad_in_progress');
            /* Getting User Simple Jobs Informations */
            $simple_job_term_id = nokriAPI_simple_jobs();
            $meta_name = 'package_job_class_' . $simple_job_term_id;
            $simple_ads = get_user_meta(get_current_user_id(), $meta_name, true);
            if ($simple_ads > 0 && !is_super_admin(get_current_user_id())) {
                $simple_ads = $simple_ads - 1;
                update_user_meta($user_id, $meta_name, $simple_ads);
            }
            update_post_meta($pid, '_nokri_ad_status_', 'active');
        }
        $custom_fields = isset($json_data['custom_fields']) ? (array) $json_data['custom_fields'] : array();
        $job_questions = isset($json_data['job_qstns']) ? $json_data['job_qstns'] : array();

        $request_from = nokriAPI_getSpecific_headerVal('Nokri-Request-From');
        if ($request_from == 'ios') {
            $custom_fields = json_decode(@$json_data['custom_fields'], true);
        }


        //if($is_update != "" ){delete_user_meta($user_id, 'ad_in_progress');}
        /* Updating Job Title */
        $title = (isset($json_data['job_title'])) ? trim($json_data['job_title']) : '';
        /* Getting  Categories */
        $job_cat = (isset($json_data['job_cat'])) ? trim($json_data['job_cat']) : '';
        $job_cat_second = (isset($json_data['job_cat_second'])) ? trim($json_data['job_cat_second']) : '';
        $job_cat_third = (isset($json_data['job_cat_third'])) ? trim($json_data['job_cat_third']) : '';
        $job_cat_forth = (isset($json_data['job_cat_forth'])) ? trim($json_data['job_cat_forth']) : '';
        /* Setting Categories */
        
        $categories = array();
        if ($job_cat != "") {
            $categories[] = $job_cat;
        }
        if ($job_cat_second != "") {
            $categories[] = $job_cat_second;
        }
        if ($job_cat_third != "") {
            $categories[] = $job_cat_third;
        }
        if ($job_cat_forth != "") {
            $categories[] = $job_cat_forth;
        }
       
        wp_set_post_terms($pid, $categories, 'job_category');
        
        
        
        
        
        /* Getting countries */
        $job_country = (isset($json_data['job_country'])) ? trim($json_data['job_country']) : '';
        $job_states = (isset($json_data['job_states'])) ? trim($json_data['job_states']) : '';
        $job_cities = (isset($json_data['job_cities'])) ? trim($json_data['job_cities']) : '';
        $job_towns = (isset($json_data['job_town'])) ? trim($json_data['job_town']) : '';
        /* Setting countries */
        $countries = array();
        if ($job_country != "") {
            $countries[] = $job_country;
        }
        if ($job_states != "") {
            $countries[] = $job_states;
        }
        if ($job_cities != "") {
            $countries[] = $job_cities;
        }
        if ($job_towns != "") {
            $countries[] = $job_towns;
        }
        wp_set_post_terms($pid, $countries, 'ad_location');
        /* Getting Tags */






        $job_type = (isset($json_data['job_type'])) ? trim($json_data['job_type']) : '';

        $job_qualif = (isset($json_data['job_qualifications'])) ? trim($json_data['job_qualifications']) : '';
        $job_level = (isset($json_data['job_level'])) ? trim($json_data['job_level']) : '';
        $job_salary = (isset($json_data['job_salary'])) ? trim($json_data['job_salary']) : '';
        $job_salary_type = (isset($json_data['job_salary_type'])) ? trim($json_data['job_salary_type']) : '';
        $job_skills = (isset($json_data['job_skills'])) ? ($json_data['job_skills']) : array();
        $job_experience = (isset($json_data['job_experience'])) ? trim($json_data['job_experience']) : '';
        $job_currency = (isset($json_data['job_currency'])) ? trim($json_data['job_currency']) : '';
        $job_shift = (isset($json_data['job_shift'])) ? trim($json_data['job_shift']) : '';
        $job_posts = (isset($json_data['job_posts'])) ? trim($json_data['job_posts']) : '';
        $job_video = (isset($json_data['job_video'])) ? ($json_data['job_video']) : "";

        $job_tags = (isset($json_data['job_tags'])) ? trim($json_data['job_tags']) : '';




        /* Setting Tags */



        $job_title = (isset($json_data['job_title'])) ? trim($json_data['job_title']) : '';
        $job_description = (isset($json_data['job_description'])) ? trim($json_data['job_description']) : '';

        $job_date = (isset($json_data['job_date'])) ? trim($json_data['job_date']) : '';
        $job_address = (isset($json_data['job_address'])) ? trim($json_data['job_address']) : '';
        $job_lat = (isset($json_data['job_lat'])) ? trim($json_data['job_lat']) : '';
        $job_long = (isset($json_data['job_long'])) ? trim($json_data['job_long']) : '';
        $job_class_checked = (isset($json_data['class_type_value'])) ? ($json_data['class_type_value']) : array();


        //nokri_get_notify_on_ad_post($pid);
        /* Getting Other Jobs Informations */
        if ((array) $job_class_checked && count($job_class_checked) > 0) {
            foreach ($job_class_checked as $job_class) {
                $no_of_jobs = get_user_meta(get_current_user_id(), 'package_job_class_' . $job_class, true);
                if ($no_of_jobs > 0) {
                    $no_of_jobs = $no_of_jobs - 1;
                    update_user_meta(get_current_user_id(), 'package_job_class_' . $job_class, $no_of_jobs);
                    update_post_meta($pid, 'package_job_class_' . $job_class, $job_class);
                    wp_set_post_terms($pid, $job_class_checked, 'job_class', true);
                }
            }
        }
        /* Bad words filteration */
        $words = explode(',', $nokriAPI['bad_words_filter']);
        $replace = $nokriAPI['bad_words_replace'];
        $desc = nokri_badwords_filter($words, $job_description, $replace);
        $title = nokri_badwords_filter($words, $job_title, $replace);
        $my_post = array(
            'ID' => $pid,
            'post_title' => $title,
            'post_status' => $ad_status,
            'post_content' => $desc,
            'post_name' => $title
        );
        wp_update_post($my_post);
        /*         * **************************** */
        /*  Dynamic feilds update      */
        /*         * **************************** */

        if (isset($custom_fields) && count($custom_fields) > 0) {
            $ios_array = array();
            foreach ($custom_fields as $key => $data) {
                if (is_array($data)) {
                    $dataArr = array();
                    foreach ($data as $k)
                        $dataArr[] = $k;
                    // $data = stripslashes(json_encode($dataArr, true));
                }

                $dataVal = $data;
                if ($key != "job_skills") {
                    $dataVal = ltrim($data, ",");
                }
                $dataKey = "_nokri_tpl_field_" . $key;
                $$key = $dataVal;

                $arr[] = $dataVal;
                update_post_meta($pid, $dataKey, $dataVal);
            }
        }

        // return $custom_fields;
        /*         * **************************** */
        /* setting taxonomoies Post Meta */
        /*         * **************************** */



        if ($job_date != '') {
            update_post_meta($pid, '_job_date', $job_date);
        }
        if ($job_type != "") {
            update_post_meta($pid, '_job_type', $job_type);
            wp_set_post_terms($pid, $job_type, 'job_type');
        }
        if ($job_level != "") {
            update_post_meta($pid, '_job_level', $job_level);
            wp_set_post_terms($pid, $job_level, 'job_level');
        }
        if ($job_shift != "") {
            update_post_meta($pid, '_job_shift', $job_shift);
            wp_set_post_terms($pid, $job_shift, 'job_shift');
        }
        if ($job_experience != "") {
            update_post_meta($pid, '_job_experience', $job_experience);
            wp_set_post_terms($pid, $job_experience, 'job_experience');
        }
        if ($job_skills != "") {
            update_post_meta($pid, '_job_skills', $job_skills);
            wp_set_post_terms($pid, $job_skills, 'job_skills');
        }
        if ($job_salary != "") {
            update_post_meta($pid, '_job_salary', $job_salary);
            wp_set_post_terms($pid, $job_salary, 'job_salary');
        }
        if ($job_salary_type != "") {
            update_post_meta($pid, '_job_salary_type', $job_salary_type);
            wp_set_post_terms($pid, $job_salary_type, 'job_salary_type');
        }
        if ($job_qualif != "") {
            update_post_meta($pid, '_job_qualifications', $job_qualif);
            wp_set_post_terms($pid, $job_qualif, 'job_qualifications');
        }
        if ($job_currency != "") {
            update_post_meta($pid, '_job_currency', $job_currency);
            wp_set_post_terms($pid, $job_currency, 'job_currency');
        }
        if ($job_posts != "") {
            update_post_meta($pid, '_job_posts', $job_posts);
        }
        if ($job_address != '') {
            update_post_meta($pid, '_job_address', $job_address);
            wp_set_post_terms($pid, $job_address, 'job_address');
        }
        if ($job_lat != '') {
            update_post_meta($pid, '_job_lat', $job_lat);
        }
        if ($job_long != '') {
            update_post_meta($pid, '_job_long', $job_long);
        }

        $tags = explode(',', $job_tags);
        wp_set_object_terms($pid, $tags, 'job_tags');

        $job_mail_link = (isset($json_data['external_email'])) ? ($json_data['external_email']) : '';
        $job_exter_link = (isset($json_data['external_link'])) ? ($json_data['external_link']) : '';
        $job_exter_whatsapp = (isset($json_data['whatsapp'])) ? ($json_data['whatsapp']) : '';

        if ($job_mail_link !== '') {
            update_post_meta($pid, '_job_apply_mail', $job_mail_link);
            update_post_meta($pid, '_job_apply_with', 'mail');
        } else if ($job_exter_link != '') {
            update_post_meta($pid, '_job_apply_url', $job_exter_link);
            update_post_meta($pid, '_job_apply_with', 'exter');
        } else if ($job_exter_whatsapp != "") {

            update_post_meta($pid, '_job_apply_whatsapp', $job_exter_whatsapp);
            update_post_meta($pid, '_job_apply_with', 'whatsapp');
        } else {
            update_post_meta($pid, '_job_apply_with', 'inter');
        }




        $questions_sanatize = array();
        if (isset($job_questions) && !empty($job_questions)) {
            foreach ($job_questions as $key) {
                if (!empty($key)) {
                    $questions_sanatize[] = sanitize_text_field($key);
                }
            }
            update_post_meta($pid, '_job_questions', ($questions_sanatize));
        }
        update_post_meta($pid, '_job_video', $job_video);

        $pid = get_user_meta($user_id, 'ad_in_progress', true);
        if (get_post_status($pid) && $pid != "") {
            $my_post = array(
                'ID' => $pid,
                'post_title' => $title
            );
            wp_update_post($my_post);
        }




        /* Jobs Status */
        update_post_meta($pid, '_job_status', 'active');
        get_the_permalink($pid);
        $message = __("Job Posted Successfully", "nokri-rest-api");
        return $response = array('success' => true, 'data' => '', 'message' => $message, "extra" => $json_data);
    }

}
/* * *************************************** */
/* Getting Job Selected Skills For Job */
/* * *************************************** */
if (!function_exists('nokriAPI_job_selected_skills')) {

    function nokriAPI_job_selected_skills($taxonomy_name = '', $meta_key = '', $job_skills = '') {
        $taxonomies = get_terms($taxonomy_name, array('hide_empty' => false, 'orderby' => 'id', 'order' => 'ASC', 'parent' => 0));
        $option = array();
        if (count($taxonomies) > 0) {
            $selected = '';
            foreach ($taxonomies as $taxonomy) {
                if ($job_skills != '') {
                    if (in_array($taxonomy->term_id, $job_skills)) {
                        $selected = true;
                    } else {
                        $selected = false;
                    }
                }
                $option[] = array(
                    "key" => $taxonomy->term_id,
                    "value" => esc_html($taxonomy->name),
                    "selected" => $selected
                );
            }
        }
        return $option;
    }

}
/* * ******************************** */
/* Getting All Sub Level Categories */
/* * ******************************** */
if (!function_exists('nokriAPI_get_job_cats_level')) {

    function nokriAPI_get_job_cats_level($id, $by = 'name') {
        $post_categories = wp_get_object_terms($id, 'job_category', array('orderby' => 'term_group'));
        $cats = array();
        foreach ($post_categories as $c) {
            $cat = get_category($c);
            $cats[] = array('name' => $cat->name, 'id' => $cat->term_id);
        }
        return $cats;
    }

}
/* * *************************************** */
/* Getting Job Selected Categories       */
/* * *************************************** */
if (!function_exists('nokriAPI_get_cats')) {

    function nokriAPI_get_cats($taxonomy = 'category', $parent_of = 0, $child_of = 0) {
        $defaults = array(
            'taxonomy' => $taxonomy,
            'orderby' => 'name',
            'order' => 'ASC',
            'hide_empty' => 0,
            'exclude' => array(),
            'exclude_tree' => array(),
            'number' => '',
            'offset' => '',
            'fields' => 'all',
            'name' => '',
            'slug' => '',
            'hierarchical' => true,
            'search' => '',
            'name__like' => '',
            'description__like' => '',
            'pad_counts' => false,
            'get' => '',
            'child_of' => $child_of,
            'parent' => $parent_of,
            'childless' => false,
            'cache_domain' => 'core',
            'update_term_meta_cache' => true,
            'meta_query' => ''
        );
        return get_terms($defaults);
    }

}
/* * ******************************* */
// check permission for job posting //
/* * ******************************* */
if (!function_exists('nokriAPI_check_jobs_validity')) {

    function nokriAPI_check_jobs_validity() {
        global $nokri;
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        /* Check Employer Have Free Jobs */
        $job_class_free = nokriAPI_simple_jobs();
        $regular_jobs = get_user_meta($user_id, 'package_job_class_' . $job_class_free, true);
        $expiry_date = get_user_meta($user_id, '_sb_expire_ads', true);
        $today = date("Y-m-d");
        /* For expiry packages */
        if ($today > $expiry_date && $expiry_date != '-1') {
            $message = 0;
        }
        /* For expiry regular jobs */ elseif ($regular_jobs == 0 && $expiry_date != '-1') {
            $message = 1;
        }
        /* For success */ else if ($expiry_date == '-1') {
            $message = 2;
        }
        /* For success */ else {
            $message = 2;
        }
        return $message;
    }

}
/* * **************************** */
/* Employer Getting Simple Jobs */
/* * ************************** */
if (!function_exists('nokriAPI_simple_jobs')) {

    function nokriAPI_simple_jobs() {
        $args = array(
            'taxonomy' => 'job_class',
            'order' => 'ASC',
            'hide_empty' => false,
            'hierarchical' => false,
            'parent' => 0,
        );
        $job_terms = get_terms($args);
        /* Getting Simple Job Class Value */
        $simple_job_id = '';
        foreach ($job_terms as $job_term) {
            if (get_term_meta($job_term->term_id, 'emp_class_check', true) == '1') {
                $simple_job_id = $job_term->term_id;
                break;
            }
        }
        return $simple_job_id;
    }

}
/* * ********************************** */
/*   Getting Child Categories        */
/* * ********************************** */
add_action('rest_api_init', 'nokriAPI_get_child_cats_hook', 0);

function nokriAPI_get_child_cats_hook() {
    register_rest_route(
            'nokri/v1', '/child_cats/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_get_child_cats',
        'permission_callback' => function () {
            return true;
        },
            )
    );
}

if (!function_exists('nokriAPI_get_child_cats')) {

    function nokriAPI_get_child_cats($request) {
        $json_data = $request->get_json_params();
        $cat_id = (isset($json_data['cat_id'])) ? trim($json_data['cat_id']) : '';
        $taxonomy = 'job_category';
        $terms = get_terms(array(
            'taxonomy' => $taxonomy,
            'hide_empty' => false,
            'parent' => $cat_id,
        ));
        $option = array();
        foreach ($terms as $term) {
            $children = get_terms($term->taxonomy, array('parent' => $term->term_id, 'hide_empty' => false));
            $has_child = ($children) ? true : false;
            $option[] = array("key" => $term->term_id, "value" => esc_html($term->name), "has_child" => $has_child);
        }
        return $option;
    }

}
/* * ********************************** */
/*   Getting Country City States       */
/* * ********************************** */
add_action('rest_api_init', 'nokriAPI_get_city_state_hook', 0);

function nokriAPI_get_city_state_hook() {
    register_rest_route(
            'nokri/v1', '/city_state/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_get_city_state',
        'permission_callback' => function () {
            return true;
        },
            )
    );
}

if (!function_exists('nokriAPI_get_city_state')) {

    function nokriAPI_get_city_state($request) {
        $json_data = $request->get_json_params();
        $country_id = (isset($json_data['country_id'])) ? trim($json_data['country_id']) : '';
        $taxonomy = 'ad_location';
        $terms = get_terms(array(
            'taxonomy' => $taxonomy,
            'hide_empty' => false,
            'parent' => $country_id,
        ));
        $option = array();
        foreach ($terms as $term) {
            $children = get_terms($term->taxonomy, array('parent' => $term->term_id, 'hide_empty' => false));
            $has_child = ($children) ? true : false;

            $option[] = array("key" => $term->term_id, "value" => esc_html($term->name), "has_child" => $has_child);
        }
        return $option;
    }

}
/* =============================== */
/* Getting Job Post Countries */
/* =============================== */
if (!function_exists('nokriAPI_get_job_country')) {

    function nokriAPI_get_job_country($id, $by = 'name') {
        $post_countries = wp_get_object_terms($id, array('ad_location'), array('orderby' => 'term_group'));
        $countries = array();
        foreach ($post_countries as $country) {
            $related_result = get_category($country);
            $countries[] = array('name' => $related_result->name, 'id' => $related_result->term_id);
        }
        return $countries;
    }

}
/* * ********************************** */
/*    Single View Job Service        */
/* * ********************************** */
add_action('rest_api_init', 'nokriAPI_view_job_hook', 0);

function nokriAPI_view_job_hook() {
    register_rest_route(
            'nokri/v1', '/view_job/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_view_job',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_view_job')) {

    function nokriAPI_view_job($request) {

        global $nokri;
        $user_id = get_current_user_id();
        $user = get_userdata($user_id);
        $json_data = $request->get_json_params();
        $job_id = (isset($json_data['job_id'])) ? trim($json_data['job_id']) : '';
        $job_categories = wp_get_object_terms($job_id, array('job_category'), array('orderby' => 'term_group'));
        /* Getting Job Details */
        $job_title = nokriAPI_convert_uniText(get_the_title($job_id));
        $content_post = get_post($job_id);
        $content = $content_post->post_content;
        $job_deadline_n = get_post_meta($job_id, '_job_date', true);
        $job_type = wp_get_post_terms($job_id, 'job_type', array("fields" => "ids"));
        $job_type = isset($job_type[0]) ? $job_type[0] : '';
        $job_salary = wp_get_post_terms($job_id, 'job_salary', array("fields" => "ids"));
        $job_salary = isset($job_salary[0]) ? $job_salary[0] : '';
        $job_salary_type = wp_get_post_terms($job_id, 'job_salary_type', array("fields" => "ids"));
        $job_salary_type = isset($job_salary_type[0]) ? $job_salary_type[0] : '';
        $job_currency = wp_get_post_terms($job_id, 'job_currency', array("fields" => "ids"));
        $job_currency = isset($job_currency[0]) ? $job_currency[0] : '';
        $job_qualifications = wp_get_post_terms($job_id, 'job_qualifications', array("fields" => "ids"));
        $job_qualifications = isset($job_qualifications[0]) ? $job_qualifications[0] : '';
        $job_experience = wp_get_post_terms($job_id, 'job_experience', array("fields" => "ids"));
        $job_experience = isset($job_experience[0]) ? $job_experience[0] : '';
        $job_shift = wp_get_post_terms($job_id, 'job_shift', array("fields" => "ids"));
        $job_shift = isset($job_shift[0]) ? $job_shift[0] : '';
        $job_level = wp_get_post_terms($job_id, 'job_level', array("fields" => "ids"));
        $job_level = isset($job_level[0]) ? $job_level[0] : '';
        $countries_last = nokri_job_categories_with_chlid_no_href($job_id, 'ad_location');
        $ad_mapLocation = get_post_meta($job_id, '_job_address', true);
        $ad_map_lat = get_post_meta($job_id, '_job_lat', true);
        $ad_map_long = get_post_meta($job_id, '_job_long', true);
        $job_phone = get_post_meta($job_id, '_job_phone', true);
        $job_video = get_post_meta($job_id, '_job_video', true);
        $job_vacancy = get_post_meta($job_id, '_job_posts', true);
        $cats = nokriAPI_get_ad_cats($job_id, 'ID');
        $post_date = get_the_date(' M j ,Y', $job_id);
        //apply with          

        $apply_with = get_post_meta($job_id, '_job_apply_with', true);
        $apply_with = !empty($apply_with) ? $apply_with : 'inter';
        $apply_with_url = get_post_meta($job_id, '_job_apply_url', true);

        if ($apply_with == 'whatsapp') {
            $apply_with_url = get_post_meta($job_id, '_job_apply_whatsapp', true);
        }

        $jobs_tags = get_the_terms($job_id, 'job_tags');
        $tags_html = join(', ', wp_list_pluck($jobs_tags, 'name'));
        /* Getting User Company Default Values */
        $post_author_id = get_post_field('post_author', $job_id);
        $web = get_user_meta($post_author_id, '_emp_web', true);
        $company_name = get_the_author_meta('display_name', $post_author_id);
        $company_email = get_the_author_meta('user_email', $post_author_id);
        $company_phone = get_user_meta($post_author_id, '_sb_contact', true);
        $company_adress = get_user_meta($post_author_id, '_emp_map_location', true);
        /* Getting Profile Photo */
        $comp_img = nokriAPI_employer_dp($post_author_id);
        /* Getting Job Skills  */
        $job_skills = get_post_meta($job_id, '_job_skills', true);
        $skill_tags = '';
        if (is_array($job_skills)) {
            $taxonomies = get_terms('job_skills', array('hide_empty' => false, 'orderby' => 'id', 'order' => 'ASC', 'parent' => 0));
            if (count($taxonomies) > 0) {
                foreach ($taxonomies as $taxonomy) {
                    if (in_array($taxonomy->term_id, $job_skills))
                        $skill_tags .= esc_html($taxonomy->name) . ',';
                }
                $skill_tags = rtrim($skill_tags, ",");
            }
        }
        /* Getting Catgories */
        $project = nokri_job_categories_with_chlid_no_href($job_id, 'job_category');
        /* Calling Funtion Job Class For Badges */
        $single_job_badges = nokriAPI_job_class_badg($job_id);
        $job_badge_text = '';
        /* if( count( $single_job_badges ) > 0) 
          {
          foreach( $single_job_badges as $job_badge => $val )
          {
          $job_badge_text .= '<li><a href="#" class="tag">'. esc_html($job_badge).'</a></li>';
          }
          } */
        /* Setting job Expiration */
        $job_deadline = date_i18n(get_option('date_format'), strtotime($job_deadline_n));
        $today = date("m/d/Y");
        $expiry_date_string = strtotime($job_deadline_n);
        $today_string = strtotime($today);
        if ($today_string > $expiry_date_string) {
            update_post_meta($job_id, '_job_status', 'inactive');
        }
        /* Validating Informations For Apply */
        $resume_exist = get_post_meta($job_id, '_job_applied_status_' . $user_id, true);
        $job_expiry = get_post_meta($job_id, '_job_status', true);

        $is_applied = ($resume_exist != '') ? $resume_exist : "false";
        $is_expire = ($job_expiry == 'inactive') ? "true" : "false";
        $is_candidate = "0";
        if (get_user_meta($user_id, '_sb_reg_type', true) == '0') {
            $is_candidate = "1";
        }
        $info[] = nokriAPI_canidate_fields(__("Is candidate", "nokri-rest-api"), $is_candidate, 'textfield', true, '', 'is_candidate');
        $info[] = nokriAPI_canidate_fields(__("Job Apply", "nokri-rest-api"), $is_applied, 'textfield', true, '', 'job_apply');


        $info[] = nokriAPI_canidate_fields(__("Job Expire", "nokri-rest-api"), $is_expire, 'textfield', true, '', 'job_expire');
        $info[] = nokriAPI_canidate_fields(__("Job Title", "nokri-rest-api"), $job_title, 'textfield', true, '', 'job_title');
        $info[] = nokriAPI_canidate_fields(__("Last Date", "nokri-rest-api"), date_i18n(get_option('date_format'), strtotime($job_deadline)), 'textfield', true, '', 'job_last');
        $info[] = nokriAPI_canidate_fields(__("Job Category", "nokri-rest-api"), $project, 'textfield', true, '', 'job_cat');

        $var1 = nokri_job_post_single_taxonomies('job_currency', $job_currency) . " " . nokri_job_post_single_taxonomies('job_salary', $job_salary);

        $var2 = nokri_job_post_single_taxonomies('job_salary_type', $job_salary_type);
        $var3 = ($var2 != "" ) ? "/" . $var2 : "";
        $var4 = $var1 . "" . $var3;
        $info[] = nokriAPI_canidate_fields(__("Job Salary", "nokri-rest-api"), $var4, 'textfield', true, '', 'job_salary');
        $info[] = nokriAPI_canidate_fields(__("Job Shift", "nokri-rest-api"), nokri_job_post_single_taxonomies('job_shift', $job_shift), 'textfield', true, '', 'job_shift');
        $info[] = nokriAPI_canidate_fields(__("No. of Openings", "nokri-rest-api"), $job_vacancy, 'textfield', true, '', 'job_vacancy');
        $info[] = nokriAPI_canidate_fields(__("Job Level", "nokri-rest-api"), nokri_job_post_single_taxonomies('job_level', $job_level), 'textfield', true, 1, 'job_level');

        $info[] = nokriAPI_canidate_fields(__("Job Type", "nokri-rest-api"), nokri_job_post_single_taxonomies('job_type', $job_type), 'textfield', true, 1, 'job_type');
        $info[] = nokriAPI_canidate_fields(__("Job Experience", "nokri-rest-api"), nokri_job_post_single_taxonomies('job_experience', $job_experience), 'textfield', true, '', 'job_exp');
        $info[] = nokriAPI_canidate_fields(__("Job Qualifications", "nokri-rest-api"), nokri_job_post_single_taxonomies('job_qualifications', $job_qualifications), 'textfield', true, '', 'job_qualifications');
        $info[] = nokriAPI_canidate_fields(__("Job Skills", "nokri-rest-api"), $skill_tags, 'textfield', true, '', 'job_skills');
        $info[] = nokriAPI_canidate_fields(__("Job Tags", "nokri-rest-api"), $tags_html, 'textfield', true, '', 'job_tags');
        $info[] = nokriAPI_canidate_fields(__("Posted On", "nokri-rest-api"), $post_date, 'textfield', true, '', 'job_posted');
        $info[] = nokriAPI_canidate_fields(__("Address", "nokri-rest-api"), $ad_mapLocation, 'textfield', true, '', 'job_address');
        $info[] = nokriAPI_canidate_fields(__("Job Video", "nokri-rest-api"), $job_video, 'textfield', true, '', 'job_video');

        $info[] = nokriAPI_canidate_fields(__("Country", "nokri-rest-api"), $countries_last, 'textfield', true, '', 'job_country');

        $user_id = get_current_user_id();
        $apply_status_check = get_post_meta($job_id, '_job_applied_resume_' . $user_id, true);

        $apply_status = false;
        if ($apply_status_check != "") {
            $apply_status = true;
        }



        /////////////
        $info2 = nokriCustomFieldsArray($job_id);
        $info3 = array_merge($info, $info2);

        ////////////
        $data['job_info'] = $info3;
        $content_info[] = nokriAPI_canidate_fields(__("Job Content", "nokri-rest-api"), $content, 'textfield', true, '', 'job_content');
        $data['job_content'] = $content_info;
        $comp_info[] = nokriAPI_canidate_fields(__("Name", "nokri-rest-api"), $company_name, 'textfield', true, '', 'comp_name');
        $comp_info[] = nokriAPI_canidate_fields(__("Email", "nokri-rest-api"), $company_email, 'textfield', true, '', 'comp_email');
        $comp_info[] = nokriAPI_canidate_fields(__("Phone", "nokri-rest-api"), $company_phone, 'textfield', true, '', 'company_phone');
        $comp_info[] = nokriAPI_canidate_fields(__("Web Site", "nokri-rest-api"), $web, 'textfield', true, '', 'comp_web');
        $comp_info[] = nokriAPI_canidate_fields(__("Adress", "nokri-rest-api"), $company_adress, 'textfield', true, '', 'company_adress');
        $comp_info[] = nokriAPI_canidate_fields(__("Profile Pic", "nokri-rest-api"), $comp_img['img'], 'textfield', true, '', 'comp_img');

        $data['comp_info'] = $comp_info;
        $info_extra[] = nokriAPI_canidate_fields(__("Job Details", "nokri-rest-api"), '', 'textfield', true, '', 'job_det');
        $info_extra[] = nokriAPI_canidate_fields(__("Apply Now", "nokri-rest-api"), '', 'textfield', true, '', 'job_apply');

        $info_extra[] = nokriAPI_canidate_fields(__("Short Description", "nokri-rest-api"), '', 'textfield', true, '', 'short_desc');
        $info_extra[] = nokriAPI_canidate_fields(__("Job Description", "nokri-rest-api"), '', 'textfield', true, '', 'job_desc');
        $info_extra[] = nokriAPI_canidate_fields(__("Bookmark", "nokri-rest-api"), '', 'textfield', true, '', 'book_mark');
        $info_extra[] = nokriAPI_canidate_fields(__("Job Expired", "nokri-rest-api"), '', 'textfield', true, '', 'job_expired');
        $info_extra[] = nokriAPI_canidate_fields(__("Job Details", "nokri-rest-api"), '', 'textfield', true, '', 'page_title');
        $info_extra[] = nokriAPI_canidate_fields(__("Apply with linkedin", "nokri-rest-api"), '', 'textfield', true, '', 'apply_linked');
        $info_extra[] = nokriAPI_canidate_fields(__("Share", "nokri-rest-api"), get_the_permalink($job_id), 'textfield', true, '', 'share_job');
        $info_extra[] = nokriAPI_canidate_fields(__("Only candidates can apply for job", "nokri-rest-api"), '', 'textfield', true, '', 'cand_apply');
        $info_extra[] = nokriAPI_canidate_fields(__("Please login as candidate", "nokri-rest-api"), '', 'textfield', true, '', 'cand_bookmark');
        $info_extra[] = nokriAPI_canidate_fields(__("Please login first", "nokri-rest-api"), '', 'textfield', true, '', 'is_login');
        $info_extra[] = nokriAPI_canidate_fields(__("Already applied on this job", "nokri-rest-api"), '', 'textfield', true, '', 'already_applied');

        $info_extra[] = nokriAPI_canidate_fields(__("Apply with", "nokri-rest-api"), $apply_with, 'textfield', true, '', 'job_apply_with');
        $info_extra[] = nokriAPI_canidate_fields(__("Apply URL", "nokri-rest-api"), $apply_with_url, 'textfield', true, '', 'job_apply_url');
        $info_extra[] = nokriAPI_canidate_fields("", $apply_status, 'textfield', true, '', 'apply_status');
        $linkedin_apply_check = isset($nokri['cand_linkedin_apply']) ? $nokri['cand_linkedin_apply'] : 0;
        $info_extra[] = nokriAPI_canidate_fields(__("Linkedin Apply", "nokri-rest-api"), $linkedin_apply_check, 'textfield', true, '', 'linkedin_apply_check');

        $apply_without_login = isset($nokri['apply_without_login']) && $nokri['apply_without_login'] == '1' ? true : false;
        $cand_name = esc_html__('Your Name', 'nokri-rest-api');
        $without_login_email = esc_html__('Your Email', 'nokri-rest-api');

        $info_extra[] = array('key' => 'apply_without_check', 'apply' => $apply_without_login, 'field_type_name' => 'apply_without_check');
        if ($apply_without_login) {
            $info_extra[] = nokriAPI_canidate_fields(__("Your Name", "nokri-rest-api"), '', 'textfield', true, '', 'cand_name');
            $info_extra[] = nokriAPI_canidate_fields(__("Your Email", "nokri-rest-api"), '', 'textfield', true, '', 'cand_email');
        }


        //job attachment
        $job_attachments = get_post_meta($job_id, '_job_attachment', true);
        if ($job_attachments != "") {

            $portfolios = explode(',', $job_attachments);
            foreach ($portfolios as $portfolio) {
                $portfolio_image = wp_get_attachment_image_src($portfolio, '');
                $data['img'][] = nokriAPI_canidate_fields('Img_url', $portfolio_image[0], $portfolio, '', 1, 'Img_url');
            }
        } else {
            $data['img'] = array();
        }

        $upload_resume_option = isset($nokri['upload_resume_option']) ? $nokri['upload_resume_option'] : 1;
        $upoload_option = 1;

        if ($upload_resume_option == "no-field") {
            $upoload_option = 3;
        } else if ($upload_resume_option == "opt") {
            $upoload_option = 0;
        }

        $info_extra[] = nokriAPI_canidate_fields(__('Resume upload option', "nokri-rest-api"), $upoload_option, 'textfield', true, '', 'upload_resume_option');
        $nearby          =     ( isset($nokri['nearby_job_switch']) && $nokri['nearby_job_switch'] != ""  ) ? $nokri['nearby_job_switch'] : '';
        $data['extras']         = $info_extra;
        $data['nearby_jobs_switch']    = $nearby;
        $data['nearby_section_heading']    = esc_html__('NearBy jobs','nokri-rest-api');
                
        $data['nearby_jobs']    =   nokriAPI_nearby_jobs_api_fun($job_id);
        return $response = array('success' => true, 'data' => $data, 'message' => '');
    }
}

function nokriCustomFieldsArray($post_id = '') {
    global $nokri;
    $style = '1';
    if ((isset($nokri['job_post_style'])) && $nokri['job_post_style'] != '') {
        $style = $nokri['job_post_style'];
    }
    $info_extra = array();

    $html = $cols = '';
    if ($post_id == "")
        return;
    $terms = nokri_getCats_desc($post_id);
    if (count((array) $terms) > 0 && $terms != "") {
        foreach ($terms as $term) {
            $term_id = $term->term_id;
            $t = nokri_dynamic_templateID($term_id);
            if ($t)
                break;
        }



        $html = $cols = '';
        $taxs = nokri_get_term_form('', '', 'static', 'arr');

        if (count((array) $taxs) > 0) {
            foreach ($taxs as $tax) {
                $slug = $tax['slug'];
                $titles = ucfirst($tax['name']);
                if ($slug == 'ad_features')
                    continue;
                $values = get_post_meta($post_id, '_nokri_' . $slug, true);
                if ($values != "") {
                    $info_extra[] = nokriAPI_canidate_fields(esc_html($titles), esc_html($values), 'textfield', true, '', 'page_title');
                }
            }
        }

        $templateID = nokri_dynamic_templateID($term_id);
        $result = get_term_meta($templateID, '_sb_dynamic_form_fields', true);


        if (isset($result) && $result != "") {
            $formData = sb_dynamic_form_data($result);
            if (count((array) $formData) > 0) {
                foreach ($formData as $data) {
                    $values = get_post_meta($post_id, "_nokri_tpl_field_" . $data['slugs'], true);
                    $value = json_decode($values);
                    $value = (is_array($value) ) ? implode($value, ", ") : $values;

                    $titles = ($data['titles']);

                    if ($value != "") {
                        if (isset($data['types']) && $data['types'] == 5) {
                            $value = esc_url($value);
                        } else if (isset($data['types']) && $data['types'] == 4) {
                            $value = date_i18n(get_option('date_format'), strtotime($value));
                        } else {
                            $value = esc_html($value);
                        }

                        $info_extra[] = nokriAPI_canidate_fields(esc_html($titles), esc_html($value), 'textfield', true, '', 'page_title');
                    }
                }
            }
        }
    }
    return $info_extra;
}

/* * ********************************** */
/* All Jobs Service */
/* * ********************************** */
add_action('rest_api_init', 'nokriAPI_all_jobs_hook', 0);

function nokriAPI_all_jobs_hook() {
    register_rest_route(
            'nokri/v1', '/all_jobs/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_all_jobs',
        'permission_callback' => function () {
            return true;
        },
            )
    );
}

function my_pre_get_posts($query) {
    $query->set('post_type', 'job_post');
}

if (!function_exists('nokriAPI_all_jobs')) {

    function nokriAPI_all_jobs($request) {
        $json_data = $request->get_json_params();
        $keyword = (isset($json_data['keyword'])) ? trim($json_data['keyword']) : '';
        $message = '';
        add_action('pre_get_posts', 'my_pre_get_posts');
        $jobs = array();
        if (get_query_var('paged')) {
            $paged = get_query_var('paged');
        } else if (isset($json_data['page_number'])) {
            // This will occur if on front page.
            $paged = $json_data['page_number'];
        } else {
            $paged = 1;
        }
        global $nokriAPI;
        $jobs_limits = (isset($nokriAPI['recent_jobs_limits']) ) ? $nokriAPI['recent_jobs_limits'] : -1;
        $args = array(
            's' => $keyword,
            'post_type' => 'job_post',
            'paged' => $paged,
            'order' => 'DESC',
            'orderby' => 'date',
            'posts_per_page' => $jobs_limits,
            'post_status' => 'publish',
            'meta_query' => array(
                array(
                    'key' => '_job_status',
                    'value' => 'active',
                    'compare' => '=',
                ),
            )
        );

        $args = nokri_wpml_show_all_posts_callback($args);
        $query = new WP_Query($args);
        if ($query->have_posts()) {
            $count = 0;
            while ($query->have_posts()) {
                $query->the_post();
                $query->post_author;
                $job_id = get_the_id();
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
                $job_adress = nokri_job_categories_with_chlid_no_href($job_id, 'ad_location');
                $job_deadline_n = get_post_meta($job_id, '_job_date', true);
                $job_deadline = date_i18n(get_option('date_format'), strtotime($job_deadline_n));
                $ad_mapLocation = get_post_meta($job_id, '_job_address', true);

                /* Getting Company  Profile Photo */
                $comp_img = nokriAPI_employer_dp($post_author_id);

                /* check is feature */
                $job_badge_ul = nokri_premium_job_class_badges($job_id);
                $is_feature = false;
                if ($job_badge_ul != '') {
                    $is_feature = true;
                }

                /* Job Id */
                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Id", "nokri-rest-api"), get_the_id(), 'textfield', true, 1, 'job_id');
                /* Company Id */
                $jobs[$count][] = nokriAPI_canidate_fields(__("Company Id", "nokri-rest-api"), $post_author_id, 'textfield', true, 1, 'company_id');
                /* Company Name */
                $jobs[$count][] = nokriAPI_canidate_fields(__("Company Name", "nokri-rest-api"), get_the_author_meta('display_name', $post_author_id), 'textfield', true, 1, 'company_name');
                /* Job Name */
                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Name", "nokri-rest-api"), nokriAPI_convert_uniText(get_the_title()), 'textfield', true, 1, 'job_name');

                /* Job Salary */
                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Salary", "nokri-rest-api"), nokri_job_post_single_taxonomies('job_currency', $job_currency) . " " . nokri_job_post_single_taxonomies('job_salary', $job_salary) . "/" . nokri_job_post_single_taxonomies('job_salary_type', $job_salary_type), 'textfield', true, 1, 'job_salary');
                /* Job Type */
                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Type", "nokri-rest-api"), nokri_job_post_single_taxonomies('job_type', $job_type), 'textfield', true, 1, 'job_type');
                /* Job Posted */
                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Posted", "nokri-rest-api"), $job_deadline, 'textfield', true, 1, 'job_posted');
                /* Company Logo */
                $c_image_link = (isset($image_link[0]) && is_array($image_link) && $image_link[0] != "" ) ? $image_link[0] : '';
                $jobs[$count][] = nokriAPI_canidate_fields(__("Company Logo", "nokri-rest-api"), $comp_img['img'], 'textfield', true, 1, 'company_logo');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Location", "nokri-rest-api"), $job_adress, 'textfield', true, 1, 'job_location');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Feature job", "nokri-rest-api"), $is_feature, 'textfield', true, 1, 'is_feature');
                $count++;
            }
        } else {
            $message = __("Not posted any job yet", "nokri-rest-api");
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
        $data['page_title'] = __("All Jobs", "nokri-rest-api");
        $data['jobs'] = $jobs;
        $data['number_of_jobs'] = $query->found_posts;
        return $response = array('success' => true, 'data' => $data, 'message' => $message, "pagination" => $pagination);
    }

}
/* * ********************************** */
/* Category base template starts  */
/* * ********************************** */
add_action('rest_api_init', 'nokriAPIpost_ad_cat_fields_get', 0);

function nokriAPIpost_ad_cat_fields_get() {
    register_rest_route(
            'nokri/v1', '/employer/job_post/dynamic_fields/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_post_ad_fields',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
    ));
}

if (!function_exists('nokriAPI_post_ad_fields')) {

    function nokriAPI_post_ad_fields($request, $is_termID = '', $ad_id = '') {
        global $nokri;
        $json_data = $request->get_json_params();
        $term_id = (isset($json_data['cat_id'])) ? $json_data['cat_id'] : '';
        if (isset($nokri['job_post_form']) && $nokri['job_post_form'] == '0') {
            return $response = array('success' => false, 'data' => '', 'message' => __("Please enable category base template", "nokri-rest-api"), 'extras' => '');
        }

        if ($is_termID != "") {

            $term_id = $is_termID;
            $ad_id = (isset($json_data['job_id'])) ? $json_data['job_id'] : $ad_id;
        } else {
            $term_id = (isset($json_data['cat_id'])) ? $json_data['cat_id'] : $is_termID;
            $ad_id = (isset($json_data['job_id'])) ? $json_data['job_id'] : $ad_id;
        }
        $arrays = array();
        $result = nokriAPI_dynamic_templateID($term_id);

        $templateID = get_term_meta($result, '_sb_dynamic_form_fields', true);
        /* Show options */
        $video_show = sb_custom_form_data($templateID, '_sb_default_cat_video_show');
        $image_show = sb_custom_form_data($templateID, '_sb_default_cat_image_show');
        /* Required options */
        $video_req = sb_custom_form_data($templateID, '_sb_default_cat_video_required');
        $image_req = sb_custom_form_data($templateID, '_sb_default_cat_image_required');
        $pid = $is_update = $ad_id;
        $tags_array = wp_get_object_terms($pid, 'ad_tags', array('fields' => 'names'));
        $ad_tags = @implode(',', $tags_array);
        $showcatData = false;
        if (isset($nokri['job_post_form']) && $nokri['job_post_form'] == '1') {
            $showcatData = true;
        }
        /* Attachment array */
        if ($image_show == 1 && $templateID != "" && $showcatData == true) {
            if ($image_req) {
                $image_req = true;
            }
            if ($image_show) {
                $image_show = true;
            }
            $arrays[] = nokriAPI_getPostAdFields('attachment', 'job_attachment', "_job_attachment", 0, __("Job Attachment", "nokri-rest-api"), '', '', '2', $image_req, $ad_yvideo, $ad_id);
        }
        /* Video array */
        if ($video_show == 1 && $templateID != "" && $showcatData == true) {
            $arrays[] = nokriAPI_getPostAdFields('textfield', 'job_video', "_job_video", 0, __("Youtube Video Link", "nokri-rest-api"), '', '', '2', false, $ad_yvideo, $ad_id);
        }
        /* Custom taxonomy feilds */
        $arrays2 = nokri_get_term_formAPI($result, $ad_id);
        $arrays = array_merge($arrays, $arrays2);
        if (isset($templateID) && $templateID != "" && isset($nokri['job_post_form']) && $nokri['job_post_form'] == '1') {
            $formData = sb_dynamic_form_data($templateID);
            foreach ($formData as $r) {
                $is_required = ( isset($r['requires']) && $r['requires'] == 1 ) ? true : false;
                if (isset($r['types']) && trim($r['types']) != "" && isset($r['status']) && trim($r['status']) == 1) {
                    ///////Make chnages here
                    $in_search = (isset($r['in_search']) && $r['in_search'] == "yes") ? 1 : 0;
                    if ($r['titles'] != "" && $r['slugs'] != "") {
                        $mainTitle = $name = $r['titles'];
                        $fieldName = $r['slugs'];
                        $fieldValue = (isset($_GET["custom"]) && isset($_GET['custom'][$r['slugs']])) ? $_GET['custom'][$r['slugs']] : '';

                        $postMetaName = '_nokri_tpl_field_' . $fieldName;
                        $nameValue = get_post_meta($ad_id, $postMetaName, true);
                        $nameValue = ( $nameValue ) ? $nameValue : "";
                        if (isset($r['types']) && $r['types'] == 1) { //_job_posts
                            $arrays[] = array("main_title" => $mainTitle, "field_type" => 'textfield', "field_type_name" => $fieldName, "field_val" => $nameValue, "field_name" => "", "title" => $name, "values" => $fieldValue, "has_page_number" => 2, "is_required" => $is_required);
                        }
                        /* Date type */
                        if (isset($r['types']) && $r['types'] == 4) {
                            $arrays[] = array("main_title" => $mainTitle, "field_type" => 'date', "field_type_name" => $fieldName, "field_val" => $nameValue, "field_name" => "", "title" => $name, "values" => $fieldValue, "has_page_number" => 2, "is_required" => $is_required);
                        }
                        /* Url */
                        if (isset($r['types']) && $r['types'] == 5) {
                            $arrays[] = array("main_title" => $mainTitle, "field_type" => 'link', "field_type_name" => $fieldName, "field_val" => $nameValue, "field_name" => "", "title" => $name, "values" => $fieldValue, "has_page_number" => 2, "is_required" => $is_required);
                        }
                        //select option
                        if (isset($r['types']) && $r['types'] == 2 || isset($r['types']) && $r['types'] == 3) {
                            $varArrs = @explode("|", $r['values']);
                            $varArrs = nokriAPI_arraySearch($varArrs, '', $nameValue);
                            $termsArr = array();
                            if ($r['types'] == 2 && $nameValue == "") {
                                $termsArr[] = array
                                    (
                                    "value" => "",
                                    "name" => __("Select Option", "nokri-rest-api"),
                                    "selected" => false,
                                );
                            }
                            foreach ($varArrs as $v) {
                                if ($r['types'] == 3) {
                                    $is_checked = false;
                                    $selected = false;
                                    if ($nameValue == $v) {
                                        $selected = true;
                                    }
                                    $termsArr[] = array
                                        (
                                        "value" => "$v",
                                        "name" => $v,
                                        "has_sub" => false,
                                        "has_template" => false,
                                        "selected" => $selected
                                    );
                                } else {
                                    $selected = false;
                                    if ($nameValue == $v) {
                                        $selected = true;
                                    }
                                    $termsArr[] = array
                                        (
                                        "value" => "$v",
                                        "name" => $v,
                                        "selected" => $selected,
                                    );
                                }
                            }

                            $ftype = ($r['types'] == 2 ) ? 'select' : 'checkbox';
                            $arrays[] = array("main_title" => $mainTitle, "field_type" => $ftype, "field_type_name" => $fieldName, "field_val" => $nameValue, "field_name" => "", "title" => $name, "values" => $termsArr, "has_page_number" => 2, "is_required" => $is_required);
                        }
                    }
                }
            }
        }
        if ($is_termID != "") {
            return $arrays;
        } else {
            //$request_from = nokriAPI_getSpecific_headerVal('nokri-Request-From');
            if ($result == '0') {


                $arrays[] = nokriAPI_getPostAdFields('select', 'job_type', 'job_type', 0, __("Job Type", "nokri-rest-api"), __("Job Type", "nokri-rest-api"), 'job_type', '2', true, 'job_type', $ad_id);


                $arrays[] = nokriAPI_getPostAdFields('select', 'job_qualifications', 'job_qualifications', 0, __("Job Qualifications", "nokri-rest-api"), __("Job Qualifications", "nokri-rest-api"), 'job_qualifications', '2', true, 'job_qualifications', $ad_id);

                $arrays[] = nokriAPI_getPostAdFields('select', 'job_level', 'job_level', 0, __("Job Level", "nokri-rest-api"), __("Job Level", "nokri-rest-api"), 'job_level', '2', true, 'job_level', $ad_id);


                $arrays[] = nokriAPI_getPostAdFields('select', 'job_salary', 'job_salary', 0, __("Job Salary", "nokri-rest-api"), __("Job Salary", "nokri-rest-api"), 'job_salary', '2', true, 'job_salary', $ad_id);

                $arrays[] = nokriAPI_getPostAdFields('select', 'job_salary_type', 'job_salary_type', 0, __("Job Salary Type", "nokri-rest-api"), __("Job Salary Type", "nokri-rest-api"), 'job_salary_type', '2', true, 'job_salary_type', $ad_id);

                $arrays[] = nokriAPI_getPostAdFields('select', 'job_experience', 'job_experience', 0, __("Job Experience", "nokri-rest-api"), __("Job Experience", "nokri-rest-api"), 'job_experience', '2', true, 'job_experience', $ad_id);

                $arrays[] = nokriAPI_getPostAdFields('select', 'job_currency', 'job_currency', 0, __("Job Currency", "nokri-rest-api"), __("Job Currency", "nokri-rest-api"), 'job_currency', '2', true, 'job_currency', $ad_id);

                $arrays[] = nokriAPI_getPostAdFields('select', 'job_shift', 'job_shift', 0, __("Job Shift", "nokri-rest-api"), __("Job Shift", "nokri-rest-api"), 'job_shift', '2', true, 'job_shift', $ad_id);

                $arrays[] = nokriAPI_getPostAdFields('select', 'job_skills', 'job_skills', 0, __("Job Skills", "nokri-rest-api"), __("Job Skills", "nokri-rest-api"), 'job_skills', '2', true, 'job_skills', $ad_id);

                $arrays[] = nokriAPI_getPostAdFields('select', 'job_tags', 'job_tags', 0, __("Job Tags", "nokri-rest-api"), __("Job Skills", "nokri-rest-api"), 'job_tags', '2', true, 'job_tags', $ad_id);

                return $response = array('success' => true, 'data' => $arrays, 'message' => '', 'extras' => $extras);
            } else {
                return $response = array('success' => true, 'data' => $arrays, 'message' => '', 'extras' => $extras);
            }
        }
    }

}


if (!function_exists('nokri_get_term_formAPI')) {

    function nokri_get_term_formAPI($term_id = '', $post_id = '', $formType = 'dynamic', $is_array = '') {
        global $nokri;
        $data = ($formType == 'dynamic' && $term_id != "") ? sb_text_field_value($term_id) : sb_getTerms('custom');
        if ($is_array == 'arr')
            return $data;
        $dataHTML = '';
        foreach ($data as $d) {
            $name = $d['name'];
            $slug = $d['slug'];
            if ($formType == 'static') {
                $showme = 1;
                $is_show = $showme;
                $is_this_req = 1;
            } else {
                $is_show = $d['is_show'];
                $is_this_req = $d['is_req'];
            }
            $is_req = $is_this_req;
            $is_search = $d['is_search'];
            $is_type = $d['is_type'];
            $required = (isset($is_req) && $is_req == 1 ) ? true : false;
            $is_show = (isset($is_show) && $is_show == 1 ) ? true : false;

            if ($is_type == 'textfield') {

                if ($slug == '_job_posts' || $slug == 'job_posts') {
                    $inputVal = get_post_meta($post_id, '_job_posts', true);
                } else {
                    $inputVal = get_post_meta($post_id, '_nokri_' . $slug, true);
                }




                $arrays[] = array("main_title" => ucfirst($name), "field_type" => 'textfield', "field_type_name" => $slug, "field_val" => $inputVal, "field_name" => "", "title" => $name, "values" => '', "has_page_number" => 2, "is_required" => $required);
            } else if ($slug == 'job_skills') {

                $adfeatures = '';
                $frs = array();


                $taxonomies = get_terms('job_skills', array('hide_empty' => false, 'orderby' => 'id', 'order' => 'ASC', 'parent' => 0));
                $job_skills = wp_get_post_terms($post_id, 'job_skills', array("fields" => "ids"));
                foreach ($taxonomies as $feature) {
                    $selected = (in_array($feature->term_id, $job_skills)) ? true : false;
                    $features_values[] = array("name" => $feature->name, "is_checked" => $selected, "id" => $feature->term_id . '');
                }

                $arrays[] = array("main_title" => $name, "field_type" => 'multi_select', "field_type_name" => "job_skills", "field_val" => $inputVal, "field_name" => "", "title" => $name, "values" => $features_values, "has_page_number" => 2, "is_required" => $required);
            } else {
                $values = nokri_get_cats($slug, 0);
                $select_values = array();
                if (!empty($values) && count((array) $values) > 0 && !is_wp_error($values)) {
                    $adfeaturesName = wp_get_post_terms($post_id, $slug, array("fields" => "ids"));
                    $adfeaturesName = isset($adfeaturesName[0]) ? $adfeaturesName[0] : '';
                    $select_values[] = array("value" => '', "selected" => false, "name" => __("Select an option", "nokri-rest-api"));
                    foreach ($values as $val) {
                        if (isset($val->term_id) && $val->term_id != "") {
                            $id = $val->term_id;
                            $name2 = $val->name;
                            $selected = ( $adfeaturesName == $val->term_id ) ? true : false;
                            $select_values[] = array("value" => (string) $id, "selected" => $selected, "name" => $name2);
                        }
                    }


                    $arrays[] = array("main_title" => $name, "field_type" => 'select', "field_type_name" => $slug, "field_val" => $inputVal, "field_name" => "", "title" => $name, "values" => $select_values, "has_page_number" => 2, "is_show" => $is_show, "is_required" => $required);
                }
            }
        }
        return $arrays;
    }

}

function nokriAPI_dynamic_templateID($cat_id) {
    $termTemplate = '';
    if ($cat_id != "") {
        $termTemplate = get_term_meta($cat_id, '_sb_category_template', true);

        $go_next = ($termTemplate == "" || $termTemplate == 0) ? true : false;
        if ($go_next) {
            $parent = get_term($cat_id);
            if ($parent->parent > 0) {

                $cat_id = $parent->parent;
                $termTemplate = get_term_meta($cat_id, '_sb_category_template', true);

                $go_next = ($termTemplate == "" || $termTemplate == 0) ? true : false;
                $parent = get_term($cat_id);
                if ($parent->parent > 0 && $go_next) {

                    $cat_id = $parent->parent;

                    $termTemplate = get_term_meta($cat_id, '_sb_category_template', true);
                    $parent = get_term($cat_id);

                    $go_next = ($termTemplate == "" || $termTemplate == 0) ? true : false;
                    if ($parent->parent > 0 && $go_next) {

                        $cat_id = $parent->parent;
                        $termTemplate = get_term_meta($cat_id, '_sb_category_template', true);
                        $parent = get_term($cat_id);
                        $go_next = ($termTemplate == "" || $termTemplate == 0) ? true : false;
                        if ($parent->parent > 0 && $go_next) {
                            $cat_id = $parent->parent;
                            $termTemplate = get_term_meta($cat_id, '_sb_category_template', true);
                            $parent = get_term($cat_id);
                            $go_next = ($termTemplate == "" || $termTemplate == 0) ? true : false;
                            if ($parent->parent > 0 && $go_next) {
                                $cat_id = $parent->parent;
                                $termTemplate = get_term_meta($cat_id, '_sb_category_template', true);
                            }
                        }
                    }
                }
            }
        }
    }

    return $termTemplate;
}

function nokriAPICustomFieldsHTML($post_id = '') {
    $html = $cols = '';
    if ($post_id == "")
        return;
    $terms = nokri_getCats_desc($post_id);
    if (count((array) $terms) > 0 && $terms != "") {
        foreach ($terms as $term) {
            $term_id = $term->term_id;
            $t = nokri_dynamic_templateID($term_id);
            if ($t)
                break;
        }

        $html = $cols = '';
        $taxs = nokri_get_term_form('', '', 'static', 'arr');

        if (count((array) $taxs) > 0) {
            foreach ($taxs as $tax) {
                $slug = $tax['slug'];
                $titles = ucfirst($tax['name']);
                if ($slug == 'ad_features')
                    continue;
                $values = get_post_meta($post_id, '_nokri_' . $slug, true);
                if ($values != "") {
                    $html .= '<div class="col-sm-4 col-md-4 col-xs-12 no-padding"><span><strong>' . esc_html($titles) . '</strong> :</span> ' . esc_html($values) . '</div>';
                }
            }
        }

        $templateID = nokri_dynamic_templateID($term_id);
        $result = get_term_meta($templateID, '_sb_dynamic_form_fields', true);
        if (isset($result) && $result != "") {
            $formData = sb_dynamic_form_data($result);
            if (count((array) $formData) > 0) {
                foreach ($formData as $data) {
                    $values = get_post_meta($post_id, "_nokri_tpl_field_" . $data['slugs'], true);
                    $value = json_decode($values);
                    $value = (is_array($value) ) ? implode($value, ", ") : $values;

                    $titles = ($data['titles']);

                    if ($value != "") {
                        if (isset($data['types']) && $data['types'] == 5) {
                            $value = '<a href="' . esc_url($value) . '" target="_blank">' . esc_url($value) . '</a>';
                        } else if (isset($data['types']) && $data['types'] == 4) {
                            $value = date_i18n(get_option('date_format'), strtotime($value));
                        } else {
                            $value = esc_html($value);
                        }

                        $html .= '<div class="col-sm-' . $cols . ' col-md-' . $cols . ' col-xs-12 no-padding">
						<span><strong>' . esc_html($titles) . '</strong> :</span>
						' . ($value) . '
						</div>';
                    }
                }
            }
        }
    }
    return $html;
}

function nokriAPI_get_dynamic_form($term_id = '', $post_id = '') {
    $html = '';

    if ($term_id == '')
        return $html;

    $result = get_term_meta($term_id, '_sb_dynamic_form_fields', true);
    if (isset($result) && $result != "") {
        $formData = sb_dynamic_form_data($result);

        foreach ($formData as $data) {
            $status = ($data['status']);

            if (isset($status) && $status == 1) {
                $types = ($data['types']);
                $titles = ($data['titles']);
                $key = '_nokri_tpl_field_' . $data['slugs'];
                $slugs = 'cat_template_field[' . $key . ']';
                $values = ($data['values']);
                $columns = ($data['columns']);
                $requires = '';

                if (isset($data['requires']) && $data['requires'] && '1') {
                    $message = 'data-parsley-error-message="' . __('This field is required.', 'nokri') . '"';
                    $requires = 'selected="selected" data-parsley-required="true" ' . $message;
                }

                $fieldValue = (isset($post_id) && $post_id != "") ? get_post_meta($post_id, $key, true) : '';

                if ($types == 1) {


                    $arrays[] = array("main_title" => esc_html($titles), "field_type" => 'input', "field_type_name" => esc_attr($slugs), "field_val" => $fieldValue, "field_name" => "", "title" => $name, "values" => '', "has_page_number" => 2, "is_required" => $requires);
                }
                $options = array();
                if ($types == 2) {

                    $vals = @explode("|", $values);

                    foreach ($vals as $val) {
                        $selected = ($fieldValue == $val) ? 'selected="selected"' : '';
                        $options[] .= '<option value="' . esc_html(trim($val)) . '" ' . $selected . '>' . esc_html($val) . '</option>';
                    }


                    $arrays[] = array("main_title" => esc_html($titles), "field_type" => 'select', "field_type_name" => esc_attr($slugs), "field_val" => $options, "field_name" => "", "title" => $name, "values" => '', "has_page_number" => 2, "is_required" => $requires);


                    $html .= '
			  <div class="col-md-' . $columns . ' col-lg-' . $columns . ' col-xs-12 col-sm-12 margin-bottom-20">
				 <label class="control-label">' . esc_html($titles) . '</label>
				 <select class="category form-control" name="' . esc_attr($slugs) . '" ' . $requires . '>
					<option value="">' . __("Select Option", "nokri") . '</option>
					' . $options . '
				 </select>
			  </div>';
                }
                if ($types == 3) {
                    $options = '';
                    $vals = @explode("|", $values);
                    $loop = 1;

                    $fieldValue = json_decode($fieldValue, true);



                    foreach ($vals as $val) {
                        $checked = '';
                        if (isset($fieldValue) && $fieldValue != "") {
                            $checked = in_array($val, $fieldValue) ? 'checked="checked"' : '';
                        }

                        $options .= '<li class="col-md-4 col-sm-6 col-xs-12 no-padding"><input type="checkbox" id="minimal-checkbox-' . $loop . '"  value="' . esc_html(trim($val)) . '" ' . $checked . ' name="' . esc_attr($slugs) . '[' . $val . ']"><label for="minimal-checkbox-' . $loop . '">' . esc_html($val) . '</label></li>';
                        $loop++;
                    }

                    $html .= '
					 <div class="col-md-' . $columns . ' col-lg-' . $columns . ' col-xs-12 col-sm-12 margin-bottom-20">
					<label class="control-label">' . esc_html($titles) . '</label>
					 <div class="skin-minimal"><ul class="list">' . $options . '</ul></div>
					 </div>';
                }
                /* For Date Field */
                if ($types == 4) {

                    $html .= '
				  <div class="col-md-' . $columns . ' col-lg-' . $columns . ' col-xs-12 col-sm-12 margin-bottom-20 calendar-div">
					 <label class="control-label">' . esc_html($titles) . '</label>
					 <input class="form-control dynamic-form-date-fields" name="' . esc_attr($slugs) . '" value="' . $fieldValue . '" type="text" ' . $requires . '><i class="fa fa-calendar"></i>
				  </div>
			  ';
                }
                /* For Website URL */
                if ($types == 5) {
                    $valid_message = __("Please enter a valid website URL", "redux-framework");
                    $html .= '
				  <div class="col-md-' . $columns . ' col-lg-' . $columns . ' col-xs-12 col-sm-12 margin-bottom-20 ">
					 <label class="control-label">' . esc_html($titles) . '</label>
					 <input class="form-control" name="' . esc_attr($slugs) . '" value="' . $fieldValue . '" type="url" ' . $requires . ' data-required-message="' . esc_attr($valid_message) . '" data-parsley-type="url" >
				  </div>
			  ';
                }
            }/* Status ends */
        }
    }
    return '<div class="row">' . $html . '</div>';
}

if (!function_exists('nokriAPI_arraySearch')) {

    function nokriAPI_arraySearch($array, $index, $value) {

        if ($value != "") {
            $arr = array();
            $count = 0;
            foreach ($array as $key => $val) {
                $data = ( $index != "" ) ? $val["$index"] : $val;
                if ($data == $value) {
                    $arr = ( $val );
                    unset($array[$count]);
                }
                $count++;
            }
            $array = array_merge(array($arr), $array);
        }

        return $array;
    }

}

if (!function_exists('nokriAPI_objArraySearch')) {

    function nokriAPI_objArraySearch($array, $index, $value, $newIndex = array()) {
        $extractedKey = array();
        if (isset($array) && count($array) > 0) {
            foreach ($array as $key => $arrayInf) {
                if ($arrayInf->{$index} == $value) {
                    unset($array[$key]);
                    $extractedKey = $arrayInf;
                    //return $arrayInf;
                }
            }
        }
        return ( isset($newIndex) && count((array) $newIndex) > 0 && $newIndex != "" ) ? array_merge(array($newIndex), $array) : $array;
    }

}


if (!function_exists('nokriAPI_getPostAdFields')) {

    function nokriAPI_getPostAdFields($field_type = '', $field_type_name = '', $term_type = 'ad_cats', $only_parent = 0, $name = '', $mainTitle = '', $defaultValue = '', $has_page_number = 1, $is_required = false, $update_val = '', $ad_id = '') {
        global $nokri;
        $values = '';
        $returnType = 1;
        $values_arr = array();
        $has_cat_template = false;
        if ($field_type == "select") {

            $termsArr = array();
            if (is_array($term_type)) {

                $is_multiArr = nokriAPI_is_multiArr($term_type);
                if ($is_multiArr == true) {
                    $term_type = nokriAPI_arraySearch($term_type, "key", $update_val);
                    foreach ($term_type as $val) {
                        $termsArr[] = array
                            (
                            "id" => (string) $val['key'],
                            "name" => $val['val'],
                            "has_sub" => false,
                            "has_template" => false,
                            "is_show" => $val['is_show'],
                        );
                    }
                } else {

                    foreach ($term_type as $key => $val) {
                        $termsArr[] = array
                            (
                            "id" => (string) $key,
                            "name" => $val,
                            "has_sub" => false,
                            "has_template" => false,
                            "is_show" => true,
                        );
                    }
                }
            } else {

                $has_cat_template = ($term_type == "ad_cats") ? true : false;
                $terms = get_terms(array('taxonomy' => $term_type, 'hide_empty' => false, 'parent' => $only_parent,));
                $ad_cats = array();
                if ($ad_id != "") {
                    $ad_cats = wp_get_object_terms($ad_id, $term_type, array('fields' => 'ids'));

                    if ($term_type == "ad_country") {
                        $ad_cats = nokriAPI_getCats_idz($ad_id, 'ad_country', true);

                        /* nokriAPI_cat_ancestors( $ad_cats, 'ad_country', true); */
                    }


                    if (count($ad_cats) > 0) {
                        $count_update_val = count($ad_cats) - 1;
                        $finalCatID = $ad_cats[$count_update_val];
                        $term_data = get_term_by('id', $finalCatID, $term_type);
                        $terms = nokriAPI_objArraySearch($terms, 'term_id', $finalCatID, $term_data);
                    }
                }


                $termsArr = array();
                $catNames = '';
                $catIDS = '';
                if (count($terms)) {

                    if (count($ad_cats) == 0) {
                        $termsArr[] = array
                            (
                            //"id" => "", 
                            "name" => __("Select Option", "nokri-rest-api"),
                            "has_sub" => false,
                            "has_template" => false,
                            "is_show" => true,
                        );
                    }
                    foreach ($terms as $ad_trm) {

                        $result = nokri_dynamic_templateID(@$ad_trm->term_id);
                        $templateID = get_term_meta($result, '_sb_dynamic_form_fields', true);
                        $has_template = (isset($templateID) && $templateID != "") ? true : false;

                        if (isset($nokri['job_post_form']) && $nokri['job_post_form'] == '1' && $term_type == "job_category") {
                            $has_template = true;
                        }

                        $term_children = get_term_children(filter_var(@$ad_trm->term_id, FILTER_VALIDATE_INT), filter_var($term_type, FILTER_SANITIZE_STRING));

                        $has_sub = ( empty($term_children) || is_wp_error($term_children) ) ? false : true;
                        $tax_type = wp_get_post_terms($ad_id, $term_type, array("fields" => "ids"));
                        $tax_type = isset($tax_type[0]) ? $tax_type[0] : '';
                        $selected = false;
                        if ($tax_type == @$ad_trm->term_id) {
                            $selected = true;
                        }

                        /* Ad features end */
                        $termsArr[] = array
                            (
                            //"id" 			=> 	(string)$ad_trm->term_id, 					
                            "name" => htmlspecialchars_decode(@$ad_trm->name, ENT_NOQUOTES),
                            "value" => (string) $ad_trm->term_id,
                            "has_sub" => $has_sub,
                            "has_template" => $has_template,
                            "is_show" => true,
                            "is_checked" => $selected
                                /* "count" => $ad_trm->count, */
                        );
                    }
                }
            }


            /* Ad features end */
            $values = $termsArr;
            $values_arr = $termsArr;
        }

        if ($field_type == "textfield" || "glocation_textfield" == $field_type || "textarea" == $field_type || $field_type == "attachment") {
            if ($field_type == "attachment") {
                $values_input[] = array("value" => "", "selected" => fasle, "name" => "");
            } else {
                $values_input = "";
            }

            $values_arr = ($defaultValue != "") ? array($defaultValue) : array();
            if ($term_type != "") {
                $update_val = get_post_meta($ad_id, $term_type, true);
            } else {
                $update_val = "";
            }

            $values = $values_input;
        }

        if ($field_type == "image") {
            $values_arr = ($defaultValue != "") ? array($defaultValue) : array();
            $values = $defaultValue;
        }
        if ($field_type == "map") {
            $values_arr = ($defaultValue != "") ? array($defaultValue) : array();
            $values = $defaultValue;
        }

        if (isset($nokri['job_post_form']) && $nokri['job_post_form'] == '0') {
            $has_cat_template = false;
        }

        $values_data = $values;


        return array("main_title" => $mainTitle, "field_type" => $field_type, "field_type_name" => $field_type_name, "field_val" => $update_val, "field_name" => "", "title" => $name, "values" => $values_data, "has_page_number" => (string) $has_page_number, "is_required" => $is_required, "has_cat_template" => $has_cat_template);
    }

}
if (!function_exists('nokriAPI_getCats_idz')) {

    function nokriAPI_getCats_idz($postId, $term_name, $reverse_arr = false) {
        $terms = wp_get_post_terms($postId, $term_name, array('orderby' => 'id', 'order' => 'DESC'));
        //$terms = get_post_meta($postId,'_carspot_ad_condition',true);

        $deepestTerm = false;
        $maxDepth = -1;
        $c = 0;
        if (isset($terms) && count($terms) > 0) {
            foreach ($terms as $term) {
                $ancestors = get_ancestors($term->term_id, $term_name);
                $termDepth = count($ancestors);
                $deepestTerm[$c] = $term->term_id;
                $maxDepth = $termDepth;
                $c++;
            }
            return ( $reverse_arr == false ) ? $deepestTerm : array_reverse($deepestTerm);
        } else {
            return array();
        }
    }

}


/* * ********************** */
/* Upload job attachments  */
/* * ********************** */
add_action('rest_api_init', 'nokriAPI_upload_attachments_hook', 0);

function nokriAPI_upload_attachments_hook() {
    register_rest_route(
            'nokri/v1', '/job_post/upload_attach/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_upload_attachments',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
    ));
}

if (!function_exists('nokriAPI_upload_attachments')) {

    function nokriAPI_upload_attachments($request) {
        global $nokri;
        global $nokriAPI;
        $json_data = $request->get_json_params();
        $user_id = get_current_user_id();
        //NOKRI_API_ALLOW_EDITING

        $job_id = isset($_POST['job_id']) ? $_POST['job_id'] : '';
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }


        require_once( ABSPATH . 'wp-admin/includes/image.php' );
        require_once( ABSPATH . 'wp-admin/includes/file.php' );
        require_once( ABSPATH . 'wp-admin/includes/media.php' );



        $size_arr = explode('-', $nokriAPI['api_upload_portfolio_size']);
        $display_size = $size_arr[1];
        $actual_size = $size_arr[0];

        if ($job_id == '') {

            return array('success' => false, 'data' => '', 'message' => __("Something Went Wrong", "nokri-rest-api"), 'extras' => '');
        }

        if (!isset($_FILES)) {

            return array('success' => false, 'data' => '', 'message' => __("No File Found", "nokri-rest-api"), 'extras' => '');
        }
        if ($_POST['job_id'] != "") {
            $job_id = $_POST['job_id'];
        } else {
            $job_id = get_user_meta(get_current_user_id(), 'ad_in_progress', true);
        }



        $formats = array();
        $is_valid = false;
        if (!empty($nokri['sb_upload_attach_format'])) {
            foreach ($nokri['sb_upload_attach_format'] as $key => $value) {
                $formats[] = $value;
            }
        }


        $user_resume = get_post_meta($job_id, '_job_attachment', true);
        if ($user_resume != "") {
            $media = explode(',', $user_resume);
            if (count($media) >= $nokri['sb_upload_attach_limit']) {
                return array('success' => false, 'data' => '', 'message' => __("You can not upload more than", "nokri-rest-api") . " " . $nokri['sb_upload_attach_limit'] . esc_html__("attachments ", 'nokri-rest-api'), 'extras' => '');
            }
        }


        foreach ($_FILES as $key => $value) {
            $fileName = $value['name'];
            $size = $value['size'];
            $tmp = explode('.', $fileName);
            $imageFileType = isset($tmp[1]) ? $tmp[1] : '';
            if (in_array($imageFileType, $formats)) {
                $is_valid = true;
            }
            if (!($is_valid)) {
                
                return array('success' => false, 'data' => '', 'message' => __("Sorry " . $imageFileType . " file type not allowed", "nokri"), 'extras' => '');
            }

            if ($size > $actual_size) {
                return array('success' => false, 'data' => '', 'message' => __("Max allowed image size is", "nokri-rest-api") . " " . $display_size, 'extras' => '');
            }

            $attachment_id = media_handle_upload($key, 0);
            if (is_wp_error($attachment_id)) {

                return array('success' => false, 'data' => '', 'message' => __("File is empty", "nokri-rest-api"), 'extras' => '');
            }
            $user_resume = get_post_meta($job_id, '_job_attachment', true);
            if ($user_resume != "") {
                $updated_resume = $user_resume . ',' . $attachment_id;
            } else {
                $updated_resume = $attachment_id;
            }
            if (is_numeric($attachment_id)) {
                update_post_meta($job_id, '_job_attachment', $updated_resume);
            }
        }
        $data = array();
        $job_attachments = get_post_meta($job_id, '_job_attachment', true);
        if ($job_attachments != "") {

            $portfolios = explode(',', $job_attachments);
            foreach ($portfolios as $portfolio) {
                $portfolio_image = wp_get_attachment_image_src($portfolio, '');
                $data['img'][] = nokriAPI_canidate_fields('Img_url', $portfolio_image[0], $portfolio, '', 1, 'Img_url');
            }
        } else {
            $data['img'] = array();
        }

        return array('success' => true, 'data' => $data, 'message' => __("Successfully uploaded", "nokri-rest-api"), 'extras' => $job_id);
    }

}


/* * ********************** */
/* Get job attachments  */
/* * ********************** */
add_action('rest_api_init', 'nokriAPI_get_attachments_hook', 0);

function nokriAPI_get_attachments_hook() {
    register_rest_route(
            'nokri/v1', '/job_post/get_attach/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_get_attachments',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
    ));
}

if (!function_exists('nokriAPI_get_attachments')) {

    function nokriAPI_get_attachments($request) {
        $json_data = $request->get_json_params();
        if ($json_data['job_id'] != "") {
            $job_id = $json_data['job_id'];
        } else {
            $job_id = get_post_meta(get_current_user_id(), 'ad_in_progress', true);
        }

        $ids = get_post_meta($job_id, '_job_attachment', true);
        if (!$ids)
            return '';
        $ids_array = explode(',', $ids);
        $result = array();
        $cv_icon = '';
        foreach ($ids_array as $m) {
            $obj = array();
            $array = explode('.', get_attached_file($m));
            $extension = end($array);
            $obj['name'] = basename(get_attached_file($m));
            $obj['url'] = wp_get_attachment_url($m);
            $obj['extension'] = end($array);
            $obj['attach_id'] = $m;
            $result[] = $obj;
        }
        return array('success' => true, 'data' => $result, 'message' => '', 'extras' => '');
    }

}

/* * ********************** */
/* Dell job attachments  */
/* * ********************** */
add_action('rest_api_init', 'nokriAPI_del_attachments_hook', 0);

function nokriAPI_del_attachments_hook() {
    register_rest_route(
            'nokri/v1', '/job_post/dell_attach/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_del_attachments',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
    ));
}

if (!function_exists('nokriAPI_del_attachments')) {

    function nokriAPI_del_attachments($request) {
        $json_data = $request->get_json_params();
        //NOKRI_API_ALLOW_EDITING
        $mode = nokriAPI_allow_editing($request->get_method());
        if (isset($mode) && count($mode) > 0) {
            return $mode;
        }
        $attach_id = (isset($json_data['attach_id'])) ? trim($json_data['attach_id']) : '';
        $job_id = (isset($json_data['job_id'])) ? trim($json_data['job_id']) : '';
        /* Getting Portfolio */
        $attachmentid = $attach_id;
        if (get_post_meta($job_id, '_job_attachment', true) != "") {
            $ids = get_post_meta($job_id, '_job_attachment', true);
            $res = str_replace($attachmentid, "", $ids);
            $res = str_replace(',,', ",", $res);
            $img_ids = trim($res, ',');
            update_post_meta($job_id, '_job_attachment', $img_ids);
        }
        wp_delete_attachment($attachmentid, true);
        $message = __("Successfully Deleted", "nokri-rest-api");

        return $response = array('success' => true, 'data' => '', 'message' => $message);
    }

}


/* * ********************** */
/* Getting All resumes   */
/* * ********************** */
add_action('rest_api_init', 'nokriAPI_employer_all_resumes_hook', 0);

function nokriAPI_employer_all_resumes_hook() {

    /* Job Post */
    register_rest_route(
            'nokri/v1', '/employer/all_resumes/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'get_all_jobs_resumes_list',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

function get_all_jobs_resumes_list($request) {
    global $nokriAPI;

    global $wpdb;
    $json_data = $request->get_json_params();
    $status_id = (isset($json_data['status_id'])) ? trim($json_data['status_id']) : '0';
    $term_id = (isset($json_data['term_id'])) ? trim($json_data['term_id']) : '';
    $emp_id = get_current_user_id();
    $emp_data = get_userdata($emp_id);
    $emp_name = $emp_data->display_name;


    if ($term_id != '') {
        $tax_query = array(
            array(
                'taxonomy' => 'job_category',
                'terms' => $term_id,
                'field' => 'id',
            ),
        );
    }

    $args = array(
        'post_type' => 'job_post',
        'orderby' => 'date',
        'author' => $emp_id,
        'fields' => 'ids',
        'post_status' => array('publish'),
        'numberposts' => -1,
        'tax_query' => ($tax_query),
        'meta_query' => array(
            'relation' => 'AND',
            array(
                'key' => '_job_status',
                'value' => 'active',
                'compare' => '='
            )
        ),
    );

    $args = nokri_wpml_show_all_posts_callback($args);
    $job_ids = get_posts($args);
    $job_counter = count($job_ids);
    $message = '';
    if ($job_counter == '') {


        $message = esc_html__('No job found', 'nokri-rest-api');
    }

    $applier = array();
    $status_wise = false;
    $data = array();
    $data_arr = array();


    foreach ($job_ids as $job_id) {

        $applier = get_all_applier_single_job($job_id, $status_id);

        if (!empty($applier)) {
            $args = array('include' => $applier, 'order ' => 'ASC',);

            $user_query = new WP_User_Query($args);
            $authors = $user_query->get_results();



            $ad_mapLocation = get_post_meta($job_id, '_job_address', true);

            $location_array = explode(' ', $ad_mapLocation);
            $array_length = is_array($location_array) && !empty($location_array) ? count($location_array) : array();
            $city_name = isset($location_array[$array_length - 2]) ? $location_array[$array_length - 2] : '';

            $author_name = get_the_author_meta('display_name', $receiver_id);


            $cand = array();
            $cand_data = array();

            foreach ($authors as $author) {
                // get all the user's id's  

                $candidate_id = ($author->ID);
                $user = get_userdata($candidate_id);
                $display_name = $user->display_name;
                $cand_status = get_post_meta($job_id, '_job_applied_status_' . $candidate_id, true);
                $cand_profile_image = get_user_meta($candidate_id, '_sb_user_pic', true);

                $cand_address = get_user_meta($candidate_id, '_cand_address', true);

                $image_dp_link = '';
                if (isset($nokriAPI['sb_user_dp']['url']) && $nokriAPI['sb_user_dp']['url'] != "") {
                    $image_dp_link = array($nokriAPI['sb_user_dp']['url']);
                    $image_dp_link = $image_dp_link[0];
                }
                if ($cand_profile_image != '') {
                    $attach_dp_id = get_user_meta($user_id, '_sb_user_pic', true);
                    $image_dp_link = wp_get_attachment_image_src($attach_dp_id, '');
                    $image_dp_link = $image_dp_link[0];
                }

                $cand['candidate_name'] = $display_name;
                $cand['cand_profile'] = $image_dp_link;
                $cand['cand_status'] = $cand_status;
                $cand['job_title'] = $author_name . " " . str_replace(',', ' ', $city_name);
                $cand['job_id'] = $job_id;
                $cand['canidate_id'] = $author->ID;
                $cand['cand_adress'] = $cand_address;
                $cand['job_adress'] = $ad_mapLocation;

                if ($cand_address != "" && $ad_mapLocation != "") {

                    $cand['distance_user'] = getDistance($cand_address, $ad_mapLocation, 'K');
                }

                $data[] = $cand;
            }
            if (empty($data)) {


                $message = esc_html__('No candidate found', 'nokri-rest-api');
            }
        }
    }

    $data_arr['candidates'] = $data;
    $response = array('success' => true, 'data' => $data_arr, 'message' => $message, 'user_counter' => count($data));

    return $response;
}

//all_accepted_candidate
add_action('rest_api_init', 'nokriAPI_employer_all_accepted_resumes_hook', 0);

function nokriAPI_employer_all_accepted_resumes_hook() {

    /* Job Post */
    register_rest_route(
            'nokri/v1', '/employer/accepted_resumes/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'get_all_jobs_accepted_resumes_list',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

function get_all_jobs_accepted_resumes_list($request) {

    global $nokriAPI;

    global $wpdb;
    $json_data = $request->get_json_params();
    $status_id = (isset($json_data['status_id'])) ? trim($json_data['status_id']) : '5';
    $term_id = (isset($json_data['term_id'])) ? trim($json_data['term_id']) : '';
    $emp_id = get_current_user_id();
    $emp_data = get_userdata($emp_id);
    $emp_name = $emp_data->display_name;


    if ($term_id != '') {
        $tax_query = array(
            array(
                'taxonomy' => 'job_category',
                'terms' => $term_id,
                'field' => 'id',
            ),
        );
    }

    $args = array(
        'post_type' => 'job_post',
        'orderby' => 'date',
        'author' => $emp_id,
        'fields' => 'ids',
        'post_status' => array('publish'),
        'numberposts' => -1,
        'tax_query' => ($tax_query),
        'meta_query' => array(
            'relation' => 'AND',
            array(
                'key' => '_job_status',
                'value' => 'active',
                'compare' => '='
            )
        ),
    );

    $args = nokri_wpml_show_all_posts_callback($args);
    $job_ids = get_posts($args);
    $job_counter = count($job_ids);
    $message = '';
    if ($job_counter == '') {


        $message = esc_html__('No candidte found', 'nokri-rest-api');
    }

    $applier = array();
    $status_wise = false;
    $data = array();
    $data_arr = array();


    foreach ($job_ids as $job_id) {

        $applier = get_all_applier_single_job($job_id, $status_id);

        if (!empty($applier)) {
            $args = array('include' => $applier, 'order ' => 'ASC',);

            $user_query = new WP_User_Query($args);
            $authors = $user_query->get_results();



            $ad_mapLocation = get_post_meta($job_id, '_job_address', true);

            $location_array = explode(' ', $ad_mapLocation);
            $array_length = is_array($location_array) && !empty($location_array) ? count($location_array) : array();
            $city_name = isset($location_array[$array_length - 2]) ? $location_array[$array_length - 2] : '';

            $author_name = get_the_author_meta('display_name', $receiver_id);


            $cand = array();
            $cand_data = array();

            foreach ($authors as $author) {
                // get all the user's id's  

                $candidate_id = ($author->ID);
                $user = get_userdata($candidate_id);
                $display_name = $user->display_name;
                $cand_status = get_post_meta($job_id, '_job_applied_status_' . $candidate_id, true);
                $cand_profile_image = get_user_meta($candidate_id, '_sb_user_pic', true);

                $cand_address = get_user_meta($candidate_id, '_cand_address', true);

                $image_dp_link = '';
                if (isset($nokriAPI['sb_user_dp']['url']) && $nokriAPI['sb_user_dp']['url'] != "") {
                    $image_dp_link = array($nokriAPI['sb_user_dp']['url']);
                    $image_dp_link = $image_dp_link[0];
                }
                if ($cand_profile_image != '') {
                    $attach_dp_id = get_user_meta($user_id, '_sb_user_pic', true);
                    $image_dp_link = wp_get_attachment_image_src($attach_dp_id, '');
                    $image_dp_link = $image_dp_link[0];
                }

                $cand['candidate_name'] = $display_name;
                $cand['cand_profile'] = $image_dp_link;
                $cand['cand_status'] = $cand_status;
                $cand['job_title'] = $author_name . " " . str_replace(',', '', $city_name);
                $cand['job_id'] = $job_id;
                $cand['canidate_id'] = $author->ID;
                $cand['cand_adress'] = $cand_address;
                $cand['job_adress'] = $ad_mapLocation;

                if ($cand_address != "" && $ad_mapLocation != "") {

                    $cand['distance_user'] = getDistance($cand_address, $ad_mapLocation, 'K');
                }

                $data[] = $cand;
            }

            if (empty($data)) {


                $message = esc_html__('No candidate found', 'nokri-rest-api');
            }
        }
    }

    $data_arr['candidates'] = $data;
    $response = array('success' => true, 'data' => $data_arr, 'message' => $message);

    return $response;
}

function get_all_applier_single_job($job_id, $staus_id) {

    global $wpdb;
    $applier = array();
    $extra = "AND meta_key LIKE '_job_applied_status_%' AND meta_value =  $staus_id ";
    $query2 = "SELECT * FROM $wpdb->postmeta WHERE post_id = $job_id $extra";
    $applier_resumes = $wpdb->get_results($query2);
    if (count($applier_resumes) != 0) {
        if (count($applier_resumes) > 0) {
            foreach ($applier_resumes as $resumes) {

                $array_data = explode('_', $resumes->meta_key);
                $applier[] = $array_data[4];
            }
        }
    }
    return $applier;
}

/* getting Job categories* */

add_action('rest_api_init', 'nokriAPI_get_job_category', 0);

function nokriAPI_get_job_category() {

    register_rest_route('nokri/v1', '/job_cats/',
            array(
                'methods' => WP_REST_Server::READABLE,
                'callback' => 'nokriApi_jobs_all_cat',
                'permission_callback' => function () {
                    return true;
                },
            )
    );
}

function nokriApi_jobs_all_cat() {

    $data = array();
    $job_category = __("Job Category", "nokri-rest-api");
    $job_category_field = nokriAPI_canidate_fields($job_category, nokriAPI_job_post_taxonomies('job_category', ''), 'textfield', true, __("Select an option", "nokri-rest-api"), 'job_category');
    $data['categories'] = $job_category_field;
    $data["cat_plc"] = "Select category";

    return $response = array('success' => true, 'data' => $data, 'message' => $message);
}

/**  changing status of candidate    * */
add_action('rest_api_init', 'nokriAPI_changing_canidate_status_accept_reject_hook', 0);

function nokriAPI_changing_canidate_status_accept_reject_hook() {

    /* Job Post */
    register_rest_route(
            'nokri/v1', '/candidate_resume_status', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'change_candidate_applier_status',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

function change_candidate_applier_status($request) {

    global $wpdb;
    $json_data = $request->get_json_params();
    $cand_status = (isset($json_data['cand_status'])) ? trim($json_data['cand_status']) : '';
    $cand_Id = (isset($json_data['cand_Id'])) ? trim($json_data['cand_Id']) : '';
    $job_id = (isset($json_data['job_id'])) ? trim($json_data['job_id']) : '';

    $message = '';


    if ($cand_status != '' && $cand_Id != '' && $job_id != '') {

        update_post_meta($job_id, '_job_applied_status_' . $cand_Id, $cand_status);

        if ($cand_status == '5') {

            $message = esc_html__('Accepted succesfully', 'nokri');
        }
        if ($cand_status == '2') {

            $message = esc_html__('Rejected Succesfuly', 'nokri');
        }
    }


    return $response = array('success' => true, 'data' => '', 'message' => $message);
}

function getDistance($addressFrom, $addressTo, $unit = '') {
    // Google API key

    global $nokri;
    $gmap_api_key = $nokri['gmap_api_key'];
    $apiKey = $gmap_api_key != '' ? $gmap_api_key : '';



    // Change address format
    $formattedAddrFrom = str_replace(' ', '+', $addressFrom);
    $formattedAddrTo = str_replace(' ', '+', $addressTo);

    // Geocoding API request with start address
    $geocodeFrom = file_get_contents('https://maps.googleapis.com/maps/api/geocode/json?address=' . $formattedAddrFrom . '&sensor=false&key=' . $apiKey);
    $outputFrom = json_decode($geocodeFrom);
    if (!empty($outputFrom->error_message)) {
        return $outputFrom->error_message;
    }

    // Geocoding API request with end address
    $geocodeTo = file_get_contents('https://maps.googleapis.com/maps/api/geocode/json?address=' . $formattedAddrTo . '&sensor=false&key=' . $apiKey);
    $outputTo = json_decode($geocodeTo);
    if (!empty($outputTo->error_message)) {
        return $outputTo->error_message;
    }

    // Get latitude and longitude from the geodata
    $latitudeFrom = $outputFrom->results[0]->geometry->location->lat;
    $longitudeFrom = $outputFrom->results[0]->geometry->location->lng;
    $latitudeTo = $outputTo->results[0]->geometry->location->lat;
    $longitudeTo = $outputTo->results[0]->geometry->location->lng;

    // Calculate distance between latitude and longitude
    $theta = $longitudeFrom - $longitudeTo;
    $dist = sin(deg2rad($latitudeFrom)) * sin(deg2rad($latitudeTo)) + cos(deg2rad($latitudeFrom)) * cos(deg2rad($latitudeTo)) * cos(deg2rad($theta));
    $dist = acos($dist);
    $dist = rad2deg($dist);
    $miles = $dist * 60 * 1.1515;

    // Convert unit and return distance
    $unit = strtoupper($unit);
    if ($unit == "K") {
        return round($miles * 1.609344, 2) . ' km';
    } elseif ($unit == "M") {
        return round($miles * 1609.344, 2) . ' meters';
    } else {
        return round($miles, 2) . ' miles';
    }
}


// Neary jobs on job details 

if(!function_exists('nokriAPI_nearby_jobs_api_fun')){
     
    function nokriAPI_nearby_jobs_api_fun($job_id){   
        
         $job_id   =  $job_id ;   
        $data    =  array();
        $message   =   '';       
        if($job_id ==  ""){          
            $message   = esc_html__('Please enter job id','nokri-rest-api');
            return array('success' => false ,'data' => $data  ,'message' => $message);
        }       
        $latitude         =      get_post_meta($job_id, '_job_lat', true);
        $longitude      =      get_post_meta($job_id, '_job_long', true);               
        $distance       =    ( isset($nokri['nearby_jobs_distance']) && $nokri['nearby_jobs_distance'] != ""  ) ? $nokri['nearby_jobs_distance'] : 10;
        $num_of_jobs    =    ( isset($nokri['nearby_jobs_numbers']) && $nokri['nearby_jobs_numbers'] != ""  ) ? $nokri['nearby_jobs_numbers'] : 5;
        $unit           =    ( isset($nokri['nearby_jobs_unit']) && $nokri['nearby_jobs_unit'] != ""  ) ? $nokri['nearby_jobs_unit'] : 'km';       
        $data_array     =  array();
         if (!empty($latitude) && !empty($longitude)) {
                 $data_array = array("latitude" => $latitude, "longitude" => $longitude, "distance" => $distance);
               }
             $type_lat = "'DECIMAL'";
             $type_lon = "'DECIMAL'";
             $lats_longs = nokri_radius_search_theme($data_array, false);  
             $jobs   =  array();                                      
             if (!empty($lats_longs) && count((array) $lats_longs) > 0) {
                            if ($latitude > 0) {
                                $lat_lng_meta_query[] = array(
                                    'key' => '_job_lat',
                                    'value' => array($lats_longs['lat']['min'], $lats_longs['lat']['max']),
                                    'compare' => 'BETWEEN',
                                );
                            } else {
                                $lat_lng_meta_query[] = array(
                                    'key' => '_job_lat',
                                    'value' => array($lats_longs['lat']['max'], $lats_longs['lat']['min']),
                                    'compare' => 'BETWEEN',
                                );
                            }
                            if ($longitude > 0) {
                                $lat_lng_meta_query[] = array(
                                    'key' => '_job_long',
                                    'value' => array($lats_longs['long']['min'], $lats_longs['long']['max']),
                                    'compare' => 'BETWEEN',
                                );
                            } else {
                                $lat_lng_meta_query[] = array(
                                    'key' => '_job_long',
                                    'value' => array($lats_longs['long']['max'], $lats_longs['long']['min']),
                                    'compare' => 'BETWEEN',
                                );
                            }
                            $args = array(
                                'posts_per_page' => $num_of_jobs,
                                'post_type' => 'job_post',
                                'post_status' => 'publish',
                                'order' => 'DESC',
                                'orderby' => 'date',
                                'post__not_in' => array($job_id),
                                'meta_query' => array(
                                    array(
                                        'key' => '_job_status',
                                        'value' => 'active',
                                        'compare' => '=',
                                    ),
                                    $lat_lng_meta_query,
                                ),
                            );
                            $args = nokri_wpml_show_all_posts_callback($args);
                            $results = new WP_Query($args);                       
                           
                            $count   =   0;
                           
                            if ($results->have_posts()) {
                                while ($results->have_posts()) {
                                    $results->the_post();
                                    $job_id = get_the_ID();
                                    
                                    $post_author_id = get_post_field('post_author', $job_id);
                                    $company_name = get_the_author_meta('display_name', $post_author_id);
                                    
                                    $image_link[0] = get_template_directory_uri() . '/images/candidate-dp.jpg';

                                    if (get_user_meta($post_author_id, '_sb_user_pic', true) != "") {
                                        $attach_id = get_user_meta($post_author_id, '_sb_user_pic', true);
                                        if (is_numeric($attach_id)) {
                                            $image_link = wp_get_attachment_image_src($attach_id, 'nokri_job_post_single');
                                        } else {
                                            $image_link[0] = $attach_id;
                                        }
                                    }                                                          
                                   $latitude1    =      get_post_meta($job_id, '_job_lat', true);
                                   $longitude1   =      get_post_meta($job_id, '_job_long', true);
                                   $calculated_distance    = nokri_nearby_distance($latitude, $longitude, $latitude1, $longitude1, $unit); 
                                   $final_distance          =   $calculated_distance . $distance_unit;                                                              
                                   $jobs[$count]['job_id']      =    $job_id;
                                   $jobs[$count]['job_title']   = get_the_title();
                                   $jobs[$count]['comp_name']   =    $company_name;
                                   $jobs[$count]['comp_img']    =    $image_link[0];
                                   $jobs[$count]['distance']    =    $final_distance;
                                    
                                    $count++;
                                }
                                wp_reset_postdata();
                            }
                             
                        }
                        
                   return $jobs;
    }
}







