# 19_ECB 책임 분리 분석 [Ask 모드] 보고서

## 1. 작업 개요

이번 세션에서는 코드 수정 없이 `UnitConverter.java`를 ECB(Entity-Control-Boundary) 패턴 관점에서 분석했다.

요청된 분석 항목은 다음과 같았다.

- 현재 `main()` 함수에서 ECB 역할이 어떻게 혼재하는지 분석
- Entity: 변환 비율 데이터인 `3.28084`, `1.09361`
- Control: 변환 로직인 `if-else` 체인
- Boundary: 입력 파싱과 출력 포맷
- 어느 코드를 어느 클래스/모듈로 이동해야 하는지 정리
- 새 단위 추가 시 변경이 최소화되는 구조 제안

## 2. 확인 대상

`UnitConverter.java` 이름의 파일은 두 개가 확인되었다.

```text
UnitConverter.java
src/main/java/UnitConverter.java
```

이번 ECB 분석은 `main()` 함수가 포함된 루트의 `UnitConverter.java`를 기준으로 진행했다.

## 3. ECB 역할 혼재 분석

현재 루트의 `UnitConverter.java`는 `main()` 함수 하나가 사용자 입력, 문자열 파싱, 숫자 변환, 단위 변환, 결과 출력까지 모두 담당한다.

ECB 관점에서 보면 다음 책임들이 한 함수 안에 섞여 있다.

| ECB 역할 | 현재 코드의 책임 | 문제점 |
|---|---|---|
| Entity | `meter`, `feet`, `yard` 단위명과 `3.28084`, `1.09361` 변환 비율 | 도메인 데이터가 `main()` 내부에 하드코딩되어 있다. |
| Control | `if-else` 체인, `meterValue`, `inMeters`, `inFeet`, `inYards` 계산 | 변환 흐름과 정책이 UI 실행 코드에 직접 들어 있다. |
| Boundary | `Scanner`, `input.split(":")`, `Double.parseDouble()`, `System.out.println()` | 콘솔 입출력과 표현 포맷이 변환 로직과 분리되어 있지 않다. |

## 4. 이동해야 할 코드

Entity로 이동할 내용은 단위명과 변환 비율 데이터다.

- `meter`, `feet`, `yard`
- `3.28084`, `1.09361`
- 예시 모듈: `Unit`, `UnitDefinition`, `UnitRegistry`

Control로 이동할 내용은 변환 계산과 변환 흐름이다.

- `if (unit.equals(...))` 조건 체인
- `meterValue` 계산
- 각 대상 단위 결과 계산
- 예시 모듈: `UnitConversionService`, `Converter`

Boundary로 남기거나 이동할 내용은 사용자와 맞닿은 입출력 처리다.

- `Scanner` 입력
- `"meter:2.5"` 형식 파싱
- `Double.parseDouble()`
- 출력 문자열 생성과 `System.out.println()`
- 예시 모듈: `ConsoleBoundary`, `InputParser`, `OutputFormatter`

## 5. 새 단위 추가 시 변경 최소화 구조

새 단위를 추가할 때 `if-else` 체인을 수정하지 않으려면, 단위별 변환 비율을 등록하는 구조가 적합하다.

예를 들어 `UnitRegistry`가 다음처럼 1 meter 기준 비율을 보관한다.

```text
meter -> 1.0
feet  -> 3.28084
yard  -> 1.09361
```

`UnitConversionService`는 공통 변환 공식을 사용한다.

```text
sourceValue -> meterValue -> targetValue
```

이 구조에서는 새 단위 `inch`를 추가할 때 Control 로직을 수정하지 않고 Registry에 비율만 추가하면 된다.

```text
inch -> 39.3701
```

권장 구조는 `UnitRegistry`가 단위 데이터를 보관하고, `UnitConversionService`가 변환을 담당하며, `main()`은 입력을 받고 결과를 출력하는 Boundary 역할만 수행하는 형태다.

## 6. 최종 산출물

이번 세션의 최종 산출물은 다음과 같다.

- `report/19_ECB 책임 분리 분석 [Ask 모드]_보고서.md`
- `Prompting/19_ECB 책임 분리 분석 [Ask 모드]_보고서-Prompting.md`
