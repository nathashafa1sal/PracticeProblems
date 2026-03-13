import java.util.*;

public class problem9 {

    static class Transaction {
        int id;
        int amount;
        String merchant;
        String account;
        long time;

        Transaction(int id, int amount, String merchant, String account, long time) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.account = account;
            this.time = time;
        }
    }

    List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    // Classic Two Sum
    public List<String> findTwoSum(int target) {

        Map<Integer, Transaction> map = new HashMap<>();
        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                Transaction prev = map.get(complement);
                result.add("(" + prev.id + "," + t.id + ")");
            }

            map.put(t.amount, t);
        }

        return result;
    }

    // Two Sum with time window (1 hour)
    public List<String> twoSumWithWindow(int target) {

        long oneHour = 3600 * 1000;
        List<String> result = new ArrayList<>();

        for (int i = 0; i < transactions.size(); i++) {

            for (int j = i + 1; j < transactions.size(); j++) {

                Transaction a = transactions.get(i);
                Transaction b = transactions.get(j);

                if (Math.abs(a.time - b.time) <= oneHour &&
                        a.amount + b.amount == target) {

                    result.add("(" + a.id + "," + b.id + ")");
                }
            }
        }

        return result;
    }

    // Duplicate detection
    public List<String> detectDuplicates() {

        Map<String, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            String key = t.amount + "-" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t);
        }

        List<String> result = new ArrayList<>();

        for (String key : map.keySet()) {

            List<Transaction> list = map.get(key);

            if (list.size() > 1) {
                result.add("Duplicate: " + key + " count=" + list.size());
            }
        }

        return result;
    }

    // K Sum (simple recursive)
    public void kSum(List<Integer> nums, int target, int k,
                     List<Integer> current, int start) {

        if (k == 0 && target == 0) {
            System.out.println(current);
            return;
        }

        if (k == 0) return;

        for (int i = start; i < nums.size(); i++) {

            current.add(nums.get(i));

            kSum(nums, target - nums.get(i), k - 1, current, i + 1);

            current.remove(current.size() - 1);
        }
    }

    public static void main(String[] args) {

        problem9 system = new problem9();

        long now = System.currentTimeMillis();

        system.addTransaction(new Transaction(1, 500, "StoreA", "acc1", now));
        system.addTransaction(new Transaction(2, 300, "StoreB", "acc2", now));
        system.addTransaction(new Transaction(3, 200, "StoreC", "acc3", now));

        System.out.println("Two Sum:");
        System.out.println(system.findTwoSum(500));

        System.out.println("Duplicates:");
        System.out.println(system.detectDuplicates());

        List<Integer> nums = Arrays.asList(500,300,200);
        System.out.println("K Sum:");
        system.kSum(nums,1000,3,new ArrayList<>(),0);
    }
}