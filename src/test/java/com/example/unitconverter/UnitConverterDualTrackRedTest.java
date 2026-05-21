package com.example.unitconverter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;

class UnitConverterDualTrackRedTest {
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
        fail("RED");
    }

    @Test
    void ui_unknown_unit_input_throws_illegal_argument_exception() {
        fail("RED");
    }

    @Test
    void ui_output_preserves_original_unit_and_value() {
        fail("RED");
    }

    @Test
    void ui_json_output_request_returns_expected_schema() {
        fail("RED");
    }

    @Test
    void convert_meter_to_feet_returns_correct_ratio() {
        UnitConverterService service = new UnitConverterService();

        assertEquals(8.202100, service.convert("meter", 2.5, "feet"), 1e-5);
    }

    @Test
    void convert_meter_to_yard_returns_correct_ratio() {
        fail("RED");
    }

    @Test
    void convert_all_returns_all_registered_unit_conversions() {
        fail("RED");
    }

    @Test
    void register_unit_allows_conversion_with_new_unit() {
        fail("RED");
    }

    @Test
    void load_config_applies_json_and_yaml_ratios() {
        fail("RED");
    }

    @Test
    void load_config_missing_path_keeps_default_ratios() {
        fail("RED");
    }
}
