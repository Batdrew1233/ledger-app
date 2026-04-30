package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

/*
 * Capstone skeleton – personal finance tracker.
 * ------------------------------------------------
 * File format  (pipe-delimited)
 *     yyyy-MM-dd|HH:mm:ss|description|vendor|amount
 * A deposit has a positive amount; a payment is stored
 * as a negative amount.
 */
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
            System.out.println("Welcome to TransactionApp");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

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

    /* ------------------------------------------------------------------
       File I/O
       ------------------------------------------------------------------ */

    /**
     * Load transactions from FILE_NAME.
     * • If the file doesn’t exist, create an empty one so that future writes succeed.
     * • Each line looks like: date|time|description|vendor|amount
     */
    public static void loadTransactions(String fileName) {
        // TODO: create file if it does not exist, then read each line,
        //       parse the five fields, build a Transaction object,
        //       and add it to the transactions list.
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

    /* ------------------------------------------------------------------
       Add new transactions
       ------------------------------------------------------------------ */

    /**
     * Prompt for ONE date+time string in the format
     * "yyyy-MM-dd HH:mm:ss", plus description, vendor, amount.
     * Validate that the amount entered is positive.
     * Store the amount as-is (positive) and append to the file.
     */
    private static void addDeposit(Scanner scanner) {
        // TODO
        //Get the date and time and using the date time formatter
        LocalDateTime dateTime = null;
        while (dateTime == null) {
            System.out.println("Date & Time (yyyy-MM-dd HH:mm:ss): ");
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
            System.out.println("Description: ");
            userDescription = scanner.nextLine().trim();
            if (userDescription.isEmpty()){
                System.out.println("Description cannot be blank.");
            }
        }
        //Get the Vendor for transaction
        String userVendor = "";
        while(userVendor.isEmpty()) {
            System.out.println("Vendor: ");
            userVendor = scanner.nextLine().trim();
            if (userVendor.isEmpty()){
                System.out.println("Vendor cannot be blank.");
            }
        }

        //Get the positive amount for transaction
        Double userAmount = null;
        while(userAmount == null || userAmount <= 0) {
            System.out.println("Amount (Positive): ");
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

            System.out.println("Deposit recorded.");



        }catch(Exception ex){
            System.out.println("Something went wrong");
        }

    }

    /**
     * Same prompts as addDeposit.
     * Amount must be entered as a positive number,
     * then converted to a negative amount before storing.
     */
    private static void addPayment(Scanner scanner) {
        // TODO
        //Get the date and time and using the date time formatter
        LocalDateTime dateTime = null;
        while (dateTime == null) {
            System.out.println("Date & Time (yyyy-MM-dd HH:mm:ss): ");
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
            System.out.println("Description: ");
            userDescription = scanner.nextLine().trim();
            if (userDescription.isEmpty()){
                System.out.println("Description cannot be blank.");
            }
        }
        //Get the Vendor for transaction
        String userVendor = "";
        while(userVendor.isEmpty()) {
            System.out.println("Vendor: ");
            userVendor = scanner.nextLine().trim();
            if (userVendor.isEmpty()){
                System.out.println("Vendor cannot be blank.");
            }
        }

        //Get the positive amount for transaction
        Double userAmount = null;
        while(userAmount == null || userAmount <= 0) {
            System.out.println("Amount (Positive): ");
            userAmount = parseDouble(scanner.nextLine());
            if (userAmount ==  null || userAmount <= 0){
                System.out.println("Enter a valid positive amount,");
            }
        }

        //Update these values into transactions
        Transaction deposit = new Transaction(userDate, userTime, userDescription, userVendor, -userAmount);
        transactions.add(deposit);

        //Write the information gathered into the csv file without deleting information
        try{
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILE_NAME,true));

            bufferedWriter.write(userDate.format(DATE_FMT) + "|" + userTime.format(TIME_FMT) + "|" + userDescription + "|" + userVendor + "|" + String.format("%.2f", userAmount));
            bufferedWriter.newLine();

            bufferedWriter.close();

            System.out.println("Deposit recorded.");



        }catch(Exception ex){
            System.out.println("Something went wrong");
        }
    }

    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Ledger");
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

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

    /* ------------------------------------------------------------------
       Display helpers: show data in neat columns
       ------------------------------------------------------------------ */
    private static void displayLedger() { /* TODO – print all transactions in column format */
        //Sorts Dates and Times then reverses the order to newest to oldest.
        transactions.sort(Comparator.comparing(Transaction::getDate).thenComparing(Transaction::getTime).reversed());

        //Format for the display
        System.out.printf("%-12s %-10s %-30s %-25s %10s%n", "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println("================================================================================================");

        //Prints out every line in the file
        for(Transaction transaction :transactions){
            System.out.printf("%-12s %-10s %-30s %-25s %10.2f%n",transaction.getDate().format(DATE_FMT), transaction.getTime().format(TIME_FMT), transaction.getDescription(), transaction.getVendor(), transaction.getAmount() );
        }
    }

    private static void displayDeposits() { /* TODO – only amount > 0               */
        //Sorts Dates and Times then reverses the order to newest to oldest.
        transactions.sort(Comparator.comparing(Transaction::getDate).thenComparing(Transaction::getTime).reversed());

        //Format for the display
        System.out.printf("%-12s %-10s %-30s %-25s %10s%n", "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println("================================================================================================");

        // Searches through the file and only prints out positive numbers
        for(Transaction transaction :transactions) {
            if (transaction.getAmount() > 0) {
                System.out.printf("%-12s %-10s %-30s %-25s %10.2f%n", transaction.getDate().format(DATE_FMT), transaction.getTime().format(TIME_FMT), transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
            }
        }
    }

    private static void displayPayments() { /* TODO – only amount < 0               */
        //Sorts Dates and Times then reverses the order to newest to oldest.
        transactions.sort(Comparator.comparing(Transaction::getDate).thenComparing(Transaction::getTime).reversed());

        //Format for the display
        System.out.printf("%-12s %-10s %-30s %-25s %10s%n", "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println("================================================================================================");

        // Searches through the file and only prints out positive numbers
        for(Transaction transaction :transactions) {
            if (transaction.getAmount() < 0) {
                System.out.printf("%-12s %-10s %-30s %-25s %10.2f%n", transaction.getDate().format(DATE_FMT), transaction.getTime().format(TIME_FMT), transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
            }
        }
    }

    /* ------------------------------------------------------------------
       Reports menu
       ------------------------------------------------------------------ */
    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");

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
                    System.out.print("Vendor Name: ");
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

    /* ------------------------------------------------------------------
       Reporting helpers
       ------------------------------------------------------------------ */
    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {
        // TODO – iterate transactions, print those within the range

        //Sorts Dates and Times then reverses the order to newest to oldest.
        transactions.sort(Comparator.comparing(Transaction::getDate).thenComparing(Transaction::getTime).reversed());

        boolean found = false;

        //Format for the display
        System.out.printf("%-12s %-10s %-30s %-25s %10s%n", "Date", "Time", "Description", "Vendor", "Amount");
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

    private static void filterTransactionsByVendor(String vendor) {
        // TODO – iterate transactions, print those with matching vendor
        //Sorts Dates and Times then reverses the order to newest to oldest.
        transactions.sort(Comparator.comparing(Transaction::getDate).thenComparing(Transaction::getTime).reversed());

        boolean found = false;

        //Format for the display
        System.out.printf("%-12s %-10s %-30s %-25s %10s%n", "Date", "Time", "Description", "Vendor", "Amount");
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

    private static void customSearch(Scanner scanner) {
        // TODO – prompt for any combination of date range, description,
        //        vendor, and exact amount, then display matches

        //Asks the user for a start date to narrow search
        System.out.println("Start date (yyyy-MM-dd, blank = none): ");
        String searchStart = scanner.nextLine().trim();
        //Checks if "searchStart" is empty. If it is the value is null, if not it will use parseDate Method to convert from string to date
        LocalDate startDate = searchStart.isEmpty() ? null : parseDate(searchStart);

        //Asks the user for end date to narrow search
        System.out.println("End date (yyyy-MM-dd, blank = none): ");
        String searchEnd = scanner.nextLine().trim();
        //Checks if "searchEnd" is empty. If it is the value is null, if not it will use parseDate Method to convert from string to date
        LocalDate endDate = searchEnd.isEmpty() ? null : parseDate(searchEnd);

        //Asks the user for description
        System.out.println("Description (blank = any): ");
        String searchDescription = scanner.nextLine().trim();

        //Asks the user for vendor
        System.out.println("Vendor      (blank = any): ");
        String searchVendor = scanner.nextLine().trim();

        //Asks the user for amount
        System.out.println("Amount      (blank = any): ");
        String searchAmount = scanner.nextLine().trim();
        //Checks if "searchAmount" is empty. If it is the value is null, if not it will use parseDouble Method to convert from string to double
        Double amountInput = searchAmount.isEmpty() ? null : parseDouble(searchAmount);

        //If this is stays false it will print cant find search
        boolean found = false;

        //search through transactions file
        for (Transaction transaction : transactions){

            //This will sort between if the filters can be found or not
            boolean matches = true;

            //Start Date
            //This will check if the start date isn't null and the transaction line date is before the user filtering date
            if (startDate != null && transaction.getDate().isBefore(startDate)){
                // If the transaction date is before the user start date then we don't need it
                matches = false;
            }

            //End Date
            //This will check if the end date isn't null and the transaction line date is after the user filtering date
            if (endDate != null && transaction.getDate().isAfter(endDate)){
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

    /* ------------------------------------------------------------------
       Utility parsers (you can reuse in many places)
       ------------------------------------------------------------------ */
    private static LocalDate parseDate(String s) {
        /* TODO – return LocalDate or null */
        try{
            //converts string to LocalDate using the date formatter and returns it
            return LocalDate.parse(s,DATE_FMT);
        //If a problem occurs then it returns null
        } catch (Exception ex) {
            return null;
        }
    }

    private static Double parseDouble(String s) {
        /* TODO – return Double   or null */
        try{
            //converts string to Double and returns it
            return Double.parseDouble(s);
        //If a problem occurs then it returns null
        }catch (Exception ex) {
            return null;
        }
    }
}
