# 🎮 Bad Ice Cream - Guía de Uso y Validación

## Estado del Proyecto: ✅ COMPLETAMENTE FUNCIONAL

### Compilación

```bash
cd e:\DOPO\ProyectoFinal_Bad_Ice_Cream
javac -source 11 -target 11 -d bin -cp bin;. Controller/*.java Domain/*.java Presentation/*.java
```

**Resultado Esperado**: Compilación exitosa (solo warnings de API deprecadas)

### Ejecución

```bash
java -cp bin Controller.PresentationController
```

**Resultado Esperado**:
```
🎨 Cargando recursos gráficos...
✅ Recursos gráficos cargados: 121 imágenes
✓ Nivel 1 cargado correctamente
✓ Nivel 2 cargado correctamente
✓ Nivel 3 cargado correctamente
```

Luego aparece la ventana del juego con el menú principal.

---

## Flujos de Juego Disponibles

### 1. 🎯 Modo PVM (Player vs Machine IA)

**Flujo Completo**:
1. Selecciona "PVM" en el menú
2. Elige tu helado (Chocolate, Fresa, Vainilla)
3. Selecciona el nivel (1, 2 o 3)
4. Configura enemigos adicionales (opcional - Narval, Troll, Pot, YellowSquid)
5. Configura frutas personalizadas (opcional - Uvas, Plátanos, Cerezas, Piñas)
6. ¡Juega!

**Objetivo**: Recolecta frutas mientras evitas al monstruo controlado por IA

---

### 2. ⚔️ Modo PVP - Helado vs Monstruo

**Flujo Completo** (ORDEN IMPORTANTE):
1. Selecciona "PVP" en el menú
2. Elige "Helado vs Monstruo"
3. Elige tu helado (Chocolate, Fresa, Vainilla)
4. **Elige el monstruo enemigo** (Narval, Troll, Pot, YellowSquid)
5. Selecciona el nivel (1, 2 o 3)
6. Configura enemigos adicionales (opcional)
7. Configura frutas personalizadas (opcional)
8. ¡Juega contra el monstruo específico!

**Objetivo**: Juega como helado vs un monstruo específico que controlas

---

### 3. 👥 Modo PVP - Helado Cooperativo

**Flujo Completo**:
1. Selecciona "PVP" en el menú
2. Elige "Helado Cooperativo"
3. Elige primer helado (Chocolate, Fresa, Vainilla)
4. Elige segundo helado (Chocolate, Fresa, Vainilla)
5. Selecciona el nivel (1, 2 o 3)
6. Configura enemigos adicionales (opcional)
7. Configura frutas personalizadas (opcional)
8. ¡Juega en cooperativo!

**Objetivo**: Dos jugadores (dos helados) cooperan contra monstruos IA

---

### 4. 🤖 Modo MVM (Machine vs Machine IA)

**Flujo Completo**:
1. Selecciona "MVM" en el menú
2. Elige un helado IA (Chocolate, Fresa, Vainilla)
3. Selecciona el nivel (1, 2 o 3)
4. Configura enemigos adicionales (opcional)
5. Configura frutas personalizadas (opcional)
6. Observa el juego automatizado

**Objetivo**: La IA juega como helado vs monstruos IA

---

## Configuración Personalizada de Frutas

### Tipos de Frutas Disponibles

| Nombre | Variantes Reconocidas | Imagen |
|--------|----------------------|--------|
| Uvas | uvas, uva, grape, grapes | Racimo de uvas |
| Plátanos | plátano, plátanos, platano, platanos, banana, bananas | Plátano amarillo |
| Cerezas | cereza, cerezas, cherry, cherries | Cereza roja |
| Piñas | piña, piñas, pina, pinas, pineapple, pineapples | Piña tropical |

### Rango de Configuración

- **Mínimo por fruta**: 1
- **Máximo por fruta**: 50

### Ejemplo de Configuración

```
Uvas: 10      → Aparecerán 10 uvas en el nivel
Plátanos: 5   → Aparecerán 5 plátanos en el nivel
Cerezas: 8    → Aparecerán 8 cerezas en el nivel
Piñas: 3      → Aparecerán 3 piñas en el nivel
```

---

## Configuración Personalizada de Enemigos

### Tipos de Enemigos Disponibles

| Nombre | Descripción |
|--------|-------------|
| Narval | Enemigo rápido con movimiento de persecución |
| Troll | Enemigo robusto |
| Pot | Enemigo pequeño y ágil |
| YellowSquid | Enemigo con movimiento especial |

### Rango de Configuración

- **Mínimo por enemigo**: 1
- **Máximo por enemigo**: 10
- **Enemigos simultáneos máximo**: 3 tipos diferentes

---

## Tests de Validación

### Ejecutar Tests de Integración

```bash
cd e:\DOPO\ProyectoFinal_Bad_Ice_Cream
javac -source 11 -target 11 -d bin -cp bin;. TestFlowIntegration.java
java -cp bin TestFlowIntegration
```

