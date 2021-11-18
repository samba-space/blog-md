# jackson 사용 시, 메서드명 주의하기
프로젝트를 진행하면서, 직렬화/역직렬화 시 jackson 라이브러리를 사용하고 있다. 직렬화 시에 메서드가 json 필드로 들어가는 경우가 있어서, jackson에 대해 공부해보고, 해결했던 과정을 기록하려한다.

## 원인
기존에 특정 정보를 redis에 저장, 조회하여 사용하고 있었다. 그런데 해당 객체에 `isNotAdmin`메서드를 추가하니 redis에서 해당 정보를 조회 시에 `UnrecognizedPropertyException`가 발생했다. 
```java
public class TestDto {

    private List<RoleType> roles;

    public List<RoleType> getRoles() {
        return roles;
    }

    public boolean isNotAdmin() {
        return roles.stream().noneMatch(RoleType.ADMIN::equals);
    }
}
```
원인을 찾아보니 역직렬화(deserialize)될 때, `notAdmin`이라는 필드가 없어서 그랬던 것이다. `TestDto`가 redis에 저장될 때, 즉 직렬화(serialize)될 때, `isNotAdmin` 메서드가 `notAdmin`이라는 필드로 들어갔던 것이다. 
왜 `isNotAdmin` 메서드가 필드로 인식되어 들어갔을까? 이를 알기 위해 jackson이 어떤 필드를 직렬화/역직렬화 대상으로 결정하는지 알아볼 필요가 있었다. 우선 jackson에 대해 알아보자.


## jackson이란?
Jackson 은 자바용 json 라이브러리로 잘 알려져 있지만 Json 뿐만 아니라 XML/YAML/CSV 등 다양한 형식의 데이타를 지원하는 data-processing 툴이다.

스트림 방식이므로 속도가 빠르며 유연하며 다양한 third party 데이터 타입을 지원하며 annotation 방식으로 메타 데이타를 기술할 수 있으므로 JSON 의 약점중 하나인 문서화와 데이터 validation 문제를 해결할 수 있다.

## jackson은 어떤 필드를 직렬화/역직렬화 대상으로 결정할까?
가장 간단하게 public 필드는 직렬화와 역직렬화 모두 가능하다. public 하지 않은 필드의 경우 어떻게 될까?

### getter 메서드는 non-public 필드를 직렬화/역직렬화 가능하게 만든다.
getter 메서드가 있으면 non-public 필드를 직렬화할 수 있다. 또한, getter는 private 필드도 역직렬화할 수 있도록 해준다. 왜냐하면, **getter가 있으면 해당 필드는 property로 간주**되기 때문이다.  

### setter 메서드는 non-public 필드를 역직렬화 가능하게 만든다.
setter 메서드는 non-public 필드를 역직렬화만 가능하게 만든다.

## 해결
원인은 getter가 있으면 해당 필드는 property로 간주되는 것 때문이였다. `isNotAdmin` 메서드가 boolean naming convention에 따라 getter로 인식되어 `notAdmin`이 property로 간주되었던 것이다.  
`notAdmin`은 직렬화되어 redis에 저장되었고, 조회 시(역직렬화)에 `TestDto`에는 notAdmin이라는 필드가 없어서 `UnrecognizedPropertyException`가 발생한 것이다.  

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
    TestDto test = new TestDto(List.of(RoleType.ADMIN));

    //when
    String result = mapper.writeValueAsString(test);

    //then
    assertThat(result, not(containsString("notAdmin")));
}
```

`notAdmin`이라는 property가 생성되지 않고, 테스트가 잘 통과된다.


## 참조
- https://www.lesstif.com/java/java-json-library-jackson-24445183.html
- https://www.baeldung.com/jackson-field-serializable-deserializable-or-not



https://bactoria.github.io/2019/08/16/ObjectMapper%EB%8A%94-Property%EB%A5%BC-%EC%96%B4%EB%96%BB%EA%B2%8C-%EC%B0%BE%EC%9D%84%EA%B9%8C/