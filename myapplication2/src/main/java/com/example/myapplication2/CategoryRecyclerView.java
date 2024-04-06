package com.example.myapplication2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.cardview.widget.CardView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryRecyclerView extends RecyclerView.Adapter<CategoryRecyclerView.ViewHolder> {
    private final List<CategoryObject> categories;

    private final HomeFragment.OnCategoryCardButtonClickListener cardButtonClickListener;

    public CategoryRecyclerView(List<CategoryObject> categories, HomeFragment.OnCategoryCardButtonClickListener cardButtonClickListener) {
        this.categories = categories;
        this.cardButtonClickListener = cardButtonClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;

        public CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.category_textview_show_categories_layout);
            imageView = itemView.findViewById(R.id.category_imageview_show_categories_layout);
            cardView = itemView.findViewById(R.id.cardView_show_categories_layout);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_categories_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryObject categoryObject = categories.get(position);
        String category = UtilityMethods.capitalizeCategoryName(categories.get(position).getCategory());
        holder.textView.setText(category);
        Picasso.get().load(categoryObject.getUrl()).resize(1024, 1024).into(holder.imageView);

        holder.cardView.setOnClickListener(v -> {
            cardButtonClickListener.onCategoryCardButtonClick(categoryObject.getCategory());
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

}
