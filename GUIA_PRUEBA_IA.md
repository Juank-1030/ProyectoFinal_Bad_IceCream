# GUÍA DE PRUEBA - Sistema de IA con Reachability Validation

## 🎮 Objetivo de las Pruebas
Verificar que las tres estrategias de IA (Hungry, Fearful, Expert) funcionan correctamente sin entrar en ciclos infinitos cuando se encuentran frutas inaccesibles.

---

## ✅ Prueba 1: IA Hungry - Búsqueda de Frutas Accesibles

### Configuración
1. Inicia el juego
2. Selecciona: **Modo MVM** (Monster vs Monster)
3. Selecciona: Cualquier **Helado**
4. Selecciona IA: **Hungry**
5. Selecciona: **Cualquier Nivel**
6. Selecciona: **Frutas** (cualquier configuración)
7. Selecciona: **Monstruos** (cualquier configuración)

### Comportamiento Esperado
✅ El helado debe:
- [ ] Moverse activamente hacia las frutas más cercanas
- [ ] **NO entrar en ciclo infinito** cuando encuentra fruta rodeada de bloques
- [ ] Saltar a la siguiente fruta reachable si la primera es inaccesible
- [ ] Recolectar todas las frutas accesibles del nivel
- [ ] Poder completar el nivel (pasar a siguiente nivel)

### Indicadores de Éxito
```
✅ BIEN:
  - Helado se mueve fluidamente hacia frutas
  - Mueve pantalla sin atorarse
  - Recolecta frutas que puede alcanzar
  - Evita frutas rodeadas de bloques
  
❌ PROBLEMA (Si ves esto):
  - Helado se queda quieto por más de 2 segundos
  - Helado se mueve repetidamente en mismo lugar
  - Helado no hace progreso hacia frutas
```

### Tiempo Estimado
⏱️ 30 segundos por nivel × 3 niveles = ~1.5 minutos

---

## ✅ Prueba 2: IA Fearful - Escape de Enemigos

### Configuración
1. Inicia el juego
2. Selecciona: **Modo MVM**
3. Selecciona: Cualquier **Helado**
4. Selecciona IA: **Fearful**
5. Selecciona: **Nivel 1 o 2**
6. Selecciona: **Frutas** (cualquier configuración)
7. Selecciona: **Monstruos** (preferiblemente 2-3 para ver evasión)

### Comportamiento Esperado
✅ El helado debe:
- [ ] Moverse LEJOS de los monstruos cuando estén cerca (< 5 celdas)
- [ ] **NO entrar en ciclo infinito** intentando escapar
- [ ] Cuando no hay peligro, explorar y buscar frutas
- [ ] Mantener distancia segura de enemigos
- [ ] Moverse de forma fluida sin quedarse atrapado

### Indicadores de Éxito
```
✅ BIEN:
  - Helado se aleja cuando monstruos se acercan
  - Se mueve con propósito, no al azar
  - Evita corredores cerrados
  - Cuando pasa el peligro, sigue explorando
  
❌ PROBLEMA (Si ves esto):
  - Helado se queda quieto en esquina
  - Monstruo lo atrapa después de 5+ segundos
  - Helado hace movimientos sin sentido
  - Helado no escapa cuando puede hacerlo
```

### Tiempo Estimado
⏱️ 1 minuto por nivel × 2 niveles = ~2 minutos

---

## ✅ Prueba 3: IA Expert - Balance Frutas + Enemigos

### Configuración
1. Inicia el juego
2. Selecciona: **Modo MVM**
3. Selecciona: Cualquier **Helado**
4. Selecciona IA: **Expert**
5. Selecciona: **Cualquier Nivel**
6. Selecciona: **Frutas** (configuración normal)
7. Selecciona: **Monstruos** (2-3 enemigos para ver balance)

### Comportamiento Esperado
✅ El helado debe:
- [ ] Recolectar frutas cercanas y accesibles
- [ ] Evitar enemigos que están demasiado cerca
- [ ] Buscar frutas lejanas cuando es seguro
- [ ] **NO entrar en ciclo infinito** en ningún escenario
- [ ] Completar el nivel recogiendo todas las frutas posibles
- [ ] Mostrarse más inteligente que Hungry (ve peligro) pero menos pasivo que Fearful

### Indicadores de Éxito
```
✅ BIEN:
  - Helado persigue frutas cercanas
  - Se detiene/desvía cuando enemigos se acercan
  - Regresa a buscar frutas cuando pasa el peligro
  - Completa nivel en tiempo razonable
  - Movimientos tienen lógica evidente
  
❌ PROBLEMA (Si ves esto):
  - Helado solo corre de enemigos (ignorando frutas)
  - Helado solo busca frutas (sintiéndose indefenso)
  - Se queda atrapado/en ciclo
  - No completa el nivel
```

### Tiempo Estimado
⏱️ 2 minutos por nivel × 3 niveles = ~6 minutos

---

## 🔍 Verificación de Ciclos - Lo MÁS IMPORTANTE

