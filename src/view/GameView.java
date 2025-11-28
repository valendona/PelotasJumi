package view;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameView extends JPanel {
    private final GameModel model;

    // Estados de teclas (moved here from controller)
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;

    public GameView(GameModel model) {
        this.model = model;
        setBackground(Color.BLACK);
        // Hacer foco y configurar bindings de teclado aquí
        setFocusable(true);
        setupKeyBindings();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!model.isGameOver()) {
            Graphics2D g2 = (Graphics2D) g.create();

            // Activar antialiasing para suavizar la nave y la llama
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Dibujar Nave con alpha según proximidad a bordes
            Ship ship = model.getShip();
            double sx = ship.getX();
            double sy = ship.getY();
            int[] xp = ship.getXPoints();
            int[] yp = ship.getYPoints();
            float alphaShip = computeAlpha(sx, sy, 15, w, h);
            Composite oldComp = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaShip));

            // Si la nave está acelerando, dibujar la llama antes o después según estética
            if (ship.isThrusting()) {
                double flameLength = 8; // longitud adicional desde la base trasera (reducida)
                int shipRadius = 15; // radio aproximado de la nave (coincide con getXPoints/getYPoints)

                int bx1 = xp[1];
                int by1 = yp[1];
                int bx2 = xp[2];
                int by2 = yp[2];

                double narrowFactor = 0.5;
                int nbx1 = (int) Math.round(sx + (bx1 - sx) * narrowFactor);
                int nby1 = (int) Math.round(sy + (by1 - sy) * narrowFactor);
                int nbx2 = (int) Math.round(sx + (bx2 - sx) * narrowFactor);
                int nby2 = (int) Math.round(sy + (by2 - sy) * narrowFactor);

                double theta = ship.getAngle();
                int tipX = (int) Math.round(sx - Math.cos(theta) * (shipRadius + flameLength));
                int tipY = (int) Math.round(sy - Math.sin(theta) * (shipRadius + flameLength));

                int[] fx = new int[]{nbx1, nbx2, tipX};
                int[] fy = new int[]{nby1, nby2, tipY};

                // Dibujar la llama
                g2.setColor(Color.BLACK);
                g2.fillPolygon(fx, fy, 3);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawPolygon(fx, fy, 3);
                g2.setStroke(new BasicStroke(1f));
            }

            g2.setColor(Color.WHITE);
            g2.fillPolygon(xp, yp, 3);
            g2.setComposite(oldComp);

            // Asteroides
            for (Asteroid a : model.getAsteroids()) {
                double ax = a.getX();
                double ay = a.getY();
                int r = (int) Math.round(a.getRadius());
                float alpha = computeAlpha(ax, ay, r, w, h);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                int cx = (int) Math.round(ax);
                int cy = (int) Math.round(ay);
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);
                g2.setComposite(oldComp);
            }

            // Balas
            for (Bullet b : model.getBullets()) {
                double bx = b.getX();
                double by = b.getY();
                float alpha = computeAlpha(bx, by, 2, w, h);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2.fillRect((int) Math.round(bx), (int) Math.round(by), 2, 2);
                g2.setComposite(oldComp);
            }

            // HUD
            g2.setColor(Color.WHITE);
            Font oldFont = g2.getFont();
            g2.setFont(oldFont.deriveFont(Font.BOLD, oldFont.getSize() + 6f));
            g2.drawString("Score: " + model.getScore(), 10, 26);
            g2.setFont(oldFont);

            g2.dispose();
        }
    }

    // Calcula alpha en función de la proximidad a bordes; 1.0 = totalmente visible, 0.0 = invisible
    private float computeAlpha(double x, double y, double radius, int width, int height) {
        float ax = 1.0f;
        if (x < radius) {
            ax = (float) Math.max(0.0, x / radius);
        } else if (x > width - radius) {
            ax = (float) Math.max(0.0, (width - x) / radius);
        }

        float ay = 1.0f;
        if (y < radius) {
            ay = (float) Math.max(0.0, y / radius);
        } else if (y > height - radius) {
            ay = (float) Math.max(0.0, (height - y) / radius);
        }

        float alpha = Math.min(ax, ay);
        // limitar entre 0.0 y 1.0
        if (Float.isNaN(alpha)) return 1.0f;
        return Math.max(0.0f, Math.min(1.0f, alpha));
    }

    // Getters para que el controller pueda leer el estado si lo necesita
    public boolean isLeftPressed() { return leftPressed; }
    public boolean isRightPressed() { return rightPressed; }
    public boolean isUpPressed() { return upPressed; }
    public boolean isDownPressed() { return downPressed; }

    // Resetea los estados de teclas y la propulsión de la nave
    public void resetKeyStates() {
        leftPressed = rightPressed = upPressed = downPressed = false;
        if (model != null && model.getShip() != null) model.getShip().setThrusting(false);
    }

    // Configuración de Key Bindings movida desde el controlador
    private void setupKeyBindings() {
        InputMap im = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "left.pressed");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "left.released");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "right.pressed");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "right.released");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "up.pressed");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "up.released");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "down.pressed");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "down.released");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "space.pressed");

        am.put("left.pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { leftPressed = true; }
        });
        am.put("left.released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { leftPressed = false; }
        });

        am.put("right.pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { rightPressed = true; }
        });
        am.put("right.released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { rightPressed = false; }
        });

        am.put("up.pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                upPressed = true;
                if (!model.isGameOver()) model.getShip().setThrusting(true);
            }
        });
        am.put("up.released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                upPressed = false;
                if (!model.isGameOver()) model.getShip().setThrusting(false);
            }
        });

        am.put("down.pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { downPressed = true; }
        });
        am.put("down.released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { downPressed = false; }
        });

        am.put("space.pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!model.isGameOver()) model.shoot();
            }
        });
    }
}
