package com.locator_app.locator.view;

import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.model.Message;
import com.locator_app.locator.service.my.BubbleScreenResponse;

import java.util.LinkedList;
import java.util.List;


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
        int radius = (int) (0.2 * bubbleLayout.getWidth());
        bubbleLayout.setBubbleRadius(schoenHierBubble.bubbleView, radius);
        bubbleLayout.setBubbleCenter(schoenHierBubble.bubbleView, 0.5, 0.38);
        schoenHierBubble.bubbleView.loadImage("drawable://" + R.drawable.schoenhier);
    }

    public void initUserProfileBubble() {
        int radius = BubbleViewHelper.getRadiusForPriority(1, bubbleLayout);
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
        for (BubbleScreenResponse.LocationResult locationResult : response.locations) {
            final int priority = response.locations.indexOf(locationResult);
            Bubble bubble = new Bubble();
            bubble.priority = priority;
            bubble.bubbleView = BubbleViewHelper.createLocationBubbleView(bubbleLayout, priority);
            bubble.data = locationResult.location;
            bubbles.add(bubble);
            bubble.bubbleView.setOnClickListener(listener -> handleOnLocationBubbleClicked(bubble));
        }
        for (Message msg : response.messages) {
            final int priority = response.messages.indexOf(msg);
            Bubble bubble = new Bubble();
            bubble.priority = priority;
            bubble.bubbleView = BubbleViewHelper.createMessageBubbleView(bubbleLayout, priority);
            bubble.data = msg;
            bubbles.add(bubble);
            bubble.bubbleView.setOnClickListener(listener -> handleOnMessageBubbleClicked(bubble));
        }
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
        int quadrant = startQuadrant;
        List<Bubble> bubblesToPosition = getBubblesByPriority(priority);
        for (int i = 0; i < bubblesToPosition.size(); i++) {
            Bubble bubbleToPosition = bubblesToPosition.get(i);
            BubbleView bubbleView = bubbleToPosition.bubbleView;
            Point bubbleCenter = getBubbleCenter(quadrant);
            bubbleLayout.setBubbleCenter(bubbleView, bubbleCenter.x, bubbleCenter.y);
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

    private List<Bubble> getBubblesByPriority(int priority) {
        List<Bubble> bubbles = new LinkedList<>();
        for (Bubble bubble: this.bubbles) {
            if (bubble.priority == priority) {
                bubbles.add(bubble);
            }
        }
        return bubbles;
    }

    private void handleBubbleUpdate(BubbleScreenResponse response) {

    }
}
