import java.util.Map;

public class Bullet {
    private double angle;
    private double x;
    private double y;
    private static final int velocity = 10;

    public double[] getXY() {
        return new double[] {x+5*Math.cos(angle), y+5*Math.sin(angle), x-5*Math.cos(angle), y-5*Math.sin(angle)};
    }
    public void move() {
        x += velocity * Math.cos(angle);
        y += velocity * Math.sin(angle);
        for (Map.Entry<String, Player> e : Game.players.entrySet()) {
            double[] XY = e.getValue().getXY();
            if (x > XY[0] && x < XY[0]+40 && y > XY[1] && y < XY[1]+60) {
                e.getValue().takeDamage(25);
                Game.bullets.remove(this);
            }
        }
        if (x > 1000 || x < 0 || y < 0 || y>590) {
            Game.bullets.remove(this);
        }
    }
    public Bullet(double angle) {
        this.angle = angle;
    }
}
