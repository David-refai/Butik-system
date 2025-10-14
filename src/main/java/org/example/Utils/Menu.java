package org.example.Utils;


import static java.lang.ProcessBuilder.Redirect.Type.READ;
import static java.lang.ProcessBuilder.Redirect.Type.WRITE;

public class Menu {


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

