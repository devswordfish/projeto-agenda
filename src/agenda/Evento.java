package agenda;

import java.util.Date;

public class Evento extends AtividadeAgenda {
    private String descricao;

    public Evento(long id, String nome, String descricao, Date dataInicio, Date dataTermino) {
        super(id, nome, dataInicio, dataTermino);
        this.descricao = descricao;
    }
}
