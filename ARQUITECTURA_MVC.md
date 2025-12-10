# Arquitectura MVC - Bad Ice Cream

## Descripción General

El proyecto **Bad Ice Cream** implementa un patrón **Model-View-Controller (MVC)** para mantener una separación clara de responsabilidades entre la lógica del juego, la interfaz gráfica y el coordinador de interacciones.

```
┌─────────────────────────────────────────────────────────┐
│                      APLICACIÓN                         │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ PRESENTATION │  │  CONTROLLER  │  │   DOMAIN     │  │
│  │    (View)    │─→│  (Mediador)  │←─│   (Model)    │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
│       ↓                                      ↑           │
│    Renderiza          Procesa Inputs        Lógica      │
│    Interface          Game Loop             del juego   │
│    Gráfica            Callbacks             Reglas      │
│                                            Estados      │
└─────────────────────────────────────────────────────────┘
```

---

## 1. DOMAIN (Modelo)

**Ubicación:** `Domain/` 

**Responsabilidad:** Contiene toda la lógica y reglas del juego.

### Clases principales:

- **`Game.java`** - Controlador del modelo del juego
  - Mantiene el estado global del juego
  - Gestiona transiciones de estado (PLAYING → PAUSED → WON/LOST)
  - Coordina actualización de lógica

- **`Board.java`** - Tablero del juego
  - Matriz de celdas que representa el mapa
  - Contiene todas las entidades (helado, enemigos, frutas, bloques de hielo)
  - Gestiona colisiones y límites

- **Entidades de juego:**
  - `IceCream.java` - El jugador controlable
  - `Enemy.java` - Clase base para enemigos
  - `Fruit.java` - Frutas a recolectar
  - `IceBlock.java` - Bloques de hielo destructibles

- **IA de enemigos:**
  - `PotAI.java`, `NarvalAI.java`, `YellowSquidAI.java` - Lógica de movimiento de enemigos
  - `IceCreamAIStrategy.java` - Estrategias de IA para el helado en modo MVM

### Restricciones importantes:
- ❌ **NO DEBE importar de Presentation**
- ❌ **NO DEBE importar de Controller**
- ✅ Puede importar dentro de Domain

---

## 2. PRESENTATION (Vista)

**Ubicación:** `Presentation/`

**Responsabilidad:** Renderizar la interfaz gráfica y capturar input del usuario.

### Clases principales:

- **`GamePanel.java`** - Panel de renderizado del juego
  - Dibuja el tablero, entidades, UI
  - Maneja eventos del ratón (pausa, botones)
  - **Nota:** Importa Domain directamente porque necesita acceso de **lectura** a objetos para renderizar
  - **Nunca modifica** la lógica del juego

- **Menús:**
  - `StartMenu.java` - Pantalla inicial
  - `SelectLevel.java` - Selección de niveles
  - `SelectIceCream.java` - Selección de sabor del helado
  - `SelectIceCreamAI.java` - Selección de estrategia de IA
  - `SelectPVPMode.java` - Selección de modo PVP
  - `SelectMonster.java` - Selección de enemigo

### Restricciones:
- ❌ **NO DEBE modificar lógica del Domain**
- ✅ **Puede importar Domain** (solo para lectura)
- ✅ Importa Controller para coordinación
- ✅ Solo accede a datos a través de GameController primariamente

---

## 3. CONTROLLER (Mediador)

**Ubicación:** `Controller/`

**Responsabilidad:** Coordinar interacciones entre Model y View.

### Clases principales:

- **`GameController.java`** - Controlador del juego
  - Implementa `KeyListener` para capturar eventos del teclado
  - Ejecuta el **Game Loop** (actualiza lógica + renderiza a 60 FPS)
  - Traduce inputs del usuario en comandos al Model
  - Proporciona métodos **accesores** para que Presentation acceda a datos
  - Maneja callbacks para cambios de estado (pausa, victoria, derrota)
  - **Métodos accesores útiles:**
    - `getGame()` / `getBoard()` - Acceso directo al modelo
    - `getGameStateAsString()` - Estado como String (alternativa a enums)
    - `isGameState(String)` - Verificar estado sin imports
    - `getBoardDimensions()` - Dimensiones del tablero
    - `getFruits()`, `getWalls()`, `getEnemies()` - Listas de entidades
    - `updateAllVisualPositions()` - Actualizar posiciones de render

