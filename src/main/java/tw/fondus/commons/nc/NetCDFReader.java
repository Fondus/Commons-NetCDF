package tw.fondus.commons.nc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.google.common.base.Preconditions;

import tw.fondus.commons.nc.util.NetCDFUtils;
import tw.fondus.commons.nc.util.key.DimensionName;
import tw.fondus.commons.nc.util.key.VariableName;
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
public class NetCDFReader extends AbstractReader {
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
		Preconditions.checkNotNull( path );
		Preconditions.checkState( NetcdfDataset.canOpen( path ), MESSAGE_CANT_OPEN );
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
		Preconditions.checkNotNull( path );
		Preconditions.checkState( NetcdfDataset.canOpen( path ), MESSAGE_CANT_OPEN );
		return new NetCDFReader( NetcdfDataset.openDataset( path ) );
	}
	
	/**
	 * Get the bottom API of NetCDF.
	 * 
	 * @return
	 * @since 0.7.0
	 */
	public NetcdfFile getNetCDF() {
		return this.orElseThrow( this.optNetCDF, MESSAGE_NOT_OPEN );
	}

	/**
	 * Open the local NetCDF file.
	 * 
	 * @param path
	 * @throws IOException
	 */
	@Deprecated
	public void open( String path ) throws IOException {
		Preconditions.checkState( NetcdfDataset.canOpen( path ), MESSAGE_CANT_OPEN );

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
		Preconditions.checkState( NetcdfDataset.canOpen( path ), MESSAGE_CANT_OPEN );

		this.optNetCDF = Optional.ofNullable( NetcdfDataset.openDataset( path ) );
	}

	/**
	 * Print NetCDF meta-information. <br/>
	 * Deprecated, use the {@link #toString()}.
	 */
	@Deprecated
	public void print() {
		Preconditions.checkState( this.optNetCDF.isPresent(), MESSAGE_NOT_OPEN );

		this.optNetCDF.ifPresent( nc -> {
			System.out.println( nc.toString() );
		} );
	}

	/**
	 * Get all global attributes from NetCDF.
	 * 
	 * @return
	 */
	public List<Attribute> getGlobalAttributes() {
		return this.orElseThrow( this.optNetCDF.map( nc -> nc.getGlobalAttributes() ), MESSAGE_NOT_OPEN );
	}

	/**
	 * Get all dimensions from NetCDF.
	 * 
	 * @return
	 */
	public List<Dimension> getDimensions() {
		return this.orElseThrow( this.optNetCDF.map( nc -> nc.getDimensions() ), MESSAGE_NOT_OPEN );
	}

	/**
	 * Get all variables from NetCDF.
	 * 
	 * @return
	 */
	public List<Variable> getVariables() {
		return this.orElseThrow( this.optNetCDF.map( nc -> nc.getVariables() ), MESSAGE_NOT_OPEN );
	}
	
	/**
	 * Find the global attribute from NetCDF.
	 * 
	 * @param id
	 * @return
	 * @since 0.7.0
	 */
	public Optional<Attribute> findGlobalAttribute( String id ){
		Preconditions.checkNotNull( id );
		return this.validFileOpened( this.optNetCDF,
				nc -> nc.findGlobalAttribute( id ) );
	}
	
	/**
	 * Find the dimension from NetCDF.
	 * 
	 * @param id
	 * @since 0.7.0
	 */
	public Optional<Dimension> findDimension( String id ) {
		Preconditions.checkNotNull( id );
		return this.validFileOpened( this.optNetCDF,
				nc -> nc.findDimension( id ) );
	}
	
	/**
	 * Find the variable from NetCDF.
	 * 
	 * @param id
	 * @return
	 * @since 0.7.0
	 */
	public Optional<Variable> findVariable( String id ){
		Preconditions.checkNotNull( id );
		return this.validFileOpened( this.optNetCDF,
				nc -> nc.findVariable( id ) );
	}
	
	/**
	 * Find the time variable values from the NetCDF file with default minute parameter. <br/>
	 * If NetCDF not contain time variable, will return empty list.
	 * 
	 * @return
	 */
	public List<Long> findTimes(){
		return findTimes( 60000 );
	}
	
	/**
	 * Find the time variable values from the NetCDF file with specified time parameter. <br/>
	 * If NetCDF not contain time variable, will return empty list.
	 * 
	 * @param parameter
	 * @return
	 */
	public List<Long> findTimes( long parameter ){
		List<Long> times = new ArrayList<>();
		this.findVariable( VariableName.TIME )
			.ifPresent( variable -> {
				try {
					Array array = variable.read();
					IntStream.range( 0, (int) array.getSize() )
						.mapToObj( i -> array.getLong( i ) * parameter )
						.forEach( time -> {
							times.add( time );
						} );
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			} );
		return times;
	}
	
	/**
	 * Read variable value.
	 * 
	 * @param id
	 * @return
	 */
	public Optional<Array> readVariable( String id ) {
		Preconditions.checkNotNull( id );
		return this.validFileOpened( this.optNetCDF,
				nc -> {
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
	 * Check the NetCDF has global attribute.
	 * 
	 * @param id
	 * @return
	 */
	public boolean hasGlobalAttribute( String id ) {
		return this.findGlobalAttribute( id ).isPresent();
	}
	
	/**
	 * Check the NetCDF has dimension.
	 * 
	 * @param id
	 * @return
	 */
	public boolean hasDimension( String id ) {
		return this.findDimension( id ).isPresent();
	}
	
	/**
	 * Check the NetCDF has variable.
	 * 
	 * @param id
	 * @return
	 */
	public boolean hasVariable( String id ) {
		return this.findVariable( id ).isPresent();
	}
	
	/**
	 * Check the NetCDF has time dimension.
	 * 
	 * @return
	 */
	public boolean hasTime() {
		return this.hasDimension( DimensionName.TIME );
	}
	
	/**
	 * Check the NetCDF is two dimension file.
	 * 
	 * @param reader
	 * @return
	 */
	public boolean is2D() {
		return ( this.hasDimension( DimensionName.X ) && this.hasDimension( DimensionName.Y ) ) ||
				( this.hasDimension( DimensionName.COL ) && this.hasDimension( DimensionName.ROW ) );
	}
	
	/**
	 * Check the NetCDF is one dimension file.
	 * 
	 * @return
	 */
	public boolean is1D() {
		return this.hasDimension( DimensionName.STATION ) && !this.is2D() ;
	}
	
	/**
	 * Check the NetCDF coordinate system is WGS84.
	 * 
	 * @return
	 */
	public boolean isWGS84() {
		return !this.hasVariable( VariableName.LAT );
	}
	
	@Override
	public String toString() {
		return this.orElseThrow( this.optNetCDF.map( nc -> nc.toString() ), MESSAGE_NOT_OPEN );
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
	 * Get type of variable. <br/>
	 * Deprecated, change to use {@link NetCDFUtils#getVariableType(Variable)}.
	 * 
	 * @param variable
	 * @return
	 */
	@Deprecated
	public DataType getVariableType( Variable variable ){
		return variable.getDataType();
	}
}
