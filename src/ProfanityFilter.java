import java.util.*;

public class ProfanityFilter {
    private static TrieNode root = new TrieNode();

    static {
        // Load common bad words into the Trie
        String[] badWords = {"damn", "hell", "crap", "idiot", "stupid"};
        for (String word : badWords) {
            insert(word);
        }
    }

    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd = false;
    }

    private static void insert(String word) {
        TrieNode node = root;
        for (char c : word.toLowerCase().toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEnd = true;
    }

    public static boolean containsProfanity(String text) {
        text = text.toLowerCase();
        for (int i = 0; i < text.length(); i++) {
            TrieNode node = root;
            int j = i;
            while (j < text.length() && node.children.containsKey(text.charAt(j))) {
                node = node.children.get(text.charAt(j));
                if (node.isEnd) return true;
                j++;
            }
        }
        return false;
    }
}
