package se.teamgejm.safesend.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import se.teamgejm.safesend.R;
import se.teamgejm.safesend.entities.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Emil Stjerneman
 */
public class UserAdapter extends BaseAdapter {

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
        holder.username.setText(user.getDisplayName());

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


}
