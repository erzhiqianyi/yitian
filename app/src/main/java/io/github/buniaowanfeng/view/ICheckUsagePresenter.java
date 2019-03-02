package io.github.buniaowanfeng.view;

/**
 * Created by caofeng on 16-7-9.
 * 处理屏幕点亮，关闭事件，查询正在运行的应用
 */
public interface ICheckUsagePresenter {
    /**
     *  屏幕点亮，保存点亮时间
     */
    void screenOn();

    /**
     * 屏幕关闭，保存关闭时间
     */
    void screenOff();

    /**
     * 查询正在使用的应用，如果切换到了其他应用，保存前一个应用的使用情况
     */
    void check();

}
