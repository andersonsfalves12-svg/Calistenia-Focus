// Projeto desenvolvido com auxílio de IA (Claude, da Anthropic).
package com.calistenia.tracker.service;

import com.calistenia.tracker.model.WorkoutLog;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Calcula o volume de treino que o gráfico mostra. */
@Component
public class VolumeCalculator {

    /**
     * Soma o volume (séries x repetições) de todos os exercícios de cada data.
     * Exemplo: no dia 10/05, Flexões 3x10 (30) + Barra 3x5 (15) = 45.
     *
     * O TreeMap devolve as datas em ordem cronológica.
     */
    public Map<LocalDate, Integer> calculateByDay(List<WorkoutLog> logs) {
        Map<LocalDate, Integer> volumeByDay = new TreeMap<>();

        for (WorkoutLog log : logs) {
            int volumeSoFar = getVolumeOrZero(volumeByDay, log.getDate());
            volumeByDay.put(log.getDate(), volumeSoFar + log.calculateVolume());
        }

        return volumeByDay;
    }

    private int getVolumeOrZero(Map<LocalDate, Integer> volumeByDay, LocalDate day) {
        if (volumeByDay.containsKey(day)) {
            return volumeByDay.get(day);
        }
        return 0;
    }
}
