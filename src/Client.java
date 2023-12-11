import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public Socket socket;
    public BufferedWriter bufferedWriter;
    public BufferedReader bufferedReader;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch( IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    public void sendMessage(char character) {
        try{
            bufferedWriter.write( username + " " +character);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch( IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()) {
                    if (Game.sendCommands.isEmpty()) {continue;}
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
                        Game.commands.addLast(messageFromChat);
                        System.out.println(Game.commands.isEmpty());
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
