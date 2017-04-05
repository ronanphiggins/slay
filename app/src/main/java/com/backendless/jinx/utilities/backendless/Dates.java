package com.backendless.jinx.utilities.backendless;


import com.backendless.BackendlessUser;

public class Dates {


    private String objectId;
    private BackendlessUser[] owner;



    public Dates()
    {
    }

    public String getObjectId()
    {
        return objectId;
    }

    public void setObjectId (String objectId) { this.objectId = objectId; }

    public BackendlessUser[] getOwner() { return owner; }

    public void setOwner (BackendlessUser[] owner) { this.owner = owner; }



}
