package org.jair.tetris.tetris;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.input.UserAction;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.sql.Time;
import java.util.Random;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;
import static com.almasb.fxgl.dsl.FXGL.*;

public class TetrisApplication extends GameApplication {

    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;

    private Timeline moveRightTimeline;
    private Timeline moveLeftTimeline;



    private static int currentPieceX;
    private static int currentPieceY;

    private static int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];

    private static int[][] currentPiece;

    private  int[][][] pieces = new int[][][]
    {
            //pieceL
            {
                    {0,1,0},
                    {0,1,0},
                    {0,1,1}
            },
            // pieceJ,
            {
                    {0,2,0},
                    {0,2,0},
                    {2,2,0}
            },
            // pieceT,
            {
                    {0,3,0},
                    {3,3,3},
                    {0,0,0}
            },
            // pieceO,
            {
                    {4,4},
                    {4,4}
            },
            // pieceS,
            {
                    {0,5,5},
                    {5,5,0},
                    {0,0,0}
            },
            // pieceZ,
            {
                    {6,6,0},
                    {0,6,6},
                    {0,0,0},
            },
            // pieceI
            {
                    {0,0,0,0},
                    {7,7,7,7},
                    {0,0,0,0},
                    {0,0,0,0}
            }
    };

    private Color[] pieceColors = {
            Color.RED,
            Color.BLUE,
            Color.ORANGE,
            Color.YELLOW,
            Color.GREEN,
            Color.PURPLE,
            Color.CYAN
    };


    private  final int CELL_SIZE = 30;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Tetris Game");
        settings.setVersion("0.1");
    }
    @Override
    protected void initGame() {

        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x <  BOARD_WIDTH; x++) {
                Rectangle cell = new Rectangle(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                cell.setFill(Color.GRAY);

                getGameScene().addUINode(cell);
                board[y][x] = 0;
            }
        }

        Timeline gameLoop = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateGame()));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();

        spawnPiece();
    }

    @Override
    protected void initInput() {
        moveRightTimeline = new Timeline(new KeyFrame(Duration.millis(200), e -> movePiece(1)));
        moveRightTimeline.setCycleCount(Timeline.INDEFINITE);

        moveLeftTimeline = new Timeline(new KeyFrame(Duration.millis(200), e -> movePiece(-1)));
        moveLeftTimeline.setCycleCount(Timeline.INDEFINITE);

        getInput().addAction(new UserAction("Move Right") {
            @Override
            protected void onActionBegin() {
                movePiece(1);
                moveRightTimeline.play();
            }

            @Override
            protected void onActionEnd() {
                moveRightTimeline.stop();
            }

//            @Override
//            protected void onAction() {
//                movePiece(1);
//
//            }
        }, KeyCode.RIGHT);
        getInput().addAction(new UserAction("Move Left") {
            @Override
            protected void onActionBegin() {
                movePiece(-1);
                moveLeftTimeline.play();
            }
            @Override
            protected void onActionEnd() {
                moveLeftTimeline.stop();

            }
        }, KeyCode.LEFT);

        getInput().addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                movePieceDown();
            }
        }, KeyCode.DOWN);
        getInput().addAction(new UserAction("Rotate") {
            @Override
            protected void onActionBegin() {
                rotatePiece();
            }
            @Override
            protected void onActionEnd()
            {
                rotatePiece();
            }
        }, KeyCode.UP);
    }

    private void rotatePiece()
    {
        int[][] rotatedPiece = new int[currentPiece[0].length][currentPiece.length];
        for(int i = 0; i < currentPiece.length; i++)
        {
            for(int j = 0; j < currentPiece[i].length; j++)
            {
                rotatedPiece[j][currentPiece.length - 1 - i] = currentPiece[i][j];
            }
        }
        for(int i = 0; i < rotatedPiece.length; i++)
        {
            for(int j = 0; j < rotatedPiece[i].length; j++)
            {
                if(currentPieceX + j >= BOARD_WIDTH || currentPieceY + i >= BOARD_HEIGHT || currentPieceX + j < 0 || currentPieceY + i < 0 || board[currentPieceY + i][currentPieceX + j] != 0)
                {
                    return;
                }
            }
        }
        currentPiece = rotatedPiece;
        drawPiece();
    }

    private void movePiece(int direction)
    {
        for(int i = 0; i < currentPiece.length;i++)
        {
            for(int j = (direction > 0) ? currentPiece[i].length - 1 : 0; (direction > 0) ? j >= 0 : j < currentPiece[i].length;j-=direction)
            {
                if(currentPiece[i][j] != 0)
                {
                    if(currentPieceX + j + direction >= BOARD_WIDTH || currentPieceX + j + direction < 0 || board[currentPieceY + i][currentPieceX + j + direction] != 0)
                    {
                        return;
                    }
                }
            }
        }
        currentPieceX += direction;
        drawPiece();
    }

    private void movePieceDown()
    {
        for(int i = 0; i < currentPiece.length;i++)
        {
            for(int j = 0; j < currentPiece[i].length;j++)
            {
                if(currentPiece[i][j] != 0)
                {
                    if(currentPieceY + i + 1 >= BOARD_HEIGHT || board[currentPieceY + i + 1][currentPieceX + j] != 0)
                    {
                        return;
                    }
                }
            }
        }
        currentPieceY++;
        drawPiece();
    }

    @Override
    protected void initPhysics() {
        // Aquí puedes configurar las reglas de física de tu juego
    }

    @Override
    protected void initUI() {
        // Aquí puedes configurar la interfaz de usuario de tu juego
    }

    private void spawnPiece()
    {
        Random random = new Random();
        int randomPiece = random.nextInt(pieces.length);
        currentPiece = pieces[randomPiece];

        currentPieceX = BOARD_WIDTH / 2;
        currentPieceY = 0;

        for(int i = 0; i< currentPiece.length; i++)
        {
            for(int j = 0; j< currentPiece[i].length; j++)
            {
                if(currentPiece[i][j] == 1)
                {
                    Rectangle cell = new Rectangle((j + currentPieceX) * CELL_SIZE, (i + currentPieceY) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    cell.setFill(pieceColors[currentPiece[i][j] - 1]);
                    getGameScene().addUINode(cell);
                }

                if(currentPiece[i][j] == 2)
                {
                    Rectangle cell = new Rectangle((j + currentPieceX) * CELL_SIZE, (i + currentPieceY) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    cell.setFill(pieceColors[currentPiece[i][j] - 1]);
                    getGameScene().addUINode(cell);
                }

                if(currentPiece[i][j] == 3)
                {
                    Rectangle cell = new Rectangle((j + currentPieceX) * CELL_SIZE, (i + currentPieceY) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    cell.setFill(pieceColors[currentPiece[i][j] - 1]);
                    getGameScene().addUINode(cell);
                }

                if(currentPiece[i][j] == 4)
                {
                    Rectangle cell = new Rectangle((j + currentPieceX) * CELL_SIZE, (i + currentPieceY) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    cell.setFill(pieceColors[currentPiece[i][j] - 1]);
                    getGameScene().addUINode(cell);
                }

                if(currentPiece[i][j] == 5)
                {
                    Rectangle cell = new Rectangle((j + currentPieceX) * CELL_SIZE, (i + currentPieceY) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    cell.setFill(pieceColors[currentPiece[i][j] - 1]);
                    getGameScene().addUINode(cell);
                }

                if(currentPiece[i][j] == 6)
                {
                    Rectangle cell = new Rectangle((j + currentPieceX) * CELL_SIZE, (i + currentPieceY) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    cell.setFill(pieceColors[currentPiece[i][j] - 1]);
                    getGameScene().addUINode(cell);
                }

                if(currentPiece[i][j] == 7)
                {
                    Rectangle cell = new Rectangle((j + currentPieceX) * CELL_SIZE, (i + currentPieceY) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    cell.setFill(pieceColors[currentPiece[i][j] - 1]);
                    getGameScene().addUINode(cell);
                }

            }
        }
    }

    private void updateGame() {
        if (currentPiece == null || !moveDown()) {
            updateBoardWithCurrentPiece();
            spawnPiece();
        }else {
            currentPieceY++;
            drawPiece();
        }
    }

    private void drawPiece() {

        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                if (board[y][x] == 0) {
                    Rectangle cell = new Rectangle(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    cell.setFill(Color.GRAY);
                    getGameScene().addUINode(cell);
                }
            }
        }
        for (int i = 0; i < currentPiece.length; i++) {
            for (int j = 0; j < currentPiece[i].length; j++) {
                if (currentPiece[i][j] != 0) {
                    Rectangle cell = new Rectangle((j + currentPieceX) * CELL_SIZE, (i + currentPieceY) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    cell.setFill(pieceColors[currentPiece[i][j] - 1]);
                    getGameScene().addUINode(cell);
                }
            }
        }
    }

    private boolean moveDown() {
        for (int y = 0; y < currentPiece.length; y++) {
            for (int x = 0; x < currentPiece[y].length; x++) {
                if (currentPiece[y][x] != 0) {
                    if (currentPieceY + y + 1 >= BOARD_HEIGHT || board[currentPieceY + y + 1][currentPieceX + x] != 0) {
                        return false;
                    }
                }
            }
        }

        // Si la pieza puede moverse hacia abajo, actualiza su posición en el tablero
        for (int y = currentPiece.length - 1; y >= 0; y--) {
            for (int x = 0; x < currentPiece[y].length; x++) {
                if (currentPiece[y][x] != 0) {
                    board[y + 1][x] = board[y][x];
                    board[y][x] = 0;
                }
            }
        }
        return true;
    }

    private static void updateBoardWithCurrentPiece() {
        for (int i = 0; i < currentPiece.length; i++) {
            for (int j = 0; j < currentPiece[i].length; j++) {
                if (currentPiece[i][j] != 0) {
                    board[currentPieceY + i][currentPieceX + j] = currentPiece[i][j];
                }
            }
        }
    }

    private static   void printBoard() {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
    public static void main(String[] args) {
        launch(args);
        updateBoardWithCurrentPiece();
        printBoard();

    }
}