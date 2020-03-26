package tw.fondus.commons.nc.util;

import com.google.common.base.Preconditions;
import tw.fondus.commons.nc.util.key.VariableAttribute;
import ucar.ma2.Array;
import ucar.ma2.ArrayChar;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.nc2.Attribute;
import ucar.nc2.Variable;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
	 * Create to 1D index by y and x index.
	 *
	 * @param y y index
	 * @param x x index
	 * @return 1D index with y, x
	 * @since 1.0.0
	 */
	public static int create1DIndex( int y, int x ){
		return y * x + x;
	}

	/**
	 * Create to 3D index by time, y and x index.
	 *
	 * @param time time index
	 * @param y y index
	 * @param x x index
	 * @return txy index
	 * @since 1.0.0
	 */
	public static int[] createTYXIndex( int time, int y, int x ){
		return createTYXIndexByOrder( time, y, x, 0, 1, 2 );
	}

	/**
	 * Create to 3D index by time, y and x index and order.
	 *
	 * @param time time index
	 * @param y y index
	 * @param x x index
	 * @param tOrder time order
	 * @param yOrder y order
	 * @param xOrder x order
	 * @return txy index
	 * @since 1.0.0
	 */
	public static int[] createTYXIndexByOrder( int time, int y, int x, int tOrder, int yOrder, int xOrder ){
		int[] index = new int[3];
		index[tOrder] = time;
		index[yOrder] = y;
		index[xOrder] = x;
		return index;
	}

	/**
	 * Get attribute with string type.
	 *
	 * @param name name
	 * @param value value
	 * @return attribute
	 * @since 1.0.0
	 */
	public static Attribute createAttribute( String name, String value ) {
		return new Attribute( name, value );
	}

	/**
	 * Create attribute with number type.
	 *
	 * @param name name
	 * @param value value
	 * @return attribute
	 * @since 1.0.0
	 */
	public static Attribute createAttribute( String name, Number value ) {
		return new Attribute( name, value );
	}

	/**
	 * Get the  data type of variable.
	 *
	 * @param variable variable
	 * @return data type
	 */
	public static DataType getVariableType( Variable variable ){
		Preconditions.checkNotNull( variable, buildNotNullMessage( "variable" ) );
		return variable.getDataType();
	}

	/**
	 * Read the attribute from variable, if not found then return orElse.
	 *
	 * @param variable variable
	 * @param key key of attribute
	 * @param orElse default value
	 * @return attribute, if not found then return orElse
	 */
	public static String readVariableAttribute( Variable variable, String key, String orElse ){
		Preconditions.checkNotNull( variable, buildNotNullMessage( "variable" ) );
		Preconditions.checkNotNull( key, buildNotNullMessage( "key" ) );
		return Optional.ofNullable( variable.findAttribute( key ) ).map( Attribute::getStringValue ).orElse( orElse );
	}
	
	/**
	 * Read the number attribute from variable, if not found then return orElse.
	 * 
	 * @param variable variable
	 * @param key key of attribute
	 * @param orElse default value
	 * @return number attribute, if not found then return orElse
	 */
	public static BigDecimal readVariableAttributeAsNumber( Variable variable, String key, BigDecimal orElse ) {
		Preconditions.checkNotNull( variable, buildNotNullMessage( "variable" ) );
		Preconditions.checkNotNull( key, buildNotNullMessage( "key" ) );
		return Optional.ofNullable( variable.findAttribute( key ) )
				.map( Attribute::getNumericValue )
				.map( NetCDFUtils::toBigDecimal )
				.orElse( orElse );
	}

	/**
	 * Read the string variable values to list.
	 *
	 * @param stringVariable string variable
	 * @return list of string variable
	 * @throws IOException has IO Exception
	 */
	public static List<String> readStringValues( Variable stringVariable ) throws IOException {
		Preconditions.checkNotNull( stringVariable, buildNotNullMessage( "stringVariable" ) );
		Preconditions.checkState( DataType.CHAR.equals( stringVariable.getDataType() ), "NetCDF tools: the value not a string type." );

		int[] shape = stringVariable.getShape();
		ArrayChar values = (ArrayChar) stringVariable.read();
		Index index = values.getIndex();

		return IntStream.range( 0, shape[0] )
				.mapToObj( i -> values.getString( index.set0( i ) ) )
				.collect( Collectors.toList() );
	}
	
	/**
	 * Read index value from NetCDF array with default scale, offset factor to original value, if is missing value, return missing.
	 * 
	 * @param values array values
	 * @param index array index
	 * @return value at array index, if is missing value, return missing
	 */
	public static BigDecimal readArrayValue( Array values, int index ) {
		return readArrayValue( values, index, new BigDecimal( "1" ), BigDecimal.ZERO );
	}
	
	/**
	 * Read index value from NetCDF array with scale, offset factor to original value, if is missing value, return missing.
	 * 
	 * @param values array values
	 * @param index array index
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @param missing missing value
	 * @return value at array index, if is missing value, return missing
	 */
	@Deprecated
	public static BigDecimal readArrayValue( Array values, int index, Number scale, Number offset, Number missing ) {
		return readArrayValue( values, index, toBigDecimal( scale ), toBigDecimal( offset ), toBigDecimal( missing ) );
	}

	/**
	 * Read index value from NetCDF array with scale, offset factor to original value, if is missing value, return default missing.
	 *
	 * @param values array values
	 * @param index array index
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @return value at array index, if is missing value, return missing
	 * @since 1.0.0
	 */
	public static BigDecimal readArrayValue( Array values, int index, BigDecimal scale, BigDecimal offset ) {
		return originalValue( toBigDecimal( values.getFloat( index ) ), scale, offset );
	}

	/**
	 * Read index value from NetCDF array with scale, offset factor to original value, if is missing value, return missing.
	 *
	 * @param values array values
	 * @param index array index
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @param missing missing value
	 * @return value at array index, if is missing value, return missing
	 * @since 1.0.0
	 */
	public static BigDecimal readArrayValue( Array values, int index, BigDecimal scale, BigDecimal offset, BigDecimal missing ) {
		Preconditions.checkNotNull( values, buildNotNullMessage( "values" ) );
		return originalValue( toBigDecimal( values.getFloat( index ) ), scale, offset, missing );
	}
	
	/**
	 * Read index value from NetCDF array with default scale, offset factor to original value, if is missing value, return missing.
	 * 
	 * @param values array values
	 * @param index array index
	 * @return value at array index, if is missing value, return missing
	 */
	public static BigDecimal readArrayValue( Array values, Index index ) {
		return readArrayValue( values, index, new BigDecimal( "1" ), BigDecimal.ZERO, VariableAttribute.MISSING );
	}
	
	/**
	 * Read index value from NetCDF array with scale, offset factor to original value, if is missing value, return missing.
	 * 
	 * @param values array values
	 * @param index array index
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @param missing missing value
	 * @return value at array index, if is missing value, return missing
	 */
	@Deprecated
	public static BigDecimal readArrayValue( Array values, Index index, Number scale, Number offset, Number missing ) {
		return readArrayValue( values, index, toBigDecimal( scale ), toBigDecimal( offset ), toBigDecimal( missing ) );
	}

	/**
	 * Read index value from NetCDF array with scale, offset factor to original value, if is missing value, return default missing.
	 *
	 * @param values array values
	 * @param index array index
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @return value at array index, if is missing value, return missing
	 * @since 1.0.0
	 */
	public static BigDecimal readArrayValue( Array values, Index index, BigDecimal scale, BigDecimal offset ) {
		return readArrayValue( values, index, scale, offset, VariableAttribute.MISSING );
	}

	/**
	 * Read index value from NetCDF array with scale, offset factor to original value, if is missing value, return missing.
	 *
	 * @param values array values
	 * @param index array index
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @param missing missing value
	 * @return value at array index, if is missing value, return missing
	 * @since 1.0.0
	 */
	public static BigDecimal readArrayValue( Array values, Index index, BigDecimal scale, BigDecimal offset, BigDecimal missing ) {
		Preconditions.checkNotNull( values, buildNotNullMessage( "values" ) );
		Preconditions.checkNotNull( index, buildNotNullMessage( "index" ) );
		return originalValue( toBigDecimal( values.getFloat( index ) ), scale, offset, missing );
	}

	/**
	 * Read the one dimension array values to list with default scale, offset factor to original value and missing value.
	 *
	 * @param values array values
	 * @return list of values
	 * @since 1.0.0
	 */
	public static List<BigDecimal> readOneDimensionArrayValues( Array values ){
		return readOneDimensionArrayValues( values, new BigDecimal( "1" ), BigDecimal.ZERO, VariableAttribute.MISSING );
	}

	/**
	 * Read the one dimension array values to list with scale, offset factor to original value and missing value.
	 *
	 * @param values array values
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @param missing missing value
	 * @return list of values
	 * @since 1.0.0
	 */
	public static List<BigDecimal> readOneDimensionArrayValues( Array values, BigDecimal scale, BigDecimal offset, BigDecimal missing ){
		Preconditions.checkNotNull( values, buildNotNullMessage( "values" ) );
		Preconditions.checkNotNull( scale, buildNotNullMessage( "scale" ) );
		Preconditions.checkNotNull( offset, buildNotNullMessage( "offset" ) );
		Preconditions.checkNotNull( missing, buildNotNullMessage( "missing" ) );
		int length = values.getShape()[0];
		return IntStream.range( 0, length )
				.mapToObj( i -> NetCDFUtils.readArrayValue( values, i, scale, offset, missing ) )
				.collect( Collectors.toList());
	}

	/**
	 * Read the Time, Y, X three-dimension array values to list with default scale, offset factor to original value and missing value, <br/>
	 * if not is three-dimension array, return empty list.
	 *
	 * @param values array values
	 * @return list with index order Time-YX values
	 * @since 1.0.0
	 */
	public static List<List<BigDecimal>> readTYXDimensionArrayValues( Array values ){
		return readTYXDimensionArrayValues( values, new BigDecimal( "1" ), BigDecimal.ZERO, VariableAttribute.MISSING );
	}

	/**
	 * Read the Time, Y, X three-dimension array values to list with scale, offset factor to original value and missing value, <br/>
	 * if not is three-dimension array, return empty list.
	 *
	 * @param values array values
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @param missing missing value
	 * @return list with index order time-yx one dimension values
	 * @since 1.0.0
	 */
	public static List<List<BigDecimal>> readTYXDimensionArrayValues( Array values, BigDecimal scale, BigDecimal offset, BigDecimal missing ){
		Preconditions.checkNotNull( values, buildNotNullMessage( "values" ) );
		Preconditions.checkNotNull( scale, buildNotNullMessage( "scale" ) );
		Preconditions.checkNotNull( offset, buildNotNullMessage( "offset" ) );
		Preconditions.checkNotNull( missing, buildNotNullMessage( "missing" ) );

		List<List<BigDecimal>> timeGrids = new ArrayList<>();
		int[] shape = values.getShape();
		if ( shape.length == 3 ){
			int timeSize = shape[ 0 ];
			int ySize = shape[ 1 ];
			int xSize = shape[ 2 ];
			Index index = values.getIndex();

			IntStream.range( 0, timeSize ).forEach( time -> {
				timeGrids.add( new ArrayList<>() );

				IntStream.range( 0, ySize ).forEach( y -> {
					IntStream.range( 0, xSize ).forEach( x -> {
						timeGrids.get( time ).add( NetCDFUtils.readArrayValue( values, index.set( time, y, x ), scale, offset, missing ) );
					} );
				} );
			} );
		}
		return timeGrids;
	}
	
	/**
	 * Unpack the package value with scale, offset factor to original value, if is missing value, return missing.
	 * 
	 * @param value package value
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @param missing missing value
	 * @return original value, if is missing value, return missing
	 */
	@Deprecated
	public static float applyFunction( float value, Number scale, Number offset, Number missing ) {
		return originalValue( toBigDecimal( value ), toBigDecimal( scale ), toBigDecimal( offset ), toBigDecimal( missing ) ).floatValue();
	}

	/**
	 * Unpack the package value with scale, offset factor to original value, if is missing value, return default missing.
	 *
	 * @param value package value
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @return original value, if is missing value, return missing
	 * @since 1.0.0
	 */
	public static BigDecimal originalValue( BigDecimal value, BigDecimal scale, BigDecimal offset ) {
		return originalValue( value, scale, offset, VariableAttribute.MISSING );
	}

	/**
	 * Unpack the package value with scale, offset factor to original value, if is missing value, return missing.
	 *
	 * @param value package value
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @param missing missing value
	 * @return original value, if is missing value, return missing
	 * @since 1.0.0
	 */
	public static BigDecimal originalValue( BigDecimal value, BigDecimal scale, BigDecimal offset, BigDecimal missing ) {
		Preconditions.checkNotNull( value, buildNotNullMessage( "value" ) );
		Preconditions.checkNotNull( scale, buildNotNullMessage( "scale" ) );
		Preconditions.checkNotNull( offset, buildNotNullMessage( "offset" ) );
		Preconditions.checkNotNull( missing, buildNotNullMessage( "missing" ) );
		return value.compareTo( missing ) == 0 ? missing : value.multiply( scale ).add( offset );
	}
	
	/**
	 * Convert Number to BigDecimal.
	 * 
	 * @param number number
	 * @return big decimal
	 */
	private static BigDecimal toBigDecimal( Number number ) {
		return new BigDecimal( String.valueOf( number ) );
	}

	/**
	 * Build the not null message for Preconditions.
	 *
	 * @param target variable name
	 * @return error message
	 * @since 1.0.0
	 */
	private static String buildNotNullMessage( String target ){
		return new StringBuilder().append( "NetCDFUtils: " ).append( target ).append(" should not be null."  ).toString();
	}
}
