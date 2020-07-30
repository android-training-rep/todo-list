package com.thoughtworks.todo_list.ui.login;

import android.annotation.SuppressLint;
import android.util.Patterns;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.thoughtworks.todo_list.R;
import com.thoughtworks.todo_list.repository.utils.Encryptor;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    private UserRepository userRepository;

    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    void observeLoginFormState(LifecycleOwner lifecycleOwner, Observer<LoginFormState> observer) {
        loginFormState.observe(lifecycleOwner, observer);
    }

    void observeLoginResult(LifecycleOwner lifecycleOwner, Observer<LoginResult> observer) {
        loginResult.observe(lifecycleOwner, observer);
    }

    @SuppressLint("CheckResult")
    public void login(String username, String password) {
        userRepository.findByName(username).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> loginResult.setValue(new LoginResult(R.string.login_failed_username)))
                .subscribe(u -> {
                    if (u == null) {
                        loginResult.postValue(new LoginResult(R.string.login_failed_username));
                        return;
                    }
                    if (u.getPassword().equals(Encryptor.md5(password))) {
                        loginResult.postValue(new LoginResult(new LoggedInUserView(u.getName())));
                        return;
                    }
                    loginResult.postValue(new LoginResult(R.string.login_failed_password));
                });
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.postValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.postValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.postValue(new LoginFormState(true));
        }
    }

    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        int length = username.trim().length();
        if (length < 3 || length > 12) {
            return false;
        }
        // todo 判断username是不是只有数字和字母
        return true;
    }

    private boolean isPasswordValid(String password) {
        if (password == null) {
            return false;
        }
        int length = password.length();
        if(length < 6 || length > 18) {
            return false;
        }
        return true;
    }
}