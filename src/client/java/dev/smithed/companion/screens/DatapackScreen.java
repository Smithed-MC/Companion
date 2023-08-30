package dev.smithed.companion.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class DatapackScreen extends Screen {

    //TODO rework this to completoin someday

    public static final Text TITLE = Text.translatable("datapackscreen.title");
//    private ButtonWidget doneButton;
//    private DatapackListWidget dataPackListWidget;
//    private Future<DatapackData>[] datapackData;
//
    public DatapackScreen() {
        super(TITLE);
    }
//
//    @Override
//    protected void init() {
//
//        datapackData = SmithedWebUtils.getAllDatapackData("creepermagnet_:tcc", "hinge:manic", "hinge:sanguine", "hinge:nucleus");
//
//        this.doneButton = this.addDrawableChild(
//                ButtonWidget.builder(ScreenTexts.DONE, button -> {
//                    this.close();
//                }).dimensions((this.width / 4), this.height - 48,150, 20).build()
//        );
//        //this.addDrawableChild(new DatapackInfoWidget());
//
//        this.dataPackListWidget = new DatapackListWidget(this.client, 200, this.height, this, Text.translatable("pack.installed.title"));
//        this.dataPackListWidget.setLeftPos(this.width / 2 - 4 - 200);
//        this.addSelectableChild(this.dataPackListWidget);
//
//        refresh();
//    }
//
//    @Override
//    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
//        this.renderBackgroundTexture(0);
//        this.dataPackListWidget.render(matrices, mouseX, mouseY, delta);
//        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
//        super.render(matrices, mouseX, mouseY, delta);
//    }
//
//    private void refresh() {
//        updatePackList(dataPackListWidget, datapackData);
//    }
//
//    private void updatePackList(DatapackListWidget widget, Future<DatapackData>[] packs) {
//        widget.children().clear();
//        for (Future<DatapackData> dataFuture : packs) {
//            try {
//                widget.children().add(new DatapackListWidget.DatapackInfoEntry(this.client, dataFuture.get(), this));
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void sendDisplayData(DatapackData info) {
//
//    }
//
//    public static class DatapackListWidget extends AlwaysSelectedEntryListWidget<dev.smithed.companion.screens.DatapackListWidget.DatapackInfoEntry> {
//
//        private final dev.smithed.companion.screens.datapack.DatapackScreen screen;
//        private final Text title;
//        public static final Identifier ICON = new Identifier("smithed","textures/gui/missing.png");
//
//        public DatapackListWidget(MinecraftClient client, int width, int height, DatapackScreen screen, Text title) {
//            super(client, width, height, 32, height - 55 + 4, 36);
//            this.screen = screen;
//            this.title = title;
//            this.centerListVertically = false;
//            Objects.requireNonNull(client.textRenderer);
//            this.setRenderHeader(true, (int)(9.0F * 1.5F));
//        }
//
//        @Override
//        protected void renderHeader(MatrixStack matrices, int x, int y, Tessellator tessellator) {
//            Text text = Text.empty().append(this.title).formatted(Formatting.UNDERLINE, Formatting.BOLD);
//            this.client.textRenderer.draw(matrices, text, (float)(x + this.width / 2 - this.client.textRenderer.getWidth(text) / 2), (float)Math.min(this.top + 3, y), 16777215);
//        }
//
//        @Override
//        public int getRowWidth() {
//            return this.width;
//        }
//
//        @Override
//        protected int getScrollbarPositionX() {
//            return this.right - 6;
//        }
//
//
//        public static class DatapackInfoEntry extends AlwaysSelectedEntryListWidget.Entry<dev.smithed.companion.screens.DatapackListWidget.DatapackInfoEntry> {
//
//            private final MinecraftClient client;
//            private final DatapackData info;
//            private final MultilineText description;
//            private final OrderedText displayName;
//            private final DatapackScreen screen;
//
//            public DatapackInfoEntry(MinecraftClient client, DatapackData info, DatapackScreen screen) {
//                this.client = client;
//                this.info = info;
//                this.screen = screen;
//
//                this.description = MultilineText.create(client.textRenderer, this.info.display.getDescription(), 157, 2);
//                displayName = trimTextToWidth(client, info.display.getName());
//            }
//
//
//
//            @Override
//            public Text getNarration() {
//                return info.display.getDescription();
//            }
//
//            // method copied from "net.minecraft.client.gui.screen.pack.PackListWidget.ResourcePackEntry.trimTextToWidth"
//            // for best resource pack menu parity
//            private static OrderedText trimTextToWidth(MinecraftClient client, Text text) {
//                int i = client.textRenderer.getWidth(text);
//                if (i > 157) {
//                    StringVisitable stringVisitable = StringVisitable.concat(client.textRenderer.trimToWidth(text, 157 - client.textRenderer.getWidth("...")), StringVisitable.plain("..."));
//                    return Language.getInstance().reorder(stringVisitable);
//                } else {
//                    return text.asOrderedText();
//                }
//            }
//
//            @Override
//            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
//                RenderSystem.setShader(GameRenderer::getPositionTexShader);
//                try {
//                    RenderSystem.setShaderTexture(0, ICON);
//                } catch (Exception e) {
//                    RenderSystem.setShaderTexture(0, ICON);
//                }
//                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//                drawTexture(matrices, x, y, 0.0F, 0.0F, 32, 32, 32, 32);
//
//                this.client.textRenderer.drawWithShadow(matrices, this.displayName, (float)(x + 32 + 2), (float)(y + 1), 16777215);
//                this.description.drawWithShadow(matrices, x + 32 + 2, y + 12, 10, 8421504);
//            }
//
//            @Override
//            public boolean mouseClicked(double mouseX, double mouseY, int button) {
//                screen.sendDisplayData(this.info);
//                return super.mouseClicked(mouseX, mouseY, button);
//            }
//        }
//    }
//
//    public static class DatapackInfoWidget extends AlwaysSelectedEntryListWidget<dev.smithed.companion.screens.datapack.DatapackInfoWidget.InfoWidgetEntry> {
//        public DatapackInfoWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
//            super(client, width, height, top, bottom, itemHeight);
//        }
//
//        @Override
//        public void appendNarrations(NarrationMessageBuilder builder) {
//
//        }
//
//        public static class InfoWidgetEntry extends AlwaysSelectedEntryListWidget.Entry<dev.smithed.companion.screens.datapack.DatapackInfoWidget.InfoWidgetEntry> {
//
//            @Override
//            public Text getNarration() {
//                return null;
//            }
//
//            @Override
//            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
//
//            }
//        }
//    }
}
