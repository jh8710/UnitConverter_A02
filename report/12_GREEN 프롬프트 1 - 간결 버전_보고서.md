# 12_GREEN 프롬프트 1 - 간결 버전 보고서

## 1. 작업 개요

이번 세션에서는 `UnitConverter_A02` 프로젝트에서 TDD GREEN 단계만 수행하라는 요청을 기준으로 현재 RED 실패 테스트를 먼저 확인했다.

사용자 제약은 다음과 같았다.

- 실패하는 테스트를 통과시키기 위한 최소 프로덕션 코드만 추가 또는 수정
- REFACTOR 금지
- 다른 AC, 출력 포맷, 범위 밖 요구사항 구현 금지
- 테스트 약화 또는 삭제 금지
- `3.28084`, `1.09361` 인라인 상수 금지

## 2. 현재 코드 확인

관련 테스트와 프로덕션 코드를 확인했다.

- `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`
- `src/main/java/com/example/unitconverter/UnitConverterService.java`
- `src/main/java/UnitConverter.java`
- `src/test/java/com/example/unitconverter/UnitConverterServiceTest.java`

`UnitConverterDualTrackRedTest`에서 실제 검증이 작성된 첫 테스트는 다음 동작을 확인하고 있었다.

```text
service.convertInputToConsoleLines("meter:2.5")
```

기대 결과는 콘솔 출력 2줄이었다.

```text
2.5 meter = 8.2 feet
2.5 meter = 2.7 yard
```

나머지 11개 테스트는 아직 `fail("RED")` placeholder로 남아 있었다.

## 3. RED 실패 확인

먼저 전체 테스트를 실행했다.

```shell
mvn test
```

결과는 실패였다.

```text
Tests run: 46, Failures: 11, Errors: 0, Skipped: 0
BUILD FAILURE
```

실패 원인은 모두 `UnitConverterDualTrackRedTest`에 남아 있는 의도된 RED placeholder였다.

```text
org.opentest4j.AssertionFailedError: RED
```

반면 실제 검증이 작성된 `ui_valid_meter_input_returns_conversion_result`는 실패 목록에 포함되지 않았다.

## 4. GREEN 확인

작성된 검증 테스트만 단독으로 재실행했다.

```shell
mvn -Dtest=UnitConverterDualTrackRedTest#ui_valid_meter_input_returns_conversion_result test
```

결과는 GREEN이었다.

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 5. 프로덕션 코드 변경

이번 세션에서 프로덕션 코드는 변경하지 않았다.

현재 `UnitConverterService.convertInputToConsoleLines()`와 기존 변환 로직만으로 작성된 첫 RED 검증 테스트가 이미 통과하고 있었기 때문이다.

GREEN을 위해 추가로 채울 함수나 최소 수정 지점은 없었다.

## 6. 최종 산출물

이번 세션의 최종 산출물은 다음과 같다.

- `report/12_GREEN 프롬프트 1 - 간결 버전_보고서.md`
- `Prompting/12_GREEN 프롬프트 1 - 간결 버전_보고서-Prompting.md`
