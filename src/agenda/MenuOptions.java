package agenda;

public class MenuOptions {
    private Option[] options;

    public MenuOptions(Option ...options) {
        this.options = options;
    }

    public void showOptions() {
        for (int i = 1; i <= this.options.length; i++) {
            System.out.format("[%d] - %s\n", i, this.options[i - 1].getOption());
        }
    }

    public int getTotalOptions() {
        return this.options.length;
    }

    public void chooseOptionAction(int option) {
        this.options[option - 1].useAction();
    }
}
