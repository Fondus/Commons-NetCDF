package tw.fondus.commons.nc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tw.fondus.commons.nc.util.key.DimensionName;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * The unit test of NerCDF reader.
 * 
 * @author Brad Chen
 *
 */
public class NetCDFReaderTest {
	private final String url = "src/test/resources/QPESUMS_QPE.nc";

	@Before
	public void setUp() {
		Path path = Paths.get( this.url );
		Assert.assertTrue( Files.exists( path ) );
	}

	@Test
	public void testBottom() throws Exception {
		try ( NetCDFReader reader = NetCDFReader.read( Paths.get( this.url ) ) ){
			Assert.assertNotNull( reader.getNetCDF() );
			Assert.assertEquals( url, reader.getPath() );
		}
	}
	
	@Test
	public void testIs() throws Exception {
		try ( NetCDFReader reader = NetCDFReader.readDataset( this.url ) ){
			Assert.assertTrue( reader.isWGS84() );
			Assert.assertTrue( reader.is2D() );
			Assert.assertFalse( reader.is1D() );
		}
	}

	@Test
	public void testTimes() throws Exception {
		try ( NetCDFReader reader = NetCDFReader.readDataset( this.url ) ){
			Assert.assertTrue( reader.hasTime() );
			Assert.assertTrue( reader.findTimes().size() > 0 );
		}
	}

	@Test
	public void testDimensionLength() throws Exception {
		try ( NetCDFReader reader = NetCDFReader.readDataset( this.url ) ){
			Assert.assertEquals( 144, reader.getDimensionLength( DimensionName.TIME ) );
			Assert.assertEquals( 441, reader.getDimensionLength( DimensionName.X ) );
			Assert.assertEquals( 561, reader.getDimensionLength( DimensionName.Y ) );
		}
	}
	
	@Test
	public void testRead() throws Exception {
		try ( NetCDFReader reader = NetCDFReader.read( this.url )){
			// Get All
			Assert.assertFalse( reader.getGlobalAttributes().isEmpty() );
			Assert.assertFalse( reader.getDimensions().isEmpty() );
			Assert.assertFalse( reader.getVariables().isEmpty() );

			// Find
			Assert.assertTrue( reader.findGlobalAttribute( "references" ).isPresent() );
			Assert.assertTrue( reader.findDimension( "time" ).isPresent() );
			Assert.assertTrue( reader.findVariable( "precipitation_radar" ).isPresent() );

			// Has
			Assert.assertTrue( reader.hasGlobalAttribute( "references" ) );
			Assert.assertTrue( reader.hasDimension( "time" ) );
			Assert.assertTrue( reader.hasVariable( "x" ) );
			Assert.assertTrue( reader.hasVariable( "y" ) );
			Assert.assertTrue( reader.hasVariable( "precipitation_radar" ) );

			// Read
			Assert.assertTrue( reader.readVariable( "time" ).isPresent() );
			Assert.assertTrue( reader.readVariable( "x" ).isPresent() );
			Assert.assertTrue( reader.readVariable( "y" ).isPresent() );
			Assert.assertTrue( reader.readVariable( "precipitation_radar" ).isPresent() );
		}
	}

	@Test
	public void testReadFirstValue() throws Exception {
		try ( NetCDFReader reader = NetCDFReader.read( this.url )){
			Assert.assertTrue( reader.findFirstX().isPresent() );
			Assert.assertTrue( reader.findFirstY().isPresent() );

			reader.findFirstX().ifPresent( value -> Assert.assertEquals( new BigDecimal( "118.00625" ), value ) );
			reader.findFirstY().ifPresent( value -> Assert.assertEquals( new BigDecimal( "19.99375" ), value ) );
		}
	}

	@Test
	public void testFindStationId() throws Exception {
		String url = "src/test/resources/Tide_6M_CWB.nc";

		Path path = Paths.get( url );
		Assert.assertTrue( Files.exists( path ) );

		try ( NetCDFReader reader = NetCDFReader.read( url ) ){
			Optional<List<String>> optional = reader.findStationIds();
			Assert.assertTrue( optional.isPresent() );

			optional.ifPresent( ids -> {
				Assert.assertEquals( "1102", ids.get( 0 ) );
				Assert.assertEquals( "1116", ids.get( 1 ) );
			} );
		}
	}
}
