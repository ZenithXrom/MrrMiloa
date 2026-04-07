# CalorieTracker (Fase 1 MVP)

App Android tipo MyFitnessPal construida con Kotlin + Jetpack Compose + Room + Firebase Auth.

## Qué incluye en esta entrega

- Login con Google (Firebase Auth)
- Registro manual de alimentos
- Cálculo de calorías por día
- Base de datos local con Room
- Dashboard principal
- Historial diario
- Base inicial offline-first con bandera `isSynced` y repositorio de sync

## Requisitos

- Android Studio estable (Hedgehog o superior)
- JDK 17
- Android SDK 34
- Cuenta Firebase

## 1) Crear proyecto vacío y pegar archivos

1. Crea un proyecto **Empty Compose Activity** en Android Studio.
2. Usa package: `com.example.calorietracker`.
3. Cierra Android Studio.
4. Reemplaza el contenido completo por estos archivos.
5. Abre nuevamente el proyecto.

## 2) Configurar Firebase + Google Login

1. En [Firebase Console](https://console.firebase.google.com), crea un proyecto.
2. Agrega app Android con `applicationId = com.example.calorietracker`.
3. Descarga `google-services.json` y colócalo en:
   - `app/google-services.json`
4. En Firebase Console habilita:
   - Authentication > Sign-in method > Google
5. En Google Cloud/Firebase, toma el **Web client ID** y reemplaza:
   - `app/src/main/res/values/strings.xml`
   - valor de `default_web_client_id`

## 3) Sincronizar Gradle

1. Abre Android Studio.
2. Presiona **Sync Project with Gradle Files**.
3. Espera a que finalice sin errores.

## 4) Ejecutar

1. Conecta dispositivo o abre emulador.
2. Presiona **Run**.

## 5) Compilar APK debug

Desde terminal:

```bash
./gradlew assembleDebug
```

APK resultante:

`app/build/outputs/apk/debug/app-debug.apk`

## Próximas fases

- Fase 2: macros, gráficas, comidas frecuentes, copiar días
- Fase 3: sync real en segundo plano con cola de cambios
- Fase 4: Open Food Facts + USDA
- Fase 5: progreso físico y reportes
- Fase 6: premium gratis

