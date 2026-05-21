# UnitConverter Java Phase 6 Report

## 1. 작업 개요

- 프로젝트: `UnitConverter_A02`
- 브랜치: `refactoring`
- 날짜: 2026-05-21
- 작업자: 정환 / GPT-5.5 Cursor Agent
- 보고 범위: Phase 6 기준 RED, GREEN, Golden Master, Refactoring, 커버리지 점검 결과 종합

이번 Phase 6 작업은 Java Maven 기반 단위 변환기에서 Dual-Track TDD 흐름을 점검하고, RED 테스트 스켈레톤, GREEN 전환 항목, Golden Master 회귀 안전장치, 리팩토링 가능 여부, JaCoCo 커버리지를 종합 확인하는 데 초점을 두었다.

## 2. 완료된 To-Do 항목 요약

### Golden Master 회귀 안전장치

완료된 항목은 다음과 같다.

- GM-01: `golden_master_expected.txt` 기준 파일 생성
- GM-02: `feet:1.0`, `yard:1.0`, `meter:0.0` 시나리오 추가
- GM-03: 기준 파일을 버전 관리 대상으로 준비
- GM-04: `GoldenMasterTest.java`와 기준 파일 작성
- GM-05: 기준 파일이 없으면 생성하고, 있으면 비교하는 approve 패턴 적용
- GM-06: `mvn test -Dgroups=golden_master` PASS 확인
- GM-09: Refactoring 이후 Golden Master 재실행 PASS 확인

미완료 또는 외부 설정이 필요한 항목은 다음과 같다.

- GM-07: `.github/workflows/golden_master.yml` 작성
- GM-08: PR required status check 설정

### Dual-Track RED To-Do

`UnitConverterDualTrackRedTest` 기준으로 실제 assertion으로 전환되어 통과한 항목은 다음과 같다.

- TC-A-01: 정상 입력 `meter:2.5` 변환 결과 반환
- TC-A-02: `:` 없는 입력에 대한 `IllegalArgumentException`
- TC-B-01: `meter -> feet` 변환 비율 검증

서비스 회귀 테스트인 `UnitConverterServiceTest`에는 README의 주요 계약이 더 넓게 구현되어 있으며, 34개 테스트가 모두 통과한다. 다만 Phase 6 RED 스켈레톤 파일 안에는 아직 9개의 `fail("RED")` placeholder가 남아 있어 전체 `mvn test`는 GREEN이 아니다.

## 3. RED 단계 결과

작성된 RED 테스트 파일은 `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`이다.

최초 RED 스켈레톤은 총 12개 테스트로 구성되었다.

- `ui_valid_meter_input_returns_conversion_result`
- `ui_missing_colon_input_throws_illegal_argument_exception`
- `ui_negative_value_input_throws_illegal_argument_exception`
- `ui_unknown_unit_input_throws_illegal_argument_exception`
- `ui_output_preserves_original_unit_and_value`
- `ui_json_output_request_returns_expected_schema`
- `convert_meter_to_feet_returns_correct_ratio`
- `convert_meter_to_yard_returns_correct_ratio`
- `convert_all_returns_all_registered_unit_conversions`
- `register_unit_allows_conversion_with_new_unit`
- `load_config_applies_json_and_yaml_ratios`
- `load_config_missing_path_keeps_default_ratios`

RED 실패 확인 결과:

- 최초 스켈레톤 기준: 12개 테스트가 의도적으로 실패
- 현재 기준: 3개는 GREEN assertion으로 전환되어 통과, 9개는 `fail("RED")`로 실패 유지
- 최신 전체 실행 결과: `Tests run: 51, Failures: 9, Errors: 0, Skipped: 0`

현재 남아 있는 RED 실패는 모두 구현 회귀가 아니라 의도적으로 남겨둔 placeholder 실패다.

## 4. GREEN 단계 결과

GREEN으로 확인한 테스트는 다음과 같다.

