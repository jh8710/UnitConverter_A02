# 23_커버리지 점검 프롬프트 보고서 - Prompting

## 1. 세션 목적

이번 세션의 목적은 `UnitConverter_A02` 프로젝트의 현재 테스트 커버리지를 확인하고, Invariant 관련 미커버 분기와 추가 테스트 필요성을 판단하는 것이었다.

주요 요구는 다음과 같았다.

- `mvn jacoco:report` 실행
- `target/site/jacoco/index.html` 기준 커버리지 확인
- 커버되지 않은 줄 중 Invariant 관련 분기 우선 식별
- `meter`↔`feet`/`yard` 비율 변환, 음수 입력, 없는 단위 처리 확인
- Domain Logic 95% 미달 시 누락 테스트 케이스 제안
- Boundary 85% 미달 시 누락 계약 테스트 케이스 제안
- 최종 결과를 지정된 표 형식으로 출력
- 이번 세션 내용을 `report`와 `Prompting` 폴더에 23번 보고서로 내보내기

## 2. 사용 프롬프트 기록

### Prompt 1. 커버리지 점검 요청

```text
프로젝트: @c:\dev\UnitConverter_A02\ 

현재 테스트 커버리지를 점검해줘.
- 실행: mvn jacoco:report && # target/site/jacoco/index.html
- 커버되지 않은 줄 중 Invariant 관련 분기 우선 식별
  (특히: meter↔feet/yard 비율 변환 분기, 음수 입력 처리, 없는 단위 처리)
- Domain Logic 95% 미달 시: 누락 테스트 케이스 제안 (구현 코드 추가 금지)
- Boundary 85% 미달 시: 누락 계약 테스트 케이스 제안

출력:
| 파일 | Stmts | Miss | Cover | 미달 원인 | 추가 테스트 제안 |
```

### Prompt 2. 보고서 및 Prompting 파일 내보내기 요청

```text
이번세션에서 진행한 내용을 report 폴더의 23_커버리지 점검 프롬프트_보고서 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더의 23_커버리지 점검 프롬프트_보고서-Prompting.md 파일로 내보내줘
```

## 3. 초기 탐색 기록

먼저 프로젝트 구조와 관련 파일을 확인했다.

확인 결과 이 프로젝트는 Maven 프로젝트이며, 루트에 `pom.xml`이 존재했다.

확인한 주요 파일은 다음과 같다.

- `pom.xml`
- `src/main/java/com/example/unitconverter/UnitConverterService.java`
- `src/main/java/com/example/unitconverter/ConversionResult.java`
- `src/main/java/UnitConverter.java`
- `src/test/java/com/example/unitconverter/UnitConverterServiceTest.java`
- `src/test/java/com/example/unitconverter/GoldenMasterTest.java`
- `src/test/java/com/example/unitconverter/GoldenMasterApprovalTest.java`
- `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`

터미널 상태도 확인했다.

기존 터미널에는 이전 테스트 실패 로그가 남아 있었지만, 실행 중인 작업은 없는 상태로 판단했다.

## 4. JaCoCo 실행 기록

Prompt 1에 따라 다음 명령을 실행했다.

```shell
mvn jacoco:report
```

실행 결과는 성공이었다.

```text
BUILD SUCCESS
Analyzed bundle 'unit-converter-a02' with 4 classes
```

생성된 리포트는 다음 위치에서 확인했다.

```text
target/site/jacoco/index.html
target/site/jacoco/jacoco.xml
```

## 5. 커버리지 원본 확인 기록

`target/site/jacoco/jacoco.xml`을 읽어 파일별 라인 카운터와 브랜치 카운터를 확인했다.

전체 라인 커버리지 수치는 다음과 같았다.

```text
covered lines: 124
missed lines: 4
total lines: 128
line coverage: 96.9%
```

전체 브랜치 커버리지 수치는 다음과 같았다.

```text
covered branches: 54
missed branches: 10
total branches: 64
branch coverage: 84.4%
```

클래스별 핵심 라인 커버리지는 다음과 같았다.

```text
UnitConverterService.java: 123 covered / 0 missed
ConversionResult.java: 1 covered / 0 missed
UnitConverter.java: 0 covered / 4 missed
```

## 6. 소스별 HTML 확인 기록

`UnitConverterService.java.html`을 확인해 라인 커버리지와 부분 분기를 점검했다.

`UnitConverterService.java`는 라인 기준 100% 커버되었다.

다만 다음 부분 분기가 남아 있었다.

