/*******************************************************************************
 * This file is part of the Technical Infrastructure Monitoring (TIM) project.
 * See http://ts-project-tim.web.cern.ch
 *
 * Copyright (C) 2004 - 2014 CERN. This program is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with this program; if not,
 * write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * Author: TIM team, tim.support@cern.ch
 ******************************************************************************/
package cern.c2mon.server.cache.device;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cern.c2mon.server.cache.DeviceCache;
import cern.c2mon.server.cache.DeviceClassCache;
import cern.c2mon.server.cache.DeviceClassFacade;
import cern.c2mon.server.cache.DeviceFacade;
import cern.c2mon.server.common.device.Device;
import cern.c2mon.server.common.device.DeviceCacheObject;
import cern.c2mon.server.common.device.DeviceClass;
import cern.c2mon.server.common.device.DeviceClassCacheObject;
import cern.c2mon.shared.common.ConfigurationException;

/**
 * @author Justin Lewis Salmon
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration({ "classpath:cern/c2mon/server/cache/config/server-cache-device-facade-test.xml" })
public class DeviceFacadeImplTest {

  /** Component to test */
  @Autowired
  DeviceClassFacade deviceClassFacade;

  @Autowired
  DeviceFacade deviceFacade;

  /** Mocked components */
  @Autowired
  DeviceCache deviceCacheMock;

  @Autowired
  DeviceClassCache deviceClassCacheMock;

  @Test
  public void testGetClassNames() {
    // Reset the mock
    EasyMock.reset(deviceCacheMock, deviceClassCacheMock);

    List<String> classNamesReturn = new ArrayList<>();
    classNamesReturn.add("test_device_class_name_1");
    classNamesReturn.add("test_device_class_name_2");

    List<Long> classIds = new ArrayList<>();
    classIds.add(1000L);
    classIds.add(2000L);

    DeviceClass class1 = new DeviceClassCacheObject(1000L, "test_device_class_name_1", "Test description");
    DeviceClass class2 = new DeviceClassCacheObject(2000L, "test_device_class_name_2", "Test description");

    // Expect the facade to get the class names from the class cache
    EasyMock.expect(deviceClassCacheMock.getKeys()).andReturn(classIds);
    EasyMock.expect(deviceClassCacheMock.get(EasyMock.anyLong())).andReturn(class1);
    EasyMock.expect(deviceClassCacheMock.get(EasyMock.anyLong())).andReturn(class2);

    // Setup is finished, need to activate the mock
    EasyMock.replay(deviceCacheMock, deviceClassCacheMock);

    List<String> classNames = deviceClassFacade.getDeviceClassNames();
    Assert.assertTrue(classNames.get(0).equals("test_device_class_name_1"));
    Assert.assertTrue(classNames.get(1).equals("test_device_class_name_2"));

    // Verify that everything happened as expected
    EasyMock.verify(deviceCacheMock, deviceClassCacheMock);
  }

  @Test
  public void testGetDevices() {
    // Reset the mock
    EasyMock.reset(deviceCacheMock, deviceClassCacheMock);

    String deviceClassName = "test_device_class_name";
    DeviceClassCacheObject deviceClassReturn = new DeviceClassCacheObject(1L, "test_device_class_name_1", "Test description");

    List<Long> deviceIds = new ArrayList<>();
    deviceIds.add(1000L);
    deviceIds.add(2000L);
    deviceClassReturn.setDeviceIds(deviceIds);

    Device device1 = new DeviceCacheObject(1000L, "test_device_1", 1L);
    Device device2 = new DeviceCacheObject(2000L, "test_device_2", 1L);

    // Expect the facade to get the device class object
    EasyMock.expect(deviceClassCacheMock.getDeviceClassByName(deviceClassName)).andReturn(deviceClassReturn);
    // Expect the facade to get the devices
    EasyMock.expect(deviceCacheMock.getCopy(1000L)).andReturn(device1);
    EasyMock.expect(deviceCacheMock.getCopy(2000L)).andReturn(device2);

    // Setup is finished, need to activate the mock
    EasyMock.replay(deviceCacheMock, deviceClassCacheMock);

    List<Device> devices = deviceFacade.getDevices(deviceClassName);
    Assert.assertTrue(devices.get(0).getId() == device1.getId());
    Assert.assertTrue(devices.get(1).getId() == device2.getId());
    Assert.assertTrue(devices.get(0).getDeviceClassId() == device1.getDeviceClassId());
    Assert.assertTrue(devices.get(1).getDeviceClassId() == device2.getDeviceClassId());

    // Verify that everything happened as expected
    EasyMock.verify(deviceCacheMock, deviceClassCacheMock);
  }

  @Test
  public void testCreateDeviceClassCacheObject() throws IllegalAccessException {
    Properties properties = new Properties();
    properties.put("name", "device_class_name");
    properties.put("properties", "<Properties><Property name=\"TEST_PROPERTY_1\" description=\"Description of TEST_PROPERTY_1\" />"
        + "<Property name=\"TEST_PROPERTY_2\" description=\"Description of TEST_PROPERTY_2\" /></Properties>");
    properties.put("commands", "<Commands><Command name=\"TEST_COMMAND_1\" description=\"Description of TEST_COMMAND_1\" />"
        + "<Command name=\"TEST_COMMAND_2\" description=\"Description of TEST_COMMAND_2\" /></Commands>");

    DeviceClass deviceClass = deviceClassFacade.createCacheObject(10L, properties);
    Assert.assertNotNull(deviceClass);
    Assert.assertTrue(deviceClass.getId() == 10L);
    Assert.assertTrue(deviceClass.getName() == properties.getProperty("name"));

    Assert.assertTrue(deviceClass.getProperties().size() == 2);
    Assert.assertTrue(deviceClass.getProperties().contains("TEST_PROPERTY_1"));
    Assert.assertTrue(deviceClass.getProperties().contains("TEST_PROPERTY_2"));

    Assert.assertTrue(deviceClass.getCommands().size() == 2);
    Assert.assertTrue(deviceClass.getCommands().contains("TEST_COMMAND_1"));
    Assert.assertTrue(deviceClass.getCommands().contains("TEST_COMMAND_2"));

    // Test XML parser throws exception with invalid XML
    properties.put("properties", "invalid XML string");
    try {
      deviceClassFacade.createCacheObject(10L, properties);
      Assert.fail("createCacheObject() did not throw exception");
    } catch (ConfigurationException e) {
    }

    properties.put("properties", "<Properties />");
    properties.put("commands", "invalid XML string");
    try {
      deviceClassFacade.createCacheObject(10L, properties);
      Assert.fail("createCacheObject() did not throw exception");
    } catch (ConfigurationException e) {
    }
  }

  @Test
  public void testCreateDeviceCacheObject() throws IllegalAccessException {
    Properties propertyValues = new Properties();
    propertyValues.put("name", "device_name");
    propertyValues.put("propertyValues", "<PropertyValues><PropertyValue name=\"TEST_PROPERTY_1\" tag-id=\"100430\" />"
        + "<PropertyValue name=\"TEST_PROPERTY_2\" tag-id=\"100431\" /></PropertyValues>");
    propertyValues.put("commandValues", "<CommandValues><CommandValue name=\"TEST_COMMAND_1\" command-tag-id=\"4287\" />"
        + "<CommandValue name=\"TEST_COMMAND_2\" command-tag-id=\"4288\" /></CommandValues>");

    Device device = deviceFacade.createCacheObject(10L, propertyValues);
    Assert.assertNotNull(device);
    Assert.assertTrue(device.getId() == 10L);
    Assert.assertTrue(device.getName() == propertyValues.getProperty("name"));

    Assert.assertTrue(device.getPropertyValues().size() == 2);
    Assert.assertTrue(device.getPropertyValues().containsKey("TEST_PROPERTY_1"));
    Assert.assertTrue(device.getPropertyValues().containsKey("TEST_PROPERTY_2"));

    Assert.assertTrue(device.getCommandValues().size() == 2);
    Assert.assertTrue(device.getCommandValues().containsKey("TEST_COMMAND_1"));
    Assert.assertTrue(device.getCommandValues().containsKey("TEST_COMMAND_2"));

    // Test XML parser throws exception with invalid XML
    propertyValues.put("propertyValues", "invalid XML string");
    try {
      deviceFacade.createCacheObject(10L, propertyValues);
      Assert.fail("createCacheObject() did not throw exception");
    } catch (ConfigurationException e) {
    }

    propertyValues.put("propertyValues", "<PropertyValues />");
    propertyValues.put("commandValues", "invalid XML string");
    try {
      deviceFacade.createCacheObject(10L, propertyValues);
      Assert.fail("createCacheObject() did not throw exception");
    } catch (ConfigurationException e) {
    }
  }
}
