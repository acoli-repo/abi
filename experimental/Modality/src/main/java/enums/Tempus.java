package enums;


public enum Tempus implements AnnotationTag{
    present ("pres"),
    past("past"),
    future("fut");

    private final String content;
    Tempus(String s){ this.content = s;}
    public String getString(){
        return this.content;
    }
}
