<?php

add_action('rest_api_init', 'nokriAPI_job_search_hook', 0);

function nokriAPI_job_search_hook() {

    register_rest_route(
            'nokri/v1', '/job_search/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_job_search_get',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );


    register_rest_route(
            'nokri/v1', '/job_search/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_job_search',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_job_search_get')) {

    function nokriAPI_job_search_get() {


        global $nokriAPI;
        global $nokri;

        /* Location headings */
        $job_country_level_1 = ( isset($nokriAPI['API_job_country_level_1']) && $nokriAPI['API_job_country_level_1'] != "" ) ? $nokriAPI['API_job_country_level_1'] : '';

        $job_country_level_2 = ( isset($nokriAPI['API_job_country_level_2']) && $nokriAPI['API_job_country_level_2'] != "" ) ? $nokriAPI['API_job_country_level_2'] : '';

        $job_country_level_3 = ( isset($nokriAPI['API_job_country_level_3']) && $nokriAPI['API_job_country_level_3'] != "" ) ? $nokriAPI['API_job_country_level_3'] : '';

        $job_country_level_4 = ( isset($nokriAPI['API_job_country_level_4']) && $nokriAPI['API_job_country_level_4'] != "" ) ? $nokriAPI['API_job_country_level_4'] : '';

        $map_heading = ( isset($nokriAPI['API_job_map_heading_txt']) && $nokriAPI['API_job_map_heading_txt'] != "" ) ? $nokriAPI['API_job_map_heading_txt'] : '';





        $job_title = __("Job Title", "nokri-rest-api");
        $job_title_field = nokriAPI_canidate_fields($job_title, __("Search Keyword", "nokri-rest-api"), 'textfield', true, 2, 'job_title');

        $job_category = __("Job Category", "nokri-rest-api");
        $job_category_field = nokriAPI_canidate_fields($job_category, nokriAPI_job_post_taxonomies('job_category', ''), 'textfield', true, __("Select an option", "nokri-rest-api"), 'job_category');

        $job_type = __("Job Type", "nokri-rest-api");
        $job_type_field = nokriAPI_canidate_fields($job_type, nokriAPI_job_post_taxonomies('job_type', ''), 'textfield', true, 2, 'job_type');


        $job_qualifications = __("Job Qualifications", "nokri-rest-api");
        $job_qualifications_field = nokriAPI_canidate_fields($job_qualifications, nokriAPI_job_post_taxonomies('job_qualifications', ''), 'textfield', true, 2, 'job_qualifications');

        $job_level = __("Job Level", "nokri-rest-api");
        $job_level_field = nokriAPI_canidate_fields($job_level, nokriAPI_job_post_taxonomies('job_level', ''), 'textfield', true, 2, 'job_level');


        $salary_offer = __("Salary Offer", "nokri-rest-api");
        $job_salary_offer_field = nokriAPI_canidate_fields($salary_offer, nokriAPI_job_post_taxonomies('job_salary', ''), 'textfield', true, 2, 'salary_offer');

        $salary_offer = __("Salary Type", "nokri-rest-api");
        $job_salary_offer_field = nokriAPI_canidate_fields($salary_offer, nokriAPI_job_post_taxonomies('job_salary_type', ''), 'textfield', true, 2, 'job_salary_type');

        $job_experience = __("Job Experience", "nokri-rest-api");
        $job_experience_field = nokriAPI_canidate_fields($job_experience, nokriAPI_job_post_taxonomies('job_experience', ''), 'textfield', true, 2, 'job_experience');

        $salary_currency = __("Salary Currency", "nokri-rest-api");
        $job_currency_field = nokriAPI_canidate_fields($salary_currency, nokriAPI_job_post_taxonomies('job_currency', ''), 'textfield', true, 2, 'salary_currency');

        $job_shift = __("Job Shift", "nokri-rest-api");
        $job_shift_field = nokriAPI_canidate_fields($job_shift, nokriAPI_job_post_taxonomies('job_shift', ''), 'textfield', true, 2, 'job_shift');

        $job_skills = __("Job Skills", "nokri-rest-api");
        $job_skills_field = nokriAPI_canidate_fields($job_skills, nokriAPI_job_selected_skills('job_skills', '_job_skills', ''), 'textfield', true, 2, 'job_skills');

        $job_tags = __("Job Tags", "nokri-rest-api");
        $job_tag_field = nokriAPI_canidate_fields($job_tags, nokriAPI_job_selected_skills('job_tags', '_job_tags', ''), 'textfield', true, 2, 'job_tags');

        $job_location = __("Location", "nokri-rest-api");
        $job_location_field = nokriAPI_canidate_fields($job_location, nokriAPI_job_post_taxonomies('ad_location', ''), 'textfield', true, __("Select an option", "nokri-rest-api"), 'job_location');

        $email_frequency = __("Email Frquency", "nokri-rest-api");
        $frequency_option = array();
        $frequency_option [] = array("key" => 1, "value" => __('Daily', 'nokri'));
        $frequency_option [] = array("key" => 7, "value" => __('Weekly', 'nokri'),);
        $frequency_option [] = array("key" => 15, "value" => __('Fortnightly', 'nokri'),);
        $frequency_option [] = array("key" => 30, "value" => __('Monthly', 'nokri'),);
        $frequency_option [] = array("key" => 23, "value" => __('Yearly', 'nokri'),);
        $email_frequency_field = nokriAPI_canidate_fields($email_frequency, $frequency_option, 'textfield', true, __("Select an option", "nokri-rest-api"), 'email_freq');
        $job_post_btn = __("Search Now", "nokri-rest-api");
        $job_search_btn_field = nokriAPI_canidate_fields($job_post_btn, '', 'textfield', true, 2, 'job_post_btn');

        $job_search_cat = __("Select Sub Category", "nokri-rest-api");
        $job_search_cat_field = nokriAPI_canidate_fields($job_search_cat, '', 'textfield', true, 2, 'job_search_cat');

        $labels['search_fields'] = array($job_category_field, $job_qualifications_field, $job_type_field, $job_currency_field, $job_shift_field, $job_level_field, $job_skills_field, $job_tag_field, $job_location_field, $email_frequency_field, $job_experience_field);

        $country = nokriAPI_canidate_fields($job_country_level_1, '', 'textfield', true, 2, 'country');

        $state = nokriAPI_canidate_fields($job_country_level_2, '', 'textfield', true, 2, 'state');

        $city = nokriAPI_canidate_fields($job_country_level_3, '', 'textfield', true, 2, 'city');

        $town = nokriAPI_canidate_fields($job_country_level_4, '', 'textfield', true, 2, 'town');

        $page_title = nokriAPI_canidate_fields(__("Advanced Job Search", "nokri-rest-api"), '', 'textfield', true, 2, 'page_title');

        $job_alerts_swith = ( isset($nokri['job_alerts_switch']) && $nokri['job_alerts_switch'] != "" ) ? $nokri['job_alerts_switch'] : "";
        $job_alerts_swith_value = "false";
        $job_alerts_type_value = "false";
        $job_alerts_cat_value = "false";
        $job_alerts_location_value = "false";
        $job_alerts_experience_value = "false";
        
        $job_alerts_geo_location = "false";
        
        
   
        if ($job_alerts_swith == "1") {

            $job_alerts_swith_value = "true";


            if (nokri_add_taxonomies_on_job_alert('job_type', true)) {
                $job_alerts_type_value = "true";
            }
            if (nokri_add_taxonomies_on_job_alert('job_experience', true)) {
                $job_alerts_experience_value = "true";
            }
            if (nokri_add_taxonomies_on_job_alert('ad_location', true)) {
                $job_alerts_location_value = "true";
            }
            if (nokri_add_taxonomies_on_job_alert('job_category', true)) {
                $job_alerts_cat_value = "true";
            }
            
            
            if (nokri_add_taxonomies_on_job_alert('ad_geo_location', true)) {
                $job_alerts_geo_location = "true";
            }
        }

        $job_alerts = nokriAPI_canidate_fields(__("Jobs alert", "nokri-rest-api"), $job_alerts_swith_value, 'boolean', true, 2, 'is_job_alert');
        $job_alerts_type = nokriAPI_canidate_fields(__("Job type", "nokri-rest-api"), $job_alerts_type_value, 'boolean', true, 2, 'job_alerts_type');
        $job_alerts_cat = nokriAPI_canidate_fields(__("Jobs category", "nokri-rest-api"), $job_alerts_cat_value, 'boolean', true, 2, 'job_alerts_cat');
        $job_alerts_location = nokriAPI_canidate_fields(__("Jobs Location", "nokri-rest-api"), $job_alerts_location_value, 'boolean', true, 2, 'job_alerts_location');
        $job_alerts_exp = nokriAPI_canidate_fields(__("Jobs Experience", "nokri-rest-api"), $job_alerts_experience_value, 'boolean', true, 2, 'job_alerts_exp');
      
        $job_alerts_geo_locations = nokriAPI_canidate_fields(__("Geo Location", "nokri-rest-api"), $job_alerts_geo_location, 'boolean', true, 2, 'job_alerts_geo_location');
        $extra = array($job_title_field, $job_search_btn_field, $job_search_cat_field, $country, $state, $city, $town, $page_title, $job_alerts, $job_alerts_type, $job_alerts_cat, $job_alerts_location, $job_alerts_exp,$job_alerts_geo_locations);

        return $response = array('success' => true, 'data' => $labels, 'extra' => $extra);
    }

}


if (!function_exists('nokriAPI_job_search')) {

    function nokriAPI_job_search($request) {
        $json_data = $request->get_json_params();

        global $nokriAPI;
        $tax = (isset($json_data['job_tax']) && $json_data['job_tax'] != "" ) ? $json_data['job_tax'] : '';
        /* Getting All Taxonomy From Query String */
        $taxonomies = array('job_type', 'job_category', 'job_qualifications', 'job_level', 'job_salary', 'job_skills', 'job_experience', 'job_currency', 'job_shift', 'job_class', 'ad_location');
        $tax_meta = array();
        foreach ($taxonomies as $tax) {

            if (isset($json_data[$tax])) {
                $tax_meta[] = array('taxonomy' => $tax, 'field' => 'term_id', 'terms' => $json_data[$tax]);
            }
        }


        /* Getting Title */
        $title = (isset($json_data['job_title']) && $json_data['job_title'] != "" ) ? $json_data['job_title'] : '';


        /* Radius search starts */
        if (!empty($json_data['e_lat']) && !empty($json_data['e_long'])) {
            $latitude = $json_data['e_lat'];
            $longitude = $json_data['e_long'];
        }
        if (!empty($latitude) && !empty($longitude)) {
            $distance = '30';
            if (!empty($json_data['e_distance']) && !empty($json_data['e_distance'])) {
                $distance = $json_data['e_distance'];
            }

            $data_array = array("latitude" => $latitude, "longitude" => $longitude, "distance" => $distance);
            $type_lat = "'DECIMAL'";
            $type_lon = "'DECIMAL'";
            $lats_longs = nokriAPI_radius_search($data_array, false);

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
            }
        }

        /* Radius search ends */

        /* Getting Pagination */
        add_action('pre_get_posts', 'my_pre_get_posts');
        $jobs = array();
        $orderBy = $order = '';
        if (get_query_var('paged')) {
            $paged = get_query_var('paged');
        } else if (isset($json_data['page_number'])) {
            // This will occur if on front page.
            $paged = $json_data['page_number'];
        } else {
            $paged = 1;
        }
        /* Search Query */
        $args = array(
            's' => $title,
            'posts_per_page' => get_option('posts_per_page'),
            'post_type' => 'job_post',
            'post_status' => 'publish',
            'order' => $order,
            'orderby' => $orderBy,
            'paged' => $paged,
            'meta_query' => array(
                'relation' => 'AND',
                array(
                    'key' => '_job_status',
                    'value' => 'active',
                    'compare' => '='
                ),
                $lat_lng_meta_query,
            ),
            //'meta_query' 		=> 	array('key'     => '_job_status','value'   => 'active','compare' => '=',$lat_lng_meta_query), 
            'tax_query' => ($tax_meta),
        );

        $args = nokri_wpml_show_all_posts_callback($args);
        $query = new WP_Query($args);


        $message = '';
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
                $job_deadline_n = get_post_meta($job_id, '_job_date', true);
                $job_deadline = date_i18n(get_option('date_format'), strtotime($job_deadline_n));
                $job_adress = get_post_meta($job_id, '_job_address', true);
                /* Getting Company  Profile Photo */
                $comp_img = nokriAPI_employer_dp($post_author_id);

                $job_badge_ul = nokri_premium_job_class_badges($job_id);
                $is_feature = false;
                if ($job_badge_ul != '') {
                    $is_feature = true;
                }
                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Id", "nokri-rest-api"), get_the_id(), 'textfield', true, 1, 'job_id');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Company Id", "nokri-rest-api"), $post_author_id, 'textfield', true, 1, 'company_id');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Name", "nokri-rest-api"), nokriAPI_convert_uniText(get_the_title()), 'textfield', true, 1, 'job_name');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Company Name", "nokri-rest-api"), get_the_author_meta('display_name', $post_author_id), 'textfield', true, 1, 'company_name');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Salary", "nokri-rest-api"), nokri_job_post_single_taxonomies('job_currency', $job_currency) . " " . nokri_job_post_single_taxonomies('job_salary', $job_salary) . "/" . nokri_job_post_single_taxonomies('job_salary_type', $job_salary_type), 'textfield', true, 1, 'job_salary');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Type", "nokri-rest-api"), nokri_job_post_single_taxonomies('job_type', $job_type), 'textfield', true, 1, 'job_type');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Posted", "nokri-rest-api"), $job_deadline, 'textfield', true, 1, 'job_time');





                //$c_image_link = (isset($image_link[0]) && is_array($image_link) && $image_link[0] != "" ) ? $image_link[0] : '';

                $jobs[$count][] = nokriAPI_canidate_fields(__("Company Logo", "nokri-rest-api"), $comp_img['img'], 'textfield', true, 1, 'company_logo');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Job Location", "nokri-rest-api"), $job_adress, 'textfield', true, 1, 'job_location');
                $jobs[$count][] = nokriAPI_canidate_fields(__("Feature job", "nokri-rest-api"), $is_feature, 'textfield', true, 1, 'is_feature');
                $count++;
            }
        } else {
            $message = __("Sorry no job matches to your criteria", "nokri-rest-api");
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

        $data['page_title'] = __("Search Jobs", "nokri-rest-api");
        $data['jobs'] = $jobs;
        $data['number_of_jobs'] = $query->found_posts;
        $data['no_txt'] = $query->found_posts . " " . __("Jobs found", "nokri-rest-api");

        return $response = array('success' => true, 'data' => $data, 'message' => $message, "pagination" => $pagination);
    }

}
/* * ******************* */
/* Candidates search */
/* * ******************* */

