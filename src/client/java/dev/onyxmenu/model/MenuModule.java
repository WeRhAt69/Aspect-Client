package dev.onyxmenu.model;

public final class MenuModule {
    private final String id;
    private final String name;
    private boolean enabled;
    private float toggleProgress;

    public MenuModule(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String id() { return id; }
    public String name() { return name; }
    public boolean enabled() { return enabled; }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.toggleProgress = enabled ? 1.0F : 0.0F;
    }
    public void toggle() { enabled = !enabled; }
    public float animateToggle(float delta) {
        float target = enabled ? 1.0F : 0.0F;
        toggleProgress += (target - toggleProgress) * Math.min(1.0F, delta * 0.28F);
        if (Math.abs(target - toggleProgress) < 0.01F) toggleProgress = target;
        return toggleProgress;
    }
}
