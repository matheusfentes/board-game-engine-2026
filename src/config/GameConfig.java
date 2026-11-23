package config;

import java.util.*;

/** Modelo do JSON de jogo e regras condicionais que validam seus dados. */
public class GameConfig {
    /** Nome do jogo que seleciona a implementação da engine. */
    public String game;
    /** Dimensões da matriz do tabuleiro. */
    public BoardConfig board;
    /** Convenção usada para interpretar coordenadas. */
    public PositionConfig position;
    /** Quantidade e identificadores dos participantes. */
    public PlayersConfig players;
    /** Tipos de peça e seus atributos declarados. */
    public List<PieceConfig> pieces;
    /** Jogador inicial, ordem dos turnos e limite de tempo. */
    public TurnConfig turn;
    /** Regras de colocação, movimento, captura e encerramento. */
    public RulesConfig rules;
    /** Posições fixas de abertura, usadas pelo Xiangqi. */
    public List<PlacementConfig> initialPosition;

    /** Valida campos comuns e encaminha a validação específica do jogo nomeado. */
    public void validar() {
        required(game, "game");
        if (board == null) throw invalid("board é obrigatório");
        board.validar();
        if (position == null) throw invalid("position é obrigatório");
        position.validar();
        if (players == null) throw invalid("players é obrigatório");
        players.validar();
        if (pieces == null || pieces.isEmpty()) throw invalid("pieces deve conter ao menos uma peça");
        for (int i = 0; i < pieces.size(); i++) {
            if (pieces.get(i) == null) throw invalid("pieces[" + i + "] não pode ser nulo");
            pieces.get(i).validar(i);
        }
        if (turn == null) throw invalid("turn é obrigatório");
        turn.validar(players);
        if (rules == null) throw invalid("rules é obrigatório");
        rules.validar();

        if ("Xiangqi".equalsIgnoreCase(game)) validarXiangqi();
        else if ("Trilha".equalsIgnoreCase(game)) validarTrilha();
        else throw invalid("game não suportado: " + game);
    }

    /** Valida dimensões, peças, referências e regras suportadas pelo Xiangqi. */
    private void validarXiangqi() {
        if (board.rows != 10 || board.columns != 9) throw invalid("Xiangqi exige board.rows=10 e board.columns=9");
        if (!"ROW_COLUMN".equals(position.type) || !Integer.valueOf(0).equals(position.indexBase))
            throw invalid("Xiangqi exige position.type=ROW_COLUMN e position.indexBase=0");
        if (players.count != 2 || !players.symbols.contains("Red") || !players.symbols.contains("Black"))
            throw invalid("Xiangqi exige exatamente os jogadores Red e Black");

        Set<String> supportedPieces = Set.of("general", "guard", "elephant", "horse", "tower", "cannon", "pawn");
        Set<String> pieceNames = new HashSet<>();
        for (int i = 0; i < pieces.size(); i++) {
            PieceConfig piece = pieces.get(i);
            String key = piece.name.toLowerCase(Locale.ROOT);
            if (!supportedPieces.contains(key)) throw invalid("pieces[" + i + "].name não é uma peça Xiangqi suportada: " + piece.name);
            required(piece.symbol, "pieces[" + i + "].symbol");
            if (!expectedMovementRule(key).equals(piece.movementRule))
                throw invalid("pieces[" + i + "].movementRule deve ser " + expectedMovementRule(key) + " para " + piece.name);
            if (!pieceNames.add(key)) throw invalid("peça duplicada: " + piece.name);
        }
        if (initialPosition == null || initialPosition.isEmpty()) throw invalid("Xiangqi exige initialPosition");
        validarInitialPosition();
        rules.validarXiangqi();
    }

