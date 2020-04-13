package com.junjunguo.pocketmaps.fragments;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.junjunguo.pocketmaps.R;

public class GroupDialog {
    private Activity activity;
    private ViewGroup groupCreateVP, groupJoinVP, title;

    public GroupDialog (Activity activity) {
        this.activity = activity;
        groupCreateVP = (ViewGroup) activity.findViewById(R.id.group_create_layout);
    }

    public void showGroupDialog (final ViewGroup calledFromVP) {
        initCancelBtn(groupCreateVP, calledFromVP);
        groupCreateVP.setVisibility(View.VISIBLE);
        calledFromVP.setVisibility(View.INVISIBLE);
    }

    private void initCancelBtn (final ViewGroup groupDialogVP, final ViewGroup calledFromVP) {
        Button cancelBtn = (Button) activity.findViewById(R.id.gp_button_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupDialogVP.setVisibility(View.INVISIBLE);
                calledFromVP.setVisibility(View.VISIBLE);
            }
        });
    }
}
