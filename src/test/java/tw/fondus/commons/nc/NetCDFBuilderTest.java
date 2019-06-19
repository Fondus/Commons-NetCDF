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
import tw.fondus.commons.nc.NetCDFBuilder;
import tw.fondus.commons.nc.util.key.Dimension;
import tw.fondus.commons.nc.util.key.GlobalAttribute;
import tw.fondus.commons.nc.util.key.Variable;
import tw.fondus.commons.nc.util.key.VariableAttribute;
import tw.fondus.commons.util.time.TimeUtils;
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
		this.valueMap.put( Dimension.X, x );
		this.valueMap.put( Dimension.Y, y );
		this.valueMap.put( Dimension.TIME, times );
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
						Strman.append( TimeUtils.toString( createTime, TimeUtils.YMDHMS ),
								" GMT: exported from FEWS-Taiwan" ) )
				.addGlobalAttribute( GlobalAttribute.REFERENCES, "http://www.delft-fews.com" )
				.addGlobalAttribute( GlobalAttribute.METADATA_CONVENTIONS, "Unidata Dataset Discovery v1.0" )
				.addGlobalAttribute( GlobalAttribute.SUMMARY, "Data exported from FEWS-Taiwan" )
				.addGlobalAttribute( GlobalAttribute.DATE_CREATE,
						Strman.append( TimeUtils.toString( createTime, TimeUtils.YMDHMS ), " GMT" ) )
				.addDimension( Dimension.TIME, 10 )
				.addDimension( Dimension.Y, 10 )
				.addDimension( Dimension.X, 10 )
				.addVariable( Variable.TIME, DataType.DOUBLE, new String[] { Dimension.TIME } )
				.addVariableAttribute( Variable.TIME, VariableAttribute.KEY_NAME, "time" )
				.addVariableAttribute( Variable.TIME, VariableAttribute.KEY_NAME_LONG, "time" )
				.addVariableAttribute( Variable.TIME, VariableAttribute.KEY_UNITS, VariableAttribute.UNITS_TIME )
				.addVariableAttribute( Variable.TIME, VariableAttribute.KEY_AXIS, VariableAttribute.AXIS_TIME )
				.addVariable( Variable.Y, DataType.DOUBLE, new String[] { Dimension.Y } )
				.addVariableAttribute( Variable.Y, VariableAttribute.KEY_NAME, VariableAttribute.COORDINATES_Y_WGS84 )
				.addVariableAttribute( Variable.Y, VariableAttribute.KEY_NAME_LONG, VariableAttribute.NAME_Y_WGS84 )
				.addVariableAttribute( Variable.Y, VariableAttribute.KEY_UNITS, VariableAttribute.UNITS_Y_WGS84 )
				.addVariableAttribute( Variable.Y, VariableAttribute.KEY_AXIS, VariableAttribute.AXIS_Y )
				.addVariableAttribute( Variable.Y, VariableAttribute.KEY_MISSINGVALUE,
						VariableAttribute.MISSINGVALUE_COORDINATES )
				.addVariable( Variable.X, DataType.DOUBLE, new String[] { Dimension.X } )
				.addVariableAttribute( Variable.X, VariableAttribute.KEY_NAME, VariableAttribute.COORDINATES_X_WGS84 )
				.addVariableAttribute( Variable.X, VariableAttribute.KEY_NAME_LONG, VariableAttribute.NAME_X_WGS84 )
				.addVariableAttribute( Variable.X, VariableAttribute.KEY_UNITS, VariableAttribute.UNITS_X_WGS84 )
				.addVariableAttribute( Variable.X, VariableAttribute.KEY_AXIS, VariableAttribute.AXIS_X )
				.addVariableAttribute( Variable.X, VariableAttribute.KEY_MISSINGVALUE,
						VariableAttribute.MISSINGVALUE_COORDINATES )
				.addVariable( "rainfall", DataType.FLOAT, new String[] { Dimension.TIME, Dimension.Y, Dimension.X } )
				.addVariableAttribute( "rainfall", VariableAttribute.KEY_NAME_LONG, "Rainfall" )
				.addVariableAttribute( "rainfall", VariableAttribute.KEY_UNITS, "mm" )
				.addVariableAttribute( "rainfall", VariableAttribute.KEY_MISSINGVALUE, VariableAttribute.MISSINGVALUE )
				.build() // Finished NetCDF file structures define mode
				.writeValues( Variable.TIME, this.valueMap.get( Dimension.TIME ) )
				.writeValues( Variable.Y, this.valueMap.get( Dimension.Y ) )
				.writeValues( Variable.X, this.valueMap.get( Dimension.X ) )
				.writeValues( "rainfall", this.valueMap.get( "rainfall" ) )
				.close(); // close IO
	}
}
