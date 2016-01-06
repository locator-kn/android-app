package com.locator_app.locator.util;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.locator_app.locator.LocatorApplication;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CacheImageLoader {
    Map<String, Bitmap> cache = new HashMap<>();
    final List<String> loading = new LinkedList<>();

    private Observable<Bitmap> loadSync(final String imageUri) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                Bitmap bitmap = null;
                if (cache.containsKey(imageUri)) {
                    bitmap = cache.get(imageUri);
                } else {
                    boolean loadImage = false;
                    synchronized (loading) {
                        if (!loading.contains(imageUri)) {
                            // this thread will load the image
                            loadImage = true;
                            loading.add(imageUri);
                        } else {
                            bitmap = sleepForBitmapInCache(imageUri);
                        }
                    }

                    if (loadImage) {
                        bitmap = ImageLoader.getInstance().loadImageSync(imageUri);
                        if (bitmap != null) {
                            cache.put(imageUri, bitmap);
                        }
                        synchronized (loading) {
                            loading.remove(imageUri);
                            loading.notifyAll();
                        }
                    }
                }

                if (bitmap == null) {
                    Log.e("CacheImageLoader", "could not load image " + imageUri);
                    subscriber.onError(new Exception("could not load image " + imageUri));
                } else {
                    subscriber.onNext(bitmap);
                    subscriber.onCompleted();
                }
            }
        });
    }

    private Bitmap sleepForBitmapInCache(final String imageUri) {
        while (loading.contains(imageUri)) {
            try {
                loading.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // note: this may return null if image loading failed
        return cache.get(imageUri);
    }

    public Observable<Bitmap> loadAsync(final String imageUri) {
        return loadSync(imageUri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void setImage(final String imageUri, ImageView view) {
        loadAsync(imageUri).subscribe(
                (bitmap -> view.setImageBitmap(bitmap)),
                (error -> {})
        );
    }

    static CacheImageLoader instance;
    synchronized public static CacheImageLoader getInstance() {
        if (instance == null) {
            instance = new CacheImageLoader();
            if (!ImageLoader.getInstance().isInited()) {
                ImageLoaderConfiguration configuration =
                        new ImageLoaderConfiguration.Builder(LocatorApplication.getAppContext()).build();
                ImageLoader.getInstance().init(configuration);
            }
        }
        return instance;
    }
}
