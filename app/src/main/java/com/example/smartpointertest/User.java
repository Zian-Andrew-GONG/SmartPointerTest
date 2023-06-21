package com.example.smartpointertest;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class User {
    private long nativePtr;

    private AtomicBoolean hasDestroyed = new AtomicBoolean(false);

    private User(long nativePtr) {
        this.nativePtr = nativePtr;
    }

    public static native User create(@NonNull UserOption userOption);

    private native void nativeDestroy();

    public void destroy() {
        if (hasDestroyed.compareAndSet(false, true)) {
            this.nativeDestroy();
        }
    }

    public native String getId();

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (hasDestroyed.compareAndSet(false, true)) {
            this.nativeDestroy();
        }
    }
}
