package agenda;

import java.util.Scanner;

import java.util.Map;
import java.util.HashMap;

public class Agenda {
    private static Scanner scan = new Scanner(System.in);
    private Map<String, MenuOptions> menus;

    private MenuOptions currentMenu;
    private boolean continueInAgenda;

    public Agenda() {
        this.setUpMenus();
        this.setCurrentMenu("main");
        this.continueInAgenda = true;
    }

    private void setUpMenus() {
        // main menu
        MenuOptions mMain = new MenuOptions(
            new Option("[1] - Criar", () -> this.setCurrentMenu("create")),
            new Option("[2] - Alterar", () -> {}),
            new Option("[3] - Cancelar", () -> {}),
            new Option("[4] - Visualizar", () -> {}),
            new Option("[5] - Sair", () -> this.finish())
        );

        // create menu
        MenuOptions mCreate = new MenuOptions(
            new Option("[1] - Criar evento", () -> {}),
            new Option("[2] - Criar lembrete", () -> {}),
            new Option("[3] - Criar tarefa", () -> {}),
            new Option("[4] - Voltar", () -> this.setCurrentMenu("main"))
        );

        this.menus = new HashMap<>();

        // store the menus
        this.menus.put("main", mMain);
        this.menus.put("create", mCreate);
    }

    public void start() {
        int option = 0;

        this.showTitle();

        // show options in the terminal for the user to choose one and, then, the action related to that option is called
        while (continueInAgenda) {
            this.currentMenu.showOptions();
            
            option = this.chooseOption();

            System.out.println("===================================================");

            this.currentMenu.chooseOptionAction(option);
        }
    }

    private void finish() {
        continueInAgenda = false;
    }

    private void showTitle() {
        System.out.println("===================================================");
        System.out.println("   +-----+");
        System.out.println("   |  :  | +-----  +----- |\\    | +--.   +-----+");
        System.out.println("   |  :  | |       |      | \\   | |   \\  |  .  |");
        System.out.println("   +-----+ |  ---+ +---   |  \\  | | :  | |_____|");
        System.out.println("   |     | |     | |      |   \\ | |   /  |     |");
        System.out.println("   |     | +-----+ +----- |    \\| +--*   |     |");
        System.out.println("===================================================");
    }

    private int chooseOption() {
        boolean keepAsking = true;
        int option = 0;

        while (keepAsking) {
            System.out.print("Opção: ");

            try {
                option = Integer.parseInt(scan.nextLine());

                keepAsking = false;
            } catch (Exception e) {
                System.out.println("[!] Digite uma opção válida");
            }
        }

        return option;
    }

    private void setCurrentMenu(String name) {
        this.currentMenu = this.menus.get(name);
    }
}
