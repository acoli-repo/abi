package enums;

public enum Modus implements AnnotationTag{
    indicative ("ind"),
    imperative("imp"),
    conjunctive("conj");

    private final String content;
    Modus(String s){ this.content = s;}
    public String getString(){
        return this.content;
    }
}
