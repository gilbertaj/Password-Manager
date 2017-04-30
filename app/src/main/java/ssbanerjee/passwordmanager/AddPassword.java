package ssbanerjee.passwordmanager;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddPassword extends AppCompatActivity implements View.OnClickListener{
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String DELIMITER = "~";
    private static final String ITEM_DELIMITER = "]";

    Crypto crypto = new Crypto();
    Button backButton;
    Button saveButton;
    EditText nameField;
    EditText passwordField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        backButton = (Button) findViewById(R.id.returnButton);
        backButton.setOnClickListener(this);
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        nameField = (EditText) findViewById(R.id.nameEdit);
        passwordField = (EditText) findViewById(R.id.passEdit);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.returnButton) {
            super.onBackPressed();
            finish();
        }
        if(v.getId() == R.id.saveButton) {
            String name = nameField.getText().toString();
            String password = passwordField.getText().toString();
            if(name.length() == 0 && password.length() == 0) {
                Toast.makeText(this, "Please enter in a Password and a Service Name",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if(name.length() == 0) {
                Toast.makeText(this, "Please enter in a Service Name",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if(password.length() == 0) {
                Toast.makeText(this, "Please enter in a Password",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if(name.contains("~")) {
                Toast.makeText(this, "Service Name can not use '~' character",
                        Toast.LENGTH_SHORT).show();
                nameField.setText("");
                return;
            }

            addData(name, password);
            super.onBackPressed();
            finish();
        }
    }

    private void addData(String name, String password) {
        String rawData = sharedPreferences.getString("Data", "");
        if(rawData.length() == 0) {
            String encrypted = "";
            try {
                encrypted = crypto.encrypt(password);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String result = String.format("%s%s%s", name, DELIMITER, encrypted);
            editor.putString("Data", result);
            editor.apply();
            return;
        } else {
            String encrypted = "";
            try {
                encrypted = crypto.encrypt(password);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String result = String.format("%s%s%s%s%s", rawData, ITEM_DELIMITER, name, DELIMITER, encrypted);
            editor.putString("Data", result);
            editor.apply();
            return;
        }
    }
}
