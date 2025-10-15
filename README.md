# 🛍️ Butik System

A clean, console-based Java application for managing **Customers**, **Products**, and **Orders**.  
Built using pure Java 21 / 17, Maven, and SLF4J (Logback), following layered architecture and SOLID principles.

---

## 📖 Overview

**Butik System** simulates a small-scale retail backend where users can:

- ➕ Add / Edit / Delete customers and products
- 🧾 Create and update orders interactively
- 🧮 Automatically calculate order totals
- 🧱 Persist data in-memory with thread-safe repositories
- 🧭 Browse data through a simple text-based menu

Designed for clarity and testability — ideal for educational or training environments.

---

## 🧩 Architecture

| Layer | Package | Responsibility |
|:------|:---------|:---------------|
| **Entity** | `org.example.Entity` | Domain models (Customer, Product, Order, Category) |
| **Repository** | `org.example.Repository` | Generic CRUD contracts |
| **ImplRepo** | `org.example.ImplRepo` | In-memory implementations using `ConcurrentHashMap` |
| **Service** | `org.example.Service` | Business logic and validation |
| **Utils** | `org.example.Utils` | Console flows, menu printing, seed data |
| **Error** | `org.example.Error` | Simple safe-execution wrappers |
| **App / Main** | `org.example.App`, `org.example.Main` | CLI entry and menu loop |

---

## ⚙️ Tech Stack

- **Language:** Java 17 / 21
- **Build Tool:** Maven 3.9+
- **Logging:** SLF4J + Logback
- **Testing:** (placeholder for JUnit if added)
- **CI/CD:** GitHub Actions matrix build (JDK 17 & 21)

---

## 🚀 Getting Started

### Requirements
- Java 17 or higher
- Apache Maven 3.9+

### Run Locally
```bash
git clone https://github.com/David-refai/Butik-system.git
cd Butik-system
mvn clean compile exec:java -Dexec.mainClass="org.example.Main"

Build JAR
mvn clean package
java -jar target/Kompetenskontroll-1-1.0-SNAPSHOT.jar

Generate Javadoc
mvn javadoc:javadoc

🧱 Project Structure
src/
 ├─ main/java/org/example/
 │   ├─ Entity/         → Customer, Product, Order, Category
 │   ├─ Repository/     → CrudRepo, Identifiable
 │   ├─ ImplRepo/       → InMemoryCurd, InMemoryOrderImp
 │   ├─ Service/        → ServiceCrud, OrderService
 │   ├─ Utils/          → Identify, Menu, Data
 │   ├─ Error/          → Safe, ErrorHandling
 │   ├─ App.java
 │   └─ Main.java
 └─ resources/
     └─ logback.xml

🧮 Example Usage
Choose an entity:
1) Customer
2) Product
3) Order
4) Exit
Your choice: 3

Choose operation:
1. Add Order
2. View Orders
3. Update Order
4. Delete Order
5. Find Order by ID
6. Back

🧰 CI Configuration (GitHub Actions)
strategy:
  matrix:
    java: [ '17', '21' ]
steps:
  - uses: actions/checkout@v4
  - uses: actions/setup-java@v4
    with:
      distribution: temurin
      java-version: ${{ matrix.java }}
  - run: mvn -B clean verify --no-transfer-progress -Dmaven.compiler.release=${{ matrix.java }}


🧾 Project Changelog
     |  Version  |    Date    | Type               | Description                                                             |
| :-------: | :--------: | :----------------- | :---------------------------------------------------------------------- |
| **1.0.0** | 2025-10-15 | 🏁 Initial Release | Project bootstrapped with full CRUD for Customer, Product, and Order.   |
| **1.0.1** | 2025-10-16 | 🧩 Refactor        | Added `ServiceCrud` and generic CRUD abstraction.                       |
| **1.0.2** | 2025-10-17 | 🧠 Improvement     | Introduced `InMemoryOrderImp` with secondary index (customer → orders). |
| **1.0.3** | 2025-10-18 | ⚙️ CI/CD           | Added GitHub Actions matrix build for JDK 17 & 21.                      |
| **1.0.4** | 2025-10-19 | 📝 Docs            | Added full `README.md` and generated Javadoc integration.               |


📚 License

This project is distributed under the MIT License.
Feel free to modify and use for educational or commercial purposes.

✍️ Author

David Alrefai
Malmö, Sweden
📧 Reach out via GitHub for collaboration or feedback.