public class RuleSet {
    private boolean isCheck;
    private boolean isInsideRiver;
    private boolean isInsidePalace;


    /**
     * Cria uma nova instancia das regras para definir o estado inicial do jogo.
     *  
     * @param check         Define se o rei esta em xeque
     * @param insideRiver   Define se a peca esta dentro do rio
     * @param insidePalace  Define se a peca esta dentro do palacio
     */
    public RuleSet(boolean check, boolean insideRiver, boolean insidePalace){
        this.isCheck = check;
        this.isInsideRiver = insideRiver;
        this.isInsidePalace = insidePalace;
    }

    /**
     * Retorna o valor da variavel privada isCheck
     */
    public boolean getIsCheck(){
        return this.isCheck;
    }

    /**
     * Retorna o valor da variavel privada isInsideRiver
     */
    public boolean getIsInsideRiver(){
        return this.isInsideRiver;
    }

    /**
     * Retorna o valor da variavel privada isInsidePalace
     */
    public boolean getInsidePalace(){
        return this.isInsidePalace;
    }
}
