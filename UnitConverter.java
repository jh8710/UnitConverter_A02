import java.util.Scanner;

public class UnitConverter {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Insert value for converting (ex: meter:2.5): ");
        String input = scanner.nextLine();
        String[] parts = input.split(":");
        String unit = parts[0];
        double value = Double.parseDouble(parts[1]);
        double meterValue = 0;

        if (unit.equals("meter")) {
            meterValue = value;
        } else if (unit.equals("feet")) {
            meterValue = value / 3.28084;
        } else if (unit.equals("yard")) {
            meterValue = value / 1.09361;
        }

        double inMeters = meterValue;
        double inFeet = meterValue * 3.28084;
        double inYards = meterValue * 1.09361;

        System.out.println(value + " " + unit + " = " + inMeters + " meter");
        System.out.println(value + " " + unit + " = " + inFeet + " feet");
        System.out.println(value + " " + unit + " = " + inYards + " yard");
    }
}
