package enums;

public enum Satzart implements AnnotationTag{
    declarative("decl"),
    imperative("imp"),
    interrogative("int");

    private final String content;
    Satzart(String s){ this.content = s;}
    public String getString(){
        return this.content;
    }
}