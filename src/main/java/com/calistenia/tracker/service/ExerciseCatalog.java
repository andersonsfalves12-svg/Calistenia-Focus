// Projeto desenvolvido com auxílio de IA (Claude, da Anthropic).
package com.calistenia.tracker.service;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A lista oficial de exercícios que o formulário oferece.
 *
 * Cada exercício tem uma chave (usada nos campos do formulário, como "sets_flexoes")
 * e um nome para mostrar na tela. O LinkedHashMap mantém a ordem em que foram adicionados.
 */
@Component
public class ExerciseCatalog {

    private final Map<String, String> namesByKey = new LinkedHashMap<>();

    public ExerciseCatalog() {
        namesByKey.put("flexoes", "Flexões");
        namesByKey.put("barra", "Barra Fixa");
        namesByKey.put("abdominais", "Abdominais");
        namesByKey.put("agachamentos", "Agachamentos");
        namesByKey.put("fundos", "Fundos em Paralelas");
    }

    /** Todos os exercícios (chave e nome), na ordem em que aparecem no formulário. */
    public Map<String, String> getAll() {
        return Collections.unmodifiableMap(namesByKey);
    }

    public boolean contains(String key) {
        return namesByKey.containsKey(key);
    }

    /** Nome para mostrar na tela, ou null se a chave não existir. */
    public String getName(String key) {
        return namesByKey.get(key);
    }
}
