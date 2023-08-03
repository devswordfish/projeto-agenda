package agenda;

import java.util.Date;

public class AtividadeAgenda {
    private long id;
    private String nome;
    private Date dataInicio;
    private Date dataTermino;

    public AtividadeAgenda(long id, String nome, Date dataInicio, Date dataTermino) {
        this.id = id;
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataTermino = dataTermino;
    }

    // getters e setters

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(Date dataTermino) {
        this.dataTermino = dataTermino;
    }
}
