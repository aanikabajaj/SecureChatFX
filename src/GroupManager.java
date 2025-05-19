import java.util.*;

public class GroupManager {
    private static Map<String, Set<String>> groupMembers = new HashMap<>();
    private static Map<String, List<Message>> groupMessages = new HashMap<>();

    public static void createGroup(String groupName, String creator) {
        groupMembers.putIfAbsent(groupName, new HashSet<>());
        groupMembers.get(groupName).add(creator);
        groupMessages.putIfAbsent(groupName, new ArrayList<>());
    }

    public static boolean joinGroup(String groupName, String username) {
        if (!groupMembers.containsKey(groupName)) return false;
        groupMembers.get(groupName).add(username);
        return true;
    }

    public static Set<String> getGroupMembers(String groupName) {
        return groupMembers.getOrDefault(groupName, new HashSet<>());
    }

    public static boolean groupExists(String groupName) {
        return groupMembers.containsKey(groupName);
    }

    public static boolean exitGroup(String groupName, String username) {
        if (!groupMembers.containsKey(groupName)) return false;
        Set<String> members = groupMembers.get(groupName);
        boolean removed = members.remove(username);
        if (members.isEmpty()) {
            groupMembers.remove(groupName);
            groupMessages.remove(groupName); // Clean up chat history if group is deleted
        }
        return removed;
    }

    public static Set<String> getAllGroups() {
        return groupMembers.keySet();
    }

    // ===== Group Messaging Support =====

    public static void addGroupMessage(String groupName, Message message) {
        groupMessages.putIfAbsent(groupName, new ArrayList<>());
        groupMessages.get(groupName).add(message);
    }

    public static List<Message> getGroupMessages(String groupName) {
        return groupMessages.getOrDefault(groupName, new ArrayList<>());
    }
}
