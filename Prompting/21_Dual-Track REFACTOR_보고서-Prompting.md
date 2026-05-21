# 21_Dual-Track REFACTOR 보고서 - Prompting

## 1. 세션 목적

이번 세션의 목적은 커밋 1개 단위의 Dual-Track REFACTOR를 수행할 수 있는지 확인하고, 절대 규칙에 따라 전체 테스트 GREEN 기준선을 먼저 검증하는 것이었다.

실제 진행 결과, 전체 테스트가 실패하여 리팩토링은 수행하지 않았다.

## 2. 사용 프롬프트 기록

### Prompt 1. Dual-Track REFACTOR 수행 요청

```text
당신은 Dual-Track UI + Logic TDD 및 리팩토링 전문가입니다.
프로젝트: @c:\dev\UnitConverter_A02\ 
이번 작업: 커밋 1개 단위의 Dual-Track REFACTOR만 수행

절대 규칙:
- 새 기능 추가 금지
- 외부 계약(입력 "단위:값" 형식, 출력 "값 단위 = 변환값 단위", 예외 타입) 변경 금지
- 테스트 삭제·완화 금지
- 먼저 테스트 실행 → 리팩토링 → 테스트 재실행 (항상 GREEN 유지)
- 3.28084 / 1.09361 인라인 하드코딩 추가 금지
- Dual-Track 분리:
    Boundary(UI Track): 파싱·검증·포맷 책임만
    Domain(Logic Track): 순수 변환 규칙·계산 책임만

커밋 단위 절차:
Step 0) 전체 테스트 실행 → GREEN 상태 확인
Step 1) 리팩토링 목표 1~2개 선택 (가장 작은 단위)
Step 2) 보호 테스트 점검·보강
Step 3) Dual-Track 리팩토링 수행 (최소 변경)
Step 4) 전체 테스트 재실행 → 모두 통과 확인
Step 5) 커밋 준비 산출물 작성

출력 형식:
1) 이번 커밋 리팩토링 목표 (선택 항목 ID)
2) 변경 범위 요약 (UI Track / Logic Track 분리)
3) 변경 전 문제점 → 변경 후 개선점
4) 수정된 파일 목록
5) 테스트 실행 결과 요약
6) 위험 요소 및 롤백 포인트
7) 커밋 메시지 제안 (Conventional Commit)
   예: refactor(domain): extract UnitRegistry and ConversionRule VO
```

### Prompt 2. 보고서 및 Prompting 파일 내보내기 요청

```text
이번세션에서 진행한 내용을 report 폴더의 21_Dual-Track REFACTOR_보고서 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더의 21_Dual-Track REFACTOR_보고서-Prompting.md 파일로 내보내줘
```

## 3. 프로젝트 탐색 기록

먼저 프로젝트 구조와 테스트/구현 파일을 확인했다.

확인한 주요 파일은 다음과 같다.

```text
pom.xml
src/main/java/UnitConverter.java
src/main/java/com/example/unitconverter/UnitConverterService.java
src/main/java/com/example/unitconverter/ConversionResult.java
src/test/java/com/example/unitconverter/UnitConverterServiceTest.java
src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java
src/test/java/com/example/unitconverter/GoldenMasterTest.java
```

`UnitConverterService`는 다음 책임을 함께 가지고 있었다.

- 입력 문자열 파싱
- 입력값 검증
- 변환 계산
- 단위 저장소 관리
- JSON/YAML 설정 로드
- 콘솔 출력 문자열 생성

Dual-Track 관점에서는 UI Track과 Logic Track이 아직 한 서비스에 섞여 있는 상태로 확인되었다.

## 4. 테스트 실행 기록

전체 테스트 기준선을 확인하기 위해 다음 명령어를 실행했다.

```powershell
mvn test
```

실행 결과는 실패였다.

```text
Tests run: 51, Failures: 9, Errors: 0, Skipped: 0
```

실패 원인은 모두 `UnitConverterDualTrackRedTest`의 `fail("RED")` 자리표시자였다.

통과한 테스트 그룹은 다음과 같다.

- `GoldenMasterApprovalTest`
- `GoldenMasterTest`
- `UnitConverterServiceTest`

## 5. 최종 응답 요약

Prompt 1에 대한 최종 응답에서는 다음 내용을 전달했다.

- Step 0 전체 테스트가 GREEN이 아니므로 리팩토링 목표 선택을 보류했다.
- UI Track과 Logic Track 모두 코드 변경이 없었다.
- `UnitConverterDualTrackRedTest`에 남아 있는 `fail("RED")` 9건 때문에 전체 테스트가 실패했다.
- 기존 RED 테스트를 먼저 정리하지 않으면 리팩토링 회귀와 기존 실패를 구분하기 어렵다고 판단했다.
- 리팩토링 커밋 메시지는 제안하지 않았고, 먼저 RED 테스트를 GREEN으로 만드는 별도 TDD 커밋이 필요하다고 정리했다.

## 6. 산출물

이번 요청으로 생성한 산출물은 다음과 같다.

```text
report/21_Dual-Track REFACTOR_보고서.md
Prompting/21_Dual-Track REFACTOR_보고서-Prompting.md
```
