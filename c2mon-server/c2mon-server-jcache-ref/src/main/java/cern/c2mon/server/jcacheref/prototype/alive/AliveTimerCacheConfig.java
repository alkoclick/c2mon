package cern.c2mon.server.jcacheref.prototype.alive;

import java.io.Serializable;

import javax.cache.Cache;
import javax.cache.CacheManager;

import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cern.c2mon.server.common.alive.AliveTimer;
import cern.c2mon.server.common.config.C2monCacheName;
import cern.c2mon.server.jcacheref.prototype.common.BasicCache;

/**
 * @author Szymon Halastra
 */

@Configuration
public class AliveTimerCacheConfig implements BasicCache, Serializable {

  private static final String ALIVE_TIMER_CACHE = "aliveTimerCache";

  @Bean(name = ALIVE_TIMER_CACHE)
  public Cache<Long, AliveTimer> getAliveTimerCache(JCacheCacheManager cacheManager) {
    CacheManager cm = cacheManager.getCacheManager();
    return cm.getCache(ALIVE_TIMER_CACHE, Long.class, AliveTimer.class);
  }

  @Override
  public C2monCacheName getName() {
    return C2monCacheName.ALIVETIMER;
  }
}