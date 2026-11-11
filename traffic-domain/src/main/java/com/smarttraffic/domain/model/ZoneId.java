package com.smarttraffic.domain.model;

/**
 * Identificador de una zona de la ciudad. Valida en el constructor
 * para que ninguna entidad reciba una zona en blanco.
 */
public record ZoneId(String value) {

    public ZoneId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("La zona debe tener un identificador.");
        }
        value = value.trim();
    }

    @Override
    public String toString() {
        return value;
    }
}
