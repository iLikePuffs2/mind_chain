package com.example.mind_chain.util;

import java.util.*;

public class CalculateLevel {
    public static void main(String[] args) {
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        // 初始化节点和边的数据
        initializeData(nodes, edges);

        // 计算节点的level值
        calculateNodeLevels(nodes, edges);

        // 按id递增的顺序打印节点的id和level值
        nodes.stream()
                .sorted(Comparator.comparingInt(n -> Integer.parseInt(n.id)))
                .forEach(node -> {
                    if (!node.id.equals("0")) {
                        System.out.println("Node ID: " + node.id + ", Level: " + node.data.level);
                    }
                });
    }
    /**
     * 计算节点的level值
     *
     * @param nodes 节点列表
     * @param edges 边列表
     */
    private static void calculateNodeLevels(List<Node> nodes, List<Edge> edges) {
        // 创建一个节点ID到节点对象的映射
        Map<String, Node> nodeMap = new HashMap<>();
        for (Node node : nodes) {
            nodeMap.put(node.id, node);
        }

        // 找到根节点
        Node rootNode = null;
        for (Node node : nodes) {
            if (node.id.equals("0")) {
                rootNode = node;
                break;
            }
        }

        // 根节点的子节点level为1
        for (Edge edge : edges) {
            if (edge.source.equals("0")) {
                nodeMap.get(edge.target).data.level = 1;
            }
        }

        // 根节点的子节点的子节点level为2
        for (Edge edge : edges) {
            if (nodeMap.get(edge.source).data.level == 1) {
                nodeMap.get(edge.target).data.level = 2;
            }
        }

        // 创建一个队列,用于存储需要计算level值的节点
        Queue<Node> queue = new LinkedList<>();

        // 将level为2的节点加入队列
        for (Node node : nodes) {
            if (node.data.level == 2) {
                queue.offer(node);
            }
        }

        // 广度优先遍历图,计算剩余节点的level值(利用广度优先遍历的特点,确保在计算一个节点的level值之前,它的父节点和收敛节点的level值都已经被正确计算过了)
        while (!queue.isEmpty()) {
            Node node = queue.poll();

            // 如果节点是多父节点,则将其level值设置为对应收敛节点的level值
            if (isMultipleParents(node.id, edges)) {
                String convergenceNodeId = findConvergenceNode(node.id, edges, nodeMap);
                if (convergenceNodeId != null) {
                    node.data.level = nodeMap.get(convergenceNodeId).data.level;
                }
            } else {
                // 如果节点是一父节点,则计算其level值
                calculateSingleParentNodeLevel(node, nodeMap, edges);
            }

            // 将当前节点的子节点加入队列
            List<Edge> childEdges = edges.stream()
                    .filter(edge -> edge.source.equals(node.id))
                    .toList();
            for (Edge edge : childEdges) {
                Node childNode = nodeMap.get(edge.target);
                if (childNode.data.level == 0) {
                    childNode.data.level = node.data.level + 1;
                    queue.offer(childNode);
                }
            }
        }
    }

