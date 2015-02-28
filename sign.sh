pushd android/build/outputs/apk

# sign
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore C:/Users/fdorothy/keys/fdorothy.keystore android-release-unsigned.apk fdorothy

# zipalign
/cygdrive/c/Program\ Files/Android/android-sdk/build-tools/21.1.2/zipalign.exe -v 4 android-release-unsigned.apk tafl.apk

popd
