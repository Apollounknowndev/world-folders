package dev.worldgen.world.folders.gui.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ColorPickerWidget extends TextFieldWidget {
    private int color = 0x5555FF;
    public ColorPickerWidget(TextRenderer textRenderer, int width, int height, Text text) {
        super(textRenderer, 0, 0, width - height - 4, height, null, text);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!this.isVisible()) {
            return;
        }

        int x = this.getX() + this.width + 4;
        int y = this.getY();

        context.drawBorder(x, y, this.height, this.height, 0xFFA0A0A0);

        context.fill(x + 1, y + 1, x + this.height - 1, y + this.height - 1, this.color + 0xFF000000);

        super.renderWidget(context, mouseX, mouseY, delta);
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
