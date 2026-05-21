# 02. BCE·계약·RED·통합 보고서

## 1. 목적

이번 세션의 목적은 `UnitConverter_A02`를 구현하기 전에 BCE 관점으로 책임 경계를 고정하고, Domain 계약, Boundary 계약, Data 계약, RED 단계 테스트, 통합 검증 기준을 테스트 가능한 문장으로 정리하는 것이다.

대상 시스템은 `meter`, `feet`, `yard` 길이 단위 변환 학습 프로젝트다. 핵심은 변환 공식 구현이 아니라 입력 검증, 비율 정확도, 출력 원본 보존, 설정 실패, 동적 단위 등록, 회귀 보호를 분리된 책임과 테스트 계약으로 정의하는 것이다.

## 2. 기준 요구사항

- 기본 지원 단위는 `meter`, `feet`, `yard`이다.
- 고정 비율은 `1 meter = 3.28084 feet`, `1 meter = 1.09361 yard`이다.
- `feet`와 `yard` 사이의 변환은 직접 비율이 아니라 meter 허브 기준으로 계산한다.
- 입력은 정확히 `unit:value` 형식이어야 한다.
- 음수 길이 값은 거부한다.
- 미지원 단위는 동적 등록 전까지 변환 입력으로 허용하지 않는다.
- 출력은 원 입력 값과 원 입력 단위를 보존해야 한다.
- JSON, CSV, 표 출력은 동일한 Domain 결과를 다른 형식으로 표현해야 하며 계산 결과를 바꾸면 안 된다.
- 잘못된 설정 파일은 기본값으로 조용히 대체하지 않고 설정 로드 실패로 중단해야 한다.

## 3. BCE 책임 경계

### 3.1 Boundary

Boundary는 외부 입력과 외부 출력 계약을 담당한다.

- `unit:value` 문자열의 구조를 검증한다.
- JSON, CSV, 표, 콘솔 출력 스키마를 직렬화한다.
- Domain 또는 Control의 실패를 고정된 오류 메시지 패턴으로 변환한다.
- 변환 공식, 단위 비율, 파일 로드 정책을 소유하지 않는다.

### 3.2 Control

Control은 단일 유스케이스의 실행 흐름을 조정한다.

- Boundary가 전달한 요청을 Domain 요청으로 연결한다.
- 필요한 단위 정의를 Data 인터페이스에서 가져온다.
- Domain 성공 또는 실패를 Boundary가 표현할 수 있는 결과로 반환한다.
- JSON, CSV, 표 직렬화와 Domain 불변식 자체를 포함하지 않는다.

### 3.3 Entity / Domain

Entity는 단위 변환 도메인의 핵심 규칙을 소유한다.

- 길이 값의 유효성, 단위명, 단위 비율, 단위 등록 규칙을 검증한다.
- `source -> meter -> target` 변환 규칙을 수행한다.
- 입력 단위를 결과 대상에서 제외한다.
- Boundary, 파일 경로, 콘솔, JSON, CSV, YAML에 의존하지 않는다.

### 3.4 Data

Data는 단위 정의를 로드하고 검증 가능한 형태로 제공한다.

- InMemory 기본 단위 정의를 제공한다.
- JSON/YAML 설정에서 단위 정의를 로드한다.
- 형식 오류, 필수 값 누락, 숫자가 아닌 비율, 0 이하 비율을 설정 실패로 분류한다.
- 사용자 메시지와 출력 직렬화를 담당하지 않는다.

## 4. Domain 설계

### 4.1 개념 목록과 SRP

- `LengthValue`: 0 이상 십진 길이값을 표현한다.
- `UnitName`: 비어 있지 않은 단위 식별자를 표현한다.
- `UnitDefinition`: 단위명과 meter 허브 기준 비율을 묶는다.
- `UnitRegistry`: 등록 단위 조회, 목록 제공, 중복 등록 거부를 담당한다.
- `ConversionRequest`: 원 입력 단위와 길이값으로 구성된 변환 요청이다.
- `ConvertedValue`: 대상 단위와 변환 수치를 표현한다.
- `ConversionResult`: 원 입력을 제외한 대상 단위별 변환 결과 목록이다.
- `UnitConversionService`: 등록된 단위 정의를 사용해 결정적 변환을 수행한다.

