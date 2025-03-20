<?php
/**
 * Modify url base from wp-json to 'api'
 */
 
/* Ads Loop Starts */
add_action( 'plugins_loaded', 'nokriAPI_add_new_image_size' );
function nokriAPI_add_new_image_size() {
	
	add_theme_support( 'post-thumbnails', array('post') );
        add_image_size( 'nokri-andriod-profile', 450, 450, true ); 
	add_image_size( 'nokri-single-post', 760, 410, true ); 
	add_image_size( 'nokri-category', 400, 300, true ); 
	add_image_size( 'nokri-single-small', 80, 80, true );
	add_image_size( 'nokri-ad-thumb', 120, 63, true );
	add_image_size( 'nokri-ad-related', 313, 234, true );
	add_image_size( 'nokri-user-profile', 300, 300, true );
	add_image_size( 'nokri-app-thumb', 400, 250, true ); 
	add_image_size( 'nokri-app-full', 700, 400, true );  
	
}

if (!function_exists('nokriAPI_getReduxValue')) 
{
	function nokriAPI_getReduxValue($param1 = '', $param2 = '', $vaidate = false)
	{
		global $nokriAPI;
		$data = '';		
		if( $param1 != "" ) { $data = $nokriAPI["$param1"]; }
		if( $param1 != "" && $param2 != "")  { $data = $nokriAPI["$param1"]["$param2"]; }
		
		if( $vaidate == true )
		{
			$data =  (isset( $data ) && $data != "" ) ? 1 : 0;	
		}
		
		return $data;
	}
}
/************************/
/* Category count function*/
/************************/
if( ! function_exists( 'nokriAPI_get_opening_count' ) )
{
	function nokriAPI_get_opening_count($term_id = '')
	{
		$custom_count = '';
		$query = new WP_Query( 
		array('post_type' => 'job_post','meta_query' => array(
		array('key'     => '_job_status','value'   => 'active','compare' => '=',),),
		'tax_query' => array(
		'relation' => 'AND',
		array(
			'taxonomy' => 'job_category',
			'field'    => 'term_id',
			'terms'    => array( $term_id ),
			'operator' => 'IN',
		),
	),																		
	));
		 
		$custom_count =  $query->found_posts;
		wp_reset_postdata();
	 	return $custom_count;
	}
}

if (!function_exists('nokriAPI_getReduxValue'))
{
	function nokriAPI_getReduxValue($param1 = '', $param2 = '', $vaidate = false)
	{
		global $nokriAPI;
		$data = '';		
		if( $param1 != "" ) { $data = $nokriAPI["$param1"]; }
		if( $param1 != "" && $param2 != "")  { $data = $nokriAPI["$param1"]["$param2"]; }
		
		if( $vaidate == true )
		{
			$data =  (isset( $data ) && $data != "" ) ? 1 : 0;	
		}
		
		return $data;
	}
}
// Get user profile PIC
if (!function_exists('nokriAPI_user_dp'))
{
	function nokriAPI_user_dp( $user_id, $size = 'nokri-andriod-profile' )
	{
		
		$user_pic = NOKRI_API_PLUGIN_URL."images/company-logo.jpg";
		if( nokriAPI_getReduxValue('sb_user_dp', 'url', true) )
		{
			$user_pic = nokriAPI_getReduxValue('sb_user_dp', 'url', false);	
		}
		$key = '_cand_dp';
		if(get_user_meta($user_id, '_sb_reg_type', true ) == '1')
		{
			$key = '_sb_user_pic';
		}
		
		
		$image_link	= array();
		if( get_user_meta($user_id, $key, true ) != "" )
		{
			$attach_id 	=	get_user_meta($user_id, $key, true );
			$image_link = 	wp_get_attachment_image_src( $attach_id, $size );
		}
	
		
		return ( count( $image_link ) > 0 ) ? $image_link[0]  : $user_pic;
		
	}
}

if (!function_exists('nokriAPI_appLogo'))  
{
	function nokriAPI_appLogo()
	{		
		global $nokriAPI;
		$defaultLogo = NOKRI_API_PLUGIN_URL."images/logo.png";
		$app_logo = (isset( $nokriAPI['app_logo'] )) ? $nokriAPI['app_logo']['url'] : $defaultLogo;
		return $app_logo;		
	}
}



