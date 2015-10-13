package br.ufrj.caronae.models;

public class JoinRequestIDsForJson {
    private final int userId;
    private final int rideId;
    private final boolean accepted;

    public JoinRequestIDsForJson(int userId, int rideId, boolean accepted) {
        this.userId = userId;
        this.rideId = rideId;
        this.accepted = accepted;
    }

    public int getUserId() {
        return userId;
    }

    public int getRideId() {
        return rideId;
    }

    public boolean isAccepted() {
        return accepted;
    }
}
