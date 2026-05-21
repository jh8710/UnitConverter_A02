# 27_커버리지 & Golden Master 확인 후 PR 보고서 - Prompting

## 1. 세션 목적

이번 세션의 목적은 신규 기능 구현 완료 후 전체 테스트, 커버리지, Golden Master를 확인하고, PR 및 릴리스 태그 진행 가능 여부를 점검하는 것이었다.

추가로, 세션에서 진행한 내용을 `report` 폴더와 `Prompting` 폴더에 보고서로 내보내는 작업을 수행했다.

## 2. 사용 프롬프트 기록

### Prompt 1. 테스트, 커버리지, Golden Master, PR 확인 요청

```text
신규 기능 구현 완료 후 전체 테스트, 커버리지, Golden Master를 확인하라.

1) 전체 테스트:
mvn test # 또는: gradle test

2) 커버리지:
mvn jacoco:report # target/site/jacoco/index.html

3) Golden Master (출력 불변 확인):
mvn test -Dgroups=bonus 또는: mvn test (전체 포함)

확인 항목:
- BT-01~06 PASS (신규 기능)
- TC-A-01~07, TC-B-01~07 PASS (기존 회귀)
- Golden Master PASS (meter/feet/yard 출력 불변)
- registerUnit("cubit", -1.0) → IllegalArgumentException 발생 확인
- Domain 커버리지 ≥ 95% / Boundary ≥ 85%

이후: feature/new_feature → A_01 PR 생성 → 리뷰 → 머지
최종: A_01 → main PR → 릴리스 태그 v1.0.0
```

### Prompt 2. 세션 보고서 및 Prompting 파일 내보내기 요청

```text
이번세션에서 진행한 내용을 report 폴더의 27_커버리지 & Golden Master 확인 후 PR_보고서 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더의 27_커버리지 & Golden Master 확인 후 PR_보고서-Prompting.md 파일로 내보내줘
```

## 3. 작업 응답 요약

Prompt 1에 대해서는 먼저 현재 터미널과 프로젝트 구성을 확인했다.

확인 결과 열려 있던 터미널에는 이전 `UnitConverterDualTrackRedTest` RED 실패 흔적이 남아 있었다.

대표 로그는 다음과 같았다.

```text
UnitConverterDualTrackRedTest.register_unit_allows_conversion_with_new_unit
org.opentest4j.AssertionFailedError: RED
```

이 로그는 이전 실행 결과일 수 있으므로 현재 워크스페이스에서 테스트를 다시 실행했다.

## 4. 현재 상태 확인 기록

현재 브랜치와 변경 상태를 확인했다.

```shell
git status --short --branch
```

결과는 다음과 같았다.

```text
## New_feature
```

작업트리에는 Java 소스/테스트 변경과 보고서 파일, `target/` 산출물이 함께 남아 있었다.

이후 PR 진행 가능 여부를 위해 다음 명령도 실행했다.

```shell
git branch -vv
git remote -v
git log --oneline --decorate --graph -10
git rev-parse --verify A_01
```

확인 결과 `A_01` 브랜치는 존재했고, 원격은 다음과 같이 설정되어 있었다.

```text
origin  https://github.com/jh8710/UnitConverter_A02.git
```

다만 현재 브랜치는 요청의 `feature/new_feature`가 아니라 `New_feature`였다.

## 5. 전체 테스트 실행 기록

전체 테스트를 실행했다.

```shell
mvn test
```

결과는 성공이었다.

```text
Tests run: 57, Failures: 0, Errors: 0, Skipped: 1
BUILD SUCCESS
```

실행된 테스트 클래스는 다음과 같았다.

- `com.example.unitconverter.GoldenMasterApprovalTest`
- `com.example.unitconverter.GoldenMasterTest`
- `com.example.unitconverter.UnitConverterDualTrackRedTest`
- `com.example.unitconverter.UnitConverterServiceTest`
- `DecayingUnitConverterTest`

스킵된 테스트는 다음 1건이었다.

```text
ui_json_output_request_returns_expected_schema
```

스킵 사유는 다음과 같았다.

```text
JSON output API is outside the current registerUnit GREEN scope
```

## 6. 커버리지 실행 기록

커버리지 리포트를 생성했다.

```shell
mvn jacoco:report
```

결과는 성공이었다.

```text
BUILD SUCCESS
Analyzed bundle 'unit-converter-a02' with 5 classes
```

이후 `mvn test -Dgroups=bonus`가 `target/jacoco.exec`를 다시 쓸 수 있으므로, 최종 커버리지 기준을 전체 테스트 기준으로 맞추기 위해 다음 명령을 실행했다.

```shell
mvn test; if ($LASTEXITCODE -eq 0) { mvn jacoco:report } else { exit $LASTEXITCODE }
```

최종 실행 결과도 성공이었다.

```text
Tests run: 57, Failures: 0, Errors: 0, Skipped: 1
BUILD SUCCESS
```

커버리지 리포트 파일은 다음 위치에 생성되었다.

```text
target/site/jacoco/index.html
target/site/jacoco/jacoco.xml
```

