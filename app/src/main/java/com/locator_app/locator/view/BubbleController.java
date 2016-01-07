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
            Point bubbleCenter = getInitialBubbleCenter(quadrant);
            bubbleLayout.setBubbleCenter(bubble.bubbleView, bubbleCenter.x, bubbleCenter.y);
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

        // location with priority=1 changes to lowest priority
        response.locations.add(response.locations.remove(1));

        // one totally new location
        response.locations.get(1).location.id = "567a96f3990007900125f513";
        response.locations.get(1).location.title = "Sonnenuntergang am See";
        response.locations.get(1).location.images.small = "/api/v2/file/567a96e5990007900125f138/sonnenuntergang_am_seerhein.jpeg";


        for (BubbleScreenResponse.LocationResult locationResult: response.locations) {
            Bubble bubble = new Bubble();
            bubble.data = locationResult.location;
            bubble.priority = response.locations.indexOf(locationResult);
            newBubbles.add(bubble);
        }
        Observable.from(newBubbles).forEach(newBubble -> merge(newBubble, newBubbles));
    }

    private void merge(Bubble newBubble, List<Bubble> newBubbles) {

        // this implementation works but looks really messy - cleanup!

        Bubble correspondingBubble = Observable.from(bubbles)
                                        .filter(bubble -> bubble.data.equals(newBubble.data))
                                        .toBlocking()
                                        .firstOrDefault(null);

        if (correspondingBubble != null) {
            merge(correspondingBubble, newBubble);
        } else {
            // find a bubble from which can be replaced
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
            onScreen.priority = newBubble.priority;
            int radius = BubbleViewHelper.getRadiusForPriority(onScreen.priority, bubbleLayout);
            bubbleLayout.setBubbleRadius(onScreen.bubbleView, radius);
        }
        // data may have changed
        if (!onScreen.data.equals(newBubble.data)) {
            onScreen.data = newBubble.data;
            if (onScreen.data instanceof LocatorLocation) {
                LocatorLocation location = (LocatorLocation) onScreen.data;
                onScreen.bubbleView.loadImage(location.images.getSmall());
            }
        }
    }
}
