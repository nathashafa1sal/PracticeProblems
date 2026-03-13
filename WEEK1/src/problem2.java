import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class problem2 {

    private ConcurrentHashMap<String, AtomicInteger> inventory = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, LinkedHashMap<Integer, Integer>> waitingLists = new ConcurrentHashMap<>();

    public problem2() {
        inventory.put("IPHONE15_256GB", new AtomicInteger(100));
        waitingLists.put("IPHONE15_256GB", new LinkedHashMap<>());
    }

    public String checkStock(String productId) {
        AtomicInteger stock = inventory.get(productId);
        if (stock == null) {
            return "Product not found";
        }
        return stock.get() + " units available";
    }

    public synchronized String purchaseItem(String productId, int userId) {

        AtomicInteger stock = inventory.get(productId);

        if (stock == null) {
            return "Product not found";
        }

        if (stock.get() > 0) {
            int remaining = stock.decrementAndGet();
            return "Success, " + remaining + " units remaining";
        } else {

            LinkedHashMap<Integer, Integer> waitingList = waitingLists.get(productId);

            int position = waitingList.size() + 1;
            waitingList.put(userId, position);

            return "Added to waiting list, position #" + position;
        }
    }

    public void printWaitingList(String productId) {

        LinkedHashMap<Integer, Integer> waitingList = waitingLists.get(productId);

        for (Map.Entry<Integer, Integer> entry : waitingList.entrySet()) {
            System.out.println("User " + entry.getKey() + " -> Position " + entry.getValue());
        }
    }

    public static void main(String[] args) {

        problem2 manager = new problem2();

        System.out.println(manager.checkStock("IPHONE15_256GB"));

        System.out.println(manager.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 67890));

        for (int i = 0; i < 100; i++) {
            manager.purchaseItem("IPHONE15_256GB", i);
        }

        System.out.println(manager.purchaseItem("IPHONE15_256GB", 99999));

        manager.printWaitingList("IPHONE15_256GB");
    }
}