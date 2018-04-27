package tw.fondus.commons.nc;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Preconditions;

import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

/**
 * NetCDF reader which contains API to take care read NetCDF.
 * 
 * @author Brad Chen
 *
 */
public class NetCDFReader implements AutoCloseable {
	private Optional<NetcdfFile> optNetCDF;

	public NetCDFReader() {
		this.optNetCDF = Optional.empty();
	}

	/**
	 * Open the local NetCDF file.
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void open( String path ) throws IOException {
		Preconditions.checkState( NetcdfDataset.canOpen( path ), "The NetCDF file can't be open." );

		this.optNetCDF = Optional.ofNullable( NetcdfDataset.openFile( path, null ) );
	}

	/**
	 * Open the remote NetCDF data set.
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void openDataSet( String path ) throws IOException {
		Preconditions.checkState( NetcdfDataset.canOpen( path ), "The NetCDF data set can't be open." );

		this.optNetCDF = Optional.ofNullable( NetcdfDataset.openDataset( path ) );
	}

	/**
	 * Print NetCDF meta-information.
	 */
	public void print() {
		Preconditions.checkState( this.optNetCDF.isPresent(), "The NetCDF not open yet!" );

		this.optNetCDF.ifPresent( nc -> {
			System.out.println( nc.toString() );
		} );
	}

	/**
	 * Get all global attributes.
	 * 
	 * @return
	 */
	public List<Attribute> getGlobalAttributes() {
		Preconditions.checkState( this.optNetCDF.isPresent(), "The NetCDF not open yet!" );

		return this.optNetCDF.get().getGlobalAttributes();
	}

	/**
	 * Get all dimensions.
	 * 
	 * @return
	 */
	public List<Dimension> getDimensions() {
		Preconditions.checkState( this.optNetCDF.isPresent(), "The NetCDF not open yet!" );

		return this.optNetCDF.get().getDimensions();
	}

	/**
	 * Get all variables.
	 * 
	 * @return
	 */
	public List<Variable> getVariables() {
		Preconditions.checkState( this.optNetCDF.isPresent(), "The NetCDF not open yet!" );

		return this.optNetCDF.get().getVariables();
	}

	/**
	 * Read variable section value.
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 * @throws InvalidRangeException
	 */
	public Array readVariableValue( String id ) throws IOException, InvalidRangeException {
		Preconditions.checkState( this.optNetCDF.isPresent(), "The NetCDF not open yet!" );

		return this.optNetCDF.get().readSection( id );
	}
	
	/**
	 * Read variables value.
	 * 
	 * @param variables
	 * @return
	 * @throws IOException
	 */
	public List<Array> readVariableValues( List<Variable> variables ) throws IOException {
		Preconditions.checkState( this.optNetCDF.isPresent(), "The NetCDF not open yet!" );

		return this.optNetCDF.get().readArrays( variables );
	}
	
	/**
	 * Get type of variable.
	 * 
	 * @param variable
	 * @return
	 */
	public DataType getVariableType( Variable variable ){
		return variable.getDataType();
	}

	@Override
	public void close() throws Exception {
		this.optNetCDF.ifPresent( nc -> {
			try {
				nc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} );
	}

}
