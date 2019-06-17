package tw.fondus.commons.nc;

import java.io.IOException;

import javax.annotation.Nonnull;

import tw.fondus.commons.nc.util.ValidateUtils;
import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFileWriter;

/**
 * NetCDF writer is used to write data to the NetCDF with NetCDF file
 * structures.
 * 
 * @author Brad Chen
 *
 */
public class NetCDFWriter {
	private NetcdfFileWriter writer;

	public NetCDFWriter(@Nonnull NetcdfFileWriter writer) {
		this.writer = writer;
	}

	/**
	 * Write data to the named variable, data must be same type and rank as
	 * Variable.
	 * 
	 * @param name
	 * @param values
	 * @return
	 * @throws IOException
	 * @throws InvalidRangeException
	 */
	public NetCDFWriter writeValues( String name, Array values ) throws IOException, InvalidRangeException {
		ValidateUtils.validateVariable( this.writer, name, false );

		this.writer.write( this.writer.findVariable( name ), values );
		return this;
	}

	/**
	 * Write data to the named variable, data must be same type and rank as
	 * Variable.<br/>
	 * offset to start writing, ignore the string size dimension.
	 * 
	 * @param name
	 * @param values
	 * @param origin
	 * @return
	 * @throws IOException
	 * @throws InvalidRangeException
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
	 * @throws IOException
	 */
	public void close() throws IOException {
		this.writer.flush();
		this.writer.close();
	}
}
