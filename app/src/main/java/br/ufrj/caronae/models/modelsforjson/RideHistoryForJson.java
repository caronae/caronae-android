package br.ufrj.caronae.models.modelsforjson;

import android.os.Parcel;
import android.os.Parcelable;

public class RideHistoryForJson extends RideForJson {
    private String feedback;

    public RideHistoryForJson(Parcel in) {
        super(in);
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    //useless, only to avoid compilation error
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public RideForJson createFromParcel(Parcel in) {
            return new RideForJson(in);
        }

        public RideForJson[] newArray(int size) {
            return new RideForJson[size];
        }
    };
}
