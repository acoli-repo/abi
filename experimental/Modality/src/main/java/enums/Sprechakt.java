package enums;

public enum Sprechakt implements AnnotationTag{
    request("req"),
    suggest("sug"),
    opine("op");
    private final String content;
    Sprechakt(String s){ this.content = s;}
    public String getString(){
        return this.content;
    }
}
