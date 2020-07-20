package tw.fondus.commons.nc;

import tw.fondus.commons.nc.util.ValidateUtils;
import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFileWriter;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * NetCDF writer is used to write data to the NetCDF with NetCDF file
 * structures.
 * 
 * @author Brad Chen
 *
 */
public class NetCDFWriter implements AutoCloseable {
	private final NetcdfFileWriter writer;

	public NetCDFWriter( @Nonnull NetcdfFileWriter writer ) {
		this.writer = writer;
	}

	/**
	 * Write data to the named variable, data must be same type and rank as Variable.
	 * 
	 * @param name name of variable
	 * @param values values of variable
	 * @return writer
	 * @throws IOException has IO Exception
	 * @throws InvalidRangeException has Invalid Range Exception
	 */
	public NetCDFWriter writeValues( String name, Array values ) throws IOException, InvalidRangeException {
		ValidateUtils.validateVariable( this.writer, name, false );
		this.writer.write( this.writer.findVariable( name ), values );
		return this;
	}

	/**
	 * Write data to the named variable, data must be same type and rank as Variable.<br/>
	 * offset to start writing, ignore the string size dimension.
	 * 
	 * @param name name of variable
	 * @param values values of variable
	 * @param origin origin
	 * @return writer
	 * @throws IOException has IO Exception
	 * @throws InvalidRangeException has Invalid Range Exception
	 */
	public NetCDFWriter writeValues( String name, Array values, int[] origin )
			throws IOException, InvalidRangeException {
		ValidateUtils.validateVariable( this.writer, name, false );
		this.writer.write( this.writer.findVariable( name ), origin, values );
		return this;
	}

	/**
	 * Close NetCDF file IO.
	 * 
	 * @throws IOException has IO Exception
	 */
	@Override
	public void close() throws IOException {
		this.writer.flush();
		this.writer.close();
	}
}
