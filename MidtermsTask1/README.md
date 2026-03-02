Low Performing Products Detector

This project analyzes a sales dataset and identifies low-performing products.
It computes total sales per product, calculates the dataset average, and displays products with sales below the average.

The project contains two implementations:

Java desktop application

JavaScript terminal application

Dataset

The programs use the file:

vgchartz-2024.csv

Place this file in the project folder or provide its full path when running the JavaScript version.

Java Version (Desktop Application)

How to run:

Open the project in VS Code or any Java IDE.

Compile and run the file:

LowPerformingProductsApp.java

The application will automatically load and analyze the dataset.

The output window will show:

Number of rows read

Number of unique products

Average sales per product

List of products below the average

JavaScript Version (Terminal)

Requirement:
Node.js must be installed.

To check installation, run:
node -v

If a version number appears, Node.js is installed.

How to run:

Open the folder containing detect.js in VS Code.

Right-click detect.js and open the integrated terminal.

Run:

node detect.js vgchartz-2024.csv

If the CSV file is located elsewhere, run:

node detect.js "full\path\to\vgchartz-2024.csv"

Program Behavior

The program:

Reads the dataset

Groups records by product title

Computes total sales per product

Calculates the dataset average

Displays all products with sales below the average

Bill Gedrick Griar
Programming 2 – Midterm Task