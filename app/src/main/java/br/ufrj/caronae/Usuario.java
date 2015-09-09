package br.ufrj.caronae;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Usuario")
public class Usuario extends Model {
    @Column
    String nome;
    @Column
    String perfil;
    @Column
    String curso;
    @Column
    String unidade;
    @Column
    String zona;
    @Column
    String bairro;

    public Usuario() {
        super();
    }
}
