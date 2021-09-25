package de.teamlapen.werewolves.proxy;

import de.teamlapen.werewolves.client.core.*;
import de.teamlapen.werewolves.network.AttackTargetEventPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    private ModHUDOverlay hudOverlay;
    private ClientEventHandler clientHandler;
    private boolean autoJump;

    public ClientProxy() {
        RenderHandler renderHandler = new RenderHandler(Minecraft.getInstance());
        MinecraftForge.EVENT_BUS.register(renderHandler);
        //Minecraft.instance is null during runData.
        //noinspection ConstantConditions
        if (Minecraft.getInstance() != null)
            ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(renderHandler); // Must be added before initial resource manager load
    }

    @Override
    public void onInitStep(Step step, ParallelDispatchEvent event) {
        super.onInitStep(step, event);
        switch (step) {
            case CLIENT_SETUP:
                ModEntityRenderer.registerEntityRenderer();
                ModBlocksRenderer.register();
                MinecraftForge.EVENT_BUS.register(clientHandler = new ClientEventHandler());
                MinecraftForge.EVENT_BUS.register(hudOverlay = new ModHUDOverlay());
                ModKeys.register(clientHandler);
                break;
            case LOAD_COMPLETE:
                ModItemRenderer.registerColors();
                ModScreens.registerScreens();
                break;
        }
    }

    @Override
    public void handleAttackTargetEventPacket(AttackTargetEventPacket packet) {
        this.hudOverlay.attackTriggered(packet.entityId);
    }
}
