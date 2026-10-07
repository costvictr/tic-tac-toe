#!/usr/bin/env bash
cd "$(dirname "$0")"
export JAVA_HOME="$HOME/.jdks/openjdk-26"
export PATH="$JAVA_HOME/bin:$PATH"
export GRADLE_OPTS="--enable-native-access=ALL-UNNAMED ${GRADLE_OPTS:-}"

./gradlew installDist --console=plain --quiet
exec "./build/install/Tic-tac-toe/bin/Tic-tac-toe"
