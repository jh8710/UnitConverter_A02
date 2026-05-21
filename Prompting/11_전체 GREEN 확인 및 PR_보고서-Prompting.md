# 11_전체 GREEN 확인 및 PR 보고서 - Prompting

## 1. 세션 목적

이번 세션의 목적은 `UnitConverter_A02` 프로젝트에서 Java 전체 GREEN 상태와 JaCoCo 커버리지를 확인하고, 남아 있는 구조 조건을 점검하는 것이었다.

최초 요청은 모든 TC(TC-A-01~07, TC-B-01~07)가 통과한 후 전체 테스트와 커버리지 리포트를 확인하는 것이었다.

이후 사용자는 `UnitConverterDualTrackRedTest` 안의 의도된 `fail("RED")` placeholder를 제외하고 다시 GREEN 상태를 확인하도록 범위를 조정했다.

## 2. 사용 프롬프트 기록

### Prompt 1. 전체 GREEN 확인 요청

```text
🔲 전체 GREEN 확인 — Java
모든 TC(TC-A-01~07, TC-B-01~07)가 통과한 후 전체 테스트를 실행하라.

mvn test # 또는: gradle test

커버리지 측정:
mvn jacoco:report # target/site/jacoco/index.html

확인 항목:
- 모든 TC PASS (0 failures)
- Domain Logic 커버리지 ≥ 95%
- Boundary Layer 커버리지 ≥ 85%
- 비율 상수 3.28084 / 1.09361이 인라인으로 남아 있지 않음
- main() 함수에 변환 로직이 남아 있지 않음 (Domain으로 분리됨)
```

### Prompt 2. 의도된 RED Placeholder 제외 요청

```text
의도된 FAIL인 UnitConverterDualTrackRedTest 안의 fail("RED") placeholde 를 제외하고 다시 GREEN 확인해줘
```

### Prompt 3. 세션 보고서 및 Prompting 파일 내보내기 요청

```text
이번세션에서 진행한 내용을 report 폴더의 11_전체 GREEN 확인 및 PR_보고서 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더의 11_전체 GREEN 확인 및 PR_보고서-Prompting.md 파일로 내보내줘
```

## 3. 작업 응답 요약

Prompt 1에 대해서는 먼저 프로젝트 구조를 확인했다.

Maven 프로젝트임을 `pom.xml`로 확인했고, Gradle 빌드 파일은 존재하지 않았다.

또한 변환 비율과 `main()` 잔존 여부를 검색했다.

```shell
rg "3\.28084|1\.09361" --glob "*.java"
rg "public\s+static\s+void\s+main|class\s+.*Application|SpringApplication\.run" --glob "*.java"
```

초기 검색 결과, 비율 값은 테스트뿐 아니라 다음 프로덕션 코드에도 남아 있었다.

- 루트 `UnitConverter.java`
- `src/main/java/UnitConverter.java`
- `src/main/java/com/example/unitconverter/UnitConverterService.java`

`main()` 함수는 루트 `UnitConverter.java`에서 확인되었다.

## 4. 최초 전체 테스트 실행

전체 테스트 실행 명령은 다음과 같았다.

```shell
mvn test
```

결과는 실패였다.

```text
Tests run: 46, Failures: 11, Errors: 0, Skipped: 0
BUILD FAILURE
```

실패 원인은 `UnitConverterDualTrackRedTest`에 남아 있는 의도된 RED placeholder였다.

대표 실패 메시지는 다음과 같았다.

```text
org.opentest4j.AssertionFailedError: RED
```

반면 기존 회귀 테스트인 `UnitConverterServiceTest`는 모두 통과했다.

```text
Running com.example.unitconverter.UnitConverterServiceTest
Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
```

## 5. 최초 JaCoCo 리포트 생성

커버리지 리포트 생성을 위해 다음 명령을 실행했다.

```shell
mvn jacoco:report
```

결과는 성공이었다.

```text
BUILD SUCCESS
Analyzed bundle 'unit-converter-a02' with 4 classes
```

리포트 위치는 다음과 같았다.

```text
target/site/jacoco/index.html
```

다만 전체 테스트가 RED placeholder 때문에 실패한 상태였으므로, 이 리포트는 최종 GREEN 기준 리포트로 확정하지 않았다.

## 6. RED Placeholder 제외 재검증

Prompt 2에 따라 `UnitConverterDualTrackRedTest`를 제외하고 테스트를 다시 실행했다.

