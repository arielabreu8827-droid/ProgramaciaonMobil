# Mi Boleta · Aula Mobile

Proyecto Final — Programación de Dispositivos Móviles (PCO-113)
Instituto Técnico Superior Padre Fantino (ITESPF)

Cliente Android nativo (Kotlin + Jetpack Compose + Material 3) que consume la API
REST real de Aula Mobile: login con Bearer token, perfil, boleta de calificaciones
(tareas/proyectos + exámenes + notas adicionales con promedio), vista de asistencia
y logout real contra el servidor.

## 1. Requisitos

- Android Studio (Koala/2024.1 o más reciente recomendado).
- JDK 17 (el que trae Android Studio es suficiente).
- Un dispositivo/emulador con Android 8.0 (API 26) o superior.
- La **base URL** real de la API de Aula Mobile (la entrega el profesor en clase).

## 2. Cómo abrir el proyecto

1. Abre Android Studio → **Open** → selecciona la carpeta `AulaMobile` (la que
   contiene `settings.gradle.kts`).
2. Espera a que Android Studio sincronice Gradle. Si te pide un `local.properties`,
   sigue el paso 3 primero.

## 3. Configurar la Base URL de la API (¡obligatorio!)

En la raíz del proyecto (junto a `settings.gradle.kts`) crea un archivo llamado
`local.properties` (si Android Studio no lo generó ya con la ruta del SDK) y agrega
la línea:

```properties
sdk.dir=/ruta/a/tu/Android/sdk
API_BASE_URL=https://LA-URL-QUE-TE-DIO-EL-PROFESOR.com/
```

- `sdk.dir` normalmente ya lo genera Android Studio automáticamente al abrir el
  proyecto por primera vez.
- `API_BASE_URL` es la única línea que **tú** debes agregar manualmente. Debe
  terminar en `/`. No hace falta escribir `/api/v1/...`, la app ya arma las rutas
  completas (`api/v1/auth/login`, `api/v1/grades`, etc.).
- Si olvidas configurarla, la app compila igual pero apuntará a un placeholder
  (`https://CAMBIA-ESTA-URL.aulamobile.edu/`) y el login fallará por "sin conexión".

Un ejemplo de referencia está en `local.properties.example`.

## 4. Compilar y correr

1. Con `local.properties` configurado, sincroniza Gradle (`File → Sync Project
   with Gradle Files`, o el ícono del elefante).
2. Selecciona un emulador (por ejemplo Pixel 6, API 33) o conecta un dispositivo
   físico con depuración USB activada.
3. Presiona **Run ▶** (Shift+F10).
4. Inicia sesión con tu propia matrícula y contraseña (las mismas que usas en la
   plataforma web de Aula Mobile).

## 5. Estructura del proyecto (MVVM)

```
app/src/main/java/com/itespf/aulamobile/
├── data/
│   ├── model/        → data classes que reflejan el JSON de la API
│   ├── remote/        → ApiService (Retrofit), AuthInterceptor (header Bearer), RetrofitClient
│   ├── local/          → TokenManager (EncryptedSharedPreferences, token seguro)
│   ├── repository/    → AuthRepository, GradesRepository (login/logout/grades + manejo de errores)
│   └── util/            → ServiceLocator (inyección de dependencias manual)
├── ui/
│   ├── login/          → LoginScreen + LoginViewModel
│   ├── home/           → HomeScreen (Scaffold + bottom navigation + logout)
│   ├── profile/       → ProfileScreen
│   ├── grades/         → GradesScreen + GradesViewModel + lógica del resumen/promedio
│   ├── attendance/   → AttendanceScreen
│   ├── navigation/   → NavGraph (reacciona al estado de sesión)
│   ├── common/         → UiState (Loading/Success/Error), ViewModelFactory
│   └── theme/           → Color.kt, Type.kt, Theme.kt (Material 3)
├── MainActivity.kt
└── AulaMobileApp.kt   → Application, inicializa el ServiceLocator
```

## 6. Decisiones técnicas relevantes

- **Autenticación**: `AuthInterceptor` (OkHttp) agrega automáticamente
  `Authorization: Bearer <token>` a toda petición saliente que lo necesite.
- **Almacenamiento seguro**: el token y los datos básicos del usuario se guardan
  con `EncryptedSharedPreferences` (`androidx.security.crypto`), nunca en texto
  plano.
- **Manejo de errores**: cada pantalla usa la sealed class `UiState`
  (`Idle / Loading / Success / Error`). Un error `401` en `GET /api/v1/grades`
  limpia la sesión local automáticamente y el `NavGraph` regresa al login.
- **Resumen de la boleta**: `GradesSummary.kt` calcula un promedio ponderado por
  `weight` cuando los assignments lo traen; si no, usa el promedio simple de las
  notas entregadas. El promedio general combina tareas, exámenes y notas
  adicionales.
- **Arquitectura**: separación estricta red / datos / UI con patrón MVVM
  (Repository + ViewModel + StateFlow), como se vio en la Unidad 3.

## 7. Demo

Grabar el flujo completo: **login → perfil → calificaciones → asistencia → logout**,
verificando en el logout que el botón realmente llama al endpoint
`POST /api/v1/auth/logout` (puedes verlo en Logcat gracias al
`HttpLoggingInterceptor`, activo solo en builds `debug`).
