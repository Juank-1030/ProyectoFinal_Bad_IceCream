# 📋 PASOS PARA SUBIR CAMBIOS A GITHUB

## Para: Mi Colaborador
## Objetivo: Que yo pueda analizar tus cambios e integrarlos al proyecto principal

---

## ✅ PASO 1: Verificar Git

Abre **PowerShell** o **CMD** y ejecuta:

```bash
git --version
```

**Resultado esperado:** Debe mostrar versión (ej: `git version 2.40.0`)

Si no funciona, instala Git desde: https://git-scm.com

---

## ✅ PASO 2: Ir a tu proyecto

Navega a la carpeta de tu proyecto:

```bash
cd C:\ruta\a\tu\ProyectoFinal_Bad_Ice_Cream
# Reemplaza con tu ruta real
```

---

## ✅ PASO 3: Verificar tu estado

```bash
git status
```

**Debe mostrar algo como:**
```
On branch main
nothing to commit, working tree clean
```

O si tienes cambios sin guardar:
```
On branch main
Changes not staged for commit:
  modified: archivo.java
```

---

## ✅ PASO 4: Asegurar que tienes los últimos cambios

```bash
git pull origin main
```

Esto trae cualquier cambio remoto que no tengas localmente.

---

## ✅ PASO 5: Crear una rama NUEVA con tus cambios

```bash
git branch cambios_colaborador
```

**Nota:** Esto NO borra nada, solo crea una rama paralela.

---

## ✅ PASO 6: Cambiar a esa rama

```bash
git checkout cambios_colaborador
```

Ahora estás en la rama `cambios_colaborador`.

---

## ✅ PASO 7: Subir la rama a GitHub

```bash
git push origin cambios_colaborador
```

**Si pide credenciales:**
- **Usuario:** Tu usuario de GitHub
- **Contraseña:** Tu token personal o contraseña

---

## ✅ PASO 8: Confirmar que está en GitHub

Ve a: https://github.com/[TU_USUARIO]/ProyectoFinal_Bad_IceCream

Deberías ver tu rama `cambios_colaborador` en el dropdown de ramas.

---

## ✅ PASO 9: Me envías esta información

Envíame por **WhatsApp, Email o Discord:**

```
═══════════════════════════════════════════
📌 URL de tu repositorio:
https://github.com/[TU_USUARIO]/ProyectoFinal_Bad_IceCream.git

📌 Nombre de la rama:
cambios_colaborador

📌 Descripción de cambios:
- Qué archivos modificaste
- Qué nuevas clases creaste
- Qué cambios de jugabilidad implementaste
- Otros detalles importantes

═══════════════════════════════════════════
```

---

## 🚀 COMANDO RÁPIDO (Copia y pega)

Si quieres todo de una vez:

```bash
git status
git branch cambios_colaborador
git checkout cambios_colaborador
git push origin cambios_colaborador
```

---

## ❌ SOLUCIÓN DE PROBLEMAS

### Problema: "fatal: 'origin' does not exist"

```bash
# Verifica tu configuración remota:
git remote -v

# Si está vacío, agrega tu repo:
git remote add origin https://github.com/[TU_USUARIO]/ProyectoFinal_Bad_IceCream.git
git push origin cambios_colaborador
```

### Problema: "Permission denied (publickey)"

- Si usas 2FA en GitHub: Usa **token personal** en lugar de contraseña
- Ve a: GitHub → Settings → Developer Settings → Personal Access Tokens → Generate new token

### Problema: "Branch already exists"

```bash
# Si la rama ya existe, cambia el nombre:
git branch cambios_colaborador_v2
git checkout cambios_colaborador_v2
git push origin cambios_colaborador_v2
```

---

## ✨ ¿LISTO?

Una vez hayas subido la rama y me envíes la información:

1. ✅ Yo clono tu rama
2. ✅ Analizo todos tus cambios
3. ✅ Identifico conflictos
4. ✅ Integro lo que funcione con el proyecto principal
5. ✅ Te muestro el resultado

---

## 📝 NOTAS IMPORTANTES

- ⚠️ **NO** modifiques archivos mientras subes la rama
- ⚠️ Asegúrate de que todos tus cambios estén **commiteados** (`git status` debe mostrar "working tree clean")
- ⚠️ Si tienes cambios sin guardar, hazlo con:
  ```bash
  git add .
  git commit -m "Descripción de cambios"
  ```

---

## 🎯 CONTACTO

Cuando hayas completado estos pasos y subido la rama:

**Envía esto:**
- 🔗 URL de tu repo
- 📝 Nombre de la rama: `cambios_colaborador`
- 📋 Descripción detallada de cambios

¡Gracias! 🚀
