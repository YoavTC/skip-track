package work.yoavtc;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

// Config screen built from vanilla widgets only; opened from Mod Menu.
public class SkipTrackConfigScreen extends Screen {
	private final Screen parent;
	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

	public SkipTrackConfigScreen(Screen parent) {
		super(Component.literal("Skip Track Config"));
		this.parent = parent;
	}

	private static KeyMapping keybind;

	public static void foo(KeyMapping key) {
		keybind = key;
	}

	@Override
	protected void init() {
		layout.addTitleHeader(title, font);

		LinearLayout contents = layout.addToContents(LinearLayout.vertical().spacing(8));
		
		// Show button toggle
		contents.addChild(CycleButton.onOffBuilder(SkipTrackConfig.drawButton)
				.create(0, 0, 200, 20, Component.literal("Pause Menu Button"),
						(button, value) -> SkipTrackConfig.drawButton = value));

		if (keybind != null) {
			contents.addChild(new StringWidget(Component.literal("Skip keybind: ").append(keybind.getTranslatedKeyMessage()), font));
			contents.addChild(new StringWidget(Component.literal("Change it under \"Options > Controls > Key Binds\""), font));
		}

		layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, _ -> onClose()).width(200).build());

		layout.visitWidgets(this::addRenderableWidget);
		repositionElements();
	}

	@Override
	protected void repositionElements() {
		layout.arrangeElements();
	}

	@Override
	public void onClose() {
		SkipTrackConfig.save();
		minecraft.gui.setScreen(parent);
	}
}
