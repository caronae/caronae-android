package br.ufrj.caronae.models;

import com.google.gson.annotations.SerializedName;

public class RideRountine {

        @SerializedName("myzone")
        protected String zone;
        protected String neighborhood;
        protected String place;
        protected String route;
        @SerializedName("mydate")
        protected String date;
        protected String slots;
        @SerializedName("mytime")
        protected String time;
        protected String hub;
        protected String description;
        @SerializedName("week_days")
        protected String weekDays;
        @SerializedName("repeats_until")
        protected RepeatisUntil repeatsUntil;
        protected boolean going;
        @SerializedName("id")
        protected int dbId;
        @SerializedName("routine_id")
        protected String routineId;
        protected String campus;

        public RideRountine(String zone, String neighborhood, String place, String route, String date, String time, String slots, String hub, String campus, String description, boolean going, String weekDays, RepeatisUntil repeatsUntil) {
                this.zone = zone;
                this.neighborhood = neighborhood;
                this.place = place;
                this.route = route;
                this.date = date;
                this.time = time;
                this.slots = slots;
                this.hub = hub;
                this.description = description;
                this.going = going;
                this.weekDays = weekDays;
                this.repeatsUntil = repeatsUntil;
                this.campus = campus;
        }

        public String getZone() {
                return zone;
        }

        public String getNeighborhood() {
                return neighborhood;
        }

        public String getPlace() {
                return place;
        }

        public String getRoute() {
                return route;
        }

        public String getDate() {
                return date;
        }

        public String getSlots() {
                return slots;
        }

        public String getTime() {
                return time;
        }

        public String getHub() {
                return hub;
        }

        public String getDescription() {
                return description;
        }

        public String getWeekDays() {
                return weekDays;
        }

        public RepeatisUntil getRepeatsUntil() {
                return repeatsUntil;
        }

        public boolean isGoing() {
                return going;
        }

        public int getDbId() {
                return dbId;
        }

        public String getRoutineId() {
                return routineId;
        }

        public String getCampus() {
                return campus;
        }

}
