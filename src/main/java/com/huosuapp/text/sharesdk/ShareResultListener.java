package com.huosuapp.text.sharesdk;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by min on 2015/7/27.
 */
public interface ShareResultListener {
    void onComplete(Map<Object, Object> platform, int i, HashMap<String, Object> hashMap);
    void onError(Map<Object, Object> platform, int i, Throwable throwable) ;
    void onCancel(Map<Object, Object> platform, int i);
}
