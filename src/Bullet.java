import java.util.HashMap;
import java.util.Map;

public class Bullet {
    private double angle;
    private double x;
    private double y;
    private Player p;
    private static final int velocity = 10;

    public double[] getXY() {
        return new double[] {x+5*Math.cos(angle), y+5*Math.sin(angle), x-5*Math.cos(angle), y-5*Math.sin(angle)};
    }
    public void move() {
        x += velocity * Math.cos(angle);
        y += velocity * Math.sin(angle);
        HashMap<String, Player> playersCopy = new HashMap<>(Game.players);
        for (Map.Entry<String, Player> e : playersCopy.entrySet()) {
            if (e.getValue() == p) {continue;}
            double[] XY = e.getValue().getXY();
            if (x > XY[0] && x < XY[0]+40 && y > XY[1] && y < XY[1]+60) {
                e.getValue().takeDamage(20);
                Game.bullets.remove(this);
            }
        }
        if (x > 1000 || x < 0 || y < 0 || y>590 || Game.inObstacle((int)getXY()[0], (int)getXY()[2], (int)getXY()[1], (int)getXY()[3]) > 0) {
            Game.bullets.remove(this);
        }
    }
    public Bullet(double x, double y, double angle, Player p) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.p = p;
    }
}
