package tw.fondus.commons.nc.util;

import com.google.common.base.Preconditions;

import ucar.ma2.DataType;
import ucar.nc2.Variable;

/**
 * The commons tools of NetCDF.
 * 
 * @author Brad Chen
 *
 */
public class CommonsUtils {
	
	private CommonsUtils() {}
	
	/**
	 * Get the variable type.
	 * 
	 * @param variable
	 * @return
	 */
	public static DataType getVariableType( Variable variable ){
		Preconditions.checkNotNull( variable, "CommonsUtils: variable" );
		return variable.getDataType();
	}
}