### ¿Cómo Detectar un Ciclo Infinito?
```
Observa durante 5 segundos si:

❌ CICLO INFINITO:
   El helado repite exactamente el MISMO movimiento o conjunto de movimientos
   - Izquierda, Izquierda, Izquierda...
   - Arriba, Derecha, Arriba, Derecha...
   - Quieto, Quieto, Quieto...
   
✅ COMPORTAMIENTO NORMAL:
   El helado hace progreso hacia objetivos
   - Se mueve explorando el tablero
   - Cambios de dirección que tienen sentido
   - Recolecta frutas o se aleja de enemigos
```

### Si Observas un Ciclo
1. 📸 Toma una captura de pantalla
2. 📝 Anota:
   - Cuál IA (Hungry/Fearful/Expert)
   - Dónde está el helado
   - Qué frutas/enemigos hay cerca
   - Qué movimiento repite
3. 🐛 Reporta los detalles

---

## 📊 Matriz de Pruebas

| IA | Nivel | Frutas | Monstruos | Resultado | Nota |
|-------|-------|--------|-----------|-----------|------|
| Hungry | 1 | Std | 2 | ✅/❌ | |
| Hungry | 2 | Std | 2 | ✅/❌ | |
| Hungry | 3 | Std | 3 | ✅/❌ | |
| Fearful | 1 | Std | 2 | ✅/❌ | |
| Fearful | 2 | Std | 3 | ✅/❌ | |
| Expert | 1 | Std | 2 | ✅/❌ | |
| Expert | 2 | Std | 3 | ✅/❌ | |
| Expert | 3 | Std | 3 | ✅/❌ | |

---

## 🚀 Flujo de Prueba Completo

### Paso a Paso (Total: ~15 minutos)

```
1. ARRANQUE [1 min]
   └─ Inicia el juego
   └─ Verifica que los tres IAs aparecen en menú SelectIceCreamAI
   
2. HUNGRY TEST [4 min]
   ├─ Prueba 1 ciclo corto: Level 1 + Hungry
   ├─ Prueba 2 ciclo medio: Level 2 + Hungry  
   └─ Prueba 3 ciclo largo: Level 3 + Hungry
   
3. FEARFUL TEST [3 min]
   ├─ Prueba 1: Level 1 + Fearful (2 monstruos)
   └─ Prueba 2: Level 2 + Fearful (3 monstruos)
   
4. EXPERT TEST [5 min]
   ├─ Prueba 1: Level 1 + Expert (balance)
   ├─ Prueba 2: Level 2 + Expert (balance)
   └─ Prueba 3: Level 3 + Expert (complejidad max)
   
5. DOCUMENTACIÓN [2 min]
   └─ Anota resultados en tabla
```

---

## 💾 Validación Post-Prueba

Después de completar las pruebas:

1. ✅ Todas las IAs deben jugar sin ciclos
2. ✅ Cada IA debe mostrar comportamiento diferente:
   - **Hungry**: Agresivamente hacia frutas
   - **Fearful**: Cauteloso, evitando enemigos
   - **Expert**: Balance entre ambos
3. ✅ El juego debe ser completable en todos los niveles
4. ✅ No debe haber excepciones en la consola

---

## 📝 Notas de Implementación

### Cambios Aplicados
- ✅ HungryAIStrategy: Pre-validación de frutas reachable
- ✅ FearfulAIStrategy: Exploración consistente sin ciclos
- ✅ ExpertAIStrategy: Validación en ambas fases de búsqueda

### Mecanismo de Validación
```java
// Antes de comprometerse a una fruta:
Direction testDir = getDirectionTowards(from, fruit, board);
if (testDir != null) {
    // Fruta es alcanzable → OK perseguirla
} else {
    // Fruta inaccesible → SKIP, buscar otra
}
```

### Compilación
✅ Compilado exitosamente
✅ 0 errores de sintaxis
✅ Todas las referencias de métodos existen
✅ Classpath correcto

---

## 🎯 Criterio de Aceptación

✅ **PRUEBAS EXITOSAS SI:**
- [ ] Ningún IA entra en ciclo infinito en ningún nivel
- [ ] Hungry recolecta frutas accesibles eficientemente
- [ ] Fearful evade enemigos sin quedar atrapado
- [ ] Expert balancea ambas tareas apropiadamente
- [ ] El juego es completable sin excepciones

❌ **PRUEBAS FALLIDAS SI:**
- [ ] Cualquier IA se queda en ciclo > 3 segundos
- [ ] El juego lanza excepciones
- [ ] Un IA ignora su objetivo completamente
- [ ] No se puede completar un nivel

---

## 📞 Soporte

Si encuentras problemas:
1. Verifica que la compilación no tiene errores
2. Revisa que los .class están en `bin/` recientemente
3. Limpia caché: `del /s /q bin\*.class` + recompila
4. Documenta: IA, nivel, configuración exacta, comportamiento

---

**Última Actualización**: 2024-11-27
**Versión de Prueba**: 1.0
**Estado**: Listo para Pruebas Manuales
