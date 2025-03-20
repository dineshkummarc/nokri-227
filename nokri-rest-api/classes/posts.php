<?php

  add_action( 'init', 'nokriAPI_post_type_rest_support', 25 );
  function nokriAPI_post_type_rest_support() {
  	global $wp_post_types;
		/*be sure to set this to the name of your post type!*/
		$post_type_name = 'post';
		if( isset( $wp_post_types[ $post_type_name ] ) ) 
		{
			$wp_post_types[$post_type_name]->show_in_rest = true;
			$wp_post_types[$post_type_name]->rest_base = $post_type_name;
			$wp_post_types[$post_type_name]->rest_controller_class = 'WP_REST_Posts_Controller';
		}
  
  }	
  

add_action( 'rest_api_init', 'nokriAPI_hook_for_getting_posts', 0 );
function nokriAPI_hook_for_getting_posts() {

    register_rest_route(
        'nokri/v1', '/posts/', array(
				'methods'  => WP_REST_Server::READABLE,
				'callback' => 'nokriAPI_get_posts_get',
				'permission_callback' => function () { return nokriAPI_basic_auth();  },
        	)
    );
    register_rest_route(
        'nokri/v1', '/posts/', array(
				'methods'  => WP_REST_Server::EDITABLE,
				'callback' => 'nokriAPI_get_posts_get',
				'permission_callback' => function () { return nokriAPI_basic_auth();  },
        	)
    );	
}  

if( !function_exists('nokriAPI_get_posts_get' ) )
{
	function nokriAPI_get_posts_get( $request )
	{
		$json_data = $request->get_json_params();
		if ( get_query_var( 'paged' ) ) {
			$paged = get_query_var( 'paged' );
		} else if ( isset( $json_data['page_number'] ) ) {
			$paged = $json_data['page_number'];
		} else {
			$paged = 1;
		}		
		$posts_per_page = get_option( 'posts_per_page' );					
		$args = array(
			'post_type' => 'post',
			'post_status' => 'publish', 
			'posts_per_page' => $posts_per_page, 
			'paged' => $paged, 
			'order'=> 'DESC', 
			'orderby' => 'ID' 
		);	
                                            
		$message = '';
		$posts = new WP_Query( $args );
		$data = array();
		$arr  = array();
		$post_data = array();
		if ( $posts->have_posts() ) {
	
			while ( $posts->have_posts() ) 
			{
				$posts->the_post();
				$post_id 			= 	get_the_ID();
				$arr['post_id'] 	= 	$post_id;
				$arr['title'] 		= 	nokriAPI_convert_uniText(get_the_title());
				$arr['excerpt'] 	= 	nokriAPI_words_count(get_the_excerpt(), 90); 
				$arr['date'] 		=  	get_the_date("", $post_id);
				
				$list = array();
				$term_lists = wp_get_post_terms( $post_id, 'category', array( 'fields' => 'all'  ) );
				foreach($term_lists as $term_list)  $list[] = array('id' => $term_list->term_id, 'name' => $term_list->name );
				$arr['cats'] = $list;
				 $image  = get_the_post_thumbnail_url( $post_id, 'medium'); 
				 if( !$image )  
					$image = '';
					
				$arr['has_image'] = ( $image ) ? true : false; 	
				$arr['image'] = $image; 
				$comments = wp_count_comments( $post_id );
				$arr['comments'] = "$comments->approved";
				$arr['read_more'] = __("Read More", "nokri-rest-api");
				$post_data[] = $arr;
			}
			/* Restore original Post Data */
			wp_reset_postdata();
		} else {
			$message = __("no posts found", "nokri-rest-api");
		}	
		
		$data['post'] = $post_data;
		
		$nextPaged = $paged + 1;
		$has_next_page = ( $nextPaged <= (int)$posts->max_num_pages ) ? true : false;
	
	$data['pagination'] = array("max_num_pages" => (int)$posts->max_num_pages, "current_page" => (int)$paged, "next_page" => (int)$nextPaged, "increment" => (int)$posts_per_page , "current_no_of_ads" =>  (int)($posts->found_posts), "has_next_page" => $has_next_page );		
		
		$extra['page_title'] 				= __("Blog", "nokri-rest-api" );
		$extra['comment_title'] 			= __("Comments", "nokri-rest-api" );
		$extra['load_more'] 				=  __("Load More", "nokri-rest-api" );
		$extra['load_more'] 				=  __("Load More", "nokri-rest-api" );		
		$extra['comment_form']['title'] 	=  __("Post your comments here", "nokri-rest-api" );
		$extra['comment_form']['textarea'] 	=  __("Your comment here", "nokri-rest-api" );
		$extra['comment_form']['btn_submit'] =  __("Post Comment", "nokri-rest-api" );
		$extra['comment_form']['btn_cancel'] =  __("Cancel Comment", "nokri-rest-api" );	
		
		return $response = array( 'success' => true, 'data' => $data, 'message'  => $message, 'extra' => $extra);			
	}
}




