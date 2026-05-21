# 10_Dual-Track 병렬 GREEN 보고서 - Prompting

## 1. 세션 목적

이번 세션의 목적은 `UnitConverter_A02` 프로젝트에서 Dual-Track UI(Boundary) + Logic(Domain) TDD 흐름에 따라 이번 커밋에서 처리할 GREEN 묶음 1개를 선택하고, RED 확인부터 최소 변경, 테스트 재검증, 커밋까지 완료하는 것이었다.

선택된 대상은 Track A의 TC-A-01이었다.

```text
TC-A-01: 정상 입력 "meter:2.5" -> 변환 결과 반환 (Happy Path)
```

## 2. 사용 프롬프트 기록

### Prompt 1. Dual-Track GREEN 1개 묶음 처리 요청

```text
당신은 Dual-Track UI(Boundary) + Logic(Domain) TDD 실무 전문가입니다.
프로젝트: @c:\dev\UnitConverter_A02\ 

이번 커밋에서 처리할 묶음을 1개 선택하고 아래 순서로 진행하라:

Step 1) 대상 TC 확인 (위 커밋 순서표 참고)
Step 2) mvn test -> 대상 TC가 FAIL인지 확인
Step 3) 대상 TC만 통과하는 최소 구현 작성
Step 4) mvn test 재실행 -> 대상 TC PASS, 나머지 영향 없음 확인
Step 5) 커밋

출력 형식:
1) 이번 커밋 목표 (Track A 또는 B, TC ID)
2) 추가/수정한 코드 범위
3) 테스트 결과 (PASS/FAIL 목록)
4) 커밋 메시지 (Conventional Commit)
5) 다음 커밋 후보 TC
```

### Prompt 2. 세션 보고서 및 Prompting 파일 내보내기 요청

```text
이번세션에서 진행한 내용을 report 폴더의 10_Dual-Track 병렬 GREEN_보고서 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더의 10_Dual-Track 병렬 GREEN_보고서-Prompting.md 파일로 내보내줘
```

## 3. 작업 응답 요약

Prompt 1에 대해서는 먼저 `docs/README.md`의 RED 단계 To-Do 리스트를 확인했다.

Track A와 Track B 중 이번 커밋 대상 1개로 Track A의 TC-A-01을 선택했다.

```text
TC-A-01: 정상 입력 "meter:2.5" -> 변환 결과 반환 (Happy Path)
```

이후 `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`를 확인했고, 해당 테스트가 다음과 같이 RED placeholder 상태임을 확인했다.

```java
@Test
void ui_valid_meter_input_returns_conversion_result() {
    fail("RED");
}
```

## 4. RED 기준선 확인

작업 시작 시 전체 테스트를 실행했다.

```shell
mvn test
```

결과는 다음과 같았다.

```text
Tests run: 46, Failures: 12, Errors: 0, Skipped: 0
BUILD FAILURE
```

실패 원인은 `UnitConverterDualTrackRedTest`의 12개 테스트가 모두 `fail("RED")`로 남아 있기 때문이었다.

기존 `UnitConverterServiceTest`는 다음과 같이 모두 통과했다.

```text
Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
```

## 5. GREEN 변경 내용

기존 `UnitConverterService`에는 `convertInputToConsoleLines(String input)` 메서드가 이미 존재했다.

따라서 TC-A-01을 위해 프로덕션 코드를 추가하지 않고, RED placeholder를 실제 Boundary 계약 테스트로 전환했다.

수정한 테스트 코드는 다음과 같다.

```java
@Test
void ui_valid_meter_input_returns_conversion_result() {
    UnitConverterService service = new UnitConverterService();

    List<String> lines = service.convertInputToConsoleLines("meter:2.5");

    assertEquals(2, lines.size());
    assertTrue(lines.contains("2.5 meter = 8.2 feet"));
    assertTrue(lines.contains("2.5 meter = 2.7 yard"));
}
```

수정 파일은 다음 1개다.

- `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`

## 6. 검증 기록

대상 TC 단독 실행 명령은 다음과 같다.

```shell
mvn -Dtest=UnitConverterDualTrackRedTest#ui_valid_meter_input_returns_conversion_result test
```

결과:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

전체 테스트 재실행 명령은 다음과 같다.

```shell
mvn test
```

결과:

```text
Tests run: 46, Failures: 11, Errors: 0, Skipped: 0
BUILD FAILURE
```

TC-A-01은 실패 목록에서 제거되었고, 남은 실패는 의도된 RED placeholder 11건이었다.

기존 `UnitConverterServiceTest`는 34개 테스트가 계속 통과했다.

## 7. 커밋 기록

이번 세션에서는 `UnitConverterDualTrackRedTest.java` 변경만 스테이징해 커밋했다.

테스트 실행으로 변경된 `target` 산출물은 커밋에서 제외했다.

커밋 정보:

```text
99bd573 test: cover valid meter boundary conversion
```

최종 응답에서 제시한 커밋 메시지는 다음과 같다.

```text
test: cover valid meter boundary conversion
```

## 8. 최종 응답 요약

Prompt 1의 출력 형식에 맞춰 다음 내용을 보고했다.

- 이번 커밋 목표: Track A, TC-A-01
- 추가/수정한 코드 범위: `UnitConverterDualTrackRedTest.java`
- 테스트 결과: 대상 TC 단독 PASS, 전체 `mvn test`는 잔여 RED 11건으로 FAIL, 기존 서비스 테스트 34개 PASS
- 커밋 메시지: `test: cover valid meter boundary conversion`
- 다음 커밋 후보: Track A, TC-A-02

## 9. 최종 산출물

이번 세션의 최종 산출물은 다음과 같다.

- `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`
- `report/10_Dual-Track 병렬 GREEN_보고서.md`
- `Prompting/10_Dual-Track 병렬 GREEN_보고서-Prompting.md`
