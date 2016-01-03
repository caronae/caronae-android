package br.ufrj.caronae.models.modelsforjson;

public class RideFeedbackForJson {
    private final int userId;
    private final int rideId;
    private final String feedback;

    public RideFeedbackForJson(int userId, int rideId, String feedback) {
        this.userId = userId;
        this.rideId = rideId;
        this.feedback = feedback;
    }

    public int getUserId() {
        return userId;
    }

    public int getRideId() {
        return rideId;
    }

    public String getFeedback() {
        return feedback;
    }
}
