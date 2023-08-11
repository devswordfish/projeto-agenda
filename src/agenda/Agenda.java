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
        this.showTitle();

        // mostra as opções referente ao menu atual, então o usuário escolhe uma opção cuja ação é, logo, executada
        while (continueInAgenda) {
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

    // print de mensagens para alertar o usuário 
    private void warningMessage(String message) {
        System.out.println();
        System.out.println("    [!] " + message);
        System.out.println();
    }

    private int inputOption(int minOption, int maxOption) {
        int option = 0;

        while (true) {
            System.out.print("Opção: ");

            try {
                option = Integer.parseInt(scan.nextLine());

                // a opção digitada está fora do alcance de opções válidas (1 até quantidade de opções)
                if (option < minOption || option > maxOption) {
                    this.warningMessage("Digite um número entre " + minOption + " e " + maxOption);
                } else {
                    break;
                }
            } catch (Exception e) {
                this.warningMessage("Digite um número");
            }
        }

        return option;
    }

    public void createTarefa() {
        this.divisor();

        // pega o nome da tarefa
        System.out.print("Digite o nome da tarefa: ");
        String name = scan.nextLine();

        // pega a data de quando essa tarefa deve acontecer
        LocalDate date = null;

        while (date == null) {
            System.out.print("Digite o dia da tarefa (formato - dia/mês/ano): ");
            String dateString = scan.nextLine().strip();

            try {
                date = AgendaDateTimeParser.parseDate(dateString);
            } catch (AgendaDateTimeFormatException e) {
                this.warningMessage("Data inválida");
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

        // pega a data e horário do início do evento
        LocalDate startDate = null;

        while (startDate == null) {
            System.out.print("Digite a data de início do evento (formato - dia/mês/ano): ");
            String dateString = scan.nextLine().strip();

            try {
                startDate = AgendaDateTimeParser.parseDate(dateString);
            } catch (AgendaDateTimeFormatException e) {
                this.warningMessage("Data inválida");
            }
        }

        LocalTime startTime = null;

        while (startTime == null) {
            System.out.print("Digite o horário de início do evento (formato - horas:minutos:segundos): ");
            String timeString = scan.nextLine().strip();

            try {
                startTime = AgendaDateTimeParser.parseTime(timeString);
            } catch (AgendaDateTimeFormatException e) {
                this.warningMessage("Horário inválido");
            }
        }

        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);

        // pula uma linha
        System.out.println();

        // pega a data e horário do término do evento
        LocalDate endDate = null;
        
        while (endDate == null) {
            System.out.print("Digite a data de término do evento (formato - dia/mês/ano): ");
            String dateString = scan.nextLine().strip();
            
            try {
                endDate = AgendaDateTimeParser.parseDate(dateString);

                // o dia de término do evento não pode ser antes do início
                if (endDate.isBefore(startDate)) {
                    this.warningMessage("Data de término não pode ser antes da data de início");
                    endDate = null;
                }
            } catch (AgendaDateTimeFormatException e) {
                this.warningMessage("Data inválida");
            }
        }

        LocalTime endTime = null;

        while (endTime == null) {
            System.out.print("Digite o horário de término do evento (formato - horas:minutos:segundos): ");
            String timeString = scan.nextLine().strip();

            try {
                endTime = AgendaDateTimeParser.parseTime(timeString);
    
                // se o evento terminar no mesmo dia, então o horário de término não pode estar antes do começo
                if (endDate.isEqual(startDate) && !endTime.isAfter(startTime)) {
                    this.warningMessage("Horário de término não pode ser o mesmo ou antes do horário de início");
                    endTime = null;
                }
            } catch (AgendaDateTimeFormatException e) {
                this.warningMessage("Horário inválido");
            }
        }

        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        // cria o evento
        Evento evento = new Evento(name, description, startDateTime, endDateTime);

        this.activities.add(evento);

        this.save();

        System.out.println();
        System.out.println("Evento criado com sucesso");
        evento.visualize();
    }
    
    public void createLembrete() {
        this.divisor();

        // pega o nome do lembrete
        System.out.print("Digite o nome do lembrete: ");
        String name = scan.nextLine();

        
        // pega a data e horário de quando o lembrete deve ser ativado
        LocalDate date = null;

        while (date == null) {
            System.out.print("Digite a data do lembrete (formato - dia/mês/ano): ");
            String dateString = scan.nextLine();

            try {
                date = AgendaDateTimeParser.parseDate(dateString);
            } catch (AgendaDateTimeFormatException e) {
                this.warningMessage("Data inválida");
            }
        }

        LocalTime time = null;

        while (time == null) {
            System.out.print("Digite o horário do lembrete (formato - horas:minutos:segundos): ");
            String timeString = scan.nextLine();

            try {
                time = AgendaDateTimeParser.parseTime(timeString);
            } catch (AgendaDateTimeFormatException e) {
                this.warningMessage("Horário inválido");
            }
        }

        LocalDateTime dateTime = LocalDateTime.of(date, time);

        // cria o lembrete
        Lembrete lembrete = new Lembrete(name, dateTime, dateTime);

        this.activities.add(lembrete);

        this.save();

        System.out.println();
        System.out.println("Lembrete criado com sucesso");
        lembrete.visualize();
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
