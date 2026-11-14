#!/usr/bin/env bash
set -euo pipefail
umask 077

if [[ $# != 1 || ! -d "$1" ]]; then
  echo "Uso: bash $0 <carpeta privada existente para el respaldo>" >&2
  exit 2
fi

repo=arcanokuro-art/Velyntora-Drawing
keystore="$1/velyntora-drawing.p12"
password_file="$1/velyntora-drawing-password.txt"

gh auth status --hostname github.com
if [[ ! -e "$keystore" && ! -e "$password_file" ]]; then
  (set -o noclobber; openssl rand -hex 32 > "$password_file")
fi
if [[ ! -s "$password_file" ]]; then
  echo 'Falta el respaldo de la contraseña del keystore. Recupera el original antes de continuar.' >&2
  exit 1
fi

if [[ ! -e "$keystore" ]]; then
  keytool -genkeypair -keystore "$keystore" -storetype PKCS12 -alias drawing \
    -keyalg RSA -keysize 3072 -validity 10000 -dname 'CN=Velyntora Drawing, O=Zaskar Startup' \
    -storepass:file "$password_file"
fi
fingerprint=$(keytool -exportcert -keystore "$keystore" -storetype PKCS12 -alias drawing \
  -storepass:file "$password_file" | openssl dgst -sha256 -r | cut -d ' ' -f 1)
existing_fingerprint=$(gh variable list --repo "$repo" --json name,value \
  --jq '.[] | select(.name == "DRAWING_ANDROID_CERT_SHA256") | .value')
if [[ -n "$existing_fingerprint" && "$existing_fingerprint" != "$fingerprint" ]]; then
  echo 'GitHub ya tiene otro certificado de Drawing. Usa el respaldo de esa clave.' >&2
  exit 1
fi

base64 -w 0 "$keystore" | gh secret set DRAWING_ANDROID_KEYSTORE_BASE64 --repo "$repo"
gh secret set DRAWING_ANDROID_KEYSTORE_PASSWORD --repo "$repo" < "$password_file"
gh variable set DRAWING_ANDROID_CERT_SHA256 --repo "$repo" --body "$fingerprint"
echo 'Firma registrada en GitHub. Conserva los dos archivos de respaldo en un lugar privado.'
