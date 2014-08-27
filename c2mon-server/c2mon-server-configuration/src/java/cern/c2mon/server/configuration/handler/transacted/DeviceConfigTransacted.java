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
package cern.c2mon.server.configuration.handler.transacted;

import java.util.Properties;

import cern.c2mon.server.configuration.impl.ProcessChange;
import cern.c2mon.shared.client.configuration.ConfigurationElement;
import cern.c2mon.shared.client.configuration.ConfigurationElementReport;

/**
 * For internal use only. Allows use of Spring AOP for transaction management.
 *
 * @author Justin Lewis Salmon
 */
public interface DeviceConfigTransacted {

  /**
   * Create a Device object in the C2MON server within a transaction.
   *
   * @param element contains details of the Device to be created
   *
   * @return the change event to send to the DAQ (none in this case)
   * @throws IllegalAccessException
   */
  public ProcessChange doCreateDevice(final ConfigurationElement element) throws IllegalAccessException;

  /**
   * Updates a Device object in the C2MON server within a transaction.
   *
   * @param id the ID of the Device object to update
   * @param properties details of the fields to modify
   *
   * @return the change event to send to the DAQ (none in this case)
   */
  public ProcessChange doUpdateDevice(final Long id, final Properties properties);

  /**
   * Removes a Device object from the C2MON server within a transaction.
   *
   * @param id the ID of the Device to remove
   * @param elementReport the report on success
   *
   * @return the change event to send to the DAQ (none in this case)
   */
  public ProcessChange doRemoveDevice(final Long id, final ConfigurationElementReport elementReport);
}
