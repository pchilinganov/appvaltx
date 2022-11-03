package chat.atc.tges.tgeschat.model;

import java.util.List;

public class FieldToJson {
    private String field;
    private String message;
    private String value;
    private boolean enabled;
    private boolean visible;
    private boolean focus;
    private String type;
    List<ItemToJson> options;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isFocus() {
        return focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ItemToJson> getOptions() {
        return options;
    }

    public void setOptions(List<ItemToJson> options) {
        this.options = options;
    }
}