/***************************/
/*Post details Start here*/
/***************************/

add_action( 'rest_api_init', 'nokriAPI_hook_for_getting_post', 0 );
function nokriAPI_hook_for_getting_post() {
    register_rest_route(
        'nokri/v1', '/posts/detail/', array(
				'methods'  => WP_REST_Server::EDITABLE,
				'callback' => 'anokriAPI_get_post_get',
				'permission_callback' => function () { return nokriAPI_basic_auth();  },
        	)
    );
}  

if( !function_exists('anokriAPI_get_post_get' ) )
{
	function anokriAPI_get_post_get( $request )
	{
		$json_data 		= $request->get_json_params();		
		$post_id 		= (isset( $json_data['post_id'] ) && $json_data['post_id'] != "" ) ? $json_data['post_id'] : '';		
		$page_num 		= (isset( $json_data['page_num'] ) && $json_data['page_num'] != "" ) ? $json_data['page_num'] : 1;	
		$post 			= get_post( $post_id );	
               
		$post_author_id = $post->post_author;
		$post_id = $post->ID;
		$arr['post_id'] = $post_id;
		$arr['author_name'] = get_the_author_meta( 'display_name', $post_author_id) ;
		$arr['title'] = nokriAPI_convert_uniText($post->post_title);
		$arr['date'] = get_the_date("", $post_id);	
		
                 
		$finalDesc = nokriAPI_youtube_url_to_iframe($post->post_content);
		$wrapHTML = '<span style=" line-height:20px;">'. wpautop($finalDesc).'</span>';
		
                
		$arr['desc'] = $wrapHTML;
		$list = array();
		$term_lists = wp_get_post_terms( $post_id, 'category', array( 'fields' => 'all'  ) );
		if( isset( $term_lists ) && count( $term_lists ) > 0 )
		foreach($term_lists as $term_list)  $list[] = array('id' => $term_list->term_id, 'name' => $term_list->name );
		$arr['cats'] = $list;		
		
		$tags = array();
		$tags_lists = wp_get_post_terms( $post_id, 'post_tag', array( 'fields' => 'all'  ) );
		if( isset( $tags_lists ) && count( $tags_lists ) > 0 )
		foreach($tags_lists as $tags_list)  $tags[] = array('id' => $tags_list->term_id, 'name' => $tags_list->name );
		$arr['tags'] = $tags;	
		

		$image  = get_the_post_thumbnail_url( $post_id, 'medium'); 
		if( !$image )  
			$image = '';
	
		$arr['has_image'] = ( $image ) ? true : false; 
		$arr['image'] = $image; 					
		$arr['comment_status'] = $post->comment_status;
		$arr['comment_count'] =  $post->comment_count;
		
		$arr['has_comment'] = ($post->comment_count > 0  ) ? true : false;
		$comment_mesage = '';
		if( $post->comment_status == 'closed' )
		{
			$comment_mesage = __("Comment are closed", "nokri-rest-api" );
		}
		else
		{
			$comment_mesage = __("No Comment Found", "nokri-rest-api" );
		}
		$arr['comment_mesage'] = $comment_mesage;
		
		$arr['comments']  	= nokriAPI_get_post_comments( $post_id, $post->comment_count,$page_num );
		
                $data['post'] 		= $arr;
		
		$extra['page_title'] = __("Blog Details", "nokri-rest-api" );
		$extra['comment_title'] = __("Comments", "nokri-rest-api" );
		$extra['load_more'] =  __("Load More", "nokri-rest-api" );
		$extra['load_more'] =  __("Load More", "nokri-rest-api" );
		
		$extra['comment_form']['title'] =  __("Post your comments here", "nokri-rest-api" );
		$extra['comment_form']['textarea'] =  __("Your comment here", "nokri-rest-api" );
		$extra['comment_form']['btn_submit'] =  __("Post Comment", "nokri-rest-api" );
		$extra['comment_form']['btn_cancel'] =  __("Cancel Comment", "nokri-rest-api" );
		
		$extra['publish']['name'] 	=  __("Your name here", "nokri-rest-api" );
		$extra['publish']['email'] 	=  __("Your email here", "nokri-rest-api" );
		$extra['publish']['comment'] 	=  __("Your comments here", "nokri-rest-api" );
		$extra['publish']['btn'] 	=  __("Publish", "nokri-rest-api" );
		$extra['publish']['msg'] 	=  __("Please login first", "nokri-rest-api" );
		
		
		return $response = array( 'success' => true, 'data' => $data, 'message'  => '', 'extra' => $extra);			
		
		
		
	}
}

