package app.commons;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisLockManager implements LockManager {
    private static final Logger log = LoggerFactory.getLogger(RedisLockManager.class);

    private static final long MAX_ATTEMPTS = 4;
    private static final long LOCK_TIME = Duration.of(5, ChronoUnit.MINUTES).toMillis();

    @Autowired
    private RedisTemplate<String, String> redis;

    @Override
    public boolean obtainLock(final String lockKey) {
    	return obtainLock(lockKey, Duration.of(0, ChronoUnit.SECONDS));
    }

    @Override
    public boolean obtainLock(String lockKey, Duration maxWaiting) {
        boolean wait = maxWaiting.toMillis() > 0;
        final long end = System.currentTimeMillis() + maxWaiting.toMillis();
        try {
            boolean result = false;
            do {
                result = redis.opsForValue().setIfAbsent(lockKey, "");
                if (result) {
                	redis.expire(lockKey, LOCK_TIME, TimeUnit.MILLISECONDS);
                    return true;
                }
                try {
                    Thread.sleep(maxWaiting.toMillis() / MAX_ATTEMPTS);
                } catch (InterruptedException ignore) {}
            } while (!result && wait && end > System.currentTimeMillis());

            return result;
        } catch (Exception e) {
            log.error("Problem obtaining lock - {} - {}", lockKey, e.getMessage());
            return false;
        }
    }

    @Override
    public void releaseLock(String key) {
        this.redis.delete(key);
    }
}
