package org.ejmc.android.simplechat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Main activity.
 * 
 * Shows login config.
 * 
 * @author startic
 * 
 */
public class LoginActivity extends Activity {

    private String user, pwd;

    private EditText editUser, pass;
    private Button login;
    private Intent nextActivity;
    private Context context;
    private TextView error;
    private String info;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        editUser = (EditText) findViewById(R.id.editUsuario);
        pass = (EditText) findViewById(R.id.editPwd);
        login = (Button) findViewById(R.id.login);
        error = (TextView) findViewById(R.id.textView3);

        info="Error: ";

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user = editUser.getText().toString();
                pwd = pass.getText().toString();

                if( checkPass(user, pwd) ){
                    nextActivity = new Intent(context, ChatActivity.class);
                    nextActivity.putExtra("nick", user);
                    startActivity(nextActivity);
                    finish();
                }
                else{
                    info+="\nIncorrect signing.";
                    error.setText(info);
                    error.setTextColor(Color.RED);
                }
                info = "Error: ";
            }
        });

    }

    public boolean someCharacter( String cadena ){
        for(int i=0; i<cadena.length()-1; i++){
            if(cadena.charAt(i)!=' ' && cadena.charAt(i)!='\n' && cadena.charAt(i)!='\t' )
                return true;
        }
        return false;
    }

    public boolean checkPass(String user, String pwd ){  //example. It is mandatory to fill both fields
        if( user.equals("") || !someCharacter(user)){ info+="Empty user. "; return false; }
        if( pass.equals("") || !someCharacter(pwd)){ info+="Empty pass. "; return false; }
        /*if( user.equals("bq") && pwd.equals("bq")) return true;
        else return false;*/
        return true;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
