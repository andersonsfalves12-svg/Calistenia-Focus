// Projeto desenvolvido com auxílio de IA (Claude, da Anthropic).
package com.calistenia.tracker.controller;

/**
 * Nomes das páginas (arquivos em templates/) e dos redirecionamentos.
 * Ficam aqui para os controllers não repetirem textos soltos pelo código.
 */
public final class Pages {

    public static final String LOGIN = "login";
    public static final String REGISTER = "register";
    public static final String DASHBOARD = "dashboard";

    public static final String REDIRECT_TO_LOGIN = "redirect:/login";
    public static final String REDIRECT_TO_DASHBOARD = "redirect:/dashboard";

    private Pages() {
    }
}
