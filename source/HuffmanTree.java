import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

class Node implements Comparable<Node> {
    byte character; // Используем byte(!!!)
    int freq;
    Node left, right;

    Node(byte character, int freq) {
        this.character = character;
        this.freq = freq;
    }

    Node(int freq, Node left, Node right) {
        this.character = 0; 
        this.freq = freq;
        this.left = left;
        this.right = right;
    }

    public int compareTo(Node other) {
        return this.freq - other.freq;
    }

    public boolean isLeaf() {
        return (this.left == null) && (this.right == null);
    }
}

public class HuffmanTree {

    // Построение карты частот для байтов
    public static Map<Byte, Integer> buildFrequencyMap(byte[] data) {
        Map<Byte, Integer> freqMap = new HashMap<>();
        for (byte b : data) {
            freqMap.put(b, freqMap.getOrDefault(b, 0) + 1);
        }
        return freqMap;
    }

    // Построение дерева Хаффмана для байтов
    public static Node buildHuffmanTree(Map<Byte, Integer> freqMap) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (Map.Entry<Byte, Integer> entry : freqMap.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            Node parent = new Node(left.freq + right.freq, left, right);
            pq.add(parent);
        }

        return pq.poll();
    }

    // Построение таблицы кодов Хаффмана для байтов
    public static Map<Byte, String> buildCodeTable(Node root) {
        Map<Byte, String> codeTable = new HashMap<>();
    
        // Если дерево состоит только из одного узла
        if (root.isLeaf()) {
            codeTable.put(root.character, "0"); // Назначаем код "0" единственному символу
        } else {
            buildCodeHelper(root, "", codeTable);
        }
    
        return codeTable;
    }

    private static void buildCodeHelper(Node node, String code, Map<Byte, String> codeTable) {
        if (!node.isLeaf()) {
            buildCodeHelper(node.left, code + '0', codeTable);
            buildCodeHelper(node.right, code + '1', codeTable);
        } else {
            codeTable.put(node.character, code);
        }
    }
}