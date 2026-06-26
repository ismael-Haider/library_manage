package library_manage.services;

import java.util.ArrayList;
import java.util.List;
import library_manage.Model.Author;
import library_manage.util.*;

public class AuthorServices {
    private final ArrayList<Author> authors;

    public AuthorServices() {
        this.authors = TxtDataStore.loadAuthors();
    }

    public List<Author> getAllAuthors() { // delete this method because we already have getAllAuthors() method that
                                          // returns the same thing
        return authors;
    }

    public ArrayList<Author> getAuthors() { // delete this method because we already have getAllAuthors() method that
                                            // returns the same thing
        return authors;
    }

    public Author findAuthorByName(String authorName) { // we using this in class
        // BookServices when we want to find an author by name from the disk when we
        // start the application , and in here in findOrCreateAuthor()

        if (authorName == null || authorName.trim().isEmpty()) {
            return null;
        }

        for (Author author : authors) {
            if (author.getName().equalsIgnoreCase(authorName)) {
                return author;
            }
        }

        return null;
    }

    public Author findOrCreateAuthor(String authorName) { // we using this in class BookServices when we want load the
                                                          // Book from disk when it not found
                                                          // we using this in class BookController when we want to
                                                          // add Book
        Author existingAuthor = findAuthorByName(authorName);
        if (existingAuthor != null) {
            return existingAuthor;
        }

        Author newAuthor = new Author(authorName);
        getAllAuthors().add(newAuthor);
        TxtDataStore.saveAuthors(authors);
        return newAuthor;
    }

    public ServiceResult getMostreadersAuthors() { // we using this in class transactionController when we want to
                                                   // display top 10 in panal reports
        authors.sort(
                (b1, b2) -> Integer.compare(
                        b2.getNumberOfReaders(),
                        b1.getNumberOfReaders()));

        if (authors.size() <= 10) {
            return new ServiceResult(true, "Most readers authors found", authors);
        } else {
            return new ServiceResult(true, "Most readers authors found", authors.subList(0, 10));
        }
    }

     public void saveAuthors() { // delete this method ,where we use this method ? we don't have data for authors 
        TxtDataStore.saveAuthors(authors);
    }
}
