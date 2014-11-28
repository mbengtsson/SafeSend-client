package se.teamgejm.safesend.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import se.teamgejm.safesend.R;
import se.teamgejm.safesend.entities.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Gustav
 */
public class MessageAdapter extends BaseAdapter {

    private Activity mContext;

    /**
     * A list of messages.
     */
    private List<Message> mItems;

    /**
     * Static holder.
     */
    static class MessageHolder {

        TextView username;
        TextView timestamp;
        TextView messageReceivedText;
        TextView messageSentText;
    }

    public MessageAdapter (Activity context) {
        super();
        this.mContext = context;
        this.mItems = new ArrayList<>();
    }

    /**
     * Adds a message to the message list.
     *
     * @param user
     *         the user to add.
     */
    public void addMessage (Message message) {
        if (!mItems.contains(message)) {
            mItems.add(message);
        }
    }

    /**
     * Gets a message from the list with the given position.
     *
     * @param position
     *         the position to get the message from.
     *
     * @return a message.
     */
    public Message getMessage (int position) {
        return mItems.get(position);
    }

    /**
     * Clears the message list.
     */
    public void clearMessages () {
        mItems.clear();
    }

    @Override
    public int getCount () {
        return mItems.size();
    }

    @Override
    public Object getItem (int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId (int position) {
        return position;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        MessageHolder holder = null;

        if (convertView == null) {
            final LayoutInflater inflater = mContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.message_list_item, parent, false);

            holder = new MessageHolder();
            holder.messageReceivedText = (TextView) convertView.findViewById(R.id.message_item_received_text);
            holder.messageSentText = (TextView) convertView.findViewById(R.id.message_item_sent_text);

            convertView.setTag(holder);
        }
        else {
            holder = (MessageHolder) convertView.getTag();
        }

        final Message message = this.getMessage(position);

        Date date = new Date(message.getTimeStamp());

        //holder.username.setText(message.getSender().getDisplayName());

        holder.messageReceivedText.setText(message.getMessage());
        holder.messageSentText.setText(message.getMessage());

        return convertView;
    }

}
