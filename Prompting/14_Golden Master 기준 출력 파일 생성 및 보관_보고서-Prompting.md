# 14_Golden Master 기준 출력 파일 생성 및 보관 보고서 - Prompting

## 1. 세션 목적

이번 세션의 목적은 `UnitConverter_A02` 프로젝트에 출력 기반 회귀 테스트를 위한 Golden Master 기준 파일을 만들고, JUnit 5 기반 approve 패턴으로 기준 출력 비교를 자동화하는 것이었다.

주요 요구는 다음과 같았다.

- 기준 파일 생성 위치: `src/test/resources/golden_master_expected.txt`
- 입력 시나리오 4개 고정
- 기준 파일 없을 때 자동 생성
- 기준 파일 있을 때 actual vs expected 비교
- 불일치 시 diff 출력 후 테스트 실패
- 기준 파일 생성 후 `git add` 수행
- 기준 파일 내용과 생성 스크립트 제공

## 2. 사용 프롬프트 기록

### Prompt 1. Golden Master 기준 출력 파일 생성 요청

```text
[P] 회귀 테스트(Approval/Golden Master) 설계 전문가입니다.
[C] Java 17, Gradle/Maven, JUnit 5
[T] UnitConverter 출력 기반 Golden Master 기준 파일을 생성해줘.

    1) 기준 출력(src/test/resources/golden_master_expected.txt) 생성/보관 전략
       입력 시나리오 4개:
         "meter:2.5" / "feet:1.0" / "yard:1.0" / "meter:0.0"
       캡처 방식: System.setOut(new PrintStream(baos))        또는 ./run.sh > actual.txt
       생성 후: git add src/test/resources/golden_master_expected.txt  (버전 관리 필수)

    2) approve 패턴 적용:
       - 기준 파일 없으면 → 현재 출력을 기준으로 자동 생성
       - 기준 파일 있으면 → actual vs expected 문자열 비교
       - 불일치 시: diff 출력 후 테스트 FAIL

    3) 기준 파일 구조:
       [meter:2.5]
       2.5 meter = 8.202100 feet
       2.5 meter = 2.734025 yard
       ---
       [feet:1.0]
       ...

[F] src/test/resources/golden_master_expected.txt 파일 내용 + 생성 스크립트
```

### Prompt 2. 세션 보고서 및 Prompting 파일 내보내기 요청

```text
이번세션에서 진행한 내용을 report 폴더의 14_Golden Master 기준 출력 파일 생성 및 보관_보고서 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더의 14_Golden Master 기준 출력 파일 생성 및 보관_보고서-Prompting.md 파일로 내보내줘
```

## 3. 프로젝트 탐색 기록

처음에는 프로젝트의 빌드 도구, 실행 방식, 기존 테스트 구조를 확인했다.

확인한 주요 파일은 다음과 같다.

- `pom.xml`
- `src/main/java/UnitConverter.java`
- `UnitConverter.java`
- `src/main/java/com/example/unitconverter/UnitConverterService.java`
- `src/main/java/com/example/unitconverter/ConversionResult.java`
- `src/test/java/com/example/unitconverter/UnitConverterServiceTest.java`
- `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`

확인 결과는 다음과 같았다.

- 프로젝트는 Gradle이 아니라 Maven 기반이었다.
- `run.sh`는 존재하지 않았다.
- `UnitConverterService`가 테스트 가능한 변환 API를 제공하고 있었다.
- `convertInputToConsoleLines`는 소수점 1자리 표시 계약을 사용하고 있었다.
- 사용자 요청의 Golden Master 예시는 소수점 6자리 정밀 출력이므로, Golden Master 전용 렌더링을 테스트 내부에 구성했다.

## 4. 설계 판단 기록

캡처 방식은 `System.setOut(new PrintStream(baos))`를 선택했다.

선택 이유는 다음과 같다.

- 저장소에 `run.sh`가 없어 쉘 리다이렉션 방식이 바로 적용되지 않았다.
- JUnit 5 테스트 내부에서 출력 캡처와 approve 비교를 한 번에 검증할 수 있었다.
- Maven 테스트 흐름에 자연스럽게 포함할 수 있었다.

기준 파일 재생성은 다음 조건으로 제한했다.

