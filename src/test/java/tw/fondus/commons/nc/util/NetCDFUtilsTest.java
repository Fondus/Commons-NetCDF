package tw.fondus.commons.nc.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tw.fondus.commons.nc.NetCDFReader;
import tw.fondus.commons.nc.util.key.VariableAttribute;
import ucar.ma2.Array;
import ucar.ma2.Index;

/**
 * The unit test of NetCDF with tools.
 * 
 * @author Brad Chen
 *
 */
public class NetCDFUtilsTest {
	private final String url = "src/test/resources/QPESUMS_QPE.nc";
	private Path path;
	
	@Before
	public void setUp() throws Exception {
		this.path = Paths.get( this.url );

		Assert.assertTrue( Files.exists( this.path ) );
	}
	
	@Test
	public void test() throws Exception {
		try ( NetCDFReader reader = NetCDFReader.read( this.url );){
			Assert.assertTrue( reader.findVariable( "precipitation_radar" ).isPresent() );
			reader.findVariable( "precipitation_radar" ).ifPresent( variable -> {
				
				Number scale = NetCDFUtils.readNumberFromVariableAttribute( variable, VariableAttribute.KEY_SCALE, 1 );
				Number offset = NetCDFUtils.readNumberFromVariableAttribute( variable, VariableAttribute.KEY_OFFSET, 0 );
				Number missing = NetCDFUtils.readNumberFromVariableAttribute( variable, VariableAttribute.KEY_MISSINGVALUE, -999f );
				
				Assert.assertTrue( scale.equals( 0.25f ) );
				Assert.assertTrue( offset.equals( 0.0f ) );
				
				try {
					Array values = variable.read();
					Index index = values.getIndex();
					
					BigDecimal value = NetCDFUtils.readArrayValue( values, index.set( 0, 0, 0 ), scale, offset, missing );
					Assert.assertTrue( value.compareTo( BigDecimal.ZERO ) == 0 );
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		};
	}
}
