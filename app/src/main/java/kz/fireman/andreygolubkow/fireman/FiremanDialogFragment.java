package kz.fireman.andreygolubkow.fireman;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;

public class FiremanDialogFragment extends DialogFragment {

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String dialogText = getArguments().getString("dialogText");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setTitle("")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(dialogText)
                .setPositiveButton("OK", null)
                .create();
    }


}