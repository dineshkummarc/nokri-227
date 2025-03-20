<?php

define('nokriAPI_IMG_DEFAULT', NOKRI_API_PLUGIN_URL."/images/placeholder.png");

add_action('admin_init', 'nokriAPI_taxonomiesIMG_init');
function nokriAPI_taxonomiesIMG_init() {
	$nokriAPI_taxonomies = get_taxonomies();
	if (is_array($nokriAPI_taxonomies)) {
	    foreach ($nokriAPI_taxonomies as $nokriAPI_taxonomy) {
			if( $nokriAPI_taxonomy == "job_category" )
			{
				add_action($nokriAPI_taxonomy.'_add_form_fields', 'nokriAPI_add_texonomy_field');
				add_action($nokriAPI_taxonomy.'_edit_form_fields', 'nokriAPI_edit_texonomy_field');
				add_filter( 'manage_edit-' . $nokriAPI_taxonomy . '_columns', 'nokriAPI_taxonomyIMG_columns' );
				add_filter( 'manage_' . $nokriAPI_taxonomy . '_custom_column', 'nokriAPI_taxonomyIMG_column', 10, 3 );
			}
	    }
	}
}


if (!function_exists('nokriAPI_add_style'))
{
function nokriAPI_add_style() {
	echo '<style type="text/css" media="screen">th.column-thumb {width:60px;}.form-field img.taxonomy-image {border:1px solid #eee;max-width:300px;max-height:300px;}.inline-edit-row fieldset .thumb label span.title {width:48px;height:48px;border:1px solid #eee;display:inline-block;}.column-thumb span {width:48px;height:48px;border:1px solid #eee;display:inline-block;}.inline-edit-row fieldset .thumb img,.column-thumb img {width:48px;height:48px;}</style>';
}
}
// add image field in add form
if (!function_exists('nokriAPI_add_texonomy_field'))
{
function nokriAPI_add_texonomy_field() {
	wp_enqueue_media();
	
	echo '<div class="form-field">
		<label for="taxonomy_image">' . __('Image', 'nokri-rest-api') . '</label>
		<input type="text" name="taxonomy_image" id="taxonomy_image" value="" />
		<br/>
		<button class="nokriAPI_upload_image_button button">' . __('Upload/Add image', 'nokri-rest-api') . '</button>
	</div>'.nokriAPI_termMedia_script();
}
}
if (!function_exists('nokriAPI_edit_texonomy_field'))
{
function nokriAPI_edit_texonomy_field($taxonomy) {

	wp_enqueue_media();
	$image_url = nokriAPI_taxonomy_image_url( $taxonomy->term_id, NULL, TRUE );
	$image_url = (nokriAPI_taxonomy_image_url( $taxonomy->term_id, NULL, TRUE ) == nokriAPI_IMG_DEFAULT) ? "" : $image_url;

	echo '<tr class="form-field"><th scope="row" valign="top"><label for="taxonomy_image">'. __('Image', 'nokri-rest-api').'</label></th><td><img class="taxonomy-image" src="' . nokriAPI_taxonomy_image_url( $taxonomy->term_id, 'medium', TRUE ) . '"/><br/><input type="text" name="taxonomy_image" id="taxonomy_image" value="'.$image_url.'" /><br /><button class="nokriAPI_remove_image_button button">' . __('Remove image', 'nokri-rest-api') . '</button><button class="nokriAPI_upload_image_button button">' . __('Upload/Add image', 'nokri-rest-api') . '</button></td></tr>'.nokriAPI_termMedia_script();
}
}
add_action('edit_term','nokriAPI_save_taxonomy_image');
add_action('create_term','nokriAPI_save_taxonomy_image');
if (!function_exists('nokriAPI_save_taxonomy_image'))
{
function nokriAPI_save_taxonomy_image($term_id) {
    if(isset($_POST['taxonomy_image']))
        update_option('nokriAPI_taxonomy_image'.$term_id, $_POST['taxonomy_image'], NULL);
}
}
if (!function_exists('nokriAPI_get_attachment_id_by_url'))
{
function nokriAPI_get_attachment_id_by_url($image_src) {
    global $wpdb;
    $query = $wpdb->prepare("SELECT ID FROM $wpdb->posts WHERE guid = %s", $image_src);
    $id = $wpdb->get_var($query);
    return (!empty($id)) ? $id : NULL;
}
}
if (!function_exists('nokriAPI_taxonomy_image_url'))
{
function nokriAPI_taxonomy_image_url($term_id = NULL, $size = 'full', $return_placeholder = false) {
	if (!$term_id) {
		if (is_category())
			$term_id = get_query_var('cat');
		elseif (is_tag())
			$term_id = get_query_var('tag_id');
		elseif (is_tax()) {
			$current_term = get_term_by('slug', get_query_var('term'), get_query_var('taxonomy'));
			$term_id = $current_term->term_id;
		}
	}
	
    $taxonomy_image_url = get_option('nokriAPI_taxonomy_image'.$term_id);
    if(!empty($taxonomy_image_url)) {
	    $attachment_id = nokriAPI_get_attachment_id_by_url($taxonomy_image_url);
	    if(!empty($attachment_id)) {
	    	$taxonomy_image_url = wp_get_attachment_image_src($attachment_id, $size);
		    $taxonomy_image_url = $taxonomy_image_url[0];
	    }
	}

    if ($return_placeholder == true)
	{
		return ($taxonomy_image_url != '') ? $taxonomy_image_url : nokriAPI_IMG_DEFAULT;
	}
	else
	{
		return $taxonomy_image_url;
	}
}
}
if (!function_exists('nokriAPI_quickEditCustomBox'))
{
function nokriAPI_quickEditCustomBox($column_name, $screen, $name) {
	if ($column_name == 'thumb') 
		echo '<fieldset>
		<div class="thumb inline-edit-col">
			<label>
				<span class="title"><img src="" alt="Thumbnail"/></span>
				<span class="input-text-wrap"><input type="text" name="taxonomy_image" value="" class="tax_list" /></span>
				<span class="input-text-wrap">
					<button class="nokriAPI_upload_image_button button">' . __('Upload/Add image', 'nokri-rest-api') . '</button>
					<button class="nokriAPI_remove_image_button button">' . __('Remove image', 'nokri-rest-api') . '</button>
				</span>
			</label>
		</div>
	</fieldset>';
}
}
if (!function_exists('nokriAPI_taxonomyIMG_columns'))
{
function nokriAPI_taxonomyIMG_columns( $columns ) {
	$new_columns = array();
	$new_columns['cb'] = $columns['cb'];
	$new_columns['thumb'] = __('Image', 'nokri-rest-api');

	unset( $columns['cb'] );

	return array_merge( $new_columns, $columns );
}
}
if (!function_exists('nokriAPI_taxonomyIMG_column'))
{
function nokriAPI_taxonomyIMG_column( $columns, $column, $id ) {
	if ( $column == 'thumb' )
		$columns = '<span><img src="' . esc_url(nokriAPI_taxonomy_image_url($id, 'thumbnail', TRUE) ). '" alt="' . esc_attr__('Thumbnail', 'nokri-rest-api') . '" class="wp-post-image" /></span>';
	
	return $columns;
}
}
if (!function_exists('nokriAPI_replaceBtnText'))
{
function nokriAPI_replaceBtnText($safe_text, $text) {
    return str_replace("Insert into Post", "Use this image", $text);
}
}
if ( strpos( $_SERVER['SCRIPT_NAME'], 'edit-tags.php' ) > 0 ) {
	add_action( 'admin_head', 'nokriAPI_add_style' );
	add_action('quick_edit_custom_box', 'nokriAPI_quickEditCustomBox', 10, 3);
	add_filter("attribute_escape", "nokriAPI_replaceBtnText", 10, 2);
}
if (!function_exists('nokriAPI_taxonomy_image'))
{
function nokriAPI_taxonomy_image($term_id = NULL, $size = 'full', $attr = NULL, $echo = true) {
	if (!$term_id) {
		if (is_category())
			$term_id = get_query_var('cat');
		elseif (is_tag())
			$term_id = get_query_var('tag_id');
		elseif (is_tax()) {
			$current_term = get_term_by('slug', get_query_var('term'), get_query_var('taxonomy'));
			$term_id = $current_term->term_id;
		}
	}
	
    $taxonomy_image_url = get_option('nokriAPI_taxonomy_image'.$term_id);
    if(!empty($taxonomy_image_url)) {
	    $attachment_id = nokriAPI_get_attachment_id_by_url($taxonomy_image_url);
	    if(!empty($attachment_id)){
	    	$taxonomy_image = wp_get_attachment_image($attachment_id, $size, FALSE, $attr);
		}else {
	    	$image_attr = '';
	    	if(is_array($attr)) {
				$image_attr .=  (!empty($attr['class'])) ? ' class="'.$attr['class'].'" ' : '';
				$image_attr .=  (!empty($attr['height'])) ? ' height="'.$attr['height'].'" ' : '';
				$image_attr .=  (!empty($attr['width'])) ? ' width="'.$attr['width'].'" ' : '';
				$image_attr .=  (!empty($attr['title'])) ? ' title="'.$attr['title'].'" ' : '';
				$image_attr .=  (!empty($attr['alt'])) ? ' alt="'.$attr['alt'].'" ' : '';
	    	}
	    	$taxonomy_image = '<img src="'.esc_url($taxonomy_image_url).'" '.$image_attr.'/>';
	    }
	}

	if ($echo) { echo $taxonomy_image; }else { return $taxonomy_image; }
}

}

