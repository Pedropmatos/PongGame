PingPong
Bem-vindo ao PingPong! Um clássico jogo de Pong recriado com novas funcionalidades e diferentes fases para desafiar suas habilidades. Este projeto foi desenvolvido com a biblioteca libGDX.

Sobre o Jogo
Este não é um Pong qualquer. Desafie um amigo em três níveis de dificuldade diferentes, cada um com suas próprias características e desafios. O jogo começa com um menu principal, seguido por uma tela de seleção de níveis, e então a ação começa!

Níveis
Nível 1 (Padrão): A experiência clássica do Pong. Ideal para aquecer e para novos jogadores.

Nível 2 (Floresta): Desvie não só da bola, mas também de folhas que caem pela tela e podem alterar a trajetória da bola, tornando a partida imprevisível.

Nível 3 (Espaço): Prepare-se para a velocidade! Neste nível, a bola é mais rápida, exigindo reflexos apurados em um cenário espacial.

Como Jogar
O objetivo é simples: rebater a bola e fazer com que seu oponente não consiga devolvê-la. O primeiro jogador a atingir a pontuação máxima do nível vence a partida.

Controles
Jogador 1 (Esquerda):

W - Mover para cima

S - Mover para baixo

Jogador 2 (Direita):

Seta para Cima - Mover para cima

Seta para Baixo - Mover para baixo

(Os controles são baseados no arquivo GameScreen.java).

Plataformas Suportadas
Desktop (LWJGL3): Jogue em seu computador Windows, macOS ou Linux.

Como Executar o Projeto
Este projeto utiliza o Gradle para gerenciar suas dependências e compilação. Você pode usar os seguintes comandos no terminal, na raiz do projeto:

Executar o jogo:

No Windows: gradlew.bat lwjgl3:run

No macOS/Linux: ./gradlew lwjgl3:run

Criar um arquivo .jar executável:

Execute gradlew.bat lwjgl3:jar (Windows) ou ./gradlew lwjgl3:jar (macOS/Linux). O arquivo será gerado na pasta lwjgl3/build/libs.

Outros Comandos Gradle Úteis
clean: Remove as pastas build com os arquivos compilados.

idea: Gera os arquivos de projeto para o IntelliJ IDEA.

--refresh-dependencies: Força a validação e o download das dependências.
