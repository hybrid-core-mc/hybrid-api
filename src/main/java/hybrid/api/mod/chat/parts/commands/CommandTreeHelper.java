package hybrid.api.mod.chat.parts.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static hybrid.api.Main.mc;

public class CommandTreeHelper {

    private final CommandDispatcher<ClientSuggestionProvider> dispatcher;
    private final ClientSuggestionProvider provider;

    private String currentInput;
    private List<Suggestion> suggestions;

    public CommandTreeHelper(String initial) {
        this.dispatcher = mc.player.connection.getCommands();
        this.provider = mc.player.connection.getSuggestionsProvider();
        this.currentInput = initial;
    }


    public CommandTreeHelper setInput(String input) {
        this.currentInput = input;
        return this;
    }

    public CompletableFuture<List<Suggestion>> update() {
        ParseResults<ClientSuggestionProvider> parse =
                dispatcher.parse(currentInput, provider);

        return dispatcher.getCompletionSuggestions(parse, currentInput.length())
                         .thenApply(result -> {
                             this.suggestions = result.getList();
                             return this.suggestions;
                         });
    }

    public CompletableFuture<Suggestion> getSuggestionAtIndexFor(int index) {
        return update().thenApply(list -> {
            if (list == null || list.isEmpty()) return null;
            if (index < 0 || index >= list.size()) return null;

            return list.get(index);
        });
    }

    public CompletableFuture<String> applySuggestionAt(int index) {
        return update().thenApply(list -> {
            if (list == null || list.isEmpty()) return null;
            if (index < 0 || index >= list.size()) return null;

            Suggestion s = list.get(index);

            this.currentInput = s.getText();

            return currentInput;
        });
    }

    public List<Suggestion> getSuggestions() {
        return suggestions;
    }

    public String getCurrentInput() {
        return currentInput;
    }
}