if (!function_exists('nokriAPI_get_ad_terms')) 
{
	function nokriAPI_get_ad_terms($post_id = '', $term_type = 'ad_cats', $only_parent = '', $name = '')
	{
		$ad_trms = wp_get_object_terms( $post_id,  $term_type);
		$termsArr = array();
		if( count( $ad_trms ) )
		{
			foreach( $ad_trms as $ad_trm )
			{
				if( isset( $ad_trm->term_id ) && $ad_trm->term_id != "" )
				{
					$termsArr[] = array
							(
								"id" => $ad_trm->term_id, 
								"name" => htmlspecialchars_decode($ad_trm->name, ENT_NOQUOTES),
								"slug" => $ad_trm->slug,
								"count" => $ad_trm->count,
								"taxonomy" => $ad_trm->taxonomy,
							);
				}
			}
		}
		return ( $name == "" ) ? $termsArr : array($name, $termsArr);
	}
}


if (!function_exists('nokriAPI_get_ad_terms_names'))  
{
	function nokriAPI_get_ad_terms_names($post_id = '', $term_type = 'ad_cats', $only_parent = '', $name = '', $separator = '>')
	{
		
		
		 $terms = wp_get_post_terms( $post_id, $term_type, array( 'orderby' => 'id', 'order' => 'DESC' ) );
		 $deepestTerm = false;
		 $maxDepth = -1;
		 $c = 0;
		 $catNames = array();
		 if( count( $terms) > 0 ){
		 foreach ($terms as $term) 
		 {
			$ancestors = get_ancestors( $term->term_id, $term_type );
			$termDepth = count($ancestors);
			$deepestTerm[$c] = $term->name;
			$maxDepth = $termDepth;
			$c++;
		 } 
		 $terms =  (isset($deepestTerm ) && count( $deepestTerm ) > 0 && $term_type != 'ad_tags' ) ? array_reverse($deepestTerm) : $deepestTerm;		
		
		
		if( count( $terms ) > 0 )
		{
			foreach( $terms as $tr )
			{
				$trName = htmlspecialchars_decode($tr, ENT_NOQUOTES);
				$catNames[] = $trName; 
			}
		}
		 }
		
		$catNames = @implode(" $separator ", $catNames);
		return ( $name == "" ) ? $catNames : array($name, $catNames);

	}
}


if ( ! function_exists( 'nokriAPI_job_categories_with_chlid_no_href' ) ) 
{	 
	function nokriAPI_job_categories_with_chlid_no_href( $pid,$taxonomy = '' )
	{
			global $nokri;
			$post_categories = wp_get_object_terms( $pid,  array($taxonomy), array('orderby' => 'term_group') );
			$cats_html	    =	'';
			foreach($post_categories as $c)
			{
				$cat = get_term( $c );
				$cats_html	.= esc_html($cat->name)." ".',';
			}
			 return rtrim($cats_html, ', ');
	}
}


if ( ! function_exists( 'nokriAPI_randomString' ) ) { 
function nokriAPI_randomString($length = 50) {
	$str = "";
	$characters = array_merge(range('A','Z'), range('a','z'), range('0','9'));
	$max = count($characters) - 1;
	for ($i = 0; $i < $length; $i++) {
		$rand = mt_rand(0, $max);
		$str .= $characters[$rand];
	}
	return $str;
}
}




if (!function_exists('nokriAPI_check_username')) 
{
	function nokriAPI_check_username( $username = '' )
	{
		if ( username_exists( $username ) )
		{
			$random = rand();
			$username	=	$username . '-' . $random;
			nokriAPI_check_username($username);		
		}
		return $username;
	}
}





add_action( 'rest_api_init', 'add_thumbnail_to_JSON' );
function add_thumbnail_to_JSON() {
register_rest_field( 'post',
    'featured_image_src', array(
        'get_callback'    => 'get_image_src',
        'update_callback' => null,
        'schema'          => null,
         )
    );
}

function get_image_src( $object, $field_name, $request ) {
    $feat_img_array = wp_get_attachment_image_src($object['featured_media'], 'nokri-single-post', true);
    return $feat_img_array[0];
}

