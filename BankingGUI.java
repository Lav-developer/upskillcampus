import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

public class BankingGUI extends JFrame {
    private final Map<String, BankAccount> accounts = new HashMap<>();
    private final DefaultTableModel tableModel;   // now properly initialized

    private JTable accountTable;
    private JTextField searchField;
    private JButton searchButton, refreshButton;
    private JLabel summaryLabel;

    private static final String PASSWORD = "admin123";

    public BankingGUI() {
        // Initialize the table model here (in the constructor)
        String[] columns = {"Account No.", "Holder Name", "Type", "Balance (₹)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        setTitle("🏦 Banking Information System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header
        JLabel header = new JLabel("Banking Information System", JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(new Color(0, 102, 204));
        header.setBorder(new EmptyBorder(10, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tabbedPane.addTab("📋 All Accounts", createAllAccountsPanel());
        tabbedPane.addTab("➕ Create Account", createCreateAccountPanel());
        tabbedPane.addTab("💰 Deposit", createDepositPanel());
        tabbedPane.addTab("💸 Withdraw", createWithdrawPanel());
        tabbedPane.addTab("🔄 Transfer", createTransferPanel());
        tabbedPane.addTab("🔍 Account Details", createDetailsPanel());
        tabbedPane.addTab("📈 Apply Interest", createInterestPanel());
        tabbedPane.addTab("🗑️ Delete Account", createDeletePanel());
        tabbedPane.addTab("📤 Export CSV", createExportPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Status bar
        JLabel status = new JLabel("Ready", JLabel.CENTER);
        status.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        status.setForeground(Color.GRAY);
        status.setBorder(new EmptyBorder(5, 0, 5, 0));
        add(status, BorderLayout.SOUTH);

        refreshTable(); // populate table
    }

    // ====================== UI Panels (unchanged except minor adjustments) ======================

    private JPanel createAllAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search by name:"));
        searchField = new JTextField(15);
        searchPanel.add(searchField);
        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> refreshTable());
        searchPanel.add(searchButton);
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshTable());
        searchPanel.add(refreshButton);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Use the already-initialized tableModel
        accountTable = new JTable(tableModel);
        accountTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        accountTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        accountTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(accountTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        summaryLabel = new JLabel();
        summaryLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        panel.add(summaryLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCreateAccountPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel accLabel = new JLabel("Account Number:");
        JTextField accField = new JTextField(15);
        JLabel nameLabel = new JLabel("Holder Name:");
        JTextField nameField = new JTextField(15);
        JLabel typeLabel = new JLabel("Account Type:");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Savings", "Current"});
        JLabel depositLabel = new JLabel("Initial Deposit (₹):");
        JTextField depositField = new JTextField(15);

        int row = 0;
        addRow(panel, gbc, accLabel, accField, row++);
        addRow(panel, gbc, nameLabel, nameField, row++);
        addRow(panel, gbc, typeLabel, typeCombo, row++);
        addRow(panel, gbc, depositLabel, depositField, row++);

        JButton createBtn = new JButton("Create Account");
        createBtn.setBackground(new Color(0, 153, 76));
        createBtn.setForeground(Color.WHITE);
        createBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(createBtn, gbc);

        createBtn.addActionListener(e -> {
            String accNum = accField.getText().trim();
            String name = nameField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            String depositStr = depositField.getText().trim();
            if (accNum.isEmpty() || name.isEmpty()) {
                showError("Account number and name cannot be empty.");
                return;
            }
            if (accounts.containsKey(accNum)) {
                showError("Account already exists.");
                return;
            }
            try {
                double deposit = Double.parseDouble(depositStr);
                if (deposit <= 0) {
                    showError("Initial deposit must be > 0.");
                    return;
                }
                BankAccount acc = new BankAccount(accNum, name, type, deposit);
                accounts.put(accNum, acc);
                showSuccess("Account created successfully!");
                refreshTable();
                clearFields(accField, nameField, depositField);
            } catch (NumberFormatException ex) {
                showError("Invalid deposit amount.");
            }
        });

        return panel;
    }

    private JPanel createDepositPanel() {
        return createTransactionPanel("Deposit", "Enter deposit amount (₹):", (acc, amt) -> {
            acc.deposit(amt);
            return "Successfully deposited ₹" + amt;
        });
    }

    private JPanel createWithdrawPanel() {
        return createTransactionPanel("Withdraw", "Enter withdrawal amount (₹):", (acc, amt) -> {
            acc.withdraw(amt);
            return "Successfully withdrawn ₹" + amt;
        });
    }

    private JPanel createTransactionPanel(String title, String prompt, TransactionAction action) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel accLabel = new JLabel("Account Number:");
        JTextField accField = new JTextField(15);
        JLabel amtLabel = new JLabel("Amount (₹):");
        JTextField amtField = new JTextField(15);

        int row = 0;
        addRow(panel, gbc, accLabel, accField, row++);
        addRow(panel, gbc, amtLabel, amtField, row++);

        JButton actionBtn = new JButton(title);
        actionBtn.setBackground(new Color(0, 102, 204));
        actionBtn.setForeground(Color.WHITE);
        actionBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(actionBtn, gbc);

        actionBtn.addActionListener(e -> {
            String accNum = accField.getText().trim();
            if (accNum.isEmpty()) {
                showError("Account number cannot be empty.");
                return;
            }
            BankAccount acc = accounts.get(accNum);
            if (acc == null) {
                showError("Account not found.");
                return;
            }
            try {
                double amount = Double.parseDouble(amtField.getText().trim());
                String msg = action.execute(acc, amount);
                showSuccess(msg);
                refreshTable();
                clearFields(accField, amtField);
            } catch (NumberFormatException ex) {
                showError("Invalid amount.");
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });

        return panel;
    }

    private JPanel createTransferPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel fromLabel = new JLabel("From Account:");
        JTextField fromField = new JTextField(15);
        JLabel toLabel = new JLabel("To Account:");
        JTextField toField = new JTextField(15);
        JLabel amtLabel = new JLabel("Amount (₹):");
        JTextField amtField = new JTextField(15);

        int row = 0;
        addRow(panel, gbc, fromLabel, fromField, row++);
        addRow(panel, gbc, toLabel, toField, row++);
        addRow(panel, gbc, amtLabel, amtField, row++);

        JButton transferBtn = new JButton("Transfer");
        transferBtn.setBackground(new Color(204, 102, 0));
        transferBtn.setForeground(Color.WHITE);
        transferBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(transferBtn, gbc);

        transferBtn.addActionListener(e -> {
            String fromAcc = fromField.getText().trim();
            String toAcc = toField.getText().trim();
            if (fromAcc.isEmpty() || toAcc.isEmpty()) {
                showError("Both account numbers required.");
                return;
            }
            if (fromAcc.equals(toAcc)) {
                showError("Cannot transfer to the same account.");
                return;
            }
            BankAccount from = accounts.get(fromAcc);
            BankAccount to = accounts.get(toAcc);
            if (from == null || to == null) {
                showError("One or both accounts not found.");
                return;
            }
            try {
                double amount = Double.parseDouble(amtField.getText().trim());
                if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
                if (from.getBalance() < amount) throw new IllegalArgumentException("Insufficient balance.");
                from.withdraw(amount);
                to.deposit(amount);
                from.addTransaction("Transfer to " + toAcc + " : -₹" + amount);
                to.addTransaction("Transfer from " + fromAcc + " : +₹" + amount);
                showSuccess("Transferred ₹" + amount + " from " + fromAcc + " to " + toAcc);
                refreshTable();
                clearFields(fromField, toField, amtField);
            } catch (NumberFormatException ex) {
                showError("Invalid amount.");
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel accLabel = new JLabel("Account Number:");
        JTextField accField = new JTextField(15);
        JTextArea detailsArea = new JTextArea(12, 40);
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(detailsArea);

        int row = 0;
        addRow(panel, gbc, accLabel, accField, row++);

        JButton showBtn = new JButton("Show Details");
        showBtn.setBackground(new Color(0, 102, 204));
        showBtn.setForeground(Color.WHITE);
        showBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(showBtn, gbc);
        row++;

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scroll, gbc);

        showBtn.addActionListener(e -> {
            String accNum = accField.getText().trim();
            if (accNum.isEmpty()) {
                showError("Enter account number.");
                return;
            }
            BankAccount acc = accounts.get(accNum);
            if (acc == null) {
                showError("Account not found.");
                return;
            }
            String info = acc.getAccountInfo() + "\n\n--- Transaction History ---\n";
            List<String> history = acc.getTransactionHistory();
            if (history.isEmpty()) info += "No transactions yet.";
            else info += String.join("\n", history);
            detailsArea.setText(info);
        });

        return panel;
    }

    private JPanel createInterestPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel accLabel = new JLabel("Account Number (leave blank for all Savings):");
        JTextField accField = new JTextField(15);
        JLabel monthsLabel = new JLabel("Number of months:");
        JTextField monthsField = new JTextField(15);
        monthsField.setText("1");

        int row = 0;
        addRow(panel, gbc, accLabel, accField, row++);
        addRow(panel, gbc, monthsLabel, monthsField, row++);

        JButton applyBtn = new JButton("Apply Interest");
        applyBtn.setBackground(new Color(0, 153, 76));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(applyBtn, gbc);

        applyBtn.addActionListener(e -> {
            String accNum = accField.getText().trim();
            int months;
            try {
                months = Integer.parseInt(monthsField.getText().trim());
                if (months <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showError("Please enter a valid positive number of months.");
                return;
            }

            if (accNum.isEmpty()) {
                int count = 0;
                for (BankAccount acc : accounts.values()) {
                    if ("Savings".equalsIgnoreCase(acc.getAccountType())) {
                        acc.applyInterest(months);
                        count++;
                    }
                }
                if (count == 0) {
                    showError("No Savings accounts found.");
                } else {
                    showSuccess("Interest applied to " + count + " savings account(s).");
                    refreshTable();
                }
            } else {
                BankAccount acc = accounts.get(accNum);
                if (acc == null) {
                    showError("Account not found.");
                    return;
                }
                if (!"Savings".equalsIgnoreCase(acc.getAccountType())) {
                    showError("Interest can only be applied to Savings accounts.");
                    return;
                }
                acc.applyInterest(months);
                showSuccess("Interest applied to account " + accNum + " for " + months + " month(s).");
                refreshTable();
            }
            clearFields(accField);
        });

        return panel;
    }

    private JPanel createDeletePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel accLabel = new JLabel("Account Number to delete:");
        JTextField accField = new JTextField(15);

        int row = 0;
        addRow(panel, gbc, accLabel, accField, row++);

        JButton deleteBtn = new JButton("Delete Account");
        deleteBtn.setBackground(new Color(204, 0, 0));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(deleteBtn, gbc);

        deleteBtn.addActionListener(e -> {
            String accNum = accField.getText().trim();
            if (accNum.isEmpty()) {
                showError("Enter account number.");
                return;
            }
            if (!accounts.containsKey(accNum)) {
                showError("Account not found.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete account " + accNum + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                accounts.remove(accNum);
                showSuccess("Account deleted.");
                refreshTable();
                accField.setText("");
            }
        });

        return panel;
    }

    private JPanel createExportPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel label = new JLabel("Export all account details and transaction history to CSV.");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(label, gbc);

        JButton exportBtn = new JButton("Choose Location & Export");
        exportBtn.setBackground(new Color(0, 102, 204));
        exportBtn.setForeground(Color.WHITE);
        exportBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 1;
        panel.add(exportBtn, gbc);

        exportBtn.addActionListener(e -> {
            if (accounts.isEmpty()) {
                showError("No accounts to export.");
                return;
            }
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("banking_export.csv"));
            int option = chooser.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    exportToCSV(file);
                    showSuccess("Export completed to " + file.getAbsolutePath());
                } catch (IOException ex) {
                    showError("Error writing file: " + ex.getMessage());
                }
            }
        });

        return panel;
    }

    // ====================== Helper Methods ======================

    private void refreshTable() {
        tableModel.setRowCount(0);
        String search = searchField.getText().trim().toLowerCase();
        for (BankAccount acc : accounts.values()) {
            if (search.isEmpty() || acc.getAccountHolder().toLowerCase().contains(search)) {
                tableModel.addRow(new Object[]{
                        acc.getAccountNumber(),
                        acc.getAccountHolder(),
                        acc.getAccountType(),
                        acc.getBalance()
                });
            }
        }
        int count = accounts.size();
        double total = accounts.values().stream().mapToDouble(BankAccount::getBalance).sum();
        summaryLabel.setText("Total accounts: " + count + " | Total balance: ₹" + String.format("%.2f", total));
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, JLabel label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        panel.add(label, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        panel.add(field, gbc);
    }

    private void clearFields(JTextField... fields) {
        for (JTextField f : fields) f.setText("");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, "❌ " + msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, "✅ " + msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportToCSV(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("Account Number,Holder Name,Type,Balance,Transaction History\n");
        for (BankAccount acc : accounts.values()) {
            sb.append(acc.getAccountNumber()).append(",")
              .append(acc.getAccountHolder()).append(",")
              .append(acc.getAccountType()).append(",")
              .append(acc.getBalance()).append(",");
            List<String> history = acc.getTransactionHistory();
            if (history.isEmpty()) {
                sb.append("No transactions");
            } else {
                sb.append(String.join("; ", history));
            }
            sb.append("\n");
        }
        Files.write(file.toPath(), sb.toString().getBytes());
    }

    @FunctionalInterface
    private interface TransactionAction {
        String execute(BankAccount acc, double amount) throws IllegalArgumentException;
    }

    // ====================== Login Dialog ======================

    private static boolean showLoginDialog() {
        JDialog loginDialog = new JDialog((Frame) null, "Login", true);
        loginDialog.setLayout(new GridBagLayout());
        loginDialog.setSize(350, 180);
        loginDialog.setLocationRelativeTo(null);
        loginDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(15);
        JButton loginBtn = new JButton("Login");
        JButton cancelBtn = new JButton("Cancel");

        gbc.gridx = 0; gbc.gridy = 0;
        loginDialog.add(userLabel, gbc);
        gbc.gridx = 1;
        loginDialog.add(userField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        loginDialog.add(passLabel, gbc);
        gbc.gridx = 1;
        loginDialog.add(passField, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(loginBtn);
        btnPanel.add(cancelBtn);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginDialog.add(btnPanel, gbc);

        final boolean[] authenticated = {false};

        loginBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());
            if (!user.isEmpty() && pass.equals(PASSWORD)) {
                authenticated[0] = true;
                loginDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(loginDialog, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> {
            authenticated[0] = false;
            loginDialog.dispose();
        });

        loginDialog.getRootPane().setDefaultButton(loginBtn);
        loginDialog.setVisible(true);
        return authenticated[0];
    }

    // ====================== Main ======================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            if (!showLoginDialog()) {
                System.exit(0);
            }
            BankingGUI gui = new BankingGUI();
            gui.setVisible(true);
        });
    }
}

// ====================== BankAccount Class (Enhanced) ======================

class BankAccount {
    private final String accountNumber;
    private final String accountHolder;
    private final String accountType;
    private double balance;
    private double interestRate;
    private final List<String> transactionHistory;

