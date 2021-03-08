package com.same.lib.base;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.UiThread;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/24
 * @description null
 * @usage null
 */
public class NotificationCenter {
    private static int totalEvents = 1;

    public static int stopAllHeavyOperations = totalEvents++;
    public static int startAllHeavyOperations = totalEvents++;

    public static int didSetNewTheme = totalEvents++;
    public static int needCheckSystemBarColors = totalEvents++;
    public static int needSetDayNightTheme = totalEvents++;
    public static int didSetPasscode = totalEvents++;

    public static void post(int id) {
        getGlobalInstance().postNotificationName(id);
    }

    public static void post(int id, boolean yes) {
        getGlobalInstance().postNotificationName(id, yes);
    }

    public static void post(int id, Object info) {
        getGlobalInstance().postNotificationName(id, info);
    }


    private SparseArray<ArrayList<NotificationCenterDelegate>> observers = new SparseArray<>();
    private SparseArray<ArrayList<NotificationCenterDelegate>> removeAfterBroadcast = new SparseArray<>();
    private SparseArray<ArrayList<NotificationCenterDelegate>> addAfterBroadcast = new SparseArray<>();
    private ArrayList<DelayedPost> delayedPosts = new ArrayList<>(10);

    private int broadcasting = 0;

    private int animationInProgressCount;
    private int animationInProgressPointer = 1;

    private final HashMap<Integer, int[]> allowedNotifications = new HashMap<>();

    /**
     * 通知接口
     *
     * 观察者模式。发生事件后会调用此接口
     */
    public interface NotificationCenterDelegate {
        void didReceivedNotification(int id, int account, Object... args);
    }

    private static class DelayedPost {

        private DelayedPost(int id, Object[] args) {
            this.id = id;
            this.args = args;
        }

        private int id;
        private Object[] args;
    }

    private int currentAccount;
    private int currentHeavyOperationFlags;
    private static volatile NotificationCenter globalInstance;

    /**
     * 这种单例模式是采用双重校验锁的线程安全的单例模式。是效率最好的安全性最好的一种写法
     *
     * Instance加上了volatile关键字确保了多线程环境下防止重排序，避免在多线程环境下实例化NotificationCenter对象时得到的引用时未初始化的。
     *
     * 实例化一个对象其实可以分为三个步骤：
     * 　　（1）分配内存空间。
     * 　　（2）初始化对象。
     * 　　（3）将内存空间的地址赋值给对应的引用。
     * 但是由于操作系统可以对指令进行重排序，所以上面的过程也可能会变成如下过程：
     * 　　（1）分配内存空间。
     * 　　（2）将内存空间的地址赋值给对应的引用。
     * 　　（3）初始化对象
     * 　　如果是这个流程，多线程环境下就可能将一个未初始化的对象引用暴露出来，从而导致不可预料的结果。
     *     因此，为了防止这个过程的重排序，我们需要将变量设置为volatile类型的变量。
     * @return NotificationCenter
     */
    @UiThread
    public static NotificationCenter getGlobalInstance() {
        NotificationCenter localInstance = globalInstance;
        if (localInstance == null) {
            synchronized (NotificationCenter.class) {
                localInstance = globalInstance;
                if (localInstance == null) {
                    globalInstance = localInstance = new NotificationCenter(-1);
                }
            }
        }
        return localInstance;
    }

    public NotificationCenter(int account) {
        currentAccount = account;
    }

    public int setAnimationInProgress(int oldIndex, int[] allowedNotifications) {
        onAnimationFinish(oldIndex);
        if (animationInProgressCount == 0) {
            NotificationCenter.post(stopAllHeavyOperations, 512);
        }

        animationInProgressCount++;
        animationInProgressPointer++;

        if (allowedNotifications == null) {
            allowedNotifications = new int[0];
        }

        this.allowedNotifications.put(animationInProgressPointer, allowedNotifications);

        return animationInProgressPointer;
    }

    public void updateAllowedNotifications(int transitionAnimationIndex, int[] allowedNotifications) {
        if (this.allowedNotifications.containsKey(transitionAnimationIndex)) {
            if (allowedNotifications == null) {
                allowedNotifications = new int[0];
            }
            this.allowedNotifications.put(transitionAnimationIndex, allowedNotifications);
        }
    }

