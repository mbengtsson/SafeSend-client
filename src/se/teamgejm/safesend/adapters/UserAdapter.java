package se.teamgejm.safesend.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.teamgejm.safesend.R;
import se.teamgejm.safesend.entities.User;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Adapter for displaying users.
 * @author Emil Stjerneman
 */
@SuppressLint("UseSparseArrays")
public class UserAdapter extends BaseAdapter {
	
	/**
	 * Map of user id and the amount of new messages.
	 */
	private Map<Long, Integer> newMessagesByUserId = new HashMap<Long, Integer>();

    private Activity mContext;

    /**
     * A list of users.
     */
    private List<User> mItems;

    /**
     * Static holder.
     */
    static class UserHolder {

        TextView username;
    }

    public UserAdapter (Activity context) {
        super();
        mContext = context;
        mItems = new ArrayList<>();
    }

    /**
     * Adds a user to the user list.
     *
     * @param user
     *         the user to add.
     */
    public void addUser (User user) {
        if (!mItems.contains(user)) {
            mItems.add(user);
        }

        Collections.sort(mItems);
    }

    /**
     * Gets a user from the list with the given position.
     *
     * @param position
     *         the position to get the user from.
     *
     * @return a user.
     */
    public User getUser (int position) {
        return mItems.get(position);
    }

    /**
     * Clears the user list.
     */
    public void clearUsers () {
        mItems.clear();
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        UserHolder holder = null;

        if (convertView == null) {
            final LayoutInflater inflater = mContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.user_list_item, parent, false);

            holder = new UserHolder();
            holder.username = (TextView) convertView.findViewById(R.id.user_item_username);

            convertView.setTag(holder);
        }
        else {
            holder = (UserHolder) convertView.getTag();
        }

        final User user = mItems.get(position);
        
    	if (newMessagesByUserId.containsKey(user.getUserId())) {
        	holder.username.setTypeface(Typeface.DEFAULT_BOLD);
        	holder.username.setText(user.getDisplayName() + " (" + newMessagesByUserId.get(user.getUserId()) + ")");
        	convertView.setBackgroundResource(R.color.green);
        } else {
        	holder.username.setTypeface(Typeface.DEFAULT);
            holder.username.setText(user.getDisplayName());
        	convertView.setBackgroundResource(R.color.lightgreen);
        }

        return convertView;
    }

    @Override
    public int getCount () {
        return mItems.size();
    }

    @Override
    public User getItem (int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId (int position) {
        return position;
    }
    
    public void setNewMessagesByUserId(Map<Long, Integer> newMessagesByUserId) {
    	this.newMessagesByUserId = newMessagesByUserId;
    }

}
