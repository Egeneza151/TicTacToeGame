package server;

public class Game {   //klasa gry
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private static Integer GAME_ID = 0;

    private Player[] gamePool = { //aktualna plansza
            null, null, null,
            null, null, null,
            null, null, null};

    public Game() { //kostruktor gry
        GAME_ID++;
    }

    public Integer getGameId() { //metoda zwracajaca ID danej gry
        return GAME_ID;
    }

    public void setPlayer1(Player player1) { //metoda ustawiajaca gracza1 w aktualnej instancji
        this.player1 = player1;
        currentPlayer = player1;

    }

    public void setPlayer2(Player player2) { //metoda ustawiajaca gracza2 w aktualnej instancji
        this.player2 = player2;
    }

    public synchronized boolean setMove(int move, Player player) { //metoda odpowiedzialna za obsluge ruchu gracza i wyslanie do drugiego gracza
        if (player == currentPlayer && gamePool[move] == null) {
            gamePool[move] = player;
            player.opponent.oponentMove(move);
            currentPlayer = currentPlayer.opponent;
            return true;
        }
        return false;
    }

    public boolean isWin() {  //warunek wygranej
        return
                (			gamePool[0] != null && gamePool[0] == gamePool[1] && gamePool[0] == gamePool[2])  // pozioma 1
                        || (gamePool[3] != null && gamePool[3] == gamePool[4] && gamePool[3] == gamePool[5])  // pozioma 2
                        || (gamePool[6] != null && gamePool[6] == gamePool[7] && gamePool[6] == gamePool[8])  // pozioma 3
                        || (gamePool[0] != null && gamePool[0] == gamePool[3] && gamePool[0] == gamePool[6])  // pionowa 1
                        || (gamePool[1] != null && gamePool[1] == gamePool[4] && gamePool[1] == gamePool[7])  // pionowa 2
                        || (gamePool[2] != null && gamePool[2] == gamePool[5] && gamePool[2] == gamePool[8])  // pionowa 3
                        || (gamePool[0] != null && gamePool[0] == gamePool[4] && gamePool[0] == gamePool[8])  // skos 1
                        || (gamePool[2] != null && gamePool[2] == gamePool[4] && gamePool[2] == gamePool[6]); // skos 2
    }

    public boolean isFiled() { //metoda odpowiedzialna za sprawdzenie czy plansza zostala wypelniona 
        for (int i = 0; i < 9; i++) {
            if (gamePool[i] == null)
                return false;
        }
        return true;
    }

    public void draw() { //metoda odpowiedzialna za poinformowanie o remisie
        player1.draw();
        player2.draw();
    }

    public void startGame() { //metoda odpowiedzialna za rozpoczecie gry
        player1.youStart();
        player2.opponentStart();
    }

    public void win(Player player) { //metoda odpowiedzialna za poinformowanie o wygranej
        player.win();
        player.opponent.lose();
    }
}
