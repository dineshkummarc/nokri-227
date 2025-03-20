<?php
/*nokri job Post Email Template */
if( !function_exists('nokriAPI_get_notify_on_ad_post' ) )
{
	function nokriAPI_get_notify_on_ad_post($pid)
	{
		global  $nokriAPI;
		if( isset( $nokriAPI['sb_send_email_on_ad_post'] ) && $nokriAPI['sb_send_email_on_ad_post'] )
		{
			$to = $nokriAPI['ad_post_email_value'];
			$subject = __('New Ad', 'nokri-rest-api') . '-' . get_bloginfo( 'name' );
			$body = '<html><body><p>'.__('Got new ad','nokri-rest-api'). ' <a href="'.get_edit_post_link($pid).'">' . get_the_title($pid) .'</a></p></body></html>';
			$from		=	get_bloginfo( 'name' );
			if( isset( $nokriAPI['sb_msg_from_on_new_ad'] ) && $nokriAPI['sb_msg_from_on_new_ad'] != "" )
			{
				$from	=	$nokriAPI['sb_msg_from_on_new_ad'];
			}
			
			$headers = array('Content-Type: text/html; charset=UTF-8',"From: $from" );
			if( isset( $nokriAPI['sb_msg_on_new_ad'] ) &&  $nokriAPI['sb_msg_on_new_ad'] != "" )
			{
				
				$author_id = get_post_field ('post_author', $pid);
				$user_info = get_userdata($author_id);
				
				$subject_keywords  = array('%site_name%', '%ad_owner%', '%ad_title%');
				$subject_replaces  = array(get_bloginfo( 'name' ), $user_info->display_name, get_the_title($pid));
				
				$subject = str_replace($subject_keywords, $subject_replaces, $nokriAPI['sb_msg_subject_on_new_ad']);
	
				$msg_keywords  = array('%site_name%', '%ad_owner%', '%ad_title%', '%ad_link%');
				$msg_replaces  = array(get_bloginfo( 'name' ), $user_info->display_name, get_the_title($pid), get_the_permalink($pid) );
				
				$body = str_replace($msg_keywords, $msg_replaces, $nokriAPI['sb_msg_on_new_ad']);
	
			}
				wp_mail( $to, $subject, $body, $headers );
	
		
		}
	}
}


/*nokri job Post Email Template */
if( !function_exists('nokriAPI_get_notify_on_ad_approval' ) )
{
	function nokriAPI_get_notify_on_ad_approval($pid)
	{
		global $nokriAPI;
		$from	=	get_bloginfo( 'name' );
		if( isset( $nokriAPI['sb_active_ad_email_from'] ) && $nokriAPI['sb_active_ad_email_from'] != "" )
		{
			$from	=	$nokriAPI['sb_active_ad_email_from'];
		}
		$headers = array('Content-Type: text/html; charset=UTF-8',"From: $from" );
		if( isset( $nokriAPI['sb_active_ad_email_message'] ) &&  $nokriAPI['sb_active_ad_email_message'] != "" )
		{
			
			$author_id = get_post_field ('post_author', $pid);
			$user_info = get_userdata($author_id);
			
			$subject = $nokriAPI['sb_active_ad_email_subject'];
	
			$msg_keywords  = array('%site_name%', '%user_name%', '%ad_title%', '%ad_link%');
			$msg_replaces  = array(get_bloginfo( 'name' ), $user_info->display_name, get_the_title($pid), get_the_permalink($pid) );
			
			$to = $user_info->user_email;
			$body = str_replace($msg_keywords, $msg_replaces, $nokriAPI['sb_active_ad_email_message']);
			wp_mail( $to, $subject, $body, $headers );
		}
	}
}



/*Send Email On Forgot pass*/
if( !function_exists('nokriAPI_forgot_pass_email_text' ) )
{
function nokriAPI_forgot_pass_email_text($email = '')
{
	global $nokriAPI;
	$params = array();

	if( email_exists( $email ) == true )
	{		

		// lets generate our new password
		$random_password = wp_generate_password( 12, false );	
			
		$to = $email;
		$subject = __( 'Your new password', 'nokri' );
		
		$body = __( 'Your new password is: ', 'nokri' ) .$random_password;
		$from	=	get_bloginfo( 'name' );	
		if( isset( $nokriAPI['sb_forgot_password_from'] ) && $nokriAPI['sb_forgot_password_from'] != "" )
		{
			$from	=	$nokriAPI['sb_forgot_password_from'];
		}
		$headers = array('Content-Type: text/html; charset=UTF-8',"From: $from" );
		if( isset( $nokriAPI['sb_forgot_password_message'] ) &&  $nokriAPI['sb_forgot_password_message'] != "" )
		{
			$subject_keywords  = array('%site_name%');
			$subject_replaces  = array(get_bloginfo('name'));
			
			$subject = str_replace($subject_keywords, $subject_replaces, $nokriAPI['sb_forgot_password_subject']);

			$user          = get_user_by( 'email', $email );
			$msg_keywords  = array('%site_name%', '%user%', '%reset_link%');
			$msg_replaces  = array(get_bloginfo( 'name' ),  $user->display_name, $random_password);
			
			$body = str_replace($msg_keywords, $msg_replaces, $nokriAPI['sb_forgot_password_message']);

		}
		
		
	
			$mail = wp_mail( $to, $subject, $body, $headers );
			if( $mail )
			{
				// Get user data by field and data, other field are ID, slug, slug and login
				$update_user = wp_update_user( array (
						'ID' => $user->ID, 
						'user_pass' => $random_password
					)
				);
					$success = true;
					$message =   __( 'Email sent', 'nokri-rest-api' );	
			}
			else
			{
					$success = false;
					$message =   __( 'Email server not responding', 'nokri-rest-api' );	
			}
				
	}
	else
	{
		$success = false;
		$message =   __( 'Email is not resgistered with us.', 'nokri-rest-api' );	
	}
	
			$response = array( 'success' => $success, 'data' => '' , 'message' => $message );
			return $response;
   }
}

