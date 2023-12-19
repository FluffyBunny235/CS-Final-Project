import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class UFO extends Special_Ability{
    private Player player;
    private double x;
    private double y;
    private Image image;
    private int frame;
    public UFO(Player p) {
        Game.sendCommands.add("UFO " + player.user);
        try {
            image = ImageIO.read(new File("src/UFO.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.player = p;
        frame = 0;
    }
    @Override
    public void move() {
        x = player.getXY()[0];
        y = player.getXY()[1];
        if (frame%6 == 0) {
            player.takeDamage(1);
        }
    }
    @Override
    public void die() {
        Game.abilities.remove(this);
    }
}
