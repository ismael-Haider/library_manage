package library_manage.services;

import java.util.ArrayList;
import java.util.List;

import library_manage.Model.Author;
import library_manage.util.*;

public class AuthorServices {
    ArrayList<Author> authors = new ArrayList<>();

    public List<Author> getAllAuthors() {
        return authors;
    }

    public Author findOrCreateAuthor(String authorName) {
        for (Author author : getAllAuthors()) {
            if (author.getName().equalsIgnoreCase(authorName)) {
                return author;
            }
        }
        Author newAuthor = new Author(authorName);
        getAllAuthors().add(newAuthor);
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
}
