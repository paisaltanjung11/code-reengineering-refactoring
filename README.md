# OnlineBookStore - Refactored Java Web Project

## Project Description

This repository contains a refactored version of the **OnlineBookStore Java Web Application**, originally developed for educational purposes. The application allows users to register, browse available books, manage shopping carts, and purchase books online, while administrators can add, remove, and manage book inventories.

The original source project can be found at:  
https://github.com/bharah08/onlinebookstore-javaproject

This refactored version was created as part of a software engineering study case focused on **Code Smell Identification** and **Refactoring Techniques**, by applying several software design principles and design patterns to improve code quality, maintainability, readability, and scalability.

## Refactoring Objectives

The refactoring process was conducted to address several technical issues observed in the original codebase:

- Eliminating duplicate code across servlets.
- Breaking down long methods into smaller, focused methods.
- Resolving feature envy within utility classes.
- Encapsulating session management to improve separation of concerns.
- Reducing responsibilities of overloaded classes (applying Single Responsibility Principle).
- Removing redundant or unnecessary comments to improve code clarity.

The refactoring process followed key principles from:

- *Refactoring: Improving the Design of Existing Code* by Martin Fowler
- *Refactoring for Software Design Smells: Managing Technical Debt* by Girish Suryanarayana et al.
- *Design Patterns: Elements of Reusable Object-Oriented Software* by Gamma et al.

## Key Improvements

- Introduced `BaseServlet` implementing Template Method Pattern to centralize common servlet behavior.
- Applied decomposition to split large `service()` methods into smaller maintainable units.
- Encapsulated session attribute access with reusable helper methods.
- Simplified utility methods (`StoreUtil`) into more focused operations (`addToCart()`, `removeFromCart()`).
- Applied Single Responsibility Principle (SRP) throughout business logic layers.
- Cleaned up excessive in-line comments for better readability.

## Notes

This refactoring was conducted for academic and learning purposes.

## Author

This repository was refactored as part of a Code Reengineering Project.
