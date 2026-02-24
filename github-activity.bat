@echo off
chcp 65001 > nul
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -jar "%~dp0target\github-user-activity-1.0-SNAPSHOT.jar" %*