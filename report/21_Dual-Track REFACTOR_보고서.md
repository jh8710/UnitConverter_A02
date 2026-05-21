# 21_Dual-Track REFACTOR 보고서

## 1. 작업 개요

이번 세션의 목표는 커밋 1개 단위의 Dual-Track REFACTOR를 수행하는 것이었다.

요청된 절차는 다음과 같았다.

- Step 0: 전체 테스트 실행 후 GREEN 상태 확인
- Step 1: 리팩토링 목표 1~2개 선택
- Step 2: 보호 테스트 점검·보강
- Step 3: Dual-Track 리팩토링 수행
- Step 4: 전체 테스트 재실행
- Step 5: 커밋 준비 산출물 작성

절대 규칙에 따라 리팩토링은 전체 테스트가 먼저 GREEN임을 확인한 뒤에만 진행해야 했다.

## 2. 확인 대상

주요 확인 대상은 다음 파일이었다.

```text
pom.xml
src/main/java/UnitConverter.java
src/main/java/com/example/unitconverter/UnitConverterService.java
src/main/java/com/example/unitconverter/ConversionResult.java
src/test/java/com/example/unitconverter/UnitConverterServiceTest.java
src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java
src/test/java/com/example/unitconverter/GoldenMasterTest.java
```

`UnitConverterService`에는 입력 파싱, 입력 검증, 출력 포맷 생성, 단위 등록, 설정 로드, 변환 계산 책임이 함께 존재했다.

## 3. Step 0 테스트 실행 결과

전체 테스트 확인을 위해 다음 명령어를 실행했다.

```powershell
mvn test
```

실행 결과는 실패였다.

```text
Tests run: 51, Failures: 9, Errors: 0, Skipped: 0
```

실패한 테스트는 모두 `UnitConverterDualTrackRedTest`에 남아 있는 `fail("RED")` 자리표시자였다.

```text
UnitConverterDualTrackRedTest.convert_all_returns_all_registered_unit_conversions
UnitConverterDualTrackRedTest.convert_meter_to_yard_returns_correct_ratio
UnitConverterDualTrackRedTest.load_config_applies_json_and_yaml_ratios
UnitConverterDualTrackRedTest.load_config_missing_path_keeps_default_ratios
UnitConverterDualTrackRedTest.register_unit_allows_conversion_with_new_unit
UnitConverterDualTrackRedTest.ui_json_output_request_returns_expected_schema
UnitConverterDualTrackRedTest.ui_negative_value_input_throws_illegal_argument_exception
UnitConverterDualTrackRedTest.ui_output_preserves_original_unit_and_value
UnitConverterDualTrackRedTest.ui_unknown_unit_input_throws_illegal_argument_exception
```

다음 테스트 그룹은 통과했다.

- `GoldenMasterApprovalTest`
- `GoldenMasterTest`
- `UnitConverterServiceTest`

## 4. 이번 커밋 리팩토링 목표

선택 보류.

Step 0에서 전체 테스트가 GREEN이 아니었으므로 Step 1 이후의 리팩토링 목표 선택, 보호 테스트 보강, 코드 변경은 진행하지 않았다.

## 5. 변경 범위 요약

### UI Track

변경 없음.

입력 `"단위:값"` 파싱, 검증, 콘솔 출력 포맷 계약은 수정하지 않았다.

### Logic Track

변경 없음.

순수 변환 규칙, 단위 비율, 계산 로직은 수정하지 않았다.

## 6. 변경 전 문제점과 변경 후 상태

변경 전 문제점은 전체 테스트 기준선이 GREEN이 아니라는 점이다.

`UnitConverterDualTrackRedTest`에 RED 자리표시자가 남아 있어 전체 테스트 실패 상태였고, 이 상태에서 리팩토링을 진행하면 기존 실패와 리팩토링 회귀를 구분하기 어렵다.

변경 후 개선점은 코드 변경이 없으므로 없음.

대신 리팩토링 중단 사유와 기준선 실패 원인을 명확히 기록했다.

## 7. 수정된 파일 목록

코드 수정 파일은 없다.

이번 보고서 작성으로 추가된 파일은 다음과 같다.

```text
report/21_Dual-Track REFACTOR_보고서.md
Prompting/21_Dual-Track REFACTOR_보고서-Prompting.md
```

## 8. 위험 요소 및 롤백 포인트

현재 가장 큰 위험은 전체 테스트가 실패하는 상태에서 REFACTOR를 시작하는 것이다.

롤백 포인트는 코드 변경이 없으므로 별도 코드 롤백 대상은 없다. 다음 작업 전에 `UnitConverterDualTrackRedTest`의 RED 자리표시자를 실제 보호 assertion으로 완성하거나, 의도적으로 별도 테스트 단계로 분리해야 한다.

## 9. 커밋 메시지 제안

이번 세션에서는 리팩토링 커밋을 만들 수 있는 코드 변경이 없었다.

보고서 파일만 커밋한다면 다음 메시지를 사용할 수 있다.

```text
docs(report): record dual-track refactor baseline failure
```
