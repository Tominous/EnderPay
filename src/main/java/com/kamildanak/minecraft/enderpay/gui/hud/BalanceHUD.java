package com.kamildanak.minecraft.enderpay.gui.hud;

import com.kamildanak.minecraft.enderpay.EnderPay;
import com.kamildanak.minecraft.enderpay.Utils;
import com.kamildanak.minecraft.foamflower.gui.GuiExtended;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.vecmath.Point2i;

public class BalanceHUD extends GuiExtended {
    private static Long balance = null;
    private static long date = 0;
    private Minecraft mc;

    public BalanceHUD(Minecraft mc) {
        super(mc);
        this.mc = mc;
    }

    public static long getDate() {
        return BalanceHUD.date;
    }

    public static void setDate(long date) {
        BalanceHUD.date = date;
    }

    public static void setBalance(long balance) {
        BalanceHUD.balance = balance;
    }

    public static String getCurrency(long balance) {
        return (balance == 1) ? EnderPay.settings.getCurrencyNameSingular() : EnderPay.settings.getCurrencyNameMultiple();
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    @SuppressWarnings("unused")
    public void onRenderInfo(RenderGameOverlayEvent.Post event) {
        if (event.isCancelable()) return;
        if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE &&
                event.getType() != RenderGameOverlayEvent.ElementType.HEALTHMOUNT)
            return;
        if (mc == null || mc.player == null || mc.world == null || mc.player.isSpectator()) return;

        drawBalance();
    }

    private void drawBalance() {
        mc.mcProfiler.startSection("balance");
        ScaledResolution resolution = new ScaledResolution(mc);
        FontRenderer fontRenderer = this.mc.getRenderManager().getFontRenderer();
        //noinspection ConstantConditions //no, it is not constant
        if (fontRenderer == null) return;

        String text = (balance == null) ? "---" :
                fontRenderer.listFormattedStringToWidth(Utils.format(balance) + getCurrency(balance),
                        64).get(0);

        bind("enderpay:textures/icons.png");

        Point2i position = EnderPay.settings.getPosition().getPoint(resolution, mc);
        int x = position.getX() + EnderPay.settings.getxOffset();
        int y = position.getY() + EnderPay.settings.getyOffset();


        if (EnderPay.settings.getAnchor() != Anchor.LEFT) {
            int textLength = fontRenderer.getStringWidth(text);
            x -= (textLength + 18) / (EnderPay.settings.getAnchor() == Anchor.CENTRE ? 2 : 1);
        }

        drawTexturedModalRect(x, y, 0, 0, 16, 11);
        drawString(fontRenderer, text, x + 18, y + 2, 0xa0a0a0);

        mc.mcProfiler.endSection();
    }
}
