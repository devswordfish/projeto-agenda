package agenda;

import java.util.Scanner;

public class MenuOptions {
    private Option[] options;

    public MenuOptions(Option ...options) {
        this.options = options;
    }

    public void showOptions() {
        for (Option option : this.options) {
            System.out.println(option.getOption());
        }
    }

    public int inputOption(Scanner scanner) {
        int option = 0;

        while (true) {
            System.out.print("Opção: ");

            try {
                option = Integer.parseInt(scanner.nextLine());

                if (option < 1 || option > this.options.length) {
                    System.out.println("[!] Digite um número entre 1 e " + this.options.length);
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("[!] Digite um número");
            }
        }

        return option;
    }

    public void chooseOptionAction(int option) {
        this.options[option - 1].useAction();
    }
}
