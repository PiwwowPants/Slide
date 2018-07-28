package me.ccrama.redditslide;

import android.os.AsyncTask;
import net.dean.jraw.managers.AccountManager;
import net.dean.jraw.models.Contribution;
import net.dean.jraw.models.Submission;

import java.util.ArrayList;

/**
 * Created by carlo_000 on 10/16/2015.
 */
public class Hidden {
    public static final ArrayList<String> id = new ArrayList<>();


    public static void setHidden(final Contribution s) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void[] params) {
                try {
                    id.add(s.getFullName());
                    new AccountManager(Authentication.reddit).hide(true, (Submission) s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public static void undoHidden(final Contribution s) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void[] params) {
                try {
                    id.remove(s.getFullName());
                    new AccountManager(Authentication.reddit).hide(false, (Submission) s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

}
