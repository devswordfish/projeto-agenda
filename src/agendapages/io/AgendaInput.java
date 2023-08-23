package agendapages.io;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.Scanner;

import agendapages.datetime.AgendaDateTimeFormatException;
import agendapages.datetime.AgendaDateTime;

public abstract class AgendaInput {
    private static Scanner scanner = new Scanner(System.in);

    public static String inputString(String inputMessage) {
        System.out.print(inputMessage);
        String str = scanner.nextLine().strip();
        return str;
    }

    public static int inputOption(int minOption, int maxOption) {
        int option = 0;

        while (true) {
            System.out.print("Opção: ");

            try {
                option = Integer.parseInt(scanner.nextLine());

                // a opção digitada está fora do alcance de opções válidas (minOption até maxOption)
                if (option < minOption || option > maxOption) {
                    AgendaOutput.errorMessage("Digite um número entre " + minOption + " e " + maxOption);
                } else {
                    break;
                }
            } catch (Exception e) {
                AgendaOutput.errorMessage("Digite um número");
            }
        }

        return option;
    }

    public static LocalDate inputDate(String inputMessage) {
        LocalDate date = null;

        while (date == null) {
            System.out.print(inputMessage);
            String dateString = scanner.nextLine().strip();

            try {
                date = AgendaDateTime.parseDate(dateString);
            } catch (AgendaDateTimeFormatException e) {
                AgendaOutput.errorMessage("Data inválida");
            }
        }

        return date;
    }

    public static LocalTime inputTime(String inputMessage){
        LocalTime time = null;

        while (time == null) {
            System.out.print(inputMessage);
            String timeString = scanner.nextLine().strip();

            try {
                time = AgendaDateTime.parseTime(timeString);
            } catch (AgendaDateTimeFormatException e) {
                AgendaOutput.errorMessage("Horário inválido");
            }
        }

        return time;
    }

    public static void holdScreen() {
        scanner.nextLine();
    }
}
