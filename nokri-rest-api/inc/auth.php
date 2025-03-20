<?php /*Some Thing */
function nokriAPI_basic_auth()
{
	$pc = $cs = false;
	//define('SHORTINIT',1);
	global $nokriAPI;	
	$response = '';
	if( NOKRI_API_REQUEST_FROM == 'ios' )
	{	
		$pcode = ( isset( $nokriAPI['appKey_pCode_ios'] ) && $nokriAPI['appKey_pCode_ios'] != ""  ) ? $nokriAPI['appKey_pCode_ios'] : '';
		$ccode = ( isset( $nokriAPI['appKey_Scode_ios'] ) && $nokriAPI['appKey_Scode_ios'] != ""  ) ? $nokriAPI['appKey_Scode_ios'] : '';
	}
	else
	{
		$pcode = ( isset( $nokriAPI['appKey_pCode'] ) && $nokriAPI['appKey_pCode'] != ""  ) ? $nokriAPI['appKey_pCode'] : '';
		$ccode = ( isset( $nokriAPI['appKey_Scode'] ) && $nokriAPI['appKey_Scode'] != ""  ) ? $nokriAPI['appKey_Scode'] : '';
	}


	if( $pcode == "" || $ccode == "") {return false;}
	foreach (nokriAPI_getallheaders() as $name => $value) {
		
		
		if( $name == "Authorization" || $name == "authorization" )
		{
			$adminInfo =  base64_decode( trim( str_replace("Basic", "", $value) ) ); 	
		}
	    
		
		if(  ($name == "Purchase-Code" || $name == "purchase-code" )	&& $pcode == $value ){ $pc = true;}
		if( ( $name == "Custom-Security" || $name == "custom-security" ) 	&& $ccode == $value ) {$cs =  true;}			
	}
            
			return ( $pc == true && $cs == true ) ? true : false;

}


if (!function_exists('getallheaders'))
{
    function getallheaders()
    {
       $headers = array();
       foreach ($_SERVER as $name => $value)
       {
           if (substr($name, 0, 5) == 'HTTP_')
           {
               $headers[str_replace(' ', '-', ucwords(strtolower(str_replace('_', ' ', substr($name, 5)))))] = $value;
           }
       }
       
       return $headers;
    }
}