#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_smartpointertest_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

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

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_smartpointertest_User_getId(JNIEnv *env, jobject thiz) {
    // TODO: implement getId()
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
    // TODO: implement create()
    UserOption opts;
    auto user_option_clazz = env->GetObjectClass(user_option);
    auto id_field = env->GetFieldID(user_option_clazz, "id", "Ljava/lang/String;");
    auto id = env->GetObjectField(user_option, id_field);
    auto id_str = env->GetStringUTFChars((jstring)id, nullptr);
    opts.id = id_str;
    auto name_field = env->GetFieldID(user_option_clazz, "name", "Ljava/lang/String;");
    auto name = env->GetObjectField(user_option, id_field);
    auto name_str = env->GetStringUTFChars((jstring)id, nullptr);
    opts.name = name_str;

    auto user = User::create(opts);
    auto ptr = new std::shared_ptr<User>(user);
    auto field = env->GetFieldID(clazz, "nativePtr", "J");
    auto constructor = env->GetMethodID(clazz, "<init>", "(J)V");
    jobject object = env->NewObject(clazz, constructor, (jlong) ptr);
    return object;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_smartpointertest_User_destroy(JNIEnv *env, jobject thiz) {
    // TODO: implement destroy()
    auto clazz = env->GetObjectClass(thiz);
    auto field = env->GetFieldID(clazz, "nativePtr", "J");
    auto ptr = env->GetLongField(thiz, field);
    if (ptr != 0) {
        auto raw_ptr = reinterpret_cast<std::shared_ptr<User> *>(ptr);
        delete raw_ptr;
        env->SetLongField(thiz, field, (jlong) 0);
    }
}