- `loadConfig`: `.yaml` 또는 `.yml` 확장자 판별 중 일부 경로
- `parseInput`: `input == null || colon count != 1` 중 일부 경로
- `parseJson`: `unit`/`metersPerUnit` 한쪽만 있는 객체 조합 일부
- `parseYaml`: `currentUnit == null || currentUnit.isBlank()` 중 일부 경로
- `validateUnitName`: `unit == null || unit.isBlank()` 중 일부 경로

`UnitConverter.java.html`에서는 모든 라인이 미커버로 표시되었다.

```text
src/main/java/UnitConverter.java
line missed: 4
line covered: 0
branch missed: 4
branch covered: 0
```

해당 클래스에는 `meter -> feet` 성공 분기와 unsupported conversion 예외 경로가 있었지만, 테스트에서 전혀 실행되지 않았다.

## 7. 테스트 파일 대조 기록

`UnitConverterServiceTest.java`를 읽어 사용자가 지정한 Invariant 관련 테스트가 존재하는지 대조했다.

`meter`↔`feet`/`yard` 변환 관련 테스트는 다음과 같이 확인했다.

- `test_normalConversion_meterToFeet_returnsExpectedFeet`
- `test_normalConversion_meterToYard_returnsExpectedYard`
- `test_normalConversion_feetToMeter_returnsReverseMeter`
- `test_normalConversion_yardToMeter_returnsReverseMeter`
- `test_normalConversion_convertAllMeter_returnsRegisteredTargetConversions`

음수 입력 관련 테스트는 다음과 같이 확인했다.

- `test_exception_negativeValue_throwsIllegalArgumentException`

없는 단위 관련 테스트는 다음과 같이 확인했다.

- `test_exception_unknownUnit_throwsIllegalArgumentException`

따라서 활성 서비스 클래스인 `UnitConverterService.java` 기준으로는 요청된 Invariant 관련 분기가 라인 기준으로 커버되어 있다고 판단했다.

## 8. 최종 응답 요약

Prompt 1에 대한 최종 응답에서는 다음 표를 전달했다.

| 파일 | Stmts | Miss | Cover | 미달 원인 | 추가 테스트 제안 |
|---|---:|---:|---:|---|---|
| `src/main/java/com/example/unitconverter/UnitConverterService.java` | 123 | 0 | 100.0% | 라인 미달 없음. `meter`↔`feet`/`yard`, 음수 입력, 없는 단위 처리는 라인 기준 커버됨. 단, 부분 분기 6개 남음. | 선택 보강: `.yml` 설정 로드, `convertInputToConsoleLines(null)`, JSON `metersPerUnit`만 있는 객체, YAML 빈 unit, `registerUnit(null)`. |
| `src/main/java/com/example/unitconverter/ConversionResult.java` | 1 | 0 | 100.0% | 미달 없음. | 추가 불필요. |
| `src/main/java/UnitConverter.java` | 4 | 4 | 0.0% | default package의 레거시 변환 클래스가 테스트에서 전혀 실행되지 않음. `meter -> feet` 성공 분기와 unsupported conversion 분기 모두 미커버. | 유지 대상이면 `meter -> feet` 성공, `meter -> yard`/`feet -> meter`/unknown unit 예외 계약 테스트. 폐기 대상이면 커버리지 대상 제외 여부 결정. |

또한 전체 라인 커버리지는 `124/128 = 96.9%`로 보고했다.

Domain Logic 95%와 Boundary 85% 기준은 라인 기준으로 미달하지 않는다고 판단했다.

다만 전체 브랜치 커버리지는 `54/64 = 84.4%`이며, 실질적인 리스크는 `UnitConverterService.java`의 부분 분기와 테스트되지 않는 `UnitConverter.java`라고 보고했다.

## 9. Canvas 작성 기록

커버리지 표와 수치를 보기 쉽게 확인할 수 있도록 다음 Canvas 파일을 생성했다.

```text
C:\Users\정환\.cursor\projects\c-dev-UnitConverter-A02\canvases\coverage-audit.canvas.tsx
```

Canvas에는 다음 내용을 포함했다.

- 전체 라인/브랜치 커버리지 요약
- 파일별 Stmts, Miss, Cover 표
- Invariant 관련 판단
- Domain / Boundary Threshold 판정

## 10. 보고서 작성 기록

Prompt 2에 따라 이번 세션 내용을 기존 보고서 형식에 맞춰 작성했다.

생성한 파일은 다음과 같다.

```text
report/23_커버리지 점검 프롬프트_보고서.md
Prompting/23_커버리지 점검 프롬프트_보고서-Prompting.md
```

프로덕션 코드와 테스트 코드는 수정하지 않았다.