add_action('rest_api_init', 'nokriAPI_candidate_search_hook', 0);

function nokriAPI_candidate_search_hook() {

    register_rest_route(
            'nokri/v1', '/candidate_search/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_candidate_search_get',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );


    register_rest_route(
            'nokri/v1', '/candidate_search/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_candidate_search',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_candidate_search_get')) {

    function nokriAPI_candidate_search_get() {
        global $nokriAPI;

        $gender_option = array();

        $gender_option[] = array(
            "key" => 1,
            "value" => "male",
            "has_child" => false,
            "selected" => false,
            "slug" => '',
        );
        $gender_option[] = array(
            "key" => 2,
            "value" => "female",
            "has_child" => false,
            "selected" => false,
            "slug" => '',
        );
        $gender_option[] = array(
            "key" => 3,
            "value" => "other",
            "has_child" => false,
            "selected" => false,
            "slug" => '',
        );

        $cand_title = __("Title", "nokri-rest-api");
        $job_title_field = nokriAPI_canidate_fields($cand_title, __("Search by name/keyword", "nokri-rest-api"), 'textfield', true, 2, 'cand_title');

        $cand_head = __("Headline", "nokri-rest-api");
        $job_head_field = nokriAPI_canidate_fields($cand_head, '', 'textfield', true, 2, 'cand_head');

        $cand_type = __("Type", "nokri-rest-api");
        $job_type_field = nokriAPI_canidate_fields($cand_type, nokriAPI_job_post_taxonomies('job_type', ''), 'textfield', true, __("Select an option", "nokri-rest-api"), 'cand_type');


        $cand_gender = __("Gender", "nokri-rest-api");
        $job_gender_field = nokriAPI_canidate_fields($cand_gender, $gender_option, 'textfield', true, __("Select an option", "nokri-rest-api"), 'cand_gender');


        $cand_experience = __("Experience", "nokri-rest-api");
        $job_experience_field = nokriAPI_canidate_fields($cand_experience, nokriAPI_job_post_taxonomies('job_experience', ''), 'textfield', true, __("Select an option", "nokri-rest-api"), 'cand_experience');



        $cand_level = __("Level", "nokri-rest-api");
        $job_level_field = nokriAPI_canidate_fields($cand_level, nokriAPI_job_post_taxonomies('job_level', ''), 'textfield', true, __("Select an option", "nokri-rest-api"), 'cand_level');

        $cand_skills = __("Skills", "nokri-rest-api");
        $job_skills_field = nokriAPI_canidate_fields($cand_skills, nokriAPI_job_post_taxonomies('job_skills', ''), 'textfield', true, __("Select an option", "nokri-rest-api"), 'cand_skills');

        $cand_qualification = __("Qualification", "nokri-rest-api");
        $job_qualification_field = nokriAPI_canidate_fields($cand_qualification, nokriAPI_job_post_taxonomies('job_qualifications', ''), 'textfield', true, __("Select an option", "nokri-rest-api"), 'cand_qualification');


        $cand_salary_range = __("Salary Range", "nokri-rest-api");
        $job_salary_range_field = nokriAPI_canidate_fields($cand_salary_range, nokriAPI_job_post_taxonomies('job_salary', ''), 'textfield', true, __("Select an option", "nokri-rest-api"), 'cand_salary_range');


        $cand_salary_type = __("Salary Type", "nokri-rest-api");
        $job_salary_type_field = nokriAPI_canidate_fields($cand_salary_type, nokriAPI_job_post_taxonomies('job_salary_type', ''), 'textfield', true, __("Select an option", "nokri-rest-api"), 'cand_salary_type');

        $cand_salary_curr = __("Salary Currency", "nokri-rest-api");
        $job_salary_curr_field = nokriAPI_canidate_fields($cand_salary_curr, nokriAPI_job_post_taxonomies('job_currency', ''), 'textfield', true, __("Select an option", "nokri-rest-api"), 'cand_salary_curr');

        $cand_location = __("Location", "nokri-rest-api");
        $job_location_field = nokriAPI_canidate_fields($cand_location, nokriAPI_job_post_taxonomies('ad_location', ''), 'textfield', true, __("Select an option", "nokri-rest-api"), 'cand_location');



        $labels['search_fields'] = array($job_title_field, $job_head_field, $job_type_field, $job_gender_field, $job_qualification_field, $job_experience_field, $job_level_field, $job_skills_field, $job_salary_range_field, $job_salary_type_field, $job_salary_curr_field, $job_location_field);


        $cand_search_title = nokriAPI_canidate_fields(__("Candidate Search", "nokri-rest-api"), '', 'textfield', true, 2, 'cand_search_title');
        $cand_search_btn = nokriAPI_canidate_fields(__("Search now", "nokri-rest-api"), '', 'textfield', true, 2, 'cand_search_now');

        $cand_search_name = nokriAPI_canidate_fields(__("Search by name", "nokri-rest-api"), '', 'textfield', true, 2, 'cand_search_name');


        /* Location headings */
        $job_country_level_1 = ( isset($nokriAPI['API_job_country_level_1']) && $nokriAPI['API_job_country_level_1'] != "" ) ? $nokriAPI['API_job_country_level_1'] : '';

        $job_country_level_2 = ( isset($nokriAPI['API_job_country_level_2']) && $nokriAPI['API_job_country_level_2'] != "" ) ? $nokriAPI['API_job_country_level_2'] : '';

        $job_country_level_3 = ( isset($nokriAPI['API_job_country_level_3']) && $nokriAPI['API_job_country_level_3'] != "" ) ? $nokriAPI['API_job_country_level_3'] : '';

        $job_country_level_4 = ( isset($nokriAPI['API_job_country_level_4']) && $nokriAPI['API_job_country_level_4'] != "" ) ? $nokriAPI['API_job_country_level_4'] : '';

        $map_heading = ( isset($nokriAPI['API_job_map_heading_txt']) && $nokriAPI['API_job_map_heading_txt'] != "" ) ? $nokriAPI['API_job_map_heading_txt'] : '';

        $country = nokriAPI_canidate_fields($job_country_level_1, '', 'textfield', true, 2, 'country');

        $state = nokriAPI_canidate_fields($job_country_level_2, '', 'textfield', true, 2, 'state');

        $city = nokriAPI_canidate_fields($job_country_level_3, '', 'textfield', true, 2, 'city');

        $town = nokriAPI_canidate_fields($job_country_level_4, '', 'textfield', true, 2, 'town');



        $extra = array($cand_search_title, $cand_search_btn, $cand_search_name, $country, $state, $city, $town,);


        return $response = array('success' => true, 'data' => $labels, 'extra' => $extra);
    }

}


if (!function_exists('nokriAPI_candidate_search')) {

    function nokriAPI_candidate_search($request) {
        $json_data = $request->get_json_params();

        /* Getting Title */
        $title = (isset($json_data['cand_title']) && $json_data['cand_title'] != "" ) ? $json_data['cand_title'] : '';
        /* Getting type */
        $type = (isset($json_data['cand_type']) && $json_data['cand_type'] != "" ) ? $json_data['cand_type'] : '';
        /* Getting level */
        $level = (isset($json_data['cand_level']) && $json_data['cand_level'] != "" ) ? $json_data['cand_level'] : '';
        /* Getting skills */
        $skills = (isset($json_data['cand_skills']) && $json_data['cand_skills'] != "" ) ? $json_data['cand_skills'] : '';
        /* Getting experience */
        $experience = (isset($json_data['cand_experience']) && $json_data['cand_experience'] != "" ) ? $json_data['cand_experience'] : '';

        /* Getting salary range */
        $cand_salaryrange = (isset($json_data['cand_salary_range']) && $json_data['cand_salary_range'] != "" ) ? $json_data['cand_salary_range'] : '';

        /* Getting salary currency */
        $cand_salarycurrency = (isset($json_data['cand_salary_curr']) && $json_data['cand_salary_curr'] != "" ) ? $json_data['cand_salary_curr'] : '';

        /* Getting salary type */
        $cand_salarytype = (isset($json_data['cand_salary_type']) && $json_data['cand_salary_type'] != "" ) ? $json_data['cand_salary_type'] : '';
        /* Getting gender */
        $cand_gender = (isset($json_data['cand_gender']) && $json_data['cand_gender'] != "" ) ? $json_data['cand_gender'] : '';

        /* Getting headline */
        $cand_headline = (isset($json_data['cand_headline']) && $json_data['cand_headline'] != "" ) ? $json_data['cand_headline'] : '';
        /* Getting qualification */
        $cand_qualification = (isset($json_data['cand_qualification']) && $json_data['cand_qualification'] != "" ) ? $json_data['cand_qualification'] : '';
        /* Getting location */
        $location = (isset($json_data['cand_location']) && $json_data['cand_location'] != "" ) ? $json_data['cand_location'] : '';



        $type_qry = '';
        if (isset($type) && $type != "") {
            $type_qry = array(
                'key' => '_cand_type',
                'value' => $type,
                'compare' => '='
            );
        }

        $level_qry = '';
        if (isset($level) && $level != "") {
            $level_qry = array(
                'key' => '_cand_level',
                'value' => $level,
                'compare' => '='
            );
        }

        $experience_qry = '';
        if (isset($experience) && $experience != "") {
            $experience_qry = array(
                'key' => '_cand_experience',
                'value' => $experience,
                'compare' => '='
            );
        }

        $skills_qry = '';
        if (isset($skills) && $skills != "") {
            $skills_qry = array(
                'key' => '_cand_skills',
                'value' => $skills,
                'compare' => 'like'
            );
        }



        $salary_qry = '';
        if (isset($cand_salaryrange) && $cand_salaryrange != "") {
            $salary_qry = array(
                'key' => '_cand_salary_range',
                'value' => $cand_salaryrange,
                'compare' => '='
            );
        }


        $salary_type_qry = '';
        if (isset($cand_salarytype) && $cand_salarytype != "") {
            $salary_type_qry = array(
                'key' => '_cand_salary_type',
                'value' => $cand_salarytype,
                'compare' => '='
            );
        }



        $salary_type_curren = '';
        if (isset($cand_salarycurrency) && $cand_salarycurrency != "") {
            $salary_type_curren = array(
                'key' => '_cand_salary_curren',
                'value' => $cand_salarycurrency,
                'compare' => '='
            );
        }


        $headline_qry = '';
        if (isset($cand_headline) && $cand_headline != "") {
            $headline_qry = array(
                'key' => '_cand_salary_curren',
                'value' => $cand_headline,
                'compare' => '='
            );
        }


        $gender_qry = '';
        $gender_value = '';

        if ($cand_gender == 1) {
            $gender_value = "male";
        } else if ($cand_gender == 2) {
            $gender_value = "female";
        } else if ($cand_gender == 3) {
            $gender_value = "other";
        }
        if (isset($gender_value) && $gender_value != "") {
            $gender_qry = array(
                'key' => '_cand_salary_curren',
                'value' => $gender_value,
                'compare' => '='
            );
        }

        $qualification_qry = '';
        if (isset($cand_qualification) && $cand_qualification != "") {
            $qualification_qry = array(
                'key' => '_cand_salary_curren',
                'value' => $cand_qualification,
                'compare' => '='
            );
        }

        $location_qry = '';
        if (isset($location) && $location != "") {
            $location_qry = array(
                'key' => '_cand_custom_location',
                'value' => $location,
                'compare' => 'like'
            );
        }
        $cands_qry = array(
            'key' => '_sb_reg_type',
            'value' => '0',
            'compare' => '='
        );



// total no of User to display
        $page_number = $paged = (isset($json_data['page_number'])) ? $json_data['page_number'] : 1;
        $current_page = $paged;
        $user_per_page = (isset($nokriAPI['api_user_pagination'])) ? $nokriAPI['api_user_pagination'] : "10";
        $paged = (int) $paged;
        $get_offset = ($paged - 1);
        $offset = $get_offset * $user_per_page;
// Query args
        $args = array(
            'search' => "*" . esc_attr($title) . "*",
            'order' => 'DESC',
            'orderby' => array(
                'meta_value',
                'registered',
            ),
            'role' => 'subscriber',
            'number' => $user_per_page,
            'offset' => $offset,
            'meta_query' => array(
                array(
                    'relation' => 'OR',
                    array(
                        'key' => '_is_candidate_featured',
                        'compare' => 'NOT EXISTS'
                    ),
                    array(
                        'key' => '_is_candidate_featured',
                        'compare' => 'EXISTS'
                    )),
                $type_qry,
                $cands_qry,
                $level_qry,
                $experience_qry,
                $skills_qry,
                $salary_qry,
                $salary_type_qry,
                $salary_type_curren,
                $headline_qry,
                $gender_qry,
                $qualification_qry,
                $location_qry
            )
        );
// Create the WP_User_Query object
        $wp_user_query = new WP_User_Query($args);
// Get the results
        $users = $wp_user_query->get_results();
        $total_users = $wp_user_query->get_total();
        $max_num_pages = ceil($total_users / $user_per_page);

        $candidates = array();
        $message = '';
        if (!empty($users)) {
            $count = 0;
            // Loop through results
            foreach ($users as $user) {
                $cand_id = $user->ID;
                $canddata = get_userdata($cand_id);
                /* Getting candiate  Profile Photo */
                $image_link[0] = get_template_directory_uri() . '/images/candidate-dp.jpg';
                if (get_user_meta($cand_id, '_cand_dp', true) != "") {
                    $attach_id = get_user_meta($cand_id, '_cand_dp', true);
                    $image_link = wp_get_attachment_image_src($attach_id, 'nokri_job_post_single');
                }

                $featured_date = get_user_meta($cand_id, '_candidate_feature_profile', true);
                $is_featured = false;
                $today = date("Y-m-d");
                $expiry_date_string = strtotime($featured_date);
                $today_string = strtotime($today);
                if ($today_string > $expiry_date_string) {
                    delete_user_meta($cand_id, '_candidate_feature_profile');
                    delete_user_meta($cand_id, '_is_candidate_featured');
                } else {
                    $is_featured = true;
                }




                $candidates[$count][] = nokriAPI_canidate_fields(__("Candidate Id", "nokri-rest-api"), $cand_id, 'textfield', true, 1, 'cand_id');
                $candidates[$count][] = nokriAPI_canidate_fields(__("Candidate DP", "nokri-rest-api"), $image_link[0], 'textfield', true, 1, 'cand_dp');
               
                
                $candidates[$count][] = nokriAPI_canidate_fields(__("Candidate Name", "nokri-rest-api"), nokri_return_dotted_name($user->display_name), 'textfield', true, 1, 'cand_name');
                
                
                
                $candidates[$count][] = nokriAPI_canidate_fields(__("Headline", "nokri-rest-api"), get_user_meta($cand_id, '_user_headline', true), 'textfield', true, 1, 'cand_headline');
                ;
                $candidates[$count][] = nokriAPI_canidate_fields(__("Location", "nokri-rest-api"), get_user_meta($cand_id, '_cand_address', true), 'textfield', true, 1, 'cand_location');

                $candidates[$count][] = nokriAPI_canidate_fields(__("Feature", "nokri-rest-api"), $is_featured, 'textfield', true, 1, 'is_featured');
                
                $count++;
            }
        } else {
            $message = __("No candidate matched", "nokri-rest-api");
        }



        /* Pagination */
        $nextPaged = (int) ($paged) + 1;
        $has_next_page = ( $nextPaged <= (int) $max_num_pages ) ? true : false;

        $pagination = array("max_num_pages" => (int) $max_num_pages, "current_page" => (int) $paged, "next_page" => (int) $nextPaged, "increment" => (int) $user_per_page, "total_followed" => (int) $total_users, "has_next_page" => $has_next_page);



        $data['candidates'] = $candidates;
        $extra['page_title'] = __("Candidates search", "nokri-rest-api");


        return $response = array('success' => true, 'data' => $data, 'message' => $message, 'extra' => $extra, "pagination" => $pagination);
    }

}

/* * ******************* */
/* Employer search   */
/* * ******************* */

add_action('rest_api_init', 'nokriAPI_employer_search_hook', 0);

function nokriAPI_employer_search_hook() {

    register_rest_route(
            'nokri/v1', '/employer_search/', array(
        'methods' => WP_REST_Server::READABLE,
        'callback' => 'nokriAPI_employer_search_get',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );


    register_rest_route(
            'nokri/v1', '/employer_search/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_employer_search',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

if (!function_exists('nokriAPI_employer_search_get')) {

    function nokriAPI_employer_search_get() {
        global $nokriAPI;



        $cand_title = __("Title", "nokri-rest-api");
        $job_title_field = nokriAPI_canidate_fields($cand_title, __("Search by name/keyword", "nokri-rest-api"), 'textfield', true, 2, 'emp_title');




        $cand_skills = __("Specialization", "nokri-rest-api");
        $job_skills_field = nokriAPI_canidate_fields($cand_skills, nokriAPI_job_post_taxonomies('job_skills', ''), 'textfield', true, __("Select an option", "nokri-rest-api"), 'cand_skills');



        $cand_location = __("Location", "nokri-rest-api");
        $job_location_field = nokriAPI_canidate_fields($cand_location, nokriAPI_job_post_taxonomies('ad_location', ''), 'textfield', true, __("Select an option", "nokri-rest-api"), 'cand_location');



        $labels['search_fields'] = array($job_title_field, $job_skills_field, $job_location_field);


        $emp_search_title = nokriAPI_canidate_fields(__("Employer Search", "nokri-rest-api"), '', 'textfield', true, 2, 'cand_search_title');
        $emp_search_btn = nokriAPI_canidate_fields(__("Search now", "nokri-rest-api"), '', 'textfield', true, 2, 'cand_search_now');

        $emp_search_name = nokriAPI_canidate_fields(__("Search by name", "nokri-rest-api"), '', 'textfield', true, 2, 'cand_search_name');


        /* Location headings */
        $job_country_level_1 = ( isset($nokriAPI['API_job_country_level_1']) && $nokriAPI['API_job_country_level_1'] != "" ) ? $nokriAPI['API_job_country_level_1'] : '';

        $job_country_level_2 = ( isset($nokriAPI['API_job_country_level_2']) && $nokriAPI['API_job_country_level_2'] != "" ) ? $nokriAPI['API_job_country_level_2'] : '';

        $job_country_level_3 = ( isset($nokriAPI['API_job_country_level_3']) && $nokriAPI['API_job_country_level_3'] != "" ) ? $nokriAPI['API_job_country_level_3'] : '';

        $job_country_level_4 = ( isset($nokriAPI['API_job_country_level_4']) && $nokriAPI['API_job_country_level_4'] != "" ) ? $nokriAPI['API_job_country_level_4'] : '';

        $map_heading = ( isset($nokriAPI['API_job_map_heading_txt']) && $nokriAPI['API_job_map_heading_txt'] != "" ) ? $nokriAPI['API_job_map_heading_txt'] : '';

        $country = nokriAPI_canidate_fields($job_country_level_1, '', 'textfield', true, 2, 'country');

        $state = nokriAPI_canidate_fields($job_country_level_2, '', 'textfield', true, 2, 'state');

        $city = nokriAPI_canidate_fields($job_country_level_3, '', 'textfield', true, 2, 'city');

        $town = nokriAPI_canidate_fields($job_country_level_4, '', 'textfield', true, 2, 'town');

        $extra = array($emp_search_title, $emp_search_btn, $emp_search_name, $country, $state, $city, $town,);


        return $response = array('success' => true, 'data' => $labels, 'extra' => $extra);
    }

}


if (!function_exists('nokriAPI_employer_search')) {

    function nokriAPI_employer_search($request) {
        $json_data = $request->get_json_params();

        /* Getting Title */
        $title = (isset($json_data['emp_title']) && $json_data['emp_title'] != "" ) ? $json_data['emp_title'] : '';
        /* Getting skills */
        $skills = (isset($json_data['emp_skills']) && $json_data['emp_skills'] != "" ) ? $json_data['emp_skills'] : '';
        /* Getting location */
        $location = (isset($json_data['emp_location']) && $json_data['emp_location'] != "" ) ? $json_data['emp_location'] : '';





        $location_qry = '';
        if (isset($location) && $location != "") {
            $location_qry = array(
                'key' => '_emp_custom_location',
                'value' => $location,
                'compare' => 'like'
            );
        }
        $skills_qry = '';
        if (isset($skills) && $skills != "") {
            $skills_qry = array(
                'key' => '_emp_skills',
                'value' => $skills,
                'compare' => 'like'
            );
        }
        $cands_qry = array(
            'key' => '_sb_reg_type',
            'value' => '1',
            'compare' => '='
        );




// total no of User to display
        $page_number = $paged = (isset($json_data['page_number'])) ? $json_data['page_number'] : 1;
        $current_page = $paged;
        $user_per_page = (isset($nokriAPI['api_user_pagination'])) ? $nokriAPI['api_user_pagination'] : "10";
        $paged = (int) $paged;
        $get_offset = ($paged - 1);
        $offset = $get_offset * $user_per_page;
// Query args
        $args = array(
            'search' => "*" . esc_attr($title) . "*",
            'order' => 'DESC',
            'orderby' => array(
                'meta_value',
                'display_name',
            ),
            'role__in' => array('editor', 'administrator', 'subscriber'),
            'number' => $user_per_page,
            'offset' => $offset,
            'meta_query' => array(
                array(
                    'relation' => 'OR',
                    array(
                        'key' => '_emp_feature_profile',
                        'compare' => 'NOT EXISTS'
                    ),
                    array(
                        'key' => '_emp_feature_profile',
                        'compare' => 'EXISTS'
                    )),
                $cands_qry,
                $skills_qry,
                $location_qry
            )
        );
// Create the WP_User_Query object
        $wp_user_query = new WP_User_Query($args);
// Get the results
        $users = $wp_user_query->get_results();
        $total_users = $wp_user_query->get_total();
        $max_num_pages = ceil($total_users / $user_per_page);

        $candidates = array();
        $message = '';
        if (!empty($users)) {
            $count = 0;
            // Loop through results
            foreach ($users as $user) {
                $cand_id = $user->ID;
                $canddata = get_userdata($cand_id);
                /* Getting candiate  Profile Photo */
                $image_link[0] = get_template_directory_uri() . '/images/candidate-dp.jpg';
                if (get_user_meta($cand_id, '_sb_user_pic', true) != "") {
                    $attach_id = get_user_meta($cand_id, '_sb_user_pic', true);
                    $image_link = wp_get_attachment_image_src($attach_id, 'nokri_job_post_single');
                }

                $featured_date = get_user_meta($cand_id, '_emp_feature_profile', true);
                $is_featured = false;
                $today = date("Y-m-d");
                $expiry_date_string = strtotime($featured_date);
                $today_string = strtotime($today);
                if ($today_string > $expiry_date_string) {
                    delete_user_meta($cand_id, '_emp_feature_profile');
                    delete_user_meta($cand_id, '_is_emp_featured');
                } else {
                    $is_featured = true;
                }

                $candidates[$count][] = nokriAPI_canidate_fields(__("Employer Id", "nokri-rest-api"), $cand_id, 'textfield', true, 1, 'emp_id');
                $candidates[$count][] = nokriAPI_canidate_fields(__("Employer DP", "nokri-rest-api"), $image_link[0], 'textfield', true, 1, 'emp_dp');
                $candidates[$count][] = nokriAPI_canidate_fields(__("Employer Name", "nokri-rest-api"), $user->display_name, 'textfield', true, 1, 'emp_name');
                $candidates[$count][] = nokriAPI_canidate_fields(__("Headline", "nokri-rest-api"), get_user_meta($cand_id, '_user_headline', true), 'textfield', true, 1, 'emp_headline');
                ;
                $candidates[$count][] = nokriAPI_canidate_fields(__("Location", "nokri-rest-api"), get_user_meta($cand_id, '_emp_map_location', true), 'textfield', true, 1, 'emp_location');
                $candidates[$count][] = nokriAPI_canidate_fields(__("Feature", "nokri-rest-api"), $is_featured, 'textfield', true, 1, 'is_featured');

                $count++;
            }
        } else {
            $message = __("No employer matched", "nokri-rest-api");
        }
        /* Pagination */
        $nextPaged = (int) ($paged) + 1;
        $has_next_page = ( $nextPaged <= (int) $max_num_pages ) ? true : false;

        $pagination = array("max_num_pages" => (int) $max_num_pages, "current_page" => (int) $paged, "next_page" => (int) $nextPaged, "increment" => (int) $user_per_page, "total_found" => (int) $total_users, "has_next_page" => $has_next_page);

        $data['candidates'] = $candidates;
        $extra['page_title'] = __("Employer search", "nokri-rest-api");

        return $response = array('success' => true, 'data' => $data, 'message' => $message, 'extra' => $extra, "pagination" => $pagination);
    }

}

//subsucribe for jobs 
add_action('rest_api_init', 'nokriAPI_candidate_job_alert_subscription', 0);

function nokriAPI_candidate_job_alert_subscription() {
    register_rest_route(
            'nokri/v1', '/job_alert_subsucription/', array(
        'methods' => WP_REST_Server::EDITABLE,
        'callback' => 'nokriAPI_candidate_job_subscription',
        'permission_callback' => function () {
            return nokriAPI_basic_auth();
        },
            )
    );
}

function nokriAPI_candidate_job_subscription($request) {

    global $nokri;
    $user_id = get_current_user_id();
    $json_data = $request->get_json_params();

    // Getting values From Param

    $alert_name = (isset($json_data['alert_name'])) ? $json_data['alert_name'] : "";
    $alert_email = (isset($json_data['alert_email'])) ? $json_data['alert_email'] : "";
    $alert_frequency = (isset($json_data['alert_frequency'])) ? $json_data['alert_frequency'] : "";
    $alert_category = (isset($json_data['alert_category'])) ? $json_data['alert_category'] : "";
    $alert_type = (isset($json_data['alert_type'])) ? $json_data['alert_type'] : "";
    $alert_experience = (isset($json_data['alert_experience'])) ? $json_data['alert_experience'] : "";
    $alert_location = (isset($json_data['alert_location'])) ? $json_data['alert_location'] : "";
    $alert_geo_location = (isset($json_data['sb_user_address2'])) ? $json_data['sb_user_address2'] : "";
    
    
    
     
    $random_string = nokri_randomString(5);
    $alert_start = date("Y/m/d");
    $type = get_user_meta($user_id, '_sb_reg_type', true);
    /* demo check */


    /* Not cand */
    if ($type != '0') {
        return $response = array('success' => true, 'data' => '', 'message' => esc_html('Only candidate can do this', "nokri-rest-api"));
    }
    /* countries */
    $cand_alert = array();
    if ($alert_name != "") {
        $cand_alert['alert_name'] = $alert_name;
    }
    if ($alert_email != "") {
        $cand_alert['alert_email'] = $alert_email;
    }
    if ($alert_frequency != "") {
        $cand_alert['alert_frequency'] = $alert_frequency;
    }
    if ($alert_type != "") {
        $cand_alert['alert_type'] = $alert_type;
    }
    if ($alert_category != "") {
        $cand_alert['alert_category'] = $alert_category;
    }
    if ($alert_location != "") {
        $cand_alert['alert_location'] = $alert_location;
    }
    if ($alert_geo_location != "") {
        $cand_alert['sb_user_address2'] = $alert_geo_location;
    }
    if ($alert_start != "") {
        $cand_alert['alert_start'] = $alert_start;
    }
    $my_alert = json_encode($cand_alert);


    update_user_meta($user_id, '_cand_alerts_' . $user_id . $random_string, ($my_alert));

    if (get_user_meta($user_id, '_cand_alerts_en', true) == '') {
        update_user_meta($user_id, '_cand_alerts_en', 1);
    }

    return $response = array('success' => true, 'data' => '', 'message' => esc_html('subscribed succesfully', "nokri-rest-api"));
}
