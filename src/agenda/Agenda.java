package agenda;

import java.util.Date;
import java.util.Scanner;

public class Agenda {
    public void iniciar() {
        Scanner scan = new Scanner(System.in);

        int opcao = 0;

        System.out.println("##########");
        System.out.println("# AGENDA #");
        System.out.println("##########");

        do {
            showOpcoes();

            boolean keepAsking = true;

            while (keepAsking) {
                System.out.print("Opcao: ");

                try {
                    opcao = Integer.parseInt(scan.nextLine());

                    keepAsking = false;
                } catch (Exception e) {
                    System.out.println("Digite umaa opcao valida");
                }
            }

        } while (opcao != 5);
    }

    public void showOpcoes() {
        System.out.println("[1] Criar");
        System.out.println("[2] Alterar");
        System.out.println("[3] Cancelar");
        System.out.println("[4] Visualizar");
        System.out.println("[5] Sair");
    }

    public void criarEvento(String name, String descricao, Date dataInicio, Date dataTermino) {

    }
}
