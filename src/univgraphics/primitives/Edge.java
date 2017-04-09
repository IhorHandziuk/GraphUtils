package univgraphics.primitives;

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

    // Return true if line segments AB and CD intersect
    public static boolean intersect(Point a, Point b, Point c, Point d) {
        return orientation(a, c, d) != orientation(b, c, d) &&
                orientation(a, b, c) != orientation(a, b, d);
    }

    public static boolean intersect(Edge e1, Edge e2) {
           return orientation(e1.start, e2.start, e2.end) != orientation(e1.end, e2.start, e2.end) &&
                   orientation(e1.start, e1.end, e2.start) != orientation(e1.start, e1.end, e2.end);
    }

    /* returns intersection point of 2 lines represented by 2 segments
     * exactly 1 line should be horizontal
     */
    public static Point getIntersectionPointHorizon(Edge e1, Edge e2) {
            float x, y;
            if (e1.start.y == e1.end.y) {
                y = e1.start.y;
                x = (y - e2.start.y) * (e2.end.x - e2.start.x) / (e2.end.y - e2.start.y) + e2.start.x;
            } else if (e2.start.y == e2.end.y){
                y = e2.start.y;
                x = (y - e1.start.y) * (e1.end.x - e1.start.x) / (e1.end.y - e1.start.y) + e1.start.x;
            } else return null;
            return new Point((int) x, (int) y);
    }

    public int substitutionX(Point p) {
        float positionX = valueInY(p.y);
        return (int)((p.x - positionX));
    }

    public float valueInX(int x) {
        return (1.f * (x - start.x) / (end.x - start.x)) * (end.y - start.y) + start.y;
    }

    public float valueInY(int y) {
        return (1.f * (y - start.y) / (end.y - start.y)) * (end.x - start.x) + start.x;
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

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }
}