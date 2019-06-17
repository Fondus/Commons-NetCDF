package tw.fondus.commons.nc.util;

import java.util.Optional;

import com.google.common.base.Preconditions;

import strman.Strman;
import ucar.nc2.NetcdfFileWriter;

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
	 * @param writer
	 */
	public static void validateDefine( NetcdfFileWriter writer ) {
		Preconditions.checkState( writer.isDefineMode(), "The NetCDF file not in define mode." );
	}

	/**
	 * Validate has global attribute or not.
	 * 
	 * @param writer
	 * @param name
	 * @param hasAlready
	 */
	public static void validateGlobalAttribute( NetcdfFileWriter writer, String name, boolean hasAlready ) {
		validateProcess( writer.findGlobalAttribute( name ), "global attribute", name, hasAlready );
	}

	/**
	 * Validate has dimension or not.
	 * 
	 * @param writer
	 * @param name
	 * @param hasAlready
	 */
	public static void validateDimension( NetcdfFileWriter writer, String name, boolean hasAlready ) {
		if ( hasAlready ) {
			Preconditions.checkState( !writer.hasDimension( null, name ),
					Strman.append( "This NetCDF has the dimension: ", name, " already!" ) );
		} else {
			Preconditions.checkState( writer.hasDimension( null, name ),
					Strman.append( "This NetCDF hasn't the dimension: ", name, "." ) );
		}
	}

	/**
	 * Validate has variable or not.
	 * 
	 * @param writer
	 * @param name
	 * @param hasAlready
	 */
	public static void validateVariable( NetcdfFileWriter writer, String name, boolean hasAlready ) {
		validateProcess( writer.findVariable( name ), "variable", name, hasAlready );
	}

	/**
	 * Validate process of has type or not.
	 * 
	 * @param instance
	 * @param type
	 * @param name
	 * @param hasAlready
	 */
	private static <T> void validateProcess( T instance, String type, String name, boolean hasAlready ) {
		Optional<T> opt = Optional.ofNullable( instance );
		if ( hasAlready ) {
			Preconditions.checkState( !opt.isPresent(),
					Strman.append( "This NetCDF has the ", type, ": ", name, " already!" ) );
		} else {
			Preconditions.checkState( opt.isPresent(),
					Strman.append( "This NetCDF hasn't the ", type, ": ", name, "." ) );
		}
	}
}
