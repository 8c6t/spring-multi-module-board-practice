package malang.board.hotarticle.service.eventhandler;

import lombok.RequiredArgsConstructor;
import malang.board.common.event.Event;
import malang.board.common.event.EventType;
import malang.board.common.event.payload.ArticleLikedEventPayload;
import malang.board.hotarticle.repository.ArticleLikeCountRepository;
import malang.board.hotarticle.utils.TimeCalculatorUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleLikedEventHandler implements EventHandler<ArticleLikedEventPayload> {

    private final ArticleLikeCountRepository articleLikeCountRepository;

    @Override
    public void handle(Event<ArticleLikedEventPayload> event) {
        ArticleLikedEventPayload payload = event.getPayload();
        articleLikeCountRepository.createOrUpdate(
                payload.getArticleId(),
                payload.getArticleLikeCount(),
                TimeCalculatorUtils.calculateDurationToMidnight()
        );
    }

    @Override
    public boolean supports(Event<ArticleLikedEventPayload> event) {
        return EventType.ARTICLE_LIKED == event.getType();
    }

    @Override
    public Long findArticleId(Event<ArticleLikedEventPayload> event) {
        return event.getPayload().getArticleId();
    }

}
