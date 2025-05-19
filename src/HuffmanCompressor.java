import java.util.*;

public class HuffmanCompressor {
    static class Node {
        char ch;
        int freq;
        Node left, right;

        Node(char ch, int freq) {
            this.ch = ch;
            this.freq = freq;
        }
    }

    private static void buildCodeMap(Node root, String code, Map<Character, String> codeMap) {
        if (root == null) return;
        if (root.left == null && root.right == null) codeMap.put(root.ch, code);
        buildCodeMap(root.left, code + "0", codeMap);
        buildCodeMap(root.right, code + "1", codeMap);
    }

    public static String compress(String text) {
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.freq));
        for (var e : freqMap.entrySet()) {
            pq.add(new Node(e.getKey(), e.getValue()));
        }

        while (pq.size() > 1) {
            Node left = pq.poll(), right = pq.poll();
            Node parent = new Node('\0', left.freq + right.freq);
            parent.left = left;
            parent.right = right;
            pq.add(parent);
        }

        Node root = pq.poll();
        Map<Character, String> codeMap = new HashMap<>();
        buildCodeMap(root, "", codeMap);

        StringBuilder encoded = new StringBuilder();
        for (char c : text.toCharArray()) {
            encoded.append(codeMap.get(c));
        }

        return rootToString(root) + "||" + encoded;
    }

    public static String decompress(String data) {
        String[] parts = data.split("\\|\\|", 2);
        Node root = stringToTree(parts[0]);
        String encoded = parts[1];

        StringBuilder result = new StringBuilder();
        Node current = root;
        for (char bit : encoded.toCharArray()) {
            current = (bit == '0') ? current.left : current.right;
            if (current.left == null && current.right == null) {
                result.append(current.ch);
                current = root;
            }
        }
        return result.toString();
    }

    // Tree serialization
    private static String rootToString(Node root) {
        if (root == null) return "#";
        return (root.ch == '\0' ? "*" : root.ch) + rootToString(root.left) + rootToString(root.right);
    }

    private static int index;
    private static Node stringToTree(String s) {
        index = 0;
        return helper(s);
    }

    private static Node helper(String s) {
        if (index >= s.length()) return null;
        char ch = s.charAt(index++);
        if (ch == '#') return null;
        Node node = new Node((ch == '*') ? '\0' : ch, 0);
        node.left = helper(s);
        node.right = helper(s);
        return node;
    }
}
