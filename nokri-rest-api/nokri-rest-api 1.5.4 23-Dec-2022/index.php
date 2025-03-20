<?php 
 /**
 * Plugin Name: Nokri Apps API
 * Plugin URI: https://codecanyon.net/user/scriptsbundle
 * Description: This plugin is essential for the Nokri android and ios apps.
 * Version: 1.5.4
 * Author: Scripts Bundle
 * Author URI: https://codecanyon.net/user/scriptsbundle
 * License: GPL2
 * Text Domain: nokri-rest-api
 */
 	/* Get Theme Info If There Is */
	$my_theme = wp_get_theme();
	$my_theme->get( 'Name' );
	
	/*Load text domain*/
	add_action( 'plugins_loaded', 'nokri_rest_api_load_plugin_textdomain' );
	function nokri_rest_api_load_plugin_textdomain()
	{
		load_plugin_textdomain( 'nokri-rest-api', FALSE, basename( dirname( __FILE__ ) ) . '/languages/' );
	}
			
 	/* Define Paths For The Plugin */
	define('NOKRI_API_PLUGIN_FRAMEWORK_PATH', plugin_dir_path(__FILE__));	 
	define('NOKRI_API_PLUGIN_PATH', plugin_dir_path(__FILE__));	  
	define('NOKRI_API_PLUGIN_URL', plugin_dir_url(__FILE__));    
	
	/*Theme Directry/Folder Paths */
	define( 'NOKRI_API_THEMEURL_PLUGIN', get_template_directory_uri () . '/' );  
	define( 'NOKRI_API_IMAGES_PLUGIN', NOKRI_API_THEMEURL_PLUGIN . 'images/'); 
	define( 'NOKRI_API_CSS_PLUGIN', NOKRI_API_THEMEURL_PLUGIN . 'css/');  
	define( 'NOKRI_API_JS_PLUGIN', NOKRI_API_THEMEURL_PLUGIN . 'js/'); 

	/*Only check if plugin activate by theme */
	
 	$my_theme = wp_get_theme();
	if( $my_theme->get( 'Name' ) != 'nokri' && $my_theme->get( 'Name' ) != 'nokri child' )
	{
		//require NOKRI_API_PLUGIN_FRAMEWORK_PATH.'/tgm/tgm-init.php';	
	}
	if (!function_exists('nokriAPI_getallheaders'))
	{
		function nokriAPI_getallheaders()
		{
		   $headers = array();
		   foreach ($_SERVER as $name => $value)
		   {
			   if (substr($name, 0, 5) == 'HTTP_')
			   {
				   $headers[str_replace(' ', '-', ucwords(strtolower(str_replace('_', ' ', substr($name, 5)))))] = $value;
			   }
		   }
		   return $headers;
		}
	} 	
	
	if (!function_exists('nokriAPI_getSpecific_headerVal'))
	{
		function nokriAPI_getSpecific_headerVal($header_key = '')
		{
			$header_val = '';
			if(count(nokriAPI_getallheaders()) > 0 )
			{
				foreach (nokriAPI_getallheaders() as $name => $value)
				{
					if( ($name == $header_key || $name == strtolower($header_key)) && $value != "" )
					{
						$header_val =  $value;
						break;
					}
				}
			}
			return $header_val;
		}
	}
	if (!function_exists('nokriAPI_set_lang_locale'))
	{
		function nokriAPI_set_lang_locale( $locale )
		{	
			$lang = nokriAPI_getSpecific_headerVal('Nokri-Lang-Locale');
			$lang = ( $lang  != "" ) ? $lang  : 'en';
			return ( $lang === 'en') ? 'en_US' : $lang;
		}
	}	
	/*add_filter('locale','adforestAPI_set_lang_locale',10);*/
	$request_from = nokriAPI_getSpecific_headerVal('Nokri-Request-From');
	define( 'NOKRI_API_REQUEST_FROM', $request_from );	
	
	/* Options Init */
	add_action('init', function(){
	  // Load the theme/plugin options
	  if (file_exists(dirname(__FILE__).'/inc/options-init.php')) {
	  require_once( dirname(__FILE__).'/inc/options-init.php' );
         if(class_exists('Redux')){   Redux::init('nokriAPI');
          }}
	});

	 /*require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/inc/cpt.php';*/
	/* Include Function  */
	require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/functions.php';	

	/* Include Classes */
        require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/inc/nokri-api-wpml-functions.php';       
	require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/inc/basic-auth.php';	
	require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/inc/auth.php';
	require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/inc/email-templates.php';
	require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/inc/categories-images.php';
	require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/inc/notifications.php';
     
		
		
	/* Include Classes */
	require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/classes/home.php';	
	require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/classes/index.php';	
	require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/classes/profile.php';
	require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/classes/register.php';
	require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/classes/login.php';
	require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/classes/pages.php';
	//require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/classes/jobs.php';

	/*Canidate section starts*/
	require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/classes/canidate/profile.php';
	require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/classes/canidate/dashboard.php';
	/*Canidate section end*/
	
	
	/*Canidate section starts*/
	require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/classes/employer/profile.php';
	require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/classes/employer/dashboard.php';
	/*Canidate section end*/
	
	/*Job Post section */
   require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/classes/job_post.php';
   /*Blog Posts section */
   require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/classes/posts.php';
   
   /*Job Search section */
   require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/classes/search.php';
   
   
   /*Job woo-commerce section */
   require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/classes/woo-commerce.php';
   /*Job Payment section */
   require NOKRI_API_PLUGIN_FRAMEWORK_PATH . '/classes/payment.php';	

/* Plugin Ends Here */
   
   
   add_action('plugins_loaded', 'nokri_switch_lang_api_callback');

if (!function_exists('nokri_switch_lang_api_callback')) {
    function nokri_switch_lang_api_callback() {
        global $sitepress;
        $lang = nokriApi_getSpecific_headerVal('Nokri-Lang-Locale');
        if (class_exists('SitePress') && !is_admin()) {
            $sitepress->switch_lang($lang, true);
        }
    }
}