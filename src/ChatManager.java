import java.math.BigInteger;
import java.util.*;

public class ChatManager {
    public static ChatRanker chatRanker = new ChatRanker();
    public static HashMap<String, User> users = new HashMap<>();
    public static HashMap<String, LinkedList<Message>> inbox = new HashMap<>();
    private Scanner scanner = new Scanner(System.in);

    public void start() {
        while (true) {
            System.out.println("\n--- Secure Messaging ---");
            System.out.println("1. Register\n2. Login\n3. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine(); // clear newline

            if (choice == 1) registerUser();
            else if (choice == 2) loginUser();
            else break;
        }
    }

    private void registerUser() {
        System.out.print("Enter username: ");
        String uname = scanner.nextLine();
        if (users.containsKey(uname)) {
            System.out.println("Username already exists.");
            return;
        }
        System.out.print("Enter password: ");
        String pwd = scanner.nextLine();
        users.put(uname, new User(uname, pwd));
        inbox.put(uname, new LinkedList<>());
        System.out.println("User registered successfully!");
    }

    private void loginUser() {
        System.out.print("Username: ");
        String uname = scanner.nextLine();
        System.out.print("Password: ");
        String pwd = scanner.nextLine();

        User user = users.get(uname);
        if (user != null && user.checkPassword(pwd)) {
            System.out.println("Login successful.");
            chatInterface(uname);
        } else {
            System.out.println("Invalid credentials.");
        }
        Queue<Message> pending = MessageQueue.getAndClearMessages(uname);
        if (!pending.isEmpty()) {
            System.out.println("You have " + pending.size() + " queued messages!");
            inbox.get(uname).addAll(pending);
        }

    }

    private void chatInterface(String username) {
        while (true) {
            System.out.println("\nWelcome, " + username);
            System.out.println("1. Send Message\n2. View Inbox\n3. Search Messages\n4. View Top Contacts\n5. Block User\n6. Create Group\n7. Join Group\n8. Message Group\n9. Add members in group\n10. Schedule Message\n11. Manage Blocked Users\n12. Logout\n");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.print("Send to: ");
                String to = scanner.nextLine();
                if (!users.containsKey(to)) {
                    System.out.println("User not found.");
                    continue;
                }
                if (BlockManager.isBlocked(username, to)) {
                    System.out.println("You are blocked by this user. Cannot send message.");
                    continue;
                }
                
                
                System.out.print("Message: ");

                Thread typingThread = new Thread(() -> {
                    try {
                        TypingIndicatorManager.setTyping(to, username);
                        Thread.sleep(3000); // Simulate 3s of typing indicator
                        TypingIndicatorManager.clearTyping(to);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                typingThread.start();

                String msg = scanner.nextLine();
                typingThread.interrupt(); // Stop early if message entered quickly
                TypingIndicatorManager.clearTyping(to); // Ensure it's cleared

                
                
                if (ProfanityFilter.containsProfanity(msg)) {
                    System.out.println("Message contains inappropriate words. Not sent.");
                    continue;
                }
                
                User receiver = users.get(to);
                BigInteger encryptedBig = receiver.rsa.encrypt(msg);
                String encrypted = encryptedBig.toString();

                Message message = new Message(username, to, encrypted);
                if (inbox.containsKey(to)) {
                    inbox.get(to).add(message);
                    System.out.println("User is online. Message delivered.");
                } else {
                    MessageQueue.addMessage(to, message);
                    System.out.println("User is offline. Message queued.");
                }
                chatRanker.recordInteraction(username, to);
                
            } else if (choice == 2) {
                LinkedList<Message> msgs = inbox.get(username);
                String typer = TypingIndicatorManager.checkTyping(username);
                if (typer != null) {
                    System.out.println(typer + " is typing...");
                }

                if (msgs.isEmpty()) {
                    System.out.println("No messages.");
                } else {
                    for (Message m : msgs) {
                        BigInteger encryptedBig = new BigInteger(m.getContent());
                        String decrypted = users.get(username).rsa.decrypt(encryptedBig);
                        System.out.println(m.getTimestamp() + " [" + m.getSender() + "]: " + decrypted);

                    }
                }
            } 
            else if (choice == 3) {
                System.out.print("Enter keyword to search: ");
                String keyword = scanner.nextLine();
                List<Message> results = SearchEngine.searchMessages(inbox.get(username), keyword);
                if (results.isEmpty()) {
                    System.out.println("No messages found with that keyword.");
                } else {
                    for (Message m : results) {
                        System.out.println(m.getTimestamp() + " [" + m.getSender() + "]: " + Encryptor.decrypt(m.getContent()));
                    }
                }
            }
            else if (choice == 4) {
                List<String> topContacts = chatRanker.getTopContacts(username, 3);
                System.out.println("Top Chats:");
                for (String contact : topContacts) {
                    System.out.println("- " + contact);
                }
            }
            else if (choice == 5) {
                System.out.print("Enter username to block: ");
                String toBlock = scanner.nextLine();
                BlockManager.blockUser(username, toBlock);
                System.out.println("User blocked successfully.");
            }  
            else if (choice == 6) {
                System.out.print("Enter group name to create: ");
                String gname = scanner.nextLine();
                GroupManager.createGroup(gname, username);
                System.out.println("Group created and you were added.");
            }
            
            else if (choice == 7) {
                System.out.print("Enter group name to join: ");
                String gname = scanner.nextLine();
                if (GroupManager.joinGroup(gname, username)) {
                    System.out.println("Joined group successfully.");
                } else {
                    System.out.println("Group not found.");
                }
            }
            
            else if (choice == 8) {
                System.out.print("Enter group name to message: ");
                String gname = scanner.nextLine();
            
                if (!GroupManager.groupExists(gname)) {
                    System.out.println("Group does not exist.");
                    continue;
                }
            
                System.out.print("Message: ");
                String msg = scanner.nextLine();
            
                if (ProfanityFilter.containsProfanity(msg)) {
                    System.out.println("Message contains inappropriate words. Not sent.");
                    continue;
                }
            
                String encrypted = Encryptor.encrypt(msg);
                Set<String> members = GroupManager.getGroupMembers(gname);
            
                for (String member : members) {
                    if (!member.equals(username)) {
                        Message message = new Message(username, member, encrypted);
                        if (inbox.containsKey(member)) {
                            inbox.get(member).add(message);
                        } else {
                            MessageQueue.addMessage(member, message);
                        }
                        chatRanker.recordInteraction(username, member); // update rank
                    }
                }
            
                System.out.println("Message sent to group: " + gname);
            }
            else if (choice == 9) {
                System.out.print("Enter group name: ");
                String gname = scanner.nextLine();
                System.out.print("Enter username to add: ");
                String toAdd = scanner.nextLine();
            
                if (!users.containsKey(toAdd)) {
                    System.out.println("User does not exist.");
                    continue;
                }
            
                if (GroupManager.groupExists(gname)) {
                    GroupManager.joinGroup(gname, toAdd);
                    System.out.println("User added to group.");
                } else {
                    System.out.println("Group not found.");
                }
            }
            else if (choice == 10) {
                System.out.print("Send to: ");
                String to = scanner.nextLine();
            
                if (!users.containsKey(to)) {
                    System.out.println("User not found.");
                    continue;
                }
            
                if (BlockManager.isBlocked(username, to)) {
                    System.out.println("You are blocked by this user.");
                    continue;
                }
            
                System.out.print("Message: ");
                String msg = scanner.nextLine();
            
                if (ProfanityFilter.containsProfanity(msg)) {
                    System.out.println("Inappropriate message. Not scheduled.");
                    continue;
                }
            
                System.out.print("Send after how many seconds? ");
                int seconds = Integer.parseInt(scanner.nextLine());

                System.out.print("Enter priority (1 = High, 10 = Low): ");
                int priority = Integer.parseInt(scanner.nextLine());

                if (priority < 1 || priority > 10) {
                    System.out.println("Invalid priority. Must be between 1 and 10.");
                    continue;
                }

                long deliveryTime = System.currentTimeMillis() + (seconds * 1000L);
                String encrypted = Encryptor.encrypt(msg);

                ScheduledMessage sm = new ScheduledMessage(username, to, encrypted, deliveryTime, priority);
                MessageScheduler.scheduleMessage(sm);

                System.out.println("Message scheduled with priority " + priority + ".");

            }

            else if (choice == 11) {
                Set<String> blockedUsers = BlockManager.getBlockedUsers(username);
                if (blockedUsers.isEmpty()) {
                    System.out.println("You have not blocked any users.");
                } else {
                    System.out.println("Blocked Users:");
                    for (String user : blockedUsers) {
                        System.out.println("- " + user);
                    }
                    System.out.print("Enter username to unblock (or press Enter to cancel): ");
                    String toUnblock = scanner.nextLine();
                    if (!toUnblock.isEmpty()) {
                        boolean success = BlockManager.unblockUser(username, toUnblock);
                        if (success) {
                            System.out.println(toUnblock + " has been unblocked.");
                        } else {
                            System.out.println("Failed to unblock " + toUnblock + ".");
                        }   
                    }
                }
            }
            
                    
            
            else break;
        }
    }

    public static void saveChat(String username, LinkedList<Message> messages) {
        StringBuilder chatData = new StringBuilder();
        for (Message m : messages) {
            chatData.append(m.getTimestamp())
                    .append(" [").append(m.getSender()).append("]: ")
                    .append(Encryptor.decrypt(m.getContent())).append("\n");
        }
        String compressed = HuffmanCompressor.compress(chatData.toString());
        try {
            java.nio.file.Files.write(java.nio.file.Paths.get(username + "_chat.txt"), compressed.getBytes());
            System.out.println("Chat saved successfully (compressed).");
        } catch (Exception e) {
            System.out.println("Failed to save chat: " + e.getMessage());
        }
    }

    

    public boolean registerUser(String uname, String pwd) {
        if (users.containsKey(uname)) {
            return false; // already exists
        }
        users.put(uname, new User(uname, pwd));
        inbox.put(uname, new LinkedList<>());
        return true;
    }

    public boolean loginUser(String uname, String pwd) {
        User user = users.get(uname);
        if (user != null && user.checkPassword(pwd)) {
        // You can load queued messages here if you want (optional)
            return true;
        }
        return false;
    }

    public boolean sendMessage(String fromUser, String toUser, String message) {
        if (!users.containsKey(fromUser) || !users.containsKey(toUser)) {
            return false;
        }
    
        LinkedList<Message> userInbox = inbox.get(toUser);
        if (userInbox == null) {
            userInbox = new LinkedList<>();
            inbox.put(toUser, userInbox);
        }
    
        User receiver = users.get(toUser);
        BigInteger encryptedBig = receiver.rsa.encrypt(message);
        String encrypted = encryptedBig.toString();
    
        Message newMessage = new Message(fromUser, toUser, encrypted);
        userInbox.add(newMessage);
    
        addMessageToConversation(fromUser, toUser, newMessage);
    
        chatRanker.recordInteraction(fromUser, toUser);
        return true;
    }
    

    public static HashMap<String, User> getUsers() {
        return users;
    }
    
    public static Map<String, Map<String, LinkedList<Message>>> conversations = new HashMap<>();

    public static void addMessageToConversation(String sender, String receiver, Message message) {
        conversations.putIfAbsent(sender, new HashMap<>());
        conversations.get(sender).putIfAbsent(receiver, new LinkedList<>());
        conversations.get(sender).get(receiver).add(message);

        conversations.putIfAbsent(receiver, new HashMap<>());
        conversations.get(receiver).putIfAbsent(sender, new LinkedList<>());
        conversations.get(receiver).get(sender).add(message);
    }

    public static boolean blockUser(String blocker, String blockee) {
        if (!users.containsKey(blockee)) {
            return false; // User to block does not exist
        }
        BlockManager.blockUser(blocker, blockee);
        return true;
    }
    

}
