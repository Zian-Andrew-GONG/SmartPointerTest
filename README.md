# Smart Pointer Test

An example of managing C++ smart pointer with Java JNI.

## C++ Classes

```C++
struct UserOption {
    std::string id;
    std::string name;
    int age;
    std::string tel;
    std::string address;
};

class User {
public:
    static std::shared_ptr<User> create(const UserOption &userOption) {
        std::shared_ptr<User> ptr(new User(userOption));
        return ptr;
    }

    std::string get_id() {
        return id;
    }

private:
    User(const UserOption &userOption) : id(userOption.id), name(userOption.name) {}

    std::string id;
    std::string name;
};
```

## Java Classes
UserOption.java
```Java
public class UserOption {
    private String id;
    private String name;

    public UserOption(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
```
User.java
```Java
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
```

## JNI Implementation
```C++
inline std::string string_cast(JNIEnv *env, const jobject &str) {
    auto str_char = env->GetStringUTFChars((jstring) str, nullptr);
    auto ret = std::string(str_char);
    env->ReleaseStringChars((jstring) str, (const jchar *) str_char);
    return ret;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_smartpointertest_User_getId(JNIEnv *env, jobject thiz) {
    auto clazz = env->GetObjectClass(thiz);
    auto field = env->GetFieldID(clazz, "nativePtr", "J");
    auto ptr = env->GetLongField(thiz, field);
    auto user = reinterpret_cast<std::shared_ptr<User> *>(ptr);
    auto id = (*user)->get_id();

    return env->NewStringUTF(id.c_str());
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_example_smartpointertest_User_create(JNIEnv *env, jclass clazz, jobject user_option) {
    UserOption opts;
    auto user_option_clazz = env->GetObjectClass(user_option);
    auto id_field = env->GetFieldID(user_option_clazz, "id", "Ljava/lang/String;");
    auto id = env->GetObjectField(user_option, id_field);
    opts.id = string_cast(env, id);
    auto name_field = env->GetFieldID(user_option_clazz, "name", "Ljava/lang/String;");
    auto name = env->GetObjectField(user_option, name_field);
    opts.name = string_cast(env, name);

    auto user = User::create(opts);
    auto ptr = new std::shared_ptr<User>(user);
    auto field = env->GetFieldID(clazz, "nativePtr", "J");
    auto constructor = env->GetMethodID(clazz, "<init>", "(J)V");
    jobject object = env->NewObject(clazz, constructor, (jlong) ptr);
    return object;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_smartpointertest_User_nativeDestroy(JNIEnv *env, jobject thiz) {
    auto clazz = env->GetObjectClass(thiz);
    auto field = env->GetFieldID(clazz, "nativePtr", "J");
    auto ptr = env->GetLongField(thiz, field);
    if (ptr != 0) {
        auto raw_ptr = reinterpret_cast<std::shared_ptr<User> *>(ptr);
        delete raw_ptr;
        env->SetLongField(thiz, field, (jlong) 0);
    }
}
```

