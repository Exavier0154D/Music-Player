@echo off
echo ====== COMPILANDO PROYECTO CON MAVEN ======
cd /d %~dp0
mvn clean package

if %ERRORLEVEL% NEQ 0 (
    echo ❌ ERROR: Falló la compilación con Maven.
    pause
    exit /b
)

echo.
echo ====== EJECUTANDO APLICACION JAR ======
set FX="C:\Users\Det-Pc\Downloads\openjfx-24.0.1_windows-x64_bin-sdk (1)\javafx-sdk-24.0.1\lib"
set JAR="target\Proyectousicalxbdl-1.0-SNAPSHOT.jar"

if not exist %JAR% (
    echo ❌ ERROR: El archivo JAR no fue encontrado en: %JAR%
    pause
    exit /b
)

java --module-path %FX% --add-modules javafx.controls,javafx.fxml,javafx.media -jar %JAR%

pause