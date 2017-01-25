package randyc;

public class RPMInvalidHeaderIndexException extends RPMException {

    public RPMInvalidHeaderIndexException() {
        super();
    }
    
    public RPMInvalidHeaderIndexException(String reason, Throwable e) {
        super(reason, e);
    }
    
    public RPMInvalidHeaderIndexException(String reason) {
        super(reason);
    }
}