/***************************/
/*  Post Comments          */
/***************************/



if( !function_exists('nokriAPI_get_post_comments' ) )
{
	function nokriAPI_get_post_comments( $post_id = '', $comments_count = '' ,$page_num = '')
	{
		
		
		$paged = $page_num;		
		
		if($comments_count == "" )
		{
			$comments_count = wp_count_comments( $post_id );
			$total_posts    = $comments_count->approved;
		}
		else
		{
			$total_posts = $comments_count;	
		}
               
                   
	        $parent_comments = nokriAPI_parent_comment_counter($post_id);
                                
		$posts_per_page = get_option( 'posts_per_page' );
                
	$max_num_pages = ceil($parent_comments/$posts_per_page);		
		$paged = (int)$paged;
		$get_offset = ($paged - 1);		
		$offset		= $get_offset * $posts_per_page;		
		
		$args = array( 
						'number' => $posts_per_page, 
						'order' => 'DESC', 
						'orderby' => 'comment_ID',
						'status' => 'approve', 
						'parent' => 0, 
						'post_id' => $post_id, 
						'offset' => $offset,
										
					);				
		$comments = get_comments($args);
		$arr = array();
		$carray = array();
		
		$comments_open = comments_open( $post_id );
		
		if( count( $comments ) > 0 ) {
		foreach($comments as $comment) {	
			$arr['blog_id'] 			= $post_id;
			$arr['img'] 				= nokriAPI_user_dp( $comment->user_id );				
			$arr['comment_id'] 			= $comment->comment_ID;
			$arr['comment_author'] 		= $comment->comment_author;
			$arr['comment_content'] 	= $comment->comment_content;
			$arr['comment_date'] 		= nokriAPI_timeago($comment->comment_date);
			$arr['comment_parent'] 		= $comment->comment_parent;
			$arr['comment_author_id'] 	= $comment->user_id;
			$arr['reply_btn_text'] 		= __("Reply", "nokri-rest-api");
			
			$replies = nokriAPI_get_comment_replys($comment->comment_ID, $post_id);
			$has_childs 			= (isset( $replies ) && count( $replies ) > 0 ) ? true : false;
			$arr['can_reply'] 		= true;//( $comments_open ) ? true : false;
			$arr['has_childs'] 		= $has_childs;
			$arr['reply'] 			= $replies;
			
			$carray[] = $arr;			
		}
		}
		else
		{
			$carray[]  = __('No Comment Found','nokri-rest-api' );
		}
		
		$nextPaged 			= 	(int)($paged) + 1;
		$has_next_page 		= 	( $nextPaged <= (int)$max_num_pages ) ? true : false;
		$data['comments'] 	= 	$carray;
	
	
	$data['pagination'] = array("max_num_pages" => (int)$max_num_pages, "current_page" => (int)$paged, "next_page" => (int)$nextPaged, "increment" => (int)$posts_per_page , "current_no_of_ads" =>  (int)$total_posts, "has_next_page" => $has_next_page );		
		
		
		return $data;
	}
}

