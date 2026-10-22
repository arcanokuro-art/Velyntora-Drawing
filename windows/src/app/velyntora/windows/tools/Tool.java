package app.velyntora.windows.tools;
public enum Tool {
 BRUSH("Pincel"),PENCIL("Lápiz"),ERASER("Goma"),FILL("Cubo"),PICKER("Selector de color"),PAN("Desplazamiento"),ZOOM("Zoom"),LINE("Línea/Curva"),RECT("Rectángulo"),ELLIPSE("Elipse"),TRIANGLE("Triángulo"),ROUNDRECT("Rectángulo redondeado"),SELECT("Selección rectangular"),ELLIPSE_SELECT("Selección elíptica"),LASSO("Lazo"),WAND("Varita"),GRADIENT("Degradado"),TEXT("Texto"),CLONE("Clonar"),RECOLOR("Recoloración"),MOVE("Mover selección");
 public final String label;Tool(String label){this.label=label;}public String toString(){return label;}
}
