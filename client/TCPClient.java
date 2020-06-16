package client;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {


    public static String receve_all(BufferedReader in) throws IOException {  //metoda odpowiedzialna za odebranie wszystkich danych od serwera
        boolean loop = true;
        StringBuilder sb = new StringBuilder(8096);
        while (loop) {
            if (in.ready()) {
                int i = 0;
                while (i != -1) {
                    i = in.read();
                    sb.append((char) i);
                }
                loop = false;
            }
        }
        return sb.toString();
    }

    public static void main(String argv[]) { //glowna funkcja programu klienta, ustawienie zmiennych
    	JFrame frame = new JFrame();
        int port = 2137;
        String host = null;
        
    	String s = (String)JOptionPane.showInputDialog( //wyswietlenie okna z prosba o wprowadzenie adresu serwera
                frame,
                "Enter server address: ",
                "Config mate",
                JOptionPane.OK_CANCEL_OPTION
                );
    	if(s == null ) System.exit(0);
    	host = s;
    	System.out.print(s+" "+host);
    	
    	

        Client client = new Client(host); //utworzenie nowej instancji klasy Client z danymi
        try {
            client.onClientStart();     //uruchomienie instancji
        } catch (IOException e) {
         System.out.println("SERVER NOT RESPONSE AT PORT: " + port+ " AND HOST: "+ host);
        }

    }
}