    public void onAnimationFinish(int index) {
        int[] notifications = allowedNotifications.remove(index);
        if (notifications != null) {
            animationInProgressCount--;
            if (animationInProgressCount == 0) {
                NotificationCenter.post(startAllHeavyOperations, 512);
                if (!delayedPosts.isEmpty()) {
                    for (int a = 0; a < delayedPosts.size(); a++) {
                        DelayedPost delayedPost = delayedPosts.get(a);
                        postNotificationNameInternal(delayedPost.id, true, delayedPost.args);
                    }
                    delayedPosts.clear();
                }
            }
        }
    }

    public boolean isAnimationInProgress() {
        return animationInProgressCount > 0;
    }

    public int getCurrentHeavyOperationFlags() {
        return currentHeavyOperationFlags;
    }

    public void postNotificationName(int id, Object... args) {
        boolean allowDuringAnimation = id == startAllHeavyOperations || id == stopAllHeavyOperations;
        if (!allowDuringAnimation && !allowedNotifications.isEmpty()) {
            int size = allowedNotifications.size();
            int allowedCount = 0;
            for (Integer key : allowedNotifications.keySet()) {
                int[] allowed = allowedNotifications.get(key);
                if (allowed != null) {
                    for (int a = 0; a < allowed.length; a++) {
                        if (allowed[a] == id) {
                            allowedCount++;
                            break;
                        }
                    }
                } else {
                    break;
                }
            }
            allowDuringAnimation = size == allowedCount;
        }
        if (id == startAllHeavyOperations) {
            Integer flags = (Integer) args[0];
            currentHeavyOperationFlags &= ~flags;
        } else if (id == stopAllHeavyOperations) {
            Integer flags = (Integer) args[0];
            currentHeavyOperationFlags |= flags;
        }
        postNotificationNameInternal(id, allowDuringAnimation, args);
    }

    @UiThread
    public void postNotificationNameInternal(int id, boolean allowDuringAnimation, Object... args) {
        if (!allowDuringAnimation && isAnimationInProgress()) {
            DelayedPost delayedPost = new DelayedPost(id, args);
            delayedPosts.add(delayedPost);
            return;
        }
        broadcasting++;
        ArrayList<NotificationCenterDelegate> objects = observers.get(id);
        if (objects != null && !objects.isEmpty()) {
            for (int a = 0; a < objects.size(); a++) {
                NotificationCenterDelegate obj = objects.get(a);
                obj.didReceivedNotification(id, currentAccount, args);
            }
        }
        broadcasting--;
        if (broadcasting == 0) {
            if (removeAfterBroadcast.size() != 0) {
                for (int a = 0; a < removeAfterBroadcast.size(); a++) {
                    int key = removeAfterBroadcast.keyAt(a);
                    ArrayList<NotificationCenterDelegate> arrayList = removeAfterBroadcast.get(key);
                    for (int b = 0; b < arrayList.size(); b++) {
                        removeObserver(arrayList.get(b), key);
                    }
                }
                removeAfterBroadcast.clear();
            }
            if (addAfterBroadcast.size() != 0) {
                for (int a = 0; a < addAfterBroadcast.size(); a++) {
                    int key = addAfterBroadcast.keyAt(a);
                    ArrayList<NotificationCenterDelegate> arrayList = addAfterBroadcast.get(key);
                    for (int b = 0; b < arrayList.size(); b++) {
                        addObserver(arrayList.get(b), key);
                    }
                }
                addAfterBroadcast.clear();
            }
        }
    }

    public void addObserver(NotificationCenterDelegate observer, int id) {
        if (broadcasting != 0) {
            ArrayList<NotificationCenterDelegate> arrayList = addAfterBroadcast.get(id);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                addAfterBroadcast.put(id, arrayList);
            }
            arrayList.add(observer);
            return;
        }
        ArrayList<NotificationCenterDelegate> objects = observers.get(id);
        if (objects == null) {
            observers.put(id, (objects = new ArrayList<>()));
        }
        if (objects.contains(observer)) {
            return;
        }
        objects.add(observer);
    }

    public void removeObserver(NotificationCenterDelegate observer, int id) {
        if (broadcasting != 0) {
            ArrayList<NotificationCenterDelegate> arrayList = removeAfterBroadcast.get(id);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                removeAfterBroadcast.put(id, arrayList);
            }
            arrayList.add(observer);
            return;
        }
        ArrayList<NotificationCenterDelegate> objects = observers.get(id);
        if (objects != null) {
            objects.remove(observer);
        }
    }

    public boolean hasObservers(int id) {
        return observers.indexOfKey(id) >= 0;
    }
}
