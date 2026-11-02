# document/dimensions

Responsabilidad propia de document/dimensions.

Fuentes: `DocumentDimensions.java`, `DocumentDimensionsDialog.java`.

Los paquetes Java existentes se conservan por compatibilidad con Android, JNI y las herramientas. La carpeta expresa la propiedad del componente. Los componentes de interfaz reciben la fachada de composición; consultan y actualizan su estado mediante métodos, sin acceso directo a campos. Los codecs y modelos puros no dependen de la Activity.

Validación: comprobación de límites modular, compilación Android y suite de integración del emulador. Los modelos y codecs también se verifican con las pruebas JVM existentes. No cambiar `tools/` para trabajar en este módulo.

## Presupuesto unificado

CanvasLimits.java y core/include/velyntora/CanvasLimits.hpp fijan 6000 px por lado y 36 millones de píxeles por documento. Creación, redimensionado, decodificación, proyectos e imágenes exportadas comparten el presupuesto. La pantalla se actualiza en franjas de hasta 262144 píxeles para reducir memoria temporal.

Crear ilustración (`configureDimensions(0)`) se delega a `components/project_setup`, que contiene los presets de dibujo y el vínculo de proporción. Aquí permanecen Tamaño de imagen y Tamaño de lienzo para documentos existentes.
