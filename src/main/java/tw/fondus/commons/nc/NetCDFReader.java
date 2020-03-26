package tw.fondus.commons.nc;

import com.google.common.base.Preconditions;
import tw.fondus.commons.nc.util.NetCDFUtils;
import tw.fondus.commons.nc.util.key.DimensionName;
import tw.fondus.commons.nc.util.key.VariableAttribute;
import tw.fondus.commons.nc.util.key.VariableName;
import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * NetCDF reader which contains API to to avoid the null point with read NetCDF.
 * 
 * @author Brad Chen
 *
 */
public class NetCDFReader extends AbstractReader {
	private Optional<NetcdfFile> optNetCDF;

	/**
	 * The constructor.
	 * 
	 * @param netcdf netcdf
	 * @since 0.7.0
	 */
	private NetCDFReader( NetcdfFile netcdf ) {
		this.optNetCDF = Optional.ofNullable( netcdf );
	}

	/**
	 * Open the NetCDF with reader.
	 *
	 * @param path string of file location
	 * @return reader
	 * @throws IOException has IO Exception
	 * @since 1.0.0
	 */
	public static NetCDFReader read( Path path ) throws IOException {
		return read( path.toString() );
	}

	/**
	 * Open the NetCDF with reader.
	 * 
	 * @param path string of file location
	 * @return reader
	 * @throws IOException has IO Exception
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
	 * @param path string of file location
	 * @return reader
	 * @throws IOException has IO Exception
	 * @since 0.7.0
	 */
	public static NetCDFReader readDataset( String path ) throws IOException {
		Preconditions.checkNotNull( path );
		Preconditions.checkState( NetcdfDataset.canOpen( path ), MESSAGE_CANT_OPEN );
		return new NetCDFReader( NetcdfDataset.openDataset( path ) );
	}
	
	@Override
	public NetcdfFile getNetCDF() {
		return this.orElseThrow( this.optNetCDF, MESSAGE_NOT_OPEN );
	}
	
	@Override
	public String getPath() {
		return this.orElseThrow( this.optNetCDF.map( NetcdfFile::getLocation ), MESSAGE_NOT_OPEN );
	}

	@Override
	public List<Attribute> getGlobalAttributes() {
		return this.orElseThrow( this.optNetCDF.map( NetcdfFile::getGlobalAttributes ), MESSAGE_NOT_OPEN );
	}

	/**
	 * Get all dimensions from NetCDF.
	 * 
	 * @return list of dimension
	 */
	public List<Dimension> getDimensions() {
		return this.orElseThrow( this.optNetCDF.map( NetcdfFile::getDimensions ), MESSAGE_NOT_OPEN );
	}

	/**
	 * Get all variables from NetCDF.
	 * 
	 * @return list of variable
	 */
	public List<Variable> getVariables() {
		return this.orElseThrow( this.optNetCDF.map( NetcdfFile::getVariables ), MESSAGE_NOT_OPEN );
	}

	/**
	 * Get the length of dimension.
	 *
	 * @param id id of dimension
	 * @return length of dimension
	 * @since 1.0.0
	 */
	public int getDimensionLength( String id ){
		return this.findDimension( id ).map( dimension -> dimension.getLength() ).orElse( 0 );
	}
	
	@Override
	public Optional<Attribute> findGlobalAttribute( String id ){
		Preconditions.checkNotNull( id );
		return this.validFileOpened( this.optNetCDF,
				nc -> nc.findGlobalAttribute( id ) );
	}
	
	/**
	 * Find the dimension from NetCDF.
	 * 
	 * @param id id of dimension
	 * @return dimension, it's optional
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
	 * @param id id of variable
	 * @return variable, it's optional
	 * @since 0.7.0
	 */
	public Optional<Variable> findVariable( String id ){
		Preconditions.checkNotNull( id );
		return this.validFileOpened( this.optNetCDF,
				nc -> nc.findVariable( id ) );
	}

	/**
	 * Find the time variable values from the NetCDF file with default time factor. <br/>
	 * If NetCDF not contain time variable, will return empty list.
	 *
	 * @return list of time value
	 * @since 1.0.0
	 */
	public List<Long> findTimes(){
		return findTimes( 1 );
	}
	
	/**
	 * Find the time variable values from the NetCDF file with specified time factor. <br/>
	 * If NetCDF not contain time variable, will return empty list.
	 * 
	 * @param constFactor time factor
	 * @return list of time value
	 * @since 0.7.0
	 */
	public List<Long> findTimes( long constFactor ){
		List<Long> times = new ArrayList<>();
		this.findVariable( VariableName.TIME )
			.ifPresent( variable -> {
				try {
					Array array = variable.read();
					IntStream.range( 0, (int) array.getSize() )
						.mapToObj( i -> array.getLong( i ) * constFactor )
						.forEach( time -> times.add( time ) );
				} catch (IOException e) {
					// nothing to do
				}
			} );
		return times;
	}
	
	/**
	 * Read variable value.
	 * 
	 * @param id id of variable
	 * @return array values of variable, it's optional
	 */
	public Optional<Array> readVariable( String id ) {
		Preconditions.checkNotNull( id );
		return this.validFileOpened( this.optNetCDF,
				nc -> {
					try {
						return nc.readSection( id );
					} catch (IOException | InvalidRangeException e) {
						// nothing to do
					}
					return null;
				} );
	}
	
	/**
	 * Check the NetCDF has dimension.
	 * 
	 * @param id id of dimension
	 * @return has dimension or not
	 * @since 0.7.0
	 */
	public boolean hasDimension( String id ) {
		return this.findDimension( id ).isPresent();
	}
	
	/**
	 * Check the NetCDF has variable.
	 * 
	 * @param id id of variable
	 * @return has variable or not
	 * @since 0.7.0
	 */
	public boolean hasVariable( String id ) {
		return this.findVariable( id ).isPresent();
	}
	
	/**
	 * Check the NetCDF has time dimension.
	 * 
	 * @return has time
	 * @since 0.7.0
	 */
	public boolean hasTime() {
		return this.hasDimension( DimensionName.TIME );
	}
	
	/**
	 * Check the NetCDF is two dimension file.
	 *
	 * @return is 2D file
	 * @since 0.7.0
	 */
	public boolean is2D() {
		return ( this.hasDimension( DimensionName.X ) && this.hasDimension( DimensionName.Y ) ) ||
				( this.hasDimension( DimensionName.COL ) && this.hasDimension( DimensionName.ROW ) );
	}
	
	/**
	 * Check the NetCDF is one dimension file.
	 * 
	 * @return is 1D file
	 * @since 0.7.0
	 */
	public boolean is1D() {
		return this.hasDimension( DimensionName.STATION ) || !this.is2D() ;
	}
	
	/**
	 * Check the NetCDF coordinate system is WGS84.
	 * 
	 * @return is WGS84
	 * @since 0.7.0
	 */
	public boolean isWGS84() {
		return this.hasVariable( VariableName.LAT ) ||
				this.findVariable( VariableName.X )
						.map( variable -> NetCDFUtils.readVariableAttribute( variable, VariableAttribute.KEY_NAME_LONG, "" ) )
						.map( attribute -> attribute.contains( VariableAttribute.NAME_X_WGS84 ) )
						.orElse( false );
	}
	
	@Override
	public String toString() {
		return this.orElseThrow( this.optNetCDF.map( NetcdfFile::toString ), MESSAGE_NOT_OPEN );
	}

	@Override
	public void close() {
		this.optNetCDF.ifPresent( nc -> {
			try {
				nc.close();
			} catch (IOException e) {
				// nothing to do
			}
		} );
	}
}
