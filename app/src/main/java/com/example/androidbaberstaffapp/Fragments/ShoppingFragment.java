package com.example.androidbaberstaffapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidbaberstaffapp.Adapter.MyShoppingItemAdapter;
import com.example.androidbaberstaffapp.Common.SpacesItemDecoration;
import com.example.androidbaberstaffapp.DoneServicesActivity;
import com.example.androidbaberstaffapp.Interface.IOnShoppingItemSelected;
import com.example.androidbaberstaffapp.Interface.IShoppingDataLoadListener;
import com.example.androidbaberstaffapp.Model.ShoppingItem;
import com.example.androidbaberstaffapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ShoppingFragment extends BottomSheetDialogFragment implements IShoppingDataLoadListener, IOnShoppingItemSelected {

    Unbinder unbinder;
    IOnShoppingItemSelected callBackToActivity;
    CollectionReference shoppingItemRef;
    IShoppingDataLoadListener iShoppingDataLoadListener;
    @BindView(R.id.chip_group)
    ChipGroup chipGroup;

    @BindView(R.id.chip_wax)
    Chip chip_wax;

    @BindView(R.id.chip_spray)
    Chip chip_spray;

    @BindView(R.id.chip_hair_care)
    Chip chip_hair_care;

    @BindView(R.id.chip_body_care)
    Chip chip_body_care;

    @OnClick(R.id.chip_wax)
    void waxLoadClick() {
        setSelectedChip(chip_wax);
        loadShoppingItem("Wax");
    }

    @OnClick(R.id.chip_spray)
    void sprayLoadClick() {
        setSelectedChip(chip_spray);
        loadShoppingItem("Spray");
    }

    @OnClick(R.id.chip_hair_care)
    void haircareLoadClick() {
        setSelectedChip(chip_hair_care);
        loadShoppingItem("HairCare");
    }

    @OnClick(R.id.chip_body_care)
    void bodycareLoadClick() {
        setSelectedChip(chip_body_care);
        loadShoppingItem("BodyCare");
    }


    @BindView(R.id.recycler_items)
    RecyclerView recycler_items;

    private static ShoppingFragment instance;
    public static ShoppingFragment getInstance(IOnShoppingItemSelected iOnShoppingItemSelected){
        return instance==null?new ShoppingFragment(iOnShoppingItemSelected):instance;
    }

    private void loadShoppingItem(String itemMenu) {
        shoppingItemRef = FirebaseFirestore.getInstance().collection("Shopping")
                .document(itemMenu)
                .collection("Items");
        //get Data
        shoppingItemRef.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iShoppingDataLoadListener.onShopingDataLoadFaile(e.getMessage());
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<ShoppingItem> shoppingItems = new ArrayList<>();
                    for (DocumentSnapshot itemSnapShop : task.getResult()) {
                        ShoppingItem shoppingItem = itemSnapShop.toObject(ShoppingItem.class);
                        shoppingItem.setId(itemSnapShop.getId());//khac setItemid
                        shoppingItems.add(shoppingItem);
                    }
                    iShoppingDataLoadListener.onShoppingDataLoadSuccess(shoppingItems);
                }
            }
        });
    }

    private void setSelectedChip(Chip chip) {
        //set color
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chipItem = (Chip) chipGroup.getChildAt(i);
            if (chipItem.getId() != chip_wax.getId()) {
                chipItem.setChipBackgroundColorResource(android.R.color.darker_gray);
                chipItem.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                chipItem.setChipBackgroundColorResource(android.R.color.holo_orange_dark);
                chipItem.setTextColor(getResources().getColor(android.R.color.black));
            }
        }
    }

    public ShoppingFragment(IOnShoppingItemSelected callBackToActivity) {
        this.callBackToActivity = callBackToActivity;
    }

    public ShoppingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_shopping, container, false);

        unbinder = ButterKnife.bind(this, itemView);

        //Defaut load
        loadShoppingItem("Wax");
        init();
        initView();
        return itemView;
    }

    private void initView() {
        recycler_items.setHasFixedSize(true);
        recycler_items.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recycler_items.addItemDecoration(new SpacesItemDecoration(8));
    }

    private void init() {
        iShoppingDataLoadListener = this;
    }

    @Override
    public void onShoppingDataLoadSuccess(List<ShoppingItem> shoppingItemList) {
        MyShoppingItemAdapter adapter = new MyShoppingItemAdapter(getContext(), shoppingItemList,this);
        recycler_items.setAdapter(adapter);
    }

    @Override
    public void onShopingDataLoadFaile(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShoppingItemSelected(ShoppingItem shoppingItem) {
        callBackToActivity.onShoppingItemSelected(shoppingItem);
    }
}
