package tw.fondus.commons.nc.util;

import java.util.List;

import org.junit.Test;

import tw.fondus.commons.nc.NetCDFReader;
import ucar.nc2.Attribute;
import ucar.nc2.Variable;

public class NetCDFReaderTest {

	@Test
	public void test() {
		try ( NetCDFReader reader = new NetCDFReader();){
			
			reader.open( "http:/localhost/thredds/fileServer/Taiwan/Southern/Tainan/Current/CombineMaximum.nc" );
			reader.print();
			
			List<Attribute> atts = reader.getGlobalAttributes();
			atts.forEach( att -> {
				System.out.println( att.getShortName() + "\t" + att.getStringValue() );
			} );
			
			List<Variable> variables = reader.getVariables();
			variables.stream()
				.filter( variable -> variable.getFullName().equals( "depth_below_surface_simulated" ) )
				.forEach( variable -> {
				
				System.out.println( reader.getVariableType( variable ) );
			} );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
