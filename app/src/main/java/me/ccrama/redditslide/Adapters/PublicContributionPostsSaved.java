package me.ccrama.redditslide.Adapters;

import net.dean.jraw.models.PublicContribution;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.UserSavedPaginator;

import java.util.ArrayList;

import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.HasSeen;
import me.ccrama.redditslide.PostMatch;
import me.ccrama.redditslide.SettingValues;

/**
 * Created by ccrama on 9/17/2015.
 */
public class PublicContributionPostsSaved extends PublicContributionPosts {
    private final String category;

    public PublicContributionPostsSaved(String subreddit, String where, String category) {
        super(subreddit, where);
        this.category = category;
    }

    UserSavedPaginator paginator;

    @Override
    public void loadMore(PublicContributionAdapter adapter, String subreddit, boolean reset) {
        new LoadData(reset).execute(subreddit);
    }

    public class LoadData extends PublicContributionPosts.LoadData {

        public LoadData(boolean reset) {
            super(reset);
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
                    paginator = new UserSavedPaginator(Authentication.reddit, where, subreddit);
                    paginator.setSorting(SettingValues.getSubmissionSort(subreddit));
                    paginator.setTimePeriod(SettingValues.getSubmissionTimePeriod(subreddit));
                    if (category != null) paginator.setCategory(category);
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
