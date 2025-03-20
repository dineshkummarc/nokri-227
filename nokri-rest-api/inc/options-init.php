<?php
/* Theme Options For Nokri WordPress API Theme */
if (!class_exists('Redux'))
    return;
$opt_name = "nokriAPI";
$theme = wp_get_theme();

$args = array(
    'opt_name' => 'nokriAPI',
    'dev_mode' => false,
    'display_name' => __('Nokri Apps API Options', "nokri-rest-api"),
    'display_version' => '1.5.4',
    'page_title' => __('Nokri Apps API Options', "nokri-rest-api"),
    'update_notice' => TRUE,
    'admin_bar' => TRUE,
    'menu_type' => 'submenu',
    'menu_title' => __('Apps API Options', "nokri-rest-api"),
    'allow_sub_menu' => TRUE,
    'page_parent_post_type' => 'your_post_type',
    'customizer' => TRUE,
    'default_show' => TRUE,
    'default_mark' => '*',
    'hints' => array('icon_position' => 'right', 'icon_size' => 'normal', 'tip_style' => array('color' => 'light',),
        'tip_position' => array('my' => 'top left', 'at' => 'bottom right',),
        'tip_effect' => array('show' => array('duration' => '500', 'event' => 'mouseover',),
            'hide' => array('duration' => '500', 'event' => 'mouseleave unfocus',),),
    ),
    'output' => TRUE,
    'output_tag' => TRUE,
    'settings_api' => TRUE,
    'cdn_check_time' => '1440',
    'compiler' => TRUE,
    'global_variable' => 'nokriAPI',
    'page_permissions' => 'manage_options',
    'save_defaults' => TRUE,
    'show_import_export' => TRUE,
    'database' => 'options',
    'transient_time' => '3600',
    'network_sites' => TRUE,
);



$args['share_icons'][] = array(
    'url' => 'https://www.facebook.com/scriptsbundle',
    'title' => __('Like us on Facebook', "nokri-rest-api"),
    'icon' => 'el el-facebook'
);

Redux::setArgs($opt_name, $args);


/* ------------------ App Settings ----------------------- */
Redux::setSection($opt_name, array(
    'title' => __('App Settings', "nokri-rest-api"),
    'id' => 'api_app_settings',
    'desc' => '',
    'icon' => 'el el-cogs',
    'fields' => array(
        array(
            'id' => 'app_logo',
            'type' => 'media',
            'url' => true,
            'title' => __('Logo', 'nokri-rest-api'),
            'compiler' => 'true',
            'desc' => __('Site Logo image for the site.', 'nokri-rest-api'),
            'subtitle' => __('Dimensions: 230 x 40', 'nokri-rest-api'),
            'default' => array('url' => NOKRI_API_PLUGIN_URL . "images/logo.png"),
        ),
        array(
            'id' => 'app_settings_demo',
            'type' => 'switch',
            'title' => __('Demo mode', "nokri-rest-api"),
            'desc' => __('Enable demo mode', "nokri-rest-api"),
            'default' => false,
        ),
        array(
            'id' => 'app_settings_rtl',
            'type' => 'switch',
            'title' => __('RTL', "nokri-rest-api"),
            'desc' => __('Make app RTL', "nokri-rest-api"),
            'default' => false,
        ),
        array(
            'id' => 'button-set-colour',
            'type' => 'color',
            'transparent' => false,
            'title' => __('Select App Colour', 'redux-framework-demo'),
            'subtitle' => __('Pick a title color for the app (default: #fb236a).', 'nokri-rest-api'),
            'default' => '#fb236a',
        ),
        array(
            'id' => 'app_settings_demo_mode',
            'type' => 'switch',
            'title' => __('Demo mode', "nokri-rest-api"),
            'desc' => __('Enable demo mode', "nokri-rest-api"),
            'default' => false,
        ),
        array(
            'id' => 'gmap_lang',
            'type' => 'text',
            'title' => __('App Language', 'nokri-rest-api'),
            'desc' => nokriAPI_make_link('https://developers.google.com/maps/faq#languagesupport', __('List of available languages.', 'nokri-rest-api')) . __('If you have selected RTL put language code here like for arabic ar', "nokri-rest-api"),
            'default' => 'en',
        ),
        array(
            'id' => 'sb_user_dp',
            'type' => 'media',
            'url' => true,
            'title' => __('Default user picture', 'nokri-rest-api'),
            'compiler' => 'true',
            'subtitle' => __('Dimensions: 200 x 200', 'nokri-rest-api'),
            'default' => array('url' => NOKRI_API_PLUGIN_URL . "images/user.jpg"),
        ),
        array(
            'id' => 'app_settings_fb_btn',
            'type' => 'switch',
            'title' => __('Facebook Login/Register', "nokri-rest-api"),
            'desc' => __('Show or hide google button.', "nokri-rest-api"),
            'default' => true,
        ),
        array(
            'id' => 'app_settings_google_btn',
            'type' => 'switch',
            'title' => __('Google Login/Register', "nokri-rest-api"),
            'desc' => __('Show or hide google button.', "nokri-rest-api"),
            'default' => true,
        ),
        array(
            'id' => 'app_settings_linkedin_btn',
            'type' => 'switch',
            'title' => __('Linkedin Login/Register', "nokri-rest-api"),
            'desc' => __('Show or hide Linkedin button.', "nokri-rest-api"),
            'default' => true,
        ),
        array(
            'id' => 'app_settings_apple_btn',
            'type' => 'switch',
            'title' => __('Apple  Login/Register', "nokri-rest-api"),
            'desc' => __('Show or hide Apple button.', "nokri-rest-api"),
            'default' => true,
            'required' => array('app_settings_ios_switch', '=', true),
        ),
        array(
            'id' => 'app_theme_style',
            'type' => 'button_set',
            'title' => __('Select Style', 'nokri'),
            'subtitle' => __('Select Your Desired Style', 'nokri'),
            'options' => array(
                'style1' => 'List style 1',
                'style2' => 'List style 2',              
            ),
            'defualt' => 'style1'
        ),
    )
));

Redux::setSection($opt_name, array(
    'title' => __('App Key Settings', "nokri-rest-api"),
    'id' => 'api_keys_settings',
    'desc' => '',
    'icon' => 'el el-key',
    'fields' => array(
    )
));

Redux::setSection($opt_name, array(
    'title' => __('Android Key Settings', "nokri-rest-api"),
    'id' => 'api_key_settings_android',
    'desc' => '',
    'icon' => 'el el-key',
    'subsection' => true,
    'fields' => array(
        array(
            'id' => 'api_key_settings-alert_andriod',
            'type' => 'info',
            'notice' => false,
            'style' => 'info',
            'title' => __('Alert', 'nokri-rest-api'),
            'desc' => __('Once added be carefull editing next time. Those Key Should Be Same In App Header', 'nokri-rest-api')
        ),
        array(
            'id' => 'api_key_settings-alert_andriod2',
            'type' => 'info',
            'notice' => false,
            'style' => 'info',
            'title' => __('Info', 'nokri-rest-api'),
            'desc' => __('Below section is only for if you have purchased Android App. Then turn it on and enter the purchase code in below text field that will appears.', 'nokri-rest-api')
        ),
        array(
            'id' => 'app_settings_andriod_switch',
            'type' => 'switch',
            'title' => __('For Android App', "nokri-rest-api"),
            'desc' => __('	If you have purchased the android app.', "nokri-rest-api"),
            'default' => false,
        ),
        array(
            'required' => array('app_settings_andriod_switch', '=', true),
            'id' => 'appKey_pCode',
            'type' => 'text',
            'title' => __('Enter You Android  Purchase Code Here', 'nokri-rest-api'),
            'default' => '',
            'text_hint' => array(
                'title' => __('Alert', 'nokri-rest-api'),
                'content' => __('Once added be carefull editing next time. This key Should be same in app header.'),
            )
        ),
        array(
            'required' => array('app_settings_andriod_switch', '=', true),
            'id' => 'appKey_Scode',
            'type' => 'text',
            'title' => __('Enter Your Android  Secret Code Here', 'nokri-rest-api'),
            'default' => '',
            'text_hint' => array(
                'title' => __('Alert', 'nokri-rest-api'),
                'content' => __('Once added be carefull editing next time. This key Should be same in app header.'),
            )
        ),
    )
));



