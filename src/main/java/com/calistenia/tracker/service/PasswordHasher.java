// Projeto desenvolvido com auxílio de IA (Claude, da Anthropic).
package com.calistenia.tracker.service;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Transforma senhas em hash SHA-256: um texto embaralhado que não volta ao original.
 *
 * O prefixo fixo deixa o hash diferente de hashes genéricos de tabelas prontas.
 * Em um sistema real, o recomendado seria BCrypt (com sal diferente para cada senha).
 */
@Component
public class PasswordHasher {

    private static final String ALGORITHM = "SHA-256";
    private static final String PREFIX = "calistenia::";

    /** Devolve o hash da senha como texto hexadecimal. */
    public String hash(String password) {
        byte[] hashBytes = digest(PREFIX + password);
        return toHexadecimal(hashBytes);
    }

    /** Confere se a senha digitada gera o mesmo hash que está guardado no banco. */
    public boolean matches(String password, String expectedHash) {
        return hash(password).equals(expectedHash);
    }

    private byte[] digest(String text) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
            return messageDigest.digest(text.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            // Não deve acontecer: todo Java tem SHA-256
            throw new IllegalStateException(ALGORITHM + " indisponível", e);
        }
    }

    /** Converte cada byte em 2 caracteres (0-9, a-f) para guardar o hash como texto. */
    private String toHexadecimal(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
