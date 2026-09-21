public class Board {
    // Tabuleiro do jogo: 10 linhas por 9 colunas
    private Piece[][] tabuleiro = new Piece[10][9];
    private Player jogador1;
    private Player jogador2;

    public Board(Player jogador1, Player jogador2) {
        this.jogador1 = jogador1;
        this.jogador2 = jogador2;
        inicializarTabuleiro();
    }

    // Coloca todas as peças no lugar pra começar a partida
    private void inicializarTabuleiro() {
        // Lado de cima (Preto)
        montarLado(jogador2, 0, 2, 3);

        // Lado de baixo (Vermelho)
        montarLado(jogador1, 9, 7, 6);
    }

    // Cria e arruma as peças de um dos lados do tabuleiro
    private void montarLado(Player jogador, int linhaPrincipal, int linhaCanhao, int linhaPeão) {
        // Ordem das peças na primeira linha
        String[] nomesPecas = {
            "Torre", "Cavalo", "Elefante", "Guarda", "General",
            "Guarda", "Elefante", "Cavalo", "Torre"
        };

        // Instancia a fileira do fundo
        for (int x = 0; x < 9; x++) {
            criarEPosicionarPeca(nomesPecas[x], jogador, x, linhaPrincipal);
        }

        // Os dois canhões
        criarEPosicionarPeca("Canhão", jogador, 1, linhaCanhao);
        criarEPosicionarPeca("Canhão", jogador, 7, linhaCanhao);

        // Os 5 peões intercalados
        for (int x = 0; x < 9; x += 2) {
            criarEPosicionarPeca("Peão", jogador, x, linhaPeão);
        }
    }

    // Checa onde a peça tá nascendo, cria as regras e põe no tabuleiro
    private void criarEPosicionarPeca(String tipo, Player jogador, int x, int y) {
        // Checa se tá na região do rio (linhas 4 e 5)
        boolean noRio = (y == 4 || y == 5);

        // Checa se tá na área do palácio
        boolean noPalacio = (x >= 3 && x <= 5) && (y <= 2 || y >= 7);

        RuleSet regras = new RuleSet(false, noRio, noPalacio);
        Piece peca = new Piece(tipo, regras, jogador, x, y);

        this.tabuleiro[y][x] = peca;
    }

    // Passa a peça de uma coordenada pra outra
    public void moverPeca(int origemX, int origemY, int destinoX, int destinoY) {
        Piece peca = tabuleiro[origemY][origemX];

        // Se não tem peça na casinha onde apertou, não faz nada
        if (peca == null) return;

        // Se já tinha peça no destino, come
        Piece pecaDestino = tabuleiro[destinoY][destinoX];
        if (pecaDestino != null) {
            pecaDestino.capturedPiece();
        }

        // Atualiza a posição da peça e limpa a casa antiga
        peca.moveTo(destinoX, destinoY);
        tabuleiro[destinoY][destinoX] = peca;
        tabuleiro[origemY][origemX] = null;
    }

    // Pega a peça em uma posição específica
    public Piece getPeca(int x, int y) {
        return tabuleiro[y][x];
    }
}