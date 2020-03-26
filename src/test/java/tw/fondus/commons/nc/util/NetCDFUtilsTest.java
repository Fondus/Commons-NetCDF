package tw.fondus.commons.nc.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
import java.util.List;

/**
 * The unit test of NetCDF with tools.
 * 
 * @author Brad Chen
 *
 */
public class NetCDFUtilsTest {
	private final String url = "src/test/resources/QPESUMS_QPE.nc";
	
	@Before
	public void setUp() {
		Path path = Paths.get( this.url );
		Assert.assertTrue( Files.exists( path ) );
	}

	@Test
	public void testIndex(){
		Assert.assertEquals( 12, NetCDFUtils.create1DIndex( 3, 3 ) );
		Assert.assertEquals( new int[]{ 3, 2, 1 }[0], NetCDFUtils.createTYXIndex( 3, 2, 1 )[0] );
	}

	@Test
	public void testAttribute(){
		Assert.assertNotNull( NetCDFUtils.createAttribute( "test", "world" ) );
		Assert.assertNotNull( NetCDFUtils.createAttribute( "test", 0 ) );
	}

	@Test
	public void testDataType() throws IOException {
		try ( NetCDFReader reader = NetCDFReader.read( this.url ) ){
			Assert.assertTrue( reader.findVariable( "precipitation_radar" ).isPresent() );
			reader.findVariable( "precipitation_radar" ).ifPresent( variable -> Assert.assertEquals( DataType.SHORT, NetCDFUtils.getVariableType( variable ) ) );
		}
	}

	@Test
	public void testReadVariableAttributeAsNumber() throws IOException {
		try ( NetCDFReader reader = NetCDFReader.read( this.url ) ){
			reader.findVariable( "precipitation_radar" ).ifPresent( variable -> {
				BigDecimal scale = NetCDFUtils.readVariableAttributeAsNumber( variable, VariableAttribute.KEY_SCALE, new BigDecimal( "1" ) );
				BigDecimal offset = NetCDFUtils.readVariableAttributeAsNumber( variable, VariableAttribute.KEY_OFFSET,
						BigDecimal.ZERO );
				BigDecimal missing = NetCDFUtils.readVariableAttributeAsNumber( variable, VariableAttribute.KEY_MISSING,
						VariableAttribute.MISSING );

				Assert.assertEquals( new BigDecimal( "0.25" ), scale );
				Assert.assertEquals( new BigDecimal( "0.0" ), offset );
				Assert.assertEquals( new BigDecimal( "-32768" ), missing );
			});
		}
	}

	@Test
	public void testReadVariableAttribute() throws IOException {
		try ( NetCDFReader reader = NetCDFReader.read( this.url ) ){
			reader.findVariable( "precipitation_radar" ).ifPresent( variable -> {
				String name = NetCDFUtils.readVariableAttribute( variable, VariableAttribute.KEY_NAME_LONG, null );
				Assert.assertEquals( "precipitation_radar", name );

				String unit = NetCDFUtils.readVariableAttribute( variable, VariableAttribute.KEY_UNITS, null );
				Assert.assertEquals( "mm/hr", unit );
			});
		}
	}
	
	@Test
	public void testReadArrayValue() throws IOException {
		try ( NetCDFReader reader = NetCDFReader.read( this.url ) ){
			reader.findVariable( "precipitation_radar" ).ifPresent( variable -> {
				// Get Factor
				BigDecimal scale = NetCDFUtils.readVariableAttributeAsNumber( variable, VariableAttribute.KEY_SCALE, new BigDecimal( "1" ) );
				BigDecimal offset = NetCDFUtils.readVariableAttributeAsNumber( variable, VariableAttribute.KEY_OFFSET, BigDecimal.ZERO );
				BigDecimal missing = NetCDFUtils.readVariableAttributeAsNumber( variable, VariableAttribute.KEY_MISSING, VariableAttribute.MISSING );

				try {
					Array values = variable.read();
					Index index = values.getIndex();

					Assert.assertEquals( NetCDFUtils.readArrayValue( values, 0 ), NetCDFUtils.readArrayValue( values, index.set( 0, 0, 0 ) ) );

					BigDecimal value1 = NetCDFUtils.readArrayValue( values, index.set( 0, 0, 0 ), scale, offset, missing );
					Assert.assertEquals( 0, BigDecimal.ZERO.compareTo( value1 ) );

					BigDecimal value2 = NetCDFUtils.readArrayValue( values, index.set( 0, 0, 0 ), scale, offset );
					Assert.assertEquals( 0, BigDecimal.ZERO.compareTo( value2 ) );
					Assert.assertEquals( value1, value2 );
					
				} catch (IOException e) {
					Assert.fail();
				}
			});
		}
	}

	@Test
	public void testReadOneDimensionArrayValues() throws IOException {
		try ( NetCDFReader reader = NetCDFReader.read( this.url ) ){
			reader.findVariable( VariableName.X ).ifPresent( variable -> {
				try {
					List<BigDecimal> values = NetCDFUtils.readOneDimensionArrayValues( variable.read() );
					Assert.assertFalse( values.isEmpty() );
					Assert.assertEquals( new BigDecimal( String.valueOf( 118.00625 ) ), values.get( 0 ) );
				} catch (IOException e) {
					Assert.fail();
				}
			} );
		}
	}

	@Test
	public void testReadTYXDimensionArrayValues() throws IOException {
		try ( NetCDFReader reader = NetCDFReader.read( this.url ) ){
			reader.findVariable( "precipitation_radar" ).ifPresent( variable -> {
				try {
					Array values = variable.read();
					List<List<BigDecimal>> timeGrids = NetCDFUtils.readTYXDimensionArrayValues( values );
					Assert.assertFalse( timeGrids.isEmpty() );
				} catch (IOException e) {
					Assert.fail();
				}
			});
		}
	}
}
