package filesprocessing;

public class IllegalUseException extends Exception{
    private static final long serialVersionUID = 1L;

    public IllegalUseException(String msg){
        super(msg);
    }
}
