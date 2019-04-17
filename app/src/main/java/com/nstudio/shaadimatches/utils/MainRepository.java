package com.nstudio.shaadimatches.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nstudio.shaadimatches.models.ProfileModel;
import com.nstudio.shaadimatches.models.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;

public class MainRepository {

    private MutableLiveData<ArrayList<ProfileModel>> mProfiles;

    public MainRepository(MutableLiveData<ArrayList<ProfileModel>> mProfiles) {
        this.mProfiles = mProfiles;
    }


    public void loadProfiles(Context context){
        Ion.with(context)
                .load("https://randomuser.me/api/?results=100")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {

                        Log.e("res","res > "+result);
                        if (e!=null){
                            e.printStackTrace();
                        }else{
                            if (result!=null){
                                try {
                                    JSONObject object = new JSONObject(result);
                                    JSONArray array = object.getJSONArray("results");
                                    Type listType = new TypeToken<ArrayList<ProfileModel>>() {}.getType();
                                    ArrayList<ProfileModel> profiles = new Gson().fromJson(array.toString(),listType);
                                    if (profiles!=null && profiles.size()>0){
                                        mProfiles.postValue(profiles);
                                    }

                                    //send event to activity for susses case
                                    RxBus.publish(RxBus.DOWNLOAD_LIST,new Response(true,""));
                                    return;
                                }catch (Exception x){
                                    x.printStackTrace();
                                }

                            }
                        }

                        //here send event to activity because api call is not successful hence handle this in activity ex. stopping animations
                        RxBus.publish(RxBus.DOWNLOAD_LIST,new Response(false,"Error Occurred!"));

                    }
                });
    }



}
