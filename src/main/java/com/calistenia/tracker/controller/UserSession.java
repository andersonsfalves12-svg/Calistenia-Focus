// Projeto desenvolvido com auxílio de IA (Claude, da Anthropic).
package com.calistenia.tracker.controller;

import com.calistenia.tracker.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

/**
 * Guarda e consulta quem está logado na HttpSession (a "memória" do navegador).
 * Concentra o acesso à sessão em um só lugar.
 */
@Component
public class UserSession {

    private static final String USER_ATTRIBUTE = "user";

    /** Devolve o usuário logado, ou null se ninguém estiver logado. */
    public User getLoggedUser(HttpSession session) {
        return (User) session.getAttribute(USER_ATTRIBUTE);
    }

    public boolean isLoggedIn(HttpSession session) {
        return getLoggedUser(session) != null;
    }

    public void logIn(HttpSession session, User user) {
        session.setAttribute(USER_ATTRIBUTE, user);
    }

    /** Destrói a sessão: o usuário deixa de estar logado. */
    public void logOut(HttpSession session) {
        session.invalidate();
    }
}
