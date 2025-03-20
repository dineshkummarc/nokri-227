<?php
add_action( 'rest_api_init', 'nokriAPI_about_us_get_hook', 0 );
function nokriAPI_about_us_get_hook() {
    register_rest_route(
		'nokri/v1', '/faqs/', array(
			'methods'  => WP_REST_Server::READABLE,
			'callback' => 'nokriAPI_about_us_get',
			'permission_callback' => function () { return nokriAPI_basic_auth();  },
		)
    );
}

if (!function_exists('nokriAPI_about_us_get'))
{
	function nokriAPI_about_us_get()
	{
		global $nokriAPI;
     
	
	 if (isset($nokriAPI['opt-slides']) && !empty($nokriAPI['opt-slides']))
	 {
		 	$count = 0;
			foreach($nokriAPI['opt-slides'] as $key)
			{
				$data[$count]['title']         =	  $key['title'];
				$data[$count]['Description']   =      $key['description'];
				$count++;
			}
			
	 }
	 
		
		$message = __("Edit", "nokri-rest-api");
		
		$response = array( 'success' => true, 'data' => $data, "message" => $message );	
		return $response;	

		
	}
}