import java.util.HashMap;
import java.util.Map;

public class TypingIndicatorManager {
    private static final Map<String, String> typingStatus = new HashMap<>();

    public static synchronized void setTyping(String receiver, String sender) {
        typingStatus.put(receiver, sender);
    }

    public static synchronized void clearTyping(String receiver) {
        typingStatus.remove(receiver);
    }

    public static synchronized String checkTyping(String receiver) {
        return typingStatus.get(receiver);
    }
}
