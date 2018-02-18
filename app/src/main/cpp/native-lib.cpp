#include <jni.h>
#include <string>
#include <stdio.h>
#include <stdlib.h>


extern "C"
JNIEXPORT jstring

JNICALL
Java_yangchengyu_shmtu_edu_cn_calligraphyrecognize_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jintArray JNICALL
Java_yangchengyu_shmtu_edu_cn_calligraphyrecognize_activity_ImageActivity_ImgFun(JNIEnv *env,
                                                                                 jclass type,
                                                                                 jintArray buf_,
                                                                                 jint w, jint h) {
    jint *buf = env->GetIntArrayElements(buf_, NULL);

    // TODO

    env->ReleaseIntArrayElements(buf_, buf, 0);
}