Redux::setSection($opt_name, array(
    'title' => __('IOS Key Settings', "nokri-rest-api"),
    'id' => 'api_key_settings_ios',
    'desc' => '',
    'icon' => 'el el-key',
    'subsection' => true,
    'fields' => array(
        array(
            'id' => 'api_key_settings-alert_ios',
            'type' => 'info',
            'notice' => false,
            'style' => 'info',
            'title' => __('Alert', 'nokri-rest-api'),
            'desc' => __('Once added be carefull editing next time. Those Key Should Be Same In App Header', 'nokri-rest-api')
        ),
        array(
            'id' => 'api_key_settings-alert_ios2',
            'type' => 'info',
            'notice' => false,
            'style' => 'info',
            'title' => __('Info', 'nokri-rest-api'),
            'desc' => __('Below section is only for if you have purchased Ios App. Then turn it on and enter the purchase code in below text field that will appears.', 'nokri-rest-api')
        ),
        array(
            'id' => 'app_settings_ios_switch',
            'type' => 'switch',
            'title' => __('For Ios App', "nokri-rest-api"),
            'desc' => __('	If you have purchased the android app.', "nokri-rest-api"),
            'default' => false,
        ),
        array(
            'required' => array('app_settings_ios_switch', '=', true),
            'id' => 'appKey_pCode_ios',
            'type' => 'text',
            'title' => __('Enter You Purchase Code Here', 'nokri-rest-api'),
            'default' => '',
            'text_hint' => array(
                'title' => __('Alert', 'nokri-rest-api'),
                'content' => __('Once added be carefull editing next time. This key Should be same in app header.'),
            )
        ),
        array(
            'required' => array('app_settings_ios_switch', '=', true),
            'id' => 'appKey_Scode_ios',
            'type' => 'text',
            'title' => __('Enter Your Secret Code Here', 'nokri-rest-api'),
            'default' => '',
            'text_hint' => array(
                'title' => __('Alert', 'nokri-rest-api'),
                'content' => __('Once added be carefull editing next time. This key Should be same in app header.'),
            )
        ),
    )
));

Redux::setSection($opt_name, array(
    'title' => __('Stripe Settings', "nokri-rest-api"),
    'id' => 'api_stripe_key_settings',
    'desc' => '',
    'icon' => 'el el-key',
    'subsection' => true,
    'fields' => array(
        array(
            'id' => 'stripe_pulish_key',
            'type' => 'text',
            'title' => __('Enter Your Stripe Publishable key Here', 'nokri-rest-api'),
            'default' => '',
            'desc' => __('This will use in app', 'nokri-rest-api'),
            'text_hint' => array(
                'title' => __('Alert', 'nokri-rest-api'),
                'content' => __('Once added be carefull editing next time. This key Should be same in app header.'),
            )
        ),
        array(
            'id' => 'appKey_stripeSKey',
            'type' => 'text',
            'title' => __('Enter Your Stripe Secret key Here', 'nokri-rest-api'),
            'default' => '',
            'desc' => __('This will use at server for varification', 'nokri-rest-api'),
            'text_hint' => array(
                'title' => __('Alert', 'nokri-rest-api'),
                'content' => __('Once added be carefull editing next time. This key Should be same in app header.'),
            )
        ),
    )
));




Redux::setSection($opt_name, array(
    'title' => __('InApp Purchase Settings', "nokri-rest-api"),
    'id' => 'api_payment_inapp',
    'desc' => '',
    'icon' => 'el el-key',
    'subsection' => true,
    'fields' => array(
        array(
            'id' => 'api-inapp-android-app',
            'type' => 'switch',
            'title' => __('Android InApp Purchase', 'nokri-rest-api'),
            'default' => false,
            'desc' => __('If you have purchased the android app.', 'nokri-rest-api'),
        ),
        array(
            'required' => array(
                'api-inapp-android-app',
                '=',
                true
            ),
            'id' => 'api_inapp-info1-1',
            'type' => 'info',
            'notice' => false,
            'style' => 'info',
            'title' => __('Info', 'nokri-rest-api'),
            'desc' => __('Go to Application then you will see Development tools option on the left side of menu. Click this option now navigate to Services &APIs. Now you will Licensing & in-app billing section copy the key from here.', 'nokri-rest-api')
        ),
        array(
            'required' => array(
                'api-inapp-android-app',
                '=',
                true
            ),
            'id' => 'inApp_androidSecret',
            'type' => 'textarea',
            'title' => __('Your Android InApp Secret Code Here', 'nokri-rest-api'),
            'default' => '',
            'desc' => __('Enter the secret code you got from store. While copy paste please make sure there is no white space.', 'nokri-rest-api'),
            'text_hint' => array(
                'title' => __('Alert', 'nokri-rest-api'),
                'content' => __('Once added be carefull editing next time.'),
            ),
        ),
        array(
            'id' => 'api-inapp-ios-app',
            'type' => 'switch',
            'title' => __('AppStore InApp Purchase', 'nokri-rest-api'),
            'default' => false,
            'desc' => __('If you have purchased the AppStore app.', 'nokri-rest-api'),
        ),
        array(
            'required' => array(
                'api-inapp-ios-app',
                '=',
                true
            ),
            'id' => 'inApp_iosSecret',
            'type' => 'textarea',
            'title' => __('Your AppStore InApp Secret Code Here', 'nokri-rest-api'),
            'default' => '',
            'desc' => __('Enter the secret code you got from store. While copy paste please make sure there is no white space.', 'nokri-rest-api'),
            'text_hint' => array(
                'title' => __('Alert', 'nokri-rest-api'),
                'content' => __('Once added be carefull editing next time.'),
            ),
        ),
    )
));

