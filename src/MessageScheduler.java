import java.util.*;

public class MessageScheduler {
    private static PriorityQueue<ScheduledMessage> queue = new PriorityQueue<>();

    static {
        // Background thread to deliver messages
        Thread schedulerThread = new Thread(() -> {
            while (true) {
                long now = System.currentTimeMillis();
                while (!queue.isEmpty() && queue.peek().deliveryTimeMillis <= now) {
                    ScheduledMessage sm = queue.poll();

                    Message message = new Message(sm.sender, sm.receiver, sm.encryptedMessage);

                    if (ChatManager.inbox.containsKey(sm.receiver)) {
                        ChatManager.inbox.get(sm.receiver).add(message);
                    } else {
                        MessageQueue.addMessage(sm.receiver, message);
                    }

                    ChatManager.chatRanker.recordInteraction(sm.sender, sm.receiver);
                }

                try {
                    Thread.sleep(1000); // Check every second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        schedulerThread.setDaemon(true);
        schedulerThread.start();
    }

    public static void scheduleMessage(ScheduledMessage sm) {
        queue.offer(sm);
    }

    public static List<ScheduledMessage> getAllScheduledMessages() {
        return new ArrayList<>(queue);
    }
    
    public static boolean removeScheduledMessage(ScheduledMessage sm) {
        return queue.remove(sm);
    }
    
}
