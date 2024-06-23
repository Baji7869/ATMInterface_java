import java.util.Scanner;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDateTime;
class ATMInterface {
    private static ATMSystem atmSystem;
    private static User currentUser;

    public static void main(String[] args) {
        atmSystem = new ATMSystem();
        atmSystem.addUser("user1", "1234");
        atmSystem.addUser("user2", "5678");

        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the ATM system");

        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();
        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();

        currentUser = atmSystem.validateUser(userId, pin);

        if (currentUser == null) {
            System.out.println("Invalid User ID or PIN.");
            return;
        }

        System.out.println("Login successful.");

        boolean quit = false;
        while (!quit) {
            System.out.println("\nATM Menu:");
            System.out.println("1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Quit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    currentUser.getATM().printTransactionHistory();
                    break;
                case 2:
                    System.out.print("Enter amount to withdraw: ");
                    double withdrawAmount = scanner.nextDouble();
                    currentUser.getATM().withdraw(withdrawAmount);
                    break;
                case 3:
                    System.out.print("Enter amount to deposit: ");
                    double depositAmount = scanner.nextDouble();
                    currentUser.getATM().deposit(depositAmount);
                    break;
                case 4:
                    System.out.print("Enter recipient User ID: ");
                    scanner.nextLine();  // consume the newline
                    String recipientId = scanner.nextLine();
                    System.out.print("Enter amount to transfer: ");
                    double transferAmount = scanner.nextDouble();
                    User recipient = atmSystem.validateUser(recipientId, "");
                    if (recipient != null) {
                        currentUser.getATM().transfer(recipient, transferAmount);
                    } else {
                        System.out.println("Invalid recipient User ID.");
                    }
                    break;
                case 5:
                    quit = true;
                    System.out.println("Thank you for using the ATM system.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();
    }
}


class ATMSystem {
    private HashMap<String, User> users;

    public ATMSystem() {
        users = new HashMap<>();
    }

    public void addUser(String userId, String pin) {
        users.put(userId, new User(userId, pin));
    }

    public User validateUser(String userId, String pin) {
        User user = users.get(userId);
        if (user != null && user.getPin().equals(pin)) {
            return user;
        }
        return null;
    }
}


class Transaction {
    private String type;
    private double amount;
    private double balanceAfter;
    private LocalDateTime timestamp;

    public Transaction(String type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return timestamp + " | " + type + ": $" + amount + " | Balance after: $" + balanceAfter;
    }
}

class User {
    private String userId;
    private String pin;
    private ATM atm;

    public User(String userId, String pin) {
        this.userId = userId;
        this.pin = pin;
        this.atm = new ATM();
    }

    public String getUserId() {
        return userId;
    }

    public String getPin() {
        return pin;
    }

    public ATM getATM() {
        return atm;
    }
}


class ATM {
    private ArrayList<Transaction> transactions;
    private double balance;

    public ATM() {
        this.transactions = new ArrayList<>();
        this.balance = 0;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            transactions.add(new Transaction("Deposit", amount, balance));
            System.out.println("Deposited: $" + amount);
        } else {
            System.out.println("Invalid amount.");
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            transactions.add(new Transaction("Withdraw", amount, balance));
            System.out.println("Withdrew: $" + amount);
        } else {
            System.out.println("Insufficient balance or invalid amount.");
        }
    }

    public void transfer(User recipient, double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            recipient.getATM().deposit(amount);
            transactions.add(new Transaction("Transfer to " + recipient.getUserId(), amount, balance));
            System.out.println("Transferred: $" + amount + " to User ID: " + recipient.getUserId());
        } else {
            System.out.println("Insufficient balance or invalid amount.");
        }
    }

    public void printTransactionHistory() {
        System.out.println("Transaction History:");
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }
}