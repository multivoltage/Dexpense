package com.tonini.diego.dexpense;

import android.app.Application;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by Diego on 10/04/2015.
 */
public class MyApplication extends Application {

    public static Bus bus = new Bus(ThreadEnforcer.MAIN);

}


