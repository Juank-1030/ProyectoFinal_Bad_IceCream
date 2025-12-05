# 📦 Última Versión del Proyecto - Bad Ice Cream

## ✅ Estado Actual (5 de Diciembre de 2025)

### Commit Actual
**Hash:** `d098a94`
**Mensaje:** "Actualización de movimiento visual en GameObject: interpolación rápida y fluida con velocidad adaptativa"

### Estado de Compilación
✅ **Compilación exitosa** - Todos los archivos compilan sin errores fatales

## 📊 Resumen Rápido

| Aspecto | Estado |
|---------|--------|
| Compilación | ✅ Exitosa |
| Modos de Juego | ✅ PVP, PVP Cooperativo, PVM, MVM |
| Sistema de Pausa | ✅ Funcional |
| IA de Monstruos | ✅ Implementada |
| Navegación de Menús | ✅ Funcional |
| Reseteo de Estado | ✅ Implementado |
| Documentación | ✅ Completa |

## 🎮 Características Principales

### Modos de Juego Implementados
1. **PVP vs Monstruo** - 1 jugador vs 1 monstruo (control manual del monstruo)
2. **PVP Cooperativo** - 2 jugadores vs 1 monstruo (monstruo con IA)
3. **PVM** - 1 jugador vs monstruos con IA automática
4. **MVM** - 2 helados con IA vs monstruos con IA

### Sistema de Control
- ✅ Input buffering pattern para control fluido
- ✅ Reset de teclas al pausar/iniciar nivel
- ✅ Soporte para 2 jugadores simultáneos
- ✅ Controles personalizables por jugador

### Sistema de Pausa
- ✅ Pausa con P o ESC
- ✅ Menú de opciones (Continuar, Guardar, Cargar, Salir)
- ✅ Atajo M para salir rápido al menú
- ✅ Preservación de estado durante pausa

### IA de Enemigos
- ✅ **Narval** - Persecución inteligente con carga
- ✅ **Maceta (Pot)** - Búsqueda de jugadores
- ✅ **Calamar Amarillo** - Movimiento adaptativo
- ✅ **Troll** - Patrón de movimiento

### Animaciones y Visuales
- ✅ Animaciones no-bloqueantes (Timer-based)
- ✅ Interpolación fluida de movimiento
- ✅ Velocidad adaptativa para fluidez
- ✅ Animaciones de frutas con aparición
- ✅ GIFs de introducción y menús

## 📁 Estructura del Proyecto

```
ProyectoFinal_Bad_Ice_Cream/
├── Domain/                          # Lógica de juego (25+ archivos)
│   ├── Game.java                   # Motor principal
│   ├── Board.java                  # Tablero y colisiones
│   ├── Level.java                  # Gestión de niveles
│   ├── *AI.java                    # IA de enemigos (Narval, Pot, etc.)
│   ├── Enemy.java                  # Clase base de enemigos
│   ├── IceCream.java               # Helado/Jugador
│   └── ... (más clases de juego)
│
├── Controller/                      # Controladores
│   ├── GameController.java         # Input y lógica de juego
│   └── PresentationController.java # Gestión de ventanas
│
├── Presentation/                    # Interfaz gráfica (8+ archivos)
│   ├── GamePanel.java              # Renderizado del juego
│   ├── Intro.java                  # Pantalla de introducción
│   ├── StartMenu.java              # Menú principal
│   ├── Modes.java                  # Selección de modos
│   ├── SelectIceCream.java         # Selector de helado
│   └── ... (más componentes UI)
│
├── Test/                            # Tests unitarios
│   ├── TestPVPMode.java
│   └── TestInputBuffering.java
│
├── Resources/                       # Activos visuales (~100 MB)
│   ├── Marca/                       # Animaciones de intro (GIFs)
│   ├── Helados/                     # Sprites de helados
│   ├── Monstruos/                   # Sprites de monstruos
│   ├── Frutas/                      # Sprites de frutas
│   ├── Botones/                     # Botones de UI
│   ├── Letreros/                    # Textos/carteles
│   └── ... (más recursos)
│
├── bin/                             # Archivos compilados
│   ├── Domain/
│   ├── Controller/
│   ├── Presentation/
│   └── Test/
│
├── levels/                          # Configuración de niveles
├── README_PROYECTO.md               # Documentación completa
├── INSTALACION.md                   # Guía de instalación
└── README.md                        # Información general

```

## 🚀 Cómo Usar

