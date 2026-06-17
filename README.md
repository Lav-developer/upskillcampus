# 🏦 Banking Information System (Core Java)

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-Desktop_App-blue?style=for-the-badge)
![Upskill Campus](https://img.shields.io/badge/Upskill_Campus-Internship-success?style=for-the-badge)

> A robust, interactive Desktop Banking Application developed in Core Java (Swing/AWT) as part of the **Upskill Campus** internship program.

**Intern:** Lav Kush  
**Domain:** Core Java  

---

## 📑 Table of Contents
- [Overview](#-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
- [Usage & Credentials](#-usage--credentials)
- [Project Structure](#-project-structure)
- [Future Enhancements](#-future-enhancements)

## 📖 Overview
The **Banking Information System** is a comprehensive GUI-based financial management tool. It simulates administrative banking operations, allowing bank staff to manage customer accounts, process daily transactions (deposits, withdrawals, transfers), calculate interest, and securely export data for auditing.

## ✨ Features
- **🔐 Secure Authentication**: Protected admin login system to ensure only authorized personnel can access financial records.
- **📝 Account Management**: Open new *Savings* or *Current* accounts and securely delete closed accounts.
- **💰 Transaction Handling**: Seamlessly perform Deposits, Withdrawals, and Peer-to-Peer Fund Transfers with strict balance validations.
- **📈 Financial Logic**: Automated interest calculation (4% p.a.) applied to Savings accounts over customizable monthly durations.
- **🔍 Real-Time Tracking**: Search accounts by holder name, view real-time bank totals, and track detailed, immutable transaction histories per account.
- **📤 CSV Export**: Export the entire bank database and transaction logs to a `.csv` file for offline reporting and data retention.

## 💻 Tech Stack
- **Language:** Java (JDK 8 or higher)
- **UI Framework:** Java Swing & AWT for a fully featured desktop Graphical User Interface.
- **Data Structures:** In-memory Java Collections (`HashMap`, `ArrayList`) for fast data retrieval.
- **File I/O:** `java.io` & `java.nio.file` for robust CSV file generation.

## 🚀 Getting Started

### Prerequisites
Ensure you have the Java SE Development Kit (JDK) installed. You can verify this by running:
```bash
java -version
```

### Installation & Execution
1. **Navigate** to the project directory containing the source code:
   ```bash
   cd "d:\Lav Kush\upskillcampus\"
   ```
2. **Compile** the Java source file:
   ```bash
   javac BankingInformationSystem.java
   ```
3. **Run** the application:
   ```bash
   java BankingInformationSystem
   ```

## 🔑 Usage & Credentials
Upon launching, the system will prompt you for administrative credentials. 
- **Username:** *Any username (e.g., admin)*
- **Password:** `admin123`

*(Note: Currently, the system uses in-memory storage. Data will reset upon application exit. Be sure to use the **Export CSV** feature to save your session data!)*

## 📂 Project Structure
The entirety of the application logic is encapsulated cleanly within `BankingInformationSystem.java`:

- **`BankingInformationSystem` Class**: The main UI Controller extending `JFrame`. It handles all view tabs, event listeners, file exporting, and user interactions.
- **`BankAccount` Model Class**: The data model defining individual account properties, core business logic (deposit/withdraw validations), and transaction logging.

## 🔮 Future Enhancements
To take this project to the next level, the following features are planned:
- [ ] **Database Integration:** Swap the `HashMap` for a persistent relational database (MySQL or SQLite) using JDBC.
- [ ] **Customer Portal:** Add multiple user roles to allow individual customers to log in and view only their specific accounts.
- [ ] **PDF Statements:** Implement PDF generation (using libraries like iText) for professional bank statements.

---
*Developed with ❤️ by Lav Kush.*
