# 06_README.md & To-Do 리스트 작성 보고서 - Prompting

## 1. 세션 목적

이번 세션의 목적은 Phase 5 PRD를 기반으로 README 문서와 To-Do 리스트를 작성하고, 작성된 문서의 추적성과 PRD 정합성을 검토하는 것이었다. 사용자는 반복해서 “코드 구현 금지”, “문서만 작성”, “보고만, 수정 금지” 조건을 명시했다.

## 2. 사용 프롬프트 기록

### Prompt 1. Phase 5 PRD 기반 README 작성

```text
당신은 오픈소스 문서화 전문가입니다.
워크스페이스: @c:\dev\UnitConverter_A02\ 
전제: Phase 5 PRD를 기반으로 작성하라.
⚠️ 코드 구현 추가 금지. 문서만 작성하라.

# 프로젝트 이름
한 줄 설명 (무엇을, 누구를 위해) — PRD 1.1 목적문 활용

## 목차
자동 링크 포함.

## 개요 (Overview)
- 이 프로젝트가 해결하는 문제 (PRD 1.2 배경 기반)
- 주요 학습 목표 (OCP/SRP, BCE, TDD)
- PRD와의 연결 (한 줄 요약 + PRD 문서 링크)

## 빠른 시작 (Quick Start)
- 사전 조건 (Java 버전, 빌드 도구)
- 빌드 & 실행 명령 (셸 블록 포함)
- 예시 입출력 (meter:5.0 → 결과)

## 지원 단위 및 비율
표 형식: 단위명 / 식별자 / meter 기준 비율 / 출처
(PRD 5.1 단위 비율 상수 기반)

## 입력 형식 계약
- 정상: 단위:값 예시 3개
- 비정상: 오류 케이스 3개 + 에러 메시지 패턴
(PRD 3.3 제약 사항 기반)

## 아키텍처
- BCE 레이어 다이어그램 (Mermaid 또는 ASCII)
- 의존성 방향 설명
- 새 단위 추가 방법 (단계별, 코드 최소화)

## 테스트 실행
- 테스트 프레임워크: JUnit 5
- 명령: 셸 블록
- 커버리지 목표 (PRD 4.3 기반)

## 설정 파일 (JSON/YAML)
- 위치 및 형식 예시
- 동적 단위 등록 예시 (PRD 5.3 기반)

## 출력 포맷
콘솔 / JSON / CSV 각 예시 블록 포함. (PRD 6.1~6.3 기반)

## 기여 가이드 (Contributing)
- 계약 변경 금지 원칙
- 테스트 없는 PR 거부 정책
- 커밋 메시지 컨벤션

## 라이선스
MIT (학습용).

Markdown만 출력. 코드 구현 추가 금지.
표·코드 블록·Mermaid 다이어그램을 적극 활용하라.
```

### Prompt 2. README를 docs/README.md로 내보내기

```text
해당내용은 docs/README.md 파일로 내보내줘
```

### Prompt 3. Phase 5 PRD 기반 To-Do 리스트 작성

```text
워크스페이스: @c:\dev\UnitConverter_A02\ 
전제: Phase 5 PRD의 기능 요구사항·인수 기준·회귀 보호 규칙을 기반으로 작성.
⚠️ 코드 작성 금지. 작업 목록 문서만.

# To-Do 리스트 — UnitConverter (Java)

## 🔴 필수 (Must-Have) — v1.0 릴리스 차단 항목
각 항목: [ ] 작업 설명 | 연관 PRD 항목 | 완료 기준(테스트 가능하게)

## 🟡 권장 (Should-Have) — 품질 향상 항목
각 항목: [ ] 작업 설명 | 연관 PRD 항목 | 완료 기준

## 🟢 선택 (Nice-to-Have) — v2.0 후보
각 항목: [ ] 작업 설명 | 기대 가치 한 줄

## 🔵 기술 부채 (Tech Debt)
각 항목: [ ] 문제 설명 | 발생 원인 | 해결 방향

## ✅ 완료 항목 (Done)
각 항목: [x] 완료 내용 | 완료일 | 관련 커밋/PR 메시지

## 📋 회귀 방지 체크리스트 (PRD 7.2 기반)
배포 전 반드시 확인: 계약 테스트 통과 / 커버리지 목표 달성 / README 갱신 여부

## 🗓️ 마일스톤
표 형식: 마일스톤 | 포함 항목(PRD 기능 번호) | 목표일 | 상태

Markdown만 출력. 각 항목은 "누가 무엇을 완료하면 통과"로 명확히.
```

