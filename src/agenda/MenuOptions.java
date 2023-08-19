package agenda;

public class MenuOptions {
    private Option[] options;

    public MenuOptions(Option ...options) {
        this.options = options;
    }

    public void showOptions() {
        int count = 0;
        for (int i = 1; i <= this.options.length; i++) {
            if (this.options[i - 1].isVisible()) {
                count++;
                System.out.format("[%d] - %s\n", count, this.options[i - 1].getOption());
            }
        }
    }

    public void setOptionVisibility(int index, boolean visibility) {
        this.options[index].setVisible(visibility);
    }

    public int getTotalOptions() {
        int count = 0;
        for (Option o : this.options) {
            if (o.isVisible()) count++;
        }
        return count;
    }

    public void chooseOptionAction(int option) {
        int countInvisible = 0;
        for (int i = 0; i < option; i++) {
            if (!this.options[i].isVisible()) countInvisible++;
        }

        this.options[option + countInvisible - 1].useAction();
    }
}
