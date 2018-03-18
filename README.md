# Commons-NetCDF
Commons-NetCDF Library is chaining APIs/Utilities of build processing NetCDF file, it's is the simplest to use chaining APIs to build  and write data to NetCDF file. 

The Commons-NetCDF Library a thin layer on top of the [UCAR unidata NetCDF Java library](https://www.unidata.ucar.edu/software/thredds/current/netcdf-java/) that allows users to define NetCDF structures on builder chaining pattern without check dimensions, variables and attributes relationship, and writing data to the NetCDF file.

The Commons-NetCDF Library comes in two flavors.

- The JRE flavor requires **JDK 1.8** or higher.
- The **UCAR unidata NetCDF Java library** flavor requires **4.6.10** or higher.

## Examples and basic usage
Please have a look at the fully examples:  [Unit test DEMO](src/test/java/tw/fondus/commons/nc/util/NetCDFBuilderTest.java)

Basic usage:
```java
 NetCDFBuilder.create(new File("src/test/resources/test.nc").getPath())
    .addGlobalAttribute(GlobalAttribute.CONVENTIONS, "CF-1.6")
    .addGlobalAttribute(GlobalAttribute.TITLE, "Test Data")
    .addDimension("time", 10)
    .addDimension("y", 10)
    .addDimension("x", 10)
    .addVariable("time", DataType.DOUBLE, new String[] { "time" })
    .addVariableAttribute("time", VariableAttribute.KEY_NAME, "time")
    .addVariableAttribute("time", VariableAttribute.KEY_UNITS, VariableAttribute.UNITS_TIME)
    .addVariableAttribute("time", VariableAttribute.KEY_AXIS, VariableAttribute.AXIS_TIME)
    .addVariable("y", DataType.DOUBLE, new String[] { "y" })
    .addVariableAttribute("y", VariableAttribute.KEY_NAME, VariableAttribute.COORDINATES_Y_84)
    .addVariableAttribute("y", VariableAttribute.KEY_UNITS, VariableAttribute.UNITS_Y_84)
    .addVariableAttribute("y", VariableAttribute.KEY_AXIS, VariableAttribute.AXIS_Y)
    .addVariable("x", DataType.DOUBLE, new String[] { "x" })
    .addVariableAttribute("x", VariableAttribute.KEY_NAME, VariableAttribute.COORDINATES_X_84)
    .addVariableAttribute("x", VariableAttribute.KEY_UNITS, VariableAttribute.UNITS_X_84)
    .addVariableAttribute("x", VariableAttribute.KEY_AXIS, VariableAttribute.AXIS_X)
    .addVariable("rainfall", DataType.FLOAT, new String[] { "time", "y", "x" })
    .addVariableAttribute("rainfall", VariableAttribute.KEY_NAME_LONG, "Rainfall")
    .addVariableAttribute("rainfall", VariableAttribute.KEY_UNITS, "mm")
    .build() // Finished NetCDF file structures define mode
    .writeValues("time", this.valueMap.get("times"))
    .writeValues("y", this.valueMap.get("y"))
    .writeValues("x", this.valueMap.get("x"))
    .writeValues("rainfall", this.valueMap.get("rainfall"))
    .close(); // close IO
```

## License
- The Commons-NetCDF Library are licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)
