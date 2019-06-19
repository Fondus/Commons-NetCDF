# Commons-NetCDF
The Commons-NetCDF Library a thin layer on top of the [UCAR unidata NetCDF Java library](https://www.unidata.ucar.edu/software/thredds/current/netcdf-java/) that allows users simplest to define NetCDF structures on builder chaining pattern without check dimensions, variables and attributes relationship, and writing data to the NetCDF file.

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
    .addDimension( Dimension.TIME, 10)
    .addDimension( Dimension.Y, 10)
    .addDimension( Dimension.X, 10)
    .addVariable( Variable.TIME, DataType.DOUBLE, new String[] { Dimension.TIME })
    .addVariableAttribute( Variable.TIME, VariableAttribute.KEY_NAME, "time")
    .addVariableAttribute( Variable.TIME, VariableAttribute.KEY_UNITS, VariableAttribute.UNITS_TIME)
    .addVariableAttribute( Variable.TIME, VariableAttribute.KEY_AXIS, VariableAttribute.AXIS_TIME)
    .addVariable( Variable.Y, DataType.DOUBLE, new String[] { Dimension.Y })
    .addVariableAttribute( Variable.Y, VariableAttribute.KEY_NAME, VariableAttribute.COORDINATES_Y_WGS84)
    .addVariableAttribute( Variable.Y, VariableAttribute.KEY_UNITS, VariableAttribute.UNITS_Y_WGS84)
    .addVariableAttribute( Variable.Y, VariableAttribute.KEY_AXIS, VariableAttribute.AXIS_Y)
    .addVariable( Variable.X, DataType.DOUBLE, new String[] { Dimension.X })
    .addVariableAttribute( Variable.X, VariableAttribute.KEY_NAME, VariableAttribute.COORDINATES_X_WGS84)
    .addVariableAttribute( Variable.X, VariableAttribute.KEY_UNITS, VariableAttribute.UNITS_X_WGS84)
    .addVariableAttribute( Variable.X, VariableAttribute.KEY_AXIS, VariableAttribute.AXIS_X)
    .addVariable("rainfall", DataType.FLOAT, new String[] { Dimension.TIME, Dimension.Y, Dimension.X })
    .addVariableAttribute("rainfall", VariableAttribute.KEY_NAME_LONG, "Rainfall")
    .addVariableAttribute("rainfall", VariableAttribute.KEY_UNITS, "mm")
    .build() // Finished NetCDF file structures define mode
    .writeValues( Variable.TIME, this.valueMap.get( Dimension.TIME ))
    .writeValues( Variable.Y, this.valueMap.get( Dimension.Y ))
    .writeValues( Variable.X, this.valueMap.get( Dimension.X ))
    .writeValues("rainfall", this.valueMap.get("rainfall"))
    .close(); // close IO
```

## License
- The Commons-NetCDF Library are licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)


## Authors and Contributors
The Commons-Java libraries are developed by the FondUS Technology Co., Ltd. and are maintained by [@Vipcube](https://github.com/Vipcube).
