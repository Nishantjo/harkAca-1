package com.accademy.harvin.harvinacademy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.accademy.harvin.harvinacademy.model.UserTest;
import com.accademy.harvin.harvinacademy.network.RetrofitInterface;
import com.accademy.harvin.harvinacademy.utils.Constants;
import com.accademy.harvin.harvinacademy.utils.Validation;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class Login_Main extends AppCompatActivity {
    private static final String TAG = "RxAndroidSamples";

    private Observable<UserTest> call=null;


    Button b1;
    TextView username;
    TextView password;
    boolean done = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__main);
        b1 = (Button) findViewById(R.id.register);
        username = (TextView) findViewById(R.id.et_username);
        password = (TextView) findViewById(R.id.et_password);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("button clicked", "OK");

                login();





            }
        });

    }

    private void login() {
        int err=0;
        if(!Validation.validateEmail(username.getText().toString())){
            showSnackBarMessage("Please try again..");
            err=1;
        }
        if(!Validation.validataFields(password.getText().toString())){
            showSnackBarMessage("Something went wrong..");
            err=1;
        }
        if(err==0)
        sendRequest(new UserTest(username.getText().toString(), password.getText().toString()));

    }

    private void sendRequest(UserTest user) {
        HttpLoggingInterceptor loggin= new HttpLoggingInterceptor();
        loggin.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        RetrofitInterface client = retrofit.create(RetrofitInterface.class);
         call = client.login(user);
        call
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<UserTest>() {


                    @Override
                    public void onNext(@NonNull UserTest userTest) {


                        done = true;
                        logindone();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "onError()", e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void logindone() {
        if (done) {
            Toast.makeText(this, "hi", Toast.LENGTH_LONG).show();
            Intent i= new Intent(Login_Main.this,MainActivity.class);
            startActivity(i);
            finish();
        }
    }
    private void showSnackBarMessage(String message){
        Toast.makeText(Login_Main.this,message,Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}