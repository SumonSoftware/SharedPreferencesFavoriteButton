package com.apkgallery.sharedpreferencesfavoritelistbutton;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;


public class FavoriteListActivity extends AppCompatActivity {

    ArrayList<HashMap<String,String>>favArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MyAdapter myAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritelist);



        recyclerView =findViewById(R.id.recyclerView);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name),MODE_PRIVATE);

        editor= sharedPreferences.edit();


        getFavItem();







    } // onCreate End Here ===========

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

            HashMap<String,String>mHashMap= favArrayList.get(position);
            String title= mHashMap.get("title");
            String subTitle= mHashMap.get("subTitle");

            holder.tvTitle.setText(title);
            holder.add= title+"\n"+subTitle;


            if (subTitle !=null){
                holder.tvSubTitle.setVisibility(View.VISIBLE);
                holder.tvSubTitle.setText(subTitle);
            }

            holder.favorite.setImageResource(R.drawable.baseline_favorite_24);



            holder.RelFavorite.setOnClickListener(v -> {

                favArrayList.remove(holder.getAdapterPosition());
                holder.favorite.setImageResource(R.drawable.baseline_favorite_border_24);
                myAdapter.notifyItemRemoved(holder.getAdapterPosition());
                Gson gson= new Gson();
                String stringArrayList= gson.toJson(favArrayList);
                editor.putString("stringArrayList",stringArrayList);
                editor.apply();
                Toast.makeText(FavoriteListActivity.this,"Favorite Remove",Toast.LENGTH_SHORT).show();

            });

            holder.LayCopy.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", holder.add);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(FavoriteListActivity.this, "Copy",Toast.LENGTH_SHORT).show();

            });


            holder.LayShare.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, holder.add);
                startActivity(intent);

            });


        }

        @Override
        public int getItemCount() {
            return favArrayList.size();
        }

        private class myViewHolder extends RecyclerView.ViewHolder{

            RelativeLayout RelFavorite;
            LinearLayout LayShare;
            LinearLayout LayCopy;
            TextView tvTitle;
            TextView  tvSubTitle;
            ImageView favorite;

            String add;


            public myViewHolder(@NonNull View itemView) {
                super(itemView);

                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvSubTitle = itemView.findViewById(R.id.tvSubTitle);
                LayCopy = itemView.findViewById(R.id.LayCopy);
                LayShare = itemView.findViewById(R.id.layShare);
                RelFavorite = itemView.findViewById(R.id.RelFavorite);
                favorite= itemView.findViewById(R.id.favorite);

            }
        }
    }// MyAdapter End Here ===



    private void getFavItem(){
        favArrayList= new ArrayList<>();
        String stringArrayList= sharedPreferences.getString("stringArrayList","");

        if (!stringArrayList.isEmpty()){
            Type type = new TypeToken<ArrayList<HashMap<String, String>>>() {}.getType();
            favArrayList = new Gson().fromJson(stringArrayList, type);

            if (favArrayList != null && favArrayList.size()>0){

                myAdapter= new MyAdapter();
                recyclerView.setAdapter(myAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(FavoriteListActivity.this));


            }else {
                Toast.makeText(FavoriteListActivity.this,"No Data ",Toast.LENGTH_SHORT).show();
            }
        }


    }// getFavItem End Here ===========






} //public class FavoriteListActivity End Here =================