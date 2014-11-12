package se.teamgejm.safesend.fragments;

import se.teamgejm.safesend.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class UserListFragment extends Fragment {
	
	private String[] mockUsers = new String[]{"Kalle", "Adam", "Eva", "Ludde", "Hater", "Jonas", "Lundell"};
	private UserAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_user_list, container, false);
		ListView userListView = (ListView) view.findViewById(R.id.user_listview);
		
		if (adapter == null) {
			adapter = new UserAdapter();
		}
		if (userListView.getAdapter() == null) {
			userListView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
		
		return view;
	}
	
	private class UserAdapter extends ArrayAdapter<String> {
		
		private String currentItem;
		private View currentView;
		
		public UserAdapter() {
			super(getActivity(), R.layout.user_list_item, mockUsers);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			currentView = convertView;
			
			if (currentView == null) {
				currentView = getActivity().getLayoutInflater().
						inflate(R.layout.user_list_item, parent, false);
			}
			
			currentItem = mockUsers[position];
			
			TextView username = (TextView) currentView.findViewById(R.id.mockText);
			username.setText(currentItem);
			
			return currentView;
		}
	}

}
