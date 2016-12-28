@echo off
title Engine
echo Launching Server...
"C:/Program Files/Java/jre1.8.0_111/bin/java.exe" -Xmx512m -cp bin;library/*; net.kagani.Engine 1 false true
pause