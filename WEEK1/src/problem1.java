import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class problem1 {

    private ConcurrentHashMap<String, Integer> userTable = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, Integer> attemptFrequency = new ConcurrentHashMap<>();

    public problem1() {
        // sample existing users
        userTable.put("john_doe", 1);
        userTable.put("admin", 2);
        userTable.put("player1", 3);
    }

    // Check username availability
    public boolean checkAvailability(String username) {

        // track attempts
        attemptFrequency.put(username,
                attemptFrequency.getOrDefault(username, 0) + 1);

        // O(1) lookup
        return !userTable.containsKey(username);
    }

    // Suggest alternative usernames
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String candidate = username + i;
            if (!userTable.containsKey(candidate)) {
                suggestions.add(candidate);
            }
        }

        String modified = username.replace("_", ".");
        if (!userTable.containsKey(modified)) {
            suggestions.add(modified);
        }

        return suggestions;
    }

    // Register username
    public void registerUser(String username, int userId) {
        userTable.put(username, userId);
    }

    // Get most attempted username
    public String getMostAttempted() {

        String mostAttempted = null;
        int max = 0;

        for (Map.Entry<String, Integer> entry : attemptFrequency.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                mostAttempted = entry.getKey();
            }
        }

        return mostAttempted + " (" + max + " attempts)";
    }

    // Demo
    public static void main(String[] args) {

        problem1 system = new problem1();

        System.out.println(system.checkAvailability("john_doe"));
        // false

        System.out.println(system.checkAvailability("jane_smith"));
        // true

        System.out.println(system.suggestAlternatives("john_doe"));
        // [john_doe1, john_doe2, john_doe3, john_doe4, john_doe5, john.doe]

        System.out.println(system.getMostAttempted());
    }
}