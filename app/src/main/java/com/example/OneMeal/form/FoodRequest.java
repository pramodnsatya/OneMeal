package com.example.OneMeal.form;

public class FoodRequest {

    private String foodrequestid;
    private String sourcelocation;
    private String destinationlocation;
    private String requestdby;
    private String donatedby;

    public String getFoodrequestid() {
        return foodrequestid;
    }

    public void setFoodrequestid(String foodrequestid) {
        this.foodrequestid = foodrequestid;
    }

    public String getSourcelocation() {
        return sourcelocation;
    }

    public void setSourcelocation(String sourcelocation) {
        this.sourcelocation = sourcelocation;
    }

    public String getDestinationlocation() {
        return destinationlocation;
    }

    public void setDestinationlocation(String destinationlocation) {
        this.destinationlocation = destinationlocation;
    }

    public String getRequestdby() {
        return requestdby;
    }

    public void setRequestdby(String requestdby) {
        this.requestdby = requestdby;
    }

    public String getDonatedby() {
        return donatedby;
    }

    public void setDonatedby(String donatedby) {
        this.donatedby = donatedby;
    }
}
