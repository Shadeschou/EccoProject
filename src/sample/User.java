package sample;

public class User {
    private int id;
    private static User instance;
    private String role;
    private String fullName;
    private double balance;

    private User(int id) {
        this.id = id;
        //constructor class using data from DB
    }

    public static User getInstance() throws NullPointerException {
        if (instance == null) {
            throw new NullPointerException("User: instance is null");
        }
        return instance;
    }

    public static User getInstance(int id) {
        if (instance == null) {
            instance = new User(id);
        }
        return instance;
    }

    public int getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }

    public double getBalance() {
        return balance;
    }
}
