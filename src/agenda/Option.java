package agenda;

public class Option {
    private String option;
    private OptionAction acao;

    public Option(String option, OptionAction action) {
        this.option = option;
        this.acao = action;
    }

    public String getOption() {
        return this.option;
    }

    public void useAction() {
        this.acao.run();
    }
}
