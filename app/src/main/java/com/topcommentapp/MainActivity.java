package com.topcommentapp;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();
    private String after = null; // Параметр для пагінації

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        postAdapter = new PostAdapter(postList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdapter);

        loadTopPosts();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == postList.size() - 1) {
                    loadTopPosts();
                }
            }
        });
    }

    private void loadTopPosts() {
        RedditApiService redditApiService = RetrofitClient.getRedditApiService();
        redditApiService.getPopularPosts(10, "day", after).enqueue(new Callback<RedditResponse>() {
            @Override
            public void onResponse(Call<RedditResponse> call, Response<RedditResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RedditResponse.Data data = response.body().getData();

                    // Оновлюємо 'after' для пагінації
                    after = data.getAfter();

                    // Додаємо нові пости до списку
                    postList.addAll(data.getChildrenPosts());

                    postAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<RedditResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
