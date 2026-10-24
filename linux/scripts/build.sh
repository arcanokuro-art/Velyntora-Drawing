#!/usr/bin/env bash
set -euo pipefail
ROOT=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd)
cd "$ROOT"
export VELYNTORA_PLATFORM=linux
export VELYNTORA_DESKTOP_BUILD="$ROOT/linux/build"
export VELYNTORA_BUILD_INPUT="$VELYNTORA_DESKTOP_BUILD/input"
BUILD=$VELYNTORA_DESKTOP_BUILD
mkdir -p "$BUILD/input"
python3 -m pip install Pillow==12.0.0
mvn -B -f windows/pom.xml dependency:copy-dependencies "-DoutputDirectory=$VELYNTORA_BUILD_INPUT"
MODEL="$BUILD/input/migan_pipeline_v2.onnx"
if [[ ! -f "$MODEL" ]]; then
 curl --location --fail --retry 2 --connect-timeout 30 --max-time 300 'https://huggingface.co/andraniksargsyan/migan/resolve/1538c135034b8cfe7a8472f34d09c8a5a45b17a7/migan_pipeline_v2.onnx' --output "$MODEL"
fi
printf '%s  %s\n' '6f1f3530a1a2324b19752018ce756088b07973cda8d7d890034ace5c8a48c40b' "$MODEL" | sha256sum --check --status
python3 windows/scripts/prepare.py
if [[ "${1:-}" == '--dependencies-only' ]]; then exit 0; fi
python3 engine/scripts/check_modular_boundaries.py
cmake -S linux -B "$BUILD/native" -G Ninja -DCMAKE_BUILD_TYPE=Release
cmake --build "$BUILD/native" --parallel 2
rm -rf "$BUILD/classes" "$BUILD/distribution"
mkdir -p "$BUILD/classes" "$BUILD/distribution"
mapfile -d '' SOURCES < <(find windows/src "$BUILD/generated" windows/tests linux/tests -name '*.java' -print0)
javac -encoding UTF-8 -cp "$BUILD/input/*" -d "$BUILD/classes" "${SOURCES[@]}"
cp "$BUILD/native/libvelyntora_desktop.so" "$BUILD/input/"
python3 windows/scripts/manifest.py
jar --create --file "$BUILD/input/Velyntora-Drawing.jar" --manifest "$BUILD/manifest.mf" -C "$BUILD/classes" . -C "$BUILD/resources" .
xvfb-run -a java -Djava.library.path="$BUILD/input" -Dvelyntora.assets="$BUILD/input" -cp "$BUILD/classes:$BUILD/resources:$BUILD/input/*" LinuxSmoke
jpackage --type app-image --dest "$BUILD/distribution" --input "$BUILD/input" --name V-Drawing --main-jar Velyntora-Drawing.jar --app-version 0.4.0 --vendor 'Zaskar Startup' --description 'Velyntora Drawing para Linux' --java-options '-Xmx2g' --java-options '-Djava.library.path=$APPDIR' --java-options '-Dvelyntora.assets=$APPDIR' --java-options '-Dvelyntora.selftest=LinuxSmoke'
cp linux/scripts/V-Drawing.sh "$BUILD/distribution/V-Drawing/"
cp linux/README.md "$BUILD/distribution/V-Drawing/LEEME.md"
chmod +x "$BUILD/distribution/V-Drawing/V-Drawing.sh"
marker="$BUILD/packaged-self-test.txt"
rm -f "$marker"
xvfb-run -a "$BUILD/distribution/V-Drawing/V-Drawing.sh" --self-test "$marker"
grep -qx ok "$marker"
# Check relocation and shell quoting after extraction, including non-ASCII paths.
tar -czf "$BUILD/Velyntora-Drawing-Linux-x64.tar.gz" -C "$BUILD/distribution" V-Drawing
relocated=$(mktemp -d "$BUILD/Prueba portátil 日本 XXXX")
trap 'rm -rf "$relocated"' EXIT
tar -xzf "$BUILD/Velyntora-Drawing-Linux-x64.tar.gz" -C "$relocated"
xvfb-run -a "$relocated/V-Drawing/V-Drawing.sh" --self-test "$relocated/result.txt"
grep -qx ok "$relocated/result.txt"
jpackage --type deb --dest "$BUILD/distribution" --app-image "$BUILD/distribution/V-Drawing" --name V-Drawing --app-version 0.4.0 --vendor 'Zaskar Startup' --linux-package-name velyntora-drawing --linux-menu-group Graphics --linux-shortcut --description 'Ilustración, capas y edición de imágenes con Velyntora Core'
mapfile -t DEBS < <(find "$BUILD/distribution" -maxdepth 1 -type f -name '*.deb')
[[ ${#DEBS[@]} -eq 1 ]]
cp "${DEBS[0]}" "$BUILD/velyntora-drawing_0.4.0_amd64.deb"
dpkg-deb --info "$BUILD/velyntora-drawing_0.4.0_amd64.deb"
(cd "$BUILD" && sha256sum Velyntora-Drawing-Linux-x64.tar.gz velyntora-drawing_0.4.0_amd64.deb > SHA256SUMS)
