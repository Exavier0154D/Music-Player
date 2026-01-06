@echo on
cd /d %~dp0
echo ====== COMPILANDO PROYECTO CON MAVEN ======
mvn clean package

if %ERRORLEVEL% NEQ 0 (
    echo ❌ ERROR: Falló la compilación Maven.
    pause
    exit /b
)

echo ====== EJECUTANDO APLICACION JAR ======
set FX="C:\Users\Det-Pc\Downloads\openjfx-24.0.1_windows-x64_bin-sdk (1)\javafx-sdk-24.0.1\lib"
set JAR="target\Proyectousicalxbdl-1.0-SNAPSHOT.jar"

if not exist %JAR% (
    echo ❌ ERROR: El archivo JAR no fue encontrado en: %JAR%
    pause
    exit /b
)

echo Ejecutando JAR...
java --module-path %FX% --add-modules javafx.controls,javafx.fxml,javafx.media -jar %JAR%

echo PROCESO TERMINADO.
pause