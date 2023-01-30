package libs.validation;

public enum MessageGDR {
    F00098("F00098", "F"),
    F00200("F00200", "F"),
    F00202("F00202", "F"),
    F00203("F00203", "F"),
    F00220("F00220", "F"),
    F02573("F02573", "F"), // Estimation du bien impossible.
    F02577("F02577", "F"),
    T00001("T00001", "T");

    private final String nomPhysique;
    private final String type;

    MessageGDR(String nomPhysique, String type) {
        this.nomPhysique = nomPhysique;
        this.type = type;
    }

    public String getNomPhysique() {
        return this.nomPhysique;
    }

    public String getType() {
        return this.type;
    }
}

