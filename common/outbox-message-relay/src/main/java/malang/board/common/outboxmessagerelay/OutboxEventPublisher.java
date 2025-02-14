package malang.board.common.outboxmessagerelay;

import lombok.RequiredArgsConstructor;
import malang.board.common.event.Event;
import malang.board.common.event.EventPayload;
import malang.board.common.event.EventType;
import malang.board.common.snowflake.Snowflake;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final Snowflake outboxIdSnowFlake = new Snowflake();
    private final Snowflake eventIdSnowFlake = new Snowflake();
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(EventType type, EventPayload payload, Long shardKey) {
        Outbox outbox = Outbox.create(
                outboxIdSnowFlake.nextId(),
                type,
                Event.of(
                        eventIdSnowFlake.nextId(), type, payload
                ).toJson(),
                shardKey % MessageRelayConstants.SHARD_COUNT
        );
        applicationEventPublisher.publishEvent(OutboxEvent.of(outbox));
    }

}
