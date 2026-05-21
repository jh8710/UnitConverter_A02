# 20_종합 리팩토링 계획서 [Ask 모드] 보고서

## 1. 작업 개요

이번 세션에서는 코드 수정 없이 현재 UnitConverter 프로젝트의 리팩토링 후보를 종합하고, 우선순위와 테스트 선행 항목, 회귀 검증 방법을 계획서 형태로 정리했다.

요청된 정리 항목은 다음과 같았다.

- 리팩토링 대상 목록을 우선순위 순으로 정리
- 리팩토링 후보 목록 검토
- 테스트 선행 필요 항목 정리
- 리팩토링 후 검증 방법과 회귀 테스트 실행 명령어 정리

## 2. 확인 대상

주요 확인 대상은 다음 파일이었다.

```text
docs/README.md
pom.xml
src/main/java/UnitConverter.java
src/main/java/com/example/unitconverter/UnitConverterService.java
src/main/java/com/example/unitconverter/ConversionResult.java
src/test/java/com/example/unitconverter/UnitConverterServiceTest.java
src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java
src/test/java/com/example/unitconverter/GoldenMasterTest.java
src/test/java/com/example/unitconverter/GoldenMasterApprovalTest.java
src/test/resources/golden_master_expected.txt
```

`UnitConverterService`는 입력 파싱, 입력 검증, 변환 계산, 단위 저장소 관리, 설정 파일 파싱, 콘솔 출력 생성까지 많은 책임을 직접 가지고 있었다.

## 3. 리팩토링 대상 목록 (우선순위 순)

| 순번 | 대상 | 문제 | 적용 기법 | 우선순위 |
|---:|---|---|---|---|
| 1 | `convertInputToConsoleLines()` / `parseInput()` | Boundary 입력 파싱과 Domain 변환이 `UnitConverterService`에 섞여 있음 | R-U1: `InputParser` 분리 | P0 |
| 2 | 콘솔 출력 생성 로직 | 출력 포맷, 반올림 정책, 원 입력 보존 계약이 서비스 내부에 고정됨 | R-U3: `OutputFormatter` 분리 | P0 |
| 3 | `metersPerUnit` Map 직접 관리 | 단위 등록, 조회, 중복 검증, 기본 단위 로딩 책임이 서비스에 집중됨 | R-L2: `UnitRegistry` 도입 | P1 |
| 4 | 변환 비율 표현 | `BigDecimal` 원시값이 의미 없이 전달되어 비율 검증/정밀도 정책이 흩어질 수 있음 | R-L1: `ConversionRule` Value Object | P1 |
| 5 | `convert()` 변환 흐름 | meter 허브 변환은 구현되어 있으나 계산 의도가 메서드 내부 산술에 묻혀 있음 | R-L4: meter 허브 변환 로직 명시적 추출 | P1 |
| 6 | 예외 메시지 문자열 | `"invalid input format"`, `"unsupported unit"` 등 문자열 계약이 코드 곳곳에 직접 존재 | R-U2: 예외 메시지·코드 상수화 | P2 |
| 7 | 변환 상수 | `UnitConverterService`에는 상수화되어 있으나 루트 `UnitConverter`에 중복 상수/구식 if 분기 존재 | R-L3: 상수 위치 단일화 또는 설정화 | P2 |

## 4. 리팩토링 후보 목록

- R-U1: 먼저 적용한다. `InputParser`가 `unit:value` 파싱, 공백 처리, 원 입력값 보존, 숫자 검증을 담당하게 하고 서비스는 `ParsedInput`만 받도록 분리한다.
- R-U2: 메시지 변경은 테스트 영향이 작지만 오류 계약이 확정된 뒤 적용하는 편이 안전하다.
- R-U3: `OutputFormatter`는 콘솔 1자리 반올림과 Golden Master 6자리 출력 계약을 분리할 수 있어 우선순위가 높다.
- R-L1: `ConversionRule`은 `unit`, `metersPerUnit`, 양수 검증을 묶는 Value Object로 두면 설정 로드와 동적 등록 모두 단순해진다.
- R-L2: 현재 `UnitConverterService`는 이미 `Map`을 쓰지만, 저장소 책임이 서비스 안에 있으므로 `UnitRegistry`로 추출하는 방향이 적합하다.
- R-L3: 서비스 상수는 이미 존재한다. 다만 루트 `UnitConverter.java`의 중복/레거시 구현 정리 여부를 먼저 결정해야 한다.
- R-L4: `toMeters()` / `fromMeters()` 또는 `convertViaMeterHub()`로 추출해 교차 변환 계약을 더 명확히 한다.

## 5. 테스트 선행 필요 항목

현재 `mvn test`는 실패한다. 원인은 `UnitConverterDualTrackRedTest`에 남아 있는 `fail("RED")` 자리표시자 9건이다.

리팩토링 전에는 해당 RED 테스트를 실제 assertion으로 완성하거나, 의도적으로 별도 태그/프로파일로 분리해야 한다.

선행 테스트는 최소한 다음 계약을 고정해야 한다.

- 입력 파싱: `meter:2.5`, `meter=2.5`, `meter:`, `:2.5`, `meter:abc`, `meter:NaN`, `meter:-1.0`
- 출력 포맷: `2.5 meter = 8.2 feet`, 원 입력값 문자열 보존
- 변환 규칙: meter↔feet, meter↔yard, feet↔yard meter 허브 변환
- 단위 등록: `cubit` 등록 후 양방향 변환, 중복/0 이하 비율 거부
- 설정 로드: JSON/YAML 성공, 누락 경로 기본값 유지, 잘못된 ratio/필드 누락 실패
- Golden Master: `meter:2.5`, `feet:1.0`, `yard:1.0`, `meter:0.0` 출력 유지

## 6. 리팩토링 후 검증 방법

기본 회귀 테스트 명령어는 다음과 같다.

```powershell
mvn test
```

현재 RED 테스트를 제외하고 기존 GREEN 계약만 확인할 때는 다음 명령어를 사용한다.

```powershell
mvn test -Dtest=UnitConverterServiceTest,GoldenMasterTest,GoldenMasterApprovalTest
```

Golden Master만 확인할 때는 다음 명령어를 사용한다.

```powershell
mvn test -Dgroups=golden_master
```

커버리지 리포트까지 확인할 때는 다음 명령어를 사용한다.

```powershell
mvn test jacoco:report
```

## 7. 테스트 실행 결과

세션 중 `mvn test`를 실행했다.

결과는 실패였다.

```text
Tests run: 51, Failures: 9, Errors: 0, Skipped: 0
```

실패 원인은 모두 `UnitConverterDualTrackRedTest`의 `fail("RED")` 자리표시자였다.

반면 다음 테스트 그룹은 통과했다.

- `UnitConverterServiceTest`
- `GoldenMasterTest`
- `GoldenMasterApprovalTest`

따라서 실제 리팩토링 착수 전 작업은 RED 테스트 정리와 Golden Master 기준선 유지 확인으로 잡는 것이 적합하다.
