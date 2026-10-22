from pathlib import Path
import re,json,shutil
from PIL import Image
root=Path(__file__).resolve().parents[2];build=root/'windows/build';generated=build/'generated/art/velyntora/core';generated.mkdir(parents=True,exist_ok=True)
source=(root/'engine/app/src/main/java/art/velyntora/core/DrawingView.java').read_text()
methods=re.findall(r'  static native [^;]+;',source)
assert len(methods)>100
(generated/'DrawingView.java').write_text('package art.velyntora.core;\npublic final class DrawingView {\n static {System.loadLibrary("velyntora_desktop");}\n'+ '\n'.join('public '+m.strip() for m in methods)+'\n public static native boolean nativeProjectPath(String path,boolean save);\n}\n')
resources=build/'resources';resources.mkdir(parents=True,exist_ok=True)
image=root/'app/src/main/res/drawable-nodpi'
Image.open(image/'start_drawing.webp').save(resources/'start_drawing.png')
logo=Image.open(image/'velyntora_logo.webp').convert('RGBA');logo.putdata([(r,g,b,0 if max(r,g,b)<28 else a) for r,g,b,a in logo.getdata()]);logo.save(resources/'logo.png')
brushroot=root/'engine/tools/brush/velyntora_brush';catalog=json.loads((brushroot/'catalog.json').read_text());rows=[]
for preset in catalog:
 folder=brushroot/preset['folder'];data=folder/'preset.json'
 if not data.exists():continue
 props=json.loads(data.read_text());dest=resources/'brushes'/str(preset['id']);dest.mkdir(parents=True,exist_ok=True)
 for name in ['tip.bin','texture.bin']:shutil.copyfile(folder/name,dest/name)
 def arr(xs):return 'new float[]{'+','.join(str(float(x))+'f' for x in xs)+'}'
 curves=[arr([v for pair in points for v in pair]) for points in props['curves']]
 rows.append('new Preset('+str(preset['id'])+','+json.dumps(preset['category']+' · '+preset['name'],ensure_ascii=False)+','+arr(props['params'])+','+','.join(curves)+')')
(generated/'BrushPresets.java').write_text('''package art.velyntora.core;
public final class BrushPresets {
 public record Preset(int id,String label,float[] params,float[] size,float[] opacity,float[] flow){public String toString(){return label.replaceFirst("^[a-z]\\\\)_", "").replace('_',' ');}}
 public static final Preset[] ALL={'''+',\n'.join(rows)+'''};
 public static boolean begin(Preset preset){try{return DrawingView.nativeConfigureTipBrush(BrushPresets.class.getResourceAsStream("/brushes/"+preset.id()+"/tip.bin").readAllBytes(),BrushPresets.class.getResourceAsStream("/brushes/"+preset.id()+"/texture.bin").readAllBytes(),preset.params(),preset.size(),preset.opacity(),preset.flow());}catch(java.io.IOException error){throw new IllegalStateException(error);}}
}''')
print('Desktop JNI:',len(methods),'methods. Brush presets:',len(rows))
