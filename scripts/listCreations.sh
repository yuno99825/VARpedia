#!/bin/bash
if [ -z "$(ls -A ./creations)" ]; then
  exit 1
fi
find ./creations -maxdepth 1 -type d
exit 0
