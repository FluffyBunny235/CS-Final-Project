import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class Game {
//floor is at 590
//jump 90 pixel minimum
    public static HashSet<Bullet> bullets = new HashSet<>();
    public static HashSet<Special_Ability> abilities = new HashSet<>();
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
        user = new Player(500,350,character, username);
        players.put(username, user);
        Socket socket = new Socket("172.16.0.49", 1452);
        client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage(character, 500, 350);
        runCommand();
        new GUI();
    }
    public static void runFrame() {
        HashSet<Bullet> bulletsCopy = new HashSet<>(bullets);
        HashMap<String, Player> playersCopy = new HashMap<>(players);
        for (Bullet b : bulletsCopy) {
            b.move();
        }
        for (Map.Entry<String, Player> e : playersCopy.entrySet()) {
            e.getValue().move();
        }
    }
    public static void runCommand() {
        new Thread(new Runnable(){
            @Override
            public void run() {
                while (client.socket.isConnected()) {
                    if (commands.isEmpty()) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        continue;
                    }
                    String command = commands.removeFirst();
                    //System.out.println("Command: " + command);
                    if (command.startsWith("new")) {
                        String[] words = command.split(" ");
                        players.put(words[1], new Player(Double.parseDouble(words[3]), Double.parseDouble(words[4]), words[2].charAt(0), words[1]));
                    }
                    else if (command.contains("Set")) {
                        String[] words = command.split(" ");
                        players.get(words[0]).setX(Double.parseDouble(words[3]));
                        players.get(words[0]).setY(Double.parseDouble(words[4]));
                        players.get(words[0]).setXVel(Double.parseDouble(words[5]));
                        players.get(words[0]).setYVel(Double.parseDouble(words[6]));
                    }
                    else if (command.endsWith("s")) { //ability
                        players.get(command.substring(0, command.indexOf(" "))).ability();
                    }
                    else if (command.endsWith("r")) { //shoot radians
                        players.get(command.substring(0, command.indexOf(" "))).shoot(Double.parseDouble(command.substring(command.indexOf(":")+2, command.length()-1)));
                    }
                    else if (command.endsWith("left.")) {
                        players.remove(command.split(" ")[0]);
                    }
                }
            }
        }).start();
    }
    public static int inObstacle(int xLeft, int xRight, int yLow, int yHigh) {
        if ((xRight > 187 && yLow > 431 && xRight < 261) || (xLeft < 261 && xLeft > 189 && yLow > 431)) { //in tall box
            return 1;
        }
        if (xLeft < 333 && xLeft > 261 && yLow > 507) { //in small box
            return 1;
        }
        if (xLeft < 548 && xRight > 357 && yHigh < 431 && yLow > 378) { //in tire swing
            return 1;
        }
        if (xRight > 843 && xLeft < 984 && yLow > 536) {//inside well
            return 2;
        }
        else if (xRight > 838 && xLeft < 989 && yLow > 536) {//on edge of well
            return 1;
        }
        if (xRight < 191 && xRight > 175 && yLow > 429) {//on ladder
            return -1;
        }
        return 0;
    }
}
