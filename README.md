# Commons-NetCDF
The Commons-NetCDF Library a thin layer on top of the [UCAR unidata NetCDF Java library](https://www.unidata.ucar.edu/software/thredds/current/netcdf-java/) that allows users simplest to define NetCDF structures on builder chaining pattern without check dimensions, variables and attributes relationship, and writing data to the NetCDF file.

The Commons-NetCDF Library comes in two flavors.

- The JRE flavor requires **JDK 1.8** or higher.
- The **UCAR unidata NetCDF Java library** flavor requires **4.6.10** or higher.

## Examples and basic usage
Please have a look at the fully examples:  [Unit test DEMO](src/test/java/tw/fondus/commons/nc/NetCDFBuilderTest.java)

Basic usage:
```java
 NetCDFBuilder.create( new File( "src/test/resources/test.nc" ).getPath())
    .addGlobalAttribute( GlobalAttribute.CONVENTIONS, "CF-1.6")
    .addGlobalAttribute( GlobalAttribute.TITLE, "Test Data")
    .addDimension( DimensionName.TIME, 10)
    .addDimension( DimensionName.Y, 10)
    .addDimension( DimensionName.X, 10)
    .addVariable( VariableName.TIME, DataType.DOUBLE, new String[] { DimensionName.TIME })
    .addVariableAttribute( VariableName.TIME, VariableAttribute.KEY_NAME, "time")
    .addVariableAttribute( VariableName.TIME, VariableAttribute.KEY_UNITS, VariableAttribute.UNITS_TIME)
    .addVariableAttribute( VariableName.TIME, VariableAttribute.KEY_AXIS, VariableAttribute.AXIS_TIME)
    .addVariable( VariableName.Y, DataType.DOUBLE, new String[] { DimensionName.Y })
    .addVariableAttribute( VariableName.Y, VariableAttribute.KEY_NAME, VariableAttribute.COORDINATES_Y_WGS84)
    .addVariableAttribute( VariableName.Y, VariableAttribute.KEY_UNITS, VariableAttribute.UNITS_Y_WGS84)
    .addVariableAttribute( VariableName.Y, VariableAttribute.KEY_AXIS, VariableAttribute.AXIS_Y)
    .addVariable( VariableName.X, DataType.DOUBLE, new String[] { DimensionName.X })
    .addVariableAttribute( VariableName.X, VariableAttribute.KEY_NAME, VariableAttribute.COORDINATES_X_WGS84)
    .addVariableAttribute( VariableName.X, VariableAttribute.KEY_UNITS, VariableAttribute.UNITS_X_WGS84)
    .addVariableAttribute( VariableName.X, VariableAttribute.KEY_AXIS, VariableAttribute.AXIS_X)
    .addVariable("rainfall", DataType.FLOAT, new String[] { DimensionName.TIME, DimensionName.Y, DimensionName.X })
    .addVariableAttribute("rainfall", VariableAttribute.KEY_NAME_LONG, "Rainfall")
    .addVariableAttribute("rainfall", VariableAttribute.KEY_UNITS, "mm")
    .build() // Finished NetCDF file structures define mode
    .writeValues( VariableName.TIME, this.valueMap.get( DimensionName.TIME ))
    .writeValues( VariableName.Y, this.valueMap.get( DimensionName.Y ))
    .writeValues( VariableName.X, this.valueMap.get( DimensionName.X ))
    .writeValues("rainfall", this.valueMap.get("rainfall"))
    .close(); // close IO
```

## License
- The Commons-NetCDF Library are licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)


## Authors and Contributors
The Commons-Java libraries are developed by the FondUS Technology Co., Ltd. and are maintained by [@Vipcube](https://github.com/Vipcube).
