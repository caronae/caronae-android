package br.ufrj.caronae.models.modelsforjson;

public class JoinRequestIDsForJson {
    private final int userId;
    private final boolean accepted;

    public JoinRequestIDsForJson(int userId, boolean accepted) {
        this.userId = userId;
        this.accepted = accepted;
    }
}
