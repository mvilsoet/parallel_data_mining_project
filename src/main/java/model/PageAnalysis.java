package model;

import java.util.Map;

public class PageAnalysis {
    private final String pageTitle;
    private final Map<String, Integer> termFrequencies;

    public PageAnalysis(String pageTitle, Map<String, Integer> termFrequencies) {
        this.pageTitle = pageTitle;
        this.termFrequencies = termFrequencies;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public Map<String, Integer> getTermFrequencies() {
        return termFrequencies;
    }

    @Override
    public String toString() {
        return "model.PageAnalysis [pageTitle=" + pageTitle + ", termFrequencies=" + termFrequencies + "]";
    }
}
