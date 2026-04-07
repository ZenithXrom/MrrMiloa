# CalorieTracker (Fase 1 + base multi-fuente de nutrición)

App Android tipo MyFitnessPal construida con Kotlin + Jetpack Compose + Room + Firebase.

## Qué incluye

- Login con Google (Firebase Auth)
- Registro manual de alimentos
- Cálculo de calorías por día
- Base de datos local con Room
- Dashboard principal + historial diario
- Base offline-first con bandera `isSynced`
- **Nueva capa de servicios nutricionales multi-fuente:**
  - Open Food Facts (barcode)
  - USDA FoodData Central (búsqueda genérica)
  - Edamam (texto natural)

## Configuración técnica

- `compileSdk = 34`
- `minSdk = 24`
- `targetSdk = 34`
- Kotlin + Compose + MVVM + Repository
- Gradle Kotlin DSL (sin Version Catalog)

## 1) Crear proyecto y pegar archivos

1. Crea un proyecto **Empty Compose Activity** en Android Studio.
2. Usa package `com.example.calorietracker`.
3. Reemplaza los archivos por este contenido.
4. Abre el proyecto y sincroniza Gradle.

## 2) Firebase (Google Login)

1. Crea proyecto en Firebase.
2. Agrega app Android con `applicationId = com.example.calorietracker`.
3. Descarga `google-services.json` en `app/google-services.json`.
4. Habilita Google Sign-In en Authentication.
5. Reemplaza `default_web_client_id` en `app/src/main/res/values/strings.xml`.

## 3) API Keys nutrición

Agrega en `~/.gradle/gradle.properties` o en `gradle.properties` local:

```properties
USDA_API_KEY=tu_api_key_usda
EDAMAM_APP_ID=tu_app_id_edamam
EDAMAM_APP_KEY=tu_app_key_edamam
```

> Open Food Facts no requiere key para el endpoint usado.

## 4) Cómo funciona la normalización de datos

Se usa un modelo unificado `FoodItem` con campos:

- `name`
- `calories`
- `protein`
- `carbs`
- `fat`
- `source` y `externalId`

Cada API se transforma a `FoodItem` desde `NutritionService`, para que la UI y persistencia no dependan de DTOs externos.

## 5) Conexión con persistencia local (Room)

1. El usuario busca alimento por modo (manual / barcode / USDA / texto).
2. `FoodViewModel` consulta `NutritionService`.
3. El resultado vuelve como `FoodItem` normalizado.
4. Al pulsar **Usar**, `FoodRepository.addFood(foodItem, date)` convierte `FoodItem` a `FoodEntity`.
5. `FoodDao.insertFood(...)` persiste localmente.
6. `isSynced = false` deja listo el registro para sincronización posterior.

## 6) Run / APK

```bash
./gradlew assembleDebug
```

APK:

`app/build/outputs/apk/debug/app-debug.apk`
