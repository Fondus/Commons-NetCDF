package tw.fondus.commons.nc.util;

import com.google.common.base.Preconditions;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.Variable;

import java.util.Optional;

/**
 * Validate NetCDF structures, such like global attribute, dimension, variable.
 * 
 * @author Brad Chen
 *
 */
public class ValidateUtils {

	/**
	 * Validate is define mode or not.
	 * 
	 * @param writer netcdf writer
	 */
	public static void validateDefine( NetcdfFileWriter writer ) {
		Preconditions.checkState( writer.isDefineMode(), "The NetCDF file not in define mode." );
	}

	/**
	 * Validate has global attribute or not.
	 * 
	 * @param writer netcdf writer
	 * @param name global attribute name
	 * @param notHas check not has or not
	 */
	public static void validateGlobalAttribute( NetcdfFileWriter writer, String name, boolean notHas ) {
		Optional<Attribute> optional = Optional.ofNullable( writer.findGlobalAttribute( name ) );
		validateProcess( !optional.isPresent(), optional.isPresent(), "global attribute", name, notHas );
	}

	/**
	 * Validate has dimension or not.
	 * 
	 * @param writer netcdf writer
	 * @param name dimension name
	 * @param notHas check not has or not
	 */
	public static void validateDimension( NetcdfFileWriter writer, String name, boolean notHas ) {
		validateProcess( !writer.hasDimension( null, name ), writer.hasDimension( null, name ), "dimension", name, notHas );
	}

	/**
	 * Validate has variable or not.
	 * 
	 * @param writer netcdf writer
	 * @param name variable name
	 * @param notHas check not has or not
	 */
	public static void validateVariable( NetcdfFileWriter writer, String name, boolean notHas ) {
		Optional<Variable> optional = Optional.ofNullable( writer.findVariable( name ) );
		validateProcess( !optional.isPresent(), optional.isPresent(), "variable", name, notHas );
	}

	/**
	 * Validate process of has type or not.
	 *
	 * @param notHasCondition not has condition
	 * @param hasCondition has condition
	 * @param type check type
	 * @param name check name
	 * @param notHas check not has or not
	 * @since 1.0.0
	 */
	private static void validateProcess( boolean notHasCondition, boolean hasCondition, String type, String name, boolean notHas ) {
		if ( notHas ) {
			Preconditions.checkState( notHasCondition, "This NetCDF has the " + type + ": " + name + " already!" );
		} else {
			Preconditions.checkState( hasCondition, "This NetCDF hasn't the " + type + ": " + name + "." );
		}
	}
}
