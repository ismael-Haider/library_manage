package library_manage.Model;

  public class Author extends Person {
     int numberOfReaders;
    public Author(String name) {
        super(name);
        numberOfReaders = 0;
    }

    public  int getNumberOfReaders() {
        return numberOfReaders;
    }

    public void incrementNumberOfReaders() {
        numberOfReaders++;
    }
    
}
