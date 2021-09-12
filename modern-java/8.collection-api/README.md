# 개선된 컬렉션 API
자바 8, 9에 추가된 새로운 컬렉션 API의 대해 알아보자.

## 기존 Arrays.asList
기존 Arrays.asList 팩토리 메서드(JAVA 1.2)를 이용하여, 적은 요소를 가진 리스트를 만들수 있었다.  
Arrays.asList는 고정 크기의 리스트를 만든다. 요소를 갱신할 수 있지만, 요소를 추가하거나 삭제할 순 없다.  
요소를 추가하거나 제거하려고 하면, UnsupportedOperationException이 발생한다. 내부적으로 고정된 크기의 배열로 구현되었기 때문이다.  

Arrays에는 asSet같은 메서드가 없으므로 Set이나 Map은 만들 수 없다.  
아래처럼 리스트를 인수로 받는 HashSet 생성자를 통해서 만들수 있지만, 이는 내부적으로 불필요한 객체 할당을 필요로 한다. 그리고 Set이 변환이 가능해진다.

```java
Set<String> friends = new HashSet<>(Arrays.asList("park","kim","song"));
```

## 컬렉션 팩토리
자바 9에서는 작은 List, Set, Map을 맵을 쉽게 만들 수 있는 팩토리 메서드를 제공한다. 하나씩 알아보자.

### List 팩토리
List.of 팩토리 메서드를 이용해서 간단하게 리스트를 만들 수 있다.  변경할 수 없는 리스트가 만들어지기 때문에  
요소를 추가, 삭제하거나 Arrays.asList와 다르게 요소를 바꾸는 set메서드도 UnsupportedOperationException이 발생한다.  
이런 제약의 장점은 컬렉션이 의도치 않게 변하는 것을 막을 수 있는 것이다. 리스트를 바꿔야 하는 상황이라면 리스트를 만들면 된다.
또, null 요소를 금지하여 의도치 않은 버그를 방지한다.

```java
List<String> friends = List.of("park", "kim", "song");
```

한편 리스트를 만들 때, 스트림 API(toList)와 컬렉션 팩토리 둘 중에 어떤 걸 사용하는게 좋을가?  
데이터 처리 형식을 설정, 데이터를 변환할 필요가 있다면 스트림 API를 사용하고, 그렇지 않을 경우  
간편한 팩토리 메서드를 사용할 것을 권장한다. 팩토리 메서드 구현이 더 단순하고 목적을 달성하는데 충분하기 때문이다.

### Set 팩토리
Set.of 팩토리 메서드를 이용해서 집합을 만들 수 잇다. 중복된 요소를 넣으면 IllegalArgumentException이 발생한다.

```java
Set<String> friends = Set.of("park", "kim", "song");
```

### Map 팩토리
Map.of와 Map.ofEntries 팩토리 메서드로 바꿀수 없는 맵을 만들 수 있다.  

Map.of는 키와 값 쌍을 인수로 받아 맵을 생성하며, 10개 이하의 키와 값 쌍을 가진 작은 맵을 만들 때 유용하다.  

```java
Map<String, Integer> ageOfFriends = Map.of("Park", 30, "Kim", 28, "Song", 31);
```

Map.ofEntries는 10개 보다 많을 때 유용하며, 인수로 Map.Entry\<K, V> 객체를 가변 인수로 받는다.  
Map.entry 팩토리 메서드는 Map.Entry 객체를 만드는 새로운 메서드이다.

```java
Map<String, Integer> ageOfFreinds = Map.ofEntries(Map.entry("Park", 30), 
                                                  Map.entry("Kim", 28),
                                                  Map.entry("Song", 31));
```

## List, Set 처리
기존 컬렉션을 바꾸는 동작은 에러를 유발하고 복잡하다.  
자바 8에선 removeIf, replaceAll, sort를 추가하여 문제 이러한 문제를 보안했다.

