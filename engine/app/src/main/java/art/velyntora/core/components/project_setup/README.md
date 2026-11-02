# Configuración de ilustraciones

Implementación Android de `Velyntora-Para-Velyntora/drawing_project_setup/README.md` (referencia `75a4b4873c0a229e06f181a51b27822804e24e30`). Se usa desde Inicio → Crear ilustración y Archivo → Nuevo, mediante `configureDimensions(0)`. No modifica el repositorio de referencia.

- `DrawingPresets`: catálogo puro de los ocho tamaños de dibujo y cálculo proporcional dentro de CanvasLimits.
- `DrawingProjectSetup`: formulario con ancho/alto en píxeles, vínculo opcional de proporción, presets y creación validada. Cambiar un tamaño manual vuelve a Personalizado. Elegir un preset actualiza la proporción vinculada.
- YouTube: 1920×1080 y 1280×720; TikTok: 1080×1920 y 720×1280; Pixiv: 1700×2400.
- Doujinshi: 2500×3600; Manga: 3072×4096; Manhwa: 2400×3840. Estos tamaños actualizados por el usuario se utilizan exactamente, sin reducción.

Exclusivo de dibujo, sin FPS ni presets de animación. Cancelar no crea un documento ni modifica el actual. La protección de cambios sin guardar sigue en DocumentSession. Redimensionar una imagen o un lienzo existentes sigue en document/dimensions; herramientas y JNI permanecen intactos.
