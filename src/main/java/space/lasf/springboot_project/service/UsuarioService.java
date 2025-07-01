package space.lasf.springboot_project.service;

import java.util.List;

import space.lasf.springboot_project.dto.UsuarioDto;

public interface UsuarioService {

    UsuarioDto criarUsuario(UsuarioDto novoUsuario);
    UsuarioDto alterarUsuario(UsuarioDto novoUsuario);
    void removerUsuario(String id);
    List<UsuarioDto> listarUsuarios();
    UsuarioDto consultarUsuario(String id);

}
