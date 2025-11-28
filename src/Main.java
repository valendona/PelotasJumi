import model.GameModel;
import view.GameView;
import view.StartScreen;
import controller.GameController;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Asteroids MVC");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            StartScreen startScreen = new StartScreen(() -> {
                GameModel model = new GameModel();
                GameView view = new GameView(model);
                GameController controller = new GameController(model, view, frame);

                frame.getContentPane().removeAll();
                frame.add(view);
                frame.revalidate();
                frame.repaint();

                controller.startGameLoop();
            });

            frame.getContentPane().removeAll();
            frame.add(startScreen);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
