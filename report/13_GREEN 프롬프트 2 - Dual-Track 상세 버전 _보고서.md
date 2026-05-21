# 13_GREEN 프롬프트 2 - Dual-Track 상세 버전 보고서

## 1. 작업 개요

이번 세션에서는 `UnitConverter_A02` 프로젝트에서 Dual-Track UI(Boundary) + Logic(Domain) TDD 흐름에 따라 1개 RED 테스트 묶음만 선택해 GREEN 처리했다.

사용자 제약은 다음과 같았다.

- 순서 준수: RED 선택 -> GREEN 최소 구현 -> 테스트 실행 -> 통과 확인 -> 커밋
- 1개 RED 테스트 묶음만 처리
- REFACTOR 금지
- 비율 상수 `3.28084`, `1.09361` 인라인 하드코딩 금지
- 도메인 레이어에 UI/출력 코드 혼입 금지

고정 계약은 다음과 같았다.

- 입력: `String`, `"단위:값"` 형식
- 출력: `"값 단위 = 변환값 변환단위"` 형식
- 예외: `IllegalArgumentException`

## 2. 선택한 RED 테스트 묶음

이번 커밋에서 선택한 RED 테스트 묶음은 다음 2건이다.

- `[TRACK B] convert(meter, 2.5, feet) -> 8.202100`, 오차 `1e-5`
- `[TRACK A] ":" 없는 입력 -> IllegalArgumentException`

선택 이유는 다음과 같다.

```text
Domain 핵심 비율 계산과 Boundary 입력 형식 검증을 각각 1개씩 다뤄 Dual-Track 균형이 맞다.
둘 다 고정 계약의 최소 핵심 경로라 이번 커밋 단위로 작고 명확하다.
```

## 3. RED 실패 확인

먼저 선택한 2개 테스트만 실행해 RED 상태를 확인했다.

```shell
mvn "-Dtest=UnitConverterDualTrackRedTest#convert_meter_to_feet_returns_correct_ratio+ui_missing_colon_input_throws_illegal_argument_exception" test
```

결과는 실패였다.

```text
Tests run: 2, Failures: 2, Errors: 0, Skipped: 0
BUILD FAILURE
```

두 실패 모두 `UnitConverterDualTrackRedTest`에 남아 있던 의도된 `fail("RED")` placeholder 때문이었다.

## 4. GREEN 최소 구현 범위

이번 GREEN에서는 선택한 2개 RED placeholder만 실제 assertion으로 전환했다.

수정 파일은 다음과 같다.

- `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`

변경한 테스트는 다음 동작을 검증한다.

- `UnitConverterService.convert("meter", 2.5, "feet")` 결과가 `8.202100`에 `1e-5` 오차로 일치
- `UnitConverterService.convertInputToConsoleLines("meter=2.5")` 호출 시 `IllegalArgumentException` 발생

프로덕션 코드는 변경하지 않았다. 기존 `UnitConverterService`가 이미 `FEET_PER_METER` 상수 기반 변환과 `":"` 형식 검증을 제공하고 있었기 때문이다.

## 5. GREEN 재검증 결과

같은 선택 묶음을 다시 실행했다.

```shell
mvn "-Dtest=UnitConverterDualTrackRedTest#convert_meter_to_feet_returns_correct_ratio+ui_missing_colon_input_throws_illegal_argument_exception" test
```

결과는 성공이었다.

```text
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

편집한 테스트 파일에 대해서 linter 확인도 수행했고, 오류는 없었다.

```text
No linter errors found.
```

## 6. 커밋 결과

이번 커밋에는 테스트 파일 변경만 포함했다.

테스트 실행으로 `target` 산출물이 갱신되었지만 커밋 대상에서 제외했다.

커밋 정보는 다음과 같다.

```text
6980920 test(green): cover meter feet and missing colon
```

## 7. 최종 응답 요약

최종 응답은 사용자 요청 형식에 맞춰 다음 항목으로 정리했다.

1. 이번 커밋 목표
2. 선택한 RED 테스트
3. GREEN 최소 구현 범위
4. 실행한 테스트 목록
5. 결과 요약
6. 커밋 메시지
7. 변경 파일 목록
8. 다음 커밋 RED 후보 2개

다음 커밋 RED 후보로는 다음 2개를 제안했다.

- `[TRACK B] convert(meter, 1.0, yard) -> 1.093610`
- `[TRACK A] 음수 입력 "meter:-1.0" -> IllegalArgumentException`

## 8. 최종 산출물

이번 세션의 최종 산출물은 다음과 같다.

- `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`
- `report/13_GREEN 프롬프트 2 - Dual-Track 상세 버전 _보고서.md`
- `Prompting/13_GREEN 프롬프트 2 - Dual-Track 상세 버전 _보고서-Prompting.md`
