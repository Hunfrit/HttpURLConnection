package com.hunfrit.httptest;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Artem Shapovalov on 21.12.2017.
 */

public class ProgressPresenter extends Fragment {

    TextView contentView;
    String contentText = null;
    WebView webView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.progress, container, false);
        contentView = (TextView) view.findViewById(R.id.content);
        webView = (WebView) view.findViewById(R.id.webview);

        if (contentText!=null){
            contentView.setText(contentText);
            webView.loadData(contentText, "text/html; charset=utf-8", "utf-8");
        }

        Button btnFetch = (Button) view.findViewById(R.id.btn);
        btnFetch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (contentText==null){
                    contentView.setText("Downloading...");
                    new ProgressTask().execute("http://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?valcode=EUR&json");
                }
            }
        });
        return view;
    }

    private class ProgressTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String content;
            try{
                content = getContent(strings[0]);
            }catch (IOException ex){
                content = ex.getMessage();
            }

            return content;
        }

        @Override
        protected void onPostExecute(String content) {
            super.onPostExecute(content);

            contentText=content;
            contentView.setText(content);
            webView.loadData(content, "text/html; charset=utf-8", "utf-8");
            Toast.makeText(getActivity(), "Done", Toast.LENGTH_LONG).show();
        }

        private String getContent(String path) throws IOException{
            BufferedReader reader = null;
            try{
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(10000);
                connection.connect();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                while((line=reader.readLine())!=null){
                    stringBuilder.append(line + "\n");
                }
                return (stringBuilder.toString());
            }finally {
                if(reader != null){
                    reader.close();
                }
            }
        }
    }
}