/***************************/
/* Posts Comments Replies  */
/***************************/


if( !function_exists('nokriAPI_get_comment_replys' ) )
{
	function nokriAPI_get_comment_replys($comment_id = '',$post_id = '')
	{
		$args = array( 'order' => 'DESC', 'orderby' => 'comment_ID', 'status' => 'approve', 'parent' => $comment_id, 'post_id' => $post_id, );
		$rcomments = get_comments($args);
		$rarr = array();
		$rply = array();
		$comments_open = comments_open( $post_id );
		
		if( count( $rcomments ) > 0 )
		{
			foreach($rcomments as $rcomment) 
			{	
				$rarr['blog_id'] 			= 	$post_id;	
				$rarr['img'] 				= 	nokriAPI_user_dp( $rcomment->user_id );	
				$rarr['comment_id'] 		= 	$rcomment->comment_ID;
				$rarr['comment_author'] 	= 	$rcomment->comment_author;
				$rarr['comment_content'] 	= 	$rcomment->comment_content;
				$rarr['comment_date'] 		= 	nokriAPI_timeago($rcomment->comment_date);
				$rarr['comment_parent'] 	= 	$rcomment->comment_parent;
				$rarr['comment_author_id'] 	= 	$rcomment->user_id;
				$rarr['reply_btn_text'] 	= 	__("Reply", "nokri-rest-api");
				$rarr['can_reply'] 			= 	false;	
				$rarr['has_childs'] 		= 	false;
				$rarr['reply'] 				= 	'';
										
				$rply[] = $rarr;
			 }
		}
		return $rply;
	}
}

/***************************/
/* Parent Comments Counts  */
/***************************/


function nokriAPI_parent_comment_counter($id){
    global $wpdb;
    $query = "SELECT COUNT(comment_post_id) AS count FROM $wpdb->comments WHERE `comment_approved` = 1 AND `comment_post_ID` = $id AND `comment_parent` = 0";
    $parents = $wpdb->get_row($query);
    return $parents;
}





add_action( 'rest_api_init', 'nokriAPI_hook_for_getting_comments', 0 );
function nokriAPI_hook_for_getting_comments() {

    register_rest_route(
        'nokri/v1', '/posts/comments/', array(
				'methods'  => WP_REST_Server::READABLE,
				'callback' => 'nokriAPI_get_post_comments',
				'permission_callback' => function () { return nokriAPI_basic_auth();  },
        	)
    );
	
    register_rest_route(
        'nokri/v1', '/posts/comments/get/', array(
				'methods'  => WP_REST_Server::EDITABLE,
				'callback' => 'nokriAPI_get_post_comments',
				'permission_callback' => function () { return nokriAPI_basic_auth();  },
        	)
    );	
	
    register_rest_route(
        'nokri/v1', '/posts/comments/', array(
				'methods'  => WP_REST_Server::EDITABLE,
				'callback' => 'nokriAPI_get_post_comments_submit_api',
				'permission_callback' => function () { return nokriAPI_basic_auth();  },
        	)
    );	
} 


