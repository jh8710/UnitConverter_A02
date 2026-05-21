# 10_Dual-Track 병렬 GREEN 보고서

## 1. 작업 개요

이번 세션에서는 `UnitConverter_A02` 프로젝트에서 Dual-Track UI(Boundary) + Logic(Domain) TDD 흐름에 따라 이번 커밋에서 처리할 GREEN 묶음 1개를 선택해 진행했다.

선택한 대상은 Track A의 첫 번째 Boundary 테스트인 TC-A-01이다.

```text
TC-A-01: 정상 입력 "meter:2.5" -> 변환 결과 반환 (Happy Path)
```

이번 세션의 원칙은 다음과 같았다.

- 커밋 순서표에서 이번 커밋 대상 TC 1개만 선택
- `mvn test`로 대상 TC의 RED 상태 확인
- 대상 TC만 통과하도록 최소 구현 또는 최소 테스트 전환
- 전체 테스트 재실행으로 영향 범위 확인
- 커밋까지 완료

## 2. 대상 TC 확인

`docs/README.md`의 RED 단계 To-Do 리스트에서 Track A 항목을 확인했다.

Track A의 첫 번째 항목은 다음과 같다.

```text
TC-A-01: 정상 입력 "meter:2.5" -> 변환 결과 반환 (Happy Path)
```

현재 RED 스켈레톤은 `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`에 존재했다.

작업 전 해당 테스트는 다음과 같이 의도적으로 실패하도록 작성되어 있었다.

```java
@Test
void ui_valid_meter_input_returns_conversion_result() {
    fail("RED");
}
```

## 3. 최초 테스트 실행

작업 시작 시 `mvn test`를 실행해 RED 기준선을 확인했다.

결과는 다음과 같았다.

```text
Tests run: 46, Failures: 12, Errors: 0, Skipped: 0
BUILD FAILURE
```

실패 원인은 `UnitConverterDualTrackRedTest`의 12개 테스트가 모두 `fail("RED")`로 남아 있기 때문이었다.

동시에 기존 회귀 테스트인 `UnitConverterServiceTest`는 모두 통과했다.

```text
Running com.example.unitconverter.UnitConverterServiceTest
Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
```

## 4. GREEN 최소 변경

기존 프로덕션 코드 `UnitConverterService`에는 이미 Boundary 입력을 콘솔 출력 라인으로 변환하는 `convertInputToConsoleLines(String input)` 기능이 존재했다.

따라서 이번 TC-A-01에서는 프로덕션 코드를 추가하지 않고, RED placeholder 테스트를 실제 Boundary 계약 테스트로 전환했다.

수정 파일은 다음과 같다.

- `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`

수정한 테스트는 다음 동작을 검증한다.

- 입력: `meter:2.5`
- 출력 라인 개수: 2개
- 포함 결과: `2.5 meter = 8.2 feet`
- 포함 결과: `2.5 meter = 2.7 yard`

최종 테스트 코드는 다음과 같다.

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

## 5. 재검증 결과

대상 TC 단독 실행 명령은 다음과 같다.

```shell
mvn -Dtest=UnitConverterDualTrackRedTest#ui_valid_meter_input_returns_conversion_result test
```

결과는 PASS였다.

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

이후 전체 테스트를 다시 실행했다.

```shell
mvn test
```

결과는 다음과 같았다.

```text
Tests run: 46, Failures: 11, Errors: 0, Skipped: 0
BUILD FAILURE
```

전체 테스트 실패는 남아 있는 RED 스켈레톤 11건 때문이다.

이번 대상 TC-A-01은 실패 목록에서 제거되었고, 기존 `UnitConverterServiceTest` 34개는 계속 통과했다.

```text
Running com.example.unitconverter.UnitConverterServiceTest
Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
```

## 6. 커밋 결과

이번 세션에서는 TC-A-01 변경만 스테이징해 커밋했다.

`target` 산출물은 테스트 실행으로 변경되었지만 커밋 대상에서 제외했다.

커밋 정보는 다음과 같다.

```text
99bd573 test: cover valid meter boundary conversion
```

커밋 메시지는 Conventional Commit 형식의 `test` 타입을 사용했다.

## 7. 최종 산출물

이번 세션의 최종 산출물은 다음과 같다.

- `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`
- `report/10_Dual-Track 병렬 GREEN_보고서.md`
- `Prompting/10_Dual-Track 병렬 GREEN_보고서-Prompting.md`

## 8. 다음 커밋 후보

다음 커밋 후보 TC는 Track A의 두 번째 Boundary 테스트다.

```text
TC-A-02: ":" 없는 입력 -> IllegalArgumentException 발생
```
