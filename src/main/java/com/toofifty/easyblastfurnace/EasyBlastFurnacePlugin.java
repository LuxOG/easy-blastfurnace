package com.toofifty.easyblastfurnace;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.toofifty.easyblastfurnace.methods.Method;
import com.toofifty.easyblastfurnace.overlays.EasyBlastFurnaceCoalBagOverlay;
import com.toofifty.easyblastfurnace.overlays.EasyBlastFurnaceInstructionOverlay;
import com.toofifty.easyblastfurnace.overlays.EasyBlastFurnaceItemStepOverlay;
import com.toofifty.easyblastfurnace.overlays.EasyBlastFurnaceObjectStepOverlay;
import com.toofifty.easyblastfurnace.utils.BlastFurnaceState;
import com.toofifty.easyblastfurnace.utils.ObjectManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
    name = "Easy Blast Furnace"
)
public class EasyBlastFurnacePlugin extends Plugin
{
    public static final int CONVEYOR_BELT = ObjectID.CONVEYOR_BELT;
    public static final int BAR_DISPENSER = NullObjectID.NULL_9092;
    public static final int BANK_CHEST = ObjectID.BANK_CHEST_26707;

    private static final Pattern COAL_FULL_MESSAGE = Pattern.compile("^The coal bag contains 27 pieces of coal.$");
    private static final Pattern COAL_EMPTY_MESSAGE = Pattern.compile("^The coal bag is now empty.$");

    private static final int FILL_ACTION = 33;
    private static final int EMPTY_ACTION = 36;

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private BlastFurnaceState state;

    @Inject
    private ObjectManager objectManager;

    @Inject
    private EasyBlastFurnaceInstructionOverlay instructionOverlay;

    @Inject
    private EasyBlastFurnaceItemStepOverlay itemStepOverlay;

    @Inject
    private EasyBlastFurnaceObjectStepOverlay objectStepOverlay;

    @Inject
    private EasyBlastFurnaceCoalBagOverlay coalBagOverlay;

    @Inject
    private MethodInstructor methodInstructor;

    @Getter
    private boolean isEnabled = false;

    @Getter
    private Method currentMethod;

    @Override
    protected void startUp()
    {
        overlayManager.add(instructionOverlay);
        overlayManager.add(coalBagOverlay);
        overlayManager.add(itemStepOverlay);
        overlayManager.add(objectStepOverlay);
    }

    @Override
    protected void shutDown()
    {
        methodInstructor.reset();

        overlayManager.remove(instructionOverlay);
        overlayManager.remove(coalBagOverlay);
        overlayManager.remove(itemStepOverlay);
        overlayManager.remove(objectStepOverlay);
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        GameObject gameObject = event.getGameObject();

        switch (gameObject.getId()) {
            case CONVEYOR_BELT:
            case BAR_DISPENSER:
            case BANK_CHEST:
                isEnabled = true;
                objectManager.add(gameObject);
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event)
    {
        GameObject gameObject = event.getGameObject();

        switch (gameObject.getId()) {
            case CONVEYOR_BELT:
            case BAR_DISPENSER:
            case BANK_CHEST:
                isEnabled = false;
                methodInstructor.reset();
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == GameState.LOADING) {
            isEnabled = false;
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        if (!isEnabled) return;

        if (event.getContainerId() == InventoryID.INVENTORY.getId()) {
            if (currentMethod == null) {
                methodInstructor.setMethodFromInventory(event.getItemContainer().getItems());
            }
        }

        // handle any inventory or bank changes
        methodInstructor.next();
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event)
    {
        if (!isEnabled) return;

        // handle furnace ore/bar quantity changes
        methodInstructor.next();
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (!isEnabled) return;
        if (event.getType() != ChatMessageType.GAMEMESSAGE) return;

        Matcher emptyMatcher = COAL_EMPTY_MESSAGE.matcher(event.getMessage());
        Matcher filledMatcher = COAL_FULL_MESSAGE.matcher(event.getMessage());

        if (emptyMatcher.matches()) {
            state.emptyCoalBag();

        } else if (filledMatcher.matches()) {
            state.fillCoalBag();
        }

        // handle coal bag changes
        methodInstructor.next();
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (!isEnabled) return;

        if (event.getMenuAction().getId() == FILL_ACTION) state.fillCoalBag();
        if (event.getMenuAction().getId() == EMPTY_ACTION) state.emptyCoalBag();

        // handle coal bag changes
        methodInstructor.next();
    }

    @Provides
    EasyBlastFurnaceConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(EasyBlastFurnaceConfig.class);
    }
}
