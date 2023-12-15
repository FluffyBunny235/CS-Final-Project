import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    public Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    public String username;
    public boolean ready = false;
    public String recentXY = "";

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("new " + username);
            username = username.split(" ")[0] +" " +username.split(" ")[1];
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
                if (messageFromClient == null) {continue;}
                if (messageFromClient.startsWith("XY")) {
                    recentXY = messageFromClient.substring(2);
                    ready = true;
                }
                else if (messageFromClient.endsWith("has left")) {
                    broadcastMessage(messageFromClient);
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
                else {
                    broadcastMessage(messageFromClient);
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
        closeEverything(socket, bufferedReader, bufferedWriter);
    }

    public void broadcastMessage(String messageToSend) {
        ArrayList<ClientHandler> clientHandlers1 = new ArrayList<>(clientHandlers);
        for (ClientHandler clientHandler : clientHandlers1) {
            try {
                if (!clientHandler.username.equals(this.username)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException | NullPointerException e) {
                System.out.println("Broadcast failed: "+ messageToSend + " To: " + clientHandler.username);
                clientHandlers.remove(clientHandler);
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
                broadcastMessageTo("new "+ ch.username + " " + ch.getXY(), this);
            }
        }
    }
    public void removeClientHandler() {
        broadcastMessage(username + " has left.");
        clientHandlers.remove(this);
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
    public String getXY () {
        broadcastMessageTo("Send XY", this);
        while (!ready) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        ready = false;
        return recentXY;
    }
}