    public BankAccount(String accountNumber, String accountHolder, String accountType, double initialDeposit) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.accountType = accountType;
        this.balance = initialDeposit;
        this.transactionHistory = new ArrayList<>();

        if ("Savings".equalsIgnoreCase(accountType)) {
            this.interestRate = 4.0;
        } else {
            this.interestRate = 0.0;
        }
        addTransaction("Account opened with ₹" + initialDeposit);
    }

    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolder() { return accountHolder; }
    public String getAccountType() { return accountType; }
    public double getBalance() { return balance; }
    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double rate) { this.interestRate = rate; }
    public List<String> getTransactionHistory() { return Collections.unmodifiableList(transactionHistory); }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
        balance += amount;
        addTransaction("Deposited ₹" + amount);
    }

    public void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive.");
        if (amount > balance) throw new IllegalArgumentException("Insufficient balance.");
        balance -= amount;
        addTransaction("Withdrew ₹" + amount);
    }

    public void addTransaction(String desc) {
        transactionHistory.add(desc + " | Balance: ₹" + balance);
    }

    public void applyInterest(int months) {
        if (months <= 0) throw new IllegalArgumentException("Months must be positive.");
        if (interestRate == 0) {
            addTransaction("Interest not applied (rate is 0%)");
            return;
        }
        double interest = balance * (interestRate / 100) * (months / 12.0);
        if (interest <= 0) return;
        balance += interest;
        addTransaction(String.format("Interest applied (%.2f%% for %d months): +₹%.2f", interestRate, months, interest));
    }

    public String getAccountInfo() {
        return "📄 Account Number: " + accountNumber + "\n" +
               "👤 Account Holder: " + accountHolder + "\n" +
               "🏷️ Account Type: " + accountType + "\n" +
               "📊 Interest Rate: " + interestRate + "% p.a.\n" +
               "💰 Current Balance: ₹" + balance;
    }
}