package tw.fondus.commons.nc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import strman.Strman;
import tw.fondus.commons.nc.util.key.DimensionName;
import tw.fondus.commons.nc.util.key.GlobalAttribute;
import tw.fondus.commons.nc.util.key.VariableAttribute;
import tw.fondus.commons.nc.util.key.VariableName;
import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;

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

		ArrayDouble.D1 y = new ArrayDouble.D1( 10 );
		ArrayDouble.D1 x = new ArrayDouble.D1( 10 );
		ArrayDouble.D1 times = new ArrayDouble.D1( 10 );
		ArrayFloat.D3 rainfall = new ArrayFloat.D3( 10, 10, 10 );

		/** Y **/
		IntStream.range( 0, 10 ).forEach( i -> {
			y.set( i, i );
		} );

		/** X **/
		IntStream.range( 0, 10 ).forEach( i -> {
			x.set( i, i );
		} );

		/** Time **/
		DateTime createTime = new DateTime();
		IntStream.range( 0, 10 ).forEach( i -> {
			long time = createTime.plusHours( i ).getMillis() / (60 * 1000); // millisseconds
																				// to
																				// minute
			times.set( i, time );
		} );

		/** Rainfall **/
		IntStream.range( 0, 10 ).forEach( t -> {
			IntStream.range( 0, 10 ).forEach( j -> {
				IntStream.range( 0, 10 ).forEach( i -> {
					rainfall.set( t, j, i, (float) Math.random() );
				} );
			} );
		} );

		this.valueMap = new HashMap<String, Array>();
		this.valueMap.put( DimensionName.X, x );
		this.valueMap.put( DimensionName.Y, y );
		this.valueMap.put( DimensionName.TIME, times );
		this.valueMap.put( "rainfall", rainfall );
	}

	@Test
	public void test() throws IOException, InvalidRangeException {
		DateTime createTime = new DateTime();

		NetCDFBuilder.create( new File( "src/test/resources/test.nc" ).getPath() )
				.addGlobalAttribute( GlobalAttribute.CONVENTIONS, "CF-1.6" )
				.addGlobalAttribute( GlobalAttribute.TITLE, "Test Data" )
				.addGlobalAttribute( GlobalAttribute.INSTITUTION, "FondUS" )
				.addGlobalAttribute( GlobalAttribute.SOURCE, "Export NETCDF-CF_GRID from FEWS-Taiwan" )
				.addGlobalAttribute( GlobalAttribute.HISTORY,
						Strman.append( createTime.toString(),
								" GMT: exported from FEWS-Taiwan" ) )
				.addGlobalAttribute( GlobalAttribute.REFERENCES, "http://www.delft-fews.com" )
				.addGlobalAttribute( GlobalAttribute.METADATA_CONVENTIONS, "Unidata Dataset Discovery v1.0" )
				.addGlobalAttribute( GlobalAttribute.SUMMARY, "Data exported from FEWS-Taiwan" )
				.addGlobalAttribute( GlobalAttribute.DATE_CREATE,
						Strman.append( createTime.toString(), " GMT" ) )
				.addDimension( DimensionName.TIME, 10 )
				.addDimension( DimensionName.Y, 10 )
				.addDimension( DimensionName.X, 10 )
				.addVariable( VariableName.TIME, DataType.DOUBLE, new String[] { DimensionName.TIME } )
				.addVariableAttribute( VariableName.TIME, VariableAttribute.KEY_NAME, "time" )
				.addVariableAttribute( VariableName.TIME, VariableAttribute.KEY_NAME_LONG, "time" )
				.addVariableAttribute( VariableName.TIME, VariableAttribute.KEY_UNITS, VariableAttribute.UNITS_TIME )
				.addVariableAttribute( VariableName.TIME, VariableAttribute.KEY_AXIS, VariableAttribute.AXIS_TIME )
				.addVariable( VariableName.Y, DataType.DOUBLE, new String[] { DimensionName.Y } )
				.addVariableAttribute( VariableName.Y, VariableAttribute.KEY_NAME, VariableAttribute.COORDINATES_Y_WGS84 )
				.addVariableAttribute( VariableName.Y, VariableAttribute.KEY_NAME_LONG, VariableAttribute.NAME_Y_WGS84 )
				.addVariableAttribute( VariableName.Y, VariableAttribute.KEY_UNITS, VariableAttribute.UNITS_Y_WGS84 )
				.addVariableAttribute( VariableName.Y, VariableAttribute.KEY_AXIS, VariableAttribute.AXIS_Y )
				.addVariableAttribute( VariableName.Y, VariableAttribute.KEY_MISSINGVALUE,
						VariableAttribute.MISSINGVALUE_COORDINATES )
				.addVariable( VariableName.X, DataType.DOUBLE, new String[] { DimensionName.X } )
				.addVariableAttribute( VariableName.X, VariableAttribute.KEY_NAME, VariableAttribute.COORDINATES_X_WGS84 )
				.addVariableAttribute( VariableName.X, VariableAttribute.KEY_NAME_LONG, VariableAttribute.NAME_X_WGS84 )
				.addVariableAttribute( VariableName.X, VariableAttribute.KEY_UNITS, VariableAttribute.UNITS_X_WGS84 )
				.addVariableAttribute( VariableName.X, VariableAttribute.KEY_AXIS, VariableAttribute.AXIS_X )
				.addVariableAttribute( VariableName.X, VariableAttribute.KEY_MISSINGVALUE,
						VariableAttribute.MISSINGVALUE_COORDINATES )
				.addVariable( "rainfall", DataType.FLOAT, new String[] { DimensionName.TIME, DimensionName.Y, DimensionName.X } )
				.addVariableAttribute( "rainfall", VariableAttribute.KEY_NAME_LONG, "Rainfall" )
				.addVariableAttribute( "rainfall", VariableAttribute.KEY_UNITS, "mm" )
				.addVariableAttribute( "rainfall", VariableAttribute.KEY_MISSINGVALUE, VariableAttribute.MISSINGVALUE )
				.build() // Finished NetCDF file structures define mode
				.writeValues( VariableName.TIME, this.valueMap.get( DimensionName.TIME ) )
				.writeValues( VariableName.Y, this.valueMap.get( DimensionName.Y ) )
				.writeValues( VariableName.X, this.valueMap.get( DimensionName.X ) )
				.writeValues( "rainfall", this.valueMap.get( "rainfall" ) )
				.close(); // close IO
	}
}
