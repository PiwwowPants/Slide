package me.ccrama.redditslide.Adapters;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;

import net.dean.jraw.models.PublicContribution;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.paginators.Sorting;
import net.dean.jraw.paginators.UserPublicContributionPaginator;

import java.util.ArrayList;

import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.HasSeen;
import me.ccrama.redditslide.PostMatch;
import me.ccrama.redditslide.Reddit;

/**
 * Created by ccrama on 9/17/2015.
 */
public class PublicContributionPosts extends GeneralPosts {
    protected final String                          where;
    protected final String                          subreddit;
    public          boolean                         loading;
    private         UserPublicContributionPaginator paginator;
    protected       SwipeRefreshLayout              refreshLayout;
    protected       PublicContributionAdapter       adapter;

    public PublicContributionPosts(String subreddit, String where) {
        this.subreddit = subreddit;
        this.where = where;
    }

    public void bindAdapter(PublicContributionAdapter a, SwipeRefreshLayout layout) {
        this.adapter = a;
        this.refreshLayout = layout;
        loadMore(a, subreddit, true);
    }

    public void loadMore(PublicContributionAdapter adapter, String subreddit, boolean reset) {
        new LoadData(reset).execute(subreddit);
    }

    public class LoadData extends AsyncTask<String, Void, ArrayList<PublicContribution>> {
        final boolean reset;

        public LoadData(boolean reset) {
            this.reset = reset;
        }

        @Override
        public void onPostExecute(ArrayList<PublicContribution> submissions) {
            loading = false;

            if (submissions != null && !submissions.isEmpty()) {
                // new submissions found

                int start = 0;
                if (posts != null) {
                    start = posts.size() + 1;
                }

                ArrayList<PublicContribution> filteredSubmissions = new ArrayList<>();
                for (PublicContribution c : submissions) {
                    if (c instanceof Submission) {
                        if (!PostMatch.doesMatch((Submission) c)) {
                            filteredSubmissions.add(c);
                        }
                    } else {
                        filteredSubmissions.add(c);
                    }
                }

                HasSeen.setHasSeenContrib(filteredSubmissions);
                if (reset || posts == null) {
                    posts = filteredSubmissions;
                    start = -1;
                } else {
                    posts.addAll(filteredSubmissions);
                }

                final int finalStart = start;
                // update online
                if (refreshLayout != null) {
                    refreshLayout.setRefreshing(false);
                }

                if (finalStart != -1) {
                    adapter.notifyItemRangeInserted(finalStart + 1, posts.size());
                } else {
                    adapter.notifyDataSetChanged();
                }

            } else if (submissions != null) {
                // end of submissions
                nomore = true;
                adapter.notifyDataSetChanged();

            } else if (!nomore) {
                // error
                adapter.setError(true);
            }
            refreshLayout.setRefreshing(false);
        }

        @Override
        protected ArrayList<PublicContribution> doInBackground(String... subredditPaginators) {
            ArrayList<PublicContribution> newData = new ArrayList<>();
            try {
                if (reset || paginator == null) {
                    paginator = new UserPublicContributionPaginator(Authentication.reddit, where,
                            subreddit);

                    paginator.setSorting(Reddit.getSorting(subreddit, Sorting.NEW));
                    paginator.setTimePeriod(Reddit.getTime(subreddit, TimePeriod.ALL));
                }

                if (!paginator.hasNext()) {
                    nomore = true;
                    return new ArrayList<>();
                }
                for (PublicContribution c : paginator.next()) {
                    if (c instanceof Submission) {
                        Submission s = (Submission) c;
                        newData.add(s);
                    } else {
                        newData.add(c);
                    }
                }

                return newData;
            } catch (Exception e) {
                return null;
            }
        }

    }

}
