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

    public ParallelProcessingPipeline(int consumerCount, int queueCapacity) {
        this.queue = new ArrayBlockingQueue<>(queueCapacity);
        this.dbInserter = new DatabaseInserter();
        this.globalDocFrequencies = new ConcurrentHashMap<>();
        this.consumerPool = Executors.newFixedThreadPool(consumerCount);
    }

    public void runPipeline(InputStream input) throws Exception {
        int consumerCount = ((ThreadPoolExecutor) consumerPool).getCorePoolSize();
        // Start consumer threads.
        for (int i = 0; i < consumerCount; i++) {
            consumerPool.submit(new PageConsumer(queue, dbInserter, globalDocFrequencies));
        }

        // Producer: parse pages from the input stream.
        WikipediaXMLParser parser = new WikipediaXMLParser();
        List<Page> pages = parser.parse(input);
        for (Page page : pages) {
            queue.put(page);
        }

        // Insert a poison pill for each consumer to signal completion.
        for (int i = 0; i < consumerCount; i++) {
            queue.put(POISON_PILL);
        }

        consumerPool.shutdown();
        consumerPool.awaitTermination(10, TimeUnit.MINUTES);
    }

    public DatabaseInserter getDatabaseInserter() {
        return dbInserter;
    }

    public ConcurrentHashMap<String, AtomicInteger> getGlobalDocFrequencies() {
        return globalDocFrequencies;
    }
}
