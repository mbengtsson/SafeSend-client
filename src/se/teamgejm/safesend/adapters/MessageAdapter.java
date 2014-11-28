package se.teamgejm.safesend.adapters;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import se.teamgejm.safesend.R;
import se.teamgejm.safesend.entities.CurrentUser;
import se.teamgejm.safesend.entities.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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

        TextView messageTextReceiver;
        TextView messageDateReceiver;
        TextView messageTextSender;
        TextView messageDateSender;

        RelativeLayout senderContainer;
        RelativeLayout receiverContainer;
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

            convertView = inflater.inflate(R.layout.message_list_item, parent, false);

            holder = new MessageHolder();
            holder.messageTextReceiver = (TextView) convertView.findViewById(R.id.message_item_text_receiver);
            holder.messageDateReceiver = (TextView) convertView.findViewById(R.id.message_item_date_receiver);
            holder.messageTextSender = (TextView) convertView.findViewById(R.id.message_item_text_sender);
            holder.messageDateSender = (TextView) convertView.findViewById(R.id.message_item_date_sender);

            holder.senderContainer = (RelativeLayout) convertView.findViewById(R.id.senderContainer);
            holder.receiverContainer = (RelativeLayout) convertView.findViewById(R.id.receiverContainer);

            convertView.setTag(holder);
        }
        else {
            holder = (MessageHolder) convertView.getTag();
        }

        if (message.getSender().getUserId() == CurrentUser.getInstance().getUserId()) {
            holder.messageDateReceiver.setText(this.getDate(message.getTimeStamp()));
            holder.messageTextReceiver.setText(message.getMessage());
            holder.senderContainer.setVisibility(View.GONE);
            holder.receiverContainer.setVisibility(View.VISIBLE);
        }
        else {
            holder.messageDateSender.setText(this.getDate(message.getTimeStamp()));
            holder.messageTextSender.setText(message.getMessage());
            holder.receiverContainer.setVisibility(View.GONE);
            holder.senderContainer.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    private String getDate (long time) {
        final Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        final String date = DateFormat.format("yyyy-MM-dd hh:mm:ss", cal).toString();
        return date;
    }

}