### Compilar
```bash
cd e:\DOPO\ProyectoFinal_Bad_Ice_Cream
javac -d bin Domain/*.java Controller/*.java Presentation/*.java Test/*.java
```

### Ejecutar
```bash
java -cp bin Controller.PresentationController
```

### Descargar/Clonar
```bash
# Opción 1: Clonar desde GitHub
git clone https://github.com/Juank-1030/ProyectoFinal_Bad_IceCream.git

# Opción 2: Descargar ZIP desde
# https://github.com/Juank-1030/ProyectoFinal_Bad_IceCream/archive/refs/heads/main.zip
```

## 🎮 Controles Rápidos

### Jugador 1 (Helado 1)
- ⬆️ Arriba / ⬇️ Abajo / ⬅️ Izquierda / ➡️ Derecha

### Jugador 2 (Helado 2 - Cooperativo)
- W / S / A / D

### Globales
- **P** o **ESC** - Pausar
- **M** - Volver al menú (desde pausa)

## 📈 Progresión de Desarrollo

### Fase 1: Implementación Base
- Creación de estructura básica
- Implementación de modos de juego
- Sistema de tablero y colisiones

### Fase 2: Sistema de IA
- Creación de EnemyAI
- Implementación de 4 tipos de monstruos
- IA inteligente para persecución

### Fase 3: Sistema de Pausa y Navegación
- Pausa con JOptionPane
- Sistema de callbacks para navegación
- Retorno a menú desde juego

### Fase 4: Correcciones y Optimizaciones
- Animaciones no-bloqueantes
- Reseteo correcto de estado
- Limpieza de recursos
- Interpolación fluida de movimiento

### Fase 5: Refinamiento Visual (Actual)
- Mejora de interpolación de movimiento
- Velocidad adaptativa
- Animaciones de frutas mejoradas
- Alineación de UI

## 📝 Especificaciones Técnicas

- **Lenguaje:** Java 21 LTS
- **Framework UI:** Swing
- **Patrón de Código:** MVC (Model-View-Controller)
- **Input System:** Input Buffering Pattern
- **Rendering:** Timer-based (60 FPS)
- **Compilación:** Exitosa sin errores fatales

## ✨ Características Avanzadas

1. **Sistema de Input Buffering**
   - Captura múltiples teclas simultáneas
   - Reseteo automático al pausar/iniciar

2. **Animaciones No-Bloqueantes**
   - Timer de Swing para animaciones
   - No bloquea el thread de UI

3. **IA Adaptativa**
   - Búsqueda de caminos
   - Comportamientos especiales por monstruo
   - Respuesta a eventos del juego

4. **Gestión de Recursos**
   - Limpieza automática entre partidas
   - Manejo correcto de ciclo de vida
   - Sin memory leaks reportados

5. **Interpolación de Movimiento**
   - Movimiento fluido y suave
   - Velocidad adaptativa según FPS
   - Transiciones visuales mejjoradas

## 🐛 Estado de Bugs

**Bugs Conocidos:** Ninguno reportado en versión actual

**Tests Pasados:**
- ✅ TestPVPMode - Verificación de modo PVP
- ✅ TestInputBuffering - Verificación de input

## 📚 Documentación Disponible

1. **README_PROYECTO.md** - Documentación completa del proyecto
2. **INSTALACION.md** - Guía detallada de instalación
3. **Código comentado** - Todos los archivos tienen comentarios explicativos

## 👤 Información del Desarrollo

- **Desarrollador:** Juank-1030
- **Repositorio:** https://github.com/Juank-1030/ProyectoFinal_Bad_IceCream
- **Rama Principal:** main
- **Estado:** Activo y en desarrollo

## 📦 Requisitos Mínimos

- **Java:** JDK 21 LTS o superior
- **RAM:** 512 MB (recomendado 1 GB)
- **Espacio:** 500 MB
- **SO:** Windows, Linux, macOS

## 🔄 Actualizaciones Recientes

1. **Interpolación de movimiento** - Movimiento más fluido
2. **Animaciones de frutas** - Aparición mejorada
3. **Selección de modo PVP** - Mejor interfaz
4. **Recursos visuales** - Más sprites y animaciones

---

## 📥 Descargar Ahora

**Git Clone:**
```bash
git clone https://github.com/Juank-1030/ProyectoFinal_Bad_IceCream.git
```

**ZIP Download:**
https://github.com/Juank-1030/ProyectoFinal_Bad_IceCream/archive/refs/heads/main.zip

---

**Proyecto completamente funcional y listo para jugar. ¡Disfruta!**
