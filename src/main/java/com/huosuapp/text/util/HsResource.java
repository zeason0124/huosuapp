package com.huosuapp.text.util;

import android.content.Context;

/**
 * Created by liu hong liang on 2016/9/30.
 */

public class HsResource {
    public static int getIdByName(Context context, String className, String name) {
        return context.getResources().getIdentifier(name,className,context.getPackageName());
    }
}
