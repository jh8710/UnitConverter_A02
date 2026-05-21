# 18_코드 스멜 탐지 [Ask 모드] 보고서

## 1. 작업 개요

이번 세션에서는 코드 수정 없이 `UnitConverter.java`의 코드 스멜을 분석했다.

요청된 점검 항목은 다음과 같았다.

- 단일 `main()` 함수에 파싱·검증·변환·출력이 혼재되어 있는지 확인
- `if-else` 체인으로 인해 OCP 위반 가능성이 있는지 확인
- `3.28084`, `1.09361` 같은 매직 넘버가 하드코딩되어 있는지 확인
- 예외 처리가 없거나 불완전한지 확인
- 20줄을 초과하는 긴 함수가 있는지 확인
- 사용하지 않는 변수·함수가 있는지 확인

## 2. 확인 대상

`UnitConverter.java` 이름의 파일은 두 개가 확인되었다.

```text
UnitConverter.java
src/main/java/UnitConverter.java
```

이번 분석에서 코드 스멜이 발견된 파일은 루트의 `UnitConverter.java`였다.

`src/main/java/UnitConverter.java`는 `FEET_PER_METER` 상수를 사용하고 `IllegalArgumentException`을 던지는 구조였으며, 요청된 항목 기준에서 별도 문제 항목으로 보고할 내용은 없었다.

## 3. 코드 스멜 탐지 결과

| 파일명 | 줄번호 | 스멜 종류 | 문제 설명 | 우선순위 |
|---|---:|---|---|---|
| `UnitConverter.java` | 4-28 | 긴 함수 / SRP 위반 | `main()` 하나에 입력 파싱, 검증, 단위 변환, 결과 출력이 모두 섞여 있어 책임이 과도하다. 함수 길이도 20줄을 초과한다. | 높음 |
| `UnitConverter.java` | 13-19 | if-else 체인 / OCP 위반 | 단위별 변환 로직이 조건문에 고정되어 있어 새 단위를 추가할 때 기존 조건문을 수정해야 한다. | 높음 |
| `UnitConverter.java` | 16, 18, 22, 23 | 매직 넘버 | `3.28084`, `1.09361` 변환 계수가 의미 있는 이름 없이 하드코딩되어 있다. | 중간 |
| `UnitConverter.java` | 8-10 | 예외 처리 없음 | 입력에 `:`가 없거나 숫자 변환이 실패하면 `ArrayIndexOutOfBoundsException`, `NumberFormatException`이 그대로 발생한다. | 높음 |
| `UnitConverter.java` | 13-19 | 불완전한 예외 처리 | 지원하지 않는 단위가 들어와도 오류를 내지 않고 `meterValue = 0` 상태로 결과를 출력한다. | 높음 |

## 4. 최종 응답 요약

사용자에게 문제 있는 부분만 표 형식으로 전달했다.

분석 결과, 루트의 `UnitConverter.java`에서 SRP 위반, OCP 위반 가능성, 매직 넘버, 예외 처리 부재, 불완전한 예외 처리, 긴 함수 문제가 확인되었다.

사용하지 않는 변수·함수는 요청된 기준에서 별도 문제 항목으로 보고하지 않았다.

## 5. 최종 산출물

이번 세션의 최종 산출물은 다음과 같다.

- `report/18_코드 스멜 탐지 [Ask 모드]_보고서.md`
- `Prompting/18_코드 스멜 탐지 [Ask 모드]_보고서-Prompting.md`

