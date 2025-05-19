import java.util.ArrayList;
import java.util.List;

public class SearchEngine {

    // KMP Preprocessing
    private static int[] buildLPS(String pattern) {
        int[] lps = new int[pattern.length()];
        int len = 0;
        int i = 1;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                lps[i++] = ++len;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i++] = 0;
                }
            }
        }

        return lps;
    }

    // KMP Search
    public static boolean containsPattern(String text, String pattern) {
        int[] lps = buildLPS(pattern);
        int i = 0, j = 0;

        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++; j++;
                if (j == pattern.length()) return true;
            } else {
                if (j != 0) j = lps[j - 1];
                else i++;
            }
        }

        return false;
    }

    // Search in list of messages
    public static List<Message> searchMessages(List<Message> messages, String keyword) {
        List<Message> result = new ArrayList<>();
        for (Message m : messages) {
            String decrypted = Encryptor.decrypt(m.getContent());
            if (containsPattern(decrypted.toLowerCase(), keyword.toLowerCase())) {
                result.add(m);
            }
        }
        return result;
    }
}
