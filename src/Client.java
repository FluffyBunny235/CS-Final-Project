import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public Socket socket;
    public BufferedWriter bufferedWriter;
    public BufferedReader bufferedReader;
    public String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
            System.out.println("Client ID: " + username);
        } catch( IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    public void sendMessage(char character, double x, double y) {
        try{
            bufferedWriter.write( username + " " +character + " " + x + " " + y);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch( IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()) {
                    if (Game.sendCommands.isEmpty()) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        continue;
                    }
                    String messageToSend = Game.sendCommands.removeFirst();
                    try {
                        bufferedWriter.write(username + " " + character + ": " + messageToSend);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    } catch( IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }
    public void listenForMessage() {
        new Thread(new Runnable(){
            @Override
            public void run() {
                String messageFromChat;
                while (socket.isConnected()) {
                    try {
                        messageFromChat = bufferedReader.readLine();
                        if (messageFromChat == null) {continue;}
                        if (messageFromChat.equals("Send XY")) {
                            try {
                                bufferedWriter.write("XY"+Game.players.get(username).getXY()[0]+" "+Game.players.get(username).getXY()[1]);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                            } catch( IOException e) {
                                closeEverything(socket, bufferedReader, bufferedWriter);
                            }
                        }
                        Game.commands.addLast(messageFromChat);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader!= null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
