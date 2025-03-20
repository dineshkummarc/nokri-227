<?php
/* ----	Register Starts Here ----*/

add_action( 'rest_api_init', 'nokriAPI_register_api_hooks_get', 0 );
function nokriAPI_register_api_hooks_get() {

    register_rest_route( 'nokri/v1', '/register/',
        array(
				'methods'  => WP_REST_Server::READABLE,
				'callback' => 'nokriAPI_register_me_get',
				'permission_callback' => function () { return true ;  },
        	)
    );
}

if( !function_exists('nokriAPI_register_me_get' ) )
{
	function nokriAPI_register_me_get()
	{
		global $nokriAPI;
		global $nokri;
		/* Is only admin can post*/
        $is_admin_update = isset($nokri['job_post_for_admin']) ? $nokri['job_post_for_admin']  : "0";
		
		$data['bg_color']				= '#000';
		$data['logo']					=   nokriAPI_appLogo();
		$data['heading']				=  __("Register With Us!", "nokri-rest-api");		
		$data['name_placeholder']		        =  __("Full Name", "nokri-rest-api");
		$data['email_placeholder']		        =  __("Email Address", "nokri-rest-api");
		$data['phone_placeholder']		        =  __("Phone Number", "nokri-rest-api");
		$data['password_placeholder']            	=  __("Password", "nokri-rest-api");
		$data['switch_cand']	                        =  __("Candidate", "nokri-rest-api");
		$data['switch_emp']	                        =  __("Employer", "nokri-rest-api");
		$data['terms_text']				=  __("Agree with our Terms and Conditions.", "nokri-rest-api");
		$data['terms_link']				=  (isset($nokriAPI['app_settings_terms_url']) && $nokriAPI['app_settings_terms_url']) ? $nokriAPI['app_settings_terms_url'] : '';
		$data['form_btn']				=  __("Register", "nokri-rest-api");
		$data['separator']				=  __("OR", "nokri-rest-api");
		$data['facebook_btn']			        =  __("Facebook", "nokri-rest-api");
		$data['google_btn']				=  __("Google+", "nokri-rest-api");
		$data['login_text']				=  __("Already have an account? Login Here", "nokri-rest-api");
		$data['is_admin_post']		                =  $is_admin_update;
                
                $data['is_password_strength']		        = isset($nokri['password_validator_switch'])  ?  $nokri['password_validator_switch']  :  false;  ;
                $data['password_strength_text']		        = esc_html__('Password must contain at least one uppercase letter,one lowercase letter,one numeric digit, 8 characters long','nokri-rest-api');
                              
                $data['is_newsletter']		        = isset($nokri['subscribe_on_user_register'])  ?  $nokri['subscribe_on_user_register']  :  false;  ;
                
                $data['newsletter-text']	   = esc_html__('Subscribe to Newsletter','nokri-rest-api');
		    
                    $custom_feild_id       = (isset($nokri['custom_registration_feilds'])) ? $nokri['custom_registration_feilds'] : '';
                    $custom_filed_data     = '';
                    if($custom_feild_id)
			{
				$custom_filed_data = nokri_get_register_custom_feildsApi('','Registration',$custom_feild_id,'','');
			}
                  
                        $data['custom_fields'] = $custom_filed_data;
                        
		return $response = array( 'success' => true, 'data' => $data, 'message'  => ''  );				
	}
}
function nokri_get_register_custom_feildsApi($author_id = '',$feilds_for = '',$id = '',$edit_profile = '',$show_profile = '',$email = false )
	{           
		if($author_id == '')
		{
			$user_id          =    get_current_user_id();
		}
		else
		{
			$user_id          =     $author_id;
		}
		$edit_profile         =    $edit_profile;
		$args                 =    array(
		    'p'               =>   $id,
			'post_type'       =>   'custom_feilds',
			'post_status'     =>   'publish',
			'posts_per_page'  =>   -1,
			'meta_query'      =>    array( array( 'key' => '_custom_feild_for', 'value' => $feilds_for )),
		);
		$args = nokri_wpml_show_all_posts_callback($args);
		$posts = new WP_Query( $args );
		$custom_feilds = '';
		if ( $posts -> have_posts() )
		{
			while ( $posts -> have_posts() )
			 {
				 $posts->the_post();
				 the_content();
				 $id                = get_the_id();
				 $custom_feilds_for = get_post_meta($id, '_custom_feild_for', true );
				 $custom_feilds     = json_decode(get_post_meta( $id, '_custom_feilds', true ));
			}
		}
		wp_reset_query();
		$custom_feilds_html =  $read_only =  $requires     =  '';
                
                $myhtml = array();
		if (is_array($custom_feilds))
	    {
			 foreach($custom_feilds as $value) 
			 {
				 $field_type   = $value->feild_type;
				 $field_label  = $value->feild_label;
				 $field_value  = $value->feild_value;
				 $field_required    = $value->feild_req;
				 $field_public   = $value->feild_pub;
                                 
                                 $field_req = false;
                                 if($field_required = "yes"){
                                      $field_req = true;
                                 } 
                                 else{
                                      $field_req = false;
                                 }
                                           
                                 $field_pub = true;
                                 if($field_public = "yes"){
                                     $field_pub =true;
                                 }
                                 else{
                                     $field_pub =false;
                                 }
                                
                                 
				 $field_values = (explode("|",$field_value));
				 if(!$show_profile)
				 {
					 /* Check boxes */
					 if($field_type == 'RadioButton')
					 {
						$check_html = array();
                                                $values = array(); 
						foreach($field_values as $value ) 
						{
							$field_slug   =  preg_replace('/\s+/', '', $field_label);
							$meta_value   =  get_user_meta($user_id, $field_slug, true);
							$checked      =  ($meta_value == $value) ? true : false;														
                                                        $values[]   = array(
                                                            
                                                            "value"          =>   $value,
                                                            "name"           =>   $value,                           
                                                            "selected"       =>   $checked,
                                                        ); 
                                                        
                                                }
                                                
                                                       $myhtml[] =  array("main_title" => esc_html($field_label), "field_type" => 'checkbox', "field_type_name" =>"$field_slug",  "value" => $values, "is_required" => $field_req , "is_show" =>$field_pub );
							
					}
					 /* Input */
					 if($field_type == 'Input')
					 {
							$field_slug   =  preg_replace('/\s+/', '', $field_label);
							$meta_value   =  get_user_meta($user_id, $field_slug, true);				                                                        
                                                        $myhtml[] = array("main_title" =>$field_label, "field_type" => 'textfield', "field_type_name" =>"$field_slug", "field_val" => $field_value,"value" => $meta_value,"is_required" => $field_req,"is_show" =>$field_pub );
					 }
					 /* Number */
					 if($field_type == 'Number')
					 {
							$field_slug   =  preg_replace('/\s+/', '', $field_label);
							$meta_value   =  get_user_meta($user_id, $field_slug, true);
							$requires = '';
							
                                                        
                                                      $myhtml[] = array("main_title" =>$field_label, "field_type" => 'Number', "field_type_name" =>"$field_slug","field_val" =>$field_value, "value" => $meta_value, "is_required" => $field_req,"is_show" =>$field_pub);   
							
					 }
					 /* Text Area */
					 if($field_type == 'Text Area')
					 {
							$field_slug   =  preg_replace('/\s+/', '', $field_label);
							$meta_value   =  get_user_meta($user_id, $field_slug, true);						
                                                        $myhtml[] = array("main_title" =>$field_label, "field_type" => 'Textarea', "field_type_name" =>"$field_slug","field_val" =>$field_value, "value" => $meta_value, "is_required" => $field_req,"is_show" =>$field_pub);   
							
					 }
					  /* Select Box */
					 if($field_type == 'Select Box')
					 {
							$options      =  $selected = '';
							$field_slug   =  preg_replace('/\s+/', '', $field_label);
							$meta_value   =  get_user_meta($user_id, $field_slug, true);
                                                        $values = array();
							foreach($field_values as $value ) 
							{
								$selected     = ($value == $meta_value) ? true : false;
								$values[]     =   array(
                                                                    
                                                                    "value"      =>   $value,
                                                                    "selected"   =>   $selected,
                                                                    "name"       =>   $value,                                                               
                                                                );
							}				
                                                 $myhtml[] = array("main_title" =>$field_slug, "field_type" => 'select', "field_type_name" =>"$field_slug", "value" =>$values, "is_required" => $field_req ,"is_show" =>$field_pub);       
							
						}
					  /* Date  */
					 if($field_type == 'Date')
					 {              
                                                     
							$field_slug   =  preg_replace('/\s+/', '', $field_label);
							$meta_value   =  get_user_meta($user_id, $field_slug, true);
							
							$myhtml[] = array("main_title" =>$field_slug, "field_type" => 'date', "field_type_name" =>"$field_slug","field_val" =>$field_value, "value" =>$meta_value, "is_required" => $field_req ,"is_show" =>$field_pub);       
						
					 }
				 }
				 else
				 {  
					 if($field_pub == 'Yes')
					 {
							 $field_slug   =  preg_replace('/\s+/', '', $field_label);
							 $meta_value   =  get_user_meta($user_id, $field_slug, true);
							 if($meta_value != '')
							 $custom_feilds_html .=  '<li><small>'.$field_label.'</small><strong>'.$meta_value.'</li></strong>';
							 else
							 $custom_feilds_html .= '';
					 }
				 }
			 }
		}
		return $myhtml ;
	}
	add_action( 'rest_api_init', 'nokriAPI_register_api_hooks_post', 0 );
	function nokriAPI_register_api_hooks_post() {
	
		register_rest_route( 'nokri/v1', '/register/',
			array(
					'methods'  => WP_REST_Server::EDITABLE,
					'callback' => 'nokriAPI_register_me_post',
					'permission_callback' => function () { return true ;  },
				)
		);
	}

        
        


    function nokriAPI_register_me_post( $request ) {		
		
		global $nokri;
		$json_data = $request->get_json_params();		
		//NOKRI_API_ALLOW_EDITING
		$mode = nokriAPI_allow_editing($request->get_method());
		if(isset($mode) && count($mode) > 0){return $mode;}	
		if( !isset( $json_data ) && count($json_data) > 0 )
		{
			
			$response = array( 'success' => false, 'data' => '' , 'message' => __("Please fill out all fields....", "nokri-rest-api") );
			return rest_ensure_response( $response );

		}	
		$from	 	= (isset($json_data['from'])) ? $json_data['from'] : '';
		$name 		= (isset($json_data['name'])) ? $json_data['name'] : '';
		$email 		= (isset($json_data['email'])) ? $json_data['email'] : '';
		$phone 		= (isset($json_data['phone'])) ? $json_data['phone'] : '';
		$password 	= (isset($json_data['pass'])) ? $json_data['pass'] : '';
		$type 		= (isset($json_data['type'])) ? $json_data['type'] : '';
                $custom_filed  = (isset($json_data['custom_fields'])) ? (array)($json_data['custom_fields'] ): array();  
                
                $request_from = nokriAPI_getSpecific_headerVal('Nokri-Request-From');
               
                if ($request_from == 'ios') {
                 $custom_filed = json_decode(@$json_data['custom_fields'], true);
                 
                }        
		
                        if( $name == "" )
		{
			$response = array( 'success' => false, 'data' => '' , 'message' => __("Please enter name.", "nokri-rest-api") );
			return $response;
		}
		if( $email == "" )
		{
			$response = array( 'success' => false, 'data' => '' , 'message'  => __("Please enter email.", "nokri-rest-api") );
			return $response;
		}
		if( $password == "" )
		{
			$response = array( 'success' => false, 'data' => '' , 'message'  => __("Please enter password.", "nokri-rest-api") );
			return $response;
		}	
		$profile_arr = array();
		$autologin = false;
		if( email_exists($email) == false )
		{
	
				$user_name	=	explode( '@', $email );
				$u_name   	=	nokri_check_user_name( $user_name[0] );
				$uid        =	wp_create_user( $u_name, $password, $email );
                               /* Updating Custom feilds */
                                if( isset($custom_filed ) && count($custom_filed) > 0)
			{
                                    
				foreach($custom_filed as $key => $val)
				{
					if( is_array($val) )
					{
						$dataArr    = array();
						foreach($val as $k )                                      
						$dataArr[]  = $k; 
                                                $val       = stripslashes(json_encode($dataArr, JSON_UNESCAPED_UNICODE));                                                                                         
					}
                                        $dataVal = ltrim($val , ",");	
                                        
					update_user_meta($uid, $key, sanitize_text_field($val) );
				}
			}
                                /* Updating Custom feilds ends */
				wp_update_user( array( 'ID' => $uid, 'display_name' => $name ) );
				update_user_meta($uid, '_sb_contact', $phone);
				update_user_meta($uid, '_sb_reg_type', $type);
                                
                                
                               $profile_status = isset($nokri['default_profile_option']) ? $nokri['default_profile_option'] : 'pub';
				update_user_meta($uid, '_user_profile_status', esc_html($profile_status));
                                
                                
                                $subscribe_now = isset($json_data['subscribe_now']) ? $json_data['subscribe_now'] : "off";
                                 if ($subscribe_now == "on") {
                                     if (function_exists('nokri_subscribe_user_on_registration')) {
                                        nokri_subscribe_user_on_registration($uid);
                                         }
                                       }
                                
                                
                                
                                 $product_id = nokri_assign_free_package();
                              
                if (isset($product_id) && $product_id != '') {
                    if (isset($nokri['user_assign_pkg']) && $nokri['user_assign_pkg'] == '1' && $type == '1') {
                        $is_pkg_free = get_post_meta($product_id, 'op_pkg_typ', true);
                        if ($is_pkg_free == 1) {
                            nokri_free_package($product_id, $uid);
                        }
                    }
                         }
                                                                          
                /* Assign package to candidate */
                $product_cand_id = nokri_candidate_assign_free_package();
                if (isset($product_cand_id) && $product_cand_id != '') {
                    if (isset($nokri['cand_assign_pkg']) && $nokri['cand_assign_pkg'] == '1' && $type  == '0') {
                        $is_pkg_free = get_post_meta($product_cand_id, 'op_pkg_typ', true);
                        if ($is_pkg_free == 1) {
                            nokri_free_package_for_candidate($product_cand_id, $uid);
                        }
                    }
                }                                
                                
				 if( isset( $nokri['sb_new_user_email_verification'] ) && $nokri['sb_new_user_email_verification'] )
				   {
					   // Email for new user
					   if ( function_exists( 'nokri_email_on_new_user' ) )
					   {
							nokri_email_on_new_user($uid);
					   }
						$user = new WP_User($uid);
						// Remove all user roles after registration
						foreach($user->roles as $role){
						 $user->remove_role($role);
					}
						$success   = false;
						$autologin = false;
						$message = __("Please confirm your account.", "nokri-rest-api") ;
				   }
				   else
				   {
						$user_info 						= 	 get_userdata( $uid );		
						$profile_arr['id']				=    $user_info->ID;
						$profile_arr['user_email']		=    $user_info->user_email;
						$profile_arr['display_name']	=    $user_info->display_name;
						$profile_arr['phone']			=    get_user_meta($user_info->ID, '_sb_contact', true );
						$profile_arr['profile_img']		=    nokriAPI_user_dp( $user_info->ID);
						//nokri_auto_login($email, $password, true );
						$success = true;
						$autologin = true;
						$message = __("Register Successfully.", "nokri-rest-api") ;
				   }
				
			}
		else
		{
			$success = false;
			$message = __("Email already exist, please try other one.", "nokri-rest-api") ;
		}
	
			$data['auto_login'] = $autologin;
			$data['profile_data'] = $profile_arr;
			$response = array( 'success' => $success, 'data' => $data, 'message' => $message);	
			return $response;		
        
    }
	
/*Forgot*/
add_action( 'rest_api_init', 'nokriAPI_forgot_api_hooks_get', 0 );
function nokriAPI_forgot_api_hooks_get() {

    register_rest_route( 'nokri/v1', '/forgot/',
        array(
				'methods'  => WP_REST_Server::READABLE,
				'callback' => 'nokriAPI_forgot_me_get',
				 'permission_callback' => function () { return true ;  },
        	)
    );
}
if( !function_exists('nokriAPI_forgot_me_get' ) )
{
	function nokriAPI_forgot_me_get()
	{		
		$data['bg_color']			= '#000';
		$data['logo']				= nokriAPI_appLogo();
		$data['heading']			=  __("Forgot Password?", "nokri-rest-api");
		$data['text']				=  __("Please enter your email address below.", "nokri-rest-api");
		$data['email_placeholder']	=  __("Email Address", "nokri-rest-api");		
		$data['submit_text']		=  __("Submit", "nokri-rest-api");
		$data['back_text']			=  __("Back", "nokri-rest-api");
		return $response = array( 'success' => true, 'data' => $data, 'message'  => ''  );		
	}
}


