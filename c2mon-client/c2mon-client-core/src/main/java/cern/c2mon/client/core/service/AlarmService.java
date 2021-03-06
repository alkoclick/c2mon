/*******************************************************************************
 * Copyright (C) 2010-2016 CERN. All rights not expressly granted are reserved.
 *
 * This file is part of the CERN Control and Monitoring Platform 'C2MON'.
 * C2MON is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the license.
 *
 * C2MON is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with C2MON. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cern.c2mon.client.core.service;

import java.util.Collection;

import javax.jms.JMSException;

import cern.c2mon.client.core.jms.AlarmListener;
import cern.c2mon.shared.client.alarm.AlarmValue;

public interface AlarmService {

  /**
   * Registers an <code>AlarmListener</code> to the <code>TagServiceImpl</code>.
   * @param listener The listener to be registered
   * @throws JMSException
   */
  void addAlarmListener(AlarmListener listener) throws JMSException;
  
  /**
   * Returns an {@link AlarmValue} object for every valid id on the list.
   * The values are fetched from the server.
   * However, in case of a connection error or an unknown tag id the corresponding
   * tag might be missing.
   *
   * @param alarmIds A collection of alarm id's
   * @return A collection of all <code>AlarmValue</code> objects
   */
  Collection<AlarmValue> getAlarms(final Collection<Long> alarmIds);
  
  /**
   * Returns an {@link AlarmValue} object for every active alarm found
   * in the server.
   *
   * @return A collection of all active <code>AlarmValue</code> objects
   */
  Collection<AlarmValue> getAllActiveAlarms();
  
  /**
   * Unregisters an <code>AlarmListener</code> from the <code>TagServiceImpl</code>.
   * @param listener The listener to be unregistered
   * @throws JMSException
   */
  void removeAlarmListener(AlarmListener listener) throws JMSException;
}
