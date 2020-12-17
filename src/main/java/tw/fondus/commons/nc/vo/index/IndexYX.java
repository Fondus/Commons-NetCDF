package tw.fondus.commons.nc.vo.index;

/**
 * The value object used to store two dimensional index.
 *
 * @author Brad Chen
 * @since 1.2.0
 */
public class IndexYX {
	private int col;
	private int row;

	public IndexYX( int col, int row ) {
		this.col = col;
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol( int col ) {
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public void setRow( int row ) {
		this.row = row;
	}
}
