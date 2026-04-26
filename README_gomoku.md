# Gomoku (Five-in-a-Row)

A desktop Gomoku game built in Java with a JavaFX UI. Two-player hot-seat gameplay on a configurable board, with a turn timer and live statistics.

Built as a course project for **CSC1004 (Object-Oriented Programming)** at CUHK-Shenzhen.

## Features

- **Configurable board size** (5×5 up to 20×20).
- **Win detection in all 4 directions** — horizontal, vertical, and both diagonals.
- **Per-turn countdown timer** (30 seconds default) — if a player runs out of time, the turn is forfeited.
- **Live statistics** — total moves played, longest line for Black, longest line for White, updated after every move.
- **Menu-driven controls** — start a new game or reset at any time.
- **Clean separation of concerns** — pure-Java game engine is independent of the JavaFX UI.

## Architecture

```
┌─────────────────────┐            ┌──────────────────────┐
│  GomokuGameFX.java  │  ◀──uses──▶│  GomokuGame.java     │
│   (JavaFX UI)       │            │   (game engine)      │
│                     │            │                      │
│  - StackPane grid   │            │  - int[][] board     │
│  - Mouse handlers   │            │  - move() / checkWin │
│  - Timer animation  │            │  - getMaxLineLength  │
│  - Status labels    │            │  - state machine     │
└─────────────────────┘            └──────────────────────┘
```

The engine has no JavaFX imports — it's a pure Java class that could just as easily be plugged into a CLI front-end, a web service, or a unit-test suite.

## Tech stack

- **Java 21**
- **JavaFX 22** (`javafx-controls`, `javafx-fxml`)
- **Maven** for build
- **JUnit 5** (configured for future tests)

## How to run

### Prerequisites
- JDK 21 or higher
- Maven

### Run
```bash
mvn clean javafx:run
```

A window will open with a 20×20 board. Click any empty cell to place a stone. Black moves first, then White. Five in a row in any direction wins.

## What I learned

- Designing a clean engine/UI separation: the `GomokuGame` class has zero UI dependencies and is fully testable.
- JavaFX layout primitives (`GridPane`, `StackPane`, `BorderPane`) and event handling.
- `Timeline` / `KeyFrame` for time-based animations and turn timers.
- Direction-vector approach to win detection — encoding the four directions as `int[][]` arrays keeps the win check concise (~25 lines for full 4-directional check).

## Possible extensions

- **AI opponent** — minimax with alpha-beta pruning would be a natural next step given the game's branching factor.
- **Save / load games** — serialize the `int[][] board` and `currentPlayer` to disk.
- **Online multiplayer** — wrap the engine in a TCP server (a natural follow-on to my [chat room project](https://github.com/Assy1zhan/java-chat-room)).
- **Difficulty levels** — once an AI is in place, vary search depth.
