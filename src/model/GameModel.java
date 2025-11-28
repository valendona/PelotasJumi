package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameModel {
    private Ship ship;
    private List<Asteroid> asteroids;
    private List<Bullet> bullets;
    private int score;
    private boolean gameOver;
    private final int WIDTH = 800, HEIGHT = 600;
    private final Random random = new Random();
    // Límite máximo de asteroides simultáneos en pantalla
    private final int MAX_ASTEROIDS = 10;

    public GameModel() {
        resetGame();
    }

    public void resetGame() {
        ship = new Ship(WIDTH / 2.0, HEIGHT / 2.0);
        asteroids = new ArrayList<>();
        bullets = new ArrayList<>();
        score = 0;
        gameOver = false;

        for (int i = 0; i < 5; i++) {
            if (asteroids.size() < MAX_ASTEROIDS) {
                asteroids.add(Asteroid.createOutsideScreen(WIDTH, HEIGHT, 40));
            }
        }
    }

    public Ship getShip() { return ship; }
    public List<Asteroid> getAsteroids() { return asteroids; }
    public List<Bullet> getBullets() { return bullets; }
    public int getScore() { return score; }
    public boolean isGameOver() { return gameOver; }
    public int getWidthArea() { return WIDTH; }
    public int getHeightArea() { return HEIGHT; }

    public void update() {
        if (gameOver) return; // no actualizar si está en game over

        ship.update(WIDTH, HEIGHT);
        for (Asteroid a : asteroids) a.update(WIDTH, HEIGHT);
        for (Bullet b : bullets) b.update();

        // Colisiones bala–asteroide
        List<Asteroid> destroyed = new ArrayList<>();
        List<Bullet> used = new ArrayList<>();
        List<Asteroid> newAsteroids = new ArrayList<>();

        for (Bullet b : bullets) {
            for (Asteroid a : asteroids) {
                if (a.collidesWith(b)) {
                    destroyed.add(a);
                    used.add(b);
                    score += 10;
                    for (Asteroid child : a.split()) {
                        // Añadir hijos sólo si no superamos el límite
                        if (asteroids.size() + newAsteroids.size() - destroyed.size() < MAX_ASTEROIDS) {
                            newAsteroids.add(child);
                        }
                    }
                }
            }
        }

        asteroids.removeAll(destroyed);
        bullets.removeAll(used);
        asteroids.addAll(newAsteroids);

        // Colisión nave–asteroide
        for (Asteroid a : asteroids) {
            if (a.collidesWith(ship)) {
                gameOver = true;
                break;
            }
        }

        if (score < 1000) {
            if (asteroids.size() < MAX_ASTEROIDS) {
                if (random.nextDouble() < 0.02) {
                    asteroids.add(Asteroid.createOutsideScreen(WIDTH, HEIGHT, 30 + random.nextInt(30)));
                }
            }
        }
    }

    public void shoot() {
        if (!gameOver) bullets.add(ship.shoot());
    }

    public void addAsteroidOutsideScreen(int radius) {
        if (asteroids.size() < MAX_ASTEROIDS) {
            asteroids.add(Asteroid.createOutsideScreen(WIDTH, HEIGHT, radius));
        }
    }
}