    /** Valida dimensões, participantes, fases e regras suportadas pela Trilha. */
    private void validarTrilha() {
        if (board.rows != 7 || board.columns != 7) throw invalid("Trilha exige board.rows=7 e board.columns=7");
        if (!"BOARD_NODE".equals(position.type)) throw invalid("Trilha exige position.type=BOARD_NODE");
        if (players.count != 2 || !players.symbols.equals(List.of("X", "O")))
            throw invalid("Trilha exige dois jogadores, com symbols [\"X\", \"O\"]");
        if (pieces.size() != 1) throw invalid("Trilha exige uma definição de peça por jogador");
        PieceConfig piece = pieces.get(0);
        if (!"Piece".equalsIgnoreCase(piece.name) || piece.quantity != 9 || !piece.perPlayer)
            throw invalid("Trilha exige Piece com quantity=9 e perPlayer=true");
        if (turn.timeLimitSeconds != 0) throw invalid("Trilha não usa turn.timeLimitSeconds; omita ou use 0");
        if (initialPosition != null && !initialPosition.isEmpty()) throw invalid("Trilha inicia sem peças; remova initialPosition");
        rules.validarTrilha();
    }

    /** Confere coordenadas únicas e quantidades da disposição inicial. */
    private void validarInitialPosition() {
        Set<String> squares = new HashSet<>();
        Map<String, Integer> counts = new HashMap<>();
        for (int i = 0; i < initialPosition.size(); i++) {
            PlacementConfig p = initialPosition.get(i);
            if (p == null) throw invalid("initialPosition[" + i + "] não pode ser nulo");
            p.validar(i, this);
            String square = p.row + ":" + p.column;
            if (!squares.add(square)) throw invalid("posição inicial duplicada: " + square);
            counts.merge(p.piece.toLowerCase(Locale.ROOT), 1, Integer::sum);
        }
        for (PieceConfig p : pieces) {
            int expected = p.quantity * players.count;
            if (counts.getOrDefault(p.name.toLowerCase(Locale.ROOT), 0) != expected)
                throw invalid("quantidade de posições iniciais de " + p.name + " deve ser " + expected);
        }
    }

    /** Associa cada peça Xiangqi ao identificador de movimento implementado. */
    private static String expectedMovementRule(String piece) {
        return switch (piece) {
            case "general" -> "ORTHOGONAL_1_IN_PALACE";
            case "guard" -> "DIAGONAL_1_IN_PALACE";
            case "elephant" -> "DIAGONAL_2_ON_HOME_SIDE";
            case "horse" -> "L_SHAPE";
            case "tower", "cannon" -> "ORTHOGONAL";
            case "pawn" -> "FORWARD_1_SIDEWAYS_AFTER_RIVER";
            default -> "";
        };
    }

    /** Rejeita texto obrigatório nulo ou vazio com o nome do campo. */
    public static void required(String value, String field) {
        if (value == null || value.trim().isEmpty()) throw invalid(field + " é obrigatório e não pode ser vazio");
    }
    /** Cria uma exceção uniforme para erros de configuração. */
    public static IllegalArgumentException invalid(String message) { return new IllegalArgumentException("Configuração inválida: " + message); }

