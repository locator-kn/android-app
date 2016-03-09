package com.locator_app.locator.view.bubble;

import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.locator_app.locator.R;
import com.locator_app.locator.apiservice.my.BubbleScreenResponse;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.model.LocatorObject;
import com.locator_app.locator.view.LocationDetailActivity;
import com.locator_app.locator.view.UiError;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import rx.Observable;


public class BubbleController {

    class Bubble {
        View view;
        LocatorObject data;
        int priority;
        boolean positionFixed = false;
    }

    Random random = new Random();
    private final List<Bubble> bubbles = new LinkedList<>();
    private RelativeBubbleLayout layout;
    private Bubble schoenHierBubble;
    private Bubble userProfileBubble;

    public BubbleController(RelativeBubbleLayout layout) {
        this.layout = layout;
        layout.requestLayout();
        layout.invalidate();
        new Handler().postDelayed(() -> {
            initUserProfileBubble();
            initSchoenHierBubble();
        }, 1000);
        layout.requestLayout();
        layout.refreshDrawableState();
    }

    synchronized
    private void initSchoenHierBubble() {
        if (schoenHierBubble == null) {
            schoenHierBubble = new Bubble();
            schoenHierBubble.priority = -1;
            schoenHierBubble.positionFixed = true;
            schoenHierBubble.view = layout.findViewById(R.id.schoenHierBubble);
            int radius = getRadiusByPriority(schoenHierBubble.priority);
            layout.setBubbleRadius(schoenHierBubble.view, radius);
            layout.setBubbleCenter(schoenHierBubble.view, 0.5, 0.38);
            schoenHierBubble.view.setVisibility(View.VISIBLE);
        }
    }

    synchronized
    private void initUserProfileBubble() {
        if (userProfileBubble == null) {
            userProfileBubble = new Bubble();
            userProfileBubble.priority = -1;
            userProfileBubble.positionFixed = true;
            userProfileBubble.view = layout.findViewById(R.id.userProfileBubble);
            int radius = getRadiusByPriority(10);
            layout.setBubbleRadius(userProfileBubble.view, radius);
            layout.setBubbleCenter(userProfileBubble.view, 0.5, 0.89);
            userProfileBubble.view.setVisibility(View.VISIBLE);
        }
    }

