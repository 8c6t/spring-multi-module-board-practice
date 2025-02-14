package malang.board.view.service;


import lombok.RequiredArgsConstructor;
import malang.board.common.event.EventType;
import malang.board.common.event.payload.ArticleViewedEventPayload;
import malang.board.common.outboxmessagerelay.OutboxEventPublisher;
import malang.board.view.entity.ArticleViewCount;
import malang.board.view.repository.ArticleViewCountBackupRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ArticleViewCountBackupProcessor {

    private final ArticleViewCountBackupRepository articleViewCountBackupRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional
    public void backup(Long articleId, Long viewCount) {
        int result = articleViewCountBackupRepository.updateViewCount(articleId, viewCount);
        if (result == 0) {
            articleViewCountBackupRepository.findById(articleId)
                    .ifPresentOrElse(ignored -> {
                            },
                            () -> articleViewCountBackupRepository.save(
                                    ArticleViewCount.init(articleId, viewCount)
                            )
                    );
        }

        outboxEventPublisher.publish(
                EventType.ARTICLE_VIEWED,
                ArticleViewedEventPayload.builder()
                        .articleId(articleId)
                        .articleViewCount(viewCount)
                        .build(),
                articleId
        );
    }

}