Redux::setSection($opt_name, array(
    'title' => __('Paypal Settings', "nokri-rest-api"),
    'id' => 'api_payment_paypal',
    'desc' => '',
    'icon' => 'el el-key',
    'subsection' => true,
    'fields' => array(
        array(
            'id' => 'appKey_paypalMode',
            'type' => 'button_set',
            'title' => __('Paypal Mode', 'nokri-rest-api'),
            'options' => array(
                'live' => __('Live', 'nokri-rest-api'),
                'sandbox' => __('Sandbox', 'nokri-rest-api'),
            ),
            'default' => 'live',
        ),
        array(
            'id' => 'appKey_paypalKey',
            'type' => 'text',
            'title' => __('Enter Your Paypal Key', 'nokri-rest-api'),
            'default' => '',
            'text_hint' => array(
                'title' => __('Alert', 'nokri-rest-api'),
                'content' => __('Once added be carefull editing next time. This key Should be same in app header.'),
            ),
            'desc' => __('Enter you paypal client id here', 'nokri-rest-api'),
        ),
        array(
            'id' => 'appKey_paypal_secret',
            'type' => 'text',
            'title' => __('Enter Your Paypal Secret', 'nokri-rest-api'),
            'default' => '',
            'text_hint' => array(
                'title' => __('Alert', 'nokri-rest-api'),
                'content' => __('Once added be carefull editing next time. This key Should be same in app header.'),
            ),
            'desc' => __('Enter you paypal Secret id here', 'nokri-rest-api'),
        ),
        array(
            'id' => 'paypalKey_merchant_name',
            'type' => 'text',
            'title' => __('Merchant Name', 'nokri-rest-api'),
            'default' => '',
            'desc' => __('Enter the merchant name', 'nokri-rest-api'),
        ),
        array(
            'id' => 'paypalKey_currency',
            'type' => 'text',
            'title' => __('Account Currency', 'nokri-rest-api'),
            'default' => '',
            'desc' => __('Currency name i.e. USD Supported currency list here: ', 'nokri-rest-api') . ' https://developer.paypal.com/docs/integration/direct/rest/currency-codes/',
        ),
        array(
            'id' => 'paypalKey_privecy_url',
            'type' => 'text',
            'title' => __('Privecy Url', 'nokri-rest-api'),
            'default' => '',
            'desc' => __('Example link ', 'nokri-rest-api') . 'https://www.example.com/privacy',
        ),
        array(
            'id' => 'paypalKey_agreement',
            'type' => 'text',
            'title' => __('Agreement Url', 'nokri-rest-api'),
            'default' => '',
            'desc' => __('Example link ', 'nokri-rest-api') . 'https://www.example.com/legal',
        ),
    )
));


Redux::setSection($opt_name, array(
    'title' => __('Thank You Settings', "nokri-rest-api"),
    'id' => 'api_payment_thankyou',
    'desc' => '',
    'icon' => 'el el-check',
    'subsection' => true,
    'fields' => array(
        array(
            'id' => 'payment_thankyou',
            'type' => 'text',
            'title' => __('Thank You Title', 'nokri-rest-api'),
            'default' => __('Thank You For Your Order', 'nokri-rest-api'),
        ),
    )
));

Redux::setSection($opt_name, array(
    'title' => __('Ads Settings', "nokri-rest-api"),
    'id' => 'api_ads_screen',
    'desc' => '',
    'icon' => 'el el-picture',
    'fields' => array(
    )
));

Redux::setSection($opt_name, array(
    'title' => __('Ads Settings', "nokri-rest-api"),
    'id' => 'api_ads_screen1',
    'desc' => '',
    'subsection' => true,
    'fields' => array(
        array(
            'id' => 'opt-info-warning0',
            'type' => 'info',
            'style' => 'warning',
            'title' => __('Ads Setting (AdMob)', 'nokri-rest-api'),
            'desc' => __('Here you can set the AdMob settings for the app', 'nokri-rest-api')
        ),
        array(
            'id' => 'api_ad_show',
            'type' => 'switch',
            'title' => __('Show Ads', 'nokri-rest-api'),
            'desc' => __('Trun ads on or off.', 'nokri-rest-api'),
            'default' => false,
        ),
        array(
            'id' => 'api_ad_type_banner',
            'type' => 'switch',
            'title' => __('Show Banner Ads', 'nokri-rest-api'),
            'subtitle' => __('Turn on or off for banner ads', 'nokri-rest-api'),
            'default' => false,
            'required' => array('api_ad_show', '=', '1'),
        ),
        array(
            'id' => 'api_ad_position',
            'type' => 'button_set',
            'title' => __('Banner Ad Position', 'nokri-rest-api'),
            'required' => array('api_ad_type_banner', '=', true),
            'options' => array(
                'top' => __('Top', 'nokri-rest-api'),
                'bottom' => __('Bottom', 'nokri-rest-api'),
            ),
            'default' => 'top'
        ),
        array(
            'id' => 'api_ad_key_banner',
            'type' => 'text',
            'title' => __('Enter Your Ad Key (banner) Android', 'nokri-rest-api'),
            'default' => '',
            'required' => array('api_ad_type_banner', '=', true),
            'desc' => __('Please make sure you are putting correct ad id your selected above banner', 'nokri-rest-api'),
        ),
        array(
            'id' => 'api_ad_key_banner_ios',
            'type' => 'text',
            'title' => __('Enter Your Ad Key (banner)  IOS', 'nokri-rest-api'),
            'default' => '',
            'required' => array('api_ad_type_banner', '=', true),
            'desc' => __('Please make sure you are putting correct ad id your selected above banner', 'nokri-rest-api'),
        ),
        array(
            'id' => 'api_ad_type_initial',
            'type' => 'switch',
            'title' => __('Show Initial Ads', 'nokri-rest-api'),
            'subtitle' => __('Turn on or off for initial ads', 'nokri-rest-api'),
            'default' => false,
            'required' => array('api_ad_show', '=', '1'),
        ),
        array(
            'id' => 'api_ad_key',
            'type' => 'text',
            'title' => __('Enter Your Ad Key (initial) Android', 'nokri-rest-api'),
            'default' => '',
            'required' => array('api_ad_type_initial', '=', true),
            'desc' => __('Please make sure you are putting correct ad id your selected above interstital', 'nokri-rest-api'),
        ),
        array(
            'id' => 'api_ad_key_ios',
            'type' => 'text',
            'title' => __('Enter Your Ad Key (initial) IOS', 'nokri-rest-api'),
            'default' => '',
            'required' => array('api_ad_type_initial', '=', true),
            'desc' => __('Please make sure you are putting correct ad id your selected above interstital', 'nokri-rest-api'),
        ),
        array(
            'id' => 'api_ad_time_initial',
            'type' => 'text',
            'title' => __('Show 1st Ad After', 'nokri-rest-api'),
            'default' => '',
            'required' => array('api_ad_type_initial', '=', true),
            'desc' => __('Show 1st ad after specific time. In seconds 1 is for 1 second', 'nokri-rest-api'),
        ),
        array(
            'id' => 'api_ad_time',
            'type' => 'text',
            'title' => __('Show Ad After', 'nokri-rest-api'),
            'default' => '',
            'required' => array('api_ad_type_initial', '=', true),
            'desc' => __('Show ads next time after specific time. In seconds 1 is for 1 second', 'nokri-rest-api'),
        ),
    )
));


Redux::setSection($opt_name, array(
    'title' => __('Analytics Settings', "nokri-rest-api"),
    'id' => 'api_ads_screen2',
    'desc' => '',
    'subsection' => true,
    'fields' => array(
        array(
            'id' => 'opt-info-warning1',
            'type' => 'info',
            'style' => 'warning',
            'title' => __('App Analytics', 'nokri-rest-api'),
            'desc' => __('Below you can setup analytics for the app.', 'nokri-rest-api')
        ),
        array(
            'id' => 'api_analytics_show',
            'type' => 'switch',
            'title' => __('Make Analytics', 'nokri-rest-api'),
            'desc' => __('Trun ads on or off.', 'nokri-rest-api'),
            'default' => false,
        ),
        array(
            'id' => 'api_analytics_id',
            'type' => 'text',
            'title' => __('Analytics ID', 'nokri-rest-api'),
            'default' => '',
            'required' => array('api_analytics_show', '=', true),
            'desc' => __('Put analytics id here i.e.', 'nokri-rest-api') . ' UA-XXXXXXXXX-X',
        ),
    )
));


