package com.example.androidbaberstaffapp.Common;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.androidbaberstaffapp.Interface.IDialogClickListener;
import com.example.androidbaberstaffapp.R;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomLoginDialog {
    @BindView(R.id.txt_title)
    TextView txt_title;
    @BindView(R.id.edt_User)
    TextInputEditText edt_user;
    @BindView(R.id.edt_password)
    TextInputEditText edt_password;
    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.btn_cancel)
    Button btn_cancle;

    public static CustomLoginDialog mDiadog;
    public IDialogClickListener iDialogClickListener;

    public static CustomLoginDialog getInstance() {
        if (mDiadog == null)
            mDiadog = new CustomLoginDialog();
        return mDiadog;
    }

    public void showLoginDialog(String title,
                                String postiveText,
                                String negativeText,
                                Context context,
                                IDialogClickListener iDialogClickListener) {
        this.iDialogClickListener = iDialogClickListener;

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_login);


        ButterKnife.bind(this, dialog);
        //setTitle
        if (!TextUtils.isEmpty(title)) {
            txt_title.setText(title);
            txt_title.setVisibility(View.VISIBLE);

        }

        btn_login.setText(postiveText);
        btn_cancle.setText(negativeText);

        dialog.setCancelable(false);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iDialogClickListener.onClickPositiveButton(dialog, edt_user.getText().toString(),
                        edt_password.getText().toString());

            }
        });

        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iDialogClickListener.onClickNegativeButton(dialog);
            }
        });

    }

}
