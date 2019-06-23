package tw.fondus.commons.nc;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;

/**
 * Abstract reader which contains API to to avoid the null point.
 * 
 * @author Brad Chen
 * @since 0.7.0
 */
public abstract class AbstractReader implements AutoCloseable {
	protected static final String MESSAGE_NOT_OPEN = "The file not open yet!";
	protected static final String MESSAGE_CANT_OPEN = "The file can't be open.";
	
	/**
	 * Get the file location. This is a URL, or a file path.
	 * 
	 * @return
	 */
	public abstract String getPath();
	
	/**
	 * Get the bottom API of NetCDF.
	 * 
	 * @return
	 */
	public abstract NetcdfFile getNetCDF();
	
	/**
	 * Get all global attributes from bottom.
	 * 
	 * @return
	 */
	public abstract List<Attribute> getGlobalAttributes();
	
	/**
	 * Find the global attribute from bottom.
	 * 
	 * @param id
	 * @return
	 */
	public abstract Optional<Attribute> findGlobalAttribute( String id );
	
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
	 * The process valid the file is open, if true then apply mapper. <br/>
	 * Otherwise throw the message of not open yet.
	 * 
	 * @param optEntity
	 * @param mapper
	 * @return
	 */
	protected <T, U> Optional<U> validFileOpened( Optional<T> optEntity, Function<T, U> mapper ) {
		if ( optEntity.isPresent() ) {
			return optEntity.map( mapper );
		} else {
			throw new NetCDFException( MESSAGE_NOT_OPEN );
		}
	}
	
	/**
	 * The process or else throw exception with message.
	 * 
	 * @param opt
	 * @param message
	 * @return
	 */
	protected <T> T orElseThrow( Optional<T> opt, String message ) {
		return opt.orElseThrow( () -> new NetCDFException( message ) );
	}
}
