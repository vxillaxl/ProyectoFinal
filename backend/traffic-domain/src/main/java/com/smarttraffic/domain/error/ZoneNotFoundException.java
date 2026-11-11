package com.smarttraffic.domain.error;

import com.smarttraffic.domain.model.ZoneId;

/** La réplica local todavía no conoce la zona pedida. */
public final class ZoneNotFoundException extends RuntimeException {

    private final ZoneId zoneId;

    public ZoneNotFoundException(ZoneId zoneId) {
        super("No hay estado conocido para " + zoneId.value() + ".");
        this.zoneId = zoneId;
    }

    public ZoneId zoneId() {
        return zoneId;
    }
}
