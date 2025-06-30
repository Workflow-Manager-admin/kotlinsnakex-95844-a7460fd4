#!/bin/bash
cd /home/kavia/workspace/code-generation/kotlinsnakex-95844-a7460fd4/snake_game_frontend
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

