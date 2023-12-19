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
    public String user;
    public int health = 100;
    public boolean abilityActive = false;
    public int abilityTimeLeft = 0; // time left on current ability
    public int newAbilityCountdown = 600; // how long until next ability
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
        if (abilityTimeLeft > 0) {abilityTimeLeft--;}
        else {abilityActive = false;}
        if (newAbilityCountdown > 0) {newAbilityCountdown--;}
        if (x > 1000 || x < -60) {takeDamage(1);}
        x += xVel;
        if (Game.inObstacle((int)x, (int)x+40, (int)y+60, (int)y) > 0) {x-=xVel;}
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
            if (yVel < 0 && isOnLadder()) {//going up ladder
                yVel = -5;
            }
            if (yVel > 0 && isOnLadder()) {
                yVel = 5;
            }
            y += yVel;
            isOnGround();
        }
        if (isInWell()) {
            x = 432;
            y = 0;
        }
        if (Game.user == this) {
            Game.sendCommands.addLast("Set " + x + " " + y +" "+ xVel + " " + yVel);
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
        abilityActive = true;
        abilityTimeLeft = 300; //5 seconds
        newAbilityCountdown = 1800; //30 seconds
    }
    public void shoot(double angle) {
        if (xVel > 0) {
            Game.bullets.add(new Bullet(this.x + 41, this.y + 30, angle, Game.players.get(user)));
        }
        else {
            Game.bullets.add(new Bullet(this.x-1, this.y + 30, angle, Game.players.get(user)));
        }
    }
    public void shoot(int x, int y) {
        if (shotCoolDown != 0) {return;}
        double angle = Math.atan((y-(this.y+30))/(x-(this.x + 20)));
        int xAdjustment = 0;
        if (xVel > 0) {xAdjustment = 40;}
        if (this.x+xAdjustment > x) {angle+=3.141592;}
        Game.sendCommands.add(angle + "r");
        if (xVel > 0) {
            Game.bullets.add(new Bullet(this.x + 41, this.y + 30, angle, Game.players.get(user)));
        }
        else {
            Game.bullets.add(new Bullet(this.x-1, this.y + 30, angle, Game.players.get(user)));
        }
        if (character != 'C' || !abilityActive) {
            shotCoolDown = 40;
        }
    }
    private boolean isOnGround(){
        double yBottom = y+60;
        double xRight = x+40;
        if (yBottom >= 584) {
            y = 525;
            return true;
        }
        else if (x < 548 && xRight > 357 && yBottom >= 376 && yBottom <= 430) { //tire swing
            y = 317;
            return true;
        }
        else if (x < 548 && xRight > 357 && y >= 376 && y <= 430) { //tire swing
            y = 431;
            yVel = 0;
            return false;
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
        return Game.inObstacle((int)x, (int)x+40, (int)y+60, (int)y)==-1;
    }
    public boolean isInWell() {
        return Game.inObstacle((int)x, (int)x+40, (int)y+60, (int)y)==2;
    }
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            die();
        }
    }
    public void die() {
        Game.players.remove(this.user);
        if (Game.user == this) {
            Game.sendCommands.add("has left.");
            Game.user = null;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(10);
        }
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
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void setXVel(double xVel) {
        this.xVel = xVel;
    }
    public void setYVel(double yVel) {
        this.yVel = yVel;
    }
    public double getXVel() {
        return xVel;
    }
    public double getYVel() {
        return yVel;
    }
}
