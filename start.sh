#!/usr/bin/env bash
set -euo pipefail

usage() {
    echo "Usage: $0 <python_script.py> <java_jar_or_class> <arg>"
    echo "  <python_script.py>   Path to a Python script to run"
    echo "  <java_jar_or_class>  Path to a .jar or a Java class name to run"
    echo "  <arg>                Single argument passed to both programs"
    exit 2
}

if [ "$#" -ne 3 ]; then
    usage
fi

PY_SCRIPT="$1"
JAVA_TARGET="$2"
ARG="$3"

# Check Python
if [ ! -f "$PY_SCRIPT" ]; then
    echo "Error: Python script '$PY_SCRIPT' not found" >&2
    exit 3
fi

if command -v python3 >/dev/null 2>&1; then
    PY_EXEC=python3
elif command -v python >/dev/null 2>&1; then
    PY_EXEC=python
else
    echo "Error: Python not found in PATH" >&2
    exit 4
fi

echo "-> Running Python: $PY_EXEC $PY_SCRIPT $ARG"
"$PY_EXEC" "$PY_SCRIPT" "$ARG"

# Check Java
if ! command -v java >/dev/null 2>&1; then
    echo "Error: java not found in PATH" >&2
    exit 5
fi

echo "-> Running Java: $JAVA_TARGET $ARG"
if [[ "$JAVA_TARGET" == *.jar ]]; then
    java -jar "$JAVA_TARGET" "$ARG"
else
    # treat as class name, using current directory on classpath
    java -cp . "$JAVA_TARGET" "$ARG"
fi

echo "-> Completed"