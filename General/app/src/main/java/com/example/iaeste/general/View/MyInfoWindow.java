package com.example.iaeste.general.View;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.iaeste.general.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;

/*
 Copyright (c) 2012-2014 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class MyInfoWindow implements InfoWindowAdapter {

    private View popup=null;
    private LayoutInflater inflater=null;

    public MyInfoWindow(LayoutInflater inflater) {
        this.inflater=inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return(null);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getInfoContents(Marker marker) {
        if (popup == null) {
            popup=inflater.inflate(R.layout.my_info_window, null);
        }

        //Title
        TextView tv=(TextView)popup.findViewById(R.id.title);
        tv.setText(marker.getTitle());

        //Snippet parameters
        String snippet[] = marker.getSnippet().split("/&");
        String author = snippet[0];
        tv=(TextView)popup.findViewById(R.id.author);
        tv.setText("Author: "+author);

        if(snippet.length>1) {
            String description = snippet[1];
            tv=(TextView)popup.findViewById(R.id.description);
            tv.setText("Description: "+description);
        }

        return(popup);
    }

}
