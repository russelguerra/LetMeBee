package com.guerra.russel.letmebee;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DoToast extends Toast {
    View layout;

    public DoToast(Context context, CharSequence text) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.z_toast, null);
        TextView textView = (TextView) layout.findViewById(R.id.text);
        textView.setText(text);
        DoToast.this.setGravity(Gravity.BOTTOM, 0, 50);
        DoToast.this.setDuration(Toast.LENGTH_SHORT);
        DoToast.this.setView(layout);
        DoToast.this.show();
    }
}
