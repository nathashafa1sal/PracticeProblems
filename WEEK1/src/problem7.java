import java.util.*;

public class problem7 {

    // Trie Node
    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        List<String> queries = new ArrayList<>();
    }

    private TrieNode root = new TrieNode();

    // query -> frequency
    private Map<String, Integer> frequencyMap = new HashMap<>();

    private static final int TOP_K = 10;

    // Insert query into Trie
    public void addQuery(String query, int freq) {

        frequencyMap.put(query, freq);

        TrieNode node = root;

        for (char c : query.toCharArray()) {

            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);

            if (!node.queries.contains(query)) {
                node.queries.add(query);
            }
        }
    }

    // Update frequency after a new search
    public void updateFrequency(String query) {

        int newFreq = frequencyMap.getOrDefault(query, 0) + 1;
        frequencyMap.put(query, newFreq);

        addQuery(query, newFreq);
    }

    // Search autocomplete suggestions
    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) {
                return new ArrayList<>();
            }
            node = node.children.get(c);
        }

        PriorityQueue<String> pq =
                new PriorityQueue<>(
                        (a, b) -> frequencyMap.get(a) - frequencyMap.get(b)
                );

        for (String q : node.queries) {

            pq.offer(q);

            if (pq.size() > TOP_K) {
                pq.poll();
            }
        }

        List<String> result = new ArrayList<>();

        while (!pq.isEmpty()) {
            result.add(pq.poll());
        }

        Collections.reverse(result);
        return result;
    }

    // Demo
    public static void main(String[] args) {

        problem7 system = new problem7();

        system.addQuery("java tutorial", 1234567);
        system.addQuery("javascript", 987654);
        system.addQuery("java download", 456789);
        system.addQuery("java 21 features", 100);

        System.out.println("Search results for 'jav':");

        List<String> results = system.search("jav");

        int rank = 1;
        for (String r : results) {
            System.out.println(rank + ". " + r + " (" +
                    system.frequencyMap.get(r) + " searches)");
            rank++;
        }

        System.out.println("\nUpdating frequency...");
        system.updateFrequency("java 21 features");

        System.out.println(system.search("jav"));
    }
}