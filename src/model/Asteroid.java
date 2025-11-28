package model;

import java.util.Random;

public class Asteroid {
    private double x, y;
    private final double dx, dy;
    private final double radius;
    private final boolean canSplit; // solo se parte una vez
    private static final Random rand = new Random();

    public Asteroid(double x, double y, double radius, boolean canSplit) {
        this(x, y, radius, canSplit, (rand.nextDouble() - 0.5) * 2, (rand.nextDouble() - 0.5) * 2);
    }

    public Asteroid(double x, double y, double radius, boolean canSplit, double dx, double dy) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.canSplit = canSplit;
        this.dx = dx;
        this.dy = dy;
    }

    public static Asteroid createOutsideScreen(int width, int height, double radius) {
        int side = rand.nextInt(4);
        double x = 0, y = 0;
        switch (side) {
            case 0 -> { x = -radius; y = rand.nextDouble() * height; }
            case 1 -> { x = width + radius; y = rand.nextDouble() * height; }
            case 2 -> { x = rand.nextDouble() * width; y = -radius; }
            case 3 -> { x = rand.nextDouble() * width; y = height + radius; }
        }

        double targetX = width * (0.4 + rand.nextDouble() * 0.2); // punto dentro del centro-ish
        double targetY = height * (0.4 + rand.nextDouble() * 0.2);
        double dirX = targetX - x;
        double dirY = targetY - y;
        double len = Math.hypot(dirX, dirY);
        if (len == 0) {
            dirX = (rand.nextDouble() - 0.5);
            dirY = (rand.nextDouble() - 0.5);
            len = Math.hypot(dirX, dirY);
        }
        // velocidad base y un poco de variación aleatoria
        double speed = 0.5 + rand.nextDouble() * 1.5; // entre 0.5 y 2.0
        double dx = (dirX / len) * speed + (rand.nextDouble() - 0.5) * 0.2;
        double dy = (dirY / len) * speed + (rand.nextDouble() - 0.5) * 0.2;

        return new Asteroid(x, y, radius, true, dx, dy);
    }

    public void update(int width, int height) {
        x += dx;
        y += dy;
        // Teletransportar sólo cuando el asteroide haya cruzado completamente el borde
        if (x < -radius) x = width + radius;
        if (x > width + radius) x = -radius;
        if (y < -radius) y = height + radius;
        if (y > height + radius) y = -radius;
    }

    public boolean collidesWith(Bullet b) {
        double dist = Math.hypot(x - b.getX(), y - b.getY());
        return dist < radius;
    }

    public boolean collidesWith(Ship s) {
        double dist = Math.hypot(x - s.getX(), y - s.getY());
        return dist < radius + 10; // margen de la nave
    }

    public Asteroid[] split() {
        if (!canSplit || radius <= 10) return new Asteroid[0];
        return new Asteroid[]{
                new Asteroid(x, y, radius / 2, false),
                new Asteroid(x, y, radius / 2, false)
        };
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getRadius() { return radius; }
}
