package com.locator_app.locator.view;

import android.graphics.Point;
import android.util.Log;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.model.Message;
import com.locator_app.locator.service.my.BubbleScreenResponse;

import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.observables.BlockingObservable;


public class BubbleController {

    class Bubble {
        BubbleView bubbleView;
        Object data;
        int priority;
        boolean fixed = false;
    }

    private List<Bubble> bubbles = new LinkedList<>();
    private RelativeBubbleLayout bubbleLayout;
    private Bubble schoenHierBubble;
    private Bubble userProfileBubble;

    public BubbleController(RelativeBubbleLayout bubbleLayout) {
        this.bubbleLayout = bubbleLayout;

        schoenHierBubble = new Bubble();
        schoenHierBubble.priority = -1;
        schoenHierBubble.bubbleView = (BubbleView)bubbleLayout.findViewById(R.id.schoenHierBubble);
        schoenHierBubble.fixed = true;

        userProfileBubble = new Bubble();
        userProfileBubble.priority = -1;
        userProfileBubble.bubbleView = (BubbleView)bubbleLayout.findViewById(R.id.userProfileBubble);
        userProfileBubble.fixed = true;
    }

    public void initSchoenHierBubble() {
        int radius = BubbleViewHelper.getRadiusForPriority(schoenHierBubble.priority, bubbleLayout);
        bubbleLayout.setBubbleRadius(schoenHierBubble.bubbleView, radius);
        bubbleLayout.setBubbleCenter(schoenHierBubble.bubbleView, 0.5, 0.38);
        schoenHierBubble.bubbleView.loadImage("drawable://" + R.drawable.schoenhier);
    }

    public void initUserProfileBubble() {
        final int prioritySize = 2; // this will create a small bubble
        int radius = BubbleViewHelper.getRadiusForPriority(prioritySize, bubbleLayout);
        bubbleLayout.setBubbleRadius(userProfileBubble.bubbleView, radius);
        bubbleLayout.setBubbleCenter(userProfileBubble.bubbleView, 0.5, 0.89);
        userProfileBubble.bubbleView.loadImage("drawable://" + R.drawable.profile);
    }

    void onBubbleScreenUpdate(BubbleScreenResponse response) {
        if (bubbles.isEmpty()) {
            handleFirstScreenUpdate(response);
        } else {
            handleBubbleUpdate(response);
        }
    }

    private void handleFirstScreenUpdate(BubbleScreenResponse response) {

        Observable.from(response.locations)
                .forEach(locationResult -> {
                    final int priority = response.locations.indexOf(locationResult);
                    Bubble bubble = new Bubble();
                    bubble.priority = priority;
                    bubble.bubbleView = BubbleViewHelper.createLocationBubbleView(bubbleLayout, priority);
                    bubble.data = locationResult.location;
                    bubble.bubbleView.setOnClickListener(listener -> handleOnLocationBubbleClicked(bubble));
                    bubble.bubbleView.loadImage(locationResult.location.images.getSmall());
                    bubbles.add(bubble);
                });

        Observable.from(response.messages)
                .forEach(message -> {
                    final int priority = response.messages.indexOf(message);
                    Bubble bubble = new Bubble();
                    bubble.priority = priority;
                    bubble.bubbleView = BubbleViewHelper.createMessageBubbleView(bubbleLayout, priority);
                    bubble.data = message;
                    bubble.bubbleView.setOnClickListener(listener -> handleOnMessageBubbleClicked(bubble));
                    bubbles.add(bubble);
                });

        positionBubblesInDifferentQuadrants();
    }

    private void handleOnLocationBubbleClicked(Bubble bubble) {
        LocatorLocation location = (LocatorLocation) bubble.data;
        Toast.makeText(bubbleLayout.getContext(), location.title, Toast.LENGTH_SHORT).show();
    }

    private void handleOnMessageBubbleClicked(Bubble bubble) {
        Message message = (Message) bubble.data;
        Toast.makeText(bubbleLayout.getContext(), message.message, Toast.LENGTH_SHORT).show();
    }

    private void positionBubblesInDifferentQuadrants() {
        final int startQuadrant = 1;
        int nextQuadrant = positionBubbles(0, startQuadrant);
        nextQuadrant = positionBubbles(1, nextQuadrant);
        nextQuadrant = positionBubbles(2, nextQuadrant);
    }

    private int positionBubbles(int priority, int startQuadrant) {
        Iterable<Bubble> bubblesToPosition = Observable.from(bubbles)
                                                .filter(bubble -> bubble.priority == priority)
                                                .toBlocking()
                                                .toIterable();

        int quadrant = startQuadrant;
        for (Bubble bubble: bubblesToPosition) {
            Point bubbleCenter = getBubbleCenter(quadrant);
            bubbleLayout.setBubbleCenter(bubble.bubbleView, bubbleCenter.x, bubbleCenter.y);
            quadrant = ((quadrant) % 4) + 1;
        }
        return quadrant;
    }

    private Point getBubbleCenter(int quadrant) {
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

        int x = (int) (distanceFromLeftInPercent * bubbleLayout.getWidth());
        int y = (int) (distanceFromTopInPercent * bubbleLayout.getHeight());
        return new Point(x, y);
    }

    private void handleBubbleUpdate(BubbleScreenResponse response) {
        List<Bubble> newBubbles = new LinkedList<>();
        for (Message msg: response.messages) {
            Bubble bubble = new Bubble();
            bubble.data = msg;
            bubble.priority = response.messages.indexOf(msg);
            newBubbles.add(bubble);
        }

        // test, delete me!
        response.locations.add(response.locations.remove(1));
        // test, delete me!

        for (BubbleScreenResponse.LocationResult locationResult: response.locations) {
            Bubble bubble = new Bubble();
            bubble.data = locationResult.location;
            bubble.priority = response.locations.indexOf(locationResult);
            newBubbles.add(bubble);
        }
        Observable.from(newBubbles).forEach(newBubble -> merge(newBubble));
    }

    private void merge(Bubble newBubble) {
        Bubble correspondingBubble = Observable.from(bubbles)
                                        .filter(bubble -> bubble.data.equals(newBubble.data))
                                        .toBlocking()
                                        .firstOrDefault(null);
        if (correspondingBubble != null) {
            if (correspondingBubble.priority != newBubble.priority) {
                // bubble will stay on home screen, but priority has changed
                correspondingBubble.priority = newBubble.priority;
                correspondingBubble.data = newBubble.data;
                int newRadius = BubbleViewHelper.getRadiusForPriority(newBubble.priority, bubbleLayout);
                bubbleLayout.setBubbleRadius(correspondingBubble.bubbleView, newRadius);
            }
        } else {
            // this is a new bubble, we have to replace another bubble
        }
    }
}
