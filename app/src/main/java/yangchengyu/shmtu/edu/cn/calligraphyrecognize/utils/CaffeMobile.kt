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

    external fun setNumThreads(numThreads: Int)

    external fun enableLog(enabled: Boolean)   // currently nonfunctional

    external fun loadModel(modelPath: String, weightsPath: String): Int   // required

    private external fun setMeanWithMeanFile(meanFile: String)

    private external fun setMeanWithMeanValues(meanValues: FloatArray)

    external fun setScale(scale: Float)

    external fun getConfidenceScore(data: ByteArray, width: Int, height: Int): FloatArray

    fun getConfidenceScore(imgPath: String): FloatArray {
        return getConfidenceScore(stringToBytes(imgPath), 0, 0)
    }

    external fun predictImage(data: ByteArray, width: Int, height: Int, k: Int): IntArray

    @JvmOverloads
    fun predictImage(imgPath: String, k: Int = 4): IntArray {
        return predictImage(stringToBytes(imgPath), 0, 0, k)
    }

    external fun extractFeatures(data: ByteArray, width: Int, height: Int, blobNames: String): Array<FloatArray>

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
