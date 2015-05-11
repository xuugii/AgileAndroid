package com.agilegithub.main;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Created by Uguudei on 2015-04-22.
 */
public class LoginActivity implements IFragmentView,OnClickListener {

    public static Button loginOk;
    EditText loginEmail;
    EditText loginPassword;
    EditText loginToken;
    EditText loginRepo;
    public static String userName;
    public static String password;
    public static String token;
    public static String repoName;
    public static boolean loggedIn = false;
    private Activity activity;
    private View loginView;

    protected LoginActivity(Activity activity, View loginView) {
        this.activity = activity;
        this.loginView = loginView;
        loginOk = (Button) loginView.findViewById(R.id.loginButton);
        loginEmail = (EditText) loginView.findViewById(R.id.loginEmail);
        loginPassword = (EditText) loginView.findViewById(R.id.loginPassword);
        loginToken = (EditText) loginView.findViewById(R.id.loginToken);
        loginRepo = (EditText) loginView.findViewById(R.id.loginRepo);
        loginOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (loginOk.isPressed()){
            if (((loginEmail.getText().length()!=0 && loginPassword.getText().length()!=0)
                    || loginToken.getText().length()!=0)
                    && loginRepo.getText().length()!=0){
                String userName = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();
                String token = loginToken.getText().toString();
                String repoName = loginRepo.getText().toString();
                LoginActivity.login(userName, password, token, repoName);
                ((MainActivity)activity).displayView(2);
            } else {
                Toast toast = Toast.makeText(activity, "Data is missing!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    public static boolean login(String userNameT, String passwordT, String tokenT, String repoNameT){
        //TODO: add validation
        userName = userNameT;
        password = passwordT;
        token = tokenT;
        repoName = repoNameT;
        loggedIn = true;
        MainActivity.state = MainActivity.State.loggedIn;
        return true;
    }

    public static void logout(){
        userName = "";
        password = "";
        token = "";
        repoName = "";
        loggedIn = false;
        MainActivity.state = MainActivity.State.logout;
    }

    @Override
    public View getView() {
        return loginView;
    }
}
