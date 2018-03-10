#include <jni.h>
#include <string>
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <vector>
#include <opencv2/opencv.hpp>
#include <opencv2/core.hpp>

using namespace cv;
using namespace std;


extern "C"
JNIEXPORT jstring JNICALL
Java_yangchengyu_shmtu_edu_cn_calligraphyrecognize_fragment_MainFragment_stringFromJNI(JNIEnv *env,
                                                                                       jobject instance) {

    return env->NewStringUTF("Hello from JNI");
}extern "C"
JNIEXPORT jintArray JNICALL
Java_yangchengyu_shmtu_edu_cn_calligraphyrecognize_utils_ImageProcessUtils_grayPic(JNIEnv *env,
                                                                                   jobject instance,
                                                                                   jintArray Pixel_,
                                                                                   jint w, jint h) {
    jint *Pixel = env->GetIntArrayElements(Pixel_, NULL);

    if (Pixel == NULL) {
        return NULL;
    }
    cv::Mat imgData(w, h, CV_8UC4, Pixel);
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

extern "C"
JNIEXPORT void JNICALL
Java_yangchengyu_shmtu_edu_cn_calligraphyrecognize_utils_ImageProcessUtils_gThin(JNIEnv *env,
                                                                                 jclass type,
                                                                                 jlong matSrcAddr,
                                                                                 jlong matDstAddr) {

    const int maxIterations = -1;
    // TODO

    Mat &src = *(Mat *) matSrcAddr;
    Mat &dst = *(Mat *) matDstAddr;

    if(dst.data!=src.data)
    {
        src.copyTo(dst);
    }

    int i, j, n;
    int width, height;
    //之所以减1，是方便处理8邻域，防止越界
    width = src.cols -1;
    height = src.rows -1;
    int step = src.step;
    int  p2,p3,p4,p5,p6,p7,p8,p9;
    uchar* img;
    bool ifEnd;
    cv::Mat tmpimg;
    int dir[4] = {-step, step, 1, -1};

    while(1)
    {
        //分四个子迭代过程，分别对应北，南，东，西四个边界点的情况
        ifEnd = false;
        for(n =0; n < 4; n++)
        {
            dst.copyTo(tmpimg);
            img = tmpimg.data;
            for(i = 1; i < height; i++)
            {
                img += step;
                for(j =1; j<width; j++)
                {
                    uchar* p = img + j;
                    //如果p点是背景点或者且为方向边界点，依次为北南东西，继续循环
                    if(p[0]==0||p[dir[n]]>0) continue;
                    p2 = p[-step]>0?1:0;
                    p3 = p[-step+1]>0?1:0;
                    p4 = p[1]>0?1:0;
                    p5 = p[step+1]>0?1:0;
                    p6 = p[step]>0?1:0;
                    p7 = p[step-1]>0?1:0;
                    p8 = p[-1]>0?1:0;
                    p9 = p[-step-1]>0?1:0;
                    //8 simple判定
                    int is8simple = 1;
                    if(p2==0&&p6==0)
                    {
                        if((p9==1||p8==1||p7==1)&&(p3==1||p4==1||p5==1))
                            is8simple = 0;
                    }
                    if(p4==0&&p8==0)
                    {
                        if((p9==1||p2==1||p3==1)&&(p5==1||p6==1||p7==1))
                            is8simple = 0;
                    }
                    if(p8==0&&p2==0)
                    {
                        if(p9==1&&(p3==1||p4==1||p5==1||p6==1||p7==1))
                            is8simple = 0;
                    }
                    if(p4==0&&p2==0)
                    {
                        if(p3==1&&(p5==1||p6==1||p7==1||p8==1||p9==1))
                            is8simple = 0;
                    }
                    if(p8==0&&p6==0)
                    {
                        if(p7==1&&(p3==9||p2==1||p3==1||p4==1||p5==1))
                            is8simple = 0;
                    }
                    if(p4==0&&p6==0)
                    {
                        if(p5==1&&(p7==1||p8==1||p9==1||p2==1||p3==1))
                            is8simple = 0;
                    }
                    int adjsum;
                    adjsum = p2 + p3 + p4+ p5 + p6 + p7 + p8 + p9;
                    //判断是否是邻接点或孤立点,0,1分别对于那个孤立点和端点
                    if(adjsum!=1&&adjsum!=0&&is8simple==1)
                    {
                        dst.at<uchar>(i,j) = 0; //满足删除条件，设置当前像素为0
                        ifEnd = true;
                    }

                }
            }
        }

        //printf("\n");
        //PrintMat(dst);
        //PrintMat(dst);
        //已经没有可以细化的像素了，则退出迭代
        if(!ifEnd) break;
    }

}