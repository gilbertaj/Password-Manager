package ssbanerjee.passwordmanager;

import android.content.DialogInterface;
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

import java.security.MessageDigest;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String DELIMITER = "~";

    Crypto crypto = new Crypto();
    Button loginButton;
    EditText passwordField;
    TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent i = getIntent();
        if(i.getBooleanExtra("EXIT", false)){
            finish();
        }

        title = (TextView) findViewById(R.id.MainScreenTitle);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        String message = sharedPreferences.getString("verify", "");
        if(message.length() == 0) {
            title.setText("Please choose a secure password");
        }


        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
        passwordField = (EditText) findViewById(R.id.passwordEdit);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.loginButton) {
            String password = passwordField.getText().toString();
            if(password.length() == 0) {
                Toast.makeText(this, "Please enter in a password", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean pass = false;
            try {
                pass = verifyLogin(password);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(pass) {
                Intent i = new Intent(LoginActivity.this, PasswordListActivity.class);
                startActivity(i);
            } else {
                Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                passwordField.setText("");
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
}
