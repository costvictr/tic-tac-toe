@echo off
cd /d "%~dp0"
set "JAVA_HOME=%USERPROFILE%\.jdks\openjdk-26"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "GRADLE_OPTS=--enable-native-access=ALL-UNNAMED %GRADLE_OPTS%"

call gradlew.bat installDist --console=plain --quiet
if errorlevel 1 exit /b 1
call "build\install\Tic-tac-toe\bin\Tic-tac-toe.bat"
