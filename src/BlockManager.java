import java.util.*;

public class BlockManager {
    private static Map<String, Set<String>> blockMap = new HashMap<>();

    public static void blockUser(String blocker, String blocked) {
        blockMap.putIfAbsent(blocker, new HashSet<>());
        blockMap.get(blocker).add(blocked);
    }

    public static boolean isBlocked(String sender, String receiver) {
        return blockMap.getOrDefault(receiver, new HashSet<>()).contains(sender);
    }

    public static Set<String> getBlockedUsers(String user) {
        return blockMap.getOrDefault(user, new HashSet<>());
    }

    public static boolean unblockUser(String blocker, String blocked) {
        Set<String> blockedUsers = blockMap.get(blocker);
        if (blockedUsers != null) {
            return blockedUsers.remove(blocked);
        }
        return false;
    }
}
