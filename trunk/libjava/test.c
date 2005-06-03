#include "jni-sablevm.h"
#include <stdio.h>

extern "C"
void Java_test_call(JNIEnv* env, jobject obj) {
   printf("Pointers: %p, %p\n", env, obj);
}