# 스프링 메시지, 국제화 기능
스프링은 `MessageSource`라는 메시지의 매개변수화 및 국제화를 지원하는 메시지 해결을 위한 전략 인터페이스를 제공한다.

## 메시지, 국제화 기능이 필요한 이유
메시지, 국제화 기능은 직접 구현할 수 있지만 번거롭다. Spring에서는 두 기능 모두 제공한다. 타임리프도 스프링 메시지, 국제화 기능을 편리하게 통합해서 제공한다.
### 메시지
메시지 같은 경우, 화면 딴에서 html에 코드에 메시지나 java 소스에서 에러 메시지가 하드코딩 되어 있는 경우 수정이 
필요하게 되면 이를 찾아 일일이 고쳐주어야 한다. 고쳐야할 메시지가 많을 경우 번거로울수 밖에 없다.  
그래서 메시지를 한 곳에서 관리하도록 하는 메시지 기능이 필요하다.  

### 국제화
국제화는 한국에서 접속할 경우 한글을 제공하고, 미국에서 접속하면 영어를 제공하는 기능을 말한다.  
이를 직접 일일이 구현하기 번거롭다. 한국에서 접근하는지 영어권에서 접근하는지 http 헤더의 `accept-language` 값을  
사용할 수 있고, 사용자가 직접 언어를 선택하여 이를 쿠키 등에 넣어 사용할 수 있다.

## 스프링 MessageSource 빈 등록
메시지 관리 기능을 사용하려면 스프링이 제공하는 `MessageSource`를 스프링 빈으로 등록하면 된다.  
`MessageSource`는 인터페이스이므로 구현체인 `ResourceBundleMessageSource`를 스프링 빈으로 등록하면 된다.

### 직접 빈 등록
```java
@Bean
public MessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasenames("messages", "errors");
    messageSource.setDefaultEncoding("utf-8");
    return messageSource;
}
```

`messageSource.setBasenames`는 설정 파일의 이름을 지정한다. 여러개 파일이 지정 가능하다.  
위의 예에서는 messages로 지정했으므로 `messages.properties` 파일을 읽어서 사용한다.  
또, 국제화를 기능을 적용하려면 `messages_ko.properties`, `messages_en.properties`와 같이 파일명에 언어 정보를 주면된다. 만약 찾는 국제화 파일이 없다면 기본적으로 언어정보가 없는 `messages.properties` 파일을 사용한다.

`messageSource.setDefaultEncoding`은 인코딩 정보를 지정한다.

### 자동 빈 등록
스프링 부트에서는 `MessageSource`가 자동으로 스프링 빈으로 등록된다. 설정 파일은 messages가 기본 값이다.  
국제화 역시 messages_ko, messages_en과 같이 파일명을 지어주면 된다.  
다르게 설정하려면 `application.properties`에 `spring.messages.basename=error` 과 같이 설정해주면 된다.

### message.properties 파일 설정
파일의 경로는 `/resources` 에 넣어주면 된다. 아래와 같이 설정해주면 된다.  
`hello.name`같은 경우 파라미터를 받을수 있는 형태이다.

**/resources/messages.properties**
```ini
hello=안녕
hello.name=안녕 {0}
```

**/resources/messages_en.properties**
```ini
hello=hello
hello.name=hello {0}
```

## 스프링 MessageSource 사용
이제 스프링 `MessageSource`를 직접 사용해보자.  

### MessageSource
```java
public interface MessageSource {

	@Nullable
	String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale);

	String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException;

	String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException;
}
```

`MessageSource`의 메서드를 살펴보면 code등의 파라미터로 메시지를 얻을 수 있다.  사용 예를 살펴보자.

```java
@Test
void helloMessage() {
    String result = messageSource.getMessage("hello", null, null);
    assertThat(result).isEqualTo("안녕");
}
```

code를 제외하고 모두 null 값을 주었다. locale 정보가 null 이므로 basename에서 설정한 기본 파일에서 조회한다.

```java
@Test
void notFoundMessageCode() {
    assertThatThrownBy(() -> messageSource.getMessage("no_code", null, null))
            .isInstanceOf(NoSuchMessageException.class);
}

@Test
void notFoundMessageCodeDefaultMessage() {
    String result = messageSource.getMessage("no_code", null, "기본 메시지", null);
    assertThat(result).isEqualTo("기본 메시지");
}
```

`notFoundMessageCode` 메서드는 code 값이 해당파일에 없는 경우 `NoSuchMessageException`이 발생한다.  
`notFoundMessageCodeDefaultMessage` 메서드처럼 3번째 파라미터로 defaultMessage를 지정해 줄 수 있다.

```java
@Test
void argumentMessage() {
    String message = messageSource.getMessage("hello.name", new Object[]{"Spring"}, null);
    assertThat(message).isEqualTo("안녕 Spring");
}
```

앞서 messages.properties 파일의 `hello.name=안녕 {0}`에 파라미터를 줄 수 있다고 했다. Object 배열을 통해서 "Spring"을 파라미터로 넘겨줬다.

```java
@Test
void international() {
    assertThat(messageSource.getMessage("hello", null, null)).isEqualTo("안녕");
    assertThat(messageSource.getMessage("hello", null, Locale.KOREA)).isEqualTo("안녕");
    assertThat(messageSource.getMessage("hello", null, Locale.ENGLISH)).isEqualTo("hello");
}
```

Locale에 null을 넘긴 경우 locale가 없으므로 기본 파일 messages를 사용한다.  
Locale에 `Locale.KOREA`를 넘긴 경우 messages_ko 파일이 없으므로 messages를 사용한다.  
Locale에 `Locale.ENGLISH`를 넘긴 경우 message_en 파일이 존재하므로 해당 파일을 사용한다.

### 타임리프 메시지 적용
타임리프의 메시지 표현식 `#{...}`를 사용하여 스프링의 메시지를 조회할 수 있다.  
`messages.properties`파일의 `label.item=상품`이면 아래와 같이 타임리프에서 사용할 수 있다.

```html
<div th:text="#{label.item}"></div>
```

아래와 같이 랜더링 된다.

```html
<div>상품</div>
```

## 스프링의 국제화 메시지 선택
스프링은 Locale을 알아야 언어를 선택할 수 있다. 스프링은 언어 선택 시 기본으로 `Accept-Language` 헤더 값을 사용한다.
브라우저의 언어 설정을 변경해서 request 헤더의 `Accept-Language` 값이 변경된다. 그러나 많은 사용자들은 직접 브라우저 언어 설정 변경을 하지 않는다. Locale 선택 방식을 변경하려면 `LocaleResolver`를 사용하면 된다.

### LocaleResolver
스프링은 Locale 선택 방식을 변경할 수 있도록 `LocaleResolver`라는 인터페이스를 제공한다.  
`LocaleResolver`의 구현체를 변경해서 쿠키나 세션 기반의 Locale 선택 기능을 사용할 수 있다.  

```java
public interface LocaleResolver {

	Locale resolveLocale(HttpServletRequest request);

	void setLocale(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale);
}
```

## Reference
- https://docs.spring.io/spring-framework/docs/current/javadoc-api/
- 인프런 스프링 MVC 2편(김영한) 
