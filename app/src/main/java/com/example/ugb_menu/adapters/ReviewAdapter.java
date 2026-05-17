package com.example.ugb_menu.adapters;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ugb_menu.R;
import com.example.ugb_menu.models.Review;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final List<Review> reviews;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.ratingDisplay.setRating(review.getRating());
        holder.tvComment.setText(review.getComment());
        
        CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
                review.getTimestamp(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS
        );
        holder.tvDate.setText(relativeTime);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final RatingBar ratingDisplay;
        final TextView tvComment;
        final TextView tvDate;

        public ViewHolder(View view) {
            super(view);
            ratingDisplay = view.findViewById(R.id.rating_display);
            tvComment = view.findViewById(R.id.tv_review_comment);
            tvDate = view.findViewById(R.id.tv_review_date);
        }
    }
}
