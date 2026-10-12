// Projeto desenvolvido com auxílio de IA (Claude, da Anthropic).
package com.calistenia.tracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

/**
 * Um usuário do sistema. Cada objeto vira uma linha na tabela "users".
 *
 * Só o hash da senha é guardado, nunca a senha digitada.
 * A classe é Serializable porque o objeto fica guardado dentro da sessão HTTP.
 */
@Entity
@Table(name = "users") // "user" é palavra reservada no H2, por isso o nome no plural
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    /** Construtor vazio: o JPA precisa dele para criar objetos lidos do banco. */
    protected User() {
    }

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
