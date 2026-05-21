# 12_GREEN 프롬프트 1 - 간결 버전 보고서 - Prompting

## 1. 세션 목적

이번 세션의 목적은 `UnitConverter_A02` 프로젝트에서 TDD GREEN 단계만 수행하고, 현재 워킹 트리에 있는 RED 실패 테스트가 의도한 이유로 실패하는지 먼저 확인하는 것이었다.

실패 테스트를 통과시키기 위한 최소 프로덕션 코드만 허용되었고, REFACTOR와 범위 밖 구현은 금지되었다.

## 2. 사용 프롬프트 기록

### Prompt 1. GREEN만 수행 요청

```text
[단계] TDD의 GREEN만 수행한다. REFACTOR는 하지 않는다.
프로젝트: @c:\dev\UnitConverter_A02\ 

전제: 현재 워킹 트리에 RED 실패 테스트가 있다.
먼저 테스트가 의도한 이유로 실패하는지 확인한 뒤 작업한다.

할 일:
1. 실패하는 테스트를 통과시키기 위한 최소한의 코드만 추가·수정한다.
2. 범위 밖 요구사항(다른 AC, 출력 포맷, 리팩터링)은 하지 않는다.
3. GREEN 완료 후 해당 테스트 재실행하여 통과를 확인한다.

금지: 테스트 약화·삭제 / 의미 없는 하드코딩 / 3.28084·1.09361 인라인 상수

산출물:
- 변경된 프로덕션 코드만
- 짧은 요약: 어떤 함수를 최소로 채웠는지 2~3문장
```

### Prompt 2. 세션 보고서 및 Prompting 파일 내보내기 요청

```text
이번세션에서 진행한 내용을 report 폴더의 12_GREEN 프롬프트 1 - 간결 버전_보고서 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더의 12_GREEN 프롬프트 1 - 간결 버전_보고서-Prompting.md 파일로 내보내줘
```

## 3. 작업 응답 요약

Prompt 1에 대해서는 먼저 현재 프로젝트의 테스트와 관련 프로덕션 코드를 확인했다.

확인한 주요 파일은 다음과 같다.

- `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`
- `src/main/java/com/example/unitconverter/UnitConverterService.java`
- `src/main/java/UnitConverter.java`
- `src/test/java/com/example/unitconverter/UnitConverterServiceTest.java`
- `pom.xml`

`UnitConverterDualTrackRedTest`의 첫 테스트인 `ui_valid_meter_input_returns_conversion_result`는 `meter:2.5` 입력에 대해 다음 2줄을 기대하고 있었다.

```text
2.5 meter = 8.2 feet
2.5 meter = 2.7 yard
```

나머지 11개 테스트는 아직 `fail("RED")` placeholder로 남아 있었다.

## 4. RED 실패 확인 기록

먼저 전체 테스트를 실행했다.

```shell
mvn test
```

결과는 실패였다.

```text
Tests run: 46, Failures: 11, Errors: 0, Skipped: 0
BUILD FAILURE
```

실패 원인은 모두 `UnitConverterDualTrackRedTest` 내부의 의도된 RED placeholder였다.

대표 실패 메시지는 다음과 같았다.

```text
org.opentest4j.AssertionFailedError: RED
```

기존 회귀 테스트인 `UnitConverterServiceTest`는 모두 통과했다.

```text
Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
```

## 5. GREEN 재실행 기록

실제 검증이 작성된 테스트만 단독 실행했다.

```shell
mvn -Dtest=UnitConverterDualTrackRedTest#ui_valid_meter_input_returns_conversion_result test
```

결과는 성공이었다.

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

따라서 이번 GREEN 단계에서 추가로 변경할 프로덕션 코드는 없었다.

## 6. 최종 응답 요약

최종 응답에서는 프로덕션 코드를 변경하지 않았다고 보고했다.

`mvn test`의 실패 원인은 프로덕션 로직 실패가 아니라 `UnitConverterDualTrackRedTest`에 남아 있는 `fail("RED")` 11개였다고 설명했다.

실제 검증이 작성된 `ui_valid_meter_input_returns_conversion_result`는 `UnitConverterService.convertInputToConsoleLines()` 경로로 이미 통과했으므로, GREEN을 위해 최소로 채울 함수는 없었다고 정리했다.

## 7. 최종 산출물

이번 세션의 최종 산출물은 다음과 같다.

- `report/12_GREEN 프롬프트 1 - 간결 버전_보고서.md`
- `Prompting/12_GREEN 프롬프트 1 - 간결 버전_보고서-Prompting.md`
