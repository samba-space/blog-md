# 새로운 JAVA의 날짜와 시간 API
기존의 자바의 날짜와 시간 API(Date, Calender)는 여러가지 문제가 있었다. 그래서 많은 개발자들이 Joda-Time, Time and Money Code Library 등의 서드파티 라이브러리를 사용해왔다.  

더 휼륭한 날짜와 시간 API를 제공하기 위해 자바 8(JSR-310)은 Joda-Time의 많은 기능을 java.time 패키지에 추가하였다.  
기존의 날짜 API의 문제점과 새로운 날짜 API에 대해 알아보자.

## 기존 날짜 API의 문제점
날짜를 의미하는 Date라는 클래스는 이름과 달리 특정 시점을 날짜가 아닌 밀리초 단위로 표현한다.  
기존 Date, Calendar 클래스의 문제점을 알아보자.

### 불변 객체가 아니다.(not immutable)
불변 객체의 장점인 스레드 안정성, 여러객체에서의 안전한 공유이다. 그러나 Date, Calendar 모두 가변 클래스(mutable)다.  
setter 메서드가 존재하며 여러 객체에 공유 시, 부작용이 생길 수 있다.

### 0부터 시작하는 월 지정 인덱스
Date 클래스는 1월을 0으로 표현했다. Date를 보완하기 위해 제공된 Calendar 클래스 역시 월 인덱스는 0부터 시작한다. (Date의 1900년도에서 시작하는 오프셋은 없앴다.) 이는 개발자들이 헷갈릴수 있고, 실수를 유발한다.

### Date, Calendar 클래스 사용의 혼란과 일관성 문제
자바 1.1에서는 Date 클래스의 여러 메서드를 deprecated 시키고, Calendar라는 클래스를 제공했다.  
두가지 클래스가 등장하면서 개발자들에게 혼란이 가중됐다. 또, DateFormat 같은 일부 기능은 Date클래스에만 작동한다. (DateFormat 역시 스레드 safe하지 않다.)

이번에는 일관성 없는 요일 상수 문제를 살펴보자. deprecated되긴 했지만, Date.getDay() 메서드로 요일 값을 가져오면 일요일은 0부터 시작한다. 그러나 Calendar.get(Calendar.DAY_OF_WEEK) 메서드는 일요일이 1부터 시작한다. 둘 사이에도 일관성이 없음을 볼 수 있다.

### int 상수 필드의 남용
Calendar에서 날짜 연산 시 int 상수 필드를 사용한다.  

아래는 초를 더하는 예제인데, 첫번째 파라미터를 엉뚱한 상수 Calendar.JUNE을 사용해도 컴파일 시점에 확인할 방법이 없다.

```java
calendar.add(Calendar.SECOND, 2);
```

### 그외 문제점
Date 클래스의 toString으로 반환되는 문자열을 추가 활용하기 어렵다.  
또, Date는 JVM 기본시간대인 CET(중앙 유럽 시간)을 사용했다. 그렇다고 Date 클래스가 자체적으로 시간대 정보를 알고 있지 않다.

## 새로운 자바의 날짜와 시간 API
새로운 날짜와 시간 API는 ISO 달력 체계에 기반을 두고 있으며, ISO달력은 그레고리 규칙을 따르고 사실상의 세계 달력이다.  
또, 모든 class는 immutable하고 tread-safe하다. 각각의 class에 대해 알아보자.

### LocalDate
LocalDate 인스턴스는 시간을 제외한 날짜를 표현하는 불변 객체이다. 어떤 시간대 정보도 포함하지 않는다.  
LocalDate 사용법에 대해 알아보자.  

**LocalDate.of** 정적 팩토리 메서드로 인스턴스를 만들 수 있다.
```java
LocalDate date = LocalDate.of(2017, 9, 21);
```

**LocalDate.now**로 시스템 시계의 정보를 이용해서 현재 날짜 정보를 얻는다.
```java
LocalDate today = LocalDate.now();//2021-09-26 출력
```

**LocalDate.get** 메서드에 TemporalField를 전달해서 정보를 얻는 방법이 있다.
```java
int year = date.get(ChronoField.YEAR);
int month = date.get(ChronoField.MONTH_OF_YEAR);
int day = date.get(ChronoField.DAY_OF_MONTH);
```

또 다른 방법으로 내장 메서드 **getYear()**, **getMonthValue()**, **getDayOfMonth()** 등을 이용해 가독성을 높일 수 있다.
```java
int year = date.getYear();
int month = date.getMonthValue();
int day = date.getDayOfMonth();
```

### LocalTime
LocalTime 인스턴스 역시 날짜를 제외한 시간을 표현하는 불변 객체이다.  

