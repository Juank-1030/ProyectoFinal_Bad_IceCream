@echo off
REM Script para compilar y ejecutar SOLO el paquete Domain
REM Sin dependencias de Controller ni Presentation

echo ============================================
echo   Compilando SOLO el paquete Domain...
echo ============================================
echo.

REM Compilar solo Domain
javac Domain\*.java

if %ERRORLEVEL% EQU 0 (
    echo ✅ Compilacion exitosa
    echo.
    echo ============================================
    echo   Ejecutando ConsoleGame (solo Domain^)...
    echo ============================================
    echo.
    
    REM Ejecutar ConsoleGame
    java Domain.ConsoleGame
) else (
    echo ❌ Error en la compilacion
    pause
    exit /b 1
)
