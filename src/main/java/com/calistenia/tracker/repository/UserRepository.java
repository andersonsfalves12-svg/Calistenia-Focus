// Projeto desenvolvido com auxílio de IA (Claude, da Anthropic).
package com.calistenia.tracker.repository;

import com.calistenia.tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acesso ao banco para a tabela de usuários.
 * Ao estender JpaRepository, métodos como save() e delete() já vêm prontos.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * O Spring entende o nome do método e cria a consulta sozinho.
     * Devolve o usuário com esse nome, ou null se ele não existir.
     */
    User findByUsername(String username);
}
