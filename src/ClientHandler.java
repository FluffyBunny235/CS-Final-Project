import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("new " + username);
            sendUsers();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    @Override
    public void run() {
        String messageFromClient;

        while(socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.username.equals(this.username)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException | NullPointerException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }
    public void broadcastMessageTo(String messageToSend, ClientHandler recipient) {
        try {
            recipient.bufferedWriter.write(messageToSend);
            recipient.bufferedWriter.newLine();
            recipient.bufferedWriter.flush();
        } catch (IOException | NullPointerException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    public void sendUsers() {
        for (ClientHandler ch : clientHandlers) {
            if (ch!=this) {
                broadcastMessageTo("new "+ ch.username, this);
            }
        }
    }
    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage(username + " has left.");
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
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
