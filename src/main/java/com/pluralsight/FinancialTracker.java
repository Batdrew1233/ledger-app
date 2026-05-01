package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class FinancialTracker {

    /* ------------------------------------------------------------------
       Shared data and formatters
       ------------------------------------------------------------------ */
    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    /* ------------------------------------------------------------------
       Main menu
       ------------------------------------------------------------------ */
    public static void main(String[] args) {
        loadTransactions(FILE_NAME);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\nWelcome to TransactionApp");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");
            System.out.print("Your Choice: ");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                case "P" -> addPayment(scanner);
                case "L" -> ledgerMenu(scanner);
                case "X" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
        scanner.close();
    }


    /*
    Load transactions from CSV file into transaction list.
    Create the file if it doesn't exist
    Reads each line from file and splits it using "|"
    Converts date and time strings into LocalDate and LocalTime
    Creates transaction objects and stores them in the array list
     */
    public static void loadTransactions(String fileName) {
        try {
            //Checks if file exists
            File file = new File(fileName);
            if (!file.exists()){
                boolean created = file.createNewFile();

                if(created) {
                    System.out.println("File created");
                }
            }

            //Read fileName
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String line;
            //Reads through the file and categorizes it splitting each section by "|"
            while ((line = bufferedReader.readLine()) != null){
                String[] parts = line.split("\\|");

                LocalDate date = LocalDate.parse(parts[0].trim(), DATE_FMT);
                LocalTime time = LocalTime.parse(parts[1].trim(), TIME_FMT);
                String description = parts[2].trim();
                String vendor = parts[3].trim();
                double amount = Double.parseDouble(parts[4].trim());

                transactions.add(new Transaction(date, time, description, vendor, amount));
            }
            bufferedReader.close();

        }catch (Exception ex){
            System.out.println("Something went wrong");
        }
    }


    /*
    Add a deposit transaction
    prompts user for date/time, description, vendor, and amount
    makes sure the amount is positive
    stores the transaction in the transaction list
    appends the transaction to the CSV file
     */
    private static void addDeposit(Scanner scanner) {
        //Get the date and time and using the date time formatter
        LocalDateTime dateTime = null;
        while (dateTime == null) {
            System.out.print("\nDate & Time (yyyy-MM-dd HH:mm:ss): ");
            String userDateTime = scanner.nextLine();

            try{
                dateTime = LocalDateTime.parse(userDateTime, DATETIME_FMT);
            }catch (Exception ex){
                System.out.println("Invalid date/time format.");
            }
        }

        LocalDate userDate = dateTime.toLocalDate();
        LocalTime userTime = dateTime.toLocalTime();

        //Get the Description for transaction
        String userDescription = "";
        while(userDescription.isEmpty()) {
            System.out.print("\nDescription: ");
            userDescription = scanner.nextLine().trim();
            if (userDescription.isEmpty()){
                System.out.println("Description cannot be blank.");
            }
        }
        //Get the Vendor for transaction
        String userVendor = "";
        while(userVendor.isEmpty()) {
            System.out.print("\nVendor: ");
            userVendor = scanner.nextLine().trim();
            if (userVendor.isEmpty()){
                System.out.println("Vendor cannot be blank.");
            }
        }

        //Get the positive amount for transaction
        Double userAmount = null;
        while(userAmount == null || userAmount <= 0) {
            System.out.print("\nAmount (Positive): ");
            userAmount = parseDouble(scanner.nextLine());
            if (userAmount ==  null || userAmount <= 0){
                System.out.println("Enter a valid positive amount,");
            }
        }

        //Gather information
        Transaction deposit = new Transaction(userDate, userTime, userDescription, userVendor, userAmount);
        transactions.add(deposit);

        //Write the information gathered into the csv file without deleting information
        try{
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILE_NAME,true));

            bufferedWriter.write(userDate.format(DATE_FMT) + "|" + userTime.format(TIME_FMT) + "|" + userDescription + "|" + userVendor + "|" + String.format("%.2f", userAmount));
            bufferedWriter.newLine();

            bufferedWriter.close();

            System.out.println("\nDeposit recorded.");



        }catch(Exception ex){
            System.out.println("\nSomething went wrong");
        }

    }

    /*
    Add a payment transaction
    prompts user for date/time, description, vendor, and amount
    makes sure the amount is positive
    converts to amount to negative
    stores transaction in the arraylist and CSV file
     */
    private static void addPayment(Scanner scanner) {
        //Get the date and time and using the date time formatter
        LocalDateTime dateTime = null;
        while (dateTime == null) {
            System.out.print("\nDate & Time (yyyy-MM-dd HH:mm:ss): ");
            String userDateTime = scanner.nextLine();

            try{
                dateTime = LocalDateTime.parse(userDateTime, DATETIME_FMT);
            }catch (Exception ex){
                System.out.println("Invalid date/time format.");
            }
        }

        LocalDate userDate = dateTime.toLocalDate();
        LocalTime userTime = dateTime.toLocalTime();

        //Get the Description for transaction
        String userDescription = "";
        while(userDescription.isEmpty()) {
            System.out.print("\nDescription: ");
            userDescription = scanner.nextLine().trim();
            if (userDescription.isEmpty()){
                System.out.println("Description cannot be blank.");
            }
        }
        //Get the Vendor for transaction
        String userVendor = "";
        while(userVendor.isEmpty()) {
            System.out.print("\nVendor: ");
            userVendor = scanner.nextLine().trim();
            if (userVendor.isEmpty()){
                System.out.println("Vendor cannot be blank.");
            }
        }

        //Get the positive amount for transaction
        Double userAmount = null;
        while(userAmount == null || userAmount <= 0) {
            System.out.print("\nAmount (Positive): ");
            userAmount = parseDouble(scanner.nextLine());
            if (userAmount ==  null || userAmount <= 0){
                System.out.println("\nEnter a valid positive amount,");
            }
        }
        //Converts user amount to negative
        userAmount *= 1;

        //Update these values into transactions
        Transaction deposit = new Transaction(userDate, userTime, userDescription, userVendor, userAmount);
        transactions.add(deposit);

        //Write the information gathered into the csv file without deleting information
        try{
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILE_NAME,true));

            bufferedWriter.write(userDate.format(DATE_FMT) + "|" + userTime.format(TIME_FMT) + "|" + userDescription + "|" + userVendor + "|" + String.format("%.2f", userAmount));
            bufferedWriter.newLine();

            bufferedWriter.close();

            System.out.println("\nDeposit recorded.");



        }catch(Exception ex){
            System.out.println("\nSomething went wrong");
        }
    }

    /*
    Display the ledger menu
    formats transactions from newest to oldest
    allows user to view transactions and reports
    continues to run until user returns
     */
    private static void ledgerMenu(Scanner scanner) {
        //Sorts Dates and Times then reverses the order to newest to oldest.
        transactions.sort(Comparator.comparing(Transaction::getDate).thenComparing(Transaction::getTime).reversed());
        boolean running = true;
        while (running) {
            System.out.println("\nLedger");
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");
            System.out.print("Your Choice: ");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A" -> displayLedger();
                case "D" -> displayDeposits();
                case "P" -> displayPayments();
                case "R" -> reportsMenu(scanner);
                case "H" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /*
    Displays all transactions
    prints transactions in formatted columns from newest to oldest
     */
    private static void displayLedger() {
        //Format for the display
        System.out.printf("\n%-12s %-10s %-30s %-25s %10s%n", "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println("================================================================================================");

        //Prints out every line in the file
        for(Transaction transaction :transactions){
            System.out.printf("%-12s %-10s %-30s %-25s %10.2f%n",transaction.getDate().format(DATE_FMT), transaction.getTime().format(TIME_FMT), transaction.getDescription(), transaction.getVendor(), transaction.getAmount() );
        }
    }

    /*
    Display only deposit transactions.
    displays transaction where the amount is greater than 0
     */
    private static void displayDeposits() {
        //Format for the display
        System.out.printf("\n%-12s %-10s %-30s %-25s %10s%n", "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println("================================================================================================");

        // Searches through the file and only prints out positive numbers
        for(Transaction transaction :transactions) {
            if (transaction.getAmount() > 0) {
                System.out.printf("%-12s %-10s %-30s %-25s %10.2f%n", transaction.getDate().format(DATE_FMT), transaction.getTime().format(TIME_FMT), transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
            }
        }
    }

    /*
    Display only payment transactions.
    displays transactions where the amount is less than 0
     */
    private static void displayPayments() {
        //Format for the display
        System.out.printf("\n%-12s %-10s %-30s %-25s %10s%n", "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println("================================================================================================");

        // Searches through the file and only prints out positive numbers
        for(Transaction transaction :transactions) {
            if (transaction.getAmount() < 0) {
                System.out.printf("%-12s %-10s %-30s %-25s %10.2f%n", transaction.getDate().format(DATE_FMT), transaction.getTime().format(TIME_FMT), transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
            }
        }
    }

    /*
    Display the report menu
    allows user to run preset and custom reports
     */
    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("\nReports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");
            System.out.print("Your Choice: ");


            String input = scanner.nextLine().trim();

            switch (input) {
                //Month to date
                case "1" -> {
                //Local date "end" equals today's date
                    LocalDate end = LocalDate.now();
                //This makes the day out of the month to the 1st
                    LocalDate start = end.withDayOfMonth(1);
                //Call Method with "start" and "end" dates
                    filterTransactionsByDate(start,end);
                }

                //Previous Month
                case "2" -> {
                    //This finds today's date and stores the value
                    LocalDate todayDate = LocalDate.now();
                    //This finds the previous Month and stores that value
                    LocalDate previousMonth = todayDate.minusMonths(1);
                    //Makes the previous Months day to the first
                    LocalDate start = previousMonth.withDayOfMonth(1);
                    //This will store in "end" the last day of the previous month
                    LocalDate end = previousMonth.withDayOfMonth(previousMonth.lengthOfMonth());
                    //Call Method with "start" and "end" dates
                    filterTransactionsByDate(start,end);
                }

                //Year to Date
                case "3" -> {
                    //Local date end equals today's date
                    LocalDate end = LocalDate.now();
                    //This makes the Month and day to the 1st
                    LocalDate start = end.withDayOfYear(1);
                    //Call Method with "start" and "end" dates
                    filterTransactionsByDate(start,end);
                }
                //Previous Year
                case "4" -> {
                    //Uses today's date and stores the previous year
                    LocalDate previousYear = LocalDate.now().minusYears(1);
                    //Stores the first Month and Day of the previous year
                    LocalDate start = previousYear.withDayOfYear(1);
                    //Stores the last Month and Year from the previous year.
                    LocalDate end = previousYear.withDayOfYear(previousYear.lengthOfYear());
                    //Call Method with "start" and "end" dates
                    filterTransactionsByDate(start,end);
                }

                //Search By vendor
                case "5" -> {
                    System.out.print("\nVendor Name: ");
                    String vendor = scanner.nextLine();
                    //Uses the vendor name to search
                    filterTransactionsByVendor(vendor);
                }
                case "6" -> customSearch(scanner);
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /*
    Filter transactions by date range.
    displays transactions between start and end dates
    includes the boundary dates in the search
     */
    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {
        boolean found = false;

        //Format for the display
        System.out.printf("\n%-12s %-10s %-30s %-25s %10s%n", "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println("================================================================================================");

        // Searches through the file
        for(Transaction transaction :transactions) {

            //Gets the date after start and before end including the filtering dates.
            if (!transaction.getDate().isBefore(start) && !transaction.getDate().isAfter(end)) {

                found = true;

                System.out.printf("%-12s %-10s %-30s %-25s %10.2f%n", transaction.getDate().format(DATE_FMT), transaction.getTime().format(TIME_FMT), transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
            }
        }
            //If is no data between the dates this will print
        if (!found){
            System.out.println("No transactions found for these dates");
            }


    }

    /*
    Filter transactions by vendor name.
    displays transactions matching the entered vendor
     */
    private static void filterTransactionsByVendor(String vendor) {
        boolean found = false;

        //Format for the display
        System.out.printf("\n%-12s %-10s %-30s %-25s %10s%n", "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println("================================================================================================");

        // Searches through the file
        for(Transaction transaction :transactions) {

            //Finds if the vendor in transactions is equal to vendor asked in scanner and prints if it's a match
            if (transaction.getVendor().equalsIgnoreCase(vendor)) {

                found = true;

                System.out.printf("%-12s %-10s %-30s %-25s %10.2f%n", transaction.getDate().format(DATE_FMT), transaction.getTime().format(TIME_FMT), transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
            }
        }
            //If no vendor is found it will print
        if(!found){
            System.out.println("No transactions found for this vendor");
            }

    }

    /*
    Perform a custom search
    allows filtering by start date, end date, description, vendor, and exact amount
    if the user leaves it blank then no filter is applied
     */
    private static void customSearch(Scanner scanner) {
        //Asks the user for a start date to narrow search
        LocalDate userStart = null;
        while(true){
            System.out.print("\nStart date (yyyy-MM-dd, blank = none): ");
            String searchStart = scanner.nextLine().trim();

            if (searchStart.isEmpty()){
                break;
            }
            //use the parseDate Method to return null if incorrect
            userStart = parseDate(searchStart);

            if(userStart != null){
                break;
            }
            System.out.println("Invalid date format.");
            }

        //Asks the user for end date to narrow search
        LocalDate userEnd = null;
        while(true){
            System.out.print("\nEnd date (yyyy-MM-dd, blank = none): ");
            String searchEnd = scanner.nextLine().trim();

            if (searchEnd.isEmpty()){
                break;
            }
            //use the parseDate Method to return null if incorrect
            userEnd = parseDate(searchEnd);

            if(userEnd != null){
                break;
            }
            System.out.println("Invalid date format.");
        }

        //Asks the user for description
        System.out.print("\nDescription (blank = any): ");
        String searchDescription = scanner.nextLine().trim();

        //Asks the user for vendor
        System.out.print("\nVendor      (blank = any): ");
        String searchVendor = scanner.nextLine().trim();

        //Asks the user for amount
        Double amountInput = null;
        while(true) {
            System.out.print("\nAmount      (blank = any): ");
            String searchAmount = scanner.nextLine().trim();

            if (searchAmount.isEmpty()){
                break;
            }
            amountInput = parseDouble(searchAmount);

            if(amountInput != null){
                break;
            }
            System.out.println("Invalid amount");
        }

        //If this is stays false it will print cant find search
        boolean found = false;

        //Format for the display
        System.out.printf("\n%-12s %-10s %-30s %-25s %10s%n", "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println("================================================================================================");

        //search through transactions file
        for (Transaction transaction : transactions){

            //This will sort between if the filters can be found or not
            boolean matches = true;

            //Start Date
            //This will check if the start date isn't null and the transaction line date is before the user filtering date
            if (userStart != null && transaction.getDate().isBefore(userStart)){
                // If the transaction date is before the user start date then we don't need it
                matches = false;
            }

            //End Date
            //This will check if the end date isn't null and the transaction line date is after the user filtering date
            if (userEnd != null && transaction.getDate().isAfter(userEnd)){
                // If the transaction date is after the user end date then we don't need it
                matches = false;
            }

            //Description
            //This will check if the user description is empty and if the file description and user description match.
            if (!searchDescription.isEmpty() && !transaction.getDescription().equalsIgnoreCase(searchDescription)){
                // If the transaction description doesn't match then we don't need it
                matches = false;
            }

            //Vendor
            //This will check if the user vendor is empty and if the file vendor matches the user vendor.
            if (!searchVendor.isEmpty() && !transaction.getVendor().equalsIgnoreCase(searchVendor)){
                //If the vendors don't match we don't need it
                matches = false;
            }

            //Amount
            //This will check if the user typed an amount and if the amount from user and file match.
            if (amountInput != null && transaction.getAmount() != amountInput){
                //If the amounts don't match we don't need it
                matches = false;
            }

            //If after the search if the value matches then we can print all matches
            if(matches){

                //It won't print about not no transactions found
                found = true;

                //This will print all the filtered results
                System.out.printf("%-12s %-10s %-30s %-25s %10.2f%n", transaction.getDate().format(DATE_FMT), transaction.getTime().format(TIME_FMT), transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
            }
        }
        if(!found){
            System.out.println("No transaction found.");
        }

    }

    /*
    Converts a string into LocalDate
    uses DATE_FMT to format the date
    returns null if input cant work
     */
    private static LocalDate parseDate(String s) {
        try{
            //converts string to LocalDate using the date formatter and returns it
            return LocalDate.parse(s,DATE_FMT);
        //If a problem occurs then it returns null
        } catch (Exception ex) {
            return null;
        }
    }

    /*
    Converts a string into a double
    returns null if the input doesn't work
     */
    private static Double parseDouble(String s) {
        try{
            //converts string to Double and returns it
            return Double.parseDouble(s);
        //If a problem occurs then it returns null
        }catch (Exception ex) {
            return null;
        }
    }
}
