package com.smarttraffic.dispatcher;

import com.smarttraffic.domain.alert.LevelChange;
import com.smarttraffic.domain.alert.CongestionObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingAlertObserver implements CongestionObserver {

    private static final Logger log = LoggerFactory.getLogger(LoggingAlertObserver.class);

    @Override
    public void onLevelRaised(LevelChange change) {
        log.warn("{}", change.current().state().alert(change.current().zoneId()));
    }
}
