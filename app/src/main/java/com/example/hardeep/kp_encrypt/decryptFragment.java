package com.example.hardeep.kp_encrypt;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.scottyab.aescrypt.AESCrypt;


public class decryptFragment extends Fragment {

    public EditText decrypt;
    public Button dEncrypt;
    public Switch simple, complex;
    public String decryptString;
    public String dResult;
    public View rootView;
    public SharedPreferences sharedPreferences;
    public static final String KEY = "lkajfikjasdlfsdciomnfjfoivcfdocinmfdmiocnfdmcoifncoiajafjckfdvjcioafiocmaosifoas";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_decrypt, container, false);

        //get the preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(rootView.getContext());
        decryptString = sharedPreferences.getString("smsMessage", "");

        //intialize
        decrypt = (EditText) rootView.findViewById(R.id.dText);
        dEncrypt = (Button) rootView.findViewById(R.id.dButton);
        simple = (Switch) rootView.findViewById(R.id.dSwitchSimple);
        complex = (Switch) rootView.findViewById(R.id.dSwtichComplex);

        //check the string
        if(!decryptString.equals("") && decryptString.startsWith("!!")) {
            decryptString = decryptString.replace("!!encrypt", "");
            decrypt.setText(decryptString);
            complex.setChecked(true);
        } else if(!decryptString.equals("")) {
            decryptString = decryptString.replace("!encrypt", "");
            decrypt.setText(decryptString);
            simple.setChecked(true);
        }

        //onClick
        dEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decryptString = decrypt.getText().toString().trim();
                if (decryptString.equals("")) {
                    Toast.makeText(rootView.getContext(), "Please enter some message and try again", Toast.LENGTH_SHORT).show();
                } else if (simple.isChecked() && complex.isChecked()) {
                    Toast.makeText(rootView.getContext(), "Please only select one encryption method", Toast.LENGTH_SHORT).show();
                } else if(!simple.isChecked() && !complex.isChecked()) {
                    Toast.makeText(rootView.getContext(), "Please select an decryption type!", Toast.LENGTH_SHORT).show();
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
            dResult = "";
            dResult = AESCrypt.decrypt(KEY, decryptString);
            showCompletionMessage();
        } catch (Exception e) {
            Toast.makeText(rootView.getContext(), "Not the right type of encrypted code", Toast.LENGTH_SHORT).show();
        }
    }

    public void simple() {
        dResult = "";
        int n;
        for(int i = 0; i < decryptString.length(); i++) {
            if(i > decryptString.length()) break;
            n = decryptString.charAt(i) - KEY.charAt(i);
            dResult += (char)n;
        }
        showCompletionMessage();
    }

    public void showCompletionMessage() {
        if(dResult != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
            builder.setTitle("Decryted!");
            builder.setMessage("Decrypted message: " + dResult);
            builder.setPositiveButton("Copy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ClipboardManager cbm = (ClipboardManager) getActivity().getSystemService(rootView.getContext().CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("eMessage", dResult);
                    cbm.setPrimaryClip(clipData);
                }
            });
            builder.setNegativeButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setData(Uri.parse("sms:"));
                    sendIntent.putExtra("sms_body", dResult);
                    startActivity(sendIntent);
                }
            });
            builder.show();
        }
    }
}
