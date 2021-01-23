package net.tslat.wgvisualizer.client.screen.widget;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.function.Supplier;

public class StateTrackingButton extends ExtendedButton {
	private final Supplier<Boolean> predicate;

	public StateTrackingButton(int x, int y, int width, int height, ITextComponent title, IPressable pressedAction, Supplier<Boolean> statePredicate) {
		super(x, y, width, height, title, pressedAction);

		this.predicate = statePredicate;
	}

	@Override
	public int getFGColor() {
		return predicate.get() ? 0xFF6060 : super.getFGColor();
	}
}
