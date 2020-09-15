import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Board {
    private final int[][] board;
    private final int n; // board dimension
    private int manhattan, hamming;
    private int zeroId; // id of zero tile (begin from 0)


    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        board = array2DCopy(tiles);
        n = board.length;
        hammanh(); // hamming and manh
    }

    // matrix copy method
    private int[][] array2DCopy(int[][] array) {

        int l1 = array.length;
        int l2 = array[0].length;
        int[][] arrayCopy = new int[l1][l2];
        for (int i = 0; i < l1; i++) {
            for (int j = 0; j < l2; j++) {
                if (array[i][j] == 0)
                    zeroId = i * l2 + j;
                arrayCopy[i][j] = array[i][j];
            }
        }
        return arrayCopy;
    }


    private class Neighbors implements Iterable<Board> {
        public Iterator<Board> iterator() {
            return new iterator();
        }

        private class iterator implements Iterator<Board> {
            private final int[][] moves; // Matrix of possible moves
            private final int rowZer; // zero tile row number
            private final int colZer; // zero tile column number
            private int currPos; // Current position at moves matrix

            public iterator() {
                // Calc  0  coordinates
                rowZer = row(zeroId);
                colZer = col(zeroId);
                // Initializing others
                currPos = 0;
                moves = new int[5][2];
                // filling moves matr with {{-1, 0}, {-1, 0}....}
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 1; j++) moves[i][j] = -1;
                }

                // all possible positions matrix {row, col} format
                int[][] posPos = {
                        { (rowZer - 1), colZer }, { rowZer, (colZer + 1) },
                        { (rowZer + 1), colZer },
                        { rowZer, (colZer - 1) }
                };

                // filling in possible moves {row, col} format
                int counter = 0;
                for (int i = 0; i < 4; i++) {
                    if (posPos[i][0] >= 1 && posPos[i][0] <= n && posPos[i][1] >= 1
                            && posPos[i][1] <= n) {
                        moves[counter][0] = posPos[i][0];
                        moves[counter][1] = posPos[i][1];
                        counter++;
                    }
                }
            }


            public Board next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                // it is array of int[][] format!!!
                int[][] boardCopy = array2DCopy(board);
                boardCopy[rowZer - 1][colZer - 1]
                        = boardCopy[(moves[currPos][0]) - 1][(moves[currPos][1]) - 1];
                boardCopy[(moves[currPos][0]) - 1][(moves[currPos][1]) - 1] = 0;
                currPos++;
                Board boardIt;
                boardIt = new Board(boardCopy);
                return boardIt;
            }

            public boolean hasNext() {
                if (moves[currPos][0] == -1) return false;
                return true;
            }
        }
    }


    private int row(int num) {
        int row = (int) Math.ceil((double) (num + 1) / n);
        return row;
    }

    private int col(int num) {
        int col = (n - (row(num) * n - num - 1));
        return col;
    }


    // Hamming & Manhattan calculation v.2
    private void hammanh() {
        manhattan = 0;
        hamming = 0;
        int l = board[0].length; // row length
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < l; j++) {
                if (board[i][j] != 0 && board[i][j] != i * l + j + 1) {
                    hamming++;
                    int cell = board[i][j]; // current number
                    // cells correct coordinates (0..n)
                    int y = ((cell - 1) / l);
                    int x = (cell - 1 - y * l);
                    manhattan += (Math.abs(j - x) + Math.abs(i - y));
                }
            }

        }
    }

    // string representation of this board
    public String toString() {

        StringBuilder strBuf = new StringBuilder();

        strBuf.append(n);

        for (int[] p :
                board) {
            strBuf.append("\n");
            for (int k : p) {
                strBuf.append(k).append(" ");
            }
        }
        return strBuf.toString();
    }

    // board dimension n
    public int dimension() {
        return n;
    }


    // number of tiles out of place
    public int hamming() {
        return hamming;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return manhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {

        if (y instanceof Board) {
            Board b = (Board) y;
            // b.boardToArray();
            // boardToArray();
            if (board.length == b.board.length && board[0].length == b.board[0].length) {
                for (int i = 0; i < board.length; i++) {
                    for (int j = 0; j < board[0].length; j++) {
                        if (board[i][j] != b.board[i][j]) return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        return new Neighbors();
    }

    // convert ID to rowId
    private int rowId(int i) {
        return (i / n);
    }

    // convert ID to colId
    private int colId(int i) {
        return (n - (n * (rowId(i) + 1) - i));
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int[][] boardCopy = array2DCopy(board);

        // get "random" ids
        int i, j;
        i = 0;
        j = 0;
        while (i == zeroId) i++;
        while (i == j || j == zeroId) j++;

        // Exchange proc
        int tileToReplace = boardCopy[rowId(i)][colId(i)];
        boardCopy[rowId(i)][colId(i)] = boardCopy[rowId(j)][colId(j)];
        boardCopy[rowId(j)][colId(j)] = tileToReplace;
        Board twinBoard = new Board(boardCopy);
        return twinBoard;

    }


    // unit testing (not graded)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);
        StdOut.println("zeroId: " + initial.zeroId);
        StdOut.println("initial:");
        StdOut.println(initial.toString());
        StdOut.println(initial.dimension());
        StdOut.println("hamming is...");
        StdOut.println(initial.hamming());
        StdOut.println("manhattan is...");
        StdOut.println(initial.manhattan());
        StdOut.println("is goal?");
        StdOut.println(initial.isGoal());
        // StdOut.println("twin");
        // StdOut.println(initial.twin().toString());

        Iterable<Board> myNeigh = initial.neighbors();
        StdOut.println("Neighbours...");
        for (Board p : myNeigh) {
            StdOut.println(p.toString());
            Iterable<Board> myNeighInside = p.neighbors();
            StdOut.println("Inside neighbours...");
            for (Board q : myNeighInside) {
                StdOut.println(q.toString());
            }

        }

    }
}


