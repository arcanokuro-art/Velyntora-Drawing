# Velyntora Drawing — Windows x64 (piloto)

Rama exclusiva `windows`. Android permanece en `main`.

Extrae **todo** Velyntora-Drawing-Windows-x64.zip y abre `V-Drawing/V-Drawing.exe`. Incluye Java 17, la DLL del motor y sus recursos. No requiere Android, emuladores, Java preinstalado ni conexión para dibujar.

## Arquitectura

- `src/.../app`: inicio y composición del editor.
- `src/.../canvas`: ratón, zoom, rotación, selección y dibujo.
- `src/.../layers`: capas, opacidad y mezcla.
- `src/.../document`: apertura, guardado atómico VLYCORE, exportación y recientes.
- `src/.../tools`: catálogo de herramientas.
- `native`: adaptación JNI y archivos temporales Windows.
- `scripts`: generación de las declaraciones JNI y recursos a partir de Core, compilación y empaquetado.
- `tests`: pruebas sobre el motor real y ventanas de escritorio.

El motor se compila directamente desde `engine/`: no se reemplazan los algoritmos por otro motor. Se generan 125 declaraciones JNI a partir de Android y se incluyen los 34 presets de punta además del pincel clásico. Se conserva la atribución y procedencia de los recursos de Core.

## Funciones del piloto

Inicio con imagen, logo transparente, aviso piloto y Powered By Zaskar Startup; nuevo lienzo hasta 6000 × 6000; PNG/JPEG/BMP/GIF/TIFF según los códecs de ImageIO; guardado/reapertura VLYCORE con capas; pinceles, lápiz, goma, cubo, selector, clonar, recoloración, figuras, texto rasterizado, selecciones, varita, degradado, capas y mezclas; ajustes de color; deshacer/rehacer; zoom hasta 7000% con píxeles definidos; rotación y desplazamiento.

Rueda: zoom. Alt+rueda: rotar. Botón central: desplazar. Ctrl+S/O/N/Z/Y/C/V. Alt+clic: origen de clonado. Línea/Curva permite añadir y arrastrar puntos, Enter confirma y Ctrl+Z recupera el borrador tras confirmar.

## Diferencias pendientes respecto a Android

Este piloto todavía no tiene paridad completa. Remove AI, importación de formatos especiales como ORA/SVG/WebP, edición no destructiva de texto/imágenes y presión de tableta no están expuestos en la interfaz Windows. La curva de tres puntos es cuadrática; más puntos se unen por segmentos. Las exportaciones fuera de VLYCORE aplanan las capas.

La versión no está firmada con un certificado comercial.

## Compilar

Windows x64, JDK 17, Python 3.12 y GCC/CMake/Ninja de MSYS2 MINGW64 en PATH. Ejecuta `windows/scripts/build.ps1` desde la raíz. GitHub Actions compila y prueba en un servidor Windows real, abre el ejecutable empaquetado y publica el ZIP sólo si las comprobaciones pasan.