- `UnitConverterServiceTest`: 34개 통과
- `GoldenMasterApprovalTest`: 1개 통과
- `GoldenMasterTest`: 4개 통과
- `UnitConverterDualTrackRedTest` 중 전환 완료된 3개 테스트 통과

최신 검증 명령과 결과:

```powershell
mvn "-Dtest=!UnitConverterDualTrackRedTest" test
```

결과:

```text
Tests run: 39, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

관련 커밋 메시지는 다음과 같다.

- `99bd573 test: cover valid meter boundary conversion`
- `6980920 test(green): cover meter feet and missing colon`
- `8f45e2d GREEN 프롬프트 2 - Dual-Track 상세 버전`
- `bf22faf Golden Master 자동화 -  회귀 안전장치`
- `a31f8dd Golden Master 자동화 -  테스트 코드 구현`

## 5. Refactoring 결과

선택 항목:

- 제품 코드 리팩토링은 보류했다.
- 이유: 전체 `mvn test` 기준선이 `UnitConverterDualTrackRedTest`의 RED placeholder 9건 때문에 GREEN이 아니었으므로, 리팩토링으로 인한 회귀와 기존 RED 실패를 구분하기 어렵다.

변경 파일:

- 제품 코드 변경 없음
- 리팩토링 커밋 `584e77b Dual-Track REFACTOR`에는 테스트 실행 산출물인 `target/jacoco.exec`, `target/surefire-reports/*` 변경만 포함됨
- 보고서 커밋 `7312c63 Dual-Track REFACTOR 보고서`에는 `report/21_Dual-Track REFACTOR_보고서.md`와 관련 Prompting 문서가 포함됨

회귀 테스트 통과 여부:

- 전체 `mvn test`: FAIL, RED placeholder 9건 때문
- RED placeholder 제외 회귀 기준: PASS, 39개 테스트 통과
- Golden Master 기준: PASS, 5개 테스트 통과

## 6. 커버리지 현황

JaCoCo 기준 파일:

- `target/site/jacoco/index.html`
- `target/site/jacoco/jacoco.xml`
- `target/site/jacoco/jacoco.csv`

전체 bundle 기준:

- Line: 124 covered / 128 total = 96.9%
- Branch: 54 covered / 64 total = 84.4%

활성 서비스 패키지 `com.example.unitconverter` 기준:

- Line: 124 covered / 124 total = 100.0%
- Branch: 54 covered / 60 total = 90.0%

파일별 핵심 수치:

- `UnitConverterService`: Line 122/122 = 100.0%, Branch 54/60 = 90.0%
- `ConversionResult`: Line 1/1 = 100.0%
- `UnitConverterService.ParsedInput`: Line 1/1 = 100.0%
- 기본 패키지 `UnitConverter`: Line 0/4 = 0.0%, Branch 0/4 = 0.0%

레이어별 판정:

- Domain Logic: 별도 domain 패키지는 없고 `UnitConverterService`에 포함되어 있다. 활성 서비스 기준 Line 100.0%로 목표 95% 이상을 충족한다.
- Boundary Layer: 입력 파싱과 콘솔 출력 포맷이 `UnitConverterService`에 함께 있다. 독립 레이어 수치는 산정할 수 없지만 활성 서비스 기준 Line 100.0%, Branch 90.0%로 README 목표 85% 이상은 충족한다.
- Legacy/default layer: 기본 패키지 `UnitConverter`는 0% 커버리지로 남아 있어 전체 branch coverage를 낮추는 주요 원인이다.

## 7. 미완료 항목 및 다음 단계 제안

미완료 항목:

- `UnitConverterDualTrackRedTest`의 남은 9개 `fail("RED")`를 실제 assertion으로 전환
- JSON 출력 스키마 테스트 및 구현 여부 결정
- Golden Master GitHub Actions 워크플로 작성
- PR required status check 설정
- 기본 패키지 `UnitConverter` 유지, 삭제, 또는 커버리지 제외 여부 결정
- Domain, Boundary, Data 책임을 별도 클래스로 나눌지 결정

다음 단계 제안:

1. 남은 RED placeholder를 한 번에 제거하지 말고, TC-A/TC-B 단위로 1~2개씩 GREEN 전환한다.
2. 전체 `mvn test`가 GREEN이 된 뒤에만 제품 코드 리팩토링을 다시 시작한다.
3. `UnitConverterService`에서 단위 저장소, 계산, 입력 파싱, 출력 포맷 책임을 분리해 레이어별 커버리지를 측정 가능하게 만든다.
4. 기본 패키지 `UnitConverter`가 학습용 레거시인지 공개 API인지 결정하고, 유지한다면 테스트를 추가한다.

## 8. 발견된 이슈 및 해결 방법

이슈 1: 전체 테스트가 GREEN이 아님

- 원인: `UnitConverterDualTrackRedTest`에 의도된 RED placeholder 9건이 남아 있음
- 해결: 현재 보고서에서는 전체 실패와 placeholder 제외 회귀 PASS를 분리해 기록했다. 다음 작업에서 placeholder를 실제 assertion으로 전환해야 한다.

이슈 2: PowerShell에서 Maven `-D` 인자 해석 문제가 발생할 수 있음

- 원인: PowerShell이 `-Dgroups=...`, `-Dgolden.master.update=true`, `-Dtest=...` 인자를 의도와 다르게 해석할 수 있음
- 해결: Maven 속성 인자를 따옴표로 감싼다.

```powershell
mvn "-Dtest=!UnitConverterDualTrackRedTest" test
mvn "-Dtest=GoldenMasterApprovalTest" "-Dgolden.master.update=true" test
```

이슈 3: 커버리지 수치와 레이어 목표를 1:1로 매핑하기 어려움

- 원인: `UnitConverterService`가 Domain, Boundary, Data 성격의 책임을 함께 가지고 있음
- 해결: 현재는 활성 서비스 기준으로 판정하고, 이후 구조 분리 후 레이어별 커버리지를 재산정한다.

이슈 4: 기본 패키지 `UnitConverter` 0% 커버리지

- 원인: 현재 회귀 테스트는 주로 `com.example.unitconverter.UnitConverterService`를 대상으로 실행됨
- 해결: 유지 대상이면 테스트를 추가하고, 폐기 대상이면 삭제 또는 JaCoCo 제외 여부를 별도로 결정한다.

## 9. 생성형 AI 활용 회고

도움이 된 순간:

- Phase별 보고서와 테스트 파일을 교차 확인해 RED, GREEN, Refactoring 상태를 한 번에 정리할 수 있었다.
- 실패 원인이 제품 결함인지 의도된 RED placeholder인지 빠르게 분리할 수 있었다.
- JaCoCo CSV/XML을 기준으로 전체 수치와 활성 서비스 수치를 나누어 해석할 수 있었다.

한계:

- 기존 대화 맥락만으로는 "Phase 6 To-Do"의 완료 여부가 코드 기준인지 README 체크박스 기준인지 모호했다.
- 레이어가 코드 구조로 분리되어 있지 않아 Domain/Boundary 커버리지 수치를 정확히 독립 계산할 수 없었다.
- 생성형 AI가 테스트 통과 여부를 추정하면 위험하므로, Maven 실행 결과와 JaCoCo 산출물 확인이 반드시 필요했다.

TC 작성 팁:

- RED 스켈레톤은 오래 남겨두지 말고, 실패 이유가 명확한 assertion으로 빠르게 전환한다.
- UI/Boundary 테스트는 입력 문자열, 예외 타입, 출력 포맷을 한 테스트에 너무 많이 섞지 않는다.
- Domain 테스트는 변환 비율과 허용 오차를 명시하고, 반올림 출력 테스트와 정밀 계산 테스트를 분리한다.
- Golden Master는 사람이 읽는 출력의 회귀 안전장치로 두고, 계산 정확도는 별도 단위 테스트로 보호한다.
- PowerShell 환경에서는 Maven `-D` 옵션을 항상 따옴표로 감싸 재현 가능한 명령을 남긴다.
# UnitConverter Java Phase 6 Report

## 1. 작업 개요

- 프로젝트: `UnitConverter_A02`
- 브랜치: `refactoring`
- 날짜: 2026-05-21
- 작업자: 정환 / GPT-5.5 Cursor Agent
- 보고 범위: Phase 6 기준 RED, GREEN, Golden Master, Refactoring, 커버리지 점검 결과 종합

이번 Phase 6 작업은 Java Maven 기반 단위 변환기에서 Dual-Track TDD 흐름을 점검하고, RED 테스트 스켈레톤, GREEN 전환 항목, Golden Master 회귀 안전장치, 리팩토링 가능 여부, JaCoCo 커버리지를 종합 확인하는 데 초점을 두었다.

## 2. 완료된 To-Do 항목 요약

### Golden Master 회귀 안전장치

완료된 항목은 다음과 같다.

- GM-01: `golden_master_expected.txt` 기준 파일 생성
- GM-02: `feet:1.0`, `yard:1.0`, `meter:0.0` 시나리오 추가
- GM-03: 기준 파일을 버전 관리 대상으로 준비
- GM-04: `GoldenMasterTest.java`와 기준 파일 작성
- GM-05: 기준 파일이 없으면 생성하고, 있으면 비교하는 approve 패턴 적용
- GM-06: `mvn test -Dgroups=golden_master` PASS 확인
- GM-09: Refactoring 이후 Golden Master 재실행 PASS 확인

미완료 또는 외부 설정이 필요한 항목은 다음과 같다.

- GM-07: `.github/workflows/golden_master.yml` 작성
- GM-08: PR required status check 설정

### Dual-Track RED To-Do

`UnitConverterDualTrackRedTest` 기준으로 실제 assertion으로 전환되어 통과한 항목은 다음과 같다.

- TC-A-01: 정상 입력 `meter:2.5` 변환 결과 반환
- TC-A-02: `:` 없는 입력에 대한 `IllegalArgumentException`
- TC-B-01: `meter -> feet` 변환 비율 검증

서비스 회귀 테스트인 `UnitConverterServiceTest`에는 README의 주요 계약이 더 넓게 구현되어 있으며, 34개 테스트가 모두 통과한다. 다만 Phase 6 RED 스켈레톤 파일 안에는 아직 9개의 `fail("RED")` placeholder가 남아 있어 전체 `mvn test`는 GREEN이 아니다.

## 3. RED 단계 결과

작성된 RED 테스트 파일은 `src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java`이다.

최초 RED 스켈레톤은 총 12개 테스트로 구성되었다.

- `ui_valid_meter_input_returns_conversion_result`
- `ui_missing_colon_input_throws_illegal_argument_exception`
- `ui_negative_value_input_throws_illegal_argument_exception`
- `ui_unknown_unit_input_throws_illegal_argument_exception`
- `ui_output_preserves_original_unit_and_value`
- `ui_json_output_request_returns_expected_schema`
- `convert_meter_to_feet_returns_correct_ratio`
- `convert_meter_to_yard_returns_correct_ratio`
- `convert_all_returns_all_registered_unit_conversions`
- `register_unit_allows_conversion_with_new_unit`
- `load_config_applies_json_and_yaml_ratios`
- `load_config_missing_path_keeps_default_ratios`

RED 실패 확인 결과:

- 최초 스켈레톤 기준: 12개 테스트가 의도적으로 실패
- 현재 기준: 3개는 GREEN assertion으로 전환되어 통과, 9개는 `fail("RED")`로 실패 유지
- 최신 전체 실행 결과: `Tests run: 51, Failures: 9, Errors: 0, Skipped: 0`

현재 남아 있는 RED 실패는 모두 구현 회귀가 아니라 의도적으로 남겨둔 placeholder 실패다.

## 4. GREEN 단계 결과

GREEN으로 확인한 테스트는 다음과 같다.

- `UnitConverterServiceTest`: 34개 통과
- `GoldenMasterApprovalTest`: 1개 통과
- `GoldenMasterTest`: 4개 통과
- `UnitConverterDualTrackRedTest` 중 전환 완료된 3개 테스트 통과

최신 검증 명령과 결과:

```powershell
mvn "-Dtest=!UnitConverterDualTrackRedTest" test
```

결과:

```text
Tests run: 39, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

관련 커밋 메시지는 다음과 같다.

- `99bd573 test: cover valid meter boundary conversion`
- `6980920 test(green): cover meter feet and missing colon`
- `8f45e2d GREEN 프롬프트 2 - Dual-Track 상세 버전`
- `bf22faf Golden Master 자동화 -  회귀 안전장치`
- `a31f8dd Golden Master 자동화 -  테스트 코드 구현`

## 5. Refactoring 결과

선택 항목:

- 제품 코드 리팩토링은 보류했다.
- 이유: 전체 `mvn test` 기준선이 `UnitConverterDualTrackRedTest`의 RED placeholder 9건 때문에 GREEN이 아니었으므로, 리팩토링으로 인한 회귀와 기존 RED 실패를 구분하기 어렵다.

변경 파일:

- 제품 코드 변경 없음
- 리팩토링 커밋 `584e77b Dual-Track REFACTOR`에는 테스트 실행 산출물인 `target/jacoco.exec`, `target/surefire-reports/*` 변경만 포함됨
- 보고서 커밋 `7312c63 Dual-Track REFACTOR 보고서`에는 `report/21_Dual-Track REFACTOR_보고서.md`와 관련 Prompting 문서가 포함됨

회귀 테스트 통과 여부:

- 전체 `mvn test`: FAIL, RED placeholder 9건 때문
- RED placeholder 제외 회귀 기준: PASS, 39개 테스트 통과
- Golden Master 기준: PASS, 5개 테스트 통과

## 6. 커버리지 현황

JaCoCo 기준 파일:

- `target/site/jacoco/index.html`
- `target/site/jacoco/jacoco.xml`
- `target/site/jacoco/jacoco.csv`

전체 bundle 기준:

- Line: 124 covered / 128 total = 96.9%
- Branch: 54 covered / 64 total = 84.4%

활성 서비스 패키지 `com.example.unitconverter` 기준:

- Line: 124 covered / 124 total = 100.0%
- Branch: 54 covered / 60 total = 90.0%

파일별 핵심 수치:

- `UnitConverterService`: Line 122/122 = 100.0%, Branch 54/60 = 90.0%
- `ConversionResult`: Line 1/1 = 100.0%
- `UnitConverterService.ParsedInput`: Line 1/1 = 100.0%
- 기본 패키지 `UnitConverter`: Line 0/4 = 0.0%, Branch 0/4 = 0.0%

레이어별 판정:

- Domain Logic: 별도 domain 패키지는 없고 `UnitConverterService`에 포함되어 있다. 활성 서비스 기준 Line 100.0%로 목표 95% 이상을 충족한다.
- Boundary Layer: 입력 파싱과 콘솔 출력 포맷이 `UnitConverterService`에 함께 있다. 독립 레이어 수치는 산정할 수 없지만 활성 서비스 기준 Line 100.0%, Branch 90.0%로 README 목표 85% 이상은 충족한다.
- Legacy/default layer: 기본 패키지 `UnitConverter`는 0% 커버리지로 남아 있어 전체 branch coverage를 낮추는 주요 원인이다.

## 7. 미완료 항목 및 다음 단계 제안

미완료 항목:

- `UnitConverterDualTrackRedTest`의 남은 9개 `fail("RED")`를 실제 assertion으로 전환
- JSON 출력 스키마 테스트 및 구현 여부 결정
- Golden Master GitHub Actions 워크플로 작성
- PR required status check 설정
- 기본 패키지 `UnitConverter` 유지, 삭제, 또는 커버리지 제외 여부 결정
- Domain, Boundary, Data 책임을 별도 클래스로 나눌지 결정

다음 단계 제안:

1. 남은 RED placeholder를 한 번에 제거하지 말고, TC-A/TC-B 단위로 1~2개씩 GREEN 전환한다.
2. 전체 `mvn test`가 GREEN이 된 뒤에만 제품 코드 리팩토링을 다시 시작한다.
3. `UnitConverterService`에서 단위 저장소, 계산, 입력 파싱, 출력 포맷 책임을 분리해 레이어별 커버리지를 측정 가능하게 만든다.
4. 기본 패키지 `UnitConverter`가 학습용 레거시인지 공개 API인지 결정하고, 유지한다면 테스트를 추가한다.

## 8. 발견된 이슈 및 해결 방법

이슈 1: 전체 테스트가 GREEN이 아님

- 원인: `UnitConverterDualTrackRedTest`에 의도된 RED placeholder 9건이 남아 있음
- 해결: 현재 보고서에서는 전체 실패와 placeholder 제외 회귀 PASS를 분리해 기록했다. 다음 작업에서 placeholder를 실제 assertion으로 전환해야 한다.

이슈 2: PowerShell에서 Maven `-D` 인자 해석 문제가 발생할 수 있음

- 원인: PowerShell이 `-Dgroups=...`, `-Dgolden.master.update=true`, `-Dtest=...` 인자를 의도와 다르게 해석할 수 있음
- 해결: Maven 속성 인자를 따옴표로 감싼다.

```powershell
mvn "-Dtest=!UnitConverterDualTrackRedTest" test
mvn "-Dtest=GoldenMasterApprovalTest" "-Dgolden.master.update=true" test
```

이슈 3: 커버리지 수치와 레이어 목표를 1:1로 매핑하기 어려움

- 원인: `UnitConverterService`가 Domain, Boundary, Data 성격의 책임을 함께 가지고 있음
- 해결: 현재는 활성 서비스 기준으로 판정하고, 이후 구조 분리 후 레이어별 커버리지를 재산정한다.

이슈 4: 기본 패키지 `UnitConverter` 0% 커버리지

- 원인: 현재 회귀 테스트는 주로 `com.example.unitconverter.UnitConverterService`를 대상으로 실행됨
- 해결: 유지 대상이면 테스트를 추가하고, 폐기 대상이면 삭제 또는 JaCoCo 제외 여부를 별도로 결정한다.

## 9. 생성형 AI 활용 회고

도움이 된 순간:

- Phase별 보고서와 테스트 파일을 교차 확인해 RED, GREEN, Refactoring 상태를 한 번에 정리할 수 있었다.
- 실패 원인이 제품 결함인지 의도된 RED placeholder인지 빠르게 분리할 수 있었다.
- JaCoCo CSV/XML을 기준으로 전체 수치와 활성 서비스 수치를 나누어 해석할 수 있었다.

한계:

- 기존 대화 맥락만으로는 "Phase 6 To-Do"의 완료 여부가 코드 기준인지 README 체크박스 기준인지 모호했다.
- 레이어가 코드 구조로 분리되어 있지 않아 Domain/Boundary 커버리지 수치를 정확히 독립 계산할 수 없었다.
- 생성형 AI가 테스트 통과 여부를 추정하면 위험하므로, Maven 실행 결과와 JaCoCo 산출물 확인이 반드시 필요했다.

TC 작성 팁:

- RED 스켈레톤은 오래 남겨두지 말고, 실패 이유가 명확한 assertion으로 빠르게 전환한다.
- UI/Boundary 테스트는 입력 문자열, 예외 타입, 출력 포맷을 한 테스트에 너무 많이 섞지 않는다.
- Domain 테스트는 변환 비율과 허용 오차를 명시하고, 반올림 출력 테스트와 정밀 계산 테스트를 분리한다.
- Golden Master는 사람이 읽는 출력의 회귀 안전장치로 두고, 계산 정확도는 별도 단위 테스트로 보호한다.
- PowerShell 환경에서는 Maven `-D` 옵션을 항상 따옴표로 감싸 재현 가능한 명령을 남긴다.
