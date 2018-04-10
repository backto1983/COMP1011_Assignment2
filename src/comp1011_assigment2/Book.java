package comp1011_assigment2;

/**
 *
 * @author Henrique
 */
public class Book {
    private int bookID, year;
    private String title, author, genre;

    public Book(int year, String title, String author, String genre) {
        setYear(year);
        setTitle(title);
        setAuthor(author);
        setGenre(genre);
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        if(bookID >= 0)        
            this.bookID = bookID;
        else
            throw new IllegalArgumentException("Book ID must be equal or greather than 0");
    }
    

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    
    
}
