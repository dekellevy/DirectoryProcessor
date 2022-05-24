package filesprocessing;

public class BadStructureException extends Exception{
    private static final long serialVersionUID = 1L;

    public BadStructureException(String msg){
        super(msg);
    }
}
