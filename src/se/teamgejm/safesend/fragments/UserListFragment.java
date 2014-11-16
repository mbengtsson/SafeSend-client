package se.teamgejm.safesend.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import de.greenrobot.event.EventBus;
import se.teamgejm.safesend.R;
import se.teamgejm.safesend.activities.SendMessageActivity;
import se.teamgejm.safesend.adapters.UserAdapter;
import se.teamgejm.safesend.entities.User;
import se.teamgejm.safesend.events.UserListFailedEvent;
import se.teamgejm.safesend.events.UserListSuccessEvent;
import se.teamgejm.safesend.rest.FetchUserList;

public class UserListFragment extends Fragment {

    private final static String TAG = "UserListFragment";

    private UserAdapter adapter;
    private ListView userListView;
    private ProgressBar userListProgressBar;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        userListView = (ListView) view.findViewById(R.id.user_listview);
        setOnItemClickListeners();

        userListProgressBar = (ProgressBar) view.findViewById(R.id.user_list_progress_bar);

        adapter = new UserAdapter(getActivity());
        userListView.setAdapter(adapter);

        startLoading();

        return view;
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

    public void onEvent (UserListFailedEvent event) {
        // TODO: Show error message?
        stopLoading();
    }

    public void onEvent (UserListSuccessEvent event) {
        adapter.clearUsers();
        for (User user : event.getUsers()) {
            adapter.addUser(user);
        }
        adapter.notifyDataSetChanged();
        stopLoading();
    }

    private void setOnItemClickListeners () {
        userListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick (AdapterView<?> parent, View view, int pos, long id) {
                final User user = adapter.getUser(pos);
                final Intent intent = new Intent(getActivity(), SendMessageActivity.class);
                intent.putExtra(SendMessageActivity.INTENT_RECEIVER, user);
                getActivity().startActivity(intent);
            }
        });
    }

    private void startLoading () {
        userListProgressBar.setVisibility(View.VISIBLE);
        FetchUserList.call();
    }

    private void stopLoading () {
        userListProgressBar.setVisibility(View.GONE);
    }
}
