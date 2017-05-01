package ssbanerjee.passwordmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PasswordListActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String DELIMITER = "~";
    private static final String ITEM_DELIMITER = "]";

    Crypto crypto = new Crypto();
    Button addButton;
    Button clearAll;
    Button changeButton;
    ListView passwordList;
    private List<passwordItem> myItems;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_list);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        addButton = (Button) findViewById(R.id.addPasswordButton);
        addButton.setOnClickListener(this);
        clearAll = (Button) findViewById(R.id.clearAllPasswordsButton);
        clearAll.setOnClickListener(this);
        changeButton = (Button) findViewById(R.id.changePasswordButton);
        changeButton.setOnClickListener(this);
        passwordList = (ListView) findViewById(R.id.passwordList);
        passwordList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longClick(position);
                return true;
            }
        });
        updateList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateList();
    }

    private void updateList() {
        myItems = getInfo();

        passwordListAdapter adapter;
        adapter = new passwordListAdapter(getApplicationContext(), myItems);
        passwordList.setAdapter(adapter);

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
        if(v.getId() == R.id.addPasswordButton) {
            Intent i = new Intent(PasswordListActivity.this, AddPassword.class);
            startActivity(i);
        }

        if(v.getId() == R.id.changePasswordButton) {
            Intent i = new Intent(PasswordListActivity.this, ChangePassword.class);
            startActivity(i);
        }

        if(v.getId() == R.id.clearAllPasswordsButton) {
            if(myItems.size() == 0) {
                Toast.makeText(this, "There is nothing to delete", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure that you want to delete ALL passwords?");
            builder.setCancelable(true);

            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clearAll();
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

    }

    private void clearAll(){
        editor.putString("Data", "");
        editor.apply();

        updateList();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure that you want to exit this app?");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exit();
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

    private void exit() {
        Intent i = new Intent(PasswordListActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("EXIT", true);
        startActivity(i);
    }



    private void longClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure that you want to delete this password?");
        builder.setCancelable(true);
        final int pos = position;

        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem(pos);
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

    private void deleteItem(int position) {

        myItems.remove(position);

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


        updateList();
    }
}
