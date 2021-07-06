# 연관관계 매핑

연관관계 매핑이란 객체의 연관관계와 테이블의 연관관계를 매핑하는 것을 말한다.  

아래처럼 객체와 테이블 사이에는 연관관계의 차이가 존재한다.

- 객체 - 참조를 사용해서 연관된 객체를 찾음
- 테이블 - 외래 키로 조인을 사용해서 연관된 테이블을 찾음

그러므로, 단순히 객체를 테이블에 맞추어 모델링하면(데이터 중심), 객체들의 협력 관계를 만들수 없다.  

## 단방향 연관관계

단방향 연관관계에서 ```데이터 중심 모델링```과 ```객체 지향 모델링```의 차이를 살펴보자.  

### 데이터 중심 모델링

![data-model](./images/data-model.png)

Team, Member는 연관관계가 없는 객체이다.  
객체 참조대신, 테이블의 FK를 객체에 그대로 사용했다.  

Member의 Team을 조회하는 코드입니다.

``` java
//Member 조회
Member findMember = em.find(Member.class, member.getId());

//Member의 Team조회
Long findTeamId = findMember.getTeamId();
Team findTeam = em.find(Team.class, findTeamId);
```

Member의 Team을 조회할 때, 식별자로 다시 조회해야 한다.  
즉, 객체 지향적인 방법이 아니다.

### 객체 지향 모델링

![object-model](./images/object-model.png)

객체 연관관계를 사용했다.(Team을 포함관계로)  
또, 객체 참조와 테이블의 외래키를 매핑하여, 객체와 테이블의 연관관계를 매핑한다.(ORM 매핑)  

Member Entity를 살펴보자.  

``` java
@Entity
public class Member {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;
```

```@ManyToOne```으로 Team과의 관계를 설정한다.  
```@JoinColumn```의 name 속성은 어떤 컬럼으로 Team table과 조인하는지를 지정한다.(객체의 참조와 테이블의 외래 키를 매핑한다.)  

Member의 Team을 조회하는 코드입니다.
``` java
//Member 조회
Member findMember = em.find(Member.class, member.getId());

//객체 참조를 통해서 연관관계 조회
Team findTeam = findMember.getTeam();
```
이전 데이터 중심의 모델링과 다르게 객체지향적으로 연관관계를 조회하는 것을 볼 수 있다.  

## 양방향 연관관계
Member에서 Team으로 접근 뿐만 아니라, Team에서도 Member를 접근하려면 양방향 연관관계를 추가해야 한다.  
양방향 연관관계에서 객체와 테이블의 연관관계를 맺는 차이로 인해 **양방향 매핑**이 필요하다.

### 객체와 테이블이 관계를 맺는 차이
#### 객체

![data-model](./images/object-bidirection.png)

객체의 양방향 관계는 단방향 관계 2개로 이뤄진다.  
즉, 객체를 양방향으로 참조하려면 단방향 연관관계를 2개 만들어야 한다.  

``` java
class Member {
    private Team team;
}

class Team {
    private Member member;
}
```

Member -> Team : member.getTeam()  
Team -> Member : team.getMember()

#### 테이블

![data-model](./images/table-fk.png)

테이블은 외래 키 하나로 두 테이블의 연관관계를 관리한다.  
즉, 외래 키 하나로 양방향 연관관계를 가질 수있다.  

아래의 SQL 문을 보면 양쪽으로 조인할 수 있는 것을 볼 수 있다.

``` sql
SELECT *
FROM MEMBER M
JOIN TEAM T
ON M.TEAM_ID = T.TEAM_ID

SELECT *
FROM TEAM T
JOIN MEMBER M
ON T.TEAM_ID = M.TEAM_ID
```

### 연관관계의 주인

위에서 객체와 테이블의 연관관계를 맺는 차이를 확인 할 수 있었다.  
이러한 차이에서 양방향 매핑을 하기위해 **연관관계의 주인**이 필요하다.(연관관계의 주인은 비지니스적으로 중요하다는 뜻이 아니다.)  
**연관관계의 주인은 table에서 외래키가 있는 곳으로 정하자.**(1:M에서 M인곳)  
또, 데이터의 등록, 수정은 **연관관계의 주인 쪽**에서 가능하고, 조회는 **주인이 아닌 쪽**에서 가능하다.

아래 클래스 다이어그램과 ERD를 통해 누구를 **연관관계의 주인**으로 정하는지 알아보자.

![data-model](./images/owner1.png)

Member.team 또는 Team.members 중 연관관계의 주인을 정해야한다.

![data-model](./images/owner2.png)

위에서 말했듯이 연관관계의 주인은 외래키가 Member table에 있으므로,  
**Member.team이 연관관계의 주인이 된다.**
Team.members를 연관관계의 주인으로 정할수 있으나,  
성능이슈도 있을 수 있고, team의 값을 바꿧는데 member에 update되어 비지니스적으로 명확하지 않다.

### mappedBy를 통한 양방향 매핑
연관관계의 주인은 변한게 없으며, **주인이 아닌 쪽에 mappedBy 속성으로 주인을 지정(필드명)**하여, 양방향 매핑을 한다.

``` java
@Entity
public class Member{
    ...
    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;
}

@Entity
public class Team{
    ...
    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<Member>();
}
```

### 양방향 연관관계 사용 시 주의점
- 연관관계의 주인에 값을 세팅하자.(주인이 아닌 곳은 읽기 전용)

- 순수 객체 상태를 고려해서 항상 양쪽에 값을 설정하자.  

``` java
Team team = new Team();
team.setName("TeamA");
em.persist(team);

Member member = new Member();
member.setName("member1");

team.getMembers().add(member);
member.setTeam(team);

em.persist(member);
```

- 연관관계 편의 메소드를 생성하자.  
하나만 호출해도 양쪽 값이 바뀌어 원자적으로 사용 할 수 있다.  
메소드명은 로직이 들어갔다는 것을 알 수 있게 setter를 쓰지 않는다.  
또, Team 쪽에 연관관계 메소드(team.addMember(...))를 생성해도 된다. 주의할 점은 둘 중 하나만 생성해야 한다.
``` java
@Entity
public class Member{
    ...
    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }
}
```

- 양방향 매핑 시에 무한 루프를 조심하자.
    - lombok의 toString 사용하면, member, team 무한루프에 빠지게 된다. 재정의해서 사용하자.
    - JSON 생성 라이브러리를 통해 양방향이 걸려있는 Entity를 JSON으로 변환 시, 무한루프에 빠지게 된다.  
    그러므로 controller에서 Entity를 직접 반환하지 말고, DTO를 사용하자. (뿐만 아니라 Entity 변경 시, API spec이 변경되는 문제도 있다.)

### 양방향 매핑 정리
- 설계 단계에서는 단방향 매핑만으로 객체와 table 매핑을 완료하자.  
실제 개발을 진행하며, 필요 시(JPQL 사용) 넣어주는 것이 좋다.(양방향 매핑 반대 방향 조회 기능이 추가된 것 뿐이다.)  
객체 입장에서도 양방향은 신경쓸 부분이 많아 이득이 크게 없다.