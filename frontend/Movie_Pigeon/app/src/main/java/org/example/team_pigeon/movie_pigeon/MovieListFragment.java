package org.example.team_pigeon.movie_pigeon;

import android.app.Fragment;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import org.example.team_pigeon.movie_pigeon.adapters.MovieListAdapter;
import org.example.team_pigeon.movie_pigeon.models.Movie;
import java.util.ArrayList;

/**
 * Created by SHENGX on 2017/2/3.
 */

public class MovieListFragment extends Fragment implements AdapterView.OnItemClickListener{
    private android.app.FragmentManager fragmentManager;
    private ArrayList<Movie> movies;
    private ListView list_movies;
    private MovieListAdapter movieListAdapter;
    public android.os.Handler mHandler;
    public View footerView;
    public boolean isLoading = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        fragmentManager = getFragmentManager();
        movies = (ArrayList<Movie>)getArguments().getSerializable("movies");
        View view = inflater.inflate(R.layout.fragment_movie_list,container,false);
        list_movies = (ListView)view.findViewById(R.id.list_movies);
        footerView = inflater.inflate(R.layout.footer_load_more,null);
        mHandler = new footerHandler();
        movieListAdapter = new MovieListAdapter(movies,getActivity());
        list_movies.setAdapter(movieListAdapter);
        list_movies.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(view.getLastVisiblePosition() == totalItemCount-1 && list_movies.getCount() >= 10 && isLoading == false) {
                    isLoading = true;
                    Thread thread = new ThreadGetMoreMovies();
                    thread.start();
                }
            }
        });
        list_movies.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MoviePageFragment moviePageFragment = new MoviePageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title",movieListAdapter.getItem(position).getTitle());
        bundle.putString("year",movieListAdapter.getItem(position).getProductionYear());
        bundle.putString("country",movieListAdapter.getItem(position).getCountry());
        bundle.putString("length",movieListAdapter.getItem(position).getLength());
        bundle.putString("director",movieListAdapter.getItem(position).getDirector());
        bundle.putString("poster",movieListAdapter.getItem(position).getPosterURL());
        bundle.putString("plot",movieListAdapter.getItem(position).getPlot());
        if(!movieListAdapter.getItem(position).getGenres().equals("NULL")) {
            bundle.putString("genres", movieListAdapter.getItem(position).getGenres().replaceAll(", ", " / "));
        }
        else {
            bundle.putString("genres", "NULL");
        }
        if(!movieListAdapter.getItem(position).getActors().equals("NULL")) {
            bundle.putString("actors", movieListAdapter.getItem(position).getActors().replaceAll(", ", " / "));
        }
        else {
            bundle.putString("actors", "NULL");
        }
        moviePageFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fl_content,moviePageFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private ArrayList<Movie> getMoreMovies() {
        //For testing
        ArrayList<Movie> list = new ArrayList<>();
        list.add(new Movie("new","111"));
        Movie movie = list.get(0);
        movie.setPosterURL("https://s-media-cache-ak0.pinimg.com/736x/17/ba/70/17ba70b5d152cb9042e4a0785dccf834.jpg");
        list.add(new Movie("new2","1112"));
        list.add(new Movie("new3","1113"));
        list.add(new Movie("new4","1114"));
        list.add(new Movie("new5","1115"));
        list.add(new Movie("new6","1112"));
        list.add(new Movie("new7","1111"));
        return list;
    }

    public class footerHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    list_movies.addFooterView(footerView);
                    break;
                case 1:
                    movieListAdapter.addListItemToAdapter((ArrayList<Movie>)message.obj);
                    list_movies.removeFooterView(footerView);
                    isLoading = false;
                    break;
                default:
                    break;
            }
        }
    }

    public class ThreadGetMoreMovies extends Thread {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(0);
            ArrayList<Movie> moreMoviesList = getMoreMovies();
            try {
                //For testing
                Thread.sleep(5000);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
            Message message = mHandler.obtainMessage(1,moreMoviesList);
            mHandler.sendMessage(message);
        }
    }
}
