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


//实现JNI的测试方法
extern "C"
JNIEXPORT jstring JNICALL
Java_yangchengyu_shmtu_edu_cn_calligraphyrecognize_fragment_MainFragment_stringFromJNI(JNIEnv *env,
                                                                                       jobject instance) {

    return env->NewStringUTF("Hello from JNI");

}

/*
 * 实现图像骨架化的Native方法：Rosenfeld细化算法
 * @param src：原图片
 * @return dst：细化后图片
 *
 * Rosenfeld细化算法描述如下：
 * 1. 扫描所有像素，如果像素是北部边界点，且是8simple，但不是孤立点和端点，删除该像素。
 * 2. 扫描所有像素，如果像素是南部边界点，且是8simple，但不是孤立点和端点，删除该像素。
 * 3. 扫描所有像素，如果像素是东部边界点，且是8simple，但不是孤立点和端点，删除该像素。
 * 4. 扫描所有像素，如果像素是西部边界点，且是8simple，但不是孤立点和端点，删除该像素。
 *
 * 执行完上面4个步骤后，就完成了一次迭代，我们重复执行上面的迭代过程，
 * 直到图像中再也没有可以删除的点后，退出迭代循环。
 *
 */
extern "C"
JNIEXPORT void JNICALL
Java_yangchengyu_shmtu_edu_cn_calligraphyrecognize_utils_ImageProcessUtils_gThin(JNIEnv *env,
                                                                                 jclass type,
                                                                                 jlong matSrcAddr,
                                                                                 jlong matDstAddr) {

    Mat &src = *(Mat *) matSrcAddr;//通过指针获取Java层对应空间的原始图片mat
    Mat &dst = *(Mat *) matDstAddr;//通过指针返回Java层的处理后图片mat

    if (dst.data != src.data) {
        src.copyTo(dst);
    }

    int i, j, n;
    int width, height;
    //方便处理8邻域，防止越界
    width = src.cols - 1;
    height = src.rows - 1;
    int step = src.step;

    /**
     * p4 p3 p2
     * p5 p0 p1
     * p6 p7 p8
     * */

    int p1, p2, p3, p4, p5, p6, p7, p8;
    uchar *img;
    bool ifEnd;
    cv::Mat tmpimg;
    int dir[4] = {-step, step, 1, -1};

    while (1) {
        //分四个子迭代过程，分别对应上、下、左、右四个边界点的情况
        ifEnd = false;
        for (n = 0; n < 4; n++) {
            dst.copyTo(tmpimg);
            img = tmpimg.data;
            for (i = 1; i < height; i++) {
                img += step;
                for (j = 1; j < width; j++) {
                    uchar *p = img + j;
                    //如果p点是背景点或者且为方向边界点，依次为上下右左，继续循环
                    if (p[0] == 0 || p[dir[n]] > 0) continue;
                    p1 = p[-step] > 0 ? 1 : 0;
                    p2 = p[-step + 1] > 0 ? 1 : 0;
                    p3 = p[1] > 0 ? 1 : 0;
                    p4 = p[step + 1] > 0 ? 1 : 0;
                    p5 = p[step] > 0 ? 1 : 0;
                    p6 = p[step - 1] > 0 ? 1 : 0;
                    p7 = p[-1] > 0 ? 1 : 0;
                    p8 = p[-step - 1] > 0 ? 1 : 0;
                    //8 simple判定
                    int is8simple = 1;
                    if (p1 == 0 && p5 == 0) {
                        if ((p8 == 1 || p7 == 1 || p6 == 1) && (p2 == 1 || p3 == 1 || p4 == 1))
                            is8simple = 0;
                    }
                    if (p3 == 0 && p7 == 0) {
                        if ((p8 == 1 || p1 == 1 || p2 == 1) && (p4 == 1 || p5 == 1 || p6 == 1))
                            is8simple = 0;
                    }
                    if (p7 == 0 && p1 == 0) {
                        if (p8 == 1 && (p2 == 1 || p3 == 1 || p4 == 1 || p5 == 1 || p6 == 1))
                            is8simple = 0;
                    }
                    if (p3 == 0 && p1 == 0) {
                        if (p2 == 1 && (p4 == 1 || p5 == 1 || p6 == 1 || p7 == 1 || p8 == 1))
                            is8simple = 0;
                    }
                    if (p7 == 0 && p5 == 0) {
                        if (p6 == 1 && (p2 == 9 || p1 == 1 || p2 == 1 || p3 == 1 || p4 == 1))
                            is8simple = 0;
                    }
                    if (p3 == 0 && p5 == 0) {
                        if (p4 == 1 && (p6 == 1 || p7 == 1 || p8 == 1 || p1 == 1 || p2 == 1))
                            is8simple = 0;
                    }
                    int adjsum;
                    adjsum = p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8;
                    //判断是否是邻接点或孤立点,0,1分别对于那个孤立点和端点
                    if (adjsum != 1 && adjsum != 0 && is8simple == 1) {
                        //满足删除条件，设置当前像素为0
                        dst.at<uchar>(i, j) = 0;
                        ifEnd = true;
                    }
                }
            }
        }

        //已经没有可以细化的像素了，则退出迭代
        if (!ifEnd) break;
    }

}extern "C"
JNIEXPORT void JNICALL
Java_yangchengyu_shmtu_edu_cn_calligraphyrecognize_utils_ImageProcessUtils_strokeLinked(JNIEnv *env,
                                                                                        jclass type,
                                                                                        jlong matSrcAddr,
                                                                                        jobjectArray link) {

    Mat &src = *(Mat *) matSrcAddr;//通过指针获取Java层对应空间的原始图片mat

    int i, j, n;
    int w, h;
    //方便处理8邻域，防止越界
    w = src.cols - 1;
    h = src.rows - 1;
    int step = src.step;

    /**
     * p4 p3 p2
     * p5 p0 p1
     * p6 p7 p8
     * */

    int p1, p2, p3, p4, p5, p6, p7, p8;
    uchar *img;
    bool ifEnd;
    cv::Mat tmpimg;
    int dir[4] = {-step, step, 1, -1};
}