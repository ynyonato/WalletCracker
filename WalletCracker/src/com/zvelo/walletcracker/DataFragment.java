package com.zvelo.walletcracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DataFragment extends ListFragment implements WalletListener {
  protected final String TAG = this.getClass().getSimpleName();
  protected final String RESTORE = ", can restore state";

  protected TextView statusView;
  protected SimpleAdapter listAdapter;
  protected List<Map<String, String>> listData;

  @Override public void walletProgress(BGLoader.Progress progress, DeviceInfoParser parser) {
    switch (progress) {
      case LOADING:
        showLoading();
        break;
      case LOADED:
        showData(parser);
        break;
    }
  }

  @Override public void walletLoaded(BGLoader.Status result, DeviceInfoParser parser) {
    switch (result) {
      case SUCCESS:
        showData(parser);
        break;
      case NO_WALLET:
        showError(R.string.wallet_not_found);
        break;
      case NO_ROOT:
        showError(R.string.root_not_found);
        break;
    }
  }

  private void showLoading() {
    showError(R.string.loading);
  }

  private void showError(int stringId) {
    statusView.setText(stringId);
    listData.clear();
    listAdapter.notifyDataSetChanged();
  }

  private void showData(DeviceInfoParser parser) {
    listData.clear();
    listData.addAll(parser.getData());
    listAdapter.notifyDataSetChanged();
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // savedInstanceState could be null
    if (savedInstanceState != null) {
      // TODO
    }

    statusView = (TextView) getActivity().findViewById(android.R.id.empty);
    listData = new ArrayList<Map<String, String>>();
    listAdapter = new SimpleAdapter(
        getActivity(),
        listData,
        R.layout.datalistitem,
        new String[] {"title", "value"},
        new int[] {R.id.title,
                   R.id.value});
    setListAdapter(listAdapter);
    setHasOptionsMenu(true);

    rebuild(false);

    Log.i(TAG, "onActivityCreated");
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.datalist, container, false);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    Log.i(TAG, "onCreateOptionsMenu");
    inflater.inflate(R.menu.datamenu, menu);
  }

  public void rebuild(Boolean force) {
    showLoading();
    Log.i(TAG, "DataFragment rebuild");
    new BGLoader().execute(this, getActivity(), force);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);
    Log.i(TAG, "onOptionsItemSelected");
    switch (item.getItemId()) {
      case R.id.rebuild:
        rebuild(true);
        break;
    }
    return true;
  }
}