package tw.fondus.commons.nc;

/**
 * The NetCDF exception.
 * 
 * @author Brad Chen
 *
 */
public class NetCDFException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public NetCDFException(String message) {
        this(message, null);
    }

    public NetCDFException(String message, Throwable cause) {
        super(message, cause);
    }
}
