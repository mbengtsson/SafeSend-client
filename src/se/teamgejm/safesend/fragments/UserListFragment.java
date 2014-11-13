package se.teamgejm.safesend.fragments;

import java.util.ArrayList;
import java.util.List;

import se.teamgejm.safesend.R;
import se.teamgejm.safesend.entities.User;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class UserListFragment extends Fragment {
	
	private List<User> users = new ArrayList<User>();
	
	private UserAdapter adapter;
	private ListView userListView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_user_list, container, false);
		userListView = (ListView) view.findViewById(R.id.user_listview);
		setOnItemClickListeners();
		
		loadUsers();
		populateList();
		
		return view;
	}
	
	private void setOnItemClickListeners() {
		userListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Log.i("MessageListFragment", users.get(pos) + " clicked");
			}
		});
	}
	
	private void loadUsers() {
		
		// Mocked users, here we will fetch all users from the server
		
		users.add(new User("Kalle"));
		users.add(new User("Ada"));
		users.add(new User("Eva"));
		users.add(new User("Ludde"));
		users.add(new User("Mats"));
	}
	
	private void populateList() {
		if (adapter == null) {
			adapter = new UserAdapter();
		}
		if (userListView.getAdapter() == null) {
			userListView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}
	
	private class UserAdapter extends ArrayAdapter<User> {
		
		private User currentItem;
		private View currentView;
		
		public UserAdapter() {
			super(getActivity(), R.layout.user_list_item, users);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			currentView = convertView;
			
			if (currentView == null) {
				currentView = getActivity().getLayoutInflater().
						inflate(R.layout.user_list_item, parent, false);
			}
			
			currentItem = users.get(position);
			
			TextView username = (TextView) currentView.findViewById(R.id.user_item_username);
			username.setText(currentItem.getUsername());
			
			return currentView;
		}
	}

}
