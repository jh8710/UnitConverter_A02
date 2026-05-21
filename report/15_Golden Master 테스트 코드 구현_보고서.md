# 15_Golden Master 테스트 코드 구현 보고서

## 1. 작업 개요

이번 세션에서는 `UnitConverter_A02` 프로젝트에 JUnit 5 기반 Golden Master 테스트 코드를 추가했다.

요청된 핵심 목표는 다음과 같았다.

- `GoldenMasterTest.java` 작성
- `golden_master_expected.txt` 기준 파일의 섹션별 expected 출력 비교
- `System.setOut(new PrintStream(baos))` 방식의 출력 캡처
- `Files.readString(Path.of(...))` 기반 파일 비교
- `@Tag("golden_master")` 테스트 마킹
- 실패 시 `--- expected`, `+++ actual`, `@@ line N @@` 형식의 라인별 diff 출력
- `mvn test -Dgroups=golden_master` 실행 결과 확인

## 2. 프로젝트 확인 결과

프로젝트는 Maven 기반 Java 21 프로젝트이며, JUnit 5를 사용하고 있었다.

확인한 주요 파일은 다음과 같다.

- `pom.xml`
- `src/main/java/com/example/unitconverter/UnitConverterService.java`
- `src/main/java/com/example/unitconverter/ConversionResult.java`
- `src/test/java/com/example/unitconverter/UnitConverterServiceTest.java`
- `src/test/java/com/example/unitconverter/GoldenMasterApprovalTest.java`
- `src/test/resources/golden_master_expected.txt`

기존 기준 파일은 이미 다음 4개 시나리오 섹션을 포함하고 있었다.

- `[meter:2.5]`
- `[feet:1.0]`
- `[yard:1.0]`
- `[meter:0.0]`

## 3. 구현 파일

새 테스트 파일을 추가했다.

```text
src/test/java/com/example/unitconverter/GoldenMasterTest.java
```

테스트 메서드는 요구사항에 맞춰 4개로 분리했다.

```text
unitConverter_meter_2_5()
unitConverter_feet_1_0()
unitConverter_yard_1_0()
unitConverter_meter_0_0()
```

각 테스트에는 다음 어노테이션을 적용했다.

```java
@Tag("golden_master")
@Test
```

## 4. 테스트 동작

테스트의 핵심 흐름은 다음과 같다.

1. 테스트 입력 문자열을 받는다.
2. `System.setOut(capture)`로 표준 출력을 캡처한다.
3. `UnitConverterService.convertAll(unit, value)` 결과를 `%.6f` 형식으로 출력한다.
4. `src/test/resources/golden_master_expected.txt`에서 해당 `[unit:value]` 섹션만 읽는다.
5. 섹션 구분자인 `---`는 비교 대상에서 제외한다.
6. expected와 actual 문자열을 `assertEquals(expected, actual)`로 비교한다.
7. 불일치 시 라인별 diff 메시지를 출력한다.

출력 캡처는 다음 방식으로 구현했다.

```java
ByteArrayOutputStream output = new ByteArrayOutputStream();
PrintStream originalOut = System.out;

try (PrintStream capture = new PrintStream(output, true, StandardCharsets.UTF_8)) {
    System.setOut(capture);
    printConversion(input);
} finally {
    System.setOut(originalOut);
}
```

기준 파일 비교는 다음 방식으로 구현했다.

```java
String content = normalize(Files.readString(GOLDEN_MASTER_PATH, StandardCharsets.UTF_8));
```

## 5. Golden Master 기준 파일

기준 파일 위치는 다음과 같다.

```text
src/test/resources/golden_master_expected.txt
```

현재 기준 파일 내용은 다음과 같다.

```text
[meter:2.5]
2.5 meter = 8.202100 feet
2.5 meter = 2.734025 yard
---
[feet:1.0]
1.0 feet = 0.304800 meter
1.0 feet = 0.333332 yard
---
[yard:1.0]
1.0 yard = 0.914403 meter
1.0 yard = 3.000009 feet
---
[meter:0.0]
0.0 meter = 0.000000 feet
0.0 meter = 0.000000 yard
```

## 6. 실패 시 Diff 출력

불일치가 발생하면 assertion message로 다음 형식의 diff를 제공하도록 구현했다.

```text
--- expected
+++ actual
@@ line N @@
- expected line
+ actual line
```

이를 통해 Golden Master 기준 출력과 현재 출력 중 어느 라인이 달라졌는지 바로 확인할 수 있다.

## 7. 검증 결과

요구된 명령으로 Golden Master 태그 테스트를 실행했다.

```shell
mvn test -Dgroups=golden_master
```

실행 결과는 성공이었다.

```text
Running com.example.unitconverter.GoldenMasterTest
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

편집한 테스트 파일에 대한 linter 확인도 수행했고, 오류는 없었다.

```text
No linter errors found.
```

## 8. Git 상태 참고

이번 테스트 실행으로 `target/` 아래 Maven, Surefire, Jacoco 산출물이 갱신되었다.

새로 추가된 주요 소스 파일은 다음과 같다.

```text
src/test/java/com/example/unitconverter/GoldenMasterTest.java
```

## 9. 최종 산출물

이번 세션의 최종 산출물은 다음과 같다.

- `src/test/java/com/example/unitconverter/GoldenMasterTest.java`
- `src/test/resources/golden_master_expected.txt`
- `report/15_Golden Master 테스트 코드 구현_보고서.md`
- `Prompting/15_Golden Master 테스트 코드 구현_보고서-Prompting.md`

