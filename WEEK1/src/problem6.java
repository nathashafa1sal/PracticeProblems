import java.util.concurrent.ConcurrentHashMap;

public class problem6 {

    // Token Bucket class
    static class TokenBucket {

        int maxTokens;
        double refillRate;
        double tokens;
        long lastRefillTime;

        TokenBucket(int maxTokens, double refillRate) {
            this.maxTokens = maxTokens;
            this.refillRate = refillRate;
            this.tokens = maxTokens;
            this.lastRefillTime = System.currentTimeMillis();
        }

        private void refill() {
            long now = System.currentTimeMillis();
            double seconds = (now - lastRefillTime) / 1000.0;

            double refillTokens = seconds * refillRate;

            tokens = Math.min(maxTokens, tokens + refillTokens);
            lastRefillTime = now;
        }

        public synchronized boolean allowRequest() {
            refill();

            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }
            return false;
        }

        public synchronized int getRemainingTokens() {
            refill();
            return (int) tokens;
        }

        public synchronized long getRetryAfterSeconds() {
            if (tokens >= 1) return 0;
            return (long) Math.ceil(1 / refillRate);
        }
    }

    private ConcurrentHashMap<String, TokenBucket> clientBuckets = new ConcurrentHashMap<>();

    private static final int MAX_REQUESTS = 1000;
    private static final int WINDOW_SECONDS = 3600;
    private static final double REFILL_RATE = (double) MAX_REQUESTS / WINDOW_SECONDS;

    public String checkRateLimit(String clientId) {

        clientBuckets.putIfAbsent(
                clientId,
                new TokenBucket(MAX_REQUESTS, REFILL_RATE)
        );

        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket.allowRequest()) {
            return "Allowed (" + bucket.getRemainingTokens() + " requests remaining)";
        } else {
            return "Denied (0 requests remaining, retry after "
                    + bucket.getRetryAfterSeconds() + "s)";
        }
    }

    public String getRateLimitStatus(String clientId) {

        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket == null) {
            return "Client not found";
        }

        int remaining = bucket.getRemainingTokens();
        int used = MAX_REQUESTS - remaining;

        long reset = System.currentTimeMillis() / 1000 + WINDOW_SECONDS;

        return "{used: " + used +
                ", limit: " + MAX_REQUESTS +
                ", reset: " + reset + "}";
    }

    public static void main(String[] args) {

        problem6 limiter = new problem6(); // FIXED

        String client = "abc123";

        for (int i = 0; i < 5; i++) {
            System.out.println(limiter.checkRateLimit(client));
        }

        System.out.println(limiter.getRateLimitStatus(client));
    }
}