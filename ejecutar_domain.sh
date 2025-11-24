#!/bin/bash

# Script para compilar y ejecutar SOLO el paquete Domain
# Sin dependencias de Controller ni Presentation

echo "============================================"
echo "  Compilando SOLO el paquete Domain..."
echo "============================================"
echo ""

# Compilar solo Domain
javac Domain/*.java

if [ $? -eq 0 ]; then
    echo "✅ Compilación exitosa"
    echo ""
    echo "============================================"
    echo "  Ejecutando ConsoleGame (solo Domain)..."
    echo "============================================"
    echo ""
    
    # Ejecutar ConsoleGame
    java Domain.ConsoleGame
else
    echo "❌ Error en la compilación"
    exit 1
fi
