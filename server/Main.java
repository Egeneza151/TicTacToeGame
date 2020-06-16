package server;

public class Main {
    public static void main(String argv[]) throws Exception {
        TCPServer tcpServer = new TCPServer(2137); //utworzenie serwera nasluchujacego na porcie 2137
        tcpServer.runServer(); //uruchomienie serwera
    }
}

