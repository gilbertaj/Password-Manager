package ssbanerjee.passwordmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener{
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String DELIMITER = "~";
    private static final String ITEM_DELIMITER = "]";

    Crypto crypto = new Crypto();
    Button returnButton;
    Button saveButton;
    EditText oldPassField;
    EditText newPassField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        returnButton = (Button) findViewById(R.id.PassChangeReturn);
        returnButton.setOnClickListener(this);
        saveButton = (Button) findViewById(R.id.PassChangeSave);
        saveButton.setOnClickListener(this);
        oldPassField = (EditText) findViewById(R.id.OldPasswordField);
        newPassField = (EditText) findViewById(R.id.NewPasswordField);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.PassChangeReturn) {
            super.onBackPressed();
            finish();
        }
        if(v.getId() == R.id.PassChangeSave) {
            String password = oldPassField.getText().toString();
            String newPassword = newPassField.getText().toString();
            if(password.length() == 0) {
                Toast.makeText(this, "Please enter in the original password", Toast.LENGTH_SHORT).show();
                return;
            }
            if(newPassword.length() == 0) {
                Toast.makeText(this, "Please enter in a new password", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean pass = false;
            try {
                pass = verifyLogin(password);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(pass) {
                try {
                    passwordChange(newPassword);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Original Password is incorrect", Toast.LENGTH_SHORT).show();
                oldPassField.setText("");
                return;
            }
        }
    }

    private boolean verifyLogin(String password) throws Exception {
        String message = sharedPreferences.getString("verify", "");

        if(message.length() == 0) {
            byte[] salt = crypto.makeSalt();
            crypto.makeKey(password, salt);
            String encrypted = crypto.encrypt("Verify This Password");
            String result = String.format("%s%s%s", Base64.encodeToString(salt, Base64.NO_WRAP),
                    DELIMITER, encrypted);
            this.editor.putString("verify", result);
            editor.apply();
            return true;
        } else {
            String[] array = message.split(DELIMITER);
            byte[] salt = Base64.decode(array[0], Base64.NO_WRAP);
            String test = String.format("%s%s%s", array[1], DELIMITER, array[2]);

            crypto.makeKey(password, salt);
            String temp = crypto.decrypt(test);

            return temp.equals("Verify This Password");
        }
    }

    private void passwordChange(String password) throws Exception{
        ArrayList<passwordItem> list = getInfo();
        makeNewKey(password);
        reEncrpt(list);
        super.onBackPressed();
        finish();
    }

    private void makeNewKey(String password) throws Exception{
        byte[] salt = crypto.makeSalt();
        crypto.makeKey(password, salt);
        String encrypted = crypto.encrypt("Verify This Password");
        String result = String.format("%s%s%s", Base64.encodeToString(salt, Base64.NO_WRAP),
                DELIMITER, encrypted);
        this.editor.putString("verify", result);
        editor.apply();
    }

    private ArrayList getInfo() {
        String rawData = sharedPreferences.getString("Data", "");
        if(rawData.length() == 0) {
            return new ArrayList<>();
        } else {
            ArrayList<passwordItem> list = new ArrayList<>();
            String[] first = rawData.split(ITEM_DELIMITER);

            for(int i = 0; i < first.length; i++) {
                String[] second = first[i].split(DELIMITER);
                String name = second[0];
                String test = String.format("%s%s%s", second[1], DELIMITER, second[2]);
                String password = "";
                try {
                    password = crypto.decrypt(test);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                list.add(new passwordItem(name, password));
            }

            return list;
        }
    }

    private void reEncrpt(ArrayList<passwordItem> list) {
        if(list.size() == 0) {
            return;
        }

        String result = "";
        for(passwordItem e : list) {
            String encrypted = "";
            try {
                encrypted = crypto.encrypt(e.getPassword());
            } catch (Exception f) {
                f.printStackTrace();
            }
            result = String.format("%s%s%s%s%s", result, e.getName(), DELIMITER,
                    encrypted, ITEM_DELIMITER);
        }

        result = result.substring(0, result.length()-1);

        editor.putString("Data", result);
        editor.apply();
    }
}
