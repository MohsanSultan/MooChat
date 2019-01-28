package com.example.moo_chat.moochat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.SpannableStringBuilder;
import android.widget.Switch;

class SectionPagerAdapter extends FragmentPagerAdapter{
    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {
            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 1:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;
            case 2:
                FriendsFragment friendsFragment= new FriendsFragment();
                return friendsFragment;
                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position) {
        SpannableStringBuilder sb;
        switch (position){
            case 0:
                sb = new SpannableStringBuilder("  CHATS");
                return sb;
            case 1:
                sb = new SpannableStringBuilder("  REQUEST");
                return sb;
            case 2:
                sb = new SpannableStringBuilder("  FRIENDS");
                return sb;
            default:
                return null;
        }

    }
}