if (!function_exists('nokriAPI_termMedia_script'))
{
function nokriAPI_termMedia_script() {
	return '<script type="text/javascript">
	    jQuery(document).ready(function($) {
			var wordpress_ver = "'.get_bloginfo("version").'", upload_button;
			$(".nokriAPI_upload_image_button").click(function(event) {
				upload_button = $(this);
				var frame;
				if (wordpress_ver >= "3.5") {
					event.preventDefault();
					if (frame) {
						frame.open();
						return;
					}
					frame = wp.media();
					frame.on( "select", function() {
						var attachment = frame.state().get("selection").first();
						frame.close();
						if (upload_button.parent().prev().children().hasClass("tax_list")) {
							upload_button.parent().prev().children().val(attachment.attributes.url);
							upload_button.parent().prev().prev().children().attr("src", attachment.attributes.url);
						}
						else
							$("#taxonomy_image").val(attachment.attributes.url);
					});
					frame.open();
				}
				else {
					tb_show("", "media-upload.php?type=image&amp;TB_iframe=true");
					return false;
				}
			});
			
			$(".nokriAPI_remove_image_button").click(function() {
				$(".taxonomy-image").attr("src", "'.nokriAPI_IMG_DEFAULT.'");
				$("#taxonomy_image").val("");
				$(this).parent().siblings(".title").children("img").attr("src","' . nokriAPI_IMG_DEFAULT . '");
				$(".inline-edit-col :input[name=\'taxonomy_image\']").val("");
				return false;
			});
			
			if (wordpress_ver < "3.5") {
				window.send_to_editor = function(html) {
					imgurl = $("img",html).attr("src");
					if (upload_button.parent().prev().children().hasClass("tax_list")) {
						upload_button.parent().prev().children().val(imgurl);
						upload_button.parent().prev().prev().children().attr("src", imgurl);
					}
					else
						$("#taxonomy_image").val(imgurl);
					tb_remove();
				}
			}
			
			$(".editinline").click(function() {	
			    var tax_id = $(this).parents("tr").attr("id").substr(4);
			    var thumb = $("#tag-"+tax_id+" .thumb img").attr("src");

				if (thumb != "' . nokriAPI_IMG_DEFAULT . '") {
					$(".inline-edit-col :input[name=\'taxonomy_image\']").val(thumb);
				} else {
					$(".inline-edit-col :input[name=\'taxonomy_image\']").val("");
				}
				
				$(".inline-edit-col .title img").attr("src",thumb);
			});
	    });
	</script>';
}
}