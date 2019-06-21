package tw.fondus.commons.nc;

import java.util.Optional;
import java.util.function.Function;

/**
 * Abstract reader which contains API to to avoid the null point.
 * 
 * @author Brad Chen
 *
 */
public abstract class AbstractReader implements AutoCloseable {
	protected static final String MESSAGE_NOT_OPEN = "The file not open yet!";
	protected static final String MESSAGE_CANT_OPEN = "The file can't be open.";
	
	/**
	 * The process valid the file is open, if true then apply mapper. <br/>
	 * Otherwise throw the message of not open yet.
	 * 
	 * @param optEntity
	 * @param mapper
	 * @return
	 * @since 0.7.0
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
	 * @since 0.7.0
	 */
	protected <T> T orElseThrow( Optional<T> opt, String message ) {
		return opt.orElseThrow( () -> new NetCDFException( message ) );
	}
}
