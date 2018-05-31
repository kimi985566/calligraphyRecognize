package yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils

import java.nio.charset.StandardCharsets

/**
 * CaffeMobile
 *
 *
 * 通过使用Caffe-android-lib动态生成手机上caffe的so文件
 * 使用so文件可以减少体积同时达到实现caffe深度学习识别的效果
 * 对于生成的库文件需要考虑cpu所使用的平台
 * 例如arm64-v8a,x86_64等
 * 主要是需要支持OpenBLAS
 */

class CaffeMobile {

    private fun stringToBytes(s: String): ByteArray {
        return s.toByteArray(StandardCharsets.US_ASCII)
    }

    //设置线程数
    external fun setNumThreads(numThreads: Int)

    //加载模型
    external fun loadModel(modelPath: String, weightsPath: String): Int

    //设置平均值文件
    private external fun setMeanWithMeanFile(meanFile: String)

    //设置平均值数值
    private external fun setMeanWithMeanValues(meanValues: FloatArray)

    external fun setScale(scale: Float)

    //设置置信值
    private external fun getConfidenceScore(data: ByteArray, width: Int, height: Int): FloatArray

    fun getConfidenceScore(imgPath: String): FloatArray {
        return getConfidenceScore(stringToBytes(imgPath), 0, 0)
    }

    //预测图片
    private external fun predictImage(data: ByteArray, width: Int, height: Int, k: Int): IntArray

    @JvmOverloads
    fun predictImage(imgPath: String, k: Int = 4): IntArray {
        return predictImage(stringToBytes(imgPath), 0, 0, k)
    }

    //获取特征
    private external fun extractFeatures(data: ByteArray, width: Int, height: Int, blobNames: String): Array<FloatArray>

    fun extractFeatures(imgPath: String, blobNames: String): Array<FloatArray> {
        return extractFeatures(stringToBytes(imgPath), 0, 0, blobNames)
    }

    fun setMean(meanValues: FloatArray) {
        setMeanWithMeanValues(meanValues)
    }

    fun setMean(meanFile: String) {
        setMeanWithMeanFile(meanFile)
    }
}