add_action( 'rest_api_init', 'add_post_comment_count' );
function add_post_comment_count() {
register_rest_field( 'post', 'post_comment_count', array(
        'get_callback'    => 'add_post_comment_count_func',
        'update_callback' => null,
        'schema'          => null,
         )
    );
}

function add_post_comment_count_func( $object, $field_name, $request ) {

    return 100;
}


if( ! function_exists( 'nokriAPI_do_register' ) )   
{
function nokriAPI_do_register($email= '', $password = '')
{
	global $nokriAPI;
	
	$user_name	=	explode( '@', $email );	
	$u_name	=	nokriAPI_check_user_name( $user_name[0] );
	$uid 	=	wp_create_user( $u_name, $password, $email );
	
	wp_update_user( array( 'ID' => $uid, 'display_name' => $u_name ) );
	//nokriAPI_auto_login($email, $password, true );

	$sb_allow_ads = Redux::getOption('nokriAPI', 'sb_allow_ads' );
	if( isset( $sb_allow_ads ) && $sb_allow_ads == true )
	{
		$free_ads 			= Redux::getOption('nokriAPI', 'sb_free_ads_limit' );
		$featured_ads 		= Redux::getOption('nokriAPI', 'sb_featured_ads_limit' );
		$package_validity 	= Redux::getOption('nokriAPI', 'sb_package_validity' );
		$allow_featured_ads = Redux::getOption('nokriAPI', 'sb_allow_featured_ads' );
		
		$free_ads 			= ( isset( $free_ads ) && $free_ads != "" ) ? $free_ads : 0;
		$featured_ads 		= ( isset( $featured_ads ) && $featured_ads != "" ) ? $featured_ads : 0;
		$package_validity 	= ( isset( $package_validity ) && $package_validity != "" ) ? $package_validity : '';
			
		update_user_meta( $uid, '_sb_simple_ads', $free_ads );
		if( isset( $allow_featured_ads ) && $allow_featured_ads == true)
		{
			update_user_meta( $uid, '_sb_featured_ads', $featured_ads );
		}
		if( $package_validity == '-1' )
		{
			update_user_meta( $uid, '_sb_expire_ads', $package_validity );
		}
		else
		{
			$days			=	$package_validity;
			$expiry_date	=	date('Y-m-d', strtotime("+$days days"));
			update_user_meta( $uid, '_sb_expire_ads', $expiry_date );		
		}	

	}
	else
	{
		update_user_meta( $uid, '_sb_simple_ads', 0 );
		update_user_meta( $uid, '_sb_featured_ads', 0 );
		update_user_meta( $uid, '_sb_expire_ads', date('Y-m-d') );
	}
	update_user_meta( $uid, '_sb_pkg_type', 'free' );
	
	return $uid;
}
}

if ( ! function_exists( 'nokriAPI_check_user_name' ) )    
{
	function nokriAPI_check_user_name( $username = '' )
	{
		
		if ( username_exists( $username ) )
		{
			$random = mt_rand();
			$username	=	$username . '-' . $random;
			nokriAPI_check_user_name($username);		
		}
		return $username;
	}
}


// Email on ad publish
add_action( 'transition_post_status', 'nokriAPI_send_mails_on_publish', 10, 3 );
function nokriAPI_send_mails_on_publish( $new_status, $old_status, $post )
{
    if ( 'publish' !== $new_status or 'publish' === $old_status or 'ad_post' !== get_post_type( $post ) )
        return;
		
	global $nokriAPI;
	if( isset( $nokriAPI['email_on_ad_approval'] ) && $nokriAPI['email_on_ad_approval'] )
	{
		nokri_get_notify_on_ad_approval( $post );
	}

}



if ( ! function_exists( 'nokriAPI_make_link' ) ) {   
function nokriAPI_make_link( $url, $text )
{
	return wp_kses( "<a href='". esc_url ( $url )."' target='_blank'>", nokriAPI_required_tags() )  . $text . wp_kses( '</a>', nokriAPI_required_tags() );	
}
}

