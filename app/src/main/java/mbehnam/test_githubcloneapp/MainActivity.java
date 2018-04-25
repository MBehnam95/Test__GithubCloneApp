package mbehnam.test_githubcloneapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by MBehnam on 4/24/2018.
 */

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Repositpries> data;
    private DataAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            //  جهت رفع خطای javax.net.ssl.SSLProtocolException: SSL handshake aborted
            ProviderInstaller.installIfNeeded(getApplicationContext());
/*
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            SSLEngine engine = sslContext.createSSLEngine();
*/

        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

        recyclerView = (RecyclerView)findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        MyApi myApi = retrofit.create(MyApi.class);
        final ProgressDialog ProgressDialog = showProgress();
        myApi.getRepositpries().enqueue(new Callback<List<Repositpries>>() {

            @Override
            public void onResponse(Call<List<Repositpries>> call, Response<List<Repositpries>> response) {
                List<Repositpries> Repositpries = response.body();
                Log.v("Repositpries", "" + Repositpries.get(0).getFull_name()+"\n"+Repositpries.get(0).getDescription()+"\n"+Repositpries.size());

                data = new ArrayList<>(Repositpries);
                adapter = new DataAdapter(data);
                recyclerView.setAdapter(adapter);

                ProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Repositpries>> call, Throwable t) {

                Toast.makeText(MainActivity.this, "خطا!"+"\n"+t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                Log.v("خطا",t.getMessage().toString());
                ProgressDialog.dismiss();
            }


        });


        ImageButton imgB_Search = (ImageButton) findViewById(R.id.imgB_Search);
        imgB_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogSearch();
            }
        });


        Button btn_UserLogin = (Button) findViewById(R.id.btn_UserLogin);
        btn_UserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogUserLogin();
            }
        });

        Button btn_UserRepos = (Button) findViewById(R.id.btn_UserRepos);
        btn_UserRepos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogUserRepos();
            }
        });

    }

    // نمایش Repositories  با نام کاربری
    private void ShowDialogUserRepos() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("User Repos");
        alert.setMessage("نام کاربری شخص مورد نظر را وارد کنید:");
// Set an EditText view to get user input
        final EditText input = new EditText(MainActivity.this);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String user_name = input.getText().toString();
                if (!user_name.equals(""))
                    Retrofit_user_name(user_name);
                else
                    Toast.makeText(MainActivity.this, "عبارتی وارد نشد!", Toast.LENGTH_SHORT).show();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    private void Retrofit_user_name(String user_name) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApi myApi = retrofit.create(MyApi.class);
        final ProgressDialog ProgressDialog = showProgress();
        myApi.getRepositories_anyuser(user_name).enqueue(new Callback<List<Repositpries>>() {

            @Override
            public void onResponse(Call<List<Repositpries>> call, Response<List<Repositpries>> response) {
                List<Repositpries> Repositpries = response.body();
                if (Repositpries!=null) {
//                    Log.v("Repositories_user", "" + Repositpries.get(0).getFull_name());
//                    Toast.makeText(MainActivity.this, Repositpries.get(0).getFull_name() + " خوش آمدید ", Toast.LENGTH_SHORT).show();
                    data = new ArrayList<>(Repositpries);
                    adapter = new DataAdapter(data);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this, "نام کاربری مورد نظر نامعتبر!", Toast.LENGTH_SHORT).show();
                }
                ProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Repositpries>> call, Throwable t) {

                Toast.makeText(MainActivity.this, "خطا!"+"\n"+t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                Log.v("خطا",t.getMessage().toString());
                ProgressDialog.dismiss();
            }
        });
    }

    // نمایش Repositories  با یوزر و رمز عبور
    private void ShowDialogUserLogin() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Login");
//        alert.setCancelable(false);
        alert.setMessage("نام کاربری و رمز عبور خود را وارد کنید:");
        
        final View view = getLayoutInflater().inflate(R.layout.dialog, null);
        final EditText usernameEditText = (EditText) view.findViewById(R.id.username_edittext);
        final EditText passwordEditText = (EditText) view.findViewById(R.id.password_edittext);


        alert.setView(view);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (!username.equals("") && !password.equals(""))
                    Retrofit_UserLogin(username, password);
                else
                    Toast.makeText(MainActivity.this, "نام کاربری یا رمز عبور وارد نشده!", Toast.LENGTH_SHORT).show();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    private void Retrofit_UserLogin(final String username, final String password) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();

                        Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                                Credentials.basic(username, password));

                        Request newRequest = builder.build();
                        return chain.proceed(newRequest);
                    }
                }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        MyApi myApi = retrofit.create(MyApi.class);
        final ProgressDialog ProgressDialog = showProgress();
        myApi.getRepositories_user().enqueue(new Callback<List<Repositpries>>() {

            @Override
            public void onResponse(Call<List<Repositpries>> call, Response<List<Repositpries>> response) {
                List<Repositpries> Repositpries = response.body();
                if (Repositpries!=null) {
//                    Log.v("Repositories_user", "" + Repositpries.get(0).getFull_name());
//                    Toast.makeText(MainActivity.this, Repositpries.get(0).getFull_name() + " خوش آمدید ", Toast.LENGTH_SHORT).show();
                    data = new ArrayList<>(Repositpries);
                    adapter = new DataAdapter(data);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this, "نام کاربری یا رمز عبور نامعتبر!", Toast.LENGTH_SHORT).show();
                }
                ProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Repositpries>> call, Throwable t) {

                Toast.makeText(MainActivity.this, "خطا!"+"\n"+t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                Log.v("خطا",t.getMessage().toString());
                ProgressDialog.dismiss();
            }


        });
    }

    // نمایش Repositories  با جستجو
    private void ShowDialogSearch() {

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Search");
        alert.setMessage("عبارت مورد نظر را وارد کنید:");
// Set an EditText view to get user input
        final EditText input = new EditText(MainActivity.this);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String search = input.getText().toString();
                if (!search.equals(""))
                Retrofit_Search(search);
                else
                    Toast.makeText(MainActivity.this, "عبارتی وارد نشد!", Toast.LENGTH_SHORT).show();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    private void Retrofit_Search(String search) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        MyApi myApi = retrofit.create(MyApi.class);
        final ProgressDialog ProgressDialog = showProgress();
        myApi.getRepositpries_Search(search,100).enqueue(new Callback<Repositories_Search>() {

            @Override
            public void onResponse(Call<Repositories_Search> call, Response<Repositories_Search> response) {
                Repositories_Search Repositpries = response.body();
//                Log.v("Repositpries", "" + Repositpries.get(0).getName()+"\n"+Repositpries.get(0).getDescription()+"\n"+Repositpries.size());

                Repositories_Search data2 = (Repositpries);
                DataAdapter adapter2 = new DataAdapter(data2);
                recyclerView.setAdapter(adapter2);

                ProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Repositories_Search> call, Throwable t) {

                Toast.makeText(MainActivity.this, "خطا!"+"\n"+t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                Log.v("خطا",t.getMessage().toString());
                ProgressDialog.dismiss();
            }
        });
    }


    private ProgressDialog showProgress(){
        final ProgressDialog progressDoalog;
        progressDoalog = ProgressDialog.show(MainActivity.this,"","");
        progressDoalog.setCancelable(false);
        progressDoalog.setCanceledOnTouchOutside(false);
        progressDoalog.setContentView(R.layout.myprogress);
        return  progressDoalog;
    }

}