- **`InputHandler.java`** - Manejador de entrada
  - Captura teclas presionadas/liberadas
  - Mantiene estado de qué teclas están activas
  - Proporciona interfaz simple para consultar acciones (isMoveUp, isMoveDown, etc.)

- **`PresentationController.java`** - Coordinador de pantallas
  - Gestiona transiciones entre menús y juego
  - Instancia GameController cuando comienza una partida
  - Maneja callbacks de los menús

### Restricciones:
- ✅ **Importa Domain** (porque necesita acceder al Model)
- ✅ **Importa Presentation** (porque necesita acceder a la View)
- ✅ Es el "puente" entre capas

---

## 4. Flujo de Datos

### Inicio del juego:
```
PresentationController
    ↓
Crea GameController(gameMode, flavor, etc.)
    ↓
GameController crea:
    - Game (Model)
    - GamePanel (View)
    - InputHandler (Input)
    - Timer (Game Loop)
    ↓
Comienza Timer → Ejecuta Game Loop cada 16ms
```

### Game Loop (60 FPS):
```
1. Procesar Inputs
   InputHandler.isMoveUp/Down/Left/Right()
   ↓
2. Actualizar Lógica (Model)
   game.update()
   - Mueve jugador/enemigos
   - Detecta colisiones
   - Recolecta frutas
   - Actualiza IA
   ↓
3. Actualizar Render (View)
   gamePanel.repaint()
   - Dibuja tablero
   - Dibuja entidades
   - Dibuja UI
   ↓
4. Verificar Fin
   ¿Victoria/Derrota?
   ↓
5. Esperar ~16ms (1/60 segundo)
```

### Captura de input:
```
Teclado presionado
    ↓
InputHandler.keyPressed(KeyEvent)
    ↓
Almacena en Set<Integer>
    ↓
Game Loop consulta: inputHandler.isMoveUp()
    ↓
GameController ejecuta: game.moveIceCream(Direction.UP)
```

---

## 5. Patrón MVC Implementado

### Separación clara:

| Aspecto | Domain | Controller | Presentation |
|---------|--------|-----------|--------------|
| Lógica del juego | ✅ | ❌ | ❌ |
| Reglas/Física | ✅ | ❌ | ❌ |
| Game Loop | ❌ | ✅ | ❌ |
| Traducir inputs | ❌ | ✅ | ❌ |
| Renderizado | ❌ | ❌ | ✅ |
| Capturar eventos | ❌ | ✅ | ❌ |
| Coordinar capas | ❌ | ✅ | ❌ |
| **Modificar Estado** | ❌ | ✅ | ❌ |
| **Leer Estado (READ-ONLY)** | ✅ | ✅ | ✅ |

### Direcciones de importación permitidas:
```
Domain  → ❌ No importa nada de otras capas

Controller → ✅ Importa Domain y Presentation
            ✅ Puede MODIFICAR Domain
            ✅ Puede llamar métodos en Presentation

Presentation → ✅ Importa Controller (para todo tipo de datos)
             ✅ Puede importar Domain (SOLO para lectura)
             ❌ NUNCA puede modificar Domain
             ❌ NUNCA accede a Domain sin pasar por Controller cuando es posible
```

### Patrón "View Read-Only":
```
Presentation puede LEER de Domain para renderizar, pero NUNCA modificar.

Ejemplo válido ✅:
public void render(Graphics g) {
    IceCream iceCream = board.getIceCream();  // LEER - OK
    g.drawImage(getIceCreamSprite(iceCream.getFlavor()), ...);
}

Ejemplo INVÁLIDO ❌:
public void handleClickOnIceCream() {
    iceCream.setPosition(newPos);  // MODIFICAR - NO PERMITIDO
    iceCream.move();               // MODIFICAR - NO PERMITIDO
}
```

---

## 6. Beneficios de esta Arquitectura

