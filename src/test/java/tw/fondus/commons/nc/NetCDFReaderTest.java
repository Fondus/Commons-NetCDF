package tw.fondus.commons.nc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
	private static final String url = "src/test/resources/QPESUMS_QPE.nc";

	@BeforeAll
	public static void setUp() {
		Path path = Paths.get( url );
		Assertions.assertTrue( Files.exists( path ) );
	}

	@Test
	public void testBottom() throws Exception {
		try ( NetCDFReader reader = NetCDFReader.read( Paths.get( url ) ) ){
			Assertions.assertNotNull( reader.getNetCDF() );
			Assertions.assertEquals( url, reader.getPath() );
		}
	}
	
	@Test
	public void testIs() throws Exception {
		try ( NetCDFReader reader = NetCDFReader.readDataset( url ) ){
			Assertions.assertAll( "2D NetCDF Is",
					() -> Assertions.assertTrue( reader.isWGS84() ),
					() -> Assertions.assertTrue( reader.is2D() ),
					() -> Assertions.assertFalse( reader.is1D() )
			);
		}

		try ( NetCDFReader reader = NetCDFReader.readDataset( "src/test/resources/Tide_6M_CWB.nc" ) ){
			Assertions.assertAll( "1D NetCDF Is",
					() -> Assertions.assertFalse( reader.isWGS84() ),
					() -> Assertions.assertFalse( reader.is2D() ),
					() -> Assertions.assertTrue( reader.is1D() )
			);
		}
	}

	@Test
	public void testTimes() throws Exception {
		try ( NetCDFReader reader = NetCDFReader.readDataset( url ) ){
			Assertions.assertTrue( reader.hasTime() );
			Assertions.assertTrue( reader.findTimes().size() > 0 );
		}
	}

	@Test
	public void testDimensionLength() throws Exception {
		try ( NetCDFReader reader = NetCDFReader.readDataset( url ) ){
			Assertions.assertEquals( 144, reader.getDimensionLength( DimensionName.TIME ) );
			Assertions.assertEquals( 441, reader.getDimensionLength( DimensionName.X ) );
			Assertions.assertEquals( 561, reader.getDimensionLength( DimensionName.Y ) );
		}
	}
	
	@Test
	public void testRead() throws Exception {
		try ( NetCDFReader reader = NetCDFReader.read( url )){
			Assertions.assertAll( "Get All",
					() -> Assertions.assertFalse( reader.getGlobalAttributes().isEmpty() ),
					() -> Assertions.assertFalse( reader.getDimensions().isEmpty() ),
					() -> Assertions.assertFalse( reader.getVariables().isEmpty() )
			);

			Assertions.assertAll( "Find",
					() -> Assertions.assertTrue( reader.findGlobalAttribute( "references" ).isPresent() ),
					() -> Assertions.assertTrue( reader.findDimension( "time" ).isPresent() ),
					() -> Assertions.assertTrue( reader.findVariable( "precipitation_radar" ).isPresent() )
			);

			Assertions.assertAll( "Has",
					() -> Assertions.assertTrue( reader.hasGlobalAttribute( "references" ) ),
					() -> Assertions.assertTrue( reader.hasDimension( "time" ) ),
					() -> Assertions.assertTrue( reader.hasVariable( "x" ) ),
					() -> Assertions.assertTrue( reader.hasVariable( "y" ) ),
					() -> Assertions.assertTrue( reader.hasVariable( "precipitation_radar" ) )
			);

			Assertions.assertAll( "Read",
					() -> Assertions.assertTrue( reader.readVariable( "time" ).isPresent() ),
					() -> Assertions.assertTrue( reader.readVariable( "x" ).isPresent() ),
					() -> Assertions.assertTrue( reader.readVariable( "y" ).isPresent() ),
					() -> Assertions.assertTrue( reader.readVariable( "precipitation_radar" ).isPresent() )
			);
		}
	}

	@Test
	public void testReadFirstValue() throws Exception {
		try ( NetCDFReader reader = NetCDFReader.read( url )){
			Assertions.assertTrue( reader.findFirstX().isPresent() );
			Assertions.assertTrue( reader.findFirstY().isPresent() );

			reader.findFirstX().ifPresent( value -> Assertions.assertEquals( new BigDecimal( "118.00625" ), value ) );
			reader.findFirstY().ifPresent( value -> Assertions.assertEquals( new BigDecimal( "19.99375" ), value ) );
		}
	}

	@Test
	public void testReadLastValue() throws Exception {
		try ( NetCDFReader reader = NetCDFReader.read( url )){
			Assertions.assertTrue( reader.findLastX().isPresent() );
			Assertions.assertTrue( reader.findLastY().isPresent() );

			reader.findLastX().ifPresent( value -> Assertions.assertEquals( new BigDecimal( "123.50625" ), value ) );
			reader.findLastY().ifPresent( value -> Assertions.assertEquals( new BigDecimal( "26.99375" ), value ) );
		}
	}

	@Test
	public void testFindCoordinates() throws Exception {
		try ( NetCDFReader reader = NetCDFReader.read( url )){
			Optional<List<BigDecimal>> optionalY = reader.findYCoordinates();
			Optional<List<BigDecimal>> optionalX = reader.findXCoordinates();

			Assertions.assertTrue( optionalY.isPresent() );
			Assertions.assertTrue( optionalX.isPresent() );

			optionalY.ifPresent( y -> {
				reader.findFirstY().ifPresent( firstY -> Assertions.assertEquals( firstY, y.get( 0 ) ) );
				reader.findLastY().ifPresent( lastY -> Assertions.assertEquals( lastY, y.get( y.size() - 1 ) ) );
			} );

			optionalX.ifPresent( x -> {
				reader.findFirstX().ifPresent( firstX -> Assertions.assertEquals( firstX, x.get( 0 ) ) );
				reader.findLastX().ifPresent( lastX -> Assertions.assertEquals( lastX, x.get( x.size() - 1 ) ) );
			} );
		}

		try ( NetCDFReader reader = NetCDFReader.read( "src/test/resources/Tide_6M_CWB.nc" )){
			Optional<List<BigDecimal>> optionalY = reader.findLatCoordinates();
			Optional<List<BigDecimal>> optionalX = reader.findLonCoordinates();

			Assertions.assertTrue( optionalY.isPresent() );
			Assertions.assertTrue( optionalX.isPresent() );
		}
	}

	@Test
	public void testFindStationId() throws Exception {
		String url = "src/test/resources/Tide_6M_CWB.nc";

		Path path = Paths.get( url );
		Assertions.assertTrue( Files.exists( path ) );

		try ( NetCDFReader reader = NetCDFReader.read( url ) ){
			Optional<List<String>> optional = reader.findStationIds();
			Assertions.assertTrue( optional.isPresent() );

			optional.ifPresent( ids -> {
				Assertions.assertEquals( "1102", ids.get( 0 ) );
				Assertions.assertEquals( "1116", ids.get( 1 ) );
			} );
		}
	}
}
