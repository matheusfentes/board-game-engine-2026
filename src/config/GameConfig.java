package config;

import java.util.*;

/** Data-only model of a game description loaded from JSON. */
public class GameConfig {
    public String game;
    public BoardConfig board;
    public PositionConfig position;
    public PlayersConfig players;
    public List<PieceConfig> pieces;
    public TurnConfig turn;
    public RulesConfig rules;
    public List<PlacementConfig> initialPosition;

    public void validar() {
        required(game, "game");
        if (!"Xiangqi".equalsIgnoreCase(game)) throw invalid("game não suportado pela engine atual: " + game);
        if (board == null) throw invalid("board é obrigatório");
        board.validar();
        if ("Xiangqi".equalsIgnoreCase(game) && (board.rows != 10 || board.columns != 9))
            throw invalid("Xiangqi exige board.rows=10 e board.columns=9");
        if (position == null) throw invalid("position é obrigatório");
        position.validar();
        if ("Xiangqi".equalsIgnoreCase(game)
                && (!"ROW_COLUMN".equals(position.type) || !Integer.valueOf(0).equals(position.indexBase)))
            throw invalid("Xiangqi exige position.type=ROW_COLUMN e position.indexBase=0");
        if (players == null) throw invalid("players é obrigatório");
        players.validar();
        if ("Xiangqi".equalsIgnoreCase(game) && (players.count != 2 || !players.symbols.contains("Red") || !players.symbols.contains("Black")))
            throw invalid("Xiangqi exige exatamente os jogadores Red e Black");
        if (pieces == null || pieces.isEmpty()) throw invalid("pieces deve conter ao menos uma peça");
        Set<String> pieceNames = new HashSet<>();
        for (int i = 0; i < pieces.size(); i++) {
            PieceConfig p = pieces.get(i);
            if (p == null) throw invalid("pieces[" + i + "] não pode ser nulo");
            p.validar(i);
            if ("Xiangqi".equalsIgnoreCase(game) && !Set.of("general", "guard", "elephant", "horse", "tower", "cannon", "pawn").contains(p.name.toLowerCase(Locale.ROOT)))
                throw invalid("pieces[" + i + "].name não é uma peça Xiangqi suportada: " + p.name);
            if ("Xiangqi".equalsIgnoreCase(game) && !expectedMovementRule(p.name).equals(p.movementRule))
                throw invalid("pieces[" + i + "].movementRule deve ser " + expectedMovementRule(p.name) + " para " + p.name);
            if (!pieceNames.add(p.name.toLowerCase(Locale.ROOT))) throw invalid("peça duplicada: " + p.name);
        }
        if (turn == null) throw invalid("turn é obrigatório");
        turn.validar(players);
        if (rules == null) throw invalid("rules é obrigatório");
        rules.validar();
        rules.validarParaXiangqi();
        if (initialPosition == null || initialPosition.isEmpty()) throw invalid("initialPosition deve definir as posições iniciais");
        Map<String, Integer> counts = new HashMap<>();
        Set<String> squares = new HashSet<>();
        for (int i = 0; i < initialPosition.size(); i++) {
            PlacementConfig p = initialPosition.get(i);
            if (p == null) throw invalid("initialPosition[" + i + "] não pode ser nulo");
            p.validar(i, this);
            counts.merge(p.piece.toLowerCase(Locale.ROOT), 1, Integer::sum);
            String square = p.row + ":" + p.column;
            if (!squares.add(square)) throw invalid("posição inicial duplicada: " + square);
        }
        for (PieceConfig p : pieces) {
            if (counts.getOrDefault(p.name.toLowerCase(Locale.ROOT), 0) != p.quantity * players.count)
                throw invalid("quantidade de posições iniciais de " + p.name + " deve ser " + (p.quantity * players.count));
        }
    }

    public static void required(String value, String field) {
        if (value == null || value.trim().isEmpty()) throw invalid(field + " é obrigatório e não pode ser vazio");
    }
    public static IllegalArgumentException invalid(String message) { return new IllegalArgumentException("Configuração inválida: " + message); }

    private static String expectedMovementRule(String piece) {
        return switch (piece.toLowerCase(Locale.ROOT)) {
            case "general" -> "ORTHOGONAL_1_IN_PALACE";
            case "guard" -> "DIAGONAL_1_IN_PALACE";
            case "elephant" -> "DIAGONAL_2_ON_HOME_SIDE";
            case "horse" -> "L_SHAPE";
            case "tower" -> "ORTHOGONAL";
            case "cannon" -> "ORTHOGONAL";
            case "pawn" -> "FORWARD_1_SIDEWAYS_AFTER_RIVER";
            default -> "";
        };
    }

