import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.example.unitconverter.ConversionResult;

class DecayingUnitConverterTest {
    private static final double TOLERANCE = 1e-5;

    @Tag("bonus")
    @Test
    void decayingUnit_conversion() {
        DecayingUnitConverter converter = new DecayingUnitConverter();

        converter.registerUnit("cubit", 0.4572);

        assertEquals(0.4572, converter.convert("cubit", 1.0, "meter"), TOLERANCE);
    }

    @Tag("bonus")
    @Test
    void decayingUnit_reverseConversion() {
        DecayingUnitConverter converter = new DecayingUnitConverter();

        converter.registerUnit("cubit", 0.4572);

        assertEquals(1.0 / 0.4572, converter.convert("meter", 1.0, "cubit"), TOLERANCE);
    }

    @Tag("bonus")
    @Test
    void decayingUnit_crossConversionToFeet() {
        DecayingUnitConverter converter = new DecayingUnitConverter();

        converter.registerUnit("cubit", 0.4572);

        assertEquals(0.4572 * 3.28084, converter.convert("cubit", 1.0, "feet"), TOLERANCE);
    }

    @Tag("bonus")
    @Test
    void decayingUnit_negativeRatioThrowsIllegalArgumentException() {
        DecayingUnitConverter converter = new DecayingUnitConverter();

        assertThrows(IllegalArgumentException.class, () -> converter.registerUnit("cubit", -0.4572));
    }

    @Tag("bonus")
    @Test
    void decayingUnit_convertAllReturnsEveryRegisteredUnit() {
        DecayingUnitConverter converter = new DecayingUnitConverter();

        converter.registerUnit("cubit", 0.4572);

        List<ConversionResult> results = converter.convertAll("cubit", 1.0);

        assertTrue(results.stream().anyMatch(result -> result.targetUnit().equals("meter")
                && Math.abs(result.convertedValue() - 0.4572) <= TOLERANCE));
        assertTrue(results.stream().anyMatch(result -> result.targetUnit().equals("feet")
                && Math.abs(result.convertedValue() - (0.4572 * 3.28084)) <= TOLERANCE));
    }

    @Tag("bonus")
    @Test
    void decayingUnit_existingMeterToFeetResultDoesNotChange() {
        DecayingUnitConverter converter = new DecayingUnitConverter();

        assertEquals(3.28084, converter.convert("meter", 1.0, "feet"), TOLERANCE);
    }
}
