import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.InputStream;
import java.util.List;

public class WikipediaXMLParserTest {

    @Test
    public void testParseAnarchyArticle() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("testPage.xml");
        assertNotNull(input, "The test XML file for the anarchy article should be found in the resources");

        WikipediaXMLParser parser = new WikipediaXMLParser();
        List<Page> pages = parser.parse(input);

        // We expect one page (the Anarchism article) in our test XML.
        assertEquals(1, pages.size(), "There should be 1 page parsed");

        Page page = pages.get(0);
        assertEquals("Anarchism", page.getTitle());
        assertEquals("0", page.getNs());
        assertEquals("123", page.getId());

        // Verify that the revision is parsed correctly.
        Revision revision = page.getRevision();
        assertNotNull(revision, "Revision should not be null");
        assertEquals("456", revision.getId());
        assertEquals("Anarchism is a political philosophy that advocates for self-governed societies based on voluntary institutions.",
                revision.getText());
    }
}
