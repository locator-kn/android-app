package com.locator_app.locator.view.bubble;


public class GravitySimulator {

    private int width;
    private int height;
    private double worldGravity;
    Iterable<GravityObject> gravityObjects;

    public GravitySimulator(double worldGravity, int width, int height) {
        this.worldGravity = worldGravity;
        this.width = width;
        this.height = height;
    }

    public void simulateGravity(Iterable<GravityObject> gravityObjects, int times) {
        this.gravityObjects = gravityObjects;
        for (int i = 0; i < times; i++) {
            for (GravityObject gravityObject: gravityObjects) {
                if (!gravityObject.fixed()) {
                    moveGravityItem(gravityObject);
                }
            }
        }
    }

    private void moveGravityItem(GravityObject gravityObject) {

        final int distanceToGravityItems = 10;

        for (GravityObject other: gravityObjects) {
            if (other.fixed())
                continue;
            double radiusSum = gravityObject.radius + other.radius + distanceToGravityItems;
            double distance = gravityObject.distanceTo(other);
            if (distance == 0 || distance >= radiusSum)
                continue;
            double pen = distance - radiusSum;
            double cos = (-gravityObject.x + other.x) / distance;
            double sin = (-gravityObject.y + other.y) / distance;

            gravityObject.vx = gravityObject.vx * 0.7 + cos * pen;
            gravityObject.vy = gravityObject.vy * 0.7 + sin * pen;
        }

        for (GravityObject other: gravityObjects) {
            if (!other.fixed())
                continue;
            double distance = gravityObject.distanceTo(other) - distanceToGravityItems;
            if (distance == 0)
                continue;
            double dx = -gravityObject.x + other.x;
            double dy = -gravityObject.y + other.y;
            double cos = dx / distance;
            double sin = dy / distance;
            double forceGravity = worldGravity * (gravityObject.mass * other.mass)/(distance*distance);

            double vx = gravityObject.vx + cos * forceGravity;
            double vy = gravityObject.vy + sin * forceGravity;
            gravityObject.vx = vx;
            gravityObject.vy = vy;
            double pen = distance - (gravityObject.radius + other.radius);
            if (pen < 0) {
                gravityObject.vx = gravityObject.vx * 0.8 + (cos * pen);
                gravityObject.vy = gravityObject.vy * 0.8 + (sin * pen);
            }
        }

        gravityObject.x = bound(gravityObject.radius,
                gravityObject.x + gravityObject.vx, width - gravityObject.radius);
        gravityObject.y = bound(gravityObject.radius,
                gravityObject.y + gravityObject.vy, height - gravityObject.radius);
    }


    double bound(double min, double val, double max) {
        if (val < min)
            return min;
        if (val > max)
            return max;
        return val;
    }
}