if ( ! function_exists( 'nokriAPI_required_attributes' ) ) {   
function nokriAPI_required_attributes()
{
	return $default_attribs = array(
		'id' => array(),
		'src' => array(),
		'href' => array(),
		'target' => array(),
		'class' => array(),
		'title' => array(),
		'type' => array(),
		'style' => array(),
		'data' => array(),
		'role' => array(),
		'aria-haspopup' => array(),
		'aria-expanded' => array(),
		'data-toggle' => array(),
		'data-hover' => array(),
		'data-animations' => array(),
		'data-mce-id' => array(),
		'data-mce-style' => array(),
		'data-mce-bogus' => array(),
		'data-href' => array(),
		'data-tabs' => array(),
		'data-small-header' => array(),
		'data-adapt-container-width' => array(),
		'data-height' => array(),
		'data-hide-cover' => array(),
		'data-show-facepile' => array(),
	);
}
}

if ( ! function_exists( 'nokriAPI_required_tags' ) ) {
function nokriAPI_required_tags()
{
        return $allowed_tags = array(
            'div'           => nokriAPI_required_attributes(),
            'span'          => nokriAPI_required_attributes(),
            'p'             => nokriAPI_required_attributes(),
            'a'             => array_merge( nokriAPI_required_attributes(), array(
                'href' => array(),
                'target' => array('_blank', '_top'),
            ) ),
            'u'             =>  nokriAPI_required_attributes(),
            'br'            =>  nokriAPI_required_attributes(),
            'i'             =>  nokriAPI_required_attributes(),
            'q'             =>  nokriAPI_required_attributes(),
            'b'             =>  nokriAPI_required_attributes(),
            'ul'            => nokriAPI_required_attributes(),
            'ol'            => nokriAPI_required_attributes(),
            'li'            => nokriAPI_required_attributes(),
            'br'            => nokriAPI_required_attributes(),
            'hr'            => nokriAPI_required_attributes(),
            'strong'        => nokriAPI_required_attributes(),
            'blockquote'    => nokriAPI_required_attributes(),
            'del'           => nokriAPI_required_attributes(),
            'strike'        => nokriAPI_required_attributes(),
            'em'            => nokriAPI_required_attributes(),
            'code'          => nokriAPI_required_attributes(),
            'style'         => nokriAPI_required_attributes(),
            'script'        => nokriAPI_required_attributes(),
            'img'          	=> nokriAPI_required_attributes(),
        );
}
}
// Bad word filter
if ( ! function_exists( 'nokriAPI_badwords_filter' ) ) {  
function nokriAPI_badwords_filter( $words = array(), $string = "", $replacement = "")
{
	foreach( $words as $word )
	{
		$string	=	str_replace($word, $replacement, $string);
	}
	return $string;
}
}

function increase_timeout_for_api_requests_27091( $r, $url ) {
	if ( false !== strpos( $url, '//api.wordpress.org/' ) ) {
		$r['timeout'] = 60;
	}

	return $r;
}
add_filter( 'http_request_args', 'increase_timeout_for_api_requests_27091', 10, 2 );

/* Getting Selected Taxonomies Name */

if ( ! function_exists( 'nokri_job_post_single_taxonomies' ) ) {	
 function nokri_job_post_single_taxonomies($taxonomy_name = '', $value = '')
 {
 		$taxonomies_single = get_term_by('id', $value, $taxonomy_name); 

		if ($taxonomies_single) 
		{
			return $taxonomies_single->name;	
		}
		else
		{
			return "";
		}
 }
}

/* Getting All Sub Level Categories */

if ( ! function_exists( 'nokriAPI_get_ad_cats' ) )
 {
	function nokriAPI_get_ad_cats( $id , $by = 'name' )
	{
		$post_categories 	= wp_get_object_terms( $id,  'job_category', array('orderby' => 'term_group') );
		$cats 				= array();
		foreach($post_categories as $c)
		{
			$cat 	= get_category( $c );
			$cats[] = array( 'name' => $cat->name, 'id' => $cat->term_id );
		}
		return $cats;
	}
}
/* Getting Job Class For Badges */ 

