package me.apqx.controller.views;

/**
 * Created by chang on 2016/8/21.
 * 监听ControllerView触摸事件的接口
 */
public interface OnControllerListener {
    void up(int velocity);
    void down(int velocity);
    void right(int velocity);
    void left(int velocity);
    void stop();
}
