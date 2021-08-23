package com.denfop.integration.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.denfop.api.IMicrochipFarbricatorRecipeManager;
import com.denfop.gui.GuiGenerationMicrochip;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import ic2.core.util.StackUtil;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;


import com.denfop.api.Recipes;
import com.denfop.Constants;

public class NEIGPU extends TemplateRecipeHandler {
    int ticks;

    public class GPURecipe extends TemplateRecipeHandler.CachedRecipe {
        public final PositionedStack output;

        public final List<PositionedStack> ingredients = new ArrayList<>();

        public List<PositionedStack> getIngredients() {
            return getCycledIngredients(NEIGPU.this.cycleticks / 20, this.ingredients);
        }

        public PositionedStack getResult() {
            return this.output;
        }

        public GPURecipe(IRecipeInput container, IRecipeInput fill, IRecipeInput fill1, IRecipeInput fill2, IRecipeInput fill3,RecipeOutput output1) {
            super();
            List<ItemStack> containerItems = new ArrayList<>();
            List<ItemStack> fillItems = new ArrayList<>();
            List<ItemStack> fillItems1 = new ArrayList<>();
            List<ItemStack> fillItems2 = new ArrayList<>();
            List<ItemStack> fillItems3 = new ArrayList<>();
            for (ItemStack item : container.getInputs())
                containerItems.add(StackUtil.copyWithSize(item, container.getAmount()));
            for (ItemStack item : fill.getInputs())
                fillItems.add(StackUtil.copyWithSize(item, fill.getAmount()));
            for (ItemStack item : fill1.getInputs())
                fillItems1.add(StackUtil.copyWithSize(item, fill1.getAmount()));
            for (ItemStack item : fill2.getInputs())
                fillItems2.add(StackUtil.copyWithSize(item, fill2.getAmount()));
            for (ItemStack item : fill3.getInputs())
                fillItems3.add(StackUtil.copyWithSize(item, fill3.getAmount()));

            this.ingredients.add(new PositionedStack(containerItems, 7, 6));
            this.ingredients.add(new PositionedStack(fillItems, 7, 27));
            this.ingredients.add(new PositionedStack(fillItems1, 40, 6));
            this.ingredients.add(new PositionedStack(fillItems2, 40, 26));
            this.ingredients.add(new PositionedStack(fillItems3, 68, 16));
            this.output = new PositionedStack(output1.items.get(0), 109, 16);
        }
    }

    public Class<? extends GuiContainer> getGuiClass() {
        return  GuiGenerationMicrochip.class;
    }

    public String getRecipeName() {
        return StatCollector.translateToLocal("iu.blockGenerationMicrochip.name");
    }

    public String getRecipeId() {
        return "iu.gpu";
    }

    public String getGuiTexture() {

        return Constants.TEXTURES + ":textures/gui/GUICirsuit.png";
    }

    public String getOverlayIdentifier() {
        return "microchipmanufacture";
    }

    public Map<IMicrochipFarbricatorRecipeManager.Input, RecipeOutput> getRecipeList() {
        return Recipes.GenerationMicrochip.getRecipes();
    }

    public void drawBackground(int i) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 3, 3, 140, 77);
    }

    public void drawExtras(int i) {

         float   f = (this.ticks >= 20) ? (((this.ticks - 20) % 20) / 20.0F) : 0.0F;
        drawProgressBar(85, 20, 176, 85, 20, 7, f, 0);

        f = (this.ticks <= 20) ? (this.ticks / 20.0F) : 1.0F;
        drawProgressBar(3, 60, 176, 0, 14, 14, f, 3);
    }

    public void onUpdate() {
        super.onUpdate();
        this.ticks++;
    }

    public void loadTransferRects() {
        this.transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(17, 4, 22, 30),
                getRecipeId()));
        this.transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(50, 4, 11, 25),
                getRecipeId()));
        this.transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(78, 14, 23, 9),
                getRecipeId()));

    }

    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(getRecipeId())) {
            for (Map.Entry<IMicrochipFarbricatorRecipeManager.Input, RecipeOutput> entry : getRecipeList().entrySet())
                this.arecipes.add(new GPURecipe(entry.getKey().container,
                        entry.getKey().fill, entry.getKey().fill1, entry.getKey().fill2, entry.getKey().container1, entry.getValue()));
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    public void loadCraftingRecipes(ItemStack result) {
        for (Map.Entry<IMicrochipFarbricatorRecipeManager.Input, RecipeOutput> entry : getRecipeList().entrySet()) {
            for (ItemStack output : entry.getValue().items) {
                if (NEIServerUtils.areStacksSameTypeCrafting(output, result))
                    this.arecipes
                            .add(new GPURecipe(entry.getKey().container,
                                    entry.getKey().fill, entry.getKey().fill1, entry.getKey().fill2, entry.getKey().container1, entry.getValue()));
            }
        }
    }
    public int recipiesPerPage() {
        return 1;
    }
    public void loadUsageRecipes(ItemStack ingredient) {
        for (Map.Entry<IMicrochipFarbricatorRecipeManager.Input, RecipeOutput> entry : getRecipeList().entrySet()) {
            if (entry.getKey().container.matches(ingredient)
                    || entry.getKey().fill.matches(ingredient)|| entry.getKey().fill1.matches(ingredient))
                this.arecipes.add(new GPURecipe(entry.getKey().container,
                        entry.getKey().fill, entry.getKey().fill1, entry.getKey().fill2, entry.getKey().container1, entry.getValue()));
        }
    }
}