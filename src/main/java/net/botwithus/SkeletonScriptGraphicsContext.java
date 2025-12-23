package net.botwithus;

import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;

public class SkeletonScriptGraphicsContext extends ScriptGraphicsContext {

    private SkeletonScript script;

    public SkeletonScriptGraphicsContext(ScriptConsole scriptConsole, SkeletonScript script) {
        super(scriptConsole);
        this.script = script;
    }

    @Override
    public void drawSettings() {
        if (ImGui.Begin("My script", ImGuiWindowFlag.None.getValue())) {
            if (ImGui.BeginTabBar("My bar", ImGuiWindowFlag.None.getValue())) {
                if (ImGui.BeginTabItem("Settings", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Welcome to my script!");
                    ImGui.Text("My scripts state is: " + script.getBotState());
                    ImGui.Separator();
                    
                    ImGui.Text("Set Bot State:");
                    if (ImGui.Button("IDLE")) {
                        script.setBotState(SkeletonScript.BotState.IDLE);
                    }
                    ImGui.SameLine();
                    if (ImGui.Button("SKILLING")) {
                        script.setBotState(SkeletonScript.BotState.SKILLING);
                    }
                    ImGui.SameLine();
                    if (ImGui.Button("BANKING")) {
                        script.setBotState(SkeletonScript.BotState.BANKING);
                    }
                    
                    ImGui.EndTabItem();
                }
                if (ImGui.BeginTabItem("Rotation", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Ability Bar Scanner");
                    ImGui.Separator();
                    
                    if (ImGui.Button("Scan Action Bar")) {
                        script.scanActionBar();
                    }
                    
                    ImGui.Text("Scans your action bar and caches ability positions.");
                    ImGui.Text("This reduces queries and prevents crashes.");
                    ImGui.Text("Run this once after setting up your action bar.");
                    

                    ImGui.Text("Cached Abilities: " + script.getCachedAbilityCount());
                    

                    if (ImGui.Button("Show Cached Slots")) {
                        script.printCachedSlots();
                    }
                    
                    ImGui.Separator();
                    ImGui.Text("Rotation Options");
                    script.setUseVulnBombs(ImGui.Checkbox("Use vuln bombs?", script.isUseVulnBombs()));
                    script.setUseDeathMark(ImGui.Checkbox("Use Death Mark?", script.isUseDeathMark()));
                    script.setUseAdrenalineRenewal(ImGui.Checkbox("Drink Adrenaline Renewal?", script.isUseAdrenalineRenewal()));
                    script.setUseSplitSoul(ImGui.Checkbox("Use Split Soul?", script.isUseSplitSoul()));
                    script.setUseLivingDeath(ImGui.Checkbox("Use Living Death?", script.isUseLivingDeath()));
                    script.setUseEssenceOfFinality(ImGui.Checkbox("Use Essence of Finality?", script.isUseEssenceOfFinality()));
                    script.setUseWeaponSpecial(ImGui.Checkbox("Use Weapon Special?", script.isUseWeaponSpecial()));
                    
                    ImGui.EndTabItem();
                }
                if (ImGui.BeginTabItem("Other", ImGuiWindowFlag.None.getValue())) {
                    script.setSomeBool(ImGui.Checkbox("Are you cool?", script.isSomeBool()));
                    ImGui.EndTabItem();
                }
                ImGui.EndTabBar();
            }
            ImGui.End();
        }
        
        // Separate Cooldown Tracking Window
        drawCooldownTracker();
    }
    
    private void drawCooldownTracker() {
        if (ImGui.Begin("Ability Cooldown Tracker", ImGuiWindowFlag.None.getValue())) {
            ImGui.Text("Real-time Ability Tracking");
            ImGui.Separator();
            
            // Get rotation manager from script
            if (script.getRotation() != null) {
                RotationManager rotation = script.getRotation();
                
                // Current and Previous Ability
                ImGui.Text("Current Ability: " + rotation.getLastAbilityUsed());
                ImGui.Text("Previous Ability: " + rotation.getPreviousAbilityUsed());
                ImGui.Text("Sequence: " + rotation.getRotationSequenceInfo());
                ImGui.Separator();
                
                // Key Necromancy Abilities with Cooldowns
                String[] trackedAbilities = {
                    "Death Skulls", "Split Soul", "Living Death", "Touch of Death",
                    "Bloat", "Weapon Special Attack", "Essence of Finality",
                    "Conjure Undead Army", "Life Transfer", "Command Skeleton Warrior",
                    "Soul Sap"
                };
                
                ImGui.Text("Ability Cooldowns:");
                ImGui.Columns(3, "CooldownColumns", true);
                ImGui.Text("Ability");
                ImGui.NextColumn();
                ImGui.Text("Cooldown");
                ImGui.NextColumn();
                ImGui.Text("Status");
                ImGui.NextColumn();
                ImGui.Separator();
                
                for (String ability : trackedAbilities) {
                    int cooldown = rotation.getPublicAbilityCooldown(ability);
                    boolean ready = cooldown <= 1;
                    
                    // Ability name
                    ImGui.Text(ability);
                    ImGui.NextColumn();
                    
                    // Cooldown
                    if (cooldown > 0) {
                        ImGui.Text(cooldown + " ticks");
                    } else {
                        ImGui.Text("Ready");
                    }
                    ImGui.NextColumn();
                    
                    // Status
                    if (ready) {
                        ImGui.Text("✓ READY");
                    } else {
                        ImGui.Text("⏱ ON CD");
                    }
                    ImGui.NextColumn();
                }
                
                ImGui.Columns(1);
                ImGui.Separator();
                
                // Recent ability usage log
                ImGui.Text("Recent Usage:");
                if (script.getRecentAbilityLog() != null) {
                    String[] recentLog = script.getRecentAbilityLog();
                    for (String logEntry : recentLog) {
                        if (logEntry != null && !logEntry.isEmpty()) {
                            ImGui.Text(logEntry);
                        }
                    }
                }
                
            } else {
                ImGui.Text("Rotation Manager not initialized");
            }
            
            ImGui.End();
        }
    }

    @Override
    public void drawOverlay() {
        super.drawOverlay();
    }
}
