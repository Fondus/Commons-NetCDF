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
import java.math.BigDecimal;
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
	private final Optional<NetcdfFile> optNetCDF;

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
		return this.findDimension( id ).map( Dimension::getLength ).orElse( 0 );
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
						.forEach( times::add );
				} catch (IOException e) {
					// nothing to do
				}
			} );
		return times;
	}

	/**
	 * Find the y coordinates from the NetCDF file.<br/>
	 * The variable read weight is <b>y -> lat</b>.
	 *
	 * @return y coordinates, it's optional
	 * @since 1.1.6
	 */
	public Optional<List<BigDecimal>> findYCoordinates(){
		Optional<List<BigDecimal>> optional = this.findOneDimensionArrayValues( VariableName.Y );
		return optional.isPresent() ? optional : this.findOneDimensionArrayValues( VariableName.LAT );
	}

	/**
	 * Find the lat coordinates from the NetCDF file.
	 *
	 * @return lat coordinates, it's optional
	 * @since 1.1.9
	 */
	public Optional<List<BigDecimal>> findLatCoordinates(){
		return this.findOneDimensionArrayValues( VariableName.LAT );
	}

	/**
	 * Find the x coordinates from the NetCDF file.<br/>
	 * The variable read weight is <b>x -> lon</b>.
	 *
	 * @return x coordinates, it's optional
	 * @since 1.1.6
	 */
	public Optional<List<BigDecimal>> findXCoordinates(){
		Optional<List<BigDecimal>> optional = this.findOneDimensionArrayValues( VariableName.X );
		return optional.isPresent() ? optional : this.findOneDimensionArrayValues( VariableName.LON );
	}

	/**
	 * Find the lon coordinates from the NetCDF file.
	 *
	 * @return lon coordinates, it's optional
	 * @since 1.1.9
	 */
	public Optional<List<BigDecimal>> findLonCoordinates(){
		return this.findOneDimensionArrayValues( VariableName.LON );
	}

	/**
	 * Find the station id values from the NetCDF file.
	 *
	 * @return list of station id, it's optional
	 * @since 1.1.5
	 */
	public Optional<List<String>> findStationIds(){
		return this.findVariable( VariableName.ID_STATION ).map( variable -> {
			try {
				return NetCDFUtils.readStringValues( variable );
			} catch (IOException e) {
				// nothing to do
			}
			return null;
		} );
	}

	/**
	 * Read the Y variable first value, it's usually is most bottom value.<br/>
	 * The variable read weight is <b>y -> lat</b>.
	 *
	 * @return first y value, it's optional
	 * @since 1.1.1
	 */
	public Optional<BigDecimal> findFirstY(){
		if ( this.hasDimension( DimensionName.Y ) ) {
			return this.readFirstValue( VariableName.Y );
		}
		if ( this.hasDimension( DimensionName.ROW ) || this.hasDimension( DimensionName.LAT )  ) {
			return this.readFirstValue( VariableName.LAT );
		}
		return Optional.empty();
	}

	/**
	 * Read the X variable first value, it's usually is most left value. <br/>
	 * The variable read weight is <b>x -> lon</b>.
	 *
	 * @return first x value, it's optional
	 * @since 1.1.1
	 */
	public Optional<BigDecimal> findFirstX(){
		if ( this.hasDimension( DimensionName.X ) ) {
			return this.readFirstValue( VariableName.X );
		}
		if ( this.hasDimension( DimensionName.COL ) || this.hasDimension( DimensionName.LON ) ) {
			return this.readFirstValue( VariableName.LON );
		}
		return Optional.empty();
	}

	/**
	 * Read the Y variable last value, it's usually is most top value. <br/
	 * The variable read weight is <b>y -> lat</b>.
	 *
	 * @return last y value, it's optional
	 * @since 1.1.6
	 */
	public Optional<BigDecimal> findLastY(){
		if ( this.hasDimension( DimensionName.Y ) ) {
			return this.readLastValue( VariableName.Y );
		}
		if ( this.hasDimension( DimensionName.ROW ) || this.hasDimension( DimensionName.LAT ) ) {
			return this.readLastValue( VariableName.LAT );
		}
		return Optional.empty();
	}

	/**
	 * Read the X variable last value, it's usually is most right value. <br/
	 * The variable read weight is <b>x -> lon</b>.
	 *
	 * @return last x value, it's optional
	 * @since 1.1.6
	 */
	public Optional<BigDecimal> findLastX(){
		if ( this.hasDimension( DimensionName.X ) ) {
			return this.readLastValue( VariableName.X );
		}
		if ( this.hasDimension( DimensionName.COL ) || this.hasDimension( DimensionName.LON ) ) {
			return this.readLastValue( VariableName.LON );
		}
		return Optional.empty();
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
				( this.hasDimension( DimensionName.COL ) && this.hasDimension( DimensionName.ROW ) ) ||
				( this.hasDimension( DimensionName.LON ) && this.hasDimension( DimensionName.LAT ) );
	}
	
	/**
	 * Check the NetCDF is one dimension file. <br/>
	 * It's check base has station dimension and is not 2D.
	 * 
	 * @return is 1D file
	 * @since 0.7.0
	 */
	public boolean is1D() {
		return this.hasDimension( DimensionName.STATION ) || !this.is2D() ;
	}
	
	/**
	 * Check the NetCDF coordinate system is WGS84. <br/>
	 * It's check base has X variable and long_name contain WGS 1984.
	 * 
	 * @return is WGS84
	 * @since 0.7.0
	 */
	public boolean isWGS84() {
		return this.findVariable( VariableName.X )
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

	/**
	 * Read the 1D variable first value.
	 *
	 * @param variableName name of variable
	 * @return first value of variable
	 * @since 1.1.1
	 */
	private Optional<BigDecimal> readFirstValue( String variableName ){
		return this.findVariable( variableName )
				.map( variable -> {
					try {
						BigDecimal value = NetCDFUtils.readArrayValue( variable.read(), 0 );
						return value.compareTo( VariableAttribute.MISSING ) == 0 ? null : value;
					} catch (IOException e) {
						return null;
					}
				} );
	}

	/**
	 * Read the 1D variable last value.
	 *
	 * @param variableName name of variable
	 * @return last value of variable
	 * @since 1.1.6
	 */
	private Optional<BigDecimal> readLastValue( String variableName ){
		return this.findVariable( variableName )
				.map( variable -> {
					try {
						BigDecimal value = NetCDFUtils.readArrayValue( variable.read(), variable.getShape(0 ) - 1 );
						return value.compareTo( VariableAttribute.MISSING ) == 0 ? null : value;
					} catch (IOException e) {
						return null;
					}
				} );
	}

	/**
	 * Find the one-dimension values from the NetCDF file.
	 *
	 * @param variableName variable name
	 * @return 1d values, it's optional
	 * @since 1.1.6
	 */
	private Optional<List<BigDecimal>> findOneDimensionArrayValues( String variableName ){
		return this.findVariable( variableName )
				.map( variable -> {
					try {
						Array array = variable.read();
						return NetCDFUtils.readOneDimensionArrayValues( array );
					} catch (IOException e) {
						// nothing to do
					}
					return null;
				} );
	}
}
