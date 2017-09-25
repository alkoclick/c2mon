package cern.c2mon.cache.command;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cern.c2mon.cache.AbstractCacheLoaderTest;
import cern.c2mon.cache.api.C2monCache;
import cern.c2mon.server.cache.dbaccess.CommandTagMapper;
import cern.c2mon.server.common.alarm.Alarm;
import cern.c2mon.server.common.command.CommandTagCacheObject;
import cern.c2mon.server.test.CacheObjectComparison;
import cern.c2mon.shared.common.command.CommandTag;

import static org.junit.Assert.*;

/**
 * @author Szymon Halastra
 */
public class CommandTagCacheLoaderTest extends AbstractCacheLoaderTest {

  @Autowired
  private C2monCache<Long, CommandTag> commandTagCacheRef;

  @Autowired
  private CommandTagMapper commandTagMapper;

  @Before
  public void prepare() {
    commandTagCacheRef.init();
  }

  @Test
  public void preloadCache() {
    assertNotNull("CommandTag Cache should not be null", commandTagCacheRef);

    List<CommandTag> commandList = commandTagMapper.getAll();

    Set<Long> keySet = commandList.stream().map(CommandTag::getId).collect(Collectors.toSet());
    assertTrue("List of command tags should not be empty", commandList.size() > 0);

    assertEquals("Size of cache and DB mapping should be equal", commandList.size(), commandTagCacheRef.getKeys().size());

    //compare all the objects from the cache and buffer
    Iterator<CommandTag> it = commandList.iterator();
    while (it.hasNext()) {
      CommandTag currentCommandTag = it.next();
      CacheObjectComparison.equals((CommandTagCacheObject) currentCommandTag,
              (CommandTagCacheObject) commandTagCacheRef.get(currentCommandTag.getId()));
    }
  }
}