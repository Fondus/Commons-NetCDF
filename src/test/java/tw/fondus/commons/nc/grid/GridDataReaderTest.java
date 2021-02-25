package tw.fondus.commons.nc.grid;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The unit test of grid data set reader.
 * 
 * @author Brad Chen
 *
 */
public class GridDataReaderTest {
	private static final String url = "src/test/resources/gfs.t00z.pgrb2.0p25.anl";

	@BeforeAll
	public static void setUp() {
		Path path = Paths.get( url );
		Assertions.assertTrue( Files.exists( path ) );
	}

	@Test
	public void testBottom() throws Exception {
		try ( GridDataReader reader = GridDataReader.read( url ) ){
			Assertions.assertNotNull( reader.getNetCDF() );
			Assertions.assertNotNull( reader.getDataset() );
			Assertions.assertEquals( url, reader.getPath() );
		}
	}
	
	@Test
	public void testReader() throws Exception {
		try ( GridDataReader reader = GridDataReader.read( url )){
			Assertions.assertAll( "Get All",
					() -> Assertions.assertFalse( reader.getGlobalAttributes().isEmpty() ),
					() -> Assertions.assertFalse( reader.getVariables().isEmpty() ),
					() -> Assertions.assertFalse( reader.getGridDataTypes().isEmpty() )
			);

			Assertions.assertAll( "Find",
					() -> Assertions.assertTrue( reader.findGlobalAttribute( "file_format" ).isPresent() ),
					() -> Assertions.assertTrue( reader.findVariable( "Temperature_height_above_ground" ).isPresent() ),
					() -> Assertions.assertTrue( reader.findGridDataType( "Temperature_height_above_ground" ).isPresent() )
			);

			Assertions.assertAll( "Has",
					() -> Assertions.assertTrue( reader.hasGlobalAttribute( "file_format" ) ),
					() -> Assertions.assertTrue( reader.hasVariable( "Temperature_sigma" ) ),
					() -> Assertions.assertTrue( reader.hasVariable( "Temperature_height_above_ground" ) ),
					() -> Assertions.assertTrue( reader.hasGridDataType( "Temperature_sigma" ) ),
					() -> Assertions.assertTrue( reader.hasGridDataType( "Temperature_height_above_ground" ) )
			);

			Assertions.assertAll( "Grid specific methods",
					() -> Assertions.assertTrue( reader.getBoundingBox().isPresent() ),
					() -> Assertions.assertTrue( reader.getDateStart().isPresent() ),
					() -> Assertions.assertTrue( reader.getDateEnd().isPresent() )
			);
		}
	}
}
