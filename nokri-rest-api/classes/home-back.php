<?php
/***************************/
/* Home Screen Starts Here */	
/***************************/

add_action( 'rest_api_init', 'nokriAPI_homescreen_api_hooks_get', 0 );
function nokriAPI_homescreen_api_hooks_get() {

    register_rest_route( 'nokri/v1', '/home/',
        array(
				'methods'  => WP_REST_Server::READABLE,
				'callback' => 'nokriAPI_home_secreen_get',
				'permission_callback' => function () {  return nokriAPI_basic_auth();  },
        	)
    );
}


if (!function_exists('nokriAPI_home_secreen_get'))
{
	function nokriAPI_home_secreen_get()
	{ 
  		global $nokriAPI;
		
		$tagline   		= (isset( $nokriAPI['hom_sec_tagline'] )) ? $nokriAPI['hom_sec_tagline'] : "";
		$headline   	= (isset( $nokriAPI['hom_sec_headline'] )) ? $nokriAPI['hom_sec_headline'] : "";
		$place_holder   = (isset( $nokriAPI['hom_sec_place_holder'] )) ? $nokriAPI['hom_sec_place_holder'] : ""; 
		$cats_text      = (isset( $nokriAPI['hom_sec_cats_text'] )) ? $nokriAPI['hom_sec_cats_text'] : "";
		
		$data['img'] 			=  nokritAPI_home_secreen_bg();
		$data['heading'] 	    =  $headline;
		$data['tagline'] 	    =  $tagline;
		$data['placehldr'] 	    =  $place_holder;
		$data['cats_text'] 	    =  $cats_text; 
		
		$catData =array();
		
		if( isset( $nokriAPI['nokri-api-ad-cats-multi'] ) )
		{
			$cats = $nokriAPI['nokri-api-ad-cats-multi'];
			if( count( $cats ) > 0 )
			{
				foreach($cats as $cat)
				{
					$term = get_term( $cat, 'job_category' ); 
					$name = htmlspecialchars_decode($term->name, ENT_NOQUOTES);
					$count = htmlspecialchars_decode($term->count, ENT_NOQUOTES)." ".__("Jobs", "nokri-rest-api");
					$imgUrl = nokriAPI_taxonomy_image_url( $cat, NULL, TRUE );
					$catData[] = array("job_category" => $term->term_id, "name" => $name, "img" => $imgUrl,"count" => $count);
				}
			}
		}
		
		$data['cat_icons'] = $catData;
		
		
		$response = array( 'success' => true, 'data' => $data , 'message' => '' );
		
		
		return $response;
		
		
	}
}


add_action( 'rest_api_init', 'nokriAPI_homescreen_premium_jobs_api_hooks_get', 0 );
function nokriAPI_homescreen_premium_jobs_api_hooks_get() {

    register_rest_route( 'nokri/v1', '/premium_jobs/',
        array(
				'methods'  => WP_REST_Server::EDITABLE,
				'callback' => 'nokritAPI_homescreen_premium_jobs',
				'permission_callback' => function () {  return nokriAPI_basic_auth();  },
        	)
    );
}

