package ModelClasses;

/**
 * Created by Daniyal Nawaz on 3/20/2016.
 */
public class Notification {
    private int notifId;
    private String message;
    private String dateTime;

    public Notification(){}

    public Notification(int notifId,String message,String dateTime){
        this.notifId = notifId;
        this.message = message;
        this.dateTime = dateTime;
    }

    public int getNotifId() {
        return notifId;
    }

    public void setNotifId(int notifId) {
        this.notifId = notifId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
