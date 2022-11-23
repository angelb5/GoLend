package pe.du.pucp.golend.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;

import pe.du.pucp.golend.Entity.User;
import pe.du.pucp.golend.R;

public class UsersTIAdapter extends FirestorePagingAdapter<User, UsersTIAdapter.UserTIViewHolder>  {
    /**
     * Construct a new FirestorePagingAdapter from the given {@link FirestorePagingOptions}.
     *
     * @param options
     */
    public UsersTIAdapter(@NonNull FirestorePagingOptions options) {
        super(options);
    }

    @NonNull
    @Override
    public UserTIViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_ti_user, parent, false);
        return new UserTIViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserTIViewHolder holder, int position, @NonNull User model) {
        holder.tvCodigo.setText(model.getCodigo());
        holder.tvNombre.setText(model.getNombre());
        holder.userTi = model;
        Glide.with(holder.itemView.getContext()).load(model.getAvatarUrl())
                .placeholder(holder.itemView.getContext().getDrawable(R.drawable.avatar_placeholder)).dontAnimate()
                .into(holder.ivProfilePic);
    }

    public class UserTIViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfilePic;
        TextView tvCodigo;
        TextView tvNombre;
        User userTi;

        public UserTIViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfilePic = itemView.findViewById(R.id.ivUserTiPImage);
            tvCodigo = itemView.findViewById(R.id.tvUserTiCodigo);
            tvNombre = itemView.findViewById(R.id.tvUserTiNombre);
            itemView.findViewById(R.id.btnUserTiActualizar).setOnClickListener(view -> {
                Log.d("msg", "hola desde el usuario "+userTi.getNombre());
            });
        }

    }


}
