package com.smarttraffic.domain.history;

/**
 * Guarda un hecho del histórico. Devuelve false si ese event_id ya estaba.
 */
public interface TrafficHistoryRepository {

    boolean append(TrafficHistoryEntry entry);
}
