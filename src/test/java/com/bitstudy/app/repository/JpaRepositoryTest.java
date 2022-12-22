package com.bitstudy.app.repository;

import com.bitstudy.app.config.jpaConfig;
import com.bitstudy.app.domain.Article;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
/** 슬라이드 테스트란 지난번 TDD 때 각 메서드들 다 남남으로 서로를 알아보지 못하게 만들었었다.
 * 이것처럼 메서드들 각각 테스트한 결과를 서로 못보게 잘라서 만드는것 *
 * */
@Import(jpaConfig.class)
/** 원래대로라면 JPA 에서 모든 정보를 컨트롤 해야되는데 JpaConfig 의 경우는 읽어오지 못한다.
 * 이유는 이건 시스템에서 만든게 아니라 우리가 별도로 만든 파일이기 때문. 그래서 따로 import를 해줘야 한다.
 안하면 config 안에 명시해놨던 JpaAuditing 기능이 동작하지 않는다.
 * */

class JpaRepositoryTest {
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    /*  */

    /* 생성자 만들기  - 여기서는 다른 파일에서 매개변수로 보내주는걸 받는거라서 위에랑 상관없이 @Autowired를 붙여야함 */
    public JpaRepositoryTest(@Autowired ArticleRepository articleRepository,
                             @Autowired ArticleCommentRepository articleCommentRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
    }

    /* 트랜잭션 시 사용하는 메서드
    * 사용법: repository명.메서드()
    * 1) findAll() - 모든 컬럼을 조회할 떄 사용, 페이징(pageable) 가능
    *                   당연히 select 작업을 하지만, 잠깐 사이에 해당 테이블에 어떤 변화가 있었는지 알 수 없기 때문에 select전에
    *                   먼저 최신 데이터를 잡기 위해서 update를 한다.
    *                   동작 순서 : update -> select
    *
    * 2) findById() - 한 건에 대한 데이터 조회시 사용
    *                   primary key로 레코드 한건 조회.
    *                   () 안에 글번호를 넣어줘야 한다.
    *
    * 3) save() - 레코드 저장할때 사용 (insert,update)
    * 4) count() - 레코드 개수 뽑을때 사용
    * 5) delete() - 레코드 삭제
    * -------------------------------------------------
    *
    * 테스트용 데이터 기져오기
    *  참고  https://www.mockaroo.com/
    *  */

    /* Select 테스트 */
    @DisplayName("셀렉트 테스트") // RUN 에 이름 달아주기
    @Test
    void selectTest(){
        /* 셀렉팅 할거니까 articleRepository 를 기준으로 테스트 할거임.
            maven방식: dao -> mapper 로 정보 보내고 DB 갔다 와서 C 까지 돌려보낼건데 dao에서 DTO를 list에 담아서 return
        *   */
//        List<Article> articles =  articleRepository.findAll();
//        /* assertJ 를 이용해서 테스트 할거임
//        *    articles 가 NotNull 이고 사이즈가 ?? 개면 통과 */
//        assertThat(articles).isNotNull().hasSize(100);

        List<Article> articles = articleRepository.findAll();
//        assertThat(articles.size() == 100);
        assertThat(articles).isNotNull().hasSize(100);
    }


    /* insert 테스트 */
    @Test
    @DisplayName("인설트 테스트")
    void insertTest(){
        /* 기존의 article 개수를 센 다음에, insert 하고, 기존꺼보다 현재꺼 1차이가 나면 성공 */
//        long prevCount = articleRepository.count();
//
//        // insert 하기
//        Article article = Article.of("제목","내용","#해시태그");
//        articleRepository.save(article);
//
//        /* !! 주의 이 상태로 테스트를 돌라면 createAt 이거 못 찾는다고 에러남
//        *   jpaConfig 파일에 auditing 을 쓰겠다고 세팅을 해놨는데,
//        *   해당 엔티티(Article.java) 에서 auditing 쓴다고 명시를 안해놓은 상태라서,
//        *   엔티티 가서 클래스별로 @EntityListeners(AuditingEntityListener.class) 걸어주자
//        * */
//        assertThat(articleRepository.count()).isEqualTo(prevCount + 1);

        long prevArticleCount = articleRepository.count();
        Article article = Article.of("제목","내용","해시태그");
        articleRepository.save(article);
        assertThat(articleRepository.count()).isNotNull().isEqualTo(prevArticleCount + 1);
    }

