package enums;

public enum Rede implements AnnotationTag{
    direkte ("dir"),
    indirekte("ind");

    private final String content;
    Rede(String s){ this.content = s;}
    public String getString(){
        return this.content;
    }
}
