# 08_Dual-Track RED 전체 설계 보고서

## 1. 작업 개요

이번 세션에서는 `UnitConverter_A02` 프로젝트에 대해 Dual-Track UI + Logic TDD 관점의 RED 테스트 설계를 수행했다.

사용자 지시에 따라 구현 코드 작성, GREEN 단계, REFACTOR 단계는 수행하지 않았다. 작업 범위는 JUnit 5 기반 RED 테스트 작성과 이후 테스트 스켈레톤 단순화로 제한했다.

## 2. 최초 RED 테스트 설계

처음에는 Track A와 Track B를 분리한 JUnit 5 테스트 클래스를 추가했다.

추가 파일은 `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`이다.

Track A - UI / Boundary RED 테스트는 다음 계약을 보호하도록 설계했다.

- `meter:2.5` 정상 입력은 `"2.5 meter = 8.202100 feet"` 형식의 변환 결과를 반환해야 한다.
- `:` 없는 입력은 `IllegalArgumentException`을 발생시켜야 한다.
- 음수 입력 `"meter:-1.0"`은 `IllegalArgumentException`을 발생시켜야 한다.
- 없는 단위 `"parsec:1.0"`은 `IllegalArgumentException`을 발생시켜야 한다.
- 출력 포맷은 원 입력 단위와 값을 보존해야 한다.
- JSON 출력 요청은 안정적인 스키마를 반환해야 한다.

Track B - Domain / Logic RED 테스트는 다음 invariant를 보호하도록 설계했다.

- `convert(fromUnit, value, toUnit)`는 meter-feet 변환을 `1e-5` 이내 정확도로 수행해야 한다.
- `convert(fromUnit, value, toUnit)`는 meter-yard 변환을 `1e-5` 이내 정확도로 수행해야 한다.
- `convertAll(fromUnit, value)`는 모든 등록 대상 단위 변환을 반환해야 한다.
- `registerUnit(name, ratio_to_meter)` 이후 새 단위 변환이 가능해야 한다.
- `loadConfig(path)`는 JSON/YAML 설정 파일의 비율을 적용해야 한다.
- `loadConfig(invalid_path)`는 파일이 없을 때 기본값을 유지해야 한다.

## 3. 최초 검증 결과

최초 RED 테스트 작성 후 `mvn test`를 실행했다.

결과는 다음과 같았다.

```text
Tests run: 46, Failures: 2, Errors: 0, Skipped: 0
BUILD FAILURE
```

확인된 RED 실패는 UI 트랙의 2개 항목이었다.

- `UI-RED-001`: 출력이 `"2.5 meter = 8.202100 feet"` 6자리 포맷 계약을 만족하지 않았다.
- `UI-RED-006`: JSON 출력 API인 `convertInputToJson(String)`이 존재하지 않았다.

Domain / Logic 트랙 테스트는 현재 저장소에 이미 구현된 서비스 코드 때문에 통과했다.

## 4. 스켈레톤 RED 재작성

이후 사용자는 JUnit 5 테스트 스켈레톤만 남기고, 모든 메서드 본문을 `fail("RED");` 한 줄로 작성하도록 지시했다.

이에 따라 `UnitConverterDualTrackRedTest.java`를 다음 원칙으로 단순화했다.

- 파일 이름: `UnitConverterDualTrackRedTest.java`
- 클래스 이름: `UnitConverterDualTrackRedTest`
- 테스트 프레임워크: JUnit 5
- 구현 호출 없음
- assertion 상세 로직 없음
- 모든 테스트 메서드 본문은 `fail("RED");` 한 줄만 포함

최종 스켈레톤 테스트는 총 12개이다.

UI / Boundary RED 스켈레톤:

- `ui_valid_meter_input_returns_conversion_result`
- `ui_missing_colon_input_throws_illegal_argument_exception`
- `ui_negative_value_input_throws_illegal_argument_exception`
- `ui_unknown_unit_input_throws_illegal_argument_exception`
- `ui_output_preserves_original_unit_and_value`
- `ui_json_output_request_returns_expected_schema`

Domain / Logic RED 스켈레톤:

- `convert_meter_to_feet_returns_correct_ratio`
- `convert_meter_to_yard_returns_correct_ratio`
- `convert_all_returns_all_registered_unit_conversions`
- `register_unit_allows_conversion_with_new_unit`
- `load_config_applies_json_and_yaml_ratios`
- `load_config_missing_path_keeps_default_ratios`

## 5. 최종 검증 결과

스켈레톤 RED 재작성 후 `mvn test`를 다시 실행했다.

결과는 다음과 같았다.

```text
Tests run: 46, Failures: 12, Errors: 0, Skipped: 0
BUILD FAILURE
```

새로 작성한 `UnitConverterDualTrackRedTest`의 12개 테스트는 모두 `fail("RED")`에 의해 의도적으로 실패했다.

기존 `UnitConverterServiceTest`의 34개 테스트는 통과했다.

## 6. 최종 산출물

이번 세션의 최종 산출물은 다음과 같다.

- `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`
- `report/08_Dual-Track RED 전체 설계_보고서.md`
- `Prompting/08_Dual-Track RED 전체 설계_보고서-Prompting.md`

## 7. 원칙 준수 여부

이번 세션에서는 RED 원칙만 수행했다.

- 구현 코드 작성 없음
- GREEN 단계 수행 없음
- REFACTOR 수행 없음
- 테스트 파일만 작성 및 단순화
- 최종 테스트 상태는 의도된 RED

