import java.util.Random;
import java.util.Scanner;

public class Sudoku {
    private static final int SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private static final int EMPTY = 0;
    private int[][] board;

    // ANSI escape codes for colors
    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";

    public Sudoku() {
        board = new int[SIZE][SIZE];
        generateSudoku();
    }

    private void generateSudoku() {
        fillDiagonal();
        fillRemaining(0, SUBGRID_SIZE);
        removeDigits();
    }

    private void fillDiagonal() {
        for (int i = 0; i < SIZE; i += SUBGRID_SIZE) {
            fillSubGrid(i, i);
        }
    }

    private boolean isValid(int row, int col, int num) {
        for (int x = 0; x < SIZE; x++) {
            if (board[row][x] == num || board[x][col] == num) {
                return false;
            }
        }
        int startRow = row - row % SUBGRID_SIZE;
        int startCol = col - col % SUBGRID_SIZE;
        for (int i = 0; i < SUBGRID_SIZE; i++) {
            for (int j = 0; j < SUBGRID_SIZE; j++) {
                if (board[i + startRow][j + startCol] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    private void fillSubGrid(int row, int col) {
        Random random = new Random();
        for (int i = 0; i < SUBGRID_SIZE; i++) {
            for (int j = 0; j < SUBGRID_SIZE; j++) {
                int num;
                do {
                    num = random.nextInt(SIZE) + 1;
                } while (!isSafeInSubGrid(row, col, num));
                board[row + i][col + j] = num;
            }
        }
    }

    private boolean isSafeInSubGrid(int row, int col, int num) {
        int startRow = row - row % SUBGRID_SIZE;
        int startCol = col - col % SUBGRID_SIZE;
        for (int i = 0; i < SUBGRID_SIZE; i++) {
            for (int j = 0; j < SUBGRID_SIZE; j++) {
                if (board[i + startRow][j + startCol] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean fillRemaining(int i, int j) {
        if (j >= SIZE && i < SIZE - 1) {
            i++;
            j = 0;
        }
        if (i >= SIZE && j >= SIZE) {
            return true;
        }
        if (i < SUBGRID_SIZE) {
            if (j < SUBGRID_SIZE) {
                j = SUBGRID_SIZE;
            }
        } else if (i < SIZE - SUBGRID_SIZE) {
            if (j == (i / SUBGRID_SIZE) * SUBGRID_SIZE) {
                j = j + SUBGRID_SIZE;
            }
        } else {
            if (j == SIZE - SUBGRID_SIZE) {
                i++;
                j = 0;
                if (i >= SIZE) {
                    return true;
                }
            }
        }
        for (int num = 1; num <= SIZE; num++) {
            if (isValid(i, j, num)) {
                board[i][j] = num;
                if (fillRemaining(i, j + 1)) {
                    return true;
                }
                board[i][j] = EMPTY;
            }
        }
        return false;
    }

    private void removeDigits() {
        Random random = new Random();
        int count = 20; // Adjust the number of empty cells for difficulty
        while (count != 0) {
            int cellId = random.nextInt(SIZE * SIZE);
            int i = cellId / SIZE;
            int j = cellId % SIZE;
            if (board[i][j] != EMPTY) {
                count--;
                board[i][j] = EMPTY;
            }
        }
    }

    public boolean solve() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    for (int num = 1; num <= SIZE; num++) {
                        if (isValid(i, j, num)) {
                            board[i][j] = num;
                            if (solve()) {
                                return true;
                            }
                            board[i][j] = EMPTY;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    public void printBoard() {
        System.out.print("    ");
        for (int i = 1; i <= SIZE; i++) {
            System.out.print(CYAN + i + "   " + RESET);
        }
        System.out.println();
        for (int i = 0; i < SIZE; i++) {
            System.out.print(CYAN + " " + (i + 1) + " " + RESET);
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    System.out.print(" . ");
                } else {
                    System.out.print(YELLOW + " " + board[i][j] + " " + RESET);
                }
                if ((j + 1) % SUBGRID_SIZE == 0 && j < SIZE - 1) {
                    System.out.print(" | ");
                }
            }
            System.out.println();
            if ((i + 1) % SUBGRID_SIZE == 0 && i < SIZE - 1) {
                System.out.println("    ------+-------+------");
            }
        }
    }

    public void userSolve() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printBoard();
            System.out.println("Enter your move (format: row column number, e.g., 1 1 5) or 'exit' to quit:");
            try {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting the game. Goodbye!");
                    break;
                }
                String[] parts = input.split(" ");
                if (parts.length != 3) {
                    System.out.println("Invalid input format. Please use the format: row column number.");
                    continue;
                }
                int row = Integer.parseInt(parts[0]) - 1;
                int col = Integer.parseInt(parts[1]) - 1;
                int num = Integer.parseInt(parts[2]);
                if (row < 0 || col < 0 || row >= SIZE || col >= SIZE || num < 1 || num > 9) {
                    System.out.println("Invalid input. Try again.");
                    continue;
                }
                if (board[row][col] != EMPTY) {
                    System.out.println("Cell already filled. Try again.");
                    continue;
                }
                if (!isValid(row, col, num)) {
                    System.out.println("Invalid move. Try again.");
                    continue;
                }
                board[row][col] = num;
                if (isBoardComplete()) {
                    printBoard();
                    System.out.println("Congratulations! You've solved the Sudoku puzzle!");
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter numbers only.");
            }
        }
        scanner.close();
    }

    private boolean isBoardComplete() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Sudoku sudoku = new Sudoku();
        sudoku.printBoard();
        System.out.println("Do you want to solve the Sudoku yourself? (yes/no)");
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("yes")) {
            sudoku.userSolve();
        } else {
            System.out.println("Solving the Sudoku puzzle:");
            long startTime = System.currentTimeMillis();
            if (sudoku.solve()) {
                long endTime = System.currentTimeMillis();
                sudoku.printBoard();
                System.out.println("Solved in " + (endTime - startTime) + "ms");
            } else {
                System.out.println("No solution exists");
            }
        }
        scanner.close();
    }
}
