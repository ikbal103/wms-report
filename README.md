# WebView App - 192.168.168.40

Aplikasi Android WebView yang membuka alamat `http://192.168.168.40`

## Fitur
- ✅ Membuka URL lokal `http://192.168.168.40` secara otomatis
- ✅ JavaScript diaktifkan
- ✅ Swipe-to-refresh untuk reload halaman
- ✅ Progress bar saat loading
- ✅ Halaman error + tombol "Coba Lagi" jika koneksi gagal
- ✅ Tombol back untuk navigasi di dalam WebView
- ✅ Dialog konfirmasi saat keluar aplikasi
- ✅ Izin HTTP cleartext (mendukung IP lokal non-HTTPS)

## Cara Build

### Prasyarat
- Android Studio Hedgehog (2023.1.1) atau lebih baru
- JDK 17
- Android SDK 34

### Langkah
1. Buka Android Studio
2. Pilih **Open** → arahkan ke folder `WebViewApp`
3. Tunggu Gradle sync selesai
4. Klik **Run ▶** atau tekan `Shift+F10`

### Build APK (tanpa USB)
1. Menu **Build → Build Bundle(s)/APK(s) → Build APK(s)**
2. APK tersimpan di: `app/build/outputs/apk/debug/app-debug.apk`

## Catatan Jaringan
- Pastikan HP dan server **192.168.168.40** berada di **jaringan WiFi yang sama**
- Aplikasi menggunakan HTTP (bukan HTTPS) — sudah dikonfigurasi di `network_security_config.xml`
- Jika server pakai port tertentu (misal :8080), ubah di `MainActivity.java`:
  ```java
  private static final String TARGET_URL = "http://192.168.168.40:8080";
  ```

## Struktur Project
```
WebViewApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/webviewapp/
│   │   │   └── MainActivity.java
│   │   ├── res/
│   │   │   ├── layout/activity_main.xml
│   │   │   ├── values/colors.xml
│   │   │   ├── values/strings.xml
│   │   │   ├── values/themes.xml
│   │   │   └── xml/network_security_config.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── gradle.properties
```
