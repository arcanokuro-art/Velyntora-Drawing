# Velyntora Drawing — versión piloto

Aplicación Android independiente. Inicio basado en Velyntora y adaptado al boceto Drawing.

## Módulos
- `ui/start/StartScreen`: composición adaptable del inicio.
- `ui/start/DrawingHero`: ilustración, acciones y crédito.
- `ui/start/RecentProjectsPanel`: estado inicial de proyectos recientes.
- `ui/start/StartActions`: contrato de navegación.
- `ui/common/Ui`: estilos y controles compartidos.
- `MainActivity`: ciclo de vida y acciones Android.

El repositorio de origen Velyntora contiene una base de interfaz Android, no el motor estable de Velyntora-Core. Este piloto no integra todavía un editor ni persistencia de proyectos. Los controles pendientes lo indican explícitamente. El selector de imágenes usa el selector del sistema, sin permisos generales de almacenamiento.

## Compilación
JDK 17, Gradle 8.9 y Android SDK 35: `gradle :app:assembleDebug`.
GitHub Actions compila y adjunta `Velyntora-Drawing-pilot.apk` en cada push a main.