if (!function_exists('nokritAPI_homescreen_premium_jobs'))
{
	function nokritAPI_homescreen_premium_jobs( $request)
	{
		$json_data 	=  	$request->get_json_params();
		global $nokriAPI;
		
		$job_class      = (isset( $nokriAPI['premium_jobs_class'] )) ? $nokriAPI['premium_jobs_class']    : "";
		$jobs_limits    = (isset( $nokriAPI['premium_jobs_limit'] ) ) ? $nokriAPI['premium_jobs_limit']   : 5;
		
		$json_data 	=  	$request->get_json_params();
		$message    =  '';
		add_action( 'pre_get_posts', 'my_pre_get_posts' );	
		$jobs = array();
		
		
		
		if ( get_query_var( 'paged' ) ) {
		$paged = get_query_var( 'paged' );
		} else if ( isset( $json_data['page_number'] ) ) {
		// This will occur if on front page.
		$paged = $json_data['page_number'];
		} else {
		$paged = 1;
		}	
		
		
		
		
		$args =  array(
				'post_type'   		=> 'job_post',
				'orderby'     		=> 'DESC',
				'posts_per_page' 	=> $jobs_limits,
				'paged' 			=> 	$paged,
				'post_status' 		=> array('publish'), 
				'tax_query' => array(
						array(
							'taxonomy' => 'job_class',
							'field' => 'term_id',
							'terms' => $job_class,
						)
					), 
				 'meta_query' 		=> array(
					array(
						'key'     => '_job_status',
						'value'   => 'active',
						'compare' => '='
					)
				)
										);
		
		
		$query = new WP_Query( $args );
		if ( $query->have_posts() )
		{ 
			$count = 0;
			while ( $query->have_posts() )  
		  { 
				$query->the_post();
				$query->post_author;
				$job_id                    =      get_the_id();
				$post_author_id            =      get_post_field( 'post_author', $job_id );
				$company_name       	   =      get_the_author_meta( 'display_name', $post_author_id );
				$job_type        		   =      wp_get_post_terms($job_id, 'job_type', array("fields" => "ids"));
				$job_type	     		   =	  isset( $job_type[0] ) ? $job_type[0] : '';
				$job_salary       		   = 	  wp_get_post_terms($job_id, 'job_salary', array("fields" => "ids"));
				$job_salary	      		   =	  isset( $job_salary[0] ) ? $job_salary[0] : '';
				$job_salary_type           =      wp_get_post_terms($job_id, 'job_salary_type', array("fields" => "ids"));
				$job_salary_type	       =	  isset( $job_salary_type[0] ) ? $job_salary_type[0] : '';
				$job_currency              =      wp_get_post_terms($job_id, 'job_currency', array("fields" => "ids"));
				$job_currency	           =	  isset( $job_currency[0] ) ? $job_currency[0] : '';
				$job_adress                =      get_post_meta($job_id, '_job_address', true);
				$job_deadline_n            =      get_post_meta($job_id, '_job_date', true);
				$job_deadline              =      date_i18n(get_option('date_format'), strtotime($job_deadline_n));
				/* Getting Company  Profile Photo */
				if( isset( $nokriAPI['sb_user_dp']['url'] ) && $nokriAPI['sb_user_dp']['url'] != "" )
				{
					$image_dp_link = array($nokriAPI['sb_user_dp']['url']);  
					$image_dp_link = $image_dp_link[0];	
				}
				if( get_user_meta($post_author_id, '_sb_user_pic', true ) != "" )
				{
					$attach_id    =	get_user_meta($post_author_id, '_sb_user_pic', true );
					$image_link   = wp_get_attachment_image_src( $attach_id, 'nokri_job_post_single' );
				}	
							
				$jobs[$count][]    	=     nokriAPI_canidate_fields(__("Job Id", "nokri-rest-api"), get_the_id(), 'textfield', true,  1,'job_id');
				$jobs[$count][]    	=     nokriAPI_canidate_fields(__("Company Id", "nokri-rest-api"), $post_author_id, 'textfield', true,  1,'company_id');
				$jobs[$count][] 	=     nokriAPI_canidate_fields(__("Job Name", "nokri-rest-api"), nokriAPI_convert_uniText(get_the_title()), 'textfield', true,  1,'job_name');
				$jobs[$count][] 	=     nokriAPI_canidate_fields(__("Company Name", "nokri-rest-api"), get_the_author_meta( 'display_name', $post_author_id ), 'textfield', true,  1,'company_name');
				$jobs[$count][] 	=     nokriAPI_canidate_fields(__("Job Salary", "nokri-rest-api"), nokri_job_post_single_taxonomies('job_currency', $job_currency)." ".nokri_job_post_single_taxonomies('job_salary', $job_salary)."/".nokri_job_post_single_taxonomies('job_salary_type', $job_salary_type), 'textfield', true,  1,'job_salary');
				$jobs[$count][] 	=     nokriAPI_canidate_fields(__("Job Type", "nokri-rest-api"), nokri_job_post_single_taxonomies('job_type', $job_type), 'textfield', true,  1,'job_type');
				$jobs[$count][] 	=    nokriAPI_canidate_fields(__("Job Posted", "nokri-rest-api"),$job_deadline, 'textfield', true,  1,'job_posted');
				
				$c_image_link = (isset($image_link[0]) && is_array($image_link) && $image_link[0] != "" ) ? $image_link[0] : '';
				
				$jobs[$count][] 	=     nokriAPI_canidate_fields(__("Company Logo", "nokri-rest-api"),$c_image_link, 'textfield', true,  1,'company_logo');
				$jobs[$count][] 	=     nokriAPI_canidate_fields(__("Job Location", "nokri-rest-api"),$job_adress, 'textfield', true,  1,'job_location');
				
				$count++;
				
			}
		}
		else
		{
			$message =  __("Not posted any job yet", "nokri-rest-api");
		}
		
		
		
		
		$nextPaged = $paged + 1;
		 
		 $has_next_page = ( $nextPaged <= (int)$query->max_num_pages ) ? true : false;
		 
		 $pagination = array(
			 "max_num_pages" => (int)$query->max_num_pages,
			 "current_page" => (int)$paged, 
			 "next_page" => (int)$nextPaged, 
			 "increment" => (int)get_option( 'posts_per_page') , 
			 "current_no_of_ads" =>  (int)count($query->posts), 
			 "has_next_page" => $has_next_page 
		 );		
		
		
		
		
		$premium_heading    		= (isset( $nokriAPI['premium_jobs_heading'] ) ) ? $nokriAPI['premium_jobs_heading']   : "";
		
		
		$data['tab_title'] 		    = 	$premium_heading;
		$data['jobs']       		= 	$jobs;
		$data['number_of_jobs']     = 	$query->found_posts;
		
		  return $response = array( 'success' => true, 'data' => $data , 'message' => $message , "pagination" => $pagination);
	}
}



