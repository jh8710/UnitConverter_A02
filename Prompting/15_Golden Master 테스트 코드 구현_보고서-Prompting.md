# 15_Golden Master 테스트 코드 구현 보고서 - Prompting

## 1. 세션 목적

이번 세션의 목적은 `UnitConverter_A02` 프로젝트에 Golden Master 테스트 코드를 추가하고, 기준 파일의 시나리오 섹션별 출력과 실제 캡처 출력을 비교하는 JUnit 5 회귀 테스트를 완성하는 것이었다.

주요 요구는 다음과 같았다.

- Java 21, Maven, JUnit 5 환경에서 구현
- 표준 출력 캡처 기반 actual 생성
- 기준 파일 섹션별 expected 읽기
- `assertEquals(expected, actual)` 비교
- `@Tag("golden_master")` 적용
- 4개 시나리오 테스트 작성
- 실패 시 라인별 diff 출력
- `mvn test -Dgroups=golden_master` 실행 결과 제공

## 2. 사용 프롬프트 기록

### Prompt 1. Golden Master 테스트 코드 구현 요청

```text
[P] 회귀 테스트(Approval/Golden Master) 설계 전문가입니다.
[C] Java 21, Gradle/Maven, JUnit 5
[T] UnitConverter Golden Master 테스트 코드를 작성해줘.

    구현 요건:
    1) 출력 캡처: System.setOut(new PrintStream(baos))        또는 ./run.sh > actual.txt
    2) 파일 비교: Files.readString(Path.of(...)) 비교        assertEquals(expected, actual)
    3) 테스트 마킹: @Tag("golden_master") @Test void unitConverter_meter_2_5()
    4) 빌드/실행: mvn test -Dgroups=golden_master 또는: mvn test  (전체 실행에 포함)
    5) (선택) Approvals 라이브러리: Approvals.verify(actual)

    테스트 케이스 (4개):
    - GM-TC-01: "meter:2.5" 출력 == 기준 파일 [meter:2.5] 섹션
    - GM-TC-02: "feet:1.0"  출력 == 기준 파일 [feet:1.0]  섹션
    - GM-TC-03: "yard:1.0"  출력 == 기준 파일 [yard:1.0]  섹션
    - GM-TC-04: "meter:0.0" 출력 == 기준 파일 [meter:0.0] 섹션

    실패 시 출력:
    --- expected
    +++ actual
    @@ 라인별 diff 표시 @@

[F] GoldenMasterTest.java + golden_master_expected.txt 완성 코드 + mvn test -Dgroups=golden_master 실행 결과
```

### Prompt 2. 세션 보고서 및 Prompting 파일 내보내기 요청

```text
이번세션에서 진행한 내용을 report 폴더의 15_Golden Master 테스트 코드 구현_보고서 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더의 15_Golden Master 테스트 코드 구현_보고서-Prompting.md 파일로 내보내줘
```

## 3. 프로젝트 탐색 기록

처음에는 Maven/JUnit 설정과 기존 변환 실행 방식을 확인했다.

확인한 주요 파일은 다음과 같다.

- `pom.xml`
- `src/main/java/UnitConverter.java`
- `UnitConverter.java`
- `src/main/java/com/example/unitconverter/UnitConverterService.java`
- `src/main/java/com/example/unitconverter/ConversionResult.java`
- `src/test/java/com/example/unitconverter/UnitConverterServiceTest.java`
- `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`
- `src/test/java/com/example/unitconverter/GoldenMasterApprovalTest.java`
- `src/test/resources/golden_master_expected.txt`

확인 결과는 다음과 같았다.

- 프로젝트는 Maven 기반이었다.
- `pom.xml`은 Java 21과 JUnit Jupiter 5.10.2를 사용하고 있었다.
- `UnitConverterService`는 `convertAll`과 `convertInputToConsoleLines`를 제공하고 있었다.
- 기존 `golden_master_expected.txt`는 4개 시나리오 섹션을 이미 포함하고 있었다.
- 기존 `GoldenMasterApprovalTest`는 전체 기준 파일 비교 방식이었고, 이번 요청은 섹션별 4개 테스트 방식이었다.

## 4. 설계 판단 기록

출력 캡처는 요청 조건에 맞춰 `System.setOut(new PrintStream(baos))` 방식을 사용했다.

