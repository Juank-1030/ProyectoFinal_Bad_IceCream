# 🎮 BAD DOPO-CREAM - CAPA DE DOMINIO

## 📁 ESTRUCTURA PLANA (SIN SUBPAQUETES)

Todos los archivos están en `Domain/` directamente - **34 archivos Java**

```
Domain/
├── Direction.java                  ← Enum (UP, DOWN, LEFT, RIGHT)
├── Position.java                   ← Posiciones en la matriz (x, y)
├── GameObject.java                 ← Clase abstracta base
├── IceBlock.java                   ← Bloques de hielo
│
├── IceCream.java                   ← Clase abstracta helados
├── VanillaIceCream.java
├── StrawberryIceCream.java
├── ChocolateIceCream.java
│
├── Enemy.java                      ← Clase abstracta enemigos
├── Troll.java
├── Pot.java
├── OrangeSquid.java
│
├── Fruit.java                      ← Clase abstracta frutas
├── Grape.java
├── Banana.java
├── Pineapple.java
├── Cherry.java
│
├── MovementBehavior.java           ← Interfaces y comportamientos
├── PatternMovement.java
├── ChaseMovement.java
├── FruitBehavior.java
├── StaticFruitBehavior.java
├── MovingFruitBehavior.java
├── TeleportFruitBehavior.java
│
├── Board.java                      ← Lógica del juego
├── Game.java
├── Level.java
├── GameMode.java
├── GameState.java
├── RecursosNivel.java             ← ⭐ NUEVO
│
├── AI.java                         ← Inteligencia Artificial
├── EnemyAI.java
├── IceCreamAI.java
│
└── GameException.java              ← Excepciones
```

## 🆕 RecursosNivel - Carga niveles desde archivos binarios

```java
// Guardar nivel
RecursosNivel.guardarNivel(nivel, "nivel_1");

// Cargar nivel
Level nivel = RecursosNivel.cargarNivel("nivel_1");

// Cargar por número (automático)
Level nivel = RecursosNivel.cargarNivelPorNumero(1);

// Crear niveles predefinidos
RecursosNivel.crearNivelesPredefinidos();
```

## 📦 Imports Simplificados

Todo en el mismo paquete = NO necesitas imports entre clases de Domain

```java
package Domain;

import java.util.List;  // Solo externos

public class Game {
    private Board board;      // ✅ Sin import
    private IceCream ice;     // ✅ Sin import
}
```

## 🚀 Compilación

```bash
javac Domain/*.java
```

¡Listo para conectar con Presentation!
