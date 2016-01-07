package com.locator_app.locator.view.bubble;

import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.model.LocatorObject;
import com.locator_app.locator.model.Message;
import com.locator_app.locator.service.my.BubbleScreenResponse;

import java.util.LinkedList;
import java.util.List;

import rx.Observable;


public class BubbleController {

    class Bubble {
        BubbleView view;
        LocatorObject data;
        int priority;
    }

    private List<Bubble> bubbles = new LinkedList<>();
    private RelativeBubbleLayout layout;
    private Bubble schoenHierBubble;
    private Bubble userProfileBubble;

    public BubbleController(RelativeBubbleLayout layout) {
        this.layout = layout;

        schoenHierBubble = new Bubble();
        schoenHierBubble.priority = -1;
        schoenHierBubble.view = (BubbleView)layout.findViewById(R.id.schoenHierBubble);
        schoenHierBubble.positionFixed = true;

        userProfileBubble = new Bubble();
        userProfileBubble.priority = -1;
        userProfileBubble.view = (BubbleView)layout.findViewById(R.id.userProfileBubble);
        userProfileBubble.positionFixed = true;
    }

    public void initSchoenHierBubble() {
        int radius = getRadiusByPriority(schoenHierBubble.priority);
        layout.setBubbleRadius(schoenHierBubble.view, radius);
        layout.setBubbleCenter(schoenHierBubble.view, 0.5, 0.38);
        schoenHierBubble.view.loadImage("drawable://" + R.drawable.schoenhier);
    }

    public void initUserProfileBubble() {
        int radius = getRadiusByPriority(2);
        layout.setBubbleRadius(userProfileBubble.view, radius);
        layout.setBubbleCenter(userProfileBubble.view, 0.5, 0.89);
        userProfileBubble.view.loadImage("drawable://" + R.drawable.profile);
    }

    public void onBubbleScreenUpdate(BubbleScreenResponse response) {
        if (bubbles.isEmpty()) {
            handleFirstScreenUpdate(response);
        } else {
            handleBubbleUpdate(response);
        }
    }

    private void handleFirstScreenUpdate(final BubbleScreenResponse response) {
        Observable<Bubble> locations = Observable.from(response.locations)
                .map(locationResult -> {
                    final int priority = getPriority(locationResult, response.locations);
                    return makeBubble(locationResult.location, priority);
                });
        Observable<Bubble> messages = Observable.from(response.messages)
                .map(message -> {
                    final int priority = getPriority(message, response.messages);
                    return makeBubble(message, priority);
                });
        Observable.merge(locations, messages)
            .subscribe(bubble -> bubbles.add(bubble));
        positionBubblesInDifferentQuadrants();
    }

    private Bubble makeBubble(Message message, int priority) {
        BubbleView view = new BubbleView(layout.getContext());
        layout.addView(view);
        layout.setBubbleRadius(view, getRadiusByPriority(priority));
        view.setFillColor(color(R.color.innerYellow));
        view.setStrokeColor(color(R.color.borderYellow));
        view.setStrokeWidth(getStrokeWidthByPriority(priority));
        view.loadImage(message.thumbnailUri());

        Bubble bubble = new Bubble();
        bubble.priority = priority;
        bubble.data = message;
        bubble.view = view;
        view.setOnClickListener(listener -> handleOnMessageBubbleClicked(bubble));
        return bubble;
    }

    private Bubble makeBubble(LocatorLocation location, int priority) {
        BubbleView view = new BubbleView(layout.getContext());
        layout.addView(view);
        layout.setBubbleRadius(view, getRadiusByPriority(priority));
        view.setFillColor(color(R.color.innerRed));
        view.setStrokeColor(color(R.color.borderRed));
        view.setStrokeWidth(getStrokeWidthByPriority(priority));
        view.loadImage(location.thumbnailUri());

        Bubble bubble = new Bubble();
        bubble.priority = priority;
        bubble.data = location;
        bubble.view = view;
        view.setOnClickListener(listener -> handleOnLocationBubbleClicked(bubble));
        return bubble;
    }

    private int getPriority(Object o, List<?> list) {
        return list.indexOf(o);
    }

    private void handleOnLocationBubbleClicked(Bubble bubble) {
        LocatorLocation location = (LocatorLocation) bubble.data;
        Toast.makeText(layout.getContext(), location.title, Toast.LENGTH_SHORT).show();
    }

    private void handleOnMessageBubbleClicked(Bubble bubble) {
        Message message = (Message) bubble.data;
        Toast.makeText(layout.getContext(), message.message, Toast.LENGTH_SHORT).show();
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

        // location with priority=1 changes to lowest priority
        response.locations.add(response.locations.remove(1));

        // one totally new location
        response.locations.get(0).location.id = "567a96f3990007900125f513";
        response.locations.get(0).location.title = "Sonnenuntergang am See";
        response.locations.get(0).location.images.small = "/api/v2/file/567a96e5990007900125f138/sonnenuntergang_am_seerhein.jpeg";

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
            onScreen.priority = newBubble.priority;
            int radius = getRadiusByPriority(onScreen.priority);
            layout.setBubbleRadius(onScreen.view, radius);
        }
        // data may have changed
        if (!onScreen.data.equals(newBubble.data)) {
            onScreen.data = newBubble.data;
            onScreen.view.loadImage(onScreen.data.thumbnailUri());
        }
    }

    private int getRadiusByPriority(int priority) {
        double widthFactor = 0;
        if (priority == -1) {
            widthFactor = 0.2;
        }
        if (priority == 0) {
            widthFactor = 0.13;
        }
        if (priority == 1) {
            widthFactor = 0.10;
        }
        if (priority == 2) {
            widthFactor = 0.07;
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
