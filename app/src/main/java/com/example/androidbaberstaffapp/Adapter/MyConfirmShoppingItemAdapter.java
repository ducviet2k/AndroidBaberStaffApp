package com.example.androidbaberstaffapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidbaberstaffapp.Model.ShoppingItem;
import com.example.androidbaberstaffapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyConfirmShoppingItemAdapter extends RecyclerView.Adapter<MyConfirmShoppingItemAdapter.MyViewHolder> {

    Context context;
    List<ShoppingItem>shoppingItemList;

    public MyConfirmShoppingItemAdapter(Context context, List<ShoppingItem> shoppingItemList) {
        this.context = context;
        this.shoppingItemList = shoppingItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View intemView = LayoutInflater.from(context).inflate(R.layout.layout_confirm_shopping,viewGroup,false);
        return new MyViewHolder(intemView);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i ) {
       Picasso.get()
               .load(shoppingItemList.get(i).getImage())
               .into(myViewHolder.item_image);
       myViewHolder.txt_name.setText(shoppingItemList.get(i).getName());

    }

    @Override
    public int getItemCount() {
        return shoppingItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_image)
        ImageView item_image;

        @BindView(R.id.txt_name)
        TextView txt_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}