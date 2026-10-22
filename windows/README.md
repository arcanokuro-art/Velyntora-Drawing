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

## Módulos completos de escritorio

- Remove AI: mismo backend MI-GAN de Core, modelo verificado SHA-256 incluido; máscara pintada, selección, ejecución sin conexión y transacción que rechaza resultados de otra revisión.
- Formatos: lectores originales de BMP, TGA, PPM/PGM/PBM, PCX, XBM/XPM, ICO/CUR y contenedores ANI/ICNS/QuickTime; ORA con capas; SVG mediante Batik; WebP mediante ImageIO. PNG/JPEG/GIF/TIFF y VLYCORE continúan disponibles. Exportaciones PNG/JPEG/BMP/TGA/PPM/ICO/GIF/TIFF/WebP y ORA.
- Imágenes editables: fuente PNG retenida, posición, escala, giro y recorte; los datos originales se conservan en VLYCORE.
- Texto editable: contenido UTF-8, fuente, tamaño, color, estilo, ajuste de línea y transformación; editable tras reabrir el proyecto.
- Todas las familias de efectos de Core y ajustes RGB, niveles y curvas; selección aplicada antes de la operación.
- Windows Ink: captura nativa de presión y promoción a eventos de ratón AWT. La respuesta de cada tableta y controlador requiere prueba física; sin muestras de lápiz se conserva la presión normal del ratón.
- Línea/Curva: reutiliza CurveDraft original, puntos sobre la curva, tensión editable, múltiples líneas antes de Enter y recuperación del borrador con Deshacer.

La versión es piloto y no tiene firma comercial. Las pruebas automáticas comprueban el motor y el paquete en Windows; no sustituyen la prueba física de una tableta ni garantizan rendimiento en todo equipo.

## Compilar

Windows x64, JDK 17, Python 3.12 y GCC/CMake/Ninja de MSYS2 MINGW64 en PATH. Ejecuta `windows/scripts/build.ps1` desde la raíz. GitHub Actions compila y prueba en un servidor Windows real, abre el ejecutable empaquetado y publica el ZIP sólo si las comprobaciones pasan.
