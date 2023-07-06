package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.instagram.services.interfaces.CallBack;

import org.json.JSONException;

@SuppressLint("ViewConstructor")
public class OnSwipeListener implements View.OnTouchListener {
    private final GestureDetector gestureDetector;
    private final CallBack callBackDelete;
    private final CallBack callBackLike;
    private final Context context;
    private final View view;
    private final String author;
    private static float firstX;

    public OnSwipeListener(Context context, View view, CallBack callBackDelete, CallBack callBackLike, String author) {
        gestureDetector = new GestureDetector(context, new GestureListener());
        this.callBackLike = callBackLike;
        this.callBackDelete = callBackDelete;
        this.context = context;
        this.author = author;
        this.view = view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (author.equals(Cache.loadStringSP(context, CacheScopes.USER_LOGIN.toString()))) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    view.setX(OnSwipeListener.firstX);
                    OnSwipeListener.firstX = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                    int cardWidth = view.getWidth();
                    int cardStart = (displayMetrics.widthPixels / 2) - (cardWidth / 2);

                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        float newX = event.getRawX();
                        if (newX - cardWidth < cardStart) {
                            view.animate().x(Math.min(cardStart, newX - ((float) cardWidth / 2))).setDuration(0).start();
                        }
                    }

                    v.performClick();
                    break;
                case MotionEvent.ACTION_DOWN:
                    OnSwipeListener.firstX = v.getX();
                    break;
            }
        }
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int MIN_SWIPE_DISTANCE = 300;
        private static final int MIN_SWIPE_VELOCITY = 100;

        public GestureListener() {
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {
            super.onDoubleTap(e);
            try {
                callBackLike.likeUnlikePost();
            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }
            return true;
        }

        @Override
        public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            if (author.equals(Cache.loadStringSP(context, CacheScopes.USER_LOGIN.toString()))) {
                super.onFling(e1, e2, velocityX, velocityY);
                try {
                    float dx = e2.getX() - e1.getX();
                    float dy = e2.getY() - e1.getY();
                    if (Math.abs(dx) > Math.abs(dy)) {
                        if (Math.abs(dx) >= MIN_SWIPE_DISTANCE && Math.abs(velocityX) >= MIN_SWIPE_VELOCITY) {
                            callBackDelete.deletePost();
                            return true;
                        }
                    } else {
                        if (Math.abs(dy) >= MIN_SWIPE_DISTANCE && Math.abs(velocityY) >= MIN_SWIPE_VELOCITY) {
                            if (dy > 0) System.out.println("Swipe bottom");
                            else System.out.println("Swipe top");
                            return true;
                        }
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            return true;
        }

        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            super.onDown(e);
            return true;
        }
    }
}
