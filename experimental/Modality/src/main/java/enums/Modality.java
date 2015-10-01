package enums;


public enum Modality implements AnnotationTag{
    epistemic("epi"),
    nonepistemic("non");

    private final String content;
    Modality(String s){ this.content = s;}
    public String getString(){
        return this.content;
    }
}
