package Model;

public class GameStateManager {
    private static int score = 0;
    private static int highScore = 0;

    private GameStateManager() {}

    public static int getScore() {
        return score;
    }

    public static void incrementScore() {
        score++;
        if (score > highScore) {
            highScore = score;
        }
    }

    public static void addScore(int amount) {
        score += amount;
        if (score > highScore) {
            highScore = score;
        }
    }

    public static void resetScore() {
        score = 0;
    }

    public static int getHighScore() {
        return highScore;
    }
}