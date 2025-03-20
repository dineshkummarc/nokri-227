<?php

add_filter('NokrirestAPI_load_active_languages', 'NokrirestAPI_load_active_languages_callback', 10, 1);
add_filter('NokrirestAPI_load_wpml_settings', 'NokrirestAPI_load_wpml_settings_callback', 10, 1);
add_filter('NokrirestAPI_wpml_show_all_posts', 'NokrirestAPI_wpml_show_all_posts_callback', 10, 1);
add_action('NokrirestAPI_duplicate_posts_lang', 'NokrirestAPI_duplicate_posts_lang_callback', 10, 1);
add_filter('NokrirestAPI_get_lang_tamonomy', 'NokrirestAPI_get_lang_tamonomy_callback', 0, 2);
add_filter('NokrirestAPI_language_page_id', 'NokrirestAPI_language_page_id_callback', 10, 1);
add_filter('NokrirestAPI_app_direction', 'NokrirestAPI_app_direction_callback', 10, 1);

function NokrirestAPI_app_direction_callback($app_dir = 'ltr') {


    if (function_exists('icl_object_id')) {
        if (apply_filters('wpml_is_rtl', NULL)) {
            $app_dir = 'rtl';
        } else {
            $app_dir = 'ltr';
        }
    }
    return $app_dir;
}

if (!function_exists('NokrirestAPI_get_lang_tamonomy_callback')) {

    function NokrirestAPI_get_lang_tamonomy_callback($cat_id = 0, $tax_name = 'ad_cats') {

        if (function_exists('icl_object_id') && $cat_id != 0) {
            $my_current_lang = apply_filters('wpml_current_language', NULL);
            $cat_id = apply_filters('wpml_object_id', $cat_id, $tax_name, FALSE, $my_current_lang);
        }

        return $cat_id;
    }

}
if (!function_exists('NokrirestAPI_duplicate_posts_lang_callback')) {

    function NokrirestAPI_duplicate_posts_lang_callback($org_post_id = 0) {
        global $sitepress, $nokriAPI;

        $sb_duplicate_post = isset($nokriAPI['sb_duplicate_post_app']) ? $nokriAPI['sb_duplicate_post_app'] : false;

        if (function_exists('icl_object_id') && $org_post_id != 0 && $sb_duplicate_post) {
            $language_details_original = $sitepress->get_element_language_details($org_post_id, 'post_ad_post');
            if (!class_exists('TranslationManagement')) {
                include(ABSPATH . 'wp-content/plugins/sitepress-multilingual-cms/inc/translation-management/translation-management.class.php');
            }
            foreach ($sitepress->get_active_languages() as $lang => $details) {
                if ($lang != $language_details_original->language_code) {
                    $iclTranslationManagement = new TranslationManagement();
                    $iclTranslationManagement->make_duplicate($org_post_id, $lang);
                }
            }
        }
    }

}
if (!function_exists('NokrirestAPI_wpml_show_all_posts_callback')) {
    function NokrirestAPI_wpml_show_all_posts_callback($query_args = array()) {
        global $sitepress, $nokriAPI;

        $sb_show_posts = isset($nokriAPI['sb_show_posts_app']) ? $nokriAPI['sb_show_posts_app'] : false;

        if (function_exists('icl_object_id') && $query_args != '' && $sb_show_posts) {
            do_action('adforest_wpml_terms_filters');
            $query_args['suppress_filters'] = true;
        }
        return $query_args;
    }
}
if (!function_exists('NokrirestAPI_load_wpml_settings_callback')) {

    function NokrirestAPI_load_wpml_settings_callback($data_return = array()) {
         global $nokriAPI;

        $defaultLogo = NOKRI_API_PLUGIN_URL . "images/logo.png";
        $wpml_logo = (isset($nokriAPI['app_wpml_logo'])) ? $nokriAPI['app_wpml_logo']['url'] : $defaultLogo;

        $json_arr_lang = array();
        $get_active_languages = apply_filters('wpml_active_languages', NULL, 'orderby=id&order=desc');
        
       
        if (isset($get_active_languages) && is_array($get_active_languages)) {
            foreach ($get_active_languages as $language) {
                
            
                if (isset($nokriAPI['sb_load_languages']) && !empty($nokriAPI['sb_load_languages']) && is_array($nokriAPI['sb_load_languages'])) {
                    foreach ($nokriAPI['sb_load_languages'] as $lang_code) {

                        if ($lang_code == $language['language_code']) {
                            $json_arr_lang[] = array(
                                'code' => $language['code'],
                                'flag_url' => $language['country_flag_url'],
                                'native_name' => $language['native_name'],
                                'translated_name' => $language['translated_name'],
                                'locale' => $language['default_locale'],
                            );
                        }
                    }
                }
            }
        }
        $wpml_active_val = isset($nokriAPI['sb_api_wpml_anable']) && $nokriAPI['sb_api_wpml_anable'] ? true : false;
        $data_return['is_wpml_active'] = $wpml_active_val;
        $data_return['wpml_logo'] = $wpml_logo;
        $data_return['wpml_header_title_1'] = isset($nokriAPI['wpml_header_title1']) && !empty($nokriAPI['wpml_header_title1']) ? $nokriAPI['wpml_header_title1'] : 'Pick your';
        $data_return['wpml_header_title_2'] = isset($nokriAPI['wpml_header_title2']) && !empty($nokriAPI['wpml_header_title2']) ? $nokriAPI['wpml_header_title2'] : 'Language';

        $wpml_custom_text = __("Languages", "adforest-rest-api");
        $data_return['wpml_custom_menu_text'] = $wpml_custom_text;
        $data_return['wpml_menu_text'] = $wpml_custom_text;
        if ($wpml_active_val) {
            $data_return['wpml_menu_text'] = isset($nokriAPI['wpml_menu_text']) && !empty($nokriAPI['wpml_menu_text']) ? $nokriAPI['wpml_menu_text'] : $wpml_custom_text;
            $data_return['wpml_custom_menu_text'] = isset($nokriAPI['api-sortable-app-menu']['wpml_menu_text']) && !empty($nokriAPI['api-sortable-app-menu']['wpml_menu_text']) ? $nokriAPI['api-sortable-app-menu']['wpml_menu_text'] : $wpml_custom_text;
        }
        $data_return['site_languages'] = $json_arr_lang;

        return $data_return;
    }

}

if (!function_exists('NokrirestAPI_load_active_languages_callback')) {

    function NokrirestAPI_load_active_languages_callback($languages_return = array()) {

        if (function_exists('icl_object_id')) {
            $get_active_languages = apply_filters('wpml_active_languages', NULL, 'orderby=id&order=desc');
            if (isset($get_active_languages) && is_array($get_active_languages)) {
                foreach ($get_active_languages as $language) {
                    $languages_return[$language['language_code']] = $language['translated_name'];
                }
            }
        }
        return $languages_return;
    }
}

if (!function_exists('NokrirestAPI_language_page_id_callback')) {

    function NokrirestAPI_language_page_id_callback($page_id = '') {
        global $sitepress;
        if (function_exists('icl_object_id') && function_exists('wpml_init_language_switcher') && $page_id != '' && is_numeric($page_id)) {
            $language_code = $sitepress->get_current_language();
            $lang_page_id = icl_object_id($page_id, 'page', false, $language_code);
            if ($lang_page_id <= 0) {
                $lang_page_id = $page_id;
            }
            return $lang_page_id;
        } else {
            return $page_id;
        }
    }

}
?>