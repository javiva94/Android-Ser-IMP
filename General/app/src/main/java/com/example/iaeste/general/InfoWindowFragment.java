package com.example.iaeste.general;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.iaeste.general.Model.MapObject;
import com.example.iaeste.general.Model.MyPolygon;
import com.example.iaeste.general.Model.MyPolyline;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InfoWindowFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InfoWindowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoWindowFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private MyPolyline myPolyline;
    private MyPolygon myPolygon;

    private OnFragmentInteractionListener mListener;

    public InfoWindowFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoWindowFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoWindowFragment newInstance(String param1, String param2) {
        InfoWindowFragment fragment = new InfoWindowFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if(getArguments().containsKey("MyPolyline")){
                myPolyline = (MyPolyline) getArguments().get("MyPolyline");
            }
            if(getArguments().containsKey("MyPolygon")){
                myPolygon = (MyPolygon) getArguments().get("MyPolygon");
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info_window, container, false);


        if(myPolyline!=null){
            inflatePolylineInfoWindowFragment(view);
        }else{
            if (myPolygon!=null){
                inflatePolygonInfoWindowFragment(view);
            }
        }

        return view;
    }

    private void inflatePolylineInfoWindowFragment(View view){
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(myPolyline.getTitle());

        TextView author = (TextView) view.findViewById(R.id.author);
        author.setText("Author: "+myPolyline.getAuthor());

        TextView description = (TextView) view.findViewById(R.id.description);
        description.setText("Description: " + myPolyline.getDescription());

        TextView geometryInfo = (TextView) view.findViewById(R.id.geometry_info);
        geometryInfo.setText("Length: "+String.format("%.2f",myPolyline.getLength())+" m");
    }

    private void inflatePolygonInfoWindowFragment(View view){
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(myPolygon.getTitle());

        TextView author = (TextView) view.findViewById(R.id.author);
        author.setText("Author: "+myPolygon.getAuthor());

        TextView description = (TextView) view.findViewById(R.id.description);
        description.setText("Description: " + myPolygon.getDescription());

        TextView geometryInfo = (TextView) view.findViewById(R.id.geometry_info);
        geometryInfo.setText("Area: "+String.format("%.2f",myPolygon.getArea())+" m\u00B2");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
