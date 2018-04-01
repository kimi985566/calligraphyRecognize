#工程目录
set(pathToProject D:\\Android\\workplace\\calligraphyRecognize)
#OpenCV目录
set(pathToOpenCv D:\\Android\\OpenCV-android-sdk)

#CMake版本信息
cmake_minimum_required(VERSION 3.4.1)
#支持-std=gnu++11
set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

#配置加载native依赖
include_directories(${pathToOpenCv}/sdk/native/jni/include)

#CPP文件夹下带编译的cpp文件
add_library( native-lib SHARED src/main/cpp/native-lib.cpp )

#动态方式加载
add_library( lib_opencv SHARED IMPORTED )

#引入libopencv_java3.so文件
set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${pathToProject}/app/src/main/jniLibs/${ANDROID_ABI}/libopencv_java3.so)

#引入的Caffe动态库
add_library(caffe_jni
            SHARED
            IMPORTED)

set_target_properties(
            caffe_jni
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_CURRENT_LIST_DIR}/src/main/jniLibs/${ANDROID_ABI}/libcaffe_jni.so)

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

target_link_libraries( native-lib
                       caffe_jni
                       lib_opencv
                       ${log-lib})