✅ **Mantenibilidad:** Cambios en lógica no afectan GUI
✅ **Testability:** Puedo testear Game sin necesidad de UI
✅ **Reutilización:** Domain puede usarse en otra GUI (console, web, etc.)
✅ **Claridad:** Cada clase sabe exactamente qué hace
✅ **Escalabilidad:** Fácil agregar nuevas pantallas o enemigos
❌ **Performance:** Indirecciones y abstracciones tienen un costo mínimo

---

## 7. Notas Importantes

### ¿Por qué Presentation puede importar Domain?
En una aplicación Swing/AWT real con MVC, la View (Presentation) necesita acceso de **lectura** a objetos del Model para renderizarlos. Es prácticamente imposible hacer esto sin imports. Lo importante es que:
- ✅ Presentation **NUNCA modifica** la lógica del Model
- ✅ Presentation solo **consulta** datos para dibujar
- ✅ Domain **NUNCA conoce** sobre Presentation

### ¿Qué son los "métodos accesores" en GameController?
Son métodos que proporcionan alternativas de acceso a datos sin necesidad de importar Domain directamente en algunas clases Presentation. Por ejemplo:
```java
// Sin necesidad de importar GameState enum:
String state = controller.getGameStateAsString();

// En lugar de:
GameState state = game.getGameState();
```

## 7. Patrón "View Read-Only" (Presentación de solo lectura)

### ¿Por qué Presentation puede importar Domain?

En una aplicación Swing/AWT real con MVC, la View (Presentation) necesita acceso de **lectura** a objetos del Model para renderizarlos. Es prácticamente imposible hacer esto sin imports. 

**Lo importante es que:**
- ✅ Presentation **SOLO LEE** datos de Domain
- ❌ Presentation **NUNCA MODIFICA** la lógica del juego
- ❌ Presentation **NO TIENE** lógica de negocio
- ✅ Presentation accede a datos a través de GameController cuando es posible

### Clases Presentation que importan Domain y por qué:

1. **GamePanel** - Importa Domain para:
   - Leer posición, dirección y estado de helados
   - Leer posición de enemigos y frutas
   - Leer estado del juego (PLAYING, PAUSED, WON, LOST)
   - Leer datos de bloques de hielo y muros
   - **NUNCA modifica** ningún objeto del Domain

2. **SelectIceCreamAI** - Importa `IceCreamAIStrategyManager` para:
   - Obtener lista de estrategias de IA disponibles (solo lectura)
   - Mostrar opciones al usuario
   - **NUNCA modifica** datos

3. **SelectPVPMode** - Importa `PVPMode` enum para:
   - Obtener valores de modos PVP disponibles (solo lectura)
   - **NUNCA modifica** nada

### Ejemplo de restricción View Read-Only:

```java
// ✅ PERMITIDO - Lectura de datos para renderizar
public void drawIceCream(Graphics2D g) {
    IceCream ice = board.getIceCream();           // Lectura OK
    float x = ice.getVisualX();                   // Lectura OK
    String flavor = ice.getFlavor();              // Lectura OK
    Direction dir = ice.getCurrentDirection();    // Lectura OK
    
    Image sprite = getSprite(flavor, dir);
    g.drawImage(sprite, x, y, size, size, null);
}

// ❌ NO PERMITIDO - Modificación de lógica
public void handleUserClick() {
    iceCream.setPosition(newX, newY);     // PROHIBIDO - modificación
    iceCream.setDirection(Direction.UP);  // PROHIBIDO - modificación
    iceCream.createIce();                 // PROHIBIDO - modificación
}
```

---

## 8. Notas Importantes (Actualizado)

### ¿Por qué Presentation puede importar Domain?

En una aplicación Swing/AWT real con MVC, la View (Presentation) necesita acceso de **lectura** a objetos del Model para renderizarlos. Es prácticamente imposible hacer esto sin imports. Lo importante es que:
- ✅ Presentation **NUNCA modifica** la lógica del Model
- ✅ Presentation solo **consulta** datos para dibujar
- ✅ Domain **NUNCA conoce** sobre Presentation
- ✅ Controller es el único que puede **modificar** Domain

La arquitectura MVC del proyecto **Bad Ice Cream** mantiene una separación clara entre:
- **Model:** Lógica pura del juego
- **View:** Renderizado e interfaz
- **Controller:** Coordinación y game loop

Esta estructura hace el código más mantenible, testeable y escalable.
