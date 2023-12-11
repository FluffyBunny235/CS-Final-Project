import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Player {
    private static final double gravity = 1.3;
    private static final double friction = 1.5;
    // characters: cowboy (C), military (M), alien (A), snowman (S)
    private double x;
    private double y;
    private double xVel =0;
    private Image image;
    private Image reverseImage;
    private double yVel=0;
    private final char character;
    private int shotCoolDown;
    private String user;
    public int health = 100;
    public Player(double x, double y, char character, String user) {
        this.user = user;
        this.x = x;
        this.y = y;
        this.character = character;
        try {
            image = ImageIO.read(new File("src/" + character + ".png"));
            reverseImage = ImageIO.read(new File("src/" + character + "_Reverse.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void move() {
        if (x > 1000 || x < -60) {takeDamage(1);}
        x += xVel;
        if (shotCoolDown > 0) {shotCoolDown-=1;}
        xVel/=friction;
        if (xVel < 0.05 && xVel > 0) {
            xVel = 0.000001;
        }
        else if (xVel > -0.05 && xVel < 0) {
            xVel = -0.000001;
        }
        if (isOnGround() && yVel >= 0) {
            yVel = 0;
        }
        else {
            yVel += gravity;
            y += yVel;
            isOnGround();
        }
    }
    public void right() {
        xVel = 6;
    }
    public void left() {
        xVel = -6;
    }
    public void jump() {
        if (isOnGround()) {
            yVel = -15;
        }
    }
    public void ability() {

    }
    public void shoot(double angle) {
        Game.bullets.add(new Bullet(angle));
        shotCoolDown = 10;
    }
    private boolean isOnGround(){
        double yBottom = y+60;
        double xRight = x+40;
        if (yBottom >= 584) {
            y = 525;
            return true;
        }
        else if (x < 548 && xRight > 357 && yBottom >= 376 && yBottom <= 430) {
            y = 317;
            return true;
        }
        else if (x > 260 && x < 333 && yBottom > 506){
            y = 447;
            return true;
        }
        else if (x < 261 && xRight > 189 && yBottom > 429) {
            y = 371;
            return true;
        }
        return false;
    }
    public boolean isOnLadder(){
        return false;
    }
    public boolean isOnWell() {
        return false;
    }
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            die();
        }
    }
    public void die() {
        Game.players.remove(this);
        Game.user = null;
    }
    public double[] getXY() {
        return new double[] {x ,y};
    }
    public Image getImage() {
        if (xVel < 0) {
            return image;
        }
        return reverseImage;
    }
}
