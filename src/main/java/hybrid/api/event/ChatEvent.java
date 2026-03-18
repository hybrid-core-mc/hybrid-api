package hybrid.api.event;


import net.minecraft.text.Text;
public class ChatEvent {

    private final String message;
    private String overrideMessage;
    private boolean override;

    public ChatEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setOverride(String overrideMessage) {
        this.overrideMessage = overrideMessage;
        this.override = true;
    }

    public boolean isOverride() {
        return override;
    }

    public String getFinalMessage() {
        return override ? overrideMessage : message;
    }
}