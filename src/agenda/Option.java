package agenda;

public class Option {
    private String option;
    private OptionAction acao;
    private boolean isVisible;

    public Option(String option, OptionAction action) {
        this.option = option;
        this.acao = action;
        this.isVisible = true;
    }

    public String getOption() {
        return this.option;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }
    
    public void useAction() {
        this.acao.run();
    }
}
