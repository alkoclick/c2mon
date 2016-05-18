/******************************************************************************
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
 *****************************************************************************/
package cern.c2mon.shared.client.tag;

import cern.c2mon.shared.client.alarm.AlarmValue;
import cern.c2mon.shared.client.alarm.AlarmValueImpl;
import cern.c2mon.shared.client.request.ClientRequestReport;
import cern.c2mon.shared.common.datatag.DataTagQualityImpl;
import lombok.Data;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * This class implements the <code>TransferTagValue</code> interface. It is
 * used for sending tag value update to the client layer. Please note that this
 * class contains only fields that can be changed by the source. All static tag
 * specification fields are defined by <code>TransferTagImpl</code> and sent in
 * a separate message.
 *
 * @author Matthias Braeger
 * @see TagValueUpdate
 */
@Data
@Slf4j
public class TransferTagValueImpl extends ClientRequestReport implements TagValueUpdate {

  /** The unique tag id */
  @NotNull @Min(1)
  private final Long id;

  /** The class type of the tag value as String representation */
  private String valueClassName;

  /** The value of the tag */
  private Object value;

  /** The current tag mode */
  @NotNull
  private final TagMode mode;

  /**
   * <code>true</code>, if the tag value is currently simulated and not
   * corresponding to a live event.
   */
  private boolean simulated = false;

  /** Collection of <code>AlarmValue</code> objects */
  @Singular
  private final Collection<AlarmValueImpl> alarms = new ArrayList<>();

  /** The quality of the tag */
  @NotNull
  private final DataTagQualityImpl dataTagQuality;

  /** The tag description */
  private String description;

  /** The current tag value description */
  private String valueDescription;

  /**
   * The source timestamp that indicates when the value change was generated.
   * This timestamp can be null, if the change message was generated by the DAQ or
   * by the server itself (usually error message).
   */
  @Past
  private final Timestamp sourceTimestamp;

  /**
   * The DAQ timestamp that indicates when the change message was sent from the DAQ module.
   * This timestamp can be null, if the change message was generated by the server (usually
   * error message).
   */
  @Past
  private final Timestamp daqTimestamp;

  /** The server timestamp that indicates when the change message passed the server */
  @NotNull @Past
  private final Timestamp serverTimestamp;

  /**
   * Hidden Constructor used by JSON
   */
  @SuppressWarnings("unused")
  private TransferTagValueImpl() {
    this(null, null, null, null, null, null, null, null, null);
  }

  /**
   * Default Constructor
   *
   * @param pTagId The unique tag id
   * @param pTagValue actual tag value (must be String or Number)
   * @param pTagQuality The quality of the tag
   * @param pSourceTimestamp The source timestamp that indicates when the value change was generated
   * @param pDaqTimestamp The DAQ timestamp that indicates when the value change message has been sent from the DAQ
   * @param pServerTimestamp The server timestamp that indicates when the change message passed the server
   * @param pDescription The tag description
   * @param pValueDescription The current tag value description
   */
  public TransferTagValueImpl(final Long pTagId,
                              final Object pTagValue,
                              final String pValueDescription,
                              final DataTagQualityImpl pTagQuality,
                              final TagMode pMode,
                              final Timestamp pSourceTimestamp,
                              final Timestamp pDaqTimestamp,
                              final Timestamp pServerTimestamp,
                              final String pDescription) {
    id = pTagId;

    value = pTagValue;
//    valueClassName = getType(valueType).getName();

    dataTagQuality = pTagQuality;
    this.mode = pMode;
    sourceTimestamp = pSourceTimestamp;
    daqTimestamp = pDaqTimestamp;
    serverTimestamp = pServerTimestamp;
    description = pDescription;
    valueDescription = pValueDescription;
  }


  /**
   * Adds the given alarm value to the transfer tag, except if an alarm with the same ID
   * id already present or, if the alarm value does not belong to the specified tag.
   * @param alarmValue The alarm value to be added
   * @return <code>true</code>, if the alarm was successfully added to this tag
   */
  public final boolean addAlarmValue(@Valid final AlarmValueImpl alarmValue) {
    if (alarmValue.getTagId().equals(id)) {
      return alarms.add(alarmValue);
    }

    return false;
  }


  /**
   * Adds the given alarm values to the transfer tag, except if an alarm with the same ID
   * id already present or, if the alarm values do not belong to the specified tag.
   * @param alarmValues The alarm values to be added
   */
  public final void addAlarmValues(final List<AlarmValueImpl> alarmValues) {
    if (alarmValues != null) {
      for (AlarmValueImpl alarmValue : alarmValues) {
        if (alarmValue != null) {
          addAlarmValue(alarmValue);
        }
      }
    }
  }


  /**
   * @see cern.c2mon.shared.client.tag.TagValueUpdate#getAlarms()
   */
  @Override
  public final Collection<AlarmValue> getAlarms() {
    return new ArrayList<AlarmValue>(alarms);
  }

}
