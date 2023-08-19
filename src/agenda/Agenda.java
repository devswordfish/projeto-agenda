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
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.function.Predicate;
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
            new Option("Criar", () -> this.setCurrentMenu("creation")),
            new Option("Alterar", () -> this.setCurrentMenu("alteration")),
            new Option("Cancelar", () -> this.setCurrentMenu("cancellation")),
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

        // menu de alteração
        MenuOptions mAlter = new MenuOptions(
            new Option("Alterar evento", () -> this.changeEvento()),
            new Option("Alterar lembrete", () -> this.changeLembrete()),
            new Option("Alterar tarefa", () -> this.changeTarefa()),
            new Option("Voltar", () -> this.setCurrentMenu("main"))
        );

        // menu de cancelamento
        MenuOptions mCancelation = new MenuOptions(
            new Option("Cancelar evento", () -> this.cancelEvento()),
            new Option("Cancelar lembrete", () -> this.cancelLembrete()),
            new Option("Cancelar tarefa", () -> this.cancelTarefa()),
            new Option("Voltar", () -> this.setCurrentMenu("main"))
        );

        // menu de visualização
        MenuOptions mVisualization = new MenuOptions(
            new Option("Ver eventos", () -> this.showEventos()),
            new Option("Ver lembretes", () -> this.showLembretes()),
            new Option("Ver tarefas", () -> this.showTarefas()),
            new Option("Ver todos", () -> this.showActivities()),
            new Option("Filtrar por data", () -> this.showActivitiesByDate()),
            new Option("Voltar", () -> this.setCurrentMenu("main"))
        );

        this.menus = new HashMap<>();

        // salva os menus
        this.menus.put("main", mMain);
        this.menus.put("creation", mCreate);
        this.menus.put("visualization", mVisualization);
        this.menus.put("cancellation", mCancelation);
        this.menus.put("alteration", mAlter);
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

            this.divisor();

            this.currentMenu.chooseOptionAction(option);
        }

        this.clear();
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

    /* mensagens da agenda */

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

    private String inputString(String inputMessage) {
        System.out.print(inputMessage);
        String str = scan.nextLine().strip();
        return str;
    }

    private int inputOption(int minOption, int maxOption) {
        int option = 0;

        while (true) {
            System.out.print("Opção: ");

            try {
                option = Integer.parseInt(scan.nextLine());

                // a opção digitada está fora do alcance de opções válidas (minOption até maxOption)
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
            String timeString = scan.nextLine().strip();

            try {
                time = AgendaDateTimeParser.parseTime(timeString);
            } catch (AgendaDateTimeFormatException e) {
                this.errorMessage("Horário inválido");
            }
        }

        return time;
    }

    /* criação das atividades da agenda */

    private void createEvento() {
        // pega o nome do evento
        String name = this.inputString("Digite o nome do evento: ");

        // pega a descrição do evento
        String description = this.inputString("Digite uma descrição do evento: ");

        // pula uma linha
        System.out.println();

        // pega a data do início do evento
        LocalDate startDate = this.inputDate("Digite a data de início do evento (formato - dia/mês/ano): ");

        // verifica se a data informada já passou
        if (this.hasDatePassed(startDate)) this.warningMessage("A data informada já passou");

        // pega o horário do início do evento
        LocalTime startTime = this.inputTime("Digite o horário de início do evento (formato - horas:minutos:segundos): ");
        
        // verifica se o horário informado já passou
        if (this.hasTimePassed(startDate, startTime)) this.warningMessage("O horário informado já passou");

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
        if (this.hasDatePassed(endDate)) this.warningMessage("A data informada já passou");


        // pega o horário do término do evento
        LocalTime endTime = this.inputTime("Digite o horário de término do evento (formato - horas:minutos:segundos): ");

        // trata o erro no qual o usuário pode digitar um horário de término antes do horário do início evento do mesmo dia
        while (endDate.isEqual(startDate) && !endTime.isAfter(startTime)) {
            this.errorMessage("Horário de término não pode ser o mesmo ou antes do horário de início");
            
            endTime = this.inputTime("Digite o horário de término do evento (formato - horas:minutos:segundos): ");
        }

        // verifica se o horário informado já passou
        if (this.hasTimePassed(endDate, endTime)) this.warningMessage("O horário informado já passou");

        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        // cria o evento
        Evento evento = new Evento(name, description, startDateTime, endDateTime);

        this.addAgendaActivity(evento);
        this.saveActivities();

        System.out.println();
        System.out.println("Evento criado com sucesso!");
        evento.showAttributes();

        scan.nextLine();
    }

    private void createLembrete() {
        // pega o nome do lembrete
        String name = this.inputString("Digite o nome do lembrete: ");

        // pega a data de quando o lembrete deve ser ativado
        LocalDate date = inputDate("Digite a data do lembrete (formato - dia/mês/ano): ");

        // verifica se a data informada já passou
        if (this.hasDatePassed(date)) this.warningMessage("A data informada já passou");

        // pega o horário de quando o lembrete deve ser ativado
        LocalTime time = this.inputTime("Digite o horário do lembrete (formato - horas:minutos:segundos): ");

        // verifica se o horário informado já passou
        if (this.hasTimePassed(date, time)) this.warningMessage("O horário informado já passou");

        LocalDateTime dateTime = LocalDateTime.of(date, time);

        // cria o lembrete
        Lembrete lembrete = new Lembrete(name, dateTime);

        this.addAgendaActivity(lembrete);
        this.saveActivities();

        System.out.println();
        System.out.println("Lembrete criado com sucesso!");
        lembrete.showAttributes();

        scan.nextLine();
    } 

    private void createTarefa() {
        // pega o nome da tarefa
        String name = this.inputString("Digite o nome da tarefa: ");

        // pega a data de quando essa tarefa deve acontecer
        LocalDate date = this.inputDate("Digite a data da tarefa (formato - dia/mês/ano): ");

        // verifica se a data informada já passou
        if (this.hasDatePassed(date)) this.warningMessage("A data informada já passou");

        // cria a tarefa
        Tarefa tarefa = new Tarefa(name, date);

        this.addAgendaActivity(tarefa);
        this.saveActivities();

        System.out.println();
        System.out.println("Tarefa criada com sucesso!");
        tarefa.showAttributes();

        scan.nextLine();
    }

    /* alteração das atividades da agenda */

    private void changeEvento() {
        List<AgendaActivity> filteredActivities = this.filterActivities(activity -> activity.getClass() == Evento.class);

        this.enumeratedDisplay(filteredActivities, "Sem eventos");
        this.changeActivity(filteredActivities, "Evento alterado com sucesso", new int[0]);
    }

    private void changeLembrete() {
        List<AgendaActivity> filteredActivities = this.filterActivities(activity -> activity.getClass() == Lembrete.class);

        this.enumeratedDisplay(this.filterActivities(activity -> activity.getClass() == Lembrete.class), "Sem lembretes");
        this.changeActivity(filteredActivities, "Lembrete alterado com sucesso", new int[] { 1 });
    }

    private void changeTarefa() {
        List<AgendaActivity> filteredActivities = this.filterActivities(activity -> activity.getClass() == Tarefa.class);

        this.enumeratedDisplay(this.filterActivities(activity -> activity.getClass() == Tarefa.class), "Sem tarefas");
        this.changeActivity(filteredActivities, "Tarefa alterado com sucesso", new int[] { 1 });
    }

    private void changeActivity(List<AgendaActivity> filteredActivities, String successMessage, int[] hide) {
        int option = this.inputOption(0, activities.size());
        
        if (option != 0) {
            AgendaActivity curActivity = filteredActivities.get(option - 1);

            MenuOptions mChange = this.createAlterationMenuOptions(curActivity);
            
            for (int h : hide) {
                mChange.setOptionVisibility(h, false);
            }

            curActivity.showAttributes();

            mChange.showOptions();

            int attrOption = this.inputOption(1, mChange.getTotalOptions());

            mChange.chooseOptionAction(attrOption);

            if (attrOption != mChange.getTotalOptions()) {
                this.activities.remove(curActivity);

                this.addAgendaActivity(curActivity);
                this.saveActivities();

                System.out.println();
                System.out.println(successMessage);

                scan.nextLine();
            }
        }
    }

    // menu dinâmico com opções dinâmicas referentes aos atributos das atividades
    private MenuOptions createAlterationMenuOptions(AgendaActivity activity) {
        return new MenuOptions(
                new Option("Mudar nome", () -> {
                    String name = inputString("Digite o novo nome: ");
                    activity.setName(name);
                }),
                new Option("Mudar descrição", () -> {
                    String description = inputString("Digite a nova descrição: ");
                    ((Evento) activity).setDescription(description);
                }),
                new Option("Mudar data de início", () -> {
                    LocalDate startDate = null;

                    while (startDate == null) {
                        startDate = this.inputDate("Digite a nova data de início (formato - dia/mês/ano): ");

                        try {
                            activity.setStartDate(startDate);
                        } catch (AgendaChronologicalOrderException e) {
                            this.errorMessage("Data de início não pode ser depois da data de término");
                            startDate = null;
                        }
                    }

                    if (this.hasDatePassed(startDate)) this.warningMessage("A data informada já passou");
                }),
                new Option("Mudar horário de início", () -> {
                    LocalTime startTime = null;

                    while (startTime == null) {
                        startTime = this.inputTime("Digite o novo horário de início (formato - horas:minutos:segundos): ");

                        try {
                            activity.setStartTime(startTime);
                        } catch (AgendaChronologicalOrderException e) {
                            this.errorMessage("Horário de início não pode ser depois do horário de término");
                            startTime = null;
                        }
                    }

                    if (this.hasTimePassed(activity.getStartDate(), startTime)) this.warningMessage("O horário informado já passou");
                }),
                new Option("Mudar data de término", () -> {
                    LocalDate endDate = null;

                    while (endDate == null) {
                        endDate = this.inputDate("Digite a nova data de término (formato - dia/mês/ano): ");

                        try {
                            activity.setEndDate(endDate);
                        } catch (AgendaChronologicalOrderException e) {
                            this.errorMessage("Data de término não pode ser antes da data de início");
                            endDate = null;
                        }
                    }

                    if (this.hasDatePassed(endDate)) this.warningMessage("A data informada já passou");
                }),
                new Option("Mudar horário de término", () -> {
                    LocalTime endTime = null;

                    while (endTime == null) {
                        endTime = this.inputTime("Digite o novo horário de término (formato - horas:minutos:segundos): ");

                        try {
                            activity.setEndTime(endTime);
                        } catch (AgendaChronologicalOrderException e) {
                            this.errorMessage("Horário de término não pode ser antes do horário de início");
                            endTime = null;
                        }
                    }

                    if (this.hasTimePassed(activity.getStartDate(), endTime)) this.warningMessage("O horário informado já passou");
                }),
                new Option("Voltar", () -> {})
            );
    }

    /* cancelamento das atividades da agenda */

    private void cancelEvento() {
        this.enumeratedDisplay(this.filterActivities(activity -> activity.getClass() == Evento.class), "Sem eventos");
        this.cancelActivity("Evento deletado com sucesso!");
    }

    private void cancelLembrete() {
        this.enumeratedDisplay(this.filterActivities(activity -> activity.getClass() == Lembrete.class), "Sem lembretes");
        this.cancelActivity("Lembrete deletado com sucesso!");
    }

    private void cancelTarefa() {
        this.enumeratedDisplay(this.filterActivities(activity -> activity.getClass() == Tarefa.class), "Sem tarefas");
        this.cancelActivity("Tarefa deletado com sucesso!");
    }

    private void cancelActivity(String successMessage) {
        int option = this.inputOption(0, activities.size());
        
        if (option != 0) {
            this.activities.remove(this.activities.get(option - 1));            
            this.saveActivities();

            System.out.println();
            System.out.println(successMessage);

            scan.nextLine();
        }
    }

    private void enumeratedDisplay(List<AgendaActivity> activities, String fallback) {
        if (activities.size() == 0) {
            System.out.println(fallback);
        } else {
            System.out.println("[0] - Voltar");
            
            for (int i = 0; i < activities.size(); i++) {
                System.out.format("[%d] - %s\n", i + 1, activities.get(i).getName());
            }
        }
    }

    /* visualização das atividades da agenda */

    private void showEventos() {
        this.visualizationDisplay(this.filterActivities(activity -> activity.getClass() == Evento.class), "Sem eventos");
    }

    private void showLembretes() {
        this.visualizationDisplay(this.filterActivities(activity -> activity.getClass() == Lembrete.class), "Sem lembretes");
    }

    private void showTarefas() {
        this.visualizationDisplay(this.filterActivities(activity -> activity.getClass() == Tarefa.class), "Sem tarefas");
    }

    private void showActivities() {
        this.visualizationDisplay(this.activities, "Sem eventos/lembretes/atividades");
    }

    private void showActivitiesByDate() {
        LocalDate date = this.inputDate("Digite a data (formato - dia/mês/ano): ");

        this.visualizationDisplay(this.filterActivities(activity -> activity.getStartDate().isEqual(date)), "Sem eventos/lembretes/atividades nesse dia");
    }

    // interface genérica para visualização
    private void visualizationDisplay(List<AgendaActivity> activities, String fallback) {
        if (activities.size() == 0) {
            System.out.println(fallback);
        } else {
            LocalDate curDateTime = null;
            
            for (AgendaActivity activity : activities) {
                LocalDate activityDate = activity.getStartDate();
                
                if (!activityDate.equals((curDateTime))) {
                    if (curDateTime != null) System.out.println();
                    
                    curDateTime = activityDate;
                    
                    System.out.println("--- " + curDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
                
                activity.show();
            }
        }

        scan.nextLine();
    }

    // filtragem das atividades em relação a uma condição
    private List<AgendaActivity> filterActivities(Predicate<AgendaActivity> predicate) {
        return this.activities.stream().filter(predicate).toList();
    }

    private boolean hasDatePassed(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }

    private boolean hasTimePassed(LocalDate date, LocalTime time) {
        return date.isEqual(LocalDate.now()) && time.isBefore(LocalTime.now());
    }

    private void setCurrentMenu(String name) {
        this.currentMenu = this.menus.get(name);
    }

    // insere a atividade ordenada por data
    private void addAgendaActivity(AgendaActivity activity) {
        LocalDateTime activityDateTime = activity.getStartDateTime();
        
        // binary search para achar a posição na qual a atividade deve ser inserida (a atividade é sempre inserida por último nas atividades referentes ao mesmo dia)
        int l = 0;
        int r = this.activities.size() - 1;

        while (l <= r) {
            int m = (l + r) / 2;

            LocalDateTime middleActivityDatetime = this.activities.get(m).getStartDateTime();

            if (activityDateTime.isBefore(middleActivityDatetime)) r = m - 1;
            else                                                   l = m + 1;
        }

        this.activities.add(l, activity);
    }

    @SuppressWarnings("unchecked")
    private void loadActivities() {
        try {
            FileInputStream fi = new FileInputStream(Agenda.SAVE_FILE);
            ObjectInputStream is = new ObjectInputStream(fi);
            this.activities = (ArrayList<AgendaActivity>) is.readObject();
            is.close();
        } catch (Exception e) {
            this.activities = new ArrayList<>();
        }
    }

    private void saveActivities() {
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
