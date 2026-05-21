package com.example.unitconverter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class UnitConverterServiceTest {
    private static final double TOLERANCE = 1e-5;

    @TempDir
    Path tempDir;

    @Test
    void test_normalConversion_meterToFeet_returnsExpectedFeet() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet.
        double value = 2.5;

        // When
        double actual = service.convert("meter", value, "feet");

        // Then
        assertEquals(8.20210, actual, TOLERANCE);
    }

    @Test
    void test_normalConversion_meterToYard_returnsExpectedYard() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet and 1 meter = 1.09361 yard.
        double value = 1.0;

        // When
        double actual = service.convert("meter", value, "yard");

        // Then
        assertEquals(1.09361, actual, TOLERANCE);
    }

    @Test
    void test_normalConversion_feetToMeter_returnsReverseMeter() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet.
        double value = 1.0;

        // When
        double actual = service.convert("feet", value, "meter");

        // Then
        assertEquals(0.30480, actual, TOLERANCE);
    }

    @Test
    void test_normalConversion_yardToMeter_returnsReverseMeter() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet and 1 meter = 1.09361 yard.
        double value = 1.0;

        // When
        double actual = service.convert("yard", value, "meter");

        // Then
        assertEquals(0.91440, actual, TOLERANCE);
    }

    @Test
    void test_normalConversion_convertAllMeter_returnsRegisteredTargetConversions() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet and 1 meter = 1.09361 yard.
        double value = 1.0;

        // When
        List<ConversionResult> results = service.convertAll("meter", value);

        // Then
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(result -> result.targetUnit().equals("feet")
                && Math.abs(result.convertedValue() - 3.28084) <= TOLERANCE));
        assertTrue(results.stream().anyMatch(result -> result.targetUnit().equals("yard")
                && Math.abs(result.convertedValue() - 1.09361) <= TOLERANCE));
    }

    @Test
    void test_boundary_zeroValue_returnsZeroFeet() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet.
        double value = 0.0;

        // When
        double actual = service.convert("meter", value, "feet");

        // Then
        assertEquals(0.0, actual, TOLERANCE);
    }

    @Test
    void test_boundary_largeValue_returnsFiniteFeet() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet.
        double value = 1.0e100;

        // When
        double actual = service.convert("meter", value, "feet");

        // Then
        assertTrue(Double.isFinite(actual));
        assertEquals(value * 3.28084, actual, value * TOLERANCE);
    }

    @Test
    void test_boundary_sixDecimalMeterValue_returnsSixDecimalPrecisionFeet() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet.
        double value = 0.123456;

        // When
        double actual = service.convert("meter", value, "feet");

        // Then
        assertEquals(0.405039, actual, 1e-6);
    }

    @Test
    void test_boundary_sixDecimalFeetValue_returnsSixDecimalPrecisionMeter() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet.
        double value = 3.280840;

        // When
        double actual = service.convert("feet", value, "meter");

        // Then
        assertEquals(1.000000, actual, 1e-6);
    }

    @Test
    void test_boundary_consoleOutput_preservesOriginalValueAndUnit() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet.
        String input = "meter:2.5";

        // When
        List<String> lines = service.convertInputToConsoleLines(input);

        // Then
        assertTrue(lines.stream().anyMatch(line -> line.startsWith("2.5 meter = ")));
    }

    @Test
    void test_exception_invalidFormat_throwsIllegalArgumentException() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet.
        String input = "meter=2.5";

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.convertInputToConsoleLines(input));
    }

    @Test
    void test_exception_negativeValue_throwsIllegalArgumentException() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet.
        String input = "meter:-1.0";

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.convertInputToConsoleLines(input));
    }

    @Test
    void test_exception_unknownUnit_throwsIllegalArgumentException() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet.
        String input = "parsec:1.0";

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.convertInputToConsoleLines(input));
    }

    @Test
    void test_exception_invalidNumber_throwsIllegalArgumentException() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet.
        String input = "meter:abc";

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.convertInputToConsoleLines(input));
    }

    @Test
    void test_exception_malformedDecimal_throwsIllegalArgumentException() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet.
        String input = "meter:2.5.1";

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.convertInputToConsoleLines(input));
    }

    @Test
    void test_dynamicRegistration_registerCubit_convertsCubitToMeter() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet; 1 cubit = 0.4572 meter.
        service.registerUnit("cubit", 0.4572);

        // When
        double actual = service.convert("cubit", 1.0, "meter");

        // Then
        assertEquals(0.4572, actual, TOLERANCE);
    }

    @Test
    void test_dynamicRegistration_registerCubit_convertsMeterToCubit() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet; 1 cubit = 0.4572 meter.
        service.registerUnit("cubit", 0.4572);

        // When
        double actual = service.convert("meter", 1.0, "cubit");

        // Then
        assertEquals(2.18723, actual, TOLERANCE);
    }

    @Test
    void test_dynamicRegistration_convertAllAfterRegister_returnsCubitTarget() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet; 1 cubit = 0.4572 meter.
        service.registerUnit("cubit", 0.4572);

        // When
        List<ConversionResult> results = service.convertAll("meter", 1.0);

        // Then
        assertTrue(results.stream().anyMatch(result -> result.targetUnit().equals("cubit")));
    }

    @Test
    void test_dynamicRegistration_duplicateUnit_throwsIllegalArgumentException() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet.
        service.registerUnit("cubit", 0.4572);

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.registerUnit("cubit", 0.5));
    }

    @Test
    void test_dynamicRegistration_zeroRatio_throwsIllegalArgumentException() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet.
        double invalidRatio = 0.0;

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.registerUnit("cubit", invalidRatio));
    }

    @Test
    void test_configLoad_validJson_loadsRatios() throws IOException {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet; JSON also defines cubit.
        Path config = tempDir.resolve("units.json");
        Files.writeString(config, """
                {
                  "units": [
                    { "unit": "meter", "metersPerUnit": 1.0 },
                    { "unit": "feet", "metersPerUnit": 0.3047999902464003 },
                    { "unit": "yard", "metersPerUnit": 0.9144027578387176 },
                    { "unit": "cubit", "metersPerUnit": 0.4572 }
                  ]
                }
                """);

        // When
        boolean loaded = service.loadConfig(config);

        // Then
        assertTrue(loaded);
        assertEquals(0.4572, service.convert("cubit", 1.0, "meter"), TOLERANCE);
    }

    @Test
    void test_configLoad_validYaml_loadsRatios() throws IOException {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet; YAML also defines cubit.
        Path config = tempDir.resolve("units.yaml");
        Files.writeString(config, """
                units:
                  - unit: meter
                    metersPerUnit: 1.0
                  - unit: feet
                    metersPerUnit: 0.3047999902464003
                  - unit: yard
                    metersPerUnit: 0.9144027578387176
                  - unit: cubit
                    metersPerUnit: 0.4572
                """);

        // When
        boolean loaded = service.loadConfig(config);

        // Then
        assertTrue(loaded);
        assertEquals(2.18723, service.convert("meter", 1.0, "cubit"), TOLERANCE);
    }

    @Test
    void test_configLoad_invalidJson_throwsIllegalArgumentException() throws IOException {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet; JSON is missing metersPerUnit.
        Path config = tempDir.resolve("invalid-units.json");
        Files.writeString(config, """
                {
                  "units": [
                    { "unit": "meter" }
                  ]
                }
                """);

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.loadConfig(config));
    }

    @Test
    void test_configLoad_invalidYaml_throwsIllegalArgumentException() throws IOException {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet; YAML has a nonnumeric ratio.
        Path config = tempDir.resolve("invalid-units.yaml");
        Files.writeString(config, """
                units:
                  - unit: meter
                    metersPerUnit: abc
                """);

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.loadConfig(config));
    }

    @Test
    void test_configLoad_missingPath_keepsDefaultRatios() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet.
        Path missingConfig = tempDir.resolve("missing-units.json");

        // When
        boolean loaded = service.loadConfig(missingConfig);
        Map<String, Double> units = service.registeredUnits();

        // Then
        assertFalse(loaded);
        assertEquals(3.28084, service.convert("meter", 1.0, "feet"), TOLERANCE);
        assertTrue(units.containsKey("meter"));
        assertTrue(units.containsKey("feet"));
        assertTrue(units.containsKey("yard"));
    }

    @Test
    void test_configLoad_emptyJson_throwsIllegalArgumentException() throws IOException {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet; JSON contains no unit entries.
        Path config = tempDir.resolve("empty-units.json");
        Files.writeString(config, "{ \"units\": [] }");

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.loadConfig(config));
    }

    @Test
    void test_configLoad_directoryPath_throwsIllegalArgumentException() throws IOException {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet; config path points to a directory.
        Path configDirectory = Files.createDirectory(tempDir.resolve("config-directory"));

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.loadConfig(configDirectory));
    }

    @Test
    void test_exception_missingValue_throwsIllegalArgumentException() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet.
        String input = "meter:";

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.convertInputToConsoleLines(input));
    }

    @Test
    void test_configLoad_rootYamlUnit_loadsRatios() throws IOException {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet; YAML uses a root-level unit entry.
        Path config = tempDir.resolve("root-unit.yaml");
        Files.writeString(config, """
                unit: meter
                metersPerUnit: 1.0
                """);

        // When
        boolean loaded = service.loadConfig(config);

        // Then
        assertTrue(loaded);
        assertEquals(1.0, service.convert("meter", 1.0, "meter"), TOLERANCE);
    }

    @Test
    void test_configLoad_missingYamlUnit_throwsIllegalArgumentException() throws IOException {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet; YAML has a ratio without a unit.
        Path config = tempDir.resolve("missing-unit.yaml");
        Files.writeString(config, "metersPerUnit: 1.0");

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.loadConfig(config));
    }

    @Test
    void test_configLoad_missingYamlRatio_throwsIllegalArgumentException() throws IOException {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet; YAML has a unit without metersPerUnit.
        Path config = tempDir.resolve("missing-ratio.yaml");
        Files.writeString(config, """
                units:
                  - unit: meter
                """);

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.loadConfig(config));
    }

    @Test
    void test_configLoad_zeroJsonRatio_throwsIllegalArgumentException() throws IOException {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet; JSON has a zero ratio.
        Path config = tempDir.resolve("zero-ratio.json");
        Files.writeString(config, """
                {
                  "units": [
                    { "unit": "meter", "metersPerUnit": 0 }
                  ]
                }
                """);

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.loadConfig(config));
    }

    @Test
    void test_exception_blankUnit_throwsIllegalArgumentException() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet.
        String input = ":2.5";

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.convertInputToConsoleLines(input));
    }

    @Test
    void test_exception_nanValue_throwsIllegalArgumentException() {
        UnitConverterService service = new UnitConverterService();

        // Given: ratio is 1 meter = 3.28084 feet.
        String input = "meter:NaN";

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> service.convertInputToConsoleLines(input));
    }
}
