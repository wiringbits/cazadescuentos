#!/bin/bash
set -e

# build
PROD=true sbt clean chromePackage

# package sources
rm -f sources.zip && zip -r sources.zip \
  build.sbt \
  README.md \
  dist.sh \
  src \
  project/build.properties \
  project/plugins.sbt

# move to the right folder
cd target/chrome/unpacked-opt/

# build the extension
web-ext build --ignore-files=manifest.temp

# back
cd -

# copy artifacts
rm -rf web-ext-artifacts 
mv target/chrome/unpacked-opt/web-ext-artifacts web-ext-artifacts
mv sources.zip web-ext-artifacts/
