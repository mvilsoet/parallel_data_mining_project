import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PerformanceComparisonTest {

    private static final String LARGE_TEST_FILE = "simplewiki-latest-pages-articles.xml.bz2";

    private static final int QUEUE_CAPACITY = 10000;
    private static final int NUM_CONSUMERS_SOME_PARALLEL = 4;

    /**
     * Measure performance with three different consumer-thread configurations:
     *  1) Sequential (1 consumer)
     *  2) Some parallel (e.g., 4 consumers)
     *  3) Maximum parallel (number of available processors)
     * Print out the timing results for comparison.
     */
    @Test
    public void testPerformanceComparison() throws Exception {
        // 1) Sequential
        runAndMeasurePerformance(1, "Sequential");

        // 2) Some parallel
        runAndMeasurePerformance(NUM_CONSUMERS_SOME_PARALLEL, "Some Parallel (" + NUM_CONSUMERS_SOME_PARALLEL + " threads)");

        // 3) Maximum parallel
        int maxParallel = Runtime.getRuntime().availableProcessors();
        runAndMeasurePerformance(maxParallel, "Max Parallel (" + maxParallel + " threads)");
    }

    private void runAndMeasurePerformance(int numConsumers, String label) throws Exception {
        // Load the large Wikipedia dump test file from src/test/resources.
        InputStream input = getClass().getClassLoader().getResourceAsStream(LARGE_TEST_FILE);
        assertNotNull(input, "Could not find " + LARGE_TEST_FILE + " in resources!");

        long startTime = System.currentTimeMillis();

        ParallelProcessingPipeline pipeline = new ParallelProcessingPipeline(numConsumers, QUEUE_CAPACITY);
        pipeline.runPipeline(input);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.printf(
                "%s run with %d consumer thread(s) took %d ms%n",
                label, numConsumers, duration
        );
    }
}