선택 이유는 다음과 같다.

- JUnit 테스트 내부에서 actual 출력을 직접 만들 수 있다.
- 별도 `run.sh` 의존 없이 Maven 테스트로 검증할 수 있다.
- 테스트 실패 시 JUnit assertion 메시지에 diff를 연결하기 쉽다.

기준 파일은 새로 생성하기보다 기존 `src/test/resources/golden_master_expected.txt`를 사용했다.

선택 이유는 다음과 같다.

- 이미 요청된 4개 시나리오가 동일한 섹션 구조로 저장되어 있었다.
- 이전 세션에서 생성된 Golden Master 기준을 그대로 회귀 기준으로 활용할 수 있었다.
- 테스트 코드는 각 섹션의 본문만 추출해 비교하도록 만들어 섹션별 테스트 요구사항을 만족시켰다.

## 5. 구현 기록

추가한 테스트 파일은 다음과 같다.

```text
src/test/java/com/example/unitconverter/GoldenMasterTest.java
```

추가한 테스트 메서드는 다음과 같다.

```text
unitConverter_meter_2_5()
unitConverter_feet_1_0()
unitConverter_yard_1_0()
unitConverter_meter_0_0()
```

각 테스트는 다음 공통 흐름을 사용한다.

- `assertGoldenMaster(input)` 호출
- 기준 파일에서 `[input]` 섹션 읽기
- `System.out` 캡처로 actual 출력 생성
- expected와 actual 비교
- 불일치 시 라인별 diff 생성

actual 출력은 `UnitConverterService.convertAll(unit, value)` 결과를 사용해 생성했다.

출력 포맷은 Golden Master 기준 파일과 일치하도록 다음 형식으로 고정했다.

```text
%s %s = %.6f %s%n
```

## 6. 기준 파일 기록

사용한 기준 파일은 다음과 같다.

```text
src/test/resources/golden_master_expected.txt
```

기준 파일 내용은 다음과 같다.

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

## 7. Diff 출력 기록

실패 메시지는 다음 구조로 나오도록 구현했다.

```text
--- expected
+++ actual
@@ line N @@
- expected line
+ actual line
```

이 형식은 사용자가 요청한 `--- expected`, `+++ actual`, `@@ 라인별 diff 표시 @@` 조건을 반영한 것이다.

## 8. 검증 기록

편집 후 linter 확인을 수행했다.

```text
No linter errors found.
```

Golden Master 태그 테스트를 실행했다.

```shell
mvn test -Dgroups=golden_master
```

실행 결과는 성공이었다.

```text
Running com.example.unitconverter.GoldenMasterTest
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 9. Git 상태 기록

테스트 실행 후 확인한 주요 변경 상태는 다음과 같았다.

```text
?? src/test/java/com/example/unitconverter/GoldenMasterTest.java
?? target/surefire-reports/TEST-com.example.unitconverter.GoldenMasterTest.xml
?? target/surefire-reports/com.example.unitconverter.GoldenMasterTest.txt
?? target/test-classes/com/example/unitconverter/GoldenMasterTest.class
 M target/jacoco.exec
 M target/maven-status/maven-compiler-plugin/testCompile/default-testCompile/createdFiles.lst
 M target/maven-status/maven-compiler-plugin/testCompile/default-testCompile/inputFiles.lst
 M target/site/jacoco/jacoco-sessions.html
 M target/site/jacoco/jacoco.xml
```

`target/` 변경은 Maven 테스트 실행 산출물이다.

## 10. 최종 응답 요약

Prompt 1에 대한 최종 응답에서는 다음 내용을 전달했다.

- `GoldenMasterTest.java` 추가
- `golden_master_expected.txt` 기준 파일 사용
- 출력 캡처, 파일 비교, 태그 마킹, diff 출력 구현
- `mvn test -Dgroups=golden_master` 성공
- 테스트 실행으로 `target/` 산출물이 갱신되었다는 참고 사항

## 11. 최종 산출물

이번 세션에서 생성한 최종 산출물은 다음과 같다.

- `src/test/java/com/example/unitconverter/GoldenMasterTest.java`
- `report/15_Golden Master 테스트 코드 구현_보고서.md`
- `Prompting/15_Golden Master 테스트 코드 구현_보고서-Prompting.md`

