package com.example.myapplication2;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    private List<UploadPersonforSeachIndex> userList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView nameTextView;
        private final TextView categoryTextView;

        private final TextView ratingTextview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.search_result_layout_imageView);
            nameTextView = itemView.findViewById(R.id.search_result_layout_name_textView);
            categoryTextView = itemView.findViewById(R.id.category_textView_search_result_layout);
            ratingTextview = itemView.findViewById(R.id.rating_textview_search_result_layout);

        }
    }

    public SearchResultAdapter(List<UploadPersonforSeachIndex> userDta) {
        userList = userDta;
    }

    @NonNull
    @Override
    public SearchResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_layout, parent, false);
        ViewHolder vh = new ViewHolder(view);

        // this function runs when an item from recycler view is clicked
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(view.getContext(), MyProfileActivity.class);
                String username = userList.get(vh.getAdapterPosition()).getUid();
                i.putExtra("uid", username);
                view.getContext().startActivity(i);
            }
        });


        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        double averageRating = userList.get(position).getAverageRating();
        long totalRatingCount = userList.get(position).getTotalRatingCount();

        if (averageRating >= 0){
            String ratingText =  averageRating + " (" + totalRatingCount + ")";
            holder.ratingTextview.setText(ratingText);
        }

        String capitalName = UtilityMethods.capitalizeFullName(userList.get(position).getFullname());
        holder.nameTextView.setText(capitalName);
        holder.categoryTextView.setText(userList.get(position).getCategory());
        Picasso.get().load(userList.get(position).getProfileUrl()).transform(new RoundImageTransformation()).resize(600, 600).centerCrop().into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void clearData() {
        userList.clear();
        notifyDataSetChanged();
    }
}
