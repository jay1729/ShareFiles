package com.gumballi.jay.sharefiles;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by jay on 2/1/18.
 */

public class PeersViewHolder extends RecyclerView.ViewHolder {

    private TextView peerName;
    public LinearLayout peerButton;
    private TextView peerAddress;
    public WifiP2pDevice device;

    public PeersViewHolder(View itemView) {
        super(itemView);
        peerName=itemView.findViewById(R.id.peerName);
        peerButton=itemView.findViewById(R.id.peerButton);
        peerAddress=itemView.findViewById(R.id.peerAddress);
    }

    public void setPeer(WifiP2pDevice peer){
        device=peer;
        peerName.setText(peer.deviceName);
        peerAddress.setText(peer.deviceAddress);
        Log.d("Set Peer","setPeer");
    }
}
