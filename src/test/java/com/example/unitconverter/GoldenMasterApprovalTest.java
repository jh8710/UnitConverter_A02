package com.example.unitconverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

class GoldenMasterApprovalTest {
    private static final Path GOLDEN_MASTER_PATH = Path.of("src/test/resources/golden_master_expected.txt");
    private static final List<String> SCENARIOS = List.of("meter:2.5", "feet:1.0", "yard:1.0", "meter:0.0");

    @Test
    void goldenMasterOutputMatchesApprovedBaseline() throws IOException {
        String actual = normalize(captureCurrentOutput());

        if (!Files.exists(GOLDEN_MASTER_PATH) || Boolean.getBoolean("golden.master.update")) {
            Files.createDirectories(GOLDEN_MASTER_PATH.getParent());
            Files.writeString(GOLDEN_MASTER_PATH, actual, StandardCharsets.UTF_8);
            return;
        }

        String expected = normalize(Files.readString(GOLDEN_MASTER_PATH, StandardCharsets.UTF_8));
        assertEquals(expected, actual, () -> buildDiff(expected, actual));
    }

    private String captureCurrentOutput() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try (PrintStream capture = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            printGoldenMasterOutput();
        } finally {
            System.setOut(originalOut);
        }

        return output.toString(StandardCharsets.UTF_8);
    }

    private void printGoldenMasterOutput() {
        UnitConverterService service = new UnitConverterService();

        for (int scenarioIndex = 0; scenarioIndex < SCENARIOS.size(); scenarioIndex++) {
            String input = SCENARIOS.get(scenarioIndex);
            String[] parts = input.split(":", -1);
            String unit = parts[0];
            String originalValue = parts[1];
            double value = Double.parseDouble(originalValue);

            System.out.println("[" + input + "]");
            service.convertAll(unit, value).forEach(result -> System.out.printf(
                    Locale.US,
                    "%s %s = %.6f %s%n",
                    originalValue,
                    unit,
                    result.convertedValue(),
                    result.targetUnit()));

            if (scenarioIndex < SCENARIOS.size() - 1) {
                System.out.println("---");
            }
        }
    }

    private String normalize(String text) {
        return text.replace("\r\n", "\n");
    }

    private String buildDiff(String expected, String actual) {
        String[] expectedLines = expected.split("\n", -1);
        String[] actualLines = actual.split("\n", -1);
        int maxLines = Math.max(expectedLines.length, actualLines.length);
        StringBuilder diff = new StringBuilder("Golden Master mismatch: expected vs actual\n");

        for (int index = 0; index < maxLines; index++) {
            String expectedLine = index < expectedLines.length ? expectedLines[index] : "<missing>";
            String actualLine = index < actualLines.length ? actualLines[index] : "<missing>";

            if (!expectedLine.equals(actualLine)) {
                diff.append("line ")
                        .append(index + 1)
                        .append(System.lineSeparator())
                        .append("- ")
                        .append(expectedLine)
                        .append(System.lineSeparator())
                        .append("+ ")
                        .append(actualLine)
                        .append(System.lineSeparator());
            }
        }

        return diff.toString();
    }
}
