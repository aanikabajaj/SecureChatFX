import java.util.*;

public class ChatRanker {
    private HashMap<String, HashMap<String, Integer>> userChats = new HashMap<>();

    public void recordInteraction(String from, String to) {
        userChats.putIfAbsent(from, new HashMap<>());
        HashMap<String, Integer> map = userChats.get(from);
        map.put(to, map.getOrDefault(to, 0) + 1);
    }

    public List<String> getTopContacts(String user, int topN) {
        List<String> result = new ArrayList<>();
        if (!userChats.containsKey(user)) return result;

        PriorityQueue<Map.Entry<String, Integer>> maxHeap = new PriorityQueue<>(
            (a, b) -> b.getValue() - a.getValue()
        );

        maxHeap.addAll(userChats.get(user).entrySet());

        while (topN-- > 0 && !maxHeap.isEmpty()) {
            result.add(maxHeap.poll().getKey());
        }

        return result;
    }
}
