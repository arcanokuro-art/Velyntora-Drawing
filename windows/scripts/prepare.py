from pathlib import Path
import re,json,shutil,os
from PIL import Image
root=Path(__file__).resolve().parents[2];build=Path(os.environ.get('VELYNTORA_DESKTOP_BUILD',str(root/'windows/build')));generated=build/'generated/art/velyntora/core';generated.mkdir(parents=True,exist_ok=True)
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
 for name in ['tip.bin','texture.bin','preview.png']:shutil.copyfile(folder/name,dest/name)
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
p=generated/'DrawingView.java';p.write_text(p.read_text().replace('\n}', '\n public static native boolean nativeRecoveryPath(String path,long revision);\n}'))
shutil.copyfile(root/'engine/tools/gradient/GradientRaster.java',generated/'GradientRaster.java')
shutil.copyfile(root/'engine/app/src/main/java/art/velyntora/core/formats/metadata/ExifOrientation.java',generated/'ExifOrientation.java')
shutil.copyfile(root/'engine/app/src/main/java/art/velyntora/core/components/effects/adjustments/ToneCurve.java',generated/'ToneCurve.java')
for name in ['BmpReaderTests','CurveDraftTests','ExifOrientationTests','GradientRasterTests','GifWriterTests','IcoCodecTests','IconContainersTests','OpenRasterTests','PcxReaderTests','PpmCodecTests','RasterFileWriterTests','TgaReaderTests','TiffReaderTests','TiffWriterTests','VlyImageElementStreamTests','VlyTextElementStreamTests','XRasterReaderTests','ToneCurveTests']:
 shutil.copyfile(root/'engine/tests'/f'{name}.java',generated/f'{name}.java')
# Keep the original assertions; replace the single Linux-only fixture path for Windows.
p=generated/'BmpReaderTests.java';p.write_text(p.read_text().replace('"/tmp/velyntora-bmp-independent.bmp"','java.nio.file.Path.of(System.getProperty("java.io.tmpdir"),"velyntora-bmp-independent.bmp").toString()'))
# Keep named X11 colors and third-party attribution in the distributed application.
shutil.copytree(root/'engine/app/src/main/resources',resources,dirs_exist_ok=True)
licenses=resources/'licenses';licenses.mkdir(exist_ok=True)
shutil.copyfile(root/'engine/tools/remove_ai/assets/remove_ai/MI-GAN-LICENSE.txt',licenses/'MI-GAN-LICENSE.txt')
for p in (root/'engine/app/src/main/assets/licenses').glob('*'):shutil.copyfile(p,licenses/p.name)
shutil.copyfile(brushroot/'provenance.json',licenses/'brush-provenance.json')
Image.open(root/'engine/tools/remove_ai/assets/remove_ai/remove_ai.webp').save(resources/'remove_ai.png')
import subprocess
input_dir=build/'input';input_dir.mkdir(exist_ok=True)
provenance={'application':'Velyntora Drawing','version':'0.4.0','branch':os.environ.get('VELYNTORA_PLATFORM','windows'),'commit':subprocess.check_output(['git','-C',str(root),'rev-parse','HEAD'],text=True).strip(),'core_commit':json.loads((root/'docs/core-source.json').read_text())['commit'],'migan_sha256':'6f1f3530a1a2324b19752018ce756088b07973cda8d7d890034ace5c8a48c40b'}
(input_dir/'BUILD-INFO.json').write_text(json.dumps(provenance,indent=2)+'\n')
shutil.copyfile(root/('linux/README.md' if os.environ.get('VELYNTORA_PLATFORM')=='linux' else 'windows/README.md'),input_dir/'LEEME.md')
shutil.copytree(licenses,input_dir/'licenses',dirs_exist_ok=True)
shutil.copyfile(root/'engine/app/src/main/java/art/velyntora/core/document/importing/ImageImportSize.java',generated/'ImageImportSize.java')
shutil.copyfile(root/'engine/tests/ImageImportSizeTests.java',generated/'ImageImportSizeTests.java')
# Reuse each original tool icon's geometry through the Java2D desktop adapter.
icon_map={'REMOVE_AI':None,'CIRCLE':'CircleIcon','FREEFORM':'FreeformIcon','MOVE_PIXELS':'MovePixelsIcon','BRUSH':'BrushIcon','PENCIL':'PencilIcon','ERASER':'EraserIcon','FILL':'BucketIcon','PICKER':'PickerIcon','PAN':'PanIcon','ZOOM':'ZoomIcon','LINE':'LineCurveIcon','RECT':'RectangleIcon','ELLIPSE':'EllipseIcon','TRIANGLE':'TriangleIcon','ROUNDRECT':'RoundedRectangleIcon','SELECT':'RectangleSelectionIcon','ELLIPSE_SELECT':'EllipseSelectionIcon','LASSO':'LassoSelectionIcon','WAND':'MagicWandIcon','GRADIENT':'GradientIcon','TEXT':'TextIcon','CLONE':'CloneIcon','RECOLOR':'RecolorIcon','MOVE':'MoveSelectionIcon'}
icon_dir=build/'generated/app/velyntora/windows/tools';icon_dir.mkdir(parents=True,exist_ok=True)
for name in set(icon_map.values())- {None}:
 source=next((root/'engine/tools').rglob(name+'.java')).read_text()
 source=source.replace('package art.velyntora.core;','package app.velyntora.windows.tools;').replace('import android.graphics.*;','').replace('extends ToolIcon','extends DesktopToolIcon')
 (icon_dir/(name+'.java')).write_text(source)
