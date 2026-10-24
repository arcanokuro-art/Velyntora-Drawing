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

## Funciones de escritorio

Inicio con imagen, logo transparente, aviso piloto y Powered By Zaskar Startup; nuevo lienzo hasta 6000 × 6000; PNG/JPEG/BMP/GIF/TIFF según los códecs de ImageIO; guardado/reapertura VLYCORE con capas; pinceles, lápiz, goma, cubo, selector, clonar, recoloración, figuras, texto editable, selecciones, varita, degradado, capas y mezclas; ajustes de color; deshacer/rehacer; zoom hasta 7000% con píxeles definidos; rotación y desplazamiento.

Rueda: zoom. Alt+rueda: rotar. Botón central: desplazar. Ctrl+S/O/N/Z/Y/C/V. Primer clic en Clonar: origen; Alt+clic: cambiar origen. Línea/Curva permite añadir y arrastrar puntos, Enter confirma y Ctrl+Z recupera el borrador tras confirmar.

## Módulos completos de escritorio

- Remove AI: mismo backend MI-GAN de Core, modelo verificado SHA-256 incluido; máscara pintada, selección, ejecución sin conexión y transacción que rechaza resultados de otra revisión.
- Formatos: lectores originales de BMP, TGA, PPM/PGM/PBM, PCX, XBM/XPM, ICO/CUR y contenedores ANI/ICNS/QuickTime; ORA con capas; SVG mediante Batik; WebP mediante ImageIO. PNG/JPEG/GIF/TIFF y VLYCORE continúan disponibles. Exportaciones PNG/JPEG/BMP/TGA/PPM/ICO/GIF/TIFF/WebP y ORA.
- Imágenes editables: fuente PNG retenida, posición, escala, giro y recorte; los datos originales se conservan en VLYCORE.
- Texto editable: contenido UTF-8, fuente, tamaño, color, estilo, ajuste de línea y transformación; editable tras reabrir el proyecto.
- Todas las familias de efectos de Core y ajustes RGB, niveles y curvas; selección aplicada antes de la operación.
- Windows Ink: captura nativa de presión y promoción a eventos de ratón AWT. La respuesta de cada tableta y controlador requiere prueba física; sin muestras de lápiz se conserva la presión normal del ratón.
- Recuperación: copia revisionada del documento tras tres segundos de inactividad, recuperación desde el inicio y descarte coordinado con escrituras en curso.
- Selecciones y capas: copiar/cortar/duplicar, invertir, expandir, contraer, mover, transformar, recortar, combinar y propiedades.
- Pinceles: clásico y bordes suavizados, 34 presets con vista previa, aerógrafo sostenido y parámetros independientes por herramienta; paleta guardada.
- Degradados: cinco modos y color primario/secundario o transparencia.
- Línea/Curva: reutiliza CurveDraft original, puntos sobre la curva, tensión editable, múltiples líneas antes de Enter y recuperación del borrador con Deshacer.

La versión es piloto y no tiene firma comercial. Las pruebas automáticas comprueban el motor y el paquete en Windows; no sustituyen la prueba física de una tableta ni garantizan rendimiento en todo equipo.

## Compilar

Windows x64, JDK 17, Python 3.12 y GCC/CMake/Ninja de MSYS2 MINGW64 en PATH. Ejecuta `windows/scripts/build.ps1` desde la raíz. GitHub Actions compila y prueba en un servidor Windows real, abre el ejecutable empaquetado y publica el ZIP sólo si las comprobaciones pasan.

## Correspondencia de controles con Android (0.4.0)

El editor incluye los iconos vectoriales originales y el mismo orden en dos columnas; figuras agrupadas con y sin relleno; pinceles por categorías; punta cuadrada y presión activable; selector HSV/RGBA con hexadecimal y rueda/cuadrado; paleta predeterminada y colores guardados con principal/secundario/eliminar. Clic derecho equivale al menú de pulsación prolongada en la paleta.

Las capas muestran miniaturas reales con transparencia. La barra inferior informa coordenadas, dimensiones, zoom y giro; el título identifica el documento, cambios pendientes y bytes de píxeles asignados en las capas (no es una medida del consumo total del proceso).

Tamaño de imagen incluye mantener proporciones, porcentaje y remuestreo; tamaño del lienzo incluye nueve anclajes. Las operaciones de archivos, capas, selección, efectos e historial usan una cola de trabajo con progreso. Mover selección desplaza el contorno; mover píxeles desplaza contenido. Línea/Curva tiene historial de borrador y recuperación editable después de confirmar y deshacer. Guardar como crea otro destino; la exportación solo reemplaza el archivo al terminar correctamente.

`ParitySmoke` verifica estos comportamientos junto con las suites originales de Core. El alcance y sus límites se documentan en `PORTING.md`; la prueba de presión con hardware físico permanece separada de las comprobaciones de código y distribución.

## Adaptación visual al diseño Android

Primera etapa: paleta oscura propia independiente del tema del sistema; menú ☰ que conserva los menús y operaciones existentes; accesos a nuevo, abrir, guardar, deshacer, rehacer y capas; ancho con deslizador; herramientas con desplazamiento únicamente vertical; panel de capas más ancho y paleta compacta. Los atajos de teclado se conservan.

La compilación Java y los límites modulares se verificaron en el entorno de desarrollo. Esta etapa todavía requiere revisión visual en Windows con distintas escalas de pantalla y resoluciones; no constituye una certificación de equivalencia visual completa con Android ni un nuevo portable verificado.
