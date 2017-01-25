package randyc;

public class RPMInvalidLeadException extends RPMException {

    public RPMInvalidLeadException(String reason) {
        super(reason);
    }
    
    public RPMInvalidLeadException() {
        super();
    }
    
    public RPMInvalidLeadException(String reason, Throwable e) {
        super(reason, e);
    }

}
