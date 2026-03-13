import java.util.*;

public class problem4 {

    private static final int N = 5; // 5-gram window

    // ngram -> set of documents containing it
    private HashMap<String, Set<String>> ngramIndex = new HashMap<>();

    // document -> list of ngrams
    private HashMap<String, List<String>> documentNgrams = new HashMap<>();

    // Add document to database
    public void addDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);

        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {
            ngramIndex.putIfAbsent(gram, new HashSet<>());
            ngramIndex.get(gram).add(docId);
        }
    }

    // Analyze document for plagiarism
    public void analyzeDocument(String docId) {

        List<String> ngrams = documentNgrams.get(docId);

        if (ngrams == null) {
            System.out.println("Document not found.");
            return;
        }

        System.out.println("Extracted " + ngrams.size() + " n-grams");

        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {

            if (ngramIndex.containsKey(gram)) {

                for (String otherDoc : ngramIndex.get(gram)) {

                    if (!otherDoc.equals(docId)) {
                        matchCount.put(
                                otherDoc,
                                matchCount.getOrDefault(otherDoc, 0) + 1
                        );
                    }
                }
            }
        }

        for (Map.Entry<String, Integer> entry : matchCount.entrySet()) {

            String otherDoc = entry.getKey();
            int matches = entry.getValue();

            double similarity = (matches * 100.0) / ngrams.size();

            System.out.println("Found " + matches +
                    " matching n-grams with \"" + otherDoc + "\"");

            System.out.printf("Similarity: %.2f%% ", similarity);

            if (similarity > 60) {
                System.out.println("(PLAGIARISM DETECTED)");
            } else if (similarity > 10) {
                System.out.println("(Suspicious)");
            } else {
                System.out.println("(Low similarity)");
            }
        }
    }

    // Generate n-grams
    private List<String> generateNgrams(String text) {

        List<String> result = new ArrayList<>();

        String[] words = text.toLowerCase().split("\\s+");

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            result.add(gram.toString().trim());
        }

        return result;
    }

    // Demo
    public static void main(String[] args) {

        problem4 detector = new problem4();

        String essay1 = "machine learning is a field of artificial intelligence that focuses on learning from data";
        String essay2 = "machine learning is a field of artificial intelligence that focuses on training models";
        String essay3 = "football is a popular sport played worldwide with millions of fans";

        detector.addDocument("essay_089.txt", essay1);
        detector.addDocument("essay_092.txt", essay2);
        detector.addDocument("essay_123.txt", essay1 + " " + essay2);

        detector.analyzeDocument("essay_123.txt");
    }
}