### removeIf
프레디케이트를 만족하는 요소를 제거한다. List, Set을 구현하거나 그 구현을 상속받은 모든 클래스에서 이용할 수 있다.  
removeIf를 사용하면 코드가 단순해지고 버그도 예방할 수 있다.  

```java
for(Tranaction transaction : transactions){
    if(Character.isDigit(transaction.getReferenceCode().charAt(0)))}{
        transactions.remove(tranaction);
    }
}
```

숫자로 시작되는 참조 코드를 가진 트랙잭션을 삭제하는 코드이다. 위의 코드는 ConcurrentModificationException이 발생된다.  
왜냐하면 내부적으로 for-each는 Iterator 객체를 사용하는데, 위의 코드는 반복자의 상태와 컬렉션의 상태가 동기화되지 않는다.  
명시적으로 Iterator 객체의 remove를 호출하여 해결할 수 있지만, 코드가 복잡해진다. 이 코드 패턴은 removeIf 메서드로 바꿀 수 있다.

### replaceAll
List 인터페이스의 replaceAll 메서드를 이용해 리스트의 각 요소를 새로운 요소로 바꿀 수 있다. 인수인 UnaryOperator 함수를 이용하여 요소를 바꾼다.  
스트림 API를 이용해도 되지만, 스트림 API는 새 컬렉션을 만든다. replaceAll은 기존의 컬렉션을 바꿀 수 있다는 차이가 있다.  
Iterator의 set 메서드를 이용할 수 있지만, 컬렉션 객체와 Iterator 객체를 혼용하면 반복과 컬렉션 변경이 동시에 이루어지면서 쉽게 문제를 일으킨다.  

다음의 코드처럼 replaceAll을 이용하여 간단하게 구현할 수 있다.

```java
referenceCodes.replaceAll(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1));
```

## 맵 처리
자바 8에서는 Map 인터페이스에 몇 가지 디폴트 메서드를 추가했다. 자주 사용되는 패턴을 직접 구현할 필요하지 않아도 되도록 추가되었다.

### forEach
기존의 맵의 키, 값을 반복하면서 확인하는 작업은 귀찮은 작업 중 하나다.
자바 8에서는 forEach 메서드를 제공한다. BiConsumer를 인수(키, 값)로 받으며, forEach를 통해 좀 더 간단하게 구현할 수 있게 되었다.  

기존의 Map.Entry\<K, V>의 반복자를 이용한 것과 비교해보면, 훨씬 간단하게 구현되는 것을 볼 수 있다.
``` java
//기존
Map<String, Integer> ageOfFriends = Map.of("Park", 30, "Kim", 28, "Song", 31);
for (Map.Entry<String, Integer> entry : ageOfFriends.entrySet()) {
    String friend = entry.getKey();
    Integer age = entry.getValue();
    System.out.printf("name = %s / age = %s\n", friend, age);
}

//forEach를 이용
ageOfFriends.forEach((friend, age) -> System.out.printf("name = %s / age = %s\n", friend, age));
```

### 정렬 - Entry.comparingByValue, Entry.comparingByKey
새로 추가된 comparingByValue, comparingByKey 유틸리티 메서드를 이용해서 맵을 정렬할 수 있다.  

``` java
Map<String, Integer> ageOfFriends = Map.of("Park", 30, "Kim", 28, "Song", 31);
ageOfFriends.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .forEachOrdered(System.out::println);
// 출력
// Kim=28
// Park=30
// Song=31
```

## getOrDefault
기존에는 찾으려는 key가 존재하지 않으면 null이 반환되어서 에러를 방지하기 위해 null 체크를 해야 했다.  
이 문제는 getOrDefault메서드를 이용해서 쉽게 해결할 수 있다. getOrDefault 메서드는 맵에 키가 존재하지 않으면 default 값을 반환한다.  

아래 예는 Moon이라는 키가 없으므로 100이 출력된다. 만약 키가 존재하고 값이 null 일 경우 null이 리턴되는 것을 주의하자.

