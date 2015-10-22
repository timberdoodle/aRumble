/*
 * Copyright (C) 2014 Disrupted Systems
 * This file is part of Rumble.
 * Rumble is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Rumble is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with Rumble.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.disrupted.rumble.userinterface.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.squareup.picasso.Picasso;

import org.disrupted.rumble.R;
import org.disrupted.rumble.app.RumbleApplication;
import org.disrupted.rumble.database.DatabaseExecutor;
import org.disrupted.rumble.database.DatabaseFactory;
import org.disrupted.rumble.database.PushStatusDatabase;
import org.disrupted.rumble.database.objects.Group;
import org.disrupted.rumble.userinterface.activity.DisplayQRCode;
import org.disrupted.rumble.userinterface.activity.GroupDetailActivity;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.crypto.SecretKey;

/**
 * @author Marlinski
 */
public class GroupRecyclerAdapter extends RecyclerView.Adapter<GroupRecyclerAdapter.GroupHolder>  {

    public static final String TAG = "GroupListAdapter";

    public class GroupHolder extends RecyclerView.ViewHolder {

        private Group group;

        public GroupHolder(View itemView) {
            super(itemView);
        }

        public void bindGroup(Group group) {
            this.group = group;

            LinearLayout title     = (LinearLayout) itemView.findViewById(R.id.group_title);
            ImageView group_lock   = (ImageView)    itemView.findViewById(R.id.group_lock_image);
            TextView  group_name   = (TextView)     itemView.findViewById(R.id.group_name);
            TextView  group_unread = (TextView)     itemView.findViewById(R.id.group_unread_msg);
            TextView  group_desc   = (TextView)     itemView.findViewById(R.id.group_desc);
            ImageView group_invite  = (ImageView)   itemView.findViewById(R.id.group_invite);

            //group_name.setTextColor(ColorGenerator.DEFAULT.getColor(groupList.get(i).getName()));
            if(this.group.isIsprivate())
                Picasso.with(activity)
                        .load(R.drawable.ic_lock_grey600_24dp)
                        .into(group_lock);
            else
                Picasso.with(activity)
                        .load(R.drawable.ic_lock_open_grey600_24dp)
                        .into(group_lock);

            group_name.setText(group.getName());
            if(group.getDesc().equals(""))
                group_desc.setVisibility(View.GONE);
            else
                group_desc.setText("Description: "+this.group.getDesc());


            /*
             * Manage click events
             */
            final String    gid          = this.group.getGid();
            final boolean   privateGroup = this.group.isIsprivate();
            final SecretKey key          = this.group.getGroupKey();
            final String    name         = this.group.getName();

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent groupStatusActivity = new Intent(activity, GroupDetailActivity.class );
                    groupStatusActivity.putExtra("GroupID",gid);
                    groupStatusActivity.putExtra("GroupName",name);
                    activity.startActivity(groupStatusActivity);
                    activity.overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit);
                }
            });

            group_invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ByteBuffer byteBuffer;
                    byte[] keybytes;
                    if(privateGroup)
                        keybytes = key.getEncoded();
                    else
                        keybytes = new byte[0];

                    byteBuffer = ByteBuffer.allocate(2 + name.length() + gid.length() + keybytes.length);

                    // send group name
                    byteBuffer.put((byte)name.length());
                    byteBuffer.put(name.getBytes(),0,name.length());

                    // send group ID
                    byteBuffer.put((byte)gid.length());
                    byteBuffer.put(gid.getBytes());

                    // send key
                    byteBuffer.put(keybytes);
                    String buffer = Base64.encodeToString(byteBuffer.array(),Base64.NO_WRAP);

                    try {
                        IntentIntegrator.shareText(activity, buffer);
                    } catch(ActivityNotFoundException notexists) {
                        Log.d(TAG, "Barcode scanner is not installed on this device");
                        int size = 200;
                        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
                        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
                        QRCodeWriter qrCodeWriter = new QRCodeWriter();
                        try {
                            BitMatrix bitMatrix = qrCodeWriter.encode(buffer, BarcodeFormat.QR_CODE, size, size, hintMap);
                            Bitmap image = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

                            if(image != null) {
                                for (int i = 0; i < size; i++) {
                                    for (int j = 0; j < size; j++) {
                                        image.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                                    }
                                }
                                Intent intent = new Intent(activity, DisplayQRCode.class);
                                intent.putExtra("EXTRA_GROUP_NAME", name);
                                intent.putExtra("EXTRA_BUFFER", buffer);
                                intent.putExtra("EXTRA_QRCODE", image);
                                activity.startActivity(intent);
                            }
                        }catch(WriterException ignore) {
                        }
                    }

                }
            });


        }
    }

    private Activity activity;
    private ArrayList<Group> groupList;

    public GroupRecyclerAdapter(Activity activity) {
        this.activity = activity;
        this.groupList = null;
    }


    @Override
    public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_list, null, true);
        return new GroupHolder(layout);
    }

    @Override
    public void onBindViewHolder(GroupHolder groupHolder, int position) {
        Group contact = groupList.get(position);
        groupHolder.bindGroup(contact);
    }

    @Override
    public int getItemCount() {
        if(groupList == null)
            return 0;
        else
            return groupList.size();
    }

    public void swap(ArrayList<Group> groupList) {
        if(this.groupList != null)
            this.groupList.clear();
        this.groupList = groupList;
        notifyDataSetChanged();
    }

}