if ( ! function_exists( 'nokriAPI_job_class_badg' ) ) {
function nokriAPI_job_class_badg( $job_id = '' )
	{
		$term_ids	=	array();
		$job_class_badges = get_terms( array( 'taxonomy' => 'job_class', 'hide_empty' => false, ) );
		foreach( $job_class_badges as $job_class_badge )
		{
			$term_id = $job_class_badge->term_id;
	 		$job_class_post_meta = get_post_meta($job_id, 'package_job_class_'.$term_id, true);
			if( $job_class_post_meta == $term_id )
	 			$term_ids[$job_class_badge->name]	=	$job_class_post_meta;
		}
		return $term_ids;
	}
}
/* Getting Resumes */ 
if ( ! function_exists( 'nokriAPI_get_resumes_list' ) ) {
function nokriAPI_get_resumes_list($user_id = '')
{
	global $wpdb;
	/* Query For Getting All Resumes Against Job */
	$query	= "SELECT meta_key, meta_value FROM $wpdb->usermeta WHERE user_id = '$user_id' AND meta_key like '_email_temp_name_$user_id%' ";
	$resumes = $wpdb->get_results( $query );
	
	$data = array();
	foreach ( $resumes as $resume ) 
	{
		$temps = nokriAPI_base64Decode($resume->meta_value);
		$value = json_decode( $temps, true );
		$data["$resume->meta_key"] = $value;
	}
	return $data;
}
}
/* Base decode */
if( ! function_exists( 'nokriAPI_base64Decode' ) )
{
	function nokriAPI_base64Decode($json){
		return base64_decode($json);

	}
}

// get post description as per need. 
if ( ! function_exists( 'nokriAPI_words_count' ) ) {	
	function nokriAPI_words_count($contect = '', $limit = 180)
	{
		 $string	=	'';
		 $contents = strip_tags( strip_shortcodes( $contect ) );
		 $contents	=	nokri_removeURL( $contents );
		 $removeSpaces = str_replace(" ", "", $contents);
		 $contents	=	preg_replace("~(?:\[/?)[^/\]]+/?\]~s", '', $contents);
		 if(strlen($removeSpaces) > $limit)
		 {
			 return mb_substr(str_replace("&nbsp;","",$contents), 0, $limit).'...';
		 }
		 else
		 {
			 return str_replace("&nbsp;","",$contents);
		 }
	}
}


// get post description as per need. 
if ( ! function_exists( 'nokriAPI_allow_editing' ) ) {	
	function nokriAPI_allow_editing( $post_type = '')
	{
		
		
		$arr = array();	
		
		if($post_type == "POST" )
		{
			global $nokriAPI;	
			$settings_demo = (isset($nokriAPI['app_settings_demo']) && $nokriAPI['app_settings_demo'] == true) ? false  :   true;
			if($settings_demo == false)
			{
				$arr =  array( 'success' => false, 'data' => '', 'message' => __("Editing is not allowed in demo.", "nokri-rest-api"));
			}
		
		}
		
		return $arr;
	}
}
/* Escaping html*/
if ( ! function_exists( 'nokriAPI_convert_uniText' ) ) 
{
 function nokriAPI_convert_uniText($string = '')
 {
	 
 $string = preg_replace('/%u([0-9A-F]+)/', '&#x$1;', $string);
  
  return  html_entity_decode($string, ENT_QUOTES);
 }
}



if (!function_exists('nokri_authorization_htaccess_contents'))
{
function nokri_authorization_htaccess_contents( $rules )
{
$my_content = <<<EOD
\n# BEGIN ADDING NOKRI Authorization
SetEnvIf Authorization .+ HTTP_AUTHORIZATION=$0
# END ADDING NOKRI Authorization\n
EOD;
    return $rules .$my_content;
}
}
add_filter('mod_rewrite_rules', 'nokri_authorization_htaccess_contents');

