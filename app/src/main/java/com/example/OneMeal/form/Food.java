package com.example.OneMeal.form;

public class Food {

    private String id;
    private String name;
    private String image;
    private String preparedtime;
    private String geolocation;
    private String membercount;
    private String status;
    private String postedby;
    private String location;
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getMembercount() {
        return membercount;
    }
    public void setMembercount(String membercount) {
        this.membercount = membercount;
    }
    public String getPostedby() {
        return postedby;
    }

    public void setPostedby(String postedby) {
        this.postedby = postedby;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() { return name;  }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPreparedtime() {
        return preparedtime;
    }

    public void setPreparedtime(String preparedtime) {
        this.preparedtime = preparedtime;
    }

    public String getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(String geolocation) {
        this.geolocation = geolocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
