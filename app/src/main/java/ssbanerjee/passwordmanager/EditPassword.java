package ssbanerjee.passwordmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EditPassword extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String DELIMITER = "~";
    private static final String ITEM_DELIMITER = "]";

    Crypto crypto = new Crypto();
    EditText serviceNameField;
    EditText passwordField;
    EditText lengthField;
    Button randomButton;
    Button returnButton;
    Button deleteButton;
    Button saveButton;
    CheckBox specialCharactersCheck;
    private List<passwordItem> myItems;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        Intent i = getIntent();
        position = i.getIntExtra("position", -1);
        if(position == -1) {
            super.onBackPressed();
            finish();
        }

        serviceNameField = (EditText) findViewById(R.id.EditPasswordServiceText);
        passwordField = (EditText) findViewById(R.id.EditPasswordpasswordText);
        lengthField = (EditText) findViewById(R.id.EditPasswordLength);
        randomButton = (Button) findViewById(R.id.EditPasswordRandomButton);
        randomButton.setOnClickListener(this);
        returnButton = (Button) findViewById(R.id.EditPasswordReturnButton);
        returnButton.setOnClickListener(this);
        deleteButton = (Button) findViewById(R.id.EditPasswordDeleteButton);
        deleteButton.setOnClickListener(this);
        saveButton = (Button) findViewById(R.id.EditPasswordSaveButton);
        saveButton.setOnClickListener(this);
        specialCharactersCheck = (CheckBox) findViewById(R.id.EditPasswordSpecialCharactersCheck);
        specialCharactersCheck.setOnClickListener(this);

        myItems = getInfo();

        serviceNameField.setText(myItems.get(position).getName());
        passwordField.setText(myItems.get(position).getPassword());
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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.EditPasswordReturnButton) {
            super.onBackPressed();
            finish();
        }
        if(v.getId() == R.id.EditPasswordDeleteButton) {
            deleteCalled();
        }
        if(v.getId() == R.id.EditPasswordSaveButton) {
            saveCalled();
        }
        if(v.getId() == R.id.EditPasswordRandomButton) {
            boolean checked = specialCharactersCheck.isChecked();
            randomPassword(checked);
        }
    }

    private void deleteCalled() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure that you want to delete this password?");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem();
                        dialog.cancel();

                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void deleteItem() {
        myItems.remove(position);

        addData();

        super.onBackPressed();
        finish();
    }

    private void saveCalled() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure that you want to save this modification?");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveItem();
                        dialog.cancel();

                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void saveItem() {
        String name = serviceNameField.getText().toString();
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
            serviceNameField.setText(myItems.get(position).getName());
            return;
        }

        for(int i = 0; i < myItems.size(); i++) {
            if(myItems.get(i).getName().equals(name) && myItems.get(i).getPassword().equals(password) && i != position) {
                Toast.makeText(this, "You already have this Service Name and Password stored",
                        Toast.LENGTH_SHORT).show();
                passwordField.setText(myItems.get(position).getPassword());
                serviceNameField.setText(myItems.get(position).getName());
                return;
            }
        }

        myItems.get(position).setName(serviceNameField.getText().toString());
        myItems.get(position).setPassword(passwordField.getText().toString());

        addData();

        super.onBackPressed();
        finish();
    }

    private void addData() {
        if(myItems.size() == 0) {
            editor.putString("Data", "");
            editor.apply();
            return;
        }

        String result = "";
        for(passwordItem e : myItems) {
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

    /*
    ⊂_ヽ
    　 ＼＼ Λ＿Λ
    　　 ＼(　ˇωˇ)　
    　　　 >　⌒ヽ
    　　　/ 　 へ＼
    　　 /　　/　＼＼
    　　 ﾚ　ノ　　 ヽ_つ
    　　/　/
    　 /　/|
    　(　(ヽ
    　|　|、＼
    　| 丿 ＼ ⌒)
    　| |　　) /
    `ノ )　　Lﾉ
    (_／
     */
    private void randomPassword(boolean checked) {
        Random random = new Random();
        int length;
        String characters;
        if(checked) {
            characters = "0123456789abcdefghijklmnopqrstuvwxyz" +
                    "ABCDEFGHIJKLMNOPQRSTUVWXYZ !#$%&'()*+,-./:;<=>?@[]^_`{|}~";
            characters = characters + '"';
        } else {
            characters = "0123456789abcdefghijklmnopqrstuvwxyz" +
                    "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        }
        if(lengthField.getText().toString().length() == 0) {
            length = random.nextInt(11) + 10;
        } else {
            try {
                length = Integer.parseInt(lengthField.getText().toString());
            } catch (Exception e) {
                Toast.makeText(this, "Please enter in a valid length", Toast.LENGTH_SHORT).show();
                lengthField.setText("");
                return;
            }
        }

        String result = "";
        for(int i = 0; i < length; i++) {
            int next = random.nextInt(characters.length());
            result = result + characters.charAt(next);
        }
        passwordField.setText(result);
    }
}