### 4.2 Domain Invariants

- `LengthValue`는 0 이상이어야 한다.
- `UnitName`은 trim 후 빈 문자열이면 안 된다.
- `metersPerUnit`은 0보다 커야 한다.
- 기본 registry는 `meter`, `feet`, `yard`를 포함해야 한다.
- `1 meter = 3.28084 feet` 비율은 PRD 변경 없이 바뀌면 안 된다.
- `1 meter = 1.09361 yard` 비율은 PRD 변경 없이 바뀌면 안 된다.
- `feet`와 `yard` 변환은 meter 허브를 통해 계산해야 한다.
- 변환 결과는 입력 단위와 같은 target을 포함하면 안 된다.
- 동적 등록은 기존 단위명을 중복 등록하면 안 된다.
- 동적 등록은 기본 단위의 비율을 변경하면 안 된다.

### 4.3 Domain 유스케이스

- `CreateDefaultRegistry`: README 기본 단위와 비율을 가진 registry를 생성한다.
- `ConvertLength`: 입력 단위를 제외한 모든 지원 단위로 변환한다.
- `ConvertLengthToTarget`: 특정 target 단위 하나로 변환한다.
- `RegisterUnit`: 새 단위와 `metersPerUnit`을 등록한다.
- `ListSupportedUnits`: 현재 등록된 단위 목록을 반환한다.

### 4.4 Domain API 시그니처 수준

본문 없는 시그니처 후보만 정의한다.

```text
LengthValue LengthValue.of(String decimalText)
UnitName UnitName.of(String rawName)
UnitDefinition UnitDefinition.of(UnitName name, BigDecimal metersPerUnit)
UnitRegistry UnitRegistry.defaultLengthUnits()
UnitRegistry UnitRegistry.register(UnitDefinition definition)
UnitDefinition UnitRegistry.require(UnitName name)
List<UnitDefinition> UnitRegistry.list()
ConversionResult UnitConversionService.convert(ConversionRequest request, UnitRegistry registry)
ConvertedValue UnitConversionService.convertTo(ConversionRequest request, UnitName target, UnitRegistry registry)
```

### 4.5 Domain 실패 조건

- `INVALID_NUMBER`: 숫자 파싱 실패.
- `NEGATIVE_VALUE`: 길이 값이 0 미만.
- `INVALID_UNIT_NAME`: 단위명이 비어 있음.
- `INVALID_RATIO`: `metersPerUnit <= 0`.
- `DUPLICATE_UNIT`: 이미 등록된 단위명.
- `UNSUPPORTED_UNIT`: registry에 없는 단위.
- `SAME_SOURCE_TARGET`: 단일 target 변환에서 source와 target이 같음.

## 5. Boundary 설계

### 5.1 입력에서 출력까지의 시나리오

1. Boundary가 raw 문자열과 출력 형식을 받는다.
2. Boundary가 `unit:value` 구조를 검증한다.
3. Boundary가 숫자 파싱 가능성, 빈 값, 음수 값을 검증한다.
4. Boundary가 Control에 변환 요청을 전달한다.
5. Control이 Data 인터페이스에서 단위 정의를 얻고 Domain 변환을 실행한다.
6. Boundary가 성공 결과를 선택된 출력 형식으로 직렬화한다.
7. 실패 시 Boundary는 변환 결과 없이 고정 오류 스키마를 출력한다.

### 5.2 Input Schema

```text
raw: string
format: console | json | csv | table
```

검증 규칙:

- 콜론은 정확히 1개여야 한다.
- 콜론 앞 unit은 비어 있으면 안 된다.
- 콜론 뒤 value는 비어 있으면 안 된다.
- value는 십진수로 파싱 가능해야 한다.
- value는 0 이상이어야 한다.

### 5.3 Success Output Schema

```text
input.value: string
input.unit: string
results[].unit: string
results[].value: number
results[].displayValue: string
error: null
```

