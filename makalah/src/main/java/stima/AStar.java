package stima;

import java.util.*;

class Node implements Comparable<Node> {
    public int row, col;
    public int g, h;
    public Node parent;

    public Node(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getF() {
        return g + h;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.getF(), other.getF());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return row == node.row && col == node.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}

public class AStar {
    private static final int[][] DIRECTIONS = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
    private final int rows, cols;
    private final List<List<Boolean>> walkable;

    public AStar(int rows, int cols, List<List<Boolean>> walkable) {
        this.rows = rows;
        this.cols = cols;
        this.walkable = walkable;
    }

    public List<Node> findPath(Node start, Node goal) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Node> closedSet = new HashSet<>();
        start.g = 0;
        start.h = heuristic(start, goal);
        openSet.add(start);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.equals(goal)) {
                return reconstructPath(current);
            }
            closedSet.add(current);

            for (int[] direction : DIRECTIONS) {
                int newRow = current.row + direction[0];
                int newCol = current.col + direction[1];

                if (isValid(newRow, newCol) && walkable.get(newRow).get(newCol)) {
                    Node neighbor = new Node(newRow, newCol);
                    if (closedSet.contains(neighbor)) continue;

                    int tentativeG = current.g + 1;
                    if (tentativeG < neighbor.g || !openSet.contains(neighbor)) {
                        neighbor.g = tentativeG;
                        neighbor.h = heuristic(neighbor, goal);
                        neighbor.parent = current;

                        if (!openSet.contains(neighbor)) {
                            openSet.add(neighbor);
                        }
                    }
                }
            }
        }
        return Collections.emptyList(); // No path found
    }

    private int heuristic(Node a, Node b) {
        return Math.abs(a.row - b.row) + Math.abs(a.col - b.col);
    }

    private boolean isValid(int row, int col) {
        return row >= 0 && col >= 0 && row < rows && col < cols;
    }

    private List<Node> reconstructPath(Node node) {
        List<Node> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }
}
