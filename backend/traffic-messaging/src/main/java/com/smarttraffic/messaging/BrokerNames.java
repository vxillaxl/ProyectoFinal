package com.smarttraffic.messaging;

/** Nombres del enunciado. Un solo sitio para topics, exchanges y colas. */
public final class BrokerNames {

    public static final String RAW_TRAFFIC = "raw_traffic_data";
    public static final String TRAFFIC_UPDATES = "traffic_updates";
    public static final String DASHBOARD_QUEUE = "traffic_updates_dashboard";
    public static final String DISPATCHER_QUEUE = "traffic_updates_dispatcher";
    public static final String QUERY_QUEUE = "query_traffic_queue";
    public static final String QUERY_ANSWERS = "query_answers";

    private BrokerNames() {
    }
}
