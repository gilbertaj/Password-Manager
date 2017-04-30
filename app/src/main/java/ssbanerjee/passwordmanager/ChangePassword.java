package ssbanerjee.passwordmanager;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener{
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String DELIMITER = "~";

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

    }
}
