package me.hyunbin.transit.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.hyunbin.transit.R;

/**
 * This fragment contains the search bar and the results from the search.
 */

public class DosSearchFragment extends Fragment {

  @Override
  public View onCreateView(
      LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_dos_search, container, false);
    return v;
  }


}