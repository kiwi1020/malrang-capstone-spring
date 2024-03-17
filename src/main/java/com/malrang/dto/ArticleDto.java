package com.malrang.dto;

import com.malrang.entity.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ArticleDto {
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class AddArticleRequest {
        private String title;

        private String content;

        public Article toEntity(String author) {
            return Article.builder()
                    .title(title)
                    .content(content)
                    .author(author)
                    .build();
        }
    }

    @Getter
    public static class ArticleListViewResponse {

        private final Long id;
        private final String title;
        private final String content;

        public ArticleListViewResponse(Article article) {
            this.id = article.getId();
            this.title = article.getTitle();
            this.content = article.getContent();
        }
    }

    @Getter
    public static class ArticleResponse {

        private final String title;
        private final String content;

        public ArticleResponse(Article article) {
            this.title = article.getTitle();
            this.content = article.getContent();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ArticleViewResponse {

        private Long id;
        private String title;
        private String content;
        private LocalDateTime createdAt;
        private String author;

        public ArticleViewResponse(Article article) {
            this.id = article.getId();
            this.title = article.getTitle();
            this.content = article.getContent();
            this.createdAt = article.getCreatedAt();
            this.author = article.getAuthor();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class UpdateArticleRequest {
        private String title;
        private String content;
    }
}
