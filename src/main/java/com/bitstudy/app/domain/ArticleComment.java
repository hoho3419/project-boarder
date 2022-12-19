package com.bitstudy.app.domain;

import java.time.LocalDateTime;

public class ArticleComment {

    private Long id;
    private Article article;
    /*
        
     */
    private String content; // 본문

    //메타데이터
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;

}
