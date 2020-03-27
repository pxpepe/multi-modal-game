package com.pxpepe.jogomultimodal;

public class DitadosDto {

    private String[] ditado01 = {"O Chocolate",
            "O chocolate é uma invenção do povo asteca",
            "Este povo habitava na américa antes da chegada de Cristóvão Colombo",
            "Era preparado à base de cacau, mel e baunilha",
            "Era uma bebida escura e um tanto amarga",
            "O produto veio para a Europa com os espanhóis",
            "Os espanhóis acrescentaram algumas especiarias, vinho e amêndoas",
            "O sabor ficou muito melhor"};

    private String[] ditado02 = {"Bandos",
                        "Alguns mamíferos, como os lobos, vivem em bandos",
                        "Os lobos vivem e caçam em alcateias",
                        "As alcateias chegam a ter quarenta membros",
                        "Juntos, conseguem perseguir e matar animais maiores que eles",
                        "Um lobo sozinho não conseguiria fazer isso"};

    private String[] ditado03 = {"Dinossauros",
                        "Sabe-se que existiram quase mil espécies de dinossauros",
                        "Algumas eram gigantescas",
                        "Podiam alcançar até quarenta metros de comprimento",
                        "Estas pesavam mais que dois aviões juntos",
                        "Também havias outras que pesavam o mesmo que uma galinha"};

    public String[] getDitadoPorNumero(int numDitado) {

        String[] ditado = null;


        switch (numDitado) {
            case 0:
                ditado=ditado01;
                break;
            case 1:
                ditado = ditado02;
                break;
            case 2:
                ditado = ditado03;
                break;
        }

        return ditado;

    }

    public int getNumDitados() {
        return 3;
    }


    public String getFraseAct(int numDitado, int numFrase) {

        String saida = "";

        String[] ditado = this.getDitadoPorNumero(numDitado);
        if (ditado!=null && numFrase<ditado.length) {
            saida = ditado[numFrase];
        }

        return saida;

    }

    public boolean haMaisFrases(int numDitado, int numFrase) {

        return !this.getFraseAct(numDitado,numFrase).equalsIgnoreCase("");

    }

}
