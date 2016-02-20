package com.example.hardeep.kp_encrypt;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.scottyab.aescrypt.AESCrypt;


public class encryptFragment extends Fragment {

    public EditText encrypt;
    public Button bEncrypt;
    public Switch simple, complex;
    public String encryptString;
    public String eResult;
    public View rootView;
    public static final String KEY = "lkajfikjasdlfsdciomnfjfoivcfdocinmfdmiocnfdmcoifncoiajafjckfdvjcioafiocmaosifoas";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_encrypt, container, false);

        encrypt = (EditText) rootView.findViewById(R.id.eText);
        bEncrypt = (Button) rootView.findViewById(R.id.eButton);
        simple = (Switch) rootView.findViewById(R.id.eSwitchSimple);
        complex = (Switch) rootView.findViewById(R.id.eSwtichComplex);

        bEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encryptString = encrypt.getText().toString().trim();
                if (encryptString.equals("")) {
                    Toast.makeText(rootView.getContext(), "Please enter some message and try again", Toast.LENGTH_SHORT).show();
                } else if (simple.isChecked() && complex.isChecked()) {
                    Toast.makeText(rootView.getContext(), "Please only select one encryption method", Toast.LENGTH_SHORT).show();
                } else if(!simple.isChecked() && !complex.isChecked()) {
                    Toast.makeText(rootView.getContext(), "Please select an encryption type!", Toast.LENGTH_SHORT).show();
                } else {
                    if (simple.isChecked()) {
                        simple();
                    } else if (complex.isChecked()) {
                        complex();
                    }
                }
            }
        });

        return rootView;
    }

    public void complex() {
        try {
            eResult = "";
            eResult = AESCrypt.encrypt(KEY, encryptString);
            showCompletionMessage();
        } catch (Exception e) {}
    }

    public void simple() {
        eResult = "";
        int n;
        for(int i = 0; i < encryptString.length(); i++) {
            if(i > encryptString.length()) break;
            n = encryptString.charAt(i) + KEY.charAt(i);
            eResult += (char)n;
        }
        showCompletionMessage();
    }

    public void showCompletionMessage() {
        if(eResult != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
            builder.setTitle("Encryted!");
            builder.setMessage("Encrypted message: " + eResult);
            builder.setPositiveButton("Copy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ClipboardManager cbm = (ClipboardManager) getActivity().getSystemService(rootView.getContext().CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("eMessage", eResult);
                    cbm.setPrimaryClip(clipData);
                }
            });
            builder.setNegativeButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setData(Uri.parse("sms:"));
                    if (simple.isChecked()) {
                        sendIntent.putExtra("sms_body", "!encrypt" + eResult);
                    } else {
                        sendIntent.putExtra("sms_body", "!!encrypt" + eResult);
                    }
                    startActivity(sendIntent);
                }
            });
            builder.show();
        }
    }
}
