// Projeto desenvolvido com auxílio de IA (Claude, da Anthropic).
package com.calistenia.tracker.controller;

import com.calistenia.tracker.model.User;
import com.calistenia.tracker.repository.UserRepository;
import com.calistenia.tracker.service.PasswordHasher;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Cuida de cadastro, login e logout. */
@Controller
public class AuthController {

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MIN_PASSWORD_LENGTH = 4;

    private static final String MSG_INVALID_CREDENTIALS = "Usuário ou senha inválidos.";
    private static final String MSG_USERNAME_TOO_SHORT =
            "O usuário deve ter pelo menos " + MIN_USERNAME_LENGTH + " caracteres.";
    private static final String MSG_PASSWORD_TOO_SHORT =
            "A senha deve ter pelo menos " + MIN_PASSWORD_LENGTH + " caracteres.";
    private static final String MSG_PASSWORDS_DIFFERENT = "As senhas não conferem.";
    private static final String MSG_USERNAME_TAKEN = "Esse usuário já existe.";
    private static final String MSG_ACCOUNT_CREATED = "Conta criada! Faça login.";

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final UserSession userSession;

    // O Spring entrega os três objetos prontos (injeção de dependência)
    public AuthController(UserRepository userRepository,
                          PasswordHasher passwordHasher,
                          UserSession userSession) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.userSession = userSession;
    }

    @GetMapping("/login")
    public String showLoginPage(HttpSession session) {
        if (userSession.isLoggedIn(session)) {
            return Pages.REDIRECT_TO_DASHBOARD;
        }
        return Pages.LOGIN;
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {

        User user = authenticate(username, password);

        if (user == null) {
            model.addAttribute(MessageKeys.ERROR, MSG_INVALID_CREDENTIALS);
            return Pages.LOGIN;
        }

        userSession.logIn(session, user);
        return Pages.REDIRECT_TO_DASHBOARD;
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return Pages.REGISTER;
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam("confirmPassword") String confirmPassword,
                           Model model,
                           RedirectAttributes redirect) {

        String name = username.trim();

        String error = findRegistrationError(name, password, confirmPassword);
        if (error != null) {
            model.addAttribute(MessageKeys.ERROR, error);
            return Pages.REGISTER;
        }

        createUser(name, password);

        // Mensagem "flash": sobrevive ao redirect e aparece uma vez na tela de login
        redirect.addFlashAttribute(MessageKeys.SUCCESS, MSG_ACCOUNT_CREATED);
        return Pages.REDIRECT_TO_LOGIN;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        userSession.logOut(session);
        return Pages.REDIRECT_TO_LOGIN;
    }

    /**
     * Procura o usuário e confere a senha.
     * Devolve o usuário, ou null se o nome não existe ou a senha está errada.
     */
    private User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username.trim());

        if (user == null) {
            return null;
        }
        if (!passwordHasher.matches(password, user.getPasswordHash())) {
            return null;
        }
        return user;
    }

    /** Devolve o texto do primeiro erro de cadastro encontrado, ou null se tudo estiver válido. */
    private String findRegistrationError(String name, String password, String confirmPassword) {
        if (name.length() < MIN_USERNAME_LENGTH) {
            return MSG_USERNAME_TOO_SHORT;
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return MSG_PASSWORD_TOO_SHORT;
        }
        if (!password.equals(confirmPassword)) {
            return MSG_PASSWORDS_DIFFERENT;
        }
        if (userRepository.findByUsername(name) != null) {
            return MSG_USERNAME_TAKEN;
        }
        return null;
    }

    private void createUser(String name, String password) {
        String passwordHash = passwordHasher.hash(password);
        userRepository.save(new User(name, passwordHash));
    }
}
