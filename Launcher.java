import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public final class Launcher {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame window =
                    new JFrame("Craft4k");


            Craft4k game =
                    new Craft4k();


            window.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE);


            window.setContentPane(game);


            window.pack();


            window.setLocationRelativeTo(null);


            window.setResizable(false);


            window.setVisible(true);


            game.requestFocusInWindow();


            game.start();
        });
    }
}