package com.camvy.kevinzhang.soulcast;

import android.util.Log;

import java.util.ArrayList;

import retrofit2.Response;

/**
 * Created by Jonathan on 5/28/2017.
 */

public interface HistoryInterface {
        void onSuccess(Response<ArrayList<Soul>> listOfSouls);
        void onAPICallError(Response<ArrayList<Soul>> response);
        void onConnectionError(Throwable t);
}