/* Employer Allowed Candidate search*/
if( ! function_exists( 'nokriAPI_is_cand_search_allowed' ) )
{
	function nokriAPI_is_cand_search_allowed($visitor_id = '')
		{
			global $nokri;
			$current_user_id 	  =  get_current_user_id();
                        
                        $can_search  =  false;
			if(get_user_meta($visitor_id, '_sb_reg_type', true) == '0' && $visitor_id != $current_user_id)
			{
				$expiry_date          =  get_user_meta($current_user_id, '_sb_expire_ads', true);
				$remaining_searches   =  get_user_meta($current_user_id, '_sb_cand_search_value', true);
				$today			      =  date("Y-m-d");
				$expiry_date_string   =  strtotime($expiry_date);
				$today_string 		  =  strtotime($today);
				if($today_string > $expiry_date_string)
				{
					update_user_meta($current_user_id, '_sb_cand_viewed_resumes','');
					update_user_meta($current_user_id, '_sb_cand_search_value','0');
				}
				$can_search           =  false;
				/* Already visited can search*/
				$resumes_viewed       =  get_user_meta($current_user_id, '_sb_cand_viewed_resumes',true);
				$resumes_viewed_array =  (explode(",",$resumes_viewed));
				if (in_array($visitor_id, $resumes_viewed_array))
				{
					$can_search = true;
				}
				/* Is admin*/
				if(current_user_can('administrator'))
				{
					$can_search = true;
				}
				/* Is unlimited searches*/
				if($remaining_searches == '-1')
				{
					$can_search = true;
				}
				/*Is Applied*/
				$args = array(
					'post_type'   => 'job_post',
					'orderby'     => 'date',
					'order'       => 'ASC',
					'author' 	  => $current_user_id,
					'post_status' => array('publish'), 
					 'meta_query' => 
						array(
							'key' => '_job_status',
							'value' => 'active',
							'compare' => '='
					)
				);
				
				$query = new WP_Query( $args ); 
				if ( $query->have_posts() )
				{
						  while ( $query->have_posts()  )
						  { 
							$query->the_post(); 
							$job_id =  get_the_id();
							if(get_post_meta($job_id, '_job_applied_resume_'.$visitor_id, true))
							{
								 $can_search = true;
							}
						 }
					}
				/*Is Free search*/
				if(isset($nokri['cand_search_mode']) && $nokri['cand_search_mode'] == '1')
				{
					$can_search = true;
				}
				if(isset($nokri['cand_search_mode']) && $nokri['cand_search_mode'] == '2' && !$can_search )
				{
						if($today_string > $expiry_date_string)
						{
							update_user_meta($current_user_id, '_sb_cand_viewed_resumes','');
							update_user_meta($current_user_id, '_sb_cand_search_value','0');
						}
						
						if($remaining_searches <= 0 || $today_string > $expiry_date_string)
						{
							$can_search = false;
						}
						/*Is profile public */
						$cand_profile_status	= get_user_meta($visitor_id, '_user_profile_status', true); 
						if($cand_profile_status != 'priv')
						{
							if (!in_array($visitor_id, $resumes_viewed_array))
							{
								$visitor_id = $visitor_id;
								if($resumes_viewed != '')
								{
									$visitor_id = $resumes_viewed.','.$visitor_id;
								}
								update_user_meta($current_user_id, '_sb_cand_viewed_resumes', $visitor_id);
								if($remaining_searches != '0')
								{
									update_user_meta($current_user_id, '_sb_cand_search_value', (int)$remaining_searches - 1);
								
                                                                           $can_search = true;
                                                                }
                                                             
							}
						}
						else
						{
							$can_search = false;
						}
				}
			}
			else
			{
				$can_search = true;
			}
			
			return $can_search;
		}
}

/* Is resume package expired*/
if( ! function_exists( 'nokriAPI_is_resume_pkg_expired' ) )
{
	function nokriAPI_is_resume_pkg_expired()
		{
				$current_user_id 	  =  get_current_user_id();
			    $expiry_date          =  get_user_meta($current_user_id, '_sb_expire_ads', true);
				$remaining_searches   =  get_user_meta($current_user_id, '_sb_cand_search_value', true);
				$today			      =  date("Y-m-d");
				$expiry_date_string   =  strtotime($expiry_date);
				$today_string 		  =  strtotime($today);
				$message              =  true;
				if($remaining_searches <= 0 || $today_string > $expiry_date_string)
				{
					$message =  __("Resume package  has been expired", "nokri-rest-api");
				}
				
				return $message;
		}
}

