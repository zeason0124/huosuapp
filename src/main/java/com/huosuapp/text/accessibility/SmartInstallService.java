package com.huosuapp.text.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.liang530.log.L;

import java.util.HashMap;
import java.util.Map;

public class SmartInstallService extends AccessibilityService {
    private String log = "SmartInstallService";
    private boolean isUninstall = false;
    Map<Integer, Boolean> eventMap = new HashMap<>();
    private static final String INSTALL = "安装";
    private static final String INSTALL_FINISHED = "应用安装完成。";
    private static final String FINISHED = "完成";
    private static final String CONFIRMED = "确定";
    private static final String UNINSTALL = "卸载";

    //服务在系统设置中开启时触发此方法
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        //获取当前服务的配置信息
        L.d("智能安装onServiceConnected");
//        AccessibilityServiceInfo info = getServiceInfo();
        //配置设置信息
//        info.packageNames = new String[]{"com.android.packageinstaller","com.google.android.packageinstaller","com.lenovo.safecenter"};
//        setServiceInfo(info);
    }

    /**
     * 问题来源：https://coolpers.github.io/accessibility/install/uninstall/2015/04/27/Accessibility-automatically-install-and-uninstall.html
     * 1.部分手机无法自动点击“打开”按钮 在部分手机（例如HTC 手机）上发现安装过程最后一步显示“打开”按钮界面，
     * 使用AccessibilityEvent.getSource()[Added in API level 14]获取到的值为空，导致无法获取触发点击“打开”按钮。
     * 通过AccessibilityService.getRootInActiveWindow ()[Added in API level 16] 获取整个窗口的控件对象信息解决此问题。
     * <p>
     * 2.部分手机自动安装页面无任何反应 例子中判断需要点击的按钮对象为Button时才触发，但是一些手机上按钮是TextView，
     * 通过添加TextView判断条件解决。
     */
    //回调监听窗口的事件
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        boolean useSmartInstall = SmartInstallUtil.getUseSmartInstall();
        if(!useSmartInstall){//用户关闭了，则不使用了
            return;
        }
        //遍历节点之前，现将卸载判定置为false;
        isUninstall = false;
        AccessibilityNodeInfo rootNodeInfo = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            rootNodeInfo = getRootInActiveWindow();
        }else{
            rootNodeInfo= event.getSource();
        }

        if (rootNodeInfo != null) {
            int eventType = event.getEventType();
            //响应窗口内容变化，窗口状态变化，控件滚动三种事件。
            if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
                    || eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                    || eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
                    //通过一个Map集合来过滤重复点击事件
                if (eventMap.get(event.getWindowId()) == null) {
                    boolean handled = handleEvent(rootNodeInfo);
                    if (handled) {
                        eventMap.put(event.getWindowId(), true);
                    }
                }
            }
        }
    }

    private boolean handleEvent(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
        //检查是否是卸载操作
            checkIsUninstallAction(nodeInfo);

            if (!isUninstall) {
                /**
                 * 这里把执行操作的代码抽取了出来，在下面这个方法中进行递归。
                 * 如若在当前方法中递归，checkUninstall方法会多次执行，没意义。
                 */
                return startPerformNodeAction(nodeInfo);
            }
        }
        return false;
    }

    private boolean startPerformNodeAction(AccessibilityNodeInfo nodeInfo) {
        //获取子节点数量
        int childCount = nodeInfo.getChildCount();

        switch (nodeInfo.getClassName().toString()) {
            case "android.widget.Button":
                if (nodeInfo.getText() != null) {
                    String nodeContent = nodeInfo.getText().toString();
                    if (INSTALL.equals(nodeContent) || FINISHED.equals(nodeContent) || CONFIRMED.equals(nodeContent)) {
                        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        return true;
                    }
                }
                break;
            case "android.widget.ScrollView":
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                break;
        }

        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo childNodeInfo = nodeInfo.getChild(i);
            if (startPerformNodeAction(childNodeInfo)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 通过遍历界面节点，查看是否存在带有卸载的字符串。来判断是否是卸载操作。
     * 通过判断字符串的方式不够严谨，可能会出现误中安装操作的情况。
     */
    private void checkIsUninstallAction(AccessibilityNodeInfo nodeInfo) {
        int childCount = nodeInfo.getChildCount();
        if (childCount != 0) {
            for (int x = 0; x < childCount; x++) {
                checkIsUninstallAction(nodeInfo.getChild(x));
            }
        } else {
            if (nodeInfo.getText() != null) {
                String nodeContent = nodeInfo.getText().toString();
                if (nodeContent.contains(UNINSTALL)) {
                    isUninstall = true;
                }
            }
        }
    }

    //服务在设置中被关掉时调用
    @Override
    public void onInterrupt() {
        L.d("智能安装onInterrupt");
    }
}