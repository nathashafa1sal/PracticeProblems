import java.util.*;

public class problem3 {

    // Entry class for DNS records
    static class DNSEntry {
        String domain;
        String ipAddress;
        long expiryTime;

        DNSEntry(String domain, String ipAddress, int ttlSeconds) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    // LRU Cache
    private LinkedHashMap<String, DNSEntry> cache;

    private int capacity;
    private int hits = 0;
    private int misses = 0;

    public problem3(int capacity) {

        this.capacity = capacity;

        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > problem3.this.capacity;
            }
        };

        startCleanupThread();
    }

    // Resolve domain
    public synchronized String resolve(String domain) {

        long start = System.nanoTime();

        if (cache.containsKey(domain)) {

            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;
                long time = System.nanoTime() - start;
                System.out.println("Cache HIT → " + entry.ipAddress +
                        " (" + time / 1_000_000.0 + " ms)");
                return entry.ipAddress;
            } else {
                cache.remove(domain);
                System.out.println("Cache EXPIRED");
            }
        }

        misses++;

        String ip = queryUpstreamDNS(domain);

        DNSEntry newEntry = new DNSEntry(domain, ip, 300);
        cache.put(domain, newEntry);

        System.out.println("Cache MISS → Query upstream → " + ip + " (TTL: 300s)");

        return ip;
    }

    // Simulated upstream DNS query
    private String queryUpstreamDNS(String domain) {

        try {
            Thread.sleep(100); // simulate 100ms network latency
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Random r = new Random();
        return "172.217.14." + r.nextInt(255);
    }

    // Cleanup expired entries periodically
    private void startCleanupThread() {

        Thread cleaner = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (this) {
                    Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();

                    while (it.hasNext()) {
                        Map.Entry<String, DNSEntry> entry = it.next();
                        if (entry.getValue().isExpired()) {
                            it.remove();
                        }
                    }
                }
            }
        });

        cleaner.setDaemon(true);
        cleaner.start();
    }

    // Cache statistics
    public void getCacheStats() {

        int total = hits + misses;

        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);

        System.out.println("Cache Hits: " + hits);
        System.out.println("Cache Misses: " + misses);
        System.out.println("Hit Rate: " + hitRate + "%");
    }

    // Demo
    public static void main(String[] args) throws Exception {

        problem3 dnsCache = new problem3(5);

        dnsCache.resolve("google.com");
        dnsCache.resolve("google.com");

        dnsCache.resolve("openai.com");
        dnsCache.resolve("github.com");

        Thread.sleep(2000);

        dnsCache.resolve("google.com");

        dnsCache.getCacheStats();
    }
}