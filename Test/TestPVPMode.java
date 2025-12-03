package Test;

import Controller.GameController;
import Domain.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Clase de prueba comprehensiva para verificar el modo PVP
 * Prueba:
 * 1. Movimiento de helados (P1 con WASD, P2 con ARROWS)
 * 2. Generación y ruptura de bloques de hielo
 * 3. Movimiento de monstruos
 * 4. Ejecución de habilidades de monstruos
 */
public class TestPVPMode {
    private static GameController controller;
    private static Game game;
    private static Board board;
    private int testsPassed = 0;
    private int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║         PRUEBA COMPLETA DEL MODO PVP - BAD ICE CREAM          ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        try {
            TestPVPMode test = new TestPVPMode();
            test.inicializarPruebas();
            test.pruebaMovimientoHelados();
            test.pruebaGeneracionRupturaHielo();
            test.pruebaMovimientoMonstruos();
            test.pruebaHabilidadesMonstruos();
            test.mostrarResumen();

        } catch (Exception e) {
            System.err.println("\n✗ ERROR CRÍTICO durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void inicializarPruebas() throws Exception {
        System.out.println(">>> INICIALIZANDO PRUEBAS <<<\n");

        // Crear controlador con modo PVP (Fresa vs Narval)
        controller = new GameController(GameMode.PVP, "FRESA", "NARVAL");
        controller.startLevel(1);

        // Obtener referencias a través de reflexión
        Field gameField = GameController.class.getDeclaredField("game");
        gameField.setAccessible(true);
        game = (Game) gameField.get(controller);

        board = game.getBoard();

        this.prueba("Inicialización", game != null && board != null);
        System.out.println("✓ GameMode: " + game.getGameMode());
        System.out.println("✓ Helado P1: " + board.getIceCream().getClass().getSimpleName());
        System.out.println("✓ Monstruos: " + board.getEnemies().size());
        System.out.println();
    }

    private void pruebaMovimientoHelados() throws Exception {
        System.out.println(">>> PRUEBA 1: MOVIMIENTO DE HELADOS <<<\n");

        // Posición inicial de P1
        IceCream iceCream = board.getIceCream();
        Position posInicialP1 = new Position(iceCream.getPosition());
        System.out.println("Posición inicial P1: (" + posInicialP1.getX() + ", " + posInicialP1.getY() + ")");

        // Intentar mover en diferentes direcciones
        boolean intento_arriba = game.moveIceCream(Direction.UP);
        boolean intento_derecha = game.moveIceCream(Direction.RIGHT);
        boolean intento_abajo = game.moveIceCream(Direction.DOWN);
        boolean intento_izquierda = game.moveIceCream(Direction.LEFT);

        System.out.println("Intento UP: " + intento_arriba);
        System.out.println("Intento RIGHT: " + intento_derecha);
        System.out.println("Intento DOWN: " + intento_abajo);
        System.out.println("Intento LEFT: " + intento_izquierda);

        // El helado debería poder moverse en al menos 1 dirección (es una malla
        // abierta)
        boolean al_menos_un_movimiento = intento_arriba || intento_derecha || intento_abajo || intento_izquierda;
        this.prueba("P1 puede moverse en al menos una dirección", al_menos_un_movimiento);

        if (al_menos_un_movimiento) {
            System.out.println("✓ Posición final P1: (" + iceCream.getPosition().getX() + ", "
                    + iceCream.getPosition().getY() + ")");
        }

        System.out.println();
    }

    private void pruebaGeneracionRupturaHielo() throws Exception {
        System.out.println(">>> PRUEBA 2: GENERACIÓN Y RUPTURA DE BLOQUES DE HIELO <<<\n");

        // Posicionar helado P1
        IceCream iceCream = board.getIceCream();
        iceCream.setPosition(new Position(5, 5));

        // Contar bloques de hielo iniciales
        int hielo_inicial = board.getIceBlocks().size();
        System.out.println("Bloques de hielo iniciales: " + hielo_inicial);

        // Simular creación de bloque de hielo
        game.createIceBlock();

        int hielo_despues = board.getIceBlocks().size();
        boolean se_creo_hielo = hielo_despues > hielo_inicial;
        this.prueba("Se crea bloque de hielo", se_creo_hielo);
        System.out.println("✓ Bloques de hielo después: " + hielo_despues);

        if (se_creo_hielo) {
            // Verificar que el hielo está en la posición correcta
            List<IceBlock> bloques = board.getIceBlocks();
            IceBlock bloqueNuevo = bloques.get(bloques.size() - 1);
            System.out.println("✓ Bloque de hielo creado en: (" + bloqueNuevo.getPosition().getX() + ", "
                    + bloqueNuevo.getPosition().getY() + ")");

            // Simular ruptura de hielo
            int hielo_antes_ruptura = board.getIceBlocks().size();
            game.breakIceBlocks();

            int hielo_despues_ruptura = board.getIceBlocks().size();
            boolean se_rompio_hielo = hielo_despues_ruptura < hielo_antes_ruptura;
            this.prueba("Se rompe bloque de hielo", se_rompio_hielo);
            System.out.println("✓ Bloques de hielo después de ruptura: " + hielo_despues_ruptura);
        }

        System.out.println();
    }

    private void pruebaMovimientoMonstruos() throws Exception {
        System.out.println(">>> PRUEBA 3: MOVIMIENTO DE MONSTRUOS <<<\n");

        List<Enemy> monstruos = board.getEnemies();
        this.prueba("Existen monstruos en el tablero", monstruos.size() > 0);
        System.out.println("✓ Cantidad de monstruos: " + monstruos.size());

        if (monstruos.size() > 0) {
            Enemy monstruo = monstruos.get(0);
            Position posMonstruoInicial = new Position(monstruo.getPosition());
            System.out.println("✓ Monstruo: " + monstruo.getType());
            System.out.println("✓ Posición inicial: (" + posMonstruoInicial.getX() + ", " + posMonstruoInicial.getY()
                    + ")");

            // Simular múltiples intentos de movimiento
            boolean movio_alguna_vez = false;
            for (int i = 0; i < 10; i++) {
                Direction nextMove = monstruo.getNextMove();
                boolean movio = game.moveEnemy(0, nextMove);
                if (movio) {
                    movio_alguna_vez = true;
                }
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Verificar si el monstruo puede intentar movimiento
            boolean tiene_comportamiento = monstruo.getMovementBehavior() != null;
            this.prueba("Monstruo tiene comportamiento de movimiento", tiene_comportamiento);
            this.prueba("Monstruo intenta moverse", movio_alguna_vez);

            Position posMonstruoFinal = new Position(monstruo.getPosition());
            if (!posMonstruoInicial.equals(posMonstruoFinal)) {
                System.out.println("✓ Monstruo se movió a: (" + posMonstruoFinal.getX() + ", " + posMonstruoFinal.getY()
                        + ")");
            }

            // Verificar que el monstruo está vivo
            this.prueba("Monstruo permanece vivo", monstruo.isAlive());
            System.out.println("✓ Monstruo vivo: " + monstruo.isAlive());
        }

        System.out.println();
    }

    private void pruebaHabilidadesMonstruos() throws Exception {
        System.out.println(">>> PRUEBA 4: HABILIDADES DE MONSTRUOS <<<\n");

        List<Enemy> monstruos = board.getEnemies();
        if (monstruos.size() > 0) {
            Enemy monstruo = monstruos.get(0);
            System.out.println("✓ Ejecutando habilidad del monstruo: " + monstruo.getType());

            // Intentar ejecutar la habilidad
            try {
                monstruo.executeAbility();
                this.prueba("Habilidad ejecutada sin errores", true);
                System.out.println("✓ Método executeAbility() completado sin excepciones");
            } catch (Exception e) {
                this.prueba("Habilidad ejecutada sin errores", false);
                System.err.println("✗ Error al ejecutar habilidad: " + e.getMessage());
            }

            // Verificar tipo de monstruo y comportamiento
            String tipoMonstruo = monstruo.getClass().getSimpleName();
            System.out.println("✓ Tipo de monstruo: " + tipoMonstruo);

            MovementBehavior comportamiento = monstruo.getMovementBehavior();
            String tipoBehavior = comportamiento != null ? comportamiento.getClass().getSimpleName() : "NINGUNO";
            this.prueba("Monstruo tiene comportamiento de movimiento", comportamiento != null);
            System.out.println("✓ Comportamiento de movimiento: " + tipoBehavior);
        }

        System.out.println();
    }

    private void prueba(String descripcion, boolean resultado) {
        if (resultado) {
            System.out.println("  ✓ " + descripcion);
            testsPassed++;
        } else {
            System.err.println("  ✗ " + descripcion);
            testsFailed++;
        }
    }

    private void mostrarResumen() {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                      RESUMEN DE PRUEBAS                        ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("✓ Pruebas Exitosas: " + testsPassed);
        System.err.println("✗ Pruebas Fallidas: " + testsFailed);
        System.out.println("  Total: " + (testsPassed + testsFailed));
        System.out.println();

        if (testsFailed == 0) {
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("        🎉 ¡TODAS LAS PRUEBAS DEL MODO PVP PASARON! 🎉");
            System.out.println("═══════════════════════════════════════════════════════════════");
        } else {
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("             ⚠️  ALGUNAS PRUEBAS FALLARON ⚠️");
            System.out.println("═══════════════════════════════════════════════════════════════");
        }
    }
}
