package com.tutorial.mvp.masterdetail.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.tutorial.mvp.masterdetail.master.MasterActivity;
import com.tutorial.mvp.masterdetail.R;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A login screen that offers login via username/password.
 *
 */
public class LoginActivity extends AppCompatActivity implements ILoginView{

    // UI references.
    @BindView(R.id.username) EditText mUsernameView;
    @BindView(R.id.password) EditText mPasswordView;
    @BindView(R.id.btn_sign_in)
    CircularProgressButton signInBtn;

    private LoginPresenterImpl mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Bind using butterknife
        ButterKnife.bind(this);

        // full screen login ui
        getSupportActionBar().hide();

        // initiate Presenter
        mPresenter = new LoginPresenterImpl(this, new LoginInteractorImpl());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Dispose loading button
        signInBtn.dispose();
    }

    @OnClick(R.id.btn_sign_in)
    void signIn() {
        // Reset errors and hide keyboard
        mUsernameView.setError(null);
        mPasswordView.setError(null);
        hideKeyboard();

        // Pass login request to Presenter
        mPresenter.doLogin(mUsernameView.getText().toString(),
                mPasswordView.getText().toString());
    }

    private void hideKeyboard() {
        try {
            //hide keyboard
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (NullPointerException e) {
            e.printStackTrace();
            //do nothing
        }
    }

    @Override
    public void showLoading() {
        // animate using loading-button lib
        signInBtn.startAnimation();
    }

    @Override
    public void hideLoading(boolean isSuccess) {
        if(isSuccess) {
            // Show done animation
            signInBtn.doneLoadingAnimation(ContextCompat.getColor(this, R.color.colorAccent),
                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp));
        } else {
            // Revert animation
            signInBtn.revertAnimation();
        }
    }

    @Override
    public void setPasswordError(int stringID) {
        mPasswordView.setError(getString(stringID));
        mPasswordView.requestFocus();
    }

    @Override
    public void setEmailError(int stringID) {
        mUsernameView.setError(getString(stringID));
        mUsernameView.requestFocus();
    }

    @Override
    public void loginSuccess() {
        // launch master activity
        Intent masterActivityIntent = new Intent(this, MasterActivity.class);
        startActivity(masterActivityIntent);
        finish();
    }

    @Override
    public void loginFailed(int errorCode) {
        Snackbar.make(findViewById(android.R.id.content), "Login failed!!", Snackbar.LENGTH_LONG).show();
    }
}