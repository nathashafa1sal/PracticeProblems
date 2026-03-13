import java.util.*;

public class problem10 {

    static class Video {
        String id;
        String data;

        Video(String id, String data) {
            this.id = id;
            this.data = data;
        }
    }

    // L1 cache
    LinkedHashMap<String, Video> L1 =
            new LinkedHashMap<>(10000,0.75f,true) {
                protected boolean removeEldestEntry(Map.Entry eldest) {
                    return size() > 10000;
                }
            };

    // L2 cache
    LinkedHashMap<String, Video> L2 =
            new LinkedHashMap<>(100000,0.75f,true) {
                protected boolean removeEldestEntry(Map.Entry eldest) {
                    return size() > 100000;
                }
            };

    // Simulated database
    Map<String, Video> database = new HashMap<>();

    int L1hits=0, L2hits=0, L3hits=0;

    public problem10() {

        for(int i=1;i<=1000;i++){
            database.put("video_"+i,new Video("video_"+i,"data"+i));
        }
    }

    public Video getVideo(String id){

        if(L1.containsKey(id)){
            L1hits++;
            System.out.println("L1 HIT");
            return L1.get(id);
        }

        if(L2.containsKey(id)){
            L2hits++;
            System.out.println("L2 HIT → promoted to L1");

            Video v = L2.get(id);
            L1.put(id,v);
            return v;
        }

        System.out.println("L3 DATABASE HIT");

        Video v = database.get(id);

        if(v!=null){
            L3hits++;
            L2.put(id,v);
        }

        return v;
    }

    public void getStatistics(){

        int total = L1hits + L2hits + L3hits;

        System.out.println("\nCache Statistics:");
        System.out.println("L1 Hits: "+L1hits);
        System.out.println("L2 Hits: "+L2hits);
        System.out.println("L3 Hits: "+L3hits);

        if(total>0){
            System.out.println("L1 Hit Rate: "+(100*L1hits/total)+"%");
            System.out.println("L2 Hit Rate: "+(100*L2hits/total)+"%");
            System.out.println("L3 Hit Rate: "+(100*L3hits/total)+"%");
        }
    }

    public static void main(String[] args){

        problem10 cache = new problem10();

        cache.getVideo("video_123");
        cache.getVideo("video_123");
        cache.getVideo("video_999");

        cache.getStatistics();
    }
}