cases=''.join('case '+tool+' -> new '+name+'();' for tool,name in icon_map.items() if name)
(icon_dir/'ToolIcons.java').write_text('package app.velyntora.windows.tools; public final class ToolIcons {public static javax.swing.Icon of(Tool tool){return switch(tool){'+cases+'case REMOVE_AI -> new javax.swing.ImageIcon(new javax.swing.ImageIcon(ToolIcons.class.getResource("/remove_ai.png")).getImage().getScaledInstance(28,28,java.awt.Image.SCALE_SMOOTH));};}}')
p=generated/'DrawingView.java';p.write_text(p.read_text().replace('\n}', '\n public static native long nativeDocumentBytes();\n}'))
# Keep Android's one-click color presets and numeric arguments, not just their algorithms.
source=(root/'engine/app/src/main/java/art/velyntora/core/components/menus/coloradjustments/ColorAdjustmentsMenu.java').read_text()
labels=re.findall(r'"([^"\\]*(?:\\.[^"\\]*)*)"',source.split('new String[] {',1)[1].split('},',1)[0])
actions=source.split('new Runnable[] {',1)[1].rsplit('});',1)[0]
parts=re.findall(r'\(\) -> \{.*?\n          \}|host::\w+',actions,re.S)
assert len(labels)==len(parts),(len(labels),len(parts))
rows=[]
for label,action in zip(labels,parts):
 match=re.search(r'host.readDrawing\(\)\.(\w+)\(([-\d,\s]*)\)',action)
 if not match:continue
 method,args=match.groups();method={'grayscaleFromChannelActive':'desaturateChannelActive'}.get(method,method)
 method='native'+method[0].upper()+method[1:]
 assert method in (generated/'DrawingView.java').read_text(),method
 rows.append('add(menu,'+json.dumps(label,ensure_ascii=False)+',()->execute.accept(()->DrawingView.'+method+'('+args+')));')
(generated/'DesktopColorPresets.java').write_text('''package art.velyntora.core;
public final class DesktopColorPresets {public static javax.swing.JMenu menu(java.util.function.Consumer<java.util.concurrent.Callable<Boolean>> execute){javax.swing.JMenu menu=new javax.swing.JMenu("Acciones rápidas de Android");'''+''.join(rows)+'''return menu;}private static void add(javax.swing.JMenu menu,String label,Runnable action){javax.swing.JMenuItem item=new javax.swing.JMenuItem(label);item.addActionListener(e->action.run());menu.add(item);}}
''')
for name in ['FilledRectangleIcon','FilledEllipseIcon','FilledRoundedRectangleIcon','FilledTriangleIcon']:
 source=next((root/'engine/tools').rglob(name+'.java')).read_text().replace('package art.velyntora.core;','package app.velyntora.windows.tools;').replace('import android.graphics.*;','').replace('extends ToolIcon','extends DesktopToolIcon')
 (icon_dir/(name+'.java')).write_text(source)
p=icon_dir/'ToolIcons.java';s=p.read_text();p.write_text(s[:-1]+'''public static javax.swing.Icon figure(Tool tool,boolean filled){if(!filled)return of(tool);return switch(tool){case RECT->new FilledRectangleIcon();case ELLIPSE->new FilledEllipseIcon();case ROUNDRECT->new FilledRoundedRectangleIcon();case TRIANGLE->new FilledTriangleIcon();default->of(tool);};}}''')
