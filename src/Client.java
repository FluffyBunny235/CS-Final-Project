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
    public void sendMessage(String message, char character) {
        try{
            bufferedWriter.write(username + " " + character + ":" + message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch( IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    public void startMessage(char character) {
        try {
            bufferedWriter.write(username + " " +character);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch( IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
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
