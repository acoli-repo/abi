package enums;


public  enum Diathese implements AnnotationTag{
    active ("act"),
    passive("pass");

    private final String content;
    Diathese(String s){ this.content = s;}
    public String getString(){
        return this.content;
    }
}