/*Send Email User verification*/
if( !function_exists('nokriAPI_verification_email' ) )
{
	function nokriAPI_verification_email($email = '')
	{
		global $nokriAPI;
		if( isset( $nokriAPI['sb_new_user_email_to_user'] ) && $nokriAPI['sb_new_user_email_to_user'] )
		 {
		  if( isset( $nokriAPI['sb_new_user_message'] ) &&  $nokriAPI['sb_new_user_message'] != "" && isset( $nokriAPI['sb_new_user_message_from'] ) && $nokriAPI['sb_new_user_message_from'] != "" )
		  {
		   // User info
		   $user_info = get_userdata( $user_id );
		   $to = $user_info->user_email;
		   $subject = $nokri['sb_new_user_message_subject'];
		   $from = $nokri['sb_new_user_message_from'];
		   $headers = array('Content-Type: text/html; charset=UTF-8',"From: $from" );
		   $user_name = $user_info->user_email;
		   if( $social != '' )
			$user_name .= "(Password: $social )";
			
		   $verification_link = '';
		   if( isset( $nokriAPI['sb_new_user_email_verification'] ) && $nokriAPI['sb_new_user_email_verification'] && $social == "" )
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
		   $body = str_replace($msg_keywords, $msg_replaces, $nokri['sb_new_user_message']);
		   $mail = wp_mail( $to, $subject, $body, $headers );
		   
		   
		   
		  }
		 }
	}
	//$response = array( 'success' => $success, 'data' => '' , 'message' => $message );
	
	//return $response;
}


// Forgot Password
if( !function_exists('nokriAPI_forgot_pass_email_link' ) )
{
	function nokriAPI_forgot_pass_email_link($email = '')
	{
		global $nokri;
		
		$email	=	$email;
		if( email_exists( $email ) == true )
		{
		// lets generate our new password
			$random_password = wp_generate_password( 12, false );
			
			
				$to = $email;
				
				$subject = __( 'Your new password', 'redux-framework' );
				
				$body = __( 'Your new password is: ', 'redux-framework' ) .$random_password;
				
				$from	=	get_bloginfo( 'name' );	
				
			if( isset( $nokri['sb_forgot_password_from'] ) && $nokri['sb_forgot_password_from'] != "" )
			{
				$from	=	$nokri['sb_forgot_password_from'];
			}
			
			$headers = array('Content-Type: text/html; charset=UTF-8',"From: $from" );
			if( isset( $nokri['sb_forgot_password_message'] ) &&  $nokri['sb_forgot_password_message'] != "" )
			{
				$subject_keywords  = array('%site_name%');
				$subject_replaces  = array(get_bloginfo( 'name' ));
				
				$subject = str_replace($subject_keywords, $subject_replaces, $nokri['sb_forgot_password_subject']);
	
			   $token  =  nokri_randomString(50);
			   $user = get_user_by( 'email', $email );
			   $msg_keywords  = array('%site_name%', '%user%', '%reset_link%');
			   $reset_link = trailingslashit( get_home_url() ) . '?token=' . $token . '-sb-uid-'. $user->ID;
			   $msg_replaces  = array(get_bloginfo( 'name' ),  $user->display_name, $reset_link );
			   $body = str_replace($msg_keywords, $msg_replaces, $nokri['sb_forgot_password_message']);
	
			}
				
				$mail = wp_mail( $to, $subject, $body, $headers );
				if( $mail )
				{
					// Get user data by field and data, other field are ID, slug, slug and login
					 update_user_meta($user->ID, 'sb_password_forget_token', $token);
				return 	$response = array( 'success' => true, 'data' => '' , 'message' => __( 'Sent Successfully', 'redux-framework' ) );
				}
				else
				{
					return $response = array( 'success' => false, 'data' => '' , 'message' => __( 'Email server not responding', 'redux-framework' ) );
				}
					
		}
		else
		{
			return $response = array( 'success' => false, 'data' => '' , 'message' => __( 'Email is not resgistered with us', 'redux-framework' ) );
		}
	}
}