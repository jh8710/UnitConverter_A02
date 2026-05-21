# 07_Dual-Track 실패 테스트 설계 보고서 - Prompting

## 1. 세션 목적

이번 세션의 목적은 `UnitConverter_A02` 프로젝트에서 Dual-Track TDD의 RED 단계 실패 테스트를 설계하고, 선택 샘플 예제인 `meter -> feet` 변환을 기준으로 테스트 계획, RED To-Do, JUnit 5 테스트, 결함 목록을 작성하는 것이었다.

후반부에는 `mvn test`가 Green이 되도록 Maven 기반 Java 21 프로젝트 구조와 최소 구현을 추가하고, 발견 결함을 `defect_list.md`로 문서화했다.

## 2. 사용 프롬프트 기록

### Prompt 1. Dual-Track TDD 브랜치 전략 요청

```text
@c:\dev\UnitConverter_A02\ 프로젝트를 구현하기 위한 깃 브랜치 전략을 알려줘.
개발방법론은 Dual-Track TDD 이고, 다음의 순서로 진행할 거야.

- Dual-Track UI + Logic TDD (RED 단계)
- GREEN 단계 (테스트 통과)
- Dual-Track Refactoring
```

### Prompt 2. 테스트 플랜 샘플 예제 선택

```text
@docs/README.md 와 PRD 문서를 참고하여
테스트 플랜 작성을 위한 샘플 예제 1개를 선택해줘.

선택 기준:
1. README의 기본 요구사항에 명시된 핵심 변환 기능
2. 비즈니스 로직(1 meter = 3.28084 feet, 1 meter = 1.09361 yard)을 직접 검증
3. 정상 경로(happy path)와 경계값 모두 포함 가능한 기능

선택한 예제에 대해 다음을 명시하라:
- 선택한 기능 이름 (예: meter → feet 변환)
- README 요구사항 번호 (예: 기본 요구사항 4번)
- 입력 예시: "meter:2.5"
- 기대 출력: "2.5 meter = 8.202100 feet"
- 이 예제를 선택한 이유 2줄

코드 작성 금지. 선택 결과 문서만 출력.
```

### Prompt 3. 테스트 계획서 작성

```text
[P] 시니어 QA 리드입니다.
[C] Java 21, Gradle/Maven, JUnit 5
[T] 선택한 샘플 예제(meter→feet 변환)를 기반으로 테스트 계획서를 작성해줘.

    포함 항목:
    - JUnit 5 기반 단위 테스트 범위/우선순위
    - 경계값 케이스 목록:
        · value = 0 (영값 변환)
        · value = 매우 큰 수 (오버플로 위험)
        · value < 0 (음수 입력 정책)
        · 소수점 파싱 실패 ("meter:abc")
        · ":"없는 입력 (형식 오류)
        · 없는 단위 ("parsec:1.0")
    - 예외/특이 케이스 목록
    - 커버리지 목표 (Domain 95%+ / Boundary 85%+)
    - mvn jacoco:report / # target/site/jacoco/index.html 측정 전략

[F] Markdown 문서. docs/test_plan.md로 저장.
```

### Prompt 4. README RED 단계 To-Do 리스트 추가

```text
docs/README.md 파일에 아래 섹션을 추가해줘.
기존 내용은 유지하고, "## RED 단계 To-Do 리스트" 섹션만 새로 삽입.

추가할 섹션 형식:

## RED 단계 To-Do 리스트

> 이 체크리스트는 test_plan.md 기반으로 생성되었습니다.
> 각 항목은 RED(실패 테스트 작성) 완료 시 체크합니다.

### Track A — UI / Boundary 테스트
- [ ] TC-A-01: 정상 입력 "meter:2.5" → 변환 결과 반환 (Happy Path)
- [ ] TC-A-02: ":" 없는 입력 → IllegalArgumentException 발생
- [ ] TC-A-03: 음수 입력 "meter:-1.0" → IllegalArgumentException 발생
- [ ] TC-A-04: 없는 단위 "parsec:1.0" → IllegalArgumentException 발생
- [ ] TC-A-05: 소수점 파싱 실패 "meter:abc" → IllegalArgumentException 발생
- [ ] TC-A-06: 출력 포맷에 원 입력 단위·값 보존 ("2.5 meter = ...")
- [ ] TC-A-07: value=0 경계값 처리 확인

### Track B — Domain / Logic 테스트
- [ ] TC-B-01: convert("meter", 2.5, "feet") == 8.20210 (오차 1e-5)
- [ ] TC-B-02: convert("meter", 1.0, "yard") == 1.09361 (오차 1e-5)
- [ ] TC-B-03: convert("feet", 1.0, "meter") == 0.30480 (역변환)
- [ ] TC-B-04: convertAll("meter", 1.0) → 모든 등록 단위 변환 반환
- [ ] TC-B-05: registerUnit("cubit", 0.4572) 후 변환 가능
- [ ] TC-B-06: loadConfig(유효한 경로) → 비율 정상 로드
- [ ] TC-B-07: loadConfig(없는 경로) → 기본값(3.28084/1.09361) 유지

### 커버리지 목표
- [ ] Domain Logic: 95%+ (mvn jacoco:report)
- [ ] Boundary Layer: 85%+
- [ ] 전체 TOTAL: 90%+

### 결함 목록 연결
- [ ] defect_list.md 생성 및 발견 결함 기록
- [ ] 모든 결함 수정 후 회귀 테스트 통과 확인
```

