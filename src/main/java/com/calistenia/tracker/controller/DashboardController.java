// Projeto desenvolvido com auxílio de IA (Claude, da Anthropic).
package com.calistenia.tracker.controller;

import com.calistenia.tracker.model.User;
import com.calistenia.tracker.model.WorkoutLog;
import com.calistenia.tracker.repository.WorkoutLogRepository;
import com.calistenia.tracker.service.ExerciseCatalog;
import com.calistenia.tracker.service.VolumeCalculator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controla o painel principal: mostra o formulário, o gráfico e o histórico,
 * registra treinos com vários exercícios de uma vez e exclui registros.
 * Todas as rotas começam com /dashboard e exigem usuário logado.
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private static final int MIN_AMOUNT = 1;
    private static final int MAX_AMOUNT = 1000;

    // Prefixos dos campos do formulário: o campo de séries da flexão é "sets_flexoes"
    private static final String SETS_FIELD_PREFIX = "sets_";
    private static final String REPS_FIELD_PREFIX = "reps_";
    private static final String SELECTED_EXERCISES_FIELD = "exercises";

    private static final String CHART_DATE_PATTERN = "dd/MM/yyyy";

    private static final String MSG_SELECT_EXERCISE = "Selecione pelo menos um exercício.";
    private static final String MSG_NO_VALID_EXERCISE = "Nenhum exercício válido foi enviado.";
    private static final String MSG_INVALID_AMOUNT = "Informe séries e repetições válidas para: ";
    private static final String MSG_LOG_DELETED = "Registro excluído.";
    private static final String MSG_LOG_NOT_FOUND = "Registro não encontrado.";

    private final WorkoutLogRepository workoutLogRepository;
    private final ExerciseCatalog exerciseCatalog;
    private final VolumeCalculator volumeCalculator;
    private final UserSession userSession;

    // O Spring entrega os quatro objetos prontos (injeção de dependência)
    public DashboardController(WorkoutLogRepository workoutLogRepository,
                               ExerciseCatalog exerciseCatalog,
                               VolumeCalculator volumeCalculator,
                               UserSession userSession) {
        this.workoutLogRepository = workoutLogRepository;
        this.exerciseCatalog = exerciseCatalog;
        this.volumeCalculator = volumeCalculator;
        this.userSession = userSession;
    }

    @GetMapping
    public String showDashboard(HttpSession session, Model model) {
        User user = userSession.getLoggedUser(session);
        if (user == null) {
            return Pages.REDIRECT_TO_LOGIN;
        }

        List<WorkoutLog> logs = workoutLogRepository.findByUserOrderByDateDescIdDesc(user);

        model.addAttribute("username", user.getUsername());
        model.addAttribute("exercises", exerciseCatalog.getAll());
        model.addAttribute("logs", logs);
        model.addAttribute("today", LocalDate.now());
        addChartData(model, logs);

        return Pages.DASHBOARD;
    }

    @PostMapping("/add-session")
    public String addSession(HttpSession session,
                             HttpServletRequest request,
                             @RequestParam(value = "date", required = false) String dateText,
                             RedirectAttributes redirect) {

        User user = userSession.getLoggedUser(session);
        if (user == null) {
            return Pages.REDIRECT_TO_LOGIN;
        }

        String[] selectedKeys = request.getParameterValues(SELECTED_EXERCISES_FIELD);
        if (selectedKeys == null || selectedKeys.length == 0) {
            return redirectWithError(redirect, MSG_SELECT_EXERCISE);
        }

        List<String> exerciseKeys = keepKnownExercises(selectedKeys);
        if (exerciseKeys.isEmpty()) {
            return redirectWithError(redirect, MSG_NO_VALID_EXERCISE);
        }

        String invalidExerciseName = findExerciseWithInvalidAmount(request, exerciseKeys);
        if (invalidExerciseName != null) {
            return redirectWithError(redirect, MSG_INVALID_AMOUNT + invalidExerciseName);
        }

        LocalDate day = parseDateOrToday(dateText);
        List<WorkoutLog> newLogs = createLogs(user, day, request, exerciseKeys);
        workoutLogRepository.saveAll(newLogs);

        redirect.addFlashAttribute(MessageKeys.SUCCESS, newLogs.size() + " exercício(s) registrado(s)!");
        return Pages.REDIRECT_TO_DASHBOARD;
    }

    @PostMapping("/delete/{id}")
    public String deleteLog(@PathVariable("id") Long id,
                            HttpSession session,
                            RedirectAttributes redirect) {

        User user = userSession.getLoggedUser(session);
        if (user == null) {
            return Pages.REDIRECT_TO_LOGIN;
        }

        // Só encontra o registro se ele for do usuário logado
        WorkoutLog log = workoutLogRepository.findByIdAndUser(id, user);
        if (log == null) {
            return redirectWithError(redirect, MSG_LOG_NOT_FOUND);
        }

        workoutLogRepository.delete(log);
        redirect.addFlashAttribute(MessageKeys.SUCCESS, MSG_LOG_DELETED);
        return Pages.REDIRECT_TO_DASHBOARD;
    }

    /** Envia ao template as datas (eixo X) e os volumes (eixo Y) do gráfico. */
    private void addChartData(Model model, List<WorkoutLog> logs) {
        Map<LocalDate, Integer> volumeByDay = volumeCalculator.calculateByDay(logs);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CHART_DATE_PATTERN);

        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        for (Map.Entry<LocalDate, Integer> entry : volumeByDay.entrySet()) {
            labels.add(entry.getKey().format(formatter));
            values.add(entry.getValue());
        }

        model.addAttribute("chartLabels", labels);
        model.addAttribute("chartValues", values);
    }

    /** Ignora chaves que não existem na lista oficial de exercícios. */
    private List<String> keepKnownExercises(String[] selectedKeys) {
        List<String> knownKeys = new ArrayList<>();

        for (String key : selectedKeys) {
            if (exerciseCatalog.contains(key)) {
                knownKeys.add(key);
            }
        }

        return knownKeys;
    }

    /** Devolve o nome do primeiro exercício com séries ou repetições inválidas, ou null se todos estão certos. */
    private String findExerciseWithInvalidAmount(HttpServletRequest request, List<String> exerciseKeys) {
        for (String key : exerciseKeys) {
            boolean validSets = isValidAmount(request.getParameter(SETS_FIELD_PREFIX + key));
            boolean validReps = isValidAmount(request.getParameter(REPS_FIELD_PREFIX + key));

            if (!validSets || !validReps) {
                return exerciseCatalog.getName(key);
            }
        }
        return null;
    }

    /** Um valor é válido se for um número inteiro entre MIN_AMOUNT e MAX_AMOUNT. */
    private boolean isValidAmount(String text) {
        try {
            int amount = Integer.parseInt(text);
            return amount >= MIN_AMOUNT && amount <= MAX_AMOUNT;
        } catch (NumberFormatException e) {
            return false; // vazio, nulo ou não é número
        }
    }

    private List<WorkoutLog> createLogs(User user, LocalDate day,
                                        HttpServletRequest request, List<String> exerciseKeys) {
        List<WorkoutLog> logs = new ArrayList<>();

        for (String key : exerciseKeys) {
            logs.add(createLog(user, day, request, key));
        }

        return logs;
    }

    /** Monta o registro de um exercício. Os valores já foram validados antes. */
    private WorkoutLog createLog(User user, LocalDate day, HttpServletRequest request, String key) {
        int sets = Integer.parseInt(request.getParameter(SETS_FIELD_PREFIX + key));
        int reps = Integer.parseInt(request.getParameter(REPS_FIELD_PREFIX + key));
        String exerciseName = exerciseCatalog.getName(key);

        return new WorkoutLog(user, day, exerciseName, sets, reps);
    }

    /** Converte o texto da data (formato yyyy-MM-dd). Se estiver vazio ou inválido, usa hoje. */
    private LocalDate parseDateOrToday(String dateText) {
        if (dateText == null || dateText.isEmpty()) {
            return LocalDate.now();
        }

        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            return LocalDate.now();
        }
    }

    private String redirectWithError(RedirectAttributes redirect, String message) {
        redirect.addFlashAttribute(MessageKeys.ERROR, message);
        return Pages.REDIRECT_TO_DASHBOARD;
    }
}
