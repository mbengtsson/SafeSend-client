package se.teamgejm.safesend.fragments;

import se.teamgejm.safesend.R;
import se.teamgejm.safesend.activities.OpenMessageActivity;
import se.teamgejm.safesend.adapters.MessageAdapter;
import se.teamgejm.safesend.entities.Message;
import se.teamgejm.safesend.events.MessageListFailedEvent;
import se.teamgejm.safesend.events.MessageListSuccessEvent;
import se.teamgejm.safesend.rest.FetchMessageList;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import de.greenrobot.event.EventBus;

/**
 * 
 * @author Gustav
 *
 */
public class MessageListFragment extends Fragment {

    private MessageAdapter adapter;
    private ListView messageListView;
    
    private ProgressBar messageListProgressBar;
    
    private static final String TAG = "MessageListFragment";

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);
        messageListView = (ListView) view.findViewById(R.id.message_listview);
        setOnItemClickListeners();
        setListViewHeader();
        
        messageListProgressBar = (ProgressBar) view.findViewById(R.id.message_list_progress_bar);

        adapter = new MessageAdapter(getActivity());
        messageListView.setAdapter(adapter);

        startLoading();

        return view;
    }
    
	private void setListViewHeader() {
		Button btn = new Button(getActivity());
		btn.setText(R.string.button_update_messages);
		btn.setBackgroundColor(Color.parseColor("#FABD69"));
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        startLoading();
			}
		});
		messageListView.addHeaderView(btn);
	}
	
	
    
    @Override
    public void onStart () {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop () {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    
    public void onEvent (MessageListFailedEvent event) {
    	Toast.makeText(getActivity(), getString(R.string.failed_message_list), Toast.LENGTH_SHORT).show();
        stopLoading();
    }

    public void onEvent (MessageListSuccessEvent event) {
        adapter.clearMessages();
        for (Message message : event.getMessages()) {
            adapter.addMessage(message);
        	Log.d(TAG, message.toString());
        }
        adapter.notifyDataSetChanged();
        stopLoading();
    }

    private void setOnItemClickListeners () {
        messageListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick (AdapterView<?> parent, View view, int pos,
                                     long id) {
            	final Message message = adapter.getMessage(pos - 1);
            	Log.d(TAG, String.valueOf(message.getMessageId()));
				final Intent intent = new Intent(getActivity(), OpenMessageActivity.class);
				intent.putExtra(OpenMessageActivity.INTENT_MESSAGE, message);
				getActivity().startActivity(intent);
            }
        });
    }
    
    private void startLoading () {
        messageListProgressBar.setVisibility(View.VISIBLE);
        FetchMessageList.call();
    }

    private void stopLoading () {
    	messageListProgressBar.setVisibility(View.GONE);
    }


}
