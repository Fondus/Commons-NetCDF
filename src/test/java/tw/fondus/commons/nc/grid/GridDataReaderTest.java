package tw.fondus.commons.nc.grid;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * The unit test of grid data set reader.
 * 
 * @author Brad Chen
 *
 */
public class GridDataReaderTest {
	private final String url = "src/test/resources/gfs.t00z.pgrb2.0p25.anl";
	private Path path;

	@Before
	public void setUp() throws Exception {
		this.path = Paths.get( this.url );

		Assert.assertTrue( Files.exists( this.path ) );
	}
	
	@Test
	public void testReader() throws Exception {
		try ( GridDataReader reader = GridDataReader.read( this.url ); ){
			/** Get All **/
			Assert.assertTrue( !reader.getGlobalAttributes().isEmpty() );
			Assert.assertTrue( !reader.getVariables().isEmpty() );
			Assert.assertTrue( !reader.getGridDataTypes().isEmpty() );
			
			/** Find **/
			Assert.assertTrue( reader.findGlobalAttribute( "file_format" ).isPresent() );
			Assert.assertTrue( reader.findVariable( "Temperature_height_above_ground" ).isPresent() );
			Assert.assertTrue( reader.findGridDataType( "Temperature_height_above_ground" ).isPresent() );
			
			/** Has **/
			Assert.assertTrue( reader.hasGlobalAttribute( "file_format" ) );
			Assert.assertTrue( reader.hasVariable( "Temperature_sigma" ) );
			Assert.assertTrue( reader.hasVariable( "Temperature_height_above_ground" ) );
			Assert.assertTrue( reader.hasGridDataType( "Temperature_sigma" ) );
			Assert.assertTrue( reader.hasGridDataType( "Temperature_height_above_ground" ) );
			
			/** Grid specific methods **/
			Assert.assertTrue( reader.getBoundingBox().isPresent() );
			Assert.assertTrue( reader.getDateStart().isPresent() );
			Assert.assertTrue( reader.getDateEnd().isPresent() );
			
			reader.getBoundingBox().ifPresent( bbox -> {
				System.out.println( bbox.getLatMin() );
				System.out.println( bbox.getLatMax() );
				System.out.println( bbox.getLonMax() );
				System.out.println( bbox.getLonMin() );
			} );
		}
	}
}
