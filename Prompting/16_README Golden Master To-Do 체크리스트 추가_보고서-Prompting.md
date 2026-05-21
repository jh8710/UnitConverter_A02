# 16_README Golden Master To-Do 체크리스트 추가 보고서 - Prompting

## 1. 세션 목적

이번 세션의 목적은 `docs/README.md`에 Golden Master 회귀 안전장치 To-Do 체크리스트를 추가하고, 이후 작업 내용을 보고서와 Prompting 기록으로 내보내는 것이었다.

주요 요구는 다음과 같았다.

- README의 `## RED 단계 To-Do 리스트` 아래에 새 섹션 추가
- Golden Master 기준 파일, 테스트 코드, CI 연동 관련 체크리스트 작성
- 작업 결과를 `report` 폴더에 보고서로 저장
- 사용자 프롬프트를 포함한 기록을 `Prompting` 폴더에 저장

## 2. 사용 프롬프트 기록

### Prompt 1. README Golden Master To-Do 섹션 추가 요청

```text
@docs/README.md 파일에 아래 섹션을 추가해줘.
"## RED 단계 To-Do 리스트" 아래에 새 섹션으로 삽입.

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

### Prompt 2. 세션 보고서 및 Prompting 파일 내보내기 요청

```text
이번세션에서 진행한 내용을 report 폴더의 16_README Golden Master To-Do 체크리스트 추가_보고서 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더의 16_README Golden Master To-Do 체크리스트 추가_보고서-Prompting.md 파일로 내보내줘
```

## 3. 프로젝트 탐색 기록

처음에는 `docs/README.md`에서 요청된 삽입 위치를 확인했다.

처음 실행한 완전 일치 검색에서는 `^## RED 단계 To-Do 리스트$` 패턴이 일치하지 않았지만, 이후 `RED 단계|RED|To-Do` 검색으로 실제 위치를 확인했다.

확인한 주요 위치는 다음과 같다.

- `docs/README.md`의 `## RED 단계 To-Do 리스트`
- 기존 RED 체크리스트 안내 문구
- 기존 Track A, Track B, 커버리지 목표, 결함 목록 연결 섹션

## 4. 수정 기록

`docs/README.md`의 `## RED 단계 To-Do 리스트` 제목 바로 아래에 Golden Master 회귀 안전장치 섹션을 삽입했다.

추가한 하위 섹션은 다음과 같다.

- `### 기준 파일 생성`
- `### 테스트 코드`
- `### CI 연동`

추가한 체크리스트 항목은 다음과 같다.

- `GM-01`부터 `GM-03`: Golden Master 기준 파일 생성 및 버전 관리
- `GM-04`부터 `GM-06`: Golden Master 테스트 코드 작성 및 실행 확인
- `GM-07`부터 `GM-09`: CI workflow, required status check, 리팩터링 후 재실행 확인

## 5. 검증 기록

편집 후 `docs/README.md`에 대해 linter 확인을 수행했다.

```text
No linter errors found.
```

이번 작업은 문서 수정이므로 Maven 테스트는 실행하지 않았다.

## 6. 최종 응답 요약

Prompt 1에 대한 최종 응답에서는 다음 내용을 전달했다.

- `docs/README.md`의 `## RED 단계 To-Do 리스트` 아래에 요청 섹션을 추가함
- `ReadLints` 확인 결과 새 linter 오류가 없음을 전달함

Prompt 2에 따라 이번 세션 내용을 다음 파일로 내보냈다.

- `report/16_README Golden Master To-Do 체크리스트 추가_보고서.md`
- `Prompting/16_README Golden Master To-Do 체크리스트 추가_보고서-Prompting.md`