**LocalTime.of** 로 인스턴스를 만들 수 있으며, 두가지 오버로드 버전의 메서드가 존재한다.
```java
LocalTime time1 = LocalTime.of(13, 45, 20);//시간, 분, 초
LocalTime time2 = LocalTime.of(13, 45);//시간, 분
```

**getHour()**, **getMinute()**, **getSecond()** 메서드로 각 정보를 가져올 수 있다.
```java
int hour = time.getHour();
int minute = time.getMinute();
int second = time.getSecond();
```

### LocalDateTime
LocalDate, LocalTime을 갖는 복합 클래스다. 즉, 날짜와 시간 모두 표현할 수 있다.  

**LocalDateTime.of**로 인스턴스를 만들 수 있다.
```java
LocalDateTime dt1 = LocalDateTime.of(2017, 9, 21, 13, 45, 20);//년,월,일,시,분,초를 모두 받는 경우
LocalDateTime dt2 = LocalDateTime.of(date, time);//LocalDate, LocalTime을 받는 경우
```

**LocalDate.atTime**, **LocalTime.atDate**로 LocalDateTime을 만들 수 있다.
```java
LocalDate date = LocalDate.of(2017, 9, 21);
LocalTime time = LocalTime.of(13, 45, 20);

LocalDateTime dt1 = date.atTime(time);
LocalDateTime dt2 = time.atDate(date);
```

반대로 **toLocalDate**, **toLocalTime**으로 LocalDate, LocalTime 인스턴스를 추출할 수 있다.
```java
LocalDateTime dt1 = LocalDateTime.of(2017, 9, 21, 13, 45, 20);

LocalDate date = dt1.toLocalDate();
LocalTime time = dt1.toLocalTime();
```

### Instant
새로운 java.time.Instant 클래스에서는 기계적인 관점에서 시간을 표현한다. Instant는 Unix time(Unix epoch time)을 기준으로 특정 지점까지의 시간을 초로 표현한다.
> ##### Unix time은 시스템에서 날짜와 시간의 흐름을 나타낼 때 기준을 삼는 시간을 의미한다.  (1970년 1월 1일 0시 0분 0초 UTC)  

<br>
**Instant.ofEpochSecond**에 초를 넘겨 Instant 인스턴스를 만들 수 있다. Instant 클래스는 나노초(10억분의 1)의 정밀도를 제공한다.  
아래의 예는 모두 같은 값을 가진다.

```java
Instant instant1 = Instant.ofEpochSecond(3);
Instant instant2 = Instant.ofEpochSecond(3, 1);
Instant instant3 = Instant.ofEpochSecond(2, 1_000_000_000);//2초 + 10억 나노초
Instant instant4 = Instant.ofEpochSecond(4, -1_000_000_000);//4초 - 10억 나노초
```

**Instant.now**로 현재 순간의 Instant 객체를 가져올 수 있다.
```java
Instant now = Instant.now();//2021-09-26T11:27:16.486177Z
```

앞서 설명한 모든 클래스는 Temporal 인터페이스를 구현하는데, Temporal 인터페이스는 특정 시간을 모델링하는 객체의 값을 어떻게 읽고 조작할지 정의한다.

### Duration, Period
Duration과 Period를 이용해서 두 시간 객체 사이의 지속시간을 만들어보자.  

**Duration.between** 정적 팩토리 메서드로 두 시간 객체 사의의 지속시간을 만들 수 있다. LocalDateTime은 사람이 사용하도록, Instant는 기계가 사용하도록 만들어져서 서로 혼합할 수 없다.
```java
Duration d1 = Duration.between(time1, time2);//LocalTime끼리
Duration d2 = Duration.between(dateTime1, dateTime2);//LocalDateTime끼리
Duration d3 = Duration.between(instant1, instant2);//Instant끼리
```

**Period.between** 정적 팩토리 메서드 년, 월, 일로 지속시간을 표현할 때 사용한다. 왜냐하면, Duration은 초와 나노초로 시간 단위를 표현하므로 between 메서드에 LocalDate를 전달할 수 없다.
```java
Period tenDays = Period.between(LocalDate.of(2017, 9, 11),
                                LocalDate.of(2017, 9, 21));
```

