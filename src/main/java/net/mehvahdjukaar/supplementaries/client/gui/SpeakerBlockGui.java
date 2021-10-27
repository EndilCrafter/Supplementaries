package net.mehvahdjukaar.supplementaries.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.supplementaries.block.tiles.SpeakerBlockTile;
import net.mehvahdjukaar.supplementaries.configs.ServerConfigs;
import net.mehvahdjukaar.supplementaries.network.NetworkHandler;
import net.mehvahdjukaar.supplementaries.network.ServerBoundSetSpeakerBlockPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fmlclient.gui.widget.Slider;

public class SpeakerBlockGui extends Screen {
    private static final Component NARRATOR_TEXT = new TranslatableComponent("gui.supplementaries.speaker_block.chat_message");
    private static final Component CHAT_TEXT = new TranslatableComponent("gui.supplementaries.speaker_block.narrator_message");

    private static final Component DISTANCE_BLOCKS = new TranslatableComponent("gui.supplementaries.speaker_block.blocks");

    private static final Component VOLUME_TEXT = new TranslatableComponent("gui.supplementaries.speaker_block.volume");

    private EditBox commandTextField;
    private final SpeakerBlockTile tileSpeaker;
    private boolean narrator;
    private final String message;
    private Button modeBtn;
    private Slider volume;

    public SpeakerBlockGui(SpeakerBlockTile te) {
        super(new TranslatableComponent("gui.supplementaries.speaker_block.edit"));
        this.tileSpeaker = te;
        this.narrator = tileSpeaker.narrator;
        this.message = tileSpeaker.message;
    }

    public static void open(SpeakerBlockTile te) {
        Minecraft.getInstance().setScreen(new SpeakerBlockGui(te));
    }

    public void tick() {
        this.commandTextField.tick();
    }

    private void updateMode() {
        if (this.narrator) {
            this.modeBtn.setMessage(NARRATOR_TEXT);
        } else {
            this.modeBtn.setMessage(CHAT_TEXT);
        }
    }

    private void toggleMode() {
        this.narrator = !this.narrator;
    }

    @Override
    public void init() {
        assert this.minecraft != null;
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);

        int range = ServerConfigs.cached.SPEAKER_RANGE;

        double v = this.tileSpeaker.volume * range;
        this.volume = new Slider(this.width / 2 - 75, this.height / 4 + 80, 150, 20, VOLUME_TEXT, DISTANCE_BLOCKS, 1, range, v, false, true, null, null);

        this.addWidget(this.volume);

        this.addWidget(new Button(this.width / 2 - 100, this.height / 4 + 120, 200, 20, CommonComponents.GUI_DONE, (p_214266_1_) -> this.close()));
        this.modeBtn = this.addWidget(new Button(this.width / 2 - 75, this.height / 4 + 50, 150, 20, CHAT_TEXT, (p_214186_1_) -> {
            this.toggleMode();
            this.updateMode();
        }));
        this.updateMode();
        this.commandTextField = new EditBox(this.font, this.width / 2 - 100, this.height / 4 + 10, 200, 20, this.title) {
            protected MutableComponent createNarrationMessage() {
                return super.createNarrationMessage();
            }
        };
        this.commandTextField.setValue(message);
        this.commandTextField.setMaxLength(32);
        this.addWidget(this.commandTextField);
        this.setInitialFocus(this.commandTextField);
        this.commandTextField.setFocus(true);
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
        this.tileSpeaker.message = this.commandTextField.getValue();
        this.tileSpeaker.narrator = this.narrator;
        this.tileSpeaker.volume = this.volume.getValue() / this.volume.maxValue;
        //update server tile
        NetworkHandler.INSTANCE.sendToServer(new ServerBoundSetSpeakerBlockPacket(this.tileSpeaker.getBlockPos(), this.tileSpeaker.message, this.tileSpeaker.narrator, this.tileSpeaker.volume));

    }

    private void close() {
        this.tileSpeaker.setChanged();
        this.minecraft.setScreen(null);
    }

    @Override
    public void onClose() {
        this.close();
    }

    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
            return true;
        } else if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
            return false;
        } else {
            this.close();
            return true;
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0)
            this.volume.onRelease(mouseX, mouseY);
        return false;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 40, 16777215);
        this.volume.render(matrixStack, mouseX, mouseY, partialTicks);
        this.commandTextField.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}