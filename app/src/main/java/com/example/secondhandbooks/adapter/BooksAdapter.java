package com.example.secondhandbooks.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secondhandbooks.R;
import com.example.secondhandbooks.model.BookModel;
import com.example.secondhandbooks.user.ui.BookInfoActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BooksViewHolder> {

    private final Context context;
    private final List<BookModel> list;

    public BooksAdapter(Context context, List<BookModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public BooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_book_list, parent, false);
        return new BooksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BooksViewHolder holder, int position) {

        BookModel model = list.get(position);

        String price = "₹ " + model.getPrice();

        holder.row_title.setText(model.getTitle());
        holder.row_author.setText(model.getAuthor());
        holder.row_price.setText(price);

        try {
            if (model.getBookImage() != null)
                Picasso.get().load(model.getBookImage()).placeholder(R.drawable.loading_shape).into(holder.row_thumbnail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.container.setOnClickListener(view -> {
            Intent i = new Intent(context , BookInfoActivity.class);

            i.putExtra("book_id" ,model.getId());
            i.putExtra("book_author" ,model.getAuthor());
            i.putExtra("book_title",model.getTitle());
            i.putExtra("book_thumbnail",model.getBookImage());
//                i.putExtra("book_desc",model.getDescription());
            i.putExtra("book_cat",model.getCategory());

            i.putExtra("sellerId", model.getUserId());
            i.putExtra("selling_price",model.getPrice());
            i.putExtra("pages",model.getPages());
            i.putExtra("ISBN",model.getISBN());
            i.putExtra("language",model.getLanguage());
            i.putExtra("publicationYear",model.getPublicationYear());

            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class BooksViewHolder extends RecyclerView.ViewHolder {

        public ImageView row_thumbnail;
        public LinearLayout container;
        public TextView row_title,row_author,row_price;

        public BooksViewHolder(@NonNull View itemView) {
            super(itemView);

            container= itemView.findViewById(R.id.container);
            row_thumbnail= itemView.findViewById(R.id.row_thumbnail);
            row_title= itemView.findViewById(R.id.row_title);
            row_author= itemView.findViewById(R.id.row_author);
            row_price= itemView.findViewById(R.id.row_price);
        }
    }
}
