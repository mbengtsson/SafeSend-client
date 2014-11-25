package se.teamgejm.safesend.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import se.teamgejm.safesend.R;
import se.teamgejm.safesend.database.dao.DbUserDao;

import java.security.Security;

/**
 * @author Gustav
 */
public class MainActivity extends Activity {

    private DbUserDao dbUserDao;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);


        dbUserDao = new DbUserDao(this);
        dbUserDao.open();


    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_send_new_message:
                Intent intent = new Intent(this, SendMessageActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
