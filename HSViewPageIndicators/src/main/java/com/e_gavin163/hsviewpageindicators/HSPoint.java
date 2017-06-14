package com.e_gavin163.hsviewpageindicators;

import static android.R.attr.angle;

/**
 * Created by gavin on 2017/6/14.
 */

class HSPoint {
    private HSPosition position;
    private HSPosition positionUpLeft;
    private HSPosition positionDownLeft;
    private HSPosition positionUpRight;
    private HSPosition positionDownRight;
    private float radius;

    public HSPoint(HSPosition position) {
        this.position = position;
    }

    public HSPoint(HSPosition position, float radius) {
        this.position = position;
        this.radius = radius;
    }

    public HSPosition getPosition() {
        return position;
    }

    public void setPosition(HSPosition position) {
        this.position = position;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public HSPosition getPositionUpLeft() {
        float x = (float) (position.getPositionX() - getRadius() * Math.cos(angle / 2.0));
        float y = (float) (position.getPositionY() - getRadius() * Math.sin(angle / 2.0));
        positionUpLeft = new HSPosition(x, y);
        return positionUpLeft;
    }

    public void setPositionUpLeft(HSPosition positionUpLeft) {
        this.positionUpLeft = positionUpLeft;
    }

    public HSPosition getPositionDownLeft() {
        float x = (float) (position.getPositionX() - getRadius() * Math.cos(angle / 2.0));
        float y = (float) (position.getPositionY() + getRadius() * Math.sin(angle / 2.0));
        positionDownLeft = new HSPosition(x, y);
        return positionDownLeft;
    }

    public void setPositionDownLeft(HSPosition positionDownLeft) {
        this.positionDownLeft = positionDownLeft;
    }

    public HSPosition getPositionUpRight() {
        float x = (float) (position.getPositionX() + getRadius() * Math.cos(angle / 2.0));
        float y = (float) (position.getPositionY() - getRadius() * Math.sin(angle / 2.0));
        positionUpRight = new HSPosition(x, y);
        return positionUpRight;
    }

    public void setPositionUpRight(HSPosition positionUpRight) {
        this.positionUpRight = positionUpRight;
    }

    public HSPosition getPositionDownRight() {
        float x = (float) (position.getPositionX() + getRadius() * Math.cos(angle / 2.0));
        float y = (float) (position.getPositionY() + getRadius() * Math.sin(angle / 2.0));
        positionDownRight = new HSPosition(x, y);
        return positionDownRight;
    }

    public void setPositionDownRight(HSPosition positionDownRight) {
        this.positionDownRight = positionDownRight;
    }
}
