# 22_최종 회귀 확인 및 PR 보고서 - Prompting

## 1. 세션 목적

이번 세션의 목적은 `UnitConverter_A02` 프로젝트의 최종 회귀 상태를 확인하고, 전체 테스트와 커버리지, 리팩토링 완료 조건을 점검하는 것이었다.

사용자는 모든 리팩토링 커밋이 완료된 뒤 다음 항목을 확인하도록 요청했다.

- 전체 테스트 실행
- JaCoCo 커버리지 리포트 생성
- TC-A-01~07, TC-B-01~07 통과 여부
- Golden Master 테스트 통과 여부
- if-else 체인 제거 및 `UnitRegistry` 교체 여부
- 변환 비율 매직 넘버 인라인 제거 여부
- Domain과 Boundary 책임 분리 여부
- Domain / Boundary 커버리지 목표 충족 여부

마지막으로 이번 세션의 내용을 `report`와 `Prompting` 폴더에 각각 22번 보고서로 내보내도록 요청했다.

## 2. 사용 프롬프트 기록

### Prompt 1. 최종 회귀 확인 요청

```text
모든 리팩토링 커밋 완료 후 전체 테스트와 커버리지를 확인하라.

mvn test # 또는: gradle test

mvn jacoco:report # target/site/jacoco/index.html

확인 항목:
- 모든 TC PASS (TC-A-01~07, TC-B-01~07)
- Golden Master 테스트도 PASS (출력 불변 확인)
- if-else 체인 제거 완료 (UnitRegistry 교체)
- 매직 넘버 3.28084/1.09361 인라인 없음
- Domain(변환 로직)과 Boundary(파싱·출력)가 분리됨
- Domain 커버리지 ≥ 95% / Boundary ≥ 85%
```

### Prompt 2. 보고서 내보내기 요청

```text
이번세션에서 진행한 내용을 report 폴더의 22_최종 회귀 확인 및 PR_보고서 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더의 22_최종 회귀 확인 및 PR_보고서-Prompting.md 파일로 내보내줘
```

## 3. 초기 탐색 기록

Prompt 1에 대해 먼저 프로젝트 구조를 확인했다.

Maven 프로젝트임을 `pom.xml`로 확인했고, Gradle 빌드 파일은 존재하지 않았다.

확인한 주요 파일은 다음과 같다.

- `pom.xml`
- `src/main/java/UnitConverter.java`
- `src/main/java/com/example/unitconverter/UnitConverterService.java`
- `src/main/java/com/example/unitconverter/ConversionResult.java`
- `src/test/java/com/example/unitconverter/UnitConverterServiceTest.java`
- `src/test/java/com/example/unitconverter/GoldenMasterTest.java`
- `src/test/java/com/example/unitconverter/GoldenMasterApprovalTest.java`
- `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`

이 단계에서 `UnitConverterDualTrackRedTest.java` 안에 아직 `fail("RED")` placeholder가 남아 있음을 확인했다.

## 4. 전체 테스트 실행 기록

전체 테스트 실행 명령은 다음과 같았다.

```shell
mvn test
```

결과는 실패였다.

```text
Tests run: 51, Failures: 9, Errors: 0, Skipped: 0
BUILD FAILURE
```

실패한 테스트는 모두 `UnitConverterDualTrackRedTest`에 남아 있는 RED placeholder였다.

실패 목록은 다음과 같았다.

```text
UnitConverterDualTrackRedTest.convert_meter_to_yard_returns_correct_ratio
UnitConverterDualTrackRedTest.ui_json_output_request_returns_expected_schema
UnitConverterDualTrackRedTest.ui_output_preserves_original_unit_and_value
UnitConverterDualTrackRedTest.register_unit_allows_conversion_with_new_unit
UnitConverterDualTrackRedTest.ui_negative_value_input_throws_illegal_argument_exception
UnitConverterDualTrackRedTest.load_config_applies_json_and_yaml_ratios
UnitConverterDualTrackRedTest.ui_unknown_unit_input_throws_illegal_argument_exception
UnitConverterDualTrackRedTest.load_config_missing_path_keeps_default_ratios
UnitConverterDualTrackRedTest.convert_all_returns_all_registered_unit_conversions
```

따라서 Prompt 1의 "모든 TC PASS" 조건은 충족되지 않았다.

## 5. Golden Master 확인 기록

전체 테스트 로그에서 Golden Master 관련 테스트는 모두 통과했다.

