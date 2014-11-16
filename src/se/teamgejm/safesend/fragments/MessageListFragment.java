package se.teamgejm.safesend.fragments;

import se.teamgejm.safesend.R;
import se.teamgejm.safesend.adapters.MessageAdapter;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MessageListFragment extends Fragment {
	
	private MessageAdapter adapter;
	private ListView messageListView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_message_list, container, false);
		messageListView = (ListView) view.findViewById(R.id.message_listview);
		setOnItemClickListeners();
		
		adapter = new MessageAdapter(getActivity());
		messageListView.setAdapter(adapter);
		
		return view;
	}
	
	private void setOnItemClickListeners() {
		messageListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
//				Log.i("MessageListFragment", messages.get(pos) + " clicked");
//				Intent intent = new Intent(getActivity(), OpenMessageActivity.class);
//				intent.putExtra(OpenMessageActivity.INTENT_MESSAGE, messages.get(pos));
//				getActivity().startActivity(intent);
			}
		});
	}
	

}
