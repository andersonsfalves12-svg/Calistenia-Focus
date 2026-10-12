// Projeto desenvolvido com auxílio de IA (Claude, da Anthropic).
package com.calistenia.tracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Um exercício feito em um dia por um usuário.
 * Exemplo: 2026-05-10 | Flexões | 4 séries | 12 repetições.
 * Cada objeto vira uma linha na tabela "workout_logs".
 */
@Entity
@Table(name = "workout_logs")
public class WorkoutLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muitos registros pertencem a um usuário. Cria a coluna "user_id".
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String exerciseName;

    @Column(nullable = false)
    private int sets;

    @Column(nullable = false)
    private int reps;

    /** Construtor vazio: o JPA precisa dele para criar objetos lidos do banco. */
    protected WorkoutLog() {
    }

    public WorkoutLog(User user, LocalDate date, String exerciseName, int sets, int reps) {
        this.user = user;
        this.date = date;
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.reps = reps;
    }

    /** O volume do exercício é o total de repetições: séries x repetições. */
    public int calculateVolume() {
        return sets * reps;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public int getSets() {
        return sets;
    }

    public int getReps() {
        return reps;
    }
}
