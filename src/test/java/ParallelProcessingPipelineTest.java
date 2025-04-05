import model.Page;
import model.PageAnalysis;
import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

public class ParallelProcessingPipelineTest {

    private static final String TEST_XML_FILE = "testPages.xml";

    /**
     * Helper method to load the XML resource as a new InputStream.
     * This is necessary because InputStreams cannot be reused.
     */
    private InputStream loadTestXml() {
        InputStream input = getClass().getClassLoader().getResourceAsStream(TEST_XML_FILE);
        assertNotNull(input, "Test XML file '" + TEST_XML_FILE + "' must be present in the resources");
        return input;
    }

    /**
     * Test that the XML parser correctly parses pages.
     */
    @Test
    public void testXMLParserShouldParsePagesCorrectly() throws Exception {
        InputStream input = loadTestXml();
        WikipediaXMLParser parser = new WikipediaXMLParser();
        List<Page> pages = parser.parse(input);

        assertNotNull(pages, "Pages list should not be null");
        assertEquals(2, pages.size(), "There should be exactly 2 pages parsed");

        // Validate first page details.
        Page firstPage = pages.get(0);
        assertEquals("Anarchism", firstPage.getTitle(), "First page title should be 'Anarchism'");
        assertEquals("123", firstPage.getId(), "First page id should be '123'");
        assertNotNull(firstPage.getRevision(), "First page should have a revision");
        assertEquals("456", firstPage.getRevision().getId(), "model.Revision id should be '456'");
    }

    /**
     * Test that the global document frequency map is updated correctly.
     */
    @Test
    public void testGlobalDocumentFrequency() throws Exception {
        InputStream input = loadTestXml();
        // Create a pipeline instance with 2 consumer threads.
        ParallelProcessingPipeline pipeline = new ParallelProcessingPipeline(2, 10);
        pipeline.runPipeline(input);

        Map<String, AtomicInteger> globalDF = pipeline.getGlobalDocFrequencies();
        assertNotNull(globalDF, "Global document frequency map should not be null");

        // For instance, the word "is" should appear in both documents.
        AtomicInteger isCount = globalDF.get("is");
        assertNotNull(isCount, "Global DF should contain the word 'is'");
        assertEquals(2, isCount.get(), "The word 'is' should appear in 2 documents");
    }

    /**
     * Integration test for the complete pipeline:
     * - Parses the XML.
     * - Processes pages in parallel.
     * - Inserts the analysis into a simulated database.
     */
    @Test
    public void testPipelineIntegration() throws Exception {
        InputStream input = loadTestXml();
        ParallelProcessingPipeline pipeline = new ParallelProcessingPipeline(2, 10);
        pipeline.runPipeline(input);

        List<PageAnalysis> insertedPages = pipeline.getDatabaseInserter().getInsertedPages();
        assertNotNull(insertedPages, "Inserted pages list should not be null");
        assertEquals(2, insertedPages.size(), "There should be 2 pages inserted into the simulated database");

        // Validate that term frequencies have been computed for each page.
        for (PageAnalysis analysis : insertedPages) {
            assertNotNull(analysis.getTermFrequencies(), "Term frequencies for page '" + analysis.getPageTitle() + "' should not be null");
            assertFalse(analysis.getTermFrequencies().isEmpty(), "Term frequencies for page '" + analysis.getPageTitle() + "' should not be empty");
            System.out.println("Analysis for " + analysis.getPageTitle() + ": " + analysis.getTermFrequencies());
        }
    }
}
