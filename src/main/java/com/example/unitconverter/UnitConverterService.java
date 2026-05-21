package com.example.unitconverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnitConverterService {
    public static final double FEET_PER_METER = 3.28084;
    public static final double YARD_PER_METER = 1.09361;

    private static final Pattern JSON_OBJECT_PATTERN = Pattern.compile("\\{([^{}]+)}");
    private static final Pattern JSON_UNIT_PATTERN = Pattern.compile("\"unit\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern JSON_METERS_PATTERN = Pattern.compile("\"metersPerUnit\"\\s*:\\s*([-+]?\\d+(?:\\.\\d+)?)");

    private final Map<String, BigDecimal> metersPerUnit = new LinkedHashMap<>();

    public UnitConverterService() {
        loadDefaults();
    }

    public final void loadDefaults() {
        metersPerUnit.clear();
        metersPerUnit.put("meter", BigDecimal.ONE);
        metersPerUnit.put("feet", BigDecimal.ONE.divide(BigDecimal.valueOf(FEET_PER_METER), 20, java.math.RoundingMode.HALF_UP));
        metersPerUnit.put("yard", BigDecimal.ONE.divide(BigDecimal.valueOf(YARD_PER_METER), 20, java.math.RoundingMode.HALF_UP));
    }

    public double convert(String sourceUnit, double sourceValue, String targetUnit) {
        validateFiniteValue(sourceValue);
        validateNonNegative(sourceValue);
        BigDecimal sourceRatio = ratioFor(sourceUnit);
        BigDecimal targetRatio = ratioFor(targetUnit);

        BigDecimal meters = BigDecimal.valueOf(sourceValue).multiply(sourceRatio);
        return meters.divide(targetRatio, 20, java.math.RoundingMode.HALF_UP).doubleValue();
    }

    public List<ConversionResult> convertAll(String sourceUnit, double sourceValue) {
        validateFiniteValue(sourceValue);
        validateNonNegative(sourceValue);
        ratioFor(sourceUnit);

        List<ConversionResult> results = new ArrayList<>();
        for (String targetUnit : metersPerUnit.keySet()) {
            if (!targetUnit.equals(sourceUnit)) {
                results.add(new ConversionResult(sourceUnit, sourceValue, targetUnit, convert(sourceUnit, sourceValue, targetUnit)));
            }
        }
        return results;
    }

    public List<String> convertInputToConsoleLines(String input) {
        ParsedInput parsedInput = parseInput(input);
        DecimalFormat format = new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(Locale.US));

        return convertAll(parsedInput.unit(), parsedInput.value()).stream()
                .map(result -> parsedInput.originalValue() + " " + parsedInput.unit()
                        + " = " + format.format(result.convertedValue()) + " " + result.targetUnit())
                .toList();
    }

    public void registerUnit(String unit, double metersPerUnitValue) {
        validateUnitName(unit);
        validateFiniteValue(metersPerUnitValue);
        if (metersPerUnitValue <= 0) {
            throw new IllegalArgumentException("metersPerUnit must be greater than zero");
        }
        if (metersPerUnit.containsKey(unit)) {
            throw new IllegalArgumentException("duplicate unit: " + unit);
        }
        metersPerUnit.put(unit, BigDecimal.valueOf(metersPerUnitValue));
    }

    public boolean loadConfig(Path configPath) {
        if (!Files.exists(configPath)) {
            return false;
        }

        try {
            String content = Files.readString(configPath);
            Map<String, BigDecimal> parsedUnits = configPath.toString().endsWith(".yaml") || configPath.toString().endsWith(".yml")
                    ? parseYaml(content)
                    : parseJson(content);
            if (parsedUnits.isEmpty()) {
                throw new IllegalArgumentException("configuration contains no units");
            }
            metersPerUnit.clear();
            metersPerUnit.putAll(parsedUnits);
            return true;
        } catch (IOException exception) {
            throw new IllegalArgumentException("configuration could not be read", exception);
        }
    }

    public Map<String, Double> registeredUnits() {
        Map<String, Double> snapshot = new LinkedHashMap<>();
        metersPerUnit.forEach((unit, ratio) -> snapshot.put(unit, ratio.doubleValue()));
        return Collections.unmodifiableMap(snapshot);
    }

    private ParsedInput parseInput(String input) {
        if (input == null || input.chars().filter(ch -> ch == ':').count() != 1) {
            throw new IllegalArgumentException("invalid input format");
        }

        String[] parts = input.split(":", -1);
        String unit = parts[0].trim();
        String valueText = parts[1].trim();
        validateUnitName(unit);
        if (valueText.isEmpty()) {
            throw new IllegalArgumentException("missing value");
        }

        double value;
        try {
            value = Double.parseDouble(valueText);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("invalid number", exception);
        }
        validateFiniteValue(value);
        validateNonNegative(value);
        ratioFor(unit);
        return new ParsedInput(unit, valueText, value);
    }

    private Map<String, BigDecimal> parseJson(String content) {
        Map<String, BigDecimal> parsedUnits = new LinkedHashMap<>();
        Matcher objectMatcher = JSON_OBJECT_PATTERN.matcher(content);
        while (objectMatcher.find()) {
            String objectBody = objectMatcher.group(1);
            Matcher unitMatcher = JSON_UNIT_PATTERN.matcher(objectBody);
            Matcher metersMatcher = JSON_METERS_PATTERN.matcher(objectBody);
            if (unitMatcher.find() || metersMatcher.find()) {
                if (!unitMatcher.reset().find() || !metersMatcher.reset().find()) {
                    throw new IllegalArgumentException("invalid unit configuration");
                }
                parsedUnits.put(unitMatcher.group(1), parsePositiveRatio(metersMatcher.group(1)));
            }
        }
        return parsedUnits;
    }

    private Map<String, BigDecimal> parseYaml(String content) {
        Map<String, BigDecimal> parsedUnits = new LinkedHashMap<>();
        String currentUnit = null;
        for (String rawLine : content.split("\\R")) {
            String line = rawLine.trim();
            if (line.startsWith("- unit:")) {
                currentUnit = line.substring("- unit:".length()).trim();
            } else if (line.startsWith("unit:")) {
                currentUnit = line.substring("unit:".length()).trim();
            } else if (line.startsWith("metersPerUnit:")) {
                if (currentUnit == null || currentUnit.isBlank()) {
                    throw new IllegalArgumentException("missing unit");
                }
                String value = line.substring("metersPerUnit:".length()).trim();
                parsedUnits.put(currentUnit, parsePositiveRatio(value));
                currentUnit = null;
            }
        }
        if (currentUnit != null) {
            throw new IllegalArgumentException("missing metersPerUnit");
        }
        return parsedUnits;
    }

    private BigDecimal parsePositiveRatio(String value) {
        try {
            BigDecimal ratio = new BigDecimal(value);
            if (ratio.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("ratio must be greater than zero");
            }
            return ratio;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("invalid ratio", exception);
        }
    }

    private BigDecimal ratioFor(String unit) {
        validateUnitName(unit);
        BigDecimal ratio = metersPerUnit.get(unit);
        if (ratio == null) {
            throw new IllegalArgumentException("unsupported unit: " + unit);
        }
        return ratio;
    }

    private void validateUnitName(String unit) {
        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("unit is required");
        }
    }

    private void validateNonNegative(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("negative value is not allowed");
        }
    }

    private void validateFiniteValue(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("value must be finite");
        }
    }

    private record ParsedInput(String unit, String originalValue, double value) {
    }
}