Redux::setSection($opt_name, array(
    'title' => __('Firebase Settings', "nokri-rest-api"),
    'id' => 'api_ads_screen3',
    'desc' => '',
    'subsection' => true,
    'fields' => array(
        array(
            'id' => 'opt-info-warning2',
            'type' => 'info',
            'style' => 'warning',
            'title' => __('Push Nofifications', 'nokri-rest-api'),
            'desc' => __('Below you can setup Push Nofifications for the app.', 'nokri-rest-api')
        ),
        array(
            'id' => 'api_firebase_id',
            'type' => 'text',
            'title' => __('Firebase API KEY', 'nokri-rest-api'),
            'default' => '',
            'desc' => __('Put firebase api key', 'nokri-rest-api'),
        ),
    )
));




Redux::setSection($opt_name, array(
    'title' => __('Home Screen', "nokri-rest-api"),
    'id' => 'api_home_screen',
    'desc' => '',
    'icon' => 'el el-home',
    'fields' => array(
        array(
            //'required' => array( 'job_apply_with', '=', array( '1' ) ),
            'id' => 'api_home_screen_style',
            'type' => 'button_set',
            'title' => __('Select your desired home style', 'nokri-rest-api'),
            'multi' => false,
            //Must provide key => value pairs for options
            'options' => array(
                '1' => __('Home 1', 'nokri-rest-api'),
                '2' => __('Home 2', 'nokri-rest-api'),
            ),
            'default' => '1',
        ),
        array(
            'id' => 'hom_sec_bg',
            'type' => 'media',
            'url' => true,
            'title' => __('Background image', 'nokri-rest-api'),
            'compiler' => 'true',
            'desc' => __('Image for Home Screen', 'nokri-rest-api'),
            'subtitle' => __('Dimensions: 800 x 800', 'nokri-rest-api'),
            'default' => array('url' => NOKRI_API_PLUGIN_URL . "images/home-secreen.jpg"),
        ),
        array(
            'id' => 'hom_sec_tagline',
            'type' => 'text',
            'title' => __('Tagline', 'nokri-rest-api'),
            'default' => __('Find Jobs', 'nokri-rest-api'),
        ),
        array(
            'id' => 'hom_sec_headline',
            'type' => 'text',
            'title' => __('Headline', 'nokri-rest-api'),
            'default' => __('Search From 1500+ Jobs', 'nokri-rest-api'),
        ),
        array(
            'required' => array('api_home_screen_style', '=', array('2')),
            'id' => 'hom_sec_key_word_heading',
            'type' => 'text',
            'title' => __('Keyword heading', 'nokri-rest-api'),
            'default' => __('Search From 1500+ Jobs...', 'nokri-rest-api'),
        ),
        array(
            'id' => 'hom_sec_place_holder',
            'type' => 'text',
            'title' => __('Keyword place holder', 'nokri-rest-api'),
            'default' => __('Search Keywords...', 'nokri-rest-api'),
        ),
        array(
            'required' => array('api_home_screen_style', '=', array('2')),
            'id' => 'hom_sec_cats_plc',
            'type' => 'text',
            'title' => __('Category place holder', 'nokri-rest-api'),
            'default' => __('Select category', 'nokri-rest-api'),
        ),
        array(
            'required' => array('api_home_screen_style', '=', array('1')),
            'id' => 'hom_sec_cats_text',
            'type' => 'text',
            'title' => __('Home Screen Cats Section Text', 'nokri-rest-api'),
            'default' => __('Select Category.', 'nokri-rest-api'),
        ),
        array(
            'required' => array('api_home_screen_style', '=', array('1')),
            'id' => 'nokri-api-ad-cats-multi',
            'type' => 'select',
            'data' => 'terms',
            'args' => array(
                'taxonomies' => 'job_category',
                'hide_empty' => false,
            ),
            'multi' => true,
            'sortable' => true,
            'title' => __('Categories Multi Select Option', 'nokri-rest-api'),
            'desc' => __('Select categories you want to show on Home page', 'nokri-rest-api'),
        ),
        array(
            'required' => array('api_home_screen_style', '=', array('1')),
            'id' => 'premium_jobs_heading',
            'type' => 'text',
            'title' => __('Home Secreen Premium Jobs Heading', 'nokri-rest-api'),
            'default' => __('Featured Jobs', 'nokri-rest-api'),
        ),
        array(
            'required' => array('api_home_screen_style', '=', array('1')),
            'id' => 'premium_jobs_class',
            'type' => 'select',
            'data' => 'terms',
            'args' => array(
                'taxonomies' => 'job_class', 'hide_empty' => false,
            ),
            'multi' => false,
            'sortable' => false,
            'title' => __('Select Job Class ', 'nokri-rest-api'),
            'desc' => __('Select job class you want to show', 'nokri-rest-api'),
        ),
        array(
            'required' => array('api_home_screen_style', '=', array('1')),
            'id' => 'premium_jobs_limit',
            'type' => 'spinner',
            'title' => __('Premium Jobs', 'nokri-rest-api'),
            'desc' => __('Select number of premium jobs', 'nokri-rest-api'),
            'default' => '5',
            'min' => '1',
            'step' => '1',
            'max' => '50',
        ),
        array(
            'required' => array('api_home_screen_style', '=', array('1')),
            'id' => 'recent_jobs_switch',
            'type' => 'switch',
            'title' => __('Hide/Show Recent Jobs', 'nokri-rest-api'),
            'default' => true,
        ),
        array(
            'required' => array('recent_jobs_switch', '=', array('1')),
            'id' => 'recent_jobs_limits',
            'type' => 'spinner',
            'title' => __('Recent jobs', 'nokri-rest-api'),
            'desc' => __('Select number of recent jobs', 'nokri-rest-api'),
            'default' => '5',
            'min' => '1',
            'step' => '1',
            'max' => '50',
        ),
        array(
            'required' => array('api_home_screen_style', '=', array('1')),
            'id' => 'home_secreen_blog',
            'type' => 'switch',
            'title' => __('Hide/Show blog', 'nokri-rest-api'),
            'default' => true,
        ),
        /* Home 2 radius options */
        array(
            'required' => array('api_home_screen_style', '=', array('2')),
            'id' => 'api_home_screen_radius_txt',
            'type' => 'text',
            'title' => __('Enter radius text', 'nokri-rest-api'),
            'default' => '',
        ),
        array(
            'required' => array('api_home_screen_style', '=', array('2')),
            'id' => 'api_home_screen_radius_value',
            'type' => 'spinner',
            'title' => __('Number of miles', 'nokri-rest-api'),
            'desc' => __('Set maximum number of km', 'nokri-rest-api'),
            'default' => '50',
            'min' => '0',
            'step' => '1',
            'max' => '100',
        ),
        array(
            'required' => array('api_home_screen_style', '=', array('2')),
            'id' => 'hom_sec_btn_txt',
            'type' => 'text',
            'title' => __('Button text', 'nokri-rest-api'),
            'default' => __('Submit', 'nokri-rest-api'),
        ),
    )
));


