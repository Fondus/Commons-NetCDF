package tw.fondus.commons.nc.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tw.fondus.commons.nc.NetCDFReader;
import tw.fondus.commons.util.file.PathUtils;
import ucar.nc2.Variable;

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
			reader.getGlobalAttributes().forEach( att -> {
				System.out.println( att.getShortName() + "\t" + att.getStringValue() );
			} );
			
			reader.getDimensions().forEach( dimension -> {
				System.out.println( dimension.getFullName() );
				System.out.println( dimension.getLength() );
			} );
			
			List<Variable> variables = reader.getVariables();
			variables.stream()
				.filter( variable -> variable.getFullName().equals( "precipitation_radar" ) )
				.forEach( variable -> {
				
				System.out.println( CommonsUtils.getVariableType( variable ) );
			} );
		}
	}

}
