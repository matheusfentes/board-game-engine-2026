public class GameManagement {
    private int time;
    private Player currentTurn;
    private Board gameBoard;
    private Player[] gamePlayers;

    /**
     * Cria uma instancia do game management pra fazer o intermedio entre o jogador e os objetos do jogo
     * 
     * @param board     Define o tabuleiro que sera usado durante o jogo
     * @param players   Define os jogadores que irao participar do jogo
     */
    public GameManagement(Board board, Player[] players){
        this.time = 0;
        this.gameBoard = board;
        this.gamePlayers = players;
        
        for(Player player : this.gamePlayers){
            if(player.getColor().equalsIgnoreCase("vermelho")){
                this.currentTurn = player;
                break;
            }
        } 
    }

    public boolean executeMove(int x, int y){
        // logica de execucao de movimento
        return true;
    }

    public void switchTurn(){
        for(Player player : this.gamePlayers){
            if(player.getPlayerCode() != this.currentTurn.getPlayerCode()){
                this.currentTurn = player;
                break;
            }
        }
    }

    public int getTime(){
        return time;
    }

    public Player getTurn(){
        return this.currentTurn;
    }

    public Player endgame(){
        return this.currentTurn;
    }
}
