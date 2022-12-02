package pe.du.pucp.golend.Adapters;

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

public class UsersClienteAdapter extends FirestorePagingAdapter<User, UsersClienteAdapter.UserClienteViewHolder>  {

    public UsersClienteAdapter(@NonNull FirestorePagingOptions options) {
        super(options);
    }

    @NonNull
    @Override
    public UserClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_admin_cliente_user, parent, false);
        return new UserClienteViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserClienteViewHolder holder, int position, @NonNull User model) {
        holder.tvCodigo.setText(model.getCodigo());
        holder.tvRol.setText(model.getRol());
        holder.tvNombre.setText(model.getNombre());
        Glide.with(holder.itemView.getContext()).load(model.getAvatarUrl())
                .placeholder(holder.itemView.getContext().getDrawable(R.drawable.avatar_placeholder)).dontAnimate()
                .into(holder.ivProfilePic);
    }

    public class UserClienteViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfilePic;
        TextView tvCodigo;
        TextView tvNombre;
        TextView tvRol;

        public UserClienteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfilePic = itemView.findViewById(R.id.ivUserClientePImage);
            tvCodigo = itemView.findViewById(R.id.tvUserClienteCodigo);
            tvNombre = itemView.findViewById(R.id.tvUserClienteNombre);
            tvRol = itemView.findViewById(R.id.tvUserClienteRol);
        }

    }


}
