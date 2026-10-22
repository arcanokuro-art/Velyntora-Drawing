from pathlib import Path
import textwrap
root=Path(__file__).resolve().parents[1]
jars=sorted(p.name for p in (root/'build/input').glob('*.jar') if p.name!='Velyntora-Drawing.jar')
value='Class-Path: '+' '.join(jars)
lines=textwrap.wrap(value,width=70,subsequent_indent=' ',break_long_words=False,break_on_hyphens=False)
(root/'build/manifest.mf').write_text('Manifest-Version: 1.0\nMain-Class: app.velyntora.windows.app.App\n'+'\n'.join(lines)+'\n\n')
