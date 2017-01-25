// https://github.com/randy-c/snippets
package randyc;

import java.lang.RuntimeException;

public class RPMException extends RuntimeException {

    public RPMException() {
        super();
    }

    public RPMException(String reason) {
        super(reason);
    }
    
    public RPMException(String reason, Throwable e) {
        super(reason, e);
    }
}