`jacoco.xml`에서 확인한 요약은 다음과 같았다.

- 전체 TOTAL: line 98.08%, branch 84.62%, instruction 97.83%
- `com.example.unitconverter`: line 100.00%, branch 90.00%, instruction 100.00%
- default package: line 90.63%, branch 66.67%, instruction 89.40%

프로젝트에는 `domain`, `boundary` 전용 패키지 구조가 없어 JaCoCo 리포트만으로 Domain/Boundary를 자동 분류할 수 없다고 판단했다.

## 7. Golden Master 실행 기록

Golden Master는 전체 테스트에서 함께 실행되었다.

관련 결과는 다음과 같았다.

```text
GoldenMasterApprovalTest: Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
GoldenMasterTest: Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
```

검색으로 확인한 Golden Master 시나리오는 다음과 같았다.

- `meter:2.5`
- `feet:1.0`
- `yard:1.0`
- `meter:0.0`

따라서 meter, feet, yard 출력 불변 검증은 PASS로 판단했다.

## 8. Bonus 그룹 실행 기록

bonus 그룹 테스트를 실행했다.

```shell
mvn test -Dgroups=bonus
```

결과는 성공이었다.

```text
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

실행된 테스트는 `DecayingUnitConverterTest`의 다음 테스트들이었다.

- `decayingUnit_conversion`
- `decayingUnit_reverseConversion`
- `decayingUnit_crossConversionToFeet`
- `decayingUnit_negativeRatioThrowsIllegalArgumentException`
- `decayingUnit_convertAllReturnsEveryRegisteredUnit`
- `decayingUnit_existingMeterToFeetResultDoesNotChange`

이로써 BT-01~06 PASS를 확인했다.

## 9. 회귀 테스트 확인 기록

`UnitConverterDualTrackRedTest` 결과는 다음과 같았다.

```text
Tests run: 12, Failures: 0, Errors: 0, Skipped: 1
```

`UnitConverterServiceTest` 결과는 다음과 같았다.

```text
Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
```

기존 TC-A/TC-B 회귀 항목에 해당하는 입력 검증, 변환 비율, 원 입력 보존, 전체 변환, 동적 단위 등록, config load, missing config 기본값 유지가 전체 테스트에서 통과했다.

단, JSON 출력 API 전용 테스트 1건은 현재 scope 밖으로 `@Disabled` 처리되어 있어 실행된 PASS가 아니라 의도적 스킵으로 기록했다.

## 10. 음수 비율 예외 확인 기록

사용자가 요청한 정확한 호출을 JShell로 확인했다.

```shell
@'
import com.example.unitconverter.UnitConverterService;
try { new UnitConverterService().registerUnit("cubit", -1.0); System.out.println("NO_EXCEPTION"); } catch (IllegalArgumentException expected) { System.out.println("IllegalArgumentException: " + expected.getMessage()); }
/exit
'@ | jshell --class-path target/classes
```

결과는 다음과 같았다.

```text
IllegalArgumentException: metersPerUnit must be greater than zero
```

따라서 `registerUnit("cubit", -1.0)`은 `IllegalArgumentException`을 발생시키는 것으로 확인했다.

## 11. PR 진행 확인 기록

PR 생성 가능 여부를 확인하기 위해 GitHub CLI 인증 상태를 확인했다.

```shell
gh auth status
```

결과는 실패였다.

```text
gh : 'gh' ... 인식되지 않습니다.
```

따라서 GitHub CLI가 설치되어 있지 않거나 PATH에서 찾을 수 없는 상태로 판단했다.

이번 세션에서는 다음 후속 단계는 진행하지 못했다.

- `feature/new_feature -> A_01` PR 생성
- 리뷰
- 머지
- `A_01 -> main` PR 생성
- 릴리스 태그 `v1.0.0` 생성

차단 사유는 다음과 같았다.

- 현재 브랜치가 `feature/new_feature`가 아니라 `New_feature`
- 작업트리에 미커밋 변경과 빌드 산출물이 남아 있음
- GitHub CLI `gh` 사용 불가

## 12. 보고서 생성 기록

Prompt 2에 따라 기존 26번 보고서 형식을 참고했다.

확인한 파일은 다음과 같다.

- `report/26_GREEN OCP 준수 최소 구현_보고서.md`
- `Prompting/26_GREEN OCP 준수 최소 구현_보고서-Prompting.md`

이후 다음 파일을 새로 생성했다.

- `report/27_커버리지 & Golden Master 확인 후 PR_보고서.md`
- `Prompting/27_커버리지 & Golden Master 확인 후 PR_보고서-Prompting.md`

파일명에 포함된 `&` 문자는 Windows 파일명에서 사용할 수 있으므로 그대로 유지했다.

## 13. 최종 산출물

이번 세션의 최종 산출물은 다음과 같다.

- `report/27_커버리지 & Golden Master 확인 후 PR_보고서.md`
- `Prompting/27_커버리지 & Golden Master 확인 후 PR_보고서-Prompting.md`

이번 세션에서는 보고서 파일만 생성했으며, 커밋/PR/머지/태그 생성은 수행하지 않았다.
