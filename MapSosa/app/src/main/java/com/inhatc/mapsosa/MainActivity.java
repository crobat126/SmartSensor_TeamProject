package com.inhatc.mapsosa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kakao.auth.Session;
import com.kakao.network.NetworkTask;
import com.kakao.util.KakaoParameterException;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {
    Session session;
    Button btnKakaoSend;                       // Button object
    Retrofit retrofit;
    ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("MainActivity :: ", "로그인 성공 페이지");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = Session.getCurrentSession();
        retrofit = new Retrofit.Builder().baseUrl(ApiService.API_URL).build();
        apiService = retrofit.create(ApiService.class);

        btnKakaoSend = (Button) findViewById(R.id.btnSendKakao);
        btnKakaoSend.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSendKakao:
                sendKaKao();
                break;
            default:
                break;
        }
    }

    private void sendKaKao() {
       String s = "테스트";
        Log.e("MainActivity :: ", "카카오 메세지 전송시작");
        String Token = session.getAccessToken();
        String reqURL = "https://kakao.com/v2/api/talk/memo/default/send";

        String result = null;

        Call<ResponseBody> postComment = apiService.postComment();
        postComment.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("sendKaKao Response:: ", response.body().toString());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("sendKaKao::", "Failed API call with call: " + call +
                        " + exception: " + t);
            }
        });

    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url;
        private ContentValues values;
        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }
        @Override protected String doInBackground(Void... params) {
            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }
        @Override protected void onPostExecute(String s) {
            super.onPostExecute(s); //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
             }
    }



}