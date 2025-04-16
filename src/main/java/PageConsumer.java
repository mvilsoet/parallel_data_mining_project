import model.Page;
import model.PageAnalysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PageConsumer implements Runnable {
    private final BlockingQueue<Page> queue;
    private final DatabaseInserter dbInserter;
    private final ConcurrentHashMap<String, AtomicInteger> globalDocFrequencies;
    private final AtomicInteger processedPages; // shared progress counter

    public PageConsumer(BlockingQueue<Page> queue, DatabaseInserter dbInserter,
                        ConcurrentHashMap<String, AtomicInteger> globalDocFrequencies,
                        AtomicInteger processedPages) {
        this.queue = queue;
        this.dbInserter = dbInserter;
        this.globalDocFrequencies = globalDocFrequencies;
        this.processedPages = processedPages;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Page page = queue.take();
                // Check for poison pill to signal shutdown.
                if (page == ParallelProcessingPipeline.POISON_PILL) {
                    queue.put(page);
                    break;
                }
                if (page.getRevision() != null && page.getRevision().getText() != null) {
                    String text = page.getRevision().getText();
                    Map<String, Integer> tf = computeTermFrequencies(text);

                    // Update global document frequencies (each word only once per document)
                    Set<String> uniqueWords = new HashSet<>(tf.keySet());
                    for (String word : uniqueWords) {
                        globalDocFrequencies.compute(word, (k, v) -> {
                            if (v == null)
                                return new AtomicInteger(1);
                            else {
                                v.incrementAndGet();
                                return v;
                            }
                        });
                    }
                    // Create a PageAnalysis instance and "insert" into the simulated database.
                    PageAnalysis analysis = new PageAnalysis(page.getTitle(), tf);
                    dbInserter.insert(analysis);
                }
                // Increment the shared progress counter after each page is processed.
                processedPages.incrementAndGet();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Map<String, Integer> computeTermFrequencies(String text) {
        Map<String, Integer> tf = new HashMap<>();
        // Simple tokenization: split by non-word characters and convert to lower case.
        String[] tokens = text.toLowerCase().split("\\W+");
        for (String token : tokens) {
            if (token.isEmpty())
                continue;
            tf.put(token, tf.getOrDefault(token, 0) + 1);
        }
        return tf;
    }
}
