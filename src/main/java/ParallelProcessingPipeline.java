import model.Page;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelProcessingPipeline {
    public static final Page POISON_PILL = new Page(); // Marker for end-of-stream

    private final BlockingQueue<Page> queue;
    private final DatabaseInserter dbInserter;
    private final ConcurrentHashMap<String, AtomicInteger> globalDocFrequencies;
    private final ExecutorService consumerPool;

    // Shared counter for pages processed
    private final AtomicInteger processedPages = new AtomicInteger(0);

    public ParallelProcessingPipeline(int consumerCount, int queueCapacity) {
        this.queue = new ArrayBlockingQueue<>(queueCapacity);
        this.dbInserter = new DatabaseInserter();
        this.globalDocFrequencies = new ConcurrentHashMap<>();
        this.consumerPool = Executors.newFixedThreadPool(consumerCount);
    }

    public void runPipeline(InputStream input) throws Exception {
        // Create a scheduled executor to update the progress bar once per second.
        ScheduledExecutorService progressScheduler = Executors.newSingleThreadScheduledExecutor();
        progressScheduler.scheduleAtFixedRate(() -> {
            // Use carriage return to rewrite the line rather than printing new lines each time.
            System.out.print("\rPages processed: " + processedPages.get());
        }, 0, 1, TimeUnit.SECONDS);

        try (BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(input)) {
            int consumerCount = ((ThreadPoolExecutor) consumerPool).getCorePoolSize();
            // Start consumer threads, passing the shared processedPages counter.
            for (int i = 0; i < consumerCount; i++) {
                consumerPool.submit(new PageConsumer(queue, dbInserter, globalDocFrequencies, processedPages));
            }

            // Producer: parse pages from the decompressed input stream.
            WikipediaXMLParser parser = new WikipediaXMLParser();
            List<Page> pages = parser.parse(bzIn);
            for (Page page : pages) {
                queue.put(page);
            }

            // Insert a poison pill for each consumer to signal completion.
            for (int i = 0; i < consumerCount; i++) {
                queue.put(POISON_PILL);
            }
            consumerPool.shutdown();
            consumerPool.awaitTermination(10, TimeUnit.MINUTES);
        } finally {
            progressScheduler.shutdownNow();
            System.out.println("\nProcessing complete. Total pages processed: " + processedPages.get());
        }
    }

    public DatabaseInserter getDatabaseInserter() {
        return dbInserter;
    }

    public ConcurrentHashMap<String, AtomicInteger> getGlobalDocFrequencies() {
        return globalDocFrequencies;
    }
}
