package model;

public class Bullet {
    private double x, y;
    private double dx, dy;

    public Bullet(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.dx = Math.cos(angle) * 5;
        this.dy = Math.sin(angle) * 5;
    }

    public void update() {
        x += dx;
        y += dy;
    }

    public double getX() { return x; }
    public double getY() { return y; }
}
