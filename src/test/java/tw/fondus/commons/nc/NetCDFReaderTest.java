package tw.fondus.commons.nc;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tw.fondus.commons.nc.util.TimeFactor;
import tw.fondus.commons.util.file.PathUtils;

/**
 * The unit test of NerCDF reader.
 * 
 * @author Brad Chen
 *
 */
public class NetCDFReaderTest {
	private final String url = "src/test/resources/QPESUMS_QPE.nc";
	private Path path;
	
	@Before
	public void setUp() throws Exception {
		this.path = Paths.get( this.url );

		Assert.assertTrue( PathUtils.exists( this.path ) );
	}
	
	@Test
	public void testCommons() throws Exception {
		try ( NetCDFReader reader = NetCDFReader.read( this.url );){
			Assert.assertTrue( reader.isWGS84() );
			Assert.assertTrue( reader.hasTime() );
			Assert.assertTrue( reader.is2D() );
			Assert.assertTrue( !reader.is1D() );
		}
	}
	
	@Test
	public void testRead() throws Exception {
		try ( NetCDFReader reader = NetCDFReader.read( this.url );){
			/** Get All **/
			Assert.assertTrue( !reader.getGlobalAttributes().isEmpty() );
			Assert.assertTrue( !reader.getDimensions().isEmpty() );
			Assert.assertTrue( !reader.getVariables().isEmpty() );
			
			/** Find **/
			Assert.assertTrue( reader.findGlobalAttribute( "references" ).isPresent() );
			Assert.assertTrue( reader.findDimension( "time" ).isPresent() );
			Assert.assertTrue( reader.findVariable( "precipitation_radar" ).isPresent() );
			
			/** Has **/
			Assert.assertTrue( reader.hasGlobalAttribute( "references" ) );
			Assert.assertTrue( reader.hasDimension( "time" ) );
			Assert.assertTrue( reader.hasVariable( "x" ) );
			Assert.assertTrue( reader.hasVariable( "y" ) );
			Assert.assertTrue( reader.hasVariable( "precipitation_radar" ) );
			
			/** Read **/
			Assert.assertTrue( reader.readVariable( "time" ).isPresent() );
			Assert.assertTrue( reader.readVariable( "x" ).isPresent() );
			Assert.assertTrue( reader.readVariable( "y" ).isPresent() );
			Assert.assertTrue( reader.readVariable( "precipitation_radar" ).isPresent() );
			Assert.assertTrue( reader.findTimes( TimeFactor.ARCHIVE ).size() > 0 );
		}
	}
}
