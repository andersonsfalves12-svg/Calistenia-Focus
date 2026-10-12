// Projeto desenvolvido com auxílio de IA (Claude, da Anthropic).
package com.calistenia.tracker.repository;

import com.calistenia.tracker.model.User;
import com.calistenia.tracker.model.WorkoutLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Acesso ao banco para a tabela de treinos. */
public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {

    /** Treinos de um usuário, do mais recente para o mais antigo (empate: maior id primeiro). */
    List<WorkoutLog> findByUserOrderByDateDescIdDesc(User user);

    /**
     * Busca um treino pelo id, mas só se ele pertencer ao usuário informado.
     * Assim ninguém consegue apagar o treino de outra pessoa.
     * Devolve null se não encontrar.
     */
    WorkoutLog findByIdAndUser(Long id, User user);
}
