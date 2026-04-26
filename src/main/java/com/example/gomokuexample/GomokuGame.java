package com.example.gomokuexample;

class GomokuGame {
    private int[][] board;          // 0: empty, 1: player1's stone, 2: player2's stone
    private int currentPlayer;      // 1: player1, 2: player2
    private boolean gameOver;       // true: game over, false: game not over
    private int winner;             // 0: no winner, 1: player 1 wins, 2: player 2 wins

    private int boardSize;
    private int maxMoves;
    private int totalMoves;

    public GomokuGame(int boardSize) {
        if (boardSize < 5 || boardSize > 20) {
            throw new IllegalArgumentException("Board size should be between 5 and 20.");
        }
        this.boardSize = boardSize;
        maxMoves = boardSize * boardSize;
        totalMoves = 0;
        board = new int[boardSize][boardSize];
        currentPlayer = 1;
        gameOver = false;
        winner = 0;
    }

    public GomokuGame() {
        this(20);
    }

    public void switchTurnWithoutMove() {
        if (!gameOver) {
            currentPlayer = (currentPlayer == 1) ? 2 : 1;
        }
    }

    public boolean checkWin(int x, int y) {
        int[][][] directionLines = {
                {{0, 1}, {0, -1}},   // vertical
                {{1, 0}, {-1, 0}},   // horizontal
                {{1, 1}, {-1, -1}},  // diagonal
                {{1, -1}, {-1, 1}}   // anti-diagonal
        };
        for (int[][] oppositeDirs : directionLines) {
            int count = 1;
            for (int[] direction : oppositeDirs) {
                int dx = direction[0];
                int dy = direction[1];
                for (int i = 1; i < 5; i++) {
                    int newX = x + i * dx;
                    int newY = y + i * dy;
                    if (!isValidPosition(newX, newY) || board[newX][newY] != board[x][y]) {
                        break;
                    }
                    count++;
                    if (count >= 5) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean move(int x, int y) {
        if (gameOver) {
            return false;
        }
        if (!isValidPosition(x, y)) {
            return false;
        }
        if (board[x][y] != 0) {
            return false;
        }

        board[x][y] = currentPlayer;
        totalMoves++;

        if (totalMoves == maxMoves) {
            gameOver = true;
            return true;
        }

        if (checkWin(x, y)) {
            gameOver = true;
            winner = currentPlayer;
        }
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
        return true;
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < boardSize && y >= 0 && y < boardSize;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getWinner() {
        return winner;
    }

    public int[][] getBoard() {
        return board;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getTotalMoves() {
        return totalMoves;
    }

    public int getMaxLineLength(int player) {
        int maxLen = 0;
        int[][] dirs = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == player) {
                    for (int[] dir : dirs) {
                        int dx = dir[0];
                        int dy = dir[1];
                        int count = 1;
                        // positive direction
                        for (int step = 1; step < 5; step++) {
                            int ni = i + step * dx;
                            int nj = j + step * dy;
                            if (ni < 0 || ni >= boardSize || nj < 0 || nj >= boardSize) break;
                            if (board[ni][nj] == player) count++;
                            else break;
                        }
                        // negative direction
                        for (int step = 1; step < 5; step++) {
                            int ni = i - step * dx;
                            int nj = j - step * dy;
                            if (ni < 0 || ni >= boardSize || nj < 0 || nj >= boardSize) break;
                            if (board[ni][nj] == player) count++;
                            else break;
                        }
                        if (count > maxLen) maxLen = count;
                    }
                }
            }
        }
        return maxLen;
    }
}