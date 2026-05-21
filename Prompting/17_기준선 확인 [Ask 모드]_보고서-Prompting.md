# 17_기준선 확인 [Ask 모드] 보고서 - Prompting

## 1. 세션 목적

이번 세션의 목적은 코드 수정 없이 `UnitConverter.java` 기준선과 테스트 존재 여부를 확인하고, 리팩토링 전 테스트 필요성을 정리하는 것이었다.

주요 요구는 다음과 같았다.

- `UnitConverter.java` 확인
- `test_*.java` 파일 존재 여부 확인
- 테스트가 없다면 어느 파일에 대한 테스트가 필요한지 답변
- 테스트 없이 리팩토링을 시작하면 안 되는 이유를 한 줄로 설명
- 이번 세션 내용을 보고서와 Prompting 파일로 내보내기

## 2. 사용 프롬프트 기록

### Prompt 1. 기준선 확인 요청

```text
아직 코드는 수정하지 말고 분석만 해줘.
@UnitConverter.java 를 보고
아래 두 가지만 먼저 답해줘:
1. 현재 테스트 파일(test_*.java)이 존재하는지 확인하고,
   없다면 어느 파일에 대한 테스트가 필요한지 알려줘
2. 테스트 없이 리팩토링을 시작하면 안 되는 이유를 한 줄로 설명해줘
```

### Prompt 2. 세션 보고서 및 Prompting 파일 내보내기 요청

```text
이번세션에서 진행한 내용을 report 폴더의 17_기준선 확인 [Ask 모드]_보고서 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더의 17_기준선 확인 [Ask 모드]_보고서-Prompting.md 파일로 내보내줘
```

## 3. 프로젝트 탐색 기록

먼저 `UnitConverter.java` 파일과 `test_*.java` 파일 존재 여부를 확인했다.

확인 결과 `UnitConverter.java`는 두 곳에 있었다.

```text
UnitConverter.java
src/main/java/UnitConverter.java
```

`test_*.java` 패턴으로 검색한 결과 해당 형식의 파일은 발견되지 않았다.

이후 JUnit 관례에 맞는 `*Test.java` 파일도 추가로 확인했다.

```text
src/test/java/com/example/unitconverter/UnitConverterServiceTest.java
src/test/java/com/example/unitconverter/UnitConverterDualTrackRedTest.java
src/test/java/com/example/unitconverter/GoldenMasterApprovalTest.java
src/test/java/com/example/unitconverter/GoldenMasterTest.java
```

`pom.xml`도 확인하여 Maven 기반 JUnit 5 프로젝트임을 확인했다.

## 4. 분석 기록

루트의 `UnitConverter.java`는 콘솔 입력, 변환 계산, 콘솔 출력이 한 클래스에 들어 있는 구조였다.

`src/main/java/UnitConverter.java`는 `convert(String sourceUnit, double value, String targetUnit)` 메서드를 제공하며, 현재는 `meter`에서 `feet`로 변환하는 경로만 구현되어 있었다.

기존 테스트 파일들은 대부분 `UnitConverterService`를 직접 대상으로 하고 있었기 때문에, `UnitConverter.java` 자체를 리팩토링하기 위한 직접적인 안전망으로 보기는 어렵다고 판단했다.

## 5. 최종 응답 요약

Prompt 1에 대한 최종 응답에서는 다음 내용을 전달했다.

- `test_*.java` 형식의 테스트 파일은 없음
- `src/test/java/.../*Test.java` 형식의 JUnit 테스트는 존재함
- 현재 테스트들은 주로 `UnitConverterService` 대상임
- `src/main/java/UnitConverter.java` 리팩토링 전에는 해당 파일에 대한 테스트가 필요함
- 루트의 `UnitConverter.java`도 유지 대상이라면 콘솔 입력/출력 동작 테스트가 필요함
- 테스트 없이 리팩토링하면 기존 변환 동작과 출력 형식이 깨져도 즉시 감지할 안전망이 없다고 설명함

## 6. 수정 기록

Prompt 2에 따라 이번 세션 내용을 다음 파일로 내보냈다.

```text
report/17_기준선 확인 [Ask 모드]_보고서.md
Prompting/17_기준선 확인 [Ask 모드]_보고서-Prompting.md
```

프로덕션 코드와 테스트 코드는 수정하지 않았다.
