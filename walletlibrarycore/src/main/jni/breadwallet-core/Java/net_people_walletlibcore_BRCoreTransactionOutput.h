/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class net_people_walletlibcore_BRCoreTransactionOutput */

#ifndef _Included_net_people_walletlibcore_BRCoreTransactionOutput
#define _Included_net_people_walletlibcore_BRCoreTransactionOutput
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     net_people_walletlibcore_BRCoreTransactionOutput
 * Method:    createTransactionOutput
 * Signature: (J[B)J
 */
JNIEXPORT jlong JNICALL Java_net_people_walletlibcore_BRCoreTransactionOutput_createTransactionOutput
  (JNIEnv *, jclass, jlong, jbyteArray);

/*
 * Class:     net_people_walletlibcore_BRCoreTransactionOutput
 * Method:    getAddress
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_net_people_walletlibcore_BRCoreTransactionOutput_getAddress
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCoreTransactionOutput
 * Method:    setAddress
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_net_people_walletlibcore_BRCoreTransactionOutput_setAddress
  (JNIEnv *, jobject, jstring);

/*
 * Class:     net_people_walletlibcore_BRCoreTransactionOutput
 * Method:    getAmount
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_net_people_walletlibcore_BRCoreTransactionOutput_getAmount
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCoreTransactionOutput
 * Method:    setAmount
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_net_people_walletlibcore_BRCoreTransactionOutput_setAmount
  (JNIEnv *, jobject, jlong);

/*
 * Class:     net_people_walletlibcore_BRCoreTransactionOutput
 * Method:    getScript
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_net_people_walletlibcore_BRCoreTransactionOutput_getScript
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
