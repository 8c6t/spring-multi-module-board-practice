package malang.board.articleread.service.event.handler;

import malang.board.common.event.Event;
import malang.board.common.event.EventPayload;

public interface EventHandler<T extends EventPayload> {

    void handle(Event<T> event);

    boolean supports(Event<T> event);

}
