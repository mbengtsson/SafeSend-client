package se.teamgejm.safesend.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import se.teamgejm.safesend.R;
import se.teamgejm.safesend.fragments.MessageListFragment;
import se.teamgejm.safesend.fragments.UserListFragment;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Gustav
 *
 */
public class MainActivity extends Activity {

    private ViewPager viewPager;
    private FragmentPagerAdapter pagerAdapter;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);

        setPagerAdapter(new MyPagerAdapter(this.getFragmentManager(), getFragments()));
        setViewPager((ViewPager) findViewById(R.id.main_viewpager_layout));
        getViewPager().setAdapter(getPagerAdapter());
    }

    private List<Fragment> getFragments () {
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new UserListFragment());
        fragments.add(new MessageListFragment());
        return fragments;
    }

    public ViewPager getViewPager () {
        return viewPager;
    }

    public void setViewPager (ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public FragmentPagerAdapter getPagerAdapter () {
        return pagerAdapter;
    }

    public void setPagerAdapter (FragmentPagerAdapter pagerAdapter) {
        this.pagerAdapter = pagerAdapter;
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragments;

        public MyPagerAdapter (FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem (int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount () {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle (int position) {
            switch (position) {
                case 0:
                    return "SafeSend";
                case 1:
                    return "Inbox";
                default:
                    return null;
            }
        }
    }

}
