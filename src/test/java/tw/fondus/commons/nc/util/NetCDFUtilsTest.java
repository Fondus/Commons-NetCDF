package tw.fondus.commons.nc.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tw.fondus.commons.nc.NetCDFReader;
import tw.fondus.commons.nc.util.key.VariableAttribute;
import tw.fondus.commons.nc.util.key.VariableName;
import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.Index;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * The unit test of NetCDF with tools.
 * 
 * @author Brad Chen
 *
 */
public class NetCDFUtilsTest {
	private static final String url = "src/test/resources/QPESUMS_QPE.nc";
	
	@BeforeAll
	public static void setUp() {
		Path path = Paths.get( url );
		Assertions.assertTrue( Files.exists( path ) );
	}

	@Test
	public void testIndex(){
		Assertions.assertEquals( 12, NetCDFUtils.create1DIndex( 3, 3, 3 ) );
		Assertions.assertEquals( new int[]{ 3, 2, 1 }[0], NetCDFUtils.createTYXIndex( 3, 2, 1 )[0] );
	}

	@Test
	public void testConvertValue(){
		BigDecimal originalValue = new BigDecimal( "20" );
		BigDecimal offset = new BigDecimal( "3" );
		BigDecimal scale = new BigDecimal( "0.01" );

		BigDecimal packageValue = NetCDFUtils.packageValue( originalValue, scale, offset );
		Assertions.assertEquals( 1700, packageValue.shortValue() );
		Assertions.assertEquals( 0, originalValue.compareTo( NetCDFUtils.originalValue( packageValue, scale, offset ) ) );
	}

	@Test
	public void testAttribute(){
		Assertions.assertNotNull( NetCDFUtils.createAttribute( "test", "world" ) );
		Assertions.assertNotNull( NetCDFUtils.createAttribute( "test", 0 ) );
	}

	@Test
	public void testArray(){
		Assertions.assertAll( "Empty 1D Array",
				() -> Assertions.assertNotNull( NetCDFUtils.empty1DArrayDouble( 1 ) ),
				() -> Assertions.assertNotNull( NetCDFUtils.empty1DArrayFloat( 1 ) ),
				() -> Assertions.assertNotNull( NetCDFUtils.empty1DArrayShort( 1 ) ),
				() -> Assertions.assertNotNull( NetCDFUtils.empty1DArrayInteger( 1 ) ),
				() -> Assertions.assertNotNull( NetCDFUtils.empty1DArrayChar( 1 ) )
		);

		List<BigDecimal> values = new ArrayList<>();
		IntStream.range( 0, 10 ).forEach( i -> values.add( new BigDecimal( i ) ) );

		Assertions.assertAll( "Create 1D Array",
				() -> Assertions.assertNotNull( NetCDFUtils.create1DArrayFloat( values ) ),
				() -> Assertions.assertNotNull( NetCDFUtils.create1DArrayInteger( values ) ),
				() -> Assertions.assertNotNull( NetCDFUtils.create1DArrayShort( values ) ),
				() -> Assertions.assertNotNull( NetCDFUtils.create1DArrayDouble( values ) )
		);

		Assertions.assertAll( "Empty 2D Array",
				() -> Assertions.assertNotNull( NetCDFUtils.empty2DArrayDouble( 12, 11 ) ),
				() -> Assertions.assertNotNull( NetCDFUtils.empty2DArrayFloat( 12, 11 ) ),
				() -> Assertions.assertNotNull( NetCDFUtils.empty2DArrayShort( 12, 11 ) ),
				() -> Assertions.assertNotNull( NetCDFUtils.empty2DArrayInteger( 12, 11 ) ),
				() -> Assertions.assertNotNull( NetCDFUtils.empty2DArrayChar( 12, 11 ) )
		);

		List<BigDecimal> yxValues = new ArrayList<>();
		IntStream.range( 0, 12 ).forEach( j ->
			IntStream.range( 0, 11 ).forEach( i ->
				yxValues.add( BigDecimal.valueOf( Math.random() ) )
			)
		);
		Assertions.assertAll( "Create 2D Array",
				() -> Assertions.assertNotNull( NetCDFUtils.create2DArrayFloat( yxValues, 12, 11 ) ),
				() -> Assertions.assertNotNull( NetCDFUtils.create2DArrayInteger( yxValues, 12, 11 ) ),
				() -> Assertions.assertNotNull( NetCDFUtils.create2DArrayShort( yxValues, 12, 11 ) ),
				() -> Assertions.assertNotNull( NetCDFUtils.create2DArrayDouble( yxValues, 12, 11 ) )
		);

		List<String> strings = new ArrayList<>();
		strings.add( "hello" );
		strings.add( "world" );
		Assertions.assertNotNull( NetCDFUtils.create2DArrayChar( strings, 11 ) );

		Assertions.assertAll( "Empty 3D Array",
				() -> Assertions.assertNotNull( NetCDFUtils.empty3DArrayDouble( 1, 12, 11 ) ),
				() -> Assertions.assertNotNull( NetCDFUtils.empty3DArrayFloat( 1, 12, 11 ) ),
				() -> Assertions.assertNotNull( NetCDFUtils.empty3DArrayShort( 1, 12, 11 ) ),
				() -> Assertions.assertNotNull( NetCDFUtils.empty3DArrayInteger( 1, 12, 11 ) )
		);

		List<List<BigDecimal>> tyxValues = new ArrayList<>();
		IntStream.range( 0, 10 ).forEach( t -> {
			tyxValues.add( new ArrayList<>() );
			IntStream.range( 0, 12 ).forEach( j ->
				IntStream.range( 0, 11 ).forEach( i ->
					tyxValues.get( t ).add( BigDecimal.valueOf( Math.random() ) )
				)
			);
		} );
		Assertions.assertAll( "Create 3D Array",
				() -> Assertions.assertNotNull( NetCDFUtils.create3DArrayFloat( tyxValues, 12, 11 ) ),
				() -> Assertions.assertNotNull( NetCDFUtils.create3DArrayInteger( tyxValues, 12, 11 ) ),
				() -> Assertions.assertNotNull( NetCDFUtils.create3DArrayShort( tyxValues, 12, 11 ) ),
				() -> Assertions.assertNotNull( NetCDFUtils.create3DArrayDouble( tyxValues, 12, 11 ) )
		);
	}