**Resultado Esperado**:
```
╔════════════════════════════════════════════════════════════════╗
║                      RESUMEN DE PRUEBAS                        ║
╠════════════════════════════════════════════════════════════════╣
║  Tests ejecutados: 4
║  Tests pasados:    4
║  Tests fallidos:   0
║  Tasa de éxito:    100,0%
╚════════════════════════════════════════════════════════════════╝

✅ ¡TODOS LOS TESTS PASARON!
```

---

## Controles del Juego

### Modo PVM / PVP Helado Cooperativo

**Jugador 1 (Helado)**:
- `W` - Mover arriba
- `A` - Mover izquierda
- `S` - Mover abajo
- `D` - Mover derecha
- `Espacio` - Crear bloque de hielo

**Jugador 2 (Segundo Helado en Cooperativo)**:
- `↑` - Mover arriba
- `←` - Mover izquierda
- `↓` - Mover abajo
- `→` - Mover derecha
- `Shift Derecho` - Crear bloque de hielo

### Interfaz

- `ESC` - Pausa el juego
- Click en botones - Navega menús

---

## Problemas y Soluciones

### Problema: Las frutas no aparecen

**Causa**: Configuración de frutas vacía (todas desmarcadas)

**Solución**: Deixa al menos una fruta seleccionada o confirma sin seleccionar nada para usar frutas predeterminadas del nivel

---

### Problema: El juego corre muy lento

**Causa**: Demasiadas frutas o enemigos configurados

**Solución**: Reduce la cantidad de frutas/enemigos personalizados

---

### Problema: Los controles no responden

**Causa**: La ventana del juego no tiene el foco

**Solución**: Click en la ventana del juego para darle el foco

---

## Características Implementadas

### ✅ Completadas

- [x] Sistema de selección de niveles (1, 2, 3)
- [x] Menú de configuración de enemigos personalizados
- [x] Menú de configuración de frutas personalizadas
- [x] Flujo correcto para todos los modos de juego
- [x] Renderizado correcto de frutas con múltiples variantes de nombres
- [x] Creación correcta de frutas plurales (Piñas, Plátanos, Uvas, Cerezas)
- [x] Modo PVP Helado vs Monstruo específico
- [x] Modo PVP Helado Cooperativo
- [x] Modo PVM (Player vs Machine)
- [x] Modo MVM (Machine vs Machine)
- [x] Animaciones de juego suave
- [x] Sistema de pausa
- [x] Menús interactivos

### 📋 Futuras Mejoras (Opcionales)

- [ ] Migración de niveles de .data a JSON
- [ ] Más niveles disponibles (4+)
- [ ] Nuevos tipos de frutas
- [ ] Nuevos enemigos
- [ ] Sistema de puntuación persistente
- [ ] Dificultades ajustables
- [ ] Efectos de sonido
- [ ] Música de fondo

---

## Estructura de Archivos

```
e:\DOPO\ProyectoFinal_Bad_Ice_Cream\
├── bin/                          # Bytecode compilado
├── Controller/
│   ├── PresentationController.java    # Orquestador principal
│   └── GameController.java            # Controlador del juego
├── Domain/                        # Lógica del juego
│   ├── Game.java
│   ├── Board.java
│   ├── IceCream.java y variantes
│   ├── Enemy.java y variantes
│   ├── Fruit.java y variantes (Grape, Banana, Cherry, Pineapple)
│   └── Level.java, LevelManager.java
├── Presentation/                  # Interfaz gráfica
│   ├── StartMenu.java
│   ├── SelectLevel.java
│   ├── GamePanel.java
│   ├── EnemyConfigurationMenu.java
│   ├── FruitConfigurationMenu.java
│   └── ImageLoader.java
├── Resources/                     # Imágenes y recursos
│   ├── Botones/
│   ├── Frutas/
│   ├── Helados/
│   ├── Monstruos/
│   └── ...
├── Test/                          # Tests unitarios
├── TestFlowIntegration.java       # Test de integración
├── README.md
└── RESUMEN_CORRECCIONES.md
```

---

## Glosario de Términos

| Término | Significado |
|---------|-------------|
| PVM | Player vs Machine (1 jugador humano vs IA) |
| PVP | Player vs Player (humano vs humano o modo específico) |
| MVM | Machine vs Machine (IA vs IA) |
| Helado | Personaje principal controlable |
| Monstruo/Enemigo | Personaje controlado por IA o humano |
| Fruta | Objetivo a recolectar para ganar puntos |
| Bloque de Hielo | Obstáculo que el helado puede crear |
| Nivel | Mapa/escenario del juego (1, 2 o 3) |

---

## Soporte y Contacto

Para reportar bugs o sugerir mejoras, consulta con el desarrollador.

---

**Última actualización**: [Fecha actual]
**Versión**: 2.0
**Estado**: ✅ Completamente Funcional
