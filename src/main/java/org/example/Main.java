package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final int port = 9001;
    private static Socket[] waitingClients = new Socket[2];

    public static void main(String[] args) {
        System.out.println("Serwer is running");
        try {
            ServerSocket listen = new ServerSocket(port);

            try {
                while (true) {
                    Main.addClient(listen.accept());
                }
            } finally {
                listen.close();
            }
        } catch (Exception e) {
            System.out.println("Serwer not running");
        }
    }

    private static void addClient(Socket s) {
        if (waitingClients[0] == null) {
            waitingClients[0] = s;
            System.out.println("Client is connected");
        } else {
            waitingClients[1] = s;
            System.out.println("Client is connected");
            new Room(waitingClients).start();
            waitingClients[0]=null;
            waitingClients[1]= null;

            System.out.println("Room was created");
        }
    }


    private static class Room extends Thread {
        private Socket Player1;
        private Socket Player2;
        private BufferedReader inPlayer1;
        private BufferedReader inPlayer2;
        private PrintWriter outPlayer1;
        private PrintWriter outPlayer2;
        int player1PawnNumber = 12;
        int player2PawnNumber = 12;

        public Room(Socket[] ClientsList){
            Player1 = ClientsList[0];
            Player2 = ClientsList[1];
        }

        public void run() {
            try {
                inPlayer1 = new BufferedReader(new InputStreamReader(Player1.getInputStream()));
                inPlayer2 = new BufferedReader(new InputStreamReader(Player2.getInputStream()));
                outPlayer1 = new PrintWriter(Player1.getOutputStream(), true);
                outPlayer2 = new PrintWriter(Player2.getOutputStream(), true);

                outPlayer1.println("white");
                outPlayer2.println("black");

                while(true){

                    // player 1 turn //
                    String message = inPlayer1.readLine();
                    if(message.length() == 6) {
                        player2PawnNumber--;
                        if(player2PawnNumber <=   0) {
                            outPlayer1.println("You win!");
                            outPlayer2.println("You lost the game");
                            System.out.println("Player Win!");
                            break;
                        }
                    }
                    System.out.println(message);
                    outPlayer2.println(message);

                    //player 2 turn //
                    message = inPlayer2.readLine();
                    if(message.length() == 6) {
                        player1PawnNumber--;
                        System.out.println("Odejmuje");
                        if(player1PawnNumber <=  0) {
                            outPlayer1.println("You lost the game");
                            outPlayer2.println("You win!");
                            System.out.println("Player Win!");
                            break;
                        }
                    }
                    outPlayer1.println(message);

                }

            }catch(IOException e) {
                System.err.println(e.toString());
            }
        }
        }
    }