```text
Running com.example.unitconverter.GoldenMasterApprovalTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

Running com.example.unitconverter.GoldenMasterTest
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
```

따라서 출력 불변 확인 목적의 Golden Master 테스트는 PASS로 보고했다.

## 6. JaCoCo 리포트 생성 기록

커버리지 리포트 생성을 위해 다음 명령을 실행했다.

```shell
mvn jacoco:report
```

결과는 성공이었다.

```text
BUILD SUCCESS
Analyzed bundle 'unit-converter-a02' with 4 classes
```

생성 위치는 다음과 같았다.

```text
target/site/jacoco/index.html
```

이후 `target/site/jacoco/jacoco.csv`를 확인했다.

주요 내용은 다음과 같았다.

```text
UnitConverter,20 missed / 0 covered instructions, 4 missed / 0 covered lines
ConversionResult,0 missed / 15 covered instructions, 0 missed / 1 covered line
UnitConverterService,0 missed / 558 covered instructions, 0 missed / 122 covered lines
UnitConverterService.ParsedInput,0 missed / 12 covered instructions, 0 missed / 1 covered line
```

`UnitConverterService`는 높은 커버리지로 확인되었지만, 기본 패키지의 `UnitConverter`는 커버되지 않았다.

또한 전체 테스트가 실패한 상태에서 생성된 리포트이므로 최종 품질 게이트 통과 근거로 확정하지 않았다.

## 7. 구조 조건 확인 기록

매직 넘버 잔존 여부를 확인했다.

```shell
rg "3\.28084|1\.09361" --glob "*.java"
```

프로덕션 코드에서는 다음 위치에 값이 남아 있었다.

```text
src/main/java/UnitConverter.java
src/main/java/com/example/unitconverter/UnitConverterService.java
UnitConverter.java
```

특히 루트 `UnitConverter.java`에는 변환식에 숫자가 직접 인라인으로 남아 있었다.

```text
meterValue = value / 3.28084;
meterValue = value / 1.09361;
double inFeet = meterValue * 3.28084;
double inYards = meterValue * 1.09361;
```

if-else 체인과 `UnitRegistry` 교체 여부도 확인했다.

`UnitRegistry` 클래스, 레코드, 인터페이스는 검색되지 않았다.

루트 `UnitConverter.java`에는 다음 형태의 if-else 체인이 남아 있었다.

```text
if (unit.equals("meter")) {
    meterValue = value;
} else if (unit.equals("feet")) {
    meterValue = value / 3.28084;
} else if (unit.equals("yard")) {
    meterValue = value / 1.09361;
}
```

Domain / Boundary 분리 여부도 검색했다.

```shell
rg "package\s+.*(domain|boundary)|class\s+.*(Parser|Formatter|Boundary|Domain|Registry)" src/main/java --glob "*.java"
```

별도 `domain`, `boundary`, `parser`, `formatter`, `UnitRegistry` 구조는 확인되지 않았다.

따라서 Domain과 Boundary가 명확히 분리되었다고 판정할 수 없었고, Domain 95% / Boundary 85% 커버리지도 독립적으로 산정할 수 없었다.

## 8. 최종 응답 요약

Prompt 1에 대한 최종 응답에서는 현재 상태가 완료 조건 미달이라고 보고했다.

핵심 판정은 다음과 같았다.

- `mvn test`: FAIL
- 전체 테스트: 51개 실행, 9개 실패
- 실패 원인: `UnitConverterDualTrackRedTest`의 `fail("RED")` placeholder
- Golden Master: PASS
- `mvn jacoco:report`: SUCCESS
- 리포트 위치: `target/site/jacoco/index.html`
- if-else 체인 제거 및 `UnitRegistry` 교체: FAIL
- 매직 넘버 인라인 제거: FAIL
- Domain / Boundary 분리: FAIL 또는 미확인
- Domain / Boundary 개별 커버리지: 독립 측정 불가

## 9. 보고서 작성 기록

Prompt 2에 따라 이번 세션 내용을 기존 보고서 형식에 맞춰 작성했다.

생성한 파일은 다음과 같다.

- `report/22_최종 회귀 확인 및 PR_보고서.md`
- `Prompting/22_최종 회귀 확인 및 PR_보고서-Prompting.md`

## 10. PR 관련 기록

이번 세션에서는 PR 생성 요청이 별도로 없었다.

따라서 GitHub PR 생성이나 `gh pr create` 실행은 하지 않았다.
