// Page.java
public class Page {
    private String title;
    private String ns;
    private String id;
    private String redirect;  // if a redirect element exists, store its title attribute
    private Revision revision;

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getNs() { return ns; }
    public void setNs(String ns) { this.ns = ns; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRedirect() { return redirect; }
    public void setRedirect(String redirect) { this.redirect = redirect; }

    public Revision getRevision() { return revision; }
    public void setRevision(Revision revision) { this.revision = revision; }

    @Override
    public String toString() {
        return "Page [title=" + title + ", ns=" + ns + ", id=" + id
                + ", redirect=" + redirect + ", revision=" + revision + "]";
    }
}
