package agenda;

public class MenuOptions {
    private Option[] options;

    public MenuOptions(Option ...options) {
        this.options = options.clone();
    }

    public void showOptions() {
        for (Option option : this.options) {
            System.out.println(option.getOption());
        }
    }

    public void chooseOptionAction(int option) {
        this.options[option - 1].useAction();
    }
}
