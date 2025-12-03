package net.botwithus;

import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.events.impl.ServerTickedEvent;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;

/**
 * Example script showing minimal setup required to use RotationManager
 * 
 * This is a bare-bones example - copy this and customize for your needs!
 */
public class SkeletonScriptExample extends LoopingScript {

    private RotationManager rotation;
    private int serverTicks = 0;
    private int lastAbilityServerTick = 0;

    public SkeletonScriptExample(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
        
        // Initialize rotation manager
        rotation = new RotationManager("Necromancy Rotation", true); // true = spend adrenaline on ultimates
        rotation.setDebug(true); // Enable debug logging
        rotation.setLogger(this::println); // Use script's println for logging
        
        // Subscribe to server tick events (required for rotation timing)
        subscribe(ServerTickedEvent.class, event -> {
            try {
                serverTicks = event.getTicks();
                rotation.setServerTick(serverTicks); // Keep rotation synced with server ticks
                
                // Execute rotation if script is active
                if (isActive()) {
                    executeRotation();
                }
            } catch (Exception e) {
                println("[ERROR] Exception in server tick handler: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Execute the rotation - called every server tick
     */
    private void executeRotation() {
        // Check if player has a target
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null || player.getTarget() == null) {
            return; // No target, don't execute rotation
        }
        
        // Execute every 3 server ticks (1.8 seconds = global cooldown)
        if (serverTicks - lastAbilityServerTick >= 3) {
            if (rotation.execute()) {
                lastAbilityServerTick = serverTicks;
                String ability = rotation.getLastAbilityUsed();
                println("Tick " + serverTicks + " - Using: " + ability);
            }
        }
    }

    @Override
    public void onLoop() {
        // Your main script loop here
        // The rotation runs automatically via server tick events
        
        // Example: Just wait
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
