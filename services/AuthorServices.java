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

    public List<Author> getAllAuthors() {
        return authors;
    }

    public ArrayList<Author> getAuthors() {
        return authors;
    }

    public Author findAuthorByName(String authorName) {
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

    public Author findOrCreateAuthor(String authorName) {
        Author existingAuthor = findAuthorByName(authorName);
        if (existingAuthor != null) {
            return existingAuthor;
        }

        Author newAuthor = new Author(authorName);
        getAllAuthors().add(newAuthor);
        TxtDataStore.saveAuthors(authors);
        return newAuthor;
    }

    public ServiceResult getMostreadersAuthors() {
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

    public void saveAuthors() {
        TxtDataStore.saveAuthors(authors);
    }
}
