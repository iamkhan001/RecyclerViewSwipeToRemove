package com.nstudio.shaadimatches.models;

import com.google.gson.annotations.SerializedName;

public class ProfileModel {


    @SerializedName("gender")
    private String gender;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("cell")
    private String cell;

    @SerializedName("nat")
    private String nat;

    @SerializedName("name")
    private Name name;

    @SerializedName("location")
    private Location location;

    @SerializedName("dob")
    private Age dob;

    @SerializedName("registered")
    private Age registered;

    @SerializedName("picture")
    private Picture picture;

    public ProfileModel(String gender, String email, String phone, String cell, String nat, Name name, Location location, Age dob, Age registered, Picture picture) {
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.cell = cell;
        this.nat = nat;
        this.name = name;
        this.location = location;
        this.dob = dob;
        this.registered = registered;
        this.picture = picture;
    }


    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCell() {
        return cell;
    }

    public String getNat() {
        return nat;
    }

    public String getName() {
        if (name!=null){
            return name.getName();
        }
        return "";
    }

    public static String getDescription(Age dob, Location location){
        StringBuilder description = new StringBuilder();
        if (dob!=null){
            description.append(dob.getAge()).append(" yrs");
        }
        if (location!=null){
            description.append(", ").append(location.getStreet()).append(", ").append(location.getCity())
                    .append(", ").append(location.getState());
        }

        return description.toString();
    }

    public Location getLocation() {
        return location;
    }


    public Age getDob() {
        return dob;
    }

    public String getRegisteredDate() {
        if (registered!=null){
            return registered.getDate();
        }
        return "";
    }

    public Picture getPicture() {
        return picture;
    }

    private class Name{

        @SerializedName("title")
        private String title;
        @SerializedName("first")
        private String first;
        @SerializedName("last")
        private String last;

        public Name(String title, String first, String last) {
            this.title = title;
            this.first = first;
            this.last = last;
        }

        private String getName() {
            return title+" "+first+" "+last;
        }

    }

    private class Location{

        @SerializedName("street")
        private String street;
        @SerializedName("city")
        private String city;
        @SerializedName("state")
        private String state;
        @SerializedName("postcode")
        private String postcode;

        @SerializedName("timezone")
        private TimeZone timeZone;

        public Location(String street, String city, String state, String postcode, TimeZone timeZone) {
            this.street = street;
            this.city = city;
            this.state = state;
            this.postcode = postcode;
            this.timeZone = timeZone;
        }

        public TimeZone getTimeZone() {
            return timeZone;
        }

        private String getStreet() {
            return street;
        }

        private String getCity() {
            return city;
        }

        private String getState() {
            return state;
        }

        public String getPostcode() {
            return postcode;
        }
    }

    private class TimeZone{

        @SerializedName("offset")
        private String offset;
        @SerializedName("description")
        private String description;

        public TimeZone(String offset, String description) {
            this.offset = offset;
            this.description = description;
        }

        public String getOffset() {
            return offset;
        }

        public String getDescription() {
            return description;
        }
    }


    private class Age{

        @SerializedName("date")
        private String date;


        @SerializedName("age")
        private String age;

        public Age(String date, String age) {
            this.date = date;
            this.age = age;
        }

        public String getDate() {
            return date;
        }

        private String getAge() {
            return age;
        }
    }

    public class Picture{
        @SerializedName("large")
        private String large;
        @SerializedName("medium")
        private String medium;
        @SerializedName("thumbnail")
        private String thumbnail;

        public Picture(String large, String medium, String thumbnail) {
            this.large = large;
            this.medium = medium;
            this.thumbnail = thumbnail;
        }

        public String getLarge() {
            return large;
        }

        public String getMedium() {
            return medium;
        }

        public String getThumbnail() {
            return thumbnail;
        }
    }

}
