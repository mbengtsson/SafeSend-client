package se.teamgejm.safesend.adapters;

import java.util.ArrayList;
import java.util.List;

import se.teamgejm.safesend.R;
import se.teamgejm.safesend.entities.Message;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 
 * @author Gustav
 *
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
		TextView messageType;
	}
	
	public MessageAdapter(Activity context) {
		super();
		this.mContext = context;
		this.mItems = new ArrayList<Message>();
	}
	
	/**
     * Adds a message to the message list.
     *
     * @param user
     *         the user to add.
     */
	public void addMessage(Message message) {
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
	public Message getMessage(int position) {
		return mItems.get(position);
	}
	
	/**
	 * Clears the message list.
	 */
	public void clearMessages() {
		mItems.clear();
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MessageHolder holder = null;

        if (convertView == null) {
            final LayoutInflater inflater = mContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.user_list_item, parent, false);

            holder = new MessageHolder();
            holder.username = (TextView) convertView.findViewById(R.id.user_item_username);
            holder.timestamp = (TextView) convertView.findViewById(R.id.message_time);
            holder.messageType = (TextView) convertView.findViewById(R.id.message_type);

            convertView.setTag(holder);
        }
        else {
            holder = (MessageHolder) convertView.getTag();
        }

        final Message message = mItems.get(position);

        final Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fontawesome-webfont.ttf");
        final TextView icon = (TextView) convertView.findViewById(R.id.user_item_icon);
        icon.setTypeface(font);

        holder.username.setText(message.getOrigin().getDisplayName());
        holder.timestamp.setText(message.getTimestamp());
        holder.messageType.setText(message.getMessageType().getNiceName());

        return convertView;
	}

}
