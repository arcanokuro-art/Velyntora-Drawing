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
# Compile the original pure-Java codecs rather than reimplementing their formats.
portable=['document/dimensions/CanvasLimits.java','document/project/VlyProjectStream.java','document/openraster/OpenRasterArchive.java','formats/bmp/BmpReader.java','formats/tga/TgaReader.java','formats/ppm/PpmCodec.java','formats/ico/IcoCodec.java','formats/pcx/PcxReader.java','formats/xbitmap/XRasterReader.java','formats/tiff/TiffReader.java','formats/containers/IconContainers.java','formats/shared/RasterFileWriter.java','formats/tiff/TiffWriter.java','formats/gif/GifWriter.java']
for rel in portable:
 p=root/'engine/app/src/main/java/art/velyntora/core'/rel
 dest=(build/'generated/art/velyntora/core/document' if p.name=='CanvasLimits.java' else generated)/p.name;dest.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(p,dest)
for p in (root/'engine/app/src/main/java/art/velyntora/core/components/layers/blending').rglob('*.java'):shutil.copyfile(p,generated/p.name)
facade=root/'windows/src/art/velyntora/core';facade.mkdir(parents=True,exist_ok=True)
backend=(root/'engine/tools/remove_ai/RemoveAiBackend.java').read_text().replace('import android.content.Context;','').replace('final class RemoveAiBackend','public final class OfflineRemove')
a=backend.index('  File model=');b=backend.index('  try(OrtSession.SessionOptions',a)
backend=backend[:a]+'''  File model=new File(System.getProperty("velyntora.assets"),"migan_pipeline_v2.onnx");
  if(!model.isFile()||!HASH.equals(digest(new FileInputStream(model))))throw new IOException("Modelo MI-GAN ausente o dañado");
'''+backend[b:]
backend=backend.replace('load(Context context)','load()').replace('synchronized int[] run(Context context,','public synchronized int[] run(').replace('runExclusive(context,','runExclusive(').replace('runExclusive(Context context,','runExclusive(').replace('load(context);','load();')
(generated/'OfflineRemove.java').write_text(backend)
p=generated/'DrawingView.java';text=p.read_text().replace('\n}', '\n public static native boolean nativeStageObject(byte[] encoded,float[] transform,byte[] text,int[] style);\n public static native byte[] nativeObjectSource(int index);\n public static native void nativeAttachPointer(long hwnd);\n public static native float nativePointerPressure();\n}');p.write_text(text)
shutil.copyfile(root/'engine/tools/line_curve/CurveDraft.java',generated/'CurveDraft.java')
