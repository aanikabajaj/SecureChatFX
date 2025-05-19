public class ScheduledMessage implements Comparable<ScheduledMessage> {
    String sender;
    String receiver;
    String encryptedMessage;
    long deliveryTimeMillis;
    int priority;  // NEW FIELD: High, Medium, Low

    public ScheduledMessage(String sender, String receiver, String encryptedMessage, long deliveryTimeMillis, int priority) {
        this.sender = sender;
        this.receiver = receiver;
        this.encryptedMessage = encryptedMessage;
        this.deliveryTimeMillis = deliveryTimeMillis;
        this.priority = priority;
    }


    // Getter for priority (optional)
    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(ScheduledMessage other) {
        // First sort by delivery time
        int timeComparison = Long.compare(this.deliveryTimeMillis, other.deliveryTimeMillis);
        if (timeComparison != 0) {
            return timeComparison;
        }

        // If delivery times are equal, prioritize High > Medium > Low
        return Integer.compare(this.priority, other.priority); 
    }

    // Helper to assign numeric value to priority
    private int priorityValue(String p) {
        switch (p.toLowerCase()) {
            case "high": return 3;
            case "medium": return 2;
            case "low": return 1;
            default: return 0;
        }
    }
}
