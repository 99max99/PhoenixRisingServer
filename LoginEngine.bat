@echo off
title Engine
echo Launching Server...
"C:/Program Files/Java/jre1.8.0_111/bin/java.exe" -cp bin;library/*; net.kagani.LoginEngine true false
pause