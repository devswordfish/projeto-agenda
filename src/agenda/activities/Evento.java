package agenda.activities;

import java.util.Date;

public class Evento extends AtividadeAgenda {
    private String descricao;

    public Evento(String nome, String descricao, Date dataInicio, Date dataTermino) {
        super(nome, dataInicio, dataTermino);
        this.descricao = descricao;
    }
}
