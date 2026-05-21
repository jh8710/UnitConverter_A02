# 25_RED 신규 기능 테스트 먼저 작성 보고서 - Prompting

## 1. 세션 목적

이번 세션의 목적은 `UnitConverter_A02` 프로젝트에서 신규 기능을 구현하지 않고, 먼저 실패하는 RED 테스트를 작성하는 것이었다.

대상 기능은 `registerUnit("cubit", 0.4572)`로 동적 단위를 등록한 뒤 `cubit`, `meter`, `feet` 사이의 변환을 검증하는 것이다.

## 2. 사용 프롬프트 기록

### Prompt 1. 신규 기능 RED 테스트 작성 요청

```text
프로젝트: @c:\dev\UnitConverter_A02\ 

신규 기능 RED 테스트를 작성하라. (구현 금지)

기능 설명:
  registerUnit("cubit", 0.4572)  // 1 cubit = 0.4572 meter
  convert("cubit", 1.0, "meter") == 0.4572 (오차 1e-5)
  convert("meter", 1.0, "cubit") == 1.0/0.4572 ≈ 2.1872 (오차 1e-5)
  convert("cubit", 1.0, "feet")  == 0.4572 * 3.28084 ≈ 1.4997 (오차 1e-5)

제약:
  - UnitConverter 클래스 본문 수정 금지 (상속/위임으로만 구현)
  - 음수 비율 등록 시 예외 발생 (비율 > 0 강제)

테스트 케이스 (최소 5개):
  BT-01: registerUnit("cubit", 0.4572) 후 cubit→meter 변환
  BT-02: registerUnit 후 meter→cubit 역변환
  BT-03: registerUnit 후 cubit→feet 교차 변환
  BT-04: 음수 비율 등록 → IllegalArgumentException 발생
  BT-05: registerUnit 후 convertAll("cubit", 1.0) → 전 단위 반환
  BT-06: 기존 meter→feet 결과 불변 (회귀 보호)

테스트 마킹: @Tag("bonus") @Test void decayingUnit_conversion()

산출물: DecayingUnitConverter.java + DecayingUnitConverterTest.java (실패 상태)
```

### Prompt 2. 세션 보고서 및 Prompting 파일 내보내기 요청

```text
이번세션에서 진행한 내용을 report 폴더의 25_RED: 신규 기능 테스트 먼저 작성 _보고서 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더의 25_RED: 신규 기능 테스트 먼저 작성 _보고서-Prompting.md 파일로 내보내줘
```

## 3. 작업 응답 요약

Prompt 1에 대해서는 먼저 프로젝트 구조와 기존 테스트 패턴을 확인했다.

확인한 주요 파일은 다음과 같다.

- `src/main/java/UnitConverter.java`
- `UnitConverter.java`
- `src/main/java/com/example/unitconverter/UnitConverterService.java`
- `src/main/java/com/example/unitconverter/ConversionResult.java`
- `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`
- `src/test/java/com/example/unitconverter/UnitConverterServiceTest.java`
- `pom.xml`

확인 결과 실제 Maven 빌드 대상의 `UnitConverter`는 기본 패키지의 `src/main/java/UnitConverter.java`에 있었고, 기존 서비스 계층은 `com.example.unitconverter` 패키지에 있었다.

## 4. RED 테스트 작성 기록

`UnitConverter` 본문 수정 금지 제약을 지키기 위해 `DecayingUnitConverter`를 `UnitConverter` 상속 스텁으로 추가했다.

추가 파일은 다음과 같다.

- `src/main/java/DecayingUnitConverter.java`
- `src/test/java/DecayingUnitConverterTest.java`

`DecayingUnitConverter`는 테스트 컴파일을 위한 최소 API만 제공했다.

- `registerUnit(String unit, double metersPerUnit)`
- `convertAll(String sourceUnit, double value)`

두 메서드는 실제 기능을 구현하지 않고 `UnsupportedOperationException`을 던지도록 했다.

## 5. 작성한 테스트 목록

`DecayingUnitConverterTest`에 `@Tag("bonus")`가 붙은 테스트 6개를 작성했다.

- `decayingUnit_conversion`
- `decayingUnit_reverseConversion`
- `decayingUnit_crossConversionToFeet`
- `decayingUnit_negativeRatioThrowsIllegalArgumentException`
- `decayingUnit_convertAllReturnsEveryRegisteredUnit`
- `decayingUnit_existingMeterToFeetResultDoesNotChange`

첫 번째 테스트는 사용자 요청 마킹을 그대로 반영했다.

```java
@Tag("bonus")
@Test
void decayingUnit_conversion()
```

## 6. 테스트 실행 기록

RED 상태 확인을 위해 전체 테스트를 실행했다.

```shell
mvn test
```

결과는 실패였다.

```text
Tests run: 57, Failures: 10, Errors: 4, Skipped: 0
BUILD FAILURE
```

신규 테스트 클래스의 결과는 다음과 같았다.

```text
DecayingUnitConverterTest
Tests run: 6, Failures: 1, Errors: 4, Skipped: 0
```

대표 에러는 다음과 같았다.

```text
java.lang.UnsupportedOperationException: RED: dynamic unit registration is not implemented yet
```

음수 비율 등록 테스트는 아직 `IllegalArgumentException` 검증 로직이 없어서 실패했다.

```text
Unexpected exception type thrown, expected: <java.lang.IllegalArgumentException> but was: <java.lang.UnsupportedOperationException>
```

전체 실패에는 기존 `UnitConverterDualTrackRedTest`의 `fail("RED")` placeholder 실패도 포함되어 있었다.

## 7. Linter 확인 기록

신규 Java 파일 2개에 대해 linter를 확인했다.

```text
No linter errors found.
```

## 8. 파일명 처리 기록

사용자가 요청한 파일명에는 `25_RED: 신규 기능 테스트 먼저 작성 _보고서`처럼 콜론(`:`)이 포함되어 있었다.

Windows 파일 시스템에서는 `:`를 파일명에 사용할 수 없기 때문에 실제 저장 파일명에서는 콜론을 제거했다.

- `report/25_RED 신규 기능 테스트 먼저 작성 _보고서.md`
- `Prompting/25_RED 신규 기능 테스트 먼저 작성 _보고서-Prompting.md`

## 9. 최종 산출물

이번 세션의 최종 산출물은 다음과 같다.

- `src/main/java/DecayingUnitConverter.java`
- `src/test/java/DecayingUnitConverterTest.java`
- `report/25_RED 신규 기능 테스트 먼저 작성 _보고서.md`
- `Prompting/25_RED 신규 기능 테스트 먼저 작성 _보고서-Prompting.md`
