package br.ufrj.caronae.models;

/**
 * Created by Luis on 6/8/2017.
 */

public class ModelValidateDuplicate {

    private boolean valid;
    private String status;
    private String message;

    public boolean isValid() {
        return valid;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