```shell
mvn "-Dtest=!UnitConverterDualTrackRedTest" test
```

결과는 GREEN이었다.

```text
Running com.example.unitconverter.UnitConverterServiceTest
Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

이후 커버리지 실행 데이터가 이전 실패 실행과 섞이지 않도록 clean 후 재실행했다.

```shell
mvn clean "-Dtest=!UnitConverterDualTrackRedTest" test
```

결과는 다시 GREEN이었다.

```text
Running com.example.unitconverter.UnitConverterServiceTest
Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

이 실행 과정에서 JaCoCo 리포트도 재생성되었다.

## 7. 커버리지 확인 기록

`target/site/jacoco/jacoco.xml`을 확인했다.

`com.example.unitconverter` 패키지 기준 커버리지는 다음과 같았다.

```text
LINE: 124 covered / 124 total = 100%
BRANCH: 54 covered / 60 total = 90%
```

전체 bundle 기준 커버리지는 다음과 같았다.

```text
LINE: 124 covered / 128 total = 96.875%
BRANCH: 54 covered / 64 total = 84.375%
```

현재 프로젝트에는 별도 `entity`, `domain`, `boundary` 패키지가 분리되어 있지 않다.

따라서 Domain Logic은 `com.example.unitconverter` 패키지 및 `UnitConverterService` 중심으로 판단했고, 해당 기준에서는 95% 이상 조건을 충족했다.

Boundary Layer는 독립 패키지로 분리되어 있지 않아 85% 이상 조건을 별도 수치로 산정할 수 없었다.

## 8. 구조 조건 확인 기록

비율 값 잔존 여부를 다시 확인했다.

```shell
rg "3\.28084|1\.09361" --glob "*.java"
```

프로덕션 코드 기준 주요 결과는 다음과 같았다.

```text
src/main/java/UnitConverter.java
  private static final double FEET_PER_METER = 3.28084;

src/main/java/com/example/unitconverter/UnitConverterService.java
  public static final double FEET_PER_METER = 3.28084;
  public static final double YARD_PER_METER = 1.09361;
```

루트 `UnitConverter.java`에는 인라인 변환식이 남아 있었다.

```text
meterValue = value / 3.28084;
meterValue = value / 1.09361;
double inFeet = meterValue * 3.28084;
double inYards = meterValue * 1.09361;
```

`main()` 함수 잔존 여부도 확인했다.

```shell
rg "public\s+static\s+void\s+main" --glob "*.java"
```

결과는 다음과 같았다.

```text
UnitConverter.java
  public static void main(String[] args) {
```

즉, 루트 `UnitConverter.java`의 `main()`에는 아직 변환 로직이 남아 있다.

## 9. 최종 응답 요약

Prompt 1에 대해서는 전체 `mvn test` 기준으로 GREEN이 아님을 보고했다.

핵심 결과는 다음과 같았다.

- 전체 테스트: `46 tests, 11 failures`
- 실패 원인: `UnitConverterDualTrackRedTest`의 의도된 `fail("RED")`
- `UnitConverterServiceTest`: 34개 모두 PASS
- `mvn jacoco:report`: 성공
- 비율 값 및 `main()` 변환 로직은 일부 소스에 남아 있음

Prompt 2에 대해서는 RED placeholder 제외 기준으로 GREEN임을 보고했다.

핵심 결과는 다음과 같았다.

- 실행 명령: `mvn clean "-Dtest=!UnitConverterDualTrackRedTest" test`
- 결과: `34 tests, 0 failures, 0 errors, 0 skipped`
- 리포트 위치: `target/site/jacoco/index.html`
- `com.example.unitconverter` 패키지 line coverage: 100%
- 전체 bundle line coverage: 96.875%
- Boundary Layer는 독립 측정 불가
- 루트 `UnitConverter.java`에 인라인 비율과 `main()` 변환 로직이 남아 있음

## 10. PR 관련 기록

이번 세션에서는 PR 생성 요청이 별도로 없었다.

따라서 GitHub PR 생성이나 `gh pr create` 실행은 하지 않았다.

이번 세션에서 수행한 범위는 검증, 커버리지 확인, 구조 조건 확인, 보고서 작성이다.

## 11. 최종 산출물

이번 세션의 최종 산출물은 다음과 같다.

- `report/11_전체 GREEN 확인 및 PR_보고서.md`
- `Prompting/11_전체 GREEN 확인 및 PR_보고서-Prompting.md`
