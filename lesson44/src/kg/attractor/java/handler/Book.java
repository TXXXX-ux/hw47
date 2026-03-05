package kg.attractor.java.handler;

public class Book {
    private int id;
    private String title;
    private String author;
    private String image;
    private String status;
    private String description;

    public Book(int id, String title, String author, String image, String status, String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.image = image;
        this.status = status;
        this.description = description;
    }

    public void setStatus(String status) { this.status = status; }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getImage() { return image; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
}