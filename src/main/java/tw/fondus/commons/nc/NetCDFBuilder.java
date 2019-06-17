package tw.fondus.commons.nc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import tw.fondus.commons.nc.util.ValidateUtils;
import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriter;

/**
 * NetCDF builder which contains API to take care build NetCDF.
 * 
 * @author Brad Chen
 *
 */
public class NetCDFBuilder {

	/**
	 * Create an new NetCDF file with default version.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static NetCDFDefiner create( @Nonnull String path ) throws IOException {
		return new NetCDFDefiner().withPath( path ).create();
	}

	/**
	 * Create an new NetCDF file with specified version.
	 * 
	 * @param path
	 * @param isLargeFile
	 * @param version
	 * @return
	 * @throws IOException
	 */
	public static NetCDFDefiner create( @Nonnull String path, boolean isLargeFile ) throws IOException {
		return new NetCDFDefiner().withPath( path ).withLargeFile( isLargeFile ).create();
	}

	/**
	 * Create an new NetCDF file with specified version.
	 * 
	 * @param path
	 * @param isLargeFile
	 * @param version
	 * @return
	 * @throws IOException
	 */
	public static NetCDFDefiner create( @Nonnull String path, boolean isLargeFile, NetcdfFileWriter.Version version )
			throws IOException {
		return new NetCDFDefiner().withPath( path ).withLargeFile( isLargeFile ).create( version );
	}

	/**
	 * Open an existing NetCDF file.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static NetCDFWriter open( @Nonnull String path ) throws IOException {
		return new NetCDFDefiner().withPath( path ).open();
	}

	/**
	 * NetCDF definer is used to define the NetCDF file structures.
	 * 
	 * @author Brad Chen
	 *
	 */
	public static class NetCDFDefiner {
		private Map<String, Dimension> dimensionsMap;
		private String path;
		private boolean isLargeFile;
		private NetcdfFileWriter writer;

		public NetCDFDefiner() {
			this.dimensionsMap = new HashMap<String, Dimension>();
		}

		/**
		 * Sets the NetCDF output path of this definer.
		 * 
		 * @param path
		 * @return
		 */
		private NetCDFDefiner withPath( @Nonnull String path ) {
			this.path = path;
			return this;
		}

		/**
		 * Set if this should be a "large file" (64-bit offset) format.<br/>
		 * Only used by netcdf-3.
		 * 
		 * @param path
		 * @return
		 */
		private NetCDFDefiner withLargeFile( boolean isLargeFile ) {
			this.isLargeFile = isLargeFile;
			return this;
		}

		/**
		 * Create an new NetCDF file with default NetCDF 3 version.
		 * 
		 * @param path
		 * @return
		 * @throws IOException
		 */
		private NetCDFDefiner create() throws IOException {
			this.writer = NetcdfFileWriter.createNew( NetcdfFileWriter.Version.netcdf3, this.path );
			this.writer.setLargeFile( this.isLargeFile );
			return this;
		}

		/**
		 * Create an new NetCDF file with specified version.<br/>
		 * Writing to the netCDF-4 file format requires installing the netCDF C
		 * library.
		 * 
		 * @param path
		 * @param version
		 * @return
		 * @throws IOException
		 */
		private NetCDFDefiner create( NetcdfFileWriter.Version version ) throws IOException {
			this.writer = NetcdfFileWriter.createNew( version, this.path );
			if ( version.equals( NetcdfFileWriter.Version.netcdf3 ) ) {
				this.writer.setLargeFile( this.isLargeFile );
			}
			return this;
		}

		/**
		 * Open an existing NetCDF file.
		 * 
		 * @param path
		 * @return
		 * @throws IOException
		 */
		private NetCDFWriter open() throws IOException {
			this.writer = NetcdfFileWriter.openExisting( this.path );
			return new NetCDFWriter( this.writer );
		}

		/**
		 * Add global attribute.
		 * 
		 * @param name
		 * @param value
		 * @return
		 */
		public NetCDFDefiner addGlobalAttribute( String name, String value ) {
			ValidateUtils.validateDefine( this.writer );
			ValidateUtils.validateGlobalAttribute( this.writer, name, true );

			this.writer.addGroupAttribute( null, this.getAttribute( name, value ) );
			return this;
		}

		/**
		 * Rename global attribute.
		 * 
		 * @param name
		 * @param newName
		 * @return
		 */
		public NetCDFDefiner renameGlobalAttribute( String name, String newName ) {
			ValidateUtils.validateDefine( this.writer );
			ValidateUtils.validateGlobalAttribute( this.writer, name, false );

			this.writer.renameGlobalAttribute( null, name, newName );
			return this;
		}

		/**
		 * Delete global attribute.
		 * 
		 * @param name
		 * @return
		 */
		public NetCDFDefiner deleteGlobalAttribute( String name ) {
			ValidateUtils.validateDefine( this.writer );
			ValidateUtils.validateGlobalAttribute( this.writer, name, false );

			this.writer.deleteGroupAttribute( null, name );
			return this;
		}

		/**
		 * Add non-limit dimension.
		 * 
		 * @param name
		 * @return
		 */
		public NetCDFDefiner addUnlimitedDimension( String name ) {
			ValidateUtils.validateDefine( this.writer );
			ValidateUtils.validateDimension( this.writer, name, true );

			Dimension dimension = this.writer.addUnlimitedDimension( name );
			this.dimensionsMap.putIfAbsent( name, dimension );
			return this;
		}

