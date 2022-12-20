package com.bitstudy.app.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/* 할일: Lombok 사용하기
   주의: maven 때랑 같은 방식인 것들도 이름이 다르게 되어 있으니 헷갈리지 않게 주의!

   순서:
   1) 롬복을 이용해서 클래스를 엔티티로 변경
   2) getter/setter, toString 등의 롬복 어노테이션 사용
   3) 동등성, 동일성 비교할 수 있는 코드 넣어볼거임
* */

/*
    @Table - 엔티티와 매핑할 정보를 지정하고,
                사용법) @Index(name="원하는 명칭",columnList = "원하는 테이블명")
                    name부분을 생략하면 원래 이름 사용한다.
     @Index - 데이터베이스 인덱스는 추가, 쓰기및 저장 공간을 희생해서 테이블에 대한 데어터 검색속도를 향상시키는 데이터 구조
                @Entity와 세트로 사용
* */
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy"),
})
@Getter // 이걸 사용하면 Getter를 다만들어줌
//@Setter // 이걸 사용하면 Setter를 다만들어줌
@ToString // 이걸 사용하면 TsoString 만들어줌
@Entity // 롬복을 이용해서 클래스를 앤티티로 변경 @Entity가 붙은 클래스는 JPA가 관리하게 된다. 그래서 기본키가 뭔지 알려줘야 한다. 그게 @ID
public class Article {
    @Id // 전체 필드 중에서 이게 PK이다 라고 말해주는 구문. @ID가 없으면 Entity가 에러 난다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 해당 필드가 auto_increment 인 경우 GenerationType 써준다 **기본키 전략
    private Long id;


    /* @Setter도 Getter 처럼 클래스 단위로 걸 수 있는데, 그렇게 하면 모든 필드에 접근이 가능해진다.
        그런데 id 같은 경우에는 내가 부여하는게 아니라 JPA에서 자동으로 부여해주는 번호이다. 메타데이터들도 자동으로 JPA가
        셋팅하게 만들어야 한다. 그래서 id와 에타데이터는 @Setter 가 필요 없다 @Setter 의 경우에는 지금처럼 필요한 필드에만
        주는 것을 연습해야 한다. (회사마다 다름)

        @Colum - 해당 컬럼이 not null 인 경우 @Column(nullable=false) 써준다.
        기본값은 true 라서 @Column 을 아예 안쓰면 null 가능
        @Column(nullable=false,length="숫자") 숫자 안쓰면 기본값 255 먹는다.
    * */
    @Setter @Column(nullable=false) private String title; // 제목
    @Setter @Column(nullable=false, length=10000) private String content; // 본문
    @Setter private String hashtag; // 해시태그

    /* 양방향 바인딩
    *    */

    @OrderBy("id") // 양방향 바인딩을 할건데 정렬 기준을 id로 하겠다는 뜻
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    @ToString.Exclude // 이거 중요. 맨 위에 @ToString이 있는데 마우스 올려보면 @ToString includes~ lazy load 가 나온다
    // 이건 퍼포먼스 메모리 저하를 일으킬 수 있어서 성능적으로 안좋은 영향을 줄 수 있다. 그래서 해당 필드를 가려주세요 하는거
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();
    /* 이건 더 중요: ToString.Exclude 이걸 안해주면 순환참조 이슈가 생길 수 있다
    *  여기서 ToString이 id, title,content, hashtag 가 찍고 Set<ArticleComment> 부분을 찍으려고
    *  ArticleComment.java 파일에 가서 거기 있는 @ToString 이 원소들 다 찍으려고 하면서 원소들 중에
    *  private Article article; 보는 순간 다시 보면서 메모리가 터질 수 있다. 그래서  @ToString.Exclude 달아준다
    *
    * ArticleComment에 걸지 않고 Article에 걸어주는 이유는 댓글이 글을 참조하는건 정상적인 경우인데,
    * */




    /*  JPA auditing: jpa 에서 자동으로 세팅하게 해줄때 사용하는 기능
                        이거 하려면 config 파일이 별도로 있어야 한다.
                        config 패키지 만들어서 jpaConfig 클래스 만든다.
    * */
    //메타데이터
    @CreatedDate
    @Column(nullable=false)
    private LocalDateTime createdAt; // 생성일자

    @CreatedBy
    @Column(nullable=false, length=100)
    private String createdBy; // 생성자
    /* 다른 생성일시 같은것들은 알아낼 수 있는데, 최초 생성자는 (현재코드 상태) 인증받고 오지 않았기 때문에 알아낼 수가 없다.
    * 이때 아까 만든 jpaConfig 파일을 사용한다. */

    @LastModifiedDate
    @Column(nullable=false)
    private LocalDateTime modifiedAt; // 수정일자

    @LastModifiedBy
    @CreatedBy
    @Column(nullable=false, length=100)
    private String modifiedBy; // 수정자

    /* 위에 처럼 어노테이션을 붙여주기만 하면 auditing이 작동한다.
    * @CreatedDate : 최초에 insert 할때 자동으로 한번 넣어준다.
    * @CreatedBy : 최초에 insert 할때 자동으로 한번 넣어준다.
    * @LastModifiedDate : 작성당시에 시간을 실시간으로 넣어준다.
    * @LastModifiedBy : 작성당시에 작성자의 이름을 실시간으로 넣어준다.
    * */

    /* Entity 를 만들때는 기본 생성자가 무조건 필요하다.
       public 또는 protected만 가능한데, 평생 아무데서도 기본생성자를 안쓰이게 하고 싶어서 protected로 변경함
    *   */
    protected Article() {}

    /* 사용자가 입력하는 값만 받기. 나머지는 시스템이 알아서 하게 해주면 됨 */
    private Article(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public static Article of(Long id, String title, String content){
        return new Article(id,title,content);
    }
    /* 정적 팩토리 메서드 (factory, method, pattern 중에 하나)
    *  정적 팩토리 메서드란 객체 생성 역할을 하는 클래스 메서드 라는 뜻.
    * of 메서드를 이용해서 위에 있는 private 생성자를 직접적으로 사용해서 객체를 생성하는 방법.
    * 무조건 static 으로 놔야 함.
    *
    * 장점
    *   1) static 이기 때문에 new 를 이용해서 생성자를 만들지 않아도 된다.
    *   2) return 을 가지고 있기 때문에 상속시 값을 확인할 수 있다.
    *   3) (중요) 객체 생성을 캡슐화 할 수 있다. */

    /*
    * public : 제한 없음
    * protected : 동일한 패키지, 파생클래스 에서만 접근 가능
    * defult : 동일한 패키지
    * private : 자기 자신 클래스 내에서만 접근 가능
    * */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return id.equals(article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    /* == 와 equals 차이
    * equals : 동등성 비교, 값만 본다.
    * hashCode : 객체를 식별하는 Integer 값
    *            객체가 가지고 있는 데이터 특정 알고리즘을 적용해서 계산된 정수값을 hashcode 라고 함.
    *            사용하는 이유: 객체를 비교할 때 드는 비용이 낮다.
    *
    *      자바에서 2개의 객체를 비교할때는 equals를 사용하는데,
    *      여러 객체를 비교할 떄는 equals 를 사용하면 Integer를 비교하는데 많은 시간이 소요된다.*/

}
