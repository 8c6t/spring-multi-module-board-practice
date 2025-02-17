package malang.board.articleread.cache;

import lombok.RequiredArgsConstructor;
import malang.board.common.dataserializer.DataSerializer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OptimizedCacheManager {

    private final StringRedisTemplate redisTemplate;
    private final OptimizedCacheLockProvider optimizedCacheLockProvider;

    private static final String DELIMITER = "::";

    public Object process(String type, long ttlSeconds, Object[] args, Class<?> returnType,
                          OptimizedCacheOriginDataSupplier<?> supplier) throws Throwable {
        String key = generateKey(type, args);
        String cachedData = redisTemplate.opsForValue().get(key);
        if (cachedData == null) {
            return refresh(supplier, key, ttlSeconds);
        }
        OptimizedCache optimizedCache = DataSerializer.deserialize(cachedData, OptimizedCache.class);
        if (optimizedCache == null) {
            return refresh(supplier, key, ttlSeconds);
        }

        if (!optimizedCache.isExpired()) {
            return optimizedCache.parseData(returnType);
        }

        if (!optimizedCacheLockProvider.lock(key)) {
            return optimizedCache.parseData(returnType);
        }

        try {
            return refresh(supplier, key, ttlSeconds);
        } finally {
            optimizedCacheLockProvider.unlock(key);
        }
    }

    private Object refresh(OptimizedCacheOriginDataSupplier<?> supplier, String key, long ttlSeconds) throws Throwable {
        Object result = supplier.get();
        OptimizedCacheTTL cacheTTL = OptimizedCacheTTL.of(ttlSeconds);
        OptimizedCache cache = OptimizedCache.of(result, cacheTTL.getLogicalTTL());

        redisTemplate.opsForValue()
                .set(
                        key,
                        DataSerializer.serialize(cache),
                        cacheTTL.getPhysicalTTL()
                );
        return result;
    }

    private String generateKey(String prefix, Object[] args) {
        return prefix + DELIMITER + Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(DELIMITER));
    }

}
