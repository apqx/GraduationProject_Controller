package me.apqx.controller;

/**
 * Created by apqx on 2017/3/18.
 */

public class Node {
    //卸货
    public static final int DO_UNLOAD=1;
    //不卸货
    public static final int NO_UNLOAD=0;
    private int velocity;
    private int time;
    private int degree;
    private int indexOfGrid;
    private int unload;
    public Node(int velocity,int time,int degree,int indexOfGrid,int unload){
        this.degree=degree;
        this.velocity=velocity;
        this.time=time;
        this.indexOfGrid=indexOfGrid;
        this.unload=unload;
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

    @Override
    public String toString() {
        return "index = "+indexOfGrid+"; velocity = "+velocity+"; time = "+time+"; degree = ="+degree;
    }
}
