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
@Entity // 테이블과 매핑 한다는 뜻. JPA가 관리한다.
@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy"),
})
public class ArticleComment extends AuditingFields{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter @ManyToOne(optional = false) private Article article;

    /* 연관관계 매핑
        연관관계 없이 만들면 private Long articleId; 이런식으로 (관계형 데이터
        데이스 스타일) 하면 된다.

    *   그런데 우리는 Article과  AricleCommnet 가 관계를 맺고 있는걸 객체
        지향적으로 표현하려고 이렇게 쓸거다.
        그러기 위해서 필요한건 단방향이라는 뜻의 @ManyToOne 에너테이션을 써주고,
        이건 필수값이다 라는 뜻으로 (optional = false)
        '댓글은 여러개: 게시글 1개' 이기 때문에 단방향 방식을 쓴다.
     */
    @Setter @Column(nullable=false, length=500)
    private String content; // 본문

    //메타데이터
//    @CreatedDate
//    @Column(nullable=false)
//    private LocalDateTime createdAt; // 생성일자
//
//    @CreatedBy
//    @Column(nullable=false, length=100)
//    private String createdBy; // 생성자
//    /* 다른 생성일시 같은것들은 알아낼 수 있는데, 최초 생성자는 (현재코드 상태) 인증받고 오지 않았기 때문에 알아낼 수가 없다.
//     * 이때 아까 만든 jpaConfig 파일을 사용한다. */
//
//    @LastModifiedDate
//    @Column(nullable=false)
//    private LocalDateTime modifiedAt; // 수정일자
//
//    @LastModifiedBy
//    @CreatedBy
//    @Column(nullable=false, length=100)
//    private String modifiedBy; // 수정자
}
