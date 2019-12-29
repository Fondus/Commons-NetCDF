package tw.fondus.commons.nc.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.base.Preconditions;

import ucar.ma2.Array;
import ucar.ma2.ArrayChar;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.nc2.Variable;

/**
 * The commons tools of NetCDF.
 * 
 * @author Brad Chen
 *
 */
public class NetCDFUtils {
	
	/**
	 * Avoid the constructor.
	 */
	private NetCDFUtils() {}

	/**
	 * Read the string variable values.
	 * 
	 * @param stringVariable
	 * @return
	 * @throws IOException
	 */
	public static List<String> readStringValues( Variable stringVariable ) throws IOException{
		Preconditions.checkNotNull( stringVariable, "NetCDFUtils: variable" );
		Preconditions.checkState( DataType.CHAR.equals( stringVariable.getDataType() ), "NetCDF tools: the value not a string type." );
		
		int[] shape = stringVariable.getShape();
		ArrayChar values = (ArrayChar) stringVariable.read();
		Index index = values.getIndex();
		
		return IntStream.range( 0, shape[0] )
				.mapToObj( i -> values.getString( index.set0( i ) ) )
				.collect( Collectors.toList() );
	}
	
	/**
	 * Get the variable data type.
	 * 
	 * @param variable
	 * @return
	 */
	public static DataType getVariableType( Variable variable ){
		Preconditions.checkNotNull( variable, "NetCDFUtils: variable" );
		return variable.getDataType();
	}

	/**
	 * Read the attribute from variable, if not found then return other.
	 *
	 * @param variable
	 * @param key
	 * @param other
	 * @return
	 */
	public static String readVariableAttribute( Variable variable, String key, String other ){
		Preconditions.checkNotNull( variable, "NetCDFUtils: variable" );
		Preconditions.checkNotNull( key, "NetCDFUtils: key" );
		return Optional.ofNullable( variable.findAttribute( key ) ).map( attribute -> attribute.getStringValue() ).orElse( other );
	}
	
	/**
	 * Read the number attribute from variable, if not found then return other.
	 * 
	 * @param variable
	 * @param key
	 * @param other
	 * @return
	 */
	public static Number readNumberFromVariableAttribute( Variable variable, String key, Number other ) {
		Preconditions.checkNotNull( variable, "NetCDFUtils: variable" );
		Preconditions.checkNotNull( key, "NetCDFUtils: key" );
		return Optional.ofNullable( variable.findAttribute( key ) ).map( attribute -> attribute.getNumericValue() ).orElse( other );
	}
	
	/**
	 * Read value from NetCDF Array with index with default scale, offset, missing.
	 * 
	 * @param values
	 * @param index
	 * @return
	 */
	public static BigDecimal readArrayValue( Array values, int index ) {
		return readArrayValue( values, index, 1, 0, -999f );
	}
	
	/**
	 * Read value from NetCDF Array with index.
	 * 
	 * @param values
	 * @param index
	 * @param scale
	 * @param offset
	 * @param missing
	 * @return
	 */
	public static BigDecimal readArrayValue( Array values, int index, Number scale, Number offset, Number missing ) {
		Preconditions.checkNotNull( values, "NetCDFUtils: values" );
		return toBigDecimal( applyFunction( values.getFloat( index ), scale, offset, missing ) );
	}
	
	/**
	 * Read value from NetCDF Array with index object with default scale, offset, missing.
	 * 
	 * @param values
	 * @param index
	 * @return
	 */
	public static BigDecimal readArrayValue( Array values, Index index ) {
		return readArrayValue( values, index, 1, 0, -999f );
	}
	
	/**
	 * Read value from NetCDF Array with index object.
	 * 
	 * @param values
	 * @param index
	 * @param scale
	 * @param offset
	 * @param missing
	 * @return
	 */
	public static BigDecimal readArrayValue( Array values, Index index, Number scale, Number offset, Number missing ) {
		Preconditions.checkNotNull( values, "NetCDFUtils: values" );
		Preconditions.checkNotNull( index, "NetCDFUtils: index" );
		return toBigDecimal( applyFunction( values.getFloat( index ), scale, offset, missing ) );
	}
	
	/**
	 * Return value with apply function with scale and offset, if is missing value, return with default missing value.
	 * 
	 * @param value
	 * @param scale
	 * @param offset
	 * @param missing
	 * @return
	 */
	public static float applyFunction( float value, Number scale, Number offset, Number missing ) {
		return value == missing.floatValue() ? -999f : (value * scale.floatValue() + offset.floatValue() );
	}
	
	/**
	 * Convert Number to BigDecimal.
	 * 
	 * @param number
	 * @return
	 */
	private static BigDecimal toBigDecimal( Number number ) {
		return new BigDecimal( String.valueOf( number ) );
	}
}
