package hybrid.api.mixin;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(CommandSuggestions.class)
public class ChatScreenMixin {
    @Shadow
    private CommandSuggestions.@Nullable SuggestionsList suggestions;

    @Shadow
    private @Nullable CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow
    @Final
    private EditBox input;

    @Inject(method = "showSuggestions", at = @At(value = "HEAD"))
    public void yes(boolean bl, CallbackInfo ci) {
        if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
            Suggestions suggestions = this.pendingSuggestions.join();
            if (!suggestions.isEmpty()) {
                int i = 0;

                String original = input.getValue();

                for (Suggestion suggestion : suggestions.getList()) {
                    StringRange range = suggestion.getRange();

                    System.out.println("Suggestion: " + suggestion.getText());

                }
            }
        }
    }
}
