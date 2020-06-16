package server;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;

public class Player extends Thread { //glowna klasa gracza
    private int gameId;
    private Socket socket;
    Player opponent;
    private Game game;
    private String mark;
    private BufferedReader in;
    private PrintWriter out;
    private InetAddress inetAddress;

    public Player(Socket socket, Game game, String mark, int gameId) { //konstruktor gracza po stronie serwera
        this.gameId = gameId;
        this.mark = mark;
        this.socket = socket;
        inetAddress = socket.getInetAddress();
        this.game = game;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sendToClient("CONNECT");
            sendToClient("WAIT_FOR_OPPONENT");

            log("Player " + inetAddress.getHostName() + " PORT: " + socket.getPort() + " is connected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {  //metoda obslugujaca gracza od strony serwera, obsluga wiadomosci (danych) od i do gracza, weryfikacja tych danych 
        try {
            String message = null;
//            out.println("START_GAME");
            sendToClient("START_GAME");
//            out.println("MARK " + mark);
            sendToClient("MARK;" + mark);
            while (true) {
                message = in.readLine();
                message = decode(message);

                log("Game_id: " + gameId + " - " + message);
                if (message.startsWith("MOVE")) {
                    int i = Integer.parseInt(message.substring(5));
                    if (i < 0 || i > 8) {
                        sendToClient("INVALID_MOVE");
                    } else if (!game.setMove(i, this)) {
                        sendToClient("BAD_MOVE");
                    } else {
                        sendToClient("ACCEPT");
                        if (game.isWin()) {
                            win();
                        } else if (game.isFiled()) {
                            draw();
                        }
                    }

                } else if (message.equals("QUIT")) {
                    log("PLAYER " + inetAddress.getHostName() + " PORT " + socket.getPort() + " DISCONNET");
                    finalize();
                } else {
                    sendToClient("BAD_REQUEST");
                }
            }
        } catch (IOException e) {
            opponent.sendMessage("OPONENT_DISCONECT");
            log("PLAYER " + inetAddress.getHostName() + " PORT " + socket.getPort() + " DISCONNET");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


    public void sendMessage(String message) { //metoda wysylajaca dane do klienta
//        out.println(message);
        sendToClient(message);
    }

    public void oponentMove(int i) {   //metoda wysylajaca ruch przeciwnika do klienta
        sendToClient("OPPONENT_MOVE;" + i);
    }

    public void log(String message) { //metoda drukujaca informacje do kosnoli
        System.out.println(LocalTime.now() + " " + message);
    }

    public void setOpponent(Player opponent) { //metoda ustalajaca przeciwnika dla danej instancji klasy Player
        this.opponent = opponent;
    }

    public void win() { //metoda odpowiedzialna za przekazanie informacji o wygranej gracza
        sendToClient("WIN");
        opponent.lose();
    }

    public void draw() {  //metoda odpowiedzialna za przekazanie informacji o remisie
        sendToClient("DRAW");
        opponent.sendToClient("DRAW");
    }

    public void lose() {//metoda odpowiedzialna za przekaanie informacji o przegranej gracza
        sendToClient("LOSE");
    }

    public void youStart() {  //metoda odpowiedzialna za wyslanie informacji o rozpoczeciu gry przez gracza
        sendToClient("YOU_START");
    }

    public void opponentStart() {//metoda odpowiedzialna za wyslanie informacji o rozpoczeciu gry przez przeciwnika
        sendToClient("OPPONENT_START");
    }

    public void sendToClient(String message) {  //metoda odpowiedzialna za wyslanie informacji do klienta w formie "zakodowanej"
        out.println(message + "::");
    }

    public String decode(String message) { //metoda dekodujaca dane
        if (message.endsWith("::")) {
            return message.substring(0, message.length() - 2);
        }
        return "INVALID_MESSAGE";
    }

}
