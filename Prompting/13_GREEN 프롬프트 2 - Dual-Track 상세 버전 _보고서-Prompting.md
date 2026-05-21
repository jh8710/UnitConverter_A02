# 13_GREEN 프롬프트 2 - Dual-Track 상세 버전 보고서 - Prompting

## 1. 세션 목적

이번 세션의 목적은 `UnitConverter_A02` 프로젝트에서 Dual-Track UI(Boundary) + Logic(Domain) TDD 흐름을 지키며, 사용자가 지정한 1개 RED 테스트 묶음만 GREEN 처리하고 하나의 커밋으로 마무리하는 것이었다.

REFACTOR는 금지되었고, 비율 상수 `3.28084`, `1.09361`은 인라인 하드코딩하지 않는 제약이 있었다.

## 2. 사용 프롬프트 기록

### Prompt 1. Dual-Track GREEN 상세 수행 요청

```text
🔲 Dual-Track GREEN 상세
당신은 Dual-Track UI(Boundary) + Logic(Domain) TDD 실무 전문가입니다.
프로젝트: c:\DEV\UnitConverter_Java / 이번 작업: "하나의 커밋" 단위

중요 규칙:
- 순서 준수: RED 선택 → GREEN 최소 구현 → 테스트 실행 → 통과 확인 → 커밋
- 1개 RED 테스트 묶음만 처리
- REFACTOR는 이번 커밋에서 금지
- 비율 상수 3.28084 / 1.09361은 인라인 하드코딩 금지 (상수 또는 설정 사용)
- 도메인 레이어에 UI/출력 코드 혼입 금지

고정 계약:
- 입력: String (단위:값) — "단위:값" 형식
- 출력: "값 단위 = 변환값 변환단위" 형식
- 예외: IllegalArgumentException

Step 1) 이번 커밋에서 선택할 RED 테스트 1묶음:
  [TRACK B] convert(meter, 2.5, feet) → 8.202100 (오차 1e-5)
  [TRACK A] ":" 없는 입력 → IllegalArgumentException 발생
  선택 이유를 2줄로 설명하십시오.

Step 2) GREEN 최소 구현 (Dual-Track 병렬)
Step 3) 테스트 실행 및 통과 확인

출력 형식:
1) 이번 커밋 목표  2) 선택한 RED 테스트  3) GREEN 최소 구현 범위
4) 실행한 테스트 목록  5) 결과 요약  6) 커밋 메시지 (Conventional Commit)
7) 변경 파일 목록  8) 다음 커밋 RED 후보 2개

주의: REFACTOR 금지 / 한 커밋에 모든 RED 해결 금지
```

### Prompt 2. 세션 보고서 및 Prompting 파일 내보내기 요청

```text
이번세션에서 진행한 내용을 report 폴더의 13_GREEN 프롬프트 2 - Dual-Track 상세 버전 _보고서 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더의 13_GREEN 프롬프트 2 - Dual-Track 상세 버전 _보고서-Prompting.md 파일로 내보내줘
```

## 3. 작업 응답 요약

Prompt 1에 대해서는 먼저 현재 프로젝트의 테스트와 관련 프로덕션 코드를 확인했다.

확인한 주요 파일은 다음과 같다.

- `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`
- `src/main/java/com/example/unitconverter/UnitConverterService.java`
- `src/main/java/UnitConverter.java`
- `src/test/java/com/example/unitconverter/UnitConverterServiceTest.java`
- `pom.xml`

`UnitConverterDualTrackRedTest`에는 이번 대상 묶음인 다음 2개 테스트가 `fail("RED")` placeholder로 남아 있었다.

- `convert_meter_to_feet_returns_correct_ratio`
- `ui_missing_colon_input_throws_illegal_argument_exception`

## 4. RED 실패 확인 기록

선택한 2개 테스트만 실행해 RED 상태를 확인했다.

```shell
mvn "-Dtest=UnitConverterDualTrackRedTest#convert_meter_to_feet_returns_correct_ratio+ui_missing_colon_input_throws_illegal_argument_exception" test
```

결과는 실패였다.

```text
Tests run: 2, Failures: 2, Errors: 0, Skipped: 0
BUILD FAILURE
```

대표 실패 원인은 다음과 같았다.

```text
org.opentest4j.AssertionFailedError: RED
```

## 5. GREEN 최소 변경 기록

선택한 2개 RED placeholder만 실제 검증으로 전환했다.

변경 파일은 다음과 같다.

- `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`

추가한 assertion은 다음 2가지다.

- `assertThrows(IllegalArgumentException.class, () -> service.convertInputToConsoleLines("meter=2.5"))`
- `assertEquals(8.202100, service.convert("meter", 2.5, "feet"), 1e-5)`

프로덕션 코드는 변경하지 않았다. 기존 `UnitConverterService`가 이미 상수 기반 비율 계산과 입력 형식 검증을 만족하고 있었기 때문이다.

## 6. GREEN 재실행 기록

동일한 선택 묶음을 다시 실행했다.

```shell
mvn "-Dtest=UnitConverterDualTrackRedTest#convert_meter_to_feet_returns_correct_ratio+ui_missing_colon_input_throws_illegal_argument_exception" test
```

결과는 성공이었다.

```text
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

편집 파일의 linter 확인 결과도 오류가 없었다.

```text
No linter errors found.
```

## 7. 커밋 기록

커밋 전 변경 범위를 확인했다.

```shell
git status --short
git diff -- src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java
git log --oneline -5
```

테스트 실행으로 `target` 산출물이 갱신되었지만 커밋 대상에서 제외했다.

커밋에는 다음 파일만 포함했다.

- `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`

커밋 정보는 다음과 같다.

```text
6980920 test(green): cover meter feet and missing colon
```

## 8. 최종 응답 요약

최종 응답에서는 사용자 요청 형식에 맞춰 다음 내용을 보고했다.

- 이번 커밋 목표: Domain `meter -> feet`, Boundary `":"` 없는 입력 예외
- 선택한 RED 테스트: Track B 1건, Track A 1건
- GREEN 최소 구현 범위: 선택된 테스트 placeholder 2건만 assertion 전환
- 실행한 테스트 목록: RED 확인과 GREEN 확인에 같은 Maven 단독 테스트 명령 사용
- 결과 요약: RED 2건 실패 후 GREEN 2건 통과
- 커밋 메시지: `test(green): cover meter feet and missing colon`
- 변경 파일 목록: `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`
- 다음 커밋 RED 후보: meter-to-yard, 음수 입력 예외

## 9. 최종 산출물

이번 세션의 최종 산출물은 다음과 같다.

- `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`
- `report/13_GREEN 프롬프트 2 - Dual-Track 상세 버전 _보고서.md`
- `Prompting/13_GREEN 프롬프트 2 - Dual-Track 상세 버전 _보고서-Prompting.md`
