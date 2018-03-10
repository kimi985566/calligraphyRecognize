#include <jni.h>
#include <string>
#include <stdio.h>
#include <stdlib.h>
#include <opencv2/opencv.hpp>
#include <opencv2/core.hpp>

using namespace cv;
using namespace std;


extern "C"
JNIEXPORT jstring JNICALL
Java_yangchengyu_shmtu_edu_cn_calligraphyrecognize_fragment_MainFragment_stringFromJNI(JNIEnv *env,
                                                                                       jobject instance) {

    // TODO


    return env->NewStringUTF("Hello from JNI");
}extern "C"
JNIEXPORT jintArray JNICALL
Java_yangchengyu_shmtu_edu_cn_calligraphyrecognize_utils_ImageProcessUtils_grayPic(JNIEnv *env,
                                                                                   jobject instance,
                                                                                   jintArray Pixel_,
                                                                                   jint w, jint h) {
    jint *Pixel = env->GetIntArrayElements(Pixel_, NULL);


    // TODO
    if (Pixel == NULL) {
        return NULL;
    }
    cv::Mat imgData(h, w, CV_8UC4, Pixel);
    uchar *ptr = imgData.ptr(0);
    for (int i = 0; i < w * h; i++) {
        int grayScale = (int) (ptr[4 * i + 2] * 0.299 + ptr[4 * i + 1] * 0.587
                               + ptr[4 * i + 0] * 0.114);
        ptr[4 * i + 1] = (uchar) grayScale;
        ptr[4 * i + 2] = (uchar) grayScale;
        ptr[4 * i + 0] = (uchar) grayScale;
    }

    int size = w * h;
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, Pixel);
    env->ReleaseIntArrayElements(Pixel_, Pixel, 0);
    return result;
}