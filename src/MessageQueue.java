import java.util.*;

public class MessageQueue {
    private static HashMap<String, Queue<Message>> queueMap = new HashMap<>();

    public static void addMessage(String username, Message message) {
        queueMap.putIfAbsent(username, new LinkedList<>());
        queueMap.get(username).add(message);
    }

    public static Queue<Message> getAndClearMessages(String username) {
        Queue<Message> msgs = queueMap.getOrDefault(username, new LinkedList<>());
        queueMap.put(username, new LinkedList<>());
        return msgs;
    }

    public static Queue<Message> getMessages(String username) {
        return queueMap.getOrDefault(username, new LinkedList<>());
    }
}
