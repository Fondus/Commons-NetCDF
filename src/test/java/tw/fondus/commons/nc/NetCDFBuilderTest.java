package tw.fondus.commons.nc;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import tw.fondus.commons.nc.util.NetCDFUtils;
import tw.fondus.commons.nc.util.key.DimensionName;
import tw.fondus.commons.nc.util.key.GlobalAttribute;
import tw.fondus.commons.nc.util.key.VariableAttribute;
import tw.fondus.commons.nc.util.key.VariableName;
import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The unit test of use builder to construct NetCDF.
 * 
 * @author Brad Chen
 *
 */
public class NetCDFBuilderTest {
	private Map<String, Array> valueMap;

	@Before
	public void prepareData() throws IOException {
		Files.deleteIfExists( Paths.get( "src/test/resources/test.nc" ) );

		int tSize = 10;
		int ySize = 10;
		int xSize = 10;

		ArrayDouble.D1 y = NetCDFUtils.create1DArrayDouble( IntStream.range( 0, ySize ).mapToObj( BigDecimal::new ).collect( Collectors.toList() ) );
		ArrayDouble.D1 x = NetCDFUtils.create1DArrayDouble( IntStream.range( 0, xSize ).mapToObj( BigDecimal::new ).collect( Collectors.toList() ) );
		ArrayDouble.D1 times = NetCDFUtils.empty1DArrayDouble( tSize );

		// Time
		DateTime createTime = new DateTime();
		IntStream.range( 0, tSize ).forEach( i -> {
			long time = createTime.plusHours( i ).getMillis() / (60 * 1000); // milliseconds to minute
			times.set( i, time );
		} );

		// Rainfall
		List<List<BigDecimal>> values = new ArrayList<>();
		IntStream.range( 0, tSize ).forEach( t -> {
			values.add( new ArrayList<>() );
			IntStream.range( 0, ySize ).forEach( j -> {
				IntStream.range( 0, xSize ).forEach( i -> {
					values.get( t ).add( new BigDecimal( Math.random() ) );
				} );
			} );
		} );
		ArrayFloat.D3 rainfall = NetCDFUtils.create3DArrayFloat( values, ySize, xSize );

		this.valueMap = new HashMap<>();
		this.valueMap.put( DimensionName.X, x );
		this.valueMap.put( DimensionName.Y, y );
		this.valueMap.put( DimensionName.TIME, times );
		this.valueMap.put( "rainfall", rainfall );
	}

	@Test
	public void test() throws IOException, InvalidRangeException {
		DateTime createTime = new DateTime();

		NetCDFBuilder.create( Paths.get( "src/test/resources/test.nc" ) )
				.addGlobalAttribute( GlobalAttribute.CONVENTIONS, "CF-1.6" )
				.addGlobalAttribute( GlobalAttribute.TITLE, "Test Data" )
				.addGlobalAttribute( GlobalAttribute.INSTITUTION, "FondUS" )
				.addGlobalAttribute( GlobalAttribute.SOURCE, "Export NETCDF-CF_GRID from FEWS-Taiwan" )
				.addGlobalAttribute( GlobalAttribute.HISTORY, createTime.toString() + " GMT: exported from FEWS-Taiwan" )
				.addGlobalAttribute( GlobalAttribute.REFERENCES, "http://www.delft-fews.com" )
				.addGlobalAttribute( GlobalAttribute.METADATA_CONVENTIONS, "Unidata Dataset Discovery v1.0" )
				.addGlobalAttribute( GlobalAttribute.SUMMARY, "Data exported from FEWS-Taiwan" )
				.addGlobalAttribute( GlobalAttribute.DATE_CREATE, createTime.toString() + " GMT" )
				.addDimension( DimensionName.TIME, 10 )
				.addDimension( DimensionName.Y, 10 )
				.addDimension( DimensionName.X, 10 )
				.addVariable( VariableName.TIME, DataType.DOUBLE, DimensionName.TIME )
				.addVariableAttribute( VariableName.TIME, VariableAttribute.KEY_NAME, "time" )
				.addVariableAttribute( VariableName.TIME, VariableAttribute.KEY_NAME_LONG, "time" )
				.addVariableAttribute( VariableName.TIME, VariableAttribute.KEY_UNITS, VariableAttribute.UNITS_TIME_MINUTES )
				.addVariableAttribute( VariableName.TIME, VariableAttribute.KEY_AXIS, VariableAttribute.AXIS_TIME )
				.addVariable( VariableName.Y, DataType.DOUBLE, DimensionName.Y )
				.addVariableAttribute( VariableName.Y, VariableAttribute.KEY_NAME, VariableAttribute.COORDINATES_Y_WGS84 )
				.addVariableAttribute( VariableName.Y, VariableAttribute.KEY_NAME_LONG, VariableAttribute.NAME_Y_WGS84 )
				.addVariableAttribute( VariableName.Y, VariableAttribute.KEY_UNITS, VariableAttribute.UNITS_Y_WGS84 )
				.addVariableAttribute( VariableName.Y, VariableAttribute.KEY_AXIS, VariableAttribute.AXIS_Y )
				.addVariableAttribute( VariableName.Y, VariableAttribute.KEY_MISSING, VariableAttribute.MISSING_COORDINATES )
				.addVariable( VariableName.X, DataType.DOUBLE, DimensionName.X )
				.addVariableAttribute( VariableName.X, VariableAttribute.KEY_NAME, VariableAttribute.COORDINATES_X_WGS84 )
				.addVariableAttribute( VariableName.X, VariableAttribute.KEY_NAME_LONG, VariableAttribute.NAME_X_WGS84 )
				.addVariableAttribute( VariableName.X, VariableAttribute.KEY_UNITS, VariableAttribute.UNITS_X_WGS84 )
				.addVariableAttribute( VariableName.X, VariableAttribute.KEY_AXIS, VariableAttribute.AXIS_X )
				.addVariableAttribute( VariableName.X, VariableAttribute.KEY_MISSING, VariableAttribute.MISSING_COORDINATES )
				.addVariable( "rainfall", DataType.FLOAT, DimensionName.TIME, DimensionName.Y, DimensionName.X )
				.addVariableAttribute( "rainfall", VariableAttribute.KEY_NAME_LONG, "Rainfall" )
				.addVariableAttribute( "rainfall", VariableAttribute.KEY_UNITS, "mm" )
				.addVariableAttribute( "rainfall", VariableAttribute.KEY_MISSING, VariableAttribute.MISSING.doubleValue() )
				.build() // Finished NetCDF file structures define mode
				.writeValues( VariableName.TIME, this.valueMap.get( DimensionName.TIME ) )
				.writeValues( VariableName.Y, this.valueMap.get( DimensionName.Y ) )
				.writeValues( VariableName.X, this.valueMap.get( DimensionName.X ) )
				.writeValues( "rainfall", this.valueMap.get( "rainfall" ) )
				.close(); // close IO
	}
}
