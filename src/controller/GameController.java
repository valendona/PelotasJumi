package controller;

import model.GameModel;
import view.GameView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameController {
    private final GameModel model;
    private final GameView view;
    private Timer timer;
    private final JFrame frame;
    @SuppressWarnings("unused")
    private long lastTickTime;
    @SuppressWarnings("unused")
    private Object lastActionSource;

    // Bandera para evitar mostrar múltiples diálogos simultáneamente
    private boolean dialogVisible = false;

    public GameController(GameModel model, GameView view, JFrame frame) {
        this.model = model;
        this.view = view;
        this.frame = frame;
        // Antes: view.setFocusable(true); setupKeyBindings(); ahora la view ya configura los bindings
        view.setFocusable(true);
    }

    public void startGameLoop() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        timer = new Timer(16, (ActionEvent e) -> {
            lastTickTime = e.getWhen();

            // Aplicar controles continuos según el estado de teclas en la view
            if (!model.isGameOver()) {
                if (view.isLeftPressed()) model.getShip().rotateLeft();
                if (view.isRightPressed()) model.getShip().rotateRight();
                if (view.isUpPressed()) model.getShip().accelerate();
                if (view.isDownPressed()) model.getShip().brake();
            }

            model.update();
            view.repaint();
            if (model.isGameOver()) {
                showGameOverDialog();
            } else if (!dialogVisible && model.getScore() >= 1000) {
                // Victoria alcanzada
                showVictoryDialog();
            }
        });
        timer.start();
        SwingUtilities.invokeLater(view::requestFocusInWindow);
    }

    private void showGameOverDialog() {
        if (dialogVisible) return;
        dialogVisible = true;
        if (timer != null && timer.isRunning()) {
            // Antes de detener, limpiar estados de teclas para que no queden activos al reiniciar
            view.resetKeyStates();
            if (model != null && model.getShip() != null) model.getShip().setThrusting(false);
            timer.stop();
        }

        JDialog dialog = new JDialog(frame, true);
        dialog.setUndecorated(true);
        dialog.getContentPane().setBackground(Color.BLACK);
        dialog.setSize(420, 220);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(frame);

        // Panel principal con BoxLayout para centrar verticalmente
        JPanel main = new JPanel();
        main.setBackground(Color.BLACK);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("GAME OVER", SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Arial", Font.BOLD, 36));
        label.setForeground(Color.WHITE);

        main.add(Box.createVerticalGlue());
        main.add(label);
        main.add(Box.createVerticalStrut(20));

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.BLACK);

        JButton restartButton = new JButton("Reiniciar");
        restartButton.setForeground(Color.WHITE);
        restartButton.setBackground(Color.DARK_GRAY);
        restartButton.setFocusPainted(false);
        restartButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        restartButton.addActionListener((ActionEvent e) -> {
            lastActionSource = e.getSource();
            dialogVisible = false;
            // Reiniciar
            model.resetGame();
            dialog.dispose();
            startGameLoop();
            view.requestFocusInWindow();
        });

        JButton quitButton = new JButton("Salir");
        quitButton.setForeground(Color.WHITE);
        quitButton.setBackground(Color.DARK_GRAY);
        quitButton.setFocusPainted(false);
        quitButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        quitButton.addActionListener((ActionEvent e) -> {
            lastActionSource = e.getSource();
            dialog.dispose();
            if (frame != null) {
                frame.dispose();
            }
            System.exit(0);
        });

        // Estilizar y agregar botones
        restartButton.setPreferredSize(new Dimension(120, 36));
        quitButton.setPreferredSize(new Dimension(120, 36));
        buttonPanel.add(restartButton);
        buttonPanel.add(quitButton);

        main.add(buttonPanel);
        main.add(Box.createVerticalGlue());

        dialog.setContentPane(main);

        dialog.getRootPane().setDefaultButton(restartButton);

        dialog.getRootPane().registerKeyboardAction((ActionEvent e) -> {
            lastActionSource = e.getSource();
            dialog.dispose();
            if (frame != null) frame.dispose();
            System.exit(0);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        dialog.setVisible(true);
    }

    private void showVictoryDialog() {
        if (dialogVisible) return;
        dialogVisible = true;
        if (timer != null && timer.isRunning()) {
            // Limpiar estados de teclas para que no queden activos al reiniciar
            view.resetKeyStates();
            if (model != null && model.getShip() != null) model.getShip().setThrusting(false);
            timer.stop();
        }

        JDialog dialog = new JDialog(frame, true);
        dialog.setUndecorated(true);
        dialog.getContentPane().setBackground(Color.BLACK);
        dialog.setSize(420, 220);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(frame);

        JPanel main = new JPanel();
        main.setBackground(Color.BLACK);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("¡FELICIDADES!", SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Arial", Font.BOLD, 36));
        label.setForeground(Color.WHITE);

        JLabel msg = new JLabel("Has alcanzado 1000 puntos.", SwingConstants.CENTER);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);
        msg.setFont(new Font("Arial", Font.PLAIN, 18));
        msg.setForeground(Color.WHITE);

        main.add(Box.createVerticalGlue());
        main.add(label);
        main.add(Box.createVerticalStrut(10));
        main.add(msg);
        main.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.BLACK);

        JButton restartButton = new JButton("Reiniciar");
        restartButton.setForeground(Color.WHITE);
        restartButton.setBackground(Color.DARK_GRAY);
        restartButton.setFocusPainted(false);
        restartButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        restartButton.setFocusable(false);
        restartButton.addActionListener((ActionEvent e) -> {
            dialogVisible = false;
            model.resetGame();
            dialog.dispose();
            startGameLoop();
            view.requestFocusInWindow();
        });

        JButton quitButton = new JButton("Salir");
        quitButton.setForeground(Color.WHITE);
        quitButton.setBackground(Color.DARK_GRAY);
        quitButton.setFocusPainted(false);
        quitButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        quitButton.setFocusable(false);
        quitButton.addActionListener((ActionEvent e) -> {
            dialogVisible = false;
            dialog.dispose();
            if (frame != null) frame.dispose();
            System.exit(0);
        });

        restartButton.setPreferredSize(new Dimension(120, 36));
        quitButton.setPreferredSize(new Dimension(120, 36));
        buttonPanel.add(restartButton);
        buttonPanel.add(quitButton);

        main.add(buttonPanel);
        main.add(Box.createVerticalGlue());

        dialog.setContentPane(main);

        dialog.getRootPane().registerKeyboardAction((ActionEvent e) -> {
            dialogVisible = false;
            dialog.dispose();
            if (frame != null) frame.dispose();
            System.exit(0);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        dialog.setVisible(true);
    }
}
