package com.example.unitconverter;

public record ConversionResult(String sourceUnit, double sourceValue, String targetUnit, double convertedValue) {
}
