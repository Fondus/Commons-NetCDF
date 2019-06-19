package tw.fondus.commons.nc;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tw.fondus.commons.nc.NetCDFReader;
import tw.fondus.commons.nc.util.CommonsUtils;
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
	public void test() throws Exception {
		try ( NetCDFReader reader = NetCDFReader.read( this.url );){
			Assert.assertTrue( !reader.getGlobalAttributes().isEmpty() );
			reader.getGlobalAttributes().forEach( att -> {
				System.out.println( att.getShortName() + "\t" + att.getStringValue() );
			} );
			
			Assert.assertTrue( !reader.getDimensions().isEmpty() );
			reader.getDimensions().forEach( dimension -> {
				System.out.println( dimension.getFullName() );
				System.out.println( dimension.getLength() );
			} );
			
			Assert.assertTrue( !reader.getVariables().isEmpty() );
			reader.getVariables().forEach( variable -> {
				System.out.println( CommonsUtils.getVariableType( variable ) );
			} );
			
			Assert.assertTrue( reader.findGlobalAttribute( "references" ).isPresent() );
			Assert.assertTrue( reader.findDimension( "time" ).isPresent() );
			Assert.assertTrue( reader.findVariable( "precipitation_radar" ).isPresent() );
		}
	}

}