/***************************/
/* Home Screen Blog Section */	
/***************************/

add_action( 'rest_api_init', 'nokriAPI_homescreen_api_blogs_hooks_get', 0 );
function nokriAPI_homescreen_api_blogs_hooks_get() {

    register_rest_route( 'nokri/v1', '/home_blog/',
        array(
				'methods'  => WP_REST_Server::READABLE,
				'callback' => 'nokriAPI_get_posts_get',
				'permission_callback' => function () {  return nokriAPI_basic_auth();  },
        	)
    );
	register_rest_route( 'nokri/v1', '/home_blog/',
        array(
				'methods'  => WP_REST_Server::EDITABLE,
				'callback' => 'nokriAPI_get_posts_get',
				'permission_callback' => function () {  return nokriAPI_basic_auth();  },
        	)
    );
}




/***************************/
/* Home Screen Get BG Image */	
/***************************/

if (!function_exists('nokritAPI_home_secreen_bg'))
{
	function nokritAPI_home_secreen_bg()
	{		
		global $nokriAPI;
		$defaultsecreen = NOKRI_API_PLUGIN_URL."images/home-secreen.jpg";
		$home_secreen   = (isset( $nokriAPI['hom_sec_bg'] )) ? $nokriAPI['hom_sec_bg']['url'] : $defaultsecreen;
		return $home_secreen;		
	}
}

/***************************/
/* Home Screen 2 Starts Here */	
/***************************/

add_action( 'rest_api_init', 'nokriAPI_homescreen2_api_hooks_get', 0 );
function nokriAPI_homescreen2_api_hooks_get() {

    register_rest_route( 'nokri/v1', '/home2/',
        array(
				'methods'  => WP_REST_Server::READABLE,
				'callback' => 'nokriAPI_homescreen2_api_get',
				'permission_callback' => function () {  return nokriAPI_basic_auth();  },
        	)
    );
}
if (!function_exists('nokriAPI_homescreen2_api_get'))
{
	function nokriAPI_homescreen2_api_get()
	{ 
  		global $nokriAPI;
		$tagline   		= (isset( $nokriAPI['hom_sec_tagline'] ))              ? $nokriAPI['hom_sec_tagline'] : "";
		$headline   	= (isset( $nokriAPI['hom_sec_headline'] ))             ? $nokriAPI['hom_sec_headline'] : "";
		$key_wrd_hdng   = (isset( $nokriAPI['hom_sec_key_word_heading'] ))     ? $nokriAPI['hom_sec_key_word_heading'] : "";
		$place_holder   = (isset( $nokriAPI['hom_sec_place_holder'] ))         ? $nokriAPI['hom_sec_place_holder'] : ""; 
		$cats_text      = (isset( $nokriAPI['hom_sec_cats_plc'] ))             ? $nokriAPI['hom_sec_cats_plc'] : "";
		$radius_text    = (isset( $nokriAPI['api_home_screen_radius_txt'] ))   ? $nokriAPI['api_home_screen_radius_txt'] : "";
		$radius_value   = (isset( $nokriAPI['api_home_screen_radius_value'] )) ? $nokriAPI['api_home_screen_radius_value'] : "100";
		$btn_txt   = (isset( $nokriAPI['hom_sec_btn_txt'] )) ? $nokriAPI['hom_sec_btn_txt'] : "100";
		
		
		
		$data['img'] 				=  nokritAPI_home_secreen_bg();
		$data['logo'] 				=  nokriAPI_appLogo();
		$data['tagline'] 	    	=  $tagline;
		$data['heading'] 	    	=  $headline;
		$data['key_wrd_headng'] 	=  $key_wrd_hdng;
		$data['key_wrd_plc'] 	    =  $place_holder;
		$data['cat_plc'] 	    	=  $cats_text;
		$data['radius_text'] 		=  $radius_text;
		$data['radius_value'] 		=  $radius_value;
		$data['btn_text'] 		    =  $btn_txt; 
		
		$job_category          		=  __("Job Category", "nokri-rest-api");  
		$job_category_field			=  nokriAPI_canidate_fields($job_category,nokriAPI_job_post_taxonomies('job_category',''), 'textfield', true, __("Select an option", "nokri-rest-api"), 'job_category');
		$data['categories'] 		=  $job_category_field;
		
		$response = array( 'success' => true, 'data' => $data , 'message' => '' );
		
		
		return $response;
		
		
	}
}