# 20_종합 리팩토링 계획서 [Ask 모드] 보고서 - Prompting

## 1. 세션 목적

이번 세션의 목적은 앞서 분석한 내용을 바탕으로 코드 수정 없이 종합 리팩토링 계획서를 작성하고, 그 결과를 보고서 파일과 Prompting 파일로 내보내는 것이었다.

주요 요구는 다음과 같았다.

- 리팩토링 대상 목록을 우선순위 순으로 정리
- 리팩토링 후보 목록 검토
- 테스트 선행 필요 항목 정리
- 리팩토링 후 검증 방법과 회귀 테스트 명령어 정리
- 이번 세션 내용을 `report` 폴더의 보고서 파일로 내보내기
- 사용 프롬프트를 포함한 Prompting 파일 작성

## 2. 사용 프롬프트 기록

### Prompt 1. 종합 리팩토링 계획서 작성 요청

```text
앞서 분석한 내용을 바탕으로 리팩토링 계획서를 작성해줘.
코드는 아직 수정하지 말고:

## 리팩토링 대상 목록 (우선순위 순)
| 순번 | 대상 | 문제 | 적용 기법 | 우선순위 |

## 리팩토링 후보 목록
R-U1: 입력 파싱을 InputParser로 분리
R-U2: 예외 메시지·코드 상수화
R-U3: 출력 포맷 생성을 OutputFormatter로 분리
R-L1: 변환 비율을 ConversionRule Value Object로 교체
R-L2: if-else 체인을 UnitRegistry(Map/Dictionary)로 교체
R-L3: 매직 넘버 3.28084/1.09361 → 상수 또는 설정 파일
R-L4: convert() 함수 추출 (meter 허브 변환 로직 단일화)

## 테스트 선행 필요 항목
## 리팩토링 후 검증 방법 (회귀 테스트 실행 명령어)
```

### Prompt 2. 세션 보고서 및 Prompting 파일 내보내기 요청

```text
이번세션에서 진행한 내용을 report 폴더의 20_종합 리팩토링 계획서 [Ask 모드]_보고서 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더의 20_종합 리팩토링 계획서 [Ask 모드]_보고서-Prompting.md 파일로 내보내줘
```

## 3. 프로젝트 탐색 기록

먼저 문서와 코드 구조를 확인했다.

확인한 주요 파일은 다음과 같다.

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

`docs/README.md`에는 Golden Master 회귀 안전장치와 RED 단계 To-Do 리스트가 정리되어 있었다.

`UnitConverterService`는 다음 책임을 모두 가지고 있었다.

- 입력 파싱
- 입력 검증
- 변환 계산
- 단위 저장소 관리
- 설정 파일 파싱
- 콘솔 출력 생성

테스트 파일을 확인한 결과 `UnitConverterServiceTest`와 Golden Master 계열 테스트는 기존 계약을 상당 부분 보호하고 있었지만, `UnitConverterDualTrackRedTest`에는 `fail("RED")` 자리표시자가 남아 있었다.

## 4. 테스트 실행 기록

세션 중 다음 명령어를 실행했다.

```powershell
mvn test
```

실행 결과는 실패였다.

```text
Tests run: 51, Failures: 9, Errors: 0, Skipped: 0
```

실패 원인은 모두 `UnitConverterDualTrackRedTest`의 `fail("RED")` 자리표시자였다.

통과한 테스트 그룹은 다음과 같았다.

- `UnitConverterServiceTest`
- `GoldenMasterTest`
- `GoldenMasterApprovalTest`

## 5. 최종 응답 요약

Prompt 1에 대한 최종 응답에서는 다음 내용을 전달했다.

리팩토링 대상 우선순위는 다음 순서로 정리했다.

| 순번 | 대상 | 적용 기법 | 우선순위 |
|---:|---|---|---|
| 1 | `convertInputToConsoleLines()` / `parseInput()` | R-U1: `InputParser` 분리 | P0 |
| 2 | 콘솔 출력 생성 로직 | R-U3: `OutputFormatter` 분리 | P0 |
| 3 | `metersPerUnit` Map 직접 관리 | R-L2: `UnitRegistry` 도입 | P1 |
| 4 | 변환 비율 표현 | R-L1: `ConversionRule` Value Object | P1 |
| 5 | `convert()` 변환 흐름 | R-L4: meter 허브 변환 로직 명시적 추출 | P1 |
| 6 | 예외 메시지 문자열 | R-U2: 예외 메시지·코드 상수화 | P2 |
| 7 | 변환 상수 | R-L3: 상수 위치 단일화 또는 설정화 | P2 |

테스트 선행 필요 항목으로는 RED 테스트 정리, 입력 파싱 계약, 출력 포맷 계약, 변환 규칙, 단위 등록, 설정 로드, Golden Master 기준선 유지를 제안했다.

회귀 테스트 명령어는 다음과 같이 정리했다.

```powershell
mvn test
mvn test -Dtest=UnitConverterServiceTest,GoldenMasterTest,GoldenMasterApprovalTest
mvn test -Dgroups=golden_master
mvn test jacoco:report
```

## 6. 수정 기록

Prompt 2에 따라 이번 세션 내용을 다음 파일로 내보냈다.

```text
report/20_종합 리팩토링 계획서 [Ask 모드]_보고서.md
Prompting/20_종합 리팩토링 계획서 [Ask 모드]_보고서-Prompting.md
```
