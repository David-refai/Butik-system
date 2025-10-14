package org.example.Utils;


import org.example.Entity.Category;

import java.util.Scanner;

public class Utility {



    private static final Scanner scanner = new Scanner(System.in);




// ==================== Input helpers ====================

    /**
     * Prompt+read a single trimmed line.
     */
    public static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Read a non-empty string (loops until valid).
     */
    public static String readNonEmpty(String prompt) {
        while (true) {
            String s = readLine(prompt);
            if (!s.isEmpty()) return s;
            System.out.println("Value cannot be empty. Try again.");
        }
    }

    /**
     * Read an int in [1..max].
     */
    private static int readInt(String prompt, int max) {
        while (true) {
            String s = readLine(prompt);
            try {
                int v = Integer.parseInt(s);
                if (v < 1 || v > max) {
                    System.out.printf("Out of range (%d-%d).%n", 1, max);
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.err.println("Invalid number. Try again.");
            }
        }
    }

    /**
     * Shortcut: read a choice in 1..6.
     */
    public static int readMenu1to6() {
        return readInt("Your choice [1-6]: ", 6);
    }

    /**
     * Read a positive integer (>0).
     */
    public static int readPositiveInt(String prompt) {
        while (true) {
            String s = readLine(prompt);
            try {
                int v = Integer.parseInt(s);
                if (v > 0) return v;
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Enter a positive number.");
        }
    }

    /**
     * Read a non-negative price (double >= 0).
     */
    public static double readDouble() {
        while (true) {
            String s = readLine("Enter a price: ");
            try {
                double v = Double.parseDouble(s);
                if (v >= 0.0) return v;
                System.out.println("Value must be >= 0.0");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    /**
     * Pick a Category from the enum by index.
     */
    public static Category readCategory() {
        Category[] cats = Category.values();
        System.out.println("Choose a category:");
        for (int i = 0; i < cats.length; i++) {
            System.out.printf("%d) %s%n", i + 1, cats[i].name());
        }
        int choice = readInt("Category [1-" + cats.length + "]: ", cats.length);
        return cats[choice - 1];
    }



//    Help for print menu


    public static void printMenu(String... args) {
        System.out.printf("Menu:%n");
        for (String arg : args) {
            System.out.println(arg);
        }
        System.out.println("Please select an option:");
        System.out.print("> ");
        System.out.println("==================================".repeat(5));
    }





    public static void printBanner() {
        final String CYAN = "\u001B[36m";
        final String RESET = "\u001B[0m";

        System.out.println(CYAN + """
                ██████╗ ██╗   ██╗████████╗██╗ ██╗  ██╗
                ██╔══██╗██║   ██║╚══██╔══╝██║ ██║ ██╔╝
                ██████╔╝██║   ██║   ██║   ██║ █████╔╝
                ██╔══██╗██║   ██║   ██║   ██║ ██╔═██╗
                ██████╔╝╚██████╔╝   ██║   ██║ ██║  ██╗
                ╚═════╝  ╚═════╝    ╚═╝   ╚═╝ ╚═╝  ╚═╝
                         B U T I K   S Y S T E M
                            by David Alrefai
                """ + RESET);
    }


}

