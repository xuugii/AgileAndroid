package githubnotify.githubpro.com.githubnotify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsoluteLayout;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


/**
 * Created by Uguudei on 2015-04-22.
 */
public class LoginActivity implements IFragmentView,OnClickListener{

    Button loginOk;
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
        loginOk.setOnClickListener((MainActivity)activity);
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
        return true;
    }

    public static void logout(){
        userName = "";
        password = "";
        token = "";
        repoName = "";
        loggedIn = false;
    }

    @Override
    public View getView() {
        return loginView;
    }
}