    /**
     * 找到多父节点的收敛节点
     *
     * @param nodeId  多父节点的ID
     * @param edges   边列表
     * @param nodeMap 节点ID到节点对象的映射
     * @return 收敛节点的ID,如果没有收敛节点则返回null
     */
    private static String findConvergenceNode(String nodeId, List<Edge> edges, Map<String, Node> nodeMap) {
        // 找到当前节点的所有父节点ID
        List<String> parentNodeIds = edges.stream()
                .filter(edge -> edge.target.equals(nodeId))
                .map(edge -> edge.source)
                .toList();

        // 记录每个父节点向上延伸的路径上的节点ID
        List<Set<String>> parentNodePaths = new ArrayList<>();
        for (String parentNodeId : parentNodeIds) {
            Set<String> path = new HashSet<>();
            findPathToRoot(parentNodeId, edges, nodeMap, path);
            parentNodePaths.add(path);
        }

        // 找到所有路径的交集 convergenceNodeIds
        List<String> convergenceNodeIds = new ArrayList<>(parentNodePaths.get(0));
        for (int i = 1; i < parentNodePaths.size(); i++) {
            convergenceNodeIds.retainAll(parentNodePaths.get(i));
        }

        // 根据convergenceNodeIds中的节点ID和edges数组重建连线关系
        Map<String, List<String>> rebuildEdges = new HashMap<>();
        for (String nodeIdInPath : convergenceNodeIds) {
            List<String> targetNodeIds = edges.stream()
                    .filter(edge -> edge.source.equals(nodeIdInPath))
                    .map(edge -> edge.target)
                    .filter(convergenceNodeIds::contains)
                    .toList();
            rebuildEdges.put(nodeIdInPath, targetNodeIds);
        }

        // 找到重建后连线的最后一个节点作为收敛节点
        String convergenceNodeId = null;
        for (String nodeIdInPath : convergenceNodeIds) {
            if (!rebuildEdges.containsKey(nodeIdInPath) || rebuildEdges.get(nodeIdInPath).isEmpty()) {
                convergenceNodeId = nodeIdInPath;
                break;
            }
        }

        return convergenceNodeId;
    }

    /**
     * 找到从当前节点到根节点的路径上的所有节点ID
     *
     * @param nodeId  当前节点ID
     * @param edges   边列表
     * @param nodeMap 节点ID到节点对象的映射
     * @param path    存储路径上节点ID的集合
     */
    private static void findPathToRoot(String nodeId, List<Edge> edges, Map<String, Node> nodeMap, Set<String> path) {
        path.add(nodeId);

        // 如果当前节点是根节点,则结束递归
        if (nodeId.equals("0")) {
            return;
        }

        // 找到当前节点的父节点ID
        String parentNodeId = edges.stream()
                .filter(edge -> edge.target.equals(nodeId))
                .map(edge -> edge.source)
                .findFirst()
                .orElse(null);

        // 如果父节点存在,则继续递归查找
        if (parentNodeId != null) {
            findPathToRoot(parentNodeId, edges, nodeMap, path);
        }
    }
    /**
     * 获取每对多父节点和收敛节点的ID
     *
     * @param nodes   节点列表
     * @param edges   边列表
     * @param nodeMap
     * @return 一个Map，键为多父节点的ID，值为对应的收敛节点的ID
     */
    private static Map<String, String> getMultipleParentsConvergenceMap(List<Node> nodes, List<Edge> edges, Map<String, Node> nodeMap) {
        Map<String, String> multipleParentsConvergenceMap = new HashMap<>();
        for (Node node : nodes) {
            if (isMultipleParents(node.id, edges)) {
                String convergenceNodeId = findConvergenceNode(node.id, edges, nodeMap);
                if (convergenceNodeId != null) {
                    multipleParentsConvergenceMap.put(node.id, convergenceNodeId);
                }
            }
        }

        return multipleParentsConvergenceMap;
    }

    /**
     * 计算一父节点的level值
     *
     * @param node    当前节点
     * @param nodeMap 节点ID到节点对象的映射
     * @param edges   边列表
     */
    private static void calculateSingleParentNodeLevel(Node node, Map<String, Node> nodeMap, List<Edge> edges) {
        // 找到当前节点的父节点边
        List<Edge> parentEdges = edges.stream()
                .filter(edge -> edge.target.equals(node.id))
                .toList();

        // 对于一父情况
        if (parentEdges.size() == 1) {
            Node parentNode = nodeMap.get(parentEdges.get(0).source);

            // 如果父节点有多个子节点
            if (hasMultipleChildren(parentNode, edges)) {
                node.data.level = parentNode.data.level + 1;
            } else {
                // 如果父节点只有一个子节点
                node.data.level = parentNode.data.level;
            }
        }
    }

    /**
     * 判断一个节点是否有多个子节点
     *
     * @param node  节点
     * @param edges 边列表
     * @return 如果节点有多个子节点返回true，否则返回false
     */
    private static boolean hasMultipleChildren(Node node, List<Edge> edges) {
        return edges.stream()
                .filter(edge -> edge.source.equals(node.id))
                .count() > 1;
    }

