$ErrorActionPreference = 'Stop'
python -m pip install Pillow==12.0.0
if ($LASTEXITCODE -ne 0) { throw 'Pillow install failed' }
python windows/scripts/prepare.py
if ($LASTEXITCODE -ne 0) { throw 'Resource generation failed' }
cmake -S windows -B windows/build/native -G Ninja -DCMAKE_BUILD_TYPE=Release
if ($LASTEXITCODE -ne 0) { throw 'CMake failed' }
cmake --build windows/build/native --parallel 2
if ($LASTEXITCODE -ne 0) { throw 'Native build failed' }
New-Item -ItemType Directory -Force windows/build/classes,windows/build/input | Out-Null
$sources = @(Get-ChildItem windows/src,windows/build/generated -Recurse -Filter *.java | ForEach-Object { $_.FullName })
javac -encoding UTF-8 -d windows/build/classes $sources windows/tests/DesktopSmoke.java
if ($LASTEXITCODE -ne 0) { throw 'Java build failed' }
Copy-Item windows/build/native/velyntora_desktop.dll windows/build/input/
jar --create --file windows/build/input/Velyntora-Drawing.jar --main-class app.velyntora.windows.app.App -C windows/build/classes . -C windows/build/resources .
if ($LASTEXITCODE -ne 0) { throw 'JAR failed' }
java '-Djava.library.path=windows/build/input' -cp 'windows/build/classes;windows/build/resources' DesktopSmoke
if ($LASTEXITCODE -ne 0) { throw 'Windows smoke tests failed' }
jpackage --type app-image --dest windows/build/distribution --input windows/build/input --name V-Drawing --main-jar Velyntora-Drawing.jar --app-version 0.2.0 --vendor 'Zaskar Startup' --description 'Velyntora Drawing - Piloto Windows' --java-options '-Xmx2g' --java-options '-Djava.library.path=$APPDIR'
if ($LASTEXITCODE -ne 0) { throw 'Packaging failed' }
# Launch the packaged executable, including its bundled runtime and DLL, before delivery.
$process = Start-Process windows/build/distribution/V-Drawing/V-Drawing.exe -PassThru
Start-Sleep -Seconds 8
if ($process.HasExited) { throw 'Packaged executable closed during startup' }
Stop-Process -Id $process.Id
Compress-Archive -Path windows/build/distribution/V-Drawing -DestinationPath windows/build/Velyntora-Drawing-Windows-x64.zip