	@Test
	public void testDataType() throws IOException {
		try ( NetCDFReader reader = NetCDFReader.read( url ) ){
			Assertions.assertTrue( reader.findVariable( "precipitation_radar" ).isPresent() );
			reader.findVariable( "precipitation_radar" ).ifPresent( variable -> Assertions.assertEquals( DataType.SHORT, NetCDFUtils.getVariableType( variable ) ) );
		}
	}

	@Test
	public void testReadVariableAttributeAsNumber() throws IOException {
		try ( NetCDFReader reader = NetCDFReader.read( url ) ){
			reader.findVariable( "precipitation_radar" ).ifPresent( variable -> {
				BigDecimal scale = NetCDFUtils.readVariableAttributeAsNumber( variable, VariableAttribute.KEY_SCALE, new BigDecimal( "1" ) );
				BigDecimal offset = NetCDFUtils.readVariableAttributeAsNumber( variable, VariableAttribute.KEY_OFFSET,
						BigDecimal.ZERO );
				BigDecimal missing = NetCDFUtils.readVariableAttributeAsNumber( variable, VariableAttribute.KEY_MISSING,
						VariableAttribute.MISSING );

				Assertions.assertAll( "Value Factors",
						() -> Assertions.assertEquals( new BigDecimal( "0.25" ), scale ),
						() -> Assertions.assertEquals( new BigDecimal( "0.0" ), offset ),
						() -> Assertions.assertEquals( new BigDecimal( "-32768" ), missing )
				);
			});
		}
	}

	@Test
	public void testReadVariableAttribute() throws IOException {
		try ( NetCDFReader reader = NetCDFReader.read( url ) ){
			reader.findVariable( "precipitation_radar" ).ifPresent( variable -> {
				String name = NetCDFUtils.readVariableAttribute( variable, VariableAttribute.KEY_NAME_LONG, null );
				Assertions.assertEquals( "precipitation_radar", name );

				String unit = NetCDFUtils.readVariableAttribute( variable, VariableAttribute.KEY_UNITS, null );
				Assertions.assertEquals( "mm/hr", unit );
			});
		}
	}
	
	@Test
	public void testReadArrayValue() throws IOException {
		try ( NetCDFReader reader = NetCDFReader.read( url ) ){
			reader.findVariable( "precipitation_radar" ).ifPresent( variable -> {
				// Get Factor
				BigDecimal scale = NetCDFUtils.readVariableAttributeAsNumber( variable, VariableAttribute.KEY_SCALE, new BigDecimal( "1" ) );
				BigDecimal offset = NetCDFUtils.readVariableAttributeAsNumber( variable, VariableAttribute.KEY_OFFSET, BigDecimal.ZERO );
				BigDecimal missing = NetCDFUtils.readVariableAttributeAsNumber( variable, VariableAttribute.KEY_MISSING, VariableAttribute.MISSING );

				try {
					Array values = variable.read();
					Index index = values.getIndex();
					Assertions.assertEquals( NetCDFUtils.readArrayValue( values, 0 ), NetCDFUtils.readArrayValue( values, index.set( 0, 0, 0 ) ) );

					BigDecimal value1 = NetCDFUtils.readArrayValue( values, index.set( 0, 0, 0 ), scale, offset, missing );
					Assertions.assertEquals( 0, BigDecimal.ZERO.compareTo( value1 ) );

					BigDecimal value2 = NetCDFUtils.readArrayValue( values, index.set( 0, 0, 0 ), scale, offset );
					Assertions.assertEquals( 0, BigDecimal.ZERO.compareTo( value2 ) );
					Assertions.assertEquals( value1, value2 );
				} catch (IOException e) {
					Assertions.fail();
				}
			});
		}
	}