Redux::setSection($opt_name, array(
    'title' => __('Users', "nokri-rest-api"),
    'id' => 'api_users_screen',
    'desc' => '',
    'icon' => 'el el-user',
    'fields' => array(
        array(
            'id' => 'deactivate_app_acount',
            'type' => 'switch',
            'title' => __('Delete account button', 'nokri-rest-api'),
            'default' => true,
        ),
        array(
            'id' => 'user_profile_setting_option_API',
            'type' => 'switch',
            'title' => __('Public/Private option', 'nokri-rest-api'),
            'subtitle' => __('Allow users to set profile public/private', 'nokri-rest-api'),
            'default' => false,
        ),
        array(
            'id' => 'api_user_contact',
            'type' => 'switch',
            'title' => __('Hide/Show phone and email', 'nokri-rest-api'),
            'default' => false,
        ),
        array(
            'id' => 'user_contact_social_API',
            'type' => 'switch',
            'title' => __('Hide/Show social links', 'nokri-rest-api'),
            'default' => true
        ),
        array(
            'id' => 'api_upload_portfolio_limit',
            'type' => 'select',
            'title' => esc_html__('Portfolio upload limit', 'nokri-rest-api'),
            'options' => array(1 => 1, 2 => 2, 3 => 3, 4 => 4, 5 => 5, 6 => 6, 7 => 7, 8 => 8, 9 => 9, 10 => 10, 11 => 11, 12 => 12, 13 => 13, 14 => 14, 15 => 15),
            'default' => 5,
        ),
        array(
            'id' => 'api_upload_portfolio_size',
            'type' => 'select',
            'title' => esc_html__('Portfolio max size', 'nokri-rest-api'),
            'options' => array('307200-300kb' => '300kb', '614400-600kb' => '600kb', '819200-800kb' => '800kb', '1048576-1MB' => '1MB', '2097152-2MB' => '2MB', '3145728-3MB' => '3MB', '4194304-4MB' => '4MB', '5242880-5MB' => '5MB'),
            'default' => '819200-800kb',
        ),
        array(
            'id' => 'api_upload_resume_limit',
            'type' => 'select',
            'title' => esc_html__('Resumes upload limit', 'nokri-rest-api'),
            'options' => array(1 => 1, 2 => 2, 3 => 3, 4 => 4, 5 => 5, 6 => 6, 7 => 7, 8 => 8, 9 => 9, 10 => 10, 11 => 11, 12 => 12, 13 => 13, 14 => 14, 15 => 15),
            'default' => 5,
        ),
        array(
            'id' => 'api_upload_resume_size',
            'type' => 'select',
            'title' => esc_html__('Resume max size', 'nokri-rest-api'),
            'options' => array('307200-300kb' => '300kb', '614400-600kb' => '600kb', '819200-800kb' => '800kb', '1048576-1MB' => '1MB', '2097152-2MB' => '2MB', '3145728-3MB' => '3MB', '4194304-4MB' => '4MB', '5242880-5MB' => '5MB'),
            'default' => '819200-800kb',
        ),
        array(
            'id' => 'api_user_pagination',
            'type' => 'select',
            'title' => esc_html__('Show Users Per Page', 'nokri-rest-api'),
            'options' => array(1 => 1, 2 => 2, 3 => 3, 4 => 4, 5 => 5, 6 => 6, 7 => 7, 8 => 8, 9 => 9, 10 => 10, 11 => 11, 12 => 12, 13 => 13, 14 => 14, 15 => 15),
            'default' => 5,
        ),
        array(
            'id' => 'api_user_profile_msg',
            'type' => 'textarea',
            'title' => __('Profile Status Message', 'nokri-rest-api'),
            'subtitle' => __('Enter message private profile', 'nokri-rest-api'),
            'default' => '',
        ),
    )
));


Redux::setSection($opt_name, array(
    'title' => __("Jobs General Settings", "nokri-rest-api"),
    'id' => 'api_ad_posts',
    'desc' => '',
    'icon' => 'el el-adjust-alt',
    'fields' => array(
        array(
            'id' => 'sb_send_email_on_ad_post',
            'type' => 'switch',
            'title' => __('Send email on Ad Post', 'nokri-rest-api'),
            'default' => true,
        ),
        array(
            'id' => 'ad_post_email_value',
            'type' => 'text',
            'title' => __('Email for notification.', 'nokri-rest-api'),
            'required' => array('sb_send_email_on_ad_post', '=', '1'),
            'default' => get_option('admin_email'),
        ),
        array(
            'id' => 'sb_ad_approval',
            'type' => 'select',
            'options' => array('auto' => 'Auto Approved', 'manual' => 'Admin manual approval'),
            'title' => __('Ad Approval', 'nokri-rest-api'),
            'default' => 'auto',
        ),
        array(
            'id' => 'sb_update_approval',
            'type' => 'select',
            'options' => array('auto' => 'Auto Approved', 'manual' => 'Admin manual approval'),
            'title' => __('Ad Update Approval', 'nokri-rest-api'),
            'default' => 'auto',
        ),
        array(
            'id' => 'email_on_ad_approval',
            'type' => 'switch',
            'title' => __('Email to Ad owner on approval', 'nokri-rest-api'),
            'default' => true,
        ),
    )
));

Redux::setSection($opt_name, array(
    'title' => __("Jobs Post Settings", "nokri-rest-api"),
    'id' => 'api_ad_post_settings',
    'desc' => '',
    'icon' => 'el el-home',
    'subsection' => true,
    'fields' => array(
        array(
            'id' => 'allow_lat_lon',
            'type' => 'switch',
            'title' => __('Latitude & Longitude', 'nokri-rest-api'),
            'desc' => __('This will be display on job post page for pin point map', 'nokri-rest-api'),
            'default' => true,
        ),
        array(
            'id' => 'sb_default_lat',
            'type' => 'text',
            'title' => __('Latitude', 'nokri-rest-api'),
            'subtitle' => __('for default map.', 'nokri-rest-api'),
            'required' => array('allow_lat_lon', '=', true),
            'default' => '40.7127837',
        ),
        array(
            'id' => 'sb_default_long',
            'type' => 'text',
            'title' => __('Longitude', 'nokri-rest-api'),
            'subtitle' => __('for default map.', 'nokri-rest-api'),
            'required' => array('allow_lat_lon', '=', true),
            'default' => '-74.00594130000002',
        ),
        array(
            'id' => 'bad_words_filter',
            'type' => 'textarea',
            'title' => __('Bad Words Filter', 'nokri-rest-api'),
            'subtitle' => __('comma separated', 'nokri-rest-api'),
            'placeholder' => __('word1,word2', 'nokri-rest-api'),
            'desc' => __('This words will be removed from AD Title and Description', 'nokri-rest-api'),
            'default' => '',
        ),
        array(
            'id' => 'bad_words_replace',
            'type' => 'text',
            'title' => __('Bad Words Replace Word', 'nokri-rest-api'),
            'desc' => __('This words will be replace with above bad words list from AD Title and Description', 'nokri-rest-api'),
            'default' => '',
        ),
        array(
            'id' => 'API_job_cat_level_1',
            'type' => 'text',
            'title' => esc_html__('Category Heading Level 1', 'nokri-rest-api'),
            'default' => 'Job category',
        ),
        array(
            'id' => 'API_job_cat_level_2',
            'type' => 'text',
            'title' => esc_html__('Category Heading Level 2', 'nokri-rest-api'),
            'default' => 'Sub category',
        ),
        array(
            'id' => 'API_job_cat_level_3',
            'type' => 'text',
            'title' => esc_html__('Category Heading Level 3', 'nokri-rest-api'),
            'default' => 'Sub sub category',
        ),
        array(
            'id' => 'API_job_cat_level_4',
            'type' => 'text',
            'title' => esc_html__('Category Heading Level 4', 'nokri-rest-api'),
            'default' => 'Sub sub sub category',
        ),
        array(
            'id' => 'API_job_country_level_heading',
            'type' => 'text',
            'title' => esc_html__('Location section heading', 'nokri-rest-api'),
            'default' => '',
        ),
        array(
            'id' => 'API_job_country_level_1',
            'type' => 'text',
            'title' => esc_html__('Location Heading Level 1', 'nokri-rest-api'),
            'default' => '',
        ),
        array(
            'id' => 'API_job_country_level_2',
            'type' => 'text',
            'title' => esc_html__('Location Heading Level 2', 'nokri-rest-api'),
            'default' => '',
        ),
        array(
            'id' => 'API_job_country_level_3',
            'type' => 'text',
            'title' => esc_html__('Location Heading Level 3', 'nokri-rest-api'),
            'default' => '',
        ),
        array(
            'id' => 'API_job_country_level_4',
            'type' => 'text',
            'title' => esc_html__('Location Heading Level 4', 'nokri-rest-api'),
            'default' => '',
        ),
        array(
            'id' => 'API_job_map_heading_txt',
            'type' => 'text',
            'title' => esc_html__('Map heading text', 'nokri-rest-api'),
            'default' => '',
        ),
    )
));


