# PixelFlow ProGuard rules
-keep class androidx.room.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase { *; }
-keep class com.pixelflow.app.data.entity.** { *; }
-dontwarn kotlinx.coroutines.**
