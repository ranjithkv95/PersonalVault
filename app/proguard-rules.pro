# Default ProGuard rules for release builds.
# Keep SQLCipher native bindings
-keep class net.zetetic.database.** { *; }
-keep class androidx.sqlite.** { *; }
