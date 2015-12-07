package br.ufrj.caronae.models.modelsforjson;

public class HistoryRideCountForJson {
    private int offeredCount, takenCount;

    public int getOfferedCount() {
        return offeredCount;
    }

    public void setOfferedCount(int offeredCount) {
        this.offeredCount = offeredCount;
    }

    public int getTakenCount() {
        return takenCount;
    }

    public void setTakenCount(int takenCount) {
        this.takenCount = takenCount;
    }
}
