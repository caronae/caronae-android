package br.ufrj.caronae;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "User")
public class User extends Model {
    @Column
    private String name;
    @Column
    private String profile;
    @Column
    private String course;
    @Column
    private String unit;
    @Column
    private String zone;
    @Column
    private String neighborhood;
    @Column
    private boolean carOwner;
    @Column
    private String carModel;
    @Column
    private String carColor;
    @Column
    private String carPlate;

    public User() {
        super();
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
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

    public boolean equals(User user) {
        if (isCarOwner() != user.isCarOwner()) return false;
        if (!getName().equals(user.getName())) return false;
        if (getProfile() != null ? !getProfile().equals(user.getProfile()) : user.getProfile() != null)
            return false;
        if (getCourse() != null ? !getCourse().equals(user.getCourse()) : user.getCourse() != null)
            return false;
        if (getUnit() != null ? !getUnit().equals(user.getUnit()) : user.getUnit() != null)
            return false;
        if (getZone() != null ? !getZone().equals(user.getZone()) : user.getZone() != null)
            return false;
        if (getNeighborhood() != null ? !getNeighborhood().equals(user.getNeighborhood()) : user.getNeighborhood() != null)
            return false;
        if (getCarModel() != null ? !getCarModel().equals(user.getCarModel()) : user.getCarModel() != null)
            return false;
        if (getCarColor() != null ? !getCarColor().equals(user.getCarColor()) : user.getCarColor() != null)
            return false;
        return !(getCarPlate() != null ? !getCarPlate().equals(user.getCarPlate()) : user.getCarPlate() != null);
    }

    public void setUser(User editedUser) {
        setName(editedUser.getName());
        setProfile(editedUser.getProfile());
        setCourse(editedUser.getCourse());
        setUnit(editedUser.getUnit());
        setZone(editedUser.getZone());
        setNeighborhood(editedUser.getNeighborhood());
        setCarOwner(editedUser.isCarOwner());
        setCarModel(editedUser.getCarModel());
        setCarModel(editedUser.getCarModel());
        setCarColor(editedUser.getCarColor());
    }

    /*@Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + (getProfile() != null ? getProfile().hashCode() : 0);
        result = 31 * result + (getCourse() != null ? getCourse().hashCode() : 0);
        result = 31 * result + (getUnit() != null ? getUnit().hashCode() : 0);
        result = 31 * result + (getZone() != null ? getZone().hashCode() : 0);
        result = 31 * result + (getNeighborhood() != null ? getNeighborhood().hashCode() : 0);
        result = 31 * result + (isCarOwner() ? 1 : 0);
        result = 31 * result + (getCarModel() != null ? getCarModel().hashCode() : 0);
        result = 31 * result + (getCarColor() != null ? getCarColor().hashCode() : 0);
        result = 31 * result + (getCarPlate() != null ? getCarPlate().hashCode() : 0);
        return result;
    }*/
}
