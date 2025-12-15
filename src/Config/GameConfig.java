// src/Config/GameConfig.java

package Config;

public class GameConfig {
    private Game game;
    private Player player;
    private Platforms platforms;
    private Generation generation;

    public Game getGame() {
        return game;
    }

    public Player getPlayer() {
        return player;
    }

    public Platforms getPlatforms() {
        return platforms;
    }

    public Generation getGeneration() {
        return generation;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setPlatforms(Platforms platforms) {
        this.platforms = platforms;
    }

    public void setGeneration(Generation generation) {
        this.generation = generation;
    }

    // Klasy wewnętrzne (nested classes) odpowiadające sekcjom w YAML
    public static class Game {
        private String title;
        private int width;
        private int height;
        private double initial_speed;
        private double max_speed;
        private int fps;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public double getInitial_speed() {
            return initial_speed;
        }

        public void setInitial_speed(double initial_speed) {
            this.initial_speed = initial_speed;
        }

        public double getMax_speed() {
            return max_speed;
        }

        public void setMax_speed(double max_speed) {
            this.max_speed = max_speed;
        }

        public int getFps() {
            return fps;
        }

        public void setFps(int fps) {
            this.fps = fps;
        }
    }

    public static class Player {
        private double gravity;
        private double jump_power;
        private double horizontal_speed;
        private int width;
        private int height;
        private int jump_window_frames;
        private double jump_time_window;

        public double getJump_buffer_duration() {
            return jump_buffer_duration;
        }

        public void setJump_buffer_duration(double jump_buffer_duration) {
            this.jump_buffer_duration = jump_buffer_duration;
        }

        private double jump_buffer_duration;

        public double getGravity() {
            return gravity;
        }

        public void setGravity(double gravity) {
            this.gravity = gravity;
        }

        public double getJump_power() {
            return jump_power;
        }

        public void setJump_power(double jump_power) {
            this.jump_power = jump_power;
        }

        public double getHorizontal_speed() {
            return horizontal_speed;
        }

        public void setHorizontal_speed(double horizontal_speed) {
            this.horizontal_speed = horizontal_speed;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public double getJump_time_window() {
            return jump_time_window;
        }

        public void setJump_time_window(double jump_time_window) {
            this.jump_time_window = jump_time_window;
        }
    }

    public static class Platforms {
        private int base_height;
        private int min_width;
        private int max_width;
        private double speed;
        private int spacing;
        private double auto_jump_chance;

        public double getAuto_jump_chance() {
            return auto_jump_chance;
        }

        public void setAuto_jump_chance(double auto_jump_chance) {
            this.auto_jump_chance = auto_jump_chance;
        }

        public int getBase_height() {
            return base_height;
        }

        public void setBase_height(int base_height) {
            this.base_height = base_height;
        }

        public int getMin_width() {
            return min_width;
        }

        public void setMin_width(int min_width) {
            this.min_width = min_width;
        }

        public int getMax_width() {
            return max_width;
        }

        public void setMax_width(int max_width) {
            this.max_width = max_width;
        }

        public double getSpeed() {
            return speed;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }

        public int getSpacing() {
            return spacing;
        }

        public void setSpacing(int spacing) {
            this.spacing = spacing;
        }
    }

    public static class Generation {
        private int min_gap_y;
        private int max_gap_y;
        private int spawn_margin_top;
        private int spawn_rate;

        public int getMin_gap_y() {
            return min_gap_y;
        }

        public void setMin_gap_y(int min_gap_y) {
            this.min_gap_y = min_gap_y;
        }

        public int getMax_gap_y() {
            return max_gap_y;
        }

        public void setMax_gap_y(int max_gap_y) {
            this.max_gap_y = max_gap_y;
        }

        public int getSpawn_margin_top() {
            return spawn_margin_top;
        }

        public void setSpawn_margin_top(int spawn_margin_top) {
            this.spawn_margin_top = spawn_margin_top;
        }

        public int getSpawn_rate() {
            return spawn_rate;
        }

        public void setSpawn_rate(int spawn_rate) {
            this.spawn_rate = spawn_rate;
        }
    }
}