package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

public class TCPServer { //glowna klasa serwera
    private int PORT;

    public TCPServer(int port) {//konstruktor klasy
        this.PORT = port;
    }

    public void runServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT); //utworzenie serwera naslucjujacego na porcie port
        System.out.println("Server up....");
        while (true) {
            Game game = new Game(); //utworzenie instancji klasy GAME

            Player first = new Player(serverSocket.accept(), game, "X", game.getGameId()); //utworzenie instancji klasy Player, akceptacja pierwszego polaczenia do serwera,ustalenie gracza jako X
            game.setPlayer1(first); //dodanie pierwszego gracza do instancji klasy Game

            Player second = new Player(serverSocket.accept(), game, "O", game.getGameId());//utworzenie drugiej instancji klasy Player, akceptacja pierwszego polaczenia do serwera,ustalenie gracza jako O
            game.setPlayer2(second);//dodanie drugiego gracza do instancji klasy Game

            
          //ustawienie graczy jako swoich przeciwnikow
            first.setOpponent(second); 
            second.setOpponent(first);

            
            //uruchomienie watkow obslugujacych graczy
            first.start();
            second.start();
            
            //rozpoczecie gry
            game.startGame();

        }
    }
}
