package com.android.sm.device_id;

import android.app.Activity;
import android.app.Application;

/**
 * created by：yang22
 * on 2023/8/3 09:43
 */
public class UtilsBridge {
    private  static Activity activity;
    public UtilsBridge(Activity activity) {
        this. activity=activity;
    }
    static void init(Application app) {
        UtilsActivityLifecycleImpl.INSTANCE.init(app);
    }

    static void unInit(Application app) {
        UtilsActivityLifecycleImpl.INSTANCE.unInit(app);
    }

    static void preLoad() {
        preLoad(AdaptScreenUtils.getPreLoadRunnable());
    }
    private static void preLoad(final Runnable... runs) {
        for (final Runnable r : runs) {
            ThreadUtils.getCachedPool().execute(r);
        }
    }

    static SPUtils getSpUtils4Utils() {
        SPUtils spUtils=new SPUtils(activity);
        return spUtils.getInstance("Utils");
    }
    static Application getApplicationByReflect() {
        return UtilsActivityLifecycleImpl.INSTANCE.getApplicationByReflect();
    }

}


