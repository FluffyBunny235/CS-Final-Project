import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class GUI_Panel extends JPanel implements Runnable{
    final int HEIGHT = 700;
    final int WIDTH = 1000;
    Thread gameThread;
    Keyboard kb;
    public GUI_Panel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setDoubleBuffered(true);
        kb = new Keyboard();
        this.addKeyListener(kb);
        this.setFocusable(true);
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawTime = 1000000000.0/60; //a lot of nano seconds, but 1/60th of a second
        double nextDraw = System.nanoTime() + drawTime;
        while (gameThread != null) { //as long as the thread exists, repeat:
            //update characters
            Game.runFrame();
            if (Game.user != null) {
                if (kb.jump) {Game.user.jump();}
                if (kb.right) {Game.user.right();}
                else if (kb.left) {Game.user.left();}
                if (kb.ability) {Game.user.ability();}
            }
            //update screen
            repaint();
            try {
                double remainingTime = nextDraw - System.nanoTime();
                if (remainingTime < 0) {
                    nextDraw += drawTime;
                    System.out.println("Frame Drop");
                    continue;
                }
                Thread.sleep((long)(remainingTime/1000000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            nextDraw += drawTime;
        }
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;
        try {
            graphics2D.drawImage(ImageIO.read(new File("src/Background.png")), 0, 0, null);
        } catch (IOException e){
            e.printStackTrace();
        }
        graphics2D.setColor(Color.yellow);
        for (Bullet b : Game.bullets) {
            graphics2D.drawLine((int)b.getXY()[0], (int)b.getXY()[1], (int)b.getXY()[2], (int)b.getXY()[3]);
        }
        for (Map.Entry<String, Player> e : Game.players.entrySet()) {
            graphics2D.drawImage(e.getValue().getImage(), (int)e.getValue().getXY()[0], (int)e.getValue().getXY()[1], null);
        }
        if (Game.user != null) {graphics2D.drawString("Health: " + Game.user.health, 500, 350);}
        else {graphics2D.drawString("Health: 0", 500, 350);}
        graphics2D.dispose();
    }
}
