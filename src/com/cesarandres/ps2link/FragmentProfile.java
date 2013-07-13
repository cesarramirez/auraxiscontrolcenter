package com.cesarandres.ps2link;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import org.ocpsoft.prettytime.PrettyTime;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.cesarandres.ps2link.base.BaseFragment;
import com.cesarandres.ps2link.module.ObjectDataSource;
import com.cesarandres.ps2link.soe.SOECensus;
import com.cesarandres.ps2link.soe.SOECensus.Game;
import com.cesarandres.ps2link.soe.SOECensus.Verb;
import com.cesarandres.ps2link.soe.content.CharacterProfile;
import com.cesarandres.ps2link.soe.content.Faction;
import com.cesarandres.ps2link.soe.content.response.Character_response_list;
import com.cesarandres.ps2link.soe.util.Collections.PS2Collection;
import com.cesarandres.ps2link.soe.volley.GsonRequest;

/**
 * Created by cesar on 6/16/13.
 */
public class FragmentProfile extends BaseFragment {

	private static boolean isCached;
	private CharacterProfile profile;
	private ArrayList<AsyncTask> taskList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ImageButton updateButton = (ImageButton) getActivity().findViewById(R.id.buttonFragmentUpdate);
		updateButton.setVisibility(View.VISIBLE);

		updateButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				downloadProfiles(profile.getId());
			}
		});

		getActivity().findViewById(R.id.buttonFragmentAppend).setVisibility(View.VISIBLE);

		
		taskList = new ArrayList<AsyncTask>();
		UpdateProfileFromTable task = new UpdateProfileFromTable();
		taskList.add(task);
		task.execute(getActivity().getIntent().getExtras()
				.getString("profileId"));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		View root = inflater.inflate(R.layout.fragment_profile, container,false);
		return root;
	}

	
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ApplicationPS2Link.volley.cancelAll(this);
		for (AsyncTask task : taskList) {
			task.cancel(true);
		}
	}

	private void updateUI(CharacterProfile character) {

		((Button) getActivity().findViewById(R.id.buttonFragmentTitle)).setText(character.getName().getFirst());

		ImageView faction = ((ImageView) getActivity().findViewById(R.id.imageViewProfileFaction));
		if (character.getFaction_id().equals(Faction.VS)) {
			faction.setImageResource(R.drawable.vs_icon);
		} else if (character.getFaction_id().equals(Faction.NC)) {
			faction.setImageResource(R.drawable.nc_icon);
		} else if (character.getFaction_id().equals(Faction.TR)) {
			faction.setImageResource(R.drawable.tr_icon);
		}

		TextView initialBR = ((TextView) getActivity().findViewById(R.id.textViewCurrentRank));
		initialBR.setText(Integer.toString(character.getBattle_rank().getValue()));
		initialBR.setTextColor(Color.BLACK);

		TextView nextBR = ((TextView) getActivity().findViewById(R.id.textViewNextRank));
		nextBR.setText(Integer.toString(character.getBattle_rank().getValue() + 1));
		nextBR.setTextColor(Color.BLACK);

		int progressBR = (character.getBattle_rank().getPercent_to_next());
		((ProgressBar) getActivity().findViewById(R.id.progressBarProfileBRProgress)).setProgress((int) (progressBR));

		Float progressCerts = Float.parseFloat(character.getCerts().getPercent_to_next());
		((ProgressBar) getActivity().findViewById(R.id.progressBarProfileCertsProgress)).setProgress((int) (progressCerts * 100));
		TextView certs = ((TextView) getActivity().findViewById(R.id.textViewProfileCertsValue));
		certs.setText(character.getCerts().getAvailable_points());

		((TextView) getActivity().findViewById(R.id.textViewProfileMinutesPlayed)).setText(Integer.toString((Integer.parseInt(character.getTimes().getMinutes_played()) / 60)));

		PrettyTime p = new PrettyTime();
		String lastLogin = p.format(new Date(Long.parseLong(character.getTimes().getLast_login()) * 1000));

		((TextView) getActivity().findViewById(R.id.textViewProfileLastLogin)).setText(lastLogin);

		((Button) getActivity().findViewById(R.id.buttonProfileFriends)).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Toast.makeText(getActivity(),
								"This will be implemented later",
								Toast.LENGTH_SHORT).show();
					}
				});

		ToggleButton star = (ToggleButton) getActivity().findViewById(
				R.id.buttonFragmentStar);
		star.setVisibility(View.VISIBLE);
		star.setOnCheckedChangeListener(null);
		SharedPreferences settings = getActivity().getSharedPreferences("PREFERENCES", 0);
		String preferedProfileId = settings.getString("preferedProfile", "");
		if (preferedProfileId.equals(character.getId())) {
			star.setChecked(true);
		} else {
			star.setChecked(false);
		}

		star.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SharedPreferences settings = getActivity().getSharedPreferences("PREFERENCES", 0);
				SharedPreferences.Editor editor = settings.edit();
				if (isChecked) {
					editor.putString("preferedProfile", profile.getId());
					editor.putString("preferedProfileName", profile.getName().getFirst());
				} else {
					editor.putString("preferedProfileName", "");
					editor.putString("preferedProfile", "");
				}
				editor.commit();
			}
		});

		ToggleButton append = ((ToggleButton) getActivity().findViewById(R.id.buttonFragmentAppend));
		append.setOnCheckedChangeListener(null);
		append.setChecked(isCached);
		append.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					CacheProfile task = new CacheProfile();
					taskList.add(task);
					task.execute(profile);
				} else {
					UnCacheProfile task = new UnCacheProfile();
					taskList.add(task);
					task.execute(profile);
				}
			}
		});
	}

	private void setActionBarEnabled(boolean enabled) {
		getActivity().findViewById(R.id.buttonFragmentUpdate).setEnabled(enabled);
		getActivity().findViewById(R.id.buttonFragmentAppend).setEnabled(enabled);
		getActivity().findViewById(R.id.buttonFragmentStar).setEnabled(enabled);
		if (enabled) {
			View loadingView = getActivity().findViewById(R.id.loadingItemList);
			if (loadingView != null) {
				LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.linearLayoutProfile);
				layout.removeView(loadingView);
			}
		} else {
			View loadingView = getActivity().findViewById(R.id.loadingItemList);
			if (loadingView == null) {
				LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.linearLayoutProfile);
				loadingView = getActivity().getLayoutInflater().inflate(R.layout.loading_item_list, null);
				layout.addView(loadingView, 0);
			}
		}
	}

	private void downloadProfiles(String character_id) {

		setActionBarEnabled(false);
		URL url;
		try {
			url = SOECensus.generateGameDataRequest(Verb.GET, Game.PS2,PS2Collection.CHARACTER, character_id, null);

			Listener<Character_response_list> success = new Response.Listener<Character_response_list>() {
				@Override
				public void onResponse(Character_response_list response) {
					profile = response.getCharacter_list().get(0);
					setActionBarEnabled(true);
					updateUI(profile);
				}
			};

			ErrorListener error = new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					error.equals(new Object());
					setActionBarEnabled(true);
				}
			};

			GsonRequest<Character_response_list> gsonOject = new GsonRequest<Character_response_list>(
					url.toString(), Character_response_list.class, null, success,
					error);
			gsonOject.setTag(this);
			ApplicationPS2Link.volley.add(gsonOject);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class UpdateProfileFromTable extends AsyncTask<String, Integer, CharacterProfile> {

		private String profile_id;

		@Override
		protected void onPreExecute() {
			setActionBarEnabled(false);
		}

		@Override
		protected CharacterProfile doInBackground(String... args) {
			ObjectDataSource data = new ObjectDataSource(getActivity());
			data.open();
			this.profile_id = args[0];
			CharacterProfile profile = data.getCharacter(this.profile_id);
			if (profile == null) {
				isCached = false;
			} else {
				isCached = profile.isCached();
			}
			data.close();
			return profile;
		}

		@Override
		protected void onPostExecute(CharacterProfile result) {
			if (result == null) {
				downloadProfiles(profile_id);
			} else {
				profile = result;
				updateUI(result);
			}
			setActionBarEnabled(true);
			taskList.remove(this);
		}
	}

	private class CacheProfile extends
			AsyncTask<CharacterProfile, Integer, CharacterProfile> {

		@Override
		protected void onPreExecute() {
			setActionBarEnabled(false);
		}

		@Override
		protected CharacterProfile doInBackground(CharacterProfile... args) {
			ObjectDataSource data = new ObjectDataSource(getActivity());
			data.open();
			try {
				CharacterProfile profile = args[0];
				if(data.getCharacter(profile.getId()) == null){
					data.insertCharacter(profile, false);
				}else{
					data.updateCharacter(profile, false);
				}
				isCached = true;
			} finally {
				data.close();
			}
			return profile;
		}

		@Override
		protected void onPostExecute(CharacterProfile result) {
			if (!this.isCancelled()) {
				profile = result;
				updateUI(result);
				setActionBarEnabled(true);
			}
			taskList.remove(this);
		}
	}

	private class UnCacheProfile extends
			AsyncTask<CharacterProfile, Integer, CharacterProfile> {

		@Override
		protected void onPreExecute() {
			setActionBarEnabled(false);
		}

		@Override
		protected CharacterProfile doInBackground(CharacterProfile... args) {
			ObjectDataSource data = new ObjectDataSource(getActivity());
			data.open();
			CharacterProfile profile = args[0];
			data.updateCharacter(profile, true);
			isCached = false;

			data.close();
			return profile;
		}

		@Override
		protected void onPostExecute(CharacterProfile result) {
			if (!this.isCancelled()) {
				profile = result;
				updateUI(result);
				setActionBarEnabled(true);
			}
			taskList.remove(this);

		}
	}

}
