package tw.fondus.commons.nc;

import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
	 * @return is a URL, or a file path
	 */
	public abstract String getPath();
	
	/**
	 * Get the bottom API of NetCDF.
	 * 
	 * @return bottom NetCDF
	 */
	public abstract NetcdfFile getNetCDF();
	
	/**
	 * Get all global attributes from bottom.
	 * 
	 * @return list of global attribute
	 */
	public abstract List<Attribute> getGlobalAttributes();
	
	/**
	 * Find the global attribute from bottom.
	 * 
	 * @param id id of global attribute
	 * @return global attribute, it's optional
	 */
	public abstract Optional<Attribute> findGlobalAttribute( String id );
	
	/**
	 * Check the NetCDF has global attribute.
	 * 
	 * @param id id of global attribute
	 * @return has global attribute or not
	 */
	public boolean hasGlobalAttribute( String id ) {
		return this.findGlobalAttribute( id ).isPresent();
	}
	
	/**
	 * The process valid the file is open, if true then apply mapper. <br/>
	 * Otherwise throw the message of not open yet.
	 * 
	 * @param optEntity entity instance, it's optional
	 * @param mapper entity mapper
	 * @return mapped type of entity, it's optional
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
	 * @param opt optional
	 * @param message error message
	 * @return optional value
	 */
	protected <T> T orElseThrow( Optional<T> opt, String message ) {
		return opt.orElseThrow( () -> new NetCDFException( message ) );
	}
}