/***************************/
/* Post Comments On posts  */
/***************************/


if( !function_exists('nokriAPI_get_post_comments_submit_api' ) )
{
	function nokriAPI_get_post_comments_submit_api( $request  )
	{

		$user = wp_get_current_user();	
		
		$user_id		 	= 	$user->data->ID;
		$display_name 		= 	$user->data->display_name;
		$user_email 		= 	$user->data->user_email;

		$json_data 			= 	$request->get_json_params();		
		$message 			= 	(isset( $json_data['message'] )) ?  $json_data['message'] : '';
		$post_id 			= 	(isset( $json_data['post_id'] )) ?  $json_data['post_id'] : '';
		$comment_id 		= 	(isset( $json_data['comment_id'] )) ?  $json_data['comment_id'] : 0;
		
		
		
		$commentdata = array(
			'comment_post_ID' => $post_id,
			'comment_author' => $display_name,
			'comment_author_email' => $user_email,
			'comment_author_url' => '',
			'comment_content' => $message,
			'comment_type' => 'comments',
			'comment_parent' => $comment_id,
			'user_id' => $user_id,
		);
		
                
                
		/*Insert new comment and get the comment ID*/
		$comment_id = wp_new_comment( $commentdata ,true );	
                
              
                
		$arr = array();
		if( is_numeric($comment_id) )
		{
			$arr['comments']  = nokriAPI_get_post_comments( $post_id );
			$success = true;
			
			$status = wp_get_comment_status( $comment_id );
			if ( $status == "approved" ) {
			 $message = __("Comment Posted Successfully.");
			}
			else
			{
				$message = __("Comment sent for approval.");
			}			
			
			
		}
		else
		{
			$arr['comments']  =  array();
			$success = false;
			$message = isset($comment_id->errors['comment_duplicate'][0]) ? __("Duplicate comment detected; it looks as though you have already said that!","nokri_rest_api"): __("Some Error Ocuured While Posting comment",'nokri-rest-api' );
			
		}
		
		return $response = array( 'success' => $success, 'data' => $arr, 'message'  => $message);
	}
	
}


/*Time Ago*/
if ( ! function_exists( 'nokriAPI_timeago' ) ) {
function nokriAPI_timeago($date) {
	   $timestamp = strtotime($date);	
	   
	   $strTime = array(__('second','nokri'), __('minute','nokri'),__('hour','nokri'),__('day','nokri'),__('month','nokri'),__('year','nokri') );
	   $length = array("60","60","24","30","12","10");

	   $currentTime = time();
	   if($currentTime >= $timestamp) {
			$diff     = time()- $timestamp;
			for($i = 0; $diff >= $length[$i] && $i < count($length)-1; $i++) {
			$diff = $diff / $length[$i];
			}

			$diff = round($diff); 
			return $diff . " " . $strTime[$i] . __('(s) ago','nokri-rest-api' );
	   }
	}
}



if (!function_exists('nokriAPI_youtube_url_to_iframe')) {

    function nokriAPI_youtube_url_to_iframe($string = '') {
        $pattern = '@(http|https)://(www\.)?youtu[^\s]*@i';
        
        $matches = array();
        preg_match_all($pattern, $string, $matches);
        foreach ($matches[0] as $matchURL) {

            preg_match('%(?:youtube(?:-nocookie)?\.com/(?:[^/]+/.+/|(?:v|e(?:mbed)?)/|.*[?&]v=)|youtu\.be/)([^"&?/ ]{11})%i', $matchURL, $match);
            $vID = (isset($match[1]) && $match[1] != "") ? $match[1] : '';
            $vidURL = 'https://www.youtube.com/embed/' . $vID;

            $string = str_replace($matchURL, '<iframe src="' . esc_url($vidURL) . '" frameborder="0" allowfullscreen style="width:100%;"></iframe>', $string);
        }
        return $string;
    }

}