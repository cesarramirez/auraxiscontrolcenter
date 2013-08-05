package com.cesarandres.ps2link;

import java.net.MalformedURLException;
import java.net.URL;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.cesarandres.ps2link.ApplicationPS2Link.ActivityMode;
import com.cesarandres.ps2link.soe.SOECensus;
import com.cesarandres.ps2link.soe.SOECensus.Game;
import com.cesarandres.ps2link.soe.SOECensus.Verb;
import com.cesarandres.ps2link.soe.content.CharacterEvent;
import com.cesarandres.ps2link.soe.content.response.Characters_event_list_response;
import com.cesarandres.ps2link.soe.util.Collections.PS2Collection;
import com.cesarandres.ps2link.soe.util.QueryString;
import com.cesarandres.ps2link.soe.util.QueryString.QueryCommand;
import com.cesarandres.ps2link.soe.util.QueryString.SearchModifier;
import com.cesarandres.ps2link.soe.view.KillItemAdapter;
import com.cesarandres.ps2link.soe.volley.GsonRequest;

/**
 * Created by cesar on 6/16/13.
 */
public class FragmentStatList extends Fragment {

	private String profileId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View root;
		if (ApplicationPS2Link.isFull()) {
			root = inflater.inflate(R.layout.fragment_server_list, container, false);
			ListView listRoot = (ListView) root.findViewById(R.id.listViewKillList);
			listRoot.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), ActivityProfile.class);
					intent.putExtra("profileId", ((CharacterEvent) myAdapter.getItemAtPosition(myItemInt)).getImportant_character_id());
					startActivity(intent);
				}
			});

			this.profileId = getActivity().getIntent().getExtras().getString("profileId");
		} else {
			root = inflater.inflate(R.layout.fragment_not_available, container, false);
		}

		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
		((Button) getActivity().findViewById(R.id.buttonFragmentTitle)).setText(getString(R.string.title_friends));
		ImageButton updateButton = (ImageButton) getActivity().findViewById(R.id.buttonFragmentUpdate);
		updateButton.setVisibility(View.VISIBLE);
		downloadKillList(this.profileId);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ApplicationPS2Link.volley.cancelAll(this);
	}

	private void setUpdateButton(boolean enabled) {
		getActivity().findViewById(R.id.buttonFragmentUpdate).setEnabled(enabled);

		if (enabled) {
			getActivity().findViewById(R.id.buttonFragmentUpdate).setVisibility(View.VISIBLE);
			getActivity().findViewById(R.id.progressBarFragmentTitleLoading).setVisibility(View.GONE);
		} else {
			getActivity().findViewById(R.id.buttonFragmentUpdate).setVisibility(View.GONE);
			getActivity().findViewById(R.id.progressBarFragmentTitleLoading).setVisibility(View.VISIBLE);
		}

	}

	public void downloadKillList(String character_id) {
		if (ApplicationPS2Link.isFull()) {
			setUpdateButton(false);
			URL url;
			try {

				url = SOECensus.generateGameDataRequest(Verb.GET, Game.PS2, PS2Collection.CHARACTERS_EVENT, character_id,
						QueryString.generateQeuryString().AddCommand(QueryCommand.RESOLVE, "character,attacker").AddCommand(QueryCommand.LIMIT, "100")
								.AddComparison("type", SearchModifier.EQUALS, "DEATH,KILL"));
				Listener<Characters_event_list_response> success = new Response.Listener<Characters_event_list_response>() {
					@Override
					public void onResponse(Characters_event_list_response response) {
						ListView listRoot = (ListView) getActivity().findViewById(R.id.listViewKillList);
						listRoot.setAdapter(new KillItemAdapter(getActivity(), response.getCharacters_event_list(), profileId));
						setUpdateButton(true);
					}
				};

				ErrorListener error = new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						error.equals(new Object());
						setUpdateButton(true);
					}
				};

				GsonRequest<Characters_event_list_response> gsonOject = new GsonRequest<Characters_event_list_response>(url.toString(),
						Characters_event_list_response.class, null, success, error);
				gsonOject.setTag(this);
				ApplicationPS2Link.volley.add(gsonOject);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}