추가적으로 Duration과 Period 클래스 인스턴스를 만들 수 있는 [다양한 팩토리 메서드](https://docs.oracle.com/javase/8/docs/api/index.html)는 API 링크를 참고하자.

## 날짜 조정
새로 추가된 날짜, 시간 객체들은 불변객체이므로 변경이 필요한 경우 변경된 객체 버전을 만들 수 있는 메서드를 제공한다.

**with~** 메서드로 기존의 LocalDate를 변경한 객체를 만들 수 있다.(Temporal 객체 필드를 갱신한 복사본 : 함수형 갱신)  
with 메서드는 get 메서드와 쌍을 이루며, 날짜 시간관련 모든 클래스가 구현하는 Temporal 인터페이스에 정의되어 있다.
```java
LocalDate date1 = LocalDate.of(2021, 4, 30);//2021-04-30
LocalDate date2 = date1.withYear(2002);//2002-04-30
LocalDate date3 = date2.withDayOfMonth(25);//2002-04-25
//TemporalField를 인수로 좀 더 범용적으로 활용가능하다.
LocalDate date4 = date3.with(ChronoField.MONTH_OF_YEAR, 2);//2002-02-25
```

**plus, minus~** 메서드로 Temporal을 특정 시간만큼 앞뒤로 이동시킬 수 있다.(상대적인 방식)
```java
LocalDate date1 = LocalDate.of(2021, 9, 24);//2021-09-24
LocalDate date2 = date1.plusWeeks(1);//2021-10-01
LocalDate date3 = date2.minusYears(6);//2015-10-01
//숫자, TemporalUnit을 활용한 케이스. 
//ChronoUnit Enum은 TemporalUnit 인터페이스를 쉽게 활용할 수 있는 구현 제공
LocalDate date4 = date3.plus(6, ChronoUnit.MONTHS);//2016-04-01
```

**TemporalAdjuster** 를 **오버로드된 with** 메서드에 전달하여 좀 더 복잡한 날짜 조정을 직관적으로 할 수 있다.
```java
LocalDate date1 = LocalDate.of(2021, 9, 24);//2021-09-24
//현재 날짜와 같거나 이후의 일요일을 반환
LocalDate date2 = date1.with(nextOrSame(DayOfWeek.SUNDAY));//2021-09-26
//월의 마지막 날을 반환
LocalDate date3 = date2.with(lastDayOfMonth());//2021-09-30
```

## Formatting과 Parsing
포매팅과 파싱은 떨어질 수 없는 관계이며, java.time.format에 새로 추가되었다.  
### DateTimeFormatter 클래스
가장 중요한 클래스는 **DateTimeFormatter**다. 기존의 java.util.DateFormat 클래스와 달리 tread-safe하다.  

**format** 메서드로 날짜나 시간을 특정 형식의 문자열로 만들 수 있다.
```java
LocalDate date = LocalDate.of(2021, 4, 30);
String s1 = date.format(DateTimeFormatter.BASIC_ISO_DATE);//20210430
String s2 = date.format(DateTimeFormatter.ISO_LOCAL_DATE);//2021-04-30
```

**parse** 메서드로 반대로 날짜나 시간을 표현하는 문자열을 파싱해서 날짜 객체로 만들 수 있다.
```java
//문자열의 형식과 formatter가 일치해야함
LocalDate date1 = LocalDate.parse("20210430", DateTimeFormatter.BASIC_ISO_DATE);
LocalDate date2 = LocalDate.parse("2021-04-30", DateTimeFormatter.ISO_LOCAL_DATE);
```

**ofPattern** 메서드로 패턴 문자열에 해당하는 formatter를 만든다.
```java
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
LocalDate date1 = LocalDate.of(2021, 9, 24);
String formattedDate = date1.format(formatter);
LocalDate date2 = LocalDate.parse(formattedDate, formatter);
```

**오버로드된 ofPattern** 메서드로 Locale formatter를 만들 수 있다.
```java
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.ITALIAN);
LocalDate date1 = LocalDate.of(2021, 9, 24);
String formattedDate = date1.format(formatter);//24. settembre 2021
LocalDate date2 = LocalDate.parse(formattedDate, formatter);
```

### DateTimeFormatterBuilder 클래스
복합적인 formmater를 정의해서 좀 더 세부적으로 포매터를 제어할 수 있다.
```java
DateTimeFormatter italianFormatter = new DateTimeFormatterBuilder()
        .appendText(ChronoField.DAY_OF_MONTH)
        .appendLiteral(". ")
        .appendText(ChronoField.MONTH_OF_YEAR)
        .appendLiteral(" ")
        .appendText(ChronoField.YEAR)
        .parseCaseInsensitive()
        .toFormatter(Locale.ITALIAN);
```

## 다양한 시간대와 캘린더 활용
새로운 날짜와 시간 API의 시간대를 간단하게 처리할 수 있다는 편리함이 있다. 기존의 java.util.TimeZone을 대체할 수 있는 java.time.ZoneId 클래스가 새롭게 등장했다. 새로운 클래스로 서머타임 같은 복잡한 사항이 자동으로 처리된다.(ZoneId 역시 불변 클래스이다.)

### 시간대(time zone) 사용
표준 시간이 같은 지역을 묶어서 time zone 규칙 집합을 정의한다.  

**ZoneId.getRules**를 이용해서 해당 시간대의 규정을 획득할 수 있다. 
```java
ZoneId romeZone = ZoneId.of("Europe/Rome");
```
> #### 지역 ID(zoneId)는 '{지역}/{도시}' 형식이며, [IANA Time Zone Database](https://www.iana.org/time-zones)에서 제공하는 집합 정보를 사용한다.  

<br>
기존의 TimeZone의 새로 추가된 **toZoneId** 메서드로 TimeZone 객체를 ZoneId 객체로 변환할 수 있다.
```java
ZoneId zoneId = TimeZone.getDefault().toZoneId();
```

**ZonedDateTime** 클래스는 지정한 시간대에 상대적인 시점을 표현한다.
LocalDate, LocalDateTime, Instant를 이용해서 **ZonedDateTime** 인스턴스로 변환할 수 있다.  
```java
ZonedDateTime zdt1 = localDate.atStartOfDay(romeZone);
ZonedDateTime zdt2 = dateTime.atZone(romeZone);
ZonedDateTime zdt3 = instant.atZone(romeZone);
```

**LocalDateTime.ofInstant** 메서드에 ZoneId를 넘겨서 Instant를 LocalDateTime으로 변환할 수 있다.
```java
Instant instant = Instant.now();
LocalDateTime timeFromInstant = LocalDateTime.ofInstant(instant, romeZone);
```

또, 기존 **Date의 toInstant()**, 정적 메서드인 **Date.from(Instant instant)**를 이용해서 폐기된 API와 새 API 간의 동작에 도움을 줄 수 있다.

### UTC/GMT 기준의 고정 오프셋
때로는 UTC(협정 세계시)/GMT(그리니치 표준시)를 기준으로 시간대를 표현하기도 한다.  

다음은 **ZoneOffset** 클래스로 뉴욕은 런던보다 5시간 느리다를 표현한 예이다.
```java
ZoneOffset newYorkOffset = ZoneOffset.of("-05:00");
```
ZoneOffset으로는 서머타임을 제대로 처리할 수 없어 권장하지 않는다.

또, **OffsetDateTime** 클래스는 ISO-8601 캘린더 시스템에서 정의하는 UTC/GMT와 오프셋으로 날짜와 시간을 표현한다.
```java
LocalDateTime dateTime =  LocalDateTime.of(2017, 9, 21, 13, 45, 20);
OffsetDateTime dateTimeInNewYork = OffsetDateTime.of(dateTime, newYorkOffset);
```

### 대안 캘린터 시스템
ISO-8601 캘린더 시스템이 실질적으로 전 세계에서 통용되지만, 자바 8에서는 추가로 4개의 캘린더 시스템을 제공한다.  
**ThaiBuddhistDate**, **MinguoDate**, **JapaneseDate**, **HijrahDate** 클래스 그에 해당한다.  
4개의 클래스와 LocalDate는 ChronoLocalDate 인터페이스를 구현하는데, ChronoLocalDate는 임의의 연대기에서 특정 날짜를 표현할 수 있는 기능을 제공하는 인터페이스다.  

예를 들어 **JapaneseDate.from** 정적 메서드에 LocalDate를 이용해서 Temporal 인스턴스를 만들 수 있다.
``` java
LocalDate date = LocalDate.of(2021, 4, 30);
JapaneseDate japaneseDate = JapaneseDate.from(date);
```

또는 Locale에 대한 날짜 인스턴스로 캘린더 시스템을 만들 수 있다. 새로운 **Chronology** 클래스는 캘린더 시스템을 의미하며 정적 팩토리 메서드 **ofLocale**로 인스턴스를 만든다.
```java
Chronology japaneseChronology = Chronology.ofLocale(Locale.JAPAN);
ChronoLocalDate now = japaneseChronology.dateNow();
```

날짜, 시간 API 설계자는 ChronoLocalDate보다는 LocalDate를 사용하라고 권고한다. 개발자가 1년은 12개월로 이루어진다와 같은 가정은 멀티캘린더에서 적용되지 않기 때문이다.  
그렇기 때문에 프로그램의 입출력을 지역화하는 상황을 제외하고는 모든 데이터 저장, 조작, 비지니스 규칙 해석 등의 작업에서 LocalDate를 사용해야 한다.

## 참고
- modern java in action
- https://docs.oracle.com/javase/8/docs/api/index.html
- https://d2.naver.com/helloworld/645609
- https://m.blog.naver.com/sehyunfa/221672212122
- https://stackoverflow.com/questions/32437550/whats-the-difference-between-instant-and-localdatetime
- https://www.daleseo.com/java8-zoned-date-time/