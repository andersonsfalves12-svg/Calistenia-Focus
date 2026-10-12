// Projeto desenvolvido com auxílio de IA (Claude, da Anthropic).
package com.calistenia.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal do Calistenia Focus: é por aqui que a aplicação começa a rodar.
 *
 * O @SpringBootApplication liga a configuração automática do Spring Boot
 * e faz ele procurar controllers, entidades e repositórios neste pacote.
 */
@SpringBootApplication
public class CalisteniaFocusApplication {

    public static void main(String[] args) {
        SpringApplication.run(CalisteniaFocusApplication.class, args);
    }
}
