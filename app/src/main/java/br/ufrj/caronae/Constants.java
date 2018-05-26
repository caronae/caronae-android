package br.ufrj.caronae;

public class Constants {

    public static final String BUILD_TYPE                   = "prod";
    public static final String API_BASE_URL                 = BUILD_TYPE.equals("prod")?"https://api.caronae.org/":"https://api.dev.caronae.org/";
    public static final String CARONAE_URL_BASE             = BUILD_TYPE.equals("prod")?"https://caronae.org/":"https://dev.caronae.org/";
    public static final String SHARE_LINK                   = CARONAE_URL_BASE + "carona/";

}