Redux::setSection($opt_name, array(
    'title' => __("Jobs Search Settings", "nokri-rest-api"),
    'id' => 'api_ad_search_settings',
    'desc' => '',
    'icon' => 'el el-home',
    'subsection' => true,
    'fields' => array(
        array(
            'id' => 'feature_on_search',
            'type' => 'switch',
            'title' => __('Featured Ads', 'nokri-rest-api'),
            'default' => true,
        ),
        array(
            'id' => 'sb_search_ads_title',
            'required' => array('feature_on_search', '=', true),
            'type' => 'text',
            'title' => __('Featured Ads Section Title', 'nokri-rest-api'),
            'default' => 'Featured Ads',
        ),
        array(
            'id' => 'search_related_posts_count',
            'required' => array('feature_on_search', '=', true),
            'type' => 'slider',
            'title' => __('Featured Posts', 'nokri-rest-api'),
            'subtitle' => __('On ad details page', 'nokri-rest-api'),
            'desc' => __('Select Number of featured posts', 'nokri-rest-api'),
            'default' => 5,
            'min' => 1,
            'step' => 1,
            'max' => 15,
            'display_value' => 'label'
        ),
    )
));

/* Only show if woocommerce plugin activated */
//if ( in_array( 'woocommerce/woocommerce.php', apply_filters( 'active_plugins', get_option( 'active_plugins' ) ) ) )
//{	
Redux::setSection($opt_name, array(
    'title' => __("Woo Products", "nokri-rest-api"),
    'id' => 'api_woo_products_settings',
    'desc' => '',
    'icon' => 'el el-list-alt',
    'fields' => array(
        array(
            'id' => 'api_woo_products_multi',
            'type' => 'select',
            'data' => 'post',
            'args' => array(
                'post_type' => array('product'),
                'posts_per_page' => '-1',
            ),
            'multi' => true,
            'sortable' => true,
            'title' => __('Select Products', 'nokri-rest-api'),
        ),
        array(
            'id' => 'api_woo_cand_products_multi',
            'type' => 'select',
            'data' => 'post',
            'args' => array(
                'post_type' => array('product'),
                'posts_per_page' => '-1',
            ),
            'multi' => true,
            'sortable' => true,
            'title' => __('Select Products for candidate', 'nokri-rest-api'),
        ),
        array(
            'id' => 'api-payment-packages',
            'type' => 'select',
            'multi' => true,
            'sortable' => true,
            'title' => __('Payment Methods', 'nokri-rest-api'),
            'desc' => __('Select the payment methods you want to add.', 'nokri-rest-api'),
            'options' => nokriAPI_payment_types(),
            'default' => array('stripe')
        ),
        array(
            'id' => 'api-payment-packages_ios',
            'type' => 'select',
            'multi' => true,
            'sortable' => true,
            'title' => __('Payment Methods IOS', 'nokri-rest-api'),
            'desc' => __('Select the payment methods you want to add.', 'nokri-rest-api'),
            'options' => nokriAPI_payment_types_ios(),
            'default' => array('app_inapp')
        ),
    )
));


