# 16_README Golden Master To-Do 체크리스트 추가 보고서

## 1. 작업 개요

이번 세션에서는 `docs/README.md` 문서의 `## RED 단계 To-Do 리스트` 아래에 Golden Master 회귀 안전장치 To-Do 섹션을 추가했다.

요청된 핵심 목표는 다음과 같았다.

- `## RED 단계 To-Do 리스트` 바로 아래에 새 섹션 삽입
- 섹션 제목을 `## Golden Master 회귀 안전장치`로 작성
- 기준 파일 생성, 테스트 코드, CI 연동 체크리스트 추가
- 요청된 GM-01부터 GM-09까지의 항목을 그대로 문서화

## 2. 수정 대상

수정 대상 파일은 다음과 같다.

```text
docs/README.md
```

확인 결과 `## RED 단계 To-Do 리스트` 섹션은 README의 기존 To-Do 영역에 위치해 있었다.

## 3. 추가한 섹션

`## RED 단계 To-Do 리스트` 바로 아래에 다음 섹션을 추가했다.

```markdown
## Golden Master 회귀 안전장치

> Refactoring 시작 전 구축. GREEN 완료 후 즉시 적용.

### 기준 파일 생성
- [ ] GM-01: golden_master_expected.txt 생성 (meter:2.5 기준 출력)
- [ ] GM-02: feet:1.0 / yard:1.0 / meter:0.0 시나리오 추가
- [ ] GM-03: git add src/test/resources/golden_master_expected.txt (버전 관리 포함)

### 테스트 코드
- [ ] GM-04: GoldenMasterTest.java + golden_master_expected.txt 작성
- [ ] GM-05: approve 패턴 적용 (파일 없으면 생성, 있으면 비교)
- [ ] GM-06: mvn test -Dgroups=golden_master → PASS 확인

### CI 연동
- [ ] GM-07: .github/workflows/golden_master.yml 작성
- [ ] GM-08: PR 머지 차단 (required status check) 설정
- [ ] GM-09: Refactoring 후 Golden Master 재실행 → PASS 확인
```

## 4. 검증 결과

문서 편집 후 `docs/README.md`에 대해 linter 확인을 수행했다.

```text
No linter errors found.
```

별도의 테스트 실행은 수행하지 않았다. 이번 작업은 README 문서 섹션 추가 작업이기 때문이다.

## 5. 최종 산출물

이번 세션의 최종 산출물은 다음과 같다.

- `docs/README.md`
- `report/16_README Golden Master To-Do 체크리스트 추가_보고서.md`
- `Prompting/16_README Golden Master To-Do 체크리스트 추가_보고서-Prompting.md`