### 5.4 Error Schema

```text
input: string
results: []
error.code: string
error.message: string
```

### 5.5 에러 메시지 규칙

- `ERROR:INVALID_FORMAT:<detail>`
- `ERROR:INVALID_NUMBER:<detail>`
- `ERROR:NEGATIVE_VALUE:<detail>`
- `ERROR:UNSUPPORTED_UNIT:<unit>`
- `ERROR:CONFIG_LOAD_FAILED:<detail>`
- `ERROR:DUPLICATE_UNIT:<unit>`
- `ERROR:INVALID_RATIO:<detail>`

테스트는 error code를 정확히 비교하고, message는 고정 prefix와 detail segment 존재 여부를 검증한다.

## 6. Data 설계

### 6.1 목적

Data 레이어의 목적은 단위 정의를 로드하고, Entity가 이해할 수 있는 단위명과 meter 기준 비율로 전달하는 것이다.

### 6.2 인터페이스 계약 이름

- `UnitDefinitionSource`
- `UnitDefinitionRepository`
- `UnitDefinitionLoader`
- `UnitDefinitionValidator`
- `DefaultUnitDefinitionProvider`

### 6.3 InMemory와 File 비교

InMemory 방식은 테스트가 빠르고 결정적이며, 초기 Domain/Boundary RED 테스트에 적합하다. 파일 시스템, 파서, 인코딩 문제 없이 핵심 계약을 먼저 고정할 수 있다.

File 방식은 README의 JSON/YAML 설정 외부화 요구를 만족하지만, fixture 관리와 형식 오류 테스트가 필요하다. 잘못된 설정을 기본값으로 대체하지 않는 실패 계약이 함께 있어야 한다.

추천 순서는 InMemory 우선, 이후 File(JSON) 추가다. 이유는 핵심 비율과 BCE 경계를 먼저 고정해야 파일 파싱 오류가 도메인 테스트를 오염시키지 않기 때문이다.

### 6.4 Data 레이어 테스트

- 기본 provider가 `meter`, `feet`, `yard`를 반환해야 한다.
- JSON fixture가 `unit`, `metersPerUnit`을 정상 로드해야 한다.
- YAML fixture가 같은 schema를 정상 로드해야 한다.
- 깨진 JSON/YAML은 `CONFIG_LOAD_FAILED`여야 한다.
- `unit` 누락은 설정 실패여야 한다.
- `metersPerUnit` 누락은 설정 실패여야 한다.
- 숫자가 아닌 ratio는 설정 실패여야 한다.
- `metersPerUnit <= 0`은 설정 실패여야 한다.
- Data 실패 시 기본값으로 조용히 대체하면 안 된다.

## 7. RED 단계 테스트 케이스 목록

### 7.1 파싱

- `shouldParseValidUnitValueInput`  
  보호 invariant: `InputFormatIsUnitColonValue`
- `shouldRejectInputWithoutColonSeparator`  
  보호 invariant: `InputRequiresSingleColonSeparator`
- `shouldRejectInputWithMoreThanOneColonSeparator`  
  보호 invariant: `InputRequiresSingleColonSeparator`
- `shouldRejectBlankUnitName`  
  보호 invariant: `UnitNameMustNotBeBlank`
- `shouldRejectBlankNumericValue`  
  보호 invariant: `LengthValueMustBePresent`
- `shouldRejectNonDecimalNumericValue`  
  보호 invariant: `LengthValueMustBeDecimalParsable`

### 7.2 음수·0

- `shouldRejectNegativeLengthValue`  
  보호 invariant: `LengthValueMustBeZeroOrPositive`
- `shouldAcceptZeroLengthValue`  
  보호 invariant: `ZeroLengthIsValid`
- `shouldConvertZeroLengthToZeroForAllTargetUnits`  
  보호 invariant: `ZeroValueConversionStaysZero`
- `shouldRejectNegativeMetersPerUnitRatio`  
  보호 invariant: `UnitRatioMustBePositive`
- `shouldRejectZeroMetersPerUnitRatio`  
  보호 invariant: `UnitRatioMustBeGreaterThanZero`

