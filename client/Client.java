package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class Client {

    private boolean endGame = false;
    
    PrintWriter out;

    private JFrame frame = new JFrame("TicTacToe XD");
    private JLabel m = new JLabel(" ");

    private Square[] board = new Square[9];
    private Square choosedSquare;

    private String h;
    String choose;
    String otherChoose;
    String fontList[] = {"Comic Sans MS","Tahoma","Veranda","Courier","Helvetica","Monospaced","Onyx","Sathu"};
    int fontIndex = 0;
    
    public Client(String host) { //Konstruktor klasy
        this.h = host;
    }

    public void onClientStart() throws IOException { 
        connect();
    }

    private void connect() throws IOException { //metoda laczaca do serwera
    	try {
        Socket socket = new Socket(h, 2137); //tworzenie polaczenia do serwera o adresie h i porcie 2137
        out = new PrintWriter(socket.getOutputStream(), true); //strumien wyjscia "do serwera"
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //strumien wejscia "od serwera"
        setFrame(frame, out);
        receiveFromServer(in);
    	}catch(IOException e) {
    		JOptionPane.showConfirmDialog(
                    frame,
                    "Thanks, this address not works or server is not running. \r\n I'm going to shut down. \r\n Have a nice day.",
                    "You did it!",
                    JOptionPane.OK_OPTION
                    );
    		System.exit(2137);
    	}
    }

    private void receiveFromServer(BufferedReader in) throws IOException {
        String msg;

        while (true) { //glowna petla pro
            msg = in.readLine(); 
            msg = decode(msg); //oodczytaj i zdekoduj wiadomosc
            //log(msg);
            int mStart = 0;
            
            mStart = msg.indexOf(";"); 
            System.out.println(mStart);
            String mtemp;
            
            if(mStart > 0) //sprawdza czy wiadomosc ma argument rozdzielony znakiem ';' 
            	mtemp = msg.substring(0,mStart);
            else
            	mtemp = msg;
            
            
            switch(mtemp) {					//interpretacja komend od serwera
            
            case "ACCEPT":  			m.setText("Opponent move"); choosedSquare.setText(choose); break;    
            case "OPPONENT_MOVE": 		int i = Integer.valueOf(msg.substring(14)); setBoard(i, otherChoose); m.setText("Your move"); frame.toFront();frame.requestFocusInWindow(); break; //ruch gracza
            case "OPONENT_DISCONECT":	m.setText("Other player disconnected - you win"); break; //przeciwnik sie rozlaczyl = walkower
            case "MARK": 				choose = msg.substring(5); frame.setTitle("Mark - " + choose); otherChoose = choose.equals("X")?"O":"X"; break; //zaznaczenie ruchu przeciwnika
            case "WIN":  				endGame = true; m.setText("You Win!!!"); break; //wygrana
            case "LOSE": 				endGame = true; m.setText("You Lose!!!"); break; //przegrana
            case "DRAW":  				endGame = true;m.setText("Tie"); break; //remis
            case "WAIT_FOR_OPPONENT": 	m.setText("Waiting for second player"); break; //oczekiwanie na gracza
            case "OPPONENT_START": 		m.setText("Opponent starts"); break; //przeciwnik zaczyna gre
            case "YOU_START": 			m.setText("You start"); break;  //gracz rozpoczyna
            case "QUIT": 				return;  //wyjscie z programu
            case "INVALID_MESSAGE" : 	m.setText("INVALID_MESSAGE"); break; //bledna komenda
            default: break;
            }
        }
    }

    private void setFrame(JFrame frame, PrintWriter out) { //metoda kreujaca okno gry, dodaje eventListener do kazdego pola gry jaki do klawisza zmiany czcionki planszy
        m.setBackground(Color.DARK_GRAY);
        frame.getContentPane().add(m, "South");

        JPanel boardPanel = new JPanel();
        boardPanel.setBackground(Color.DARK_GRAY);
        boardPanel.setLayout(new GridLayout(3, 3, 2, 2));
        for (int i = 0; i < board.length; i++) {
            final int j = i;
            board[i] = new Square();
            board[i].addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                	choosedSquare = board[j]; 
                	if (endGame || !choosedSquare.isEmpty())
                         return;
                    sendToServer("MOVE " + j);
                }
            });
            boardPanel.add(board[i]);
        }
        frame.getContentPane().add(boardPanel, "Center");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(320, 240);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.addKeyListener(new KeyListener() {
        	@Override
        	public void keyPressed(KeyEvent e) {
        		//System.out.println("Key: " + e.getKeyCode() + " , "+e.getKeyChar());
        		if(e.getKeyChar() == 'F'|| e.getKeyChar()=='f') {
        			fontIndex++;
        			if(fontIndex>=fontList.length)
        				fontIndex = 0;
        			for(int i=0;i<board.length;i++)
        				board[i].setFont(fontList[fontIndex]);
        		}
        			
        	}

			@Override
			public void keyReleased(KeyEvent arg0) {
				
				
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
				
				
			}

        });
    }
    

    public void setBoard(int i, String mark) { //ustawia w danym polu znak X lub O
        board[i].setText(mark);
    }

    public void log(String message) {  //metoda drukujaca informacje do kosnoli 
        System.out.println(LocalTime.now() + " - " + message);
    }


    public void sendToServer(String message) { //metoda wysylajaca dane do serwera w formie "zakodowanej"
        out.println(message + "::");
    }

    public String decode(String message) {  //metoda dekodujaca dane
        if (message.endsWith("::")) {
            return message.substring(0, message.length() - 2);
        }
        return "INVALID_MESSAGE";
    }

    static class Square extends JPanel {   //glowna klasa pola gry
		private static final long serialVersionUID = 1L;
		JLabel label = new JLabel((Icon) null);
        Font font = new Font("Comic Sans MS", Font.BOLD, 50);
       
        public Square() {
            setBackground(Color.white);
            add(label);
            label.setFont(font);
        }

        public void setText(String text) {
            label.setText(text);
        }

        public String getText() {
            return label.getText();
        }

        public boolean isEmpty() {
            return getText() == null ? true : false;
        }

        public void setIcon(Icon icon) {
            label.setIcon(icon);
        }
        public void setFont(String fontname) {
        	Font font = new Font(fontname, Font.BOLD, 50);
        	label.setFont(font);
        }
    }

}
