#!/bin/bash

lein clean
lein uberjar
scp target/uberjar/ecsctl-*-standalone.jar \
trever.anderson@work.computer:~/git/open-source/ecsctl/ecsctl.jar