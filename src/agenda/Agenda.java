package agenda;

import agenda.activities.AgendaActivity;
import agenda.activities.Evento;
import agenda.activities.Lembrete;
import agenda.activities.Tarefa;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Agenda {
    private static Scanner scan = new Scanner(System.in);
    private Map<String, MenuOptions> menus;
    private List<AgendaActivity> activities;

    private MenuOptions currentMenu;
    private boolean continueInAgenda;

    public Agenda() {
        this.setUpMenus();
        this.setCurrentMenu("main");
        this.continueInAgenda = true;
        this.activities = new ArrayList<>();
    }

    private void setUpMenus() {
        // menu principal
        MenuOptions mMain = new MenuOptions(
            new Option("[1] - Criar", () -> this.setCurrentMenu("create")),
            new Option("[2] - Alterar", () -> System.out.println("Opção indisponível")),
            new Option("[3] - Cancelar", () -> System.out.println("Opção indisponível")),
            new Option("[4] - Visualizar", () -> System.out.println("Opção indisponível")),
            new Option("[5] - Sair", () -> this.finish())
        );

        // menu de criação
        MenuOptions mCreate = new MenuOptions(
            new Option("[1] - Criar evento", () -> this.createEvento()),
            new Option("[2] - Criar lembrete", () -> this.createLembrete()),
            new Option("[3] - Criar tarefa", () -> this.createTarefa()),
            new Option("[4] - Voltar", () -> this.setCurrentMenu("main"))
        );

        this.menus = new HashMap<>();

        // salva os menus
        this.menus.put("main", mMain);
        this.menus.put("create", mCreate);
    }

    public void start() {
        int option = 0;

        this.showTitle();

        // mostra as opções referente ao menu atual, então o usuário escolhe uma opção cuja ação é, logo, executada
        while (continueInAgenda) {
            this.divisor();

            this.currentMenu.showOptions();
            
            System.out.println();

            option = this.currentMenu.inputOption(scan);

            this.currentMenu.chooseOptionAction(option);
        }
    }

    private void finish() {
        continueInAgenda = false;
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

    private void divisor() {
        System.out.println("===================================================");
    }

    public void createTarefa() {
        // pega o nome da tarefa
        System.out.print("Digite o nome da tarefa: ");
        String name = scan.nextLine();

        // pega a data de quando essa tarefa deve acontecer
        LocalDate date = null;

        while (date == null) {
            System.out.print("Digite o dia da tarefa (formato - dia/mês/ano): ");
            String dateString = scan.nextLine();

            try {
                date = Agenda.parseDate(dateString);
            } catch (Exception e) {
                System.out.println("[!] Data inválida");
            }
        }

        // cria a tarefa
        Tarefa tarefa = new Tarefa(
            name,
            LocalDateTime.of(date, LocalTime.of(0, 0, 0)), // começo do dia
            LocalDateTime.of(date, LocalTime.of(23, 59, 59)) // fim do dia
        );

        this.activities.add(tarefa);

        this.save();

        System.out.println();
        System.out.println("Tarefa criada com sucesso");
        tarefa.visualize();
        System.out.println();
    }

    public void createEvento() {
        // pega o nome do evento
        System.out.print("Digite o nome do evento: ");
        String name = scan.nextLine();

        // pega a descrição do evento
        System.out.print("Digite uma descrição do evento: ");
        String description = scan.nextLine();

        // pega a data de início do evento
        LocalDateTime startDateTime = null;

        while (startDateTime == null) {
            System.out.print("Digite a data de início do evento (formato - dia/mês/ano): ");
            String dateString = scan.nextLine();

            System.out.print("Digite o horário de início do evento (formato - horas:minutos:segundos): ");
            String timeString = scan.nextLine();

            try {
                startDateTime = Agenda.parseDateTime(dateString, timeString);
            } catch (Exception e) {
                System.out.println("[!] Data inválida");
            }
        }

        // pega a data de término do evento
        LocalDateTime endDateTime = null;

        while (endDateTime == null) {
            System.out.print("Digite a data de término do evento (formato - dia/mês/ano): ");
            String dateString = scan.nextLine();

            System.out.print("Digite o horário de término do evento (formato - horas:minutos:segundos): ");
            String timeString = scan.nextLine();

            try {
                endDateTime = Agenda.parseDateTime(dateString, timeString);
            } catch (Exception e) {
                System.out.println("[!] Data inválida");
            }
        }

        // cria o evento
        Evento evento = new Evento(name, description, startDateTime, endDateTime);

        this.activities.add(evento);

        this.save();

        System.out.println();
        System.out.println("Evento criado com sucesso");
        evento.visualize();
        System.out.println();
    }
    
    public void createLembrete() {
        // pega o nome do lembrete
        System.out.print("Digite o nome do lembrete: ");
        String name = scan.nextLine();

        LocalDateTime dateTime = null;

        // pega a data de quando o lembrete deve ser ativado
        while (dateTime == null) {
            System.out.print("Digite a data do lembrete (formato - dia/mês/ano): ");
            String dateString = scan.nextLine();

            System.out.print("Digite o horário do lembrete (formato - horas:minutos:segundos): ");
            String timeString = scan.nextLine();

            try {
                dateTime = Agenda.parseDateTime(dateString, timeString);
            } catch (Exception e) {
                System.out.println("[!] Data inválida");
            }
        }

        // cria o lembrete
        Lembrete lembrete = new Lembrete(name, dateTime, dateTime);

        this.activities.add(lembrete);

        this.save();

        System.out.println();
        System.out.println("Lembrete criado com sucesso");
        lembrete.visualize();
        System.out.println();
    } 

    public static LocalDate parseDate(String dateString) throws DateTimeParseException {
        // possibilita que a data seja informada, omitindo-se um dos seus componentes
        LocalDate now = LocalDate.now();

        String[] shorthands = {
            "",                                             // dia, mês e ano
            "/" + now.getYear(),                            // mês e ano
            "/" + now.getMonthValue() + "/" + now.getYear() // dia
        };

        LocalDate date = null;

        // faz a análise da data digitada pelo usuário
        for (String shorthand : shorthands) {
            try {
                date = LocalDate.parse(
                    dateString + shorthand,
                    DateTimeFormatter.ofPattern("d/M/y")
                );
            } catch (Exception e) {}
        }

        return date;
    }

    public static LocalTime parseTime(String timeString) throws DateTimeParseException {
        // possibilita que o tempo possa ser informado, omitindo-se um de seus componentes
        String shorthands[] = {
            "",      // horas, minutos e segundos
            ":00",   // horas e minutos
            ":00:00" // horas
        };

        LocalTime time = null;

        // faz a análise do tempo
        for (String shorthand : shorthands) {
            try {
                time = LocalTime.parse(
                    timeString + shorthand,
                    DateTimeFormatter.ofPattern("H:m:s")
                );
            } catch (Exception e) {}
        }

        return time;
    }

    public static LocalDateTime parseDateTime(String dateString, String timeString) throws DateTimeParseException {
        // faz a checagem da data, podendo ter um de seus componentes omitidos
        LocalDate date = Agenda.parseDate(dateString);
        // faz a checagem do tempo, podendo ter um de seus componentes omitidos
        LocalTime time = Agenda.parseTime(timeString);
        
        LocalDateTime dateTime = LocalDateTime.of(date, time);

        return dateTime;
    }

    private void setCurrentMenu(String name) {
        this.currentMenu = this.menus.get(name);
    }

    private void save() {
        try {
            FileOutputStream fs = new FileOutputStream("atividades.ser");
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(this.activities);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
