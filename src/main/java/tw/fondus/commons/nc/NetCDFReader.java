package tw.fondus.commons.nc;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Preconditions;

import tw.fondus.commons.nc.util.CommonsUtils;
import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

/**
 * NetCDF reader which contains API to to avoid the null point with read NetCDF.
 * 
 * @author Brad Chen
 *
 */
public class NetCDFReader implements AutoCloseable {
	private Optional<NetcdfFile> optNetCDF;
	
	/**
	 * Deprecated at version 0.7.0.
	 */
	@Deprecated
	public NetCDFReader() {
		this( null );
	}
	
	/**
	 * The constructor.
	 * 
	 * @param netcdf
	 * @since 0.7.0
	 */
	private NetCDFReader( NetcdfFile netcdf ) {
		this.optNetCDF = Optional.ofNullable( netcdf );
	}
	
	/**
	 * Open the NetCDF with reader.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 * @since 0.7.0
	 */
	public static NetCDFReader read( String path ) throws IOException {
		Preconditions.checkState( NetcdfDataset.canOpen( path ), "The NetCDF file can't be open." );
		return new NetCDFReader( NetcdfDataset.openFile( path, null ) );
	}
	
	/**
	 * Open the data set through the netCDF API, with reader.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 * @since 0.7.0
	 */
	public static NetCDFReader readDataset( String path ) throws IOException {
		Preconditions.checkState( NetcdfDataset.canOpen( path ), "The NetCDF file can't be open." );
		return new NetCDFReader( NetcdfDataset.openDataset( path ) );
	}

	/**
	 * Open the local NetCDF file.
	 * 
	 * @param path
	 * @throws IOException
	 */
	@Deprecated
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
	@Deprecated
	public void openDataSet( String path ) throws IOException {
		Preconditions.checkState( NetcdfDataset.canOpen( path ), "The NetCDF data set can't be open." );

		this.optNetCDF = Optional.ofNullable( NetcdfDataset.openDataset( path ) );
	}

	/**
	 * Print NetCDF meta-information. <br/>
	 * Deprecated, use the {@link #toString()}.
	 */
	@Deprecated
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
		return this.orElseThrow( this.optNetCDF.map( nc -> nc.getGlobalAttributes() ) );
	}

	/**
	 * Get all dimensions.
	 * 
	 * @return
	 */
	public List<Dimension> getDimensions() {
		return this.orElseThrow( this.optNetCDF.map( nc -> nc.getDimensions() ) );
	}

	/**
	 * Get all variables.
	 * 
	 * @return
	 */
	public List<Variable> getVariables() {
		return this.orElseThrow( this.optNetCDF.map( nc -> nc.getVariables() ) );
	}
	
	/**
	 * Read variable value.
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 * @throws InvalidRangeException
	 */
	public Optional<Array> readVariable( String id ) {
		return this.optNetCDF.map( nc -> {
			try {
				return nc.readSection( id );
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidRangeException e) {
				e.printStackTrace();
			}
			return null;
		} );
	}
	
	/**
	 * Get type of variable. <br/>
	 * Deprecated, change to use {@link CommonsUtils#getVariableType(Variable)}.
	 * 
	 * @param variable
	 * @return
	 */
	@Deprecated
	public DataType getVariableType( Variable variable ){
		return variable.getDataType();
	}
	
	@Override
	public String toString() {
		return this.orElseThrow( this.optNetCDF.map( nc -> nc.toString() ) );
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
	
	/**
	 * The process or else throw exception.
	 * 
	 * @param opt
	 * @return
	 */
	private <T> T orElseThrow( Optional<T> opt ) {
		return opt.orElseThrow( () -> new NetCDFException( "The NetCDF not open yet!" ) );
	}
}
