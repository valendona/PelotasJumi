package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class StartScreen extends JPanel {
    public StartScreen(Runnable onStart) {
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.gridx = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1.0;
        c.weighty = 1.0;

        JLabel title = new JLabel("ASTEROIDS");
        title.setFont(new Font("Arial", Font.BOLD, 44));
        title.setForeground(Color.WHITE);

        JLabel controls = new JLabel("<html><div style='color:white; font-size:14px;'>" +
                "Controles:<br/>" +
                "- Flechas: mover<br/>" +
                "- Barra espaciadora: disparar<br/>" +
                "</div></html>");
        controls.setForeground(Color.WHITE);

        JButton startButton = new JButton("Iniciar");
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(Color.DARK_GRAY);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        startButton.setPreferredSize(new Dimension(240, 72));
        startButton.setFont(startButton.getFont().deriveFont(Font.BOLD, 20f));
        startButton.setMargin(new Insets(16, 32, 16, 32));
        startButton.addActionListener(e -> onStart.run());

        // Crear un panel contenedor vertical y centrar sus elementos
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        controls.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(12));
        content.add(controls);
        content.add(Box.createVerticalStrut(16));
        content.add(startButton);

        c.gridy = 0;
        add(content, c);

        // Permitir iniciar con Enter aunque el botón no tenga foco
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "start");
        getActionMap().put("start", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onStart.run();
            }
        });
    }
}
