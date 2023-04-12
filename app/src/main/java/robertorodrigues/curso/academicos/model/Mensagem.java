package robertorodrigues.curso.academicos.model;

public class Mensagem {

    private String idUsuario;
    private String nome;
    private String mensagem;
    private String imagem;
    private String arquivo;

    private String horaConversa;
    private String dataConversa;



    public Mensagem() {
        this.setNome("");
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public String getHoraConversa() {
        return horaConversa;
    }

    public void setHoraConversa(String horaConversa) {
        this.horaConversa = horaConversa;
    }

    public String getDataConversa() {
        return dataConversa;
    }

    public void setDataConversa(String dataConversa) {
        this.dataConversa = dataConversa;
    }
}
