package univgraphics.common.primitives;

import java.util.Objects;

public class Edge {
    private Point start, end;

    public Edge(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    private static boolean orientation(Point a, Point b, Point c) {
        return (c.y - a.y) * (b.x - a.x) > (b.y - a.y) * (c.x - a.x);
    }

    // Return true if line segments AB and CD intersects
    public static boolean intersects(Point a, Point b, Point c, Point d) {
        return orientation(a, c, d) != orientation(b, c, d) &&
                orientation(a, b, c) != orientation(a, b, d);
    }

    public static boolean intersects(Edge e1, Edge e2) {
           return orientation(e1.start, e2.start, e2.end) != orientation(e1.end, e2.start, e2.end) &&
                   orientation(e1.start, e1.end, e2.start) != orientation(e1.start, e1.end, e2.end);
    }

    public static Point getIntersectionPoint(Edge e1, Edge e2) {
        float x, y;
        if (e1.start.y == e1.end.y) {
            y = e1.start.y;
            x = e2.valueInY(y);
        } else if (e2.start.y == e2.end.y){
            y = e2.start.y;
            x = e1.valueInY(y);
        } else if (e1.start.x == e1.end.x) {
            x = e1.start.x;
            y = e2.valueInX(x);
        } else if (e2.start.x == e2.end.x) {
            x = e2.start.x;
            y = e1.valueInX(x);
        } else {
            float denominator = (e1.start.x - e1.end.x) * (e2.start.y - e2.end.y)
                    - (e1.start.y - e1.end.y) * (e2.start.x - e2.end.x);
            if (denominator == 0) return null; // lines are parallel
            float xNumerator = (e1.start.x * e1.end.y - e1.start.y * e1.end.x) * (e2.start.x - e2.end.x)
                    - (e1.start.x - e1.end.x) * (e2.start.x * e2.end.y - e2.start.y * e2.end.x);
            float yNumerator = (e1.start.x * e1.end.y - e1.start.y * e1.end.x) * (e2.start.y - e2.end.y)
                    - (e1.start.y - e1.end.y) * (e2.start.x * e2.end.y - e2.start.y * e2.end.x);
            x = xNumerator / denominator;
            y = yNumerator / denominator;
        }
        return new Point((int) x, (int) y);
    }

    private double calcCrossProduct(Point point) {
        return (end.x - start.x) * (point.getY() - start.y)
                - (end.y - start.y) * (point.getX() - start.x);
    }

    public boolean pointIsOnRightSide(Point point) {
        return calcCrossProduct(point) < 0;
    }

    public int substitutionX(Point p) {
        float positionX = valueInY(p.y);
        return (int)((p.x - positionX));
    }

    public float valueInX(float x) { return (x - start.x) * (end.y - start.y) / (end.x - start.x)  + start.y; }

    public float valueInY(float y) {
        return (y - start.y) * (end.x - start.x) / (end.y - start.y) + start.x;
    }

    public double distance(Point p) {
        return Math.abs((end.y - start.y) * p.getX() - (end.x - start.x) * p.getY() + end.x * start.y - end.y * start.x)
                / Math.sqrt(Math.pow(end.y - start.y, 2) + Math.pow(end.x - start.x, 2));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        return (start.equals(edge.start) && end.equals(edge.end)) || (start.equals(edge.end) && end.equals(edge.start));
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end) ^ Objects.hash(end, start);
    }

    public static Edge createRandomEdge(int x0, int y0, int width, int height) {
        Point startPoint = Point.createRandomPoint(x0, y0, width, height);
        Point endPoint = Point.createRandomPoint(x0, y0, width, height);
        Node start = new Node(startPoint);
        Node end = new Node(endPoint);
        start.adj().add(end);
        end.adj().add(start);
        return new Edge(start, end);
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

    public Node getStartNode() {
        if (start.getClass() == Node.class) {
            return (Node)start;
        } else return null;
    }

    public Node getEndNode() {
        if (end.getClass() == Node.class) {
            return (Node)end;
        } else return null;
    }
}