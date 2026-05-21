public class UnitConverter {
    private static final double FEET_PER_METER = 3.28084;

    public double convert(String sourceUnit, double value, String targetUnit) {
        if ("meter".equals(sourceUnit) && "feet".equals(targetUnit)) {
            return value * FEET_PER_METER;
        }

        throw new IllegalArgumentException("unsupported conversion");
    }
}
