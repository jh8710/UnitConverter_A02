package com.example.unitconverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class GoldenMasterTest {
    private static final Path GOLDEN_MASTER_PATH = Path.of("src/test/resources/golden_master_expected.txt");

    @Tag("golden_master")
    @Test
    void unitConverter_meter_2_5() throws IOException {
        assertGoldenMaster("meter:2.5");
    }

    @Tag("golden_master")
    @Test
    void unitConverter_feet_1_0() throws IOException {
        assertGoldenMaster("feet:1.0");
    }

    @Tag("golden_master")
    @Test
    void unitConverter_yard_1_0() throws IOException {
        assertGoldenMaster("yard:1.0");
    }

    @Tag("golden_master")
    @Test
    void unitConverter_meter_0_0() throws IOException {
        assertGoldenMaster("meter:0.0");
    }

    private void assertGoldenMaster(String input) throws IOException {
        String expected = readExpectedSection(input);
        String actual = captureOutput(input);

        assertEquals(expected, actual, () -> diff(expected, actual));
    }

    private String captureOutput(String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try (PrintStream capture = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            printConversion(input);
        } finally {
            System.setOut(originalOut);
        }

        return normalize(output.toString(StandardCharsets.UTF_8));
    }

    private void printConversion(String input) {
        UnitConverterService service = new UnitConverterService();
        String[] parts = input.split(":", -1);
        String unit = parts[0];
        String originalValue = parts[1];
        double value = Double.parseDouble(originalValue);

        service.convertAll(unit, value).forEach(result -> System.out.printf(
                Locale.US,
                "%s %s = %.6f %s%n",
                originalValue,
                unit,
                result.convertedValue(),
                result.targetUnit()));
    }

    private String readExpectedSection(String sectionName) throws IOException {
        String content = normalize(Files.readString(GOLDEN_MASTER_PATH, StandardCharsets.UTF_8));
        String header = "[" + sectionName + "]";
        int headerStart = content.indexOf(header);
        if (headerStart < 0) {
            throw new IllegalArgumentException("Golden Master section not found: " + header);
        }

        int bodyStart = content.indexOf('\n', headerStart);
        if (bodyStart < 0) {
            return "";
        }

        int nextHeaderStart = content.indexOf("\n[", bodyStart + 1);
        String sectionBody = nextHeaderStart < 0
                ? content.substring(bodyStart + 1)
                : content.substring(bodyStart + 1, nextHeaderStart);

        return ensureTrailingNewline(removeSectionSeparator(sectionBody.strip()));
    }

    private String diff(String expected, String actual) {
        String[] expectedLines = expected.split("\n", -1);
        String[] actualLines = actual.split("\n", -1);
        int maxLines = Math.max(expectedLines.length, actualLines.length);
        StringBuilder builder = new StringBuilder()
                .append("--- expected").append(System.lineSeparator())
                .append("+++ actual").append(System.lineSeparator());

        for (int index = 0; index < maxLines; index++) {
            String expectedLine = index < expectedLines.length ? expectedLines[index] : "<missing>";
            String actualLine = index < actualLines.length ? actualLines[index] : "<missing>";

            if (!expectedLine.equals(actualLine)) {
                builder.append("@@ line ").append(index + 1).append(" @@").append(System.lineSeparator())
                        .append("- ").append(expectedLine).append(System.lineSeparator())
                        .append("+ ").append(actualLine).append(System.lineSeparator());
            }
        }

        return builder.toString();
    }

    private String normalize(String value) {
        return value.replace("\r\n", "\n");
    }

    private String ensureTrailingNewline(String value) {
        return value.endsWith("\n") ? value : value + "\n";
    }

    private String removeSectionSeparator(String value) {
        return value.endsWith("\n---") ? value.substring(0, value.length() - "\n---".length()) : value;
    }
}
