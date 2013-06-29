package com.cesarandres.ps2link;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cesarandres.ps2link.base.BaseFragment;
import com.cesarandres.ps2link.module.ObjectDataSource;
import com.cesarandres.ps2link.soe.content.CharacterProfile;
import com.cesarandres.ps2link.soe.view.ProfileItemAdapter;

/**
 * Created by cesar on 6/16/13.
 */
public class FragmentProfileList extends BaseFragment {



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.fragment_profile_list, container,
				false);

		new ReadProfilesTable().execute();

		((TextView) root.findViewById(R.id.textViewFragmentTitle))
				.setText("List of Profiles");

		ListView listRoot = (ListView) root
				.findViewById(R.id.listViewProfileList);
		listRoot.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> myAdapter, View myView,
					int myItemInt, long mylng) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), ActivityProfile.class);
				intent.putExtra("profileId", ((CharacterProfile) myAdapter
						.getItemAtPosition(myItemInt)).getId());
				startActivity(intent);
			}
		});

		Button updateButton = (Button) root
				.findViewById(R.id.buttonFragmentUpdate);
		updateButton.setVisibility(View.VISIBLE);

		updateButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new ReadProfilesTable().execute();
			}
		});

		Button searchButton = (Button) root
				.findViewById(R.id.buttonFragmentAdd);
		searchButton.setVisibility(View.VISIBLE);

		searchButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), ActivityAddProfile.class);
				startActivity(intent);
			}
		});

		return root;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		new ReadProfilesTable().execute();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_profile_list_menu, menu);
	}

	private class ReadProfilesTable extends
			AsyncTask<Integer, Integer, ArrayList<CharacterProfile>> {

		@Override
		protected ArrayList<CharacterProfile> doInBackground(Integer... params) {
			ObjectDataSource data = new ObjectDataSource(getActivity());
			data.open();
			ArrayList<CharacterProfile> tmpProfileList = data
					.getAllCharacterProfiles();
			data.close();
			return tmpProfileList;
		}

		@Override
		protected void onPostExecute(ArrayList<CharacterProfile> result) {
			Toast.makeText(getActivity(), "Used profiles from DB",
					Toast.LENGTH_SHORT).show();
			ListView listRoot = (ListView) getActivity().findViewById(
					R.id.listViewProfileList);
			listRoot.setAdapter(new ProfileItemAdapter(getActivity(), result));
		}

	}

	
}
