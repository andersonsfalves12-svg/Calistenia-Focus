// Projeto desenvolvido com auxílio de IA (Claude, da Anthropic).
package com.calistenia.tracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Cuida da rota raiz ("/"). */
@Controller
public class HomeController {

    /** Quem abre o site pela raiz é enviado para o login. */
    @GetMapping("/")
    public String home() {
        return Pages.REDIRECT_TO_LOGIN;
    }
}