### Prompt 4. To-Do 리스트를 docs/README.md에 반영

```text
docs/README.md에 업데이트해줘
```

### Prompt 5. README와 Phase 5 PRD 비교

```text
완성된 README.md와 Phase 5 PRD를 비교하라.
보고만 (수정 금지):
1) PRD에 있으나 README에 빠진 항목
2) README에 있으나 PRD에 없는 항목 (범위 초과 위험)
3) 입출력 계약 불일치
4) 테스트 커버리지 목표 불일치
bullet만.
```

### Prompt 6. Phase 4 Story와 Phase 6 To-Do 추적 매트릭스

```text
Phase 4의 User Stories와 Phase 6 To-Do 리스트를 대조하여
추적 매트릭스 표를 작성하라.
표 형식: Story ID | Story 제목 | 연관 To-Do | PRD 항목 | 상태 | 완료 기준

누락된 To-Do(Story는 있지만 To-Do 없음)와
고아 To-Do(Story·PRD 없이 To-Do만 있음)를 별도 bullet로 표시.
코드 금지.
```

### Prompt 7. 세션 보고서와 프롬프트 파일 내보내기

```text
이번세션에서 진행한 내용을 report 폴더를 만들어서 06_README.md & To-Do 리스트 작성_보고서 파일로 내보내주고, 
Prompting 폴더를 만든 후 프롬프트를 포함해서 06_README.md & To-Do 리스트 작성_보고서-Prompting.md 파일로 내보내줘
```

## 3. 작업 흐름 요약

1. `docs/PRD.md`, `docs/README.md`, `docs/requirements.md`를 확인했다.
2. Phase 5 PRD의 목적문, 기능 요구사항, 출력 요구사항, 테스트 커버리지 목표를 기준으로 README 초안을 작성했다.
3. 사용자의 요청에 따라 README 내용을 `docs/README.md`로 내보냈다.
4. Phase 5 PRD의 FR1~FR14, PRD 4.3, PRD 7.1, PRD 7.2를 기준으로 `docs/TODO.md`를 작성했다.
5. To-Do 리스트를 `docs/README.md`에 통합했다.
6. README와 Phase 5 PRD를 비교해 누락, 범위 초과, 입출력 계약 불일치, 커버리지 목표 불일치를 bullet로 보고했다.
7. Phase 4 User Stories와 Phase 6 To-Do 리스트를 대조해 추적 매트릭스와 누락/고아 To-Do를 보고했다.

## 4. 생성·수정된 문서

| 구분 | 경로 | 설명 |
|---|---|---|
| README | `docs/README.md` | Phase 5 PRD 기반 README와 To-Do 리스트 통합 |
| To-Do | `docs/TODO.md` | PRD 기반 작업 목록 |
| 보고서 | `report/06_README.md & To-Do 리스트 작성_보고서.md` | 이번 세션 작업 보고서 |
| 프롬프트 기록 | `Prompting/06_README.md & To-Do 리스트 작성_보고서-Prompting.md` | 이번 세션 사용자 프롬프트와 작업 흐름 기록 |

## 5. 준수 조건

- 코드 구현은 추가하지 않았다.
- 문서 파일만 생성·수정했다.
- README/To-Do 작성은 Phase 5 PRD를 기준으로 했다.
- 추적 매트릭스는 Phase 4 User Stories와 Phase 6 To-Do 리스트를 기준으로 했다.
