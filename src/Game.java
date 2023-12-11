import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class Game {
//floor is at 590
//jump 90 pixel minimum
    public static HashSet<Bullet> bullets = new HashSet<>();
    public static HashMap<String, Player> players = new HashMap<>();
    public static ArrayDeque<String> commands = new ArrayDeque<>();
    public static ArrayDeque<String> sendCommands = new ArrayDeque<>();
    public static Client client;
    public static Player user;
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a username: ");
        String username = scanner.nextLine().replaceAll(" ", "_");
        System.out.println("Which character do you want to be?");
        char character = scanner.nextLine().toUpperCase().charAt(0);
        Socket socket = new Socket("localhost", 1452);
        client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage(character);
        user = new Player(500,350,character, username);
        players.put(username, user);
        runCommand();
        new GUI();
    }
    public static void runFrame() {
        for (Bullet b : bullets) {
            b.move();
        }
        for (Map.Entry<String, Player> e : Game.players.entrySet()) {
            e.getValue().move();
        }
    }
    public static void runCommand() {
        new Thread(new Runnable(){
            @Override
            public void run() {
                while (client.socket.isConnected()) {
                    System.out.println("NAY");
                    if (commands.isEmpty()) {continue;}
                    System.out.println("YAY");
                    String command = commands.removeFirst();
                    System.out.println("Command: " + command);
                    System.exit(10);
                    if (command.startsWith("new")) {
                        String[] words = command.split(" ");
                        players.put(words[1], new Player(500, 350, words[2].charAt(0), words[1]));
                    }
                    else if (command.endsWith("d")) { //move right
                        players.get(command.substring(0, command.indexOf(" "))).right();
                    }
                    else if (command.endsWith("a")) { //move left
                        players.get(command.substring(0, command.indexOf(" "))).left();
                    }
                    else if (command.endsWith("w")) { //jump
                        players.get(command.substring(0, command.indexOf(" "))).jump();
                    }
                    else if (command.endsWith("r")) { //shoot radians
                        players.get(command.substring(0, command.indexOf(" "))).shoot(Double.parseDouble(command.substring(command.indexOf(" ")+1, command.length()-1)));
                    }
                }
            }
        }).start();
    }
    public static boolean inObstacle(int xLeft, int xRight, int yLow, int yHigh) {
        if ((xRight > 189 && yLow < 430 && xRight < 261) || (xLeft < 261 && xLeft > 189 && yLow < 430)) { //in tall box
            return true;
        }
        if (xLeft < 333 && xLeft > 261 && yLow < 506) { //in small box
            return true;
        }
        return false;
    }
}
