/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class net_people_walletlibcore_BRCoreMerkleBlock */

#ifndef _Included_net_people_walletlibcore_BRCoreMerkleBlock
#define _Included_net_people_walletlibcore_BRCoreMerkleBlock
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     net_people_walletlibcore_BRCoreMerkleBlock
 * Method:    createJniCoreMerkleBlock
 * Signature: ([BI)J
 */
JNIEXPORT jlong JNICALL Java_net_people_walletlibcore_BRCoreMerkleBlock_createJniCoreMerkleBlock
  (JNIEnv *, jclass, jbyteArray, jint);

/*
 * Class:     net_people_walletlibcore_BRCoreMerkleBlock
 * Method:    createJniCoreMerkleBlockEmpty
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_net_people_walletlibcore_BRCoreMerkleBlock_createJniCoreMerkleBlockEmpty
  (JNIEnv *, jclass);

/*
 * Class:     net_people_walletlibcore_BRCoreMerkleBlock
 * Method:    getBlockHash
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_net_people_walletlibcore_BRCoreMerkleBlock_getBlockHash
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCoreMerkleBlock
 * Method:    getVersion
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_net_people_walletlibcore_BRCoreMerkleBlock_getVersion
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCoreMerkleBlock
 * Method:    getPrevBlockHash
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_net_people_walletlibcore_BRCoreMerkleBlock_getPrevBlockHash
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCoreMerkleBlock
 * Method:    getRootBlockHash
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_net_people_walletlibcore_BRCoreMerkleBlock_getRootBlockHash
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCoreMerkleBlock
 * Method:    getTimestamp
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_net_people_walletlibcore_BRCoreMerkleBlock_getTimestamp
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCoreMerkleBlock
 * Method:    getTarget
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_net_people_walletlibcore_BRCoreMerkleBlock_getTarget
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCoreMerkleBlock
 * Method:    getNonce
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_net_people_walletlibcore_BRCoreMerkleBlock_getNonce
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCoreMerkleBlock
 * Method:    getTransactionCount
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_net_people_walletlibcore_BRCoreMerkleBlock_getTransactionCount
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCoreMerkleBlock
 * Method:    getHeight
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_net_people_walletlibcore_BRCoreMerkleBlock_getHeight
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCoreMerkleBlock
 * Method:    serialize
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_net_people_walletlibcore_BRCoreMerkleBlock_serialize
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCoreMerkleBlock
 * Method:    isValid
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_net_people_walletlibcore_BRCoreMerkleBlock_isValid
  (JNIEnv *, jobject, jlong);

/*
 * Class:     net_people_walletlibcore_BRCoreMerkleBlock
 * Method:    containsTransactionHash
 * Signature: ([B)Z
 */
JNIEXPORT jboolean JNICALL Java_net_people_walletlibcore_BRCoreMerkleBlock_containsTransactionHash
  (JNIEnv *, jobject, jbyteArray);

/*
 * Class:     net_people_walletlibcore_BRCoreMerkleBlock
 * Method:    disposeNative
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_people_walletlibcore_BRCoreMerkleBlock_disposeNative
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
