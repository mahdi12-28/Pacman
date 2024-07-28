package org.example.pacman;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class HelloController implements EventHandler<KeyEvent> {
    File  file= new File("src\\main\\resources\\lose sfx 2.mp3");
    AudioClip sound1 = new AudioClip(file.toURI().toString());
    File  file2= new File("src\\main\\resources\\Applause3 sound effect.mp3");
    AudioClip sound2 = new AudioClip(file2.toURI().toString());
    private static final String[] levelFiles = {"D:\\Java\\Pacman\\src\\main\\resources\\org\\example\\pacman\\levels\\level0.txt",
            "D:\\Java\\Pacman\\src\\main\\resources\\org\\example\\pacman\\levels\\level1.txt",
            "D:\\Java\\Pacman\\src\\main\\resources\\org\\example\\pacman\\levels\\level2.txt",
            "D:\\Java\\Pacman\\src\\main\\resources\\org\\example\\pacman\\levels\\level3.txt"};
    final private static double FRAMES_PER_SECOND = 5.0;
    private static int ghostEatingModeCounter;
    private static int iceModeCounter;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Label gameOverLabel;
    @FXML
    private PacmanView pacManView;
    private PacmanModel pacmanModel;
    private Timer timer;
    private boolean paused;

    public HelloController() {
        this.paused = false;
    }

    public static void setGhostEatingModeCounter() {
        ghostEatingModeCounter = 25;
    }

    public static void setIceModeCounter() {
        iceModeCounter = 20;
    }

    public static int getGhostEatingModeCounter() {
        return ghostEatingModeCounter;
    }

    public static String getLevelFile(int x) {
        return levelFiles[x];
    }

    public void initialize() {
        String file = this.getLevelFile(0);
        this.pacmanModel = new PacmanModel();
        this.update(Direction.NONE);
        iceModeCounter = 20;
        ghostEatingModeCounter = 25;
        this.startTimer();
    }

    private void startTimer() {
        this.timer = new java.util.Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        update(pacmanModel.getCurrentDirection());
                    }
                });
            }
        };

        long frameTimeInMilliseconds = (long) (1000.0 / FRAMES_PER_SECOND);
        this.timer.schedule(timerTask, 0, frameTimeInMilliseconds);
    }

    private void update(Direction direction) {

        this.pacmanModel.step(direction);
        this.pacManView.update(pacmanModel);
        this.scoreLabel.setText(String.format("Score: %d", this.pacmanModel.getScore()));
        this.levelLabel.setText(String.format("Map: %d", this.pacmanModel.getLevel()+1));
        if (PacmanModel.isGameOver()) {
            this.gameOverLabel.setText(String.format("GAME OVER"));
            sound1.play();
            pause();
        }
        if (PacmanModel.AreYouWon()) {
            this.gameOverLabel.setText(String.format("YOU WON!"));
            sound2.play();
            pause();
        }
        if (PacmanModel.getGhostEatingMode()) {
            ghostEatingModeCounter--;
        }
        if (PacmanModel.getIceMode()) {
            iceModeCounter--;
            System.out.println(iceModeCounter);
        }
        if (ghostEatingModeCounter == 0)
            if (PacmanModel.getGhostEatingMode()) {
                PacmanModel.setGhostEatingMode(false);
            }
        if (iceModeCounter == 0 && PacmanModel.getIceMode()) {
            PacmanModel.setIceMode(false);
        }
    }

    @Override
    public void handle(KeyEvent keyEvent) {
        boolean keyRecognized = true;
        KeyCode code = keyEvent.getCode();
        Direction direction = Direction.NONE;
        if (code == KeyCode.LEFT) {
            direction = Direction.LEFT;
        } else if (code == KeyCode.RIGHT) {
            direction = Direction.RIGHT;
        } else if (code == KeyCode.UP) {
            direction = Direction.UP;
        } else if (code == KeyCode.DOWN) {
            direction = Direction.DOWN;
        } else if (code == KeyCode.W) {
            pause();
            this.pacmanModel.startNewGame(pacmanModel.getLevel());
            this.gameOverLabel.setText(String.format(""));
            paused = false;
            this.startTimer();
        } else if (code == KeyCode.A) {

            if (pacmanModel.getLevel() > 0) {
                pause();
                pacmanModel.setLevel(pacmanModel.getLevel()-1);

                this.pacmanModel.startNewGame(pacmanModel.getLevel());
                this.gameOverLabel.setText(String.format(""));
                paused = false;
                this.startTimer();
            }
            else {
                this.gameOverLabel.setText(String.format("It's first map!"));
            }
        }else if (code == KeyCode.D) {


            if (pacmanModel.getLevel() < levelFiles.length) {
                pause();
                pacmanModel.setLevel(pacmanModel.getLevel()+1);
                this.pacmanModel.startNewGame(pacmanModel.getLevel());
                this.gameOverLabel.setText(String.format(""));
                paused = false;
                this.startTimer();
            }
            else {
                this.gameOverLabel.setText(String.format("It's last map!"));
            }
        } else {
            keyRecognized = false;
        }
        if (keyRecognized) {
            keyEvent.consume();
            pacmanModel.setCurrentDirection(direction);
        }
    }

    @FXML
    void switchToNextMap(ActionEvent event) throws IOException {
        if (pacmanModel.getLevel() < levelFiles.length) {
            pause();
            pacmanModel.setLevel(pacmanModel.getLevel()+1);
            this.pacmanModel.startNewGame(pacmanModel.getLevel());
            this.gameOverLabel.setText(String.format(""));
            paused = false;
            this.startTimer();
        }
        else {
            this.gameOverLabel.setText(String.format("It's last map!"));
        }

        levelLabel.requestFocus();
    }
    @FXML
    void switchToPreMap(ActionEvent event) throws IOException {
        if (pacmanModel.getLevel() > 0) {
            pause();
            pacmanModel.setLevel(pacmanModel.getLevel()-1);

            this.pacmanModel.startNewGame(pacmanModel.getLevel());
            this.gameOverLabel.setText(String.format(""));
            paused = false;
            this.startTimer();
        }
        else {
            this.gameOverLabel.setText(String.format("It's first map!"));
        }
        levelLabel.requestFocus();

    }
    @FXML
    void switchToNewMap(ActionEvent event) throws IOException {
        pause();
        this.pacmanModel.startNewGame(pacmanModel.getLevel());
        this.gameOverLabel.setText(String.format(""));
        paused = false;
        this.startTimer();
        levelLabel.requestFocus();

    }

    public void pause() {
        this.timer.cancel();
        this.paused = true;
    }

    public double getBoardWidth() {
        return PacmanView.CELL_WIDTH * this.pacManView.getColumnCount();
    }

    public double getBoardHeight() {
        return PacmanView.CELL_WIDTH * this.pacManView.getRowCount();
    }

    public boolean getPaused() {
        return paused;
    }

}