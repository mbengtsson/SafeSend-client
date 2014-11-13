package se.teamgejm.safesend.fragments;

import java.util.ArrayList;
import java.util.List;

import se.teamgejm.safesend.R;
import se.teamgejm.safesend.entities.Message;
import se.teamgejm.safesend.enums.MessageType;
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

public class MessageListFragment extends Fragment {
	
	private List<Message> messages = new ArrayList<Message>();
	
	private MessageAdapter adapter;
	private ListView messageListView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_message_list, container, false);
		messageListView = (ListView) view.findViewById(R.id.message_listview);
		setOnItemClickListeners();
		
		loadMessages();
		populateList();
		
		return view;
	}
	
	private void setOnItemClickListeners() {
		messageListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Log.i("MessageListFragment", messages.get(pos) + " clicked");
			}
		});
	}
	
	private void loadMessages() {
		
		// Mocked messages, here we will fetch all messages from the server
		
		messages.add(new Message("Kalle", "28 okt 14:48", MessageType.TEXT));
		messages.add(new Message("Eva", "27 okt 18:12", MessageType.TEXT));
	}
	
	private void populateList() {
		if (adapter == null) {
			adapter = new MessageAdapter();
		}
		if (messageListView.getAdapter() == null) {
			messageListView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}
	
	private class MessageAdapter extends ArrayAdapter<Message> {
		
		private Message currentItem;
		private View currentView;
		
		public MessageAdapter() {
			super(getActivity(), R.layout.message_list_item, messages);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			currentView = convertView;
			
			if (currentView == null) {
				currentView = getActivity().getLayoutInflater().
						inflate(R.layout.message_list_item, parent, false);
			}
			
			currentItem = messages.get(position);
			
			TextView origin = (TextView) currentView.findViewById(R.id.message_item_origin);
			origin.setText(getString(R.string.from) + " " + currentItem.getOrigin());
			
			TextView time = (TextView) currentView.findViewById(R.id.message_item_time);
			time.setText(currentItem.getTimestamp());
			
			TextView type = (TextView) currentView.findViewById(R.id.message_item_type);
			type.setText(getString(R.string.type) + " " + currentItem.getMessageType().getNiceName());
			
			return currentView;
		}
	}

}