if ( ! function_exists( 'nokriAPI_radius_search' ) )
{
 function nokriAPI_radius_search($data_arr = array(), $check_db = true )
 {
  $data = array();
  $user_id = get_current_user_id();
  $success = false;
  
  if( isset( $data_arr ) && !empty( $data_arr ) )
  {
   		$nearby_data = $data_arr;
  }
 
   
  if( isset($nearby_data) && $nearby_data != ""  )
  {

   //array("latitude" => $latitude, "longitude" => $longitude, "distance" => $distance );
   $original_lat  = $nearby_data['latitude'];
   $original_long = $nearby_data['longitude'];
   $distance     = $nearby_data['distance'];
   
   $lat      = $original_lat; //latitude
   $lon    = $original_long; //longitude
   $distance = $distance; //your distance in KM
   $R     = 6371.009; //constant earth radius. You can add precision here if you wish
   
   $maxLat = $lat + rad2deg($distance/$R);
   $minLat = $lat - rad2deg($distance/$R);
   $maxLon = $lon + rad2deg(asin($distance/$R) / @cos(deg2rad($lat)));
   $minLon = $lon - rad2deg(asin($distance/$R) / @cos(deg2rad($lat)));
   
   $data['radius']   = $R;
   $data['distance'] = $distance;
   $data['lat']['original'] = $original_lat;
   $data['long']['original'] = $original_long;

   $data['lat']['min'] = $minLat;
   $data['lat']['max'] = $maxLat;

   $data['long']['min'] = $minLon;
   $data['long']['max'] = $maxLon;
  }
  
  
  return $data;
 }
}

/******************************/
/* Job post feilds operations*/
/*****************************/

if( ! function_exists( 'nokriAPI_job_post_feilds_operations' ) )
{
	function nokriAPI_job_post_feilds_operations($key = '', $type = 'show')
	{
		global $nokri;
		
		if($type == 'show')
		{
			
			if((!empty($nokri["$key"]) && $nokri["$key"] == 'show') || !empty($nokri["$key"]) && $nokri["$key"] == 'required') 
			{
				return true;
			}
			else
			{
				return false;	
			}
			
		}
		
		if($type == 'required')
		{			
			
			return (!empty($nokri["$key"]) && $nokri["$key"]== 'required') ?  true : false;	
		}
		
	}
}

/******************************/
/* Job post feilds label*/
/*****************************/

if( ! function_exists( 'nokriAPI_feilds_label' ) )
{
	function nokriAPI_feilds_label($key = '',$default = '')
	{
		global $nokri;
		
		return (!empty($nokri["$key"]) && $nokri["$key"] != '') ?  $nokri["$key"] : $default;
	}
}


/******************************/
/* Job post external links */
/*****************************/

if( ! function_exists( 'nokriAPI_external_links' ) )
{
	function nokriAPI_external_links($type = '')
	{
		global $nokri;
		if(!empty($nokri['job_external_source']))
		{
			  $val = false;
			  foreach ($nokri['job_external_source'] as $key => $value) 
			  {
				if($value == 'exter' && $type = 'exter')
				{
					$val = 'exter';
				}
				if($value == 'inter'  && $type = 'inter')
				{
					$val = true;
				}
				if($value == 'mail' && $type = 'mail')
				{
					$val = 'mail';
				}
			}
			return $val;
		}
	}
}



//job get question answer 
if (! function_exists ( 'nokri_get_questions_answers_api' )) 
{
	function nokri_get_questions_answers_api($job_id = '',$candidate_id = '')
	{       
                global $nokri ;
                
		$qstn_ans_html      =  array();
		$job_questions      =   $cand_answers = array();
		$job_questions      =   get_post_meta( $job_id, '_job_questions', true);
		$cand_answers       =   get_user_meta( $candidate_id, '_job_answers'.$candidate_id, true);
                
                $allow      =   (isset($nokri['allow_questinares']) && $nokri['allow_questinares'] != "") ? $nokri['allow_questinares'] : false;
                
                if($allow) {
		if( isset($job_questions) && !empty($job_questions) &&  count($job_questions) > 0 )
		{
			foreach($job_questions as $key => $csv )
			{
				if( isset($cand_answers) && is_array($cand_answers))
				{
					if(array_key_exists($key,$cand_answers))
					{
						$skill_lavel = $cand_answers[$key];
					}
				}			
				$qstn_ans[] = array("question" => $csv, "answer" => $skill_lavel);	
			}
		}
		if(isset($qstn_ans) && !empty($qstn_ans))
		{
			foreach( $qstn_ans  as $res )  
			{
				$qstn_ans_html[]= array("quest" => esc_html($res['question']), "answer" => esc_html($res['answer']));
			}
		}
                }
			return $qstn_ans_html;
	}
}
/* Getting Public Resume */
if ( ! function_exists( 'nokri_get_resume_publically_api' ) )
 {	
	 function nokri_get_resume_publically_api($user_id = '',$type = '')
	 {
		 	$download_btn  =   $attach_id =  $link = $final_url ='';
			$ids_array	   =   get_user_meta($user_id, '_cand_resume', true);
			if(!empty($ids_array))
			{
				$ids_array	  =	explode( ',', $ids_array );
				$attach_id	  =	$ids_array[0];
				$link         = nokri_set_url_param(get_the_permalink($attach_id), 'attachment_id', esc_attr( $attach_id ));
				$final_url    = esc_url(nokri_page_lang_url_callback($link));				
			} 
			if($type == 'id')
			{
				return $attach_id;
			}
			else
			{
				return $final_url;
			}
	 }  
}
//Resume name
if ( ! function_exists( 'nokri_get_resume_name_publically_api' ) )
 {	
	 function nokri_get_resume_name_publically_api($user_id = '',$type = '')
	 {
		 	$download_btn  =   $attach_id =  $link = $file_name ='';
			$ids_array	   =   get_user_meta($user_id, '_cand_resume', true);
			if(!empty($ids_array))
			{
				$ids_array	  =	explode( ',', $ids_array );
				$attach_id	  =	$ids_array[0];
				$file_name        =     basename ( get_attached_file( $attach_id ) );
			} 
			if($type == 'id')
			{
				return $attach_id;
			}
			else
			{
				return $file_name;
			}
	 }  
}


