import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WikipediaXMLParser {

    public List<Page> parse(InputStream input) throws Exception {
        List<Page> pages = new ArrayList<>();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
        XMLStreamReader reader = factory.createXMLStreamReader(input);

        Page currentPage = null;
        Revision currentRevision = null;
        String currentElement = null;

        // Loop over the XML stream
        while (reader.hasNext()) {
            int event = reader.next();
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    currentElement = reader.getLocalName();
                    if ("page".equals(currentElement)) {
                        currentPage = new Page();
                    } else if ("revision".equals(currentElement)) {
                        currentRevision = new Revision();
                    } else if ("redirect".equals(currentElement) && currentPage != null) {
                        // Handle the redirect element; it is an empty element with an attribute "title"
                        String redirectTitle = reader.getAttributeValue(null, "title");
                        currentPage.setRedirect(redirectTitle);
                    }
                    break;

                case XMLStreamConstants.CHARACTERS:
                    String text = reader.getText().trim();
                    if (text.isEmpty() || currentElement == null) {
                        break;
                    }
                    // Depending on the current element and context, set the corresponding fields
                    if ("title".equals(currentElement) && currentPage != null && currentPage.getTitle() == null) {
                        currentPage.setTitle(text);
                    } else if ("ns".equals(currentElement) && currentPage != null && currentPage.getNs() == null) {
                        currentPage.setNs(text);
                    } else if ("id".equals(currentElement)) {
                        // "id" appears both for page and revision.
                        // We assume that the first encountered id inside <page> is the page id,
                        // and within <revision> the first id is the revision id.
                        if (currentRevision != null && currentRevision.getId() == null) {
                            currentRevision.setId(text);
                        } else if (currentPage != null && currentPage.getId() == null) {
                            currentPage.setId(text);
                        }
                    } else if ("timestamp".equals(currentElement) && currentRevision != null) {
                        currentRevision.setTimestamp(text);
                    } else if ("comment".equals(currentElement) && currentRevision != null) {
                        currentRevision.setComment(text);
                    } else if ("text".equals(currentElement) && currentRevision != null) {
                        currentRevision.setText(text);
                    } else if ("parentid".equals(currentElement) && currentRevision != null) {
                        currentRevision.setParentId(text);
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    String endElement = reader.getLocalName();
                    if ("revision".equals(endElement)) {
                        if (currentPage != null) {
                            currentPage.setRevision(currentRevision);
                        }
                        currentRevision = null;
                    } else if ("page".equals(endElement)) {
                        pages.add(currentPage);
                        currentPage = null;
                    }
                    currentElement = null;
                    break;
            }
        }
        reader.close();
        return pages;
    }
}