### 7.3 미지원 단위

- `shouldRejectUnsupportedSourceUnit`  
  보호 invariant: `SourceUnitMustBeRegistered`
- `shouldProduceNoResultsForUnsupportedSourceUnit`  
  보호 invariant: `InvalidInputProducesNoConversionResults`
- `shouldReportUnsupportedUnitWithExactErrorCode`  
  보호 invariant: `UnsupportedUnitErrorCodeIsStable`
- `shouldNotTreatUnknownUnitAsMeterFallback`  
  보호 invariant: `UnsupportedUnitMustNotUseDefaultRatio`

### 7.4 비율 정확도

- `shouldConvertOneMeterToThreePointTwoEightZeroEightFourFeet`  
  보호 invariant: `ReadmeMeterToFeetRatioIsFixed`
- `shouldConvertOneMeterToOnePointZeroNineThreeSixOneYard`  
  보호 invariant: `ReadmeMeterToYardRatioIsFixed`
- `shouldConvertThreePointTwoEightZeroEightFourFeetToOneMeter`  
  보호 invariant: `FeetToMeterUsesMeterHub`
- `shouldConvertOnePointZeroNineThreeSixOneYardToOneMeter`  
  보호 invariant: `YardToMeterUsesMeterHub`
- `shouldConvertFeetToYardThroughMeterHub`  
  보호 invariant: `FeetYardConversionMustUseMeterHub`
- `shouldExcludeSourceUnitFromConversionResults`  
  보호 invariant: `ConversionResultsExcludeSourceUnit`

### 7.5 동적 등록

- `shouldRegisterCubitWithPositiveMetersPerUnit`  
  보호 invariant: `RegisteredUnitRequiresPositiveRatio`
- `shouldAllowConversionFromRegisteredCubit`  
  보호 invariant: `RegisteredUnitBecomesValidSourceUnit`
- `shouldIncludeRegisteredCubitAsTargetForDefaultUnitInput`  
  보호 invariant: `RegisteredUnitBecomesTargetUnit`
- `shouldRejectDuplicateUnitRegistration`  
  보호 invariant: `UnitRegistryRejectsDuplicateNames`
- `shouldRejectDynamicRegistrationWithBlankUnitName`  
  보호 invariant: `RegisteredUnitNameMustNotBeBlank`
- `shouldKeepDefaultRatiosUnchangedAfterDynamicRegistration`  
  보호 invariant: `DynamicRegistrationMustNotChangeDefaultRatios`

### 7.6 출력 직렬화 JSON

- `shouldSerializeJsonWithInputValueAndInputUnit`  
  보호 invariant: `JsonOutputPreservesOriginalInput`
- `shouldSerializeJsonResultsWithUnitValueAndDisplayValue`  
  보호 invariant: `JsonResultSchemaIsStable`
- `shouldSerializeJsonErrorAsNullOnSuccess`  
  보호 invariant: `JsonSuccessErrorFieldIsNull`
- `shouldSerializeJsonErrorObjectAndEmptyResultsOnFailure`  
  보호 invariant: `JsonFailureProducesErrorAndNoResults`

### 7.7 출력 직렬화 CSV

- `shouldSerializeCsvWithStableHeaderOrder`  
  보호 invariant: `CsvHeaderOrderIsStable`
- `shouldSerializeCsvRowsWithOriginalInputColumns`  
  보호 invariant: `CsvOutputPreservesOriginalInput`
- `shouldSerializeCsvRowsWithTargetUnitConvertedValueAndDisplayValue`  
  보호 invariant: `CsvResultColumnOrderIsStable`
- `shouldSerializeCsvFailureWithoutConversionRows`  
  보호 invariant: `CsvFailureProducesNoResultRows`

### 7.8 출력 직렬화 표

- `shouldSerializeTableWithStableColumnNames`  
  보호 invariant: `TableColumnNamesAreStable`
- `shouldSerializeTableRowsWithOriginalInputValueAndUnit`  
  보호 invariant: `TableOutputPreservesOriginalInput`
