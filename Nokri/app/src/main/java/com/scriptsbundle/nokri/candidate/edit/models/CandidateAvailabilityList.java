package com.scriptsbundle.nokri.candidate.edit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CandidateAvailabilityList {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose(deserialize =  false)
    private Data data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{

        @SerializedName("zones")
        @Expose
        private List<Zone> zones = null;


        @SerializedName("days")
        @Expose
        private List<Day> days = null;


        @SerializedName("hours_type")
        @Expose
        private String hoursType;

        @SerializedName("extra")
        @Expose
        private Extra extra;

        public Extra getExtra() {
            return extra;
        }

        public void setExtra(Extra extra) {
            this.extra = extra;
        }

        public List<Zone> getZones() {
            return zones;
        }

        public void setZones(List<Zone> zones) {
            this.zones = zones;
        }

        public List<Day> getDays() {
            return days;
        }

        public void setDays(List<Day> days) {
            this.days = days;
        }

        public String getHoursType() {
            return hoursType;
        }

        public void setHoursType(String hoursType) {
            this.hoursType = hoursType;
        }
    }


    public class Zone{
        public String key;
        public String value;
        public boolean selected;
        public boolean has_child;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean isHas_child() {
            return has_child;
        }

        public void setHas_child(boolean has_child) {
            this.has_child = has_child;
        }
    }


    public static class Day{
        public String day_name;
        public String start_time;
        public String end_time;
        public boolean closed;

        public String getDay_name() {
            return day_name;
        }

        public void setDay_name(String day_name) {
            this.day_name = day_name;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public boolean getClosed() {
            return closed;
        }

        public void setClosed(boolean closed) {
            this.closed = closed;
        }
    }
    public class Extra{
        @SerializedName("cand_availability")
        @Expose
        public String cand_availability;
        @SerializedName("selected_hours")
        @Expose
        public String selected_hours;
        @SerializedName("time_zone")
        @Expose
        public String time_zone;
        @SerializedName("submit")
        @Expose
        public String submit;
        @SerializedName("to")
        @Expose
        public String to;
        @SerializedName("open")
        @Expose
        public String open;
        @SerializedName("selective_hours")
        @Expose
        public String selective_hours;
        @SerializedName("not_available")
        @Expose
        public String not_available;


        public String getNot_available() {
            return not_available;
        }

        public void setNot_available(String not_available) {
            this.not_available = not_available;
        }

        public String getCand_availability() {
            return cand_availability;
        }

        public void setCand_availability(String cand_availability) {
            this.cand_availability = cand_availability;
        }

        public String getSelected_hours() {
            return selected_hours;
        }

        public void setSelected_hours(String selected_hours) {
            this.selected_hours = selected_hours;
        }

        public String getTime_zone() {
            return time_zone;
        }

        public void setTime_zone(String time_zone) {
            this.time_zone = time_zone;
        }

        public String getSubmit() {
            return submit;
        }

        public void setSubmit(String submit) {
            this.submit = submit;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getOpen() {
            return open;
        }

        public void setOpen(String open) {
            this.open = open;
        }

        public String getSelective_hours() {
            return selective_hours;
        }

        public void setSelective_hours(String selective_hours) {
            this.selective_hours = selective_hours;
        }
    }

}
