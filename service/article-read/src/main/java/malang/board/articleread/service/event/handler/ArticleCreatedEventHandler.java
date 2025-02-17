package malang.board.articleread.service.event.handler;

import lombok.RequiredArgsConstructor;
import malang.board.articleread.repository.ArticleIdListRepository;
import malang.board.articleread.repository.ArticleQueryModel;
import malang.board.articleread.repository.ArticleQueryModelRepository;
import malang.board.articleread.repository.BoardArticleCountRepository;
import malang.board.common.event.Event;
import malang.board.common.event.EventType;
import malang.board.common.event.payload.ArticleCreatedEventPayload;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ArticleCreatedEventHandler implements EventHandler<ArticleCreatedEventPayload> {

    private final ArticleIdListRepository articleIdListRepository;
    private final ArticleQueryModelRepository articleQueryModelRepository;
    private final BoardArticleCountRepository boardArticleCountRepository;

    @Override
    public void handle(Event<ArticleCreatedEventPayload> event) {
        ArticleCreatedEventPayload payload = event.getPayload();
        articleQueryModelRepository.create(
                ArticleQueryModel.create(payload),
                Duration.ofDays(1L)
        );

        articleIdListRepository.add(payload.getBoardId(), payload.getArticleId(), 1000L);
        boardArticleCountRepository.createOrUpdate(payload.getBoardId(), payload.getBoardArticleCount());
    }

    @Override
    public boolean supports(Event<ArticleCreatedEventPayload> event) {
        return EventType.ARTICLE_CREATED == event.getType();
    }

}
