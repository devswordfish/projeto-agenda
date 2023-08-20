package agenda;

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
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Agenda {
    private static Scanner scan = new Scanner(System.in);
    private static final String SAVE_FILE = "atividades.ser";

    private Map<String, MenuOptions> menus;
    private List<Evento> eventos;
    private List<Lembrete> lembretes;
    private List<Tarefa> tarefas;

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

        this.addEvento(evento);
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

        this.addLembrete(lembrete);
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

        this.addTarefa(tarefa);
        this.saveActivities();

        System.out.println();
        System.out.println("Tarefa criada com sucesso!");
        tarefa.showAttributes();

        scan.nextLine();
    }

    /* alteração das atividades da agenda */

    private void changeEvento() {
        this.displayEventos();

        int option = this.inputOption(0, this.eventos.size());
        
        if (option != 0) {
            Evento curActivity = this.eventos.get(option - 1);

            MenuOptions mChange = new MenuOptions(
                new Option("Mudar nome", () -> {
                    String name = inputString("Digite o novo nome: ");
                    curActivity.setName(name);
                }),
                new Option("Mudar descrição", () -> {
                    String description = inputString("Digite a nova descrição: ");
                    curActivity.setDescription(description);
                }),
                new Option("Mudar data de início", () -> {
                    LocalDate startDate = null;

                    while (startDate == null) {
                        startDate = this.inputDate("Digite a nova data de início (formato - dia/mês/ano): ");

                        try {
                            curActivity.setStartDate(startDate);
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
                            curActivity.setStartTime(startTime);
                        } catch (AgendaChronologicalOrderException e) {
                            this.errorMessage("Horário de início não pode ser depois do horário de término");
                            startTime = null;
                        }
                    }

                    if (this.hasTimePassed(curActivity.getStartDate(), startTime)) this.warningMessage("O horário informado já passou");
                }),
                new Option("Mudar data de término", () -> {
                    LocalDate endDate = null;

                    while (endDate == null) {
                        endDate = this.inputDate("Digite a nova data de término (formato - dia/mês/ano): ");

                        try {
                            curActivity.setEndDate(endDate);
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
                            curActivity.setEndTime(endTime);
                        } catch (AgendaChronologicalOrderException e) {
                            this.errorMessage("Horário de término não pode ser antes do horário de início");
                            endTime = null;
                        }
                    }

                    if (this.hasTimePassed(curActivity.getStartDate(), endTime)) this.warningMessage("O horário informado já passou");
                }),
                new Option("Voltar", () -> {})
            );

            curActivity.showAttributes();

            mChange.showOptions();

            int attrOption = this.inputOption(1, mChange.getTotalOptions());

            mChange.chooseOptionAction(attrOption);

            if (attrOption != mChange.getTotalOptions()) {
                this.eventos.remove(curActivity);

                this.addEvento(curActivity);
                this.saveActivities();

                System.out.println();
                System.out.println("Evento alterado com sucesso!");

                scan.nextLine();
            }
        }
    }

    private void changeLembrete() {
        this.displayLembretes();

        int option = this.inputOption(0, this.lembretes.size());
        
        if (option != 0) {
            Lembrete curActivity = this.lembretes.get(option - 1);

            MenuOptions mChange = new MenuOptions(
                new Option("Mudar nome", () -> {
                    String name = inputString("Digite o novo nome: ");
                    curActivity.setName(name);
                }),
                new Option("Mudar data", () -> {
                    LocalDate startDate = null;

                    while (startDate == null) {
                        startDate = this.inputDate("Digite a nova data (formato - dia/mês/ano): ");

                        curActivity.setDate(startDate);
                    }

                    if (this.hasDatePassed(startDate)) this.warningMessage("A data informada já passou");
                }),
                new Option("Mudar horário", () -> {
                    LocalTime startTime = null;

                    while (startTime == null) {
                        startTime = this.inputTime("Digite o novo horário (formato - horas:minutos:segundos): ");

                        curActivity.setTime(startTime);
                    }

                    if (this.hasTimePassed(curActivity.getDate(), startTime)) this.warningMessage("O horário informado já passou");
                }),
                new Option("Voltar", () -> {})
            );

            curActivity.showAttributes();

            mChange.showOptions();

            int attrOption = this.inputOption(1, mChange.getTotalOptions());

            mChange.chooseOptionAction(attrOption);

            if (attrOption != mChange.getTotalOptions()) {
                this.lembretes.remove(curActivity);

                this.addLembrete(curActivity);
                this.saveActivities();

                System.out.println();
                System.out.println("Lembrete alterado com sucesso!");

                scan.nextLine();
            }
        }
    }

    private void changeTarefa() {
        this.displayTarefas();

        int option = this.inputOption(0, this.tarefas.size());
        
        if (option != 0) {
            Tarefa curActivity = this.tarefas.get(option - 1);

            MenuOptions mChange = new MenuOptions(
                new Option("Mudar nome", () -> {
                    String name = inputString("Digite o novo nome: ");
                    curActivity.setName(name);
                }),
                new Option("Mudar data", () -> {
                    LocalDate startDate = null;

                    while (startDate == null) {
                        startDate = this.inputDate("Digite a nova data (formato - dia/mês/ano): ");

                        curActivity.setDate(startDate);
                    }

                    if (this.hasDatePassed(startDate)) this.warningMessage("A data informada já passou");
                }),
                new Option("Voltar", () -> {})
            );

            curActivity.showAttributes();

            mChange.showOptions();

            int attrOption = this.inputOption(1, mChange.getTotalOptions());

            mChange.chooseOptionAction(attrOption);

            if (attrOption != mChange.getTotalOptions()) {
                this.tarefas.remove(curActivity);

                this.addTarefa(curActivity);
                this.saveActivities();

                System.out.println();
                System.out.println("Tarefa alterado com sucesso!");

                scan.nextLine();
            }
        }
    }

    /* cancelamento das atividades da agenda */

    private void cancelEvento() {
        this.displayEventos();

        if (this.eventos.size() != 0) {
            int option = this.inputOption(0, this.eventos.size());
            
            if (option != 0) {
                this.eventos.remove(this.eventos.get(option - 1));            
                this.saveActivities();

                System.out.println();
                System.out.println("Evento deletado com sucesso!");

                scan.nextLine();
            }
        }
    }

    private void cancelLembrete() {
        this.displayLembretes();

        if (this.lembretes.size() != 0) {
            int option = this.inputOption(0, this.lembretes.size());
            
            if (option != 0) {
                this.lembretes.remove(this.lembretes.get(option - 1));            
                this.saveActivities();

                System.out.println();
                System.out.println("Lembrete deletado com sucesso!");

                scan.nextLine();
            }
        }
    }

    private void cancelTarefa() {
        this.displayTarefas();

        if (this.tarefas.size() != 0) {
            int option = this.inputOption(0, this.tarefas.size());
            
            if (option != 0) {
                this.tarefas.remove(this.tarefas.get(option - 1));            
                this.saveActivities();

                System.out.println();
                System.out.println("Tarefa deletado com sucesso!");

                scan.nextLine();
            }
        }
    }

    public void displayEventos() {
        if (this.eventos.size() == 0) {
            System.out.println("Sem eventos");
        } else {
            System.out.println("[0] - Voltar");
            
            for (int i = 0; i < this.eventos.size(); i++) {
                System.out.format("[%d] - %s\n", i + 1, this.eventos.get(i).getName());
            }
        }
    }

    public void displayLembretes() {
        if (this.lembretes.size() == 0) {
            System.out.println("Sem lembretes");
        } else {
            System.out.println("[0] - Voltar");
            
            for (int i = 0; i < this.lembretes.size(); i++) {
                System.out.format("[%d] - %s\n", i + 1, this.lembretes.get(i).getName());
            }
        }
    }

    public void displayTarefas() {
        if (this.tarefas.size() == 0) {
            System.out.println("Sem tarefas");
        } else {
            System.out.println("[0] - Voltar");
            
            for (int i = 0; i < this.tarefas.size(); i++) {
                System.out.format("[%d] - %s\n", i + 1, this.tarefas.get(i).getName());
            }
        }
    }

    /* visualização das atividades da agenda */

    private void showEventos() {
        if (this.eventos.size() == 0) {
            System.out.println("Sem eventos");
        } else {
            LocalDate curDateTime = null;
            
            for (Evento activity : this.eventos) {
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

    private void showLembretes() {
        if (this.lembretes.size() == 0) {
            System.out.println("Sem lembretes");
        } else {
            LocalDate curDateTime = null;
            
            for (Lembrete activity : this.lembretes) {
                LocalDate activityDate = activity.getDate();
                
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

    private void showTarefas() {
        if (this.tarefas.size() == 0) {
            System.out.println("Sem tarefas");
        } else {
            LocalDate curDateTime = null;
            
            for (Tarefa activity : this.tarefas) {
                LocalDate activityDate = activity.getDate();
                
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
    private void addEvento(Evento evento) {
        LocalDate activityDate = evento.getStartDate();
        
        // binary search para achar a posição na qual a atividade deve ser inserida (a atividade é sempre inserida por último nas atividades referentes ao mesmo dia)
        int l = 0;
        int r = this.eventos.size() - 1;

        while (l <= r) {
            int m = (l + r) / 2;

            LocalDate middleActivityDate = this.eventos.get(m).getStartDate();

            if (activityDate.isBefore(middleActivityDate)) r = m - 1;
            else                                                   l = m + 1;
        }

        this.eventos.add(l, evento);
    }

    private void addLembrete(Lembrete lembrete) {
        LocalDate activityDate = lembrete.getDate();
        
        // binary search para achar a posição na qual a atividade deve ser inserida (a atividade é sempre inserida por último nas atividades referentes ao mesmo dia)
        int l = 0;
        int r = this.lembretes.size() - 1;

        while (l <= r) {
            int m = (l + r) / 2;

            LocalDate middleActivityDate = this.lembretes.get(m).getDate();

            if (activityDate.isBefore(middleActivityDate)) r = m - 1;
            else                                                   l = m + 1;
        }

        this.lembretes.add(l, lembrete);
    }

    private void addTarefa(Tarefa tarefa) {
        LocalDate activityDate = tarefa.getDate();
        
        // binary search para achar a posição na qual a atividade deve ser inserida (a atividade é sempre inserida por último nas atividades referentes ao mesmo dia)
        int l = 0;
        int r = this.tarefas.size() - 1;

        while (l <= r) {
            int m = (l + r) / 2;

            LocalDate middleActivityDate = this.tarefas.get(m).getDate();

            if (activityDate.isBefore(middleActivityDate)) r = m - 1;
            else                                                   l = m + 1;
        }

        this.tarefas.add(l, tarefa);
    }

    @SuppressWarnings("unchecked")
    private void loadActivities() {
        try {
            FileInputStream fi1 = new FileInputStream("eventos");
            ObjectInputStream is1 = new ObjectInputStream(fi1);

            this.eventos = (List<Evento>) is1.readObject();

            FileInputStream fi2 = new FileInputStream("lembretes");
            ObjectInputStream is2 = new ObjectInputStream(fi2);

            this.lembretes = (List<Lembrete>) is2.readObject();

            FileInputStream fi3 = new FileInputStream("eventos");
            ObjectInputStream is3 = new ObjectInputStream(fi3);

            this.tarefas = (List<Tarefa>) is3.readObject();

            is1.close();
            is2.close();
            is3.close();
        } catch (Exception e) {
            this.eventos = new ArrayList<>();
            this.lembretes = new ArrayList<>();
            this.tarefas = new ArrayList<>();
        }
    }

    private void saveActivities() {
        try {
            FileOutputStream fo1 = new FileOutputStream("eventos");
            ObjectOutputStream os1 = new ObjectOutputStream(fo1);
            os1.writeObject(this.eventos);

            FileOutputStream fo2 = new FileOutputStream("lembretes");
            ObjectOutputStream os2 = new ObjectOutputStream(fo2);
            os2.writeObject(this.lembretes);

            FileOutputStream fo3 = new FileOutputStream("tarefas");
            ObjectOutputStream os3 = new ObjectOutputStream(fo3);
            os3.writeObject(this.tarefas);

            os1.close();
            os2.close();
            os3.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
