package com.smarttraffic.domain.model;

/**
 * Cliente que pregunta por un trayecto. La routing key de su respuesta
 * sale de aquí, así el enrutamiento no se arma con strings sueltos.
 */
public record ClientId(String value) {

    public ClientId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El cliente debe tener un identificador.");
        }
        value = value.trim();
        if (!value.matches("[A-Za-z0-9_\\-]+")) {
            throw new IllegalArgumentException("Identificador de cliente inválido: " + value);
        }
    }

    public String answerRoutingKey() {
        return "answer." + value;
    }
}