``` java
Map<String, Integer> ageOfFriends = Map.of("Park", 30, "Kim", 28, "Song", 31);

Integer ageOfMoon = ageOfFriends.getOrDefault("Moon", 100);
System.out.println("Moon = " + ageOfPark);
// 출력 : Moon = 100
```

## 계산 패턴
맵에 키가 존재하는지 여부에 따라 어떤 동작을 실행하고 결과를 저장해야 하는 상황이 있다.  
다음 3가지 연산이 이런 상황에 도움을 준다.
- computeIfAbsent : 제공된 키가 없거나 키에 해당하는 값이 null이면, 키를 이용해 새 값을 계산하고 맵에 추가한다.(키가 존재하면 기존 값을 반환(get과 같음))
- computeIfPresent : 제공된 키가 존재하고, 값이 null 아니면, 새 값을 계산하고 맵에 추가한다.  
- compute : 제공된 키로 새 값을 계산하고 맵에 저장한다.  

Map\<K, List\<V>>과 같이 여러 값을 저장하는 맵을 처리할 때 유용하게 사용할 수 있다.  
아래 예는 부서별 멤버 리스트를 가진 맵을 가지고 testing team에 park을 추가한다. 매우 귀찮은 작업이다.

```java
Map<String, List<String>> memberNameByDept = new HashMap<>();
String deptName = "testing team";
List<String> members = memberNameByDept.get(deptName);
if (members == null) {
    members = new ArrayList<>();
    memberNameByDept.put(deptName, members);
}
members.add("Park");
```

이 작업을 computeIfAbsent를 바꾸면, 매우 간결해진다.

```java
memberNameByDept.computeIfAbsent("develop team", name -> new ArrayList<>())
                .add("Park");
```

## 삭제 패턴
자바 8에서는 키와 값 모두 일치하면 항목을 제거하는 오버로딩된 remove 메서드를 제공한다.  

기존의 코드는 아래와 같이 key 존재여부와 key에 매핑된 값이 내가 보낸 값이랑 일치하는지까지 구현해야한다.  

```java
if (map.containsKey(key) && Objects.equals(map.get(key), value)) {
     map.put(key, newValue);
     return true;
 } else
     return false;
```
오버로딩된 remove로 구현하면 매우 간단하게 구현할 수 있다.

```java
map.remove(key, value);
```

## 교체 패턴
맵의 항목을 바꾸는 데 사용할 수 있는 2개의 메서드가 추가되었다.
- replaceAll : BiFunction을 적용한 결과로 각 항목의 값을 교체한다.
- replace : 키가 존재하면 맵의 값을 바꾼다.
    - replace(K, V) : Key가 존재하면 V로 값을 바꾼다.
    - replace(K, oldV, newV) Key, Value 모두 일치하면 newV로 바꾼다.

replaceAll을 이용하여, 부서의 리더이름을 모두 대문자로 바꿔보자.

``` java
Map<String, String> leaderByDept = new HashMap<>();
leaderByDept.put("testing team", "Song");
leaderByDept.put("develop team", "Park");

leaderByDept.replaceAll((dept, leader) -> leader.toUpperCase());
System.out.println("leaderByDept = " + leaderByDept);
// 출력 : leaderByDept = {testing team=SONG, develop team=PARK}
```

## 합침
새로운 merge 메서드를 이용하여 두 개의 맵을 합치거나 바꿀수 있다.  
중복된 키의 값을 어떻게 합칠지 결정하는 BiFunction을 인수로 받는다.  

기존의 putAll을 이용하여 두개의 맵을 합치면 중복된 키가 없다면 잘 동작할 것이다.(중복되면 한개만 저장됨)

``` java
Map<String, String> family = Map.ofEntries(Map.entry("Park", "1111"), Map.entry("Kim", "2222"));
Map<String, String> friends = Map.ofEntries(Map.entry("Song", "3333"));
Map<String, String> everyone = new HashMap<>(family);
everyone.putAll(friends);
```

