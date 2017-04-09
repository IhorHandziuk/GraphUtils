package sample.primitives;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Node extends Point {
    private Set<Node> adjacent = new HashSet<>();

    public Node(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;
        return (x == node.x) && (y == node.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public Set<Node> adj() {
        return adjacent;
    }
}

