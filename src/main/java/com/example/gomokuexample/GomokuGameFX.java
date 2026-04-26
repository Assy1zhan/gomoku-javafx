package com.example.gomokuexample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

public class GomokuGameFX extends Application {
    private static final int CELL_SIZE = 40;
    private static final int BOARD_SIZE = 20;
    private static final int BOARD_LENGTH = CELL_SIZE * BOARD_SIZE;
    private Label statusLabel;
    private Label totalMovesLabel;
    private Label blackMaxLabel;
    private Label whiteMaxLabel;
    private GomokuGame game;
    private javafx.animation.Timeline timer;
    private javafx.animation.Timeline revertTimer;   // for invalid message revert
    private Label timerLabel;
    private int timeLimitSeconds = 30;
    private int remainingSeconds;

    private static StackPane createBlock() {
        StackPane block = new StackPane();
        block.setMinSize(CELL_SIZE, CELL_SIZE);
        block.setPrefSize(CELL_SIZE, CELL_SIZE);
        block.setMaxSize(CELL_SIZE, CELL_SIZE);
        block.setStyle("-fx-border-color: black");
        return block;
    }

    private void updateStats() {
        totalMovesLabel.setText("Total Moves: " + game.getTotalMoves());
        blackMaxLabel.setText("Black max line: " + game.getMaxLineLength(1));
        whiteMaxLabel.setText("White max line: " + game.getMaxLineLength(2));
    }

    private void startTimer() {
        if (timer != null) {
            timer.stop();
        }
        remainingSeconds = timeLimitSeconds;
        timerLabel.setText("Time left: " + remainingSeconds + " sec");

        timer = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), e -> {
                    remainingSeconds--;
                    timerLabel.setText("Time left: " + remainingSeconds + " sec");
                    if (remainingSeconds <= 0) {
                        timer.stop();
                        game.switchTurnWithoutMove();
                        updateStats();
                        int nextPlayer = game.getCurrentPlayer();
                        statusLabel.setText("Player " + nextPlayer + "'s turn (" +
                                (nextPlayer == 1 ? "Black" : "White") + ") - Time's up! Next turn.");
                        startTimer();
                    }
                })
        );
        timer.setCycleCount(timeLimitSeconds);
        timer.play();
    }

    @Override
    public void start(Stage primaryStage) {
        game = new GomokuGame(BOARD_SIZE);
        BorderPane root = new BorderPane();

        // Menu bar
        MenuBar menuBar = new MenuBar();
        Menu gameMenu = new Menu("Game");
        MenuItem resetItem = new MenuItem("Reset");
        MenuItem exitItem = new MenuItem("Exit");
        gameMenu.getItems().addAll(resetItem, exitItem);
        menuBar.getMenus().add(gameMenu);
        root.setTop(menuBar);

        // Status label (will go inside right panel)
        statusLabel = new Label("Player 1's turn (Black)");
        statusLabel.setStyle("-fx-font-size: 12px; -fx-padding: 5px;");
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(240);
        statusLabel.setPrefHeight(60);   // enough for two lines
        statusLabel.setAlignment(Pos.CENTER);

        // Stats panel (right side)
        VBox statsPanel = new VBox(10);
        statsPanel.setStyle("-fx-padding: 15px; -fx-border-color: lightgray; -fx-border-width: 1px;");
        statsPanel.setAlignment(Pos.TOP_CENTER);
        statsPanel.setPrefWidth(280);    // wider to fit messages

        totalMovesLabel = new Label("Total Moves: 0");
        blackMaxLabel = new Label("Black max line: 0");
        whiteMaxLabel = new Label("White max line: 0");
        timerLabel = new Label("Time left: 30 sec");
        timerLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: darkblue;");

        statsPanel.getChildren().addAll(
                new Label("GAME STATS"),
                totalMovesLabel,
                blackMaxLabel,
                whiteMaxLabel,
                statusLabel,
                timerLabel
        );
        root.setRight(statsPanel);

        // Game board grid
        GridPane grid = new GridPane();
        StackPane[][] blocks = new StackPane[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                StackPane block = createBlock();
                blocks[row][col] = block;
                grid.add(block, col, row);

                final int r = row;
                final int c = col;
                block.setOnMouseClicked(e -> {
                    int currentPlayer = game.getCurrentPlayer();
                    if (game.move(r, c)) {
                        // Place stone
                        double circSize = 0.4 * CELL_SIZE;
                        Circle circle = new Circle(circSize);
                        Color color = currentPlayer == 1 ? Color.BLACK : Color.WHITE;
                        circle.setFill(color);
                        circle.setStroke(Color.BLACK);
                        block.getChildren().add(circle);

                        updateStats();
                        startTimer();

                        if (game.isGameOver()) {
                            if (timer != null) timer.stop();
                            if (game.getWinner() == 0) {
                                statusLabel.setText("GAME OVER!\nIt's a draw!");
                            } else {
                                statusLabel.setText("GAME OVER!\nPlayer " + game.getWinner() + " (" +
                                        (game.getWinner() == 1 ? "Black" : "White") + ") wins!");
                            }
                        } else {
                            int nextPlayer = game.getCurrentPlayer();
                            statusLabel.setText("Player " + nextPlayer + "'s turn (" +
                                    (nextPlayer == 1 ? "Black" : "White") + ")");
                        }
                    } else {
                        // Invalid move
                        statusLabel.setText("INVALID MOVE!\nCell occupied or game is over.");
                        // Cancel previous revert timer if any
                        if (revertTimer != null) revertTimer.stop();
                        revertTimer = new javafx.animation.Timeline(
                                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(2.5), ev -> {
                                    if (game.isGameOver()) {
                                        if (timer != null) timer.stop();
                                        if (game.getWinner() == 0) {
                                            statusLabel.setText("GAME OVER!\nIt's a draw!");
                                        } else {
                                            statusLabel.setText("GAME OVER!\nPlayer " + game.getWinner() + " (" +
                                                    (game.getWinner() == 1 ? "Black" : "White") + ") wins!");
                                        }
                                    } else {
                                        int nextPlayer = game.getCurrentPlayer();
                                        statusLabel.setText("Player " + nextPlayer + "'s turn (" +
                                                (nextPlayer == 1 ? "Black" : "White") + ")");
                                    }
                                })
                        );
                        revertTimer.setCycleCount(1);
                        revertTimer.play();
                    }
                });
            }
        }
        root.setCenter(grid);

        // Reset action
        resetItem.setOnAction(e -> {
            game = new GomokuGame(BOARD_SIZE);
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    blocks[i][j].getChildren().clear();
                }
            }
            updateStats();
            statusLabel.setText("Game reset!\nPlayer 1's turn (Black)");
            startTimer();
        });

        // Exit action
        exitItem.setOnAction(e -> primaryStage.close());

        Scene scene = new Scene(root, BOARD_LENGTH + 300, BOARD_LENGTH + 50);
        primaryStage.setTitle("Gomoku Game");
        primaryStage.setScene(scene);
        primaryStage.show();
        startTimer();
    }

    public static void main(String[] args) {
        launch(args);
    }
}