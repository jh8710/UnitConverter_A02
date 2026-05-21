# 27_커버리지 & Golden Master 확인 후 PR 보고서

## 1. 작업 개요

이번 세션에서는 신규 기능 구현 완료 후 전체 테스트, 커버리지, Golden Master를 확인했다.

사용자 요청의 핵심 확인 항목은 다음과 같았다.

- 전체 테스트 실행
- JaCoCo 커버리지 리포트 생성
- Golden Master 출력 불변 확인
- BT-01~06 PASS 확인
- TC-A-01~07, TC-B-01~07 기존 회귀 PASS 확인
- `registerUnit("cubit", -1.0)` 호출 시 `IllegalArgumentException` 발생 확인
- Domain 커버리지 95% 이상, Boundary 커버리지 85% 이상 확인
- 이후 PR, 리뷰, 머지, 릴리스 태그 절차 진행 가능 여부 확인

## 2. 현재 브랜치 및 작업트리 확인

먼저 현재 Git 상태를 확인했다.

```shell
git status --short --branch
```

확인 결과 현재 브랜치는 `New_feature`였다.

```text
## New_feature
```

요청에 명시된 브랜치명은 `feature/new_feature`였으나, 실제 로컬 브랜치는 `New_feature`로 존재했다.

작업트리에는 다음 범주의 변경이 남아 있었다.

- `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java` 수정
- `src/main/java/DecayingUnitConverter.java` 신규 파일
- `src/test/java/DecayingUnitConverterTest.java` 신규 파일
- `Prompting/25...`, `Prompting/26...` 보고서 파일
- `report/25...`, `report/26...` 보고서 파일
- `target/` 아래 빌드 산출물 및 테스트/커버리지 산출물

## 3. 전체 테스트 확인

전체 Maven 테스트를 실행했다.

```shell
mvn test
```

실행 결과는 성공이었다.

```text
Tests run: 57, Failures: 0, Errors: 0, Skipped: 1
BUILD SUCCESS
```

실행된 주요 테스트 클래스는 다음과 같다.

- `com.example.unitconverter.GoldenMasterApprovalTest`
- `com.example.unitconverter.GoldenMasterTest`
- `com.example.unitconverter.UnitConverterDualTrackRedTest`
- `com.example.unitconverter.UnitConverterServiceTest`
- `DecayingUnitConverterTest`

스킵 1건은 다음 테스트였다.

```text
ui_json_output_request_returns_expected_schema
```

스킵 사유는 코드상 다음과 같이 명시되어 있었다.

```text
JSON output API is outside the current registerUnit GREEN scope
```

## 4. 커버리지 확인

JaCoCo 리포트를 생성했다.

```shell
mvn jacoco:report
```

결과는 성공이었다.

```text
BUILD SUCCESS
Analyzed bundle 'unit-converter-a02' with 5 classes
```

생성된 HTML 리포트 위치는 다음과 같다.

```text
target/site/jacoco/index.html
```

다만 이후 `mvn test -Dgroups=bonus` 실행이 `target/jacoco.exec`를 다시 쓸 수 있으므로, 최종 커버리지 확인을 위해 전체 테스트 후 리포트를 다시 생성했다.

```shell
mvn test; if ($LASTEXITCODE -eq 0) { mvn jacoco:report } else { exit $LASTEXITCODE }
```

최종 결과도 성공이었다.

```text
Tests run: 57, Failures: 0, Errors: 0, Skipped: 1
BUILD SUCCESS
```

`target/site/jacoco/jacoco.xml` 기준 커버리지 요약은 다음과 같다.

- 전체 TOTAL: line 98.08%, branch 84.62%, instruction 97.83%
- `com.example.unitconverter`: line 100.00%, branch 90.00%, instruction 100.00%
- default package: line 90.63%, branch 66.67%, instruction 89.40%

프로젝트에는 `domain`, `boundary` 전용 패키지가 분리되어 있지 않아 JaCoCo 리포트만으로 Domain/Boundary를 자동 분류할 수는 없었다.

현재 주요 구현 패키지인 `com.example.unitconverter` 기준으로는 line 100.00%, branch 90.00%로 요청 기준인 Domain 95% 이상, Boundary 85% 이상을 만족하는 수준이다.

## 5. Golden Master 확인

Golden Master는 전체 테스트에 포함되어 실행되었다.

관련 테스트 결과는 다음과 같았다.

```text
GoldenMasterApprovalTest: Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
GoldenMasterTest: Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
```

Golden Master가 보호하는 시나리오는 다음과 같다.

- `meter:2.5`
- `feet:1.0`
- `yard:1.0`
- `meter:0.0`

