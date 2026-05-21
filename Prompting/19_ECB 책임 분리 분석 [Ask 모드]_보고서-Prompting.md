# 19_ECB 책임 분리 분석 [Ask 모드] 보고서 - Prompting

## 1. 세션 목적

이번 세션의 목적은 코드 수정 없이 `UnitConverter.java`를 ECB(Entity-Control-Boundary) 패턴 관점에서 분석하고, 그 결과를 보고서 파일과 Prompting 파일로 내보내는 것이었다.

주요 요구는 다음과 같았다.

- `UnitConverter.java`의 `main()` 함수에 Entity, Control, Boundary 책임이 어떻게 혼재되어 있는지 분석
- 어느 코드를 어느 클래스/모듈로 이동해야 하는지 정리
- 새 단위 추가 시 변경이 최소화되는 구조 제안
- 이번 세션 내용을 `report` 폴더의 보고서 파일로 내보내기
- 사용 프롬프트를 포함한 Prompting 파일 작성

## 2. 사용 프롬프트 기록

### Prompt 1. ECB 책임 분리 분석 요청

```text
@UnitConverter.java 를 ECB(Entity-Control-Boundary) 패턴으로 분석해줘.
코드는 수정하지 말고 다음만 답해줘:

1. 현재 main() 함수에서 ECB 역할이 어떻게 혼재하는가?
   - Entity: 변환 비율 데이터 (3.28084, 1.09361)
   - Control: 변환 로직 (if-else 체인)
   - Boundary: 입력 파싱, 출력 포맷
2. 어느 코드를 어느 클래스/모듈로 이동해야 하는가?
3. 새 단위 추가 시 변경이 최소화되는 구조를 제안하라.
```

### Prompt 2. 세션 보고서 및 Prompting 파일 내보내기 요청

```text
이번세션에서 진행한 내용을 report 폴더의 19_ECB 책임 분리 분석 [Ask 모드]_보고서 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더의 19_ECB 책임 분리 분석 [Ask 모드]_보고서-Prompting.md 파일로 내보내줘
```

## 3. 프로젝트 탐색 기록

먼저 `UnitConverter.java` 파일 위치를 확인했다.

확인 결과 `UnitConverter.java`는 두 곳에 있었다.

```text
UnitConverter.java
src/main/java/UnitConverter.java
```

두 파일을 모두 읽었고, ECB 분석은 `main()` 함수가 포함된 루트의 `UnitConverter.java`를 기준으로 진행했다.

## 4. 분석 기록

루트의 `UnitConverter.java`는 `main()` 함수 안에 콘솔 입력, 문자열 파싱, 숫자 변환, 단위 변환, 결과 출력이 모두 들어 있는 구조였다.

Entity 관점에서는 `meter`, `feet`, `yard` 단위명과 `3.28084`, `1.09361` 변환 비율 데이터가 `main()` 내부에 직접 하드코딩되어 있었다.

Control 관점에서는 입력 단위를 meter 기준 값으로 변환하고, 다시 각 출력 단위 값으로 계산하는 흐름이 `if-else` 체인과 계산식으로 구현되어 있었다.

Boundary 관점에서는 `Scanner`, `input.split(":")`, `Double.parseDouble()`, `System.out.println()`이 모두 `main()`에 있어 입력 파싱과 출력 포맷이 변환 로직과 분리되어 있지 않았다.

## 5. 최종 응답 요약

Prompt 1에 대한 최종 응답에서는 다음 내용을 전달했다.

- `main()`은 Entity, Control, Boundary 책임을 모두 가진 상태라고 설명했다.
- Entity로는 단위명과 변환 비율 데이터를 분리해야 한다고 정리했다.
- Control로는 `if-else` 체인과 변환 계산 로직을 이동해야 한다고 정리했다.
- Boundary로는 콘솔 입력, 입력 파싱, 출력 포맷을 분리해야 한다고 정리했다.
- 새 단위 추가 시 변경을 최소화하려면 `UnitRegistry`에 1 meter 기준 비율을 등록하고, `UnitConversionService`가 공통 변환 공식을 사용하도록 제안했다.

권장 구조는 다음과 같이 요약했다.

```text
UnitRegistry
  - meter -> 1.0
  - feet  -> 3.28084
  - yard  -> 1.09361

UnitConversionService
  - sourceValue -> meterValue -> targetValue

ConsoleBoundary
  - input
  - parse
  - format output
```

## 6. 수정 기록

Prompt 2에 따라 이번 세션 내용을 다음 파일로 내보냈다.

```text
report/19_ECB 책임 분리 분석 [Ask 모드]_보고서.md
Prompting/19_ECB 책임 분리 분석 [Ask 모드]_보고서-Prompting.md
```
