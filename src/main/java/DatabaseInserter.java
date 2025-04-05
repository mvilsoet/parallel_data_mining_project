import java.util.ArrayList;
import java.util.List;

public class DatabaseInserter {
    private final List<PageAnalysis> insertedPages = new ArrayList<>();

    // Simulated batch insert
    public synchronized void insert(PageAnalysis analysis) {
        insertedPages.add(analysis);
        // In a real implementation, use JDBC batch operations
        System.out.println("Inserted page: " + analysis.getPageTitle() + " with term frequencies: " + analysis.getTermFrequencies());
    }

    public List<PageAnalysis> getInsertedPages() {
        return insertedPages;
    }
}
