# RotationManager Guide

A comprehensive Necromancy combat rotation system for RuneScape 3 with manual cooldown tracking, action bar caching, and crash prevention.

## Features

- ✅ **Manual Cooldown Tracking** - No component queries, prevents crashes
- ✅ **Action Bar Caching** - Scan once, use forever
- ✅ **Living Death Rotation** - Optimized for ultimate ability
- ✅ **Death Mark Support** - Automatic Invoke Death usage
- ✅ **Vulnerability Bombs** - Auto-throw with spam prevention
- ✅ **Adrenaline Renewal** - Auto-drink with Living Death
- ✅ **Desync Prevention** - Auto-reset on combat end + manual reset button
- ✅ **Special Mechanics** - Living Death cooldown resets, Life Transfer extension, Command Ghost once-per-summon

## Quick Start

### 1. Copy Files

Copy these files to your project:
- `RotationManager.java` - The rotation system
- `SkeletonScriptExample.java` - Minimal example (use as template)

### 2. Basic Integration

```java
// In your script constructor
rotation = new RotationManager("Necromancy Rotation", true);
rotation.setDebug(true);
rotation.setLogger(this::println);

// Subscribe to server ticks
subscribe(ServerTickedEvent.class, event -> {
    serverTicks = event.getTicks();
    rotation.setServerTick(serverTicks);
    if (isActive()) {
        executeRotation();
    }
});
```

#### Constructor Arguments Explained

```java
new RotationManager(String name, boolean spend)
```

**Parameters:**
- `name` (String): Display name for the rotation (used in logs)
  - Example: `"Necromancy Rotation"`, `"My Combat Rotation"`
  - Only used for identification in debug messages
  
- `spend` (boolean): Whether to spend adrenaline on ultimate abilities
  - `true` = Use Living Death when at 100% adrenaline (aggressive)
  - `false` = Never use Living Death, save adrenaline (conservative)
  - Affects Living Death usage in normal rotation
  - Does NOT affect other abilities

**Examples:**
```java
// Aggressive - uses Living Death
rotation = new RotationManager("Aggressive Necro", true);

// Conservative - saves adrenaline, no Living Death
rotation = new RotationManager("Conservative Necro", false);

// Boss fighting - use ultimates
rotation = new RotationManager("Boss Rotation", true);

// Slayer - save adrenaline for next mob
rotation = new RotationManager("Slayer Rotation", false);
```

### 3. Execute Rotation

```java
private void executeRotation() {
    LocalPlayer player = Client.getLocalPlayer();
    if (player == null || player.getTarget() == null) {
        return; // No target
    }
    
    if (serverTicks - lastAbilityServerTick >= 3) {
        if (rotation.execute()) {
            lastAbilityServerTick = serverTicks;
            println("Using: " + rotation.getLastAbilityUsed());
        }
    }
}
```

### 4. Scan Action Bar

**IMPORTANT:** You must scan your action bar before the rotation will work!

1. Start your script
2. Open the GUI
3. Go to "Rotation" tab
4. Click "Scan Action Bar"
5. Verify "Cached Abilities" shows the correct count

## GUI Features

### Rotation Tab

**Action Bar Scanner:**
- `Scan Action Bar` - Scans and caches ability positions
- `Show Cached Slots` - Prints cached abilities to console
- `Cached Abilities: X` - Shows how many abilities were found

**Cooldown Management:**
- `Reset Cooldowns` - Manual reset if abilities get desynced
- Auto-resets when combat ends (no target)

**Rotation Options:**
- `Use vuln bombs?` - Auto-throw Vulnerability Bombs
- `Use Death Mark?` - Auto-cast Invoke Death
- `Drink Adrenaline Renewal?` - Auto-drink with Living Death

## Ability Priority

### Normal Rotation
1. Death Skulls (60s CD, 60+ adrenaline)
2. Split Soul (60s CD)
3. Living Death (90s CD, 100 adrenaline, spend mode)
4. Volley of Souls (4+ soul stacks)
5. Finger of Death (6+ necrosis stacks)
6. Bloat (25s CD, 20+ adrenaline, 20s real-time CD)
7. Weapon Special Attack (60s CD, 27+ adrenaline, 4+ necrosis)
8. Essence of Finality (30s CD, 23+ adrenaline, 4+ necrosis)
9. Conjure Undead Army (when summons inactive)
10. Life Transfer (45s CD, 9000+ health, not if Army ready <5s)
11. Command Skeleton Warrior (15s CD, summons active)
12. Command Vengeful Ghost (once per summon, summons active)
13. Touch of Death (14.4s CD)
14. Soul Sap (5.4s CD)
15. Life Transfer (secondary, 8000+ health)
16. Basic Attack (fallback)

