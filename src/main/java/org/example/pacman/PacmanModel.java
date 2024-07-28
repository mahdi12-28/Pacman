package org.example.pacman;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.media.AudioClip;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class PacmanModel {
    File  file= new File("src\\main\\resources\\Pain Yell Sound Effect.mp3");
    AudioClip sound = new AudioClip(file.toURI().toString());
    File  file2= new File("src\\main\\resources\\Correct Buzzer Sound Effect.mp3");
    AudioClip sound2 = new AudioClip(file2.toURI().toString());
    File  file3= new File("src\\main\\resources\\Twitch Follower Alert Sound Effect SFX.mp3");
    AudioClip sound3 = new AudioClip(file3.toURI().toString());
    File  file4= new File("src\\main\\resources\\Twitch Follower Alert Sound Effect SFX.mp3");
    AudioClip sound4 = new AudioClip(file4.toURI().toString());
    final int DOT_SCORE = 20;
    final int DOUBLE_DOT_SCORE = 45;
    final int GHOST_EAT_SCORE = 120;
    final int GHOST_ICE_SCORE = 75;
    @FXML
    private int rowCount;
    @FXML
    private int columnCount;
    private CellValue[][] grid;
    private int score;
    private int level;
    private int dotCount;
    private static boolean gameOver;
    private static boolean youWon;
    private static boolean ghostEatingMode;
    private static boolean iceMode;
    private Point2D pacmanLocation;
    private Point2D pacmanVelocity;
    private Point2D ghost1Location;
    private Point2D ghost1Velocity;
    private Point2D ghost1OldVelocity;
    private Point2D ghost2Location;
    private Point2D ghost2Velocity;
    private Point2D ghost2OldVelocity;
    private static Direction lastDirection;
    private static Direction currentDirection;

    public PacmanModel() {
        this.startNewGame(0);
    }

    public void startNewGame(int level) {
        gameOver = false;
        youWon = false;
        ghostEatingMode = false;
        iceMode = false;
        dotCount = 0;
        rowCount = 0;
        columnCount = 0;
        this.score = 0;
        this.level = level;
        this.initializeLevel(HelloController.getLevelFile(level));
    }
    public void initializeLevel(String fileName) {
        File file = new File(fileName);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);

        } catch (FileNotFoundException e) {
            System.out.println(file.exists());
            e.printStackTrace();
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            while (lineScanner.hasNext()) {
                lineScanner.next();
                columnCount++;
            }
            rowCount++;
        }
        columnCount = columnCount/rowCount;

        Scanner scanner2 = null;
        try {
            scanner2 = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        grid = new CellValue[rowCount][columnCount];

        int row = 0;
        int pacmanRow = 0;
        int pacmanColumn = 0;
        int ghost1Row = 0;
        int ghost1Column = 0;
        int ghost2Row = 0;
        int ghost2Column = 0;

        while(scanner2.hasNextLine()){
            int column = 0;
            String line= scanner2.nextLine();
            Scanner lineScanner = new Scanner(line);
            while (lineScanner.hasNext()){
                String value = lineScanner.next();
                CellValue thisValue;
                switch (value) {
                    case "W" :
                        thisValue = CellValue.WALL;
                        break;
                    case "S" :
                        thisValue = CellValue.DOT;
                        dotCount++;
                        break;
                    case "B" :
                        thisValue = CellValue.DoubleDOT;
                        dotCount++;
                        break;
                    case "I" :
                        thisValue = CellValue.ICE;
                        dotCount++;
                        break;
                    case "1" :
                        thisValue = CellValue.GHOST1;
                        ghost1Row = row;
                        ghost1Column = column;
                        break;
                    case "2" :
                        thisValue = CellValue.GHOST2;
                        ghost2Row = row;
                        ghost2Column = column;
                        break;
                    case "P" :
                        thisValue = CellValue.PACMAN;
                        pacmanRow = row;
                        pacmanColumn = column;
                        break;
                    default :
                            thisValue = CellValue.EMPTY;
                }
                grid[row][column] = thisValue;
                column++;
            }
            row++;
        }

        pacmanLocation = new Point2D(pacmanRow, pacmanColumn);
        pacmanVelocity = new Point2D(0,0);
        ghost1Location = new Point2D(ghost1Row,ghost1Column);
        ghost1Velocity = new Point2D(-1, 0);
        ghost2Location = new Point2D(ghost2Row,ghost2Column);
        ghost2Velocity = new Point2D(-1, 0);
        currentDirection = Direction.NONE;
        lastDirection = Direction.NONE;
    }

    public void startNextLevel() {
        if (this.isLevelComplete()) {

            this.level++;
            rowCount = 0;
            columnCount = 0;
            youWon = false;
            ghostEatingMode = false;
            iceMode=false;
            try {
                this.initializeLevel(HelloController.getLevelFile(level - 1));
            }
            catch (ArrayIndexOutOfBoundsException e) {
                youWon = true;
                gameOver = true;
                level--;
            }
        }
    }

    public void movePacman(Direction direction) {
        Point2D potentialPacmanVelocity = changeVelocity(direction);
        Point2D potentialPacmanLocation = pacmanLocation.add(potentialPacmanVelocity);

        if (direction.equals(lastDirection)) {
            if (grid[(int) potentialPacmanLocation.getX()][(int) potentialPacmanLocation.getY()] == CellValue.WALL){
                pacmanVelocity = changeVelocity(Direction.NONE);
                setLastDirection(Direction.NONE);
            }
            else {
                pacmanVelocity = potentialPacmanVelocity;
                pacmanLocation = potentialPacmanLocation;
            }
        }

        else {
            if (grid[(int) potentialPacmanLocation.getX()][(int) potentialPacmanLocation.getY()] == CellValue.WALL){
                potentialPacmanVelocity = changeVelocity(lastDirection);
                potentialPacmanLocation = pacmanLocation.add(potentialPacmanVelocity);
                if (grid[(int) potentialPacmanLocation.getX()][(int) potentialPacmanLocation.getY()] == CellValue.WALL){
                    pacmanVelocity = changeVelocity(Direction.NONE);
                    setLastDirection(Direction.NONE);
                }
                else {
                    pacmanVelocity = changeVelocity(lastDirection);
                    pacmanLocation = pacmanLocation.add(pacmanVelocity);
                }
            }
            else {
                pacmanVelocity = potentialPacmanVelocity;
                pacmanLocation = potentialPacmanLocation;
                setLastDirection(direction);
            }
        }
    }
    public Point2D changeVelocity(Direction direction){
        if(direction == Direction.LEFT){
            return new Point2D(0,-1);
        }
        else if(direction == Direction.RIGHT){
            return new Point2D(0,1);
        }
        else if(direction == Direction.UP){
            return new Point2D(-1,0);
        }
        else if(direction == Direction.DOWN){
            return new Point2D(1,0);
        }
        else{
            return new Point2D(0,0);
        }
    }
    public void moveGhosts() {
        Point2D[] ghost1 = moveAGhost(ghost1Velocity, ghost1Location);
        Point2D[] ghost2 = moveAGhost(ghost2Velocity, ghost2Location);
        ghost1Velocity = ghost1[0];
        ghost1Location = ghost1[1];
        ghost2Velocity = ghost2[0];
        ghost2Location = ghost2[1];
    }

    public Point2D[] moveAGhost(Point2D velocity, Point2D location){
        if (!iceMode) {
            Random generator = new Random();
            if (!ghostEatingMode) {
                if (location.getY() == pacmanLocation.getY()) {
                    if (location.getX() > pacmanLocation.getX()) {
                        velocity = changeVelocity(Direction.UP);
                    } else {
                        velocity = changeVelocity(Direction.DOWN);
                    }
                    Point2D potentialLocation = location.add(velocity);
                    while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {
                        int randomNum = generator.nextInt(4);
                        Direction direction = intToDirection(randomNum);
                        velocity = changeVelocity(direction);
                        potentialLocation = location.add(velocity);
                    }
                    location = potentialLocation;
                } else if (location.getX() == pacmanLocation.getX()) {
                    if (location.getY() > pacmanLocation.getY()) {
                        velocity = changeVelocity(Direction.LEFT);
                    } else {
                        velocity = changeVelocity(Direction.RIGHT);
                    }
                    Point2D potentialLocation = location.add(velocity);
                    while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {
                        int randomNum = generator.nextInt(4);
                        Direction direction = intToDirection(randomNum);
                        velocity = changeVelocity(direction);
                        potentialLocation = location.add(velocity);
                    }
                    location = potentialLocation;
                } else {
                    if (velocity.equals(changeVelocity(Direction.NONE))){
                        int randomNum = generator.nextInt(4);
                        Direction direction = intToDirection(randomNum);
                        velocity = changeVelocity(direction);
                    }

                    Point2D potentialLocation = location.add(velocity);
                    while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {

                        int randomNum = generator.nextInt(4);
                        Direction direction = intToDirection(randomNum);
                        velocity = changeVelocity(direction);
                        potentialLocation = location.add(velocity);
                    }
                    location = potentialLocation;
                }
            }
            else {
                if (location.getY() == pacmanLocation.getY()) {
                    if (location.getX() > pacmanLocation.getX()) {
                        velocity = changeVelocity(Direction.DOWN);
                    } else {
                        velocity = changeVelocity(Direction.UP);
                    }
                    Point2D potentialLocation = location.add(velocity);
                    while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {
                        int randomNum = generator.nextInt(4);
                        Direction direction = intToDirection(randomNum);
                        velocity = changeVelocity(direction);
                        potentialLocation = location.add(velocity);
                    }
                    location = potentialLocation;
                } else if (location.getX() == pacmanLocation.getX()) {
                    if (location.getY() > pacmanLocation.getY()) {
                        velocity = changeVelocity(Direction.RIGHT);
                    } else {
                        velocity = changeVelocity(Direction.LEFT);
                    }
                    Point2D potentialLocation = location.add(velocity);
                    while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {
                        int randomNum = generator.nextInt(4);
                        Direction direction = intToDirection(randomNum);
                        velocity = changeVelocity(direction);
                        potentialLocation = location.add(velocity);
                    }
                    location = potentialLocation;
                } else {
                    if (velocity.equals(changeVelocity(Direction.NONE))){
                        int randomNum = generator.nextInt(4);
                        Direction direction = intToDirection(randomNum);
                        velocity = changeVelocity(direction);
                    }
                    Point2D potentialLocation = location.add(velocity);
                    while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {

                        int randomNum = generator.nextInt(4);
                        Direction direction = intToDirection(randomNum);
                        velocity = changeVelocity(direction);
                        potentialLocation = location.add(velocity);
                    }
                    location = potentialLocation;
                }
            }
            return new Point2D[]{velocity, location};
        }
        else {
            velocity = changeVelocity(Direction.NONE);
            return new Point2D[]{velocity, location};
        }
    }
    public Direction intToDirection(int x){
        if (x == 0){
            return Direction.LEFT;
        }
        else if (x == 1){
            return Direction.RIGHT;
        }
        else if(x == 2){
            return Direction.UP;
        }
        else{
            return Direction.DOWN;
        }
    }

    public void sendGhost1Home() {
        sound.play();

        for (int row = 0; row < this.rowCount; row++) {
            for (int column = 0; column < this.columnCount; column++) {
                if (grid[row][column] == CellValue.GHOST1) {
                    ghost1Location = new Point2D(row, column);
                }
            }
        }
        ghost1Velocity = new Point2D(-1, 0);
    }
    public void sendGhost2Home() {
        for (int row = 0; row < this.rowCount; row++) {
            for (int column = 0; column < this.columnCount; column++) {
                if (grid[row][column] == CellValue.GHOST2) {
                    ghost2Location = new Point2D(row, column);
                }
            }
        }
        ghost2Velocity = new Point2D(-1, 0);
    }

    public void step(Direction direction) {
        this.movePacman(direction);
        CellValue pacmanLocationCellValue = grid[(int) pacmanLocation.getX()][(int) pacmanLocation.getY()];
        if (pacmanLocationCellValue == CellValue.DOT) {
            grid[(int) pacmanLocation.getX()][(int) pacmanLocation.getY()] = CellValue.EMPTY;
            dotCount--;
            sound2.setVolume(5);
            sound2.play();
            score += DOT_SCORE;
        }
        if (pacmanLocationCellValue == CellValue.DoubleDOT) {
            grid[(int) pacmanLocation.getX()][(int) pacmanLocation.getY()] = CellValue.EMPTY;
            dotCount--;
            sound3.play();
            score += DOUBLE_DOT_SCORE;
            ghostEatingMode = true;
            HelloController.setGhostEatingModeCounter();
        }
        if (pacmanLocationCellValue == CellValue.ICE) {
            grid[(int) pacmanLocation.getX()][(int) pacmanLocation.getY()] = CellValue.EMPTY;
            dotCount--;
            sound4.play();
            score += DOT_SCORE;
            iceMode = true;
            HelloController.setIceModeCounter();
        }

        if (iceMode){
            if (pacmanLocation.equals(ghost1Location)) {
                sendGhost1Home();
                score += GHOST_ICE_SCORE;
            }
            if (pacmanLocation.equals(ghost2Location)) {
                sendGhost2Home();
                score += GHOST_ICE_SCORE;
            }
        }
        if (ghostEatingMode) {
            if (pacmanLocation.equals(ghost1Location)) {
                sendGhost1Home();
                score += GHOST_EAT_SCORE;
            }
            if (pacmanLocation.equals(ghost2Location)) {
                sendGhost2Home();
                score += GHOST_EAT_SCORE;
            }
        }
        else {
            if (pacmanLocation.equals(ghost1Location)) {
                gameOver = true;
                pacmanVelocity = new Point2D(0,0);
            }
            if (pacmanLocation.equals(ghost2Location)) {
                gameOver = true;
                pacmanVelocity = new Point2D(0,0);
            }
        }
        this.moveGhosts();
        if (ghostEatingMode) {
            if (pacmanLocation.equals(ghost1Location)) {
                sendGhost1Home();
                score += GHOST_EAT_SCORE;
            }
            if (pacmanLocation.equals(ghost2Location)) {
                sendGhost2Home();
                score += GHOST_EAT_SCORE;
            }
        }
        else {
            if (pacmanLocation.equals(ghost1Location)) {
                gameOver = true;
                pacmanVelocity = new Point2D(0,0);
            }
            if (pacmanLocation.equals(ghost2Location)) {
                gameOver = true;
                pacmanVelocity = new Point2D(0,0);
            }
        }
        if (this.isLevelComplete()) {
            pacmanVelocity = new Point2D(0,0);
            youWon=true;
//            startNextLevel();
        }
    }

    public static boolean AreYouWon() {
        return youWon;
    }
    public boolean isLevelComplete() {
        return this.dotCount == 0;
    }

    public static boolean isGameOver() {
        return gameOver;
    }
    public void addToScore(int points) {
        this.score += points;
    }

    // getter setter

    public static boolean getGhostEatingMode() {
        return ghostEatingMode;
    }
    public static boolean getIceMode() {
        return iceMode;
    }

    public static void setGhostEatingMode(boolean ghostEatingModeBool) {
        ghostEatingMode = ghostEatingModeBool;
    }
    public static void setIceMode(boolean IceModeBool) {
        iceMode = IceModeBool;
    }
    public CellValue[][] getGrid() {
        return grid;
    }

    public void setLastDirection(Direction direction) {
        lastDirection = direction;
    }

    public CellValue getCellValue(int row, int column) {
        if (row >= 0 && row < this.grid.length && column >= 0 && column < this.grid[0].length)
             return this.grid[row][column];
        else
            return null;
    }

    public static Direction getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(Direction direction) {
        currentDirection = direction;
    }

    public static Direction getLastDirection() {
        return lastDirection;
    }
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getDotCount() {
        return dotCount;
    }

    public void setDotCount(int dotCount) {
        this.dotCount = dotCount;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public Point2D getPacmanLocation() {
        return pacmanLocation;
    }

    public void setPacmanLocation(Point2D pacmanLocation) {
        this.pacmanLocation = pacmanLocation;
    }

    public Point2D getGhost1Location() {
        return ghost1Location;
    }

    public void setGhost1Location(Point2D ghost1Location) {
        this.ghost1Location = ghost1Location;
    }

    public Point2D getGhost2Location() {
        return ghost2Location;
    }

    public void setGhost2Location(Point2D ghost2Location) {
        this.ghost2Location = ghost2Location;
    }

    public Point2D getPacmanVelocity() {
        return pacmanVelocity;
    }

    public void setPacmanVelocity(Point2D velocity) {
        this.pacmanVelocity = velocity;
    }

    public Point2D getGhost1Velocity() {
        return ghost1Velocity;
    }

    public void setGhost1Velocity(Point2D ghost1Velocity) {
        this.ghost1Velocity = ghost1Velocity;
    }

    public Point2D getGhost2Velocity() {
        return ghost2Velocity;
    }

    public void setGhost2Velocity(Point2D ghost2Velocity) {
        this.ghost2Velocity = ghost2Velocity;
    }
}
