package tw.fondus.commons.nc.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import strman.Strman;
import tw.fondus.commons.nc.NetCDFBuilder;
import tw.fondus.commons.nc.util.key.GlobalAttribute;
import tw.fondus.commons.nc.util.key.VariableAttribute;
import tw.fondus.commons.util.time.TimeUtils;
import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;

/**
 * The unit test of use builder to construct NetCDF.
 * 
 * @author Brad Chen
 *
 */
public class NetCDFBuilderTest {
	private Map<String, Array> valueMap;

	@Before
	public void prepareData() throws IOException {
		Files.deleteIfExists(Paths.get("src/test/resources/test.nc"));
		
		ArrayDouble.D1 y = new ArrayDouble.D1(10);
		ArrayDouble.D1 x = new ArrayDouble.D1(10);
		ArrayDouble.D1 times = new ArrayDouble.D1(10);
		ArrayFloat.D3 rainfall = new ArrayFloat.D3(10, 10, 10);
		
		/** Y **/
		IntStream.range(0, 10).forEach(i -> {
			y.set(i, i);
		});
		
		/** X **/
		IntStream.range(0, 10).forEach(i -> {
			x.set(i, i);
		});
		
		/** Time **/
		DateTime createTime = new DateTime();
		IntStream.range(0, 10).forEach(i -> {
			long time = createTime.plusHours(i).getMillis() / (60  * 1000); // millisseconds to minute
			times.set(i, time);
		});
		
		/** Rainfall **/
		IntStream.range(0, 10).forEach(t -> {
			IntStream.range(0, 10).forEach(j -> {
				IntStream.range(0, 10).forEach(i -> {
					rainfall.set(t, j, i, (float) Math.random());
				});
			});
		});
		
		this.valueMap = new HashMap<String, Array>();
		this.valueMap.put("x", x);
		this.valueMap.put("y", y);
		this.valueMap.put("times", times);
		this.valueMap.put("rainfall", rainfall);
	}

	@Test
	public void test() throws IOException, InvalidRangeException {
		DateTime createTime = new DateTime();

		NetCDFBuilder.create(new File("src/test/resources/test.nc").getPath())
				.addGlobalAttribute(GlobalAttribute.CONVENTIONS, "CF-1.6")
				.addGlobalAttribute(GlobalAttribute.TITLE, "Test Data")
				.addGlobalAttribute(GlobalAttribute.INSTITUTION, "FondUS")
				.addGlobalAttribute(GlobalAttribute.SOURCE, "Export NETCDF-CF_GRID from FEWS-Taiwan")
				.addGlobalAttribute(GlobalAttribute.HISTORY, Strman
						.append(TimeUtils.toString(createTime, TimeUtils.YMDHMS), " GMT: exported from FEWS-Taiwan"))
				.addGlobalAttribute(GlobalAttribute.REFERENCES, "http://www.delft-fews.com")
				.addGlobalAttribute(GlobalAttribute.METADATA_CONVENTIONS, "Unidata Dataset Discovery v1.0")
				.addGlobalAttribute(GlobalAttribute.SUMMARY, "Data exported from FEWS-Taiwan")
				.addGlobalAttribute(GlobalAttribute.DATE_CREATE,
						Strman.append(TimeUtils.toString(createTime, TimeUtils.YMDHMS), " GMT"))
				.addDimension("time", 10)
				.addDimension("y", 10)
				.addDimension("x", 10)
				.addVariable("time", DataType.DOUBLE, new String[] { "time" })
				.addVariableAttribute("time", VariableAttribute.KEY_NAME, "time")
				.addVariableAttribute("time", VariableAttribute.KEY_NAME_LONG, "time")
				.addVariableAttribute("time", VariableAttribute.KEY_UNITS, VariableAttribute.UNITS_TIME)
				.addVariableAttribute("time", VariableAttribute.KEY_AXIS, VariableAttribute.AXIS_TIME)
				.addVariable("y", DataType.DOUBLE, new String[] { "y" })
				.addVariableAttribute("y", VariableAttribute.KEY_NAME, VariableAttribute.COORDINATES_Y_WGS84)
				.addVariableAttribute("y", VariableAttribute.KEY_NAME_LONG, VariableAttribute.NAME_Y_TWD97)
				.addVariableAttribute("y", VariableAttribute.KEY_UNITS, VariableAttribute.UNITS_Y_WGS84)
				.addVariableAttribute("y", VariableAttribute.KEY_AXIS, VariableAttribute.AXIS_Y)
				.addVariableAttribute("y", VariableAttribute.KEY_MISSINGVALUE,
						VariableAttribute.MISSINGVALUE_COORDINATES)
				.addVariable("x", DataType.DOUBLE, new String[] { "x" })
				.addVariableAttribute("x", VariableAttribute.KEY_NAME, VariableAttribute.COORDINATES_X_WGS84)
				.addVariableAttribute("x", VariableAttribute.KEY_NAME_LONG, VariableAttribute.NAME_X_TWD97)
				.addVariableAttribute("x", VariableAttribute.KEY_UNITS, VariableAttribute.UNITS_X_WGS84)
				.addVariableAttribute("x", VariableAttribute.KEY_AXIS, VariableAttribute.AXIS_X)
				.addVariableAttribute("x", VariableAttribute.KEY_MISSINGVALUE,
						VariableAttribute.MISSINGVALUE_COORDINATES)
				.addVariable("rainfall", DataType.FLOAT, new String[] { "time", "y", "x" })
				.addVariableAttribute("rainfall", VariableAttribute.KEY_NAME_LONG, "Rainfall")
				.addVariableAttribute("rainfall", VariableAttribute.KEY_UNITS, "mm")
				.addVariableAttribute("rainfall", VariableAttribute.KEY_MISSINGVALUE, VariableAttribute.MISSINGVALUE)
				.build() // Finished NetCDF file structures define mode
				.writeValues("time", this.valueMap.get("times"))
				.writeValues("y", this.valueMap.get("y"))
				.writeValues("x", this.valueMap.get("x"))
				.writeValues("rainfall", this.valueMap.get("rainfall"))
				.close(); // close IO
	}
}