//send verification and welcome email socail login 
function social_login_email($user_id, $social = '', $admin_email = false)
{
    
    if(class_exists('Redux')){

 if( Redux::getOption('nokri','sb_new_user_email_to_admin') && $admin_email )
 {
  if(  Redux::getOption('nokri','sb_new_user_admin_message_admin') != "" &&  Redux::getOption('nokri','sb_new_user_admin_message_from_admin') != "" )
  {
   $to = get_option( 'admin_email');
   $subject = Redux::getOption('nokri','sb_new_user_admin_message_subject_admin');
   $from = Redux::getOption('nokri','sb_new_user_admin_message_from_admin');
   $headers = array('Content-Type: text/html; charset=UTF-8',"From: $from" );
   
   // User info
   $user_info = get_userdata( $user_id );
   $msg_keywords  = array('%site_name%', '%display_name%', '%email%');
   $msg_replaces  = array(get_bloginfo( 'name' ), $user_info->display_name, $user_info->user_email );
   
   $body = str_replace($msg_keywords, $msg_replaces, Redux::getOption('nokri','sb_new_user_admin_message_admin'));
   wp_mail( $to, $subject, $body, $headers );
  }
  
 }
 if( Redux::getOption('nokri','sb_new_user_email_to_user') )
 {
	 
  if(   Redux::getOption('nokri','sb_new_user_message') != ""  && Redux::getOption('nokri','sb_new_user_message_from') != "" )
  {
   // User info
   $user_info = get_userdata( $user_id );
   $to = $user_info->user_email;
   $subject = Redux::getOption('nokri','sb_new_user_message_subject');
   $from = Redux::getOption('nokri','sb_new_user_message_from');
   $headers = array('Content-Type: text/html; charset=UTF-8',"From: $from" );
   $user_name = $user_info->user_email;
   if( $social != '' )
    $user_name .= "(Password: $social )";
	
   $verification_link = '';
   if(Redux::getOption('nokri','sb_new_user_email_verification') && $social == "" )
   {
    $token = get_user_meta($user_id, 'sb_email_verification_token', true);
    if( $token == "" )
    {
     $token  =  nokri_randomString(50);
    }
    $verification_link = trailingslashit( get_home_url()) . '?verification_key=' . $token . '-sb-uid-'. $user_id;

    update_user_meta($user_id, 'sb_email_verification_token', $token);
   }
	
   $msg_keywords  = array('%site_name%', '%user_name%', '%display_name%','%verification_link%');
   $msg_replaces  = array(get_bloginfo( 'name' ), $user_name, $user_info->display_name ,$verification_link);
   $body = str_replace($msg_keywords, $msg_replaces, Redux::getOption('nokri','sb_new_user_message'));
   wp_mail( $to, $subject, $body, $headers );
  }
 }
    }
}