package me.dio.dio_springboot_project.service;

import me.dio.dio_springboot_project.dto.UsuarioDto;

import java.util.List;

public interface UsuarioService {

    UsuarioDto criarUsuario(UsuarioDto novoUsuario);
    UsuarioDto alterarUsuario(UsuarioDto novoUsuario);
    void removerUsuario(String id);
    List<UsuarioDto> listarUsuarios();
    UsuarioDto consultarUsuario(String id);

}
