package com.junjunguo.pocketmaps.fragments;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.junjunguo.pocketmaps.R;

public class GroupDialog {
    private final static int RA_GROUP_CREATE = 0;
    private final static int RA_GROUP_JOIN = 1;

    private Activity activity;
    private ViewGroup groupCreateVP, groupJoinVP, title;

    public GroupDialog (Activity activity) {
        this.activity = activity;
        groupCreateVP = (ViewGroup) activity.findViewById(R.id.group_create_layout);
        groupJoinVP = (ViewGroup) activity.findViewById(R.id.group_join_layout);
    }

    public void showGroupDialog (final ViewGroup calledFromVP, final int target) {
        if(target == RA_GROUP_CREATE) {
            initCancelBtn(groupCreateVP, calledFromVP, RA_GROUP_CREATE);
            groupCreateVP.setVisibility(View.VISIBLE);
        } else if (target == RA_GROUP_JOIN) {
            initCancelBtn(groupJoinVP, calledFromVP, RA_GROUP_JOIN);
            groupJoinVP.setVisibility(View.VISIBLE);
        }
        calledFromVP.setVisibility(View.INVISIBLE);
    }

    private void initCancelBtn (final ViewGroup groupDialogVP, final ViewGroup calledFromVP, final int target) {
        Button cancelBtn = null;
        if(target == RA_GROUP_CREATE) {
            cancelBtn = (Button) activity.findViewById(R.id.gp_button_cancel);
        } else if (target == RA_GROUP_JOIN) {
            cancelBtn = (Button) activity.findViewById(R.id.gj_button_cancel);
        }
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupDialogVP.setVisibility(View.INVISIBLE);
                calledFromVP.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initCreateBtn () {

    }

    private void initJoinBtn () {
        
    }
}
