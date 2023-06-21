package com.example.smartpointertest;

import androidx.annotation.NonNull;

public class User {
    private long nativePtr;

    private boolean destroyed;

    private User(long nativePtr) {
        this.nativePtr = nativePtr;
    }

    public static native User create(@NonNull UserOption userOption);

    private native void nativeDestroy();

    public void destroy() {
        if (!destroyed) {
            destroyed = true;
            this.nativeDestroy();
        }
    }

    public native String getId();

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (!destroyed) {
            destroyed = true;
            this.nativeDestroy();
        }
    }
}