    @Test
    @DisplayName("업데이트 테스트")
    void updateTest() {
        /* 기존의 데이터 하나 있어야 되고, 그걸 수정 했을때 관찰할거임.
        *  1) 기존 영속성 컨텍스트로부터 하나 엔티티를 객체를 가져온다.(DB에서 한줄 뽑아온다.)
        *  2) 업데이트로 해시태그를 바꾸기
        * */

        /* 순서 - 1) 기존 영속성 컨텍스트로부터 한줄 가져온다.
        * articleRepository - 기존의 영속성 컨텍스트로부터
        * findById(1L) -> 하나 엔티티 객체를 가져온다.
        * .orElseThrow() -> 없으면 throw 시켜서 일단 테스트가 끝나게 하자.
        * */
//        Article article = articleRepository.findById(1L).orElseThrow();

        /* 순서 - 2) 업데이트로 해시태그를 바꾸기.
            엔티티에 있는 setter를 이용해서 updateHashtag 에 있는 문자열로 업데이트 하기
            1. 변수 updateHashtag 에 바꿀 문자열 저장
            2. 엔티티(article) 에 있는 setter를 이용해서 변수 updateHashtag 에 있는 문자열을 넣고
            (해시태그 바꿀꺼니까 setHashtag. 이름 어찌할지 모르겠으면 실제 엔티티 파일 가서 setter 만들어보기
            그 이름 그대로 쓰면 됨)
            3. 데이터 베이스에 업데이트 하기.
         */
//        article.setHashtag("해시태그 바꿈");
//        Article savedArticle = articleRepository.saveAndFlush(article);

        /* save 로 놓고 테스트를 돌리면 콘솔(RUN) 탭에 update 구문이 나오지 않고 select
        * 구문만 나온다. 이유는 영속성 컨텍스트로부터 가져온 데이터를 그냥 save만 하고 아무것도
        * 하지 않고 끝내버리면 어짜피 롤백 되니까 스프링부트는 다시 원래 값으로 돌아가질거다.
        * 그래서 그냥 했다 치고 update를 하지 않는다.(코드의 유효성은 확인)
        * 그래서 save 를 하고 flush를 해줘야 한다.
        *   flush 란 (push 같은거)
        *   1. 변경점 감지
        *   2. 수정된 Entity 를 sql 저장소에 등록
        *   3. sql 저장소에 있는 쿼리를 DB에 전송 */

        /* 순서3) 위에서 바꾼 savedArticle 에 업데이트 된 hashtag 필드에
        *   updatehashtag 에 저장되어 있는 값("abcd")이 있는지 확인해봐라  */
//        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag","해시태그 바꿈");
        String hashText = "#해시태그";
        Article prevarticle = articleRepository.findById(1L).orElseThrow();
        prevarticle.setHashtag(hashText);
        articleRepository.saveAndFlush(prevarticle);

        assertThat(articleRepository.findById(1L).orElseThrow().getHashtag()).isEqualTo(hashText);
    }

    @DisplayName("delete 테스트")
    @Test
    void deleteTest(){
        /* 기존의 데이터들이 있다고 치고, 그중에 값을 하나 꺼내고, 지워야 한다.
        *   1) 기존에 개수를 센다.
        *   2) 하나 뺀다.
        *   3) 그리고 개수가 맞나 확인한다.
        * */
//------------------1번 방법-------------------
//        long prevArticleCount = articleRepository.count();
//        long prevArticleCommentCount = articleCommentRepository.count();
//
////         delete 하기 (전체 게시글 수 -1 됨)
//
//        Article article = articleRepository.findById(1L).orElseThrow();
//        long comment = article.getArticleComments().size();
//        articleRepository.delete(article);
//        assertThat(articleRepository.count()).isEqualTo(prevArticleCount - 1);
// ---------------------------2번 방법---------------------
        long prevArticleCount = articleRepository.count();
        long prevArticleComment = articleCommentRepository.count();
        Article article = articleRepository.findById(1L).orElseThrow();
        articleRepository.delete(article);
        long nextArticleCount = articleRepository.count();
        long nextArticleCommentCount = articleRepository.count();
        long articleTotal = prevArticleCount - nextArticleCount;
        long commentTotal = prevArticleComment - nextArticleCommentCount;
        assertThat(prevArticleCount).isEqualTo(nextArticleCount + articleTotal);
        assertThat(prevArticleComment).isEqualTo(nextArticleCommentCount + commentTotal);
//-------------------------3번 방법---------------------------


    }

}