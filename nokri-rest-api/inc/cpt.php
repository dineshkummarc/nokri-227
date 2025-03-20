<?php
// Register Custom Post Type
function nokriAPI_custom_post_type() {

	$labels = array(
		'name'                  => _x( 'App Pages', 'Post Type General Name', 'nokri-rest-api' ),
		'singular_name'         => _x( 'App Page', 'Post Type Singular Name', 'nokri-rest-api' ),
		'menu_name'             => __( 'App Pages', 'nokri-rest-api' ),
		'name_admin_bar'        => __( 'App Page', 'nokri-rest-api' ),
		'archives'              => __( 'Item Archives', 'nokri-rest-api' ),
		'attributes'            => __( 'Item Attributes', 'nokri-rest-api' ),
		'parent_item_colon'     => __( 'Parent Item:', 'nokri-rest-api' ),
		'all_items'             => __( 'All Items', 'nokri-rest-api' ),
		'add_new_item'          => __( 'Add New Item', 'nokri-rest-api' ),
		'add_new'               => __( 'Add New', 'nokri-rest-api' ),
		'new_item'              => __( 'New Item', 'nokri-rest-api' ),
		'edit_item'             => __( 'Edit Item', 'nokri-rest-api' ),
		'update_item'           => __( 'Update Item', 'nokri-rest-api' ),
		'view_item'             => __( 'View Item', 'nokri-rest-api' ),
		'view_items'            => __( 'View Items', 'nokri-rest-api' ),
		'search_items'          => __( 'Search Item', 'nokri-rest-api' ),
		'not_found'             => __( 'Not found', 'nokri-rest-api' ),
		'not_found_in_trash'    => __( 'Not found in Trash', 'nokri-rest-api' ),
		'featured_image'        => __( 'Featured Image', 'nokri-rest-api' ),
		'set_featured_image'    => __( 'Set featured image', 'nokri-rest-api' ),
		'remove_featured_image' => __( 'Remove featured image', 'nokri-rest-api' ),
		'use_featured_image'    => __( 'Use as featured image', 'nokri-rest-api' ),
		'insert_into_item'      => __( 'Insert into item', 'nokri-rest-api' ),
		'uploaded_to_this_item' => __( 'Uploaded to this item', 'nokri-rest-api' ),
		'items_list'            => __( 'Items list', 'nokri-rest-api' ),
		'items_list_navigation' => __( 'Items list navigation', 'nokri-rest-api' ),
		'filter_items_list'     => __( 'Filter items list', 'nokri-rest-api' ),
	);
	$args = array(
		'label'                 => __( 'App Page', 'nokri-rest-api' ),
		'description'           => __( 'App Page is design to set the app layouts', 'nokri-rest-api' ),
		'labels'                => $labels,
		'supports'              => array( 'title', 'editor', ),
		'hierarchical'          => true,
		'public'                => false,
		'show_ui'               => true,
		'show_in_menu'          => true,
		'menu_position'         => 5,
		'show_in_admin_bar'     => true,
		'show_in_nav_menus'     => true,
		'can_export'            => true,
		'has_archive'           => false,		
		'exclude_from_search'   => true,
		'publicly_queryable'    => false,
		'capability_type'       => 'page',
	);
	register_post_type( 'app_page', $args );

}
add_action( 'init', 'nokriAPI_custom_post_type', 0 );