package org.tpaagp.ZimbraClient;

public class ContactData {
    private  String FirstName;
    private  String LastName;
    private  String CellPhone;
    private  String OfficePhone;
    private  String HomePhone;
    private  String Email;
    private String Adress;
    private  String UserId;
    public String getUserId() {
        return UserId;
    }
    public String getFristName() {
        return FirstName;
    }
    public String getLastName() {
        return LastName;
    }
    public String getCellPhone() {
        return CellPhone;
    }
    public String getOfficePhone() {
        return OfficePhone;
    }
    public String getHomePhone() {
        return HomePhone;
    }
    public String getEmail() {
        return Email;
    }
    public String getAdress() {
        return Adress;
    }
    public void setUserId(String mUserId) {
        UserId=mUserId;
    }
    public void setFirstName(String mFirstName) {
        FirstName=mFirstName;
    }
    public void setLastName(String mLastName) {
         LastName=mLastName;
    }
    public void setCellPhone(String mCellPhone) {
        CellPhone=mCellPhone;
    }
    public void setOfficePhone(String mOfficePhone) {
        OfficePhone=mOfficePhone;
    }
    public void setHomePhone(String mHomePhone) {
        HomePhone=mHomePhone;
    }
    public void setEmail(String mEmail) {
        Email=mEmail;
    }
    public void setAdress(String mAdress) {
        Adress=mAdress;
    }
    public ContactData(String mUserid,String mFirstName,String mLastName, String mHomePhone,String mCellPhone, String mOfficePhone, String mEmail, String mAdress){
    	UserId=mUserid;
    	FirstName=mFirstName;
    	LastName=mLastName;
    	CellPhone=mCellPhone;
    	OfficePhone=mOfficePhone;
    	HomePhone=mHomePhone;
    	Email=mEmail;
    	Adress=mAdress;
    }

}
