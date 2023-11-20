#!/usr/bin/env bash
set -eu

rm .asciidoctor/diag-*.png >& /dev/null || echo "No file to remove. Let's go ahead." >&2