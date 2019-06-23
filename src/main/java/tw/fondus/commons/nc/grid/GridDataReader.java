package tw.fondus.commons.nc.grid;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Preconditions;

import tw.fondus.commons.nc.AbstractReader;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.VariableSimpleIF;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;
import ucar.nc2.time.CalendarDate;
import ucar.unidata.geoloc.LatLonRect;

/**
 * Grid data reader which contains some API to avoid the null point with read grids with geo referencing coordinate systems only.
 * 
 * @author Brad Chen
 * @since 0.7.0
 */
public class GridDataReader extends AbstractReader {
	private Optional<GridDataset> optGrid;
	
	private GridDataReader( GridDataset dataset ) {
		this.optGrid = Optional.ofNullable( dataset );
	}
	
	/**
	 * Open the file contain grid type data with reader.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static GridDataReader read( String path ) throws IOException {
		Preconditions.checkState( NetcdfDataset.canOpen( path ), MESSAGE_CANT_OPEN );
		
		return new GridDataReader( GridDataset.open( path ) );
	}
	
	/**
	 * Get the bottom API of GridDataset.
	 * 
	 * @return
	 */
	public GridDataset getDataset() {
		return this.orElseThrow( this.optGrid, MESSAGE_NOT_OPEN );
	}
	
	@Override
	public NetcdfFile getNetCDF() {
		return this.orElseThrow( this.optGrid.map( dataset -> dataset.getNetcdfFile() ), MESSAGE_NOT_OPEN );
	}
	
	@Override
	public String getPath() {
		return this.orElseThrow( this.optGrid.map( dataset -> dataset.getLocation() ), MESSAGE_NOT_OPEN );
	}
	
	@Override
	public List<Attribute> getGlobalAttributes() {
		return this.orElseThrow( this.optGrid.map( dataset -> dataset.getGlobalAttributes() ), MESSAGE_NOT_OPEN );
	}
	
	/**
	 * Get the list of grid data type from grid data.
	 * 
	 * @return
	 */
	public List<GridDatatype> getGridDataTypes() {
		return this.orElseThrow( this.optGrid.map( dataset -> dataset.getGrids() ), MESSAGE_NOT_OPEN );
	}
	
	/**
	 * Get the list of variable from grid data.
	 * 
	 * @return
	 */
	public List<VariableSimpleIF> getVariables() {
		return this.orElseThrow( this.optGrid.map( dataset -> dataset.getDataVariables() ), MESSAGE_NOT_OPEN );
	}
	
	/**
	 * Get boundingBox for the entire dataset.
	 * 
	 * @return
	 */
	public Optional<LatLonRect> getBoundingBox() {
		return this.validFileOpened( this.optGrid,
				dataset -> dataset.getBoundingBox() );
	}
	
	/**
	 * Get start Calendar date for the entire dataset.
	 * 
	 * @return
	 */
	public Optional<CalendarDate> getDateStart() {
		return this.validFileOpened( this.optGrid,
				dataset -> dataset.getCalendarDateStart() );
	}
	
	/**
	 * Get ending Calendar date for the entire dataset.
	 * 
	 * @return
	 */
	public Optional<CalendarDate> getDateEnd() {
		return this.validFileOpened( this.optGrid,
				dataset -> dataset.getCalendarDateEnd() );
	}
	
	@Override
	public Optional<Attribute> findGlobalAttribute( String id ){
		Preconditions.checkNotNull( id );
		return this.validFileOpened( this.optGrid,
				dataset -> dataset.findGlobalAttributeIgnoreCase( id ) );
	}
	
	/**
	 * Find the grid data type from data set.
	 * 
	 * @param id
	 * @return
	 */
	public Optional<GridDatatype> findGridDataType( String id ){
		Preconditions.checkNotNull( id );
		return this.validFileOpened( this.optGrid,
				dataset -> dataset.findGridDatatype( id ) );
	}
	
	/**
	 * Find the variable from data set.
	 * 
	 * @param id
	 * @return
	 */
	public Optional<VariableSimpleIF> findVariable( String id ){
		Preconditions.checkNotNull( id );
		return this.validFileOpened( this.optGrid,
				dataset -> dataset.getDataVariable( id ) );
	}
	
	/**
	 * Check the has grid data type or not.
	 * 
	 * @param id
	 * @return
	 */
	public boolean hasGridDataType( String id ) {
		return this.findGridDataType( id ).isPresent();
	}
	
	/**
	 * Check the has variable or not.
	 * 
	 * @param id
	 * @return
	 */
	public boolean hasVariable( String id ) {
		return this.findVariable( id ).isPresent();
	}
	
	@Override
	public String toString() {
		return this.orElseThrow( this.optGrid.map( dataset -> dataset.getDetailInfo() ), MESSAGE_NOT_OPEN );
	}
	
	@Override
	public void close() throws Exception {
		this.optGrid.ifPresent( dataSet -> {
			try {
				dataSet.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} );
	}
}
