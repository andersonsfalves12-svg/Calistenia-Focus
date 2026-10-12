// Projeto desenvolvido com auxílio de IA (Claude, da Anthropic).
//
// Gráfico de evolução (Chart.js): volume total de treino por dia.
// O gráfico "atualiza sozinho": cada treino adicionado ou excluído recarrega a página,
// o controller recalcula os volumes e esta função desenha o gráfico de novo.

const LINE_COLOR = '#0d6efd';
const FILL_COLOR = 'rgba(13, 110, 253, 0.2)';
const AXIS_TEXT_COLOR = '#adb5bd';
const GRID_COLOR = 'rgba(255,255,255,0.08)';
const LEGEND_TEXT_COLOR = '#dee2e6';

// labels: datas (eixo X) | values: volume de cada dia (eixo Y)
function drawEvolutionChart(labels, values) {
    const canvas = document.getElementById('evolutionChart');

    // Sem canvas (página sem dados) ou sem valores: não há o que desenhar
    if (canvas === null || labels.length === 0) {
        return;
    }

    new Chart(canvas, {
        type: 'line',
        data: buildChartData(labels, values),
        options: buildChartOptions()
    });
}

function buildChartData(labels, values) {
    return {
        labels: labels,
        datasets: [{
            label: 'Volume total (séries × repetições)',
            data: values,
            borderColor: LINE_COLOR,
            backgroundColor: FILL_COLOR,
            fill: true,          // preenche a área abaixo da linha
            tension: 0.3,        // suaviza a curva
            pointRadius: 5       // tamanho das bolinhas
        }]
    };
}

function buildChartOptions() {
    const xAxis = buildAxisOptions();
    const yAxis = buildAxisOptions();
    yAxis.beginAtZero = true;  // o eixo Y sempre começa em 0

    return {
        responsive: true,            // acompanha o tamanho da tela
        maintainAspectRatio: false,  // usa a altura definida no CSS (.chart-container)
        scales: { x: xAxis, y: yAxis },
        plugins: { legend: { labels: { color: LEGEND_TEXT_COLOR } } }
    };
}

// Cores claras nos eixos para combinar com o tema escuro
function buildAxisOptions() {
    return {
        ticks: { color: AXIS_TEXT_COLOR },
        grid: { color: GRID_COLOR }
    };
}
