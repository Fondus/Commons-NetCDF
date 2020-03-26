package tw.fondus.commons.nc;

import tw.fondus.commons.nc.util.NetCDFUtils;
import tw.fondus.commons.nc.util.ValidateUtils;
import ucar.ma2.DataType;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	 * @param path file path.
	 * @return definer
	 * @throws IOException has IO Exception
	 * @since 1.0.0
	 */
	public static NetCDFDefiner create( @Nonnull Path path ) throws IOException {
		return create( path.toString() );
	}

	/**
	 * Create an new NetCDF file with specified version.
	 *
	 * @param path file path
	 * @param isLargeFile is large file
	 * @return definer
	 * @throws IOException has IO Exception
	 * @since 1.0.0
	 */
	public static NetCDFDefiner create( @Nonnull Path path, boolean isLargeFile ) throws IOException {
		return create( path.toString(), isLargeFile );
	}

	/**
	 * Create an new NetCDF file with specified version.
	 *
	 * @param path string of file path
	 * @param isLargeFile is large file
	 * @param version version
	 * @return definer
	 * @throws IOException has IO Exception
	 */
	public static NetCDFDefiner create( @Nonnull Path path, boolean isLargeFile, NetcdfFileWriter.Version version )
			throws IOException {
		return create( path.toString(), isLargeFile, version );
	}

	/**
	 * Create an new NetCDF file with default version.
	 * 
	 * @param path string of file path.
	 * @return definer
	 * @throws IOException has IO Exception
	 */
	public static NetCDFDefiner create( @Nonnull String path ) throws IOException {
		return new NetCDFDefiner().withPath( path ).create();
	}

	/**
	 * Create an new NetCDF file with specified version.
	 * 
	 * @param path string of file path
	 * @param isLargeFile is large file
	 * @return definer
	 * @throws IOException has IO Exception
	 */
	public static NetCDFDefiner create( @Nonnull String path, boolean isLargeFile ) throws IOException {
		return new NetCDFDefiner().withPath( path ).withLargeFile( isLargeFile ).create();
	}

	/**
	 * Create an new NetCDF file with specified version.
	 * 
	 * @param path string of file path
	 * @param isLargeFile is large file
	 * @param version version
	 * @return definer
	 * @throws IOException has IO Exception
	 */
	public static NetCDFDefiner create( @Nonnull String path, boolean isLargeFile, NetcdfFileWriter.Version version )
			throws IOException {
		return new NetCDFDefiner().withPath( path ).withLargeFile( isLargeFile ).create( version );
	}

	/**
	 * Open an existing NetCDF file.
	 *
	 * @param path path of file location
	 * @return writer
	 * @throws IOException has IO Exception
	 * @since 1.0.0
	 */
	public static NetCDFWriter open( @Nonnull Path path ) throws IOException {
		return open( path.toString() );
	}

	/**
	 * Open an existing NetCDF file.
	 * 
	 * @param path path of file location
	 * @return writer
	 * @throws IOException has IO Exception
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
			this.dimensionsMap = new HashMap<>();
		}

		/**
		 * Sets the NetCDF output path of this definer.
		 * 
		 * @param path path of file location
		 * @return definer
		 */
		private NetCDFDefiner withPath( @Nonnull String path ) {
			this.path = path;
			return this;
		}

		/**
		 * Set if this should be a "large file" (64-bit offset) format.<br/>
		 * Only used by netcdf-3.
		 *
		 * @return definer
		 */
		private NetCDFDefiner withLargeFile( boolean isLargeFile ) {
			this.isLargeFile = isLargeFile;
			return this;
		}

		/**
		 * Create an new NetCDF file with default NetCDF 3 version.
		 *
		 * @return definer
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
		 * @param version netcdf version
		 * @return definer
		 * @throws IOException has IO Exception
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
		 * @return writer
		 * @throws IOException has IO Exception
		 */
		private NetCDFWriter open() throws IOException {
			this.writer = NetcdfFileWriter.openExisting( this.path );
			return new NetCDFWriter( this.writer );
		}

		/**
		 * Add global attribute.
		 * 
		 * @param name name of global attribute
		 * @param value value of value
		 * @return definer
		 */
		public NetCDFDefiner addGlobalAttribute( String name, String value ) {
			ValidateUtils.validateDefine( this.writer );
			ValidateUtils.validateGlobalAttribute( this.writer, name, true );
			this.writer.addGroupAttribute( null, NetCDFUtils.createAttribute( name, value ) );
			return this;
		}

		/**
		 * Rename global attribute.
		 * 
		 * @param name name of global attribute
		 * @param newName new name of global attribute
		 * @return definer
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
		 * @param name name of globalAttribute
		 * @return definer
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
		 * @param name name of dimension
		 * @return definer
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
		 * @param name name of dimension
		 * @param size size of dimension
		 * @return definer
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
		 * @param name old name
		 * @param newName new name
		 * @return definer
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
		 * @param name variable name
		 * @param dataType type of data
		 * @return definer
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
		 * @param name variable name
		 * @param dataType type of data
		 * @param dimensions dimensions
		 * @return definer
		 */
		public NetCDFDefiner addVariable( String name, DataType dataType, String... dimensions ) {
			ValidateUtils.validateDefine( this.writer );
			ValidateUtils.validateVariable( this.writer, name, true );
			List<Dimension> list = this.collectDimensionsToList( dimensions );
			this.writer.addVariable( null, name, dataType, list );
			return this;
		}

		/**
		 * Add variable with specified data type.
		 *
		 * @param name variable name
		 * @param dataType type of data
		 * @param dimensions dimensions
		 * @return definer
		 * @since 1.0.0
		 */
		public NetCDFDefiner addVariable( String name, DataType dataType, List<String> dimensions ) {
			ValidateUtils.validateDefine( this.writer );
			ValidateUtils.validateVariable( this.writer, name, true );
			this.writer.addVariable( null, name, dataType, this.validateDimensionList( dimensions ) );
			return this;
		}

		/**
		 * Add string values variable with specified size.
		 * 
		 * @param name variable name
		 * @param stringSize each string max size
		 * @param dimensions dimensions
		 * @return definer
		 */
		public NetCDFDefiner addStringVariable( String name, int stringSize, String... dimensions ) {
			ValidateUtils.validateDefine( this.writer );
			ValidateUtils.validateVariable( this.writer, name, true );
			List<Dimension> list = this.collectDimensionsToList( dimensions );
			this.writer.addStringVariable( null, name, list, stringSize );
			return this;
		}

		/**
		 * Add string values variable with specified size.
		 *
		 * @param name variable name
		 * @param stringSize each string max size
		 * @param dimensions dimensions
		 * @return definer
		 * @since 1.0.0
		 */
		public NetCDFDefiner addStringVariable( String name, int stringSize, List<String> dimensions ) {
			ValidateUtils.validateDefine( this.writer );
			ValidateUtils.validateVariable( this.writer, name, true );
			this.writer.addStringVariable( null, name, this.validateDimensionList( dimensions ), stringSize );
			return this;
		}

		/**
		 * Rename variable.
		 * 
		 * @param name variable name
		 * @param newName new variable name
		 * @return definer
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
		 * @param name variable name
		 * @param key attribute key
		 * @param value attribute value
		 * @return definer
		 */
		public NetCDFDefiner addVariableAttribute( String name, String key, String value ) {
			ValidateUtils.validateDefine( this.writer );
			ValidateUtils.validateVariable( this.writer, name, false );
			this.writer.addVariableAttribute( this.writer.findVariable( name ), NetCDFUtils.createAttribute( key, value ) );
			return this;
		}

		/**
		 * Add variable attribute with number type value.
		 * 
		 * @param name variable name
		 * @param key attribute key
		 * @param value attribute value
		 * @return definer
		 */
		public NetCDFDefiner addVariableAttribute( String name, String key, Number value ) {
			ValidateUtils.validateDefine( this.writer );
			ValidateUtils.validateVariable( this.writer, name, false );
			this.writer.addVariableAttribute( this.writer.findVariable( name ), NetCDFUtils.createAttribute( key, value ) );
			return this;
		}

		/**
		 * Delete variable attribute.
		 * 
		 * @param name variable name
		 * @param key attribute key
		 * @return definer
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
		 * @param name variable name
		 * @param key old attribute key
		 * @param newKey new attribute key
		 * @return definer
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
		 * @return writer
		 * @throws IOException has IO Exception
		 */
		public NetCDFWriter build() throws IOException {
			this.writer.create();
			return new NetCDFWriter( this.writer );
		}

		/**
		 * Validate has dimensions and collect to list.
		 * 
		 * @param dimensions string of dimensions
		 * @return list of dimension
		 */
		private List<Dimension> collectDimensionsToList( String... dimensions ) {
			return validateDimensionList( Stream.of( dimensions ).collect( Collectors.toList() ) );
		}

		/**
		 * Validate has dimensions and collect to list.
		 *
		 * @param dimensions string of dimensions
		 * @return list of dimension
		 * @since 1.0.0
		 */
		private List<Dimension> validateDimensionList( List<String> dimensions ) {
			return dimensions.stream().map( dimension -> {
				ValidateUtils.validateDimension( this.writer, dimension, false );
				return this.dimensionsMap.get( dimension );
			} ).collect( Collectors.toList() );
		}
	}
}
