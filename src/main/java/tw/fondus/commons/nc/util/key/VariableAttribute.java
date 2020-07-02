package tw.fondus.commons.nc.util.key;

import java.math.BigDecimal;

/**
 * The standard variable attribute name for usually use.
 * 
 * @author Brad Chen
 *
 */
public class VariableAttribute {
	// Key
	public static final String KEY_NAME = "standard_name";
	public static final String KEY_NAME_LONG = "long_name";
	public static final String KEY_UNITS = "units";
	public static final String KEY_AXIS = "axis";
	public static final String KEY_MISSING = "_FillValue";
	public static final String KEY_COORDINATES = "coordinates";
	public static final String KEY_SCALE = "scale_factor";
	public static final String KEY_OFFSET = "add_offset";
	
	// Value
	public static final BigDecimal MISSING = new BigDecimal( "-999" );
	public static final BigDecimal MISSING_GRID = new BigDecimal( "-9999" );

	@Deprecated
	public static final float MISSINGVALUE = -9999;

	public static final double MISSING_COORDINATES = 9.96921E36;
	public static final String COORDINATES_Y = "projection_y_coordinate";
	public static final String COORDINATES_X = "projection_x_coordinate";
	public static final String COORDINATES_Y_WGS84 = "latitude";
	public static final String COORDINATES_X_WGS84 = "longitude";
	public static final String NAME_Y_WGS84 = "y coordinate according to WGS 1984";
	public static final String NAME_X_WGS84 = "x coordinate according to WGS 1984";
	public static final String NAME_Y_TWD97 = "y coordinate according to TWD 1997";
	public static final String NAME_X_TWD97 = "x coordinate according to TWD 1997";
	public static final String UNITS_TIME_MINUTES = "minutes since 1970-01-01 00:00:00.0 +0000";
	public static final String UNITS_TIME_HOURS = "hours since 1970-01-01 00:00:00.0 +0000";
	public static final String UNITS_Y_WGS84 = "degrees_north";
	public static final String UNITS_X_WGS84 = "degrees_east";
	public static final String UNITS_TWD97 = "m";
	public static final String AXIS_X = "X";
	public static final String AXIS_Y = "Y";
	public static final String AXIS_Z = "Z";
	public static final String AXIS_TIME = "T";

	private VariableAttribute(){}
}
