import model.PageAnalysis;
import java.util.ArrayList;
import java.util.List;

public class DatabaseInserter {
    private final List<PageAnalysis> insertedPages = new ArrayList<>();

    /**
     * Optimistic database insert simulates an insert. Local insertedPages holds in-memory.
     *
     * @param analysis the PageAnalysis instance to insert
     * @return true indicating that the insert was "successful"
     */
    public synchronized boolean insert(PageAnalysis analysis) {
        insertedPages.add(analysis);
        // Simulate an optimistic insert with a log statement.
        System.out.println("Inserted page: " + analysis.getPageTitle() +
                " with term frequencies: " + analysis.getTermFrequencies());
        return true;
    }

    public List<PageAnalysis> getInsertedPages() {
        return insertedPages;
    }
}
