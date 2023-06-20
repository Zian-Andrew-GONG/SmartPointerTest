package com.example.smartpointertest;

import androidx.annotation.NonNull;

public class User {
    private long nativePtr;

    private User(long nativePtr) {
        this.nativePtr = nativePtr;
    }

    public static native User create(@NonNull UserOption userOption);
    public native void destroy();
    public native String getId();
}