    public void onBubbleScreenUpdate(BubbleScreenResponse response) {
        initUserProfileBubble();
        initSchoenHierBubble();

        if (bubbles.isEmpty()) {
            createNewBubblesFromBubbleScreenResponse(response);
        } else {
            for (Bubble b: bubbles) {
                int delay = random.nextInt(1200);
                YoYo.with(Techniques.TakingOff)
                        .delay(delay)
                        .duration(600)
                        .withListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                synchronized (bubbles) {
                                    layout.removeView(b.view);
                                    b.view = null;
                                    bubbles.remove(b);
                                    if (bubbles.isEmpty()) {
                                        createNewBubblesFromBubbleScreenResponse(response);
                                    }
                                }
                            }
                        })
                        .playOn(b.view);
            }
        }
    }

    private void createNewBubblesFromBubbleScreenResponse(BubbleScreenResponse response) {
        int numberOfLocations = Math.min(response.locations.size(), random.nextInt(4) + 10);

        Observable.from(response.locations)
                .take(numberOfLocations)
                .map(locationResult -> {
                    final int priority = getPriority(locationResult, response.locations);
                    return makeLocationBubble(locationResult.location, priority);
                })
                .subscribe(
                        bubbles::add,
                        (err) -> {
                        }
                );

        positionBubblesInDifferentQuadrants();

        simulateGravity();
        simulateGravity();
        simulateGravity();

        showBubblesWithAnimation();
    }

    private void showBubblesWithAnimation() {
        Observable.from(bubbles)
            .filter(b -> b.priority != -1)
            .map(b -> b.view)
            .toBlocking()
            .forEach(
                    bubbleView -> {
                        new Handler().postDelayed(() ->
                                YoYo.with(Techniques.FadeInUp)
                                    .duration(1500)
                                    .playOn(bubbleView),
                                random.nextInt(1500));
                    });
    }

    private void simulateGravity() {
        List<GravityObject> gravityObjects = new LinkedList<>();
        for (Bubble bubble: bubbles) {
            GravityObject gravityObject = toGravityObject(bubble);
            gravityObjects.add(gravityObject);
        }
        GravityObject userProfileGravityObject = toGravityObject(userProfileBubble);
        userProfileGravityObject.mass = -100;
        gravityObjects.add(userProfileGravityObject);

        GravityObject schoenHierGravityObject = toGravityObject(schoenHierBubble);
        schoenHierGravityObject.mass = 400;
        gravityObjects.add(schoenHierGravityObject);

        GravitySimulator simulator = new GravitySimulator(10.0, layout.getWidth(), layout.getHeight());
        simulator.simulateGravity(gravityObjects, 200);
        for (GravityObject gravityObject: gravityObjects) {
            Bubble bubble = (Bubble)gravityObject.payload;
            if (!bubble.positionFixed) {
                int posX = (int) gravityObject.x;
                int posY = (int) gravityObject.y;
                layout.setBubbleCenter(bubble.view, posX, posY);
            }
        }
    }

    private GravityObject toGravityObject(Bubble bubble) {
        GravityObject gravityObject = new GravityObject(bubble.positionFixed);
        gravityObject.payload = bubble;
        gravityObject.radius = layout.getBubbleRadius(bubble.view);
        gravityObject.mass = gravityObject.radius * 2;
        gravityObject.x = layout.getBubbleCenter(bubble.view).x;
        gravityObject.y = layout.getBubbleCenter(bubble.view).y;
        return gravityObject;
    }

    private Bubble makeLocationBubble(LocatorLocation location, int priority) {
        BubbleView view = new BubbleView(layout.getContext());
        view.setAlpha(0);
        layout.addView(view);
        layout.setBubbleRadius(view, getRadiusByPriority(priority));
        view.setFillColor(color(R.color.innerRed));
        view.setStrokeColor(color(R.color.borderRed));
        view.setStrokeWidth(getStrokeWidthByPriority(priority));
        view.setImage(location.thumbnailUri());

        Bubble bubble = new Bubble();
        bubble.data = location;
        bubble.priority = priority;
        bubble.view = view;
        view.setOnClickListener(listener -> handleOnLocationBubbleClicked(bubble));
        return bubble;
    }

    private int getPriority(Object o, List<?> list) {
        return list.indexOf(o);
    }

    private void handleOnLocationBubbleClicked(Bubble bubble) {
        LocatorLocation location = (LocatorLocation) bubble.data;
        LocationController.getInstance()
                .getLocationById(location.id)
                .subscribe(
                        (result) -> {
                            Intent intent = new Intent(layout.getContext(), LocationDetailActivity.class);
                            intent.putExtra("location", result);
                            layout.getContext().startActivity(intent);
                        },
                        (err) -> {
                            UiError.showError(layout.getContext(), err, "Die Location konnte nicht geladen werden :(");
                        }
                );
    }

    private void positionBubblesInDifferentQuadrants() {
        int nextQuadrant = 0;
        int priority = 0;
        while (nextQuadrant != -1) {
            nextQuadrant = positionBubbles(priority, nextQuadrant+1);
            priority++;
        }
    }

    private int positionBubbles(int priority, int startQuadrant) {
        List<Bubble> bubblesToPosition = Observable.from(bubbles)
                                                .filter(bubble -> bubble.priority == priority)
                                                .toList().toBlocking().single();
        if (bubblesToPosition.isEmpty()) {
            return -1;
        }
        int quadrant = startQuadrant;
        for (Bubble bubble: bubblesToPosition) {
            Point bubbleCenter = getInitialBubbleCenter(quadrant);
            layout.setBubbleCenter(bubble.view, bubbleCenter.x, bubbleCenter.y);
            quadrant = ((quadrant) % 4) + 1;
        }
        return quadrant;
    }

    private Point getInitialBubbleCenter(int quadrant) {
        final double distanceFromBorder = 0.2;
        double distanceFromLeftInPercent;
        double distanceFromTopInPercent;

        if (quadrant == 1) {
            distanceFromLeftInPercent = 1.0 - distanceFromBorder;
            distanceFromTopInPercent = distanceFromBorder;
        } else if (quadrant == 2) {
            distanceFromLeftInPercent = distanceFromBorder;
            distanceFromTopInPercent = distanceFromBorder;
        } else if (quadrant == 3) {
            distanceFromLeftInPercent = distanceFromBorder;
            distanceFromTopInPercent = 0.9 - distanceFromBorder;
        } else { // quadrant == 4
            distanceFromLeftInPercent = 1.0 - distanceFromBorder;
            distanceFromTopInPercent = 0.9 - distanceFromBorder;
        }

        int x = (int) (distanceFromLeftInPercent * layout.getWidth());
        int y = (int) (distanceFromTopInPercent * layout.getHeight());
        return new Point(x, y);
    }

    private int getRadiusByPriority(int priority) {
        double widthFactor = 0.1;
        if (priority == -1) {
            widthFactor = 0.2;
        }
        else if (priority == 0) {
            widthFactor = 0.15;
        }
        else if (priority <= 3) {
            widthFactor = 0.12;
        }
        return (int) (widthFactor * layout.getWidth());
    }

    private int getStrokeWidthByPriority(int priority) {
        return (int) (layout.getWidth() * 0.015);
    }

    private int color(int id) {
        return ContextCompat.getColor(layout.getContext(), id);
    }
}
