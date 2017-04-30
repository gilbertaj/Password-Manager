package ssbanerjee.passwordmanager;


/**
 * Created by Gilbert on 4/30/2017.
 */

public class passwordItem {
    private String name;
    private String password;


    public passwordItem(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