	@Test
	public void testReadOneDimensionArrayValues() throws IOException {
		try ( NetCDFReader reader = NetCDFReader.read( url ) ){
			reader.findVariable( VariableName.X ).ifPresent( variable -> {
				try {
					List<BigDecimal> values = NetCDFUtils.readOneDimensionArrayValues( variable.read() );
					Assertions.assertAll( "Structure",
							() -> Assertions.assertFalse( values.isEmpty() ),
							() -> Assertions.assertEquals( 441, values.size() )
					);
					Assertions.assertEquals( new BigDecimal( String.valueOf( 118.00625 ) ), values.get( 0 ) );
				} catch (IOException e) {
					Assertions.fail();
				}
			} );
		}
	}

	@Test
	public void testSliceTDimensionArrayYXValues() throws IOException {
		try ( NetCDFReader reader = NetCDFReader.read( url ) ){
			reader.findVariable( "precipitation_radar" ).ifPresent( variable -> {
				try {
					Array values = variable.read();
					List<BigDecimal> yxGrid = NetCDFUtils.sliceTDimensionArrayYXValues( values, 0 );
					Assertions.assertAll( "Structure",
							() -> Assertions.assertFalse( yxGrid.isEmpty() ),
							() -> Assertions.assertEquals( 561 * 441, yxGrid.size() ),
							() -> Assertions.assertEquals( yxGrid.size(), NetCDFUtils.sliceTDimensionArrayYXValues( values, 0, new BigDecimal( "1" ), BigDecimal.ZERO, VariableAttribute.MISSING ).size() )
					);
				} catch (IOException e) {
					Assertions.fail();
				}
			});
		}
	}

	@Test
	public void testReadTYXDimensionArrayValues() throws IOException {
		try ( NetCDFReader reader = NetCDFReader.read( url ) ){
			reader.findVariable( "precipitation_radar" ).ifPresent( variable -> {
				try {
					Array values = variable.read();
					List<List<BigDecimal>> timeGrids = NetCDFUtils.readTYXDimensionArrayValues( values );
					Assertions.assertAll( "Structure",
							() -> Assertions.assertFalse( timeGrids.isEmpty() ),
							() -> Assertions.assertEquals( 144, timeGrids.size() ),
							() -> Assertions.assertEquals( 561 * 441, timeGrids.get( 0 ).size() ),
							() -> Assertions.assertEquals( timeGrids.get( 0 ).size(), NetCDFUtils.readTYXDimensionArrayValues( values, new BigDecimal( "1" ), BigDecimal.ZERO, VariableAttribute.MISSING ).get( 0 ).size() )
					);
				} catch (IOException e) {
					Assertions.fail();
				}
			});
		}
	}

	@Test
	public void testReadYXDimensionArrayValues() throws IOException {
		Path path = Paths.get( "src/test/resources/2D.nc" );
		Assertions.assertTrue( Files.exists( path ) );
		try ( NetCDFReader reader = NetCDFReader.read( path ) ){
			reader.findVariable( "block" ).ifPresent( variable -> {
				try {
					Array values = variable.read();
					List<BigDecimal> grid = NetCDFUtils.readYXDimensionArrayValues( values );
					Assertions.assertAll( "Structure",
							() -> Assertions.assertFalse( grid.isEmpty() ),
							() -> Assertions.assertEquals( 2309 * 1833, grid.size() ),
							() -> Assertions.assertEquals( grid.size(), NetCDFUtils.readYXDimensionArrayValues( values, new BigDecimal( "1" ), BigDecimal.ZERO, VariableAttribute.MISSING ).size() )
					);
				} catch (IOException e) {
					Assertions.fail();
				}
			});
		}
	}

	@Test
	public void testReadTimeStationArrayValues() throws IOException {
		Path path = Paths.get( "src/test/resources/Tide_6M_CWB.nc" );
		Assertions.assertTrue( Files.exists( path ) );
		try ( NetCDFReader reader = NetCDFReader.read( path ) ){
			reader.findVariable( "level_tide_observed" ).ifPresent( variable -> {
				try {
					Array values = variable.read();
					List<BigDecimal> series = NetCDFUtils.readTimeStationArrayValues( values, 0 );
					Assertions.assertAll( "Structure",
							() -> Assertions.assertFalse( series.isEmpty() ),
							() -> Assertions.assertEquals( 240, series.size() )
					);
				} catch (IOException e) {
					Assertions.fail();
				}
			});
		}
	}
}
