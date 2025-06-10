package me.dio.dio_springboot_project.service;

import me.dio.dio_springboot_project.domain.model.Usuario;

import java.util.List;

public interface UserService {

    Usuario criarUsuario(Usuario novoUsuario);
    Boolean alterarUsuario(Usuario novoUsuario);
    Boolean removerUsuario(Integer id);
    List<Usuario> listarUsuarios();
    Usuario consultarUsuario(Integer id);

}
