package app.commons;

import java.time.Duration;


public interface LockManager {
    
    String DEFAULT = "default";
    
    boolean obtainLock(final String key);
    boolean obtainLock(final String key, Duration maxWaiting);
    
    void releaseLock(String key);
}