    /**
     * 判断一个节点是否有多个父节点
     *
     * @param nodeId 节点ID
     * @param edges  边列表
     * @return 如果节点有多个父节点返回true，否则返回false
     */
    private static boolean isMultipleParents(String nodeId, List<Edge> edges) {
        return edges.stream()
                .filter(edge -> edge.target.equals(nodeId))
                .count() > 1;
    }


    /**
     * 初始化节点和边的数据
     *
     * @param nodes 节点列表
     * @param edges 边列表
     */
    private static void initializeData(List<Node> nodes, List<Edge> edges) {
        // 初始化节点
        nodes.add(new Node("0", new NodeData(0)));
        nodes.add(new Node("1", new NodeData(0)));
        nodes.add(new Node("2", new NodeData(0)));
        nodes.add(new Node("3", new NodeData(0)));
        nodes.add(new Node("4", new NodeData(0)));
        nodes.add(new Node("5", new NodeData(0)));
        nodes.add(new Node("6", new NodeData(0)));
        nodes.add(new Node("7", new NodeData(0)));
        nodes.add(new Node("8", new NodeData(0)));
        nodes.add(new Node("9", new NodeData(0)));
        nodes.add(new Node("10", new NodeData(0)));
        nodes.add(new Node("11", new NodeData(0)));
        nodes.add(new Node("12", new NodeData(0)));
        nodes.add(new Node("13", new NodeData(0)));
        nodes.add(new Node("14", new NodeData(0)));
        nodes.add(new Node("15", new NodeData(0)));
        nodes.add(new Node("16", new NodeData(0)));
        nodes.add(new Node("17", new NodeData(0)));
        nodes.add(new Node("18", new NodeData(0)));
        nodes.add(new Node("19", new NodeData(0)));
        nodes.add(new Node("20", new NodeData(0)));
        nodes.add(new Node("21", new NodeData(0)));
        nodes.add(new Node("22", new NodeData(0)));
        nodes.add(new Node("23", new NodeData(0)));
        nodes.add(new Node("24", new NodeData(0)));

        // 初始化边
        edges.add(new Edge("0", "1"));
        edges.add(new Edge("1", "2"));
        edges.add(new Edge("1", "3"));
        edges.add(new Edge("2", "4"));
        edges.add(new Edge("4", "5"));
        edges.add(new Edge("3", "6"));
        edges.add(new Edge("3", "7"));
        edges.add(new Edge("6", "10"));
        edges.add(new Edge("7", "10"));
        edges.add(new Edge("5", "8"));
        edges.add(new Edge("5", "9"));
        edges.add(new Edge("8", "11"));
        edges.add(new Edge("8", "12"));
        edges.add(new Edge("8", "13"));
        edges.add(new Edge("9", "24"));
        edges.add(new Edge("11", "14"));
        edges.add(new Edge("11", "15"));
        edges.add(new Edge("11", "16"));
        edges.add(new Edge("11", "17"));
        edges.add(new Edge("12", "18"));
        edges.add(new Edge("13", "19"));
        edges.add(new Edge("13", "20"));
        edges.add(new Edge("14", "21"));
        edges.add(new Edge("15", "21"));
        edges.add(new Edge("16", "21"));
        edges.add(new Edge("17", "21"));
        edges.add(new Edge("21", "22"));
        edges.add(new Edge("21", "23"));
        edges.add(new Edge("22", "24"));
        edges.add(new Edge("23", "24"));
        edges.add(new Edge("18", "24"));
        edges.add(new Edge("19", "24"));
        edges.add(new Edge("20", "24"));
    }
    // 节点类
    static class Node {
        String id;
        NodeData data;

        Node(String id, NodeData data) {
            this.id = id;
            this.data = data;
        }
    }

    // 节点数据类
    static class NodeData {
        int level;

        NodeData(int level) {
            this.level = level;
        }
    }

    // 边类
    static class Edge {
        String source;
        String target;

        Edge(String source, String target) {
            this.source = source;
            this.target = target;
        }
    }
}