package univgraphics.geomsearch;

import univgraphics.common.primitives.Node;
import univgraphics.common.primitives.Point;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Ihor Handziuk on 10.04.2017.
 * All code is free to use and distribute.
 */
public class RegionTree {

    private static class TreeNode {
        int xLeft, xRight; // interval [xLeft, xRight)
        TreeNode left;
        TreeNode right;
        List<Point> points = new ArrayList<>();

    }

    private List<Node> graph;
    private int leftX, rightX;
    private int topY, bottomY;
    private TreeNode root;

    public RegionTree(List<Node> graph) {
        this.graph = graph;
        root = new TreeNode();
        graph.sort(Comparator.comparingInt(Point::getX));
        root.points.addAll(graph);
        root.xLeft = graph
                .stream()
                .min(Comparator.comparingInt(Point::getX))
                .get()
                .getX();
        root.xRight = graph
                .stream()
                .max(Comparator.comparingInt(Point::getX))
                .get()
                .getX() + 1;  // + 1 for maintaining the rule [xLeft, xRight)
        buildTree(root);
    }

    public List<Point> getPoints(Point startCorner, Point endCorner) {
        leftX = Math.min(startCorner.getX(), endCorner.getX());
        rightX = Math.max(startCorner.getX(), endCorner.getX());
        topY = Math.max(startCorner.getY(), endCorner.getY());
        bottomY = Math.min(startCorner.getY(), endCorner.getY());

        List<Point> res = new ArrayList<>();
        search(root, res);
        return res;
    }

    private void search(TreeNode parent, List<Point> list) {
        if (parent.points.size() > 1) {
            if (leftX <= parent.left.xRight) {
                search(parent.left, list);
            }
            if (rightX >= parent.right.xLeft) {
                search(parent.right, list);
            }
        } else {
            Point point = parent.points.get(0);
            if (point.getX() > leftX &&
                point.getX() < rightX &&
                point.getY() > bottomY &&
                point.getY() < topY) {
                list.add(point);
            }
        }
    }

    private void buildTree(TreeNode parent) {
       if (parent.points.size() > 1) {
           parent.left = new TreeNode();
           parent.right = new TreeNode();
           parent.left.points.addAll(parent.points
                   .stream()
                   .limit(parent.points.size() / 2)
                   .collect(Collectors.toList()));
           parent.right.points.addAll(parent.points
                   .stream()
                   .skip(parent.points.size() / 2)
                   .collect(Collectors.toList()));
           parent.left.xLeft = parent.xLeft;
           parent.left.xRight = parent.left.points.get(parent.left.points.size() - 1).getX();
           parent.right.xLeft = parent.right.points.get(0).getX();
           parent.right.xRight = parent.xRight;
           buildTree(parent.left);
           buildTree(parent.right);
       }
    }
}
