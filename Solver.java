import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Solver {
    private ArrayList<Board> solution; // Solution array
    private final boolean isSolvable;
    private final int moves; // number of moves

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException("Initial board is null");
        SearchNode currentSearchNode = new SearchNode(initial, 0, null);
        SearchNode currentSearchNodeTwin = new SearchNode(initial.twin(), 0, null);
        solution = new ArrayList<>();
        MinPQ<SearchNode> pq; // My priority queue
        MinPQ<SearchNode> pqTwin; // My priority queue twin
        pq = new MinPQ<>();
        pqTwin = new MinPQ<>();
        pq.insert(currentSearchNode);
        pqTwin.insert(currentSearchNodeTwin);
        while (!currentSearchNode.board.isGoal()) {
            if (currentSearchNodeTwin.board.isGoal()) {
                isSolvable = false;
                moves = -1;
                solution = null;
                return;
            }
            currentSearchNode = pq.delMin();
            currentSearchNodeTwin = pqTwin.delMin();
            addNeighbors(pq, currentSearchNode); // chose addNeighborsCheck or addNeighbors
            addNeighbors(pqTwin, currentSearchNodeTwin);
        }
        isSolvable = true;
        moves = currentSearchNode.moves;
        for (SearchNode n : currentSearchNode) {
            solution.add(0, n.board);
        }
    }

    // Method checks only neighbors of neighbors
    private void addNeighbors(MinPQ<SearchNode> pq, SearchNode currentSearchNode) {
        Board similarBoard;
        if (currentSearchNode.parentNode != null) {
            similarBoard = currentSearchNode.parentNode.board;
            for (Board b : currentSearchNode.board.neighbors()) {

                if (!similarBoard.equals(b)) pq.insert(
                        new SearchNode(b, (currentSearchNode.moves + 1),
                                       currentSearchNode));
            }
        }
        else {
            for (Board b : currentSearchNode.board.neighbors()) {
                pq.insert(
                        new SearchNode(b, (currentSearchNode.moves + 1),
                                       currentSearchNode));
            }
        }
    }


    private class SearchNode implements Comparable<SearchNode>, Iterable<SearchNode> {
        private final int parametr, moves, priority; // *parametr = manhattan || hamming
        private final Board board;
        private final SearchNode parentNode;
        private final SearchNode firstNode;

        // constructor
        public SearchNode(Board b, int movesNum, SearchNode parent) {
            parentNode = parent;
            firstNode = this;
            moves = movesNum;
            board = b;
            parametr = b.manhattan();
            priority = (parametr + moves);

        }

        public Iterator<SearchNode> iterator() {
            return new NodeIterator();
        }

        private class NodeIterator implements Iterator<SearchNode> {
            private SearchNode currentNode = firstNode;

            public SearchNode next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more items to return");
                }
                SearchNode nextNode = currentNode;
                currentNode = nextNode.parentNode;
                return nextNode; // parentNode return
            }

            public boolean hasNext() {
                return currentNode != null;
            }
        }

        public int compareTo(SearchNode that) {
            if (this.priority < that.priority) return -1;
            if (this.priority > that.priority) return 1;
            return 0;
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return isSolvable;
    }

    // min number of moves to solve initial board
    public int moves() {
        return moves;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        return solution;
    }


    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);
        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}











