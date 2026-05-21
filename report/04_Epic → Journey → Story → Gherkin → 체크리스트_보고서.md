# 04. Epic → Journey → Story → Gherkin → 체크리스트 보고서

## 1. 목적

이번 세션의 목적은 `UnitConverter_A02` 저장소의 README 요구사항을 기준으로, 구현이나 코드 없이 요구사항 서술 패키지를 완성하는 것이다.

산출물은 Epic에서 시작해 Journey, Story, Gherkin, 체크리스트까지 이어지는 추적 가능한 구조를 가진다. 핵심 목표는 단위 변환 알고리즘 구현이 아니라, Java/클린 아키텍처 학습자가 계약 정의, 테스트 설계, 레이어 분리를 요구사항 수준에서 학습할 수 있도록 문서화하는 것이다.

최종 산출물은 `docs/requirements.md`에 반영했으며, 동일 세션 보고서는 이 파일에 기록한다.

## 2. 진행 단계

### 2.1 Level 1 Epic 작성

Epic 제목은 다음으로 고정했다.

| 항목 | 내용 |
|---|---|
| 제목 | 확장 가능한 Java 단위 변환 학습 시스템 |

목적은 4줄로 정리했다.

| No. | 목적 |
|---:|---|
| 1 | Java/클린 아키텍처 학습자가 단위 변환 문제를 통해 입력 검증, 계약 정의, 도메인 분리, 테스트 설계를 함께 학습한다. |
| 2 | README에 명시된 `meter`, `feet`, `yard` 비율을 기준 계약으로 고정하여 변환 정확도를 검증 가능하게 만든다. |
| 3 | 새 단위, 출력 포맷, 외부 설정이 추가되어도 기존 변환 계약을 깨뜨리지 않는 확장성을 요구사항으로 정의한다. |
| 4 | Dual-Track TDD 관점에서 기능 인수 기준과 설계 회귀 보호 기준을 함께 추적한다. |

성공 기준은 커버리지, 계약 테스트, 회귀 정책, 확장성으로 측정 가능하게 구성했다.

### 2.2 Level 2 User Journey 작성

Persona는 `Java/클린 아키텍처 학습자`로 고정했다.

정본 Journey는 다음 6단계로 구성했다.

| 단계 | Journey |
|---:|---|
| 1 | 문제 인식 |
| 2 | 계약 정의 |
| 3 | 도메인 분리 |
| 4 | Dual-Track TDD |
| 5 | 확장 요구 확인 |
| 6 | 회귀 보호 |

각 단계마다 Pain과 Opportunity를 한 줄 이상 작성해, 요구사항 분석이 알고리즘 구현 중심으로 흐르지 않도록 했다.

### 2.3 스토리보드 추가

사용자 여정을 스토리보드 형식으로 확장했다.

| Stage | 포함 항목 |
|---|---|
| Awareness | Action, Thinking, Emotion, Pain, Opportunity |
| Entry | Action, Thinking, Emotion, Pain, Opportunity |
| Action | Action, Thinking, Emotion, Pain, Opportunity |
| Validation | Action, Thinking, Emotion, Pain, Opportunity |
| Outcome | Action, Thinking, Emotion, Pain, Opportunity |

스토리보드 목표는 다음 문장으로 명시했다.

| 항목 | 내용 |
|---|---|
| 목표 | 이 여정의 학습 목표는 단위 변환 알고리즘 구현이 아니라, README 요구사항을 계약으로 고정하고 테스트와 레이어 분리를 통해 확장 가능한 설계를 학습하는 것이다. |

### 2.4 Level 3 User Stories 작성

README 기본 요구사항, 품질 요구사항, 추가 요구사항을 기준으로 6개 User Story를 작성했다.

| Story | 주제 | 핵심 검증 |
|---|---|---|
| S1 | 입력 검증 | `unit:value`, 빈 값, 숫자 파싱, 음수 입력, 결과 미생성 |
| S2 | 레지스트리·OCP | 기본 단위, 등록 단위 기준 판단, 기존 비율 계약 보호 |
| S3 | 환산 정확도 | README 비율, `feet`↔`yard` meter 기준 계산 |
| S4 | 출력 포맷 | 원 입력 보존, JSON, CSV, 표 형태, 계산 결과 불변 |
| S5 | 설정 로드 실패 | 형식 오류, 누락 값, 숫자 오류, 0 이하 비율, 변환 중단 |
| S6 | 동적 단위 등록 | `1 cubit = 0.4572 meter`, 등록 검증, 입력·출력 대상 포함 |

