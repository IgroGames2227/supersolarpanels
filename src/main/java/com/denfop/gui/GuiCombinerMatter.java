package com.denfop.gui;

import com.denfop.Constants;
import com.denfop.container.ContainerCombinerMatter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.util.DrawUtil;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static ic2.core.util.GuiTooltipHelper.drawTooltip;

@SideOnly(Side.CLIENT)
public class GuiCombinerMatter extends GuiIC2 {
    public final ContainerCombinerMatter container;
    public final String progressLabel;
    public final String amplifierLabel;

    public GuiCombinerMatter(ContainerCombinerMatter container1) {
        super(container1);
        this.container = container1;
        this.progressLabel = StatCollector.translateToLocal("ic2.Matter.gui.info.progress");
        this.amplifierLabel = StatCollector.translateToLocal("ic2.Matter.gui.info.amplifier");
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.progressLabel, 69, 12, 4210752);
        this.fontRendererObj.drawString((this.container.base).getProgressAsString(), 72+35, 12, 4210752);
        this.fontRendererObj.drawString(getName(),
                (this.xSize - this.fontRendererObj.getStringWidth(getName())) / 2-10, 3, 4210752);
        if (this.container.base instanceof IUpgradableBlock) {
            GuiTooltipHelper.drawUpgradeslotTooltip(par1 - this.guiLeft, par2 - this.guiTop, 0, 0, 12, 12, this.container.base, 25, 0);
        }
        if ((this.container.base).scrap > 0) {
            this.fontRendererObj.drawString(this.amplifierLabel, 10, 71, 4210752);
            this.fontRendererObj.drawString("" + (this.container.base).scrap, 50, 71, 4210752);
        }
        super.drawGuiContainerForegroundLayer(par1, par2);
        FluidStack fluidstack = (this.container.base).getFluidStackfromTank();
        if (fluidstack != null) {
            String tooltip = StatCollector.translateToLocal("ic2.uumatter") + ": " + fluidstack.amount
                    + StatCollector.translateToLocal("ic2.generic.text.mb");
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, tooltip, 99, 25, 112, 73);
        }
        drawUpgradeslotTooltip(par1 - this.guiLeft, par2 - this.guiTop, 165, 0, 175, 12,
                25, 0);
    }
    public static void drawUpgradeslotTooltip(int x, int y, int minX, int minY, int maxX, int maxY, int yoffset, int xoffset) {
        if (  x >= minX && x <= maxX && y >= minY && y <= maxY) {
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
            int width = fontRenderer.getStringWidth(StatCollector.translateToLocal("iu.combMatterinformation"));
            List<String> compatibleUpgrades = getInformation();
            Iterator var12 = compatibleUpgrades.iterator();

            String itemstack;
            while(var12.hasNext()) {
                itemstack = (String)var12.next();
                if (fontRenderer.getStringWidth(itemstack) > width) {
                    width = fontRenderer.getStringWidth(itemstack);
                }
            }

            drawTooltip(x-120, y, yoffset, xoffset, StatCollector.translateToLocal("iu.combMatterinformation"), true, width);
            yoffset += 15;

            for(var12 = compatibleUpgrades.iterator(); var12.hasNext(); yoffset += 14) {
                itemstack = (String)var12.next();
                drawTooltip(x-120, y, yoffset, xoffset, itemstack, false, width);
            }
        }

    }

    private static List<String> getInformation() {
        List<String> ret = new ArrayList();
        ret.add(StatCollector.translateToLocal("iu.combMatterinformation1"));
        ret.add(StatCollector.translateToLocal("iu.combMatterinformation2"));


        return ret;
    }
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        drawTexturedModalRect(this.xoffset, this.yoffset, 0, 0, this.xSize, this.ySize);
        this.mc.getTextureManager()
                .bindTexture(new ResourceLocation(IC2.textureDomain, "textures/gui/infobutton.png"));
        drawTexturedModalRect(this.xoffset + 165, this.yoffset, 0, 0, 10, 10);
        if (this.container.base instanceof IUpgradableBlock) {
            this.mc.getTextureManager().bindTexture(new ResourceLocation(IC2.textureDomain, "textures/gui/infobutton.png"));
            this.drawTexturedModalRect(this.xoffset + 3, this.yoffset + 3, 0, 0, 10, 10);
            this.mc.getTextureManager().bindTexture(this.getResourceLocation());
        }
        this.mc.getTextureManager().bindTexture(getResourceLocation());

        if ((this.container.base).getTankAmount() > 0) {
            IIcon fluidIcon = (this.container.base).getFluidTank().getFluid().getFluid().getIcon();
            if (fluidIcon != null) {
                drawTexturedModalRect(this.xoffset + 96, this.yoffset + 22, 176, 0, 20, 55);
                this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                int liquidHeight = (this.container.base).gaugeLiquidScaled(47);
                DrawUtil.drawRepeated(fluidIcon, (this.xoffset + 100), (this.yoffset + 26 + 47 - liquidHeight), 12.0D,
                        liquidHeight, this.zLevel);
                this.mc.renderEngine.bindTexture(getResourceLocation());
                drawTexturedModalRect(this.xoffset + 100, this.yoffset + 26, 176, 55, 12, 47);
            }
        }
    }

    public String getName() {
        return this.container.base.getInventoryName();
    }

    public ResourceLocation getResourceLocation() {

            return new ResourceLocation(Constants.TEXTURES, "textures/gui/GUICombineMatter.png");

        }

}