따라서 meter, feet, yard 출력 불변 검증은 PASS로 확인했다.

## 6. Bonus / 신규 기능 확인

bonus 그룹 테스트를 실행했다.

```shell
mvn test -Dgroups=bonus
```

결과는 성공이었다.

```text
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

실행된 테스트는 `DecayingUnitConverterTest`의 다음 6개 테스트였다.

- `decayingUnit_conversion`
- `decayingUnit_reverseConversion`
- `decayingUnit_crossConversionToFeet`
- `decayingUnit_negativeRatioThrowsIllegalArgumentException`
- `decayingUnit_convertAllReturnsEveryRegisteredUnit`
- `decayingUnit_existingMeterToFeetResultDoesNotChange`

이 결과로 BT-01~06 PASS를 확인했다.

## 7. 기존 회귀 테스트 확인

전체 테스트 결과에서 기존 회귀 테스트도 PASS로 확인했다.

`UnitConverterDualTrackRedTest` 결과는 다음과 같았다.

```text
Tests run: 12, Failures: 0, Errors: 0, Skipped: 1
```

`UnitConverterServiceTest` 결과는 다음과 같았다.

```text
Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
```

TC-A-01~07, TC-B-01~07에 해당하는 입력 검증, 변환 비율, 원 입력 보존, `convertAll`, `registerUnit`, config load, missing config 기본값 유지 회귀가 전체 테스트에서 통과했다.

단, JSON 출력 API 전용 테스트 1건은 현재 구현 범위 밖으로 `@Disabled` 처리되어 있어 실행 PASS가 아니라 의도적 스킵으로 남아 있다.

## 8. 음수 비율 예외 확인

요청된 정확한 호출을 별도로 확인했다.

```shell
jshell --class-path target/classes
```

실행한 확인 코드는 다음과 같다.

```java
import com.example.unitconverter.UnitConverterService;
try {
    new UnitConverterService().registerUnit("cubit", -1.0);
    System.out.println("NO_EXCEPTION");
} catch (IllegalArgumentException expected) {
    System.out.println("IllegalArgumentException: " + expected.getMessage());
}
```

결과는 다음과 같았다.

```text
IllegalArgumentException: metersPerUnit must be greater than zero
```

따라서 `registerUnit("cubit", -1.0)`은 `IllegalArgumentException` 발생으로 확인했다.

## 9. PR 진행 가능 여부 확인

요청된 이후 절차는 다음과 같았다.

```text
feature/new_feature -> A_01 PR 생성 -> 리뷰 -> 머지
A_01 -> main PR -> 릴리스 태그 v1.0.0
```

PR 단계 진행을 위해 다음을 확인했다.

```shell
git branch -vv
git remote -v
git log --oneline --decorate --graph -10
git rev-parse --verify A_01
gh auth status
```

확인 결과는 다음과 같았다.

- 로컬 `A_01` 브랜치는 존재한다.
- 현재 브랜치는 `New_feature`이며 요청의 `feature/new_feature`와 이름이 다르다.
- `origin` 원격은 `https://github.com/jh8710/UnitConverter_A02.git`로 설정되어 있다.
- `gh` 명령이 설치되어 있지 않거나 PATH에서 찾을 수 없어 GitHub CLI 기반 PR 생성이 불가능했다.

`gh auth status` 실행 결과는 다음과 같았다.

```text
gh : 'gh' ... 인식되지 않습니다.
```

따라서 이번 세션에서는 PR 생성, 리뷰, 머지, 릴리스 태그 생성까지는 진행하지 못했다.

## 10. 최종 판단

검증 결과는 다음과 같다.

- 전체 테스트: PASS
- 커버리지 리포트 생성: PASS
- Golden Master: PASS
- BT-01~06: PASS
- 기존 회귀 테스트: PASS
- `registerUnit("cubit", -1.0)` 예외: PASS
- 주요 구현 패키지 커버리지: line 100.00%, branch 90.00%
- PR 생성: 미진행

PR 생성이 미진행된 이유는 다음과 같다.

- 현재 브랜치명이 요청과 다름: `New_feature`
- 작업트리에 미커밋 변경과 `target/` 산출물이 남아 있음
- GitHub CLI `gh` 사용 불가

## 11. 최종 산출물

이번 세션의 최종 산출물은 다음과 같다.

- `report/27_커버리지 & Golden Master 확인 후 PR_보고서.md`
- `Prompting/27_커버리지 & Golden Master 확인 후 PR_보고서-Prompting.md`

이번 세션에서는 커밋, PR, 머지, 태그 생성을 수행하지 않았다.
