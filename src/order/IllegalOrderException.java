package order;

public class IllegalOrderException extends Exception{
    private static final long serialVersionUID = 1L;

    public IllegalOrderException(String msg){
        super(msg);
    }
}
