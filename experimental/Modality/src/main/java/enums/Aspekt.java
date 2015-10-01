package enums;
public enum Aspekt implements AnnotationTag{
    imperfect ("imp"),
    perfect("per");

    private final String content;
    Aspekt(String s){ this.content = s;}
    public String getString(){
        return this.content;
    }
}