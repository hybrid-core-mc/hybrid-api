package hybrid.api.componenets;

public abstract class Component {
    String name; // used for saving values in future..

    public Component(String name) {
        this.name = name;
    }
    public void render(){

    }

    public String getName() {
        return name;
    }
}
