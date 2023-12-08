public class Player {
    private static final double gravity = 5;
    private static final double friction = 2;
    // characters: cowboy (C), military (M), alien (A), snowman (S)
    private double x;
    private double y;
    private double xVel =0;
    private double yVel=0;
    private final char character;
    private int shotCoolDown;
    private String user;
    private int health = 100;
    public Player(double x, double y, char character, String user) {
        this.user = user;
        this.x = x;
        this.y = y;
        this.character = character;
    }

    public void move() {
        if (shotCoolDown > 0) {shotCoolDown-=1;}
        xVel/=friction;
        if (isOnGround()) {
            yVel = 0;
        }
        else {
            yVel += gravity;
            y += yVel;
        }
        x += xVel;
    }
    public void right() {
        xVel = 10;
    }
    public void left() {
        xVel = -10;
    }
    public void jump() {
        if (isOnGround()) {
            yVel = -20;
        }
    }
    public void ability() {

    }
    public void shoot(double angle) {
        Game.bullets.add(new Bullet(angle));
        shotCoolDown = 10;
    }
    private boolean isOnGround(){
        return true;
    }
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            die();
        }
    }
    public void die() {
        Game.players.remove(this);
    }
    public double[] getXY() {
        return new double[] {x ,y};
    }
}
