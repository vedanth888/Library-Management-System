import java.util.*;
import java.time.*;

// ------------------- Exceptions -------------------
class BookNotAvailableException extends Exception {
    public BookNotAvailableException(String message) { super(message); }
}

class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) { super(message); }
}

// ------------------- Models -------------------
class Book {
    private String bookId;
    private String title;
    private String author;
    private boolean isAvailable;

    public Book(String bookId, String title, String author) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isAvailable = true;
    }

    public String getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    @Override
    public String toString() {
        return "Book ID: " + bookId + ", Title: " + title + ", Author: " + author +
               ", Available: " + isAvailable;
    }
}

class Student {
    private String studentId;
    private String name;

    public Student(String studentId, String name) {
        this.studentId = studentId;
        this.name = name;
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return "Student ID: " + studentId + ", Name: " + name;
    }
}

class Librarian {
    private String librarianId;
    private String name;

    public Librarian(String librarianId, String name) {
        this.librarianId = librarianId;
        this.name = name;
    }

    public String getLibrarianId() { return librarianId; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return "Librarian ID: " + librarianId + ", Name: " + name;
    }
}

class Transaction {
    private Book book;
    private Student student;
    private LocalDateTime issueDate;
    private LocalDateTime returnDate;

    public Transaction(Book book, Student student) {
        this.book = book;
        this.student = student;
        this.issueDate = LocalDateTime.now();
    }

    public void returnBook() {
        this.returnDate = LocalDateTime.now();
        book.setAvailable(true);
    }

    @Override
    public String toString() {
        return "Book: " + book.getTitle() + ", Student: " + student.getName() +
               ", Issue Date: " + issueDate +
               ", Return Date: " + (returnDate != null ? returnDate : "Not returned yet");
    }
}

// ------------------- Service -------------------
class LibraryService {
    private List<Book> books = new ArrayList<>();
    private List<Student> students = new ArrayList<>();
    private List<Librarian> librarians = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();

    // Add initial data
    public void initialize() {
        books.add(new Book("B001", "Java Programming", "John Doe"));
        books.add(new Book("B002", "Data Structures", "Jane Smith"));
        books.add(new Book("B003", "Database Systems", "Alan Turing"));

        students.add(new Student("S001", "Alice"));
        students.add(new Student("S002", "Bob"));

        librarians.add(new Librarian("L001", "Mr. Admin"));
    }

    public void issueBook(String bookId, String studentId) throws BookNotAvailableException, UserNotFoundException {
        Book book = findBook(bookId);
        if (!book.isAvailable()) throw new BookNotAvailableException("Book is not available for issue.");
        Student student = findStudent(studentId);
        if (student == null) throw new UserNotFoundException("Student not found.");

        book.setAvailable(false);
        Transaction transaction = new Transaction(book, student);
        transactions.add(transaction);
        System.out.println("Book issued successfully!");
    }

    public void returnBook(String bookId, String studentId) throws UserNotFoundException {
        for (Transaction t : transactions) {
            if (t.student.getStudentId().equals(studentId) &&
                t.book.getBookId().equals(bookId) &&
                t.returnDate == null) {
                t.returnBook();
                System.out.println("Book returned successfully!");
                return;
            }
        }
        throw new UserNotFoundException("No such issued book found for the student.");
    }

    public void listAvailableBooks() {
        System.out.println("Available Books:");
        for (Book b : books) if (b.isAvailable()) System.out.println(b);
    }

    public void listTransactions() {
        System.out.println("Transaction History:");
        for (Transaction t : transactions) System.out.println(t);
    }

    private Book findBook(String bookId) {
        for (Book b : books) if (b.getBookId().equals(bookId)) return b;
        return null;
    }

    private Student findStudent(String studentId) {
        for (Student s : students) if (s.getStudentId().equals(studentId)) return s;
        return null;
    }
}

// ------------------- Main -------------------
public class LibraryManagementSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LibraryService service = new LibraryService();
        service.initialize();

        while (true) {
            System.out.println("\n===== Library Management System =====");
            System.out.println("1. List Available Books");
            System.out.println("2. Issue Book");
            System.out.println("3. Return Book");
            System.out.println("4. View Transaction History");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1" -> service.listAvailableBooks();
                    case "2" -> {
                        System.out.print("Enter Book ID: ");
                        String bookId = scanner.nextLine();
                        System.out.print("Enter Student ID: ");
                        String studentId = scanner.nextLine();
                        service.issueBook(bookId, studentId);
                    }
                    case "3" -> {
                        System.out.print("Enter Book ID: ");
                        String bookId = scanner.nextLine();
                        System.out.print("Enter Student ID: ");
                        String studentId = scanner.nextLine();
                        service.returnBook(bookId, studentId);
                    }
                    case "4" -> service.listTransactions();
                    case "5" -> {
                        System.out.println("Exiting...");
                        scanner.close();
                        return;
                    }
                    default -> System.out.println("Invalid option! Try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
