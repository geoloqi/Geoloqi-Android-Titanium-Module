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
	String SRV_ACTION_NA_CODE = "SR001";
	String SRV_ACTION_NA_DESC = "Action provided is not defined";

	String SRV_UND_EXTRA_FLDS_CODE = "SR002";
	String SRV_UND_EXTRA_FLDS_DESC = "Undefined Extra Field provided, field: ";

	String SRV_INV_EXTRA_FLDS_CODE = "SR003";
	String SRV_INV_EXTRA_FLDS_DESC = "Invalid Extra Fields provided";

	String SRV_ACTIVITY_NULL_CODE = "SR004";
	String SRV_ACTIVITY_NULL_DESC = "Activity is null";

	String SRV_SERVICE_NA_CODE = "SR005";
	String SRV_SERVICE_NA_DESC = "Service not available";

	// LQLocationProxy validations

	String LOC_LOCATION_NP_CODE = "LC001";
	String LOC_LOCATION_NP_DESC = "Location not provided";

	String LOC_LQLOCATION_NA_CODE = "LC002";
	String LOC_LQLOCATION_NA_DESC = "LQLocation not available";

	String LOC_INV_BATT_PARAM_CODE = "LC003";
	String LOC_INV_BATT_PARAM_DESC = "Invalid value provided for battery parameter";

	String LOC_UN_CONV_JSON_CODE = "LC004";
	String LOC_UN_CONV_JSON_DESC = "Unable to convert to JSON";

	// LQSessionProxy validation

	String SES_SESSION_NA_CODE = "SE001";
	String SES_SESSION_NA_DESC = "Session not available";

	String SES_INVALID_CALLBK_CODE = "SE002";
	String SES_INVALID_CALLBK_DESC = "Invalid callback object provided";
}
