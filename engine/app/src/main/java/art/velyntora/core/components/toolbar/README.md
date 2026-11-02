# components/toolbar

Barra superior y presentación de los controles de herramientas.

Fuentes: `CommandBar.java`, `ToolControls.java`.

Los paquetes Java existentes se conservan por compatibilidad con Android, JNI y las herramientas. La carpeta expresa la propiedad del componente. Los componentes de interfaz reciben la fachada de composición; consultan y actualizan su estado mediante métodos, sin acceso directo a campos. Los codecs y modelos puros no dependen de la Activity.

Validación: comprobación de límites modular, compilación Android y suite de integración del emulador. Los modelos y codecs también se verifican con las pruebas JVM existentes. No cambiar `tools/` para trabajar en este módulo.

## Acceso a Pincel y borrador

BrushSettingsPolicy define únicamente ocho exclusiones: mover píxeles, mover contorno, zoom, cuentagotas, desplazamiento, selección rectangular, selección elíptica y selección libre de contorno. ToolControls retira el botón Configurar pincel de la jerarquía de la barra y lo vuelve a crear al elegir otra herramienta; no deja controles invisibles ni espacio reservado. El menú Herramientas tampoco incluye esa entrada para las ocho exclusiones y el diálogo verifica la misma regla al abrirse. Cubeta, pincel, borrador, Línea/Curva y todas las demás herramientas conservan su acceso. No se cambian parámetros, algoritmos ni archivos de tools/.
