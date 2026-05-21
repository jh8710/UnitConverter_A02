# 07_Dual-Track 실패 테스트 설계 보고서

## 1. 작업 개요

이번 세션에서는 `UnitConverter_A02` 프로젝트의 README/PRD 계약을 기준으로 Dual-Track TDD의 RED 단계 실패 테스트 설계와 GREEN 확인을 진행했다.

작업 흐름은 다음 순서로 진행했다.

| 단계 | 내용 | 산출물 |
|---|---|---|
| 브랜치 전략 검토 | Dual-Track UI + Logic TDD 흐름에 맞는 기능 브랜치와 단계별 커밋 전략 제안 | 대화 내 전략 보고 |
| 샘플 예제 선정 | README/PRD 기준 핵심 변환 기능 중 `meter -> feet` 중심 예제 선정 | 대화 내 선택 결과 |
| 테스트 계획 수립 | JUnit 5 기반 테스트 범위, 경계값, 예외, 커버리지, JaCoCo 측정 전략 작성 | `docs/test_plan.md` |
| RED To-Do 정리 | UI/Boundary와 Domain/Logic Track의 실패 테스트 체크리스트 추가 | `docs/README.md` |
| 테스트 코드 작성 | Java 21, Maven, JUnit 5 기반 테스트와 최소 구현 작성 | `pom.xml`, `src/main/java`, `src/test/java` |
| 결함 문서화 | 발견 결함과 수정 요약을 표 형식으로 정리 | `defect_list.md` |

## 2. 샘플 예제 선정

테스트 계획의 기준 샘플은 `meter -> feet` 변환으로 선정했다.

| 항목 | 내용 |
|---|---|
| 선택 기능 | `meter -> feet` 변환 |
| 대표 입력 | `meter:2.5` |
| 기대 정밀값 | `8.202100 feet` |
| 기준 비율 | `1 meter = 3.28084 feet` |
| PRD 추적성 | `FR4`, `PRD 5.1`, `PRD 7.1 S3`, `RR1` |

선정 이유는 README 기본 요구사항의 핵심 비즈니스 로직을 직접 검증할 수 있고, 정상 경로와 `value=0`, 큰 수, 숫자 파싱 실패, 음수, 미지원 단위 같은 경계/예외 케이스로 확장하기 쉽기 때문이다.

## 3. 테스트 계획 작성

`docs/test_plan.md`에는 QA 리드 관점의 테스트 계획을 작성했다.

포함한 주요 내용은 다음과 같다.

- JUnit 5 기반 Domain 단위 테스트 범위와 우선순위
- Boundary 단위 테스트 범위와 우선순위
- `value=0`, 매우 큰 수, 음수, 숫자 파싱 실패, 형식 오류, 미지원 단위 경계값 목록
- 빈 입력, 값 누락, 단위 누락, `NaN`, `Infinity` 등 예외/특이 케이스
- Domain 95% 이상, Boundary 85% 이상 커버리지 목표
- `mvn jacoco:report`와 `target/site/jacoco/index.html` 기반 측정 전략

## 4. README RED To-Do 반영

`docs/README.md`에 `## RED 단계 To-Do 리스트` 섹션을 추가했다.

RED To-Do는 Dual-Track 관점으로 분리했다.

| Track | 목적 | 대표 항목 |
|---|---|---|
| Track A - UI / Boundary 테스트 | 입력 문자열, 예외, 출력 포맷 검증 | `meter:2.5`, `meter:-1.0`, `parsec:1.0`, `meter:abc`, 원 입력 보존 |
| Track B - Domain / Logic 테스트 | 변환 비율, 역변환, 등록, 설정 로드 검증 | `convert("meter", 2.5, "feet")`, `convertAll`, `registerUnit`, `loadConfig` |

또한 커버리지 목표와 결함 목록 연결 체크박스를 추가했고, 이후 `defect_list.md` 생성 및 회귀 테스트 통과 확인 항목을 완료 상태로 갱신했다.

## 5. 테스트 코드 및 최소 구현

기존 저장소에는 Maven/Gradle 설정과 `src` 디렉터리 구조가 없고 루트의 단일 `UnitConverter.java`만 있었다. `mvn test` Green 요구를 만족하기 위해 Maven 기반 Java 21 프로젝트 골격과 테스트 가능한 최소 구현을 추가했다.

추가한 파일은 다음과 같다.

| 파일 | 역할 |
|---|---|
| `pom.xml` | Java 21, JUnit 5, Surefire, JaCoCo 설정 |
| `src/main/java/com/example/unitconverter/ConversionResult.java` | 변환 결과 DTO |
| `src/main/java/com/example/unitconverter/UnitConverterService.java` | 변환, 입력 검증, 동적 등록, JSON/YAML 설정 로드 |
| `src/test/java/com/example/unitconverter/UnitConverterServiceTest.java` | JUnit 5 테스트 34개 |

테스트는 사용자 규칙에 맞춰 다음 원칙으로 작성했다.

- JUnit 5 사용
- Given-When-Then 주석 구조 사용
- 각 테스트에 `1 meter = 3.28084 feet` 비율 명시
- 테스트 이름을 `test_[변환타입]_[조건]_[기대결과]` 형식으로 작성
- 정상 변환, 경계값, 예외, 동적 등록, 설정 로드 유형을 모두 포함

## 6. 결함 문서화

`defect_list.md`에는 RED 테스트 관점에서 발견된 결함 9건을 기록했다.

결함 분류는 다음 기준을 사용했다.

| Severity | 기준 |
|---|---|
| Critical | 변환 결과가 완전히 틀리거나 미지원 단위가 잘못 계산되는 경우 |
| Major | 경계값, 입력 정책, 설정 로드, 동적 등록 요구를 위반하는 경우 |
| Minor | 출력 포맷 또는 책임 분리 부족 |
| Info | 빌드/테스트 설정, 스타일, 문서화 수준 문제 |

주요 결함은 미지원 단위 처리 누락, 음수 입력 허용, 형식 오류 처리 미흡, 입력 단위 제외 규칙 누락, 동적 등록 및 설정 로드 API 부재였다.

## 7. 검증 결과

최종 검증은 Maven 테스트로 수행했다.

```shell
mvn test
```

결과는 다음과 같다.

```text
Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

JaCoCo 리포트도 생성되었다.

| 항목 | 결과 |
|---|---|
| HTML 리포트 | `target/site/jacoco/index.html` |
| 분석 클래스 | 3 classes |
| `UnitConverterService` line coverage | 100% |
| linter 진단 | 오류 없음 |

## 8. 비고

- 루트의 기존 `UnitConverter.java`는 삭제하거나 직접 수정하지 않았다.
- 테스트 가능한 새 구현은 `src/main/java/com/example/unitconverter` 패키지에 추가했다.
- 이번 세션의 최종 상태는 Dual-Track RED 테스트 설계에서 GREEN 통과까지 확인된 상태다.
