package model;

public class Ship {
    private double x, y;
    private double angle;
    private double dx, dy;
    private static final double MAX_SPEED = 5.0;
    private boolean thrusting = false;

    public Ship(double x, double y) {
        this.x = x;
        this.y = y;
        this.angle = -Math.PI / 2;
        this.dx = 0;
        this.dy = 0;
    }

    public void rotateLeft() { angle -= 0.1; }
    public void rotateRight() { angle += 0.1; }
    public void accelerate() {
        double thrust = 0.12; // antes 0.2
        dx += Math.cos(angle) * thrust;
        dy += Math.sin(angle) * thrust;
        limitSpeed();
    }

    public void brake() {
        dx *= 0.95;
        dy *= 0.95;
    }

    private void limitSpeed() {
        double speed = Math.hypot(dx, dy);
        if (speed > MAX_SPEED) {
            dx = dx / speed * MAX_SPEED;
            dy = dy / speed * MAX_SPEED;
        }
    }

    public void update(int width, int height) {
        x += dx;
        y += dy;
        if (x < 0) x = width;
        if (x > width) x = 0;
        if (y < 0) y = height;
        if (y > height) y = 0;
    }

    public int[] getXPoints() {
        int[] xp = new int[3];
        xp[0] = (int)(x + Math.cos(angle) * 15);
        xp[1] = (int)(x + Math.cos(angle + 2.5) * 15);
        xp[2] = (int)(x + Math.cos(angle - 2.5) * 15);
        return xp;
    }

    public int[] getYPoints() {
        int[] yp = new int[3];
        yp[0] = (int)(y + Math.sin(angle) * 15);
        yp[1] = (int)(y + Math.sin(angle + 2.5) * 15);
        yp[2] = (int)(y + Math.sin(angle - 2.5) * 15);
        return yp;
    }

    public Bullet shoot() {
        double bx = x + Math.cos(angle) * 15;
        double by = y + Math.sin(angle) * 15;
        return new Bullet(bx, by, angle);
    }

    // Estado de empuje
    public boolean isThrusting() { return thrusting; }
    public void setThrusting(boolean thrusting) { this.thrusting = thrusting; }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getAngle() { return angle; }
}
