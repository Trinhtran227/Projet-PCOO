package com.Trinh21812006.ufoshoot;

public class GameModel {
    private int score;
    private boolean isGameOver;
    private boolean collisionOccurred; // Trạng thái chờ hiệu ứng nổ

    public GameModel() {
        reset();
    }

    public void reset() {
        score = 0;
        isGameOver = false;
        collisionOccurred = false;
    }

    public void addScore(int points) {
        this.score += points;
    }

    // --- Getters & Setters ---
    public int getScore() { return score; }

    public boolean isGameOver() { return isGameOver; }
    public void setGameOver(boolean gameOver) { this.isGameOver = gameOver; }

    public boolean isCollisionOccurred() { return collisionOccurred; }
    public void setCollisionOccurred(boolean occurred) { this.collisionOccurred = occurred; }
}
