package mbehnam.test_githubcloneapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by MBehnam on 4/19/2018.
 */

public interface MyApi {
    @GET("/repositories")        // به آدرس مشخص شده داخل پرانتز یک درخواست GET ارسال بشه.
    Call<List<Repositpries>> getRepositpries();    // شخص میکنیم که چیزی که از سرور برای ما میاد فهرستی از پست‌ها خواهد بود

    //https://api.github.com/search/repositories?q=GoodMorning
    @GET("/search/repositories")
    Call<Repositories_Search> getRepositpries_Search(@Query("q") String Key_Search,@Query("per_page") int per_page);


    @GET("/user/repos?per_page=100")
    Call<List<Repositpries>> getRepositories_user();


    @GET("/users/{user_name}/repos?per_page=100")
    Call<List<Repositpries>> getRepositories_anyuser(@Path("user_name") String user_name);
}