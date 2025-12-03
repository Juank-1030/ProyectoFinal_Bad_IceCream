package Test;

import Presentation.ImageLoader;
import java.awt.Image;

/**
 * Test to verify all sprite loading functionality works correctly
 */
public class TestSpriteLoading {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║           SPRITE LOADING VERIFICATION TEST                     ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
        
        int passed = 0;
        int failed = 0;
        
        // Load all images
        System.out.println(">>> Loading all sprites...");
        ImageLoader.loadAllImages();
        System.out.println();
        
        // Test map background
        System.out.println(">>> Testing Map Background:");
        if (testImage(ImageLoader.getMapBackground(), "Map Background")) {
            passed++;
        } else {
            failed++;
        }
        
        // Test ice cream sprites
        System.out.println("\n>>> Testing Ice Cream Sprites:");
        String[] flavors = {"chocolate", "strawberry", "vainillia"};
        for (String flavor : flavors) {
            if (testImage(ImageLoader.getIceCreamSprite(flavor, "stand", "down"), 
                         "Ice Cream - " + flavor + " StandDown")) {
                passed++;
            } else {
                failed++;
            }
        }
        
        // Test monster sprites
        System.out.println("\n>>> Testing Monster Sprites:");
        String[] monsters = {"troll", "pot", "yellowsquid", "narval"};
        for (String monster : monsters) {
            if (testImage(ImageLoader.getMonsterSprite(monster, "walk", "down"), 
                         "Monster - " + monster + " DownWalk")) {
                passed++;
            } else {
                failed++;
            }
        }
        
        // Test fruit sprites
        System.out.println("\n>>> Testing Fruit Sprites:");
        String[] fruits = {"grapes", "banana", "cherry", "pineapple", "cactus"};
        for (String fruit : fruits) {
            if (testImage(ImageLoader.getFruitSprite(fruit, "normal"), 
                         "Fruit - " + fruit + " Normal")) {
                passed++;
            } else {
                failed++;
            }
        }
        
        // Test ice block sprite
        System.out.println("\n>>> Testing Ice Block Sprite:");
        if (testImage(ImageLoader.getIceBlockSprite("static"), "Ice Block Static")) {
            passed++;
        } else {
            failed++;
        }
        
        // Summary
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                        TEST SUMMARY                            ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║ Tests Passed: " + String.format("%-49d", passed) + "║");
        System.out.println("║ Tests Failed: " + String.format("%-49d", failed) + "║");
        System.out.println("║ Total Tests:  " + String.format("%-49d", passed + failed) + "║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        
        if (failed == 0) {
            System.out.println("\n✅ ALL SPRITE LOADING TESTS PASSED!");
        } else {
            System.out.println("\n❌ SOME TESTS FAILED - Check sprite files in Resources/");
        }
    }
    
    private static boolean testImage(Image image, String description) {
        if (image != null) {
            System.out.println("  ✓ " + description + " - LOADED");
            return true;
        } else {
            System.out.println("  ✗ " + description + " - FAILED");
            return false;
        }
    }
}
