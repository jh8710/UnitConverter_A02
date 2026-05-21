# 08_Dual-Track RED 전체 설계 보고서 - Prompting

## 1. 세션 목적

이번 세션의 목적은 `UnitConverter_A02` 프로젝트에 대해 Dual-Track UI + Logic TDD 관점의 RED 테스트를 설계하고, 사용자의 추가 지시에 따라 JUnit 5 테스트 스켈레톤만 남기는 것이었다.

구현 코드 작성, GREEN 단계, REFACTOR 단계는 수행하지 않았다.

## 2. 사용 프롬프트 기록

### Prompt 1. Dual-Track UI + Logic RED 테스트 작성 요청

```text
당신은 Dual-Track UI + Logic TDD 전문가입니다.
프로젝트: @c:\dev\UnitConverter_A02\ 
현재 코드 상태: 단일 main() 함수, if-else 체인, 예외 처리 없음

중요: 구현 코드 작성 금지 / GREEN 단계 금지 / REFACTOR 금지
오직 RED 테스트만 작성하십시오.

프로젝트 조건:
- 입력: String (단위:값) (예: "meter:2.5")
- 변환: 1 meter = 3.28084 feet, 1 meter = 1.09361 yard
- 출력: "2.5 meter = 8.202100 feet" 형식
- 예외: IllegalArgumentException

TRACK A — UI / Boundary RED:
1. "meter:2.5" 정상 입력 → 변환 결과 반환
2. ":" 없는 입력 → 예외 발생 (잘못된 형식)
3. 음수 값 입력 ("meter:-1.0") → 예외 발생
4. 없는 단위 ("parsec:1.0") → 예외 발생
5. 반환 포맷에 원 입력 단위·값 보존 확인
6. JSON 출력 요청 시 올바른 스키마 반환
각 테스트에: 테스트 이름 / Given / When / Then / 보호하는 계약

TRACK B — Domain / Logic RED:
1. convert(fromUnit, value, toUnit) — meter↔feet 정확도 (1e-5 이내)
2. convert(fromUnit, value, toUnit) — meter↔yard 정확도
3. convertAll(fromUnit, value) — 모든 등록 단위로 변환 반환
4. registerUnit(name, ratio_to_meter) — 새 단위 등록 후 변환 가능
5. loadConfig(path) — JSON/YAML 설정 파일 로드 후 비율 적용
6. loadConfig(invalid_path) — 파일 없을 때 기본값 유지
각 테스트: 실패 상태 명확히 설명 / 보호 Invariant 명시

출력 형식:
# UI RED Tests — Test ID / Given/When/Then / Invariant
# Logic RED Tests — Test ID / Scenario / Invariant
```

### Prompt 2. JUnit 5 RED 스켈레톤 작성 요청

```text
JUnit 5 테스트 스켈레톤을 작성하라.
파일/클래스 이름만 적고, 메서드 본문은 fail("RED") 한 줄만.
RED 원칙으로만 작성하십시오.

예시:
@Test void convert_meter_to_feet_returns_correct_ratio() {   fail("RED"); }
```

### Prompt 3. 세션 보고서 및 Prompting 파일 내보내기 요청

```text
이번세션에서 진행한 내용을 report 폴더의 08_Dual-Track RED 전체 설계_보고서 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더의 08_Dual-Track RED 전체 설계_보고서-Prompting.md 파일로 내보내줘
```

## 3. 작업 응답 요약

Prompt 1에 대해서는 `UnitConverterDualTrackRedTest.java`를 추가하고, UI / Boundary 트랙과 Domain / Logic 트랙을 나누어 총 12개 RED 테스트를 작성했다.

초기 테스트는 Given / When / Then, 보호 계약, 실패 메시지, invariant 설명을 포함했다. 이후 `mvn test`를 실행해 UI 트랙 2개 항목이 RED로 실패함을 확인했다.

Prompt 2에 대해서는 같은 테스트 파일을 스켈레톤 형태로 단순화했다. 모든 테스트 메서드는 구현 호출 없이 `fail("RED");` 한 줄만 포함하도록 변경했다.

Prompt 3에 대해서는 이번 세션의 작업 내용과 프롬프트 원문을 각각 `report`와 `Prompting` 폴더에 08번 문서로 저장했다.

## 4. 최종 테스트 스켈레톤

최종 테스트 파일은 `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`이다.

```java
package com.example.unitconverter;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

class UnitConverterDualTrackRedTest {
    @Test
    void ui_valid_meter_input_returns_conversion_result() {
        fail("RED");
    }

    @Test
    void ui_missing_colon_input_throws_illegal_argument_exception() {
        fail("RED");
    }

    @Test
    void ui_negative_value_input_throws_illegal_argument_exception() {
        fail("RED");
    }

    @Test
    void ui_unknown_unit_input_throws_illegal_argument_exception() {
        fail("RED");
    }

    @Test
    void ui_output_preserves_original_unit_and_value() {
        fail("RED");
    }

    @Test
    void ui_json_output_request_returns_expected_schema() {
        fail("RED");
    }

    @Test
    void convert_meter_to_feet_returns_correct_ratio() {
        fail("RED");
    }

    @Test
    void convert_meter_to_yard_returns_correct_ratio() {
        fail("RED");
    }

    @Test
    void convert_all_returns_all_registered_unit_conversions() {
        fail("RED");
    }

    @Test
    void register_unit_allows_conversion_with_new_unit() {
        fail("RED");
    }

    @Test
    void load_config_applies_json_and_yaml_ratios() {
        fail("RED");
    }

    @Test
    void load_config_missing_path_keeps_default_ratios() {
        fail("RED");
    }
}
```

## 5. 검증 기록

최종 상태에서 `mvn test`를 실행했다.

검증 결과:

```text
Tests run: 46, Failures: 12, Errors: 0, Skipped: 0
BUILD FAILURE
```

실패 12개는 모두 `UnitConverterDualTrackRedTest`의 의도된 `fail("RED")` 실패이다.

