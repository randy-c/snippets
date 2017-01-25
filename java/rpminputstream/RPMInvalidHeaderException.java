//https://github.com/randy-c/snippets
package randyc;

public class RPMInvalidHeaderException extends RPMException {
    
    public RPMInvalidHeaderException(String reason) {
        super(reason);
    }
    
    public RPMInvalidHeaderException() {
        super();
    }
    
    public RPMInvalidHeaderException(String reason, Throwable e) {
        super(reason, e);
    }
}
