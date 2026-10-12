// Projeto desenvolvido com auxílio de IA (Claude, da Anthropic).
//
// Formulário "Treino do dia": cada exercício tem um switch.
// Os campos de séries e repetições só ficam ativos enquanto o switch está marcado.

function setupExerciseSwitches() {
    const switches = document.querySelectorAll('.exercise-check');

    for (let i = 0; i < switches.length; i++) {
        switches[i].addEventListener('change', onExerciseSwitchChanged);
    }
}

function onExerciseSwitchChanged(event) {
    const exerciseSwitch = event.target;

    // A linha (bloco) do exercício a que esse switch pertence
    const row = exerciseSwitch.closest('.exercise-row');
    const inputs = row.querySelectorAll('.sets-input, .reps-input');

    for (let i = 0; i < inputs.length; i++) {
        setInputActive(inputs[i], exerciseSwitch.checked);
    }
}

// Campo ativo: habilitado e obrigatório. Campo inativo: desabilitado e vazio.
// Campos desabilitados NÃO são enviados ao servidor.
function setInputActive(input, isActive) {
    input.disabled = !isActive;
    input.required = isActive;

    if (!isActive) {
        input.value = '';
    }
}

setupExerciseSwitches();