- `shouldSerializeTableRowsWithTargetUnitAndConvertedValue`  
  보호 invariant: `TableResultColumnsAreStable`
- `shouldSerializeTableFailureWithErrorRowOnly`  
  보호 invariant: `TableFailureDoesNotRenderConversionRows`

## 8. 통합 검증 계획

### 8.1 정상 시나리오

- `meter:2.5` 입력 시 `8.2 feet`, `2.7 yard`가 표시되고 `meter` target은 없어야 한다.
- `feet:3.28084` 입력 시 `1.0 meter`와 meter 허브 기반 yard 결과가 생성되어야 한다.
- `register:cubit:0.4572` 이후 `cubit:1`은 변환 입력 단위로 허용되어야 한다.

### 8.2 실패 시나리오

- `meter=2.5`는 `INVALID_FORMAT`이며 결과가 없어야 한다.
- `meter:2.5.1`은 `INVALID_NUMBER`이며 결과가 없어야 한다.
- `meter:-1`은 `NEGATIVE_VALUE`이며 결과가 없어야 한다.
- `mile:1`은 `UNSUPPORTED_UNIT`이며 결과가 없어야 한다.
- 깨진 JSON/YAML 설정은 `CONFIG_LOAD_FAILED`이며 변환이 실행되면 안 된다.

## 9. 회귀 보호 규칙

- README 비율 변경은 Domain 계약 테스트 변경 없이는 금지한다.
- `feet`와 `yard` 직접 비율 도입은 금지한다.
- 음수 입력 허용으로 정책을 바꾸려면 요구사항 문서와 RED 테스트를 먼저 변경해야 한다.
- 숫자 파싱 실패를 0 또는 기본값으로 대체하면 안 된다.
- 미지원 단위를 자동 등록하면 안 된다.
- 모든 출력 포맷은 원 입력 값과 원 입력 단위를 보존해야 한다.
- 결함 수정은 재현 RED 테스트가 먼저 있어야 한다.

## 10. 커버리지 목표

- Domain: 라인 커버리지 95% 이상, 핵심 invariant별 테스트 1개 이상.
- Boundary: 분기 커버리지 90% 이상, error code별 계약 테스트 1개 이상.
- Data: 라인 커버리지 85% 이상, 정상 fixture 2개와 실패 fixture 4개 이상.
- Integration: 라인 커버리지 80% 이상, 정상 2개 이상과 실패 3개 이상.

## 11. Traceability Matrix

| Concept | Rule | UseCase | Contract | Test | Component |
|---|---|---|---|---|---|
| 입력 형식 | 콜론 1개 | ConvertLength | Input schema | malformed boundary test | Boundary |
| 값 규칙 | 숫자 및 0 이상 | ConvertLength | Error schema | value invariant tests | Entity / Boundary |
| 기본 단위 | meter, feet, yard | CreateDefaultRegistry | Unit definitions | registry test | Entity / Data |
| 비율 계약 | README ratios | ConvertLength | results schema | precision tests | Entity |
| 입력 단위 제외 | source 제외 | ConvertLength | results[] | no self target test | Entity |
| 원 입력 보존 | value/unit 문자열 보존 | FormatResult | output schema | formatter tests | Boundary |
| 설정 실패 | 잘못된 설정으로 변환 금지 | LoadUnits | CONFIG_LOAD_FAILED | fixture failure tests | Data / Control |
| 동적 등록 | 중복 거부, ratio > 0 | RegisterUnit | register request | registration tests | Entity / Data |

## 12. 결론

이번 세션의 설계 결론은 Domain과 Boundary를 분리해 두 개의 테스트 트랙을 동시에 고정하는 것이다. Domain 트랙은 비율, 단위 등록, 변환 정확도, 입력 단위 제외 규칙을 보호한다. Boundary 트랙은 입력 스키마, 출력 스키마, 오류 메시지 패턴, 원 입력 보존을 보호한다.

구현을 시작하기 전 RED 테스트 제목과 invariant를 먼저 확정하면, 이후 리팩터링이나 출력 포맷 확장 중에도 README 비율과 실패 정책을 안정적으로 보호할 수 있다.
