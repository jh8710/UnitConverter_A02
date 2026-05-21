# 25_RED 신규 기능 테스트 먼저 작성 보고서

## 1. 작업 개요

이번 세션에서는 `UnitConverter_A02` 프로젝트에 신규 기능을 구현하지 않고, 먼저 실패하는 RED 테스트를 추가했다.

대상 기능은 동적 단위 등록을 통해 `cubit` 단위를 추가하고, 기존 `meter`, `feet` 변환과 함께 상호 변환 및 전체 변환 결과를 검증하는 것이다.

사용자 제약은 다음과 같았다.

- `UnitConverter` 클래스 본문 수정 금지
- 상속/위임 방식만 허용
- 음수 비율 등록 시 `IllegalArgumentException` 발생
- 비율은 `0`보다 커야 함
- 산출물은 `DecayingUnitConverter.java`, `DecayingUnitConverterTest.java`
- 구현은 금지하고 실패 상태로 유지

## 2. 추가한 산출물

이번 세션에서 추가한 파일은 다음과 같다.

- `src/main/java/DecayingUnitConverter.java`
- `src/test/java/DecayingUnitConverterTest.java`

`DecayingUnitConverter`는 기존 `UnitConverter`를 상속하는 최소 스텁으로 작성했다. 신규 API가 테스트에서 컴파일되도록 `registerUnit`, `convertAll` 메서드만 노출했고, 실제 기능은 구현하지 않았다.

`UnitConverter` 본문은 수정하지 않았다.

## 3. 작성한 RED 테스트

`DecayingUnitConverterTest`에는 `@Tag("bonus")`를 붙인 테스트 6개를 추가했다.

- BT-01: `registerUnit("cubit", 0.4572)` 후 `cubit -> meter` 변환
- BT-02: 등록 후 `meter -> cubit` 역변환
- BT-03: 등록 후 `cubit -> feet` 교차 변환
- BT-04: 음수 비율 등록 시 `IllegalArgumentException` 발생
- BT-05: 등록 후 `convertAll("cubit", 1.0)` 호출 시 전체 등록 단위 반환
- BT-06: 기존 `meter -> feet` 결과가 `3.28084`로 유지되는 회귀 보호

요청된 테스트 마킹도 반영했다.

```java
@Tag("bonus")
@Test
void decayingUnit_conversion()
```

## 4. RED 상태 확인

테스트 실행 명령은 다음과 같다.

```shell
mvn test
```

실행 결과는 실패 상태였다.

```text
Tests run: 57, Failures: 10, Errors: 4, Skipped: 0
BUILD FAILURE
```

신규 `DecayingUnitConverterTest`에서는 총 6개 테스트 중 5개가 실패 또는 에러 상태로 확인되었다.

```text
Tests run: 6, Failures: 1, Errors: 4, Skipped: 0
```

대표 실패 원인은 아직 구현하지 않은 스텁 때문이다.

```text
java.lang.UnsupportedOperationException: RED: dynamic unit registration is not implemented yet
```

음수 비율 테스트는 현재 스텁이 `IllegalArgumentException`이 아니라 `UnsupportedOperationException`을 던지기 때문에 실패했다. 이는 비율 검증 구현이 아직 없다는 RED 신호다.

## 5. 기존 RED 실패와의 구분

전체 `mvn test` 결과에는 이번 세션 이전부터 존재하던 `UnitConverterDualTrackRedTest`의 RED placeholder 실패도 함께 포함되었다.

기존 RED 실패는 다음 테스트 클래스에서 발생했다.

- `com.example.unitconverter.UnitConverterDualTrackRedTest`

이번 세션의 신규 RED 범위는 다음 테스트 클래스다.

- `DecayingUnitConverterTest`

## 6. Linter 확인

신규로 편집한 Java 파일에 대해 linter 확인을 수행했다.

```text
No linter errors found.
```

## 7. 최종 산출물

이번 세션의 최종 산출물은 다음과 같다.

- `src/main/java/DecayingUnitConverter.java`
- `src/test/java/DecayingUnitConverterTest.java`
- `report/25_RED 신규 기능 테스트 먼저 작성 _보고서.md`
- `Prompting/25_RED 신규 기능 테스트 먼저 작성 _보고서-Prompting.md`
