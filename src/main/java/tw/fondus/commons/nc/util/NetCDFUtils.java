package tw.fondus.commons.nc.util;

import com.google.common.base.Preconditions;
import tw.fondus.commons.nc.util.key.VariableAttribute;
import tw.fondus.commons.nc.vo.index.IndexTYX;
import tw.fondus.commons.nc.vo.index.IndexYX;
import ucar.ma2.Array;
import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayShort;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.nc2.Attribute;
import ucar.nc2.Variable;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
	 * @param xSize size of x
	 * @return 1D index with y, x
	 * @since 1.0.0
	 */
	public static int create1DIndex( int y, int x, int xSize ){
		return y * xSize + x;
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
	 * Create the 1D short array with data values.
	 *
	 * @param values values
	 * @return 1D short array
	 * @since 1.1.0
	 */
	public static ArrayShort.D1 create1DArrayShort( List<BigDecimal> values ){
		Preconditions.checkNotNull( values, buildNotNullMessage( "values" ) );
		ArrayShort.D1 array = empty1DArrayShort( values.size() );
		parallelRange( values.size(), false ).forEachOrdered( i -> array.set( i, values.get( i ).shortValue() ) );
		return array;
	}

	/**
	 * Create the 1D integer array with data values.
	 *
	 * @param values values
	 * @return 1D integer array
	 * @since 1.1.2
	 */
	public static ArrayInt.D1 create1DArrayInteger( List<BigDecimal> values ){
		Preconditions.checkNotNull( values, buildNotNullMessage( "values" ) );
		ArrayInt.D1 array = empty1DArrayInteger( values.size() );
		parallelRange( values.size(), false ).forEachOrdered( i -> array.set( i, values.get( i ).intValue() ) );
		return array;
	}

	/**
	 * Create the 1D float array with data values.
	 *
	 * @param values values
	 * @return 1D float array
	 * @since 1.1.0
	 */
	public static ArrayFloat.D1 create1DArrayFloat( List<BigDecimal> values ){
		Preconditions.checkNotNull( values, buildNotNullMessage( "values" ) );
		ArrayFloat.D1 array = empty1DArrayFloat( values.size() );
		parallelRange( values.size(), false ).forEachOrdered( i -> array.set( i, values.get( i ).floatValue() ) );
		return array;
	}

	/**
	 * Create the 1D double array with data values.
	 *
	 * @param values values
	 * @return 1D double array
	 * @since 1.1.0
	 */
	public static ArrayDouble.D1 create1DArrayDouble( List<BigDecimal> values ){
		Preconditions.checkNotNull( values, buildNotNullMessage( "values" ) );
		ArrayDouble.D1 array = empty1DArrayDouble( values.size() );
		parallelRange( values.size(), false ).forEachOrdered( i -> array.set( i, values.get( i ).doubleValue() ) );
		return array;
	}

	/**
	 * Create the 2D short array with y-x order one dimension data values.
	 *
	 * @param yxValues yx two dimension values
	 * @param ySize y dimension size
	 * @param xSize x dimension size
	 * @return 2D short array
	 * @since 1.1.2
	 */
	public static ArrayShort.D2 create2DArrayShort( List<BigDecimal> yxValues, int ySize, int xSize ){
		Preconditions.checkNotNull( yxValues, buildNotNullMessage( "yxValues" ) );
		ArrayShort.D2 array = empty2DArrayShort( ySize, xSize );
		parallelRange2D( ySize, xSize, false )
				.forEachOrdered( index -> {
					int x = index.getCol();
					int y = index.getRow();
					array.set( y, x, yxValues.get( create1DIndex( y, x, xSize ) ).shortValue() );
				});
		return array;
	}

	/**
	 * Create the 2D integer array with y-x order one dimension data values.
	 *
	 * @param yxValues yx two dimension values
	 * @param ySize y dimension size
	 * @param xSize x dimension size
	 * @return 2D integer array
	 * @since 1.1.2
	 */
	public static ArrayInt.D2 create2DArrayInteger( List<BigDecimal> yxValues, int ySize, int xSize ){
		Preconditions.checkNotNull( yxValues, buildNotNullMessage( "yxValues" ) );
		ArrayInt.D2 array = empty2DArrayInteger( ySize, xSize );
		parallelRange2D( ySize, xSize, false )
				.forEachOrdered( index -> {
					int x = index.getCol();
					int y = index.getRow();
					array.set( y, x, yxValues.get( create1DIndex( y, x, xSize ) ).intValue() );
				});
		return array;
	}

	/**
	 * Create the 2D float array with y-x order one dimension data values.
	 *
	 * @param yxValues yx two dimension values
	 * @param ySize y dimension size
	 * @param xSize x dimension size
	 * @return 2D float array
	 * @since 1.1.2
	 */
	public static ArrayFloat.D2 create2DArrayFloat( List<BigDecimal> yxValues, int ySize, int xSize ){
		Preconditions.checkNotNull( yxValues, buildNotNullMessage( "yxValues" ) );
		ArrayFloat.D2 array = empty2DArrayFloat( ySize, xSize );
		parallelRange2D( ySize, xSize, false )
				.forEachOrdered( index -> {
					int x = index.getCol();
					int y = index.getRow();
					array.set( y, x, yxValues.get( create1DIndex( y, x, xSize ) ).floatValue() );
				});
		return array;
	}

	/**
	 * Create the 2D double array with y-x order one dimension data values.
	 *
	 * @param yxValues yx two dimension values
	 * @param ySize y dimension size
	 * @param xSize x dimension size
	 * @return 2D double array
	 * @since 1.1.2
	 */
	public static ArrayDouble.D2 create2DArrayDouble( List<BigDecimal> yxValues, int ySize, int xSize ){
		Preconditions.checkNotNull( yxValues, buildNotNullMessage( "yxValues" ) );
		ArrayDouble.D2 array = empty2DArrayDouble( ySize, xSize );
		parallelRange2D( ySize, xSize, false )
				.forEachOrdered( index -> {
					int x = index.getCol();
					int y = index.getRow();
					array.set( y, x, yxValues.get( create1DIndex( y, x, xSize ) ).doubleValue() );
				});
		return array;
	}

	/**
	 * Create the 2D char array with order-string length, the order size is depend on collection.
	 *
	 * @param strings collection of string
	 * @param stringSize size of string dimension
	 * @return 2D char array
	 * @since 1.2.1.1
	 */
	public static ArrayChar.D2 create2DArrayChar( List<String> strings, int stringSize ){
		Preconditions.checkNotNull( strings );
		Preconditions.checkState( strings.size() > 0 );
		ArrayChar.D2 array = empty2DArrayChar( strings.size(), stringSize );
		IntStream.range( 0, strings.size() ).forEach( i -> array.setString( i, strings.get( i ) ) );
		return array;
	}

	/**
	 * Create the 3D short array with t-y-x order dimension data values.
	 *
	 * @param tyxValues t-yx three dimension values, first list size equals to t dimension size
	 * @param ySize y dimension size
	 * @param xSize x dimension size
	 * @return 3D short array
	 * @since 1.1.0
	 */
	public static ArrayShort.D3 create3DArrayShort( List<List<BigDecimal>> tyxValues, int ySize, int xSize ){
		Preconditions.checkNotNull( tyxValues, buildNotNullMessage( "tyxValues" ) );
		ArrayShort.D3 array = empty3DArrayShort( tyxValues.size(), ySize, xSize );
		parallelRange3D( tyxValues.size(), ySize, xSize )
			.forEachOrdered( index -> {
				int t = index.getTime();
				int x = index.getCol();
				int y = index.getRow();
				array.set( t, y, x, tyxValues.get( t ).get( create1DIndex( y, x, xSize ) ).shortValue() );
			} );
		return array;
	}

	/**
	 * Create the 3D integer array with t-y-x order dimension data values.
	 *
	 * @param tyxValues t-yx three dimension values, first list size equals to t dimension size
	 * @param ySize y dimension size
	 * @param xSize x dimension size
	 * @return 3D integer array
	 * @since 1.1.2
	 */
	public static ArrayInt.D3 create3DArrayInteger( List<List<BigDecimal>> tyxValues, int ySize, int xSize ){
		Preconditions.checkNotNull( tyxValues, buildNotNullMessage( "tyxValues" ) );
		ArrayInt.D3 array = empty3DArrayInteger( tyxValues.size(), ySize, xSize );
		parallelRange3D( tyxValues.size(), ySize, xSize )
				.forEachOrdered( index -> {
					int t = index.getTime();
					int x = index.getCol();
					int y = index.getRow();
					array.set( t, y, x, tyxValues.get( t ).get( create1DIndex( y, x, xSize ) ).intValue() );
				} );
		return array;
	}

	/**
	 * Create the 3D float array with t-y-x order dimension data values.
	 *
	 * @param tyxValues t-yx three dimension values, first list size equals to t dimension size
	 * @param ySize y dimension size
	 * @param xSize x dimension size
	 * @return 3D float array
	 * @since 1.1.0
	 */
	public static ArrayFloat.D3 create3DArrayFloat( List<List<BigDecimal>> tyxValues, int ySize, int xSize ){
		Preconditions.checkNotNull( tyxValues, buildNotNullMessage( "tyxValues" ) );
		ArrayFloat.D3 array = empty3DArrayFloat( tyxValues.size(), ySize, xSize );
		parallelRange3D( tyxValues.size(), ySize, xSize )
				.forEachOrdered( index -> {
					int t = index.getTime();
					int x = index.getCol();
					int y = index.getRow();
					array.set( t, y, x, tyxValues.get( t ).get( create1DIndex( y, x, xSize ) ).floatValue() );
				} );
		return array;
	}

	/**
	 * Create the 3D double array with t-y-x order dimension data values.
	 *
	 * @param tyxValues t-yx three dimension values, first list size equals to t dimension size
	 * @param ySize y dimension size
	 * @param xSize x dimension size
	 * @return 3D double array
	 * @since 1.1.0
	 */
	public static ArrayDouble.D3 create3DArrayDouble( List<List<BigDecimal>> tyxValues, int ySize, int xSize ){
		Preconditions.checkNotNull( tyxValues, buildNotNullMessage( "tyxValues" ) );
		ArrayDouble.D3 array = empty3DArrayDouble( tyxValues.size(), ySize, xSize );
		parallelRange3D( tyxValues.size(), ySize, xSize )
				.forEachOrdered( index -> {
					int t = index.getTime();
					int x = index.getCol();
					int y = index.getRow();
					array.set( t, y, x, tyxValues.get( t ).get( create1DIndex( y, x, xSize ) ).doubleValue() );
				} );
		return array;
	}

	/**
	 * Create the empty 1D shout array with size.
	 *
	 * @param size size
	 * @return empty 1D shout array
	 * @since 1.1.0
	 */
	public static ArrayShort.D1 empty1DArrayShort( int size ){
		Preconditions.checkArgument( size > 0 );
		return new ArrayShort.D1( size );
	}

	/**
	 * Create the empty 1D integer array with size.
	 *
	 * @param size size
	 * @return empty 1D integer array
	 * @since 1.1.2
	 */
	public static ArrayInt.D1 empty1DArrayInteger( int size ){
		Preconditions.checkArgument( size > 0 );
		return new ArrayInt.D1( size );
	}

	/**
	 * Create the empty 1D float array with size.
	 *
	 * @param size size
	 * @return empty 1D float array
	 * @since 1.1.0
	 */
	public static ArrayFloat.D1 empty1DArrayFloat( int size ){
		Preconditions.checkArgument( size > 0 );
		return new ArrayFloat.D1( size );
	}

	/**
	 * Create the empty 1D double array with size.
	 *
	 * @param size size
	 * @return empty 1D double array
	 * @since 1.1.0
	 */
	public static ArrayDouble.D1 empty1DArrayDouble( int size ){
		Preconditions.checkArgument( size > 0 );
		return new ArrayDouble.D1( size );
	}

	/**
	 * Create the empty 1D char array with size.
	 *
	 * @param size size
	 * @return empty 1D char array
	 * @since 1.2.1.1
	 */
	public static ArrayChar.D1 empty1DArrayChar( int size ){
		Preconditions.checkArgument( size > 0 );
		return new ArrayChar.D1( size );
	}

	/**
	 * Create the empty 2D short array with size.
	 *
	 * @param ySize y dimension size
	 * @param xSize x dimension size
	 * @return empty 2D short array
	 * @since 1.1.2
	 */
	public static ArrayShort.D2 empty2DArrayShort( int ySize, int xSize ){
		Preconditions.checkArgument( ySize > 0 && xSize > 0 );
		return new ArrayShort.D2( ySize, xSize );
	}

	/**
	 * Create the empty 2D integer array with size.
	 *
	 * @param ySize y dimension size
	 * @param xSize x dimension size
	 * @return empty 2D integer array
	 * @since 1.1.2
	 */
	public static ArrayInt.D2 empty2DArrayInteger( int ySize, int xSize ){
		Preconditions.checkArgument( ySize > 0 && xSize > 0 );
		return new ArrayInt.D2( ySize, xSize );
	}

	/**
	 * Create the empty 2D float array with size.
	 *
	 * @param ySize y dimension size
	 * @param xSize x dimension size
	 * @return empty 2D float array
	 * @since 1.1.2
	 */
	public static ArrayFloat.D2 empty2DArrayFloat( int ySize, int xSize ){
		Preconditions.checkArgument( ySize > 0 && xSize > 0 );
		return new ArrayFloat.D2( ySize, xSize );
	}

	/**
	 * Create the empty 2D double array with size.
	 *
	 * @param ySize y dimension size
	 * @param xSize x dimension size
	 * @return empty 2D double array
	 * @since 1.1.2
	 */
	public static ArrayDouble.D2 empty2DArrayDouble( int ySize, int xSize ){
		Preconditions.checkArgument( ySize > 0 && xSize > 0 );
		return new ArrayDouble.D2( ySize, xSize );
	}

	/**
	 * Create the empty 2D char array with size.
	 *
	 * @param orderSize order size
	 * @param stringSize string size
	 * @return empty 2D char array
	 * @since 1.2.1.1
	 */
	public static ArrayChar.D2 empty2DArrayChar( int orderSize, int stringSize ){
		Preconditions.checkArgument( orderSize > 0 && stringSize > 0 );
		return new ArrayChar.D2( orderSize, stringSize );
	}

	/**
	 * Create the empty 3D short array with size.
	 *
	 * @param tSize t dimension size
	 * @param ySize y dimension size
	 * @param xSize x dimension size
	 * @return empty 3D short array
	 * @since 1.1.0
	 */
	public static ArrayShort.D3 empty3DArrayShort( int tSize, int ySize, int xSize ){
		Preconditions.checkArgument( tSize > 0 && ySize > 0 && xSize > 0 );
		return new ArrayShort.D3( tSize, ySize, xSize );
	}

	/**
	 * Create the empty 3D integer array with size.
	 *
	 * @param tSize t dimension size
	 * @param ySize y dimension size
	 * @param xSize x dimension size
	 * @return empty 3D integer array
	 * @since 1.1.0
	 */
	public static ArrayInt.D3 empty3DArrayInteger( int tSize, int ySize, int xSize ){
		Preconditions.checkArgument( tSize > 0 && ySize > 0 && xSize > 0 );
		return new ArrayInt.D3( tSize, ySize, xSize );
	}

	/**
	 * Create the empty 3D float array with size.
	 *
	 * @param tSize t dimension size
	 * @param ySize y dimension size
	 * @param xSize x dimension size
	 * @return empty 3D float array
	 * @since 1.1.0
	 */
	public static ArrayFloat.D3 empty3DArrayFloat( int tSize, int ySize, int xSize ){
		Preconditions.checkArgument( tSize > 0 && ySize > 0 && xSize > 0 );
		return new ArrayFloat.D3( tSize, ySize, xSize );
	}

	/**
	 * Create the empty 3D double array with size.
	 *
	 * @param tSize t dimension size
	 * @param ySize y dimension size
	 * @param xSize x dimension size
	 * @return empty 3D double array
	 * @since 1.1.0
	 */
	public static ArrayDouble.D3 empty3DArrayDouble( int tSize, int ySize, int xSize ){
		Preconditions.checkArgument( tSize > 0 && ySize > 0 && xSize > 0 );
		return new ArrayDouble.D3( tSize, ySize, xSize );
	}

	/**
	 * Get the data type of variable.
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
		validateArray( values, scale, offset, missing );
		int length = values.getShape()[0];
		return IntStream.range( 0, length )
				.parallel()
				.mapToObj( i -> NetCDFUtils.readArrayValue( values, i, scale, offset, missing ) )
				.collect( Collectors.toList());
	}

	/**
	 * Read the Y, X array values from the Y, X two-dimension array with default scale, offset factor to original value, missing value and not inverted Y dimension. <br/>
	 * if not is two-dimension array, return empty list.
	 *
	 * @param values array values
	 * @return list of yx one dimension values
	 * @since 1.1.2
	 */
	public static List<BigDecimal> readYXDimensionArrayValues( Array values ){
		return readYXDimensionArrayValues( values, false );
	}

	/**
	 * Read the Y, X array values from the Y, X two-dimension array with default scale, offset factor to original value and missing value. <br/>
	 * if not is two-dimension array, return empty list.
	 *
	 * @param values array values
	 * @param invertedY inverted Y dimension or not
	 * @return list of yx one dimension values
	 * @since 1.1.3
	 */
	public static List<BigDecimal> readYXDimensionArrayValues( Array values, boolean invertedY ){
		return readYXDimensionArrayValues( values, new BigDecimal( "1" ), BigDecimal.ZERO, VariableAttribute.MISSING, invertedY );
	}

	/**
	 * Read the Y, X array values from the Y, X two-dimension array with scale, offset factor to original value, missing value and not inverted Y dimension. <br/>
	 * if not is two-dimension array, return empty list.
	 *
	 * @param values array values
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @param missing missing value
	 * @return list of yx one dimension values
	 * @since 1.1.2
	 */
	public static List<BigDecimal> readYXDimensionArrayValues( Array values, BigDecimal scale, BigDecimal offset, BigDecimal missing ){
		return readYXDimensionArrayValues( values, scale, offset, missing, false );
	}

	/**
	 * Read the Y, X array values from the Y, X two-dimension array with scale, offset factor to original value and missing value. <br/>
	 * if not is two-dimension array, return empty list.
	 *
	 * @param values array values
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @param missing missing value
	 * @param invertedY inverted Y dimension or not
	 * @return list of yx one dimension values
	 * @since 1.1.3
	 */
	public static List<BigDecimal> readYXDimensionArrayValues( Array values, BigDecimal scale, BigDecimal offset, BigDecimal missing, boolean invertedY ){
		validateArray( values, scale, offset, missing );
		int[] shape = values.getShape();
		Preconditions.checkArgument( shape.length == 2, "NetCDFUtils: The values array shape size should be 2." );

		List<BigDecimal> grid = new ArrayList<>();
		int ySize = shape[ 0 ];
		int xSize = shape[ 1 ];
		Index index = values.getIndex();

		parallelRange2D( ySize, xSize, invertedY ).forEachOrdered( index2D -> {
			int x = index2D.getCol();
			int y = index2D.getRow();
			grid.add( NetCDFUtils.readArrayValue( values, index.set( y, x ), scale, offset, missing ) );
		} );
		return grid;
	}

	/**
	 * Read the Time, Y, X three-dimension array values to list with default scale, offset factor to original value, missing value and not inverted Y dimension.
	 *
	 * @param values array values
	 * @return list with index order time-YX values
	 * @since 1.0.0
	 */
	public static List<List<BigDecimal>> readTYXDimensionArrayValues( Array values ){
		return readTYXDimensionArrayValues( values, false );
	}

	/**
	 * Read the Time, Y, X three-dimension array values to list with default scale, offset factor to original value, missing value.
	 *
	 * @param values array values
	 * @param invertedY inverted Y dimension or not
	 * @return list with index order time-YX values
	 * @since 1.1.3
	 */
	public static List<List<BigDecimal>> readTYXDimensionArrayValues( Array values, boolean invertedY ){
		return readTYXDimensionArrayValues( values, new BigDecimal( "1" ), BigDecimal.ZERO, VariableAttribute.MISSING, invertedY );
	}

	/**
	 * Read the Time, Y, X three-dimension array values to list with scale, offset factor to original value, missing value and not inverted Y dimension.
	 *
	 * @param values array values
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @param missing missing value
	 * @return list with index order time-yx one dimension values
	 * @since 1.0.0
	 */
	public static List<List<BigDecimal>> readTYXDimensionArrayValues( Array values, BigDecimal scale, BigDecimal offset, BigDecimal missing ){
		return readTYXDimensionArrayValues( values, scale, offset, missing, false );
	}

	/**
	 * Read the Time, Y, X three-dimension array values to list with scale, offset factor to original value and missing value.
	 *
	 * @param values array values
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @param missing missing value
	 * @param invertedY inverted Y dimension or not
	 * @return list with index order time-yx one dimension values
	 * @since 1.1.3
	 */
	public static List<List<BigDecimal>> readTYXDimensionArrayValues( Array values, BigDecimal scale, BigDecimal offset, BigDecimal missing, boolean invertedY ){
		validateArray( values, scale, offset, missing );
		int[] shape = values.getShape();
		Preconditions.checkArgument( shape.length == 3, "NetCDFUtils: The values array shape size should be 3." );

		int timeSize = shape[ 0 ];
		return IntStream.range( 0, timeSize )
				.parallel()
				.mapToObj( time -> sliceTDimensionArrayYXValues( values, time, scale, offset, missing, invertedY ) )
				.collect( Collectors.toList() );
	}

	/**
	 * Slice the Y, X array values at t index from the Time, Y, X three-dimension array with <p>default</> scale, offset factor to original value, missing value and not inverted Y dimension. <br/>
	 * if not is three-dimension array, return empty list.
	 *
	 * @param values array values
	 * @param tIndex t dimension index
	 * @return list of yx one dimension values
	 * @since 1.1.0
	 */
	public static List<BigDecimal> sliceTDimensionArrayYXValues( Array values, int tIndex ){
		return sliceTDimensionArrayYXValues( values, tIndex, false );
	}

	/**
	 * Slice the Y, X array values at t index from the Time, Y, X three-dimension array with scale, offset factor to original value and missing value. <br/>
	 * if not is three-dimension array, return empty list.
	 *
	 * @param values array values
	 * @param tIndex t dimension index
	 * @param invertedY inverted Y dimension or not
	 * @return list of yx one dimension values
	 * @since 1.1.3
	 */
	public static List<BigDecimal> sliceTDimensionArrayYXValues( Array values, int tIndex, boolean invertedY ){
		return sliceTDimensionArrayYXValues( values, tIndex, new BigDecimal( "1" ), BigDecimal.ZERO, VariableAttribute.MISSING, invertedY );
	}

	/**
	 * Slice the Y, X array values at t index from the Time, Y, X three-dimension array with scale, offset factor to original value, missing value and not inverted Y dimension. <br/>
	 * if not is three-dimension array, return empty list.
	 *
	 * @param values array values
	 * @param tIndex t dimension index
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @param missing missing value
	 * @return list of yx one dimension values
	 * @since 1.1.0
	 */
	public static List<BigDecimal> sliceTDimensionArrayYXValues( Array values, int tIndex, BigDecimal scale, BigDecimal offset, BigDecimal missing ){
		return sliceTDimensionArrayYXValues( values, tIndex, scale, offset, missing, false );
	}

	/**
	 * Slice the Y, X array values at t index from the Time, Y, X three-dimension array with scale, offset factor to original value and missing value. <br/>
	 * if not is three-dimension array, return empty list.
	 *
	 * @param values array values
	 * @param tIndex t dimension index
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @param missing missing value
	 * @param invertedY inverted Y dimension or not
	 * @return list of yx one dimension values
	 * @since 1.1.3
	 */
	public static List<BigDecimal> sliceTDimensionArrayYXValues( Array values, int tIndex, BigDecimal scale, BigDecimal offset, BigDecimal missing, boolean invertedY ){
		validateArray( values, scale, offset, missing );
		int[] shape = values.getShape();
		Preconditions.checkArgument( shape.length == 3, "NetCDFUtils: The values array shape size should be 3." );
		Preconditions.checkElementIndex( tIndex, shape[0], "NetCDFUtils: the tIndex should not greater than t dimension size." );

		List<BigDecimal> grid = new ArrayList<>();
		int ySize = shape[ 1 ];
		int xSize = shape[ 2 ];
		Index index = values.getIndex();

		parallelRange2D( ySize, xSize, invertedY ).forEachOrdered( index2D -> {
			int x = index2D.getCol();
			int y = index2D.getRow();
			grid.add( NetCDFUtils.readArrayValue( values, index.set( tIndex, y, x ), scale, offset, missing ) );
		} );
		return grid;
	}

	/**
	 * Read the Time, Station two-dimension array values to list with default scale, offset factor to original value and missing value.
	 *
	 * @param values array values
	 * @param stationIndex station index
	 * @return station time series values
	 * @since 1.1.5
	 */
	public static List<BigDecimal> readTimeStationArrayValues( Array values, int stationIndex ){
		return readTimeStationArrayValues( values, stationIndex, new BigDecimal( "1" ), BigDecimal.ZERO, VariableAttribute.MISSING );
	}

	/**
	 * Read the Time, Station two-dimension array values to list with scale, offset factor to original value and missing value.
	 *
	 * @param values array values
	 * @param stationIndex station index
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @param missing missing value
	 * @return station time series values
	 * @since 1.1.5
	 */
	public static List<BigDecimal> readTimeStationArrayValues( Array values, int stationIndex, BigDecimal scale, BigDecimal offset, BigDecimal missing ){
		validateArray( values, scale, offset, missing );
		int[] shape = values.getShape();
		Preconditions.checkArgument( shape.length == 2, "NetCDFUtils: The values array shape size should be 2." );
		Preconditions.checkElementIndex( stationIndex, shape[1], "NetCDFUtils: the stationIndex should not greater than station dimension size." );

		int tSize = shape[ 0 ];
		Index index = values.getIndex();

		return IntStream.range( 0, tSize )
				.parallel()
				.mapToObj( time -> NetCDFUtils.readArrayValue( values, index.set( time, stationIndex ), scale, offset, missing ) )
				.collect( Collectors.toList() );
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
		validateFactor( scale, offset, missing );
		return value.compareTo( missing ) == 0 ? VariableAttribute.MISSING : value.multiply( scale ).add( offset );
	}

	/**
	 * Package the original value with scale, offset factor to package value, if is missing value, return default missing.
	 *
	 * @param value package value
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @return package value, if is missing value, return missing
	 * @since 1.1.0
	 */
	public static BigDecimal packageValue( BigDecimal value, BigDecimal scale, BigDecimal offset ) {
		return packageValue( value, scale, offset, VariableAttribute.MISSING );
	}

	/**
	 * Package the original value with scale, offset factor to package value, if is missing value, return missing.
	 *
	 * @param value package value
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @param missing missing value
	 * @return package value, if is missing value, return missing
	 * @since 1.1.0
	 */
	public static BigDecimal packageValue( BigDecimal value, BigDecimal scale, BigDecimal offset, BigDecimal missing ) {
		Preconditions.checkNotNull( value, buildNotNullMessage( "value" ) );
		validateFactor( scale, offset, missing );
		return value.compareTo( missing ) == 0 ? VariableAttribute.MISSING : value.subtract( offset ).divide( scale, 0, RoundingMode.HALF_UP );
	}

	/**
	 *  Validate the array and factor number not null.
	 *
	 * @param values array
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @param missing missing value
	 * @since 1.1.2
	 */
	private static void validateArray( Array values, BigDecimal scale, BigDecimal offset, BigDecimal missing ){
		Preconditions.checkNotNull( values, buildNotNullMessage( "values" ) );
		validateFactor( scale, offset, missing );
	}

	/**
	 * Validate the factor number not null.
	 *
	 * @param scale value scale factor
	 * @param offset value offset factor
	 * @param missing missing value
	 * @since 1.1.2
	 */
	private static void validateFactor( BigDecimal scale, BigDecimal offset, BigDecimal missing  ){
		Preconditions.checkNotNull( scale, buildNotNullMessage( "scale" ) );
		Preconditions.checkNotNull( offset, buildNotNullMessage( "offset" ) );
		Preconditions.checkNotNull( missing, buildNotNullMessage( "missing" ) );
	}

	/**
	 * Get int stream with range.
	 *
	 * @param to to index
	 * @param inverted is inverted or not
	 * @return IntStream
	 * @since 1.1.3
	 */
	@SuppressWarnings( "unused" )
	private static IntStream range( int to, boolean inverted ){
		return inverted ? IntStream.range( 0, to ).map( i -> to - i - 1 ) : IntStream.range( 0, to );
	}

	/**
	 * Get int parallel stream with range.
	 *
	 * @param to to index
	 * @param inverted is inverted or not
	 * @return IntStream
	 * @since 1.2.0
	 */
	private static IntStream parallelRange( int to, boolean inverted ){
		return inverted ? IntStream.range( 0, to ).parallel().map( i -> to - i - 1 ) : IntStream.range( 0, to ).parallel();
	}

	/**
	 * Get two dimensional index parallel stream with range.
	 *
	 * @param rows size of row
	 * @param cols size of col
	 * @param inverted is inverted or not
	 * @return stream of two dimensional index
	 * @since 1.2.0
	 */
	private static Stream<IndexYX> parallelRange2D( int rows, int cols, boolean inverted ){
		return parallelRange( rows, inverted )
				.mapToObj( y -> IntStream.range( 0, cols ).parallel().mapToObj( x -> new IndexYX( x, y ) ) )
				.flatMap( Function.identity() );
	}

	/**
	 * Get two dimensional index parallel stream with range.
	 *
	 * @param times times
	 * @param rows size of row
	 * @param cols size of col
	 * @return stream of three dimensional index
	 * @since 1.2.0
	 */
	public static Stream<IndexTYX> parallelRange3D( int times, int rows, int cols ){
		return IntStream.range( 0, times )
				.parallel()
				.mapToObj( t -> IntStream.range( 0, rows )
						.parallel()
						.mapToObj( y -> IntStream.range( 0, cols ).parallel().mapToObj( x -> new IndexTYX( x, y, t ) ) )
						.flatMap( Function.identity() )
				)
				.flatMap( Function.identity() );
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
		return "NetCDFUtils: " + target + " should not be null.";
	}
}
