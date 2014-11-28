package se.teamgejm.safesend.adapters;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import se.teamgejm.safesend.R;
import se.teamgejm.safesend.entities.CurrentUser;
import se.teamgejm.safesend.entities.Message;

import java.util.*;

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

        TextView messageText;
        TextView messageDate;
    }

    public MessageAdapter (Activity context) {
        super();
        this.mContext = context;
        this.mItems = new ArrayList<>();
    }

    /**
     * Adds a message to the message list.
     *
     * @param message
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

        final Message message = this.getMessage(position);

        if (convertView == null) {
            final LayoutInflater inflater = mContext.getLayoutInflater();

            if (message.getSender().getUserId() == CurrentUser.getInstance().getUserId()) {
                convertView = inflater.inflate(R.layout.message_list_item_sent, parent, false);
            }
            else {
                convertView = inflater.inflate(R.layout.message_list_item_received, parent, false);
            }


            holder = new MessageHolder();
            holder.messageText = (TextView) convertView.findViewById(R.id.message_item_text);
            holder.messageDate = (TextView) convertView.findViewById(R.id.message_item_date);

            convertView.setTag(holder);
        }
        else {
            holder = (MessageHolder) convertView.getTag();
        }

        Date date = new Date(message.getTimeStamp());

        //holder.username.setText(message.getSender().getDisplayName());

        holder.messageDate.setText(this.getDate(message.getTimeStamp()));
        holder.messageText.setText(message.getMessage());

        return convertView;
    }

    private String getDate (long time) {
        final Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        final String date = DateFormat.format("yyyy-MM-dd hh:mm:ss", cal).toString();
        return date;
    }

}
