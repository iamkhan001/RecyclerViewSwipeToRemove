package com.nstudio.shaadimatches.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.functions.Consumer;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.nstudio.shaadimatches.R;
import com.nstudio.shaadimatches.models.ProfileModel;
import com.nstudio.shaadimatches.models.Response;
import com.nstudio.shaadimatches.utils.RecyclerItemTouchHelper;
import com.nstudio.shaadimatches.utils.RxBus;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView rvProfiles;
    private ShimmerFrameLayout shimmerFrameLayout;
    private ProfileAdapter profileAdapter;
    private final String tag = MainActivity.class.getSimpleName();
    private ArrayList<ProfileModel> list;
    private CoordinatorLayout parentView;
    private OnItemRemoveListener itemRemoveListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewModelFactory factory = new ViewModelFactory(getApplication());
        final MainViewModel viewModel = ViewModelProviders.of(this,factory).get(MainViewModel.class);


        rvProfiles = findViewById(R.id.rvProfiles);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        refreshLayout = findViewById(R.id.swipeRefresh);
        parentView = findViewById(R.id.parentView);


        itemRemoveListener = new OnItemRemoveListener() {
            @Override
            public void onItemRemove(RecyclerView.ViewHolder viewHolder, int position) {
                onSwiped(viewHolder,ItemTouchHelper.RIGHT,position);
            }
        };

        // Rx Event Listener Subscription
        // useful in case of any error in repository class and handle error in activity if required
        RxBus.subscribe(RxBus.DOWNLOAD_LIST, this, new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {

                        Response response = (Response) o;
                        handleResponse(response);
                    }
                }
        );



        //observe for user list updates
        viewModel.getProfiles().observe(this, new Observer<ArrayList<ProfileModel>>() {
            @Override
            public void onChanged(ArrayList<ProfileModel> list) {
                clearScreen();
                MainActivity.this.list = list;
                profileAdapter = new ProfileAdapter(list,itemRemoveListener);
                final LayoutAnimationController controller =
                        AnimationUtils.loadLayoutAnimation(MainActivity.this, R.anim.layout_animation_from_bottom);
                rvProfiles.setLayoutAnimation(controller);
                rvProfiles.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                rvProfiles.setAdapter(profileAdapter);
            }
        });


        // adding swipe to remove feature too, comment below 2 lines if not required!
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvProfiles);


        // pull down reload list
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isOnline()){
                    startAnimation();
                    viewModel.loadProfileList();
                }else {
                    refreshLayout.setRefreshing(false);
                    Toast.makeText(MainActivity.this,"No Internet Access!",Toast.LENGTH_LONG).show();
                }
            }
        });


        if(isOnline()){
            startAnimation();
            viewModel.loadProfileList();
        }else {
            Toast.makeText(MainActivity.this,"No Internet Access!",Toast.LENGTH_LONG).show();
        }

        Snackbar.make(parentView,"Swipe Down To Reload User List!",Snackbar.LENGTH_LONG).show();

    }

    private void startAnimation(){
        rvProfiles.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();
    }

    private void handleResponse(final Response response) {
        //handle response
        //as of now, i'm handling only error case here

        if (!response.isSuccess()){
            clearScreen();
            rvProfiles.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this,response.getMessage(),Toast.LENGTH_SHORT).show();
            Log.v(tag, response.getMessage());
        }


    }

    private void clearScreen() {
        //stoop animation
        shimmerFrameLayout.stopShimmerAnimation();
        refreshLayout.setRefreshing(false);
        shimmerFrameLayout.setVisibility(View.GONE);
        rvProfiles.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unsubscribe for events
        RxBus.unregister(this);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ProfileAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = list.get(viewHolder.getAdapterPosition()).getName();

            // backup of removed item for undo purpose
            final ProfileModel deletedItem = list.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            profileAdapter.removeItem(viewHolder.getAdapterPosition(),viewHolder);

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(parentView, name + " removed from list!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    profileAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }


    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    interface OnItemRemoveListener{
         void onItemRemove(RecyclerView.ViewHolder viewHolder,  int position);
    }
}
