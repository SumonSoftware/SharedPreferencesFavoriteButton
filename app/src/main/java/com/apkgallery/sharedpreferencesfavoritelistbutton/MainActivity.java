package com.apkgallery.sharedpreferencesfavoritelistbutton;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    public static ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
    ArrayList<HashMap<String,String>> favArrayList = new ArrayList<>();
    HashMap<String,String>hashMap;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MyAdapter myAdapter;

    Button button2;
    ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        button2 = findViewById(R.id.button2);
        progressBar = findViewById(R.id.progressBar);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name),MODE_PRIVATE);
        editor= sharedPreferences.edit();
        getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

        getFavItem();
        LoadData();


        myAdapter= new MyAdapter();
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // FavoriteListActivity যাওয়ার Code
        button2.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this,FavoriteListActivity.class));
        });

    }///onCreate End Here =================

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.myViewHolder>{

        @NonNull
        @Override
        public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


            LayoutInflater layoutInflater= getLayoutInflater();
            View itemView= layoutInflater.inflate(R.layout.items_view,parent,false);


            return new myViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

            HashMap<String,String>mHashMap= arrayList.get(position);
            String title= mHashMap.get("title");
            String subTitle= mHashMap.get("subTitle");

            holder.tvTitle.setText(title);

            holder.addText.setText(title+"\n"+subTitle);

            holder.add= title+"\n"+subTitle;



            if (subTitle !=null){
                holder.tvSubTitle.setVisibility(View.VISIBLE);
                holder.tvSubTitle.setText(subTitle);
            }


            holder.LayCopy.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", holder.add);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(MainActivity.this, "Copy",Toast.LENGTH_SHORT).show();

            }); // LayCopy End Here =========


            holder.layShare.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, holder.add);
                startActivity(intent);

            });// layShare End Here =========


            boolean isFavorite = isFavoriteItem(title);

            if (isFavorite){
                holder.favorite.setImageResource(R.drawable.baseline_favorite_24);
            }else {
                holder.favorite.setImageResource(R.drawable.baseline_favorite_border_24);
            }


            holder.RelFavorite.setOnClickListener(v -> {


                boolean isFavoriteRemove = isFavoriteItemRemove(title);

                if (isFavoriteRemove){
                    holder.favorite.setImageResource(R.drawable.baseline_favorite_border_24);
                    Toast.makeText(MainActivity.this,"Favorite Remove",Toast.LENGTH_SHORT).show();
                }else {

                    HashMap<String,String>favHashMap= arrayList.get(holder.getAdapterPosition());
                    favArrayList.add(favHashMap);

                    Gson gson= new Gson();
                    String stringArrayList= gson.toJson(favArrayList);
                    editor.putString("stringArrayList",stringArrayList);
                    editor.apply();
                    Toast.makeText(MainActivity.this,"Favorite Add ",Toast.LENGTH_SHORT).show();
                    holder.favorite.setImageResource(R.drawable.baseline_favorite_24);
                }





            }); //RelFavorite End Here ============


        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        private class myViewHolder extends RecyclerView.ViewHolder{

            RelativeLayout RelFavorite;
            LinearLayout layShare;
            LinearLayout LayCopy;
            TextView  tvTitle,tvSubTitle,addText;
            ImageView favorite;

            String add;

            public myViewHolder(@NonNull View itemView) {
                super(itemView);

                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvSubTitle = itemView.findViewById(R.id.tvSubTitle);
                LayCopy = itemView.findViewById(R.id.LayCopy);
                layShare = itemView.findViewById(R.id.layShare);
                RelFavorite = itemView.findViewById(R.id.RelFavorite);
                favorite= itemView.findViewById(R.id.favorite);
                addText= itemView.findViewById(R.id.addText);


            }
        }
    }// MyAdapter End Here ===

    private void LoadData(){

        // Your Server API
        String url = "https://sumondevo.000webhostapp.com/FavoriteButton/Items.json";

        JsonArrayRequest jsonArrayRequest= new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {

                progressBar.setVisibility(View.GONE);

                try {
                    for (int x= 0; x<jsonArray.length();x++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(x);
                        String title= jsonObject.getString("title");
                        String subTitle= jsonObject.getString("subTitle");

                        hashMap = new HashMap<>();
                        hashMap.put("title",title);
                        hashMap.put("subTitle",subTitle);
                        arrayList.add(hashMap);
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

// Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);

    } //LoadData End Here ==========




    private boolean isFavoriteItem(String title){
        // যদি Server থেকে তথ্য আসে তাহল ্এখানে নতুন করে favArrayList  করার প্রয়োোজন নে্ই
//favArrayList = new ArrayList<>();
        if (favArrayList != null && favArrayList.size() > 0){

            for (int x=0; x< favArrayList.size(); x++){

                HashMap<String,String>favHashMap= favArrayList.get(x);

                if (title.equals(favHashMap.get("title"))){

                    return true;
                }

            }

        }

        return false;
    }

    private boolean isFavoriteItemRemove(String title){

        if (favArrayList != null && favArrayList.size() > 0){

            for (int x=0; x< favArrayList.size(); x++){


                HashMap<String,String>favHashMap= favArrayList.get(x);

                if (title.equals(favHashMap.get("title"))){

                    favArrayList.remove(x);


                    Gson gson= new Gson();
                    String stringArrayList= gson.toJson(favArrayList);
                    editor.putString("stringArrayList",stringArrayList);
                    editor.apply();




                    return true;
                }

            }

        }

        return false;
    }


    private void getFavItem(){

        String stringArrayList= sharedPreferences.getString("stringArrayList","");
        if (!stringArrayList.isEmpty()){
            Type type = new TypeToken<ArrayList<HashMap<String, String>>>() {}.getType();
            favArrayList = new Gson().fromJson(stringArrayList, type);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        getFavItem();
        LoadData(); // Server থেকে তথ্য আসলে এটা Comment করে রাখতে হবে।
        myAdapter.notifyDataSetChanged();
    }
}
