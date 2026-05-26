package library_manage.Model;

public class Author extends Person {
    int numberOfReaders;

    public Author(String name) {
        super(name);
        numberOfReaders = 0;
    }

    public Author(String name, int numberOfReaders) {
        super(name);
        this.numberOfReaders = numberOfReaders;
    }

    public int getNumberOfReaders() {
        return numberOfReaders;
    }

    public void setNumberOfReaders(int numberOfReaders) {
        this.numberOfReaders = numberOfReaders;
    }

    public void incrementNumberOfReaders() {
        numberOfReaders++;
    }

}
