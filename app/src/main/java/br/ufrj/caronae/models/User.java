package br.ufrj.caronae.models;

import com.google.gson.annotations.SerializedName;

public class User {
    private String name;
    private String profile;
    private String course;
    @SerializedName("phone_number")
    private String phoneNumber;
    private String email;
    @SerializedName("car_owner")
    private boolean carOwner;
    @SerializedName("car_model")
    private String carModel;
    @SerializedName("car_color")
    private String carColor;
    @SerializedName("car_plate")
    private String carPlate;
    @SerializedName("id")
    private int dbId;
    @SerializedName("created_at")
    private String createdAt;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isCarOwner() {
        return carOwner;
    }

    public void setCarOwner(boolean carOwner) {
        this.carOwner = carOwner;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public String getCarPlate() {
        return carPlate;
    }

    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean sameFieldsState(User user) {
        if (isCarOwner() != user.isCarOwner()) return false;
        if (!getName().equals(user.getName())) return false;
        if (getProfile() != null ? !getProfile().equals(user.getProfile()) : user.getProfile() != null)
            return false;
        if (getCourse() != null ? !getCourse().equals(user.getCourse()) : user.getCourse() != null)
            return false;
        if (getPhoneNumber() != null ? !getPhoneNumber().equals(user.getPhoneNumber()) : user.getPhoneNumber() != null)
            return false;
        if (getEmail() != null ? !getEmail().equals(user.getEmail()) : user.getEmail() != null)
            return false;
        if (getCarModel() != null ? !getCarModel().equals(user.getCarModel()) : user.getCarModel() != null)
            return false;
        //noinspection SimplifiableIfStatement
        if (getCarColor() != null ? !getCarColor().equals(user.getCarColor()) : user.getCarColor() != null)
            return false;
        return !(getCarPlate() != null ? !getCarPlate().equals(user.getCarPlate()) : user.getCarPlate() != null);
    }

    public void setUser(User editedUser) {
        setName(editedUser.getName());
        setProfile(editedUser.getProfile());
        setCourse(editedUser.getCourse());
        setPhoneNumber(editedUser.getPhoneNumber());
        setEmail(editedUser.getEmail());
        setCarOwner(editedUser.isCarOwner());
        setCarModel(editedUser.getCarModel());
        setCarModel(editedUser.getCarModel());
        setCarColor(editedUser.getCarColor());
    }
}
