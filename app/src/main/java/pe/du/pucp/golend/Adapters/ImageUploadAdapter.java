package pe.du.pucp.golend.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import pe.du.pucp.golend.R;
import pe.du.pucp.golend.TI.TICreateDeviceActivity;


public class ImageUploadAdapter extends RecyclerView.Adapter<ImageUploadAdapter.ViewHolder>{
    private Activity activity;
    private List<String> images;

    public ImageUploadAdapter(Activity activity, List<String> images) {
        this.activity = activity;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_upload, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(activity).load(images.get(position)).placeholder(AppCompatResources.getDrawable(activity,R.drawable.ic_image_placeholder_48)).into(holder.ivImage);
        holder.position = position;
    }


    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivImage;
        int position;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivItemImageUpload);
            itemView.findViewById(R.id.btnRemovemImageUpload).setOnClickListener(v ->{
                ((TICreateDeviceActivity)  activity).removerFoto(position);
            });
        }
    }
}
