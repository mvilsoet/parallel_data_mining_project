package model;

// model.Revision.java
public class Revision {
    private String id;
    private String parentId;
    private String timestamp;
    private String comment;
    private String text;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    @Override
    public String toString() {
        return "model.Revision [id=" + id + ", parentId=" + parentId + ", timestamp=" + timestamp
                + ", comment=" + comment + ", text=" + (text != null && text.length() > 50 ? text.substring(0, 50) + "..." : text) + "]";
    }
}
