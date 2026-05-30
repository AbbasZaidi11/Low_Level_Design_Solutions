package org.example;

public class User {
    int userId;
    String userName;
    String drivingLicenseNo;

    public User(int userId,String userName,String drivingLicenseNo){
        this.userId = userId;
        this.userName = userName;
        this.drivingLicenseNo = drivingLicenseNo;
    }

    public int getUserId(){
        return userId;
    }

    public void setUserId(int userId){
        this.userId = userId;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public void setDrivingLicenseNo(String drivingLicenseNo){
        this.drivingLicenseNo = drivingLicenseNo;
    }
}
