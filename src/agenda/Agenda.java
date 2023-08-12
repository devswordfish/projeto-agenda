package agenda;

import agenda.activities.AgendaActivity;
import agenda.activities.Evento;
import agenda.activities.Lembrete;
import agenda.activities.Tarefa;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Agenda {
    private static Scanner scan = new Scanner(System.in);
    private static final String SAVE_FILE = "atividades.ser";

    private Map<String, MenuOptions> menus;
    private List<AgendaActivity> activities;

    private MenuOptions currentMenu;
    private boolean continueInAgenda;

    public Agenda() {
        this.setUpMenus();
        this.setCurrentMenu("main");
        this.loadActivities();
        this.continueInAgenda = true;
    }

    private void setUpMenus() {
        // menu principal
        MenuOptions mMain = new MenuOptions(
            new Option("Criar", () -> this.setCurrentMenu("create")),
            new Option("Alterar", () -> System.out.println("Opção indisponível")),
            new Option("Cancelar", () -> System.out.println("Opção indisponível")),
            new Option("Visualizar", () -> this.setCurrentMenu("visualization")),
            new Option("Sair", () -> this.finish())
        );

        // menu de criação
        MenuOptions mCreate = new MenuOptions(
            new Option("Criar evento", () -> this.createEvento()),
            new Option("Criar lembrete", () -> this.createLembrete()),
            new Option("Criar tarefa", () -> this.createTarefa()),
            new Option("Voltar", () -> this.setCurrentMenu("main"))
        );

        MenuOptions mVisualization = new MenuOptions(
            new Option("Ver eventos", () -> System.out.println("Opção indisponível")),
            new Option("Ver lembretes", () -> System.out.println("Opção indisponível")),
            new Option("Ver tarefas", () -> System.out.println("Opção indisponível")),
            new Option("Ver todos", () -> System.out.println("Opção indisponível")),
            new Option("Filtrar por data", () -> System.out.println("Opção indisponível")),
            new Option("Voltar", () -> this.setCurrentMenu("main"))
        );

        this.menus = new HashMap<>();

        // salva os menus
        this.menus.put("main", mMain);
        this.menus.put("create", mCreate);
        this.menus.put("visualization", mVisualization);
    }

    public void start() {
        // mostra as opções referente ao menu atual, então o usuário escolhe uma opção cuja ação é, logo, executada
        while (continueInAgenda) {
            this.clear();
            this.showTitle();
            this.divisor();

            this.currentMenu.showOptions();
            
            System.out.println();
            int option = this.inputOption(1, this.currentMenu.getTotalOptions());

            this.currentMenu.chooseOptionAction(option);
        }
    }

    private void finish() {
        continueInAgenda = false;
    }

    private void clear() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();  
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showTitle() {
        this.divisor();
        System.out.println("   +-----+");
        System.out.println("   |  :  | +-----  +----- |\\    | +--.   +-----+");
        System.out.println("   |  :  | |       |      | \\   | |   \\  |  .  |");
        System.out.println("   +-----+ |  ---+ +---   |  \\  | | :  | |_____|");
        System.out.println("   |     | |     | |      |   \\ | |   /  |     |");
        System.out.println("   |     | +-----+ +----- |    \\| +--°   |     |");
    }

    // divisor entre menus
    private void divisor() {
        System.out.println("===================================================");
    }

    // mostra uma mensagem de erro para o usuário
    private void errorMessage(String message) {
        System.out.println();
        System.out.println("    [?] " + message);
        System.out.println();
    }
    
    // mostra uma mensagem de alerta para o usuário
    private void warningMessage(String message) {
        System.out.println();
        System.out.println("    [!] " + message);
        System.out.println();
    }

    /* inputs */

    private int inputOption(int minOption, int maxOption) {
        int option = 0;

        while (true) {
            System.out.print("Opção: ");

            try {
                option = Integer.parseInt(scan.nextLine());

                // a opção digitada está fora do alcance de opções válidas (1 até quantidade de opções)
                if (option < minOption || option > maxOption) {
                    this.errorMessage("Digite um número entre " + minOption + " e " + maxOption);
                } else {
                    break;
                }
            } catch (Exception e) {
                this.errorMessage("Digite um número");
            }
        }

        return option;
    }

    private LocalDate inputDate(String inputMessage) {
        LocalDate date = null;

        while (date == null) {
            System.out.print(inputMessage);
            String dateString = scan.nextLine().strip();

            try {
                date = AgendaDateTimeParser.parseDate(dateString);
            } catch (AgendaDateTimeFormatException e) {
                this.errorMessage("Data inválida");
            }
        }

        return date;
    }

    private LocalTime inputTime(String inputMessage){
        LocalTime time = null;

        while (time == null) {
            System.out.print(inputMessage);
            String dateString = scan.nextLine().strip();

            try {
                time = AgendaDateTimeParser.parseTime(dateString);
            } catch (AgendaDateTimeFormatException e) {
                this.errorMessage("Horário inválido");
            }
        }

        return time;
    }

    /* criação das atividades da agenda */

    public void createTarefa() {
        this.divisor();

        // pega o nome da tarefa
        System.out.print("Digite o nome da tarefa: ");
        String name = scan.nextLine();

        // pega a data de quando essa tarefa deve acontecer
        LocalDate date = this.inputDate("Digite a data da tarefa (formato - dia/mês/ano): ");

        // verifica se a data informada já passou
        if (this.hasDatePassed(date)) {
            this.warningMessage("A data informada já passou");
        }

        // cria a tarefa
        Tarefa tarefa = new Tarefa(
            name,
            LocalDateTime.of(date, LocalTime.of(0, 0, 0)), // começo do dia
            LocalDateTime.of(date, LocalTime.of(23, 59, 59)) // fim do dia
        );

        this.activities.add(tarefa);

        this.saveActivities();

        System.out.println();
        System.out.println("Tarefa criada com sucesso!");
        tarefa.visualize();

        scan.nextLine();
    }

    public void createEvento() {
        this.divisor();

        // pega o nome do evento
        System.out.print("Digite o nome do evento: ");
        String name = scan.nextLine();

        // pega a descrição do evento
        System.out.print("Digite uma descrição do evento: ");
        String description = scan.nextLine();

        // pula uma linha
        System.out.println();

        // pega a data do início do evento
        LocalDate startDate = this.inputDate("Digite a data de início do evento (formato - dia/mês/ano): ");

        // verifica se a data informada já passou
        if (this.hasDatePassed(startDate)) {
            this.warningMessage("A data informada já passou");
        }

        // pega o horário do início do evento
        LocalTime startTime = this.inputTime("Digite o horário de início do evento (formato - horas:minutos:segundos): ");
        
        // verifica se o horário informado já passou
        if (this.hasTimePassed(startDate, startTime)) {
            this.warningMessage("O horário informado já passou");
        }

        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);

        // pula uma linha
        System.out.println();

        // pega a data do término do evento
        LocalDate endDate = inputDate("Digite a data de término do evento (formato - dia/mês/ano): ");
        
        // trata o erro no qual o usuário pode digitar uma data de término antes do início do evento
        while (endDate.isBefore(startDate)) {
            this.errorMessage("Data de término não pode ser antes da data de início");

            endDate = inputDate("Digite a data de término do evento (formato - dia/mês/ano): ");
        }

        // verifica se a data informada já passou
        if (this.hasDatePassed(endDate)) {
            this.warningMessage("A data informada já passou");
        }

        // pega o horário do término do evento
        LocalTime endTime = this.inputTime("Digite o horário de término do evento (formato - horas:minutos:segundos): ");

        // trata o erro no qual o usuário pode digitar um horário de término antes do horário do início evento do mesmo dia
        while (endDate.isEqual(startDate) && !endTime.isAfter(startTime)) {
            this.errorMessage("Horário de término não pode ser o mesmo ou antes do horário de início");
            
            endTime = this.inputTime("Digite o horário de término do evento (formato - horas:minutos:segundos): ");
        }

        // verifica se o horário informado já passou
        if (this.hasTimePassed(endDate, endTime)) {
            this.warningMessage("O horário informado já passou");
        }

        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        // cria o evento
        Evento evento = new Evento(name, description, startDateTime, endDateTime);

        this.activities.add(evento);

        this.saveActivities();

        System.out.println();
        System.out.println("Evento criado com sucesso");
        evento.visualize();
        
        scan.nextLine();
    }
    
    public void createLembrete() {
        this.divisor();

        // pega o nome do lembrete
        System.out.print("Digite o nome do lembrete: ");
        String name = scan.nextLine();

        // pega a data de quando o lembrete deve ser ativado
        LocalDate date = inputDate("Digite a data do lembrete (formato - dia/mês/ano): ");

        // verifica se a data informada já passou
        if (this.hasDatePassed(date)) {
            this.warningMessage("A data informada já passou");
        }

        // pega o horário de quando o lembrete deve ser ativado
        LocalTime time = this.inputTime("Digite o horário do lembrete (formato - horas:minutos:segundos): ");
        
        // verifica se o horário informado já passou
        if (this.hasTimePassed(date, time)) {
            this.warningMessage("O horário informado já passou");
        }

        LocalDateTime dateTime = LocalDateTime.of(date, time);

        // cria o lembrete
        Lembrete lembrete = new Lembrete(name, dateTime, dateTime);

        this.activities.add(lembrete);

        this.saveActivities();

        System.out.println();
        System.out.println("Lembrete criado com sucesso");
        lembrete.visualize();

        scan.nextLine();
    } 

    /* alteração das atividades da agenda */

    public void changeTarefa() {}

    public void changeEvento() {}
    
    public void changeLembrete() {}

    /* cancelamento das atividades da agenda */

    public void cancelTarefa() {}

    public void cancelEvento() {}
    
    public void cancelLembrete() {}

    /* visualização das atividades da agenda */

    public void visualizeTarefas() {}

    public void visualizeEventos() {}

    public void visualizeLembretes() {}

    public boolean hasDatePassed(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }

    public boolean hasTimePassed(LocalDate date, LocalTime time) {
        return date.isEqual(LocalDate.now()) && time.isBefore(LocalTime.now());
    }

    private void setCurrentMenu(String name) {
        this.currentMenu = this.menus.get(name);
    }

    @SuppressWarnings("unchecked")
    public void loadActivities() {
        try {
            FileInputStream fi = new FileInputStream(Agenda.SAVE_FILE);
            ObjectInputStream is = new ObjectInputStream(fi);
            this.activities = (ArrayList<AgendaActivity>) is.readObject();
            is.close();
        } catch (Exception e) {
            this.activities = new ArrayList<>();
        }
    }

    public void saveActivities() {
        try {
            FileOutputStream fo = new FileOutputStream(Agenda.SAVE_FILE);
            ObjectOutputStream os = new ObjectOutputStream(fo);
            os.writeObject(this.activities);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
