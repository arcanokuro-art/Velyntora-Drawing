# Velyntora Drawing — Linux x64

Esta rama `linux` adapta la aplicación de escritorio completa, con el mismo motor C++ y los módulos Java de dibujo, a Linux. `main` conserva Android y `windows` conserva Windows.

- **Portable:** extraer `Velyntora-Drawing-Linux-x64.tar.gz` y ejecutar `V-Drawing/V-Drawing.sh`.
- **Debian/Ubuntu:** instalar `velyntora-drawing_0.4.0_amd64.deb`.
- Incluye Java 17, MI-GAN sin conexión, herramientas, pinceles, capas, formatos y recursos.
- Linux x86-64, glibc 2.35+, X11 o XWayland. Compilación y comprobaciones en Ubuntu 22.04/24.04.
- [Instrucciones, arquitectura y pruebas Linux](linux/README.md).
- Compilar: `bash linux/scripts/build.sh`.

## Base Android conservada en el repositorio

# Velyntora Drawing — piloto integrado

Nombre completo: Velyntora Drawing. Nombre bajo el icono: V-Drawing.

- `app/`: inicio, imagen, logo transparente, aviso piloto, crédito Powered By Zaskar Startup y navegación al editor.
- `engine/`: instantánea completa de Velyntora-Core; motor C++, herramientas, pinceles, formatos, persistencia, IA Remove, recursos y pruebas.
- `:drawing-engine`: biblioteca Android en `engine/app`; mantiene el namespace `art.velyntora.core` y las firmas JNI.
- `docs/core-source.json`: revisión de origen y SHA-256 de todos sus archivos.

Crear ilustración abre el formulario de dimensiones original del editor. Abrir imagen pasa su URI al importador original. Atrás usa la protección de cambios de Core y vuelve al inicio. Se mantiene pantalla completa en ambos entornos.

Los proyectos recientes y las preferencias del inicio siguen pendientes; los controles informan de ello. Las opciones del editor Core permanecen disponibles.

Compilar con JDK 17, Gradle 8.12, SDK 35 y CMake 3.22.1: `gradle :app:assembleDebug :app:lintDebug`.
El modelo MI-GAN se descarga durante la compilación y se verifica con SHA-256; la aplicación lo incluye para uso sin conexión.

Pruebas nativas: `cmake -S engine/tests -B build/core-tests && cmake --build build/core-tests --parallel 2 && ctest --test-dir build/core-tests --output-on-failure`.
Validación modular: `python engine/scripts/check_modular_boundaries.py`.