forEach와 merge를 이용하여, 키가 중복되면 연락처를 &로 붙여보자.

```java
Map<String, String> family = Map.ofEntries(Map.entry("Park", "1111"), Map.entry("Kim", "2222"));
Map<String, String> friends = Map.ofEntries(Map.entry("Park", "4444"), Map.entry("Song", "3333"));
Map<String, String> everyone = new HashMap<>(family);
friends.forEach((k, v) ->
        everyone.merge(k, v, (phone1, phone2) -> phone1 + " & " + phone2));
System.out.println("everyone = " + everyone);
// 출력 : everyone = {Song=3333, Kim=2222, Park=1111 & 4444}
```

merge는 null값과 관련된 복잡한 상황도 처리한다.  
merge는 전달된 key와 맵에 일치하는 key가 없거나 값이 null이면, 전달된 value로 key에 매핑한다.  
전달된 key와 일치하는 key가 있고, 값이 null 아니면 기존 값과 전달된 value를 재매핑 함수에 적용한 결과로 값을 바꾼다.  
만약 그 결과가 null이면 항목을 제거한다.(remove(key))


## 개선된 ConcurrentHashMap
ConcurrentHashMap은 동시성 친화적이며 최신 기술을 반영한 HashMap 버전이다.  
특정 부분만 잠궈 동시 추가, 수정을 허용하여, 동기화된 HashTable에 비해 읽기 쓰기 연산 성능이 월등하다.  
HashMap은 동기화되지 않아 멀티쓰레드 환경에 적합하지 않다.

### 리듀스와 검색 - forEach, reduce, search
forEach는 각 키, 값 쌍에 주어진 함수를 실행한다.  
reduce는 모든 키, 값 쌍을 제공된 리듀스 함수를 이용해 결과로 합친다.  
searchr는 null이 아닌 값을 반환할 때까지 각 키, 값 쌍에 함수를 적용한다.  

각 메서드 아래처럼 4가지 연산 형태를 지원한다. 
- forEach, reduce, search - 키, 값으로 연산
- forEachKey, reduceKeys, searchKeys - 키로 연산
- forEachValue, reduceValues, searchValues - 값으로 연산
- forEachEntry, reduceEntries, searchEntries - Map.Entry 객체로 연산

이들 연산은 상태를 잠그지 않고 연산을 수행하므로, 연산에 제공할 함수는 계산이 진행되는 동안 바뀔 수 있는 객체, 값, 순서 등에 의존하지 않아야 한다. 이 연산들은 병렬성 기준 값을 지정해야 한다. 병렬성 기준 값 보다 맵의 크기가 작으면 순차적으로 연산이 실행된다.

reduceValues 메서드를 이용해 맵의 최대값을 찾는 예이다.

```java
ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();
long parallelismThreshold = 1;
Optional<Long> maxValue = Optional.ofNullable(map.reduceValues(parallelismThreshold, Long::max));
```

병렬성 기준값이 1이면 공통 스레드 풀을 이용해 병렬성을 극대화한다. Long.MAX_VALUE일 경우 단일 스레드로 연산을 실행한다.  
또, int, long, double 기본형 특화 메서드(reduceValuesToInt 등등)를 제공한다. 

### size보단 mappingCount
ConcurrentHashMap은 매핑 개수를 반환하는 mappingCount 메서드를 제공한다.  
mappingCount는 long 반환하므로 기존의 int를 반환하는 size 대신 사용하는 것이 좋다.  
왜냐하면 실제 매핑 개수가 int를 넘을 수 있기 때문이다.

### 집합뷰 - keySet, newKeySet
ConcurrentHashMap을 집합 뷰로 반환하는 keySet이라는 새 메서드를 제공한다.  
맵을 바꾸면 집합도 바뀌고 반대로 집합을 바꾸면 맵도 영향을 받는다.  
newKeySet이라는 새 메서드를 이용해 ConcurrentHashMap으로 유지되는 집합을 만들 수도 있다.