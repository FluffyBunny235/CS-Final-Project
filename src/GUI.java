import javax.swing.*;

public class GUI{
    public GUI() {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        GUI_Panel panel = new GUI_Panel();
        window.add(panel);

        window.pack();
        window.setResizable(false);
        window.setTitle("Battalion Bash");
        window.setVisible(true);
    }
}
