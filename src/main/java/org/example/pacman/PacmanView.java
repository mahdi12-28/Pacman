package org.example.pacman;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class PacmanView extends Group  {


    public final static double CELL_WIDTH = 40.0;
    @FXML
    private int rowCount;
    @FXML
    private int columnCount;
    private ImageView[][] cellViews;
    private Image pacmanRightImage;
    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image ghost1Image;
    private Image ghost2Image;
    private Image blueGhostImage;
    private Image wallImage;
    private Image bigDotImage;
    private Image smallDotImage;
    private Image iceDotImage;
    private Image grave;


    public PacmanView() {
        this.pacmanRightImage = new Image("pacmanRight.gif");
        this.pacmanUpImage = new Image("pacmanUp.gif");
        this.pacmanDownImage = new Image("pacmanDown.gif");
        this.pacmanLeftImage = new Image("pacmanLeft.gif");
        this.ghost1Image = new Image("ghost1.gif");
        this.ghost2Image = new Image("ghost2.gif");
        this.blueGhostImage = new Image("ghost3.gif");
        this.wallImage = new Image("0.png");
        this.bigDotImage = new Image("dot.gif");
        this.smallDotImage = new Image("dot2.png");
        this.iceDotImage = new Image("ice.png");
        this.grave = new Image("grave.png");
    }

    private void initializeGrid() {
        if (this.rowCount > 0 && this.columnCount > 0) {
            this.cellViews = new ImageView[this.rowCount][this.columnCount];
            for (int row = 0; row < this.rowCount; row++) {
                for (int column = 0; column < this.columnCount; column++) {
                    ImageView imageView = new ImageView();
                    imageView.setX((double)column * CELL_WIDTH);
                    imageView.setY((double)row * CELL_WIDTH);
                    imageView.setFitWidth(CELL_WIDTH);
                    imageView.setFitHeight(CELL_WIDTH);
                    this.cellViews[row][column] = imageView;
                    this.getChildren().add(imageView);
                }
            }
        }
    }

    public void update(PacmanModel model) {
        assert model.getRowCount() == this.rowCount && model.getColumnCount() == this.columnCount;
        for (int row = 0; row < this.rowCount; row++){
            for (int column = 0; column < this.columnCount; column++){
                CellValue value = model.getCellValue(row, column);
                if (value == CellValue.WALL) {
                    this.cellViews[row][column].setImage(this.wallImage);
                }
                else if (value == CellValue.DoubleDOT) {
                    this.cellViews[row][column].setImage(this.bigDotImage);
                }
                else if (value == CellValue.DOT) {
                    this.cellViews[row][column].setImage(this.smallDotImage);
                }
                else if (value == CellValue.ICE) {
                    this.cellViews[row][column].setImage(this.iceDotImage);
                }
                else if (value == CellValue.GHOST1 || value == CellValue.GHOST2) {
                    this.cellViews[row][column].setImage(this.grave);
                }
                else {
                    this.cellViews[row][column].setImage(null);
                }
                if (row == model.getPacmanLocation().getX() && column == model.getPacmanLocation().getY() && PacmanModel.isGameOver())
                    this.cellViews[row][column].setImage(this.grave);
                else if (row == model.getPacmanLocation().getX() && column == model.getPacmanLocation().getY() && (PacmanModel.getLastDirection() == Direction.RIGHT || PacmanModel.getLastDirection() == Direction.NONE)) {
                    this.cellViews[row][column].setImage(this.pacmanRightImage);
                }
                else if (row == model.getPacmanLocation().getX() && column == model.getPacmanLocation().getY() && PacmanModel.getLastDirection() == Direction.LEFT) {
                    this.cellViews[row][column].setImage(this.pacmanLeftImage);
                }
                else if (row == model.getPacmanLocation().getX() && column == model.getPacmanLocation().getY() && PacmanModel.getLastDirection() == Direction.UP) {
                    this.cellViews[row][column].setImage(this.pacmanUpImage);
                }
                else if (row == model.getPacmanLocation().getX() && column == model.getPacmanLocation().getY() && PacmanModel.getLastDirection() == Direction.DOWN) {
                    this.cellViews[row][column].setImage(this.pacmanDownImage);
                }

                if (PacmanModel.getGhostEatingMode() && (HelloController.getGhostEatingModeCounter() == 6 ||HelloController.getGhostEatingModeCounter() == 4 || HelloController.getGhostEatingModeCounter() == 2)) {
                    if (row == model.getGhost1Location().getX() && column == model.getGhost1Location().getY()) {
                        this.cellViews[row][column].setImage(this.ghost1Image);
                    }
                    if (row == model.getGhost2Location().getX() && column == model.getGhost2Location().getY()) {
                        this.cellViews[row][column].setImage(this.ghost2Image);
                    }
                }
                else if (PacmanModel.getGhostEatingMode()) {
                    if (row == model.getGhost1Location().getX() && column == model.getGhost1Location().getY()) {
                        this.cellViews[row][column].setImage(this.blueGhostImage);
                    }
                    if (row == model.getGhost2Location().getX() && column == model.getGhost2Location().getY()) {
                        this.cellViews[row][column].setImage(this.blueGhostImage);
                    }
                }
                else {
                    if (row == model.getGhost1Location().getX() && column == model.getGhost1Location().getY()) {
                        this.cellViews[row][column].setImage(this.ghost1Image);
                    }
                    if (row == model.getGhost2Location().getX() && column == model.getGhost2Location().getY()) {
                        this.cellViews[row][column].setImage(this.ghost2Image);
                    }
                }
            }
        }
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
        this.initializeGrid();
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
        this.initializeGrid();
    }
}
