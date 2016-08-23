<#if package??>package ${package};</#if>

public class AuthenticationException extends Exception {

    /**
     * Creates a new instance of <code>AuthenticationException</code> without
     * detail message.
     */
    public AuthenticationException() {
    }

    /**
     * Constructs an instance of <code>AuthenticationException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public AuthenticationException(String msg) {
        super(msg);
    }
}
