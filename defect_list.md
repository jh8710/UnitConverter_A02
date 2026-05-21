# Defect List

현재 결함 목록은 RED 단계 테스트 설계와 기존 단일 파일 구현(`UnitConverter.java`) 점검 과정에서 발견된 결함을 기준으로 정리한다. 수정 검증 기준은 Maven 기반 JUnit 5 회귀 테스트(`mvn test`)이다.

| ID | Severity | 변환 타입 | 재현 절차 | 기대값 | 실제값 | 근본 원인 | 수정 요약 |
|---|---|---|---|---|---|---|---|
| DEF-001 | Critical | 없는 단위 | `"parsec:1.0"` 입력 | `IllegalArgumentException` 발생 및 변환 결과 미생성 | `meterValue`가 `0`으로 유지되어 `0.0 meter`, `0.0 feet`, `0.0 yard` 출력 가능 | if-else 체인에서 미지원 단위 `else` 처리 누락 | 미지원 단위 검증을 추가하고 등록되지 않은 단위는 즉시 `IllegalArgumentException` 발생 |
| DEF-002 | Major | 음수 입력 | `"meter:-1.0"` 입력 | `IllegalArgumentException` 발생 및 변환 결과 미생성 | `-1.0 meter = -3.28084 feet` 형태의 음수 변환 결과 출력 가능 | 0 미만 값 거부 정책 검증 누락 | 변환 전 `value < 0` 검증을 추가해 음수 입력 차단 |
| DEF-003 | Major | 잘못된 형식 | `"meter=2.5"` 입력 | `IllegalArgumentException` 발생 및 변환 결과 미생성 | `parts[1]` 접근 중 `ArrayIndexOutOfBoundsException` 가능 | `unit:value` 형식과 콜론 개수 검증 없이 배열 인덱스 접근 | 콜론 1개 포함 여부를 먼저 검증하고 `split(":", -1)` 사용 |
| DEF-004 | Major | 출력 대상 선정 | `"meter:2.5"` 입력 | 입력 단위 `meter`를 제외하고 `feet`, `yard` 결과만 반환 | `2.5 meter = 2.5 meter`까지 함께 출력 | 입력 단위를 결과 대상에서 제외하는 규칙 누락 | `convertAll`에서 입력 단위와 동일한 대상 단위는 제외 |
| DEF-005 | Minor | 출력 포맷 | `"meter:2.5"` 입력 | 원 입력 값과 단위를 보존한 `<originalValue> <originalUnit> = <convertedValue> <targetUnit>` 구조 | 콘솔 직접 출력에 고정되어 테스트 가능한 결과 객체/문자열 분리 부족 | 계산 로직과 콘솔 출력 책임이 분리되지 않음 | 변환 결과(`ConversionResult`)와 콘솔 출력 문자열 생성을 분리 |
| DEF-006 | Major | 매우 큰 수 | `"meter:1.0E309"` 또는 무한대 입력 | 유한하지 않은 값은 `IllegalArgumentException` 발생 | `Infinity` 또는 `NaN` 기반 계산 가능 | `Double.isFinite` 검증 누락 | 변환 전 유한 숫자 검증을 추가 |
| DEF-007 | Major | 동적 등록 | `registerUnit("cubit", 0.4572)` 후 변환 | 새 단위 등록 후 `cubit` 변환 가능 | 동적 등록 API 부재 | 단위 등록 저장소와 `metersPerUnit` 확장 경로 부재 | `registerUnit`과 등록 단위 기반 변환 로직 추가 |
| DEF-008 | Major | 설정 로드 | JSON/YAML 설정 로드 | 유효 설정은 비율 로드, 실패 설정은 오류 처리 | 설정 로드 API 부재 | 외부 설정 파싱 및 실패 분류 책임 부재 | `loadConfig(Path)`로 JSON/YAML 기본 설정 로드 및 실패 케이스 처리 |
| DEF-009 | Info | 테스트/빌드 | `mvn test` 실행 | JUnit 5 테스트 실행 및 JaCoCo 리포트 생성 | Maven 프로젝트 설정 부재 | `pom.xml` 및 테스트 디렉터리 구조 부재 | Java 21, JUnit 5, Surefire, JaCoCo Maven 설정 추가 |

## 회귀 확인

```shell
mvn test
```

최종 확인 결과:

```text
Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```
