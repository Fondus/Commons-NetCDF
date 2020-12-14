package tw.fondus.commons.nc.vo.index;

/**
 * The value object used to store three dimensional index.
 *
 * @author Brad Chen
 * @since 1.2.0
 */
public class IndexTYX extends IndexYX {
	private int time;

	public IndexTYX( int col, int row, int time ) {
		super( col, row );
		this.time = time;
	}

	public int getTime() {
		return time;
	}

	public void setTime( int time ) {
		this.time = time;
	}
}