### Prompt 5. JUnit 5 테스트 코드 작성

```text
[P] 테스트 설계에 강한 시니어 Java QA입니다.
[C] Java 21, Gradle/Maven, JUnit 5
[T] 아이템 타입별 최소 5개 테스트를 작성해줘.

    대상 케이스:
    - 정상 변환: meter→feet, meter→yard, feet→meter (역변환)
    - 경계값: value=0 / 매우 큰 수 / 소수점 6자리 정확도
    - 예외: 잘못된 형식 / 음수 / 없는 단위
    - 동적 등록: registerUnit 후 변환
    - 설정 로드: JSON/YAML 정상·실패 케이스

    형식 규칙:
    - JUnit 5 사용
    - Given-When-Then 주석 구조
    - 각 테스트에 변환 비율 명시 (1 meter = 3.28084 feet)
    - 테스트 이름: test_[변환타입]_[조건]_[기대결과] 형식

[F] 완성된 테스트 코드. mvn test가 Green이 되게 작성.
```

### Prompt 6. 테스트 실패 분석 템플릿 요청

```text
[P] 디버깅과 결함 분석에 능한 Java QA 엔지니어입니다.
[T] (여기에 mvn test 실패 로그를 붙여넣을 것)

    1) 테스트 실패의 기대/실제 차이 요약
       예: expected 8.202100 but got 0.000000
    2) 버그 위치 특정 (파일명:줄번호)
       예: UnitConverter.java:27
    3) 결함 심각도 분류 및 근거:
       Critical   — 변환 결과가 완전히 틀린 경우
       Major      — 경계값에서 오차 초과
       Minor      — 출력 포맷 불일치
       Info       — 코드 스타일/주석 문제
    4) 최소 변경 수정 방안 (Java 스타일)
       - if-else 체인 유지 최소 수정 (GREEN 통과 우선)
       - Item/기본 구조체 수정 금지

[F] 수정 diff 제안 + mvn test Green 확인 절차
```

### Prompt 7. 결함 목록 문서화

```text
[P] QA 리드입니다.
[C] 현재까지 발견된 테스트 실패/결함들을 문서화합니다.
[T] 발견된 결함을 defect_list.md에 정리해줘.

    항목 형식:
    | ID | Severity | 변환 타입 | 재현 절차 | 기대값 | 실제값 | 근본 원인 | 수정 요약 |

    예시:
    | DEF-001 | Critical | meter→feet | "meter:2.5" 입력 | 8.202100 | 0.000000 |
    | if-else 체인에서 feet 분기 누락 | meterValue * 3.28084 추가 |

[F] Markdown. defect_list.md로 저장.
    README의 "RED 단계 To-Do 리스트 > 결함 목록 연결" 체크박스도 업데이트.
```

### Prompt 8. 세션 보고서와 프롬프트 내보내기

```text
이번세션에서 진행한 내용을 report 폴더의 07_Dual-Track 실패 테스트 설계_보고서 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더의 07_Dual-Track 실패 테스트 설계_보고서-Prompting.md 파일로 내보내줘
```

## 3. 주요 응답 및 작업 요약

| 프롬프트 | 응답/작업 |
|---|---|
| Prompt 1 | 기능 브랜치 중심 전략과 RED/GREEN/Refactor 커밋 전략 제안 |
| Prompt 2 | `meter -> feet` 중심 샘플 예제 선정 |
| Prompt 3 | `docs/test_plan.md` 작성 |
| Prompt 4 | `docs/README.md`에 RED 단계 To-Do 리스트 삽입 |
| Prompt 5 | Maven/JUnit 5 프로젝트 구조, 최소 구현, 테스트 34개 작성 |
| Prompt 6 | 실패 로그가 없음을 알리고 결함 분석 템플릿과 수정 diff 예시 제시 |
| Prompt 7 | `defect_list.md` 작성 및 README 체크박스 완료 처리 |
| Prompt 8 | 본 보고서와 Prompting 문서 생성 |

## 4. 산출물

| 산출물 | 경로 |
|---|---|
| 테스트 계획서 | `docs/test_plan.md` |
| RED 단계 To-Do 리스트 | `docs/README.md` |
| 결함 목록 | `defect_list.md` |
| Maven 설정 | `pom.xml` |
| 변환 서비스 구현 | `src/main/java/com/example/unitconverter/UnitConverterService.java` |
| 변환 결과 DTO | `src/main/java/com/example/unitconverter/ConversionResult.java` |
| JUnit 5 테스트 | `src/test/java/com/example/unitconverter/UnitConverterServiceTest.java` |
| 세션 보고서 | `report/07_Dual-Track 실패 테스트 설계_보고서.md` |
| 프롬프트 기록 | `Prompting/07_Dual-Track 실패 테스트 설계_보고서-Prompting.md` |

## 5. 검증 기록

최종 테스트 명령:

```shell
mvn test
```

최종 결과:

```text
Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

JaCoCo 리포트:

```text
target/site/jacoco/index.html
```
