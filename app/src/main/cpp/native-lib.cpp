#include <jni.h>
#include <string>
#include <iostream>
#include <vector>
#include <opencv2/opencv.hpp>
#include <opencv2/core.hpp>
#include <opencv2/highgui/highgui.hpp>

using namespace cv;
using namespace std;

CvPoint aoiGravityCenter(IplImage *src) {

    CvPoint center;

    double m00, m10, m01;

    CvMoments moment;

    //函数 cvMoments 计算最高达三阶的空间和中心矩，并且将结果存在结构 moments 中
    cvMoments(src, &moment, 1);

    //从矩状态结构中提取空间矩
    m00 = cvGetSpatialMoment(&moment, 0, 0);
    if (m00 == 0)
        return 1;

    m10 = cvGetSpatialMoment(&moment, 1, 0);
    m01 = cvGetSpatialMoment(&moment, 0, 1);

    center.x = (int) (m10 / m00);
    center.y = (int) (m01 / m00);

    return center;
}

CvPoint grayCenter(IplImage *TheImage) {
    //灰度重心法求质心
    CvPoint Center;

    int i, j;

    CvScalar cs = cvSum(TheImage);

    Center.x = Center.y = 0;

    double x = 0;
    double y = 0;

    for (i = 0; i < TheImage->width; i++) {
        for (j = 0; j < TheImage->height; j++) {
            CvScalar s = cvGet2D(TheImage, j, i);
            x += i * s.val[0] / cs.val[0];
            y += j * s.val[0] / cs.val[0];
        }
    }

    Center.x = cvRound(x);
    Center.y = cvRound(y);

    return Center;
}

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

}

extern "C"
JNIEXPORT jint JNICALL
Java_yangchengyu_shmtu_edu_cn_calligraphyrecognize_utils_ImageProcessUtils_nativeGravity(
        JNIEnv *env, jclass type, jlong matSrcAddr, jobject x, jobject y) {

    Mat &src = *(Mat *) matSrcAddr;//通过指针获取Java层对应空间的原始图片mat

    IplImage frame = IplImage(src);
    CvPoint result = aoiGravityCenter(&frame);

    //通过JNI传值，保存到Java层的数据
    jclass c;
    jfieldID id;

    c = env->FindClass("java/lang/Integer");
    id = env->GetFieldID(c, "value", "I");

    env->SetIntField(x, id, (int) result.x);
    env->SetIntField(y, id, (int) result.y);

    return 0;
}

extern "C"
JNIEXPORT jdouble JNICALL
Java_yangchengyu_shmtu_edu_cn_calligraphyrecognize_utils_ImageProcessUtils_nativeBinaryRatio__J(
        JNIEnv *env, jclass type, jlong matSrcAddr) {

    Mat &src = *(Mat *) matSrcAddr;//通过指针获取Java层对应空间的原始图片mat

    double counterW = 0.0;
    double counterB = 0.0;
    //迭代器访问像素点
    Mat_<uchar>::iterator it = src.begin<uchar>();
    Mat_<uchar>::iterator itend = src.end<uchar>();
    for (; it != itend; ++it) {
        if ((*it) > 0) {
            counterW += 1;
        } else {
            counterB += 1;
        }
    }
    jdouble ratio = counterB / counterW;
    return ratio;

}