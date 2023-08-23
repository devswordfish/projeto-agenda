package agendapages;

import java.util.Map;
import java.util.HashMap;

import agendapages.activities.AgendaActivity;

import agendapages.pages.Page;
import agendapages.pages.TaskPage;
import agendapages.pages.ReminderPage;
import agendapages.pages.EventPage;

import agendapages.io.AgendaInput;
import agendapages.io.AgendaOutput;

import agendapages.menu.Menu;
import agendapages.menu.Option;

public class Agenda {
    // páginas da agenda
    private static enum PAGE { TASK, REMINDER, EVENT };
    
    // menus da agenda
    private static enum MENU { MAIN, CREATION, CHANGE, CANCEL, VIEW };

    private Map<MENU, Menu<Void>> menus;
    private Map<PAGE, Page<? extends AgendaActivity>> pages;

    private Menu<Void> currentMenu;
    private MENU menu;
    private boolean continueInAgenda;

    public Agenda() {
        this.setUpMenus();
        this.setUpPages();
        
        this.setCurrentMenu(MENU.MAIN);
        this.continueInAgenda = true;
    }

    /* métodos de configurações */
    private void setUpMenus() {
        // menu principal
        Menu<Void> mMain = new Menu<>(0);
        mMain.addOption(new Option<>("Sair", __ -> this.finish()));
        mMain.addOption(new Option<>("Criar", __ -> this.setCurrentMenu(MENU.CREATION)));
        mMain.addOption(new Option<>("Alterar", __ -> this.setCurrentMenu(MENU.CHANGE)));
        mMain.addOption(new Option<>("Cancelar", __ -> this.setCurrentMenu(MENU.CANCEL)));
        mMain.addOption(new Option<>("Visualizar", __ -> this.setCurrentMenu(MENU.VIEW)));

        // menu de criação
        Menu<Void> mCreate = new Menu<>(0);
        mCreate.addOption(new Option<>("Voltar", __ -> this.setCurrentMenu(MENU.MAIN)));
        mCreate.addOption(new Option<>("Criar evento", __ -> this.getEventoPage().create()));
        mCreate.addOption(new Option<>("Criar lembrete", __ -> this.getLembretePage().create()));
        mCreate.addOption(new Option<>("Criar tarefa", __ -> this.getTarefaPage().create()));

        // menu de alteração
        Menu<Void> mChange = new Menu<>(0);
        mChange.addOption(new Option<>("Voltar", __ -> this.setCurrentMenu(MENU.MAIN)));
        mChange.addOption(new Option<>("Alterar evento", __ -> this.getEventoPage().change()));
        mChange.addOption(new Option<>("Alterar lembrete", __ -> this.getLembretePage().change()));
        mChange.addOption(new Option<>("Alterar tarefa", __ -> this.getTarefaPage().change()));

        // menu de cancelamento
        Menu<Void> mCancelation = new Menu<>(0);
        mCancelation.addOption(new Option<>("Voltar", __ -> this.setCurrentMenu(MENU.MAIN)));
        mCancelation.addOption(new Option<>("Cancelar evento", __ -> this.getEventoPage().cancel()));
        mCancelation.addOption(new Option<>("Cancelar lembrete", __ -> this.getLembretePage().cancel()));
        mCancelation.addOption(new Option<>("Cancelar tarefa", __ -> this.getTarefaPage().cancel()));

        // menu de visualização
        Menu<Void> mVisualization = new Menu<>(0);
        mVisualization.addOption(new Option<>("Voltar", __ -> this.setCurrentMenu(MENU.MAIN)));
        mVisualization.addOption(new Option<>("Ver eventos", __ -> this.getEventoPage().view()));
        mVisualization.addOption(new Option<>("Ver lembretes", __ -> this.getLembretePage().view()));
        mVisualization.addOption(new Option<>("Ver tarefas", __ -> this.getTarefaPage().view()));

        this.menus = new HashMap<>();

        // salva os menus
        this.menus.put(MENU.MAIN, mMain);
        this.menus.put(MENU.CREATION, mCreate);
        this.menus.put(MENU.CHANGE, mChange);
        this.menus.put(MENU.CANCEL, mCancelation);
        this.menus.put(MENU.VIEW, mVisualization);
    }

    private void setUpPages() {
        this.pages = new HashMap<>();

        this.pages.put(PAGE.TASK, new TaskPage());
        this.pages.put(PAGE.REMINDER, new ReminderPage());
        this.pages.put(PAGE.EVENT, new EventPage());
    }

    // começo do programa
    public void start() {
        // mostra as opções referente ao menu atual, então o usuário escolhe uma opção cuja ação é, logo, executada
        while (continueInAgenda) {
            AgendaOutput.clear();
            this.showTitle();

            AgendaOutput.section(this.menu.toString());

            this.currentMenu.show();
            
            System.out.println();
            int option = AgendaInput.inputOption(0, this.currentMenu.getTotalOptions());

            AgendaOutput.section();

            this.currentMenu.chooseOptionAction(option, null);
        }

        AgendaOutput.clear();
    }

    private void finish() {
        continueInAgenda = false;
    }

    // titulo do programa
    private void showTitle() {
        AgendaOutput.section();
        System.out.println("   +-----+");
        System.out.println("   |  :  | +-----  +----- |\\    | +--.   +-----+");
        System.out.println("   |  :  | |       |      | \\   | |   \\  |  .  |");
        System.out.println("   +-----+ |  ---+ +---   |  \\  | | :  | |_____|");
        System.out.println("   |     | |     | |      |   \\ | |   /  |     |");
        System.out.println("   |     | +-----+ +----- |    \\| +--°   |     |");
    }

    /* getters e setters */

    private Page<? extends AgendaActivity> getTarefaPage() {
        return this.pages.get(PAGE.TASK);
    }

    private Page<? extends AgendaActivity> getLembretePage() {
        return this.pages.get(PAGE.REMINDER);
    }

    private Page<? extends AgendaActivity> getEventoPage() {
        return this.pages.get(PAGE.EVENT);
    }

    private void setCurrentMenu(MENU menuName) {
        this.menu = menuName;
        this.currentMenu = this.menus.get(menuName);
    }
}
