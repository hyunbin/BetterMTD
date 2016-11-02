package me.hyunbin.transit.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.List;

import me.hyunbin.transit.AutoCompleteClient;
import me.hyunbin.transit.R;
import me.hyunbin.transit.adapters.AutoCompleteAdapter;
import me.hyunbin.transit.helpers.LayoutUtil;
import me.hyunbin.transit.models.AutoCompleteItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This fragment contains the search bar and the results from the search.
 */

public class DosSearchFragment extends Fragment {

  private static final String TAG = DosSearchFragment.class.getSimpleName();

  private static final int ANIMATION_DURATION_MS = 400;
  private static final int SEARCH_SPAN_COUNT = 2;

  private AutoCompleteClient mAutoCompleteClient;
  private Callback<List<AutoCompleteItem>> mAutoCompleteResponse;
  private InputMethodManager mInputMethodManager;

  private AutoCompleteAdapter mAutoCompleteAdapter;
  private EditText mSearchText;
  private RecyclerView mAutoCompleteList;
  private View mCancelButton;
  private View mSearchContainer;
  private View mSearchTopBar;

  private boolean mIsInSearchMode = false;

  @Override
  public View onCreateView(
      LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_dos_search, container, false);

    mSearchContainer = v.findViewById(R.id.search_container);
    mSearchTopBar = v.findViewById(R.id.search_top_bar);

    mAutoCompleteClient = new AutoCompleteClient();
    mAutoCompleteResponse = new Callback<List<AutoCompleteItem>>() {
      @Override
      public void onResponse(Response<List<AutoCompleteItem>> response) {
        if (response.isSuccess()) {
          updateAutoCompleteData(response.body());
        } else {
          Log.d(TAG, "Retrofit Error: " + response.errorBody().toString());
          // TODO: Show network error.
        }
      }

      @Override
      public void onFailure(Throwable t) {
        Log.d(TAG, "Retrofit Error: " + t.toString());
        // TODO: Show network error.
      }
    };

    mInputMethodManager = (InputMethodManager) getActivity()
        .getSystemService(Context.INPUT_METHOD_SERVICE);
    mCancelButton = v.findViewById(R.id.cancel_button);
    mCancelButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        cancelSearchMode();
      }
    });

    mSearchText = (EditText) v.findViewById(R.id.search_text);
    mSearchText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startSearchMode();
      }
    });
    mSearchText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void afterTextChanged(Editable editable) {
        String query = editable.toString();
        Call<List<AutoCompleteItem>> suggestions = mAutoCompleteClient.getSuggestions(query);
        suggestions.enqueue(mAutoCompleteResponse);
      }
    });
    mSearchText.setFocusable(true);
    mSearchText.setFocusableInTouchMode(true);

    mAutoCompleteList = (RecyclerView) v.findViewById(R.id.search_suggestions_list);
    setupRecyclerView(mAutoCompleteList);

    return v;
  }

  /**
   * A function to override the back pressed functionality to cancel search view as necessary.
   *
   * @return true if back pressed was handled by fragment, false otherwise
   */
  public boolean onBackPressed() {
    if (mIsInSearchMode) {
      cancelSearchMode();
      return true;
    }
    return false;
  }

  private void startSearchMode() {
    mIsInSearchMode = true;
    mSearchText.requestFocus();
    mSearchText.setCursorVisible(true);
    mCancelButton.setVisibility(View.VISIBLE);

    setSearchContainerHeight(mSearchContainer, 5);
  }

  private void cancelSearchMode() {
    mIsInSearchMode = false;
    mCancelButton.setVisibility(View.GONE);
    mSearchText.getText().clear();
    mSearchText.setCursorVisible(false);
    mInputMethodManager.hideSoftInputFromWindow(mCancelButton.getWindowToken(), 0);

    setSearchContainerHeight(mSearchContainer, 0);
  }

  private void setupRecyclerView(RecyclerView view) {
    view.getItemAnimator().setAddDuration(ANIMATION_DURATION_MS);
    view.getItemAnimator().setRemoveDuration(ANIMATION_DURATION_MS);

    LinearLayoutManager layoutManager;
    layoutManager = new LinearLayoutManager(getActivity());

    view.setLayoutManager(layoutManager);

    setSearchContainerHeight(view, 0);
  }

  private void setSearchContainerHeight(final View view, final int itemCount) {
    final int initialHeight = view.getMeasuredHeight();
    if (initialHeight == 0) {
      return;
    }
    final int minimumHeight = mSearchTopBar.getMeasuredHeight() + LayoutUtil.dpToPx(6);
    final int targetHeight = minimumHeight + itemCount * LayoutUtil.dpToPx(52); // TODO: number is completely arbitrary here

    Animation anim = new Animation() {
      @Override
      protected void applyTransformation(float interpolatedTime, Transformation t) {
        view.getLayoutParams().height =
            (int) (initialHeight * (1 - interpolatedTime) + targetHeight * interpolatedTime);
        view.requestLayout();
      }

      @Override
      public boolean willChangeBounds() {
        return true;
      }
    };

    anim.setDuration(ANIMATION_DURATION_MS);
    anim.setInterpolator(new FastOutSlowInInterpolator());

    view.setPivotY(0f);
    view.startAnimation(anim);
  }

  private void updateAutoCompleteData(List<AutoCompleteItem> data) {
    if (mAutoCompleteAdapter == null) {
      mAutoCompleteAdapter = new AutoCompleteAdapter(data);
      mAutoCompleteList.setAdapter(mAutoCompleteAdapter);
      mAutoCompleteAdapter.notifyItemRangeInserted(0, data.size() - 1);
    } else {
      mAutoCompleteAdapter.swapData(data);
      mAutoCompleteAdapter.notifyDataSetChanged();
    }
  }
}
