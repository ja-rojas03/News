package com.example.altice.alticedoapp;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import javax.security.auth.login.LoginException;

public class myRecyclerViewAdapter extends RecyclerView.Adapter<myRecyclerViewAdapter.MyRecyclerItemViewHolder> {

    //Articles list
    private ArrayList<Article> articles;

    //DECLARE THE FIELDS

    private Context context;


    public myRecyclerViewAdapter(Context context, ArrayList<Article> articles) {

        this.context = context;
        this.articles = articles;
    }

    @NonNull
    @Override
    public MyRecyclerItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).
                inflate(R.layout.cardview, parent, false);

        MyRecyclerItemViewHolder holder = new MyRecyclerItemViewHolder(view);

        return holder;
    }

    //SET DATA ON CARDVIEW AND ON SCREEN
    @Override
    public void onBindViewHolder(@NonNull MyRecyclerItemViewHolder holder, int position) {
        //GET ARTICLE ABOUT TO BE DISPLAYED
        Article article = articles.get(position);

        //ASIGN IMAGE FROM RECIEVED URL FROM ARTICLE
        Glide.with(holder.itemView).load(article.getImageUrl()).into(holder.imagenNoticia);

        //ASSIGN TITLE FROM RECIEVED ARTICLE
        holder.tituloNoticia.setText(article.getTitle());


        //SETS ON CLICK LISTENER ON CARDVIEW LAYOUT TO SEND TO ARTICLEREAD ACTIVITY
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //SENDS INFO OF ARTICLE THROUGH INTENT
                Intent intent = new Intent(context, ArticleRead.class);
                intent.putExtra("image_url", article.getImageUrl());
                intent.putExtra("article_title", article.getTitle());
                intent.putExtra("article_description", article.getDescription());
                intent.putExtra("article_location", article.getLocation());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() { return articles.size(); }

    public class MyRecyclerItemViewHolder extends RecyclerView.ViewHolder {

        //DECLARE FIELDS
        ImageView imagenNoticia;
        TextView tituloNoticia;
        CardView parentLayout;


        public MyRecyclerItemViewHolder(View itemView) {
            super(itemView);
            //ASSIGN ID'S
            imagenNoticia = (ImageView) itemView.findViewById(R.id.my_Image_View_CV);
            tituloNoticia = (TextView) itemView.findViewById(R.id.my_Text_View_CV);
            parentLayout = (CardView) itemView.findViewById(R.id.parent_layout);
        }
    }


}
