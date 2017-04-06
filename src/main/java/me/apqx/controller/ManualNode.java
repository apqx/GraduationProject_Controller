package me.apqx.controller;

/**
 * Created by apqx on 2017/4/6.
 * 手动模式下的命令节点，现在来没有考虑两轮的差速来实现小车在运动中转向，转向前，小车必须停止运动，然后执行转向，然后再运动
 */

public class ManualNode {
    public static final int NO=0;
    public static final int YES=1;
    public static final int NO_VELOCITY=0;
    private int up,down,left,right,stop,velocity;
    public ManualNode(int velocity) {
        this.velocity=velocity;
        up = NO;
        down = NO;
        left = NO;
        right = NO;
        stop = NO;
    }

    public void setUp(int up) {
        this.up = up;
    }

    public int getUp() {
        return up;
    }

    public void setDown(int down) {
        this.down = down;
    }

    public int getDown() {
        return down;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getLeft() {
        return left;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getRight() {
        return right;
    }

    public void setStop(int stop) {
        this.stop = stop;
    }

    public int getStop() {
        return stop;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public int getVelocity() {
        return velocity;
    }
}
