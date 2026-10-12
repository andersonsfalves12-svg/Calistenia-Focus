# Calistenia Focus
#### Video Demo: https://youtu.be/yvZdMC8UvDY?si=VInAaZZf2ZGmsrsW
#### Description:

**Calistenia Focus** é uma aplicação web para quem treina calistenia e quer acompanhar a própria evolução. Cada pessoa cria uma conta, registra os exercícios que fez em cada treino e vê um gráfico com o volume de treino ao longo dos dias. A ideia nasceu de uma necessidade pessoal: eu treino calistenia e queria uma ferramenta simples, sem planilhas e sem distrações, em que o progresso ficasse visível. O projeto foi feito em Java 17 com Spring Boot 3, Spring Data JPA, banco de dados H2 em memória, Thymeleaf para gerar as páginas, Bootstrap 5 (tema escuro) para o visual e Chart.js para o gráfico. O Maven gerencia as dependências.

## O que o aplicativo faz

- **Cadastro:** a pessoa escolhe um nome de usuário (mínimo de 3 caracteres) e uma senha (mínimo de 4), e confirma a senha. Não é possível ter dois usuários com o mesmo nome.
- **Login:** o site compara a senha digitada com a guardada. Em caso de erro, mostra sempre "Usuário ou senha inválidos", sem revelar se o usuário existe.
- **Painel:** o formulário "Treino do dia" lista cinco exercícios: Flexões, Barra Fixa, Abdominais, Agachamentos e Fundos em Paralelas. A pessoa marca um ou mais, informa séries e repetições de cada um (de 1 a 1000) e a data (hoje, por padrão). Tudo é salvo de uma vez.
- **Gráfico e histórico:** o gráfico mostra o volume de treino por dia, que é séries × repetições somado entre todos os exercícios daquela data. Abaixo fica o histórico, do registro mais recente para o mais antigo, e cada registro pode ser excluído.
- **Segurança básica:** as rotas do painel exigem login, e cada pessoa só enxerga e apaga os próprios treinos.

## Arquivos do projeto

O código Java fica em `src/main/java/com/calistenia/tracker`, dividido em pastas por responsabilidade.

- `pom.xml`: configura o Maven e lista as dependências (Spring Web, Spring Data JPA, H2, Thymeleaf).
- `application.properties`: porta 8080, conexão com o banco H2 em memória e a opção que cria as tabelas automaticamente a partir das classes.
- `CalisteniaFocusApplication.java`: classe principal, que inicia o Spring Boot.
- `model/User.java`: entidade do usuário, com nome único e o hash da senha.
- `model/WorkoutLog.java`: entidade de um registro de treino (usuário, data, exercício, séries e repetições). Também sabe calcular o próprio volume.
- `repository/UserRepository.java`: busca usuários pelo nome.
- `repository/WorkoutLogRepository.java`: lista os treinos de um usuário em ordem decrescente de data e busca um treino pelo id apenas se pertencer ao usuário, o que impede apagar o treino de outra pessoa.
- `service/PasswordHasher.java`: gera o hash SHA-256 da senha e confere se uma senha digitada bate com o hash guardado.
- `service/ExerciseCatalog.java`: a lista oficial dos cinco exercícios, com chave e nome para mostrar.
- `service/VolumeCalculator.java`: soma séries × repetições de cada dia, em ordem cronológica, para alimentar o gráfico.
- `controller/HomeController.java`: redireciona a página inicial para o login.
- `controller/AuthController.java`: cadastro, login e logout, com as regras de validação do cadastro.
- `controller/DashboardController.java`: mostra o painel, valida e salva os treinos e exclui registros.
- `controller/UserSession.java`: guarda e consulta quem está logado na sessão HTTP, em um só lugar.
- `controller/Pages.java` e `controller/MessageKeys.java`: constantes com os nomes das páginas, dos redirecionamentos e das mensagens, para não repetir textos soltos.
- `templates/login.html`, `register.html` e `dashboard.html`: as três páginas do site.
- `templates/fragments/layout.html`: cabeçalho e área de mensagens compartilhados pelas três páginas.
- `static/css/app.css`: os poucos estilos próprios, além do Bootstrap.
- `static/js/exercise-form.js`: ativa os campos de séries e repetições quando o exercício é marcado.
- `static/js/evolution-chart.js`: desenha o gráfico de evolução com o Chart.js.
- `.gitignore`: evita enviar a pasta `target`, gerada na compilação.

## Decisões de projeto

- **HttpSession em vez de Spring Security.** O Spring Security seria mais completo, mas é bem mais complexo. Com a sessão, guardo o usuário logado e protejo as rotas de um jeito que consigo entender e explicar.
- **Hash SHA-256 com prefixo fixo.** Nunca guardo a senha em texto puro. Sei que um SHA-256 simples, sem sal individual, não é o ideal para produção (o recomendado seria bcrypt), mas foi uma escolha consciente para um projeto didático.
- **Banco H2 em memória.** Não exige instalar nada, então o projeto roda em qualquer computador. O custo é que os dados somem quando a aplicação reinicia.
- **Volume = séries × repetições.** É uma medida fácil de entender e suficiente para mostrar a evolução ao longo do tempo.
- **Treino com vários exercícios.** Em vez de um formulário por exercício, o formulário recebe vários de uma vez, porque é assim que um treino acontece. Por isso o controller lê os campos `sets_<exercício>` e `reps_<exercício>` pelo nome, e ignora qualquer exercício que não esteja na lista oficial.
- **Código limpo e em estilo simples.** Cada classe tem uma responsabilidade (por exemplo, só o `PasswordHasher` mexe com hash), os métodos são curtos e têm nomes que dizem o que fazem, e os números e textos fixos viram constantes com nome. Usei `for`, `if/else` e métodos pequenos, sem recursos avançados, e os repositórios devolvem `null` quando não acham nada. Os nomes no código estão em inglês e os comentários em português.
- **JavaScript e estilos em arquivos próprios.** Tirei o JavaScript e o CSS de dentro das páginas e coloquei em `static/`. As páginas ficaram só com a estrutura, e o cabeçalho comum fica em um único fragmento do Thymeleaf.

## Limitações e melhorias futuras

O logout é feito por uma requisição GET e não há proteção contra CSRF. Não há páginas de erro personalizadas nem validação de datas absurdas, e a lista de exercícios é fixa. Os dados não persistem entre execuções. Como melhorias, penso em um banco persistente, bcrypt para as senhas, logout por POST, exercícios personalizados e edição de registros.

## Como executar

Com o Java 17 e o Maven instalados, entre na pasta do projeto e rode `mvn spring-boot:run`. Depois, abra http://localhost:8080. No IntelliJ, basta abrir a pasta e rodar a classe `CalisteniaFocusApplication`. O gráfico usa o Chart.js e o visual usa o Bootstrap, ambos carregados por CDN, então é preciso ter internet.

## Uso de IA

O Claude (Anthropic) escreveu a maior parte do código a partir das minhas ideias e instruções, e também reorganizou o projeto em código limpo (nomes claros e métodos curtos). Os comentários no topo de cada arquivo indicam esse uso. Eu defini o tema e as funções do aplicativo, testei o projeto no IntelliJ e no VS Code, relatei os problemas que encontrei e estudei o código para explicá-lo. 