    public static class BoardConfig {
        public int rows, columns;
        public void validar() { if (rows < 1) throw invalid("board.rows deve ser positivo"); if (columns < 1) throw invalid("board.columns deve ser positivo"); }
    }
    public static class PositionConfig {
        public String type;
        public Integer indexBase;
        public void validar() { required(type, "position.type"); if (indexBase != null && indexBase < 0) throw invalid("position.indexBase não pode ser negativo"); }
    }
    public static class PlayersConfig {
        public int count;
        public List<String> symbols;
        public void validar() {
            if (count < 2) throw invalid("players.count deve ser no mínimo 2");
            if (symbols == null || symbols.size() != count) throw invalid("players.symbols deve ter exatamente players.count símbolos");
            Set<String> seen = new HashSet<>();
            for (int i=0;i<symbols.size();i++) { String s=symbols.get(i); required(s,"players.symbols["+i+"]"); if (!seen.add(s.toLowerCase(Locale.ROOT))) throw invalid("símbolo de jogador duplicado: "+s); }
        }
    }
    public static class PieceConfig {
        public String name, symbol, movementRule;
        public int quantity;
        public void validar(int i) { required(name,"pieces["+i+"].name"); required(symbol,"pieces["+i+"].symbol"); required(movementRule,"pieces["+i+"].movementRule"); if(quantity<1) throw invalid("pieces["+i+"].quantity deve ser positivo"); }
    }
    public static class TurnConfig {
        public String firstPlayer, order;
        public int timeLimitSeconds;
        public void validar(PlayersConfig players) {
            required(firstPlayer,"turn.firstPlayer"); required(order,"turn.order");
            if (!players.symbols.contains(firstPlayer)) throw invalid("turn.firstPlayer deve corresponder a um símbolo de players.symbols");
            if (!"ALTERNATING".equals(order)) throw invalid("turn.order não suportado: " + order);
            if (timeLimitSeconds < 0) throw invalid("turn.timeLimitSeconds não pode ser negativo");
        }
    }
    public static class RulesConfig {
        public String placement;
        public List<String> movementRule, captureRule;
        public WinConditionConfig winCondition;
        public DrawConditionConfig drawCondition;
        public void validar() {
            required(placement,"rules.placement");
            if(movementRule==null || movementRule.isEmpty()) throw invalid("rules.movementRule deve conter regras");
            if(captureRule==null || captureRule.isEmpty()) throw invalid("rules.captureRule deve conter regras");
            if(winCondition==null) throw invalid("rules.winCondition é obrigatório"); winCondition.validar();
            if(drawCondition==null) throw invalid("rules.drawCondition é obrigatório"); drawCondition.validar();
        }
        public void validarParaXiangqi() {
            if (!"FIXED".equals(placement)) throw invalid("rules.placement não suportado; Xiangqi aceita FIXED");
            validateRuleList(movementRule, Set.of("PATH_MUST_BE_CLEAR", "GENERAL_MUST_STAY_SAFE"), "rules.movementRule");
            validateRuleList(captureRule, Set.of("OPPONENT_ONLY", "CANNON_REQUIRES_ONE_SCREEN"), "rules.captureRule");
            if (!captureRule.contains("OPPONENT_ONLY"))
                throw invalid("rules.captureRule deve incluir OPPONENT_ONLY; capturar peças próprias não é suportado");
            if (!"CHECKMATE_OR_NO_LEGAL_MOVE".equals(winCondition.type))
                throw invalid("rules.winCondition.type não suportado: " + winCondition.type);
            if (!"ONLY_DEFENSIVE_PIECES_REMAIN".equals(drawCondition.type))
                throw invalid("rules.drawCondition.type não suportado: " + drawCondition.type);
        }
        private static void validateRuleList(List<String> rules, Set<String> supported, String field) {
            Set<String> seen = new HashSet<>();
            for (int i = 0; i < rules.size(); i++) {
                String rule = rules.get(i);
                required(rule, field + "[" + i + "]");
                if (!supported.contains(rule)) throw invalid(field + "[" + i + "] não suportada: " + rule);
                if (!seen.add(rule)) throw invalid(field + " contém regra duplicada: " + rule);
            }
        }
    }
    public static class WinConditionConfig { public String type; public void validar(){ required(type,"rules.winCondition.type"); } }
    public static class DrawConditionConfig { public String type; public void validar(){ required(type,"rules.drawCondition.type"); } }
    public static class PlacementConfig {
        public String player, piece;
        public int row, column;
        public void validar(int i, GameConfig c) {
            required(player,"initialPosition["+i+"].player"); required(piece,"initialPosition["+i+"].piece");
            if(!c.players.symbols.contains(player)) throw invalid("initialPosition["+i+"].player não existe: "+player);
            if(c.pieces.stream().noneMatch(p->p.name.equalsIgnoreCase(piece))) throw invalid("initialPosition["+i+"].piece não existe: "+piece);
            if(row<0 || row>=c.board.rows || column<0 || column>=c.board.columns) throw invalid("initialPosition["+i+"] está fora do tabuleiro");
        }
    }
}
