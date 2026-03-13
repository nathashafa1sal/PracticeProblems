import java.util.*;

public class problem5 {

    // pageUrl -> total visits
    private HashMap<String, Integer> pageViews = new HashMap<>();

    // pageUrl -> unique visitors
    private HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();

    // traffic source -> count
    private HashMap<String, Integer> trafficSources = new HashMap<>();

    // Event class
    static class Event {
        String url;
        String userId;
        String source;

        Event(String url, String userId, String source) {
            this.url = url;
            this.userId = userId;
            this.source = source;
        }
    }

    // Process incoming page view event
    public void processEvent(Event event) {

        // Update page views
        pageViews.put(
                event.url,
                pageViews.getOrDefault(event.url, 0) + 1
        );

        // Track unique visitors
        uniqueVisitors.putIfAbsent(event.url, new HashSet<>());
        uniqueVisitors.get(event.url).add(event.userId);

        // Track traffic sources
        trafficSources.put(
                event.source,
                trafficSources.getOrDefault(event.source, 0) + 1
        );
    }

    // Get top 10 pages
    private List<Map.Entry<String, Integer>> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {
            pq.offer(entry);
            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<Map.Entry<String, Integer>> result = new ArrayList<>(pq);
        result.sort((a, b) -> b.getValue() - a.getValue());

        return result;
    }

    // Display dashboard
    public void getDashboard() {

        System.out.println("\n=== Real-Time Dashboard ===");

        System.out.println("\nTop Pages:");

        List<Map.Entry<String, Integer>> topPages = getTopPages();

        int rank = 1;
        for (Map.Entry<String, Integer> entry : topPages) {

            String url = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(url).size();

            System.out.println(
                    rank + ". " + url +
                            " - " + views + " views (" +
                            unique + " unique)"
            );

            rank++;
        }

        System.out.println("\nTraffic Sources:");

        int total = 0;
        for (int count : trafficSources.values()) {
            total += count;
        }

        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {

            double percent = (entry.getValue() * 100.0) / total;

            System.out.printf(
                    "%s: %.2f%%\n",
                    entry.getKey(),
                    percent
            );
        }
    }

    // Demo simulation
    public static void main(String[] args) throws InterruptedException {

        problem5 analytics = new problem5();

        analytics.processEvent(new Event("/article/breaking-news", "user_123", "google"));
        analytics.processEvent(new Event("/article/breaking-news", "user_456", "facebook"));
        analytics.processEvent(new Event("/sports/championship", "user_777", "direct"));
        analytics.processEvent(new Event("/sports/championship", "user_123", "google"));
        analytics.processEvent(new Event("/sports/championship", "user_888", "google"));
        analytics.processEvent(new Event("/tech/ai", "user_999", "direct"));
        analytics.processEvent(new Event("/tech/ai", "user_321", "facebook"));

        // simulate dashboard refresh every 5 seconds
        while (true) {
            analytics.getDashboard();
            Thread.sleep(5000);
        }
    }
}