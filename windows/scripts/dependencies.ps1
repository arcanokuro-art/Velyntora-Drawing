$ErrorActionPreference = 'Stop'
$ProgressPreference = 'SilentlyContinue'
New-Item -ItemType Directory -Force windows/build/input | Out-Null
mvn -B -f windows/pom.xml dependency:copy-dependencies "-DoutputDirectory=$((Resolve-Path windows/build/input).Path)"
if ($LASTEXITCODE -ne 0) { throw 'Desktop dependencies failed' }
$env:VELYNTORA_BUILD_INPUT = (Resolve-Path windows/build/input).Path
$model = Join-Path $env:VELYNTORA_BUILD_INPUT 'migan_pipeline_v2.onnx'
if (!(Test-Path $model)) {
curl.exe -L --fail --retry 2 --connect-timeout 30 --max-time 300 'https://huggingface.co/andraniksargsyan/migan/resolve/1538c135034b8cfe7a8472f34d09c8a5a45b17a7/migan_pipeline_v2.onnx' -o $model
if ($LASTEXITCODE -ne 0) { throw 'MI-GAN download failed' }
}
if ((Get-FileHash $model -Algorithm SHA256).Hash.ToLower() -ne '6f1f3530a1a2324b19752018ce756088b07973cda8d7d890034ace5c8a48c40b') { throw 'MI-GAN SHA256 mismatch' }