- 기준 파일이 없을 때 자동 생성
- 또는 `-Dgolden.master.update=true` 옵션이 있을 때 명시적으로 갱신

일반 테스트 실행에서는 기준 파일이 존재하면 항상 actual과 expected를 비교하도록 했다.

## 5. 구현 기록

추가한 테스트 파일은 다음과 같다.

```text
src/test/java/com/example/unitconverter/GoldenMasterApprovalTest.java
```

테스트의 핵심 흐름은 다음과 같다.

- `SCENARIOS`에 `meter:2.5`, `feet:1.0`, `yard:1.0`, `meter:0.0`를 고정한다.
- `ByteArrayOutputStream`과 `PrintStream`으로 `System.out` 출력을 캡처한다.
- 각 시나리오를 `[input]` 헤더로 출력한다.
- `UnitConverterService.convertAll(unit, value)` 결과를 `%.6f` 형식으로 출력한다.
- 시나리오 사이에는 `---` 구분자를 출력한다.
- 기준 파일이 없거나 update 옵션이 있으면 현재 출력을 파일로 저장한다.
- 기준 파일이 있으면 문자열 비교를 수행한다.
- 불일치 시 라인 단위 diff 메시지를 만들어 JUnit failure 메시지로 제공한다.

추가한 생성 스크립트는 다음과 같다.

```text
scripts/generate-golden-master.ps1
```

스크립트는 다음 명령을 실행한다.

```powershell
mvn "-Dtest=GoldenMasterApprovalTest" "-Dgolden.master.update=true" test
git add "src/test/resources/golden_master_expected.txt"
```

## 6. 생성된 Golden Master 기준 파일

생성된 파일 위치는 다음과 같다.

```text
src/test/resources/golden_master_expected.txt
```

생성된 파일 내용은 다음과 같다.

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

## 7. 문제 해결 기록

처음 생성 스크립트를 실행했을 때 PowerShell이 Maven `-Dgolden.master.update=true` 인자를 잘못 해석했다.

실패 메시지는 다음과 같았다.

```text
Unknown lifecycle phase ".master.update=true"
```

원인은 PowerShell에서 점이 포함된 `-D` 인자가 Maven에 의도대로 전달되지 않은 것이었다.

수정 전 명령은 다음과 같았다.

```powershell
mvn -Dtest=GoldenMasterApprovalTest -Dgolden.master.update=true test
```

수정 후 명령은 다음과 같다.

```powershell
mvn "-Dtest=GoldenMasterApprovalTest" "-Dgolden.master.update=true" test
```

또한 Maven 실패 후에도 스크립트가 계속 진행하지 않도록 `$LASTEXITCODE` 확인을 추가했다.

## 8. 검증 기록

기준 파일 생성 스크립트를 실행했다.

```powershell
powershell -ExecutionPolicy Bypass -File scripts/generate-golden-master.ps1
```

결과는 성공이었다.

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
Generated and staged src/test/resources/golden_master_expected.txt
```

일반 비교 모드로 테스트를 다시 실행했다.

```shell
mvn "-Dtest=GoldenMasterApprovalTest" test
```

결과는 성공이었다.

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

편집한 테스트 파일에 linter 오류가 없는지도 확인했다.

```text
No linter errors found.
```

## 9. 최종 응답 요약

Prompt 1에 대한 최종 응답에서는 다음 내용을 전달했다.

- 추가된 핵심 파일
- `golden_master_expected.txt` 전체 내용
- 생성 스크립트 실행 명령
- 스크립트가 기준 파일 재생성과 `git add`를 수행한다는 점
- `mvn "-Dtest=GoldenMasterApprovalTest" test` 검증 성공
- `src/test/resources/golden_master_expected.txt`가 staged 상태라는 점

## 10. 최종 산출물

이번 세션에서 생성한 최종 산출물은 다음과 같다.

- `src/test/resources/golden_master_expected.txt`
- `src/test/java/com/example/unitconverter/GoldenMasterApprovalTest.java`
- `scripts/generate-golden-master.ps1`
- `report/14_Golden Master 기준 출력 파일 생성 및 보관_보고서.md`
- `Prompting/14_Golden Master 기준 출력 파일 생성 및 보관_보고서-Prompting.md`
