# 14_Golden Master 기준 출력 파일 생성 및 보관 보고서

## 1. 작업 개요

이번 세션에서는 `UnitConverter_A02` 프로젝트에 출력 기반 회귀 테스트를 위한 Golden Master 기준 파일과 approve 패턴 검증 테스트를 추가했다.

요청된 핵심 목표는 다음과 같았다.

- `src/test/resources/golden_master_expected.txt` 기준 출력 파일 생성 및 보관
- 입력 시나리오 4개 고정
  - `meter:2.5`
  - `feet:1.0`
  - `yard:1.0`
  - `meter:0.0`
- 기준 파일이 없으면 현재 출력을 자동 생성
- 기준 파일이 있으면 actual vs expected 문자열 비교
- 불일치 시 diff 메시지를 출력하고 테스트 실패
- 생성 후 `git add src/test/resources/golden_master_expected.txt` 수행
- 기준 파일 내용과 생성 스크립트 제공

## 2. 프로젝트 확인 결과

프로젝트는 Maven 기반 Java 프로젝트였다.

확인한 주요 파일은 다음과 같다.

- `pom.xml`
- `src/main/java/com/example/unitconverter/UnitConverterService.java`
- `src/main/java/com/example/unitconverter/ConversionResult.java`
- `src/test/java/com/example/unitconverter/UnitConverterServiceTest.java`
- `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`
- `UnitConverter.java`

`run.sh`는 존재하지 않았으므로, 별도 쉘 실행 캡처보다 JUnit 5 테스트 내부에서 `System.setOut(new PrintStream(baos))` 방식으로 현재 출력을 캡처하는 전략을 선택했다.

## 3. Golden Master 기준 파일 구조

생성된 기준 파일은 다음 위치에 저장했다.

```text
src/test/resources/golden_master_expected.txt
```

기준 파일은 요청한 블록 구조를 따른다.

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

출력 정밀도는 Golden Master 기준에 맞춰 소수점 6자리 `%.6f`로 고정했다.

## 4. Approve 패턴 적용

새 테스트 파일을 추가했다.

```text
src/test/java/com/example/unitconverter/GoldenMasterApprovalTest.java
```

테스트 동작은 다음과 같다.

- `SCENARIOS`에 4개 입력 시나리오를 고정한다.
- `System.setOut(capture)`로 현재 Golden Master 출력을 캡처한다.
- `src/test/resources/golden_master_expected.txt`가 없으면 현재 출력을 기준 파일로 자동 생성한다.
- `-Dgolden.master.update=true`가 주어지면 기준 파일을 현재 출력으로 재생성한다.
- 기준 파일이 있으면 파일 내용과 현재 출력을 문자열로 비교한다.
- 줄바꿈은 `\n` 기준으로 정규화해 Windows CRLF 차이로 인한 실패를 줄였다.
- 불일치 시 라인 단위 diff 메시지를 assertion failure 메시지로 출력한다.

대표 실행 명령은 다음과 같다.

```shell
mvn "-Dtest=GoldenMasterApprovalTest" test
```

기준 파일 재생성 명령은 다음과 같다.

```shell
mvn "-Dtest=GoldenMasterApprovalTest" "-Dgolden.master.update=true" test
```

## 5. 생성 스크립트

기준 파일 생성 및 staging 자동화를 위해 PowerShell 스크립트를 추가했다.

```text
scripts/generate-golden-master.ps1
```

스크립트 동작은 다음과 같다.

1. 저장소 루트로 이동한다.
2. `GoldenMasterApprovalTest`를 `-Dgolden.master.update=true` 옵션으로 실행한다.
3. Maven 실행 실패 시 오류로 중단한다.
4. `git add src/test/resources/golden_master_expected.txt`를 수행한다.
5. staging 실패 시 오류로 중단한다.

실행 명령은 다음과 같다.

```powershell
powershell -ExecutionPolicy Bypass -File scripts/generate-golden-master.ps1
```

초기 스크립트 실행에서는 PowerShell이 Maven `-Dgolden.master.update=true` 인자를 잘못 해석해 실패했다.

실패 메시지는 다음과 같았다.

```text
Unknown lifecycle phase ".master.update=true"
```

이를 해결하기 위해 Maven `-D` 인자를 각각 따옴표로 감싸도록 수정했다.

```powershell
mvn "-Dtest=GoldenMasterApprovalTest" "-Dgolden.master.update=true" test
```

## 6. 검증 결과

기준 파일 생성 스크립트를 실행해 Golden Master 기준 파일을 생성했다.

```powershell
powershell -ExecutionPolicy Bypass -File scripts/generate-golden-master.ps1
```

결과는 성공이었다.

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
Generated and staged src/test/resources/golden_master_expected.txt
```

그 후 일반 비교 모드로 테스트를 다시 실행했다.

```shell
mvn "-Dtest=GoldenMasterApprovalTest" test
```

결과는 성공이었다.

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

편집한 테스트 파일에 대해서 linter 확인도 수행했고, 오류는 없었다.

```text
No linter errors found.
```

## 7. Git 상태

요구사항에 따라 기준 파일을 staging했다.

```text
git add src/test/resources/golden_master_expected.txt
```

staged 파일은 다음과 같다.

```text
src/test/resources/golden_master_expected.txt
```

테스트 실행으로 `target` 산출물이 갱신되었지만, 기준 파일 외에는 자동 staging하지 않았다.

## 8. 최종 산출물

이번 세션의 최종 산출물은 다음과 같다.

- `src/test/resources/golden_master_expected.txt`
- `src/test/java/com/example/unitconverter/GoldenMasterApprovalTest.java`
- `scripts/generate-golden-master.ps1`

Golden Master 기준 파일은 버전 관리 대상으로 staging되었고, 이후 회귀 테스트에서는 기준 파일이 존재할 때 actual 출력과 expected 기준 출력이 일치해야 통과한다.