각 Story에는 체크 가능한 Acceptance Criteria를 작성했다.

### 2.5 Level 4 Gherkin Feature 작성

README의 `meter`↔`feet`↔`yard` 비율을 Background로 고정했다.

| Given | 내용 |
|---|---|
| README 기준 비율 | `1 meter = 3.28084 feet`, `1 meter = 1.09361 yard` |
| feet↔yard 계산 | meter 기준 비율로 계산 |
| 기본 등록 단위 | `meter`, `feet`, `yard` |

음수 입력 정책은 시나리오보다 먼저 문장으로 고정했다.

| 정책 | 내용 |
|---|---|
| 음수 입력 정책 | 길이 단위 변환에서 0 미만 값은 유효하지 않은 입력이며, 변환 결과를 생성하지 않는다. |

Gherkin 시나리오는 총 8개로 구성했다.

| No. | Scenario |
|---:|---|
| 1 | Convert meter input to feet and yard |
| 2 | Convert feet and yard input through meter baseline |
| 3 | Reject malformed input without colon separator |
| 4 | Reject decimal parsing failure |
| 5 | Reject negative input according to fixed policy |
| 6 | Reject unknown unit |
| 7 | Preserve original input unit and value in output |
| 8 | Reject invalid configuration file format |

설정 파일 형식 오류 요구와 README 비율 기반 역방향 환산 요구를 함께 유지하기 위해 `feet` 입력과 `yard` 입력 검증은 하나의 meter baseline 시나리오로 묶었다.

### 2.6 Level 5 체크리스트 작성

Level 5는 Epic, Journey, Story, Gherkin과 대조 가능한 완성도 체크리스트로 재작성했다.

표 구조는 다음과 같다.

| 컬럼 | 의미 |
|---|---|
| 영역 | 검증할 요구사항 영역 |
| 검증 방법 | 누가 무엇을 어떻게 대조하는지 |
| 통과 기준 | 무엇을 확인하면 통과인지 |
| 추적 ID | 연결되는 Story 번호 |

각 항목은 "누가 무엇을 어떻게 검증하면 통과인지" 한 줄로 정리했다.

## 3. 최종 산출물

### 3.1 `docs/requirements.md`

최종 요구사항 서술 패키지는 `docs/requirements.md`에 반영했다.

포함된 Level은 다음과 같다.

| Level | 산출물 |
|---|---|
| Level 1 | Epic |
| Level 2 | User Journey, 스토리보드 |
| Level 3 | User Stories |
| Level 4 | Gherkin Feature |
| Level 5 | 완성도 체크리스트 |

### 3.2 `report/04_Epic → Journey → Story → Gherkin → 체크리스트_보고서.md`

이번 세션에서 진행한 요구사항 문서화 흐름과 의사결정 기준을 보고서 형식으로 기록했다.

### 3.3 `Prompting/04_.Epic → Journey → Story → Gherkin → 체크리스트_보고서-Prompting.md`

이번 세션에서 사용한 요청 프롬프트와 해석 기준을 별도 Prompting 문서로 기록했다.

## 4. 범위 제한

이번 세션 산출물은 README의 기본 요구사항, 품질 요구사항, 추가 요구사항 범위 안에서만 작성했다.

| 포함 | 제외 |
|---|---|
| 입력 검증 | 구현 코드 |
| 단위 레지스트리와 OCP 요구 | 클래스 설계 |
| README 비율 기반 환산 정확도 | 메서드 시그니처 |
| 출력 포맷 계약 | 패키지 구조 |
| 설정 로드 실패 | GUI, 웹 API, DB, 인증, 배포 |
| 동적 단위 등록 | 저장소 범위 밖 기능 |

## 5. 검증 결과

`docs/requirements.md`에 대해 문서 진단을 확인했으며, linter 오류는 발견되지 않았다.
