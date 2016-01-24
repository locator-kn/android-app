package com.locator_app.locator.customglide;


import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.GlideModule;

public class LocatorGlideModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // increase image quality (default is RGB565 and looks ugly)
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);

        // set memory cache size
        final int memoryCacheSize = 8 * 1024 * 1024; // 8mb
        builder.setMemoryCache(new LruResourceCache(memoryCacheSize));

        // set disk cache size
        final int diskCacheSize = 100 * 1024 * 1024; // 100mb
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSize));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
