/******************************************************************************
 * Copyright (C) 2010-2019 CERN. All rights not expressly granted are reserved.
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
package cern.c2mon.server.cache.alarm.oscillation;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import cern.c2mon.server.cache.AlarmCache;
import cern.c2mon.server.cache.alarm.config.OscillationProperties;
import cern.c2mon.server.common.alarm.AlarmCacheObject;

/**
 *
 * @author Emiliano Piselli
 *
 */
@Component
@Data
@Slf4j
public final class OscillationUpdater {

    @Autowired
    AlarmCache alarmCache;

    @Autowired
    OscillationProperties oscillationProperties;

    /**
     * Check, if the alarm shall still keep its oscillation flag. This is the case, if the last alarm update was longer ago than the defined threshold. 
     * @see OscillationProperties#getTimeOscillationAlive()
     * @param alarmCacheObject The current alarm object of the cache
     * @return true, if the alarm shall still keep its oscillation flag
     */
    public boolean checkOscillAlive(AlarmCacheObject alarmCacheObject) {
        long systemTime = System.currentTimeMillis();
        long alarmTs = alarmCacheObject.getTimestamp().getTime();
        if (log.isTraceEnabled()) {
            log.trace(" -> OscillationUpdater.checkOscillAlive()  Alarm #{} diff: {} systime : {} alarmts : {}",
                    alarmCacheObject.getId(), (systemTime - alarmTs), new Timestamp(systemTime),
                    alarmCacheObject.getTimestamp().toString());
        }
        return (systemTime - alarmTs) < (oscillationProperties.getTimeOscillationAlive() * 1000);
    }

    public void increaseOscillCounter(AlarmCacheObject alarmCacheObject) {
        alarmCacheObject.setCounterFault(alarmCacheObject.getCounterFault() + 1);
        if (alarmCacheObject.getCounterFault() == 1
                || ((alarmCacheObject.getCounterFault() % oscillationProperties.getOscNumbers()) == 0)) {
            alarmCacheObject.setFirstOscTS(System.currentTimeMillis()-1);
        }
        
        if(checkOscillConditions(alarmCacheObject)) {
            // We only change the oscillation status if the alarm is currently oscillating.
            // Expiring the oscillation is the work of the OscillationUpdateChecker class.
            alarmCacheObject.setOscillating(true);
        }
    }

    public void resetOscillationCounter(AlarmCacheObject alarmCacheObject) {
        alarmCacheObject.setCounterFault(0);
        alarmCacheObject.setOscillating(false);
        alarmCacheObject.setFirstOscTS(0);
    }

    /**
     * Check whether we have accumulated enough oscillations over the prescribed
     * period of time to declare we are actually oscillating.
     *
     * @param alarmCacheObject
     *            the alarm cache object containing already the new alarm state.
     * @return true, if alarm shall be marked as oscillating.
     */
    private boolean checkOscillConditions(AlarmCacheObject alarmCacheObject) {
        return ((alarmCacheObject.getCounterFault() >= oscillationProperties.getOscNumbers())
                && ((System.currentTimeMillis() - alarmCacheObject.getFirstOscTS())
                    <= oscillationProperties.getTimeRange() * 1000));
    }
}
