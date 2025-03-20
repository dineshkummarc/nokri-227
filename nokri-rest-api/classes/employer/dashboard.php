<?php

add_action( 'rest_api_init', 'nokriAPI_employer_profile_api_hooks_dashboard', 0 );
function nokriAPI_employer_profile_api_hooks_dashboard() {
    register_rest_route(
		'nokri/v1', '/employer/dashboard/', array(
			'methods'  => WP_REST_Server::READABLE,
			'callback' => 'nokriAPI_emp_dashboard_get',
			'permission_callback' => function () { return nokriAPI_basic_auth();  },
		)
    );
}



if (!function_exists('nokriAPI_emp_dashboard_get'))
{
	function nokriAPI_emp_dashboard_get()
	{
		global $nokriAPI;
		$user_id   =  get_current_user_id();
	        $user      =  get_userdata($user_id);
		$extra     = array();
		
		$data['profile'] 	    = nokriAPI_employer_profile_get( $user->ID );
		//$data['social_icons'] = nokriAPI_canidate_social_icons( $user->ID );
		//$data['cand_cover'] 	= nokriAPI_candidate_cover( $user->ID );
		//$data['cand_dp'] 	    = nokriAPI_candidate_dp( $user->ID );
		
		//$data['tabs']['dashboard1']      = __("Dashboard1",   "nokri-rest-api");
		$data['tabs']['edit'] 		     = __("Edit Profile", "nokri-rest-api");
		$data['tabs']['full_profile']    = __("Full Profile", "nokri-rest-api");
		$data['tabs']['dashboard4']      = __("Dashboard4",   "nokri-rest-api");
		$data['tabs']['dashboard5']      = __("Dashboard5",   "nokri-rest-api");
		$data['tabs']['dashboard6']      = __("Dashboard6",   "nokri-rest-api");
		$data['tabs']['dashboard7']      = __("Dashboard7",   "nokri-rest-api");
		
		if(isset($nokriAPI['appKey_stripeSKey']) && $nokriAPI['appKey_stripeSKey'] != ''  )
		{
			$extra['appKey_stripeSKey'] = $nokriAPI['appKey_stripeSKey'];
		}
		
		
		
		
		$response = array( 'success' => true, 'data' => $data,'extra' => $extra  , "message" => "" );	
		return $response;	

		
	}
}