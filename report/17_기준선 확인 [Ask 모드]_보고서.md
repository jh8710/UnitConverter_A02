# 17_기준선 확인 [Ask 모드] 보고서

## 1. 작업 개요

이번 세션에서는 코드 수정 없이 `UnitConverter.java` 기준선과 테스트 존재 여부를 확인했다.

요청된 핵심 목표는 다음과 같았다.

- `UnitConverter.java`를 기준으로 현재 테스트 파일 존재 여부 확인
- `test_*.java` 형식의 테스트 파일 존재 여부 확인
- 테스트가 없다면 어느 파일에 대한 테스트가 필요한지 판단
- 테스트 없이 리팩토링을 시작하면 안 되는 이유를 한 줄로 정리

## 2. 확인 대상

확인된 `UnitConverter.java` 파일은 두 개였다.

```text
UnitConverter.java
src/main/java/UnitConverter.java
```

두 파일의 역할은 서로 달랐다.

- `UnitConverter.java`: 콘솔 입력, 단위 변환, 콘솔 출력이 한 클래스에 함께 들어 있는 구현
- `src/main/java/UnitConverter.java`: `convert(String sourceUnit, double value, String targetUnit)` API를 제공하는 단순 변환 클래스

## 3. 테스트 파일 확인 결과

`test_*.java` 패턴으로 검색한 결과 해당 형식의 테스트 파일은 없었다.

다만 JUnit 관례인 `*Test.java` 형식의 테스트 파일은 존재했다.

```text
src/test/java/com/example/unitconverter/UnitConverterServiceTest.java
src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java
src/test/java/com/example/unitconverter/GoldenMasterApprovalTest.java
src/test/java/com/example/unitconverter/GoldenMasterTest.java
```

확인 결과 기존 테스트들은 주로 `UnitConverterService`를 대상으로 하고 있었다.

## 4. 필요한 테스트 판단

`UnitConverter.java`를 리팩토링하려면 먼저 `src/main/java/UnitConverter.java`에 대한 테스트가 필요하다고 판단했다.

또한 루트의 `UnitConverter.java`도 유지 대상이라면, 해당 파일의 콘솔 입력/출력 동작에 대한 별도 테스트가 필요하다.

## 5. 테스트 없이 리팩토링하면 안 되는 이유

테스트 없이 리팩토링을 시작하면 기존 변환 동작과 출력 형식이 깨져도 즉시 감지할 안전망이 없다.

## 6. 최종 응답 요약

사용자에게 다음 내용을 전달했다.

- `test_*.java` 형식의 테스트 파일은 없음
- `*Test.java` 형식의 JUnit 테스트는 존재함
- 현재 테스트들은 주로 `UnitConverterService` 대상임
- `UnitConverter.java` 리팩토링 전에는 해당 파일을 보호하는 테스트가 먼저 필요함
- 테스트 없이 리팩토링을 시작하면 동작 회귀를 감지하기 어렵다는 점을 설명함

## 7. 최종 산출물

이번 세션의 최종 산출물은 다음과 같다.

- `report/17_기준선 확인 [Ask 모드]_보고서.md`
- `Prompting/17_기준선 확인 [Ask 모드]_보고서-Prompting.md`
