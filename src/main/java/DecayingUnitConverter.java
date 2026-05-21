import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.unitconverter.ConversionResult;

public class DecayingUnitConverter extends UnitConverter {
    private static final double FEET_PER_METER = 3.28084;

    private final Map<String, Double> metersPerUnit = new LinkedHashMap<>();

    public DecayingUnitConverter() {
        metersPerUnit.put("meter", 1.0);
        metersPerUnit.put("feet", 1.0 / FEET_PER_METER);
    }

    public void registerUnit(String unit, double metersPerUnit) {
        validateUnit(unit);
        if (!Double.isFinite(metersPerUnit) || metersPerUnit <= 0) {
            throw new IllegalArgumentException("metersPerUnit must be greater than zero");
        }
        this.metersPerUnit.put(unit, metersPerUnit);
    }

    @Override
    public double convert(String sourceUnit, double value, String targetUnit) {
        double sourceRatio = ratioFor(sourceUnit);
        double targetRatio = ratioFor(targetUnit);

        return value * sourceRatio / targetRatio;
    }

    public List<ConversionResult> convertAll(String sourceUnit, double value) {
        ratioFor(sourceUnit);

        List<ConversionResult> results = new ArrayList<>();
        for (String targetUnit : metersPerUnit.keySet()) {
            if (!targetUnit.equals(sourceUnit)) {
                results.add(new ConversionResult(sourceUnit, value, targetUnit, convert(sourceUnit, value, targetUnit)));
            }
        }
        return results;
    }

    private double ratioFor(String unit) {
        validateUnit(unit);
        Double ratio = metersPerUnit.get(unit);
        if (ratio == null) {
            throw new IllegalArgumentException("unsupported unit: " + unit);
        }
        return ratio;
    }

    private void validateUnit(String unit) {
        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("unit is required");
        }
    }
}
