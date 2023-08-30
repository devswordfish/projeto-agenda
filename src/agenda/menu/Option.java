package agenda.menu;

public class Option<T> {
    private String option;
    private Action<T> action;

    public Option(String option, Action<T> action) {
        this.option = option;
        this.action = action;
    }

    public String getOption() {
        return this.option;
    }
    
    public void useAction(T element) {
        this.action.run(element);
    }
}
