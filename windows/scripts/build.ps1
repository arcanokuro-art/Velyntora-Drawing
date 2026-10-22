$ErrorActionPreference = 'Stop'
python -m pip install Pillow==12.0.0
if ($LASTEXITCODE -ne 0) { throw 'Pillow install failed' }
$env:VELYNTORA_BUILD_INPUT = (Resolve-Path windows/build/input).Path
python windows/scripts/prepare.py
if ($LASTEXITCODE -ne 0) { throw 'Resource generation failed' }
cmake -S windows -B windows/build/native -G Ninja -DCMAKE_BUILD_TYPE=Release -DCMAKE_CXX_COMPILER=g++
if ($LASTEXITCODE -ne 0) { throw 'CMake failed' }
cmake --build windows/build/native --parallel 2
if ($LASTEXITCODE -ne 0) { throw 'Native build failed' }
New-Item -ItemType Directory -Force windows/build/classes,windows/build/input | Out-Null
$sources = @(Get-ChildItem windows/src,windows/build/generated -Recurse -Filter *.java | ForEach-Object { $_.FullName })
javac -cp "windows/build/input/*" -encoding UTF-8 -d windows/build/classes $sources @(Get-ChildItem windows/tests -Filter *.java | ForEach-Object { $_.FullName })
if ($LASTEXITCODE -ne 0) { throw 'Java build failed' }
Copy-Item windows/build/native/velyntora_desktop.dll windows/build/input/
python windows/scripts/manifest.py
jar --create --file windows/build/input/Velyntora-Drawing.jar --manifest windows/build/manifest.mf -C windows/build/classes . -C windows/build/resources .
if ($LASTEXITCODE -ne 0) { throw 'JAR failed' }
java '-Djava.library.path=windows/build/input' "-Dvelyntora.assets=$env:VELYNTORA_BUILD_INPUT" -cp 'windows/build/classes;windows/build/resources;windows/build/input/*' DesktopSmoke
if ($LASTEXITCODE -ne 0) { throw 'Windows smoke tests failed' }
jpackage --type app-image --dest windows/build/distribution --input windows/build/input --name V-Drawing --main-jar Velyntora-Drawing.jar --app-version 0.3.0 --vendor 'Zaskar Startup' --description 'Velyntora Drawing - Piloto Windows' --java-options '-Xmx2g' --java-options '-Djava.library.path=$APPDIR' --java-options '-Dvelyntora.assets=$APPDIR'
if ($LASTEXITCODE -ne 0) { throw 'Packaging failed' }
# Launch the packaged executable, including its bundled runtime and DLL, before delivery.
$marker = Join-Path $env:RUNNER_TEMP 'velyntora-packaged-test.txt'
$process = Start-Process windows/build/distribution/V-Drawing/V-Drawing.exe -ArgumentList @('--self-test', $marker) -PassThru -Wait
if ($process.ExitCode -ne 0 -or !(Test-Path $marker) -or (Get-Content $marker) -ne 'ok') { throw 'Packaged executable self-test failed' }
Compress-Archive -Path windows/build/distribution/V-Drawing -DestinationPath windows/build/Velyntora-Drawing-Windows-x64.zip
