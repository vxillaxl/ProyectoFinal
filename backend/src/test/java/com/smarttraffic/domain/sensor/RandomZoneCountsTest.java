package com.smarttraffic.domain.sensor;

import com.smarttraffic.domain.model.ZoneId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomZoneCountsTest {

    private final ZoneId zoneA = new ZoneId("Zona A");
    private final ZoneId zoneB = new ZoneId("Zona B");

    @Test
    void enQuinceMinutosSeVenLosTresRangos() {
        RandomZoneCounts counts = new RandomZoneCounts(41, new Random(42));
        Instant start = Instant.EPOCH;
        boolean low = false;
        boolean mid = false;
        boolean high = false;
        for (ZoneId zone : new ZoneId[]{zoneA, zoneB}) {
            for (int second = 0; second < 900; second++) {
                int count = counts.count(zone, start.plusSeconds(second));
                if (count <= 9) {
                    low = true;
                }
                if (count >= 10 && count <= 24) {
                    mid = true;
                }
                if (count >= 25) {
                    high = true;
                }
            }
        }
        assertTrue(low);
        assertTrue(mid);
        assertTrue(high);
    }

    @Test
    void conSemillaFijaElConteoSeQuedaEnElRango() {
        RandomZoneCounts counts = new RandomZoneCounts(41, new Random(7));
        Instant when = Instant.parse("2026-11-12T00:00:00Z");
        for (int second = 0; second < 180; second++) {
            int count = counts.count(zoneA, when.plusSeconds(second));
            assertTrue(count >= 3 && count <= 40, "conteo " + count);
        }
    }

    @Test
    void elRegimenCambiaCuandoVenceSuDuracion() {
        RandomZoneCounts counts = new RandomZoneCounts(41, new ScriptedRandom(
                0, 0, 4,
                1,
                2, 0, 0
        ));
        Instant start = Instant.EPOCH;
        assertEquals(7, counts.count(zoneA, start));
        assertEquals(4, counts.count(zoneA, start.plusSeconds(19)));
        assertEquals(25, counts.count(zoneA, start.plusSeconds(20)));
    }

    @Test
    void cadaZonaSorteaSuPropioRegimen() {
        RandomZoneCounts counts = new RandomZoneCounts(41, new ScriptedRandom(
                0, 0, 0,
                2, 0, 0
        ));
        Instant start = Instant.EPOCH;
        assertEquals(3, counts.count(zoneA, start));
        assertEquals(25, counts.count(zoneB, start));
    }

    private static final class ScriptedRandom extends Random {
        private final int[] values;
        private int cursor;

        private ScriptedRandom(int... values) {
            this.values = values;
        }

        @Override
        public int nextInt(int bound) {
            return values[cursor++];
        }
    }
}
