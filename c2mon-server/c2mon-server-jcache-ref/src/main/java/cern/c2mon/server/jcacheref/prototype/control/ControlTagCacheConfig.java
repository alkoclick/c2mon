package cern.c2mon.server.jcacheref.prototype.control;

import javax.cache.Cache;
import javax.cache.CacheManager;

import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cern.c2mon.server.common.config.C2monCacheName;
import cern.c2mon.server.common.control.ControlTag;
import cern.c2mon.server.jcacheref.prototype.common.BasicCache;

/**
 * @author Szymon Halastra
 */
@Configuration
public class ControlTagCacheConfig implements BasicCache {

  private static final String CONTROL_TAG_CACHE = "controlTagCache";

  @Bean(name = CONTROL_TAG_CACHE)
  public Cache<Long, ControlTag> getControlTagCache(JCacheCacheManager cacheManager) {
    CacheManager cm = cacheManager.getCacheManager();
    return cm.getCache(CONTROL_TAG_CACHE, Long.class, ControlTag.class);
  }

  @Override
  public C2monCacheName getName() {
    return C2monCacheName.CONTROLTAG;
  }
}