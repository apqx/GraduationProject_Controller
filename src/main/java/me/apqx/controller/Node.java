package me.apqx.controller;

/**
 * Created by apqx on 2017/3/18.
 */

public class Node {
    //卸货
    public static final int DO_UNLOAD=0;
    //不卸货
    public static final int NO_UNLOAD=1;
    public static final int MANUAL_MOD=0;
    public static final int PATH_MOD=1;
    private int velocity;
    private int time;
    private int degree;
    private int indexOfGrid;
    private int unload;
    private int up,down,right,left,stop;
    public Node(int velocity,int time,int degree,int indexOfGrid,int unload){
        this.degree=degree;
        this.velocity=velocity;
        this.time=time;
        this.indexOfGrid=indexOfGrid;
        this.unload=unload;
        up=PATH_MOD;
        down=PATH_MOD;
        right=PATH_MOD;
        left=PATH_MOD;
        stop=PATH_MOD;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public int getDegree() {
        return degree;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setIndexOfGrid(int indexOfGrid) {
        this.indexOfGrid = indexOfGrid;
    }

    public int getIndexOfGrid() {
        return indexOfGrid;
    }

    public void setUnload(int unload) {
        this.unload = unload;
    }

    public int getUnload() {
        return unload;
    }

    public void setUpManual(){
        up=MANUAL_MOD;
    }
    public void setDownManual(){
        down=MANUAL_MOD;
    }
    public void setRightManual(){
        right=MANUAL_MOD;
    }
    public void setLeftManual(){
        left=MANUAL_MOD;
    }
    public void setStopManual(){
        stop=MANUAL_MOD;
    }

    public int getUp() {
        return up;
    }

    public int getDown() {
        return down;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public int getStop() {
        return stop;
    }

    @Override
    public String toString() {
        return "index = "+indexOfGrid+"; velocity = "+velocity+"; time = "+time+"; degree = ="+degree;
    }
}
