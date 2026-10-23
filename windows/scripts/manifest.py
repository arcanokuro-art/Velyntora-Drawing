from pathlib import Path
root=Path(__file__).resolve().parents[1]
jars=sorted(p.name for p in (root/'build/input').glob('*.jar') if p.name!='Velyntora-Drawing.jar')
value='Class-Path: '+' '.join(jars)
# A manifest continuation removes exactly its first space. Preserve every byte
# of the original classpath, including separator spaces at a line boundary.
lines=[value[:70]];remaining=value[70:]
while remaining:
 lines.append(' '+remaining[:69]);remaining=remaining[69:]
assert ''.join([lines[0]]+[line[1:] for line in lines[1:]])==value
(root/'build/manifest.mf').write_text('Manifest-Version: 1.0\nMain-Class: app.velyntora.windows.app.App\n'+'\n'.join(lines)+'\n\n')
