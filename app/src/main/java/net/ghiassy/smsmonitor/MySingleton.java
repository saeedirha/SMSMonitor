package net.ghiassy.smsmonitor;

public class MySingleton {

    private String email;
    private static MySingleton instance = null;

    private MySingleton(){
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static MySingleton getInstance()
    {
        if(instance == null)
            instance = new MySingleton();
        return instance;
    }
}