Redux::setSection($opt_name, array(
    'title' => __('App Extra Settings', "nokri-rest-api"),
    'id' => 'api_key_extra_settings',
    'subsectio' => 'true',
    'desc' => '',
    'icon' => 'el el-cogs',
    'fields' => array(
    )
));
Redux::setSection($opt_name, array(
    'title' => __('Faqs Settings', "nokri-rest-api"),
    'id' => 'api_faqs',
    'desc' => '',
    'icon' => 'el el-check',
    'subsection' => true,
    'fields' => array(
        array(
            'id' => 'opt-slides',
            'type' => 'slides',
            'title' => __('Add faqs', 'nokri-rest-api'),
            'subtitle' => __('Unlimited faqs with drag and drop sortings.', 'nokri-rest-api'),
            'show' => array(
                'title' => true,
                'description' => true,
                'url' => false
            ),
            'placeholder' => array(
                'title' => __('This is a title', 'nokri-rest-api'),
                'description' => __('Description Here', 'nokri-rest-api'),
            ),
        ),
    )
));
Redux::setSection($opt_name, array(
    'title' => __('App about', "nokri-rest-api"),
    'id' => 'api_key_app_about',
    'desc' => '',
    'subsection' => true,
    'fields' => array(
        array(
            'id' => 'app_settings_about_switch',
            'type' => 'switch',
            'title' => __('Show About Section', "nokri-rest-api"),
            'desc' => __('	Show app about section in setting page', "nokri-rest-api"),
            'default' => false,
        ),
        array(
            'required' => array('app_settings_about_switch', '=', true),
            'id' => 'app_settings_about_title',
            'type' => 'text',
            'title' => __('App About Title', 'nokri-rest-api'),
            'desc' => __('	Enter app about title.', "nokri-rest-api"),
            'default' => __('App about', "nokri-rest-api"),
        ),
        array(
            'required' => array('app_settings_about_switch', '=', true),
            'id' => 'app_settings_about_txt',
            'type' => 'text',
            'title' => __('App About Description', 'nokri-rest-api'),
            'desc' => __('	Enter app about description.', "nokri-rest-api"),
            'default' => __('Nokri is one of the leading and the best Premium Jobboard WordPress theme with outstanding front-end UI', "nokri-rest-api"),
        ),
    )
));
Redux::setSection($opt_name, array(
    'title' => __('App Version', "nokri-rest-api"),
    'id' => 'api_key_app_version',
    'desc' => '',
    'subsection' => true,
    'fields' => array(
        array(
            'id' => 'app_settings_version_switch',
            'type' => 'switch',
            'title' => __('Show App Version', "nokri-rest-api"),
            'desc' => __('Show app version in setting page', "nokri-rest-api"),
            'default' => false,
        ),
        array(
            'required' => array('app_settings_version_switch', '=', true),
            'id' => 'app_settings_version_txt',
            'type' => 'text',
            'title' => __('App About Description', 'nokri-rest-api'),
            'desc' => __('	Enter app version title.', "nokri-rest-api"),
            'default' => __('App Version', "nokri-rest-api"),
        ),
    )
));
Redux::setSection($opt_name, array(
    'title' => __('App Rating ', "nokri-rest-api"),
    'id' => 'api_key_app_rating',
    'desc' => '',
    'subsection' => true,
    'fields' => array(
        array(
            'id' => 'app_settings_rating_switch',
            'type' => 'switch',
            'title' => __('App Rating', "nokri-rest-api"),
            'desc' => __('Show app rating icon on the top.', "nokri-rest-api"),
            'default' => false,
        ),
        array(
            'required' => array('app_settings_rating_switch', '=', true),
            'id' => 'app_settings_rating_txt',
            'type' => 'text',
            'title' => __('Rating Title', 'nokri-rest-api'),
            'desc' => __('	Rating title in the popup.', "nokri-rest-api"),
            'default' => __('App Store Rating', "nokri-rest-api"),
        ),
        array(
            'required' => array('app_settings_rating_switch', '=', true),
            'id' => 'app_settings_rating_url',
            'type' => 'text',
            'title' => __('App URL (For Android)', 'nokri-rest-api'),
            'desc' => __('Enter app URL for app rating. URL is required', "nokri-rest-api"),
            'default' => '',
        ),
        array(
            'required' => array('app_settings_rating_switch', '=', true),
            'id' => 'app_settings_rating_id',
            'type' => 'text',
            'title' => __('App ID For Ios App', 'nokri-rest-api'),
            'desc' => __('Enter app ID for app rating', "nokri-rest-api"),
            'default' => '',
        ),
    )
));
Redux::setSection($opt_name, array(
    'title' => __('App Share Settings ', "nokri-rest-api"),
    'id' => 'api_key_app_share',
    'desc' => '',
    'subsection' => true,
    'fields' => array(
        array(
            'id' => 'app_settings_share_switch',
            'type' => 'switch',
            'title' => __('App Share', "nokri-rest-api"),
            'desc' => __('Show app share icon on the top.', "nokri-rest-api"),
            'default' => false,
        ),
        array(
            'required' => array('app_settings_share_switch', '=', true),
            'id' => 'app_settings_share_txt',
            'type' => 'text',
            'title' => __('Share Popup Title', 'nokri-rest-api'),
            'desc' => __('Title in the popup.', "nokri-rest-api"),
            'default' => '',
        ),
        array(
            'required' => array('app_settings_share_switch', '=', true),
            'id' => 'app_settings_share_subject',
            'type' => 'text',
            'title' => __('Subject', 'nokri-rest-api'),
            'desc' => __('App share subject. Not required.', "nokri-rest-api"),
            'default' => '',
        ),
        array(
            'required' => array('app_settings_share_switch', '=', true),
            'id' => 'app_settings_share_url',
            'type' => 'text',
            'title' => __('App Share URL', 'nokri-rest-api'),
            'desc' => __('	Enter app share URL for app sharing. URL is required.', "nokri-rest-api"),
            'default' => '',
        ),
    )
));
Redux::setSection($opt_name, array(
    'title' => __('Privacy Policy Settings ', "nokri-rest-api"),
    'id' => 'api_key_app_privacy',
    'desc' => '',
    'subsection' => true,
    'fields' => array(
        array(
            'id' => 'app_settings_privacy_switch',
            'type' => 'switch',
            'title' => __('Show Privacy Policy Section', "nokri-rest-api"),
            'desc' => __('Show app Privacy Policy section in setting page', "nokri-rest-api"),
            'default' => false,
        ),
        array(
            'required' => array('app_settings_privacy_switch', '=', true),
            'id' => 'app_settings_privacy_title',
            'type' => 'text',
            'title' => __('Title', 'nokri-rest-api'),
            'desc' => __('Enter App Privacy Policy Title', "nokri-rest-api"),
            'default' => __('Privacy Policy', "nokri-rest-api"),
        ),
        array(
            'required' => array('app_settings_privacy_switch', '=', true),
            'id' => 'app_settings_privacy_url',
            'type' => 'text',
            'title' => __('App Privacy Policy URL', 'nokri-rest-api'),
            'desc' => __('Enter app Privacy Policy URL.', "nokri-rest-api"),
            'default' => '',
        ),
    )
));
Redux::setSection($opt_name, array(
    'title' => __('Terms & Contdition Settings ', "nokri-rest-api"),
    'id' => 'api_key_app_terms',
    'desc' => '',
    'subsection' => true,
    'fields' => array(
        array(
            'id' => 'app_settings_terms_switch',
            'type' => 'switch',
            'title' => __('Show term and condition Section', "nokri-rest-api"),
            'desc' => __('Show app Terms and Condition section in setting page', "nokri-rest-api"),
            'default' => false,
        ),
        array(
            'required' => array('app_settings_terms_switch', '=', true),
            'id' => 'app_settings_terms_title',
            'type' => 'text',
            'title' => __('Title', 'nokri-rest-api'),
            'desc' => __('Enter App Terms and Contdition Title.', "nokri-rest-api"),
            'default' => __('Terms and Condition', "nokri-rest-api"),
        ),
        array(
            'required' => array('app_settings_terms_switch', '=', true),
            'id' => 'app_settings_terms_url',
            'type' => 'text',
            'title' => __('Terms and Condition URL', 'nokri-rest-api'),
            'desc' => __('Enter app Terms and Condition URL.', "nokri-rest-api"),
            'default' => '',
        ),
    )
));
Redux::setSection($opt_name, array(
    'title' => __("Feedback Settings", "nokri-rest-api"),
    'id' => 'api_app_feedback_settings',
    'desc' => '',
    'subsection' => true,
    'fields' => array(
        array(
            'id' => 'app_feedback_show',
            'type' => 'switch',
            'title' => __("Feedback Section", "nokri-rest-api"),
            'desc' => __("Show app feedback section in setting page", "nokri-rest-api"),
            'default' => true,
        ),
        array(
            'id' => 'app_feedback_title',
            'type' => 'text',
            'title' => __('Title', 'nokri-rest-api'),
            'default' => __("Feedback", "nokri-rest-api"),
            'desc' => __("Enter App Feedback  Title.", 'nokri-rest-api'),
            'required' => array('app_feedback_show', '=', '1'),
        ),
        array(
            'id' => 'app_feedback_subline',
            'type' => 'text',
            'title' => __('Subline', 'nokri-rest-api'),
            'default' => __("Got any queries? We are here to help you!", "nokri-rest-api"),
            'desc' => __("Enter App Feedback  subline.", 'nokri-rest-api'),
            'required' => array('app_feedback_show', '=', '1'),
        ),
        array(
            'id' => 'app_feedback_admin_email',
            'type' => 'text',
            'title' => __("Feedback Admin email", 'nokri-rest-api'),
            'default' => get_option('admin_email'),
            'desc' => __("Enter app feedback email for admin where he received emails.", 'nokri-rest-api'),
            'required' => array('app_feedback_show', '=', '1'),
        ),
        array(
            'id' => 'api_key_settings-info1',
            'type' => 'info',
            'notice' => false,
            'style' => 'info',
            'title' => __('Info', 'nokri-rest-api'),
            'desc' => __('Email tempate settings.', 'nokri-rest-api')
        ),
        /* Email Template */
        array(
            'id' => 'sb_app_feedback_subject',
            'type' => 'text',
            'title' => __('Feedback email subject', 'nokri-rest-api'),
            'desc' => __('%feedback_from% , %site_name% will be translated accordingly.', 'nokri-rest-api'),
            'default' => 'You Have a new feedback On %feedback_from%',
        ),
        array(
            'id' => 'sb_app_feedback_from',
            'type' => 'text',
            'title' => __('Ad Feedback email FROM', 'nokri-rest-api'),
            'desc' => __('NAME valid@email.com is compulsory as we gave in default.', 'nokri-rest-api'),
            'default' => 'From: ' . get_bloginfo('name') . ' <' . get_option('admin_email') . '>',
        ),
        array(
            'id' => 'sb_app_feedback_message',
            'type' => 'editor',
            'title' => __('Feedback Email template', 'nokri-rest-api'),
            'desc' => __('%feedback_subject% , %feedback_email% , %feedback_message% , %feedback_from% will be translated accordingly.', 'nokri-rest-api'),
            'default' => '<table class="body" style="border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f6f6f6; width: 100%;" border="0" cellspacing="0" cellpadding="0"><tbody><tr><td style="font-family: sans-serif; font-size: 14px; vertical-align: top;"></td><td class="container" style="font-family: sans-serif; font-size: 14px; vertical-align: top; display: block; max-width: 580px; padding: 10px; width: 580px; margin: 0 auto !important;"><div class="content" style="box-sizing: border-box; display: block; margin: 0 auto; max-width: 580px; padding: 10px;"><table class="main" style="border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; background: #fff; border-radius: 3px; width: 100%;"><tbody><tr><td class="wrapper" style="font-family: sans-serif; font-size: 14px; vertical-align: top; box-sizing: border-box; padding: 20px;"><table style="border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; width: 100%;" border="0" cellspacing="0" cellpadding="0"><tbody><tr style="font-family: \'Helvetica Neue\',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;"><td class="alert" style="font-family: \'Helvetica Neue\',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 16px; vertical-align: top; color: #000; font-weight: 500; text-align: center; border-radius: 3px 3px 0 0; background-color: #fff; margin: 0; padding: 20px;" align="center" valign="top" bgcolor="#fff">A Designing and development company</td></tr><tr><td style="font-family: sans-serif; font-size: 14px; vertical-align: top;"><p style="font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; margin-bottom: 15px;"><span style="font-family: sans-serif; font-weight: normal;">Hello </span><span style="font-family: \'Helvetica Neue\', Helvetica, Arial, sans-serif;"><b>Admin,</b></span></p><p style="font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; margin-bottom: 15px;"><strong>You have received a new feedback on:</strong> %feedback_from%</p><p style="font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; margin-bottom: 15px;"><strong>Subject:</strong> %feedback_subject%</p><strong>Email: </strong>%feedback_email%<p style="font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; margin-bottom: 15px;"><strong>Message:</strong></p>%feedback_message%<p style="font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; margin-bottom: 15px;"><strong>Thanks!</strong></p><p style="font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; margin-bottom: 15px;">ScriptsBundle</p></td></tr></tbody></table></td></tr></tbody></table><div class="footer" style="clear: both; padding-top: 10px; text-align: center; width: 100%;"><table style="border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; width: 100%;" border="0" cellspacing="0" cellpadding="0"><tbody><tr><td class="content-block powered-by" style="font-family: sans-serif; font-size: 12px; vertical-align: top; color: #999999; text-align: center;"><a style="color: #999999; text-decoration: underline; font-size: 12px; text-align: center;" href="https://themeforest.net/user/scriptsbundle">Scripts Bundle</a>.</td></tr></tbody></table></div>&nbsp;</div></td><td style="font-family: sans-serif; font-size: 14px; vertical-align: top;"></td></tr></tbody></table>',
        ),
    )
));
$load_languages = array();
$load_languages = apply_filters('AdftiorestAPI_load_active_languages', $load_languages);
$desc = __('Please select your desire languages used in app', 'nokri-rest-api');
if (empty($load_languages)) {
    $desc = __('Please configure wpml and translate language to use this functionality', 'nokri-rest-api');
}
Redux::setSection($opt_name, array(
    'title' => __('WPML Settings', "nokri-rest-api"),
    'id' => 'api_wpml_settings',
    'desc' => '',
    'icon' => 'el el-cogs',
    'fields' => array(
        array(
            'id' => 'sb_api_wpml_anable',
            'type' => 'switch',
            'title' => __('Enable WPML', 'nokri-rest-api'),
            'default' => false,
        ),
        array(
            'id' => 'sb_load_languages',
            'type' => 'select',
            'multi' => true,
            'options' => $load_languages,
            'title' => __('Languages', 'nokri-rest-api'),
            'default' => 'en',
            'desc' => $desc,
        ),
        array(
            'id' => 'sb_duplicate_post_app',
            'type' => 'switch',
            'title' => __('Duplicate Posts ( While Ad Posting )', 'adforest'),
            'default' => false,
            'subtitle' => __('Enable this option to duplicate posts in all others languages after successfully publish.', 'adforest'),
            'desc' => __('<b>Note : </b> Disable means the posts publish only in the current language.', 'adforest'),
        ),
        array(
            'id' => 'sb_show_posts_app',
            'type' => 'switch',
            'title' => __('Display Posts', 'adforest'),
            'default' => false,
            'subtitle' => __('Enable this option to display all others languages posts in current language.', 'adforest'),
            'desc' => __('<b>Note : </b> Disable means to display only current language posts.', 'adforest'),
        ),
        array(
            'id' => 'app_wpml_logo',
            'type' => 'media',
            'url' => true,
            'title' => __('WPML Logo', 'nokri-rest-api'),
            'compiler' => 'true',
            'desc' => __('Site Logo image for the site.', 'nokri-rest-api'),
            'subtitle' => __('Dimensions: 230 x 40', 'nokri-rest-api'),
            'default' => array(
                'url' => NOKRI_API_PLUGIN_URL . "images/logo.png"
            ),
        ),
        array(
            'id' => 'wpml_header_title1',
            'type' => 'text',
            'title' => __('Wpml header Title 1', 'nokri-rest-api'),
            'default' => 'Pick your',
        ),
        array(
            'id' => 'wpml_header_title2',
            'type' => 'text',
            'title' => __('Wpml header Title 2', 'nokri-rest-api'),
            'default' => 'Language',
        ),
        array(
            'id' => 'wpml_menu_text',
            'type' => 'text',
            'title' => __('Wpml Menu Text', 'nokri-rest-api'),
            'default' => 'Languages',
        ),
        
        
         array(
            'id' => 'wpml_desc_text',
            'type' => 'text',
            'title' => __('Language Description text', 'nokri-rest-api'),
            'default' => 'Languages',
        ),
           
    )
));