### Living Death Rotation
1. Death Skulls (12s CD during LD, 60+ adrenaline)
2. Touch of Death (<60 adrenaline)
3. Finger of Death (6+ necrosis, if DS CD >8 or adrenaline >60)
4. Touch of Death
5. Command Skeleton Warrior (if DS CD >=8 or adrenaline >60)

## Special Mechanics

### Living Death
- Resets Death Skulls and Touch of Death cooldowns
- Death Skulls has 12s cooldown during Living Death
- After Living Death ends, Death Skulls reverts to 60s cooldown
- If Adrenaline Renewal enabled, drinks potion 1 tick after activation

### Conjure Undead Army
- Tracked via varp 11018 (1 = active, 0 = inactive)
- 6-tick minimum between casts
- Resets Command Ghost usage flag
- Puts Command abilities on 3.6s cooldown when cast
- Life Transfer extends duration by 21 seconds

### Command Abilities
- **Command Skeleton Warrior**: 15s cooldown
- **Command Vengeful Ghost**: Once per summon (flag-based)
- Both get 3.6s cooldown when army is conjured

### Death Mark
- Uses Invoke Death ability (no cooldown)
- Applies Death Mark debuff on next attack
- Lasts until enemy is defeated
- 20-tick spam prevention (waits for buff to apply)

### Vulnerability Bombs
- Checks varbit 1939 for vulnerability status
- 5-tick spam prevention (waits for bomb to land)
- Searches action bar then backpack

### Life Transfer
- Won't use if Conjure Army ready within 5 seconds
- Prevents wasting extension on summons about to expire

## Cooldown Tracking

All cooldowns are tracked manually (no component queries):

| Ability | Cooldown |
|---------|----------|
| Death Skulls | 60s (12s during Living Death) |
| Split Soul | 60s |
| Living Death | 90s |
| Touch of Death | 14.4s |
| Soul Sap | 5.4s |
| Command Skeleton Warrior | 15s |
| Life Transfer | 45s |
| Conjure Undead Army | Varp-based |
| Invoke Death | None |
| Weapon Special Attack | 60s |
| Essence of Finality | 30s |
| Bloat | 25s |

## Troubleshooting

### Abilities Not Working
1. Click "Scan Action Bar" in GUI
2. Check "Cached Abilities" count
3. Click "Show Cached Slots" to verify abilities found
4. Make sure abilities are on your action bar

### Cooldowns Desynced
1. Click "Reset Cooldowns" button in GUI
2. Or lose target (auto-resets)
3. Check debug logs for errors

### Client Crashes
- The system is designed to prevent crashes
- Uses manual cooldown tracking (no ActionBar.getCooldown queries)
- Caches ability positions (no repeated scans)
- All API calls wrapped in try-catch

### Rotation Not Executing
1. Check bot state is SKILLING
2. Verify you have a target
3. Check server tick events are firing
4. Enable debug logging: `rotation.setDebug(true)`

## Advanced Usage

### Custom Rotation Priority

Modify `improviseNecromancy()` method in RotationManager.java to change ability priority.

### Add New Abilities

1. Add to ability scan list in `performSlotScan()`
2. Add cooldown to `ABILITY_COOLDOWNS` map
3. Add check in `improviseNecromancy()` method

### Disable Features

```java
rotation.setUseAdrenalineRenewal(false); // Don't drink potions
// Or use GUI checkboxes
```

### Custom Logging

```java
rotation.setLogger(message -> {
    // Your custom logging here
    myLogger.log(message);
});
```

## API Reference

### RotationManager Methods

```java
// Setup
void setDebug(boolean debug)
void setLogger(Consumer<String> logger)
void setServerTick(int serverTick)

// Execution
boolean execute() // Execute rotation, returns true if ability used

// Settings
void setUseAdrenalineRenewal(boolean use)

// Utility
void scanActionBar() // Manually trigger scan
void resetCooldowns() // Reset all cooldown tracking
String getLastAbilityUsed() // Get last ability name
int getCachedAbilityCount() // Get number of cached abilities
void printCachedSlots() // Print cached abilities to log

// Debuff Management
boolean ensureDeathMarked() // Apply Death Mark if needed
boolean ensureVulned() // Apply Vulnerability if needed
```

## Credits

Built for BotWithUs RS3 scripting framework.

## License

Use freely in your scripts!
