package com.example.onlinebusticketing;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class FragmentWalletActivation extends BottomSheetDialogFragment {
    CardView btnActivate,btnLater;

    private final ActivityResultLauncher<Intent> ActivityResultLauncher =
            registerForActivityResult(new ActivityResult(), new ActivityResultCallback<String>() {
                @Override
                public void onActivityResult(String result) {
                    if (result != null) {
                        Toast.makeText(getActivity(), "Wallet Activated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Wallet Activation Failed", Toast.LENGTH_SHORT).show();
                    }
                    dismiss();
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activate_wallet, container, false);

        btnActivate = view.findViewById(R.id.btnActivate);
        btnLater = view.findViewById(R.id.btnLater);



        btnActivate.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                onOption1Click();
            }
        });

        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOption2Click();
            }
        });

        return view;
    }
    private void onOption1Click() {
        Intent intent = new Intent(getActivity(), WalletKYC.class);
        ActivityResultLauncher.launch(intent);
//        dismiss();
    }

    private void onOption2Click() {
        dismiss();
    }

}