		/**
		 * Add dimension.
		 * 
		 * @param name
		 * @param size
		 * @return
		 */
		public NetCDFDefiner addDimension( String name, int size ) {
			ValidateUtils.validateDefine( this.writer );
			ValidateUtils.validateDimension( this.writer, name, true );

			Dimension dimension = this.writer.addDimension( null, name, size );
			this.dimensionsMap.putIfAbsent( name, dimension );
			return this;
		}

		/**
		 * Rename dimension.
		 * 
		 * @param name
		 * @param newName
		 * @return
		 */
		public NetCDFDefiner renameDimension( String name, String newName ) {
			ValidateUtils.validateDefine( this.writer );
			ValidateUtils.validateDimension( this.writer, name, false );

			this.dimensionsMap.remove( name );

			Dimension dimension = this.writer.renameDimension( null, name, newName );
			this.dimensionsMap.putIfAbsent( newName, dimension );
			return this;
		}

		/**
		 * Add non-dimensions variable with specified data type.
		 * 
		 * @param name
		 * @param dataType
		 * @return
		 */
		public NetCDFDefiner addVariableNonDimensions( String name, DataType dataType ) {
			ValidateUtils.validateDefine( this.writer );
			ValidateUtils.validateVariable( this.writer, name, true );

			this.writer.addVariable( null, name, dataType, "" );
			return this;
		}

		/**
		 * Add variable with specified data type.
		 * 
		 * @param name
		 * @param dataType
		 * @param dimensions
		 * @return
		 */
		public NetCDFDefiner addVariable( String name, DataType dataType, String[] dimensions ) {
			ValidateUtils.validateDefine( this.writer );
			ValidateUtils.validateVariable( this.writer, name, true );

			List<Dimension> list = this.collectDimensionsToList( dimensions );
			this.writer.addVariable( null, name, dataType, list );
			return this;
		}

		/**
		 * Add string values variable with specified size.
		 * 
		 * @param name
		 * @param stringSize
		 * @param dimensions
		 * @return
		 */
		public NetCDFDefiner addStringVariable( String name, int stringSize, String[] dimensions ) {
			ValidateUtils.validateDefine( this.writer );
			ValidateUtils.validateVariable( this.writer, name, true );

			List<Dimension> list = this.collectDimensionsToList( dimensions );
			this.writer.addStringVariable( null, name, list, stringSize );
			return this;
		}

		/**
		 * Rename variable.
		 * 
		 * @param name
		 * @param newName
		 * @return
		 */
		public NetCDFDefiner renameVariable( String name, String newName ) {
			ValidateUtils.validateDefine( this.writer );
			ValidateUtils.validateVariable( this.writer, name, false );

			this.writer.renameVariable( name, newName );
			return this;
		}

		/**
		 * Add variable attribute with string type value.
		 * 
		 * @param name
		 * @param key
		 * @param value
		 * @return
		 */
		public NetCDFDefiner addVariableAttribute( String name, String key, String value ) {
			ValidateUtils.validateDefine( this.writer );
			ValidateUtils.validateVariable( this.writer, name, false );

			this.writer.addVariableAttribute( this.writer.findVariable( name ), this.getAttribute( key, value ) );
			return this;
		}

		/**
		 * Add variable attribute with number type value.
		 * 
		 * @param name
		 * @param key
		 * @param value
		 * @return
		 */
		public NetCDFDefiner addVariableAttribute( String name, String key, Number value ) {
			ValidateUtils.validateDefine( this.writer );
			ValidateUtils.validateVariable( this.writer, name, false );

			this.writer.addVariableAttribute( this.writer.findVariable( name ), this.getAttribute( key, value ) );
			return this;
		}

		/**
		 * Delete variable attribute.
		 * 
		 * @param name
		 * @param key
		 * @return
		 */
		public NetCDFDefiner deleteVariableAttribute( String name, String key ) {
			ValidateUtils.validateDefine( this.writer );
			ValidateUtils.validateVariable( this.writer, name, false );

			this.writer.deleteVariableAttribute( this.writer.findVariable( name ), key );
			return this;
		}

		/**
		 * Rename variable attribute.
		 * 
		 * @param name
		 * @param key
		 * @param newKey
		 * @return
		 */
		public NetCDFDefiner renameVariableAttribute( String name, String key, String newKey ) {
			ValidateUtils.validateDefine( this.writer );
			ValidateUtils.validateVariable( this.writer, name, false );

			this.writer.renameVariableAttribute( this.writer.findVariable( name ), key, newKey );
			return this;
		}

		/**
		 * After you have added all of the Dimensions, Variables, and
		 * Attributes, call build() to actually create the file. You must be in
		 * define mode. After this call, you are no longer in define mode.
		 * 
		 * @return
		 * @throws IOException
		 */
		public NetCDFWriter build() throws IOException {
			this.writer.create();
			return new NetCDFWriter( this.writer );
		}

		/**
		 * Get attribute with string type.
		 * 
		 * @param name
		 * @param value
		 * @return
		 */
		private Attribute getAttribute( String name, String value ) {
			return new Attribute( name, value );
		}

		/**
		 * Get attribute with number type.
		 * 
		 * @param name
		 * @param value
		 * @return
		 */
		private Attribute getAttribute( String name, Number value ) {
			return new Attribute( name, value );
		}

		/**
		 * Validate has dimensions and collect to list.
		 * 
		 * @param dimensions
		 * @return
		 */
		private List<Dimension> collectDimensionsToList( String[] dimensions ) {
			List<Dimension> list = new ArrayList<Dimension>();
			Stream.of( dimensions ).forEach( dimension -> {
				ValidateUtils.validateDimension( this.writer, dimension, false );

				list.add( this.dimensionsMap.get( dimension ) );
			} );
			;

			return list;
		}
	}
}
