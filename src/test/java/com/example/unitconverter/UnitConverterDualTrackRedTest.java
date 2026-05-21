package com.example.unitconverter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class UnitConverterDualTrackRedTest {
    private static final double TOLERANCE = 1e-5;

    @TempDir
    Path tempDir;

    @Test
    void ui_valid_meter_input_returns_conversion_result() {
        UnitConverterService service = new UnitConverterService();

        List<String> lines = service.convertInputToConsoleLines("meter:2.5");

        assertEquals(2, lines.size());
        assertTrue(lines.contains("2.5 meter = 8.2 feet"));
        assertTrue(lines.contains("2.5 meter = 2.7 yard"));
    }

    @Test
    void ui_missing_colon_input_throws_illegal_argument_exception() {
        UnitConverterService service = new UnitConverterService();

        assertThrows(IllegalArgumentException.class, () -> service.convertInputToConsoleLines("meter=2.5"));
    }

    @Test
    void ui_negative_value_input_throws_illegal_argument_exception() {
        UnitConverterService service = new UnitConverterService();

        assertThrows(IllegalArgumentException.class, () -> service.convertInputToConsoleLines("meter:-1.0"));
    }

    @Test
    void ui_unknown_unit_input_throws_illegal_argument_exception() {
        UnitConverterService service = new UnitConverterService();

        assertThrows(IllegalArgumentException.class, () -> service.convertInputToConsoleLines("parsec:1.0"));
    }

    @Test
    void ui_output_preserves_original_unit_and_value() {
        UnitConverterService service = new UnitConverterService();

        List<String> lines = service.convertInputToConsoleLines("meter:2.5");

        assertTrue(lines.stream().allMatch(line -> line.startsWith("2.5 meter = ")));
    }

    @Disabled("JSON output API is outside the current registerUnit GREEN scope")
    @Test
    void ui_json_output_request_returns_expected_schema() {
    }

    @Test
    void convert_meter_to_feet_returns_correct_ratio() {
        UnitConverterService service = new UnitConverterService();

        assertEquals(8.202100, service.convert("meter", 2.5, "feet"), 1e-5);
    }

    @Test
    void convert_meter_to_yard_returns_correct_ratio() {
        UnitConverterService service = new UnitConverterService();

        assertEquals(1.09361, service.convert("meter", 1.0, "yard"), TOLERANCE);
    }

    @Test
    void convert_all_returns_all_registered_unit_conversions() {
        UnitConverterService service = new UnitConverterService();

        List<ConversionResult> results = service.convertAll("meter", 1.0);

        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(result -> result.targetUnit().equals("feet")
                && Math.abs(result.convertedValue() - 3.28084) <= TOLERANCE));
        assertTrue(results.stream().anyMatch(result -> result.targetUnit().equals("yard")
                && Math.abs(result.convertedValue() - 1.09361) <= TOLERANCE));
    }

    @Test
    void register_unit_allows_conversion_with_new_unit() {
        UnitConverterService service = new UnitConverterService();

        service.registerUnit("cubit", 0.4572);

        assertEquals(0.4572, service.convert("cubit", 1.0, "meter"), TOLERANCE);
    }

    @Test
    void load_config_applies_json_and_yaml_ratios() throws IOException {
        UnitConverterService jsonService = new UnitConverterService();
        UnitConverterService yamlService = new UnitConverterService();
        Path jsonConfig = tempDir.resolve("units.json");
        Path yamlConfig = tempDir.resolve("units.yaml");

        Files.writeString(jsonConfig, """
                {
                  "units": [
                    { "unit": "meter", "metersPerUnit": 1.0 },
                    { "unit": "feet", "metersPerUnit": 0.3047999902464003 },
                    { "unit": "yard", "metersPerUnit": 0.9144027578387176 },
                    { "unit": "cubit", "metersPerUnit": 0.4572 }
                  ]
                }
                """);
        Files.writeString(yamlConfig, """
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

        assertTrue(jsonService.loadConfig(jsonConfig));
        assertTrue(yamlService.loadConfig(yamlConfig));
        assertEquals(0.4572, jsonService.convert("cubit", 1.0, "meter"), TOLERANCE);
        assertEquals(2.18723, yamlService.convert("meter", 1.0, "cubit"), TOLERANCE);
    }

    @Test
    void load_config_missing_path_keeps_default_ratios() {
        UnitConverterService service = new UnitConverterService();

        assertTrue(!service.loadConfig(tempDir.resolve("missing-units.json")));
        assertEquals(3.28084, service.convert("meter", 1.0, "feet"), TOLERANCE);
    }
}