    /** Dimensões retangulares declaradas para o tabuleiro. */
    public static class BoardConfig {
        /** Número de linhas. */
        public int rows;
        /** Número de colunas. */
        public int columns;
        /** Garante que linhas e colunas sejam positivas. */
        public void validar() {
            if (rows < 1) throw invalid("board.rows deve ser positivo");
            if (columns < 1) throw invalid("board.columns deve ser positivo");
        }
    }
    /** Convenção de coordenadas usada pelo jogo. */
    public static class PositionConfig {
        /** Identificador do sistema de coordenadas. */
        public String type;
        /** Índice inicial das coordenadas, quando aplicável. */
        public Integer indexBase;
        /** Valida o tipo da posição e a base de índice opcional. */
        public void validar() {
            required(type, "position.type");
            if (indexBase != null && indexBase < 0) throw invalid("position.indexBase não pode ser negativo");
        }
    }
    /** Quantidade de participantes e símbolos que os identificam. */
    public static class PlayersConfig {
        /** Número de jogadores da partida. */
        public int count;
        /** Símbolos que identificam os jogadores na ordem configurada. */
        public List<String> symbols;
        /** Valida quantidade mínima, correspondência e unicidade dos símbolos. */
        public void validar() {
            if (count < 2) throw invalid("players.count deve ser no mínimo 2");
            if (symbols == null || symbols.size() != count) throw invalid("players.symbols deve ter exatamente players.count símbolos");
            Set<String> seen = new HashSet<>();
            for (int i = 0; i < symbols.size(); i++) {
                String symbol = symbols.get(i);
                required(symbol, "players.symbols[" + i + "]");
                if (!seen.add(symbol.toLowerCase(Locale.ROOT))) throw invalid("símbolo de jogador duplicado: " + symbol);
            }
        }
    }
    /** Tipo, símbolo, quantidade e regra de movimento de uma peça. */
    public static class PieceConfig {
        /** Nome reconhecido pelo construtor de peças do jogo. */
        public String name;
        /** Representação textual da peça no tabuleiro. */
        public String symbol;
        /** Regra de movimento declarada para este tipo. */
        public String movementRule;
        /** Quantidade de peças por jogador ou pela partida, conforme o jogo. */
        public int quantity;
        /** Indica que a quantidade se aplica individualmente a cada jogador. */
        public boolean perPlayer;
        /** Valida os campos essenciais e a quantidade da peça. */
        public void validar(int i) {
            required(name, "pieces[" + i + "].name");
            if (symbol != null && symbol.trim().isEmpty()) throw invalid("pieces[" + i + "].symbol não pode ser vazio");
            if (movementRule != null && movementRule.trim().isEmpty()) throw invalid("pieces[" + i + "].movementRule não pode ser vazio");
            if (quantity < 1) throw invalid("pieces[" + i + "].quantity deve ser positivo");
        }
    }
    /** Ordem de turnos, jogador inicial e limite de tempo. */
    public static class TurnConfig {
        /** Símbolo do jogador que começa a partida. */
        public String firstPlayer;
        /** Política de alternância dos turnos. */
        public String order;
        /** Tempo máximo em segundos; zero indica partida sem relógio. */
        public int timeLimitSeconds;
        /** Valida jogador inicial, alternância e duração não negativa. */
        public void validar(PlayersConfig players) {
            required(firstPlayer, "turn.firstPlayer");
            required(order, "turn.order");
            if (!players.symbols.contains(firstPlayer)) throw invalid("turn.firstPlayer deve existir em players.symbols");
            if (!"ALTERNATING".equals(order)) throw invalid("turn.order não suportado: " + order);
            if (timeLimitSeconds < 0) throw invalid("turn.timeLimitSeconds não pode ser negativo");
        }
    }
    /** Regras de posicionamento, movimento, captura, vitória e empate. */
    public static class RulesConfig {
        /** Regra usada para posicionar peças no início. */
        public String placement;
        /** Regras de movimento declaradas no arquivo JSON. */
        public List<String> movementRule;
        /** Restrições de captura declaradas no arquivo JSON. */
        public List<String> captureRule;
        /** Condições para vitória normal e vitória adicional. */
        public WinConditionConfig winCondition, additionalWinCondition;
        /** Condição de empate e seus parâmetros. */
        public DrawConditionConfig drawCondition;
        /** Valida a presença e a estrutura dos grupos de regras. */
        public void validar() {
            required(placement, "rules.placement");
            if (movementRule == null || movementRule.isEmpty()) throw invalid("rules.movementRule deve conter regras");
            if (captureRule == null || captureRule.isEmpty()) throw invalid("rules.captureRule deve conter regras");
            if (winCondition == null) throw invalid("rules.winCondition é obrigatório");
            winCondition.validar("rules.winCondition");
            if (drawCondition == null) throw invalid("rules.drawCondition é obrigatório");
            drawCondition.validar();
        }
        /** Confere se o Xiangqi declara apenas regras implementadas. */
        public void validarXiangqi() {
            if (!"FIXED".equals(placement)) throw invalid("Xiangqi exige rules.placement=FIXED");
            validateList(movementRule, Set.of("PATH_MUST_BE_CLEAR", "GENERAL_MUST_STAY_SAFE"), "rules.movementRule");
            validateList(captureRule, Set.of("OPPONENT_ONLY", "CANNON_REQUIRES_ONE_SCREEN"), "rules.captureRule");
            if (!captureRule.contains("OPPONENT_ONLY")) throw invalid("Xiangqi exige rules.captureRule OPPONENT_ONLY");
            if (!"CHECKMATE_OR_NO_LEGAL_MOVE".equals(winCondition.type)) throw invalid("rules.winCondition.type Xiangqi não suportado: " + winCondition.type);
            if (!"ONLY_DEFENSIVE_PIECES_REMAIN".equals(drawCondition.type)) throw invalid("rules.drawCondition.type Xiangqi não suportado: " + drawCondition.type);
        }
        /** Confere se a Trilha declara apenas regras implementadas. */
        public void validarTrilha() {
            if (!"EMPTY_POSITION".equals(placement)) throw invalid("Trilha exige rules.placement=EMPTY_POSITION");
            validateList(movementRule, Set.of("ADJACENT", "FLY_WITH_3_PIECES"), "rules.movementRule");
            validateList(captureRule, Set.of("AFTER_MILL", "PROTECTED_UNLESS_ALL_OPPONENT_PIECES_IN_MILLS"), "rules.captureRule");
            if (!movementRule.contains("ADJACENT")) throw invalid("Trilha exige rules.movementRule ADJACENT");
            if (!captureRule.contains("AFTER_MILL")) throw invalid("Trilha exige rules.captureRule AFTER_MILL");
            if (!"OPPONENT_HAS_LESS_THAN".equals(winCondition.type) || winCondition.quantity != 3)
                throw invalid("Trilha exige winCondition OPPONENT_HAS_LESS_THAN quantity=3");
            if (additionalWinCondition == null) throw invalid("Trilha exige additionalWinCondition");
            additionalWinCondition.validar("rules.additionalWinCondition");
            if (!"OPPONENT_HAS_NO_LEGAL_MOVE".equals(additionalWinCondition.type))
                throw invalid("Trilha exige additionalWinCondition OPPONENT_HAS_NO_LEGAL_MOVE");
            if (!"NO_CAPTURE_FOR_TURNS_WITH_3_PIECES".equals(drawCondition.type) || drawCondition.turns < 1)
                throw invalid("Trilha exige drawCondition NO_CAPTURE_FOR_TURNS_WITH_3_PIECES com turns positivo");
        }
        /** Verifica se cada valor é suportado e não aparece duplicado. */
        private static void validateList(List<String> values, Set<String> supported, String field) {
            Set<String> seen = new HashSet<>();
            for (int i = 0; i < values.size(); i++) {
                required(values.get(i), field + "[" + i + "]");
                if (!supported.contains(values.get(i))) throw invalid(field + " não suportada: " + values.get(i));
                if (!seen.add(values.get(i))) throw invalid(field + " contém regra duplicada: " + values.get(i));
            }
        }
    }
    /** Condição de vitória e seu parâmetro numérico opcional. */
    public static class WinConditionConfig {
        /** Identificador da condição de vitória. */
        public String type;
        /** Limite numérico usado por condições como peças restantes. */
        public int quantity;
        /** Exige um identificador de condição de vitória. */
        public void validar(String field) { required(type, field + ".type"); }
    }
    /** Condição de empate e seu limite de turnos opcional. */
    public static class DrawConditionConfig {
        /** Identificador da condição de empate. */
        public String type;
        /** Número de turnos sem captura permitido pela condição. */
        public int turns;
        /** Exige um identificador de condição de empate. */
        public void validar() { required(type, "rules.drawCondition.type"); }
    }
    /** Colocação inicial de uma peça Xiangqi em uma casa e lado. */
    public static class PlacementConfig {
        /** Símbolo do jogador dono da peça. */
        public String player;
        /** Nome do tipo de peça colocado. */
        public String piece;
        /** Linha inicial da peça. */
        public int row;
        /** Coluna inicial da peça. */
        public int column;
        /** Valida jogador, peça referenciada e coordenadas dentro do tabuleiro. */
        public void validar(int i, GameConfig c) {
            required(player, "initialPosition[" + i + "].player");
            required(piece, "initialPosition[" + i + "].piece");
            if (!c.players.symbols.contains(player)) throw invalid("initialPosition[" + i + "].player não existe: " + player);
            if (c.pieces.stream().noneMatch(p -> p.name.equalsIgnoreCase(piece))) throw invalid("initialPosition[" + i + "].piece não existe: " + piece);
            if (row < 0 || row >= c.board.rows || column < 0 || column >= c.board.columns) throw invalid("initialPosition[" + i + "] fora do tabuleiro");
        }
    }
}
