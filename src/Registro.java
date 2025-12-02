public class Registro {
    private String dominio;
    private String tipo;
    private String ip;

    public Registro(String dominio, String tipo, String valor) {
        this.dominio = dominio;
        this.tipo = tipo;
        this.ip = valor;
    }

    public String getDominio() {
        return dominio;
    }

    public String getTipo() {
        return tipo;
    }

    public String getIp() {
        return ip;
    }

    public String toString() {
        return dominio + " " + tipo + " " + ip;
    }
}

