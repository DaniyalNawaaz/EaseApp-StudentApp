package ModelClasses;

/**
 * Created by Daniyal Nawaz on 2/15/2016.
 */
public class Student {
    private String regid;
    private String firstname;
    private String lastname;
    private String email;
    private String contact;
    private String password;
    private String accountStatus;
    private String gcm_id;

    public Student(){}

    public Student(String regid, String firstname, String lastname, String email,
                   String contact, String password)
    {
        this.regid = regid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.contact = contact;
        this.password = password;
        //this.accountStatus = accountStatus;
    }

    public Student(String regid, String firstname, String lastname, String email,
                   String contact, String password,String accountStatus,String gcm_id)
    {
        this.regid = regid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.contact = contact;
        this.password = password;
        this.accountStatus = accountStatus;
        this.gcm_id = gcm_id;
    }

    public String getRegid() {
        return regid;
    }

    public void setRegid(String regid) {
        this.regid = regid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getGcm_id() {
        return gcm_id;
    }

    public void setGcm_id(String gcm_id) {
        this.gcm_id = gcm_id;
    }
}
