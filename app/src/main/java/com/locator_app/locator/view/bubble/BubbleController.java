package com.locator_app.locator.view.bubble;

import android.content.Intent;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.apiservice.my.BubbleScreenResponse;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.model.LocatorObject;
import com.locator_app.locator.model.Message;
import com.locator_app.locator.view.LocationDetailActivity;

import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class BubbleController {

    class Bubble {
        BubbleView view;
        LocatorObject data;
        int priority;
        boolean positionFixed = false;
    }

    private List<Bubble> bubbles = new LinkedList<>();
    private RelativeBubbleLayout layout;
    private Bubble schoenHierBubble;
    private Bubble userProfileBubble;
    private boolean needGravityUpdate = false;

    public BubbleController(RelativeBubbleLayout layout) {
        this.layout = layout;

        schoenHierBubble = new Bubble();
        schoenHierBubble.priority = -1;
        schoenHierBubble.positionFixed = true;
        schoenHierBubble.view = (BubbleView)layout.findViewById(R.id.schoenHierBubble);

        userProfileBubble = new Bubble();
        userProfileBubble.priority = -1;
        userProfileBubble.positionFixed = true;
        userProfileBubble.view = (BubbleView)layout.findViewById(R.id.userProfileBubble);
    }

    public void initSchoenHierBubble() {
        int radius = getRadiusByPriority(schoenHierBubble.priority);
        layout.setBubbleRadius(schoenHierBubble.view, radius);
        layout.setBubbleCenter(schoenHierBubble.view, 0.5, 0.38);
        schoenHierBubble.view.setImage(R.drawable.schoenhier);
    }

    public void initUserProfileBubble() {
        int radius = getRadiusByPriority(10);
        layout.setBubbleRadius(userProfileBubble.view, radius);
        layout.setBubbleCenter(userProfileBubble.view, 0.5, 0.89);
        userProfileBubble.view.setImage(R.drawable.profile);
    }

    public void onBubbleScreenUpdate(BubbleScreenResponse response) {
        needGravityUpdate = false;
        if (bubbles.isEmpty()) {
            handleFirstScreenUpdate(response);
            needGravityUpdate = true;
        } else {
            handleBubbleUpdate(response);
        }
        if (needGravityUpdate) {
            simulateGravity();
            simulateGravity();
            simulateGravity();
        }
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

    private void handleFirstScreenUpdate(final BubbleScreenResponse response) {
        Observable<Bubble> locations = Observable.from(response.locations)
                .take(10)
                .map(locationResult -> {
                    final int priority = getPriority(locationResult, response.locations);
                    return makeLocationBubble(locationResult.location, priority);
                });
        Observable<Bubble> messages = Observable.from(response.messages)
                .take(0)
                .map(message -> {
                    final int priority = getPriority(message, response.messages);
                    return makeMessageBubble(message, priority);
                });
        Observable.merge(locations, messages)
            .subscribe(
                    bubbles::add,
                    (error) -> {
                        Log.d("BubbleController", error.getMessage());
                    });
        positionBubblesInDifferentQuadrants();
    }

    private Bubble makeMessageBubble(Message message, int priority) {
        BubbleView view = new BubbleView(layout.getContext());
        layout.addView(view);
        layout.setBubbleRadius(view, getRadiusByPriority(priority));
        view.setFillColor(color(R.color.innerYellow));
        view.setStrokeColor(color(R.color.borderYellow));
        view.setStrokeWidth(getStrokeWidthByPriority(priority));
        view.setImage(R.drawable.message);

        Bubble bubble = new Bubble();
        bubble.data = message;
        bubble.priority = priority;
        bubble.view = view;
        view.setOnClickListener(listener -> handleOnMessageBubbleClicked(bubble));
        return bubble;
    }

    private Bubble makeLocationBubble(LocatorLocation location, int priority) {
        BubbleView view = new BubbleView(layout.getContext());
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

                        }
                );
    }

    private void handleOnMessageBubbleClicked(Bubble bubble) {
        Message message = (Message) bubble.data;
        Toast.makeText(layout.getContext(), message.message, Toast.LENGTH_SHORT).show();
    }

    private void positionBubblesInDifferentQuadrants() {
        int nextQuadrant = 0;
        int priority = 0;
        while (true) {
            nextQuadrant = positionBubbles(priority, nextQuadrant+1);
            priority++;
            if (nextQuadrant == -1) {
                break;
            }
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

    private void handleBubbleUpdate(BubbleScreenResponse response) {
        Observable<Bubble> locations = Observable.from(response.locations)
                .map(locationResult -> {
                    Bubble bubble = new Bubble();
                    bubble.data = locationResult.location;
                    bubble.priority = getPriority(locationResult, response.locations);
                    return bubble;
                });
        Observable<Bubble> messages = Observable.from(response.messages)
                .map(message -> {
                    Bubble bubble = new Bubble();
                    bubble.data = message;
                    bubble.priority = getPriority(message, response.messages);
                    return bubble;
                });
        Observable<Bubble> merged = Observable.merge(locations, messages);
        Iterable<Bubble> newBubbles = merged.toBlocking().toIterable();
        merged.forEach(bubble -> merge(bubble, newBubbles));
    }

    private void merge(Bubble newBubble, Iterable<Bubble> newBubbles) {

        // this implementation works but looks really messy - cleanup!

        Bubble correspondingBubble = Observable.from(bubbles)
                                        .filter(bubble -> bubble.data.equals(newBubble.data))
                                        .toBlocking()
                                        .firstOrDefault(null);

        if (correspondingBubble != null) {
            merge(correspondingBubble, newBubble);
        } else {
            // find a bubble which can be replaced
            Iterable<Bubble> candidates = Observable.from(bubbles)
                    .filter(bubble -> bubble.data.getClass().equals(newBubble.data.getClass()))
                    .toBlocking()
                    .toIterable();
            Iterable<Bubble> newBubblesWithTheSameType = Observable.from(newBubbles)
                    .filter(bubble -> bubble.data.getClass().equals(newBubble.data.getClass()))
                    .toBlocking()
                    .toIterable();

            for (Bubble candidate: candidates) {
                boolean mayBeReplaced = true;
                for (Bubble b: newBubblesWithTheSameType) {
                    if (candidate.data.equals(b.data)) {
                        mayBeReplaced = false;
                        break;
                    }
                }
                if (mayBeReplaced) {
                    merge(candidate, newBubble);
                    break;
                }
            }
        }
    }

    private void merge(Bubble onScreen, Bubble newBubble) {
        // priority may have changed
        if (onScreen.priority != newBubble.priority) {
            needGravityUpdate = true;
            onScreen.priority = newBubble.priority;
            int radius = getRadiusByPriority(onScreen.priority);
            layout.setBubbleRadius(onScreen.view, radius);
        }
        // data may have changed
        if (!onScreen.data.equals(newBubble.data)) {
            onScreen.data = newBubble.data;
            onScreen.view.setImage(onScreen.data.thumbnailUri());
        }
    }

    private int getRadiusByPriority(int priority) {
        double widthFactor = 0.09;
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
        return (int) (layout.getWidth() * 0.013);
    }

    private int color(int id) {
        return ContextCompat.getColor(layout.getContext(), id);
    }
}
