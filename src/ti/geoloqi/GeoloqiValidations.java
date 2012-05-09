/**
 * 
 */
package ti.geoloqi;

/**
 * This is an all constant interface containing validation codes and
 * descriptions
 * <ul>
 * conventions used:
 * </ul>
 * <li>$Validation Mnemonic$_CODE = String value denoting error code. this is a
 * 5 character code with first one or two characters denoting class name</li>
 * </br> <li>$Validation Mnemonic$_DESC = String value denoting description</li>
 * </br>
 * <p>
 * Note: please use first one or two characters in code for class and
 * </p>
 */
public interface GeoloqiValidations {
	// LQServiceWrapper Validations
	String SRV_CALLBACK_NA_CODE = "SR001";
	String SRV_CALLBACK_NA_DESC = "Callback object missing or invalid in init parameter";

	String SRV_INIT_PARAMS_EMPTY_CODE = "SR002";
	String SRV_INIT_PARAMS_EMPTY_DESC = "Init parameters are either not provided or empty";

	String SRV_CLIENTID_NA_CODE = "SR003";
	String SRV_CLIENTID_NA_DESC = "clientId not provided";

	String SRV_CLIENTSECRET_NA_CODE = "SR004";
	String SRV_CLIENTSECRET_NA_DESC = "clientSecret not provided";

	String SRV_INIT_FAILED_CODE = "SR005";
	String SRV_INIT_FAILED_DESC = "Internal error occured while initializing, check log for details";

	String SRV_SERVICE_NA_CODE = "SR006";
	String SRV_SERVICE_NA_DESC = "Service not available";

	// LQSessionProxy validation

	String SES_SESSION_NA_CODE = "SE001";
	String SES_SESSION_NA_DESC = "Session not available";

	String SES_INVALID_CALLBK_CODE = "SE002";
	String SES_INVALID_CALLBK_DESC = "Invalid callback object provided";
}
