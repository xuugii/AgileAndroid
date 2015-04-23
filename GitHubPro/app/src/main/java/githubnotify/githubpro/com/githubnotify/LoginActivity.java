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
public class LoginActivity extends Activity implements OnClickListener{

    Button loginOk;
    EditText loginEmail;
    EditText loginPassword;
    EditText loginToken;
    EditText loginRepo;
    public static String userName;
    public static String password;
    public static String token;
    public static String repoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login_main);
        loginOk = (Button) findViewById(R.id.loginButton);
        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
        loginToken = (EditText) findViewById(R.id.loginToken);
        loginRepo = (EditText) findViewById(R.id.loginRepo);
        loginOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (loginOk.isPressed()){
            if (((loginEmail.getText().length()!=0 && loginPassword.getText().length()!=0)
                    || loginToken.getText().length()!=0)
                    && loginRepo.getText().length()!=0){
                //userName = loginEmail.getText().toString();
                //password = loginPassword.getText().toString();
                //token = loginToken.getText().toString();
                //repoName = loginRepo.getText().toString();
                token = loginToken.getText().toString();
                //TODO fix remove this
                repoName = "AgileAndroid";
                Intent main = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(main);
            } else {
                Toast toast = Toast.makeText(this, "Data is missing!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}
