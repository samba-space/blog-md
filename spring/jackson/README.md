# jackson 사용 시, 메서드명 주의하기
프로젝트를 진행하면서, 직렬화/역직렬화 시 jackson 라이브러리를 사용하고 있다.(spring 내부에서) 직렬화 시에 메서드가 json 필드로 들어가는 경우가 있어서, jackson에 대해 공부해보고, 해결했던 과정을 기록하려한다.

## 원인
기존에 특정 정보를 redis에 저장, 조회하여 사용하고 있었다. 그런데 해당 객체에 `isNotAdmin`메서드를 추가하니 redis에서 해당 정보를 조회 시에 `UnrecognizedPropertyException`가 발생했다. 

```java
public class Member {

    private List<RoleType> roles;

    public List<RoleType> getRoles() {
        return roles;
    }

    public boolean isNotAdmin() {
        return roles.stream().noneMatch(RoleType.ADMIN::equals);
    }
}
```

원인을 찾아보니 역직렬화(deserialize)될 때, `notAdmin`이라는 필드가 없어서 그랬던 것이다. `Member`가 redis에 저장될 때, 즉 직렬화(serialize)될 때, `isNotAdmin` 메서드가 `notAdmin`이라는 필드로 들어갔던 것이다. 
왜 `isNotAdmin` 메서드가 필드로 인식되어 들어갔을까? 이를 알기 위해 jackson이 어떤 필드를 직렬화/역직렬화 대상으로 결정하는지 알아볼 필요가 있었다. 우선 jackson에 대해 알아보자.


## jackson이란?
Jackson 은 자바용 json 라이브러리로 잘 알려져 있지만 Json 뿐만 아니라 XML/YAML/CSV 등 다양한 형식의 데이타를 지원하는 data-processing 툴이다.

스트림 방식이므로 속도가 빠르며 유연하며 다양한 third party 데이터 타입을 지원하며 annotation 방식으로 메타 데이타를 기술할 수 있으므로 JSON 의 약점중 하나인 문서화와 데이터 validation 문제를 해결할 수 있다.

## Spring에서 jackson의 동작방식
Spring에서 request body의 객체 변환과 객체의 response body 변환을 `HttpMessageConverter` 인터페이스를 통해서 제공한다. `@RequestBody`, `@ResponseBody`가 없으면 `HttpMessageConverter`가 아닌 View Resolver가 사용된다.

JSON 요청, 응답의 형변환을 담당하는 것이 `MappingJacksonHttpMessageConverter`이다. `MappingJacksonHttpMessageConverter`는 Jackson의 `ObjectMapper`를 사용한다. `@RequestBody`로 JSON 데이터가 넘어오면 JSON을 java object로 변환해주고, 반대로 `@ResponseBody`일 경우 리플렉션을 사용해 객체를 가져와 JSON으로 변환해준다.
>>> ##### 스프링 3.1 부터는 클래스패스에 Jackson 라이브러리가 있으면 `MappingJacksonHttpMessageConverter` 자동 등록된다.

## jackson은 어떤 필드를 직렬화/역직렬화 대상으로 결정할까?
가장 간단하게 public 필드는 직렬화와 역직렬화 모두 가능하다. 
```java
public class Person {
    public String name;

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }
}
```
`Person`의 name을 public으로 설정하여 테스트해보면, 직렬화/역직렬화 테스트에 모두 통과한다.

```java
@Test
void public_필드_직렬화() throws JsonProcessingException {
    //given
    Person person = new Person("park");

    //when
    String result = mapper.writeValueAsString(person);

    //then
    assertThat(result, containsString("name"));
}

@Test
void public_필드_역직렬화() throws JsonProcessingException {
    //given
    String json = "{\"name\":\"park\"}";

    //when
    Person result = mapper.readValue(json, Person.class);

    //then
    assertThat(result.name, equalTo("park"));
}
```

public 하지 않은 필드의 경우 어떻게 될까?

### getter 메서드는 non-public 필드를 직렬화/역직렬화 가능하게 만든다.
getter 메서드가 있으면 non-public 필드를 직렬화할 수 있다. 또한, getter는 private 필드도 역직렬화할 수 있도록 해준다. 왜냐하면, **getter가 있으면 해당 필드는 property로 간주**되기 때문이다.  

```java
public class Person {
    private String name;

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
```

name 필드를 private으로 설정하고 getter 메서드를 추가하면, 위의 public 필드와 마찬가지로 직렬화/역직렬화 테스트에 통과하게 된다.


### setter 메서드는 non-public 필드를 역직렬화 가능하게 만든다.
setter 메서드는 non-public 필드를 역직렬화만 가능하게 만든다.

```java
public class Person {
    private String name;

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String accessName() {
        return name;
    }
}
```

name은 private으로 설정하고, setter 메서드와 테스트코드를 위해 getter naming convention을 따리지 않는 accessName 메서드를 추가하였다.

```java
@Test
void public_필드_역직렬화() throws JsonProcessingException {
    //given
    String json = "{\"name\":\"park\"}";

    //when
    Person result = mapper.readValue(json, Person.class);

    //then
    assertThat(result.accessName(), equalTo("park"));
}
```

역직렬화 테스트는 통과한다. 

```java
@Test
void public_필드_직렬화() throws JsonProcessingException {
    //given
    Person person = new Person("park");

    //when
    String result = mapper.writeValueAsString(person);

    //then
    assertThat(result, containsString("name"));
}
```

직렬화 테스트의 경우 `InvalidDefinitionException`이 발생하며, 다음과 같은 메시지가 출력된다. `No serializer found for class com.admin.model.Person and no properties discovered to create BeanSerializer` 
해석 해보면, serializer가 없고 BeanSerializer 생성을 위한 property가 없다는 뜻이다.

## 해결
원인은 getter가 있으면 해당 필드는 property로 간주되는 것 때문이였다. `isNotAdmin` 메서드가 boolean naming convention에 따라 getter로 인식되어 `notAdmin`이 property로 간주되었던 것이다.  
`notAdmin`은 직렬화되어 redis에 저장되었고, 조회 시(역직렬화)에 `Member`에는 notAdmin이라는 필드가 없어서 `UnrecognizedPropertyException`가 발생한 것이다.  

해결책으로 해당 메서드에 `@JsonIgnore`를 추가하거나 메서드명을 `checkNotAdmin` 이런식으로 변경해주면 된다.

```java
@JsonIgnore
public boolean isNotAdmin() {
    return roles.stream().noneMatch(RoleType.ADMIN::equals);
}
```

`isNotAdmin` 메서드 위에 `@JsonIgnore`를 추가하여, 직렬화/역직렬화 대상에서 제외하였다.  

```java
public boolean checkNotAdmin() {
    return roles.stream().noneMatch(RoleType.ADMIN::equals);
}
```
메서드명을 boolean getter로 인식되지 않게 수정하였다.

```java
@Test
void 직렬화대상제외() throws JsonProcessingException {
    //given
    Member member = new Member(List.of(RoleType.ADMIN));

    //when
    String result = mapper.writeValueAsString(member);

    //then
    assertThat(result, not(containsString("notAdmin")));
}
```

`notAdmin`이라는 property가 생성되지 않고, 테스트가 잘 통과된다.


## 참조
- https://github.com/FasterXML/jackson-docs
- https://www.baeldung.com/jackson-field-serializable-deserializable-or-not
- https://bactoria.github.io/2019/08/16/ObjectMapper%EB%8A%94-Property%EB%A5%BC-%EC%96%B4%EB%96%BB%EA%B2%8C-%EC%B0%BE%EC%9D%84%EA%B9%8C/