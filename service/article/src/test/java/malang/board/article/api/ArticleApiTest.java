package malang.board.article.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import malang.board.article.service.response.ArticleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class ArticleApiTest {

    RestClient restClient = RestClient.create("http://localhost:9000");

    @Test
    void createTest() {
        ArticleResponse response = create(new ArticleCreateRequest(
                "hi", "my content", 1L, 1L
        ));
        System.out.println("response = " + response);
    }

    ArticleResponse create(ArticleCreateRequest request) {
        return restClient.post()
                .uri("/v1/articles")
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void readTest() {
        ArticleResponse response = read(147361960888610816L);
        System.out.println("response = " + response);
    }

    ArticleResponse read(Long articleId) {
        return restClient.get()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void updateTest() {
        update(147361960888610816L);
        ArticleResponse response = read(147361960888610816L);
        System.out.println("response = " + response);
    }

    void update(Long articleId) {
        restClient.put()
            .uri("/v1/articles/{articleId}", articleId)
            .body(new ArticleUpdateRequest("hi 2", "my content 2"))
            .retrieve();
    }

    @Test
    void deleteTest() {
        delete(147361960888610816L);
    }

    void delete(Long articleId) {
        restClient.delete()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve();
    }

    @Getter
    @AllArgsConstructor
    static class ArticleCreateRequest {
        private String title;
        private String content;
        private Long writerId;
        private Long boardId;
    }

    @Getter
    @AllArgsConstructor
    static class ArticleUpdateRequest {
        private String title;
        private String content;
    }

}
