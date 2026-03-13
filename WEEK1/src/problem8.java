import java.util.*;

public class problem8 {

    enum Status { EMPTY, OCCUPIED, DELETED }

    static class ParkingSpot {
        String licensePlate;
        long entryTime;
        Status status;

        ParkingSpot() {
            status = Status.EMPTY;
        }
    }

    private static final int CAPACITY = 500;
    private ParkingSpot[] table = new ParkingSpot[CAPACITY];

    private int occupied = 0;
    private int totalProbes = 0;
    private int parkOperations = 0;

    public problem8() {
        for (int i = 0; i < CAPACITY; i++) {
            table[i] = new ParkingSpot();
        }
    }

    // Custom hash function
    private int hash(String license) {
        return Math.abs(license.hashCode()) % CAPACITY;
    }

    // Park vehicle
    public void parkVehicle(String license) {

        int index = hash(license);
        int probes = 0;

        while (table[index].status == Status.OCCUPIED) {
            index = (index + 1) % CAPACITY;
            probes++;
        }

        table[index].licensePlate = license;
        table[index].entryTime = System.currentTimeMillis();
        table[index].status = Status.OCCUPIED;

        occupied++;
        totalProbes += probes;
        parkOperations++;

        System.out.println(
                "parkVehicle(\"" + license + "\") → Assigned spot #" +
                        index + " (" + probes + " probes)"
        );
    }

    // Exit vehicle
    public void exitVehicle(String license) {

        int index = hash(license);
        int probes = 0;

        while (table[index].status != Status.EMPTY) {

            if (table[index].status == Status.OCCUPIED &&
                    license.equals(table[index].licensePlate)) {

                long durationMs = System.currentTimeMillis() - table[index].entryTime;
                double hours = durationMs / 3600000.0;

                double fee = Math.max(1, hours) * 5; // $5/hour

                table[index].status = Status.DELETED;
                occupied--;

                System.out.printf(
                        "exitVehicle(\"%s\") → Spot #%d freed, Duration: %.2fh, Fee: $%.2f\n",
                        license, index, hours, fee
                );
                return;
            }

            index = (index + 1) % CAPACITY;
            probes++;
        }

        System.out.println("Vehicle not found.");
    }

    // Find nearest available spot
    public void findNearestSpot() {

        for (int i = 0; i < CAPACITY; i++) {
            if (table[i].status != Status.OCCUPIED) {
                System.out.println("Nearest available spot: #" + i);
                return;
            }
        }

        System.out.println("Parking lot full.");
    }

    // Parking statistics
    public void getStatistics() {

        double occupancyRate = (occupied * 100.0) / CAPACITY;
        double avgProbes = parkOperations == 0 ? 0 : (double) totalProbes / parkOperations;

        System.out.println("\n=== Parking Statistics ===");
        System.out.printf("Occupancy: %.2f%%\n", occupancyRate);
        System.out.printf("Average Probes: %.2f\n", avgProbes);
        System.out.println("Peak Hour: 2–3 PM (simulated)");
    }

    // Demo
    public static void main(String[] args) throws InterruptedException {

        problem8 parking = new problem8();

        parking.parkVehicle("ABC-1234");
        parking.parkVehicle("ABC-1235");
        parking.parkVehicle("XYZ-9999");

        Thread.sleep(2000);

        parking.exitVehicle("ABC-1234");

        parking.findNearestSpot();

        parking.getStatistics();
    }
}